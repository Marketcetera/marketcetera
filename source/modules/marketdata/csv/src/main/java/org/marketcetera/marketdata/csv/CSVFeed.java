package org.marketcetera.marketdata.csv;

import static org.marketcetera.marketdata.Capability.LATEST_TICK;
import static org.marketcetera.marketdata.Capability.TOP_OF_BOOK;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVStrategy;
import org.marketcetera.core.NoMoreIDsException;
import org.marketcetera.marketdata.*;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.misc.ClassVersion;

/**
 * Implementation of the market data feed that reads from a CSV file.
 *
 * <p>Market data is available in one or more CSV files.  The market data
 * request specifies the CSV file or files to use.  Market data is read
 * from the file or files until exhausted.
 * 
 * @author toli kuznets
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: CSVFeed.java 4348 2009-09-24 02:33:11Z toli $
 */
@ClassVersion("$Id: CSVFeed.java 4348 2009-09-24 02:33:11Z toli $")
public class CSVFeed 
        extends AbstractMarketDataFeed<CSVFeedToken,
                                       CSVFeedCredentials,
                                       CSVFeedMessageTranslator,
                                       CSVFeedEventTranslator,
                                       MarketDataRequest,
                                       CSVFeed> 
{
    /**
     * Returns an instance of <code>CSVFeed</code>.
     *
     * @param inProviderName a <code>String</code> value
     * @return a <code>CSVFeed</code> value
     * @throws org.marketcetera.core.NoMoreIDsException if a unique identifier could not be generated to
     *   be assigned
     */
    public synchronized static CSVFeed getInstance(String inProviderName) 
            throws NoMoreIDsException
    {
        if(sInstance != null) {
            return sInstance;
        }
        sInstance = new CSVFeed(inProviderName);
        return sInstance;
    }
    /**
     * Create a new CSVFeed instance.
     *
     * @param inProviderName a <code>String</code> value
     * @throws NoMoreIDsException if a unique identifier could not be generated to
     *   be assigned
     */
	private CSVFeed(String inProviderName) 
		throws NoMoreIDsException 
	{
		super(FeedType.DELAYED, // technically, it's historical
              inProviderName);
        setLoggedIn(false);
	}
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.AbstractMarketDataFeed#start()
     */
    @Override
	public synchronized void start() {
        if(getFeedStatus().isRunning()) {
            throw new IllegalStateException();
        }
        SLF4JLoggerProxy.debug(CSVFeed.class,
                               "CSVFeed starting"); //$NON-NLS-1$
        super.start();
	}
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.AbstractMarketDataFeed#stop()
     */
    @Override
    public synchronized void stop() {
        SLF4JLoggerProxy.debug(CSVFeed.class,
                               "CSVFeed stopping..."); //$NON-NLS-1$
        super.stop();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.MarketDataFeed#getCapabilities()
     */
    @Override
    public Set<Capability> getCapabilities()
    {
        return capabilities;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.AbstractMarketDataFeed#doCancel(java.lang.String)
     */
    @Override
    protected final synchronized void doCancel(String inHandle)
    {
        CsvFeedRequest request = requests.remove(inHandle);
        if(request == null) {
            SLF4JLoggerProxy.warn(CSVFeed.class,
                                  "Cannot cancel request for handle: {}", // TODO
                                  inHandle);
            return;
        }
        request.stop();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.AbstractMarketDataFeed#doLevelOneMarketDataRequest(java.lang.Object)
     */
    @Override
    protected final synchronized List<String> doMarketDataRequest(MarketDataRequest inData)
            throws FeedException
    {
        try {
            List<String> handleList = new ArrayList<String>();
            for(String filename : inData.getSymbols()) {
                CsvFeedRequest request = new CsvFeedRequest(filename,
                                                            inData);
                String handle = request.getHandle();
                handleList.add(handle);
                requests.put(handle,
                             request);
            }
            return handleList;
        } catch (FileNotFoundException e) {
            throw new FeedException(e);
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.AbstractMarketDataFeed#doLogin(org.marketcetera.marketdata.AbstractMarketDataFeedCredentials)
     */
    @Override
    protected final boolean doLogin(CSVFeedCredentials inCredentials)
    {
        setLoggedIn(true);
        credentials = inCredentials;
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
    protected final CSVFeedToken generateToken(MarketDataFeedTokenSpec inTokenSpec)
            throws FeedException
    {
        return CSVFeedToken.getToken(inTokenSpec,
                                       this);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.AbstractMarketDataFeed#getEventTranslator()
     */
    @Override
    protected final CSVFeedEventTranslator getEventTranslator()
    {
        return credentials.getEventTranslator();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.AbstractMarketDataFeed#getMessageTranslator()
     */
    @Override
    protected final CSVFeedMessageTranslator getMessageTranslator()
    {
        return CSVFeedMessageTranslator.getInstance();
    }
    @Override
    protected final boolean isLoggedIn()
    {
        return mLoggedIn;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.AbstractMarketDataFeed#afterDoExecute(org.marketcetera.marketdata.AbstractMarketDataFeedToken, java.lang.Exception)
     */
    @SuppressWarnings("unchecked")
    @Override
    protected void afterDoExecute(CSVFeedToken inToken,
                                  Exception inException)
    {
        if(inException != null) {
            // this means the request failed, do nothing
            return;
        }
        // the request succeeded - proceed
        // this code is not good.  it is bad.  it must be replaced, but changes need to be made to AbstractMarketDataFeed
        // this can't be done right now.
        // so, here it goes - to avoid a race condition, we need to delay parsing the file until the parent is ready to receive
        //  market data.  and we can almost do it with this method.  the problem is we have no way of associating a handle
        //  with a token.  the parent knows tokens, we know handles.  so, what we're going to do is sneak a peek at the set
        //  of handles associated with a token.  the parent knows this, but doesn't expose the information.  that's the change
        //  that needs to be made in the parent
        Class<?> thisClass = AbstractMarketDataFeed.class;
        try {
            Class<?> marketDataHandleClass = null;
            for(Class<?> innerClass : thisClass.getDeclaredClasses()) {
                if(innerClass.getName().contains("MarketDataHandle")) {
                    marketDataHandleClass = innerClass;
                }
            }
            if(marketDataHandleClass == null) {
                throw new NullPointerException();  // TODO
            } else {
                Field handleHolderField = thisClass.getDeclaredField("mHandleHolder");
                handleHolderField.setAccessible(true);
                Object handleHolder = handleHolderField.get(this);
                Method getHandlesMethod = handleHolder.getClass().getDeclaredMethod("getHandles",
                                                                                    AbstractMarketDataFeedToken.class);
                getHandlesMethod.setAccessible(true);
                List<Object> handles = (List<Object>)getHandlesMethod.invoke(handleHolder,
                                                                             inToken);
                SLF4JLoggerProxy.debug(CSVFeed.class,
                                       "Found {} for token {}",
                                       handles,
                                       inToken);
                for(Object handle : handles) {
                    // almost there, the handle isn't in quite the right format
                    Field actualHandleField = handle.getClass().getDeclaredField("mProtoHandle");
                    actualHandleField.setAccessible(true);
                    String actualHandle = (String)actualHandleField.get(handle);
                    CsvFeedRequest request = requests.get(actualHandle);
                    SLF4JLoggerProxy.debug(CSVFeed.class,
                                           "Found request {} for handle {}",
                                           request,
                                           actualHandle);
                    if(request == null) {
                        throw new NullPointerException();  // TODO
                    } else {
                        SLF4JLoggerProxy.debug(CSVFeed.class,
                                               "Submitting {}",
                                               request);
                        requestExecutor.submit(request);
                    }
                }
            }
        } catch (Exception e) {
            throw new IllegalArgumentException(e);  // TODO
        }
    }
    /**
     * Sets the loggedIn value.
     *
     * @param inLoggedIn Logged-in status of the feed
     */
    private void setLoggedIn(boolean inLoggedIn)
    {
        mLoggedIn = inLoggedIn;
    }
    /**
     * indicates if the feed has been logged in to
     */
    private boolean mLoggedIn;
    /**
     * 
     */
    private CSVFeedCredentials credentials;
    /**
     * holds active market data requests
     */
    private final Map<String,CsvFeedRequest> requests = new HashMap<String,CsvFeedRequest>();
    /**
     * 
     */
    private final ExecutorService requestExecutor = Executors.newCachedThreadPool();
    /**
     * capabilities for CSVFeed - note that these are not dynamic as Bogus requires no provisioning
     */
    private static final Set<Capability> capabilities = Collections.unmodifiableSet(EnumSet.of(TOP_OF_BOOK,LATEST_TICK));
    /**
     * static instance of <code>CSVFeed</code>
     */
    private static CSVFeed sInstance;
    /**
     * counter used to generate unique ids
     */
    private static final AtomicLong counter = new AtomicLong(0);
    /**
     * Corresponds to a single market data request submitted to {@link CSVFeed}.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id: CSVFeed.java 4348 2009-09-24 02:33:11Z toli $
     * @since 1.5.0
     */
    @ClassVersion("$Id: CSVFeed.java 4348 2009-09-24 02:33:11Z toli $")
    private class CsvFeedRequest
            implements Runnable
    {
        /* (non-Javadoc)
         * @see java.lang.Runnable#run()
         */
        @Override
        public void run()
        {
            SLF4JLoggerProxy.debug(CSVFeed.class,
                                   "Beggining request {}",
                                   this);
            isRunning.set(true);
            try {
                while(isRunning.get()) {
                    String[] line = parser.getLine();
                    if(line == null) {
                        SLF4JLoggerProxy.debug(CSVFeed.class,
                                               "Request {} is complete",
                                               this);
                        break;
                    }
                    dataReceived(handle,
                                 CSVQuantum.getQuantum(line,
                                                       request));
                }
            } catch (IOException e) {
                throw new IllegalArgumentException(e);  // TODO
            } finally {
                isRunning.set(false);
            }
        }
        /* (non-Javadoc)
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString()
        {
            return String.format("CsvFeedRequest [handle=%s, request=%s ]",
                                 handle,
                                 request);
        }
        /**
         * Create a new CsvFeedRequest instance.
         *
         * @param inDataFilename
         * @param inRequest
         * @throws FileNotFoundException
         */
        private CsvFeedRequest(String inDataFilename,
                               MarketDataRequest inRequest)
                throws FileNotFoundException
        {
            parser = new CSVParser(new FileReader(new File(inDataFilename)),
                                   CSVStrategy.EXCEL_STRATEGY);
            request = inRequest;
        }
        /**
         * 
         *
         *
         */
        private void stop()
        {
            if(!isRunning.get()) {
                // TODO warn
                return;
            }
            isRunning.set(false);
        }
        /**
         * 
         *
         *
         * @return
         */
        private String getHandle()
        {
            return handle;
        }
        /**
         * 
         */
        private final CSVParser parser;
        /**
         * 
         */
        private final MarketDataRequest request;
        /**
         * 
         */
        private final String handle = Long.toHexString(counter.incrementAndGet());
        /**
         * 
         */
        private final AtomicBoolean isRunning = new AtomicBoolean(false);
//        /**
//         * Executes the given <code>MarketDataRequest</code> and returns
//         * a handle corresponding to the request.
//         *
//         * @param inRequest a <code>MarketDataRequest</code> value
//         * @param inParentFeed a <code>CSVFeed</code> value
//         * @param delayInSeconds optional delay to wait between sending market data in
//         * @return a <code>String</code> value
//         */
//        private static String execute(MarketDataRequest inRequest,
//                                      CSVFeed inParentFeed,
//                                      Queue<CSVReaderRunnable> queue,
//                                      long delayInSeconds)
//                throws FileNotFoundException
//        {
//            CsvFeedRequest request = new CsvFeedRequest(inRequest,
//                                                        inParentFeed);
//            request.execute(queue,
//                            delayInSeconds);
//            return request.getIDAsString();
//        }
//        /**
//         * Create a new Request instance.
//         *
//         * @param inRequest a <code>MarketDataRequest</code> value
//         * @param inFeed a <code>CSVFeed</code> value
//         */
//        private CsvFeedRequest(MarketDataRequest inRequest,
//                               CSVFeed inFeed)
//        {
//            request = inRequest;
//            feed = inFeed;
//            subscriber = new ISubscriber() {
//                @Override
//                public boolean isInteresting(Object inData)
//                {
//                    return true;
//                }
//                @Override
//                public void publishTo(Object inData)
//                {
//                    SLF4JLoggerProxy.debug(CSVFeed.class,
//                                           "CSVFeed publishing {}", //$NON-NLS-1$
//                                           inData);
//                    feed.dataReceived(getIDAsString(),
//                                      inData);
//                }
//            };
//        }
//        /**
//         * Executes the market data request associated with this object.
//         * 
//         * @throws IllegalStateException if this method has already been executed for this object
//         * @param queue
//         * @param dataDir Location of CSV file
//         * @param delayInSecs   Seconds to wait between sending each event
//         */
//        private synchronized void execute(Queue<CSVReaderRunnable> queue,
//                                          long delayInSecs)
//                throws FileNotFoundException
//        {
//            if(executed) {
//                throw new IllegalStateException();
//            }
//            try {
//                List<Instrument> symbols = new ArrayList<Instrument>();
//                for(String symbol : request.getSymbols()) {
//                    symbols.add(new Equity(symbol));
//                }
//                for(Instrument symbol : symbols) {
//                    // all symbols for which we want data are collected in the symbols list
//                    // each type of subscription is managed differently
//                    CSVParser parser;
//                    for(Content content : request.getContent()) {
//                        parser = new CSVParser(new FileReader(new File(dataDir, symbol.getSymbol() + EXTENSION)),
//                                               CSVStrategy.EXCEL_STRATEGY);
//                        switch(content) {
//                            case TOP_OF_BOOK :
//                                // TOP_OF_BOOK from the specified exchange only
//                                queue.add(new CSVReaderRunnable(parser,
//                                                                symbol.getSymbol(),
//                                                                Content.TOP_OF_BOOK,
//                                                                subscriber,
//                                                                delayInSecs));
//                                break;
//                            case LATEST_TICK :
//                                // LATEST_TICK is the most recent trade
//                                queue.add(new CSVReaderRunnable(parser, symbol.getSymbol(),
//                                          Content.LATEST_TICK, subscriber, delayInSecs));
//                                break;
//                            default:
//                                throw new UnsupportedOperationException();
//                        }
//                    }
//                }
//            } finally {
//                executed = true;
//            }
//        }
//        /**
//         * Returns the request ID as a <code>String</code>. 
//         *
//         * @return a <code>String</code> value
//         */
//        private String getIDAsString()
//        {
//            return Long.toHexString(id);
//        }
//        /**
//         * the market data request associated with this object
//         */
//        private final MarketDataRequest request;
//        /**
//         * the unique identifier of this request
//         */
//        private final long id = counter.incrementAndGet();
//        /**
//         * the parent object for this request
//         */
//        private final CSVFeed feed;
//        /**
//         * the bridge object which receives responses from the parent's nested exchanges
//         * and forwards them to the submitter of the request
//         */
//        private final ISubscriber subscriber;
//        /**
//         * indicates whether this object has been executed yet or not
//         */
//        private boolean executed = false;
    }
}
