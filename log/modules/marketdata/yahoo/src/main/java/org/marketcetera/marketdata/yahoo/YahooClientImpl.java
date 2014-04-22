package org.marketcetera.marketdata.yahoo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import javax.annotation.concurrent.GuardedBy;

import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Provides a <code>YahooClient</code> implementation.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 2.1.4
 */
@ClassVersion("$Id$")
class YahooClientImpl
        implements Runnable, YahooClient
{
    /* (non-Javadoc)
     * @see org.springframework.context.Lifecycle#isRunning()
     */
    @Override
    public boolean isRunning()
    {
        return isRunning.get();
    }
    /* (non-Javadoc)
     * @see org.springframework.context.Lifecycle#start()
     */
    @Override
    public synchronized void start()
    {
        if(isRunning.get()) {
            return;
        }
        thread = new Thread(this,
                            "Yahoo Client Thread"); //$NON-NLS-1$
        thread.start();
        isRunning.set(true);
    }
    /* (non-Javadoc)
     * @see org.springframework.context.Lifecycle#stop()
     */
    @Override
    public synchronized void stop()
    {
        if(!isRunning.get()) {
            return;
        }
        try {
            if(thread != null) {
                thread.interrupt();
                try {
                    thread.join();
                } catch (InterruptedException ignored) {}
            }
        } finally {
            thread = null;
            isRunning.set(false);
        }
    }
    /* (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run()
    {
        try {
            while(isRunning.get()) {
                synchronized(requests) {
                    for(YahooRequest request : requests) {
                        try {
                            feedServices.doDataReceived(request.getHandle(),
                                                        submit(request));
                        } catch (IOException e) {
                            SLF4JLoggerProxy.debug(YahooClientImpl.class,
                                                   e,
                                                   "Retrying...");
                        }
                    }
                }
                Thread.sleep(feedServices.getRefreshInterval());
            }
        } catch (InterruptedException e) {
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.yahoo.YahooClient#login(org.marketcetera.marketdata.yahoo.YahooFeedCredentials)
     */
    @Override
    public boolean login(YahooFeedCredentials inCredentials)
    {
        credentials = inCredentials;
        start();
        return isRunning();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.yahoo.YahooClient#logout()
     */
    @Override
    public void logout()
    {
        stop();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.yahoo.YahooClient#isLoggedIn()
     */
    @Override
    public boolean isLoggedIn()
    {
        return isRunning();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.yahoo.YahooClient#request(org.marketcetera.marketdata.yahoo.YahooRequest)
     */
    @Override
    public void request(YahooRequest inRequest)
    {
        synchronized(requests) {
            requests.add(inRequest);
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.yahoo.YahooClient#cancel(org.marketcetera.marketdata.yahoo.YahooRequest)
     */
    @Override
    public void cancel(YahooRequest inRequest)
    {
        synchronized(requests) {
            requests.remove(inRequest);
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.yahoo.YahooClient#getRequestCounter()
     */
    @Override
    public long getRequestCounter()
    {
        return requestCounter.get();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.yahoo.YahooClient#resetRequestcounter()
     */
    @Override
    public void resetRequestcounter()
    {
        requestCounter.set(0);
    }
    /**
     * Create a new YahooClient instance.
     *
     * @param inFeedServices a <code>YahooFeedServices</code> value
     */
    YahooClientImpl(YahooFeedServices inFeedServices)
    {
        feedServices = inFeedServices;
    }
    /**
     * Submits the given request and returns the response from Yahoo.
     *
     * @param inRequest a <code>YahooRequest</code> value
     * @return a <code>String</code> value
     * @throws IOException if an error occurs submitting the request
     */
    private String submit(YahooRequest inRequest)
            throws IOException
    {
        StringBuilder response = new StringBuilder();
        String query = inRequest.getQuery();
        response.append(query).append(QUERY_SEPARATOR);
        // Create a URL for the desired page
        URL url = new URL(credentials.getURL() + query.replaceAll(",", //$NON-NLS-1$
                                                                  "")); //$NON-NLS-1$
        SLF4JLoggerProxy.trace(YahooClientImpl.class,
                               "Submitting request for {}", //$NON-NLS-1$
                               url);
        // Read all the text returned by the server
        BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
        requestCounter.incrementAndGet();
        String str;
        while ((str = in.readLine()) != null) {
            // str is one line of text; readLine() strips the newline character(s)
            response.append(str);
        }
        in.close();
        return response.toString();
    }
    /**
     * sentinel value used to separate query tokens
     */
    static final String QUERY_SEPARATOR = "&&/&&"; //$NON-NLS-1$

    /**
     * sentinel value used to separate query tokens
     */
    static final String FIELD_DELIMITER = ","; //$NON-NLS-1$

    /**
     * sentinel value used to separate query tokens
     */
    static final String DELIMITER_SYMBOL = ",?"; //$NON-NLS-1$
    
    /**
     * Yahoo feed services value
     */
    private final YahooFeedServices feedServices;
    /**
     * thread used for submitting requests
     */
    private volatile Thread thread;
    /**
     * credentials used for the Yahoo connection
     */
    private volatile YahooFeedCredentials credentials;
    /**
     * indicates if the client is running
     */
    private final AtomicBoolean isRunning = new AtomicBoolean(false);
    /**
     * the active Yahoo requests
     */
    @GuardedBy("requests")
    private final Set<YahooRequest> requests = new HashSet<YahooRequest>();
    /**
     * the counter used to keep track of the number of requests
     */
    private final AtomicLong requestCounter = new AtomicLong(0);
}
