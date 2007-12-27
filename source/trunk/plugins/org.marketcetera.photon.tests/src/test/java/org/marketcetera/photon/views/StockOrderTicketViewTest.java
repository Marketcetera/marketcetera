package org.marketcetera.photon.views;

import java.math.BigDecimal;
import java.util.Date;


import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.internal.ErrorViewPart;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.marketcetera.core.IDFactory;
import org.marketcetera.core.MSymbol;
import org.marketcetera.photon.PhotonPlugin;
import org.marketcetera.photon.marketdata.MarketDataFeedService;
import org.marketcetera.photon.messaging.JMSFeedService;
import org.marketcetera.photon.preferences.CustomOrderFieldPage;
import org.marketcetera.photon.ui.IBookComposite;
import org.marketcetera.photon.views.MarketDataViewTest.MyMarketDataFeed;
import org.marketcetera.quickfix.FIXMessageFactory;
import org.marketcetera.quickfix.FIXVersion;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.springframework.jms.core.JmsOperations;

import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.field.DeliverToCompID;
import quickfix.field.LastPx;
import quickfix.field.MDEntryPx;
import quickfix.field.MDEntryType;
import quickfix.field.MsgType;
import quickfix.field.NoMDEntries;
import quickfix.field.OrdType;
import quickfix.field.OrderQty;
import quickfix.field.PrevClosePx;
import quickfix.field.SecurityType;
import quickfix.field.Side;
import quickfix.field.Symbol;
import quickfix.field.TimeInForce;
import quickfix.field.TransactTime;
import quickfix.fix42.MarketDataSnapshotFullRefresh;


/**
 * Tests for the Stock Order Ticket view.
 * 
 * @author gmiller
 * @author andrei@lissovski.org
 */
public class StockOrderTicketViewTest extends ViewTestBase {

    private FIXMessageFactory msgFactory = FIXVersion.FIX42.getMessageFactory();
	private StockOrderTicketController controller;

	public StockOrderTicketViewTest(String name) {
		super(name);
	}

    @Override
	protected void setUp() throws Exception {
		super.setUp();
		IViewPart theTestView = getTestView();
		if (theTestView instanceof ErrorViewPart){
			fail("Test view was not created");
		}
		IStockOrderTicket ticket = (IStockOrderTicket) theTestView;
		controller = new StockOrderTicketController();
		controller.bind(ticket);
	}

    @Override
	protected void tearDown() throws Exception {
		super.tearDown();
		controller.dispose();
	}

	public void testShowOrder() throws NoSuchFieldException, IllegalAccessException {
		IStockOrderTicket ticket = (IStockOrderTicket) getTestView();
		Message message = msgFactory.newLimitOrder("1",
				Side.BUY, BigDecimal.TEN, new MSymbol("QWER"), BigDecimal.ONE,
				TimeInForce.DAY, null);
		controller.showMessage(message);
		assertEquals("10", ticket.getQuantityText().getText());
		assertEquals("B", ticket.getSideCombo().getText());
		assertEquals("1", ticket.getPriceText().getText());
		assertEquals("QWER", ticket.getSymbolText().getText());
		assertEquals("DAY", ticket.getTifCombo().getText());

		message = msgFactory.newMarketOrder("2",
				Side.SELL_SHORT, BigDecimal.ONE, new MSymbol("QWER"),
				TimeInForce.AT_THE_OPENING, "123456789101112");
		controller.showMessage(message);
		assertEquals("1", ticket.getQuantityText().getText());
		assertEquals("SS", ticket.getSideCombo().getText());
		assertEquals("MKT", ticket.getPriceText().getText());
		assertEquals("QWER", ticket.getSymbolText().getText());
		assertEquals("OPG", ticket.getTifCombo().getText());
		assertEquals("123456789101112", ticket.getAccountText().getText());

		// bug #393 - verify quantity doesn't have commas in them
		message = msgFactory.newMarketOrder("3",
				Side.SELL_SHORT, new BigDecimal(2000), new MSymbol("QWER"),
				TimeInForce.AT_THE_OPENING, "123456789101112");
		controller.showMessage(message);
		assertEquals("2000", ticket.getQuantityText().getText());
	}
	
	public void testShowQuote() throws Exception {
		BundleContext bundleContext = PhotonPlugin.getDefault().getBundleContext();
		MarketDataFeedService marketDataFeed = MarketDataViewTest.getNullQuoteFeedService();
		bundleContext.registerService(MarketDataFeedService.class.getName(), marketDataFeed, null);
		
		
		final String symbolStr = "MRKT";
		StockOrderTicket view = (StockOrderTicket) getTestView();
		Message orderMessage = msgFactory.newLimitOrder("1",
				Side.BUY, BigDecimal.TEN, new MSymbol(symbolStr), BigDecimal.ONE,
				TimeInForce.DAY, null);
		controller.getOrderTicketControllerHelper().listenMarketData(symbolStr);
		controller.showMessage(orderMessage);

		
		MarketDataSnapshotFullRefresh quoteMessageToSend = new MarketDataSnapshotFullRefresh();
		quoteMessageToSend.set(new Symbol("MRKT"));
		
		MarketDataViewTest.addGroup(quoteMessageToSend, MDEntryType.BID, BigDecimal.ONE, BigDecimal.TEN, new Date(), "BGUS");
		MarketDataViewTest.addGroup(quoteMessageToSend, MDEntryType.OFFER, BigDecimal.TEN, BigDecimal.TEN, new Date(), "BGUS");
		quoteMessageToSend.setString(LastPx.FIELD,"123.4");
		
		MyMarketDataFeed feed = (MarketDataViewTest.MyMarketDataFeed)marketDataFeed.getMarketDataFeed();
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
	
	public void testTypeNewOrder() throws Exception {
		controller.handleCancel();
		StockOrderTicket view = (StockOrderTicket) getTestView();
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
				"" + DeliverToCompID.FIELD + "=ABCD" + "&" + PrevClosePx.FIELD + "=EFGH");  //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$

		delay(10);
		
		StockOrderTicket view = (StockOrderTicket) getTestView();
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
	
	/**
	 * Tests that enabled custom fields (the header and body kind) are inserted into outgoing messages.
	 */
	public void testEnabledCustomFieldsAddedToMessage() throws Exception {
		ScopedPreferenceStore prefStore = PhotonPlugin.getDefault().getPreferenceStore();
		prefStore.setValue(CustomOrderFieldPage.CUSTOM_FIELDS_PREFERENCE, 
				"" + DeliverToCompID.FIELD + "=ABCD" + "&" + PrevClosePx.FIELD + "=EFGH");  //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$

		MockJmsOperations mockJmsOperations = new MockJmsOperations();
		setUpJMSFeedService(mockJmsOperations);

		StockOrderTicket view = (StockOrderTicket) getTestView();

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
	
	/**
	 * Tests that disabled custom fields (the header and body kind) are <em>not</em> inserted into outgoing messages.
	 */
	public void testDisabledCustomFieldsNotAddedToMessage() throws Exception {
		ScopedPreferenceStore prefStore = PhotonPlugin.getDefault().getPreferenceStore();
		prefStore.setValue(CustomOrderFieldPage.CUSTOM_FIELDS_PREFERENCE, 
				"" + DeliverToCompID.FIELD + "=ABCD" + "&" + PrevClosePx.FIELD + "=EFGH");  //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		
		MockJmsOperations mockJmsOperations = new MockJmsOperations();
		setUpJMSFeedService(mockJmsOperations);
		
		StockOrderTicket view = (StockOrderTicket) getTestView();
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
	
	@Override
	protected String getViewID() {
		return StockOrderTicket.ID;
	}
	
	public void testTransactTimeCorrect() throws Exception {
		MockJmsOperations mockJmsOperations = new MockJmsOperations();
		setUpJMSFeedService(mockJmsOperations);

		StockOrderTicket view = (StockOrderTicket) getTestView();

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
		
		view.clear();

		delay(5000);

		view.getSideCombo().setText("S");
		view.getQuantityText().setText("45");
		view.getSymbolText().setText("ASDF");
		view.getPriceText().setText("MKT");
		view.getTifCombo().setText("FOK");

		controller.handleSend();

		delay(1);
		
		Message sentMessage = (Message) mockJmsOperations.getStoredMessage();
		assertNotNull( sentMessage );

		long sentTime = sentMessage.getUtcTimeStamp(TransactTime.FIELD).getTime();
		long diff = System.currentTimeMillis() - sentTime;
		assertTrue("Found diff of: "+diff, diff > 0 && diff < 1000);
	}
	
	/** Verify that MarketOnClose orders are translated correctly 
	 * Setup the outgoing order to be Market and CLO (at the close)
	 * in FIX.4.2 Photon
	 */
	public void testMarketOnCloseCorrect() throws Exception {
		MockJmsOperations mockJmsOperations = new MockJmsOperations();
		setUpJMSFeedService(mockJmsOperations);

		StockOrderTicket view = (StockOrderTicket) getTestView();
		view.clear();
		delay(5000);

		view.getSideCombo().setText("S");
		view.getQuantityText().setText("45");
		view.getSymbolText().setText("ASDF");
		view.getPriceText().setText("MKT");
		view.getTifCombo().setText("CLO");

		controller.handleSend();

		delay(1);
		
		Message sentMessage = (Message) mockJmsOperations.getStoredMessage();
		assertNotNull( sentMessage );

		assertEquals(TimeInForce.DAY, sentMessage.getChar(TimeInForce.FIELD));
		assertEquals(OrdType.MARKET_ON_CLOSE, sentMessage.getChar(OrdType.FIELD));
	}

	public void testMKTOrderCaseInsensitive() throws Exception {
		MockJmsOperations mockJmsOperations = new MockJmsOperations();
		setUpJMSFeedService(mockJmsOperations);

		StockOrderTicket view = (StockOrderTicket) getTestView();
		view.clear();
		delay(5000);

		view.getSideCombo().setText("S");
		view.getQuantityText().setText("45");
		view.getSymbolText().setText("ASDF");
		view.getPriceText().setText("mkt");
		view.getTifCombo().setText("DAY");

		controller.handleSend();

		delay(1);
		
		Message sentMessage = (Message) mockJmsOperations.getStoredMessage();
		assertNotNull( sentMessage );
		assertEquals(OrdType.MARKET, sentMessage.getChar(OrdType.FIELD));
	}

}


