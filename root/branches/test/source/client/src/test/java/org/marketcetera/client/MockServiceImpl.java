package org.marketcetera.client;

import java.math.BigDecimal;
import java.util.*;

import org.marketcetera.client.brokers.BrokerStatus;
import org.marketcetera.client.brokers.BrokersStatus;
import org.marketcetera.client.users.UserInfo;
import org.marketcetera.core.position.PositionKey;
import org.marketcetera.core.position.PositionKeyFactory;
import org.marketcetera.trade.*;
import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.ws.stateful.ClientContext;
import org.marketcetera.util.ws.stateful.RemoteCaller;
import org.marketcetera.util.ws.stateful.ServiceBaseImpl;
import org.marketcetera.util.ws.stateful.SessionHolder;
import org.marketcetera.util.ws.stateful.SessionManager;
import org.marketcetera.util.ws.wrappers.DateWrapper;
import org.marketcetera.util.ws.wrappers.MapWrapper;
import org.marketcetera.util.ws.wrappers.RemoteException;

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
         Equity equity)
    {
        return new BigDecimal(date.getTime());
    }

    private MapWrapper<PositionKey<Equity>,BigDecimal> getPositionsAsOfImpl
        (Date date)
    {
        return new MapWrapper<PositionKey<Equity>, BigDecimal>(POSITIONS);
    }

    private BigDecimal getOptionPositionAsOfImpl
        (Date inRaw,
         Option inOption)
    {
        return inOption.getStrikePrice().add(BigDecimal.valueOf(inRaw.getTime()));
    }

    private MapWrapper<PositionKey<Option>, BigDecimal> getAllOptionPositionsAsOfImpl
        (Date inRaw)
    {
        Map<PositionKey<Option>, BigDecimal> map = new HashMap<PositionKey<Option>, BigDecimal>();
        map.put(PositionKeyFactory.createOptionKey("OPT", "20101010",
                BigDecimal.TEN, OptionType.Call, "acc", "tra"),
                BigDecimal.valueOf(inRaw.getTime()));
        return new MapWrapper<PositionKey<Option>, BigDecimal>(map);
    }
    
    private MapWrapper<PositionKey<Option>, BigDecimal> getOptionPositionsAsOfImpl
        (Date inRaw,
         String... inSymbols)
    {
        Map<PositionKey<Option>, BigDecimal> map = new HashMap<PositionKey<Option>, BigDecimal>();
        if (inSymbols != null) {
            for(String symbol: inSymbols) {
                map.put(PositionKeyFactory.createOptionKey(symbol, "20101010",
                        BigDecimal.TEN, OptionType.Call, "acc", "tra"),
                        BigDecimal.valueOf(inRaw.getTime()));
            }
        }
        return new MapWrapper<PositionKey<Option>, BigDecimal>(map);
    }

    private String getNextOrderIDImpl()
    {
        return ID_PREFIX +(mNextOrderID++);
    }

    private String getUnderlyingImpl(String inOptionRoot)
    {
        mServiceInvoked = true;
        return inOptionRoot;
    }

    private Collection<String> getOptionRootsImpl(String inUnderlying)
    {
        mServiceInvoked = true;
        if(inUnderlying == null) {
            return null;
        }
        List<String> list = new LinkedList<String>();
        for(int i = 0; i < inUnderlying.length(); i++) {
            list.add(inUnderlying);
        }
        return list;
    }

    public boolean isServiceInvoked()
    {
        return mServiceInvoked;
    }

    public void resetServiceInvoked()
    {
        mServiceInvoked = false;
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
    public BigDecimal getEquityPositionAsOf
        (ClientContext context,
         final DateWrapper date,
         final Equity equity)
        throws RemoteException
    {
        return (new RemoteCaller<Object,BigDecimal>
                (getSessionManager()) {
            @Override
            protected BigDecimal call
                (ClientContext context,
                 SessionHolder<Object> sessionHolder)
            {
                return getPositionAsOfImpl(date.getRaw(),equity);
            }}).execute(context);
    }

    @Override
    public MapWrapper<PositionKey<Equity>,BigDecimal> getAllEquityPositionsAsOf
        (ClientContext context,
         final DateWrapper date)
        throws RemoteException
    {
        return (new RemoteCaller<Object,MapWrapper<PositionKey<Equity>,BigDecimal>>
                (getSessionManager()) {
            @Override
            protected MapWrapper<PositionKey<Equity>,BigDecimal> call
                (ClientContext context,
                 SessionHolder<Object> sessionHolder)
            {
                return getPositionsAsOfImpl(date.getRaw());
            }}).execute(context);
    }

    @Override
    public BigDecimal getOptionPositionAsOf
            (ClientContext context,
             final DateWrapper date,
             final Option option) throws RemoteException
    {
        return (new RemoteCaller<Object,BigDecimal>
                (getSessionManager()) {
            @Override
            protected BigDecimal call
                (ClientContext context,
                 SessionHolder<Object> sessionHolder)
            {
                return getOptionPositionAsOfImpl
                    (date.getRaw(),option);
            }}).execute(context);
    }

    @Override
    public MapWrapper<PositionKey<Option>, BigDecimal> getAllOptionPositionsAsOf
            (ClientContext context,
             final DateWrapper date) throws RemoteException
    {
        return (new RemoteCaller<Object,MapWrapper<PositionKey<Option>,
                                                          BigDecimal>>
                (getSessionManager()) {
            @Override
            protected MapWrapper<PositionKey<Option>,BigDecimal> call
                (ClientContext context,
                 SessionHolder<Object> sessionHolder)
            {
                return getAllOptionPositionsAsOfImpl
                    (date.getRaw());
            }}).execute(context);
    }

    @Override
    public MapWrapper<PositionKey<Option>, BigDecimal> getOptionPositionsAsOf
            (ClientContext context,
             final DateWrapper date,
             final String... symbols) throws RemoteException
    {
        return (new RemoteCaller<Object,MapWrapper<PositionKey<Option>,
                                                          BigDecimal>>
                (getSessionManager()) {
            @Override
            protected MapWrapper<PositionKey<Option>,BigDecimal> call
                (ClientContext context,
                 SessionHolder<Object> sessionHolder)
            {
                return getOptionPositionsAsOfImpl
                    (date.getRaw(), symbols);
            }}).execute(context);
    }

    @Override
    public String getUnderlying
            (ClientContext context,
             final String optionRoot) throws RemoteException
    {
        return (new RemoteCaller<Object,String>
                (getSessionManager()) {
            @Override
            protected String call
                (ClientContext context,
                 SessionHolder<Object> sessionHolder)
            {
                return getUnderlyingImpl(optionRoot);
            }}).execute(context);
    }

    @Override
    public Collection<String> getOptionRoots
            (ClientContext context,
             final String underlying) throws RemoteException
    {
        return (new RemoteCaller<Object,Collection<String>>
                (getSessionManager()) {
            @Override
            protected Collection<String> call
                (ClientContext context,
                 SessionHolder<Object> sessionHolder)
            {
                return getOptionRootsImpl(underlying);
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
    private boolean mServiceInvoked = false;

    static ReportBaseImpl[] sReports = null;
    static boolean sActive = true;
    static final Map<PositionKey<Equity>, BigDecimal> POSITIONS;
    static {
        Map<PositionKey<Equity>, BigDecimal> positions =
            new HashMap<PositionKey<Equity>, BigDecimal>();
        positions.put(PositionKeyFactory.createEquityKey("A","acme","bob"),
                      BigDecimal.TEN);
        positions.put(PositionKeyFactory.createEquityKey("B","wally","sue"),
                      BigDecimal.ONE.negate());
        POSITIONS = Collections.unmodifiableMap(positions);
    }
}
