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
import org.marketcetera.trade.FIXConverter;
import org.marketcetera.trade.FIXOrder;
import org.marketcetera.trade.Factory;
import org.marketcetera.trade.MessageCreationException;
import org.marketcetera.trade.Order;
import org.marketcetera.trade.OrderBase;
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
import quickfix.field.OrderID;
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
    private final ReplyPersister mPersister;
    private final IQuickFIXSender mSender;
    private final IDFactory mIDFactory;


    // CONSTRUCTORS.

    public RequestHandler
        (Destinations destinations,
         Selector selector,
         OrderFilter allowedOrders,
         ReplyPersister persister,
         IQuickFIXSender sender,
         IDFactory idFactory)
    {
        mDestinations=destinations;
        mSelector=selector;
        mAllowedOrders=allowedOrders;
        mPersister=persister;
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

    public ReplyPersister getPersister()
    {
        return mPersister;
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
        TradeMessage reply=null;
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
                qMsg=FIXConverter.toQMessage
                    (d.getFIXMessageFactory(),d.getFIXVersion(),om);
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
            Message report;
            try {
                report=createExecutionReport(qMsg);
            } catch (FieldNotFound ex) {
                throw new I18NException
                    (ex,new I18NBoundMessage1P
                     (Messages.RH_REPORT_FAILED_SENT,qMsg));
            }
            if (report!=null) {
                reply=Factory.getInstance().createExecutionReport
                    (report,dID,Originator.Server);
            }
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
                    report=createRejectionMessage(ex,msg,qMsg);
                }
            } catch (CoreException ex2) {
                Messages.RH_REPORT_FAILED.error(this,ex,msg);
                return null;
            }
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
        }
        if (reply!=null) {
            getPersister().persistReply(reply);
        }
        return reply;
	}


    // LEGACY CODE.

    /** Creates a rejection message based on the message that causes the rejection
     * Currently, if it's an orderCancel then we send back an OrderCancelReject,
     * otherwise we always send back the ExecutionReport.
     * @param existingOrder
     * @return Corresponding rejection Message
     */
    protected Message createRejectionMessage
        (Exception causeEx,
         TradeMessage order,
         Message existingOrder)
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
        if (existingOrder!=null) {
            FIXMessageUtil.fillFieldsFromExistingMessage(rejection,  existingOrder);
        }
        
        
        String msg = (causeEx.getLocalizedMessage() == null) ? causeEx.toString() : causeEx.getLocalizedMessage();
        Messages.ERROR_MESSAGE_EXCEPTION.error(this, msg, existingOrder);
        SLF4JLoggerProxy.debug(this, causeEx, "Reason for above rejection: {}", msg); //$NON-NLS-1$
        rejection.setString(Text.FIELD, msg);
        // manually set the ClOrdID since it's not required in the dictionary but is for electronic orders
        if (order instanceof OrderBase) {
            rejection.setField(new ClOrdID(((OrderBase)order).getOrderID().
                                           getValue()));
        } else if (order instanceof FIXOrder) {
            try {
                rejection.setField
                    (new ClOrdID(((FIXOrder)order).getMessage().
                                 getString(ClOrdID.FIELD)));
            } catch(FieldNotFound ignored) {
                // don't set it if it's not there
            }
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

    protected Message createExecutionReport
        (Message msg)
        throws CoreException,
               FieldNotFound
    {
        Message report;
        if (FIXMessageUtil.isOrderSingle(msg)) {
            report=getMsgFactory().newExecutionReport
                (null,
                 getOptFieldStr(msg,ClOrdID.FIELD),
                 getNextExecId().getValue(),
                 OrdStatus.PENDING_NEW,
                 getOptFieldChar(msg,Side.FIELD),
                 getOptFieldNum(msg,OrderQty.FIELD),
                 getOptFieldNum(msg,Price.FIELD),
                 BigDecimal.ZERO,
                 BigDecimal.ZERO,
                 BigDecimal.ZERO,
                 BigDecimal.ZERO,
                 getOptFieldSymbol(msg,Symbol.FIELD),
                 getOptFieldStr(msg,Account.FIELD));
        } else if (FIXMessageUtil.isCancelReplaceRequest(msg)) {
            report=getMsgFactory().newExecutionReport
                (getOptFieldStr(msg,OrderID.FIELD),
                 getOptFieldStr(msg,ClOrdID.FIELD),
                 getNextExecId().getValue(),
                 OrdStatus.PENDING_REPLACE,
                 getOptFieldChar(msg,Side.FIELD),
                 getOptFieldNum(msg,OrderQty.FIELD),
                 getOptFieldNum(msg,Price.FIELD),
                 BigDecimal.ZERO,
                 BigDecimal.ZERO,
                 BigDecimal.ZERO,
                 BigDecimal.ZERO,
                 getOptFieldSymbol(msg,Symbol.FIELD),
                 getOptFieldStr(msg,Account.FIELD));
        } else {
            return null;
        }
        report.getHeader().setField(new SendingTime(new Date())); //non-i18n
        FIXMessageUtil.fillFieldsFromExistingMessage(report,msg,false);
        return report;
    }

    private static String getOptFieldStr
        (Message msg,
         int field)
    {
        try {
            return msg.getString(field);
        } catch(FieldNotFound ex) {
            return null;
        }
    }

    private static char getOptFieldChar
        (Message msg,
         int field)
    {
        try {
            return msg.getChar(field);
        } catch(FieldNotFound ex) {
            return '\0';
        }
    }

    private static BigDecimal getOptFieldNum
        (Message msg,
         int field)
    {
        String str=getOptFieldStr(msg,field);
        if (str==null) {
            return null;
        }
        return new BigDecimal(str);
    }

    private static MSymbol getOptFieldSymbol
        (Message msg,
         int field)
    {
        String str=getOptFieldStr(msg,field);
        if (str==null) {
            return null;
        }
        return new MSymbol(str);
    }
}
