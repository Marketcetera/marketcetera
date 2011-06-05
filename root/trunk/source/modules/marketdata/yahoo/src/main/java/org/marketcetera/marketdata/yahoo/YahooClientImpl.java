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
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
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
                            "Yahoo Client Thread");
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
                        feedServices.doDataReceived(request.getHandle(),
                                                    getResponse(request));
                    }
                }
                Thread.sleep(feedServices.getRefreshInterval());
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
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
     * @param inFeedServices
     */
    YahooClientImpl(YahooFeedServices inFeedServices)
    {
        feedServices = inFeedServices;
    }
    /**
     * 
     *
     *
     * @param inRequest
     * @return
     * @throws IOException
     */
    private String getResponse(YahooRequest inRequest)
            throws IOException
    {
        StringBuilder response = new StringBuilder();
        String query = inRequest.getQuery();
        response.append(query).append(QUERY_SEPARATOR);
        // Create a URL for the desired page
        URL url = new URL(credentials.getURL() + query.replaceAll(",",
                                                                  ""));
        SLF4JLoggerProxy.trace(YahooClientImpl.class,
                               "Submitting request for {}",
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
     * 
     */
    static final String QUERY_SEPARATOR = "&&/&&";
    /**
     * 
     */
    private final YahooFeedServices feedServices;
    /**
     * 
     */
    private volatile Thread thread;
    /**
     * 
     */
    private volatile YahooFeedCredentials credentials;
    /**
     * 
     */
    private final AtomicBoolean isRunning = new AtomicBoolean(false);
    /**
     * 
     */
    @GuardedBy("requests")
    private final Set<YahooRequest> requests = new HashSet<YahooRequest>();
    /**
     * 
     */
    private final AtomicLong requestCounter = new AtomicLong(0);
}
