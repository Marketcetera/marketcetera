package org.marketcetera.photon.views;

import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.marketcetera.core.position.impl.BigDecimalMatchers.comparesEqualTo;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.Callable;

import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.marketcetera.client.brokers.BrokerStatus;
import org.marketcetera.client.brokers.BrokersStatus;
import org.marketcetera.photon.BrokerManager;
import org.marketcetera.photon.PhotonPlugin;
import org.marketcetera.photon.preferences.CustomOrderFieldPage;
import org.marketcetera.photon.test.AbstractUIRunner;
import org.marketcetera.photon.test.PhotonTestBase;
import org.marketcetera.photon.test.WorkbenchRunner;
import org.marketcetera.photon.test.AbstractUIRunner.ThrowableRunnable;
import org.marketcetera.photon.test.AbstractUIRunner.UI;
import org.marketcetera.trade.BrokerID;
import org.marketcetera.trade.Factory;
import org.marketcetera.trade.Instrument;
import org.marketcetera.trade.NewOrReplaceOrder;
import org.marketcetera.trade.OrderReplace;
import org.marketcetera.trade.OrderSingle;
import org.marketcetera.trade.OrderType;
import org.marketcetera.trade.Side;
import org.marketcetera.trade.TimeInForce;

import quickfix.field.DeliverToCompID;
import quickfix.field.PrevClosePx;

/* $License$ */

/**
 * Base class for {@link OrderTicketView} tests. 
 *
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 2.0.0
 */
@RunWith(WorkbenchRunner.class)
public abstract class OrderTicketViewTestBase<T extends IOrderTicket, M extends OrderTicketModel>
        extends PhotonTestBase {

    protected volatile M mModel;
    protected volatile OrderTicketViewFixture<T> mFixture;

    @Before
    @UI
    public void before() throws Exception {
        mModel = getModel();
        BrokerStatus status1 = new BrokerStatus("Goldman Sachs", new BrokerID(
                "gs"), true);
        BrokerStatus status2 = new BrokerStatus("Exchange Simulator",
                new BrokerID("metc"), false);
        BrokersStatus statuses = new BrokersStatus(Arrays.asList(status1,
                status2));
        BrokerManager.getCurrent().setBrokersStatus(statuses);
        clearOrder();
    }

    abstract protected M getModel();

    @After
    @UI
    public void after() throws Exception {
        if (mFixture != null) {
            mFixture.getView().close();
        }
        BrokerManager.getCurrent().setBrokersStatus(
                new BrokersStatus(Collections.<BrokerStatus> emptyList()));
        clearOrder();
    }

    @Test
    public void newTicketIsEmpty() throws Exception {
        mFixture = createFixture();
        mFixture.assertTicket("", "", "", "", "", "Auto Select", "Day", "");
    }

    abstract protected OrderTicketViewFixture<T> createFixture() throws Exception;

    @Test
    public void ticketShowsModel() throws Exception {
        setOrderSingle(Side.Buy, "10", "QWER", OrderType.Limit, "1", "gs",
                TimeInForce.Day, null);
        mFixture = createFixture();
        mFixture.assertTicket("Buy", "10", "QWER", "Limit", "1",
                "Goldman Sachs (gs)", "Day", "");
        setOrderSingle(Side.Sell, "1", "QWER", OrderType.Market, null, null,
                TimeInForce.AtTheOpening, "123456789101112");
        mFixture.assertTicket("Sell", "1", "QWER", "Market", "", "Auto Select",
                "AtTheOpening", "123456789101112");
        clearOrder();
        mFixture.assertTicket("", "", "", "", "", "Auto Select", "Day", "");
    }

    @Test
    public void fieldsDisabledForReplaceOrders() throws Exception {
        mFixture = createFixture();
        mFixture.assertEnabled(true, true, true, true, false, true, true, true);
        setReplace(Side.Buy, "10", "QWER", OrderType.Limit, "10", null,
                TimeInForce.Day, null);
        mFixture.assertEnabled(false, true, false, true, true, false, true,
                true);
    }

    @Test
    public void testSide() throws Exception {
        assertSide(null);
        mFixture = createFixture();
        Side[] sides = getValidSides();
        assertThat(mFixture.getSideCombo().itemCount(), is(sides.length));
        for (Side side : sides) {
            mFixture.getSideCombo().setSelection(side.name());
            assertSide(side);
        }
    }
    
    protected Side[] getValidSides() {
        return new Side[] {Side.Buy, Side.Sell, Side.SellShort};
    }

    @Test
    public void testQuantity() throws Exception {
        assertQuantity(null);
        mFixture = createFixture();
        mFixture.getQuantityText().setText("1");
        assertQuantity("1");
        mFixture.getQuantityText().setText(".34234");
        assertQuantity(".34234");
        mFixture.getQuantityText().setText("2000");
        assertQuantity("2000");
        mFixture.getQuantityText().setText("a");
        assertQuantity("2000");
        mFixture.getQuantityText().setText("");
        assertQuantity(null);
    }

    @Test
    public void testOrderTypeAndPrice() throws Exception {
        mFixture = createFixture();
        assertThat(mFixture.getOrderTypeCombo().itemCount(), is(2));
        assertOrderTypeAndPrice(null, null, false);
        mFixture.getOrderTypeCombo().setSelection("Limit");
        assertOrderTypeAndPrice(OrderType.Limit, null, true);
        mFixture.getPriceText().setText("1");
        assertOrderTypeAndPrice(OrderType.Limit, "1", true);
        mFixture.getPriceText().setText("");
        assertOrderTypeAndPrice(OrderType.Limit, null, true);
        mFixture.getPriceText().setText("5.5");
        assertOrderTypeAndPrice(OrderType.Limit, "5.5", true);
        mFixture.getOrderTypeCombo().setSelection("Market");
        assertOrderTypeAndPrice(OrderType.Market, null, false);
    }

    @Test
    public void testBrokerId() throws Exception {
        assertBrokerId(null);
        mFixture = createFixture();
        assertThat(mFixture.getBrokerCombo().itemCount(), is(2));
        mFixture.getBrokerCombo().setSelection("Goldman Sachs (gs)");
        assertBrokerId("gs");
        mFixture.getBrokerCombo().setSelection("Auto Select");
        assertBrokerId(null);
    }

    @Test
    public void testTimeInForce() throws Exception {
        assertTimeInForce(TimeInForce.Day);
        mFixture = createFixture();
        assertThat(mFixture.getTimeInForceCombo().itemCount(), is(7));
        mFixture.getTimeInForceCombo().setSelection("");
        assertTimeInForce(null);
        mFixture.getTimeInForceCombo().setSelection("Day");
        assertTimeInForce(TimeInForce.Day);
        mFixture.getTimeInForceCombo().setSelection("GoodTillCancel");
        assertTimeInForce(TimeInForce.GoodTillCancel);
        mFixture.getTimeInForceCombo().setSelection("AtTheOpening");
        assertTimeInForce(TimeInForce.AtTheOpening);
        mFixture.getTimeInForceCombo().setSelection("ImmediateOrCancel");
        assertTimeInForce(TimeInForce.ImmediateOrCancel);
        mFixture.getTimeInForceCombo().setSelection("FillOrKill");
        assertTimeInForce(TimeInForce.FillOrKill);
        mFixture.getTimeInForceCombo().setSelection("AtTheClose");
        assertTimeInForce(TimeInForce.AtTheClose);
    }

    @Test
    public void testAccount() throws Exception {
        assertAccount(null);
        mFixture = createFixture();
        mFixture.getAccountText().setText("1");
        assertAccount("1");
        mFixture.getAccountText().setText("account");
        assertAccount("account");
        mFixture.getAccountText().setText("");
        assertAccount(null);
        mFixture.getAccountText().setText("   ");
        assertAccount("   ");
    }

    @Test
    public void testFormText() throws Exception {
        mFixture = createFixture();
        final OrderTicketView<?, T> view = mFixture.getRealView();
        AbstractUIRunner.syncRun(new ThrowableRunnable() {
            @Override
            public void run() throws Throwable {
                T ticket = view.getXSWTView();
                assertThat(ticket.getForm().getText(), is(getNewOrderText()));
                setReplace(Side.Buy, "10", "QWER", OrderType.Limit, "10", null,
                        TimeInForce.Day, null);
                assertThat(ticket.getForm().getText(),
                        is(getReplaceOrderText()));
                setOrderSingle(Side.Buy, "10", "QWER", OrderType.Limit, "1",
                        null, TimeInForce.Day, null);
                assertThat(ticket.getForm().getText(), is(getNewOrderText()));
            }
        });
    }
    
    @Test
    public void testClearButton() throws Exception {
        setOrderSingle(Side.Buy, "10", "QWER", OrderType.Limit, "1", "gs",
                TimeInForce.Day, null);
        mFixture = createFixture();
        mFixture.assertTicket("Buy", "10", "QWER", "Limit", "1",
                "Goldman Sachs (gs)", "Day", "");
        mFixture.getClearButton().click();
        mFixture.assertTicket("", "", "", "", "", "Goldman Sachs (gs)", "Day", "");
    }

    protected abstract String getReplaceOrderText();

    protected abstract String getNewOrderText();

    public void testFocus() throws Exception {
        mFixture = createFixture();
        assertThat(mFixture.getSideCombo().isActive(), is(true));
        setReplace(Side.Buy, "10", "QWER", OrderType.Limit, "10", null,
                TimeInForce.Day, null);
        assertThat(mFixture.getQuantityText().isActive(), is(true));
        setOrderSingle(Side.Buy, "10", "QWER", OrderType.Limit, "1", null,
                TimeInForce.Day, null);
        assertThat(mFixture.getSideCombo().isActive(), is(true));
    }

    private void clearOrder() throws Exception {
        AbstractUIRunner.syncRun(new ThrowableRunnable() {
            @Override
            public void run() throws Throwable {
                mModel.clearOrderMessage();
            }
        });
    }

    private void setOrderSingle(final Side side, final String quantity,
            final String symbol, final OrderType type, final String price,
            final String brokerId, final TimeInForce tif, final String account)
            throws Exception {
        AbstractUIRunner.syncRun(new ThrowableRunnable() {
            @Override
            public void run() throws Throwable {
                OrderSingle order = Factory.getInstance().createOrderSingle();
                fill(order, side, quantity, symbol, type, price, tif, account,
                        brokerId);
                mModel.getOrderObservable().setValue(order);
            }
        });
    }

    /**
     * UI thread only.
     */
    private void fill(NewOrReplaceOrder order, Side side, String quantity,
            String symbol, OrderType type, String price, TimeInForce tif,
            String account, String brokerId) {
        order.setSide(side);
        if (quantity != null) {
            order.setQuantity(new BigDecimal(quantity));
        }
        if (symbol != null) {
            order.setInstrument(createInstrument(symbol));
        }
        order.setOrderType(type);
        if (price != null) {
            order.setPrice(new BigDecimal(price));
        }
        order.setTimeInForce(tif);
        if (brokerId != null) {
            order.setBrokerID(new BrokerID(brokerId));
        }
        order.setAccount(account);
    }

    protected abstract Instrument createInstrument(String symbol);

    protected void setReplace(final Side side, final String quantity,
            final String symbol, final OrderType type, final String price,
            final String brokerId, final TimeInForce tif, final String account)
            throws Exception {
        AbstractUIRunner.syncRun(new ThrowableRunnable() {
            @Override
            public void run() throws Throwable {
                OrderReplace order = Factory.getInstance().createOrderReplace(
                        null);
                fill(order, side, quantity, symbol, type, price, tif, account,
                        brokerId);
                mModel.getOrderObservable().setValue(order);
            }
        });
    }

    protected void assertSide(final Side side) throws Exception {
        AbstractUIRunner.syncRun(new ThrowableRunnable() {
            @Override
            public void run() throws Throwable {
                assertThat(mModel.getOrderObservable().getTypedValue()
                        .getSide(), is(side));
            }
        });
    }

    protected void assertQuantity(final String quantity) throws Exception {
        AbstractUIRunner.syncRun(new ThrowableRunnable() {
            @Override
            public void run() throws Throwable {
                assertThat(mModel.getOrderObservable().getTypedValue()
                        .getQuantity(), comparesEqualTo(quantity));
            }
        });
    }

    protected void assertInstrument(final Instrument instrument) throws Exception {
        AbstractUIRunner.syncRun(new ThrowableRunnable() {
            @Override
            public void run() throws Throwable {
                assertThat(mModel.getOrderObservable().getTypedValue()
                        .getInstrument(), is(instrument));
            }
        });
    }

    protected void assertOrderTypeAndPrice(final OrderType type,
            final String price, final boolean priceEnabled) throws Exception {
        AbstractUIRunner.syncRun(new ThrowableRunnable() {
            @Override
            public void run() throws Throwable {
                NewOrReplaceOrder order = mModel.getOrderObservable()
                        .getTypedValue();
                assertThat(order.getOrderType(), is(type));
                assertThat(order.getPrice(), comparesEqualTo(price));
            }
        });
        assertThat(mFixture.getPriceText().isEnabled(), is(priceEnabled));
    }

    protected void assertBrokerId(final String brokerId) throws Exception {
        AbstractUIRunner.syncRun(new ThrowableRunnable() {
            @Override
            public void run() throws Throwable {
                BrokerID expected = brokerId == null ? null : new BrokerID(
                        brokerId);
                assertThat(mModel.getOrderObservable().getTypedValue()
                        .getBrokerID(), is(expected));
            }
        });
    }

    protected void assertTimeInForce(final TimeInForce timeInForce)
            throws Exception {
        AbstractUIRunner.syncRun(new ThrowableRunnable() {
            @Override
            public void run() throws Throwable {
                assertThat(mModel.getOrderObservable().getTypedValue()
                        .getTimeInForce(), is(timeInForce));
            }
        });
    }

    protected void assertAccount(final String account) throws Exception {
        AbstractUIRunner.syncRun(new ThrowableRunnable() {
            @Override
            public void run() throws Throwable {
                assertThat(mModel.getOrderObservable().getTypedValue()
                        .getAccount(), is(account));
            }
        });
    }

    @UI
    public void testAddCustomFieldsToPreferences() throws Exception {
        final OrderTicketView<?, T> view = openViewOnUIThread();
        ScopedPreferenceStore prefStore = PhotonPlugin.getDefault()
                .getPreferenceStore();
        prefStore.setValue(CustomOrderFieldPage.CUSTOM_FIELDS_PREFERENCE,
                String.format("%d=ABCD&%d=EFGH", DeliverToCompID.FIELD,
                        PrevClosePx.FIELD));

        ViewTestBase.doDelay(new Callable<Boolean>() {
            public Boolean call() throws Exception {
                return view.getXSWTView().getCustomFieldsTable().getItem(0)
                        .getText(1).length() > 0;
            }
        });
        Table customFieldsTable = view.getXSWTView().getCustomFieldsTable();
        assertEquals(2, customFieldsTable.getItemCount());

        TableItem item0 = customFieldsTable.getItem(0);
        assertEquals(false, item0.getChecked());
        assertEquals("" + DeliverToCompID.FIELD, item0.getText(0)); //$NON-NLS-1$
        assertEquals("ABCD", item0.getText(1)); //$NON-NLS-1$

        TableItem item1 = customFieldsTable.getItem(1);
        assertEquals(false, item1.getChecked());
        assertEquals("" + PrevClosePx.FIELD, item1.getText(0)); //$NON-NLS-1$
        assertEquals("EFGH", item1.getText(1)); //$NON-NLS-1$
    }

    @UI
    public void testSingleCustomField() throws Exception {
        final OrderTicketView<?, T> view = openViewOnUIThread();
        ScopedPreferenceStore prefStore = PhotonPlugin.getDefault()
                .getPreferenceStore();
        prefStore.setValue(CustomOrderFieldPage.CUSTOM_FIELDS_PREFERENCE,
                "" + DeliverToCompID.FIELD + "=ABCD"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$

        ViewTestBase.doDelay(new Callable<Boolean>() {
            public Boolean call() throws Exception {
                return view.getXSWTView().getCustomFieldsTable().getItem(0)
                        .getText(1).length() > 0;
            }
        });
        Table customFieldsTable = view.getXSWTView().getCustomFieldsTable();
        customFieldsTable.getItem(0).setChecked(true);
        ((CustomField) mModel.getCustomFieldsList().get(0)).setEnabled(true);

        setOrderSingle(Side.Buy, "10", "DREI", OrderType.Limit, "1", null,
                TimeInForce.Day, null);
        mModel.completeMessage();
        NewOrReplaceOrder updatedMessage = mModel.getOrderObservable()
                .getTypedValue();
        Map<String, String> custom = updatedMessage.getCustomFields();
        assertThat(custom, hasEntry("128", "ABCD"));
    }

    @UI
    public void testEnabledCustomFieldsAddedToMessage() throws Exception {
        final OrderTicketView<?, T> view = openViewOnUIThread();
        setOrderSingle(Side.Buy, "10", "DREI", OrderType.Limit, "1", null,
                TimeInForce.Day, null);
        ScopedPreferenceStore prefStore = PhotonPlugin.getDefault()
                .getPreferenceStore();
        prefStore
                .setValue(
                        CustomOrderFieldPage.CUSTOM_FIELDS_PREFERENCE,
                        ""      + DeliverToCompID.FIELD + "=ABCD" + "&" + PrevClosePx.FIELD + "=EFGH"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$

        Table customFieldsTable = view.getXSWTView().getCustomFieldsTable();
        customFieldsTable.getItem(0).setChecked(true);
        customFieldsTable.getItem(1).setChecked(true);
        ((CustomField) mModel.getCustomFieldsList().get(0)).setEnabled(true);
        ((CustomField) mModel.getCustomFieldsList().get(1)).setEnabled(true);

        mModel.completeMessage();
        NewOrReplaceOrder updatedMessage = mModel.getOrderObservable()
                .getTypedValue();
        Map<String, String> custom = updatedMessage.getCustomFields();
        assertThat(custom, hasEntry("128", "ABCD"));
        assertThat(custom, hasEntry("140", "EFGH"));
    }

    @UI
    public void testDisabledCustomFieldsNotAddedToMessage() throws Exception {
        final OrderTicketView<?, T> view = openViewOnUIThread();
        setOrderSingle(Side.Buy, "10", "DREI", OrderType.Limit, "1", null,
                TimeInForce.Day, null);

        ScopedPreferenceStore prefStore = PhotonPlugin.getDefault()
                .getPreferenceStore();
        prefStore
                .setValue(
                        CustomOrderFieldPage.CUSTOM_FIELDS_PREFERENCE,
                        ""      + DeliverToCompID.FIELD + "=ABCDE" + "&" + PrevClosePx.FIELD + "=EFGH"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$

        Table customFieldsTable = view.getXSWTView().getCustomFieldsTable();
        TableItem item0 = customFieldsTable.getItem(0);
        item0.setChecked(false);
        TableItem item1 = customFieldsTable.getItem(1);
        item1.setChecked(false);

        mModel.completeMessage();
        NewOrReplaceOrder updatedMessage = mModel.getOrderObservable()
                .getTypedValue();
        Map<String, String> custom = updatedMessage.getCustomFields();
        assertThat(custom, not(hasEntry("128", "ABCD")));
        assertThat(custom, not(hasEntry("140", "EFGH")));
    }

    private OrderTicketView<?, T> openViewOnUIThread() throws PartInitException {
        @SuppressWarnings("unchecked")
        final OrderTicketView<?, T> view = (OrderTicketView<?, T>) PlatformUI
                .getWorkbench().getActiveWorkbenchWindow().getActivePage()
                .showView(getViewId());
        return view;
    }
    
    abstract protected String getViewId();

    protected void assertSendDisabled(String errorText) {
        mFixture.assertError(errorText);
        assertThat(mFixture.getSendButton().isEnabled(), is(false));
    }
}
