/**
 * Project: ${puma-common.aid}
 * 
 * File Created at 2012-6-24
 * $Id$
 * 
 * Copyright 2010 dianping.com.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Dianping Company. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with dianping.com.
 */
package com.dianping.puma.utils;

import java.util.Arrays;
import java.util.BitSet;

/**
 * TODO Comment of CodecUtils
 * 
 * @see http://code.google.com/p/open-replicator/
 * 
 */
public final class CodecUtils {

	private CodecUtils() {

	}

    public static int ByteNumToBitNum(int byteNum)
    {
        return byteNum << 3;
    }

    public static int BitNumToByteNum(int bitNum)
    {
        return (bitNum + 7) >> 3;
    }

    public static int BitIndexToByteIndex(int bitIndex)
    {
        return bitIndex >> 3;
    }

    public static int GetByteIndexByBitIndex_Reverse(int bytesLen, int bitIndex)
    {
        int byteIndex = BitIndexToByteIndex(bitIndex);
        return bytesLen - 1 - byteIndex;
    }

    /**
     *
     * @param bytes  LittleEndian Bytes (高地址存放最高有效字节)
     * @return BigEndian BitSet (低地址存放最高有效字节)
     */
	public static BitSet fromByteArray(byte[] bytes) {
		BitSet bits = new BitSet();
		for (int i = 0; i < ByteNumToBitNum(bytes.length); i++) {
            int byteIndex_Reverse = GetByteIndexByBitIndex_Reverse(bytes.length, i);
			if ((bytes[byteIndex_Reverse] & (1 << (i % 8))) > 0) {
				bits.set(i);
			}
		}
		return bits;
	}

    /**
     *
     * @param bits  BigEndian BitSet (低地址存放最高有效字节)
     * @return LittleEndian Bytes (高地址存放最高有效字节)
     */
    public static byte[] toByteArray(BitSet bits) {
		byte[] bytes = new byte[BitNumToByteNum(bits.length())];
		for (int i = 0; i < bits.length(); i++) {
			if (bits.get(i)) {
                int byteIndex_Reverse = GetByteIndexByBitIndex_Reverse(bytes.length, i);
				bytes[byteIndex_Reverse] |= 1 << (i % 8);
			}
		}
		return bytes;
	}

	public static byte[] toBigEndian(byte[] value) {
		for (int i = 0, length = value.length >> 1; i < length; i++) {
			final int j = value.length - 1 - i;
			final byte t = value[i];
			value[i] = value[j];
			value[j] = t;
		}
		return value;
	}

	/**
	 * 
	 */
	public static int toUnsigned(byte b) {
		return b & 0xFF;
	}

	public static int toUnsigned(short s) {
		return s & 0xFFFF;
	}

	public static long toUnsigned(int i) {
		return i & 0xFFFFFFFFL;
	}

	/**
	 *  BigEndian (低地址存放最高有效字节)
	 */
	public static byte[] toByteArray(byte num) {
		return new byte[] { num };
	}

	public static byte[] toByteArray(short num) {
		final byte[] r = new byte[2];
		for (int i = 0; i < 2; i++) {
			r[i] = (byte) (num >>> (8 - i * 8));
		}
		return r;
	}

	public static byte[] toByteArray(int num) {
		final byte[] r = new byte[4];
		for (int i = 0; i < 4; i++) {
			r[i] = (byte) (num >>> (24 - i * 8));
		}
		return r;
	}

	public static byte[] toByteArray(long num) {
		final byte[] r = new byte[8];
		for (int i = 0; i < 8; i++) {
			r[i] = (byte) (num >>> (56 - i * 8));
		}
		return r;
	}

	/**
	 * 
	 */
	public static int toInt(byte[] data, int offset, int length) {
		int r = 0;
		for (int i = offset; i < (offset + length); i++) {
			final byte b = data[i];
			r = (r << 8) | (b >= 0 ? (int) b : (b + 256));
		}
		return r;
	}

	public static long toLong(byte[] data, int offset, int length) {
		long r = 0;
		for (int i = offset; i < (offset + length); i++) {
			final byte b = data[i];
			r = (r << 8) | (b >= 0 ? (int) b : (b + 256));
		}
		return r;
	}

	public static byte[] or(byte[] data1, byte[] data2) {
		//
		if (data1.length != data2.length) {
			throw new IllegalArgumentException("array lenth does NOT match, " + data1.length + " vs " + data2.length);
		}

		//
		final byte r[] = new byte[data1.length];
		for (int i = 0; i < r.length; i++) {
			r[i] = (byte) (data1[i] | data2[i]);
		}
		return r;
	}

	public static byte[] and(byte[] data1, byte[] data2) {
		//
		if (data1.length != data2.length) {
			throw new IllegalArgumentException("array lenth does NOT match, " + data1.length + " vs " + data2.length);
		}

		//
		final byte r[] = new byte[data1.length];
		for (int i = 0; i < r.length; i++) {
			r[i] = (byte) (data1[i] & data2[i]);
		}
		return r;
	}

	public static byte[] xor(byte[] data1, byte[] data2) {
		//
		if (data1.length != data2.length) {
			throw new IllegalArgumentException("array lenth does NOT match, " + data1.length + " vs " + data2.length);
		}

		//
		final byte r[] = new byte[data1.length];
		for (int i = 0; i < r.length; i++) {
			r[i] = (byte) (data1[i] ^ data2[i]);
		}
		return r;
	}

	public static boolean equals(byte[] data1, byte[] data2) {
		return Arrays.equals(data1, data2);
	}

	public static byte[] concat(byte[] data1, byte[] data2) {
		final byte r[] = new byte[data1.length + data2.length];
		System.arraycopy(data1, 0, r, 0, data1.length);
		System.arraycopy(data2, 0, r, data1.length, data2.length);
		return r;
	}
}
