package org.marketcetera.oms;

import org.marketcetera.core.*;
import org.marketcetera.quickfix.*;
import org.marketcetera.jcyclone.*;
import org.jcyclone.core.handler.EventHandlerException;
import org.jcyclone.core.queue.IElement;
import org.jcyclone.core.queue.SinkException;
import org.jcyclone.core.cfg.IConfigData;
import quickfix.Message;
import quickfix.FieldNotFound;
import quickfix.fix42.ExecutionReport;
import quickfix.fix42.OrderCancelReject;
import quickfix.field.*;
import quickfix.field.Symbol;

import java.util.*;
import java.util.prefs.BackingStoreException;
import java.math.BigDecimal;

/**
 * OrderManager is now a "middle" stagein JCyclone setup
 * It takes input from both QuickfixAdapter and JMS adapter, and
 * subsequently routes the resultto the Output stage
 * @author gmiller
 */
@ClassVersion("$Id$")
public class OrderManager extends JCycloneStageBase {
    HashSet mSessions;

    protected DefaultOrderModifier defaultFieldModifier;
    private OrderRouteManager routeMgr;
    private ArrayList<OrderModifier> orderModifiers = new ArrayList<OrderModifier>(2);

    public static final String ORDER_MANAGER_NAME = "OrderManager";
    public static final String ORDER_MANAGER_FIX_CONDUIT_NAME = "OrderManagerFIXConduit";
    protected static final String FIX_HEADER_PREFIX = "fix.header.";
    protected static final String FIX_TRAILER_PREFIX = "fix.trailer.";
    protected static final String FIX_FIELDS_PREFIX = "fix.fields.";

    /** JCyclone constructor: Creates a new instance of OrderManager */
    public OrderManager()
    {
        defaultFieldModifier = new DefaultOrderModifier();
        orderModifiers.add(defaultFieldModifier);
        orderModifiers.add(new TransactionTimeInsertOrderModifier());
        routeMgr = new OrderRouteManager();
    }

    public void init(IConfigData config) throws Exception {
        super.init(config);
        postInitialize(OrderManagementSystem.getOMS().getInitProps());
    }

    protected void postInitialize(ConfigData props) throws BackingStoreException
    {
        routeMgr.init(props);
        readDefaultFields(props, defaultFieldModifier);
    }

    /** We can have the following types of events come in:
     * <ol>
     * <li>A new order event coming in from GUI/order loader. this is a JMS event,
     * and needs to be responded to with an immediate executionReport (on JMS queue) and
     * we send the FIX message through
     *
     * @param inEvent
     * @throws EventHandlerException
     */
    public void handleEvent(IElement inEvent) throws EventHandlerException {
        Object theEvent = ((StageElement)inEvent).getElement();

        if(theEvent instanceof quickfix.Message) {
            Message message = (Message) theEvent;
            try {
                modifyOrder(message);
                // if single, pre-create an executionReport and send it back
                if (FIXMessageUtil.isOrderSingle(message))
                {
                    Message outReport = executionReportFromNewOrder(message);
                    if(LoggerAdapter.isDebugEnabled(this)) {
                        LoggerAdapter.debug("Sending immediate execReport:  "+outReport, this);
                    }
                    getNextStage().enqueue(new JMSStageOutput(outReport,
                            OrderManagementSystem.getOMS().getJmsOutputInfo()));
                }
                if(inEvent instanceof FIXStageOutput) {
                    // apply the route modifier
                    routeMgr.modifyOrder(message);
                }
                getNextStage().enqueue(inEvent);
            } catch (FieldNotFound fnfEx) {
                MarketceteraFIXException mfix = MarketceteraFIXException.createFieldNotFoundException(fnfEx);
                sendRejectionMessage(mfix, message);
            } catch (MarketceteraException e) {
                sendRejectionMessage(e, message);
            } catch (SinkException ex) {
                LoggerAdapter.error(MessageKey.JCYCLONE_ERROR_SEND_NEXT_STAGE.getLocalizedMessage(), ex, this);
                throw new MarketceteraEventHandlerException(MessageKey.JCYCLONE_ERROR_SEND_NEXT_STAGE.getLocalizedMessage(), ex);
            } catch(Exception ex) {
                sendRejectionMessage(ex, message);
            }
        } else {
            LoggerAdapter.error(MessageKey.FIX_UNEXPECTED_MSGTYPE.getLocalizedMessage(theEvent), this);
            // todo: handle others
        }

    }

    /** Helper function to send a rejection message that is caused by @param origMessage.
     * We create a rejection message, try to extract most of the fields from original
     * and send it back to the origin.
     *
     * @param causeEx Exception that causes the rejection to be sent
     * @param origMessage Incoming message causing the exception
     */
    protected void sendRejectionMessage(Exception causeEx, Message origMessage)
            throws MarketceteraEventHandlerException
    {
        Message rejection = createRejectionMessage(origMessage);
        String msg = (causeEx.getMessage() == null) ? causeEx.toString() : causeEx.getMessage();
        LoggerAdapter.error(OMSMessageKey.MESSAGE_EXCEPTION.getLocalizedMessage(new Object[]{msg, origMessage}), causeEx, this);
        rejection.setString(Text.FIELD, msg);
        FIXMessageUtil.fillFieldsFromExistingMessage(rejection,  origMessage);
        try {
            // manually set the ClOrdID since it's not required in the dictionary but is for electronic orders
            try {
                rejection.setField(new ClOrdID(origMessage.getString(ClOrdID.FIELD)));
            } catch(FieldNotFound ignored) {
                // don't set it if it's not there
            }
            getNextStage().enqueue(new JMSStageOutput(rejection,
                    OrderManagementSystem.getOMS().getJmsOutputInfo()));
        } catch(SinkException ex) {
            LoggerAdapter.error(MessageKey.JMS_SEND_ERROR.getLocalizedMessage(), ex, this);
            throw new MarketceteraEventHandlerException(MessageKey.JMS_SEND_ERROR.getLocalizedMessage(), ex);
        }
    }

    /** Creates a rejection message based on the message that causes the rejection
     * Currently, if it's an orderCancel then we send back an OrderCancelReject,
     * otherwise we always send back the ExecutionReport.
     * @param existingOrder
     * @return Corresponding rejection Message
     */
    protected Message createRejectionMessage(Message existingOrder)
    {
        Message outMessage = null;
        if(FIXMessageUtil.isCancelReplaceRequest(existingOrder) ||
           FIXMessageUtil.isCancelRequest(existingOrder) )
        {
            outMessage = new OrderCancelReject();
        } else {
            outMessage = new ExecutionReport();
        }

        outMessage.setField(new OrdStatus(OrdStatus.REJECTED));
        outMessage.setField(new ExecType(ExecType.REJECTED));
        FIXMessageUtil.fillFieldsFromExistingMessage(outMessage,  existingOrder);
        return outMessage;
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

            AccountID inAccountID = null;
            try {
                inAccountID = new AccountID(newOrder.getString(Account.FIELD));
            } catch (FieldNotFound ex) {
                // only set the Account field if it's there
            }

            return FIXMessageUtil.newExecutionReport(
                    null,
                    new InternalID(clOrdId),
                    "ZZ-INTERNAL",
                    '\0',
                    ExecType.NEW,
                    OrdStatus.NEW,
                    side,
                    orderQty,
                    orderPrice,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    orderQty,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    new MSymbol(symbol),
                    inAccountID);
        } else {
            return null;
        }
    }

    public String getName() {
        return ORDER_MANAGER_NAME;
    }

    protected void readDefaultFields(ConfigData props, DefaultOrderModifier inOrderModifier)
            throws BackingStoreException
    {
        String[] propNames = props.keys();
        for(String oneName : propNames) {
            if(oneName.startsWith(FIX_FIELDS_PREFIX)) {
                readDefaultFieldsHelper(props, oneName, FIX_FIELDS_PREFIX, inOrderModifier,
                        DefaultOrderModifier.MessageFieldType.MESSAGE);
            } else if(oneName.startsWith(FIX_HEADER_PREFIX)) {
                readDefaultFieldsHelper(props, oneName, FIX_HEADER_PREFIX, inOrderModifier,
                        DefaultOrderModifier.MessageFieldType.HEADER);
            } else if(oneName.startsWith(FIX_TRAILER_PREFIX)) {
                readDefaultFieldsHelper(props, oneName, FIX_TRAILER_PREFIX, inOrderModifier,
                        DefaultOrderModifier.MessageFieldType.TRAILER);
            }
        }
    }

    /** The header fields are of form:
     * <prefix>.<fieldName>=<fieldValue>
     * Where fieldName is an integer number.
     * So we parse out the field name, store it as an int, and store the value as an object.
     * @param inProps
     * @param propName
     * @param propPrefix
     * @param inOrderModifier
     * @param fieldType Which particular kind of field we are modifying: trailer/header/message
     */
    protected void readDefaultFieldsHelper(ConfigData inProps, String propName, String propPrefix,
                                           DefaultOrderModifier inOrderModifier,
                                           DefaultOrderModifier.MessageFieldType fieldType)
    {
        String realFieldName = propName.substring(propPrefix.length());     // trailing . is included in prefix
        try {
            int fieldID = Integer.parseInt(realFieldName);
            inOrderModifier.addDefaultField(fieldID, inProps.get(propName, ""), fieldType);
        } catch (Exception ex) {
            LoggerAdapter.error(OMSMessageKey.ERROR_INIT_PROPNAME_IGNORE.getLocalizedMessage(propName), ex, this);
        }
    }

    /** Apply all the order modifiers to this message */
    protected void modifyOrder(Message inOrder) throws MarketceteraException
    {
        for (OrderModifier oneModifier : orderModifiers) {
            oneModifier.modifyOrder(inOrder);
        }
    }

}
