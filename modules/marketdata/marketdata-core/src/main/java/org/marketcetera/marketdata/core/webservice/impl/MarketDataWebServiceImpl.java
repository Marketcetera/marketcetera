package org.marketcetera.marketdata.core.webservice.impl;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.lang.Validate;
import org.marketcetera.core.publisher.ISubscriber;
import org.marketcetera.event.Event;
import org.marketcetera.marketdata.MarketDataRequest;
import org.marketcetera.marketdata.core.manager.MarketDataManager;
import org.marketcetera.marketdata.core.webservice.MarketDataWebService;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.ws.stateful.*;
import org.marketcetera.util.ws.stateless.ServiceInterface;
import org.marketcetera.util.ws.wrappers.RemoteException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.Lifecycle;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public class MarketDataWebServiceImpl
        extends ServiceBaseImpl<Object>
        implements MarketDataWebService,Lifecycle
{
    /**
     * Create a new MarketDataWebServiceImpl instance.
     *
     * @param inSessionManager a <code>SessionManager&lt;Object&gt;</code> value
     */
    public MarketDataWebServiceImpl(SessionManager<Object> inSessionManager)
    {
        super(inSessionManager);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.core.webservice.MarketDataService#request(org.marketcetera.util.ws.stateful.ClientContext, org.marketcetera.marketdata.MarketDataRequest)
     */
    @Override
    public long request(final ClientContext inContext,
                        final MarketDataRequest inRequest)
            throws RemoteException
    {
        return new RemoteCaller<Object,Long>(getSessionManager()) {
            @Override
            protected Long call(ClientContext inContext,
                                SessionHolder<Object> inSessionHolder)
                    throws Exception
            {
                SLF4JLoggerProxy.debug(this,
                                       "{} requesting {}",
                                       inContext.getSessionId(),
                                       inRequest);
                Validate.isTrue(isRunning());
                WebSubscriber subscriber = new WebSubscriber();
                long requestId = marketDataManager.requestMarketData(inRequest,
                                                                     subscriber);
                subscribersByRequestId.put(requestId,
                                           subscriber);
                SLF4JLoggerProxy.debug(this,
                                       "{} returning {} for {}",
                                       inContext,
                                       requestId,
                                       inRequest);
                return requestId;
            }
        }.execute(inContext);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.core.webservice.MarketDataWebService#cancel(org.marketcetera.util.ws.stateful.ClientContext, long)
     */
    @Override
    public void cancel(ClientContext inContext,
                       final long inRequestId)
            throws RemoteException
    {
        new RemoteCaller<Object,Void>(getSessionManager()){
            @Override
            protected Void call(ClientContext inContext,
                                SessionHolder<Object> inSessionHolder)
                    throws Exception
            {
                marketDataManager.cancelMarketDataRequest(inRequestId);
                subscribersByRequestId.remove(inRequestId);
                return null;
            }
        }.execute(inContext);
    }
    /* (non-Javadoc)
     * @see org.springframework.context.Lifecycle#isRunning()
     */
    @Override
    public boolean isRunning()
    {
        return running.get();
    }
    /* (non-Javadoc)
     * @see org.springframework.context.Lifecycle#start()
     */
    @Override
    public void start()
    {
        if(isRunning()) {
            stop();
        }
        Validate.notNull(marketDataManager);
        Validate.notNull(serverProvider);
        remoteService = serverProvider.getServer().publish(this,
                                                           MarketDataWebService.class);
        running.set(true);
    }
    /* (non-Javadoc)
     * @see org.springframework.context.Lifecycle#stop()
     */
    @Override
    public void stop()
    {
        try {
            remoteService.stop();
        } catch (RuntimeException ignored) {
        } finally {
            remoteService = null;
            subscribersByRequestId.clear();
            running.set(false);
        }
    }
    /**
     * Get the server value.
     *
     * @return a <code>ServerProvider&lt;?&gt;</code> value
     */
    public ServerProvider<?> getServer()
    {
        return serverProvider;
    }
    /**
     * Sets the server value.
     *
     * @param inServer a <code>ServerProvider&lt;?&gt;</code> value
     */
    public void setServer(ServerProvider<?> inServer)
    {
        serverProvider = inServer;
    }
    /**
     * Get the marketDataManager value.
     *
     * @return a <code>MarketDataManager</code> value
     */
    public MarketDataManager getMarketDataManager()
    {
        return marketDataManager;
    }
    /**
     * Sets the marketDataManager value.
     *
     * @param inMarketDataManager a <code>MarketDataManager</code> value
     */
    public void setMarketDataManager(MarketDataManager inMarketDataManager)
    {
        marketDataManager = inMarketDataManager;
    }
    /**
     *
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    @ClassVersion("$Id$")
    private class WebSubscriber
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
            events.add((Event)inData);
        }
        /**
         * 
         */
        private final List<Event> events = Lists.newArrayList();
    }
    /**
     * handle to the remote web service.
     */
    private ServiceInterface remoteService;
    /**
     * 
     */
    @Autowired
    private ServerProvider<?> serverProvider;
    /**
     * 
     */
    @Autowired
    private MarketDataManager marketDataManager;
    /**
     * 
     */
    private final Map<Long,WebSubscriber> subscribersByRequestId = Maps.newHashMap();
    /**
     * 
     */
    private final AtomicBoolean running = new AtomicBoolean(false);
}
