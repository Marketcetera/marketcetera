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

	private List<OrderModifier> orderModifiers;
    private OrderRouteManager routeMgr;
    private SessionID defaultSessionID;         // used to store the SessionID so that FIX sender can find it
    private IQuickFIXSender quickFIXSender = new QuickFIXSender();
    private IDFactory idFactory;
    private FIXMessageFactory msgFactory;
    
    public OutgoingMessageHandler(SessionSettings settings, FIXMessageFactory inFactory)
            throws ConfigError, FieldConvertError, MarketceteraException {
        setOrderModifiers(new LinkedList<OrderModifier>());
        setOrderRouteManager(new OrderRouteManager());
        msgFactory = inFactory;
        idFactory = createDatabaseIDFactory(settings);
        try {
            idFactory.init();
        } catch (Exception ex) {
            if(LoggerAdapter.isDebugEnabled(this)) { LoggerAdapter.debug("Error initializing the ID factory", ex, this); }
            // ignore the exception - should get the in-memory id factory instead
        }
    }

    public void setOrderRouteManager(OrderRouteManager inMgr)
    {
        routeMgr = inMgr;
    }

    public void setOrderModifiers(List<OrderModifier> mods){
		orderModifiers = new LinkedList<OrderModifier>();
		for (OrderModifier mod : mods) {
			orderModifiers.add(mod);
		}
		orderModifiers.add(new TransactionTimeInsertOrderModifier());
	}
	
	public Message handleMessage(Message message) throws MarketceteraException {
        if(message == null) {
            LoggerAdapter.error(OMSMessageKey.ERROR_INCOMING_MSG_NULL.getLocalizedMessage(), this);
            return null;
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

        Message returnVal = null;
        try {
            modifyOrder(message);
            // if single, pre-create an executionReport and send it back
            if (FIXMessageUtil.isOrderSingle(message))
            {
                Message outReport = executionReportFromNewOrder(message);
                if(LoggerAdapter.isDebugEnabled(this)) {
                    LoggerAdapter.debug("Sending immediate execReport:  "+outReport, this);
                }
				returnVal = outReport;
            }
            routeMgr.modifyOrder(message, msgFactory.getMsgAugmentor());
            if (defaultSessionID != null)
            	quickFIXSender.sendToTarget(message, defaultSessionID);
            else 
            	quickFIXSender.sendToTarget(message);
            	
        } catch (FieldNotFound fnfEx) {
            MarketceteraFIXException mfix = MarketceteraFIXException.createFieldNotFoundException(fnfEx);
            returnVal = createRejectionMessage(mfix, message);
        } catch(SessionNotFound snf) {
            MarketceteraException ex = new MarketceteraException(MessageKey.SESSION_NOT_FOUND.getLocalizedMessage(defaultSessionID), snf);
            returnVal = createRejectionMessage(ex, message);
        } catch (MarketceteraException e) {
        	returnVal = createRejectionMessage(e, message);
        } catch(Exception ex) {
        	returnVal = createRejectionMessage(ex, message);
        }
        return returnVal;
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
        LoggerAdapter.error(OMSMessageKey.MESSAGE_EXCEPTION.getLocalizedMessage(msg, existingOrder), causeEx, this);
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
        return rejection;
    }

    public Message executionReportFromNewOrder(Message newOrder) throws FieldNotFound {
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

            return msgFactory.newExecutionReport(
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
        } else {
            return null;
        }
    }

    /** Apply all the order modifiers to this message */
    protected void modifyOrder(Message inOrder) throws MarketceteraException
    {
        for (OrderModifier oneModifier : orderModifiers) {
            oneModifier.modifyOrder(inOrder, msgFactory.getMsgAugmentor());
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
