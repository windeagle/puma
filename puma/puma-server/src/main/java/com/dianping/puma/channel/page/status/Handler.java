package com.dianping.puma.channel.page.status;

import java.io.IOException;

import javax.servlet.ServletException;

import com.dianping.puma.channel.ChannelPage;
import com.dianping.puma.common.SystemStatusContainer;
import com.site.lookup.annotation.Inject;
import com.site.web.mvc.PageHandler;
import com.site.web.mvc.annotation.InboundActionMeta;
import com.site.web.mvc.annotation.OutboundActionMeta;
import com.site.web.mvc.annotation.PayloadMeta;

public class Handler implements PageHandler<Context> {
	@Inject
	private JspViewer	m_jspViewer;

	@Override
	@PayloadMeta(Payload.class)
	@InboundActionMeta(name = "status")
	public void handleInbound(Context ctx) throws ServletException, IOException {
		// display only, no action here
	}

	@Override
	@OutboundActionMeta(name = "status")
	public void handleOutbound(Context ctx) throws ServletException, IOException {
		Model model = new Model(ctx);

		model.setSystemStatus(SystemStatusContainer.instance);
		model.setAction(Action.VIEW);
		model.setPage(ChannelPage.STATUS);
		m_jspViewer.view(ctx, model);
	}
}
