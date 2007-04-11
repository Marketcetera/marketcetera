package org.marketcetera.photon.views;

import java.math.BigDecimal;
import java.util.Date;

import javax.jms.Destination;

import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.marketcetera.core.AccessViolator;
import org.marketcetera.core.MSymbol;
import org.marketcetera.photon.PhotonPlugin;
import org.marketcetera.photon.marketdata.MarketDataFeedService;
import org.marketcetera.photon.messaging.JMSFeedService;
import org.marketcetera.photon.preferences.CustomOrderFieldPage;
import org.marketcetera.photon.ui.BookComposite;
import org.marketcetera.photon.views.MarketDataViewTest.MyMarketDataFeed;
import org.marketcetera.quickfix.FIXMessageFactory;
import org.marketcetera.quickfix.FIXVersion;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.springframework.jms.JmsException;
import org.springframework.jms.core.JmsOperations;
import org.springframework.jms.core.MessageCreator;
import org.springframework.jms.core.MessagePostProcessor;
import org.springframework.jms.core.ProducerCallback;
import org.springframework.jms.core.SessionCallback;

import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.field.DeliverToCompID;
import quickfix.field.LastPx;
import quickfix.field.MDEntryPx;
import quickfix.field.MDEntryType;
import quickfix.field.NoMDEntries;
import quickfix.field.PrevClosePx;
import quickfix.field.Side;
import quickfix.field.Symbol;
import quickfix.field.TimeInForce;
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
		IStockOrderTicket ticket = (IStockOrderTicket) getTestView();
		controller = new StockOrderTicketController(ticket);
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
		assertEquals("B", ticket.getSideCCombo().getText());
		assertEquals("1", ticket.getPriceText().getText());
		assertEquals("QWER", ticket.getSymbolText().getText());
		assertEquals("DAY", ticket.getTifCCombo().getText());
	}
	
	public void testShowQuote() throws Exception {
		BundleContext bundleContext = PhotonPlugin.getDefault().getBundleContext();
		MarketDataFeedService marketDataFeed = MarketDataViewTest.getNullQuoteFeedService();
		bundleContext.registerService(MarketDataFeedService.class.getName(), marketDataFeed, null);
		
		
		StockOrderTicket view = (StockOrderTicket) getTestView();
		Message orderMessage = msgFactory.newLimitOrder("1",
				Side.BUY, BigDecimal.TEN, new MSymbol("MRKT"), BigDecimal.ONE,
				TimeInForce.DAY, null);
		controller.showMessage(orderMessage);

		
		MarketDataSnapshotFullRefresh quoteMessageToSend = new MarketDataSnapshotFullRefresh();
		quoteMessageToSend.set(new Symbol("MRKT"));
		
		MarketDataViewTest.addGroup(quoteMessageToSend, MDEntryType.BID, BigDecimal.ONE, BigDecimal.TEN, new Date(), "BGUS");
		MarketDataViewTest.addGroup(quoteMessageToSend, MDEntryType.OFFER, BigDecimal.TEN, BigDecimal.TEN, new Date(), "BGUS");
		quoteMessageToSend.setString(LastPx.FIELD,"123.4");
		
		MyMarketDataFeed feed = (MarketDataViewTest.MyMarketDataFeed)marketDataFeed.getMarketDataFeed();
		feed.sendMessage(quoteMessageToSend);
				
		AccessViolator violator = new AccessViolator(StockOrderTicket.class);

		BookComposite bookComposite = (BookComposite) violator.getField("bookComposite", view);

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

	/**
	 * Tests that adding custom fields into preferences makes them appear in the Stock Order Ticket view.
	 */
	public void testAddCustomFieldsToPreferences() throws Exception {
		ScopedPreferenceStore prefStore = PhotonPlugin.getDefault().getPreferenceStore();
		prefStore.setValue(CustomOrderFieldPage.CUSTOM_FIELDS_PREFERENCE, 
				"" + DeliverToCompID.FIELD + "=ABCD" + "&" + PrevClosePx.FIELD + "=EFGH");  //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$

		delay(1);
		
		StockOrderTicket view = (StockOrderTicket) getTestView();
		AccessViolator violator = new AccessViolator(StockOrderTicket.class);
		Table customFieldsTable = (Table) violator.getField("customFieldsTable", view);  //$NON-NLS-1$
		
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

		AccessViolator violator = new AccessViolator(StockOrderTicket.class);
		Table customFieldsTable = (Table) violator.getField("customFieldsTable", view);  //$NON-NLS-1$
		TableItem item0 = customFieldsTable.getItem(0);
		item0.setChecked(true);
		TableItem item1 = customFieldsTable.getItem(1);
		item1.setChecked(true);

		Message newMessage = msgFactory.newLimitOrder("1",  //$NON-NLS-1$
				Side.BUY, BigDecimal.TEN, new MSymbol("DREI"), BigDecimal.ONE,  //$NON-NLS-1$
				TimeInForce.DAY, null);
		controller.showMessage(newMessage);

		controller.handleSend();

		delay(1);
		
		Message sentMessage = (Message) mockJmsOperations.getStoredMessage();
		try {
			String value = sentMessage.getHeader().getString(DeliverToCompID.FIELD);  // header field
			assertEquals("ABCD", value);  //$NON-NLS-1$
		} catch (FieldNotFound e) {
			fail();
		}
		try {
			String value = sentMessage.getString(DeliverToCompID.FIELD);
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
		AccessViolator violator = new AccessViolator(StockOrderTicket.class);
		Table customFieldsTable = (Table) violator.getField("customFieldsTable", view);  //$NON-NLS-1$
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
}

/**
 * A mock <code>JmsOperations</code> that retains the last message sent via the 
 * <code>convertAndSend(Object message)</code> method. The stored message can be 
 * retrieved with a call to <code>getStoredMessage()</code>.
 */
class MockJmsOperations implements JmsOperations {
	private Object storedMessage;
	
	public void convertAndSend(Object message) throws JmsException {
		storedMessage = message;
	}
	
	public Object getStoredMessage() {
		return storedMessage;
	}

	public void convertAndSend(Destination destination, Object message) throws JmsException {
	}

	public void convertAndSend(String destinationName, Object message) throws JmsException {
	}

	public void convertAndSend(Object message, MessagePostProcessor postProcessor) throws JmsException {
	}

	public void convertAndSend(Destination destination, Object message, MessagePostProcessor postProcessor) throws JmsException {
	}

	public void convertAndSend(String destinationName, Object message, MessagePostProcessor postProcessor) throws JmsException {
	}

	public Object execute(SessionCallback action) throws JmsException {
		return null;
	}

	public Object execute(ProducerCallback action) throws JmsException {
		return null;
	}

	public javax.jms.Message receive() throws JmsException {
		return null;
	}

	public javax.jms.Message receive(Destination destination) throws JmsException {
		return null;
	}

	public javax.jms.Message receive(String destinationName) throws JmsException {
		return null;
	}

	public Object receiveAndConvert() throws JmsException {
		return null;
	}

	public Object receiveAndConvert(Destination destination) throws JmsException {
		return null;
	}

	public Object receiveAndConvert(String destinationName) throws JmsException {
		return null;
	}

	public javax.jms.Message receiveSelected(String messageSelector) throws JmsException {
		return null;
	}

	public javax.jms.Message receiveSelected(Destination destination, String messageSelector) throws JmsException {
		return null;
	}

	public javax.jms.Message receiveSelected(String destinationName, String messageSelector) throws JmsException {
		return null;
	}

	public Object receiveSelectedAndConvert(String messageSelector) throws JmsException {
		return null;
	}

	public Object receiveSelectedAndConvert(Destination destination, String messageSelector) throws JmsException {
		return null;
	}

	public Object receiveSelectedAndConvert(String destinationName, String messageSelector) throws JmsException {
		return null;
	}

	public void send(MessageCreator messageCreator) throws JmsException {
	}

	public void send(Destination destination, MessageCreator messageCreator) throws JmsException {
	}

	public void send(String destinationName, MessageCreator messageCreator) throws JmsException {
	}

}


