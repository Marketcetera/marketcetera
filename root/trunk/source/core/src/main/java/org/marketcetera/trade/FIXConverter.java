package org.marketcetera.trade;

import java.math.BigDecimal;
import java.util.Map;
import org.marketcetera.quickfix.FIXMessageFactory;
import org.marketcetera.quickfix.FIXMessageUtil;
import org.marketcetera.quickfix.FIXVersion;
import org.marketcetera.util.except.I18NException;
import org.marketcetera.util.log.I18NBoundMessage1P;
import org.marketcetera.util.misc.ClassVersion;
import quickfix.Message;
import quickfix.field.Account;
import quickfix.field.ClOrdID;
import quickfix.field.OrdType;
import quickfix.field.OrderQty;
import quickfix.field.OrigClOrdID;
import quickfix.field.Price;
import quickfix.field.Symbol;

/**
 * FIX conversion utilities.
 * 
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$") //$NON-NLS-1$
public final class FIXConverter
{

    // CLASS METHODS.

    /**
     * Adds the given symbol to the given QuickFIX/J message (of the
     * given FIX version).
     *
     * @param symbol The symbol. It may be null.
     * @param fixVersion The FIX version. 
     * @param msg The QuickFIX/J message. 
     * @param required True if the symbol is required but is not set.
     *
     * @throws I18NException Thrown if the symbol is required but is
     * not set.
     */

    private static void addSymbol
        (MSymbol symbol,
         FIXVersion fixVersion,
         Message msg,
         boolean required)
        throws I18NException
    {
        if ((symbol==null) ||
            (symbol.getFullSymbol()==null)) {
            if (required) {
                throw new I18NException
                    (new I18NBoundMessage1P(Messages.NO_SYMBOL,symbol));
            }
        } else{
            msg.setField(new Symbol(symbol.getFullSymbol()));
        }
    }

    /**
     * Adds the given price to the given QuickFIX/J message (of the
     * given FIX version).
     *
     * @param price The price. It may be null.
     * @param fixVersion The FIX version. 
     * @param msg The QuickFIX/J message. 
     * @param required True if the price is required but is not set.
     *
     * @throws I18NException Thrown if the price is required but is
     * not set.
     */

    private static void addPrice
        (BigDecimal price,
         FIXVersion fixVersion,
         Message msg,
         boolean required)
        throws I18NException
    {
        if (price==null) {
            if (required) {
                throw new I18NException(Messages.NO_PRICE);
            }
        } else{
            msg.setField(new Price(price));
        }
    }

    /**
     * Adds the given quantity to the given QuickFIX/J message (of the
     * given FIX version).
     *
     * @param quantity The quantity. It may be null.
     * @param fixVersion The FIX version. 
     * @param msg The QuickFIX/J message. 
     * @param required True if the quantity is required but is not set.
     *
     * @throws I18NException Thrown if the quantity is required but is
     * not set.
     */

    private static void addQuantity
        (BigDecimal quantity,
         FIXVersion fixVersion,
         Message msg,
         boolean required)
        throws I18NException
    {
        if (quantity==null) {
            if (required) {
                throw new I18NException(Messages.NO_QUANTITY);
            }
        } else{
            msg.setField(new OrderQty(quantity));
        }
    }

    /**
     * Adds the given account to the given QuickFIX/J message (of the
     * given FIX version).
     *
     * @param account The account. It may be null.
     * @param fixVersion The FIX version. 
     * @param msg The QuickFIX/J message. 
     * @param required True if the account is required but is not set.
     *
     * @throws I18NException Thrown if the account is required but is
     * not set.
     */

    private static void addAccount
        (String account,
         FIXVersion fixVersion,
         Message msg,
         boolean required)
        throws I18NException
    {
        if (account==null) {
            if (required) {
                throw new I18NException(Messages.NO_ACCOUNT);
            }
        } else{
            msg.setField(new Account(account));
        }
    }

    /**
     * Adds the given order ID to the given QuickFIX/J message (of the
     * given FIX version).
     *
     * @param orderID The order ID. It may be null.
     * @param fixVersion The FIX version. 
     * @param msg The QuickFIX/J message. 
     * @param required True if the order ID is required but is not
     * set.
     *
     * @throws I18NException Thrown if the order ID is required but is
     * not set.
     */

    private static void addOrderID
        (OrderID orderID,
         FIXVersion fixVersion,
         Message msg,
         boolean required)
        throws I18NException
    {
        if (orderID==null) {
            if (required) {
                throw new I18NException(Messages.NO_ORDER_ID);
            }
        } else{
            msg.setField(new ClOrdID(orderID.getValue()));
        }
    }

    /**
     * Adds the given original order ID to the given QuickFIX/J
     * message (of the given FIX version).
     *
     * @param originalOrderID The original order ID. It may be null.
     * @param fixVersion The FIX version. 
     * @param msg The QuickFIX/J message. 
     * @param required True if the original order ID is required but
     * is not set.
     *
     * @throws I18NException Thrown if the original order ID is
     * required but is not set.
     */

    private static void addOriginalOrderID
        (OrderID originalOrderID,
         FIXVersion fixVersion,
         Message msg,
         boolean required)
        throws I18NException
    {
        if (originalOrderID==null) {
            if (required) {
                throw new I18NException(Messages.NO_ORIGINAL_ORDER_ID);
            }
        } else{
            msg.setField(new OrigClOrdID(originalOrderID.getValue()));
        }
    }

    /**
     * Adds the given broker order ID to the given QuickFIX/J
     * message (of the given FIX version).
     *
     * @param brokerOrderID The broker order ID. It may be
     * null.
     * @param fixVersion The FIX version. 
     * @param msg The QuickFIX/J message. 
     * @param required True if the broker order ID is required
     * but is not set.
     *
     * @throws I18NException Thrown if the broker order ID is
     * required but is not set.
     */

    private static void addBrokerOrderID
        (String brokerOrderID,
         FIXVersion fixVersion,
         Message msg,
         boolean required)
        throws I18NException
    {
        if (brokerOrderID==null) {
            if (required) {
                throw new I18NException(Messages.NO_BROKER_ORDER_ID);
            }
        } else{
            msg.setField(new quickfix.field.OrderID(brokerOrderID));
        }
    }

    /**
     * Adds the given side to the given QuickFIX/J message (of the
     * given FIX version).
     *
     * @param side The side. It may be null or unknown.
     * @param fixVersion The FIX version. 
     * @param msg The QuickFIX/J message. 
     * @param required True if the side is required but is not set.
     *
     * @throws I18NException Thrown if the side is required but is not
     * set.
     */

    private static void addSide
        (Side side,
         FIXVersion fixVersion,
         Message msg,
         boolean required)
        throws I18NException
    {
        if ((side==null) ||
            (side==Side.Unknown)) {
            if (required) {
                throw new I18NException
                    (new I18NBoundMessage1P(Messages.NO_SIDE,side));
            }
        } else{
            msg.setField(new quickfix.field.Side(side.getFIXValue()));
        }
    }

    /**
     * Adds the given security type to the given QuickFIX/J message
     * (of the given FIX version).
     *
     * @param securityType The security type. It may be null or
     * unknown.
     * @param fixVersion The FIX version. 
     * @param msg The QuickFIX/J message. 
     * @param required True if the security type is required but is
     * not set.
     *
     * @throws I18NException Thrown if the security type is required
     * but is not set.
     */

    private static void addSecurityType
        (SecurityType securityType,
         FIXVersion fixVersion,
         Message msg,
         boolean required)
        throws I18NException
    {
        boolean supported=(fixVersion!=FIXVersion.FIX40);
        if ((securityType==null) ||
            (securityType==SecurityType.Unknown)) {
            if (supported && required) {
                throw new I18NException
                    (new I18NBoundMessage1P
                     (Messages.NO_SECURITY_TYPE,securityType));
            }
        } else{
            if (!supported) {
                throw new I18NException
                    (new I18NBoundMessage1P
                     (Messages.UNSUPPORTED_SECURITY_TYPE,fixVersion));
            }
            msg.setField(new quickfix.field.SecurityType
                         (securityType.getFIXValue()));
        }
    }

    /**
     * Adds the given time-in-force to the given QuickFIX/J message
     * (of the given FIX version).
     *
     * @param timeInForce The time-in-force. It may be null or
     * unknown.
     * @param fixVersion The FIX version. 
     * @param msg The QuickFIX/J message. 
     * @param required True if the time-in-force is required but is
     * not set.
     *
     * @throws I18NException Thrown if the time-in-force is required
     * but is not set.
     */

    private static void addTimeInForce
        (TimeInForce timeInForce,
         FIXVersion fixVersion,
         Message msg,
         boolean required)
        throws I18NException
    {
        boolean supported=(fixVersion!=FIXVersion.FIX40);
        if ((timeInForce==null) ||
            (timeInForce==TimeInForce.Unknown)) {
            if (supported && required) {
                throw new I18NException
                    (new I18NBoundMessage1P
                     (Messages.NO_TIME_IN_FORCE,timeInForce));
            }
        } else{
            if (!supported) {
                throw new I18NException
                    (new I18NBoundMessage1P
                     (Messages.UNSUPPORTED_TIME_IN_FORCE,fixVersion));
            }
            msg.setField(new quickfix.field.TimeInForce
                         (timeInForce.getFIXValue()));
        }
    }

    /**
     * Adds the given position effect to the given QuickFIX/J message
     * (of the given FIX version).
     *
     * @param positionEffect The position effect. It may be null or
     * unknown.
     * @param fixVersion The FIX version. 
     * @param msg The QuickFIX/J message. 
     * @param required True if the position effect is required but is
     * not set.
     *
     * @throws I18NException Thrown if the position effect is required
     * but is not set.
     */

    private static void addPositionEffect
        (PositionEffect positionEffect,
         FIXVersion fixVersion,
         Message msg,
         boolean required)
        throws I18NException
    {
        boolean supported=((fixVersion!=FIXVersion.FIX40) &&
                           (fixVersion!=FIXVersion.FIX41) &&
                           (fixVersion!=FIXVersion.FIX42));
        if ((positionEffect==null) ||
            (positionEffect==PositionEffect.Unknown)) {
            if (supported && required) {
                throw new I18NException
                    (new I18NBoundMessage1P
                     (Messages.NO_POSITION_EFFECT,positionEffect));
            }
        } else{
            if (!supported) {
                throw new I18NException
                    (new I18NBoundMessage1P
                     (Messages.UNSUPPORTED_POSITION_EFFECT,fixVersion));
            }
            msg.setField(new quickfix.field.PositionEffect
                         (positionEffect.getFIXValue()));
        }
    }

    /**
     * Adds the given order capacity to the given QuickFIX/J message
     * (of the given FIX version).
     *
     * @param orderCapacity The order capacity. It may be null or
     * unknown.
     * @param fixVersion The FIX version. 
     * @param msg The QuickFIX/J message. 
     * @param required True if the order capacity is required but is
     * not set.
     *
     * @throws I18NException Thrown if the order capacity is required
     * but is not set.
     */

    private static void addOrderCapacity
        (OrderCapacity orderCapacity,
         FIXVersion fixVersion,
         Message msg,
         boolean required)
        throws I18NException
    {
        boolean supported=((fixVersion!=FIXVersion.FIX40) &&
                           (fixVersion!=FIXVersion.FIX41) &&
                           (fixVersion!=FIXVersion.FIX42));
        if ((orderCapacity==null) ||
            (orderCapacity==OrderCapacity.Unknown)) {
            if (supported && required) {
                throw new I18NException
                    (new I18NBoundMessage1P
                     (Messages.NO_ORDER_CAPACITY,orderCapacity));
            }
        } else{
            if (!supported) {
                throw new I18NException
                    (new I18NBoundMessage1P
                     (Messages.UNSUPPORTED_ORDER_CAPACITY,fixVersion));
            }
            msg.setField(new quickfix.field.OrderCapacity
                         (orderCapacity.getFIXValue()));
        }
    }

    /**
     * Adds the given order type to the given QuickFIX/J message (of
     * the given FIX version).
     *
     * @param orderType The order type. It may be null or unknown.
     * @param fixVersion The FIX version. 
     * @param msg The QuickFIX/J message. 
     * @param required True if the order type is required but is not
     * set.
     *
     * @throws I18NException Thrown if the order type is required but
     * is not set.
     */

    private static void addOrderType
        (OrderType orderType,
         FIXVersion fixVersion,
         Message msg,
         boolean required)
        throws I18NException
    {
        boolean supported=(fixVersion!=FIXVersion.FIX40);
        if ((orderType==null) ||
            (orderType==OrderType.Unknown)) {
            if (supported && required) {
                throw new I18NException
                    (new I18NBoundMessage1P(Messages.NO_ORDER_TYPE,orderType));
            }
        } else{
            if (!supported) {
                throw new I18NException
                    (new I18NBoundMessage1P
                     (Messages.UNSUPPORTED_ORDER_TYPE,fixVersion));
            }
            msg.setField(new OrdType(orderType.getFIXValue()));
        }
    }

    /**
     * Adds the custom fields of the given order to the given
     * QuickFIX/J message.
     *
     * @param o The order.
     * @param msg The message.
     */

    private static void addCustomFields
        (OrderBase o,
         Message msg)
    {
        Map<String,String> fields=o.getCustomFields();
        if (fields==null) {
            return;
        }
        for (String k:fields.keySet()) {
            msg.setString(Integer.parseInt(k),fields.get(k));
        }
    }

    /**
     * Returns the QuickFIX/J message form of the given single order,
     * using the given version-specific message factory (alongside the
     * associated version) to effect conversion.
     *
     * @param fixFactory The factory.
     * @param fixVersion The FIX version. 
     * @param o The order.
     *
     * @return The QuickFIX/J message.
     *
     * @throws I18NException Thrown if conversion fails.
     */

    public static Message toQMessage
        (FIXMessageFactory fixFactory,
         FIXVersion fixVersion,
         OrderSingle o)
        throws I18NException
    {
        Message msg=fixFactory.newOrderEmpty();
        addOrderID(o.getOrderID(),fixVersion,msg,true);
        addSymbol(o.getSymbol(),fixVersion,msg,true);
        addSide(o.getSide(),fixVersion,msg,true);
        addOrderType(o.getOrderType(),fixVersion,msg,true);
        addSecurityType(o.getSecurityType(),fixVersion,msg,false);
        addQuantity(o.getQuantity(),fixVersion,msg,false);
        addTimeInForce(o.getTimeInForce(),fixVersion,msg,false);
        addAccount(o.getAccount(),fixVersion,msg,false);
        addPositionEffect(o.getPositionEffect(),fixVersion,msg,false);
        addOrderCapacity(o.getOrderCapacity(),fixVersion,msg,false);
        if (o.getOrderType()==OrderType.Limit) {
            addPrice(o.getPrice(),fixVersion,msg,true);
        }
        addCustomFields(o,msg);
        fixFactory.getMsgAugmentor().newOrderSingleAugment(msg);
        return msg;
    }

    /**
     * Returns the QuickFIX/J message form of the given order
     * cancellation, using the given version-specific message factory
     * (alongside the associated version) to effect conversion.
     *
     * @param fixFactory The factory.
     * @param fixVersion The FIX version. 
     * @param o The order cancellation.
     *
     * @return The QuickFIX/J message.
     *
     * @throws I18NException Thrown if conversion fails.
     */

    public static Message toQMessage
        (FIXMessageFactory fixFactory,
         FIXVersion fixVersion,
         OrderCancel o)
        throws I18NException
    {
        Message msg=fixFactory.newCancelEmpty();
        addOriginalOrderID(o.getOriginalOrderID(),fixVersion,msg,true);
        addOrderID(o.getOrderID(),fixVersion,msg,true);
        addSymbol(o.getSymbol(),fixVersion,msg,true);
        addSide(o.getSide(),fixVersion,msg,true);
        addQuantity(o.getQuantity(),fixVersion,msg,false);
        addBrokerOrderID(o.getBrokerOrderID(),fixVersion,msg,false);
        addAccount(o.getAccount(),fixVersion,msg,false);
        addSecurityType(o.getSecurityType(),fixVersion,msg,false);
        addCustomFields(o,msg);
        fixFactory.getMsgAugmentor().cancelRequestAugment(msg);
        return msg;
    }

    /**
     * Returns the QuickFIX/J message form of the given order
     * replacement, using the given version-specific message factory
     * (alongside the associated version) to effect conversion.
     *
     * @param fixFactory The factory.
     * @param fixVersion The FIX version. 
     * @param o The order replacement.
     *
     * @return The QuickFIX/J message.
     *
     * @throws I18NException Thrown if conversion fails.
     */

    public static Message toQMessage
        (FIXMessageFactory fixFactory,
         FIXVersion fixVersion,
         OrderReplace o)
        throws I18NException
    {
        Message msg=fixFactory.newCancelReplaceEmpty();
        addOriginalOrderID(o.getOriginalOrderID(),fixVersion,msg,true);
        addOrderID(o.getOrderID(),fixVersion,msg,true);
        addSymbol(o.getSymbol(),fixVersion,msg,true);
        addSide(o.getSide(),fixVersion,msg,true);
        addOrderType(o.getOrderType(),fixVersion,msg,true);
        addQuantity(o.getQuantity(),fixVersion,msg,false);
        addAccount(o.getAccount(),fixVersion,msg,false);
        addPrice(o.getPrice(),fixVersion,msg,false);
        addSecurityType(o.getSecurityType(),fixVersion,msg,false);
        addTimeInForce(o.getTimeInForce(),fixVersion,msg,false);
        addPositionEffect(o.getPositionEffect(),fixVersion,msg,false);
        addOrderCapacity(o.getOrderCapacity(),fixVersion,msg,false);
        addBrokerOrderID(o.getBrokerOrderID(),fixVersion,msg,false);
        addCustomFields(o,msg);
        fixFactory.getMsgAugmentor().cancelReplaceRequestAugment(msg);
        return msg;
    }

    /**
     * Returns the QuickFIX/J message form of the given order, using
     * the given version-specific message factory (alongside the
     * associated version) to effect conversion.
     *
     * @param fixFactory The factory.
     * @param fixVersion The FIX version. 
     * @param o The order.
     *
     * @return The QuickFIX/J message.
     *
     * @throws I18NException Thrown if conversion fails.
     */

    public static Message toQMessage
        (FIXMessageFactory fixFactory,
         FIXVersion fixVersion,
         Order o)
        throws I18NException
    {
        if (o instanceof FIXOrder) {
            return ((FIXOrder)o).getMessage();
        }
        if (o instanceof OrderSingle) {
            return toQMessage(fixFactory,fixVersion,(OrderSingle)o);
        }
        if (o instanceof OrderCancel) {
            return toQMessage(fixFactory,fixVersion,(OrderCancel)o);
        }
        if (o instanceof OrderReplace) {
            return toQMessage(fixFactory,fixVersion,(OrderReplace)o);
        }
        throw new I18NException
            (new I18NBoundMessage1P(Messages.CANNOT_CONVERT,o));
    }

    /**
     * Returns the FIX Agnostic message form of the given QuickFIX/J
     * message.
     *
     * @param msg The QuickFIX/J message.
     * @param originator The message originator.
     * @param brokerID The ID of the broker which generated
     * the QuickFIX/J message. It may be null.
     * @param actorID The ID of the actor user of this QuickFIX/J
     * message.
     * @param viewerID The ID of the viewer user of this QuickFIX/J
     * message.
     *
     * @return The FIX Agnostic message. It is null if conversion is
     * not available for the QuickFIX/J message type.
     *
     * @throws MessageCreationException Thrown if conversion is
     * available for the QuickFIX/J message type, but it fails.
     */

    public static TradeMessage fromQMessage
        (Message msg,
         Originator originator,
         BrokerID brokerID,
         UserID actorID,
         UserID viewerID)
        throws MessageCreationException
    {
        if (FIXMessageUtil.isExecutionReport(msg)) {
            return Factory.getInstance().createExecutionReport
                (msg,brokerID,originator,actorID,viewerID);
        }
        if (FIXMessageUtil.isCancelReject(msg)) {
            return Factory.getInstance().createOrderCancelReject
                (msg,brokerID,originator,actorID,viewerID);
        }
        return null;
    }


    // CONSTRUCTORS.

    /**
     * Constructor. It is private so that no instances can be created.
     */

    private FIXConverter() {}
}
