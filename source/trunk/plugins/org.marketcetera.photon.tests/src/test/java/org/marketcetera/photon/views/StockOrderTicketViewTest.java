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
import org.marketcetera.core.InternalID;
import org.marketcetera.core.MSymbol;
import org.marketcetera.photon.PhotonPlugin;
import org.marketcetera.photon.messaging.JMSFeedService;
import org.marketcetera.photon.preferences.CustomOrderFieldPage;
import org.marketcetera.photon.quotefeed.QuoteFeedService;
import org.marketcetera.photon.ui.BookComposite;
import org.marketcetera.quickfix.FIXMessageUtil;
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

	@Override
	protected void setUp() throws Exception {
		
		super.setUp();
	}

	public StockOrderTicketViewTest(String name) {
		super(name);
	}

	public void testShowOrder() throws NoSuchFieldException, IllegalAccessException {
		StockOrderTicket ticket = (StockOrderTicket) getTestView();
		Message message = FIXMessageUtil.newLimitOrder(new InternalID("asdf"),
				Side.BUY, BigDecimal.TEN, new MSymbol("QWER"), BigDecimal.ONE,
				TimeInForce.DAY, null);
		ticket.showOrder(message);
		AccessViolator violator = new AccessViolator(StockOrderTicket.class);
		assertEquals("10", ((Text)violator.getField("quantityText", ticket)).getText());
		assertEquals("B", ((CCombo)violator.getField("sideCCombo", ticket)).getText());
		assertEquals("1", ((Text)violator.getField("priceText", ticket)).getText());
		assertEquals("QWER", ((Text)violator.getField("symbolText", ticket)).getText());
		assertEquals("DAY", ((CCombo)violator.getField("tifCCombo", ticket)).getText());
	}
	
	public void testShowQuote() throws Exception {
		BundleContext bundleContext = PhotonPlugin.getDefault().getBundleContext();
		QuoteFeedService quoteFeed = MarketDataViewTest.getNullQuoteFeedService();
		bundleContext.registerService(QuoteFeedService.class.getName(), quoteFeed, null);
		
		
		StockOrderTicket view = (StockOrderTicket) getTestView();
		Message orderMessage = FIXMessageUtil.newLimitOrder(new InternalID("asdf"),
				Side.BUY, BigDecimal.TEN, new MSymbol("MRKT"), BigDecimal.ONE,
				TimeInForce.DAY, null);
		view.showOrder(orderMessage);

		
		JmsOperations jmsOperations = PhotonPlugin.getDefault().getQuoteJmsOperations();
		MarketDataSnapshotFullRefresh quoteMessageToSend = new MarketDataSnapshotFullRefresh();
		quoteMessageToSend.set(new Symbol("MRKT"));
		
		MarketDataViewTest.addGroup(quoteMessageToSend, MDEntryType.BID, BigDecimal.ONE, BigDecimal.TEN, new Date(), "BGUS");
		MarketDataViewTest.addGroup(quoteMessageToSend, MDEntryType.OFFER, BigDecimal.TEN, BigDecimal.TEN, new Date(), "BGUS");
		quoteMessageToSend.setString(LastPx.FIELD,"123.4");
		
		jmsOperations.convertAndSend(quoteMessageToSend);
		
		// TODO: fix me...
		delay(10000);
		
		AccessViolator violator = new AccessViolator(StockOrderTicket.class);

		BookComposite bookComposite = (BookComposite) violator.getField("bookComposite", view);
		Message returnedMessage = bookComposite.getInput();

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

		Message newMessage = FIXMessageUtil.newLimitOrder(new InternalID("asdf"),  //$NON-NLS-1$
				Side.BUY, BigDecimal.TEN, new MSymbol("DREI"), BigDecimal.ONE,  //$NON-NLS-1$
				TimeInForce.DAY, null);
		view.showOrder(newMessage);

		view.handleSend();

		delay(1);
		
		Message sentMessage = (Message) mockJmsOperations.getStoredMessage();
		try {
			String value = sentMessage.getHeader().getString(DeliverToCompID.FIELD);  // header field
			assertEquals("ABCD", value);  //$NON-NLS-1$
		} catch (FieldNotFound e) {
			fail();
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
		
		Message newMessage = FIXMessageUtil.newLimitOrder(new InternalID("asdf"),  //$NON-NLS-1$
				Side.BUY, BigDecimal.TEN, new MSymbol("DREI"), BigDecimal.ONE,  //$NON-NLS-1$
				TimeInForce.DAY, null);
		view.showOrder(newMessage);
		
		view.handleSend();
		
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


