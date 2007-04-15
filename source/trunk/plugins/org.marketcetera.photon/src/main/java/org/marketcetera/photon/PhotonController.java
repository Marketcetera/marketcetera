package org.marketcetera.photon;

import org.apache.log4j.Logger;
import org.eclipse.swt.widgets.Display;
import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.IDFactory;
import org.marketcetera.core.LoggerAdapter;
import org.marketcetera.core.NoMoreIDsException;
import org.marketcetera.photon.core.FIXMessageHistory;
import org.marketcetera.photon.messaging.JMSFeedService;
import org.marketcetera.quickfix.FIXMessageFactory;
import org.marketcetera.quickfix.FIXMessageUtil;
import org.marketcetera.quickfix.MarketceteraFIXException;
import org.osgi.util.tracker.ServiceTracker;
import org.springframework.jms.core.JmsOperations;

import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.field.ClOrdID;
import quickfix.field.CxlRejReason;
import quickfix.field.ExecID;
import quickfix.field.OrdStatus;
import quickfix.field.OrderID;
import quickfix.field.OrigClOrdID;
import quickfix.field.Symbol;
import quickfix.field.Text;

/**
 * OrderManager is the main repository for business logic.  It can be considered
 * the "controller" in a standard model-view-controller architecture.  The main entry
 * points are the <code>handle*</code> methods for handling incoming and outgoing 
 * messages of various types.  
 * 
 * @author gmiller
 */
@ClassVersion("$Id$")
public class PhotonController {

	private Logger internalMainLogger = PhotonPlugin.getMainConsoleLogger();

	private FIXMessageHistory fixMessageHistory;

	private IDFactory idFactory;
	
	ServiceTracker jmsServiceTracker;

	private FIXMessageFactory messageFactory;

	/** Creates a new instance of OrderManager.  Also gets a reference to
	 *  the JMS service using a {@link ServiceTracker}
	 */
	public PhotonController(){
		jmsServiceTracker = new ServiceTracker(PhotonPlugin.getDefault().getBundleContext(),
				JMSFeedService.class.getName(), null);
		jmsServiceTracker.open();
	}

	public void setMessageHistory(FIXMessageHistory fixMessageHistory) 
	{
		this.fixMessageHistory = fixMessageHistory;
	}
	
	public void setMessageFactory(FIXMessageFactory messageFactory) {
		this.messageFactory = messageFactory;
	}

	public void handleCounterpartyMessage(final Message aMessage) {
		asyncExec(
			new Runnable() {
				public void run() {
					fixMessageHistory.addIncomingMessage(aMessage);
				}
			}
		);
		try {
			if (FIXMessageUtil.isExecutionReport(aMessage)) {
				handleExecutionReport(aMessage);
			} else if (FIXMessageUtil.isCancelReject(aMessage)) {
				handleCancelReject(aMessage);
			}
		} catch (FieldNotFound fnfEx) {
			MarketceteraFIXException mfix = MarketceteraFIXException.createFieldNotFoundException(fnfEx);
			internalMainLogger.error(
					"Error decoding incoming message "+mfix.getMessage(), mfix);
			mfix.printStackTrace();
		} catch (Throwable ex) {
			internalMainLogger.error(
					"Error decoding incoming message "+ex.getMessage(), ex);
			ex.printStackTrace();
		}
	}
	
	protected void asyncExec(Runnable runnable) {
		Display.getDefault().asyncExec(runnable);
	}

	public void handleInternalMessage(Message aMessage) {
	
		try {
			if (FIXMessageUtil.isOrderSingle(aMessage)) {
				handleNewOrder(aMessage);
			} else if (FIXMessageUtil.isCancelRequest(aMessage)) {
				cancelOneOrder(aMessage);
			} else if (FIXMessageUtil.isCancelReplaceRequest(aMessage)) {
				cancelReplaceOneOrder(aMessage);
			}
		} catch (FieldNotFound fnfEx) {
			MarketceteraFIXException mfix = MarketceteraFIXException.createFieldNotFoundException(fnfEx);
			internalMainLogger.error(
					"Error decoding incoming message "+mfix.getMessage(), mfix);
			mfix.printStackTrace();
		} catch (Throwable ex) {
			internalMainLogger.error(
					"Error decoding incoming message "+ex.getMessage(), ex);
			ex.printStackTrace();
		}
	}

	protected void handleExecutionReport(Message aMessage) throws FieldNotFound, NoMoreIDsException {
		String orderID;
		ClOrdID clOrdID = new ClOrdID();
		aMessage.getField(clOrdID);
		orderID = clOrdID.getValue();
		char ordStatus = aMessage.getChar(OrdStatus.FIELD);

		if (ordStatus == OrdStatus.REJECTED) {
			String rejectReason = "";
			if(aMessage.isSetField(Text.FIELD)) {
				rejectReason = ": "+aMessage.getString(Text.FIELD);
			}
			
			String rejectMsg = "Order rejected " + orderID + " "
					+ aMessage.getString(Symbol.FIELD) + rejectReason;
			internalMainLogger.error(rejectMsg);
		}
	}


	protected void handleCancelReject(Message aMessage) throws FieldNotFound {
		String reason = null;
		try {
			reason = aMessage.getString(CxlRejReason.FIELD);
		} catch (FieldNotFound fnf){
			//do nothing
		}
		String text = aMessage.getString(Text.FIELD);
		String origClOrdID = aMessage.getString(OrigClOrdID.FIELD);
		String errorMsg = "Cancel rejected for order " + origClOrdID + ": "
				+ (text == null ? "" : text)
				+ (reason == null ? "" : " (" + reason + ")");
		internalMainLogger.error(errorMsg);
	}



	protected void handleNewOrder(final Message aMessage) {
		try {
			String orderID;
			orderID = idFactory.getNext();
			if (!aMessage.isSetField(ClOrdID.FIELD)){
				aMessage.setField(new ClOrdID(orderID));
			}
			asyncExec(new Runnable() {
				public void run() {
					fixMessageHistory.addOutgoingMessage(aMessage);
				}
			});
			convertAndSend(aMessage);
		} catch (NoMoreIDsException e) {
			internalMainLogger.error("Could not send message, no order IDs", e);
		}
	}

	protected void cancelReplaceOneOrder(final Message cancelMessage) {
		asyncExec(new Runnable() {
			public void run() {
				fixMessageHistory.addOutgoingMessage(cancelMessage);
			}
		});
		convertAndSend(cancelMessage);
	}

	protected void cancelOneOrder(Message cancelMessage)
			throws NoMoreIDsException, FieldNotFound {
		String clOrdId = (String) cancelMessage.getString(OrigClOrdID.FIELD);
		cancelOneOrderByClOrdID(clOrdId);
	}
	
	public void cancelOneOrderByClOrdID(String clOrdID) throws NoMoreIDsException {
		Message latestMessage = fixMessageHistory.getLatestExecutionReport(clOrdID);
		if (latestMessage == null){
			latestMessage = fixMessageHistory.getLatestMessage(clOrdID);
			if (latestMessage == null){
				internalMainLogger.error("Could not send cancel request for order ID "+clOrdID);
				return;
			}
		}
		try { LoggerAdapter.debug("Exec id for cancel execution report:"+latestMessage.getString(ExecID.FIELD), this); } catch (FieldNotFound e1) {	}
		try {
			final Message cancelMessage = messageFactory.newCancelFromMessage(latestMessage);
			cancelMessage.setField(new ClOrdID(idFactory.getNext()));
			try {
				cancelMessage.setField(new OrderID(latestMessage.getString(OrderID.FIELD)));
			} catch (FieldNotFound e) {
				// do nothing
			}
			FIXMessageUtil.fillFieldsFromExistingMessage(cancelMessage, latestMessage);
			asyncExec(new Runnable() {
				public void run() {
					fixMessageHistory.addOutgoingMessage(cancelMessage);
				}
			});
			convertAndSend(cancelMessage);
		} catch (FieldNotFound fnf){
			LoggerAdapter.error("Could not send cancel for message "+latestMessage.toString(), fnf, this);
		}
	}

	private void convertAndSend(Message fixMessage) {
		JMSFeedService service = (JMSFeedService) jmsServiceTracker.getService();
		JmsOperations jmsOperations;
		if (service != null && ((jmsOperations = service.getJmsOperations()) != null)){
			try {
				jmsOperations.convertAndSend(fixMessage);
			} catch (Exception ex){
				service.onException(ex);
			}
		} else {
			internalMainLogger.error("Could not send message, not connected");
		}
	}
	
	public void setIDFactory(IDFactory fact){
		idFactory = fact;
	}

	/**
	 * @return Returns the mainConsoleLogger.
	 */
	public Logger getMainConsoleLogger() {
		return internalMainLogger;
	}

	/**
	 * @param mainConsoleLogger The mainConsoleLogger to set.
	 */
	public void setMainConsoleLogger(Logger mainConsoleLogger) {
		this.internalMainLogger = mainConsoleLogger;
	}


}
