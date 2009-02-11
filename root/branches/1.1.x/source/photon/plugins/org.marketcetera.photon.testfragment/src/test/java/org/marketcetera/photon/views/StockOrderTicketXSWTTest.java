package org.marketcetera.photon.views;

import java.io.InputStream;

import org.marketcetera.photon.PhotonPlugin;

import com.swtworkbench.community.xswt.XSWT;
import com.swtworkbench.community.xswt.XSWTException;
import com.swtworkbench.community.xswt.layoutbuilder.FormToolkitLayoutBuilder;

public class StockOrderTicketXSWTTest extends XSWTViewTestBase {

	public StockOrderTicketXSWTTest(String name) {
		super(IStockOrderTicket.class, name);
	}

	@Override
	public Object instantiateXSWT() throws XSWTException {
		InputStream resourceAsStream = PhotonPlugin.class.getResourceAsStream(
				"/stock_order_ticket.xswt");
		XSWT xswt = XSWT.create(resourceAsStream);
		new FormToolkitLayoutBuilder(xswt);
		return xswt.parse(((XSWTTestView)getTestView()).getParent(), IStockOrderTicket.class);
	}

	@Override
	protected String getViewID() {
		return XSWTTestView.ID;
	}

}
