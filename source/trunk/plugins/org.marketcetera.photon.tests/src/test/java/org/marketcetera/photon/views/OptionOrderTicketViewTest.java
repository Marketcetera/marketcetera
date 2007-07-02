package org.marketcetera.photon.views;

import java.math.BigDecimal;
import java.util.Date;

import org.eclipse.ui.IViewPart;
import org.eclipse.ui.internal.ErrorViewPart;
import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.MSymbol;
import org.marketcetera.marketdata.MarketceteraOptionSymbol;
import org.marketcetera.photon.PhotonPlugin;
import org.marketcetera.photon.marketdata.MarketDataFeedService;
import org.marketcetera.photon.messaging.JMSFeedService;
import org.marketcetera.photon.ui.IBookComposite;
import org.marketcetera.quickfix.FIXMessageFactory;
import org.marketcetera.quickfix.FIXVersion;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.springframework.jms.core.JmsOperations;

import quickfix.Group;
import quickfix.Message;
import quickfix.StringField;
import quickfix.field.CFICode;
import quickfix.field.LastPx;
import quickfix.field.MDEntryPx;
import quickfix.field.MDEntryType;
import quickfix.field.MaturityDate;
import quickfix.field.MaturityMonthYear;
import quickfix.field.MsgType;
import quickfix.field.NoMDEntries;
import quickfix.field.OrdType;
import quickfix.field.OrderQty;
import quickfix.field.PutOrCall;
import quickfix.field.SecurityReqID;
import quickfix.field.SecurityRequestResult;
import quickfix.field.SecurityResponseID;
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
	private MockMarketDataFeed mockFeed;
	
	public OptionOrderTicketViewTest(String name) {
		super(name);
	}

    @Override
	protected void setUp() throws Exception {
		super.setUp();
		IViewPart theTestView = getTestView();
		if (theTestView instanceof ErrorViewPart){
			fail("Test view was not created");
		}
		
		MarketDataFeedService feedService = MockMarketDataFeed.registerMockMarketDataFeed();
		mockFeed = MockMarketDataFeed.getMockMarketDataFeed(feedService);
		
		IOptionOrderTicket ticket = (IOptionOrderTicket) theTestView;
		controller = new OptionOrderTicketController();
		controller.bind(ticket);
	}

    @Override
	protected void tearDown() throws Exception {
		super.tearDown();
		controller.dispose();
	}

	@Override
	protected String getViewID() {
		return OptionOrderTicket.ID;
	}

	/**
	 * Ensure that the option order ticket controller is primed for use and then
	 * call showMessage on it. When a user enters an option root, they cause a
	 * subscription to the specific option contracts.
	 */
	private void showMessageInOptionTicket(IOptionOrderTicket ticket, Message message,
			OptionOrderTicketController optController, String optionRoot,
			String[] optionContractSpecifiers, String[] strikePrices) {
		// Set an option root before simulating the subscription response
		ticket.getSymbolText().setText(optionRoot);
		// Subscription response has the contract symbols (create a fake quote and send that through)
        Message dsl = createDummySecurityList(optionRoot, optionContractSpecifiers, strikePrices);
        optController.onMessage(dsl);
        // Show the message for the specific contract
        optController.showMessage(message);
		
	}
	/** Show a basic Option order ticket, then fake an incoming market data quote with a 
	 * put/call for that underlying symbol
	 */
	public void testShowOrder() throws NoSuchFieldException, IllegalAccessException {
		IOptionOrderTicket ticket = (IOptionOrderTicket) getTestView();
        /**
		 * Note the difference between an option contract symbol ("MSQ+GE") and
		 * an option root ("MSQ"). An OptionOrderTicket has both.
		 */
		final String optionRoot = "MSQ";
		final String optionContractSpecifier = "GE";
		final String optionContractSymbol = optionRoot + "+" + optionContractSpecifier;
		Message message = msgFactory.newLimitOrder("1",
				Side.BUY, BigDecimal.TEN, new MSymbol(optionContractSymbol), BigDecimal.ONE,
				TimeInForce.DAY, null);
        message.setField(new UnderlyingSymbol(optionRoot));
        message.setField(new MaturityDate());
		message.setField(new StrikePrice(23));
		message.setField(new PutOrCall(PutOrCall.CALL));
        
		showMessageInOptionTicket(ticket, message, controller, optionRoot,
				new String[] { optionContractSpecifier }, new String[] { "10" });
        
        assertEquals("10", ticket.getQuantityText().getText());
		assertEquals("B", ticket.getSideCombo().getText());
		assertEquals("1", ticket.getPriceText().getText());
		assertEquals(optionContractSymbol, ticket.getOptionSymbolControl().getText());
		assertEquals("DAY", ticket.getTifCombo().getText());
		/**
		 * The OptionOrderTicketControllerHelper updates expire, put/call, and
		 * strike based on market data and ensures those fields match the option
		 * contract. This test isn't subscribing to any market data, so the
		 * OptionOrderTicket has no way of properly populating those fields.
		 */
		assertNotNull(ticket.getExpireMonthCombo().getText());
		assertNotNull(ticket.getExpireYearCombo().getText());
		assertEquals("C", ticket.getPutOrCallCombo().getText());
		assertEquals("10", ticket.getStrikePriceControl().getText());
        assertEquals("MSQ", ticket.getSymbolText().getText());
        assertFalse(controller.hasBindErrors());
		
		message = msgFactory.newMarketOrder("2",
				Side.SELL, BigDecimal.ONE, new MSymbol(optionContractSymbol),
				TimeInForce.AT_THE_OPENING, "123456789101112");
		message.setField(new MaturityDate());
		message.setField(new PutOrCall(PutOrCall.CALL));
		controller.showMessage(message);
		assertEquals("1", ticket.getQuantityText().getText());
		assertEquals("S", ticket.getSideCombo().getText());
		assertEquals("MKT", ticket.getPriceText().getText());
		assertEquals("C", ticket.getPutOrCallCombo().getText());
		assertEquals("OPG", ticket.getTifCombo().getText());
		assertEquals("123456789101112", ticket.getAccountText().getText());
		assertNotNull(ticket.getExpireMonthCombo().getText());
		assertNotNull(ticket.getExpireYearCombo().getText());
		assertFalse(controller.hasBindErrors());
	}

	// todo: finish up these tests
	public void testShowQuote() throws Exception {
		OptionOrderTicket ticket = (OptionOrderTicket) getTestView();
		final String optionRoot = "MRK";
		final String optionContractSpecifier = "GA";
		final String optionContractSymbol = optionRoot + "+" + optionContractSpecifier;
		Message orderMessage = msgFactory.newLimitOrder("1",
				Side.BUY, BigDecimal.TEN, new MSymbol(optionContractSymbol), BigDecimal.ONE,
				TimeInForce.DAY, null);
		orderMessage.setField(new UnderlyingSymbol(optionRoot));
		orderMessage.setField(new MaturityDate());
		orderMessage.setField(new StrikePrice(10));
		orderMessage.setField(new PutOrCall(PutOrCall.CALL));
		
		showMessageInOptionTicket(ticket, orderMessage, controller, optionRoot,
				new String[] { optionContractSpecifier }, new String[] { "10" });


		MarketDataSnapshotFullRefresh quoteMessageToSend = new MarketDataSnapshotFullRefresh();
		quoteMessageToSend.set(new Symbol(optionContractSymbol));

		MarketDataViewTest.addGroup(quoteMessageToSend, MDEntryType.BID, BigDecimal.ONE, BigDecimal.TEN, new Date(), "BGUS");
		MarketDataViewTest.addGroup(quoteMessageToSend, MDEntryType.OFFER, BigDecimal.TEN, BigDecimal.TEN, new Date(), "BGUS");
		quoteMessageToSend.setString(LastPx.FIELD,"123.4");

		mockFeed.sendMessage(quoteMessageToSend);

		IBookComposite bookComposite = ticket.getBookComposite();

		Message returnedMessage = null;
		for (int i = 0; i < 10; i ++){
			returnedMessage = bookComposite.getInput();
			if (returnedMessage == null){
				this.waitForJobs();
				Thread.sleep(100 * i);
			} else {
				break;
			}
		}
		assertNotNull(returnedMessage);
		assertEquals(optionContractSymbol, returnedMessage.getString(Symbol.FIELD));
		int noEntries = returnedMessage.getInt(NoMDEntries.FIELD);
		for (int i = 1; i < noEntries+1; i++){
			MarketDataSnapshotFullRefresh.NoMDEntries group = new MarketDataSnapshotFullRefresh.NoMDEntries();
			returnedMessage.getGroup(i, group);
			if (i == 1){
				assertEquals(MDEntryType.BID, group.getChar(MDEntryType.FIELD));
				assertEquals(1, group.getInt(MDEntryPx.FIELD));
			} else if (i == 2) {
				assertEquals(MDEntryType.OFFER, group.getChar(MDEntryType.FIELD));
				assertEquals(10, group.getInt(MDEntryPx.FIELD));
			} else {
				assertTrue(false);
			}
		}
	}

	public void testTypeNewOrder() throws Exception {
		controller.handleCancel();
		OptionOrderTicket view = (OptionOrderTicket) getTestView();
		view.getSideCombo().setText("S");
		view.getQuantityText().setText("45");
		view.getSymbolText().setText("ABC");
		view.getPriceText().setText("MKT");
		view.getTifCombo().setText("FOK");

		Message orderMessage = controller.getMessage();
		assertEquals(MsgType.ORDER_SINGLE, orderMessage.getHeader().getString(MsgType.FIELD));
		assertEquals(Side.SELL, orderMessage.getChar(Side.FIELD));
		assertEquals(45, orderMessage.getInt(OrderQty.FIELD));
		assertEquals("ABC", orderMessage.getString(UnderlyingSymbol.FIELD));
		assertEquals(OrdType.MARKET, orderMessage.getChar	(OrdType.FIELD));
		assertEquals(TimeInForce.FILL_OR_KILL, orderMessage.getChar(TimeInForce.FIELD));
	}

/**
	 * Tests that adding custom fields into preferences makes them appear in the Option Stock Order Ticket view.
	 */
/*
	public void testAddCustomFieldsToPreferences() throws Exception {
		ScopedPreferenceStore prefStore = PhotonPlugin.getDefault().getPreferenceStore();
		prefStore.setValue(CustomOrderFieldPage.CUSTOM_FIELDS_PREFERENCE,
				"" + DeliverToCompID.FIELD + "=ABCD" + "&" + PrevClosePx.FIELD + "=EFGH");  //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$

		delay(1);

		OptionOrderTicket view = (OptionOrderTicket) getTestView();
		Table customFieldsTable = view.getCustomFieldsTable();

		assertEquals(2, customFieldsTable.getItemCount());

		TableItem item0 = customFieldsTable.getItem(0);
		assertEquals(false, item0.getChecked());
		assertEquals("" + DeliverToCompID.FIELD, item0.getText(1));  //$NON-NLS-1$
		assertEquals("ABCD", item0.getText(2));  //$NON-NLS-1$

		TableItem item1 = customFieldsTable.getItem(1);
		assertEquals(false, item1.getChecked());
		assertEquals("" + PrevClosePx.FIELD, item1.getText(1));  //$NON-NLS-1$
		assertEquals("EFGH", item1.getText(2));  //$NON-NLS-1$
	}

	*/
/**
	 * Tests that enabled custom fields (the header and body kind) are inserted into outgoing messages.
	 */
/*
	public void testEnabledCustomFieldsAddedToMessage() throws Exception {
		ScopedPreferenceStore prefStore = PhotonPlugin.getDefault().getPreferenceStore();
		prefStore.setValue(CustomOrderFieldPage.CUSTOM_FIELDS_PREFERENCE,
				"" + DeliverToCompID.FIELD + "=ABCD" + "&" + PrevClosePx.FIELD + "=EFGH");  //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$

		MockJmsOperations mockJmsOperations = new MockJmsOperations();
		setUpJMSFeedService(mockJmsOperations);

		OptionOrderTicket view = (OptionOrderTicket) getTestView();

		Table customFieldsTable = view.getCustomFieldsTable();
		TableItem item0 = customFieldsTable.getItem(0);
		item0.setChecked(true);
		TableItem item1 = customFieldsTable.getItem(1);
		item1.setChecked(true);

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
		controller.showMessage(newMessage);

		controller.handleSend();

		delay(1);

		Message sentMessage = (Message) mockJmsOperations.getStoredMessage();
		assertNotNull( sentMessage );
		try {
			quickfix.Message.Header header = sentMessage.getHeader();
			String value = header.getString(DeliverToCompID.FIELD);  // header field
			assertEquals("ABCD", value);  //$NON-NLS-1$
		} catch (FieldNotFound e) {
			fail();
		}
		try {
			sentMessage.getString(DeliverToCompID.FIELD);
			//shouldn't be in the body.
			fail();
		} catch (FieldNotFound e) {
			//expected result
		}
		try {
			String value = sentMessage.getString(PrevClosePx.FIELD);  // body field
			assertEquals("EFGH", value);  //$NON-NLS-1$
		} catch (FieldNotFound e) {
			fail();
		}
	}

	*/
/**
	 * Tests that disabled custom fields (the header and body kind) are <em>not</em> inserted into outgoing messages.
	 */
/*
	public void testDisabledCustomFieldsNotAddedToMessage() throws Exception {
		ScopedPreferenceStore prefStore = PhotonPlugin.getDefault().getPreferenceStore();
		prefStore.setValue(CustomOrderFieldPage.CUSTOM_FIELDS_PREFERENCE,
				"" + DeliverToCompID.FIELD + "=ABCD" + "&" + PrevClosePx.FIELD + "=EFGH");  //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$

		MockJmsOperations mockJmsOperations = new MockJmsOperations();
		setUpJMSFeedService(mockJmsOperations);

		OptionOrderTicket view = (OptionOrderTicket) getTestView();
		Table customFieldsTable = view.getCustomFieldsTable();
		TableItem item0 = customFieldsTable.getItem(0);
		item0.setChecked(false);
		TableItem item1 = customFieldsTable.getItem(1);
		item1.setChecked(false);

		Message newMessage = msgFactory.newLimitOrder("1",  //$NON-NLS-1$
				Side.BUY, BigDecimal.TEN, new MSymbol("DREI"), BigDecimal.ONE,  //$NON-NLS-1$
				TimeInForce.DAY, null);
		controller.showMessage(newMessage);

		controller.handleSend();

		delay(1);

		Message sentMessage = (Message) mockJmsOperations.getStoredMessage();
		try {
			sentMessage.getHeader().getString(DeliverToCompID.FIELD);  // header field
			fail();
		} catch (FieldNotFound e) {
			// expected behavior
		}
		try {
			sentMessage.getString(PrevClosePx.FIELD);  // body field
			fail();
		} catch (FieldNotFound e) {
			// expected behavior
		}
	}
*/
    private DerivativeSecurityList createDummySecurityList(String symbol, String[] optionSuffixes, String[] strikePrices) {
        SecurityRequestResult resultCode = new SecurityRequestResult(SecurityRequestResult.VALID_REQUEST);
        DerivativeSecurityList responseMessage = new DerivativeSecurityList();
        responseMessage.setField(new SecurityReqID("bob"));
        responseMessage.setField(new SecurityResponseID("123"));

        responseMessage.setField(new UnderlyingSymbol(symbol));
        for (int i = 0; i < optionSuffixes.length; i++) {
            MarketceteraOptionSymbol optionSymbol = new MarketceteraOptionSymbol(symbol + "+" + optionSuffixes[i]);
            // put first
            Group optionGroup = new DerivativeSecurityList.NoRelatedSym();
            optionGroup.setField(new Symbol(optionSymbol.toString()));
            optionGroup.setField(new StringField(StrikePrice.FIELD, strikePrices[i]));
            optionGroup.setField(new CFICode("OPASPS"));
            optionGroup.setField(new MaturityMonthYear("200701"));
            optionGroup.setField(new MaturityDate("20070122"));
            responseMessage.addGroup(optionGroup);

            // now call
            optionGroup.setField(new Symbol(optionSymbol.toString()));
            optionGroup.setField(new StringField(StrikePrice.FIELD, strikePrices[i]));
            optionGroup.setField(new CFICode("OCASPS"));
            optionGroup.setField(new MaturityMonthYear("200701"));
            optionGroup.setField(new MaturityDate("20070122"));
            responseMessage.addGroup(optionGroup);

        }
        responseMessage.setField(resultCode);
        return responseMessage;
    }

    private void setUpJMSFeedService(JmsOperations jmsOperations) {
		JMSFeedService jmsFeedService = null;

		BundleContext bundleContext = PhotonPlugin.getDefault().getBundleContext();
		ServiceReference jmsFeedServiceReference = bundleContext.getServiceReference(JMSFeedService.class.getName());
		if (jmsFeedServiceReference == null) {
			jmsFeedService = new JMSFeedService();
			ServiceRegistration registration = bundleContext.registerService(
						JMSFeedService.class.getName(),
						jmsFeedService,
						null);
			jmsFeedService.setServiceRegistration(registration);
		}
		else {
			jmsFeedService = (JMSFeedService) bundleContext.getService(jmsFeedServiceReference);
		}

		jmsFeedService.setJmsOperations(jmsOperations);
	}
}


