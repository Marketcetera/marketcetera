package org.marketcetera.client;

import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedList;

import org.marketcetera.client.dest.DestinationStatus;
import org.marketcetera.client.dest.DestinationsStatus;
import org.marketcetera.core.MSymbol;
import org.marketcetera.trade.DestinationID;
import org.marketcetera.trade.MessageCreationException;
import org.marketcetera.trade.ReportBaseImpl;
import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.ws.stateful.ClientContext;
import org.marketcetera.util.ws.stateful.RemoteCaller;
import org.marketcetera.util.ws.stateful.ServiceBaseImpl;
import org.marketcetera.util.ws.stateful.SessionHolder;
import org.marketcetera.util.ws.stateful.SessionManager;
import org.marketcetera.util.ws.wrappers.RemoteException;

/**
 * A test service implementation to aid testing of client via {@link
 * MockServer}.
 * 
 * @author tlerios@marketcetera.com
 * @since $Release$
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$") //$NON-NLS-1$
public class MockServiceImpl
    extends ServiceBaseImpl<Object>
    implements Service
{

    // INSTANCE DATA.

    private int mNextOrderID;


    // CONSTRUCTORS.

    public MockServiceImpl
        (SessionManager<Object> sessionManager)
    {
        super(sessionManager);
    }


    // INSTANCE METHODS.

    private DestinationsStatus getDestinationsStatusImpl()
    {
        LinkedList<DestinationStatus> list=new LinkedList<DestinationStatus>();
        list.add(new DestinationStatus("N1",new DestinationID("ID1"),true));
        list.add(new DestinationStatus("N2",new DestinationID("ID2"),false));
        return new DestinationsStatus(list);
    }

    private ReportBaseImpl[] getReportsSinceImpl
        (Date date)
        throws MessageCreationException
    {
        return sReports;
    }

    private BigDecimal getPositionAsOfImpl
        (Date date,
         MSymbol symbol)
    {
        return new BigDecimal(date.getTime());
    }

    private String getNextOrderIDImpl()
    {
        return ID_PREFIX +(mNextOrderID++);
    }


    // Service.

    @Override
    public DestinationsStatus getDestinationsStatus
        (ClientContext context)
        throws RemoteException
    {
        return (new RemoteCaller<Object,DestinationsStatus>
                (getSessionManager()) {
            @Override
            protected DestinationsStatus call
                (ClientContext context,
                 SessionHolder<Object> sessionHolder)
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
        return (new RemoteCaller<Object,ReportBaseImpl[]>
                (getSessionManager()) {
            @Override
            protected ReportBaseImpl[] call
                (ClientContext context,
                 SessionHolder<Object> sessionHolder)
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
        return (new RemoteCaller<Object,BigDecimal>
                (getSessionManager()) {
            @Override
            protected BigDecimal call
                (ClientContext context,
                 SessionHolder<Object> sessionHolder)
            {
                return getPositionAsOfImpl(date,symbol);
            }}).execute(context);
    }

    @Override
    public String getNextOrderID
        (ClientContext context)
        throws RemoteException
    {
        return (new RemoteCaller<Object,String>
                (getSessionManager()) {
            @Override
            protected String call
                (ClientContext context,
                 SessionHolder<Object> sessionHolder)
            {
                return getNextOrderIDImpl();
            }}).execute(context);
    }

    static final String ID_PREFIX = "MyID";

    // Mocking interface.

    static ReportBaseImpl[] sReports = null;
}
