/**
 * Project: ${puma-server.aid}
 * 
 * File Created at 2012-6-7 $Id$
 * 
 * Copyright 2010 dianping.com. All rights reserved.
 * 
 * This software is the confidential and proprietary information of Dianping
 * Company. ("Confidential Information"). You shall not disclose such
 * Confidential Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with dianping.com.
 */
package com.dianping.puma.parser.mysql.packet;

import java.nio.ByteBuffer;

import com.dianping.puma.bo.PumaContext;
import com.dianping.puma.parser.mysql.MySQLCommunicationConstant;
import com.dianping.puma.parser.mysql.utils.MySQLUtils;
import com.dianping.puma.utils.PacketUtils;

/**
 * 
 * @author Leo Liang
 * 
 */
public class ConnectPacket extends AbstractResponsePacket {

	private static final long	serialVersionUID	= -4346727912577548259L;

	@Override
	protected void doReadPacket(ByteBuffer buf, PumaContext context) {
		context.setProtocolVersion(buf.get());
		context.setServerVersion(PacketUtils.readNullTerminatedString(buf));
        context.setThreadId(PacketUtils.readLong(buf, 4));
        context.setSeed(PacketUtils.readNullTerminatedString(buf));

        //以上为HandshakeV10和HandshakeV9公用部分
        //以下为optional内容

        context.setServerCapabilities(PacketUtils.readInt(buf, 2));
        context.setServerCharsetIndex(PacketUtils.readInt(buf, 1));
        context.setServerStatus(PacketUtils.readInt(buf, 2));

        parseVersion(context);

        if(MySQLUtils.versionMeetsMinimum(context.getServerMajorVersion(), context.getServerMinorVersion(),
                context.getServerSubMinorVersion(), 5, 5, 7))
        {
            int serverCapabilitiesPart2 = PacketUtils.readInt(buf, 2);
            // TODO: context 的 ServerCapabilities 属性需要修改为 long 类型
            // context.setServerCapabilities(context.getServerCapabilities() +
            // 65536 * serverCapabilitiesPart2);
        }
        else
        {
            PacketUtils.readInt(buf, 2);
        }

//        if((context.getServerCapabilities() & MySQLCommunicationConstant.CLIENT_PLUGIN_AUTH) != 0)
//        {
//            int scramble_len = PacketUtils.readInt(buf, 1);
//        }
//        else
//        {
            PacketUtils.readInt(buf, 1);
//        }

        if (buf.position() + 10 <= buf.limit()) {
            buf.position(buf.position() + 10);
        }

//        if((context.getServerCapabilities() & MySQLCommunicationConstant.CLIENT_SECURE_CONNECTION) != 0)
//        {
            String seedPart2 = PacketUtils.readNullTerminatedString(buf);
            StringBuilder newSeed = new StringBuilder(seedPart2.length() + 8);
            newSeed.append(context.getSeed());
            newSeed.append(seedPart2);
            context.setSeed(newSeed.toString());

            //5.5.7及以后版本后面还可能有“auth-plugin name”
//            if((context.getServerCapabilities() & MySQLCommunicationConstant.CLIENT_PLUGIN_AUTH) != 0)
//            {
//                String auth_plugin_name = PacketUtils.readNullTerminatedString(buf);
//            }
//        }

		if (MySQLUtils.versionMeetsMinimum(context.getServerMajorVersion(), context.getServerMinorVersion(),
				context.getServerSubMinorVersion(), 4, 0, 8)) {
			context.setMaxThreeBytes((256 * 256 * 256) - 1);
		} else {
			context.setMaxThreeBytes(255 * 255 * 255);
		}

		if (context.getProtocolVersion() > 9) {
			context.setClientParam(context.getClientParam() | MySQLCommunicationConstant.CLIENT_LONG_PASSWORD);
		} else {
			context.setClientParam(context.getClientParam() & ~MySQLCommunicationConstant.CLIENT_LONG_PASSWORD);
		}

        //4.1.1及以上版本，直接使用新的协议
        if(MySQLUtils.versionMeetsMinimum(context.getServerMajorVersion(), context.getServerMinorVersion(),
                context.getServerSubMinorVersion(), 4, 1, 1))
        {
            context.setUse41Extensions(true);
            setClientParamFor41(context);
        }
        else if (MySQLUtils.versionMeetsMinimum(context.getServerMajorVersion(), context.getServerMinorVersion(),
                context.getServerSubMinorVersion(), 4, 1, 0))
        {
            context.setUse41Extensions(true);
            //4.1.0版本，是否使用新的协议看服务器的CLIENT_PROTOCOL_41标记
            if(context.getProtocolVersion() > 9 && (context
                    .getServerCapabilities() & MySQLCommunicationConstant.CLIENT_PROTOCOL_41) != 0)
            {
                setClientParamFor41(context);
            } else {
                setClientParamPre41(context);
            }
        }
        //4.1.0以前版本，是否使用新的协议看服务器的CLIENT_RESERVED和CLIENT_PROTOCOL_41标记
        else if(context.getProtocolVersion() > 9 && (context
                    .getServerCapabilities() & MySQLCommunicationConstant.CLIENT_RESERVED) != 0)
        {
            context.setUse41Extensions(true);
            if((context
                    .getServerCapabilities() & MySQLCommunicationConstant.CLIENT_PROTOCOL_41) != 0)
            {
                setClientParamFor41(context);
            } else {
                setClientParamPre41(context);
            }
        }
	}

    private void setClientParamFor41(PumaContext context) {
        context.setClientParam(context.getClientParam() | MySQLCommunicationConstant.CLIENT_PROTOCOL_41);
        context.setHas41NewNewProt(true);
    }
    private void setClientParamPre41(PumaContext context) {
        context.setClientParam(context.getClientParam() | MySQLCommunicationConstant.CLIENT_RESERVED);
        context.setHas41NewNewProt(false);
    }

    /**
     * 
     */
	private void parseVersion(PumaContext context) {
		// Parse the server version into major/minor/subminor
		int point = context.getServerVersion().indexOf('.');

		if (point != -1) {
			try {
				int n = Integer.parseInt(context.getServerVersion().substring(0, point));
				context.setServerMajorVersion(n);
			} catch (NumberFormatException nfe) {
				// ignore
			}

			String remaining = context.getServerVersion().substring(point + 1, context.getServerVersion().length());
			point = remaining.indexOf('.'); //$NON-NLS-1$

			if (point != -1) {
				try {
					int n = Integer.parseInt(remaining.substring(0, point));
					context.setServerMinorVersion(n);
				} catch (NumberFormatException nfe) {
					// ignore
				}

				remaining = remaining.substring(point + 1, remaining.length());

				int pos = 0;

				while (pos < remaining.length()) {
					if ((remaining.charAt(pos) < '0') || (remaining.charAt(pos) > '9')) {
						break;
					}

					pos++;
				}

				try {
					int n = Integer.parseInt(remaining.substring(0, pos));
					context.setServerSubMinorVersion(n);
				} catch (NumberFormatException nfe) {
					// ignore
				}
			}
		}
	}

}
