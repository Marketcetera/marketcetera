package org.marketcetera.photon.views;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.marketcetera.photon.PhotonPlugin;
import org.marketcetera.trade.Equity;
import org.marketcetera.trade.Instrument;

/* $License$ */

/**
 * Tests {@link StockOrderTicketView}.
 *
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
public class StockOrderTicketViewTest extends
        OrderTicketViewTestBase<IStockOrderTicket, StockOrderTicketModel> {

    @Override
    protected StockOrderTicketModel getModel() {
        /*
         * Only init once.
         */
        StockOrderTicketModel model = PhotonPlugin.getDefault().getStockOrderTicketModel();
        if (model == null) {
            PhotonPlugin.getDefault().initOrderTickets();
            model = PhotonPlugin.getDefault().getStockOrderTicketModel();
        }
        return model;
    }
    
    @Override
    protected String getViewId() {
        return StockOrderTicketView.ID;
    }
    
    @Override
    protected StockOrderTicketViewFixture createFixture()
            throws Exception {
        return new StockOrderTicketViewFixture();
    }

    @Override
    protected String getReplaceOrderText() {
        return "Replace Equity Order";
    }

    @Override
    protected String getNewOrderText() {
        return "New Equity Order";
    }
    
    @Override
    protected Instrument createInstrument(String symbol) {
        return new Equity(symbol);
    }

    @Test
    public void testSymbol() throws Exception {
        assertInstrument(null);
        mFixture = createFixture();
        mFixture.getSymbolText().setText("1");
        assertInstrument(new Equity("1"));
        mFixture.getSymbolText().setText("IBM");
        assertInstrument(new Equity("IBM"));
        mFixture.getSymbolText().setText("ABC.D");
        assertInstrument(new Equity("ABC.D"));
        mFixture.getSymbolText().setText("");
        assertInstrument(null);
    }
    
    @Test
    public void testSendButton() throws Exception {
        StockOrderTicketViewFixture fixture = createFixture();
        mFixture = fixture;
        assertSendDisabled("Side is required");
        fixture.getSideCombo().setSelection("Buy");
        assertSendDisabled("Quantity is required");
        fixture.getQuantityText().setText("10");
        assertSendDisabled("Symbol is required");
        fixture.getSymbolText().setText("M");
        assertSendDisabled("Order Type is required");
        fixture.getOrderTypeCombo().setSelection("Limit");
        assertSendDisabled("Price is required");
        fixture.getPriceText().setText("10");
        assertThat(fixture.getSendButton().isEnabled(), is(true));
    }
}
