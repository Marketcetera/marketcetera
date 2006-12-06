package org.marketcetera.photon;

import org.apache.log4j.Logger;
import org.eclipse.swt.widgets.Display;
import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.IDFactory;
import org.marketcetera.core.LoggerAdapter;
import org.marketcetera.core.NoMoreIDsException;
import org.marketcetera.photon.core.FIXMessageHistory;
import org.marketcetera.quickfix.FIXMessageUtil;
import org.marketcetera.quickfix.MarketceteraFIXException;
import org.springframework.jms.core.JmsOperations;
import org.springframework.jms.core.JmsTemplate;

import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.field.ClOrdID;
import quickfix.field.CxlRejReason;
import quickfix.field.ExecID;
import quickfix.field.MsgType;
import quickfix.field.OrdStatus;
import quickfix.field.OrderID;
import quickfix.field.OrigClOrdID;
import quickfix.field.SenderCompID;
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
	
	private JmsOperations jmsOperations;


	/** Creates a new instance of OrderManager with the specified {@link IDFactory}--
	 * used to create new order id's among other things,
	 * and the specified {@link FIXMessageHistory} into which all fix messages
	 * will be placed for later use in the UI.
	 * @param idFactory the id factory for generating order ids and other FIX ids
	 * @param fixMessageHistory the message history object for storing histories of FIX messages
	 * 
	 */
	public PhotonController(){
		
	}

	public void setMessageHistory(FIXMessageHistory fixMessageHistory) 
	{
		this.fixMessageHistory = fixMessageHistory;
	}
	
	public void handleMessage(Message aMessage) {
		if (aMessage.getHeader().isSetField(SenderCompID.FIELD))
		{
			handleCounterpartyMessage(aMessage);
		} else {
			handleInternalMessage(aMessage);
		}
	}

	protected void handleCounterpartyMessage(final Message aMessage) {
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

	protected void handleInternalMessage(Message aMessage) {
	
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
			internalMainLogger.info(rejectMsg);
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
		asyncExec(new Runnable() {
			public void run() {
				fixMessageHistory.addOutgoingMessage(aMessage);
			}
		});
		convertAndSend(aMessage);
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
		final Message cancelMessage = new quickfix.fix42.Message();
		cancelMessage.getHeader().setString(MsgType.FIELD, MsgType.ORDER_CANCEL_REQUEST);
		cancelMessage.setField(new OrigClOrdID(clOrdID));
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
	}

	private void convertAndSend(Message fixMessage) {
		if (jmsOperations != null){
			jmsOperations.convertAndSend(fixMessage);
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

	public JmsOperations getJmsOperations() {
		return jmsOperations;
	}

	public void setJmsOperations(JmsOperations jmsOperations) {
		this.jmsOperations = jmsOperations;
	}

}
