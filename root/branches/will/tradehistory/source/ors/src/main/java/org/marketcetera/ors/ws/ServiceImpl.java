package org.marketcetera.ors.ws;

import java.math.BigDecimal;
import java.util.Date;
import org.marketcetera.client.Service;
import org.marketcetera.client.dest.DestinationsStatus;
import org.marketcetera.core.CoreException;
import org.marketcetera.core.IDFactory;
import org.marketcetera.core.MSymbol;
import org.marketcetera.ors.dest.Destinations;
import org.marketcetera.trade.DestinationID;
import org.marketcetera.trade.ExecutionReportImpl;
import org.marketcetera.trade.Factory;
import org.marketcetera.trade.MessageCreationException;
import org.marketcetera.trade.OrderCancelRejectImpl;
import org.marketcetera.trade.Originator;
import org.marketcetera.trade.ReportBaseImpl;
import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.ws.stateful.ClientContext;
import org.marketcetera.util.ws.stateful.RemoteCaller;
import org.marketcetera.util.ws.stateful.ServiceBaseImpl;
import org.marketcetera.util.ws.stateful.SessionHolder;
import org.marketcetera.util.ws.stateful.SessionManager;
import org.marketcetera.util.ws.wrappers.RemoteException;
import quickfix.field.OrigClOrdID;
import quickfix.fix44.ExecutionReport;
import quickfix.fix44.OrderCancelReject;

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


    // CONSTRUCTORS.

    /**
     * Creates a new service implementation with the given session
     * manager and destinations.
     *
     * @param sessionManager The session manager, which may be null.
     * @param destinations The destinations.
     */    

    public ServiceImpl
        (SessionManager<ClientSession> sessionManager,
         Destinations destinations,
         IDFactory idFactory)
    {
        super(sessionManager);
        mDestinations=destinations;
        mIDFactory=idFactory;
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
        throws MessageCreationException
    {
        Factory f=Factory.getInstance();
        DestinationID dID=new DestinationID("me");
        ExecutionReport er=new ExecutionReport();
        er.set(new OrigClOrdID("42"));
        OrderCancelReject ocr=new OrderCancelReject();
        ocr.set(new OrigClOrdID("43"));
        return new ReportBaseImpl[] {
            (ExecutionReportImpl)
            f.createExecutionReport(er,dID,Originator.Server),
            (OrderCancelRejectImpl)
            f.createOrderCancelReject(ocr,dID)
        };
    }

    private BigDecimal getPositionAsOfImpl
        (Date date,
         MSymbol symbol)
    {
        return new BigDecimal(date.getTime());
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
                throws MessageCreationException
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
            {
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
