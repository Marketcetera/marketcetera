package org.marketcetera.oms;

import org.marketcetera.core.*;
import org.marketcetera.quickfix.*;
import quickfix.*;
import quickfix.field.*;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;

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
@ClassVersion("$Id$")
public class OutgoingMessageHandler {

	private List<MessageModifier> messageModifiers;
    private MessageRouteManager routeMgr;
    private SessionID defaultSessionID;         // used to store the SessionID so that FIX sender can find it
    private IQuickFIXSender quickFIXSender = new QuickFIXSender();
    private IDFactory idFactory;
    private FIXMessageFactory msgFactory;
    private OrderLimits orderLimits;

    // this is temporary, until we have much better JMX visibility
    protected QuickFIXApplication qfApp;
    
    public OutgoingMessageHandler(SessionSettings settings, FIXMessageFactory inFactory, OrderLimits inLimits,
                                  QuickFIXApplication inQFApp)
            throws ConfigError, FieldConvertError, MarketceteraException {
        setMessageModifiers(new LinkedList<MessageModifier>());
        setOrderRouteManager(new MessageRouteManager());
        msgFactory = inFactory;
        idFactory = createDatabaseIDFactory(settings);
        orderLimits = inLimits;
        qfApp = inQFApp;
        try {
            idFactory.init();
        } catch (Exception ex) {
            if(LoggerAdapter.isDebugEnabled(this)) { LoggerAdapter.debug("Error initializing the ID factory", ex, this); }
            // ignore the exception - should get the in-memory id factory instead
        }
    }

    public void setOrderRouteManager(MessageRouteManager inMgr)
    {
        routeMgr = inMgr;
    }

    public void setMessageModifiers(List<MessageModifier> mods){
		messageModifiers = new LinkedList<MessageModifier>();
		for (MessageModifier mod : mods) {
			messageModifiers.add(mod);
		}
		messageModifiers.add(new TransactionTimeInsertMessageModifier());
	}

    /** Only supports NewOrderSingle, OrderCancelReplace and OrderCancel orders at this point
     * Rejects orders that are of the wrong FIX version, or if the OMS is not logged on to a FIX destination.
     * Runs the incoming orders through message modifiers, and forwards them on to a FIX destination.
     * @param message Incoming message
     * @return ExecutionReport for this message
     * @throws MarketceteraException
     */
    @SuppressWarnings({"ThrowableInstanceNeverThrown"})
    public Message handleMessage(Message message) throws MarketceteraException {
        if(message == null) {
            LoggerAdapter.error(OMSMessageKey.ERROR_INCOMING_MSG_NULL.getLocalizedMessage(), this);
            return null;
        }

        if(!qfApp.isLoggedOn()) {
            return createRejectionMessage(new MarketceteraException(OMSMessageKey.ERROR_NO_DESTINATION_CONNECTION.getLocalizedMessage()),
                    message);
        }


        try {
            String version = message.getHeader().getField(new BeginString()).getValue();
            if(!msgFactory.getBeginString().equals(version)) {
                return createRejectionMessage(new MarketceteraException(OMSMessageKey.ERROR_MISMATCHED_FIX_VERSION.getLocalizedMessage(
                                                    msgFactory.getBeginString(), version)), message);
            }
        } catch (FieldNotFound fieldNotFound) {
            return createRejectionMessage(new MarketceteraException(OMSMessageKey.ERROR_MALFORMED_MESSAGE_NO_FIX_VERSION.getLocalizedMessage()),
                    message);
        }

        Message returnExecReport = null;
        try {
            if(!(FIXMessageUtil.isOrderSingle(message) || FIXMessageUtil.isCancelRequest(message)
                    || FIXMessageUtil.isCancelReplaceRequest(message))) {
                throw new UnsupportedMessageType();
            }

            modifyOrder(message);
            orderLimits.verifyOrderLimits(message);
            routeMgr.modifyMessage(message, msgFactory.getMsgAugmentor());
            // if single, pre-create an executionReport and send it back
            if (FIXMessageUtil.isOrderSingle(message))
            {
                returnExecReport = executionReportFromNewOrder(message);
            }
            sendMessage(message);
            if(returnExecReport != null && LoggerAdapter.isDebugEnabled(this)) {
                LoggerAdapter.debug("Sending immediate execReport:  "+returnExecReport, this);
            }
        } catch (FieldNotFound fnfEx) {
            MarketceteraFIXException mfix = MarketceteraFIXException.createFieldNotFoundException(fnfEx);
            returnExecReport = createRejectionMessage(mfix, message);
        } catch(SessionNotFound snf) {
            MarketceteraException ex = new MarketceteraException(MessageKey.SESSION_NOT_FOUND.getLocalizedMessage(defaultSessionID), snf);
            returnExecReport = createRejectionMessage(ex, message);
        } catch(UnsupportedMessageType umt) {
            try {
                String msgType = message.getHeader().getString(MsgType.FIELD);
                returnExecReport = createBusinessMessageReject(msgType,
                        OMSMessageKey.ERROR_UNSUPPORTED_ORDER_TYPE.getLocalizedMessage(
                        FIXDataDictionaryManager.getCurrentFIXDataDictionary().getHumanFieldValue(MsgType.FIELD, msgType)));
            } catch (FieldNotFound fieldNotFound) {
                returnExecReport = createBusinessMessageReject("UNKNOWN", OMSMessageKey.ERROR_UNSUPPORTED_ORDER_TYPE.getLocalizedMessage("UNKNOWN"));
            }
        } catch (MarketceteraException e) {
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
        
        
        String msg = (causeEx.getMessage() == null) ? causeEx.toString() : causeEx.getMessage();
        LoggerAdapter.error(OMSMessageKey.MESSAGE_EXCEPTION.getLocalizedMessage(msg, existingOrder), this);
        if(LoggerAdapter.isDebugEnabled(this)) { LoggerAdapter.debug("Reason for above rejection: "+msg, causeEx, this); }
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
            if(LoggerAdapter.isDebugEnabled(this)) { LoggerAdapter.debug(mfix.getLocalizedMessage(), fieldNotFound, this); }
            // ignore the exception since we are already sending a reject
        }
        rejection.getHeader().setField(new SendingTime());
        return rejection;
    }

    protected Message executionReportFromNewOrder(Message newOrder) throws FieldNotFound {
        if (FIXMessageUtil.isOrderSingle(newOrder)){
            String clOrdId = newOrder.getString(ClOrdID.FIELD);
            char side = newOrder.getChar(Side.FIELD);
            String symbol = newOrder.getString(Symbol.FIELD);
            BigDecimal orderQty = new BigDecimal(newOrder.getString(OrderQty.FIELD));
            BigDecimal orderPrice = null;
            try {
                String strPrice = newOrder.getString(Price.FIELD);
                orderPrice =  new BigDecimal(strPrice);
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
                        OrdStatus.NEW,
                        side,
                        orderQty,
                        orderPrice,
                        BigDecimal.ZERO,
                        BigDecimal.ZERO,
                        BigDecimal.ZERO,
                        BigDecimal.ZERO,
                        new MSymbol(symbol),
                        inAccount);
            execReport.getHeader().setField(new SendingTime());
            FIXMessageUtil.fillFieldsFromExistingMessage(execReport, newOrder, false);
            return execReport;
        } else {
            return null;
        }
    }

    /** Apply all the order modifiers to this message */
    protected void modifyOrder(Message inOrder) throws MarketceteraException
    {
        for (MessageModifier oneModifier : messageModifiers) {
            oneModifier.modifyMessage(inOrder, msgFactory.getMsgAugmentor());
        }
    }

    /** Sets the default session  */
    public void setDefaultSessionID(SessionID inSessionID)
    {
        defaultSessionID = inSessionID;
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

    protected IDFactory createDatabaseIDFactory(SessionSettings settings) throws ConfigError, FieldConvertError {
        return new DatabaseIDFactory(settings.getString(JdbcSetting.SETTING_JDBC_CONNECTION_URL),
                settings.getString(JdbcSetting.SETTING_JDBC_DRIVER), settings.getString(JdbcSetting.SETTING_JDBC_USER),
                settings.getString(JdbcSetting.SETTING_JDBC_PASSWORD), DatabaseIDFactory.TABLE_NAME, DatabaseIDFactory.COL_NAME,
                DatabaseIDFactory.NUM_IDS_GRABBED);
    }


    /** Returns the next ExecID from the factory, or a hardcoded ZZ-internal if we have
     * problems creating an execID
     * @return
     */
    private ExecID getNextExecId() {
        try {
            return new ExecID(idFactory.getNext());
        } catch(NoMoreIDsException ex) {
            LoggerAdapter.error(OMSMessageKey.ERROR_GENERATING_EXEC_ID.getLocalizedMessage(ex.getMessage()), this);
            return new ExecID("ZZ-INTERNAL");
        }
    }

}
