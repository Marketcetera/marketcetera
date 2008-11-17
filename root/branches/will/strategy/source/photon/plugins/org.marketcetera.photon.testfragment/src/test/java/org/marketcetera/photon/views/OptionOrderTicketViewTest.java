package org.marketcetera.photon.views;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.Callable;

import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.IDFactory;
import org.marketcetera.core.MSymbol;
import org.marketcetera.event.MockEventTranslator;
import org.marketcetera.marketdata.FeedException;
import org.marketcetera.photon.PhotonPlugin;
import org.marketcetera.photon.marketdata.OptionMessageHolder;
import org.marketcetera.photon.preferences.CustomOrderFieldPage;
import org.marketcetera.quickfix.FIXMessageFactory;
import org.marketcetera.quickfix.FIXMessageUtilTest;
import org.marketcetera.quickfix.FIXVersion;

import quickfix.FieldMap;
import quickfix.FieldNotFound;
import quickfix.Group;
import quickfix.Message;
import quickfix.field.CFICode;
import quickfix.field.DeliverToCompID;
import quickfix.field.LastPx;
import quickfix.field.MDEntryPx;
import quickfix.field.MDEntrySize;
import quickfix.field.MDEntryType;
import quickfix.field.MaturityDate;
import quickfix.field.MaturityMonthYear;
import quickfix.field.MsgType;
import quickfix.field.NoMDEntries;
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
import quickfix.fix44.MarketDataSnapshotFullRefresh;

/**
 * @author toli
 * @version $Id$
 */

@ClassVersion("$Id$")
public class OptionOrderTicketViewTest extends ViewTestBase {
    private FIXMessageFactory msgFactory = FIXVersion.FIX42.getMessageFactory();
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
	private void showMessageInOptionTicket(IOptionOrderTicket ticket, Message message,
			OptionOrderTicketController optController, String optionRoot,
			String[] callSpecifiers, String [] putSpecifiers, BigDecimal[] strikePrices) {
		// Set an option root before simulating the subscription response
		ticket.getSymbolText().setText(optionRoot);
		// Subscription response has the contract symbols (create a fake quote and send that through)
		Message dsl = createDummySecurityList(optionRoot, callSpecifiers, putSpecifiers, strikePrices);
        optController.handleDerivativeSecurityList(dsl);
        // Show the message for the specific contract
        optController.setOrderMessage(message);
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
	
	/** Show a basic Option order ticket, then fake an incoming market data quote with a 
	 * put/call for that underlying symbol
	 */
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

		showMessageInOptionTicket(ticket, message, controller, optionRoot,
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
			
			controller.requestOptionRootInfo(optionRoot);
			
			DerivativeSecurityList securityList = createDummySecurityList(optionRoot, new String[] { callContractSpecifier }, new String[] { putContractSpecifier }, new BigDecimal[] { BigDecimal.TEN });
	//		securityList.setField(new SecurityReqID(((FIXCorrelationFieldSubscription)controller.getDerivativeSecurityListSubscription()).getCorrelationFieldValue()));
	
			controller.handleDerivativeSecurityList(securityList);
			
			optionOrderTicketModel.updateOptionInfo();
	
			assertEquals(1, ticket.getExpireMonthCombo().getItemCount());
		} catch (Exception ex){
			System.out.println(""+ex);
		}
	}

//  // Broken during 1.0 M2 (Will)
//	/**
//	 * This test show an order in the option order ticket
//     * followed by a derivative security list message that has different specifiers. 
//	 * (for example the strike price specified by the order is not an allowed strike price)
//	 * Then try the same two operations in the other order.
//	 * In each case, check that there are validation errors.
//	 */
//	public void testShowOrderMismatchedOptionSpecifiers() throws Exception {
//
//		OptionOrderTicketView ticketView = (OptionOrderTicketView) getTestView();
//		IOptionOrderTicket ticket = ((OptionOrderTicketView)getTestView()).getOptionOrderTicket();
//
//		final String optionRoot = "MRK";
//		final String callContractSpecifier = "GA";
//		final String callContractSymbol = optionRoot + "+" + callContractSpecifier;
//		final String putContractSpecifier = "RA";
//		
//		controller.requestOptionRootInfo(optionRoot);
//		
//		DerivativeSecurityList securityList = createDummySecurityList(optionRoot, new String[] { callContractSpecifier }, new String[] { putContractSpecifier }, new BigDecimal[] { BigDecimal.TEN });
////		FIXCorrelationFieldSubscription derivativeSecurityListSubscription = (FIXCorrelationFieldSubscription)controller.getDerivativeSecurityListSubscription();
////		securityList.setField(new SecurityReqID((derivativeSecurityListSubscription).getCorrelationFieldValue()));
//
//		controller.handleDerivativeSecurityList(securityList);
//
//		Message message = msgFactory.newMarketOrder("2",
//				Side.SELL, BigDecimal.ONE, new MSymbol(callContractSymbol),
//				TimeInForce.AT_THE_OPENING, "123456789101112");
//		message.setField(new MaturityDate());
//		message.setField(new PutOrCall(PutOrCall.CALL));
//		message.setField(new StrikePrice(new BigDecimal(23)));
//		controller.setOrderMessage(message);
//
//		/**
//		 * The OptionOrderTicketControllerHelper updates expire, put/call, and
//		 * strike based on market data and ensures those fields match the option
//		 * contract. This test isn't subscribing to any market data, so the
//		 * OptionOrderTicket has no way of properly populating those fields.
//		 */
//		assertNotNull(ticket.getExpireMonthCombo().getText());
//		assertNotNull(ticket.getExpireYearCombo().getText());
//		assertEquals("C", ticket.getPutOrCallCombo().getText());
//		assertEquals("23", ticket.getStrikePriceCombo().getText());
//        assertEquals("MRK+GA", ticket.getSymbolText().getText());
//        IObservableList bindings = ticketView.getDataBindingContext().getBindings();
//        boolean passed = false;
//        for (Object object : bindings) {
//        	if (IStatus.WARNING == ((IStatus)((Binding)object).getValidationStatus().getValue()).getSeverity()){
//        		passed = true;
//        	}
//		}
//        assertTrue("At least one binding should have an error", passed);
//
//		message = msgFactory.newMarketOrder("2",
//				Side.SELL, BigDecimal.ONE, new MSymbol(callContractSymbol),
//				TimeInForce.AT_THE_OPENING, "123456789101112");
//		message.setField(new MaturityDate());
//		message.setField(new PutOrCall(PutOrCall.CALL));
//		message.setField(new StrikePrice(new BigDecimal(10)));
//		controller.setOrderMessage(message);
//		assertEquals("1", ticket.getQuantityText().getText());
//		assertEquals("S", ticket.getSideCombo().getText());
//		assertEquals("MKT", ticket.getPriceText().getText());
//		assertEquals("C", ticket.getPutOrCallCombo().getText());
//		assertEquals("OPG", ticket.getTifCombo().getText());
//		assertEquals("123456789101112", ticket.getAccountText().getText());
//		assertNotNull(ticket.getExpireMonthCombo().getText());
//		assertNotNull(ticket.getExpireYearCombo().getText());
//        for (Object object : bindings) {
//			assertEquals(IStatus.OK, ((IStatus)((Binding)object).getValidationStatus().getValue()).getSeverity());
//		}
//
//	}

//	// Broken during 1.0 M2 (Will)
//	public void testShowOptionQuote() throws Exception {
//		OptionOrderTicketView ticketView = (OptionOrderTicketView) getTestView();
//		final String optionRoot = "MRK";
//		final String callContractSpecifier = "GA";
//		final String callContractSymbol = optionRoot + "+" + callContractSpecifier;
//		final String putContractSpecifier = "RA";
//		
//		controller.requestOptionRootInfo(optionRoot);
//		
//		DerivativeSecurityList securityList = createDummySecurityList(optionRoot, new String[] { callContractSpecifier }, new String[] { putContractSpecifier }, new BigDecimal[] { BigDecimal.TEN });
//
//		controller.handleDerivativeSecurityList(securityList);
//
//		MarketDataSnapshotFullRefresh quoteMessageToSend = new MarketDataSnapshotFullRefresh();
//		quoteMessageToSend.set(new Symbol(callContractSymbol));
//
//		FIXMarketDataViewTest.addGroup(quoteMessageToSend, MDEntryType.BID, BigDecimal.ONE, BigDecimal.TEN, new Date(), "BGUS");
//		FIXMarketDataViewTest.addGroup(quoteMessageToSend, MDEntryType.OFFER, BigDecimal.TEN, BigDecimal.TEN, new Date(), "BGUS");
//		quoteMessageToSend.setString(LastPx.FIELD,"123.4");
////		quoteMessageToSend.setField(new MDReqID(((FIXCorrelationFieldSubscription)subscription).getCorrelationFieldValue()));
//
//		MockEventTranslator.setMessageToReturn(quoteMessageToSend);
//		
//		controller.listenMarketData(optionRoot);
//
//		TableViewer marketDataViewer = ((IOptionOrderTicket)ticketView.getOrderTicket()).getOptionMarketDataTableViewer();
//		List<?> marketDataList = (List<?>)marketDataViewer.getInput();
//		int noEntries = marketDataList.size();
//		assertEquals(1, noEntries);
//		final OptionMessageHolder holder = (OptionMessageHolder)marketDataList.get(0);
//		doDelay(new Callable<Boolean>() {
//            public Boolean call() 
//                throws Exception
//            {
//                return holder.getMarketData(PutOrCall.CALL) != null;
//            }});
//        FieldMap marketData = holder.getMarketData(PutOrCall.CALL);
//		int noMDEntries = marketData.getInt(NoMDEntries.FIELD);
//		MarketDataSnapshotFullRefresh.NoMDEntries group = new MarketDataSnapshotFullRefresh.NoMDEntries();
//		for (int i = 1; i <= noMDEntries; i++)
//		{
//			marketData.getGroup(i, group);
//			switch (group.getChar(MDEntryType.FIELD)){
//			case MDEntryType.BID:
//				assertEquals(BigDecimal.ONE, group.getDecimal(MDEntryPx.FIELD));
//				assertEquals(BigDecimal.TEN, group.getDecimal(MDEntrySize.FIELD));
//				break;
//			case MDEntryType.OFFER:
//				assertEquals(BigDecimal.TEN, group.getDecimal(MDEntryPx.FIELD));
//				assertEquals(BigDecimal.TEN, group.getDecimal(MDEntrySize.FIELD));
//				break;
//				
//			}
//		}
//	}
	
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
		assertEquals("Open", ticket.getOpenCloseCombo().getText());
		
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

		// Attempt to get a message ID. This currently has the side effect of
		// initializing an in memory ID generator if the database backed one is
		// unavailable. This allows the subsequent ID generation to succeed,
		// which is required for handleSend() below.
		try {
			IDFactory idFactory = PhotonPlugin.getDefault()
					.getPhotonController().getIDFactory();
			idFactory.getNext();
		} catch (Exception anyException) {
			// Ignore
		}

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
        showMessageInOptionTicket(ticket, cxr, controller, optionRoot,
                new String[] { callContractSpecifier }, new String[] {putContractSpecifier}, new BigDecimal[] { BigDecimal.TEN });

        assertEquals("10", ticket.getQuantityText().getText());
        assertEquals("B", ticket.getSideCombo().getText());
        assertEquals("1", ticket.getPriceText().getText());
        assertEquals(callContractSymbol, ticket.getOptionSymbolText().getText());
        assertEquals("DAY", ticket.getTifCombo().getText());

        assertEquals("10", ticket.getQuantityText().getText());
        assertEquals("B", ticket.getSideCombo().getText());
        assertEquals("1", ticket.getPriceText().getText());
        assertEquals("MSQ+GE", ticket.getSymbolText().getText());
        assertEquals("DAY", ticket.getTifCombo().getText());

        // verify Side/Symbol/TIF are disabled for cancel/replace
        assertFalse("Side should not be enabled", ticket.getSideCombo().isEnabled());
        assertFalse("TIF should not be enabled", ticket.getTifCombo().isEnabled());
        assertFalse("Symbol should not be enabled", ticket.getSymbolText().isEnabled());
        assertFalse("Expiry Month should not be enabled", ticket.getExpireMonthCombo().isEnabled());
        assertFalse("Expiry year should not be enabled", ticket.getExpireYearCombo().isEnabled());
        assertFalse("PutOrCall should not be enabled", ticket.getPutOrCallCombo().isEnabled());
        assertFalse("Strike should not be enabled", ticket.getStrikePriceCombo().isEnabled());

        // verify enabled after cancel
        controller.clear();
        assertTrue("Side should be enabled", ticket.getSideCombo().isEnabled());
        assertTrue("TIF should be enabled", ticket.getTifCombo().isEnabled());
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
	
//	// Broken during 1.0 M2 (Will)
//	public void testUnderlyingMarketData() throws Exception {
//		OptionOrderTicketView ticketView = (OptionOrderTicketView) getTestView();
//		IOptionOrderTicket optionOrderTicket = ticketView.getOptionOrderTicket();
//
//		controller.clear();
//		assertEquals("", optionOrderTicket.getUnderlyingSymbolLabel().getText());
//		assertEquals(false, optionOrderTicket.getUnderlyingMarketDataComposite().getVisible());
//		
//		final String symbolStr = "IBM";
//		controller.listenMarketData(symbolStr);
//		
//		MarketDataSnapshotFullRefresh quoteMessageToSend = new MarketDataSnapshotFullRefresh();
//		quoteMessageToSend.set(new Symbol("IBM"));
//		
//		// set the system TZ to UTC to ensure that timestamps are in UTC - this is just a short-cut for testing
//		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
//		Date testDate = new Date(1206576015015L); // 3/26/2008 5PM PST
//		FIXMarketDataViewTest.addGroup(quoteMessageToSend, MDEntryType.BID, BigDecimal.ONE, BigDecimal.TEN, testDate, "BGUS");
//		FIXMarketDataViewTest.addGroup(quoteMessageToSend, MDEntryType.OFFER, new BigDecimal(23), new BigDecimal(24), testDate, "BGUS");
//		FIXMarketDataViewTest.addGroup(quoteMessageToSend, MDEntryType.TRADE, new BigDecimal(25), new BigDecimal(26), testDate, "BGUS"); 
//		FIXMarketDataViewTest.addGroup(quoteMessageToSend, MDEntryType.TRADE_VOLUME, new BigDecimal(27), new BigDecimal(28), testDate, "BGUS");
//		FIXMarketDataViewTest.addGroup(quoteMessageToSend, MDEntryType.TRADING_SESSION_HIGH_PRICE, new BigDecimal(29), new BigDecimal(30), testDate, "BGUS");
//		FIXMarketDataViewTest.addGroup(quoteMessageToSend, MDEntryType.TRADING_SESSION_LOW_PRICE, new BigDecimal(31), new BigDecimal(32), testDate, "BGUS");
//		FIXMarketDataViewTest.addGroup(quoteMessageToSend, MDEntryType.OPENING_PRICE, new BigDecimal(31), new BigDecimal(32), testDate, "BGUS");
//
//		controller.doOnPrimaryQuote(quoteMessageToSend);
//		// create the expected result for the timestamp above
//		SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
//		String testResult = formatter.format(testDate);
//		assertEquals(true, optionOrderTicket.getUnderlyingMarketDataComposite().getVisible());
//		assertEquals("25", optionOrderTicket.getUnderlyingLastPriceLabel().getText());
//		assertEquals(testResult, optionOrderTicket.getUnderlyingLastUpdatedTimeLabel().getText());
//		assertEquals(true, optionOrderTicket.getUnderlyingBidPriceLabel().getVisible());
//		assertEquals("IBM", optionOrderTicket.getUnderlyingSymbolLabel().getText());
//		assertEquals("1", optionOrderTicket.getUnderlyingBidPriceLabel().getText());
//		assertEquals("10", optionOrderTicket.getUnderlyingBidSizeLabel().getText());
//		assertEquals("23", optionOrderTicket.getUnderlyingOfferPriceLabel().getText());
//		assertEquals("24", optionOrderTicket.getUnderlyingOfferSizeLabel().getText());
//		assertEquals("", optionOrderTicket.getUnderlyingTradedValueLabel().getText());
//		assertEquals("28", optionOrderTicket.getUnderlyingVolumeLabel().getText());
//		
//		optionOrderTicket.getForm().reflow(true);
//	}

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
}


