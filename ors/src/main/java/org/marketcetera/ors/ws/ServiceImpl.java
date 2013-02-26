package org.marketcetera.ors.ws;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;

import org.marketcetera.client.Service;
import org.marketcetera.client.brokers.BrokersStatus;
import org.marketcetera.client.users.UserInfo;
import org.marketcetera.core.CoreException;
import org.marketcetera.core.IDFactory;
import org.marketcetera.core.Util;
import org.marketcetera.core.position.PositionKey;
import org.marketcetera.ors.OptionRootUnderlyingMap;
import org.marketcetera.ors.brokers.Brokers;
import org.marketcetera.ors.history.ReportHistoryServices;
import org.marketcetera.ors.history.ReportPersistenceException;
import org.marketcetera.ors.security.SimpleUser;
import org.marketcetera.ors.security.SingleSimpleUserQuery;
import org.marketcetera.persist.PersistenceException;
import org.marketcetera.trade.*;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.ws.stateful.*;
import org.marketcetera.util.ws.wrappers.DateWrapper;
import org.marketcetera.util.ws.wrappers.MapWrapper;
import org.marketcetera.util.ws.wrappers.RemoteException;

/**
 * The implementation of the application's web services.
 *
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
public class ServiceImpl
    extends ServiceBaseImpl<ClientSession>
    implements Service
{

    // INSTANCE DATA.

    private final Brokers mBrokers;
    private final IDFactory mIDFactory;
    private final ReportHistoryServices mHistoryServices;


    // CONSTRUCTORS.

    /**
     * Creates a new service implementation with the given session
     * manager, brokers, and report history services provider.
     *
     * @param sessionManager The session manager, which may be null.
     * @param brokers The brokers.
     * @param idFactory the ID factory.
     * @param historyServices The report history services provider.
     */    

    public ServiceImpl
        (SessionManager<ClientSession> sessionManager,
         Brokers brokers,
         IDFactory idFactory,
         ReportHistoryServices historyServices)
    {
        super(sessionManager);
        mBrokers=brokers;
        mIDFactory=idFactory;
        mHistoryServices=historyServices;
    }


    // INSTANCE METHODS.

    /**
     * Returns the receiver's brokers.
     *
     * @return The brokers.
     */

    private Brokers getBrokers()
    {
        return mBrokers;
    }

    /**
     * Returns the receiver's ID factory.
     *
     * @return The factory.
     */

    public IDFactory getIDFactory()
    {
        return mIDFactory;
    }

    /**
     * Returns the receiver's report history services provider.
     *
     * @return The provider.
     */

    public ReportHistoryServices getHistoryServices()
    {
        return mHistoryServices;
    }


    // Service IMPLEMENTATIONS.

    private BrokersStatus getBrokersStatusImpl()
    {
        return getBrokers().getStatus();
    }

    private UserInfo getUserInfoImpl
        (UserID id)
        throws PersistenceException
    {
        SimpleUser u=(new SingleSimpleUserQuery(id.getValue())).fetch();
        return new UserInfo(u.getName(),u.getUserID(),u.isActive(),u.isSuperuser(),Util.propertiesFromString(u.getUserData()));
    }

    private ReportBaseImpl[] getReportsSinceImpl
        (ClientSession session,
         Date date)
        throws ReportPersistenceException,
               PersistenceException
    {
        return getHistoryServices().getReportsSince
            (session.getUser(),date);
    }

    private BigDecimal getEquityPositionAsOfImpl
        (ClientSession session,
         Date date,
         Equity equity)
        throws PersistenceException
    {
        return getHistoryServices().getEquityPositionAsOf
            (session.getUser(),date,equity);
    }

    private MapWrapper<PositionKey<Equity>,BigDecimal> getAllEquityPositionsAsOfImpl
        (ClientSession session,
         Date date)
        throws PersistenceException
    {
        return new MapWrapper<PositionKey<Equity>, BigDecimal>(
                getHistoryServices().getAllEquityPositionsAsOf(session.getUser(),date));
    }
    
	private BigDecimal getCurrencyPositionAsOfImpl(ClientSession session,
			Date date, Currency currency) throws PersistenceException {
		return getHistoryServices().getCurrencyPositionAsOf(session.getUser(),
				date, currency);
	}

	private MapWrapper<PositionKey<Currency>, BigDecimal> getAllCurrencyPositionsAsOfImpl(
			ClientSession session, Date date) throws PersistenceException {
		return new MapWrapper<PositionKey<Currency>, BigDecimal>(
				getHistoryServices().getAllCurrencyPositionsAsOf(
						session.getUser(), date));
	}
    
    /**
     * Gets the position for the given future.
     *
     * @param inSession a <code>ClientSession</code> value
     * @param inDate a <code>Date</code> value
     * @param inFuture a <code>Future</code> value
     * @return a <code>BigDecimal</code> value
     * @throws PersistenceException if an error occurs
     */
    private BigDecimal getFuturePositionAsOfImpl(ClientSession inSession,
                                                 Date inDate,
                                                 Future inFuture)
            throws PersistenceException
    {
        return getHistoryServices().getFuturePositionAsOf(inSession.getUser(),
                                                          inDate,
                                                          inFuture);
    }
    /**
     * Gets all future positions as of the given date.
     *
     * @param inSession a <code>ClientSession</code> value
     * @param inDate a <code>Date</code> value
     * @return a <code>MapWrapper&lt;PositionKey&lt;Future&gt;,BigDecimal&gt;</code> value
     * @throws PersistenceException if an error occurs
     */
    private MapWrapper<PositionKey<Future>,BigDecimal> getAllFuturePositionsAsOfImpl(ClientSession inSession,
                                                                                     Date inDate)
            throws PersistenceException
    {
        return new MapWrapper<PositionKey<Future>,BigDecimal>(getHistoryServices().getAllFuturePositionsAsOf(inSession.getUser(),
                                                                                                             inDate));
    }
    private BigDecimal getOptionPositionAsOfImpl
    (ClientSession session,
     Date date,
     Option inOption) throws PersistenceException
     {
        return getHistoryServices().getOptionPositionAsOf(session.getUser(),
                                                          date, inOption);
     }
    private MapWrapper<PositionKey<Option>, BigDecimal> getAllOptionPositionsAsOfImpl
        (ClientSession session,
         Date date)
        throws PersistenceException
    {
        return new MapWrapper<PositionKey<Option>, BigDecimal>(
                getHistoryServices().getAllOptionPositionsAsOf(session.getUser(),
                        date));
    }

    private MapWrapper<PositionKey<Option>, BigDecimal> getOptionPositionsAsOfImpl
        (ClientSession session,
         Date date,
         String... symbols)
        throws PersistenceException
    {
        return new MapWrapper<PositionKey<Option>, BigDecimal>(
                getHistoryServices().getOptionPositionsAsOf(
                        session.getUser(), date, symbols));
    }

    private String getNextOrderIDImpl()
        throws CoreException
    {
        return getIDFactory().getNext();
    }

    private String getUnderlyingImpl
        (String inOptionRoot)
    {
        OptionRootUnderlyingMap map = OptionRootUnderlyingMap.getInstance();
        return map == null? null: map.getUnderlying(inOptionRoot);
    }

    private Collection<String> getOptionRootsImpl
        (String inUnderlying)
    {
        OptionRootUnderlyingMap map = OptionRootUnderlyingMap.getInstance();
        return map == null? null: map.getOptionRoots(inUnderlying);
    }
    /**
     * Gets the user data associated with the given username.
     *
     * @param inUsername a <code>String</code> value
     * @return a <code>String</code> value
     * @throws PersistenceException if an error occurs retrieving the user data
     */
    private String getUserDataImpl(String inUsername)
            throws PersistenceException
    {
        return new SingleSimpleUserQuery(inUsername).fetch().getUserData();
    }
    /**
     * Sets the user data associated with the given username.
     *
     * @param inUsername a <code>String</code> value
     * @param inUserData a <code>String</code> value
     * @throws PersistenceException if an error occurs saving the user data
     */
    private void setUserDataImpl(String inUsername,
                                 String inUserData)
            throws PersistenceException
    {
        SimpleUser user = new SingleSimpleUserQuery(inUsername).fetch();
        user.setUserData(inUserData);
        user.save();
    }
    // Service.

    @Override
    public BrokersStatus getBrokersStatus
        (ClientContext context)
        throws RemoteException
    {
        return (new RemoteCaller<ClientSession,BrokersStatus>
                (getSessionManager()) {
            @Override
            protected BrokersStatus call
                (ClientContext context,
                 SessionHolder<ClientSession> sessionHolder)
            {
                return getBrokersStatusImpl();
            }}).execute(context);
    }

    @Override
    public UserInfo getUserInfo
        (ClientContext context,
         final UserID id)
        throws RemoteException
    {
        return (new RemoteCaller<ClientSession,UserInfo>
                (getSessionManager()) {
            @Override
            protected UserInfo call
                (ClientContext context,
                 SessionHolder<ClientSession> sessionHolder)
                throws PersistenceException
            {
                return getUserInfoImpl(id);
            }}).execute(context);
    }

    @Override
    public ReportBaseImpl[] getReportsSince
        (ClientContext context,
         final DateWrapper date)
        throws RemoteException
    {
        return (new RemoteCaller<ClientSession,ReportBaseImpl[]>
                (getSessionManager()) {
            @Override
            protected ReportBaseImpl[] call
                (ClientContext context,
                 SessionHolder<ClientSession> sessionHolder)
                throws ReportPersistenceException,
                       PersistenceException
            {
                return getReportsSinceImpl
                    (sessionHolder.getSession(),date.getRaw());
            }}).execute(context);
    }

    @Override
    public BigDecimal getEquityPositionAsOf
        (ClientContext context,
         final DateWrapper date,
         final Equity equity)
        throws RemoteException
    {
        return (new RemoteCaller<ClientSession,BigDecimal>
                (getSessionManager()) {
            @Override
            protected BigDecimal call
                (ClientContext context,
                 SessionHolder<ClientSession> sessionHolder)
                throws PersistenceException
            {
                return getEquityPositionAsOfImpl
                    (sessionHolder.getSession(),date.getRaw(),equity);
            }}).execute(context);
    }

    @Override
    public MapWrapper<PositionKey<Equity>,BigDecimal> getAllEquityPositionsAsOf
        (ClientContext context,
         final DateWrapper date)
        throws RemoteException
    {
        return (new RemoteCaller<ClientSession,MapWrapper<PositionKey<Equity>,
                                                          BigDecimal>>
                (getSessionManager()) {
            @Override
            protected MapWrapper<PositionKey<Equity>,BigDecimal> call
                (ClientContext context,
                 SessionHolder<ClientSession> sessionHolder)
                throws PersistenceException
            {
                return getAllEquityPositionsAsOfImpl
                    (sessionHolder.getSession(),date.getRaw());
            }}).execute(context);
    }
   
    @Override
    public BigDecimal getCurrencyPositionAsOf
        (ClientContext context,
         final DateWrapper date,
         final Currency currency)
        throws RemoteException
    {
        return (new RemoteCaller<ClientSession,BigDecimal>
                (getSessionManager()) {
            @Override
            protected BigDecimal call
                (ClientContext context,
                 SessionHolder<ClientSession> sessionHolder)
                throws PersistenceException
            {
                return getCurrencyPositionAsOfImpl
                    (sessionHolder.getSession(),date.getRaw(),currency);
            }}).execute(context);
    }

    @Override
    public MapWrapper<PositionKey<Currency>,BigDecimal> getAllCurrencyPositionsAsOf
        (ClientContext context,
         final DateWrapper date)
        throws RemoteException
    {
        return (new RemoteCaller<ClientSession,MapWrapper<PositionKey<Currency>,
                                                          BigDecimal>>
                (getSessionManager()) {
            @Override
            protected MapWrapper<PositionKey<Currency>,BigDecimal> call
                (ClientContext context,
                 SessionHolder<ClientSession> sessionHolder)
                throws PersistenceException
            {
                return getAllCurrencyPositionsAsOfImpl
                    (sessionHolder.getSession(),date.getRaw());
            }}).execute(context);
    }
    
    /* (non-Javadoc)
     * @see org.marketcetera.client.Service#getAllFuturePositionsAsOf(org.marketcetera.util.ws.stateful.ClientContext, org.marketcetera.util.ws.wrappers.DateWrapper)
     */
    @Override
    public MapWrapper<PositionKey<Future>, BigDecimal> getAllFuturePositionsAsOf(ClientContext inContext,
                                                                                 final DateWrapper inDate)
            throws RemoteException
    {
        return (new RemoteCaller<ClientSession,MapWrapper<PositionKey<Future>,BigDecimal>>(getSessionManager()) {
            @Override
            protected MapWrapper<PositionKey<Future>,BigDecimal> call(ClientContext context,
                                                                      SessionHolder<ClientSession> sessionHolder)
                     throws PersistenceException
             {
                return getAllFuturePositionsAsOfImpl(sessionHolder.getSession(),
                                                     inDate.getRaw());
             }}).execute(inContext);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.Service#getFuturePositionAsOf(org.marketcetera.util.ws.stateful.ClientContext, org.marketcetera.util.ws.wrappers.DateWrapper, org.marketcetera.trade.Future)
     */
    @Override
    public BigDecimal getFuturePositionAsOf(ClientContext inContext,
                                            final DateWrapper inDate,
                                            final Future inFuture)
            throws RemoteException
    {
        return (new RemoteCaller<ClientSession,BigDecimal>(getSessionManager()) {
            @Override
            protected BigDecimal call(ClientContext context,
                                      SessionHolder<ClientSession> sessionHolder)
                    throws PersistenceException
            {
                return getFuturePositionAsOfImpl(sessionHolder.getSession(),
                                                 inDate.getRaw(),
                                                 inFuture);
            }}).execute(inContext);
    }
    @Override
    public BigDecimal getOptionPositionAsOf
        (ClientContext context,
         final DateWrapper date,
         final Option inOption)
        throws RemoteException
    {
        return (new RemoteCaller<ClientSession,BigDecimal>
                (getSessionManager()) {
            @Override
            protected BigDecimal call
                (ClientContext context,
                 SessionHolder<ClientSession> sessionHolder)
                throws PersistenceException
            {
                return getOptionPositionAsOfImpl
                    (sessionHolder.getSession(),date.getRaw(),
                            inOption);
            }}).execute(context);
    }

    @Override
    public MapWrapper<PositionKey<Option>, BigDecimal> getAllOptionPositionsAsOf
        (ClientContext context,
         final DateWrapper date)
        throws RemoteException
    {
        return (new RemoteCaller<ClientSession,MapWrapper<PositionKey<Option>,
                                                          BigDecimal>>
                (getSessionManager()) {
            @Override
            protected MapWrapper<PositionKey<Option>,BigDecimal> call
                (ClientContext context,
                 SessionHolder<ClientSession> sessionHolder)
                throws PersistenceException
            {
                return getAllOptionPositionsAsOfImpl
                    (sessionHolder.getSession(),date.getRaw());
            }}).execute(context);
    }

    @Override
    public MapWrapper<PositionKey<Option>, BigDecimal> getOptionPositionsAsOf
        (ClientContext context,
         final DateWrapper date,
         final String... rootSymbols)
        throws RemoteException
    {
        return (new RemoteCaller<ClientSession,MapWrapper<PositionKey<Option>,
                                                          BigDecimal>>
                (getSessionManager()) {
            @Override
            protected MapWrapper<PositionKey<Option>,BigDecimal> call
                (ClientContext context,
                 SessionHolder<ClientSession> sessionHolder)
                throws PersistenceException
            {
                return getOptionPositionsAsOfImpl
                    (sessionHolder.getSession(),date.getRaw(), rootSymbols);
            }}).execute(context);
    }


    @Override
    public String getNextOrderID
        (ClientContext context)
        throws RemoteException
    {
        return (new RemoteCaller<ClientSession,String>
                (getSessionManager()) {
            @Override
            protected String call
                (ClientContext context,
                 SessionHolder<ClientSession> sessionHolder)
                throws CoreException
            {
                return getNextOrderIDImpl();
            }}).execute(context);
    }

    @Override
    public String getUnderlying
            (ClientContext context,
             final String optionRoot)
            throws RemoteException
    {
        return (new RemoteCaller<ClientSession,String>
                (getSessionManager()) {
            @Override
            protected String call
                (ClientContext context,
                 SessionHolder<ClientSession> sessionHolder)
                throws CoreException
            {
                return getUnderlyingImpl(optionRoot);
            }}).execute(context);
    }

    @Override
    public Collection<String> getOptionRoots
            (ClientContext context,
             final String underlying)
            throws RemoteException
    {
        return (new RemoteCaller<ClientSession,Collection<String>>
                (getSessionManager()) {
            @Override
            protected Collection<String> call
                (ClientContext context,
                 SessionHolder<ClientSession> sessionHolder)
                throws CoreException
            {
                return getOptionRootsImpl(underlying);
            }}).execute(context);
    }

    @Override
    public void heartbeat
        (ClientContext context)
        throws RemoteException
    {
        (new RemoteRunner<ClientSession>
         (getSessionManager()) {
            @Override
            protected void run
                (ClientContext context,
                 SessionHolder<ClientSession> sessionHolder)
            {
                // The enclosing RemoteRunner takes care of marking
                // the session as active.
                SLF4JLoggerProxy.debug
                    (this,"Received heartbeat for: {}", //$NON-NLS-1$
                     context.getSessionId());
            }}).execute(context);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.Service#getUserData(org.marketcetera.util.ws.stateful.ClientContext)
     */
    @Override
    public String getUserData(final ClientContext inContext)
            throws RemoteException
    {
        String userData = (new RemoteCaller<ClientSession,String>(getSessionManager()) {
            @Override
            protected String call(ClientContext context,
                                  SessionHolder<ClientSession> sessionHolder)
                    throws CoreException, PersistenceException
            {
                return getUserDataImpl(getSessionManager().get(inContext.getSessionId()).getSession().getUser().getName());
        }}).execute(inContext);
        return userData;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.Service#setUserData(org.marketcetera.util.ws.stateful.ClientContext, java.util.Properties)
     */
    @Override
    public void setUserData(final ClientContext inContext,
                            final String inData)
            throws RemoteException
    {
        (new RemoteRunner<ClientSession>(getSessionManager()) {
            @Override
            protected void run(ClientContext context,
                               SessionHolder<ClientSession> sessionHolder)
                    throws PersistenceException
            {
                setUserDataImpl(getSessionManager().get(inContext.getSessionId()).getSession().getUser().getName(),
                                inData);
            }}).execute(inContext);
    }
}
