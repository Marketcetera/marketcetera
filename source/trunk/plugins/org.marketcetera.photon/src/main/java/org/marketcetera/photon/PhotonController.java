package org.marketcetera.photon;

import java.util.Vector;

import org.apache.log4j.Logger;
import org.eclipse.swt.widgets.Display;
import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.IDFactory;
import org.marketcetera.core.NoMoreIDsException;
import org.marketcetera.messagehistory.FIXMessageHistory;
import org.marketcetera.messagehistory.MessageVisitor;
import org.marketcetera.photon.messaging.JMSFeedService;
import org.marketcetera.quickfix.FIXMessageFactory;
import org.marketcetera.quickfix.FIXMessageUtil;
import org.marketcetera.quickfix.MarketceteraFIXException;
import org.osgi.util.tracker.ServiceTracker;
import org.springframework.jms.core.JmsOperations;

import quickfix.CharField;
import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.field.ClOrdID;
import quickfix.field.CxlRejReason;
import quickfix.field.ExecID;
import quickfix.field.OrdStatus;
import quickfix.field.OrderID;
import quickfix.field.OrigClOrdID;
import quickfix.field.Side;
import quickfix.field.Symbol;

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
		fixMessageHistory.addIncomingMessage(aMessage);
		try {
			if (FIXMessageUtil.isExecutionReport(aMessage)) {
				handleExecutionReport(aMessage);
			} else if (FIXMessageUtil.isCancelReject(aMessage)) {
				handleCancelReject(aMessage);
			} else if (FIXMessageUtil.isReject(aMessage) || 
					FIXMessageUtil.isBusinessMessageReject(aMessage)) {
				handleReject(aMessage);
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
			} else if (FIXMessageUtil.isResendRequest(aMessage)) {
				requestResend(aMessage);
			} else {
				internalMainLogger.warn("Photon controller received message of unknown type: "+aMessage.toString());
			}
		} catch (FieldNotFound fnfEx) {
			MarketceteraFIXException mfix = MarketceteraFIXException.createFieldNotFoundException(fnfEx);
			internalMainLogger.error(
					"Error decoding outgoing message "+mfix.getMessage(), mfix);
			mfix.printStackTrace();
		} catch (Throwable ex) {
			internalMainLogger.error(
					"Error decoding outgoing message "+ex.getMessage(), ex);
			ex.printStackTrace();
		}
	}


	protected void handleExecutionReport(Message aMessage) throws FieldNotFound, NoMoreIDsException {
		char ordStatus = aMessage.getChar(OrdStatus.FIELD);

		if (ordStatus == OrdStatus.REJECTED) {
			String rejectReason = FIXMessageUtil.getTextOrEncodedText(aMessage,"Unknown");
			
			String orderID = "";
			try {
				orderID = aMessage.getString(ClOrdID.FIELD);
			} catch (Exception ex){
				try {
					orderID = aMessage.getString(OrderID.FIELD);
				} catch (Exception ex2){
					// do nothing
				}
			}

			String rejectMsg = "Order rejected " + orderID + " "
					+ aMessage.getString(Symbol.FIELD) + ": "+ rejectReason;
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
		String text = FIXMessageUtil.getTextOrEncodedText(aMessage,"Unknown");
		String origClOrdID = "Unknown";
		if (aMessage.isSetField(OrigClOrdID.FIELD)){
			origClOrdID = aMessage.getString(OrigClOrdID.FIELD);
		}
		String errorMsg = "Cancel rejected for order " + origClOrdID + ": "
				+ (text == null ? "" : text)
				+ (reason == null ? "" : " (" + reason + ")");
		internalMainLogger.error(errorMsg);
	}
	
	protected void handleReject(Message aMessage) throws FieldNotFound {
		String text = FIXMessageUtil.getTextOrEncodedText(aMessage,"Unknown");
		String errorMsg = "Received reject message: " + (text == null ? "" : text);
		internalMainLogger.error(errorMsg);
		internalMainLogger.debug("Reject FIX reply: " + aMessage);
	}

	protected void handleNewOrder(final Message aMessage) {
		try {
			String orderID;
			orderID = idFactory.getNext();
			if (!aMessage.isSetField(ClOrdID.FIELD)){
				aMessage.setField(new ClOrdID(orderID));
			}
			messageFactory.getMsgAugmentor().newOrderSingleAugment(aMessage);
			fixMessageHistory.addOutgoingMessage(aMessage);
			convertAndSend(aMessage);
		} catch (NoMoreIDsException e) {
			internalMainLogger.error("Could not send message, no order IDs", e);
		}
	}

	private void requestResend(Message message) {
		fixMessageHistory.addOutgoingMessage(message);
		convertAndSend(message);
	}

	protected void cancelReplaceOneOrder(final Message cancelMessage) {
		fixMessageHistory.addOutgoingMessage(cancelMessage);
		convertAndSend(cancelMessage);
	}

	protected void cancelOneOrder(Message cancelMessage)
			throws NoMoreIDsException, FieldNotFound {
		String clOrdId = (String) cancelMessage.getString(OrigClOrdID.FIELD);
		cancelOneOrderByClOrdID(clOrdId);
	}
	
	public void cancelOneOrderByClOrdID(String clOrdID) throws NoMoreIDsException {
		cancelOneOrderByClOrdID(clOrdID, null);
	}
	public void cancelOneOrderByClOrdID(String clOrdID, String textField) throws NoMoreIDsException {
		Message latestMessage = fixMessageHistory.getLatestExecutionReport(clOrdID);
		if (latestMessage == null){
			latestMessage = fixMessageHistory.getLatestMessage(clOrdID);
			if (latestMessage == null){
				internalMainLogger.error("Could not send cancel request for order ID "+clOrdID);
				return;
			}
		}
		try { 
			if(internalMainLogger.isDebugEnabled()) {
				internalMainLogger.debug("Exec id for cancel execution report:"+latestMessage.getString(ExecID.FIELD)); 
			} 
		} catch (FieldNotFound ignored) {	}
		try {
			final Message cancelMessage = messageFactory.newCancelFromMessage(latestMessage);
			cancelMessage.setField(new ClOrdID(idFactory.getNext()));
			try {
				cancelMessage.setField(new OrderID(latestMessage.getString(OrderID.FIELD)));
			} catch (FieldNotFound e) {
				// do nothing
			}
			FIXMessageUtil.fillFieldsFromExistingMessage(cancelMessage, latestMessage);
			fixMessageHistory.addOutgoingMessage(cancelMessage);
			convertAndSend(cancelMessage);
		} catch (FieldNotFound fnf){
			internalMainLogger.error("Could not send cancel for message "+latestMessage.toString(), fnf);
		}
	}

	/** Panic button: cancel all open orders
	 * Need to do the cancel in 2 phases: first collect all clOrderIds to cancel, 
	 * then cancel them.
	 * Trying to cancel them while collecting results in a deadlock, since we are 
	 * holding a read lock while collecting, and sending a cancel tries to acquire 
	 * the write lock to add new messages to message history. 
	 */
	public void cancelAllOpenOrders()
	{
		final Vector<String> clOrdIdsToCancel = new Vector<String>();
		fixMessageHistory.visitOpenOrdersExecutionReports(new MessageVisitor() {
            public void visitOpenOrderExecutionReports(Message message) {
                try {
            		String clOrdId = (String) message.getString(ClOrdID.FIELD);
            		clOrdIdsToCancel.add(clOrdId);
                } catch (FieldNotFound fnf){
                	internalMainLogger.error("Could not send cancel for message "+message.toString(), fnf);
                }
            }
        });
		for (String clOrdId: clOrdIdsToCancel) {
    		try {
				cancelOneOrderByClOrdID(clOrdId, "PANIC");
	            if(internalMainLogger.isDebugEnabled()) { internalMainLogger.debug("cancelling order for "+clOrdId);} 
            } catch (NoMoreIDsException ignored) {
                // ignore
			}
		}
	}

	/**
	 * For debug purposes only. Logs a debug message if the Side field is
	 * missing.
	 */
	private void checkSideField(Message fixMessage) {
		try {
			CharField sideField = fixMessage.getField(new Side());
			if (sideField == null || sideField.getValue() == 0) {
				throw new MarketceteraFIXException("Missing side field. Was: "
						+ sideField);
			}
		} catch (Exception anyException) {
			internalMainLogger.debug("Missing Side field in message: "
					+ fixMessage);
		}
	}
	
	public void convertAndSend(Message fixMessage) {
		JMSFeedService service = (JMSFeedService) jmsServiceTracker.getService();
		JmsOperations jmsOperations;
		if (service != null && ((jmsOperations = service.getJmsOperations()) != null)){
			try {
				if(internalMainLogger.isDebugEnabled()) {
					internalMainLogger.debug("Sending: " + fixMessage);
					checkSideField(fixMessage);
				}
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
	
	public IDFactory getIDFactory() {
		return idFactory;
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
