package org.marketcetera.marketdata.core.webservice.impl;

import java.net.ConnectException;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.concurrent.ThreadSafe;
import javax.xml.ws.WebServiceException;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.marketcetera.core.ApplicationVersion;
import org.marketcetera.core.Util;
import org.marketcetera.core.notifications.ServerStatusListener;
import org.marketcetera.core.publisher.ISubscriber;
import org.marketcetera.core.publisher.PublisherEngine;
import org.marketcetera.event.Event;
import org.marketcetera.marketdata.Content;
import org.marketcetera.marketdata.MarketDataRequest;
import org.marketcetera.marketdata.core.manager.MarketDataProviderNotAvailable;
import org.marketcetera.marketdata.core.manager.MarketDataRequestFailed;
import org.marketcetera.marketdata.core.manager.MarketDataRequestTimedOut;
import org.marketcetera.marketdata.core.manager.NoMarketDataProvidersAvailable;
import org.marketcetera.marketdata.core.webservice.*;
import org.marketcetera.trade.Instrument;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.ws.ContextClassProvider;
import org.marketcetera.util.ws.stateful.Client;
import org.marketcetera.util.ws.tags.AppId;
import org.marketcetera.util.ws.wrappers.RemoteException;

/* $License$ */

/**
 * Provides access to Market Data Nexus services.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ThreadSafe
@ClassVersion("$Id$")
public class MarketDataServiceClientImpl
        implements MarketDataServiceClient
{
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.core.webservice.MarketDataServiceClient#request(org.marketcetera.marketdata.MarketDataRequest, boolean)
     */
    @Override
    public long request(MarketDataRequest inRequest,
                        boolean inStreamEvents)
    {
        try {
            checkConnection();
            return marketDataService.request(serviceClient.getContext(),
                                             inRequest,
                                             inStreamEvents);
        } catch (RemoteException e) {
            if(e.getCause() != null) {
                if(e.getCause() instanceof NoMarketDataProvidersAvailable) {
                    throw (NoMarketDataProvidersAvailable)e.getCause();
                }
                if(e.getCause() instanceof MarketDataProviderNotAvailable) {
                    throw (MarketDataProviderNotAvailable)e.getCause();
                }
                if(e.getCause() instanceof MarketDataRequestFailed) {
                    throw (MarketDataRequestFailed)e.getCause();
                }
                if(e.getCause() instanceof MarketDataRequestTimedOut) {
                    throw (MarketDataRequestTimedOut)e.getCause();
                }
            }
            throw new RuntimeException(e);
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.core.webservice.MarketDataServiceClient#getLastUpdate(long)
     */
    @Override
    public long getLastUpdate(long inRequestId)
    {
        try {
            checkConnection();
            return marketDataService.getLastUpdate(serviceClient.getContext(),
                                                   inRequestId);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.core.webservice.MarketDataServiceClient#cancel(long)
     */
    @Override
    public void cancel(long inRequestId)
    {
        try {
            checkConnection();
            marketDataService.cancel(serviceClient.getContext(),
                                     inRequestId);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.core.webservice.MarketDataWebServiceClient#getEvents(long)
     */
    @Override
    public Deque<Event> getEvents(long inRequestId)
    {
        try {
            checkConnection();
            return marketDataService.getEvents(serviceClient.getContext(),
                                               inRequestId);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.core.webservice.MarketDataServiceClient#getAllEvents(java.util.List)
     */
    @Override
    public Map<Long,LinkedList<Event>> getAllEvents(List<Long> inRequestIds)
    {
        try {
            checkConnection();
            return marketDataService.getAllEvents(serviceClient.getContext(),
                                                  inRequestIds);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.core.webservice.MarketDataServiceClient#getSnapshot(org.marketcetera.trade.Instrument, org.marketcetera.marketdata.Content, java.lang.String)
     */
    @Override
    public Deque<Event> getSnapshot(Instrument inInstrument,
                                    Content inContent,
                                    String inProvider)
    {
        try {
            checkConnection();
            return marketDataService.getSnapshot(serviceClient.getContext(),
                                                 inInstrument,
                                                 inContent,
                                                 inProvider);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.core.webservice.MarketDataServiceClient#getSnapshotPage(org.marketcetera.trade.Instrument, org.marketcetera.marketdata.Content, java.lang.String, org.springframework.data.domain.PageRequest)
     */
    @Override
    public Deque<Event> getSnapshotPage(Instrument inInstrument,
                                        Content inContent,
                                        String inProvider,
                                        PageRequest inPage)
    {
        try {
            checkConnection();
            return marketDataService.getSnapshotPage(serviceClient.getContext(),
                                                     inInstrument,
                                                     inContent,
                                                     inProvider,
                                                     inPage);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }
    /* (non-Javadoc)
     * @see org.springframework.context.Lifecycle#isRunning()
     */
    @Override
    public synchronized boolean isRunning()
    {
        return running.get();
    }
    /* (non-Javadoc)
     * @see org.springframework.context.Lifecycle#start()
     */
    @Override
    public synchronized void start()
    {
        Validate.notNull(hostname);
        Validate.notNull(username);
        Validate.notNull(password);
        Validate.isTrue(port > 0);
        serviceClient = new Client(hostname,
                                   port,
                                   APP_ID,
                                   contextClassProvider);
        try {
            serviceClient.login(username,
                                password.toCharArray());
        } catch (WebServiceException e) {
            if(e.getCause() != null && e.getCause() instanceof ConnectException) {
                throw new ConnectionException(hostname,
                                              port);
            }
        } catch (RemoteException e) {
            throw new CredentialsException(username);
        } catch (Exception e) {
            SLF4JLoggerProxy.warn(this,
                                  e);
            throw new RuntimeException(e);
        }
        marketDataService = serviceClient.getService(MarketDataService.class);
        try {
            marketDataService.heartbeat(serviceClient.getContext());
        } catch (RemoteException | WebServiceException e) {
            if(e.getCause() != null) {
                if(e.getCause() instanceof java.net.UnknownHostException) {
                    throw new UnknownHostException(hostname,
                                                   port);
                }
            }
            throw new RuntimeException(e);
        }
        heartbeatToken = heartbeatExecutor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run()
            {
                try {
                    marketDataService.heartbeat(serviceClient.getContext());
                } catch (Exception e) {
                    heartbeatError(e);
                }
            }
        },heartbeatInterval,heartbeatInterval,TimeUnit.MILLISECONDS);
        running.set(true);
    }
    /* (non-Javadoc)
     * @see org.springframework.context.Lifecycle#stop()
     */
    @Override
    public synchronized void stop()
    {
        try {
            if(heartbeatToken != null) {
                heartbeatToken.cancel(true);
            }
        } catch (Exception ignored) {}
        try {
            if(serviceClient != null) {
                serviceClient.logout();
            }
        } catch (Exception ignored) {
        } finally {
            heartbeatToken = null;
            serviceClient = null;
            marketDataService = null;
            running.set(false);
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.core.webservice.MarketDataServiceClient#addServerStatusListener(org.marketcetera.core.notifications.ServerStatusListener)
     */
    @Override
    public void addServerStatusListener(ServerStatusListener inListener)
    {
        serverStatusPublisher.subscribe(new ServerStatusSubscriber(inListener));
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.core.webservice.MarketDataServiceClient#removeServerStatusListener(org.marketcetera.core.notifications.ServerStatusListener)
     */
    @Override
    public void removeServerStatusListener(ServerStatusListener inListener)
    {
        serverStatusPublisher.unsubscribe(new ServerStatusSubscriber(inListener));
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("Market Data Service Client - ").append(hostname).append(":").append(port).append(" as ").append(username);
        return builder.toString();
    }
    /**
     * Get the hostname value.
     *
     * @return a <code>String</code> value
     */
    public String getHostname()
    {
        return hostname;
    }
    /**
     * Sets the hostname value.
     *
     * @param inHostname a <code>String</code> value
     */
    public void setHostname(String inHostname)
    {
        hostname = inHostname;
    }
    /**
     * Get the port value.
     *
     * @return an <code>int</code> value
     */
    public int getPort()
    {
        return port;
    }
    /**
     * Sets the port value.
     *
     * @param inPort an <code>int</code> value
     */
    public void setPort(int inPort)
    {
        port = inPort;
    }
    /**
     * Get the contextClassProvider value.
     *
     * @return a <code>ContextClassProvider</code> value
     */
    public ContextClassProvider getContextClassProvider()
    {
        return contextClassProvider;
    }
    /**
     * Sets the contextClassProvider value.
     *
     * @param inContextClassProvider a <code>ContextClassProvider</code> value
     */
    public void setContextClassProvider(ContextClassProvider inContextClassProvider)
    {
        contextClassProvider = inContextClassProvider;
    }
    /**
     * Get the username value.
     *
     * @return a <code>String</code> value
     */
    public String getUsername()
    {
        return username;
    }
    /**
     * Sets the username value.
     *
     * @param inUsername a <code>String</code> value
     */
    public void setUsername(String inUsername)
    {
        username = inUsername;
    }
    /**
     * Sets the password value.
     *
     * @param inPassword a <code>String</code> value
     */
    public void setPassword(String inPassword)
    {
        password = inPassword;
    }
    /**
     * Get the heartbeatInterval value.
     *
     * @return a <code>long</code> value
     */
    public long getHeartbeatInterval()
    {
        return heartbeatInterval;
    }
    /**
     * Sets the heartbeatInterval value.
     *
     * @param inHeartbeatInterval a <code>long</code> value
     */
    public void setHeartbeatInterval(long inHeartbeatInterval)
    {
        heartbeatInterval = inHeartbeatInterval;
    }
    /**
     * Checks that the connection is up and running.
     *
     * @throws IllegalArgumentException if the connection is not running
     */
    private void checkConnection()
    {
        Validate.isTrue(isRunning());
    }
    /**
     * Indicates that an error occurred during a heartbeat request or response.
     * 
     * <p>Attempts to make an orderly shutdown of the connection.
     *
     * @param inE an <code>Exception</code> value
     */
    private void heartbeatError(Exception inE)
    {
        SLF4JLoggerProxy.error(this,
                               inE,
                               "Heartbeat failure");
        reportServerStatus(false);
        try {
            stop();
        } catch (Exception ignored) {}
    }
    /**
     * Processes updated status from the server.
     *
     * @param inUpdatedStatus a <code>boolean</code> value
     */
    private void reportServerStatus(boolean inUpdatedStatus)
    {
        if(lastReportedStatus == null || lastReportedStatus != inUpdatedStatus) {
            serverStatusPublisher.publish(inUpdatedStatus);
            lastReportedStatus = inUpdatedStatus;
        }
    }
    /**
     * Subscribes to service status changes.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    @ClassVersion("$Id$")
    private static class ServerStatusSubscriber
           implements ISubscriber
    {
        /* (non-Javadoc)
         * @see org.marketcetera.core.publisher.ISubscriber#isInteresting(java.lang.Object)
         */
        @Override
        public boolean isInteresting(Object inData)
        {
            return true;
        }
        /* (non-Javadoc)
         * @see org.marketcetera.core.publisher.ISubscriber#publishTo(java.lang.Object)
         */
        @Override
        public void publishTo(Object inData)
        {
            if(inData instanceof Boolean) {
                listener.receiveServerStatus((Boolean)inData);
            } else {
                throw new UnsupportedOperationException();
            }
        }
        /* (non-Javadoc)
         * @see java.lang.Object#hashCode()
         */
        @Override
        public int hashCode()
        {
            return new HashCodeBuilder().append(listener).toHashCode();
        }
        /* (non-Javadoc)
         * @see java.lang.Object#equals(java.lang.Object)
         */
        @Override
        public boolean equals(Object obj)
        {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (!(obj instanceof ServerStatusSubscriber)) {
                return false;
            }
            ServerStatusSubscriber other = (ServerStatusSubscriber)obj;
            return new EqualsBuilder().append(other.listener,listener).isEquals();
        }
        /**
         * Create a new ServerStatusSubscriber instance.
         *
         * @param inListener a <code>ServerStatusListener</code> value
         */
        private ServerStatusSubscriber(ServerStatusListener inListener)
        {
            listener = inListener;
        }
        /**
         * listener value to which to announce server updates
         */
        private final ServerStatusListener listener;
    }
    /**
     * tracks the last reported status value reported, may be <code>null</code>, indicating that status has not yet been reported
     */
    private volatile Boolean lastReportedStatus = null;
    /**
     * tracks subscribers to and manages publications about server status changes
     */
    private PublisherEngine serverStatusPublisher = new PublisherEngine();
    /**
     * username with which to connect to the market data service
     */
    private String username;
    /**
     * password with which to connect to the market data service
     */
    private String password;
    /**
     * hostname at which to connect to the market data service
     */
    private String hostname;
    /**
     * port at which to connect to the market data service
     */
    private int port;
    /**
     * interval at which heartbeats are executed
     */
    private long heartbeatInterval;
    /**
     * provides context classes for marshal/unmarshal
     */
    private ContextClassProvider contextClassProvider;
    /**
     * market data service connection
     */
    private MarketDataService marketDataService;
    /**
     * provides access to the service client
     */
    private Client serviceClient;
    /**
     * represents the job responsible for sending and receiving heartbeats to the server
     */
    private ScheduledFuture<?> heartbeatToken;
    /**
     * indicates if the client connection is up and running or not
     */
    private final AtomicBoolean running = new AtomicBoolean(false);
    /**
     * application ID used to verify credentials with the market data nexus
     */
    private static final AppId APP_ID = Util.getAppId("MDClient",
                                                      ApplicationVersion.getVersion().getVersionInfo());
    /**
     * executes heartbeat jobs
     */
    private static final ScheduledExecutorService heartbeatExecutor = Executors.newScheduledThreadPool(1);
}
