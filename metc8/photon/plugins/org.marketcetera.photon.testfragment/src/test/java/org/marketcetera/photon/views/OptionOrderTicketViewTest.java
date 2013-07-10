package org.marketcetera.photon.views;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.math.BigDecimal;

import org.junit.Test;
import org.marketcetera.photon.PhotonPlugin;
import org.marketcetera.photon.test.AbstractUIRunner;
import org.marketcetera.photon.test.AbstractUIRunner.ThrowableRunnable;
import org.marketcetera.trade.Instrument;
import org.marketcetera.trade.Option;
import org.marketcetera.trade.OptionType;
import org.marketcetera.trade.OrderCapacity;
import org.marketcetera.trade.OrderType;
import org.marketcetera.trade.PositionEffect;
import org.marketcetera.trade.Side;
import org.marketcetera.trade.TimeInForce;

/* $License$ */

/**
 * Test {@link OptionOrderTicketView}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id: OptionOrderTicketViewTest.java 10861 2009-11-09 04:29:18Z will
 *          $
 * @since 2.0.0
 */
public class OptionOrderTicketViewTest extends
        OrderTicketViewTestBase<IOptionOrderTicket, OptionOrderTicketModel> {

    @Override
    protected String getViewId() {
        return OptionOrderTicketView.ID;
    }

    @Override
    protected OptionOrderTicketViewFixture createFixture() throws Exception {
        return new OptionOrderTicketViewFixture();
    }

    @Override
    protected OptionOrderTicketModel getModel() {
        /*
         * Only init once.
         */
        OptionOrderTicketModel model = PhotonPlugin.getDefault()
                .getOptionOrderTicketModel();
        if (model == null) {
            PhotonPlugin.getDefault().initOrderTickets();
            model = PhotonPlugin.getDefault().getOptionOrderTicketModel();
        }
        return model;
    }

    @Override
    protected String getReplaceOrderText() {
        return "Replace Option Order";
    }

    @Override
    protected String getNewOrderText() {
        return "New Option Order";
    }

    @Override
    protected Instrument createInstrument(String symbol) {
        return new Option(symbol, "200910", BigDecimal.ONE, OptionType.Call);
    }

    protected Side[] getValidSides() {
        return new Side[] { Side.Buy, Side.Sell };
    }

    @Test
    public void testInstrument() throws Exception {
        assertInstrument(null);
        OptionOrderTicketViewFixture fixture = createFixture();
        mFixture = fixture;
        fixture.getSymbolText().setText("1");
        fixture.getExpiryText().setText("200910");
        fixture.getStrikeText().setText("1.5");
        fixture.getOptionTypeCombo().setSelection("Call");
        assertInstrument(new Option("1", "200910", new BigDecimal("1.5"),
                OptionType.Call));

        // symbol
        fixture.getSymbolText().setText("IBM");
        assertInstrument(new Option("IBM", "200910", new BigDecimal("1.5"),
                OptionType.Call));
        fixture.getSymbolText().setText("");
        assertInstrument(null);
        fixture.getSymbolText().setText("IBM");

        // expiry
        fixture.getExpiryText().setText("20091010");
        assertInstrument(new Option("IBM", "20091010", new BigDecimal("1.5"),
                OptionType.Call));
        fixture.getExpiryText().setText("200910w1");
        assertInstrument(new Option("IBM", "200910w1", new BigDecimal("1.5"),
                OptionType.Call));
        fixture.getExpiryText().setText("");
        assertInstrument(null);
        fixture.getExpiryText().setText("200910");

        // strike
        fixture.getStrikeText().setText("a");
        assertInstrument(new Option("IBM", "200910", new BigDecimal("1.5"),
                OptionType.Call));
        fixture.getStrikeText().setText("500");
        assertInstrument(new Option("IBM", "200910", new BigDecimal("500"),
                OptionType.Call));
        fixture.getStrikeText().setText("");
        assertInstrument(null);
        fixture.getStrikeText().setText("1.5");

        // type
        assertThat(fixture.getOptionTypeCombo().itemCount(), is(2));
        fixture.getOptionTypeCombo().setSelection("Put");
        assertInstrument(new Option("IBM", "200910", new BigDecimal("1.5"),
                OptionType.Put));
    }

    @Test
    public void testOpenClose() throws Exception {
        assertPositionEffect(null);
        OptionOrderTicketViewFixture fixture = createFixture();
        mFixture = fixture;
        assertThat(fixture.getOpenCloseCombo().itemCount(), is(3));
        fixture.getOpenCloseCombo().setSelection("Open");
        assertPositionEffect(PositionEffect.Open);
        fixture.getOpenCloseCombo().setSelection("Close");
        assertPositionEffect(PositionEffect.Close);
        fixture.getOpenCloseCombo().setSelection("");
        assertPositionEffect(null);
    }

    @Test
    public void testCapacity() throws Exception {
        assertOrderCapacity(null);
        OptionOrderTicketViewFixture fixture = createFixture();
        mFixture = fixture;
        assertThat(fixture.getCapacityCombo().itemCount(), is(4));
        fixture.getCapacityCombo().setSelection("Agency");
        assertOrderCapacity(OrderCapacity.Agency);
        fixture.getCapacityCombo().setSelection("Principal");
        assertOrderCapacity(OrderCapacity.Principal);
        fixture.getCapacityCombo().setSelection("RisklessPrincipal");
        assertOrderCapacity(OrderCapacity.RisklessPrincipal);
        fixture.getCapacityCombo().setSelection(0);
        assertOrderCapacity(null);
    }

    @Test
    public void testSendButton() throws Exception {
        OptionOrderTicketViewFixture fixture = createFixture();
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
        assertSendDisabled("Expiry is required");
        fixture.getExpiryText().setText("200910");
        assertSendDisabled("Strike Price is required");
        fixture.getStrikeText().setText("3");
        assertSendDisabled("Option Type is required");
        fixture.getOptionTypeCombo().setSelection("Call");
        assertThat(fixture.getSendButton().isEnabled(), is(true));
    }

    @Override
    public void fieldsDisabledForReplaceOrders() throws Exception {
        OptionOrderTicketViewFixture fixture = createFixture();
        mFixture = fixture;
        fixture.assertEnabled(true, true, true, true, false, true, true, true,
                true, true, true, true, true, true);
        setReplace(Side.Buy, "10", "QWER", OrderType.Limit, "10", null,
                TimeInForce.Day, null);
        fixture.assertEnabled(false, true, false, true, true, false, true,
                false, false, false, false, true, true, true);
    }

    protected void assertPositionEffect(final PositionEffect positionEffect)
            throws Exception {
        AbstractUIRunner.syncRun(new ThrowableRunnable() {
            @Override
            public void run() throws Throwable {
                assertThat(mModel.getOrderObservable().getTypedValue()
                        .getPositionEffect(), is(positionEffect));
            }
        });
    }

    protected void assertOrderCapacity(final OrderCapacity Capacity)
            throws Exception {
        AbstractUIRunner.syncRun(new ThrowableRunnable() {
            @Override
            public void run() throws Throwable {
                assertThat(mModel.getOrderObservable().getTypedValue()
                        .getOrderCapacity(), is(Capacity));
            }
        });
    }
}
