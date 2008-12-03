package org.marketcetera.ors.ws;

import java.math.BigDecimal;
import java.util.Date;
import org.marketcetera.client.Service;
import org.marketcetera.client.dest.DestinationsStatus;
import org.marketcetera.core.CoreException;
import org.marketcetera.core.IDFactory;
import org.marketcetera.core.MSymbol;
import org.marketcetera.ors.dest.Destinations;
import org.marketcetera.ors.history.ReportHistoryServices;
import org.marketcetera.ors.history.ReportPersistenceException;
import org.marketcetera.trade.ReportBaseImpl;
import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.ws.stateful.ClientContext;
import org.marketcetera.util.ws.stateful.RemoteCaller;
import org.marketcetera.util.ws.stateful.ServiceBaseImpl;
import org.marketcetera.util.ws.stateful.SessionHolder;
import org.marketcetera.util.ws.stateful.SessionManager;
import org.marketcetera.util.ws.wrappers.RemoteException;
import org.marketcetera.persist.PersistenceException;

/**
 * The implementation of the application's web services.
 *
 * @author tlerios@marketcetera.com
 * @since $Release$
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$") //$NON-NLS-1$
public class ServiceImpl
    extends ServiceBaseImpl<ClientSession>
    implements Service
{

    // INSTANCE DATA.

    private final Destinations mDestinations;
    private final IDFactory mIDFactory;
    private final ReportHistoryServices mHistoryServices;


    // CONSTRUCTORS.

    /**
     * Creates a new service implementation with the given session
     * manager and destinations.
     *
     * @param sessionManager The session manager, which may be null.
     * @param destinations The destinations.
     * @param inHistoryServices The report history service provider.
     */    

    public ServiceImpl
            (SessionManager<ClientSession> sessionManager,
             Destinations destinations,
             IDFactory idFactory,
             ReportHistoryServices inHistoryServices)
    {
        super(sessionManager);
        mDestinations=destinations;
        mIDFactory=idFactory;
        mHistoryServices=inHistoryServices;
    }


    // INSTANCE METHODS.

    /**
     * Returns the receiver's destinations.
     *
     * @return The destinations.
     */

    private Destinations getDestinations()
    {
        return mDestinations;
    }

    /**
     * Returbs the receiver's ID factory.
     *
     * @return The factory.
     */

    public IDFactory getIDFactory()
    {
        return mIDFactory;
    }


    // Service IMPLEMENTATIONS.

    private DestinationsStatus getDestinationsStatusImpl()
    {
        return getDestinations().getStatus();
    }

    private ReportBaseImpl[] getReportsSinceImpl
        (Date date)
            throws ReportPersistenceException, PersistenceException {
        return mHistoryServices.getReportsSince(date);
    }

    private BigDecimal getPositionAsOfImpl
        (Date date,
         MSymbol symbol) throws PersistenceException {
        return mHistoryServices.getPositionAsOf(date,symbol);
    }

    private String getNextOrderIDImpl()
        throws CoreException
    {
        return getIDFactory().getNext();
    }


    // Service.

    @Override
    public DestinationsStatus getDestinationsStatus
        (ClientContext context)
        throws RemoteException
    {
        return (new RemoteCaller<ClientSession,DestinationsStatus>
                (getSessionManager()) {
            @Override
            protected DestinationsStatus call
                (ClientContext context,
                 SessionHolder<ClientSession> sessionHolder)
            {
                return getDestinationsStatusImpl();
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
                    throws ReportPersistenceException, PersistenceException {
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
                    throws PersistenceException {
                return getPositionAsOfImpl(date,symbol);
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
