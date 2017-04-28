package org.marketcetera.client;

import java.beans.ExceptionListener;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.marketcetera.client.brokers.BrokersStatus;
import org.marketcetera.client.users.UserInfo;
import org.marketcetera.core.notifications.ServerStatusListener;
import org.marketcetera.core.position.PositionKey;
import org.marketcetera.event.Event;
import org.marketcetera.trade.BrokerID;
import org.marketcetera.trade.Currency;
import org.marketcetera.trade.Equity;
import org.marketcetera.trade.ExecutionReportImpl;
import org.marketcetera.trade.FIXMessageWrapper;
import org.marketcetera.trade.FIXOrder;
import org.marketcetera.trade.Future;
import org.marketcetera.trade.Hierarchy;
import org.marketcetera.trade.Instrument;
import org.marketcetera.trade.Option;
import org.marketcetera.trade.OrderCancel;
import org.marketcetera.trade.OrderID;
import org.marketcetera.trade.OrderReplace;
import org.marketcetera.trade.OrderSingle;
import org.marketcetera.trade.ReportBase;
import org.marketcetera.trade.ReportBaseImpl;
import org.marketcetera.trade.UserID;
import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.ws.tags.SessionId;

/* $License$ */

/**
 * A client end point that communicates with the server.
 * <p>
 * The client provides the following lifecycle methods.
 * <ul>
 *      <li>{@link #reconnect()}: to reconnect to the server</li>
 *      <li>{@link #reconnect(ClientParameters)}: to reconnect to a server
 *      that is different from the one the client was originally connected
 *      to.</li>
 * </ul>
 * <p>
 * The client provides the following set of services that communicate with
 * the server.
 * <ul>
 *      <li>sendOrder: {@link #sendOrder(OrderSingle)},
 *      {@link #sendOrder(OrderReplace)}, {@link #sendOrder(OrderCancel)},
 *      {@link #sendOrderRaw(FIXOrder)}</li>
 *      <li>{@link #addReportListener(ReportListener) receive reports}</li>
 *      <li>{@link #addBrokerStatusListener(BrokerStatusListener) receive broker status updates}</li>
 *      <li>{@link #getReportsSince(Date) fetch past reports} </li>
 *      <li>{@link #getEquityPositionAsOf(Date, Equity)}  fetch equity position} </li>
 *      <li>{@link #getAllEquityPositionsAsOf(Date)}  fetch all open equity positions} </li>
 *      <li>{@link #getOpenOrders() fetch all visible open orders}</li>
 *      <li>{@link #getOptionPositionAsOf(java.util.Date, Option)}  fetch option position} </li>
 *      <li>{@link #getOptionPositionsAsOf(java.util.Date, String[])}  fetch option positions} </li>
 *      <li>{@link #getAllOptionPositionsAsOf(java.util.Date)}  fetch all open option positions} </li>
 *      <li>{@link #getCurrencyPositionAsOf(Date, Currency)}  fetch currency positions} </li>
 *      <li>{@link #getAllCurrencyPositionsAsOf(java.util.Date)}  fetch all open currency positions} </li>
 *      <li>{@link #getFuturePositionAsOf(Date, Future)}  fetch future positions} </li>
 *      <li>{@link #getAllFuturePositionsAsOf(java.util.Date)}  fetch all open future positions} </li>
 * </ul>
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$")
public interface Client
        extends ReportPublisher,BrokerStatusPublisher
{
    /**
     * Sends the supplied order to the server.
     *
     * @param inOrderSingle The order to send.
     *
     * @throws ConnectionException if there were connection errors sending
     * the order out to the server.
     *
     * @throws OrderValidationException if the order didn't have complete
     * or consistent data to be sent to the server.
     */
    public void sendOrder(OrderSingle inOrderSingle)
            throws ConnectionException, OrderValidationException;
    /**
     * Sends the supplied order to the server.
     *
     * @param inOrderReplace The order to send.
     *
     * @throws ConnectionException if there were connection errors sending
     * the order out to the server.
     *
     * @throws OrderValidationException if the orders didn't have complete
     * or consistent data to be sent to the server.
     */
    public void sendOrder(OrderReplace inOrderReplace)
            throws ConnectionException, OrderValidationException;
    /**
     * Sends the supplied order to the server.
     *
     * @param inOrderCancel The order to send.
     *
     * @throws ConnectionException if there were connection errors sending
     * the order out to the server.
     *
     * @throws OrderValidationException if the orders didn't have complete
     * or consistent data to be sent to the server.
     */
    public void sendOrder(OrderCancel inOrderCancel)
            throws ConnectionException, OrderValidationException;
    /**
     * Sends the supplied FIX Message Order to the server.
     * <p>
     * When supplying raw FIX Message, a brokerID has to be supplied
     *
     * @param inFIXOrder the raw FIX Order to send.
     *
     * @throws ConnectionException if there were connection errors sending
     * the order to the server.
     *
     * @throws OrderValidationException if the supplied message was not of a
     * type that's supported by the system. 
     */
    public void sendOrderRaw(FIXOrder inFIXOrder)
            throws ConnectionException, OrderValidationException;
    /**
     * Send the supplied Event to the server.
     *
     * @param inEvent an <code>Event</code> value
     * @throws ConnectionException if there were connection errors sending the event to the server
     */
    public void sendEvent(Event inEvent)
                throws ConnectionException;
    /**
     * Returns all the reports (execution report and order cancel rejects)
     * generated and received by the server since the supplied date in UTC.
     *
     * @param inDate The date in UTC. Cannot be null.
     *
     * @return All the reports since the supplied date, may be empty.
     *
     * @throws ConnectionException if there were connection errors fetching
     * data from the server.
     */
    public ReportBase[] getReportsSince(Date inDate) throws ConnectionException;

    /**
     * Returns the position of the supplied equity based on reports,
     * generated and received on or before the supplied date in UTC.
     *
     * @param inDate the date in UTC. Cannot be null.
     * @param inEquity The equity. Cannot be null.
     *
     * @return the current position of the equity.
     *
     * @throws ConnectionException if there were connection errors fetching
     * data from the server.
     */
    public BigDecimal getEquityPositionAsOf(Date inDate, Equity inEquity)
            throws ConnectionException;

    /**
     * Returns all open equity positions based on reports,
     * generated and received on or before the supplied date in UTC.
     *
     * @param inDate the date in UTC. Cannot be null.
     *
     * @return the open equity positions. Includes non-zero positions only.
     *
     * @throws ConnectionException if there were connection errors fetching
     * data from the server.
     */
    public Map<PositionKey<Equity>, BigDecimal> getAllEquityPositionsAsOf(Date inDate)
            throws ConnectionException;
    /**
     * Returns the position of the supplied future based on reports,
     * generated and received on or before the supplied date in UTC.
     *
     * @param inDate the date in UTC. Cannot be null.
     * @param inFuture The future. Cannot be null.
     *
     * @return the current position of the future.
     *
     * @throws ConnectionException if there were connection errors fetching
     * data from the server.
     */
    public BigDecimal getFuturePositionAsOf(Date inDate, Future inFuture)
            throws ConnectionException;
    /**
     * Returns all open future positions based on reports,
     * generated and received on or before the supplied date in UTC.
     *
     * @param inDate the date in UTC. Cannot be null.
     *
     * @return the open future positions. Includes non-zero positions only.
     *
     * @throws ConnectionException if there were connection errors fetching
     * data from the server.
     */
    public Map<PositionKey<Future>,BigDecimal> getAllFuturePositionsAsOf(Date inDate)
            throws ConnectionException;
    /**
     * Gets the current aggregate position for the option instrument based on
     * execution reports received on or before the supplied date in UTC, and which
     * are visible to the given user.
     *
     * <p>
     * Buy trades result in positive positions. All other kinds of trades
     * result in negative positions.
     *
     * @param inDate the date in UTC. Cannot be null.
     * @param inOption the option instrument
     *
     * @return the aggregate position for the option.
     *
     * @throws ConnectionException if there were errors retrieving the
     * position.
     */
    public BigDecimal getOptionPositionAsOf(Date inDate, Option inOption)
            throws ConnectionException;
    
    /**
     * Returns the aggregate position of each option (option,account,actor)
     * tuple based on all reports received for each option instrument on or before
     * the supplied date in UTC and which are visible to the given user.
     *
     * <p> Buy trades result in positive positions. All other kinds of
     * trades result in negative positions.
     *
     * @param inDate the date in UTC. Cannot be null.
     *
     * @return the position map.
     *
     * @throws ConnectionException if there were errors retrieving the
     * position map.
     */
    public Map<PositionKey<Option>, BigDecimal> getAllOptionPositionsAsOf(Date inDate)
        throws ConnectionException;

    /**
     * Returns the aggregate position of each option
     * (option,account,actor)
     * tuple based on all reports received for each option instrument on or before
     * the supplied date in UTC, and which are visible to the given user.
     *
     * <p> Buy trades result in positive positions. All other kinds of
     * trades result in negative positions.
     *
     * @param inDate the date in UTC. Cannot be null.
     * @param inRootSymbols the list of option root symbols.
     *
     * @return the position map.
     *
     * @throws ConnectionException if there were errors retrieving the
     * position map.
     */
    public Map<PositionKey<Option>, BigDecimal> getOptionPositionsAsOf(
            Date inDate, String... inRootSymbols)
            throws ConnectionException;    

    /**
     * Returns the position of the supplied currency based on reports,
     * generated and received on or before the supplied date in UTC.
     *
     * @param inDate the date in UTC. Cannot be null.
     * @param inCurrency The currency. Cannot be null.
     *
     * @return the current position of the currency.
     *
     * @throws ConnectionException if there were connection errors fetching
     * data from the server.
     */
    public BigDecimal getCurrencyPositionAsOf(Date inDate, Currency inCurrency)
            throws ConnectionException;

    /**
     * Returns all open currency positions based on reports,
     * generated and received on or before the supplied date in UTC.
     *
     * @param inDate the date in UTC. Cannot be null.
     *
     * @return the open currency positions. Includes non-zero positions only.
     *
     * @throws ConnectionException if there were connection errors fetching
     * data from the server.
     */
    public Map<PositionKey<Currency>, BigDecimal> getAllCurrencyPositionsAsOf(Date inDate)
            throws ConnectionException;
    
    /**
     * Returns the underlying symbol for the supplied option root, if
     * a mapping is found for it. Null otherwise.
     * <p>
     * The mapping is retrieved from the server the first time an
     * option root symbol is specified. The value returned by the server is
     * cached on the client for all subsequent invocations for the
     * same root symbol. All the cached values are cleared when the client
     * is {@link #close() closed}.
     *
     * @param inOptionRoot The option root symbol.
     *
     * @return The underlying symbol for the supplied option root. null, if
     * no mapping was found.
     *
     * @throws ConnectionException if there were errors retrieving the
     * underlying symbol.
     */
    public String getUnderlying(String inOptionRoot)
            throws ConnectionException;

    /**
     * Returns the collection of known option roots for the underlying symbol.
     * <p>
     * The mapping is retrieved from the server the first time an
     * underlying symbol is specified. The value returned by the server is
     * cached on the client for all subsequent invocations for the
     * same underlying symbol. All the cached values are cleared when the client
     * is {@link #close() closed}.
     *
     * @param inUnderlying The underlying symbol.
     *
     * @return The sorted collection of option roots if mappings are found
     * for the option root, null otherwise.
     *
     * @throws ConnectionException if there were errors retrieving the
     * option roots.
     */
    public Collection<String> getOptionRoots(String inUnderlying)
            throws ConnectionException;
    /**
     * Adds a server connection status listener, which receives all
     * the server connection status changes.
     *
     * <p>If the same listener is added more than once, it will receive
     * notifications as many times as it has been added.</p>
     *
     * <p>The listeners are notified in the reverse order of their
     * addition.</p>
     *
     * @param listener The listener which should be supplied the
     * server connection status changes.
     */
    public void addServerStatusListener
        (ServerStatusListener listener);

    /**
     * Removes a server connection status listener that was previously
     * added via {@link
     * #addServerStatusListener(ServerStatusListener)}.
     *
     * <p>If the listener was added more than once, only its most
     * recently added instance will be removed.</p>
     *
     * @param listener The listener which should stop receiving server
     * connection status changes.
     */
    public void removeServerStatusListener
        (ServerStatusListener listener);

    /**
     * Adds an exception listener. The exception listeners are notified
     * whenever the client encounters connectivity issues when communicating
     * with the server.
     * <p>
     * The listeners are notified only when connectivity issues are
     * encountered when sending or receiving messages, ie. when any of
     * the <code>send*()</code> methods are invoked, or when the
     * client receives a message and encounters errors processing it
     * before delivering it to {@link ReportListener} or {@link
     * BrokerStatusListener}, or when client heartbeats cannot reach
     * the server.
     * <p>
     * If the same listener is added more than once, it will receive
     * notifications as many times as it's been added.
     * <p>
     * The listeners are notified in the reverse order of their addition.
     *
     * @param inListener the listener instance.
     */
    public void addExceptionListener(ExceptionListener inListener);

    /**
     * Removes exception listener that was previously added via
     * {@link #addExceptionListener(java.beans.ExceptionListener)}. The
     * listener will stop receiving exception notifications after this
     * method returns.
     * If the listener was added more than once, only its most
     * recently added occurrence will be removed. 
     *
     * @param inListener The exception listener that should no longer
     */
    public void removeExceptionListener(ExceptionListener inListener);
    /**
     * Closes the connection to the server. The behavior of any of
     * the methods of this class after this method is invoked is undefined.
     */
    public void close();

    /**
     * Disconnects the connection to the server and reconnects back
     * using the same properties as were supplied when creating this
     * instance.
     *
     * @throws ConnectionException if there were errors reconnecting.
     */
    public void reconnect() throws ConnectionException;

    /**
     * Disconnects the connection to the server and reconnects back
     * using the properties supplied to this method.
     *
     * @param inParameters The parameters to use when reconnecting to the
     * server. These parameters are stored so that subsequent invocations
     * of {@link #reconnect()} will use these parameters instead of the
     * ones that were supplied when creating this instance.
     * 
     * @throws ConnectionException if there were errors reconnecting.
     */
    public void reconnect(ClientParameters inParameters) throws ConnectionException;

    /**
     * Returns the parameters that client is using to connect to the server.
     *
     * @return the parameters used by the client to connect to the server.
     */
    public ClientParameters getParameters();

    /**
     * Returns the last time the client was successfully connected
     * or reconnected to the server.
     *
     * @return the last time the client was successfully connected
     * or reconnected to the server.
     */
    public Date getLastConnectTime();
    
    /**
     * Returns the server's broker status.
     *
     * @return The status.
     *
     * @throws ConnectionException Thrown if the operation cannot be
     * completed.
     */

    BrokersStatus getBrokersStatus() throws ConnectionException;

    /**
     * Returns the information of the user with the given ID. A local
     * cache can be used to avoid frequent server roundtrips, but it
     * may return stale information. The cache is updated whether or
     * not it was used for retrieval.
     * <p>
     * All cached values are cleared when the client is {@link #close() closed}.
     *
     * @param id The user ID.
     * @param useCache True if the local cache should be used.
     *
     * @return The information.
     *
     * @throws ConnectionException Thrown if the operation cannot be
     * completed.
     */

    UserInfo getUserInfo(UserID id,
                         boolean useCache) throws ConnectionException;

    /**
     * Returns true if the supplied user name, password match the
     * credentials used to connect to the server.
     * <p>
     * This method returns false if the client is not connected to
     * the server.
     *
     * @param inUsername the username
     * @param inPassword the password
     *
     * @return true, if the supplied credentials match the ones used to
     * authenticate to the server and the client is connected to the server,
     * false otherwise.
     */
    boolean isCredentialsMatch(String inUsername, char[] inPassword);

    /**
     * Returns true if client has a live connection to the server.
     *
     * @return true, if the connection to the server is alive.
     */
    boolean isServerAlive();
    /**
     * Sets the user data associated with the current user.
     *
     * @param inProperties a <code>Properties</code> value
     * @throws ConnectionException if an error occurs connecting to the server
     */
    void setUserData(Properties inProperties)
            throws ConnectionException;
    /**
     * Gets the user data associated with the current user.
     *
     * @return a <code>Properties</code> value
     * @throws ConnectionException if an error occurs connecting to the server
     */
    Properties getUserData()
            throws ConnectionException;
    /**
     * Adds the given report to the system data flow.
     * 
     * <p>Reports added this way will be added to the system data bus. Reports will be
     * persisted and become part of the system record. All clients will receive this
     * report.
     * 
     * <p><em>This will affect reported positions</em></p>.
     *
     * @param inReport a <code>FIXMessageWrapper</code> value
     * @param inBrokerID a <code>BrokerID</code> value
     * @param inHierarchy a <code>Hierarchy</code> value
     * @throws ConnectionException if an error occurs connecting to the server
     */
    void addReport(FIXMessageWrapper inReport,
                   BrokerID inBrokerID,
                   Hierarchy inHierarchy)
            throws ConnectionException;
    /**
     * Removes the given report from the persistent report store.
     * 
     * <p>Reports removed this way will not be added to the system data bus and no clients
     * will receive this report.
     * 
     * <p><em>This will affect reported positions</em></p>.
     *
     * @param inReport an <code>ExecutionReportImpl</code> value
     * @throws ConnectionException if an error occurs connecting to the server
     */
    void deleteReport(ExecutionReportImpl inReport)
            throws ConnectionException;
    /**
     * Resolves the given symbol to an <code>Instrument</code>.
     *
     * @param inSymbol a <code>String</code> value
     * @return an <code>Instrument</code> value
     * @throws ConnectionException if an error occurs connecting to the server
     */
    Instrument resolveSymbol(String inSymbol)
            throws ConnectionException;
    /**
     * Gets all open orders visible to the current user.
     *
     * @return a <code>List&lt;ReportBaseImpl&gt;</code> value
     * @throws ConnectionException if an error occurs connecting to the server
     */
    List<ReportBaseImpl> getOpenOrders()
            throws ConnectionException;
    /**
     * Find the root order ID for the order chain of the given order ID.
     *
     * @param inOrderID an <code>OrderID</code> value
     * @return an <code>OrderID</code> value
     */
    public OrderID findRootOrderIdFor(OrderID inOrderID);
    /**
     * Get the session id for the current session.
     *
     * @return a <code>SessionId</code> value
     */
    SessionId getSessionId();
    /**
     * Add the given order modifier.
     *
     * @param inOrderModifier an <code>OrderModifier</code> value
     */
    void addOrderModifier(OrderModifier inOrderModifier);
    /**
     * Remove the given order modifier.
     *
     * @param inOrderModifier an <code>OrderModifier</code> value
     */
    void removeOrderModifier(OrderModifier inOrderModifier);
}
