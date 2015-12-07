package org.marketcetera.marketdata.csv;

import static org.marketcetera.marketdata.AssetClass.EQUITY;
import static org.marketcetera.marketdata.AssetClass.FUTURE;
import static org.marketcetera.marketdata.AssetClass.OPTION;
import static org.marketcetera.marketdata.Capability.LATEST_TICK;
import static org.marketcetera.marketdata.Capability.TOP_OF_BOOK;
import static org.marketcetera.marketdata.csv.Messages.CANCEL_REQUEST_FAILED_HANDLE_NOT_FOUND;
import static org.marketcetera.marketdata.csv.Messages.FAILED_TO_START_REQUEST;
import static org.marketcetera.marketdata.csv.Messages.REQUEST_FAILED;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVStrategy;
import org.marketcetera.core.NoMoreIDsException;
import org.marketcetera.marketdata.AbstractMarketDataFeed;
import org.marketcetera.marketdata.AssetClass;
import org.marketcetera.marketdata.Capability;
import org.marketcetera.marketdata.FeedException;
import org.marketcetera.marketdata.MarketDataFeedTokenSpec;
import org.marketcetera.marketdata.MarketDataRequest;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.misc.ClassVersion;

/**
 * Implementation of the market data feed that reads from a CSV file.
 *
 * <p>Market data is available in one or more CSV files. The market data
 * request specifies a symbol which is matched against a file of the same
 * name in the specified directory. Market data is read
 * from the file or files until exhausted.
 * 
 * @author toli kuznets
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @since 2.1.0
 * @version $Id$
 */
@ClassVersion("$Id$")
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
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return String.format("CSVFeed"); //$NON-NLS-1$
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
        requestExecutor = Executors.newCachedThreadPool();
        super.start();
	}
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.AbstractMarketDataFeed#stop()
     */
    @Override
    public synchronized void stop() {
        SLF4JLoggerProxy.debug(CSVFeed.class,
                               "CSVFeed stopping..."); //$NON-NLS-1$
        requestExecutor.shutdownNow();
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
     * @see org.marketcetera.marketdata.MarketDataFeed#getSupportedAssetClasses()
     */
    @Override
    public Set<AssetClass> getSupportedAssetClasses()
    {
        return assetClasses;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.AbstractMarketDataFeed#doCancel(java.lang.String)
     */
    @Override
    protected final synchronized void doCancel(String inHandle)
    {
        CsvFeedRequest request = requests.remove(inHandle);
        if(request == null) {
            CANCEL_REQUEST_FAILED_HANDLE_NOT_FOUND.warn(CSVFeed.class,
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
    {
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
    @Override
    protected void afterDoExecute(CSVFeedToken inToken,
                                  List<String> inHandles,
                                  Exception inException)
    {
        if(inException != null) {
            // this means the request failed, do nothing
            return;
        }
        try {
            for(String actualHandle : inHandles) {
                CsvFeedRequest request = requests.get(actualHandle);
                SLF4JLoggerProxy.debug(CSVFeed.class,
                                       "Found request {} for handle {}", //$NON-NLS-1$
                                       request,
                                       actualHandle);
                if(request == null) {
                    throw new NullPointerException();
                } else {
                    SLF4JLoggerProxy.debug(CSVFeed.class,
                                           "Submitting {}", //$NON-NLS-1$
                                           request);
                    requestExecutor.submit(request);
                }
            }
        } catch (Exception e) {
            throw new IllegalArgumentException(FAILED_TO_START_REQUEST.getText(),
                                               e);
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
     * the credentials object used to initiate the feed
     */
    private CSVFeedCredentials credentials;
    /**
     * holds active market data requests
     */
    private final Map<String,CsvFeedRequest> requests = new HashMap<String,CsvFeedRequest>();
    /**
     * executes and manages market data requests
     */
    private ExecutorService requestExecutor;
    /**
     * capabilities for CSVFeed - note that these are not dynamic as Bogus requires no provisioning
     */
    private static final Set<Capability> capabilities = Collections.unmodifiableSet(EnumSet.of(TOP_OF_BOOK,LATEST_TICK));
    /**
     * supported asset classes
     */
    private static final Set<AssetClass> assetClasses = EnumSet.of(EQUITY,OPTION,FUTURE);
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
     * @version $Id$
     * @since 1.5.0
     */
    @ClassVersion("$Id$")
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
                                   "Beginning request {}", //$NON-NLS-1$
                                   this);
            isRunning.set(true);
            try {
                CSVParser parser = null;
                long start = System.currentTimeMillis();
                long count = 0;
                long delay = credentials.getReplayRate();
                if(delay < 0) {
                    delay = 0;
                }
                while(isRunning.get()) {
                    if(parser == null) {
                        start = System.currentTimeMillis();
                        count = 0;
                        parser = new CSVParser(new FileReader(new File(credentials.getMarketdataDirectory(),
                                                                       dataFilename)),
                                              CSVStrategy.EXCEL_STRATEGY);
                    }
                    String[] line = parser.getLine();
                    if(line == null) {
                        Messages.END_OF_DATA_REACHED.debug(org.marketcetera.core.Messages.USER_MSG_CATEGORY,
                                                           count,
                                                           System.currentTimeMillis() - start);
                        if(credentials.getReplayEvents() && isRunning.get()) {
                            count = 0;
                            parser = null;
                            continue;
                        } else {
                            break;
                        }
                    }
                    if(delay != 0) {
                        Thread.sleep(delay);
                    }
                    count += 1;
                    dataReceived(handle,
                                 CSVQuantum.getQuantum(line,
                                                       request,
                                                       credentials.getReplayRate()));
                }
            } catch (Exception e) {
                REQUEST_FAILED.warn(org.marketcetera.core.Messages.USER_MSG_CATEGORY,
                                    e,
                                    this);
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
            return String.format("CsvFeedRequest [handle=%s, request=%s ]", //$NON-NLS-1$
                                 handle,
                                 request);
        }
        /**
         * Create a new CsvFeedRequest instance.
         *
         * @param inDataFilename a <code>String</code> value
         * @param inRequest a <code>MarketDataRequest</code> value
         */
        private CsvFeedRequest(String inDataFilename,
                               MarketDataRequest inRequest)
        {
            request = inRequest;
            dataFilename = inDataFilename;
        }
        /**
         * Stops the currently running request as soon as possible. 
         */
        private void stop()
        {
            if(!isRunning.get()) {
                return;
            }
            isRunning.set(false);
        }
        /**
         * Gets the request handle. 
         *
         * @return a <code>String</code> value
         */
        private String getHandle()
        {
            return handle;
        }
        /**
         * data filename
         */
        private final String dataFilename;
        /**
         * the original request
         */
        private final MarketDataRequest request;
        /**
         * the handle assigned to this request 
         */
        private final String handle = Long.toHexString(counter.incrementAndGet());
        /**
         * indicates if the request is running or not
         */
        private final AtomicBoolean isRunning = new AtomicBoolean(false);
    }
}
