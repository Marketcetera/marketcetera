package org.marketcetera.photon.views;

import java.io.InputStream;

import org.marketcetera.photon.PhotonPlugin;

import com.swtworkbench.community.xswt.XSWT;
import com.swtworkbench.community.xswt.XSWTException;
import com.swtworkbench.community.xswt.layoutbuilder.FormToolkitLayoutBuilder;

public class OptionOrderTicketXSWTTest extends XSWTViewTestBase {

	public OptionOrderTicketXSWTTest(String name) {
		super(IOptionOrderTicket.class, name);
	}

	@Override
	public Object instantiateXSWT() throws XSWTException {
		InputStream resourceAsStream = PhotonPlugin.class.getResourceAsStream(
				"/option_order_ticket.xswt");
		XSWT xswt = XSWT.create(resourceAsStream);
		new FormToolkitLayoutBuilder(xswt);
		return xswt.parse(((XSWTTestView)getTestView()).getParent(), IOptionOrderTicket.class);
	}

	@Override
	protected String getViewID() {
		return XSWTTestView.ID;
	}

}
