package org.marketcetera.photon.views;

import java.math.BigDecimal;

import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.widgets.Text;
import org.marketcetera.core.AccessViolator;
import org.marketcetera.core.InternalID;
import org.marketcetera.core.MSymbol;
import org.marketcetera.photon.Application;
import org.marketcetera.quickfix.FIXMessageUtil;

import quickfix.Message;
import quickfix.field.Side;
import quickfix.field.TimeInForce;

public class StockOrderTicketViewTest extends ViewTestBase {

	@Override
	protected void setUp() throws Exception {
		
		super.setUp();
	}

	public StockOrderTicketViewTest(String name) {
		super(name);
	}

	public void testShowOrder() throws NoSuchFieldException, IllegalAccessException {
		StockOrderTicket ticket = (StockOrderTicket) getTestView();
		Message message = FIXMessageUtil.newLimitOrder(new InternalID("asdf"),
				Side.BUY, BigDecimal.TEN, new MSymbol("QWER"), BigDecimal.ONE,
				TimeInForce.DAY, null);
		ticket.showOrder(message);
		AccessViolator violator = new AccessViolator(StockOrderTicket.class);
		assertEquals("10", ((Text)violator.getField("quantityText", ticket)).getText());
		assertEquals("B", ((CCombo)violator.getField("sideCCombo", ticket)).getText());
		assertEquals("1", ((Text)violator.getField("priceText", ticket)).getText());
		assertEquals("QWER", ((Text)violator.getField("symbolText", ticket)).getText());
		assertEquals("DAY", ((CCombo)violator.getField("tifCCombo", ticket)).getText());
	}

	@Override
	protected String getViewID() {
		return StockOrderTicket.ID;
	}

}
