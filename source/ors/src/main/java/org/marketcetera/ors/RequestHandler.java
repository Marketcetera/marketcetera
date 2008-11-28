package org.marketcetera.ors;

import java.math.BigDecimal;
import java.util.Date;
import org.marketcetera.core.CoreException;
import org.marketcetera.core.IDFactory;
import org.marketcetera.core.MSymbol;
import org.marketcetera.ors.dest.Destination;
import org.marketcetera.ors.dest.Destinations;
import org.marketcetera.ors.dest.Selector;
import org.marketcetera.ors.filters.OrderFilter;
import org.marketcetera.ors.jms.ReplyHandler;
import org.marketcetera.quickfix.FIXMessageFactory;
import org.marketcetera.quickfix.FIXMessageUtil;
import org.marketcetera.quickfix.FIXVersion;
import org.marketcetera.quickfix.IQuickFIXSender;
import org.marketcetera.quickfix.MarketceteraFIXException;
import org.marketcetera.trade.DestinationID;
import org.marketcetera.trade.FIXOrder;
import org.marketcetera.trade.FIXConverter;
import org.marketcetera.trade.Factory;
import org.marketcetera.trade.MessageCreationException;
import org.marketcetera.trade.Order;
import org.marketcetera.trade.OrderCancel;
import org.marketcetera.trade.OrderReplace;
import org.marketcetera.trade.OrderSingle;
import org.marketcetera.trade.Originator;
import org.marketcetera.trade.TradeMessage;
import org.marketcetera.util.except.I18NException;
import org.marketcetera.util.log.I18NBoundMessage1P;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.misc.ClassVersion;
import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.SessionNotFound;
import quickfix.field.Account;
import quickfix.field.AvgPx;
import quickfix.field.BusinessRejectReason;
import quickfix.field.ClOrdID;
import quickfix.field.CumQty;
import quickfix.field.ExecID;
import quickfix.field.ExecTransType;
import quickfix.field.LastPx;
import quickfix.field.LastShares;
import quickfix.field.MsgType;
import quickfix.field.OrdStatus;
import quickfix.field.OrderQty;
import quickfix.field.Price;
import quickfix.field.SendingTime;
import quickfix.field.Side;
import quickfix.field.Symbol;
import quickfix.field.Text;

/**
 * A handler for incoming trade requests (orders).
 *
 * @author tlerios@marketcetera.com
 * @since $Release$
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$") //$NON-NLS-1$
public class RequestHandler 
    implements ReplyHandler<TradeMessage>
{

    // INSTANCE DATA.

    private final Destinations mDestinations;
    private final Selector mSelector;
    private final OrderFilter mAllowedOrders;
    private final IQuickFIXSender mSender;
    private final IDFactory mIDFactory;


    // CONSTRUCTORS.

    public RequestHandler
        (Destinations destinations,
         Selector selector,
         OrderFilter allowedOrders,
         IQuickFIXSender sender,
         IDFactory idFactory)
    {
        mDestinations=destinations;
        mSelector=selector;
        mAllowedOrders=allowedOrders;
        mSender=sender;
        mIDFactory=idFactory;
    }


    // INSTANCE METHODS.

    public Destinations getDestinations()
    {
        return mDestinations;
    }

    public Selector getSelector()
    {
        return mSelector;
    }

    public OrderFilter getAllowedOrders()
    {
        return mAllowedOrders;
    }

    public IQuickFIXSender getSender()
    {
        return mSender;
    }

    public IDFactory getIDFactory()
    {
        return mIDFactory;
    }

    public FIXMessageFactory getMsgFactory()
    {
        return FIXVersion.FIX_SYSTEM.getMessageFactory();
    }

    private ExecID getNextExecId()
        throws CoreException
    {
        return new ExecID(getIDFactory().getNext());
    }


    // ReplyHandler.

    @Override
    public TradeMessage replyToMessage
        (TradeMessage msg)
    {
        DestinationID dID=null;
        Message qMsg=null;
        try {

            // Reject null messages.

            if (msg==null) {
                throw new I18NException(Messages.RH_NULL_MESSAGE);
            }

            // Reject messages of unsupported types.

            if (!(msg instanceof OrderSingle) &&
                !(msg instanceof OrderCancel) &&                
                !(msg instanceof OrderReplace) &&                
                !(msg instanceof FIXOrder)) {
                throw new I18NException(Messages.RH_UNSUPPORTED_MESSAGE);
            }
            Order om=(Order)msg;

            // Identify destination.

            dID=om.getDestinationID();
            if (dID==null) {
                dID=getSelector().chooseDestination(om);
            }
            if (dID==null) {
                throw new I18NException(Messages.RH_UNKNOWN_DESTINATION);
            }

            // Ensure destination is available.

            Destination d=getDestinations().getDestination(dID);
            if (!d.getLoggedOn()) {
                throw new I18NException
                    (new I18NBoundMessage1P
                     (Messages.RH_UNAVAILABLE_DESTINATION,d.toString()));  
            }

            // Convert to a FIX message.

            try {
                qMsg=FIXConverter.toQMessage(d.getFIXMessageFactory(),om);
            } catch (I18NException ex) {
                throw new I18NException(ex,Messages.RH_CONVERSION_FAILED);
            }

            // Ensure the order is allowed.

            try {
                getAllowedOrders().assertAccepted(qMsg);
            } catch (CoreException ex) {
                throw new I18NException(ex,Messages.RH_ORDER_DISALLOWED);
            }

            // Apply message modifiers.

            if (d.getModifiers()!=null) {
                try {
                    d.getModifiers().modifyMessage(qMsg);
                } catch (CoreException ex) {
                    throw new I18NException
                        (ex,new I18NBoundMessage1P
                         (Messages.RH_MODIFICATION_FAILED,d.toString()));  
                }
            }

            // Apply order routing.

            if (d.getRoutes()!=null) {
                try {
                    d.getRoutes().modifyMessage
                        (qMsg,d.getFIXMessageAugmentor());
                } catch (CoreException ex) {
                    throw new I18NException
                        (ex,new I18NBoundMessage1P
                         (Messages.RH_ROUTING_FAILED,d.toString()));  
                }
            }

            // Send message to QuickFIX/J.

            try {
                getSender().sendToTarget(qMsg,d.getSessionID());
            } catch (SessionNotFound ex) {
                throw new I18NException
                    (ex,new I18NBoundMessage1P
                     (Messages.RH_UNAVAILABLE_DESTINATION,d.toString()));  
            }
            if (msg instanceof OrderSingle) {
                try {
                    return Factory.getInstance().createExecutionReport
                        (executionReportFromNewOrder(qMsg),dID,
                         Originator.Server);
                } catch (FieldNotFound ex) {
                    throw new I18NException
                    (ex,new I18NBoundMessage1P
                     (Messages.RH_REPORT_FAILED_SENT,qMsg));
                }
            }
            return null;
        } catch (I18NException ex) {
            Messages.RH_MESSAGE_REJECTED.error(this,ex,msg);
            Message report;
            try {
                if (ex.getI18NBoundMessage()==Messages.RH_UNSUPPORTED_MESSAGE) {
                    report=getMsgFactory().newBusinessMessageReject
                        (msg.getClass().getName(),
                         BusinessRejectReason.UNSUPPORTED_MESSAGE_TYPE,
                         ex.getLocalizedDetail());
                } else {
                    report=createRejectionMessage(ex,qMsg);
                }
            } catch (CoreException ex2) {
                Messages.RH_REPORT_FAILED.error(this,ex,msg);
                return null;
            }
            TradeMessage reply=null;
            try {
                reply=FIXConverter.fromQMessage
                    (report,Originator.Server,dID);
            } catch (MessageCreationException ex2) {
                Messages.RH_REPORT_FAILED.error(this,ex2,msg);
                return null;
            }
            if (reply==null) {
                Messages.RH_REPORT_TYPE_UNSUPPORTED.info(this,msg);
            }
            return reply;
        }
	}


    // LEGACY CODE.

    /** Creates a rejection message based on the message that causes the rejection
     * Currently, if it's an orderCancel then we send back an OrderCancelReject,
     * otherwise we always send back the ExecutionReport.
     * @param existingOrder
     * @return Corresponding rejection Message
     */
    protected Message createRejectionMessage(Exception causeEx, Message existingOrder)
        throws CoreException
    {
        Message rejection = null;
        if(FIXMessageUtil.isCancelReplaceRequest(existingOrder) ||
           FIXMessageUtil.isCancelRequest(existingOrder) )
        {
            rejection = getMsgFactory().newOrderCancelReject();
        } else {
            rejection = getMsgFactory().createMessage(MsgType.EXECUTION_REPORT);
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
            getMsgFactory().getMsgAugmentor().executionReportAugment(rejection);
        } catch (FieldNotFound fieldNotFound) {
            MarketceteraFIXException mfix = MarketceteraFIXException.createFieldNotFoundException(fieldNotFound);
            SLF4JLoggerProxy.debug(this, mfix, "Could not find field"); //$NON-NLS-1$
            // ignore the exception since we are already sending a reject
        }
        rejection.getHeader().setField(new SendingTime(new Date())); //non-i18n
        return rejection;
    }


    protected Message executionReportFromNewOrder(Message newOrder)
        throws CoreException, FieldNotFound {
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

            Message execReport = getMsgFactory().newExecutionReport(
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
}
