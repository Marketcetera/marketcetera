package org.marketcetera.client;

import java.math.BigDecimal;
import java.util.*;

import javax.jws.WebParam;

import org.marketcetera.client.brokers.BrokerStatus;
import org.marketcetera.client.brokers.BrokersStatus;
import org.marketcetera.client.users.UserInfo;
import org.marketcetera.core.position.PositionKey;
import org.marketcetera.core.position.PositionKeyFactory;
import org.marketcetera.trade.*;
import org.marketcetera.trade.Currency;
import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.ws.stateful.*;
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
        return new UserInfo("bob",id,sActive,false,null);
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
    private BigDecimal getFuturePositionAsOfImpl(Date inDate,
                                                 Future inFuture)
    {
        return new BigDecimal(inDate.getTime());
    }
    private MapWrapper<PositionKey<Future>,BigDecimal> getFuturePositionsAsOfImpl(Date date)
    {
        return new MapWrapper<PositionKey<Future>, BigDecimal>(FUTURES_POSITIONS);
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
    /* (non-Javadoc)
     * @see org.marketcetera.client.Service#getAllFuturePositionsAsOf(org.marketcetera.util.ws.stateful.ClientContext, org.marketcetera.util.ws.wrappers.DateWrapper)
     */
    @Override
    public MapWrapper<PositionKey<Future>, BigDecimal> getAllFuturePositionsAsOf(ClientContext inContext,
                                                                                 final DateWrapper inDate)
            throws RemoteException
    {
        return (new RemoteCaller<Object,MapWrapper<PositionKey<Future>,BigDecimal>>
        (getSessionManager()) {
            @Override
            protected MapWrapper<PositionKey<Future>,BigDecimal> call
            (ClientContext context,
             SessionHolder<Object> sessionHolder)
             {
                return getFuturePositionsAsOfImpl(inDate.getRaw());
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
        return (new RemoteCaller<Object,BigDecimal>(getSessionManager()) {
            @Override
            protected BigDecimal call(ClientContext context,
                                      SessionHolder<Object> sessionHolder)
            {
                return getFuturePositionAsOfImpl(inDate.getRaw(),
                                                 inFuture);
            }}).execute(inContext);
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
    /* (non-Javadoc)
     * @see org.marketcetera.client.Service#getUserData(org.marketcetera.util.ws.stateful.ClientContext)
     */
    @Override
    public String getUserData(final ClientContext inContext)
            throws RemoteException
    {
        return userdata;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.Service#setUserData(org.marketcetera.util.ws.stateful.ClientContext, java.util.Properties)
     */
    @Override
    public void setUserData(ClientContext inContext,
                            String inData)
            throws RemoteException
    {
        userdata = inData;
    }
    private String userdata;
    static final String ID_PREFIX = "MyID";

    // Mocking interface.

    private int mHeartbeatCount = 0;
    private boolean mHeartbeatSuccess = true;
    private boolean mServiceInvoked = false;

    static ReportBaseImpl[] sReports = null;
    static boolean sActive = true;
    static final Map<PositionKey<Equity>, BigDecimal> POSITIONS;
    static final Map<PositionKey<Future>,BigDecimal> FUTURES_POSITIONS;
    static {
        Map<PositionKey<Equity>, BigDecimal> positions =
            new HashMap<PositionKey<Equity>, BigDecimal>();
        positions.put(PositionKeyFactory.createEquityKey("A","acme","bob"),
                      BigDecimal.TEN);
        positions.put(PositionKeyFactory.createEquityKey("B","wally","sue"),
                      BigDecimal.ONE.negate());
        POSITIONS = Collections.unmodifiableMap(positions);
        Map<PositionKey<Future>, BigDecimal> futurePositions = new HashMap<PositionKey<Future>,BigDecimal>();
        futurePositions.put(PositionKeyFactory.createFutureKey("ENOQ1-11","201103","acme","bob"),
                            BigDecimal.TEN);
        futurePositions.put(PositionKeyFactory.createFutureKey("BRN12N","201207","wally","sue"),
                            BigDecimal.ONE.negate());
        FUTURES_POSITIONS = Collections.unmodifiableMap(futurePositions);
    }
	@Override
	public BigDecimal getCurrencyPositionAsOf(
			@WebParam(name = "context") ClientContext context,
			@WebParam(name = "date") DateWrapper date,
			@WebParam(name = "currency") Currency currency)
			throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public MapWrapper<PositionKey<Currency>, BigDecimal> getAllCurrencyPositionsAsOf(
			@WebParam(name = "context") ClientContext context,
			@WebParam(name = "date") DateWrapper date) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}
    /* (non-Javadoc)
     * @see org.marketcetera.client.Service#addReport(org.marketcetera.util.ws.stateful.ClientContext, quickfix.Message)
     */
    @Override
    public void addReport(ClientContext inContext,
                          FIXMessageWrapper inReport,
                          BrokerID inBrokerID)
            throws RemoteException
    {
        throw new UnsupportedOperationException();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.Service#deleteReport(org.marketcetera.util.ws.stateful.ClientContext, org.marketcetera.trade.ExecutionReport)
     */
    @Override
    public void deleteReport(ClientContext inContext,
                             ExecutionReport inReport)
            throws RemoteException
    {
        throw new UnsupportedOperationException();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.Service#resolveSymbol(org.marketcetera.util.ws.stateful.ClientContext, java.lang.String)
     */
    @Override
    public Instrument resolveSymbol(@WebParam(name = "context")
                                    ClientContext inContext,
                                    @WebParam(name = "symbol")
                                    String inSymbol)
            throws RemoteException
    {
        throw new UnsupportedOperationException();
    }
}
