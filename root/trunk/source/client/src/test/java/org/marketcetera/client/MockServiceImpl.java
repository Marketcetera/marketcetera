package org.marketcetera.client;

import java.math.BigDecimal;
import java.util.*;

import org.marketcetera.client.brokers.BrokerStatus;
import org.marketcetera.client.brokers.BrokersStatus;
import org.marketcetera.client.users.UserInfo;
import org.marketcetera.core.position.PositionKey;
import org.marketcetera.core.position.impl.PositionKeyImpl;
import org.marketcetera.trade.BrokerID;
import org.marketcetera.trade.MSymbol;
import org.marketcetera.trade.MessageCreationException;
import org.marketcetera.trade.ReportBaseImpl;
import org.marketcetera.trade.UserID;
import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.ws.stateful.ClientContext;
import org.marketcetera.util.ws.stateful.RemoteCaller;
import org.marketcetera.util.ws.stateful.ServiceBaseImpl;
import org.marketcetera.util.ws.stateful.SessionHolder;
import org.marketcetera.util.ws.stateful.SessionManager;
import org.marketcetera.util.ws.wrappers.RemoteException;
import org.marketcetera.util.ws.wrappers.MapWrapper;
import org.marketcetera.util.ws.wrappers.DateWrapper;

/**
 * A test service implementation to aid testing of client via {@link
 * MockServer}.
 * 
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
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

    private BrokersStatus getBrokersStatusImpl()
    {
        LinkedList<BrokerStatus> list=new LinkedList<BrokerStatus>();
        list.add(new BrokerStatus("N1",new BrokerID("ID1"),true));
        list.add(new BrokerStatus("N2",new BrokerID("ID2"),false));
        return new BrokersStatus(list);
    }

    private UserInfo getUserInfoImpl
        (UserID id)
    {
        if (id==null) {
            throw new NullPointerException();
        }
        return new UserInfo("bob",id,sActive,false);
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

    private MapWrapper<PositionKey,BigDecimal> getPositionsAsOfImpl
        (Date date)
    {
        return new MapWrapper<PositionKey, BigDecimal>(POSITIONS);
    }

    private String getNextOrderIDImpl()
    {
        return ID_PREFIX +(mNextOrderID++);
    }

    boolean toggleServerStatus()
    {
        mHeartbeatSuccess=!mHeartbeatSuccess;
        return mHeartbeatSuccess;
    }

    int getHeartbeatCount()
    {
        return mHeartbeatCount;
    }

    // Service.

    @Override
    public BrokersStatus getBrokersStatus
        (ClientContext context)
        throws RemoteException
    {
        return (new RemoteCaller<Object,BrokersStatus>
                (getSessionManager()) {
            @Override
            protected BrokersStatus call
                (ClientContext context,
                 SessionHolder<Object> sessionHolder)
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
        return (new RemoteCaller<Object,UserInfo>
                (getSessionManager()) {
            @Override
            protected UserInfo call
                (ClientContext context,
                 SessionHolder<Object> sessionHolder)
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
        return (new RemoteCaller<Object,ReportBaseImpl[]>
                (getSessionManager()) {
            @Override
            protected ReportBaseImpl[] call
                (ClientContext context,
                 SessionHolder<Object> sessionHolder)
                throws MessageCreationException
            {
                return getReportsSinceImpl(date.getRaw());
            }}).execute(context);
    }

    @Override
    public BigDecimal getPositionAsOf
        (ClientContext context,
         final DateWrapper date,
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
                return getPositionAsOfImpl(date.getRaw(),symbol);
            }}).execute(context);
    }

    @Override
    public MapWrapper<PositionKey,BigDecimal> getPositionsAsOf
        (ClientContext context,
         final DateWrapper date)
        throws RemoteException
    {
        return (new RemoteCaller<Object,MapWrapper<PositionKey,BigDecimal>>
                (getSessionManager()) {
            @Override
            protected MapWrapper<PositionKey,BigDecimal> call
                (ClientContext context,
                 SessionHolder<Object> sessionHolder)
            {
                return getPositionsAsOfImpl(date.getRaw());
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

    @Override
    public void heartbeat
        (ClientContext context)
        throws RemoteException
    {
        if (!mHeartbeatSuccess) {
            throw new IllegalStateException();
        }
        mHeartbeatCount++;
    }

    static final String ID_PREFIX = "MyID";

    // Mocking interface.

    private int mHeartbeatCount = 0;
    private boolean mHeartbeatSuccess = true;

    static ReportBaseImpl[] sReports = null;
    static boolean sActive = true;
    static final Map<PositionKey, BigDecimal> POSITIONS;
    static {
        Map<PositionKey, BigDecimal> positions =
            new HashMap<PositionKey, BigDecimal>();
        positions.put(new PositionKeyImpl("A","acme","bob"),
                      BigDecimal.TEN);
        positions.put(new PositionKeyImpl("B","wally","sue"),
                      BigDecimal.ONE.negate());
        POSITIONS = Collections.unmodifiableMap(positions);
    }
}
