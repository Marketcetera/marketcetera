package org.marketcetera.ors;

import org.apache.commons.lang.ObjectUtils;

import org.marketcetera.core.*;
import org.marketcetera.quickfix.*;
import org.marketcetera.util.log.I18NBoundMessage1P;
import org.marketcetera.util.log.I18NBoundMessage2P;
import org.marketcetera.util.log.SLF4JLoggerProxy;

import org.springframework.jms.core.JmsOperations;
import org.springframework.jms.JmsException;
import quickfix.*;
import quickfix.field.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedList;

/**
 * OutgoingMessageHandler is the "middle" stage that recieves an incoming order request
 * from JMS, does some operations on it in and sends it on to the FIX sender
 *
 * This is essentially the "OrderManager" stage: we send an immediate "ack" to an
 * incoming NewOrder, apply order modifiers and send it on.
 *
 * @author gmiller
 * $Id$
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class OutgoingMessageHandler {

    private MessageModifierManager messageModifierMgr;
    private MessageRouteManager routeMgr;
    private SessionID defaultSessionID;         // used to store the SessionID so that FIX sender can find it
    private IQuickFIXSender quickFIXSender = new QuickFIXSender();
    private IDFactory idFactory;
    private FIXMessageFactory msgFactory;
    private OrderLimits orderLimits;
    private JmsOperations incomingCommandsCopier;

    // this is temporary, until we have much better JMX visibility
    protected QuickFIXApplication qfApp;
    
    public OutgoingMessageHandler(FIXMessageFactory inFactory, OrderLimits inLimits,
                                  QuickFIXApplication inQFApp, IDFactory inIdFactory)
            throws ConfigError, FieldConvertError, CoreException {
        setOrderRouteManager(new MessageRouteManager());
        msgFactory = inFactory;
        orderLimits = inLimits;
        qfApp = inQFApp;
        setMessageModifierMgr(new MessageModifierManager(new LinkedList<MessageModifier>(), msgFactory));
        idFactory = inIdFactory;
        try {
            idFactory.init();
        } catch (Exception ex) {
            SLF4JLoggerProxy.debug(this, ex, "Error initializing the ID factory"); //$NON-NLS-1$
            // ignore the exception - should get the in-memory id factory instead
        }
    }

    public void setOrderRouteManager(MessageRouteManager inMgr)
    {
        routeMgr = inMgr;
    }

    public void setMessageModifierMgr(MessageModifierManager inMgr){
		messageModifierMgr = inMgr;
        qfApp.setMessageModifierMgr(inMgr);
    }

    /** Only supports NewOrderSingle, OrderCancelReplace and OrderCancel orders at this point
     * Rejects orders that are of the wrong FIX version, or if the ORS is not logged on to a FIX destination.
     * Runs the incoming orders through message modifiers, and forwards them on to a FIX destination.
     * @param message Incoming message
     * @return ExecutionReport for this message
     * @throws CoreException
     */
    public Message handleMessage(Message message) throws CoreException {
        if(message == null) {
            Messages.ERROR_INCOMING_MSG_NULL.error(this);
            return null;
        }

        // send a copy to the copyTopic
        try {
            if(incomingCommandsCopier!=null) {
                incomingCommandsCopier.convertAndSend(message);
            }
        } catch(JmsException ex) {
            // ignore
        }
        
        if(!qfApp.isLoggedOn()) {
            Message notLoggedOnReject = createRejectionMessage(new CoreException(Messages.ERROR_NO_DESTINATION_CONNECTION),
                                                               message);
            // explicitly remove the OrdStatus b/c we don't know what it is - we aren't logged on
            notLoggedOnReject.setField(new OrdStatus(OrdStatus.REJECTED));
            return notLoggedOnReject;
        }


        try {
            String version = message.getHeader().getField(new BeginString()).getValue();
            if(!msgFactory.getBeginString().equals(version)) {
                return createRejectionMessage(new CoreException(new I18NBoundMessage2P(Messages.ERROR_MISMATCHED_FIX_VERSION, msgFactory.getBeginString(), version)),
                                              message);
            }
        } catch (FieldNotFound fieldNotFound) {
            return createRejectionMessage(new CoreException(Messages.ERROR_MESSAGE_MALFORMED_NO_FIX_VERSION),
                                          message);
        }

        Message returnExecReport = null;
        try {
            if(!(FIXMessageUtil.isOrderSingle(message) || FIXMessageUtil.isCancelRequest(message)
                    || FIXMessageUtil.isCancelReplaceRequest(message))) {
                throw new UnsupportedMessageType();
            }

            messageModifierMgr.modifyMessage(message);
            orderLimits.verifyOrderLimits(message);
            routeMgr.modifyMessage(message, msgFactory.getMsgAugmentor());
            // if single, pre-create an executionReport and send it back
            if (FIXMessageUtil.isOrderSingle(message))
            {
                returnExecReport = executionReportFromNewOrder(message);
            }
            sendMessage(message);
            if(returnExecReport != null) {
                SLF4JLoggerProxy.debug(this, "Sending immediate execReport: {}", returnExecReport); //$NON-NLS-1$
            }
        } catch (FieldNotFound fnfEx) {
            MarketceteraFIXException mfix = MarketceteraFIXException.createFieldNotFoundException(fnfEx);
            returnExecReport = createRejectionMessage(mfix, message);
        } catch(SessionNotFound snf) {
            CoreException ex = new CoreException(snf, new I18NBoundMessage1P(org.marketcetera.core.Messages.ERROR_FIX_SESSION_NOT_FOUND, ObjectUtils.toString(defaultSessionID,null)));
            returnExecReport = createRejectionMessage(ex, message);
        } catch(UnsupportedMessageType umt) {
            try {
                String msgType = message.getHeader().getString(MsgType.FIELD);
                returnExecReport = createBusinessMessageReject(msgType,
                        Messages.ERROR_UNSUPPORTED_ORDER_TYPE.getText(
                        CurrentFIXDataDictionary.getCurrentFIXDataDictionary().getHumanFieldValue(MsgType.FIELD, msgType)));
            } catch (FieldNotFound fieldNotFound) {
                returnExecReport = createBusinessMessageReject("UNKNOWN", Messages.ERROR_UNSUPPORTED_ORDER_TYPE.getText("UNKNOWN")); //$NON-NLS-1$ //$NON-NLS-2$
            }
        } catch (CoreException e) {
        	returnExecReport = createRejectionMessage(e, message);
        } catch(Exception ex) {
        	returnExecReport = createRejectionMessage(ex, message);
        }
        return returnExecReport;
	}


    /** Sends the message to the destination
     * @param message Message to send
     * @throws SessionNotFound
     */
    private void sendMessage(Message message) throws SessionNotFound {
        if (defaultSessionID != null) {
            quickFIXSender.sendToTarget(message, defaultSessionID);
        } else {
            quickFIXSender.sendToTarget(message);
        }
    }

    /** Creates a rejection message
     * If we are using a FIX4.2 or higher, we return a BusinessMessageReject,
     * otherwise it's a session-level reject.
     * @param msgType   {@link MsgType} of the offending message
     * @param rejReason Text string explaining the reason for rejection.
     * @return  rejection message
     */
    protected Message createBusinessMessageReject(String msgType, String rejReason) {
        Message reject;
        if(FIXVersion.FIX40.toString().equals(msgFactory.getBeginString()) ||
           FIXVersion.FIX41.toString().equals(msgFactory.getBeginString())) {
            reject = msgFactory.createMessage(MsgType.REJECT);
            reject.setField(new Text(rejReason));
        } else {
            reject = msgFactory.newBusinessMessageReject(msgType,
                    BusinessRejectReason.UNSUPPORTED_MESSAGE_TYPE, rejReason);
        }
        return reject;
    }


    /** Creates a rejection message based on the message that causes the rejection
     * Currently, if it's an orderCancel then we send back an OrderCancelReject,
     * otherwise we always send back the ExecutionReport.
     * @param existingOrder
     * @return Corresponding rejection Message
     */
    protected Message createRejectionMessage(Exception causeEx, Message existingOrder)
    {
        Message rejection = null;
        if(FIXMessageUtil.isCancelReplaceRequest(existingOrder) ||
           FIXMessageUtil.isCancelRequest(existingOrder) )
        {
            rejection = msgFactory.newOrderCancelReject();
        } else {
            rejection = msgFactory.createMessage(MsgType.EXECUTION_REPORT);
            rejection.setField(getNextExecId());
            rejection.setField(new AvgPx(0));
            rejection.setField(new CumQty(0));
            rejection.setField(new LastShares(0));
            rejection.setField(new LastPx(0));
            rejection.setField(new ExecTransType(ExecTransType.STATUS));
        }

        rejection.setField(new OrdStatus(OrdStatus.REJECTED));
        FIXMessageUtil.fillFieldsFromExistingMessage(rejection,  existingOrder);
        
        
        String msg = (causeEx.getLocalizedMessage() == null) ? causeEx.toString() : causeEx.getLocalizedMessage();
        Messages.ERROR_MESSAGE_EXCEPTION.error(this, msg, existingOrder);
        SLF4JLoggerProxy.debug(this, causeEx, "Reason for above rejection: {}", msg); //$NON-NLS-1$
        rejection.setString(Text.FIELD, msg);
        // manually set the ClOrdID since it's not required in the dictionary but is for electronic orders
        try {
            rejection.setField(new ClOrdID(existingOrder.getString(ClOrdID.FIELD)));
        } catch(FieldNotFound ignored) {
            // don't set it if it's not there
        }
        try {
            msgFactory.getMsgAugmentor().executionReportAugment(rejection);
        } catch (FieldNotFound fieldNotFound) {
            MarketceteraFIXException mfix = MarketceteraFIXException.createFieldNotFoundException(fieldNotFound);
            SLF4JLoggerProxy.debug(this, mfix, "Could not find field"); //$NON-NLS-1$
            // ignore the exception since we are already sending a reject
        }
        rejection.getHeader().setField(new SendingTime(new Date())); //non-i18n
        return rejection;
    }

    protected Message executionReportFromNewOrder(Message newOrder) throws FieldNotFound {
        if (FIXMessageUtil.isOrderSingle(newOrder)){
            String clOrdId = newOrder.getString(ClOrdID.FIELD);
            char side = newOrder.getChar(Side.FIELD);
            String symbol = newOrder.getString(Symbol.FIELD);
            BigDecimal orderQty = new BigDecimal(newOrder.getString(OrderQty.FIELD)); //non-i18n
            BigDecimal orderPrice = null;
            try {
                String strPrice = newOrder.getString(Price.FIELD);
                orderPrice =  new BigDecimal(strPrice); //non-i18n
            } catch(FieldNotFound ex) {
                // leave as null
            }

            String inAccount = null;
            try {
                inAccount = newOrder.getString(Account.FIELD);
            } catch (FieldNotFound ex) {
                // only set the Account field if it's there
            }

            Message execReport = msgFactory.newExecutionReport(
                        null,
                        clOrdId,
                        getNextExecId().getValue(),
                        OrdStatus.PENDING_NEW,
                        side,
                        orderQty,
                        orderPrice,
                        BigDecimal.ZERO,
                        BigDecimal.ZERO,
                        BigDecimal.ZERO,
                        BigDecimal.ZERO,
                        new MSymbol(symbol),
                        inAccount);
            execReport.getHeader().setField(new SendingTime(new Date())); //non-i18n
            FIXMessageUtil.fillFieldsFromExistingMessage(execReport, newOrder, false);
            return execReport;
        } else {
            return null;
        }
    }

    /** Sets the default session  */
    public void setDefaultSessionID(SessionID inSessionID)
    {
        defaultSessionID = inSessionID;
    }

    public void setIdFactory(IDFactory idFactory) {
        this.idFactory = idFactory;
    }

    public SessionID getDefaultSessionID() {
        return defaultSessionID;
    }

	public IQuickFIXSender getQuickFIXSender() {
		return quickFIXSender;
	}

	public void setQuickFIXSender(IQuickFIXSender quickFIXSender) {
		this.quickFIXSender = quickFIXSender;
	}

    public void setIncomingCommandsCopier(JmsOperations incomingCommandsCopier) {
        this.incomingCommandsCopier = incomingCommandsCopier;
    }

    /** Returns the next ExecID from the factory, or a hardcoded ZZ-internal if we have
     * problems creating an execID
     * @return
     */
    private ExecID getNextExecId() {
        try {
            return new ExecID(idFactory.getNext());
        } catch(NoMoreIDsException ex) {
            Messages.ERROR_GENERATING_EXEC_ID.error(this, ex.getMessage());
            return new ExecID("ZZ-INTERNAL"); //$NON-NLS-1$
        }
    }

}
