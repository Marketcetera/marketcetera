package org.marketcetera.systemmodel;

import org.marketcetera.trade.*;
import org.marketcetera.util.misc.ClassVersion;

import quickfix.Message;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public interface OrderFactory
{
    /**
     * Creates a new order to trade a security.
     *
     * @return A new order to trade a security.
     */
    public OrderSingle createOrderSingle();
    /**
     * Creates a new order based on a
     * {@link org.marketcetera.quickfix.FIXVersion#FIX_SYSTEM}
     *  new single order message.
     *
     * @param inMessage A System FIX New Single Order Message. Cannot be null.
     * @param inBrokerID the optional ID of the broker to which this order should 
     * be sent to. Can be null.
     *
     * @return a new order to trade a security based on the supplied message.
     */
    public OrderSingle createOrderSingle(Message inMessage,
                                         BrokerID inBrokerID);
    /**
     * Creates a suggestion for a new order to trade a security.
     *
     * @return a suggestion for a new order.
     */
    public OrderSingleSuggestion createOrderSingleSuggestion();
    /**
     * Creates an order to cancel a previously placed order as
     * identified by the supplied execution report. The execution
     * report should be the latest execution report for the order on a
     * best-effort basis. If the execution report supplied happens to not
     * be the latest execution report, the returned order might be
     * rejected by the broker.
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
    public OrderCancel createOrderCancel(ExecutionReport inLatestReport);
    /**
     * Creates a cancel order based on the supplied
     * {@link org.marketcetera.quickfix.FIXVersion#FIX_SYSTEM} order cancel
     * message.
     *
     * @param inMessage A System FIX Order Cancel message. Cannot be null.
     *
     * @param inBrokerID the optional ID of the broker to which this order 
     * should be sent to. Can be null.
     * 
     * @return An order cancel message based on the supplied message.
     */
    public OrderCancel createOrderCancel(Message inMessage,
                                         BrokerID inBrokerID);
    /**
     * Creates an order to replace a previously placed order as
     * identified by the supplied execution report. The execution
     * report should be the latest execution report for the order on a
     * best-effort basis. If the execution report supplied happens to not
     * be the latest execution report, the returned order might be
     * rejected by the broker.
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
    public OrderReplace createOrderReplace(ExecutionReport inLatestReport);
    /**
     * Creates an order to replace a previously placed order based on the
     * supplied {@link org.marketcetera.quickfix.FIXVersion#FIX_SYSTEM}
     * replace order message.
     *
     * @param inMessage A System FIX Order Replace message. Cannot be null.
     *
     * @param inBrokerID the optional ID of the broker
     * to which this order should be sent to. Can be null.
     * 
     * @return An Order Replace message based on the supplied message.
     */
    public OrderReplace createOrderReplace(Message inMessage,
                                           BrokerID inBrokerID);
    /**
     * Creates an order based on the supplied NewOrderSingle FIX Message.
     * It's recommended that this method only be used if the other means
     * to create a FIX message do not provide features necessary to carry
     * out this trade.
     * <p>
     * Orders created via this method will have to be created such that
     * they can be accepted by the broker without requiring any
     * transformations. Since the contents of the message may be
     * broker specific, it is required that a
     * {@link Order#getBrokerID()}  brokerID}
     * be specified when sending this message to the server. 
     * <p>
     * Do note that this API will overwrite the <code>ClOrdID</code> field
     * value with a system generated value. It's recommended that the
     * ClOrdID field value is the one that is sent to the server. If the
     * ClOrdID field value is not unique, it may result in data consistency
     * issues in the system and hence it's recommended that the clients
     * depend on the system generated value. 
     *
     * @param inMessage The FIX message for the order. Cannot be null.
     *
     * @param inBrokerID the optional ID of the broker
     * to which this order should be sent to. Cannot be null.
     * 
     * @return The order wrapping the supplied FIX Message in a form
     * that can be sent to the server. The returned type will implement
     * {@link FIXMessageSupport}.
     */
    public FIXOrder createOrder(Message inMessage,
                                BrokerID inBrokerID);
    /**
     * Creates an execution report based on the supplied execution report
     * FIX Message.
     *
     * @param inMessage the execution report FIX message. Cannot be null.
     *
     * @param inBrokerID the ID of the broker from which this
     * message was received.
     *
     * @param inOriginator the originator of this message. Cannot be null.
     *
     * @param inActorID the ID of the actor user of this
     * message.
     * 
     * @param inViewerID the ID of the viewer user of this
     * message.
     * 
     * @return An execution report instance based on the supplied
     * FIX Message. The returned type will implement
     * {@link FIXMessageSupport}.
     */
    public ExecutionReport createExecutionReport(Message inMessage,
                                                 BrokerID inBrokerID,
                                                 Originator inOriginator,
                                                 UserID inActorID,
                                                 UserID inViewerID);
    /**
     * Creates an order cancel reject message based on the supplied
     * order cancel reject FIX Message.
     *
     * @param inMessage the order cancel FIX Message. Cannot be null.
     *
     * @param inBrokerID the ID of the broker from which this
     * message was received.
     *
     * @param inOriginator the originator of this message. Cannot be null.
     *
     * @param inActorID the ID of the actor user of this
     * message.
     *
     * @param inViewerID the ID of the viewer user of this
     * message.
     * 
     * @return an order cancel reject message wrapping the supplied
     * FIX Message. The returned type will implement
     * {@link FIXMessageSupport}.
     */
    public OrderCancelReject createOrderCancelReject(Message inMessage,
                                                     BrokerID inBrokerID,
                                                     Originator inOriginator,
                                                     UserID inActorID,
                                                     UserID inViewerID);
    /**
     * Creates an ORS response that wraps a generic FIX message which
     * cannot be wrapped by any other FIX Agnostic wrapper.
     *
     * @param inMessage the FIX message. Cannot be null.
     *
     * @param inBrokerID the ID of the broker from which this message
     * was received.
     *
     * @param inOriginator the originator of this message. Cannot be null.
     *
     * @param inActorID the ID of the actor user of this message.
     *
     * @param inViewerID the ID of the viewer user of this message.
     *
     * @return the FIX Response message wrapping the supplied FIX Message.
     */
    public FIXResponse createFIXResponse(Message inMessage,
                                                  BrokerID inBrokerID,
                                                  Originator inOriginator,
                                                  UserID inActorID,
                                                  UserID inViewerID);
}
