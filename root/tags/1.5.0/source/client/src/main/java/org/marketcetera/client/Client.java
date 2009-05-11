package org.marketcetera.client;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.trade.*;
import org.marketcetera.client.brokers.BrokersStatus;
import org.marketcetera.client.users.UserInfo;
import org.marketcetera.core.position.PositionKey;

import java.util.Date;
import java.util.Map;
import java.math.BigDecimal;
import java.beans.ExceptionListener;

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
 *      <li>{@link #getPositionAsOf(Date, MSymbol)}  fetch positions} </li> 
 *      <li>{@link #getPositionsAsOf(java.util.Date)}  fetch all open positions} </li> 
 * </ul>
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$")
public interface Client {
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
     * Returns the position of the supplied symbol based on reports,
     * generated and received until the supplied date in UTC.
     *
     * @param inDate the date in UTC. Cannot be null.
     * @param inSymbol The symbol. Cannot be null.
     *
     * @return the current position of the symbol.
     *
     * @throws ConnectionException if there were connection errors fetching
     * data from the server.
     */
    public BigDecimal getPositionAsOf(Date inDate, MSymbol inSymbol)
            throws ConnectionException;

    /**
     * Returns all open positions based on reports,
     * generated and received up until the supplied date in UTC.
     *
     * @param inDate the date in UTC. Cannot be null.
     *
     * @return the open positions. Includes non-zero positions only.
     *
     * @throws ConnectionException if there were connection errors fetching
     * data from the server.
     */
    public Map<PositionKey, BigDecimal> getPositionsAsOf(Date inDate)
            throws ConnectionException;

    /**
     * Adds a report listener. The report listener receives all the reports
     * sent out by the server.
     * <p>
     * If the same listener is added more than once, it will receive
     * notifications as many times as it's been added.
     * <p>
     * The listeners are notified in the reverse order of their addition. 
     *
     * @param inListener The listener instance that should be supplied
     * the reports.
     */
    public void addReportListener(ReportListener inListener);

    /**
     * Removes a report listener that was previously added via
     * {@link #addReportListener(ReportListener)}. If the listener
     * was added more than once, only its most recently added occurrence
     * will be removed. 
     *
     * @param inListener The listener instance that should no longer
     * be receiving the reports.
     */
    public void removeReportListener(ReportListener inListener);

    /**
     * Adds a broker status listener, which receives all the
     * broker status changes sent out by the server.
     *
     * <p>If the same listener is added more than once, it will receive
     * notifications as many times as it has been added.</p>
     *
     * <p>The listeners are notified in the reverse order of their
     * addition.</p>
     *
     * @param listener The listener which should be supplied the
     * broker status changes.
     */
    public void addBrokerStatusListener
        (BrokerStatusListener listener);

    /**
     * Removes a broker status listener that was previously added
     * via {@link
     * #addBrokerStatusListener(BrokerStatusListener)}.
     *
     * <p>If the listener was added more than once, only its most
     * recently added instance will be removed.</p>
     *
     * @param listener The listener which should stop receiving
     * broker status changes.
     */
    public void removeBrokerStatusListener
        (BrokerStatusListener listener);

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
}
