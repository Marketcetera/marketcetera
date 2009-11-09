package org.marketcetera.ors.ws;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Collection;

import org.marketcetera.client.Service;
import org.marketcetera.client.brokers.BrokersStatus;
import org.marketcetera.client.users.UserInfo;
import org.marketcetera.core.CoreException;
import org.marketcetera.core.IDFactory;
import org.marketcetera.core.position.PositionKey;
import org.marketcetera.ors.brokers.Brokers;
import org.marketcetera.ors.history.ReportHistoryServices;
import org.marketcetera.ors.history.ReportPersistenceException;
import org.marketcetera.ors.security.SimpleUser;
import org.marketcetera.ors.security.SingleSimpleUserQuery;
import org.marketcetera.ors.OptionRootUnderlyingMap;
import org.marketcetera.persist.PersistenceException;
import org.marketcetera.trade.*;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.ws.stateful.ClientContext;
import org.marketcetera.util.ws.stateful.RemoteCaller;
import org.marketcetera.util.ws.stateful.RemoteRunner;
import org.marketcetera.util.ws.stateful.ServiceBaseImpl;
import org.marketcetera.util.ws.stateful.SessionHolder;
import org.marketcetera.util.ws.stateful.SessionManager;
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
        return new UserInfo
            (u.getName(),u.getUserID(),u.isActive(),u.isSuperuser());
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

    private BigDecimal getPositionAsOfImpl
        (ClientSession session,
         Date date,
         Equity equity)
        throws PersistenceException
    {
        return getHistoryServices().getPositionAsOf
            (session.getUser(),date,equity);
    }

    private MapWrapper<PositionKey<Equity>,BigDecimal> getPositionsAsOfImpl
        (ClientSession session,
         Date date)
        throws PersistenceException
    {
        return new MapWrapper<PositionKey<Equity>, BigDecimal>(
                getHistoryServices().getPositionsAsOf(session.getUser(),date));
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
    public BigDecimal getPositionAsOf
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
                return getPositionAsOfImpl
                    (sessionHolder.getSession(),date.getRaw(),equity);
            }}).execute(context);
    }

    @Override
    public MapWrapper<PositionKey<Equity>,BigDecimal> getPositionsAsOf
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
                return getPositionsAsOfImpl
                    (sessionHolder.getSession(),date.getRaw());
            }}).execute(context);
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
}
