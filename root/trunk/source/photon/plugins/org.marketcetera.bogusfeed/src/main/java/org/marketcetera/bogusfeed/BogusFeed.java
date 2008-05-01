package org.marketcetera.bogusfeed;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.marketcetera.core.BigDecimalUtils;
import org.marketcetera.core.IDFactory;
import org.marketcetera.core.InMemoryIDFactory;
import org.marketcetera.core.MSymbol;
import org.marketcetera.core.MarketceteraException;
import org.marketcetera.core.NoMoreIDsException;
import org.marketcetera.marketdata.AbstractMarketDataFeed;
import org.marketcetera.marketdata.FeedException;
import org.marketcetera.marketdata.IMarketDataFeed;
import org.marketcetera.marketdata.MarketDataFeedTokenSpec;

import quickfix.Group;
import quickfix.Message;
import quickfix.field.CFICode;
import quickfix.field.MDEntryPx;
import quickfix.field.MDEntrySize;
import quickfix.field.MDEntryType;
import quickfix.field.MDMkt;
import quickfix.field.MaturityDate;
import quickfix.field.MaturityMonthYear;
import quickfix.field.SecurityReqID;
import quickfix.field.SecurityRequestResult;
import quickfix.field.SecurityResponseID;
import quickfix.field.StrikePrice;
import quickfix.field.Symbol;
import quickfix.field.UnderlyingSymbol;
import quickfix.fix44.DerivativeSecurityList;
import quickfix.fix44.MarketDataSnapshotFullRefresh;

/**
 * Sample implementation of {@link IMarketDataFeed}.
 *
 * <p>This implementation generates random market data for each
 * symbol for which a market data request is received.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 0.43-SNAPSHOT
 */
public class BogusFeed 
    extends AbstractMarketDataFeed<BogusFeedToken,
                                   BogusFeedCredentials,
                                   BogusFeedMessageTranslator,
                                   BogusFeedEventTranslator,
                                   BogusMessage,
                                   BogusFeed> 
{
    /**
     * indicates if the feed has been logged in to
     */
    private boolean mLoggedIn;
    /**
     * value used to add to or subtract from prices
     */
    private static final BigDecimal PENNY = new BigDecimal("0.01");
    /**
     * bogus market exchange code
     */
    private static final String BGUS_MARKET = "BGUS";
    /**
     * list of symbols for which values are tracked
     */
    private final Map<String, BigDecimal> valueMap = new WeakHashMap<String, BigDecimal>();
    /**
     * used to generate handle ids to track specific requests
     */
    private final IDFactory idFactory = new InMemoryIDFactory(5000);
    /**
     * mechanism which manages the threads that create the market data
     */
    private ScheduledThreadPoolExecutor executor;
    /**
     * random generator used to manipulate prices
     */
    private final Random random = new Random(System.nanoTime());
    /**
     * Returns an instance of <code>BogusFeed</code>.
     *
     * @param inProviderName a <code>String</code> value
     * @param inCredentials a <code>BogusFeedCredentials</code> value
     * @return a <code>BogusFeed</code> value
     * @throws NoMoreIDsException if a unique identifier could not be generated to
     *   be assigned
     */
    static BogusFeed getInstance(String inProviderName,
                                 BogusFeedCredentials inCredentials) 
        throws NoMoreIDsException
    {
        return new BogusFeed(inProviderName,
                             inCredentials);
    }
    /**
     * Create a new BogusFeed instance.
     *
     * @param inProviderName a <code>String</code> value
     * @param inCredentials a <code>BogusFeedCredentials</code> value
     * @throws NoMoreIDsException if a unique identifier could not be generated to
     *   be assigned
     */
	private BogusFeed(String inProviderName,
	                  BogusFeedCredentials inCredentials) 
		throws NoMoreIDsException 
	{
		super(FeedType.SIMULATED,
              inProviderName,
              inCredentials);
        setLoggedIn(false);
	}
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.AbstractMarketDataFeed#start()
     */
    @Override
	public void start() {
        if(getFeedStatus().isRunning()) {
            throw new IllegalStateException();
        }
        executor = new ScheduledThreadPoolExecutor(1);
        executor.scheduleAtFixedRate(new Runnable() {
			public void run() {
				try {
					sendQuotes();
				} catch (Throwable t){
					t.printStackTrace();
				}
			}

        }, 0, 1, TimeUnit.SECONDS);
        super.start();
	}
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.AbstractMarketDataFeed#stop()
     */
    @Override
    public void stop() {
        if(!getFeedStatus().isRunning()) {
            throw new IllegalStateException();
        }
        super.stop();
        executor.shutdownNow();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.AbstractMarketDataFeed#doCancel(java.lang.String)
     */
    protected final void doCancel(String inHandle)
    {
        BogusRequest.removeRequest(inHandle);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.AbstractMarketDataFeed#doMarketDataRequest(java.lang.Object)
     */
    protected final List<String> doMarketDataRequest(BogusMessage inData)
        throws FeedException
    {
        try {
            List<String> handles = new ArrayList<String>();
            List<MSymbol> symbols = inData.getSymbols();
            for(MSymbol msymbol : symbols) {                
                String symbol = msymbol.toString();
                if (valueMap.containsKey(symbol)){
                    // This may seem a little bit silly,
                    // but because valueMap is a weak map,
                    // we need to use the actual string that 
                    // is the key.
                    for (String aKey : valueMap.keySet()) {
                        if (symbol.equals(aKey)){
                            symbol = aKey;
                            break;
                        }
                    }
                } else {
                    valueMap.put(symbol, 
                                 getRandPrice());
                }
                String handle = generateHandle();
                handles.add(handle);
                BogusRequest.addRequest(handle,
                                        msymbol);
            }
            return handles;
        } catch (Throwable t) {
            throw new FeedException(t);
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.AbstractMarketDataFeed#doDerivativeSecurityListRequest(java.lang.Object)
     */
    @Override
    protected final List<String> doDerivativeSecurityListRequest(BogusMessage inData)
            throws FeedException
    {
        throw new UnsupportedOperationException();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.AbstractMarketDataFeed#doSecurityListRequest(java.lang.Object)
     */
    @Override
    protected final List<String> doSecurityListRequest(BogusMessage inData)
            throws FeedException
    {
        throw new UnsupportedOperationException();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.AbstractMarketDataFeed#doLogin(org.marketcetera.marketdata.AbstractMarketDataFeedCredentials)
     */
    @Override
    protected final boolean doLogin(BogusFeedCredentials inCredentials)
    {
        setLoggedIn(true);
        return true;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.AbstractMarketDataFeed#doLogout()
     */
    @Override
    protected final void doLogout()
    {
        setLoggedIn(false);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.AbstractMarketDataFeed#generateToken(quickfix.Message)
     */
    @Override
    protected final BogusFeedToken generateToken(MarketDataFeedTokenSpec<BogusFeedCredentials> inTokenSpec)
            throws FeedException
    {
        return BogusFeedToken.getToken(inTokenSpec,
                                       this);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.AbstractMarketDataFeed#getEventTranslator()
     */
    @Override
    protected final BogusFeedEventTranslator getEventTranslator()
    {
        return new BogusFeedEventTranslator();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.AbstractMarketDataFeed#getMessageTranslator()
     */
    protected final BogusFeedMessageTranslator getMessageTranslator()
    {
        return new BogusFeedMessageTranslator();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.AbstractMarketDataFeed#isLoggedIn(org.marketcetera.marketdata.AbstractMarketDataFeedCredentials)
     */
    protected final boolean isLoggedIn(BogusFeedCredentials inCredentials)
    {
        return isLoggedIn();
    }
    private void sendQuotes() 
        throws MarketceteraException {
		for (String symbol : valueMap.keySet()) {
			BigDecimal currentValue = valueMap.get(symbol);
			valueMap.put(symbol, currentValue.add(PENNY));
			sendQuote(symbol, currentValue);
		}
	}
    /**
     * Sends quote to internal quote queue.
     *
     * @param symbol a <code>String</code> value
     * @param currentValue a <code>BigDecimal</code> value
     * @throws MarketceteraException 
     */
	private void sendQuote(String symbol, 
                           BigDecimal currentValue) 
        throws MarketceteraException 
    {
		Message refresh = new MarketDataSnapshotFullRefresh();
		refresh.setField(new Symbol(symbol));
        {
            Group group = new MarketDataSnapshotFullRefresh.NoMDEntries();
            group.setField(new MDEntryType(MDEntryType.BID));
            group.setField(new MDEntryPx(currentValue.subtract(PENNY)));
            group.setField(new MDMkt(BGUS_MARKET));
            group.setField(new MDEntrySize(random.nextInt(50000) + 1));
            refresh.addGroup(group);
        }
        {
            Group group = new MarketDataSnapshotFullRefresh.NoMDEntries();
            group.setField(new MDEntryType(MDEntryType.OFFER));
            group.setField(new MDEntryPx(currentValue.add(PENNY)));
            group.setField(new MDMkt(BGUS_MARKET));
            group.setField(new MDEntrySize(random.nextInt(50000) + 1));
            refresh.addGroup(group);
        }
        {
            Group group = new MarketDataSnapshotFullRefresh.NoMDEntries();
            group.setField(new MDEntryType(MDEntryType.TRADE));
            group.setField(new MDEntryPx(currentValue));
            group.setField(new MDMkt(BGUS_MARKET));
            group.setField(new MDEntrySize(random.nextInt(50000) + 1));
            refresh.addGroup(group);
        }		
        List<String> handles = BogusRequest.getHandlesForSymbol(new MSymbol(symbol));
        for(String handle : handles) {
            dataReceived(handle,
                         refresh);
        }
	}
    /**
     * Generates a random price.
     *
     * @return a <code>BigDecimal</code> value
     */
	private BigDecimal getRandPrice() 
    {
		return BigDecimalUtils.multiply(new BigDecimal(100), 
                                        random.nextDouble()).setScale(2, 
                                                                      RoundingMode.HALF_UP);
	}
    /**
     * Generates a unique handle.
     *
     * @return a <code>String</code> value
     * @throws NoMoreIDsException if a new handle cannot be generated
     */
    private String generateHandle() 
        throws NoMoreIDsException
    {
        return idFactory.getNext();
    }
    /**
     * Get the loggedIn value.
     *
     * @return a <code>BogusFeed</code> value
     */
    private boolean isLoggedIn()
    {
        return mLoggedIn;
    }
    /**
     * Sets the loggedIn value.
     *
     * @param a <code>BogusFeed</code> value
     */
    private void setLoggedIn(boolean inLoggedIn)
    {
        mLoggedIn = inLoggedIn;
    }
    /**
     * Encapsulates a request passed to the feed.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since 0.43-SNAPSHOT
     */
    private static class BogusRequest
    {
        /**
         * all requests by the symbol requested
         */
        private static final Hashtable<MSymbol,Set<BogusRequest>> sHandlesBySymbol = new Hashtable<MSymbol,Set<BogusRequest>>();
        /**
         * 
         */
        private static final Hashtable<BogusRequest,Set<MSymbol>> sSymbolsByHandle = new Hashtable<BogusRequest,Set<MSymbol>>();
        private static final Object sLock = new Object();
        /**
         * the internal unique handle corresponding to this request
         */
        private final String mHandle;
        /**
         * Create a new BogusRequest instance.
         *
         * @param inHandle a <code>String</code> value containing the unique handle for this request
         */
        private BogusRequest(String inHandle)
        {
            mHandle = inHandle;
        }
        /* (non-Javadoc)
         * @see java.lang.Object#hashCode()
         */
        public int hashCode()
        {
            final int PRIME = 31;
            int result = 1;
            result = PRIME * result + ((mHandle == null) ? 0 : mHandle.hashCode());
            return result;
        }
        /* (non-Javadoc)
         * @see java.lang.Object#equals(java.lang.Object)
         */
        public boolean equals(Object obj)
        {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            final BogusRequest other = (BogusRequest) obj;
            if (mHandle == null) {
                if (other.mHandle != null)
                    return false;
            } else if (!mHandle.equals(other.mHandle))
                return false;
            return true;
        }
        /**
         * Adds a request represented by the given handle.
         * 
         * <p>Adding requests allows the system to inform
         * subscribers when responses from the request
         * are generated.
         *
         * @param inHandle a <code>String</code> value
         */
        private static void addRequest(String inHandle,
                                       MSymbol inSymbol)
        {
            BogusRequest handle = new BogusRequest(inHandle);
            synchronized(sLock) {
                Set<MSymbol> symbols = sSymbolsByHandle.get(handle);
                if(symbols == null) {
                    symbols = new HashSet<MSymbol>();
                    sSymbolsByHandle.put(handle,
                                         symbols);
                }
                symbols.add(inSymbol);                
                Set<BogusRequest> requesters = sHandlesBySymbol.get(inSymbol);
                if(requesters == null) {
                    requesters = new HashSet<BogusRequest>();
                    sHandlesBySymbol.put(inSymbol, 
                                         requesters);
                }
                requesters.add(handle);
            }
        }
        /**
         * Removes a request represented by the given handle.
         *
         * <p>Once the handle is removed, responses to the
         * given request will not by published to subscribers.
         *
         * @param inHandle a <code>String</code> value
         */
        private static void removeRequest(String inHandle)
        {
            BogusRequest handle = new BogusRequest(inHandle);
            synchronized(sLock) {
                sSymbolsByHandle.remove(handle);
                Set<MSymbol> symbols = sSymbolsByHandle.remove(new BogusRequest(inHandle));
                if(symbols != null) {
                    for(MSymbol symbol : symbols) {
                        Set<BogusRequest> requesters = sHandlesBySymbol.get(symbol);
                        if(requesters != null) {
                            requesters.remove(handle);
                        }
                    }
                }
            }
        }
        /**
         * Gets handles from all requests.
         *
         * @return a <code>List&lt;String&gt;</code> value
         */
        private static List<String> getHandlesForSymbol(MSymbol inSymbol)
        {
            List<String> handles = new ArrayList<String>();
            synchronized(sLock) {
                Set<BogusRequest> requesters = sHandlesBySymbol.get(inSymbol);
                if(requesters != null) {
                    for(BogusRequest request : requesters) {
                        handles.add(new String(request.mHandle));
                    }
                }
            }
            return handles;
        }
    }
    
    public static DerivativeSecurityList createDummySecurityList(String symbol, 
                                                                 String[] callSuffixes, 
                                                                 String [] putSuffixes, 
                                                                 BigDecimal[] strikePrices) 
    {
        SecurityRequestResult resultCode = new SecurityRequestResult(SecurityRequestResult.VALID_REQUEST);
        DerivativeSecurityList responseMessage = new DerivativeSecurityList();
        responseMessage.setField(new SecurityReqID("bob"));
        responseMessage.setField(new SecurityResponseID("123"));

        responseMessage.setField(new UnderlyingSymbol(symbol));
        for (int i = 0; i < callSuffixes.length; i++) {
            MSymbol putSymbol = new MSymbol(symbol+"+"+putSuffixes[i]);
            // put first
            Group optionGroup = new DerivativeSecurityList.NoRelatedSym();
            optionGroup.setField(new Symbol(putSymbol.toString()));
            optionGroup.setField(new StrikePrice(strikePrices[i]));
            optionGroup.setField(new CFICode("OPASPS"));
            optionGroup.setField(new MaturityMonthYear("200801"));
            optionGroup.setField(new MaturityDate("20080122"));
            responseMessage.addGroup(optionGroup);

            MSymbol callSymbol = new MSymbol(symbol + "+" + callSuffixes[i]);
            // now call
            optionGroup.setField(new Symbol(callSymbol.toString()));
            optionGroup.setField(new StrikePrice(strikePrices[i]));
            optionGroup.setField(new CFICode("OCASPS"));
            optionGroup.setField(new MaturityMonthYear("200801"));
            optionGroup.setField(new MaturityDate("20080122"));
            responseMessage.addGroup(optionGroup);

        }
        responseMessage.setField(resultCode);
        return responseMessage;
    }

}
