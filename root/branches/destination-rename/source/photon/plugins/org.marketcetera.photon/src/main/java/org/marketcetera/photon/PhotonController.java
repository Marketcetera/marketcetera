package org.marketcetera.photon;

import java.util.Vector;

import org.apache.log4j.Logger;
import org.eclipse.swt.widgets.Display;
import org.marketcetera.client.Client;
import org.marketcetera.client.ClientInitException;
import org.marketcetera.client.ConnectionException;
import org.marketcetera.client.OrderValidationException;
import org.marketcetera.client.ReportListener;
import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.MSymbol;
import org.marketcetera.core.NoMoreIDsException;
import org.marketcetera.event.HasFIXMessage;
import org.marketcetera.messagehistory.MessageVisitor;
import org.marketcetera.messagehistory.TradeReportsHistory;
import org.marketcetera.photon.messaging.ClientFeedService;
import org.marketcetera.quickfix.FIXMessageUtil;
import org.marketcetera.quickfix.MarketceteraFIXException;
import org.marketcetera.trade.BrokerID;
import org.marketcetera.trade.ExecutionReport;
import org.marketcetera.trade.FIXOrder;
import org.marketcetera.trade.Factory;
import org.marketcetera.trade.MessageCreationException;
import org.marketcetera.trade.Order;
import org.marketcetera.trade.OrderCancel;
import org.marketcetera.trade.OrderCancelReject;
import org.marketcetera.trade.OrderID;
import org.marketcetera.trade.OrderReplace;
import org.marketcetera.trade.OrderSingle;
import org.marketcetera.trade.OrderStatus;
import org.marketcetera.trade.Originator;
import org.marketcetera.trade.ReportBase;
import org.osgi.util.tracker.ServiceTracker;

import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.field.ClOrdID;
import quickfix.field.ExecID;
import quickfix.field.OrigClOrdID;

/* $License$ */

/**
 * OrderManager is the main repository for business logic.  It can be considered
 * the "controller" in a standard model-view-controller architecture.  The main entry
 * points are the <code>handle*</code> methods for handling incoming and outgoing 
 * messages of various types.  
 * 
 * @author gmiller
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public class PhotonController
    implements Messages, ReportListener
{

	private Logger internalMainLogger = PhotonPlugin.getMainConsoleLogger();

	private TradeReportsHistory fixMessageHistory;

	private final ServiceTracker mClientServiceTracker;

	public static final BrokerID DEFAULT_DESTINATION = null; 

	/** Creates a new instance of OrderManager.  Also gets a reference to
	 *  the Client service using a {@link ServiceTracker}
	 */
	public PhotonController(){
		mClientServiceTracker = new ServiceTracker(PhotonPlugin.getDefault().getBundleContext(),
				ClientFeedService.class.getName(), null);
		mClientServiceTracker.open();
	}

	public void setMessageHistory(TradeReportsHistory fixMessageHistory) 
	{
		this.fixMessageHistory = fixMessageHistory;
	}
	
	@Override
	public void receiveCancelReject(OrderCancelReject inReport) {
		checkReportID(inReport);
		fixMessageHistory.addIncomingMessage(inReport);
		try {
			handleCancelReject(inReport);
		} catch (FieldNotFound e) {
			MarketceteraFIXException mfix = MarketceteraFIXException.createFieldNotFoundException(e);
			internalMainLogger.error(CANNOT_DECODE_INCOMING_SPECIFIED_MESSAGE.getText(mfix.getMessage()),
			                         mfix);
		}		
	}

	@Override
	public void receiveExecutionReport(ExecutionReport inReport) {
		checkReportID(inReport);
		fixMessageHistory.addIncomingMessage(inReport);
		try {
			handleExecutionReport(inReport);
		} catch (NoMoreIDsException e) {
            internalMainLogger.error(CANNOT_DECODE_INCOMING_MESSAGE.getText(),
                    e);
		} catch (FieldNotFound e) {
			MarketceteraFIXException mfix = MarketceteraFIXException.createFieldNotFoundException(e);
			internalMainLogger.error(CANNOT_DECODE_INCOMING_SPECIFIED_MESSAGE.getText(mfix.getMessage()),
			                         mfix);
		}		
	}
	
	private void checkReportID(ReportBase report) {
		if (report.getReportID() == null) {
			internalMainLogger.error(PHOTON_CONTROLLER_MISSING_REPORT_ID.getText(report.toString()));
		}
	}
	
	protected void asyncExec(Runnable runnable) {
		Display.getDefault().asyncExec(runnable);
	}

	public void handleInternalMessage(Message aMessage) {
		handleInternalMessage(aMessage, DEFAULT_DESTINATION);
	}
	
	public void handleInternalMessage(Message aMessage, String brokerId) {
		handleInternalMessage(aMessage, brokerId == null ? null : new BrokerID(brokerId));
	}
	
	public void handleInternalMessage(Message aMessage, BrokerID destination) {
		Factory factory = Factory.getInstance();
	
		try {
			if (FIXMessageUtil.isOrderSingle(aMessage)) {
				sendOrder(factory.createOrderSingle(aMessage, destination));
			} else if (FIXMessageUtil.isCancelRequest(aMessage)) {
				sendOrder(factory.createOrderCancel(aMessage, destination));
			} else if (FIXMessageUtil.isCancelReplaceRequest(aMessage)) {
				sendOrder(factory.createOrderReplace(aMessage, destination));
			} else {
				internalMainLogger.warn(UNKNOWN_INTERNAL_MESSAGE_TYPE.
						getText(aMessage.toString()));
			}
		} catch (MessageCreationException e) {
            internalMainLogger.error(ERROR_HANDLING_MESSAGE.getText(
            		aMessage.toString()),e);
		}
	}


	protected void handleExecutionReport(ExecutionReport inReport) throws FieldNotFound, NoMoreIDsException {

		if (OrderStatus.Rejected == inReport.getOrderStatus()) {
			// TODO: improve ExecutionReport API to expose encoded text
			String rejectReason = inReport.getText();
			if(rejectReason == null) {
				if (inReport instanceof HasFIXMessage) {
					rejectReason = FIXMessageUtil.getTextOrEncodedText(((HasFIXMessage) inReport).getMessage(), Messages.UNKNOWN_VALUE.getText());
				} else {
					rejectReason = Messages.UNKNOWN_VALUE.getText();
				}
			}
			
			org.marketcetera.trade.OrderID orderID = inReport.getOrderID();
			
			MSymbol symbol = inReport.getSymbol();
			String rejectMsg = REJECT_MESSAGE.getText(orderID.getValue(),
			                                          symbol == null ? Messages.UNKNOWN_VALUE.getText() : symbol.getFullSymbol(),
			                                          rejectReason);
			internalMainLogger.error(rejectMsg);
		}
	}


	protected void handleCancelReject(OrderCancelReject inReport) throws FieldNotFound {
		String reason = null;
		String text = inReport.getText();
		if(text == null) {
			text = Messages.UNKNOWN_VALUE.getText();
		}
		String origClOrdID = Messages.UNKNOWN_VALUE.getText();
		org.marketcetera.trade.OrderID oID = inReport.getOriginalOrderID();
		if(oID != null) {
			origClOrdID = oID.getValue();
		}
		String errorMsg = CANCEL_REJECT_MESSAGE.getText(origClOrdID,
		                                                (text == null ? 0 : 1),
		                                                text,
		                                                (reason == null ? 0 : 1),
		                                                reason);
		internalMainLogger.error(errorMsg);
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
		OrderID orderid = new OrderID(clOrdID);
		ReportBase latestExecutionReport = fixMessageHistory.getLatestExecutionReport(orderid);
		Message latestMessage;
		if (latestExecutionReport == null){
			latestMessage = fixMessageHistory.getLatestMessage(orderid);
			if (latestExecutionReport == null){
				internalMainLogger.error(CANNOT_SEND_CANCEL.getText(clOrdID));
				return;
			}
		} else if (latestExecutionReport instanceof HasFIXMessage) {
			latestMessage = ((HasFIXMessage) latestExecutionReport).getMessage();
		} else {
			internalMainLogger.error(CANNOT_SEND_CANCEL.getText(clOrdID));
			return;
		}
		
		try { 
			if(internalMainLogger.isDebugEnabled()) {
				internalMainLogger.debug("Exec id for cancel execution report:"+latestMessage.getString(ExecID.FIELD));  //$NON-NLS-1$
			} 
		} catch (FieldNotFound ignored) {	}
		try {
			ExecutionReport report = Factory.getInstance().createExecutionReport(
					latestMessage, DEFAULT_DESTINATION, Originator.Server);
			sendOrder(Factory.getInstance().createOrderCancel(report));
		} catch (MessageCreationException e) {
			internalMainLogger.error(CANNOT_SEND_CANCEL_FOR_REASON.getText(clOrdID,
					latestMessage.toString()),e);
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

	public void sendOrder(Order inOrder) {
		internalMainLogger.info(PHOTON_CONTROLLER_SENDING_MESSAGE.getText(inOrder.toString()));
		ClientFeedService service = (ClientFeedService) mClientServiceTracker.getService();
		if (service != null){
			try {
				Client client = service.getClient();
				if(inOrder instanceof OrderSingle) {
					client.sendOrder((OrderSingle) inOrder);
				} else if(inOrder instanceof OrderReplace) {
					client.sendOrder((OrderReplace)inOrder);
				} else if(inOrder instanceof OrderCancel) {
					client.sendOrder((OrderCancel)inOrder);
				} else if(inOrder instanceof FIXOrder) {
					client.sendOrderRaw((FIXOrder)inOrder);
				} else {
					internalMainLogger.error(SEND_ORDER_FAIL_UNKNOWN_TYPE.getText(inOrder.toString()));
				}
			} catch (OrderValidationException e){
				internalMainLogger.error(SEND_ORDER_VALIDATION_FAILED.getText(inOrder.toString()), e);
			} catch (ConnectionException ignore){
				//ignore as the exception listener will be invoked 
			} catch (ClientInitException e) {
				internalMainLogger.error(SEND_ORDER_NOT_INITIALIZED.getText(inOrder.toString()), e);
			}
		} else {
			internalMainLogger.error(CANNOT_SEND_NOT_CONNECTED.getText());
		}
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
