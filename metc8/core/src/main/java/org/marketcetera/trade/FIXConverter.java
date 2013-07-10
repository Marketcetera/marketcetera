package org.marketcetera.trade;

import java.math.BigDecimal;
import java.util.Map;

import org.marketcetera.core.instruments.InstrumentToMessage;
import org.marketcetera.quickfix.FIXMessageFactory;
import org.marketcetera.quickfix.FIXMessageUtil;
import org.marketcetera.util.except.I18NException;
import org.marketcetera.util.log.I18NBoundMessage1P;
import org.marketcetera.util.misc.ClassVersion;

import quickfix.DataDictionary;
import quickfix.Message;
import quickfix.field.*;

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
     * Adds the given instrument to the given QuickFIX/J message (of the
     * given FIX dictionary).
     *
     * @param instrument The instrument. It cannot be null.
     * @param fixDictionary The FIX dictionary.
     * @param msgType The FIX message type
     * @param required True if the instrument is required.
     * @param msg The QuickFIX/J message.
     * @throws I18NException Thrown if the instrument is required but is
     * not set.
     */

    private static void addInstrument
            (Instrument instrument,
             DataDictionary fixDictionary,
             String msgType,
             Message msg,
             boolean required)
            throws I18NException
    {
        if(instrument==null) {
            if (required) {
                throw new I18NException(new I18NBoundMessage1P(Messages.NO_INSTRUMENT,instrument));
            }
        } else{
            InstrumentToMessage<?> instrumentFunction=InstrumentToMessage.SELECTOR.
                    forInstrument(instrument);
            if (!instrumentFunction.isSupported(fixDictionary,msgType)) {
                throw new I18NException(Messages.UNSUPPORTED_INSTRUMENT);
            }
            instrumentFunction.set(instrument,fixDictionary,msgType,msg);
        }
    }

    /**
     * Adds the given price to the given QuickFIX/J message (of the
     * given FIX dictionary).
     *
     * @param price The price. It may be null.
     * @param fixDictionary The FIX dictionary. 
     * @param msgType The FIX message type
     * @param msg The QuickFIX/J message.
     * @param required True if the price is required but is not set.
     *
     * @throws I18NException Thrown if the price is required but is
     * not set.
     */

    private static void addPrice
        (BigDecimal price,
         DataDictionary fixDictionary,
         String msgType,
         Message msg,
         boolean required)
        throws I18NException
    {
        boolean supported=
            (fixDictionary.isMsgField(msgType,Price.FIELD));
        if (price==null) {
            if (supported && required) {
                throw new I18NException(Messages.NO_PRICE);
            }
        } else{
            if (!supported) {
                throw new I18NException(Messages.UNSUPPORTED_PRICE);
            }
            msg.setField(new Price(price));
        }
    }

    /**
     * Adds the given quantity to the given QuickFIX/J message (of the
     * given FIX dictionary).
     *
     * @param quantity The quantity. It may be null.
     * @param fixDictionary The FIX dictionary. 
     * @param msgType The FIX message type
     * @param msg The QuickFIX/J message.
     * @param required True if the quantity is required but is not set.
     *
     * @throws I18NException Thrown if the quantity is required but is
     * not set.
     */

    private static void addQuantity
        (BigDecimal quantity,
         DataDictionary fixDictionary,
         String msgType,
         Message msg,
         boolean required)
        throws I18NException
    {
        boolean supported=
            (fixDictionary.isMsgField(msgType,OrderQty.FIELD));
        if (quantity==null) {
            if (supported && required) {
                throw new I18NException(Messages.NO_QUANTITY);
            }
        } else{
            if (!supported) {
                throw new I18NException(Messages.UNSUPPORTED_QUANTITY);
            }
            msg.setField(new OrderQty(quantity));
        }
    }
    
    /**
     * Adds the given display quantity to the given QuickFIX/J message (of the
     * given FIX dictionary).
     *
     * @param display quantity The quantity. It may be null.
     * @param fixDictionary The FIX dictionary. 
     * @param msgType The FIX message type
     * @param msg The QuickFIX/J message.
     * @param required True if the display quantity is required but is not set.
     *
     * @throws I18NException Thrown if the display quantity is required but is
     * not set.
     */

    private static void addDisplayQuantity
        (BigDecimal displayQuantity,
         DataDictionary fixDictionary,
         String msgType,
         Message msg,
         boolean required)
        throws I18NException
    {
        boolean supported=
            (fixDictionary.isMsgField(msgType,MaxFloor.FIELD));
        if (displayQuantity==null) {
            if (supported && required) {
                throw new I18NException(Messages.NO_DISPLAY_QUANTITY);
            }
        } else{
            if (!supported) {
                throw new I18NException(Messages.UNSUPPORTED_DISPLAY_QUANTITY);
            }
            msg.setField(new MaxFloor(displayQuantity));
        }
    }
    
    

    /**
     * Adds the given account to the given QuickFIX/J message (of the
     * given FIX dictionary).
     *
     * @param account The account. It may be null.
     * @param fixDictionary The FIX dictionary. 
     * @param msgType The FIX message type
     * @param msg The QuickFIX/J message.
     * @param required True if the account is required but is not set.
     *
     * @throws I18NException Thrown if the account is required but is
     * not set.
     */

    private static void addAccount
        (String account,
         DataDictionary fixDictionary,
         String msgType,
         Message msg,
         boolean required)
        throws I18NException
    {
        boolean supported=
            (fixDictionary.isMsgField(msgType,Account.FIELD));
        if (account==null) {
            if (supported && required) {
                throw new I18NException(Messages.NO_ACCOUNT);
            }
        } else{
            if (!supported) {
                throw new I18NException(Messages.UNSUPPORTED_ACCOUNT);
            }
            msg.setField(new Account(account));
        }
    }

    /**
     * Adds the given text to the given QuickFIX/J message (of the
     * given FIX dictionary).
     *
     * @param text The text. It may be null.
     * @param fixDictionary The FIX dictionary. 
     * @param msgType The FIX message type
     * @param msg The QuickFIX/J message.
     * @param required True if the account is required but is not set.
     *
     * @throws I18NException Thrown if the account is required but is
     * not set.
     */

    private static void addText
        (String text,
         DataDictionary fixDictionary,
         String msgType,
         Message msg,
         boolean required)
        throws I18NException
    {
        boolean supported=
            (fixDictionary.isMsgField(msgType,Text.FIELD));
        if (text==null) {
            if (supported && required) {
                throw new I18NException(Messages.NO_TEXT);
            }
        } else{
            if (!supported) {
                throw new I18NException(Messages.UNSUPPORTED_TEXT);
            }
            msg.setField(new Text(text));
        }
    }
    /**
     * Adds the given order ID to the given QuickFIX/J message (of the
     * given FIX dictionary).
     *
     * @param orderID The order ID. It may be null.
     * @param fixDictionary The FIX dictionary. 
     * @param msgType The FIX message type
     * @param msg The QuickFIX/J message.
     * @param required True if the order ID is required but is not
     * set.
     *
     * @throws I18NException Thrown if the order ID is required but is
     * not set.
     */

    private static void addOrderID
        (OrderID orderID,
         DataDictionary fixDictionary,
         String msgType,
         Message msg,
         boolean required)
        throws I18NException
    {
        boolean supported=
            (fixDictionary.isMsgField(msgType,ClOrdID.FIELD));
        if (orderID==null) {
            if (supported && required) {
                throw new I18NException(Messages.NO_ORDER_ID);
            }
        } else{
            if (!supported) {
                throw new I18NException(Messages.UNSUPPORTED_ORDER_ID);
            }
            msg.setField(new ClOrdID(orderID.getValue()));
        }
    }

    /**
     * Adds the given original order ID to the given QuickFIX/J
     * message (of the given FIX dictionary).
     *
     * @param originalOrderID The original order ID. It may be null.
     * @param fixDictionary The FIX dictionary. 
     * @param msgType The FIX message type
     * @param msg The QuickFIX/J message.
     * @param required True if the original order ID is required but
     * is not set.
     *
     * @throws I18NException Thrown if the original order ID is
     * required but is not set.
     */

    private static void addOriginalOrderID
        (OrderID originalOrderID,
         DataDictionary fixDictionary,
         String msgType,
         Message msg,
         boolean required)
        throws I18NException
    {
        boolean supported=
            (fixDictionary.isMsgField(msgType,OrigClOrdID.FIELD));
        if (originalOrderID==null) {
            if (supported && required) {
                throw new I18NException(Messages.NO_ORIGINAL_ORDER_ID);
            }
        } else{
            if (!supported) {
                throw new I18NException(Messages.UNSUPPORTED_ORIGINAL_ORDER_ID);
            }
            msg.setField(new OrigClOrdID(originalOrderID.getValue()));
        }
    }

    /**
     * Adds the given broker order ID to the given QuickFIX/J
     * message (of the given FIX dictionary).
     *
     * @param brokerOrderID The broker order ID. It may be
     * null.
     * @param fixDictionary The FIX dictionary. 
     * @param msgType The FIX message type
     * @param msg The QuickFIX/J message.
     * @param required True if the broker order ID is required
     * but is not set.
     *
     * @throws I18NException Thrown if the broker order ID is
     * required but is not set.
     */

    private static void addBrokerOrderID
        (String brokerOrderID,
         DataDictionary fixDictionary,
         String msgType,
         Message msg,
         boolean required)
        throws I18NException
    {
        boolean supported=
            (fixDictionary.isMsgField(msgType,quickfix.field.OrderID.FIELD));
        if (brokerOrderID==null) {
            if (supported && required) {
                throw new I18NException(Messages.NO_BROKER_ORDER_ID);
            }
        } else{
            if (!supported) {
                throw new I18NException(Messages.UNSUPPORTED_BROKER_ORDER_ID);
            }
            msg.setField(new quickfix.field.OrderID(brokerOrderID));
        }
    }

    /**
     * Adds the given side to the given QuickFIX/J message (of the
     * given FIX dictionary).
     *
     * @param side The side. It may be null or unknown.
     * @param fixDictionary The FIX dictionary. 
     * @param msgType The FIX message type
     * @param msg The QuickFIX/J message.
     * @param required True if the side is required but is not set.
     *
     * @throws I18NException Thrown if the side is required but is not
     * set.
     */

    private static void addSide
        (Side side,
         DataDictionary fixDictionary,
         String msgType,
         Message msg,
         boolean required)
        throws I18NException
    {
        boolean supported=
            (fixDictionary.isMsgField(msgType,quickfix.field.Side.FIELD));
        if ((side==null) ||
            (side==Side.Unknown)) {
            if (supported && required) {
                throw new I18NException
                    (new I18NBoundMessage1P(Messages.NO_SIDE,side));
            }
        } else{
            if (!supported) {
                throw new I18NException(Messages.UNSUPPORTED_SIDE);
            }
            msg.setField(new quickfix.field.Side(side.getFIXValue()));
        }
    }

    /**
     * Adds the given time-in-force to the given QuickFIX/J message
     * (of the given FIX dictionary).
     *
     * @param timeInForce The time-in-force. It may be null or
     * unknown.
     * @param fixDictionary The FIX dictionary. 
     * @param msgType The FIX message type
     * @param msg The QuickFIX/J message.
     * @param required True if the time-in-force is required but is
     * not set.
     *
     * @throws I18NException Thrown if the time-in-force is required
     * but is not set.
     */

    private static void addTimeInForce
        (TimeInForce timeInForce,
         DataDictionary fixDictionary,
         String msgType,
         Message msg,
         boolean required)
        throws I18NException
    {
        boolean supported=
            (fixDictionary.isMsgField(msgType,quickfix.field.TimeInForce.FIELD));
        if ((timeInForce==null) ||
            (timeInForce==TimeInForce.Unknown)) {
            if (supported && required) {
                throw new I18NException
                    (new I18NBoundMessage1P
                     (Messages.NO_TIME_IN_FORCE,timeInForce));
            }
        } else{
            if (!supported) {
                throw new I18NException(Messages.UNSUPPORTED_TIME_IN_FORCE);
            }
            msg.setField(new quickfix.field.TimeInForce
                         (timeInForce.getFIXValue()));
        }
    }

    /**
     * Adds the given position effect to the given QuickFIX/J message
     * (of the given FIX dictionary).
     *
     * @param positionEffect The position effect. It may be null or
     * unknown.
     * @param fixDictionary The FIX dictionary. 
     * @param msgType The FIX message type
     * @param msg The QuickFIX/J message.
     * @param required True if the position effect is required but is
     * not set.
     *
     * @throws I18NException Thrown if the position effect is required
     * but is not set.
     */

    private static void addPositionEffect
        (PositionEffect positionEffect,
         DataDictionary fixDictionary,
         String msgType,
         Message msg,
         boolean required)
        throws I18NException
    {
        boolean supported=
            (fixDictionary.isMsgField(msgType,quickfix.field.PositionEffect.FIELD));
        if ((positionEffect==null) ||
            (positionEffect==PositionEffect.Unknown)) {
            if (supported && required) {
                throw new I18NException
                    (new I18NBoundMessage1P
                     (Messages.NO_POSITION_EFFECT,positionEffect));
            }
        } else{
            if (!supported) {
                throw new I18NException(Messages.UNSUPPORTED_POSITION_EFFECT);
            }
            msg.setField(new quickfix.field.PositionEffect
                         (positionEffect.getFIXValue()));
        }
    }

    /**
     * Adds the given order capacity to the given QuickFIX/J message
     * (of the given FIX dictionary).
     *
     * @param orderCapacity The order capacity. It may be null or
     * unknown.
     * @param fixDictionary The FIX dictionary. 
     * @param msgType The FIX message type
     * @param msg The QuickFIX/J message.
     * @param required True if the order capacity is required but is
     * not set.
     *
     * @throws I18NException Thrown if the order capacity is required
     * but is not set.
     */

    private static void addOrderCapacity
        (OrderCapacity orderCapacity,
         DataDictionary fixDictionary,
         String msgType,
         Message msg,
         boolean required)
        throws I18NException
    {
        boolean supported=
            (fixDictionary.isMsgField(msgType,quickfix.field.OrderCapacity.FIELD));
        if ((orderCapacity==null) ||
            (orderCapacity==OrderCapacity.Unknown)) {
            if (supported && required) {
                throw new I18NException
                    (new I18NBoundMessage1P
                     (Messages.NO_ORDER_CAPACITY,orderCapacity));
            }
        } else{
            if (!supported) {
                throw new I18NException(Messages.UNSUPPORTED_ORDER_CAPACITY);
            }
            msg.setField(new quickfix.field.OrderCapacity
                         (orderCapacity.getFIXValue()));
        }
    }

    /**
     * Adds the given order type to the given QuickFIX/J message (of
     * the given FIX dictionary).
     *
     * @param orderType The order type. It may be null or unknown.
     * @param fixDictionary The FIX dictionary. 
     * @param msgType The FIX message type
     * @param msg The QuickFIX/J message.
     * @param required True if the order type is required but is not
     * set.
     *
     * @throws I18NException Thrown if the order type is required but
     * is not set.
     */

    private static void addOrderType
        (OrderType orderType,
         DataDictionary fixDictionary,
         String msgType,
         Message msg,
         boolean required)
        throws I18NException
    {
        boolean supported=
            (fixDictionary.isMsgField(msgType,OrdType.FIELD));
        if ((orderType==null) ||
            (orderType==OrderType.Unknown)) {
            if (supported && required) {
                throw new I18NException
                    (new I18NBoundMessage1P(Messages.NO_ORDER_TYPE,orderType));
            }
        } else{
            if (!supported) {
                throw new I18NException(Messages.UNSUPPORTED_ORDER_TYPE);
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
    private static void addCustomFields(OrderBase o,
                                        Message msg)
    {
        Map<String,String> fields=o.getCustomFields();
        if (fields==null) {
            return;
        }
        for(Map.Entry<String,String> entry : fields.entrySet()) {
            msg.setString(Integer.parseInt(String.valueOf(entry.getKey())),
                          String.valueOf(entry.getValue()));
        }
    }

    /**
     * Returns the QuickFIX/J message form of the given single order,
     * using the given message factory (alongside the associated data
     * dictionary) to effect conversion.
     *
     * @param fixFactory The factory.
     * @param fixDictionary The FIX dictionary. 
     * @param o The order.
     *
     * @return The QuickFIX/J message.
     *
     * @throws I18NException Thrown if conversion fails.
     */

    public static Message toQMessage
        (FIXMessageFactory fixFactory,
         DataDictionary fixDictionary,
         OrderSingle o)
        throws I18NException
    {
        Message msg=fixFactory.newOrderEmpty();
        String msgType=MsgType.ORDER_SINGLE;
        addOrderID(o.getOrderID(),fixDictionary,msgType,msg,true);
        addInstrument(o.getInstrument(),fixDictionary,msgType,msg,true);
        addSide(o.getSide(),fixDictionary,msgType,msg,true);
        addOrderType(o.getOrderType(),fixDictionary,msgType,msg,true);
        addQuantity(o.getQuantity(),fixDictionary,msgType,msg,false);
        addDisplayQuantity(o.getDisplayQuantity(),fixDictionary,msgType,msg,false);
        addTimeInForce(o.getTimeInForce(),fixDictionary,msgType,msg,false);
        addAccount(o.getAccount(),fixDictionary,msgType,msg,false);
        addText(o.getText(),fixDictionary,msgType,msg,false);
        addPositionEffect(o.getPositionEffect(),fixDictionary,msgType,msg,false);
        addOrderCapacity(o.getOrderCapacity(),fixDictionary,msgType,msg,false);
        if (o.getOrderType()==OrderType.Limit) {
            addPrice(o.getPrice(),fixDictionary,msgType,msg,true);
        }
        addCustomFields(o, msg);
        fixFactory.getMsgAugmentor().newOrderSingleAugment(msg);
        return msg;
    }

    /**
     * Returns the QuickFIX/J message form of the given order
     * cancellation, using the given message factory (alongside the
     * associated data dictionary) to effect conversion.
     *
     * @param fixFactory The factory.
     * @param fixDictionary The FIX dictionary. 
     * @param o The order cancellation.
     *
     * @return The QuickFIX/J message.
     *
     * @throws I18NException Thrown if conversion fails.
     */

    public static Message toQMessage
        (FIXMessageFactory fixFactory,
         DataDictionary fixDictionary,
         OrderCancel o)
        throws I18NException
    {
        Message msg=fixFactory.newCancelEmpty();
        String msgType=MsgType.ORDER_CANCEL_REQUEST;
        addOriginalOrderID(o.getOriginalOrderID(),fixDictionary,msgType,msg,true);
        addOrderID(o.getOrderID(),fixDictionary,msgType,msg,true);
        addInstrument(o.getInstrument(),fixDictionary,msgType,msg,true);
        addSide(o.getSide(),fixDictionary,msgType,msg,true);
        addQuantity(o.getQuantity(),fixDictionary,msgType,msg,false);
        addBrokerOrderID(o.getBrokerOrderID(),fixDictionary,msgType,msg,false);
        addAccount(o.getAccount(),fixDictionary,msgType,msg,false);
        addText(o.getText(),fixDictionary,msgType,msg,false);
        addCustomFields(o, msg);
        fixFactory.getMsgAugmentor().cancelRequestAugment(msg);
        return msg;
    }

    /**
     * Returns the QuickFIX/J message form of the given order
     * replacement, using the given message factory (alongside the
     * associated data dictionary) to effect conversion.
     *
     * @param fixFactory The factory.
     * @param fixDictionary The FIX dictionary. 
     * @param o The order replacement.
     *
     * @return The QuickFIX/J message.
     *
     * @throws I18NException Thrown if conversion fails.
     */

    public static Message toQMessage
        (FIXMessageFactory fixFactory,
         DataDictionary fixDictionary,
         OrderReplace o)
        throws I18NException
    {
        Message msg=fixFactory.newCancelReplaceEmpty();
        String msgType=MsgType.ORDER_CANCEL_REPLACE_REQUEST;
        addOriginalOrderID(o.getOriginalOrderID(),fixDictionary,msgType,msg,true);
        addOrderID(o.getOrderID(),fixDictionary,msgType,msg,true);
        addInstrument(o.getInstrument(),fixDictionary,msgType,msg,true);
        addSide(o.getSide(),fixDictionary,msgType,msg,true);
        addOrderType(o.getOrderType(),fixDictionary,msgType,msg,true);
        addQuantity(o.getQuantity(),fixDictionary,msgType,msg,false);
        addDisplayQuantity(o.getDisplayQuantity(),fixDictionary,msgType,msg,false);
        addAccount(o.getAccount(),fixDictionary,msgType,msg,false);
        addText(o.getText(),fixDictionary,msgType,msg,false);
        addPrice(o.getPrice(),fixDictionary,msgType,msg,false);
        addTimeInForce(o.getTimeInForce(),fixDictionary,msgType,msg,false);
        addPositionEffect(o.getPositionEffect(),fixDictionary,msgType,msg,false);
        addOrderCapacity(o.getOrderCapacity(),fixDictionary,msgType,msg,false);
        addBrokerOrderID(o.getBrokerOrderID(),fixDictionary,msgType,msg,false);
        addCustomFields(o, msg);
        fixFactory.getMsgAugmentor().cancelReplaceRequestAugment(msg);
        return msg;
    }

    /**
     * Returns the QuickFIX/J message form of the given order, using
     * the given message factory (alongside the associated data
     * dictionary) to effect conversion.
     *
     * @param fixFactory The factory.
     * @param fixDictionary The FIX dictionary. 
     * @param o The order.
     *
     * @return The QuickFIX/J message.
     *
     * @throws I18NException Thrown if conversion fails.
     */

    public static Message toQMessage
        (FIXMessageFactory fixFactory,
         DataDictionary fixDictionary,
         Order o)
        throws I18NException
    {
        if (o instanceof FIXOrder) {
            return ((FIXOrder)o).getMessage();
        }
        if (o instanceof OrderSingle) {
            return toQMessage(fixFactory,fixDictionary,(OrderSingle)o);
        }
        if (o instanceof OrderCancel) {
            return toQMessage(fixFactory,fixDictionary,(OrderCancel)o);
        }
        if (o instanceof OrderReplace) {
            return toQMessage(fixFactory,fixDictionary,(OrderReplace)o);
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
     * message. It may be null.
     * @param viewerID The ID of the viewer user of this QuickFIX/J
     * message. It may be null.
     *
     * @return The FIX Agnostic message.
     *
     * @throws MessageCreationException Thrown if conversion fails.
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
        return Factory.getInstance().createFIXResponse
            (msg,brokerID,originator,actorID,viewerID);
    }


    // CONSTRUCTORS.

    /**
     * Constructor. It is private so that no instances can be created.
     */

    private FIXConverter() {}
}
