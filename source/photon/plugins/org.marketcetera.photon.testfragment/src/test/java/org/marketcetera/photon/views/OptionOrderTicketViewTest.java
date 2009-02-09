package org.marketcetera.photon.views;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;

import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.marketcetera.client.brokers.BrokerStatus;
import org.marketcetera.client.brokers.BrokersStatus;
import org.marketcetera.core.ClassVersion;
import org.marketcetera.event.MockEventTranslator;
import org.marketcetera.marketdata.FeedException;
import org.marketcetera.photon.BrokerManager;
import org.marketcetera.photon.Messages;
import org.marketcetera.photon.PhotonPlugin;
import org.marketcetera.photon.preferences.CustomOrderFieldPage;
import org.marketcetera.quickfix.FIXMessageFactory;
import org.marketcetera.quickfix.FIXMessageUtilTest;
import org.marketcetera.quickfix.FIXVersion;
import org.marketcetera.trade.BrokerID;
import org.marketcetera.trade.MSymbol;

import quickfix.FieldNotFound;
import quickfix.Group;
import quickfix.Message;
import quickfix.field.CFICode;
import quickfix.field.DeliverToCompID;
import quickfix.field.MaturityDate;
import quickfix.field.MaturityMonthYear;
import quickfix.field.MsgType;
import quickfix.field.OrdType;
import quickfix.field.OrderQty;
import quickfix.field.PrevClosePx;
import quickfix.field.PutOrCall;
import quickfix.field.SecurityReqID;
import quickfix.field.SecurityRequestResult;
import quickfix.field.SecurityResponseID;
import quickfix.field.SecurityType;
import quickfix.field.Side;
import quickfix.field.StrikePrice;
import quickfix.field.Symbol;
import quickfix.field.TimeInForce;
import quickfix.field.UnderlyingSymbol;
import quickfix.fix44.DerivativeSecurityList;

/**
 * @author toli
 * @version $Id$
 */

@ClassVersion("$Id$")
public class OptionOrderTicketViewTest extends ViewTestBase {
    private FIXMessageFactory msgFactory = FIXVersion.FIX_SYSTEM.getMessageFactory();
	private OptionOrderTicketController controller;
	
	public OptionOrderTicketViewTest(String name) {
		super(name);
	}

    @SuppressWarnings("restriction")
    @Override
	protected void setUp() throws Exception {
		super.setUp();
		IViewPart theTestView = getTestView();
		if (theTestView instanceof org.eclipse.ui.internal.ErrorViewPart){
			fail("Test view was not created");
		}
		
		
		controller = PhotonPlugin.getDefault().getOptionOrderTicketController();
		MockEventTranslator.reset();
	}
	
	@Override
	protected void tearDown() throws Exception {
		MockEventTranslator.reset();
		super.tearDown();
	}

	@Override
	protected String getViewID() {
		return OptionOrderTicketView.ID;
	}

	/**
	 * Ensure that the option order ticket controller is primed for use and then
	 * call showMessage on it. When a user enters an option root, they cause a
	 * subscription to the specific option contracts.
	 */
	private void showMessageInOptionTicket(IOptionOrderTicket ticket, Message message, String broker,
			OptionOrderTicketController optController, String optionRoot,
			String[] callSpecifiers, String [] putSpecifiers, BigDecimal[] strikePrices) {
		// Set an option root before simulating the subscription response
		ticket.getSymbolText().setText(optionRoot);
		// Subscription response has the contract symbols (create a fake quote and send that through)
		Message dsl = createDummySecurityList(optionRoot, callSpecifiers, putSpecifiers, strikePrices);
        optController.handleDerivativeSecurityList(dsl);
        // Show the message for the specific contract
        optController.setOrderMessage(message);
        optController.setBrokerId(broker);
        OptionOrderTicketModel orderTicketModel = (OptionOrderTicketModel)optController.getOrderTicketModel();
		(orderTicketModel).updateOptionInfo();
	}
	
	public void testListBug() throws Exception {
		IOptionOrderTicket ticket = ((OptionOrderTicketView)getTestView()).getOptionOrderTicket();
		{
	        /**
			 * Note the difference between an option contract symbol ("MSQ+GE") and
			 * an option root ("MSQ"). An OptionOrderTicket has both.
			 */
			final String optionRoot = "IBM";
			final String callContractSpecifier = "AB";
			final String putContractSpecifier = "CD";
			Message message = FIXMessageUtilTest.createOptionNOS(optionRoot, callContractSpecifier, "200701", new BigDecimal(10),
					PutOrCall.CALL, new BigDecimal(1), new BigDecimal(10), Side.BUY, msgFactory);
	
			ticket.getSymbolText().setText(optionRoot);
			Message dsl = createDummySecurityList(optionRoot, (new String[] { callContractSpecifier }), (new String[] { putContractSpecifier }), (new BigDecimal[] { BigDecimal.ONE }));
			controller.handleDerivativeSecurityList(dsl);
			controller.setOrderMessage(message);
			((OptionOrderTicketModel)controller.getOrderTicketModel()).updateOptionInfo();
		}
		{
			controller.clear();

			ticket.getExpireMonthCombo().setText("JAN");

		}
		{
	        /**
	         * Note the difference between an option contract symbol ("MSQ+GE") and
	         * an option root ("MSQ"). An OptionOrderTicket has both.
	         */
	        final String optionRoot = "MSQ";
	        final String callContractSpecifier = "GE";
	        final String putContractSpecifier = "RE";
	        Message buy = FIXMessageUtilTest.createOptionNOS(optionRoot, callContractSpecifier, "200701", new BigDecimal(10),
	                PutOrCall.CALL, new BigDecimal(1), new BigDecimal(10), Side.BUY, msgFactory);
	
	        Message cxr = msgFactory.newCancelReplaceFromMessage(buy);
			ticket.getSymbolText().setText(optionRoot);
			Message dsl = createDummySecurityList(optionRoot, (new String[] { callContractSpecifier }), (new String[] {putContractSpecifier}), (new BigDecimal[] { BigDecimal.TEN }));
			controller.handleDerivativeSecurityList(dsl);
			controller.setOrderMessage(cxr);
			((OptionOrderTicketModel)controller.getOrderTicketModel()).updateOptionInfo();
		}
	}
	
	public void testShowOrder() throws NoSuchFieldException, IllegalAccessException {
		IOptionOrderTicket ticket = ((OptionOrderTicketView)getTestView()).getOptionOrderTicket();
        /**
		 * Note the difference between an option contract symbol ("MSQ+GE") and
		 * an option root ("MSQ"). An OptionOrderTicket has both.
		 */
		final String optionRoot = "MSQ";
		final String callContractSpecifier = "GE";
		final String callContractSymbol = optionRoot + "+" + callContractSpecifier;
		final String putContractSpecifier = "RE";
		Message message = FIXMessageUtilTest.createOptionNOS(optionRoot, callContractSpecifier, "200701", new BigDecimal(10),
				PutOrCall.CALL, new BigDecimal(1), new BigDecimal(10), Side.BUY, msgFactory);

		controller.getOrderTicketModel().removeAllCachedOptionData();
		controller.clear();
		
		assertEquals(0, ticket.getExpireMonthCombo().getItemCount());
		assertEquals(0, ticket.getExpireYearCombo().getItemCount());
		assertEquals(0, ticket.getStrikePriceCombo().getItemCount());

		showMessageInOptionTicket(ticket, message, null, controller, optionRoot,
				new String[] { callContractSpecifier }, new String[] { putContractSpecifier },  new BigDecimal[] { BigDecimal.TEN });
        
		assertEquals(1, ticket.getExpireMonthCombo().getItemCount());
		assertEquals(1, ticket.getExpireYearCombo().getItemCount());
		assertEquals(1, ticket.getStrikePriceCombo().getItemCount());
		
        assertEquals("10", ticket.getQuantityText().getText());
		assertEquals("B", ticket.getSideCombo().getText());
		assertEquals("1", ticket.getPriceText().getText());
		assertEquals(callContractSymbol, ticket.getOptionSymbolText().getText());
		assertEquals("DAY", ticket.getTifCombo().getText());
		
		// bug #393 - verify quantity doesn't have commas in them
		message = msgFactory.newMarketOrder("3",
				Side.SELL_SHORT, new BigDecimal(2000), new MSymbol(callContractSymbol),
				TimeInForce.AT_THE_OPENING, "123456789101112");
		message.setField(new MaturityDate());
		message.setField(new PutOrCall(PutOrCall.CALL));
		controller.setOrderMessage(message);
		assertEquals("2000", ticket.getQuantityText().getText());

        // verify Side/Symbol/TIF enabled for 'new order single' orders
        assertTrue("Side should be enabled", ticket.getSideCombo().isEnabled());
        assertTrue("TIF should be enabled", ticket.getTifCombo().isEnabled());
        assertTrue("Symbol should be enabled", ticket.getSymbolText().isEnabled());
        
        // test for bug #481
        assertEquals(2, ticket.getSideCombo().getItemCount());
        assertEquals("B", ticket.getSideCombo().getItem(0));
        assertEquals("S", ticket.getSideCombo().getItem(1));
    }

	
	public void testUpdateOptionControls() throws FeedException {
		try {
			IOptionOrderTicket ticket = ((OptionOrderTicketView)getTestView()).getOptionOrderTicket();
	
			OptionOrderTicketModel optionOrderTicketModel = controller.getOrderTicketModel();
			optionOrderTicketModel.removeAllCachedOptionData();
			controller.clear();
			
			assertEquals(0, ticket.getExpireMonthCombo().getItemCount());
			assertEquals(null, optionOrderTicketModel.getCurrentOptionSymbol().getValue());
			
			final String optionRoot = "MRK";
			final String callContractSpecifier = "GA";
			final String putContractSpecifier = "RA";
			
			DerivativeSecurityList securityList = createDummySecurityList(optionRoot, new String[] { callContractSpecifier }, new String[] { putContractSpecifier }, new BigDecimal[] { BigDecimal.TEN });
	//		securityList.setField(new SecurityReqID(((FIXCorrelationFieldSubscription)controller.getDerivativeSecurityListSubscription()).getCorrelationFieldValue()));
	
			controller.handleDerivativeSecurityList(securityList);
			
			optionOrderTicketModel.updateOptionInfo();
	
			assertEquals(1, ticket.getExpireMonthCombo().getItemCount());
		} catch (Exception ex){
			System.out.println(""+ex);
		}
	}
	
	public void testTypeNewOrder() throws Exception {
		OptionOrderTicketModel optionOrderTicketModel = PhotonPlugin.getDefault().getOptionOrderTicketModel();
		
		OptionOrderTicketView ticketView = (OptionOrderTicketView) getTestView();
		IOptionOrderTicket ticket = ticketView.getOptionOrderTicket();

		controller.clear();

		assertEquals("", ticket.getSideCombo().getText());
		assertEquals("", ticket.getQuantityText().getText());
		assertEquals("", ticket.getSymbolText().getText());
		assertEquals("", ticket.getExpireMonthCombo().getText());
		assertEquals("", ticket.getExpireYearCombo().getText());
		assertEquals("", ticket.getStrikePriceCombo().getText());
		assertEquals("", ticket.getPutOrCallCombo().getText());
		assertEquals("", ticket.getPriceText().getText());
		assertEquals("", ticket.getTifCombo().getText());
		assertEquals("", ticket.getOpenCloseCombo().getText());
		
		ticket.getSideCombo().setText("S");
		ticket.getQuantityText().setText("45");
		ticket.getSymbolText().setText("ABC");
		ticket.getExpireMonthCombo().setText("JAN");
		ticket.getExpireYearCombo().setText("2012");
		ticket.getStrikePriceCombo().setText("25");
		ticket.getPutOrCallCombo().setText("P");
		ticket.getPriceText().setText("MKT");
		ticket.getTifCombo().setText("FOK");
		ticket.getOpenCloseCombo().setText("Open");

		optionOrderTicketModel.completeMessage();

		//Test for bug #497
		ticket.getPutOrCallCombo().setText("z");
		assertFalse(ticket.getSendButton().isEnabled());
		ticket.getPutOrCallCombo().setText("P");
		assertTrue(ticket.getSendButton().isEnabled());
		
		Message orderMessage = optionOrderTicketModel.getOrderMessage();
		assertEquals(MsgType.ORDER_SINGLE, orderMessage.getHeader().getString(MsgType.FIELD));
		assertEquals(Side.SELL, orderMessage.getChar(Side.FIELD));
		assertEquals(45, orderMessage.getInt(OrderQty.FIELD));
		assertEquals(OrdType.MARKET, orderMessage.getChar(OrdType.FIELD));
		assertEquals("201201", orderMessage.getString(MaturityMonthYear.FIELD));
		assertEquals(TimeInForce.FILL_OR_KILL, orderMessage.getChar(TimeInForce.FIELD));
		assertEquals(SecurityType.OPTION, orderMessage.getString(SecurityType.FIELD));
		assertEquals("ABC", orderMessage.getString(Symbol.FIELD));
	}

  /**
	 * Tests that adding custom fields into preferences makes them appear in the Option Stock Order Ticket view.
	 */
	public void testAddCustomFieldsToPreferences() throws Exception {
		ScopedPreferenceStore prefStore = PhotonPlugin.getDefault().getPreferenceStore();
		prefStore.setValue(CustomOrderFieldPage.CUSTOM_FIELDS_PREFERENCE,
				"" + DeliverToCompID.FIELD + "=ABCD" + "&" + PrevClosePx.FIELD + "=EFGH");  //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$

		OptionOrderTicketView ticketView = (OptionOrderTicketView) getTestView();
		IOptionOrderTicket ticket = ticketView.getOptionOrderTicket();
		Table customFieldsTable = ticket.getCustomFieldsTableViewer().getTable();

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

/**
	 * Tests that enabled custom fields (the header and body kind) are inserted into outgoing messages.
	 */
	public void testEnabledCustomFieldsAddedToMessage() throws Exception {
		OptionOrderTicketModel optionOrderTicketModel = PhotonPlugin.getDefault().getOptionOrderTicketModel();

		ScopedPreferenceStore prefStore = PhotonPlugin.getDefault().getPreferenceStore();
		prefStore.setValue(CustomOrderFieldPage.CUSTOM_FIELDS_PREFERENCE,
				"" + DeliverToCompID.FIELD + "=ABCD" + "&" + PrevClosePx.FIELD + "=EFGH");  //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$

		OptionOrderTicketView ticketView = (OptionOrderTicketView) getTestView();

		Table customFieldsTable = ticketView.getOrderTicket().getCustomFieldsTableViewer().getTable();
		customFieldsTable.getItem(0).setChecked(true);
		customFieldsTable.getItem(1).setChecked(true);
		((CustomField) optionOrderTicketModel.getCustomFieldsList().get(0)).setEnabled(true);
		((CustomField) optionOrderTicketModel.getCustomFieldsList().get(1)).setEnabled(true);

		Message newMessage = msgFactory.newLimitOrder("1",  //$NON-NLS-1$
				Side.BUY, BigDecimal.TEN, new MSymbol("DREI"), BigDecimal.ONE,  //$NON-NLS-1$
				TimeInForce.DAY, null);
		optionOrderTicketModel.setOrderMessage(newMessage);
		optionOrderTicketModel.completeMessage();


		Message updatedMessage = optionOrderTicketModel.getOrderMessage();
		assertNotNull( updatedMessage );
		try {
			quickfix.Message.Header header = updatedMessage.getHeader();
			String value = header.getString(DeliverToCompID.FIELD);  // header field
			assertEquals("ABCD", value);  //$NON-NLS-1$
		} catch (FieldNotFound e) {
			fail();
		}
		try {
			updatedMessage.getString(DeliverToCompID.FIELD);
			//shouldn't be in the body.
			fail();
		} catch (FieldNotFound e) {
			//expected result
		}
		try {
			String value = updatedMessage.getString(PrevClosePx.FIELD);  // body field
			assertEquals("EFGH", value);  //$NON-NLS-1$
		} catch (FieldNotFound e) {
			fail();
		}
	}

    /** Bug #421 - verify that Symbol/Side/TIF aren't enabled for cancel/replace orders */
    public void testFieldsDisabledOnCancelReplace() throws Exception {
        IOptionOrderTicket ticket = ((OptionOrderTicketView)getTestView()).getOptionOrderTicket();
        /**
         * Note the difference between an option contract symbol ("MSQ+GE") and
         * an option root ("MSQ"). An OptionOrderTicket has both.
         */
        final String optionRoot = "MSQ";
        final String callContractSpecifier = "GE";
        final String callContractSymbol = optionRoot + "+" + callContractSpecifier;
        final String putContractSpecifier = "RE";
        Message buy = FIXMessageUtilTest.createOptionNOS(optionRoot, callContractSpecifier, "200701", new BigDecimal(10),
                PutOrCall.CALL, new BigDecimal(1), new BigDecimal(10), Side.BUY, msgFactory);

        Message cxr = msgFactory.newCancelReplaceFromMessage(buy);
        showMessageInOptionTicket(ticket, cxr, null, controller, optionRoot,
                new String[] { callContractSpecifier }, new String[] {putContractSpecifier}, new BigDecimal[] { BigDecimal.TEN });

        assertEquals("10", ticket.getQuantityText().getText());
        assertEquals("B", ticket.getSideCombo().getText());
        assertEquals("1", ticket.getPriceText().getText());
        assertEquals(callContractSymbol, ticket.getOptionSymbolText().getText());
        assertEquals("MSQ+GE", ticket.getSymbolText().getText());
        assertEquals("DAY", ticket.getTifCombo().getText());
        assertEquals(Messages.BROKER_MANAGER_AUTO_SELECT.getText(), ticket.getBrokerCombo().getText());

        // verify Side/Symbol/TIF are disabled for cancel/replace
        assertFalse("Side should not be enabled", ticket.getSideCombo().isEnabled());
        assertFalse("TIF should not be enabled", ticket.getTifCombo().isEnabled());
        assertFalse("Broker should not be enabled", ticket.getBrokerCombo().isEnabled());
        assertFalse("Symbol should not be enabled", ticket.getSymbolText().isEnabled());
        assertFalse("Expiry Month should not be enabled", ticket.getExpireMonthCombo().isEnabled());
        assertFalse("Expiry year should not be enabled", ticket.getExpireYearCombo().isEnabled());
        assertFalse("PutOrCall should not be enabled", ticket.getPutOrCallCombo().isEnabled());
        assertFalse("Strike should not be enabled", ticket.getStrikePriceCombo().isEnabled());

        // verify enabled after cancel
        controller.clear();
        assertTrue("Side should be enabled", ticket.getSideCombo().isEnabled());
        assertTrue("TIF should be enabled", ticket.getTifCombo().isEnabled());
        assertTrue("Broker should be enabled", ticket.getBrokerCombo().isEnabled());
        assertTrue("Symbol should be enabled", ticket.getSymbolText().isEnabled());
        assertTrue("Expiry Month should be enabled", ticket.getExpireMonthCombo().isEnabled());
        assertTrue("Expiry year should be enabled", ticket.getExpireYearCombo().isEnabled());
        assertTrue("PutOrCall should be enabled", ticket.getPutOrCallCombo().isEnabled());
        assertTrue("Strike should be enabled", ticket.getStrikePriceCombo().isEnabled());
    }

    /**
	 * Tests that disabled custom fields (the header and body kind) are <em>not</em> inserted into outgoing messages.
	 */
	public void testDisabledCustomFieldsNotAddedToMessage() throws Exception {
		OptionOrderTicketModel optionOrderTicketModel = PhotonPlugin.getDefault().getOptionOrderTicketModel();

		ScopedPreferenceStore prefStore = PhotonPlugin.getDefault().getPreferenceStore();
		prefStore.setValue(CustomOrderFieldPage.CUSTOM_FIELDS_PREFERENCE,
				"" + DeliverToCompID.FIELD + "=ABCD" + "&" + PrevClosePx.FIELD + "=EFGH");  //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$

		OptionOrderTicketView ticketView = (OptionOrderTicketView) getTestView();

		Table customFieldsTable = ticketView.getOrderTicket().getCustomFieldsTableViewer().getTable();
		customFieldsTable.getItem(0).setChecked(false);
		customFieldsTable.getItem(1).setChecked(false);
		((CustomField) optionOrderTicketModel.getCustomFieldsList().get(0)).setEnabled(false);
		((CustomField) optionOrderTicketModel.getCustomFieldsList().get(1)).setEnabled(false);

		Message newMessage = msgFactory.newLimitOrder("1",  //$NON-NLS-1$
				Side.BUY, BigDecimal.TEN, new MSymbol("DREI"), BigDecimal.ONE,  //$NON-NLS-1$
				TimeInForce.DAY, null);
		optionOrderTicketModel.setOrderMessage(newMessage);
		
		optionOrderTicketModel.completeMessage();
		
		Message updatedMessage = optionOrderTicketModel.getOrderMessage();
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
	

	public void testBindArbitraryOptionOrder() throws Exception {
		OptionOrderTicketView ticketView = (OptionOrderTicketView) getTestView();
		IOptionOrderTicket ticket = ticketView.getOptionOrderTicket();

		controller.clear();
		assertEquals("", ticket.getSymbolText().getText());
		
		String messageString = "8=FIX.4.29=13435=D11=1184285237034-capybara/192.168.0.10121=138=1040=244=4.554=155=IBM59=060=20070713-00:12:56.781200=200710201=1202=2510=169";
		Message message = new Message(messageString);
		controller.setOrderMessage(message);
		assertEquals("B", ticket.getSideCombo().getText());
		assertEquals("10", ticket.getQuantityText().getText());
		assertEquals("IBM", ticket.getSymbolText().getText());
		assertEquals("OCT", ticket.getExpireMonthCombo().getText());
		assertEquals("07", ticket.getExpireYearCombo().getText());
		assertEquals("25", ticket.getStrikePriceCombo().getText());
		assertEquals("C", ticket.getPutOrCallCombo().getText());
		assertEquals("4.5", ticket.getPriceText().getText());
	}

    public static DerivativeSecurityList createDummySecurityList(String symbol, String[] callSuffixes, String [] putSuffixes, BigDecimal[] strikePrices) {
        SecurityRequestResult resultCode = new SecurityRequestResult(SecurityRequestResult.VALID_REQUEST);
        DerivativeSecurityList responseMessage = new DerivativeSecurityList();
        responseMessage.setField(new SecurityReqID("bob"));
        responseMessage.setField(new SecurityResponseID("123"));

        responseMessage.setField(new UnderlyingSymbol(symbol));
        for (int i = 0; i < callSuffixes.length; i++) {
        	MSymbol putSymbol = new MSymbol(symbol+"+"+putSuffixes[i]);
            // put first
            Group optionGroup = new DerivativeSecurityList.NoRelatedSym();
            optionGroup.setField(new Symbol(putSymbol.toString()));
            optionGroup.setField(new StrikePrice(strikePrices[i]));
            optionGroup.setField(new CFICode("OPASPS"));
            optionGroup.setField(new MaturityMonthYear("200801"));
            optionGroup.setField(new MaturityDate("20080122"));
            responseMessage.addGroup(optionGroup);

            MSymbol callSymbol = new MSymbol(symbol + "+" + callSuffixes[i]);
            // now call
            optionGroup.setField(new Symbol(callSymbol.toString()));
            optionGroup.setField(new StrikePrice(strikePrices[i]));
            optionGroup.setField(new CFICode("OCASPS"));
            optionGroup.setField(new MaturityMonthYear("200801"));
            optionGroup.setField(new MaturityDate("20080122"));
            responseMessage.addGroup(optionGroup);

        }
        responseMessage.setField(resultCode);
        return responseMessage;
    }
    
    public void testBrokerId() {
    	IOptionOrderTicket ticket = ((OptionOrderTicketView)getTestView()).getOptionOrderTicket();
    	BrokerStatus status1 = new BrokerStatus("Goldman Sachs", new BrokerID("gs"), true);
		BrokerStatus status2 = new BrokerStatus("Exchange Simulator", new BrokerID("metc"), false);
		BrokersStatus statuses =  new BrokersStatus(Arrays.asList(status1, status2));
    	BrokerManager.getCurrent().setBrokersStatus(statuses);
        final String optionRoot = "MSQ";
        final String callContractSpecifier = "GE";
        Message buy = FIXMessageUtilTest.createOptionNOS(optionRoot, callContractSpecifier, "200701", new BigDecimal(10),
                PutOrCall.CALL, new BigDecimal(1), new BigDecimal(10), Side.BUY, msgFactory);
        controller.setOrderMessage(buy);
        controller.setBrokerId(null);
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


