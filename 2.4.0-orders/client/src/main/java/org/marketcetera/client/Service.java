package org.marketcetera.client;

import java.math.BigDecimal;
import java.util.Collection;

import javax.jws.WebParam;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;

import org.marketcetera.client.brokers.BrokersStatus;
import org.marketcetera.client.users.UserInfo;
import org.marketcetera.core.position.PositionKey;
import org.marketcetera.core.position.impl.PositionKeyImpl;
import org.marketcetera.trade.*;
import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.ws.stateful.ClientContext;
import org.marketcetera.util.ws.stateful.ServiceBase;
import org.marketcetera.util.ws.wrappers.DateWrapper;
import org.marketcetera.util.ws.wrappers.MapWrapper;
import org.marketcetera.util.ws.wrappers.RemoteException;

/**
 * The application's web services.
 *
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id$
 */

/* $License$ */

@WebService(targetNamespace = "http://marketcetera.org/services")
@ClassVersion("$Id$")
@XmlSeeAlso({PositionKeyImpl.class,Equity.class})
public interface Service
    extends ServiceBase
{

    /**
     * The prefix of the topic on which the client receives server
     * replies.
     */

    public static final String REPLY_TOPIC_PREFIX=
        "ors-messages-"; //$NON-NLS-1$

    /**
     * The topic on which the client receives broker status
     * notifications.
     */

    public static final String BROKER_STATUS_TOPIC=
        "ors-broker-status"; //$NON-NLS-1$

    /**
     * The queue on which the client places orders for the server.
     */

    public static final String REQUEST_QUEUE=
        "ors-commands"; //$NON-NLS-1$

    /**
     * Returns the server's broker status to the client with the
     * given context.
     *
     * @param context The context.
     *
     * @return The status.
     *
     * @throws RemoteException Thrown if the operation cannot be
     * completed.
     */

    BrokersStatus getBrokersStatus
        (@WebParam(name= "context") ClientContext context)
        throws RemoteException;

    /**
     * Returns the information of the user with the given ID to the
     * client with the given context.
     *
     * @param context The context.
     * @param id The user ID.
     *
     * @return The information.
     *
     * @throws RemoteException Thrown if the operation cannot be
     * completed.
     */

    UserInfo getUserInfo
        (@WebParam(name= "context")ClientContext context,
         @WebParam(name= "userID")UserID id)
        throws RemoteException;

    /**
     * Returns all the reports (execution report and order cancel
     * rejects) generated and received by the server since the
     * supplied date to the client with the given context.
     *
     * @param context The context.
     * @param date The date, in UTC.
     *
     * @return The reports.
     *
     * @throws RemoteException Thrown if the operation cannot be
     * completed.
     */

    ReportBaseImpl[] getReportsSince
        (@WebParam(name= "context")ClientContext context,
         @WebParam(name= "date")DateWrapper date)
        throws RemoteException;

    /**
     * Returns the position of the supplied equity instrument based on reports,
     * generated and received on or before the supplied date in UTC to the
     * client with the given context.
     *
     * @param context The context.
     * @param date The date, in UTC.
     * @param equity The equity instrument.
     *
     * @return The position.
     *
     * @throws RemoteException Thrown if the operation cannot be
     * completed.
     */

    BigDecimal getEquityPositionAsOf
        (@WebParam(name= "context")ClientContext context,
         @WebParam(name= "date")DateWrapper date,
         @WebParam(name= "equity")Equity equity)
        throws RemoteException;

    /**
     * Returns all the open positions for equity instruments based on reports,
     * generated and received on or before the supplied date in UTC to the client
     * with the given context.
     *
     * @param context The context.
     * @param date The date, in UTC.
     *
     * @return The open positions.
     *
     * @throws RemoteException Thrown if the operation cannot be
     * completed.
     */

    MapWrapper<PositionKey<Equity>,BigDecimal> getAllEquityPositionsAsOf
        (@WebParam(name= "context")ClientContext context,
         @WebParam(name= "date")DateWrapper date)
        throws RemoteException;
    /**
     * Returns the position of the supplied currency instrument based on reports,
     * generated and received on or before the supplied date in UTC to the
     * client with the given context.
     *
     * @param context The context.
     * @param date The date, in UTC.
     * @param currency The currency instrument.
     *
     * @return The position.
     *
     * @throws RemoteException Thrown if the operation cannot be
     * completed.
     */

    BigDecimal getCurrencyPositionAsOf
        (@WebParam(name= "context")ClientContext context,
         @WebParam(name= "date")DateWrapper date,
         @WebParam(name= "currency")Currency currency)
        throws RemoteException;

    /**
     * Returns all the open positions for currency instruments based on reports,
     * generated and received on or before the supplied date in UTC to the client
     * with the given context.
     *
     * @param context The context.
     * @param date The date, in UTC.
     *
     * @return The open positions.
     *
     * @throws RemoteException Thrown if the operation cannot be
     * completed.
     */

    MapWrapper<PositionKey<Currency>,BigDecimal> getAllCurrencyPositionsAsOf
        (@WebParam(name= "context")ClientContext context,
         @WebParam(name= "date")DateWrapper date)
        throws RemoteException;
    
    /**
     * Returns the position of the supplied future instrument based on reports,
     * generated and received on or before the supplied date in UTC to the
     * client with the given context.
     *
     * @param inContext The context.
     * @param inDate The date, in UTC.
     * @param inFuture The equity instrument.
     *
     * @return The position.
     *
     * @throws RemoteException Thrown if the operation cannot be
     * completed.
     */
    BigDecimal getFuturePositionAsOf(@WebParam(name= "context")ClientContext inContext,
                                     @WebParam(name= "date")DateWrapper inDate,
                                     @WebParam(name= "future")Future inFuture)
        throws RemoteException;
    /**
     * Returns all the open positions for future instruments based on reports,
     * generated and received on or before the supplied date in UTC to the client
     * with the given context.
     *
     * @param inContext The context.
     * @param inDate The date, in UTC.
     *
     * @return The open positions.
     *
     * @throws RemoteException Thrown if the operation cannot be
     * completed.
     */
    MapWrapper<PositionKey<Future>,BigDecimal> getAllFuturePositionsAsOf(@WebParam(name= "context")ClientContext inContext,
                                                                         @WebParam(name= "date")DateWrapper inDate)
            throws RemoteException;
    /**
     * Gets the current aggregate position for the option instrument  based on
     * execution reports received on or before the supplied date in UTC, and which
     * are visible to the given user.
     *
     * <p>
     * Buy trades result in positive positions. All other kinds of trades
     * result in negative positions.
     *
     * @param context The context.
     * @param date The date, in UTC.
     * @param option The option instrument.
     *
     * @return The position.
     *
     * @throws RemoteException Thrown if the operation cannot be completed.
     */
    BigDecimal getOptionPositionAsOf
        (@WebParam(name= "context")ClientContext context,
         @WebParam(name= "date")DateWrapper date,
         @WebParam(name= "option")Option option)
        throws RemoteException;

    /**
     * Returns the aggregate position of each option (option,account,actor)
     * tuple based on all reports received for each tuple on or before
     * the supplied date in UTC, and which are visible to the given user.
     *
     * <p> Buy trades result in positive positions. All other kinds of
     * trades result in negative positions.
     *
     * @param context The context.
     * @param date The date, in UTC.
     *
     * @return The open positions.
     *
     * @throws RemoteException Thrown if the operation cannot be completed.
     */
    MapWrapper<PositionKey<Option>, BigDecimal> getAllOptionPositionsAsOf
         (@WebParam(name= "context")ClientContext context,
          @WebParam(name= "date")DateWrapper date)
         throws RemoteException;

    /**
     * Returns the aggregate position of each option (option,account,actor)
     * tuple based on all reports received for each tuple on or before
     * the supplied date in UTC, and which are visible to the given user.
     * The aggregate positions are only returned for the set of option 
     * root symbols supplied.
     *
     * <p> Buy trades result in positive positions. All other kinds of
     * trades result in negative positions.
     *
     * @param context The context.
     * @param date The date, in UTC.
     * @param rootSymbols The option root symbols.
     *
     * @return The open positions.
     *
     * @throws RemoteException Thrown if the operation cannot be completed.
     */
    MapWrapper<PositionKey<Option>, BigDecimal> getOptionPositionsAsOf
             (@WebParam(name= "context")ClientContext context,
              @WebParam(name= "date")DateWrapper date,
              @WebParam(name= "rootSymbols")String... rootSymbols)
             throws RemoteException;

    /**
     * Returns the next server order ID to the client with the given
     * context.
     *
     * @param context The context.
     *
     * @return The next order ID.
     *
     * @throws RemoteException Thrown if the operation cannot be
     * completed.
     */

    String getNextOrderID
        (@WebParam(name= "context")ClientContext context)
        throws RemoteException;

    /**
     * Returns the underlying symbol for the supplied option root, if
     * a mapping is found for it. Null otherwise.
     *
     * @param context The context.
     * @param optionRoot The option root symbol.
     *
     * @return The underlying symbol for the supplied option root. null, if
     * no mapping was found.
     *
     * @throws RemoteException Thrown if the operation cannot be
     * completed.
     */
    String getUnderlying(@WebParam(name = "context") ClientContext context,
                         @WebParam(name = "optionRoot") String optionRoot)
            throws RemoteException;

    /**
     * Returns the collection of known option roots for the underlying symbol.
     *
     * @param context The context.
     * @param inUnderlying The underlying symbol.
     *
     * @return The sorted collection of option roots if mappings are found for
     * the option root. A null collection is returned otherwise.
     *
     * @throws RemoteException Thrown if the operation cannot be
     * completed.
     */
    Collection<String> getOptionRoots(@WebParam(name = "context") ClientContext context,
                                      @WebParam(name = "underlying") String inUnderlying)
            throws RemoteException;

    /**
     * Sends a heartbeat to the server.
     *
     * @param context The context.
     *
     * @throws RemoteException Thrown if the operation cannot be
     * completed.
     */

    void heartbeat
        (@WebParam(name= "context")ClientContext context)
        throws RemoteException;
    /**
     * Gets the user data associated with the current user. 
     *
     * @param inContext a <code>ClientContent</code> value
     * @return a <code>String</code> value
     * @throws RemoteException if the operation cannot be completed
     */
    String getUserData(@WebParam(name= "context")ClientContext inContext)
            throws RemoteException;
    /**
     * Sets the user data associated with the current user.
     *
     * @param inContext a <code>ClientContent</code> value
     * @param inData a <code>String</code> value 
     * @throws RemoteException if the operation cannot be completed
     */
    void setUserData(@WebParam(name= "context")ClientContext inContext,
                     @WebParam(name = "userData")String inData)
            throws RemoteException;
    /**
     * Adds the given report to the system data flow.
     * 
     * <p>Reports added this way will be added to the system data bus. Reports will be
     * persisted and become part of the system record. All clients will receive this
     * report.
     * 
     * <p><em>This will affect reported positions</em></p>.
     *
     * @param inContext a <code>ClientContent</code> value
     * @param inReport a <code>FIXMessageWrapper</code> value
     * @param inBrokerID a <code>BrokerID</code> value
     * @throws RemoteException if an error occurs
     */
    void addReport(@WebParam(name="context")ClientContext inContext,
                   @WebParam(name="report")FIXMessageWrapper inReport,
                   @WebParam(name="brokerID")BrokerID inBrokerID)
            throws RemoteException;
    /**
     * Removes the given report from the persistent report store.
     * 
     * <p>Reports removed this way will not be added to the system data bus and no clients
     * will receive this report.
     * 
     * <p><em>This will affect reported positions</em></p>.
     *
     * @param inContext a <code>ClientContent</code> value
     * @param inReport an <code>ExecutionReport</code> value
     * @throws RemoteException if an error occurs
     */
    void deleteReport(@WebParam(name="context")ClientContext inContext,
                      @WebParam(name="report")ExecutionReport inReport)
            throws RemoteException;
    /**
     * Resolves the given symbol to an instrument.
     *
     * @param inContext a <code>ClientContext</code> value
     * @param inSymbol a <code>String</code> value
     * @return an <code>Instrument</code> value
     * @throws RemoteException if an error occurs
     */
    Instrument resolveSymbol(@WebParam(name="context")ClientContext inContext,
                             @WebParam(name="symbol")String inSymbol)
            throws RemoteException;
}
