package org.marketcetera.photon.views;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.Callable;

import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.marketcetera.client.brokers.BrokerStatus;
import org.marketcetera.client.brokers.BrokersStatus;
import org.marketcetera.event.MockEventTranslator;
import org.marketcetera.photon.BrokerManager;
import org.marketcetera.photon.Messages;
import org.marketcetera.photon.PhotonPlugin;
import org.marketcetera.photon.parser.TimeInForceImage;
import org.marketcetera.photon.preferences.CustomOrderFieldPage;
import org.marketcetera.quickfix.FIXMessageFactory;
import org.marketcetera.quickfix.FIXVersion;
import org.marketcetera.trade.BrokerID;
import org.marketcetera.trade.MSymbol;
import org.marketcetera.util.log.SLF4JLoggerProxy;

import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.field.DeliverToCompID;
import quickfix.field.MsgType;
import quickfix.field.OrdType;
import quickfix.field.OrderQty;
import quickfix.field.PrevClosePx;
import quickfix.field.SecurityType;
import quickfix.field.Side;
import quickfix.field.Symbol;
import quickfix.field.TimeInForce;


/**
 * Tests for the Stock Order Ticket view.
 * 
 * @author gmiller
 * @author andrei@lissovski.org
 */
public class StockOrderTicketViewTest extends ViewTestBase {

    private FIXMessageFactory msgFactory = FIXVersion.FIX_SYSTEM.getMessageFactory();
	private StockOrderTicketController controller;

	public StockOrderTicketViewTest(String name) {
		super(name);
	}

    @SuppressWarnings("restriction")
    @Override
	protected void setUp() throws Exception {
		super.setUp();
		IViewPart theTestView = getTestView();
		if (theTestView instanceof org.eclipse.ui.internal.ErrorEditorPart){
			fail("Test view was not created");
		}
		controller = PhotonPlugin.getDefault().getStockOrderTicketController();
		MockEventTranslator.reset();
    }

    @Override
	protected void tearDown() throws Exception {
    	MockEventTranslator.reset();
		super.tearDown();
		controller.clear();
	}

	public void testShowOrder() throws NoSuchFieldException, IllegalAccessException {
		StockOrderTicketView view = (StockOrderTicketView) getTestView();
		IOrderTicket ticket = view.getOrderTicket();
		
		assertEquals("", view.getOrderTicket().getQuantityText().getText());
		assertEquals("", view.getOrderTicket().getSideCombo().getText());
		assertEquals("", view.getOrderTicket().getPriceText().getText());
		assertEquals("", view.getOrderTicket().getSymbolText().getText());
		assertEquals("", view.getOrderTicket().getTifCombo().getText());

		Message message = msgFactory.newLimitOrder("1",
				Side.BUY, BigDecimal.TEN, new MSymbol("QWER"), BigDecimal.ONE,
				TimeInForce.DAY, null);
		controller.setOrderMessage(message);
		assertEquals("10", view.getOrderTicket().getQuantityText().getText());
		assertEquals("B", view.getOrderTicket().getSideCombo().getText());
		assertEquals("1", view.getOrderTicket().getPriceText().getText());
		assertEquals("QWER", view.getOrderTicket().getSymbolText().getText());
		assertEquals("DAY", view.getOrderTicket().getTifCombo().getText());

		message = msgFactory.newMarketOrder("2",
				Side.SELL_SHORT, BigDecimal.ONE, new MSymbol("QWER"),
				TimeInForce.AT_THE_OPENING, "123456789101112");
		controller.setOrderMessage(message);
		assertEquals("1", view.getOrderTicket().getQuantityText().getText());
		assertEquals("SS", view.getOrderTicket().getSideCombo().getText());
		assertEquals("MKT", view.getOrderTicket().getPriceText().getText());
		assertEquals("QWER", view.getOrderTicket().getSymbolText().getText());
		assertEquals("OPG", view.getOrderTicket().getTifCombo().getText());
		assertEquals("123456789101112", view.getOrderTicket().getAccountText().getText());

		// bug #393 - verify quantity doesn't have commas in them
		message = msgFactory.newMarketOrder("3",
				Side.SELL_SHORT, new BigDecimal(2000), new MSymbol("QWER"),
				TimeInForce.AT_THE_OPENING, "123456789101112");
		controller.setOrderMessage(message);
		assertEquals("2000", view.getOrderTicket().getQuantityText().getText());

        // verify Side/Symbol/TIF enabled for 'new order single' orders
        assertTrue("Side should be enabled", view.getOrderTicket().getSideCombo().isEnabled());
        assertTrue("TIF should be enabled", view.getOrderTicket().getTifCombo().isEnabled());
        assertTrue("Symbol should be enabled", view.getOrderTicket().getSymbolText().isEnabled());
        
        // Tests for bug #429
        ticket.getQuantityText().setFocus();
        assertFalse(ticket.getSideCombo().isFocusControl());
        view.handleSend();
        assertTrue(ticket.getSideCombo().isFocusControl());
    }


	public void testClear() throws NoSuchFieldException, IllegalAccessException {
		StockOrderTicketView view = (StockOrderTicketView) getTestView();
		OrderTicketModel model = this.controller.getOrderTicketModel();
		
		Message message = msgFactory.newLimitOrder("1",
				Side.BUY, BigDecimal.TEN, new MSymbol("QWER"), BigDecimal.ONE,
				TimeInForce.DAY, null);
		controller.setOrderMessage(message);

		IOrderTicket orderTicket = view.getOrderTicket();
		assertEquals("10", orderTicket.getQuantityText().getText());
		assertEquals("B", orderTicket.getSideCombo().getText());
		assertEquals("1", orderTicket.getPriceText().getText());
		assertEquals("QWER", orderTicket.getSymbolText().getText());
		assertEquals("DAY", orderTicket.getTifCombo().getText());

		model.clearOrderMessage();
		
		assertEquals("", orderTicket.getQuantityText().getText());
		assertEquals("", orderTicket.getSideCombo().getText());
		assertEquals("", orderTicket.getPriceText().getText());
		assertEquals("", orderTicket.getSymbolText().getText());
		assertEquals("", orderTicket.getTifCombo().getText());
	}
	
	public void testComboValues() throws Exception {
		StockOrderTicketView view = (StockOrderTicketView) getTestView();
		IOrderTicket orderTicket = view.getOrderTicket();
		assertEquals(3, orderTicket.getSideCombo().getItemCount());
		assertEquals("B", orderTicket.getSideCombo().getItem(0));
		assertEquals("S", orderTicket.getSideCombo().getItem(1));
		assertEquals("SS", orderTicket.getSideCombo().getItem(2));
		
		assertEquals(6, orderTicket.getTifCombo().getItemCount());
		int i = 0;
		for (TimeInForceImage image : TimeInForceImage.values()) {
			assertEquals(image.getImage(), orderTicket.getTifCombo().getItem(i++));
		}
	}

	public void testTypeNewOrder() throws Exception {
		OrderTicketModel stockOrderTicketModel = PhotonPlugin.getDefault().getStockOrderTicketModel();
		controller.clear();
		StockOrderTicketView view = (StockOrderTicketView) getTestView();
		// test for case sensitivity bug #196
		view.getOrderTicket().getSideCombo().setText("ss");
		view.getOrderTicket().getQuantityText().setText("4501");
		view.getOrderTicket().getSymbolText().setText("ASDF");
		view.getOrderTicket().getPriceText().setText("MKT");
		view.getOrderTicket().getTifCombo().setText("FOK");
		
		
		Message orderMessage = stockOrderTicketModel.getOrderMessage();
		assertEquals(MsgType.ORDER_SINGLE, orderMessage.getHeader().getString(MsgType.FIELD));
		assertEquals(Side.SELL_SHORT, orderMessage.getChar(Side.FIELD));
		assertEquals(4501, orderMessage.getInt(OrderQty.FIELD));
		assertEquals("ASDF", orderMessage.getString(Symbol.FIELD));
		assertEquals(OrdType.MARKET, orderMessage.getChar(OrdType.FIELD));
		assertEquals(TimeInForce.FILL_OR_KILL, orderMessage.getChar(TimeInForce.FIELD));
		assertEquals(SecurityType.COMMON_STOCK, orderMessage.getString(SecurityType.FIELD));
	}

	/**
	 * Tests that adding custom fields into preferences makes them appear in the Stock Order Ticket view.
	 */
	public void testAddCustomFieldsToPreferences() throws Exception {
		ScopedPreferenceStore prefStore = PhotonPlugin.getDefault().getPreferenceStore();
		prefStore.setValue(CustomOrderFieldPage.CUSTOM_FIELDS_PREFERENCE, 
                           String.format("%d=ABCD&%d=EFGH",
		                                 DeliverToCompID.FIELD,
		                                 PrevClosePx.FIELD));

		final StockOrderTicketView view = (StockOrderTicketView) getTestView();
		doDelay(new Callable<Boolean>() {
		    public Boolean call() 
		        throws Exception
		    {
		        return view.getOrderTicket().getCustomFieldsTableViewer().getTable().getItem(0).getText(1).length() > 0;
		    }
		});
        Table customFieldsTable = view.getOrderTicket().getCustomFieldsTableViewer().getTable();        
        assertEquals(2, customFieldsTable.getItemCount());        

        TableItem item0 = customFieldsTable.getItem(0);
		assertEquals(false, item0.getChecked());
		assertEquals("" + DeliverToCompID.FIELD, item0.getText(0));  //$NON-NLS-1$
		assertEquals("ABCD", item0.getText(1));  //$NON-NLS-1$

		TableItem item1 = customFieldsTable.getItem(1);
		assertEquals(false, item1.getChecked());
		assertEquals("" + PrevClosePx.FIELD, item1.getText(0));  //$NON-NLS-1$
		assertEquals("EFGH", item1.getText(1));  //$NON-NLS-1$
	}
	
	public void testSingleCustomField() throws Exception {
		OrderTicketModel stockOrderTicketModel = PhotonPlugin.getDefault().getStockOrderTicketModel();

		ScopedPreferenceStore prefStore = PhotonPlugin.getDefault().getPreferenceStore();
		prefStore.setValue(CustomOrderFieldPage.CUSTOM_FIELDS_PREFERENCE, 
				"" + DeliverToCompID.FIELD + "=ABCD");  //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$

		StockOrderTicketView view = (StockOrderTicketView) getTestView();

		Table customFieldsTable = view.getOrderTicket().getCustomFieldsTableViewer().getTable();
		customFieldsTable.getItem(0).setChecked(true);
		((CustomField) stockOrderTicketModel.getCustomFieldsList().get(0)).setEnabled(true);
		
		Message newMessage = msgFactory.newLimitOrder("1",  //$NON-NLS-1$
				Side.BUY, BigDecimal.TEN, new MSymbol("DREI"), BigDecimal.ONE,  //$NON-NLS-1$
				TimeInForce.DAY, null);
		stockOrderTicketModel.setOrderMessage(newMessage);

		
		stockOrderTicketModel.completeMessage();
		
		Message updatedMessage = stockOrderTicketModel.getOrderMessage();
		assertNotNull( updatedMessage );
		try {
			quickfix.Message.Header header = updatedMessage.getHeader();
			String value = header.getString(DeliverToCompID.FIELD);  // header field
			assertEquals("ABCD", value);  //$NON-NLS-1$
		} catch (FieldNotFound e) {
			fail();
		}
	}
	
	/**
	 * Tests that enabled custom fields (the header and body kind) are inserted into outgoing messages.
	 */
	public void testEnabledCustomFieldsAddedToMessage() throws Exception {
		doTestEnabledCustomFieldsAddedToMessage(msgFactory.newLimitOrder("1",  //$NON-NLS-1$
				Side.BUY, BigDecimal.TEN, new MSymbol("DREI"), BigDecimal.ONE,  //$NON-NLS-1$
				TimeInForce.DAY, null));
	}
	private void doTestEnabledCustomFieldsAddedToMessage(Message message) throws Exception {
		OrderTicketModel stockOrderTicketModel = PhotonPlugin.getDefault().getStockOrderTicketModel();

		ScopedPreferenceStore prefStore = PhotonPlugin.getDefault().getPreferenceStore();
		prefStore.setValue(CustomOrderFieldPage.CUSTOM_FIELDS_PREFERENCE, 
				"" + DeliverToCompID.FIELD + "=ABCD" + "&" + PrevClosePx.FIELD + "=EFGH");  //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$

		StockOrderTicketView view = (StockOrderTicketView) getTestView();

		Table customFieldsTable = view.getOrderTicket().getCustomFieldsTableViewer().getTable();
		customFieldsTable.getItem(0).setChecked(true);
		customFieldsTable.getItem(1).setChecked(true);
		((CustomField) stockOrderTicketModel.getCustomFieldsList().get(0)).setEnabled(true);
		((CustomField) stockOrderTicketModel.getCustomFieldsList().get(1)).setEnabled(true);
		
		stockOrderTicketModel.setOrderMessage(message);

		
		stockOrderTicketModel.completeMessage();
		
		Message updatedMessage = stockOrderTicketModel.getOrderMessage();
		assertNotNull( updatedMessage );
		quickfix.Message.Header header = updatedMessage.getHeader();
		String value = header.getString(DeliverToCompID.FIELD);  // header field
		assertEquals("ABCD", value);  //$NON-NLS-1$
		try {
			updatedMessage.getString(DeliverToCompID.FIELD);
			//shouldn't be in the body.
			fail();
		} catch (FieldNotFound e) {
			//expected result
		}
		value = updatedMessage.getString(PrevClosePx.FIELD);  // body field
		assertEquals("EFGH", value);  //$NON-NLS-1$
	}
	/**
	 * Test for bug #378. Verify that a cancel replace order with enabled custom fields
	 * doesn't fail with custom field already present errors.
	 * 
	 */
	public void testCancelReplaceWithCustomFieldsAddedToMessage() throws Exception {
		OrderTicketModel stockOrderTicketModel = PhotonPlugin.getDefault().getStockOrderTicketModel();
		//Run the test to get a message with custom fields in it
		doTestEnabledCustomFieldsAddedToMessage(
				msgFactory.newCancelReplaceFromMessage(
						msgFactory.newLimitOrder("1",  //$NON-NLS-1$
						Side.BUY, BigDecimal.TEN, 
						new MSymbol("DREI"), BigDecimal.ONE,  //$NON-NLS-1$
						TimeInForce.DAY, null)));
		//The message already has custom fields, try and complete the
		//message again. This shouldn't cause an exception in 
		//FIXMessageUtil.insertFieldIfMissing()
		stockOrderTicketModel.completeMessage();
		Message updatedMessage = stockOrderTicketModel.getOrderMessage();
		assertNotNull( updatedMessage );
		//Verify that the updated message has expected field values.
		quickfix.Message.Header header = updatedMessage.getHeader();
		String value = header.getString(DeliverToCompID.FIELD);  // header field
		assertEquals("ABCD", value);  //$NON-NLS-1$
		try {
			updatedMessage.getString(DeliverToCompID.FIELD);
			//shouldn't be in the body.
			fail();
		} catch (FieldNotFound e) {
			//expected result
		}
		value = updatedMessage.getString(PrevClosePx.FIELD);  // body field
		assertEquals("EFGH", value);  //$NON-NLS-1$
		//Now change the values of the custom fields and verify that they are updated
		ScopedPreferenceStore prefStore = PhotonPlugin.getDefault().getPreferenceStore();
		prefStore.setValue(CustomOrderFieldPage.CUSTOM_FIELDS_PREFERENCE, 
				"" + DeliverToCompID.FIELD + "=PQRS" + "&" + PrevClosePx.FIELD + "=XYZ");  //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		//Since new fields are added we need to re-enable them
		((CustomField) stockOrderTicketModel.getCustomFieldsList().get(0)).setEnabled(true);
		((CustomField) stockOrderTicketModel.getCustomFieldsList().get(1)).setEnabled(true);

		stockOrderTicketModel.completeMessage();
		updatedMessage = stockOrderTicketModel.getOrderMessage();
		assertNotNull( updatedMessage );
		//Verify that the updated message has expected field values.
		header = updatedMessage.getHeader();
		value = header.getString(DeliverToCompID.FIELD);  // header field
		assertEquals("PQRS", value);  //$NON-NLS-1$
		try {
			updatedMessage.getString(DeliverToCompID.FIELD);
			//shouldn't be in the body.
			fail();
		} catch (FieldNotFound e) {
			//expected result
		}
		value = updatedMessage.getString(PrevClosePx.FIELD);  // body field
		assertEquals("XYZ", value);  //$NON-NLS-1$
	}
	
	/**
	 * Tests that disabled custom fields (the header and body kind) are <em>not</em> inserted into outgoing messages.
	 */
	public void testDisabledCustomFieldsNotAddedToMessage() throws Exception {
		OrderTicketModel stockOrderTicketModel = PhotonPlugin.getDefault().getStockOrderTicketModel();

		ScopedPreferenceStore prefStore = PhotonPlugin.getDefault().getPreferenceStore();
		prefStore.setValue(CustomOrderFieldPage.CUSTOM_FIELDS_PREFERENCE, 
				"" + DeliverToCompID.FIELD + "=ABCDE" + "&" + PrevClosePx.FIELD + "=EFGH");  //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		
		StockOrderTicketView view = (StockOrderTicketView) getTestView();
		Table customFieldsTable = view.getOrderTicket().getCustomFieldsTableViewer().getTable();
		TableItem item0 = customFieldsTable.getItem(0);
		item0.setChecked(false);
		TableItem item1 = customFieldsTable.getItem(1);
		item1.setChecked(false);
		
		Message newMessage = msgFactory.newLimitOrder("1",  //$NON-NLS-1$
				Side.BUY, BigDecimal.TEN, new MSymbol("DREI"), BigDecimal.ONE,  //$NON-NLS-1$
				TimeInForce.DAY, null);
		stockOrderTicketModel.setOrderMessage(newMessage);
		
		stockOrderTicketModel.completeMessage();
		
		Message updatedMessage = stockOrderTicketModel.getOrderMessage();
		try {
			updatedMessage.getHeader().getString(DeliverToCompID.FIELD);  // header field
			fail();
		} catch (FieldNotFound e) {
			// expected behavior
		}
		try {
			updatedMessage.getString(PrevClosePx.FIELD);  // body field
			fail();
		} catch (FieldNotFound e) {
			// expected behavior
		}
	}
	
	@Override
	protected String getViewID() {
		return StockOrderTicketView.ID;
	}
	
	/** Verify that MarketOnClose orders are translated correctly 
	 * Setup the outgoing order to be Market and CLO (at the close)
	 * in FIX.4.2 Photon
	 */
	public void testMarketOnCloseCorrect() throws Exception {
		OrderTicketModel stockOrderTicketModel = PhotonPlugin.getDefault().getStockOrderTicketModel();

		StockOrderTicketView view = (StockOrderTicketView) getTestView();
		stockOrderTicketModel.clearOrderMessage();

		view.getOrderTicket().getSideCombo().setText("S");
		view.getOrderTicket().getQuantityText().setText("45");
		view.getOrderTicket().getSymbolText().setText("ASDF");
		view.getOrderTicket().getPriceText().setText("MKT");
		view.getOrderTicket().getTifCombo().setText("CLO");

		Message sentMessage = stockOrderTicketModel.getOrderMessage();
		assertNotNull( sentMessage );

		assertEquals(TimeInForce.AT_THE_CLOSE, sentMessage.getChar(TimeInForce.FIELD));
		assertEquals(OrdType.MARKET, sentMessage.getChar(OrdType.FIELD));
	}

	public void testMKTOrderCaseInsensitive() throws Exception {
		OrderTicketModel stockOrderTicketModel = PhotonPlugin.getDefault().getStockOrderTicketModel();

		StockOrderTicketView view = (StockOrderTicketView) getTestView();
		stockOrderTicketModel.clearOrderMessage();

		view.getOrderTicket().getSideCombo().setText("S");
		view.getOrderTicket().getQuantityText().setText("45");
		view.getOrderTicket().getSymbolText().setText("ASDF");
		view.getOrderTicket().getPriceText().setText("mkt");
		view.getOrderTicket().getTifCombo().setText("DAY");

		Message sentMessage = stockOrderTicketModel.getOrderMessage();
		assertNotNull( sentMessage );
		assertEquals(OrdType.MARKET, sentMessage.getChar(OrdType.FIELD));
	}

    /** Bug #421 - verify that Symbol/Side/TIF aren't enabled for cancel/replace orders */
    public void testFieldsDisabledOnCancelReplace() throws Exception {
        IStockOrderTicket ticket = (IStockOrderTicket) ((StockOrderTicketView)getTestView()).getOrderTicket();

        assertTrue("Side should be enabled", ticket.getSideCombo().isEnabled());
        assertTrue("TIF should be enabled", ticket.getTifCombo().isEnabled());
        assertTrue("Symbol should be enabled", ticket.getSymbolText().isEnabled());
        assertTrue("Broker should be enabled", ticket.getBrokerCombo().isEnabled());
        ticket.getSideCombo().setFocus();
        assertFalse(ticket.getPriceText().isFocusControl());
        
        Message buy = msgFactory.newLimitOrder("1",
                Side.BUY, BigDecimal.TEN, new MSymbol("QWER"), BigDecimal.ONE,
                TimeInForce.DAY, null);
        Message cxr = msgFactory.newCancelReplaceFromMessage(buy);
        controller.setOrderMessage(cxr);
        controller.setBrokerId(null);
        assertEquals("10", ticket.getQuantityText().getText());
        assertEquals("B", ticket.getSideCombo().getText());
        assertEquals("1", ticket.getPriceText().getText());
        assertEquals("QWER", ticket.getSymbolText().getText());
        assertEquals("DAY", ticket.getTifCombo().getText());
        assertEquals(Messages.BROKER_MANAGER_AUTO_SELECT.getText(), ticket.getBrokerCombo().getText());

        // verify Side/Symbol/TIF are disabled for cancel/replace
        assertFalse("Side should not be enabled", ticket.getSideCombo().isEnabled());
        assertFalse("TIF should not be enabled", ticket.getTifCombo().isEnabled());
        assertFalse("Symbol should not be enabled", ticket.getSymbolText().isEnabled());
        assertFalse("Broker should not be enabled", ticket.getBrokerCombo().isEnabled());
        
        assertTrue(ticket.getForm().getText().contains("Replace"));
        // test for bug #438
        assertTrue(ticket.getPriceText().isFocusControl());
        
        // verify enabled after cancel
        controller.clear();
        assertTrue("Side should be enabled", ticket.getSideCombo().isEnabled());
        assertTrue("TIF should be enabled", ticket.getTifCombo().isEnabled());
        assertTrue("Symbol should be enabled", ticket.getSymbolText().isEnabled());
        assertTrue("Broker should be enabled", ticket.getBrokerCombo().isEnabled());
    }
    
    /**  verify the side combo is focused after clear - ie after send or cancel
     * This test is tough - it sometimes passes, sometimes fails. Seems to work in "real app".
     * Not sure what the right test should be then
     */
    public void testSideComboFocusedAfterClear()
    {
        IStockOrderTicket ticket = (IStockOrderTicket) ((StockOrderTicketView)getTestView()).getOrderTicket();
    	controller.clear();
        SLF4JLoggerProxy.info(this,
                              "side is enabled/has focus: {}/{}",
                              ticket.getSideCombo().isEnabled(),
                              ticket.getSideCombo().isFocusControl());
    	assertTrue("side is not focused",
    	           ticket.getSideCombo().isFocusControl());
    }
    
    public void testBrokerId() {
    	IStockOrderTicket ticket = (IStockOrderTicket) ((StockOrderTicketView)getTestView()).getOrderTicket();
    	BrokerStatus status1 = new BrokerStatus("Goldman Sachs", new BrokerID("gs"), true);
		BrokerStatus status2 = new BrokerStatus("Exchange Simulator", new BrokerID("metc"), false);
		BrokersStatus statuses =  new BrokersStatus(Arrays.asList(status1, status2));
    	BrokerManager.getCurrent().setBrokersStatus(statuses);
        Message buy = msgFactory.newLimitOrder("1",
                Side.BUY, BigDecimal.TEN, new MSymbol("QWER"), BigDecimal.ONE,
                TimeInForce.DAY, null);
        controller.setOrderMessage(buy);
        controller.setBrokerId("gs");
        assertEquals("Goldman Sachs (gs)", ticket.getBrokerCombo().getText());
        controller.setBrokerId(null);
        assertEquals(Messages.BROKER_MANAGER_AUTO_SELECT.getText(), ticket.getBrokerCombo().getText());
        controller.setOrderMessage(buy);
        controller.setBrokerId("gs");
        controller.clear();
        // last broker is saved
        assertEquals("Goldman Sachs (gs)", ticket.getBrokerCombo().getText());
        controller.setBrokerId(null);
        BrokerManager.getCurrent().setBrokersStatus(new BrokersStatus(new ArrayList<BrokerStatus>()));
    }
}
