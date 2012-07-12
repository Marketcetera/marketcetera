/* $License$ */

/**
 * <p>
 *     Defines various trading messages that can be sent to and received from
 *     the system's trading server. These messages can be created using
 *     the {@link org.marketcetera.trade.Factory}.
 * </p>
 * <p>
 *     The goal of the messages in this package is to enable a user
 *     to be able to send orders and receive reports without having to worry
 *     about the actual protocol or if the protocol is FIX, the actual version
 *     of FIX used by the broker.
 * </p>
 * <p>
 *     There are two types of messages that can be exchanged with the trading
 *     server.
 * </p>
 * <ol>
 *     <li>Orders</li>
 *     <li>Reports</li>
 * </ol>
 * <p>
 *     These messages can either be specified in the protocol that is used
 *     by the broker or they can be specified via various types
 *     defined in this package.
 * </p>
 * <h3>Types</h3>
 * <p>
 *     It's recommended that java types defined in this package be used for sending
 *     and receiving the messages to the system's server. However, since FIX
 *     is the most prevalent protocol for communicating with the brokers and
 *     the types in this package support the most common but a limited set
 *     of features supported by FIX, mechanisms are provided to
 *     interact with FIX brokers using FIX messages.
 * </p>
 * <p>
 *     The types created by the {@link org.marketcetera.trade.Factory factory}
 *     will implement the {@link org.marketcetera.trade.FIXMessageSupport}
 *     interface, if they are wrapping a FIX Message.
 *     While using the API, any message type
 *     can be queried to find out if it implements the <code>FIXMessageSupport</code>
 *     interface and if it does, they can obtain a reference to the underlying
 *     FIX message and manipulate it directly.
 *     Typically, all the reports wrap a FIX Message, however this may not be
 *     true if the broker doesn't use FIX for communications,
 *     which might be rare but not impossible.
 * </p>
 * <p>
 *     The downside of using FIX Messages directly is that one ends up depending
 *     on the exact format of messages expected and provided by the
 *     broker and one may need to modify the code to be able to
 *     integrate with a different broker.
 * </p>
 * <p>
 *     Whereas the benefit of using java types defined in this package
 *     is that the system will ensure that those messages can be exchanged with
 *     any brokerss that the server is integrated with. However
 *     it may be necessary to use the FIX messages if one wants to use
 *     FIX features that are not exposed via the types provided in this package.
 * </p>
 * <p>
 *     Do note that orders support a
 *     {@link org.marketcetera.trade.OrderBase#getCustomFields()} attribute that
 *     can be used to set FIX fields that are not directly supported by the
 *     order types defined in this package.
 * </p>
 * <h3>Orders</h3>
 * <p>
 *     The following kinds of orders are supported to trade securities.
 * </p>
 * <ol>
 *     <li>{@link org.marketcetera.trade.OrderSingle New Orders}</li>
 *     <li>{@link org.marketcetera.trade.OrderReplace Replacement Orders}</li>
 *     <li>{@link org.marketcetera.trade.OrderCancel Cancel Orders}</li>
 *     <li>{@link org.marketcetera.trade.OrderSingleSuggestion New Order Suggestion}</li>
 * </ol>
 * <p>
 *     Users are encouraged to use one of the above-mentioned types of the
 *     orders as the system will be able to send such orders to any connected
 *     broker, after translating them to the messaging protocol
 *     supported by the broker.
 * </p>
 * <p>
 *     Since the set of order features exposed via the above-mentioned order
 *     types is limited, a mechanism to create orders based on raw FIX messages
 *     is {@link org.marketcetera.trade.Factory#createOrder(quickfix.Message,org.marketcetera.trade.BrokerID) provided}.
 *     Do note that if this mechanism is used, the order may fail
 *     if it doesn't exactly match the FIX version used by the
 *     broker or if it doesn't contain the set of fields that
 *     the broker requires to be set.
 * </p>
 * <h3>Reports</h3>
 * <p>
 *     The reports corresponding to the orders sent to the broker
 *     are wrapped in one of the following types, by the system, to enable
 *     users to process them without having to concern themselves with the
 *     exact format of reports provided by the broker.
 * </p>
 * <ol>
 *     <li>{@link org.marketcetera.trade.ExecutionReport Execution Reports}</li>
 *     <li>{@link org.marketcetera.trade.OrderCancelReject Cancellation Rejects}</li>
 * </ol>
 * <h3>Unique IDs</h3>
 * <p>
 *     Both Orders and Reports are assigned unique IDs within the system. The
 *     {@link org.marketcetera.trade.Factory} assigns unique OrderIDs to all
 *     the orders when they are created by it. The reports when initially created
 *     have their ID set to a null value. An ID is assigned to them by the server
 *     when they are persisted.
 * </p>
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.0.0
 */
@XmlSchema (namespace = "http://marketcetera.org/types/trade")
package org.marketcetera.trade;

import javax.xml.bind.annotation.XmlSchema;