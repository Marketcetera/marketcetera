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
import org.marketcetera.util.log.I18NBoundMessage1P;
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
@ClassVersion("$Id$") //$NON-NLS-1$
public class PhotonController
    implements Messages
{

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
			internalMainLogger.error(CANNOT_DECODE_INCOMING_SPECIFIED_MESSAGE.getText(mfix.getMessage()),
			                         mfix);
		} catch (Throwable ex) {
            internalMainLogger.error(CANNOT_DECODE_INCOMING_MESSAGE.getText(),
                                     ex);
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
				internalMainLogger.warn(UNKNOWN_INTERNAL_MESSAGE_TYPE.getText(aMessage.toString()));
			}
		} catch (FieldNotFound fnfEx) {
			MarketceteraFIXException mfix = MarketceteraFIXException.createFieldNotFoundException(fnfEx);
			internalMainLogger.error(CANNOT_DECODE_OUTGOING_SPECIFIED_MESSAGE.getText(mfix.getMessage()),
			                         mfix);
		} catch (Throwable ex) {
            internalMainLogger.error(CANNOT_DECODE_OUTGOING_MESSAGE.getText(),
                                     ex);
		}
	}


	protected void handleExecutionReport(Message aMessage) throws FieldNotFound, NoMoreIDsException {
		char ordStatus = aMessage.getChar(OrdStatus.FIELD);

		if (ordStatus == OrdStatus.REJECTED) {
			String rejectReason = FIXMessageUtil.getTextOrEncodedText(aMessage,"Unknown"); //$NON-NLS-1$
			
			String orderID = ""; //$NON-NLS-1$
			try {
				orderID = aMessage.getString(ClOrdID.FIELD);
			} catch (Exception ex){
				try {
					orderID = aMessage.getString(OrderID.FIELD);
				} catch (Exception ex2){
					// do nothing
				}
			}
			
			String rejectMsg = REJECT_MESSAGE.getText(orderID,
			                                          aMessage.getString(Symbol.FIELD),
			                                          rejectReason);
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
		String text = FIXMessageUtil.getTextOrEncodedText(aMessage,
		                                                  "Unknown"); //$NON-NLS-1$
		String origClOrdID = "Unknown"; //$NON-NLS-1$
		if (aMessage.isSetField(OrigClOrdID.FIELD)){
			origClOrdID = aMessage.getString(OrigClOrdID.FIELD);
		}
		String errorMsg = CANCEL_REJECT_MESSAGE.getText(origClOrdID,
		                                                (text == null ? 0 : 1),
		                                                text,
		                                                (reason == null ? 0 : 1),
		                                                reason);
		internalMainLogger.error(errorMsg);
	}
	
	protected void handleReject(Message aMessage) throws FieldNotFound {
		String text = FIXMessageUtil.getTextOrEncodedText(aMessage,
		                                                  "Unknown"); //$NON-NLS-1$
		String errorMsg = HANDLE_REJECT_MESSAGE.getText((text == null ? 0 : 1),
		                                                text);
		internalMainLogger.error(errorMsg);
		internalMainLogger.debug("Reject FIX reply: " + aMessage); //$NON-NLS-1$
	}

	protected void handleNewOrder(final Message aMessage) {
		try {
			String orderID;
			orderID = idFactory.getNext();
			if (!aMessage.isSetField(ClOrdID.FIELD)){
				aMessage.setField(new ClOrdID(orderID));
			}
			messageFactory.getMsgAugmentor().newOrderSingleAugment(aMessage);
			convertAndSend(aMessage);
		} catch (NoMoreIDsException e) {
			internalMainLogger.error(CANNOT_SEND_MESSAGE_NO_ID.getText(),
			                         e);
		}
	}

	private void requestResend(Message message) {
		convertAndSend(message);
	}

	protected void cancelReplaceOneOrder(final Message cancelMessage) {
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
				internalMainLogger.error(CANNOT_SEND_CANCEL.getText(clOrdID));
				return;
			}
		}
		try { 
			if(internalMainLogger.isDebugEnabled()) {
				internalMainLogger.debug("Exec id for cancel execution report:"+latestMessage.getString(ExecID.FIELD));  //$NON-NLS-1$
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
			convertAndSend(cancelMessage);
		} catch (FieldNotFound fnf){
            internalMainLogger.error(CANNOT_SEND_CANCEL_FOR_REASON.getText(clOrdID,
                                                                           latestMessage.toString()),
                                                                           fnf);
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
                String clOrdId = "unknown"; //$NON-NLS-1$
                try {
            		clOrdId = (String)message.getString(ClOrdID.FIELD);
            		clOrdIdsToCancel.add(clOrdId);
                } catch (FieldNotFound fnf){
                    internalMainLogger.error(CANNOT_SEND_CANCEL_FOR_REASON.getText(clOrdId,
                                                                                   message.toString()),
                                                                                   fnf);
                }
            }
        });
		for (String clOrdId: clOrdIdsToCancel) {
    		try {
				cancelOneOrderByClOrdID(clOrdId, "PANIC"); //$NON-NLS-1$
	            if(internalMainLogger.isDebugEnabled()) { internalMainLogger.debug("cancelling order for "+clOrdId);}  //$NON-NLS-1$
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
				throw new MarketceteraFIXException(new I18NBoundMessage1P(MISSING_SIDE,
				                                                          fixMessage));
			}
		} catch (Exception anyException) {
			internalMainLogger.debug(MISSING_SIDE.getText(fixMessage),
			                         anyException);
		}
	}
	
	public void convertAndSend(Message fixMessage) {
		internalMainLogger.info(PHOTON_CONTROLLER_SENDING_MESSAGE.getText(fixMessage));
		if(internalMainLogger.isDebugEnabled()) {
			checkSideField(fixMessage);
		}
		JMSFeedService service = (JMSFeedService) jmsServiceTracker.getService();
		JmsOperations jmsOperations;
		if (service != null && ((jmsOperations = service.getJmsOperations()) != null)){
			try {
				jmsOperations.convertAndSend(fixMessage);
			} catch (Exception ex){
				service.onException(ex);
			}
		} else {
			internalMainLogger.error(CANNOT_SEND_NOT_CONNECTED.getText());
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
