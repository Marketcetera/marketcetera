package org.marketcetera.ors.ws;

import java.math.BigDecimal;
import java.util.Date;

import org.marketcetera.client.Service;
import org.marketcetera.client.brokers.BrokersStatus;
import org.marketcetera.core.CoreException;
import org.marketcetera.core.IDFactory;
import org.marketcetera.ors.brokers.Brokers;
import org.marketcetera.ors.history.ReportHistoryServices;
import org.marketcetera.ors.history.ReportPersistenceException;
import org.marketcetera.persist.PersistenceException;
import org.marketcetera.trade.MSymbol;
import org.marketcetera.trade.ReportBaseImpl;
import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.ws.stateful.ClientContext;
import org.marketcetera.util.ws.stateful.RemoteCaller;
import org.marketcetera.util.ws.stateful.ServiceBaseImpl;
import org.marketcetera.util.ws.stateful.SessionHolder;
import org.marketcetera.util.ws.stateful.SessionManager;
import org.marketcetera.util.ws.wrappers.RemoteException;
import org.marketcetera.util.ws.wrappers.MapWrapper;

/**
 * The implementation of the application's web services.
 *
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$") //$NON-NLS-1$
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

    private ReportBaseImpl[] getReportsSinceImpl
        (Date date)
        throws ReportPersistenceException,
               PersistenceException
    {
        return getHistoryServices().getReportsSince(date);
    }

    private BigDecimal getPositionAsOfImpl
        (Date date,
         MSymbol symbol)
        throws PersistenceException
    {
        return getHistoryServices().getPositionAsOf(date,symbol);
    }

    private MapWrapper<MSymbol,BigDecimal> getPositionsAsOfImpl
        (Date date)
        throws PersistenceException
    {
        return new MapWrapper<MSymbol, BigDecimal>(
                getHistoryServices().getPositionsAsOf(date));
    }

    private String getNextOrderIDImpl()
        throws CoreException
    {
        return getIDFactory().getNext();
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
    public ReportBaseImpl[] getReportsSince
        (ClientContext context,
         final Date date)
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
                return getReportsSinceImpl(date);
            }}).execute(context);
    }

    @Override
    public BigDecimal getPositionAsOf
        (ClientContext context,
         final Date date,
         final MSymbol symbol)
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
                return getPositionAsOfImpl(date,symbol);
            }}).execute(context);
    }

    @Override
    public MapWrapper<MSymbol,BigDecimal> getPositionsAsOf
        (ClientContext context,
         final Date date)
        throws RemoteException
    {
        return (new RemoteCaller<ClientSession,MapWrapper<MSymbol, BigDecimal>>
                (getSessionManager()) {
            @Override
            protected MapWrapper<MSymbol, BigDecimal> call
                (ClientContext context,
                 SessionHolder<ClientSession> sessionHolder)
                throws PersistenceException
            {
                return getPositionsAsOfImpl(date);
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
}
