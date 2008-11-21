package org.marketcetera.trade;

import org.marketcetera.util.misc.ClassVersion;
import quickfix.Message;

/* $License$ */
/**
 * Factory for creating the messages in this package.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public abstract class Factory {
    /**
     * Returns a factory instance that can be used to create various messages.
     *
     * @return a factory instance for creating various messages.
     */
    public static Factory getInstance() {
        return sFactory;
    }
    /**
     * Creates a new order to trade a security.
     *
     * @return A new order to trade a security.
     */
    public abstract OrderSingle createOrderSingle();

    /**
     * Creates a new order based on a
     * {@link org.marketcetera.quickfix.FIXVersion#FIX_SYSTEM}
     *  new single order message.
     *
     * @param inMessage A System FIX New Single Order Message. Cannot be null.
     * @param inDestinationID the optional ID of the destination / broker
     * to which this order should be sent to. Can be null.
     *
     * @return a new order to trade a security based on the supplied message.
     *
     * @throws MessageCreationException If the supplied message
     * cannot be translated into the order.
     */
    public abstract OrderSingle createOrderSingle(
            Message inMessage,
            DestinationID inDestinationID)
            throws MessageCreationException;

    /**
     * Creates a suggestion for a new order to trade a security.
     *
     * @return a suggestion for a new order.
     */
    public abstract OrderSingleSuggestion createOrderSingleSuggestion();

    /**
     * Creates an order to cancel a previously placed order as
     * identified by the supplied execution report. The execution
     * report should be the latest execution report for the order on a
     * best-effort basis. If the execution report supplied happens to not
     * be the latest execution report, the returned order might be
     * rejected by the broker / destination.
     *
     * @param inLatestReport the latest execution report for the order
     * that needs to be cancelled. Can be null. If a null value is supplied
     * the return value has its attributes uninitialized.
     *
     * @return The cancel order based on the supplied execution report.
     * Appropriate fields from the supplied execution report are copied into
     * this order. Do note that
     * {@link OrderCancel#getCustomFields() custom field}
     * values are not set in the returned order.
     */
    public abstract OrderCancel createOrderCancel(ExecutionReport inLatestReport);

    /**
     * Creates a cancel order based on the supplied
     * {@link org.marketcetera.quickfix.FIXVersion#FIX_SYSTEM} order cancel
     * message.
     *
     * @param inMessage A System FIX Order Cancel message. Cannot be null.
     *
     * @param inDestinationID the optional ID of the destination / broker
     * to which this order should be sent to. Can be null.
     * 
     * @return An order cancel message based on the supplied message.
     *
     * @throws MessageCreationException If the supplied message
     * cannot be translated into the order.
     */
    public abstract OrderCancel createOrderCancel(
            Message inMessage,
            DestinationID inDestinationID)
            throws MessageCreationException;

    /**
     * Creates an order to replace a previously placed order as
     * identified by the supplied execution report. The execution
     * report should be the latest execution report for the order on a
     * best-effort basis. If the execution report supplied happens to not
     * be the latest execution report, the returned order might be
     * rejected by the broker / destination.
     *
     * @param inLatestReport the latest execution report for the order
     * that needs to be cancelled. If null, an uninitialized order instance
     * is returned.
     *
     * @return The replace order based on the supplied execution report.
     * Appropriate fields from the supplied execution report are copied
     * into the replace order. Do note that
     * {@link OrderReplace#getCustomFields() custom field}
     * values are not set in the returned order.
     */
    public abstract OrderReplace createOrderReplace(ExecutionReport inLatestReport);

    /**
     * Creates an order to replace a previously placed order based on the
     * supplied {@link org.marketcetera.quickfix.FIXVersion#FIX_SYSTEM}
     * replace order message.
     *
     * @param inMessage A System FIX Order Replace message. Cannot be null.
     *
     * @param inDestinationID the optional ID of the destination / broker
     * to which this order should be sent to. Can be null.
     * 
     * @return An Order Replace message based on the supplied message.
     *
     * @throws MessageCreationException If the supplied message
     * cannot be translated into the order.
     */
    public abstract OrderReplace createOrderReplace(
            Message inMessage,
            DestinationID inDestinationID)
            throws MessageCreationException;

    /**
     * Creates an order based on the supplied NewOrderSingle FIX Message.
     * It's recommended that this method only be used if the other means
     * to create a FIX message do not provide features necessary to carry
     * out this trade.
     * <p>
     * Orders created via this method will have to be created such that
     * they can be accepted by the broker / destination without requiring any
     * transformations. Since the contents of the message may be
     * broker / destination specific, it is required that a
     * {@link Order#getDestinationID()}  destinationID}
     * be specified when sending this message to the server. 
     *
     * @param inMessage The FIX message for the order. Cannot be null.
     *
     * @param inDestinationID the optional ID of the destination / broker
     * to which this order should be sent to. Cannot be null.
     * 
     * @return The order wrapping the supplied FIX Message in a form
     * that can be sent to the server. The returned type will implement
     * {@link FIXMessageSupport}.
     *
     * @throws MessageCreationException if there were errors wrapping
     * the supplied FIX Message.
     */
    public abstract FIXOrder createOrder(Message inMessage,
                                      DestinationID inDestinationID)
            throws MessageCreationException;

    /**
     * Creates an execution report based on the supplied execution report
     * FIX Message.
     *
     * @param inMessage the execution report FIX message. Cannot be null.
     *
     * @param inDestinationID the ID of the destination from which this
     * message was received.
     *
     * @param inOriginator the originator of this message. Cannot be null.
     * 
     * @return An execution report instance based on the supplied
     * FIX Message. The returned type will implement
     * {@link FIXMessageSupport}.
     *
     * @throws MessageCreationException if there were errors wrapping
     * the supplied FIX Message.
     */
    public abstract ExecutionReport createExecutionReport(
            Message inMessage, DestinationID inDestinationID,
            Originator inOriginator)
            throws MessageCreationException;
    /**
     * Creates an execution report based on the supplied execution report
     * FIX Message. Defaults the Originator to {@link Originator#Server}
     *
     * @param inMessage the execution report FIX message. Cannot be null.
     *
     * @param inDestinationID the ID of the destination from which this
     * message was received.
     *
     * @return An execution report instance based on the supplied
     * FIX Message. The returned type will implement
     * {@link FIXMessageSupport}.
     *
     * @throws MessageCreationException if there were errors wrapping
     * the supplied FIX Message.
     *
     * @deprecated Use
     * {@link #createExecutionReport(quickfix.Message, DestinationID, Originator)}
     * instead. 
     */
    @Deprecated
    public abstract ExecutionReport createExecutionReport(
            Message inMessage, DestinationID inDestinationID)
            throws MessageCreationException;

    /**
     * Creates an order cancel reject message based on the supplied
     * order cancel reject FIX Message.
     *
     * @param inMessage the order cancel FIX Message. Cannot be null.
     *
     * @param inDestinationID the ID of the destination from which this
     * message was received.
     * 
     * @return an order cancel reject message wrapping the supplied
     * FIX Message. The returned type will implement
     * {@link FIXMessageSupport}.
     *
     * @throws MessageCreationException if there were errors wrapping the
     * supplied FIX Message.
     */
    public abstract OrderCancelReject createOrderCancelReject(
            Message inMessage, DestinationID inDestinationID)
            throws MessageCreationException;

    /**
     * Creates an instance. 
     */
    protected Factory() {
        //do nothing.
    }
    private static final Factory sFactory = new FactoryImpl();
}
