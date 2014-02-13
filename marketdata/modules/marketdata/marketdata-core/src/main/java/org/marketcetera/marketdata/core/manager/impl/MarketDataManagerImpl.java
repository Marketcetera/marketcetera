package org.marketcetera.marketdata.core.manager.impl;

import java.lang.management.ManagementFactory;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;
import javax.management.JMException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.marketcetera.core.Pair;
import org.marketcetera.core.QueueProcessor;
import org.marketcetera.core.publisher.ISubscriber;
import org.marketcetera.event.Event;
import org.marketcetera.event.HasInstrument;
import org.marketcetera.marketdata.*;
import org.marketcetera.marketdata.IFeedComponent.FeedType;
import org.marketcetera.marketdata.core.MarketDataProvider;
import org.marketcetera.marketdata.core.MarketDataProviderMBean;
import org.marketcetera.marketdata.core.ProviderStatus;
import org.marketcetera.marketdata.core.manager.*;
import org.marketcetera.marketdata.core.module.MarketDataCoreModuleFactory;
import org.marketcetera.marketdata.core.provider.AbstractMarketDataProvider;
import org.marketcetera.marketdata.core.request.MarketDataRequestAtom;
import org.marketcetera.marketdata.core.request.MarketDataRequestToken;
import org.marketcetera.module.*;
import org.marketcetera.modules.receiver.ReceiverModule;
import org.marketcetera.modules.receiver.ReceiverModuleFactory;
import org.marketcetera.trade.Instrument;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.misc.ClassVersion;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

/* $License$ */

/**
 * Routes market data requests to available providers.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: MarketDataManagerImpl.java 16454 2013-01-17 01:55:01Z colin $
 * @since $Release$
 */
@ThreadSafe
@ClassVersion("$Id$")
public class MarketDataManagerImpl
        implements MarketDataManager, MarketDataProviderRegistry
{
    /**
     * Gets the singleton instance of this class.
     *
     * @return a <code>MarketDataManagerImpl</code> value or <code>null</code> if this class has not yet been instantiated
     */
    public static MarketDataManagerImpl getInstance()
    {
        return instance;
    }
    /**
     * Create a new MarketDataManagerImpl instance.
     */
    public MarketDataManagerImpl()
    {
        instance = this;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.manager.MarketDataProviderRegistry#setStatus(org.marketcetera.marketdata.provider.MarketDataProvider, org.marketcetera.marketdata.FeedStatus)
     */
    @Override
    public void setStatus(MarketDataProvider inProvider,
                          ProviderStatus inStatus)
    {
        SLF4JLoggerProxy.info(this,
                              "Market data provider {} reports status {}", // TODO
                              inProvider.getProviderName(),
                              inStatus);
        Lock statusUpdateLock = requestLockObject.writeLock();
        try {
            ObjectName providerObjectName = getObjectNameFor(inProvider);
            statusUpdateLock.lockInterruptibly();
            if(inStatus == ProviderStatus.AVAILABLE) {
                // TODO check for duplicate provider name and warn
                activeProvidersByName.put(inProvider.getProviderName(),
                                          inProvider);
                if(!mbeanServer.isRegistered(providerObjectName)) {
                    mbeanServer.registerMBean((MarketDataProviderMBean)inProvider,
                                              providerObjectName); 
                }
            } else {
                activeProvidersByName.remove(inProvider.getProviderName());
            }
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
        } catch (JMException e) {
            SLF4JLoggerProxy.warn(this,
                                  e,
                                  "Unable to register/unregister JMX interface for {} market data provider", // TODO
                                  inProvider.getProviderName());
        } finally {
            statusUpdateLock.unlock();
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.manager.MarketDataManager#requestMarketData(org.marketcetera.marketdata.request.MarketDataRequest, org.marketcetera.api.systemmodel.Subscriber)
     */
    @Override
    public long requestMarketData(MarketDataRequest inRequest,
                                  ISubscriber inSubscriber)
    {
        SLF4JLoggerProxy.debug(this,
                               "Received: {}",
                               inRequest);
        // route the request to available providers or to a particular provider
        Collection<MarketDataProvider> successfulProviders = new ArrayList<MarketDataProvider>();
        MarketDataRequestToken token = new Token(inSubscriber,
                                                 inRequest);
        if(inRequest.getProvider() != null) {
            // a specific provider was requested - use that provider only
            MarketDataProvider provider = getMarketDataProviderForName(inRequest.getProvider());
            if(provider == null) {
                throw new MarketDataProviderNotAvailable();
            }
            SLF4JLoggerProxy.debug(this,
                                   "Submitting {} to {}",
                                   token,
                                   provider);
            try {
                provider.requestMarketData(token);
            } catch (RuntimeException e) {
                throw new MarketDataException(e);
            }
            successfulProviders.add(provider);
        } else {
            for(MarketDataProvider provider : getActiveMarketDataProviders()) {
                try {
                    SLF4JLoggerProxy.debug(this,
                                           "Submitting {} to {}",
                                           token,
                                           provider);
                    provider.requestMarketData(token);
                    successfulProviders.add(provider);
                } catch (RuntimeException e) {
                    SLF4JLoggerProxy.warn(this,
                                          e,
                                          "Unable to request market data {} from {}", // TODO
                                          inRequest,
                                          provider.getProviderName());
                    // continue to try from the next provider
                }
            }
            if(successfulProviders.isEmpty()) {
                throw new NoMarketDataProvidersAvailable();
            }
        }
        Lock requestLock = requestLockObject.writeLock();
        try {
            requestLock.lockInterruptibly();
            for(MarketDataProvider provider : successfulProviders) {
                providersByToken.put(token,
                                     provider);
            }
            tokensByTokenId.put(token.getId(),
                                token);
        } catch (InterruptedException e) {
            SLF4JLoggerProxy.warn(this,
                                  "Market data request {} interrupted", // TODO
                                  inRequest);
            throw new MarketDataRequestTimedOut(e);
        } finally {
            requestLock.unlock();
        }
        return token.getId();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.manager.MarketDataManager#cancelMarketDataRequest(org.marketcetera.api.systemmodel.Subscriber)
     */
    @Override
    public void cancelMarketDataRequest(long inRequestId)
    {
        SLF4JLoggerProxy.debug(this,
                               "Canceling request {}",
                               inRequestId);
        Lock cancelLock = requestLockObject.writeLock();
        try {
            cancelLock.lockInterruptibly();
            MarketDataRequestToken token = tokensByTokenId.remove(inRequestId);
            if(token != null) {
                for(MarketDataProvider provider : providersByToken.removeAll(token)) {
                    SLF4JLoggerProxy.debug(this,
                                           "Canceling request {} with {}",
                                           inRequestId,
                                           provider);
                    provider.cancelMarketDataRequest(token);
                }
            }
        } catch (InterruptedException ignored) {
        } finally {
            cancelLock.unlock();
        }
    }
    /**
     * Constructs a unique <code>ObjectName</code> for the given provider.
     *
     * @param inProvider a <code>MarketDataProvider</code> value
     * @return an <code>ObjectName</code> value
     * @throws MalformedObjectNameException if the provider name is invalid
     */
    private ObjectName getObjectNameFor(MarketDataProvider inProvider)
            throws MalformedObjectNameException
    {
        return new ObjectName("org.marketcetera.marketdata.provider:name=" + inProvider.getProviderName());
    }
    /**
     * Gets a market data provider for the given name.
     *
     * @param inProviderName a <code>String</code> value
     * @return a <code>MarketDataProvider</code> value
     */
    private MarketDataProvider getMarketDataProviderForName(String inProviderName)
    {
        populateProviderList();
        return activeProvidersByName.get(inProviderName);
    }
    /**
     * 
     *
     *
     * @return
     */
    private Collection<MarketDataProvider> getActiveMarketDataProviders()
    {
        populateProviderList();
        return activeProvidersByName.values();
    }
    /**
     * 
     *
     *
     */
    private void populateProviderList()
    {
        List<ModuleURN> providerUrns = ModuleManager.getInstance().getProviders();
        for(ModuleURN providerUrn : providerUrns) {
            String providerName = providerUrn.providerName();
            if(providerUrn.providerType().equals("mdata") && !providerName.equals(MarketDataCoreModuleFactory.IDENTIFIER) && !activeProvidersByName.containsKey(providerName)) {
                List<ModuleURN> instanceUrns = ModuleManager.getInstance().getModuleInstances(providerUrn);
                if(!instanceUrns.isEmpty()) {
                    ModuleURN instanceUrn = instanceUrns.get(0);
                    ModuleInfo info = ModuleManager.getInstance().getModuleInfo(instanceUrn);
                    if(info.getState() == ModuleState.STARTED) {
                        ModuleProvider provider = new ModuleProvider(providerName,
                                                                     AbstractMarketDataModule.getFeedForProviderName(providerName));
                        SLF4JLoggerProxy.debug(this,
                                               "Creating market data provider proxy for {}",
                                               providerName);
                        activeProvidersByName.put(providerName,
                                                  provider);
                    }
                }
            }
        }
    }
    /**
     *
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id: MarketDataManagerImpl.java 16454 2013-01-17 01:55:01Z colin $
     * @since $Release$
     */
    @Immutable
    @ClassVersion("$Id$")
    private class Token
            implements MarketDataRequestToken
    {
        /* (non-Javadoc)
         * @see org.marketcetera.marketdata.request.MarketDataRequestToken#getId()
         */
        @Override
        public long getId()
        {
            return id;
        }
        /* (non-Javadoc)
         * @see org.marketcetera.marketdata.request.MarketDataRequestToken#getSubscriber()
         */
        @Override
        public ISubscriber getSubscriber()
        {
            return subscriber;
        }
        /* (non-Javadoc)
         * @see org.marketcetera.marketdata.request.MarketDataRequestToken#getRequest()
         */
        @Override
        public MarketDataRequest getRequest()
        {
            return request;
        }
        /* (non-Javadoc)
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString()
        {
            return new StringBuilder().append("Token [").append(id).append("] ").append(request).toString();
        }
        /* (non-Javadoc)
         * @see java.lang.Object#hashCode()
         */
        @Override
        public int hashCode()
        {
            return new HashCodeBuilder().append(id).toHashCode();
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
            if (!(obj instanceof Token)) {
                return false;
            }
            Token other = (Token) obj;
            return new EqualsBuilder().append(id,other.getId()).isEquals();
        }
        /**
         * Create a new Token instance.
         *
         * @param inSubscriber an <code>ISubscriber</code> value
         * @param inRequest a <code>MarketDataRequest</code> value
         */
        private Token(ISubscriber inSubscriber,
                      MarketDataRequest inRequest)
        {
            subscriber = inSubscriber;
            request = inRequest;
        }
        /**
         * 
         */
        private final long id = requestCounter.incrementAndGet();
        /**
         * 
         */
        private final ISubscriber subscriber;
        /**
         * 
         */
        private final MarketDataRequest request;
        private static final long serialVersionUID = 1L;
    }
    /**
     * Creates an interface between the newer {@link MarketDataProvider} and the older, module-based {@link MarketDataFeed}.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    @ClassVersion("$Id$")
    private class ModuleProvider
            extends AbstractMarketDataProvider
    {
        /* (non-Javadoc)
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString()
        {
            return description;
        }
        /**
         * Create a new ModuleProvider instance.
         *
         * @param inProviderName a <code>String</code> value
         * @param inFeed an <code>AbstractMarketDataModule&lt;?,?&gt;</code> value
         */
        private ModuleProvider(String inProviderName,
                               AbstractMarketDataModule<?,?> inFeed)
        {
            providerName = inProviderName;
            description = providerName + " proxy";
            feed = inFeed;
            moduleManager = ModuleManager.getInstance();
            processor = new Processor();
            // TODO register a feed status listener
            start();
        }
        /* (non-Javadoc)
         * @see org.marketcetera.marketdata.core.MarketDataProvider#getProviderName()
         */
        @Override
        public String getProviderName()
        {
            return providerName;
        }
        /* (non-Javadoc)
         * @see org.marketcetera.marketdata.core.MarketDataProvider#getCapabilities()
         */
        @Override
        public Set<Capability> getCapabilities()
        {
            return feed.getCapabilities();
        }
        /* (non-Javadoc)
         * @see org.marketcetera.marketdata.core.MarketDataProvider#getFeedType()
         */
        @Override
        public FeedType getFeedType()
        {
            return feed.getFeedType();
        }
        /* (non-Javadoc)
         * @see org.marketcetera.marketdata.core.provider.AbstractMarketDataProvider#doStart()
         */
        @Override
        protected void doStart()
        {
            processor.start();
            String receiverInstanceName = "MDMPROXY" + requestCounter.incrementAndGet();
            moduleManager.createModule(ReceiverModuleFactory.PROVIDER_URN,
                                       receiverInstanceName);
            receiver = ReceiverModule.getModuleForInstanceName(receiverInstanceName);
        }
        /* (non-Javadoc)
         * @see org.marketcetera.marketdata.core.provider.AbstractMarketDataProvider#doStop()
         */
        @Override
        protected void doStop()
        {
            processor.stop();
            if(receiver != null) {
                try {
                    moduleManager.stop(receiver.getURN());
                    moduleManager.deleteModule(receiver.getURN());
                } catch (RuntimeException ignored) {}
                receiver = null;
            }
        }
        /* (non-Javadoc)
         * @see org.marketcetera.marketdata.core.provider.AbstractMarketDataProvider#doCancel(org.marketcetera.marketdata.core.request.MarketDataRequestAtom)
         */
        @Override
        protected void doCancel(MarketDataRequestAtom inAtom)
        {
            Request request = requestsByAtom.remove(inAtom);
            if(request != null) {
                try {
                    moduleManager.getDataFlowInfo(request.flow);
                    moduleManager.cancel(request.flow);
                } catch (DataFlowNotFoundException ignored) {}
                requestsByFlow.remove(request.flow);
            }
        }
        /* (non-Javadoc)
         * @see org.marketcetera.marketdata.core.provider.AbstractMarketDataProvider#doMarketDataRequest(org.marketcetera.marketdata.MarketDataRequest, org.marketcetera.marketdata.core.request.MarketDataRequestAtom)
         */
        @Override
        protected void doMarketDataRequest(MarketDataRequest inCompleteRequest,
                                           MarketDataRequestAtom inRequestAtom)
                throws InterruptedException
        {
            Request request = new Request(inRequestAtom,
                                          inCompleteRequest);
            requestsByAtom.put(inRequestAtom,
                               request);
            requestsByFlow.put(request.flow,
                               request);
        }
        /**
         *
         *
         * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
         * @version $Id$
         * @since $Release$
         */
        @ClassVersion("$Id$")
        private class Request
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
            @SuppressWarnings("unchecked")
            @Override
            public void publishTo(Object inData)
            {
                processor.add((Pair<DataFlowID,Event>)inData);
            }
            /**
             * Create a new Request instance.
             *
             * @param inAtom
             * @param inCompleteRequest
             */
            private Request(MarketDataRequestAtom inAtom,
                            MarketDataRequest inCompleteRequest)
            {
                atom = inAtom;
                receiver.subscribe(this);
                flow = moduleManager.createDataFlow(new DataRequest[] { new DataRequest(feed.getURN(),generateRequestFromAtom(inAtom,inCompleteRequest)),new DataRequest(receiver.getURN()) });
            }
            /**
             * 
             *
             *
             * @param inAtom
             * @param inCompleteRequest
             * @return
             */
            private MarketDataRequest generateRequestFromAtom(MarketDataRequestAtom inAtom,
                                                              MarketDataRequest inCompleteRequest)
            {
                return MarketDataRequestBuilder.newRequest().withAssetClass(inCompleteRequest.getAssetClass()).withSymbols(inAtom.getSymbol()).withContent(inAtom.getContent()).withExchange(inAtom.getExchange()).create();
            }
            /**
             * 
             */
            private final DataFlowID flow;
            /**
             * 
             */
            private final MarketDataRequestAtom atom;
        }
        /**
         * Processes returned data for a proxied market data provider.
         *
         * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
         * @version $Id$
         * @since $Release$
         */
        @ClassVersion("$Id$")
        private class Processor
                extends QueueProcessor<Pair<DataFlowID,Event>>
        {
            private Processor()
            {
                super(providerName+"ProxyProcessor");
            }
            /**
             * 
             *
             *
             * @param inData
             */
            private void add(Pair<DataFlowID,Event> inData)
            {
                super.getQueue().add(inData);
            }
            /* (non-Javadoc)
             * @see org.marketcetera.core.QueueProcessor#processData(java.lang.Object)
             */
            @Override
            protected void processData(Pair<DataFlowID,Event> inData)
                    throws Exception
            {
                Request request = null;
                while(request == null) {
                    // wait until the data flow has been mapped before proceeding - should be minimal delay
                    Thread.sleep(100);
                    request = requestsByFlow.get(inData.getFirstMember());
                }
                Event event = (Event)inData.getSecondMember();
                Instrument instrument = null;
                if(event instanceof HasInstrument) {
                    HasInstrument hi = (HasInstrument)event;
                    instrument = hi.getInstrument();
                    if(mappedSymbols.add(request.atom.getSymbol())) {
                        addSymbolMapping(request.atom.getSymbol(),
                                         hi.getInstrument());
                    }
                    publishEvents(request.atom.getContent(),
                                  instrument,
                                  event);
                }
            }
            /* (non-Javadoc)
             * @see org.marketcetera.core.QueueProcessor#shutdownOnException(java.lang.Exception)
             */
            @Override
            protected boolean shutdownOnException(Exception inException)
            {
                return !(inException instanceof InterruptedException);
            }
            /**
             * 
             */
            private final Set<String> mappedSymbols = Sets.newHashSet();
        }
        /**
         * 
         */
        private final Processor processor;
        /**
         * 
         */
        private final Map<MarketDataRequestAtom,Request> requestsByAtom = Maps.newHashMap();
        /**
         * 
         */
        private final Map<DataFlowID,Request> requestsByFlow = Maps.newHashMap();
        /**
         * 
         */
        private ReceiverModule receiver;
        /**
         * 
         */
        private final ModuleManager moduleManager;
        /**
         * 
         */
        private final String providerName;
        /**
         * 
         */
        private final String description;
        /**
         * 
         */
        private final AbstractMarketDataModule<?,?> feed;
    }
    /**
     * 
     */
    private static MarketDataManagerImpl instance;
    /**
     * 
     */
    @GuardedBy("requestLockObject")
    private final Map<String,MarketDataProvider> activeProvidersByName = new HashMap<String,MarketDataProvider>();
    /**
     * 
     */
    private final ReadWriteLock requestLockObject = new ReentrantReadWriteLock();
    /**
     * 
     */
    private final Map<Long,MarketDataRequestToken> tokensByTokenId = new HashMap<Long,MarketDataRequestToken>();
    /**
     * 
     */
    private final Multimap<MarketDataRequestToken,MarketDataProvider> providersByToken = HashMultimap.create();
    /**
     * 
     */
    private final AtomicLong requestCounter = new AtomicLong(0);
    /**
     * 
     */
    private final MBeanServer mbeanServer = ManagementFactory.getPlatformMBeanServer();
}
