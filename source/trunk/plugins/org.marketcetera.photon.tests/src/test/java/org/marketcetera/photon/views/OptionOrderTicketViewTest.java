package org.marketcetera.photon.views;

import java.math.BigDecimal;
import java.util.Date;

import org.eclipse.ui.IViewPart;
import org.eclipse.ui.internal.ErrorViewPart;
import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.MSymbol;
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

import quickfix.Message;
import quickfix.field.LastPx;
import quickfix.field.MDEntryPx;
import quickfix.field.MDEntryType;
import quickfix.field.MaturityDate;
import quickfix.field.NoMDEntries;
import quickfix.field.PutOrCall;
import quickfix.field.Side;
import quickfix.field.StrikePrice;
import quickfix.field.Symbol;
import quickfix.field.TimeInForce;
import quickfix.fix42.MarketDataSnapshotFullRefresh;

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

    @Override
	protected void setUp() throws Exception {
		super.setUp();
		IViewPart theTestView = getTestView();
		if (theTestView instanceof ErrorViewPart){
			fail("Test view was not created");
		}
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

	public void testShowOrder() throws NoSuchFieldException, IllegalAccessException {
		IOptionOrderTicket ticket = (IOptionOrderTicket) getTestView();
		Message message = msgFactory.newLimitOrder("1",
				Side.BUY, BigDecimal.TEN, new MSymbol("QWE"), BigDecimal.ONE,
				TimeInForce.DAY, null);
		message.setField(new MaturityDate());
		message.setField(new StrikePrice(23));
		message.setField(new PutOrCall(PutOrCall.CALL));
		controller.showMessage(message);
		assertEquals("10", ticket.getQuantityText().getText());
		assertEquals("B", ticket.getSideCombo().getText());
		assertEquals("1", ticket.getPriceText().getText());
		assertEquals("QWE", ticket.getSymbolText().getText());
		assertEquals("DAY", ticket.getTifCombo().getText());
		assertNotNull(ticket.getExpireMonthCombo().getText());
		assertNotNull(ticket.getExpireYearCombo().getText());
		assertEquals(PutOrCall.CALL, ticket.getPutOrCallCombo().getText().charAt(0));
		assertEquals("23", ticket.getStrikePriceControl().getText());
		
		message = msgFactory.newMarketOrder("2",
				Side.SELL, BigDecimal.ONE, new MSymbol("QWE"),
				TimeInForce.AT_THE_OPENING, "123456789101112");
		message.setField(new MaturityDate());
		message.setField(new PutOrCall(PutOrCall.CALL));
		controller.showMessage(message);
		assertEquals("1", ticket.getQuantityText().getText());
		assertEquals("S", ticket.getSideCombo().getText());
		assertEquals("MKT", ticket.getPriceText().getText());
		assertEquals("QWE", ticket.getSymbolText().getText());
		assertEquals("OPG", ticket.getTifCombo().getText());
		assertEquals("123456789101112", ticket.getAccountText().getText());
		assertNotNull(ticket.getExpireMonthCombo().getText());
		assertNotNull(ticket.getExpireYearCombo().getText());
		assertEquals(PutOrCall.CALL, ticket.getPutOrCallCombo().getText().charAt(0));
	}

	// todo: finish up these tests
	/*
	public void testShowQuote() throws Exception {
		BundleContext bundleContext = PhotonPlugin.getDefault().getBundleContext();
		MarketDataFeedService marketDataFeed = MarketDataViewTest.getNullQuoteFeedService();
		bundleContext.registerService(MarketDataFeedService.class.getName(), marketDataFeed, null);


		OptionOrderTicket view = (OptionOrderTicket) getTestView();
		Message orderMessage = msgFactory.newLimitOrder("1",
				Side.BUY, BigDecimal.TEN, new MSymbol("MRKT"), BigDecimal.ONE,
				TimeInForce.DAY, null);
		orderMessage.setField(new MaturityDate());
		orderMessage.setField(new StrikePrice(23));
		orderMessage.setField(new PutOrCall(PutOrCall.CALL));
		controller.showMessage(orderMessage);


		MarketDataSnapshotFullRefresh quoteMessageToSend = new MarketDataSnapshotFullRefresh();
		quoteMessageToSend.set(new Symbol("MRKT"));

		MarketDataViewTest.addGroup(quoteMessageToSend, MDEntryType.BID, BigDecimal.ONE, BigDecimal.TEN, new Date(), "BGUS");
		MarketDataViewTest.addGroup(quoteMessageToSend, MDEntryType.OFFER, BigDecimal.TEN, BigDecimal.TEN, new Date(), "BGUS");
		quoteMessageToSend.setString(LastPx.FIELD,"123.4");

		MarketDataViewTest.MyMarketDataFeed feed = (MarketDataViewTest.MyMarketDataFeed)marketDataFeed.getMarketDataFeed();
		feed.sendMessage(quoteMessageToSend);

		IBookComposite bookComposite = view.getBookComposite();

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
		assertEquals("MRKT", returnedMessage.getString(Symbol.FIELD));
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
/*
	public void testTypeNewOrder() throws Exception {
		controller.handleCancel();
		OptionOrderTicket view = (OptionOrderTicket) getTestView();
		view.getSideCombo().setText("S");
		view.getQuantityText().setText("45");
		view.getSymbolText().setText("ASDF");
		view.getPriceText().setText("MKT");
		view.getTifCombo().setText("FOK");

		Message orderMessage = controller.getMessage();
		assertEquals(MsgType.ORDER_SINGLE, orderMessage.getHeader().getString(MsgType.FIELD));
		assertEquals(Side.SELL, orderMessage.getChar(Side.FIELD));
		assertEquals(45, orderMessage.getInt(OrderQty.FIELD));
		assertEquals("ASDF", orderMessage.getString(Symbol.FIELD));
		assertEquals(OrdType.MARKET, orderMessage.getChar	(OrdType.FIELD));
		assertEquals(TimeInForce.FILL_OR_KILL, orderMessage.getChar(TimeInForce.FIELD));
	}

	*/
/**
	 * Tests that adding custom fields into preferences makes them appear in the Stock Order Ticket view.
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


