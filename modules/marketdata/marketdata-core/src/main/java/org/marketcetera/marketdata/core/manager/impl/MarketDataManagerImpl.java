package org.marketcetera.marketdata.core.manager.impl;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.concurrent.atomic.AtomicInteger;
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

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.marketcetera.core.Pair;
import org.marketcetera.core.QueueProcessor;
import org.marketcetera.core.publisher.ISubscriber;
import org.marketcetera.event.Event;
import org.marketcetera.event.HasInstrument;
import org.marketcetera.marketdata.AbstractMarketDataModule;
import org.marketcetera.marketdata.Capability;
import org.marketcetera.marketdata.Content;
import org.marketcetera.marketdata.IFeedComponent.FeedType;
import org.marketcetera.marketdata.MarketDataFeed;
import org.marketcetera.marketdata.MarketDataRequest;
import org.marketcetera.marketdata.MarketDataRequestBuilder;
import org.marketcetera.marketdata.core.MarketDataProvider;
import org.marketcetera.marketdata.core.MarketDataProviderMBean;
import org.marketcetera.marketdata.core.Messages;
import org.marketcetera.marketdata.core.ProviderStatus;
import org.marketcetera.marketdata.core.manager.MarketDataException;
import org.marketcetera.marketdata.core.manager.MarketDataManager;
import org.marketcetera.marketdata.core.manager.MarketDataProviderNotAvailable;
import org.marketcetera.marketdata.core.manager.MarketDataProviderRegistry;
import org.marketcetera.marketdata.core.manager.MarketDataRequestFailed;
import org.marketcetera.marketdata.core.manager.MarketDataRequestTimedOut;
import org.marketcetera.marketdata.core.manager.NoMarketDataProvidersAvailable;
import org.marketcetera.marketdata.core.module.MarketDataCoreModuleFactory;
import org.marketcetera.marketdata.core.module.ReceiverModule;
import org.marketcetera.marketdata.core.module.ReceiverModuleFactory;
import org.marketcetera.marketdata.core.provider.AbstractMarketDataProvider;
import org.marketcetera.marketdata.core.request.MarketDataRequestAtom;
import org.marketcetera.marketdata.core.request.MarketDataRequestToken;
import org.marketcetera.module.DataFlowID;
import org.marketcetera.module.DataFlowNotFoundException;
import org.marketcetera.module.DataRequest;
import org.marketcetera.module.ModuleInfo;
import org.marketcetera.module.ModuleManager;
import org.marketcetera.module.ModuleNotFoundException;
import org.marketcetera.module.ModuleURN;
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
 * @version $Id$
 * @since 2.4.0
 */
@ThreadSafe
@ClassVersion("$Id$")
public class MarketDataManagerImpl
        implements MarketDataManager,MarketDataProviderRegistry
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
        Messages.PROVIDER_REPORTS_STATUS.info(this,
                                              inProvider.getProviderName(),
                                              inStatus);
        Lock statusUpdateLock = requestLockObject.writeLock();
        try {
            ObjectName providerObjectName = getObjectNameFor(inProvider);
            statusUpdateLock.lockInterruptibly();
            if(inStatus == ProviderStatus.AVAILABLE) {
                // TODO check for duplicate provider name and warn
                addProvider(inProvider);
                if(!mbeanServer.isRegistered(providerObjectName)) {
                    mbeanServer.registerMBean((MarketDataProviderMBean)inProvider,
                                              providerObjectName); 
                }
            } else {
                removeProvider(inProvider);
            }
        } catch (InterruptedException e) {
            throw new MarketDataRequestFailed(e);
        } catch (JMException e) {
            Messages.JMX_REGISTRATION_ERROR.warn(this,
                                                 e,
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
        String providerName = StringUtils.trimToNull(inRequest.getProvider());
        if(MarketDataCoreModuleFactory.IDENTIFIER.equals(providerName)) {
            providerName = null;
        }
        if(providerName != null) {
            // a specific provider was requested - use that provider only
            MarketDataProvider provider = getMarketDataProviderForName(providerName);
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
            boolean liveRequestSubmitted = false;
            for(MarketDataProvider provider : getActiveMarketDataProviders()) {
                if(liveRequestSubmitted && provider.getFeedType() != FeedType.LIVE) {
                    SLF4JLoggerProxy.debug(this,
                                           "Request has been submitted to all live feeds, no more requests will be issued");
                    break;
                }
                try {
                    SLF4JLoggerProxy.debug(this,
                                           "Submitting {} to {} [{}]",
                                           token,
                                           provider,
                                           provider.getFeedType());
                    provider.requestMarketData(token);
                    successfulProviders.add(provider);
                    if(provider.getFeedType() == FeedType.LIVE) {
                        liveRequestSubmitted = true;
                    }
                } catch (RuntimeException e) {
                    Messages.UNABLE_TO_REQUEST_MARKETDATA.warn(this,
                                                               e,
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
            Messages.MARKETDATA_REQUEST_INTERRUPTED.warn(this,
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
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.core.manager.MarketDataManager#requestMarketDataSnapshot(org.marketcetera.trade.Instrument, org.marketcetera.marketdata.Content, java.lang.String)
     */
    @Override
    public Event requestMarketDataSnapshot(Instrument inInstrument,
                                           Content inContent,
                                           String inProvider)
    {
        if(inProvider == null) {
            SortedSet<Pair<FeedType,Event>> snapshotCandidates = Sets.newTreeSet(SnapshotComparator.INSTANCE);
            for(MarketDataProvider provider : getActiveMarketDataProviders()) {
                Event snapshotCandidate = provider.getSnapshot(inInstrument,
                                                               inContent);
                if(snapshotCandidate != null) {
                    snapshotCandidates.add(Pair.create(provider.getFeedType(),
                                                       snapshotCandidate));
                }
            }
            if(snapshotCandidates.isEmpty()) {
                return null;
            }
            return snapshotCandidates.first().getSecondMember();
        } else {
            MarketDataProvider provider = getMarketDataProviderForName(inProvider);
            if(provider == null) {
                throw new MarketDataProviderNotAvailable();
            }
            return provider.getSnapshot(inInstrument,
                                        inContent);
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.core.manager.MarketDataManager#getAvailableCapability()
     */
    @Override
    public Set<Capability> getAvailableCapability()
    {
        Set<Capability> capabilities = Sets.newHashSet();
        boolean includesLiveData = false;
        for(MarketDataProvider provider : getActiveMarketDataProviders()) {
            if(provider.getFeedType() == FeedType.LIVE) {
                includesLiveData = true;
            }
            if(includesLiveData && provider.getFeedType() != FeedType.LIVE) {
                // we've looked at at least one live provider and providers are non-live from here on, so quite
                break;
            }
            capabilities.addAll(provider.getCapabilities());
        }
        return capabilities;
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
     * Gets the currently active market data providers.
     *
     * @return a <code>Collection&lt;MarketDataProvider&gt;</code> value
     */
    private Collection<MarketDataProvider> getActiveMarketDataProviders()
    {
        populateProviderList();
        return providersByPriority;
    }
    /**
     * Removes the provider from the collection of active providers.
     *
     * @param inProvider a <code>MarketDataProvider</code> value
     */
    private void removeProvider(MarketDataProvider inProvider)
    {
        activeProvidersByName.remove(inProvider.getProviderName());
        providersByPriority.remove(inProvider);
    }
    /**
     * Adds the given provider to the collection of active providers.
     *
     * @param inProvider a <code>MarketDataProvider</code> value
     */
    private void addProvider(MarketDataProvider inProvider)
    {
        activeProvidersByName.put(inProvider.getProviderName(),
                                  inProvider);
        providersByPriority.add(inProvider);
    }
    /**
     * Populates the provider list with provider proxies that are implemented as old school modules.
     */
    private void populateProviderList()
    {
        ModuleManager moduleManager = ModuleManager.getInstance();
        synchronized(activeProvidersByName) {
            if(ModuleManager.getInstance() != null) {
                List<ModuleURN> providerUrns = moduleManager.getProviders();
                for(ModuleURN providerUrn : providerUrns) {
                    String providerName = providerUrn.providerName();
                    if(providerUrn.providerType().equals(MDATA) && !providerName.equals(MarketDataCoreModuleFactory.IDENTIFIER)) {
                        List<ModuleURN> instanceUrns = moduleManager.getModuleInstances(providerUrn);
                        if(!instanceUrns.isEmpty()) {
                            ModuleURN instanceUrn = instanceUrns.get(0);
                            ModuleInfo info = moduleManager.getModuleInfo(instanceUrn);
                            if(info.getState().isStarted()) {
                                try {
                                    ModuleURN proxyModuleUrn = getModuleProviderInstanceUrn(providerName);
                                    ModuleInfo proxyModuleInfo = moduleManager.getModuleInfo(proxyModuleUrn);
                                    if(proxyModuleInfo.getState().isStarted()) {
                                        continue;
                                    }
                                    moduleManager.start(proxyModuleUrn);
                                    continue;
                                } catch (ModuleNotFoundException ignored) {}
                                ModuleProvider provider = new ModuleProvider(providerName,
                                                                             AbstractMarketDataModule.getFeedForProviderName(providerName));
                                SLF4JLoggerProxy.debug(this,
                                                       "Creating market data provider proxy for {}",
                                                       providerName);
                                addProvider(provider);
                            }
                        }
                    }
                }
            }
        }
    }
    /**
     * Generates the ModuleURN for a proxy module.
     *
     * @param inProviderName a <code>String</code> value
     * @return a <code>ModuleURN</code> value
     */
    private static ModuleURN getModuleProviderInstanceUrn(String inProviderName)
    {
        return new ModuleURN(ReceiverModuleFactory.PROVIDER_URN,
                             "MDMPROXY_" + inProviderName);
    }
    /**
     * Identifies a particular market data request.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since 2.4.0
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
         * unique identifier for this token
         */
        private final long id = requestCounter.incrementAndGet();
        /**
         * subscriber to which updates should be sent
         */
        private final ISubscriber subscriber;
        /**
         * request object indicating what data is desired
         */
        private final MarketDataRequest request;
        private static final long serialVersionUID = 1L;
    }
    /**
     * Creates an interface between the newer {@link MarketDataProvider} and the older, module-based {@link MarketDataFeed}.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since 2.4.0
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
            processor = new Processor();
            // TODO register a feed status listener to detect starts and stops and update status
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
            ModuleURN receiverInstanceUrn = getModuleProviderInstanceUrn(providerName);
            ModuleManager.getInstance().createModule(ReceiverModuleFactory.PROVIDER_URN,
                                                     receiverInstanceUrn.instanceName());
            receiver = ReceiverModule.getModuleForInstanceName(receiverInstanceUrn.instanceName());
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
                    ModuleManager.getInstance().stop(receiver.getURN());
                    ModuleManager.getInstance().deleteModule(receiver.getURN());
                } catch (RuntimeException ignored) {
                } finally {
                    removeProvider(this);
                }
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
                requestsByFlow.remove(request.flow);
                request.cancel();
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
            SLF4JLoggerProxy.debug(this,
                                   "{} created {} for {}",
                                   this,
                                   request,
                                   inRequestAtom);
        }
        /**
         * Represents a market data request submitted to a proxy market data provider.
         *
         * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
         * @version $Id$
         * @since 2.4.0
         */
        @ClassVersion("$Id$")
        private class Request
                implements ISubscriber
        {
            /* (non-Javadoc)
             * @see java.lang.Object#toString()
             */
            @Override
            public String toString()
            {
                return new StringBuilder().append("proxy request for ").append(atom).append(" via flow ").append(flow).toString();
            }
            /* (non-Javadoc)
             * @see org.marketcetera.core.publisher.ISubscriber#isInteresting(java.lang.Object)
             */
            @Override
            public boolean isInteresting(Object inData)
            {
                if(inData instanceof Pair<?,?>) {
                    Pair<?,?> data = (Pair<?,?>)inData;
                    if(data.getFirstMember() instanceof DataFlowID) {
                        DataFlowID dataFlowID = (DataFlowID)data.getFirstMember();
                        if(dataFlowID.equals(flow)) {
                            return true;
                        }
                    }
                }
                return false;
            }
            /* (non-Javadoc)
             * @see org.marketcetera.core.publisher.ISubscriber#publishTo(java.lang.Object)
             */
            @Override
            public void publishTo(Object inData)
            {
                if(inData instanceof Pair<?,?>) {
                    Pair<?,?> pairData = (Pair<?,?>)inData;
                    Object secondMember = pairData.getSecondMember();
                    if(secondMember instanceof Event) {
                        Event event = (Event)secondMember;
                        if(atom.getContent().isRelevantTo(event.getClass())) {
                            Pair<MarketDataRequestAtom,Event> toProcess = Pair.create(atom,event);
                            processor.add(toProcess);
                        }
                        return;
                    }
                }
                throw new UnsupportedOperationException();
            }
            /**
             * Cancels this request.
             */
            private void cancel()
            {
                receiver.unsubscribe(this);
                try {
                    ModuleManager.getInstance().getDataFlowInfo(flow);
                    ModuleManager.getInstance().cancel(flow);
                } catch (DataFlowNotFoundException ignored) {}
            }
            /**
             * Create a new Request instance.
             *
             * @param inAtom a <code>MarketDataRequestAtom</code> value
             * @param inCompleteRequest a <code>MarketDataRequest</code> value
             */
            private Request(MarketDataRequestAtom inAtom,
                            MarketDataRequest inCompleteRequest)
            {
                atom = inAtom;
                receiver.subscribe(this);
                ModuleManager moduleManager = ModuleManager.getInstance();
                flow = moduleManager.createDataFlow(new DataRequest[] { new DataRequest(feed.getURN(),generateRequestFromAtom(inAtom,inCompleteRequest)),new DataRequest(receiver.getURN()) });
            }
            /**
             * Generates a <code>MarktDataRequest</code> from the given request atom.
             *
             * @param inAtom a <code>MarketDataRequestAtom</code> value
             * @param inCompleteRequest a <code>MarketDataRequest</code> value
             * @return a <code>MarketDataRequest</code> value
             */
            private MarketDataRequest generateRequestFromAtom(MarketDataRequestAtom inAtom,
                                                              MarketDataRequest inCompleteRequest)
            {
                return MarketDataRequestBuilder.newRequest().withAssetClass(inCompleteRequest.getAssetClass()).withSymbols(inAtom.getSymbol()).withContent(inAtom.getContent()).withExchange(inAtom.getExchange()).create();
            }
            /**
             * data flow for this request
             */
            private final DataFlowID flow;
            /**
             * data requested
             */
            private final MarketDataRequestAtom atom;
        }
        /**
         * Processes returned data for a proxied market data provider.
         *
         * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
         * @version $Id$
         * @since 2.4.0
         */
        @ClassVersion("$Id$")
        private class Processor
                extends QueueProcessor<Pair<MarketDataRequestAtom,Event>>
        {
            /* (non-Javadoc)
             * @see org.marketcetera.core.QueueProcessor#processData(java.lang.Object)
             */
            @Override
            protected void processData(Pair<MarketDataRequestAtom,Event> inData)
                    throws Exception
            {
                MarketDataRequestAtom atom = inData.getFirstMember();
                Event event = inData.getSecondMember();
                Instrument instrument = null;
                if(event instanceof HasInstrument) {
                    HasInstrument hi = (HasInstrument)event;
                    instrument = hi.getInstrument();
                    if(mappedSymbols.add(atom.getSymbol())) {
                        addSymbolMapping(atom.getSymbol(),
                                         hi.getInstrument());
                    }
                    publishEvents(atom.getContent(),
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
             * Create a new Processor instance.
             */
            private Processor()
            {
                super(providerName+"ProxyProcessor-"+proxyCounter.incrementAndGet());
            }
            /**
             * Adds data to the processing queue to be processed.
             *
             * @param inData a <code>Pair&lt;MarketDataRequestAtom,Event&gt;</code> value
             */
            @Override
            protected void add(Pair<MarketDataRequestAtom,Event> inData)
            {
                super.add(inData);
            }
            /**
             * symbols that have already been mapped
             */
            private final Set<String> mappedSymbols = Sets.newHashSet();
        }
        /**
         * processing queue for market data
         */
        private final Processor processor;
        /**
         * market data requests by the atom being requested
         */
        private final Map<MarketDataRequestAtom,Request> requestsByAtom = Maps.newHashMap();
        /**
         * market data requests by data flow associated with a request
         */
        private final Map<DataFlowID,Request> requestsByFlow = Maps.newHashMap();
        /**
         * receiver module for this proxied provider
         */
        private ReceiverModule receiver;
        /**
         * provider name
         */
        private final String providerName;
        /**
         * provider description
         */
        private final String description;
        /**
         * underlying feed module to which requests are sent
         */
        private final AbstractMarketDataModule<?,?> feed;
    }
    /**
     * Compares two snapshots to determine the precedence.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since 2.4.0
     */
    @ClassVersion("$Id$")
    private static class SnapshotComparator
            implements Comparator<Pair<FeedType,Event>>
    {
        /* (non-Javadoc)
         * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
         */
        @Override
        public int compare(Pair<FeedType,Event> inLeft,
                           Pair<FeedType,Event> inRight)
        {
            if(inLeft.getFirstMember() == FeedType.LIVE) {
                if(inRight.getFirstMember() != FeedType.LIVE) {
                    return -1;
                }
            } else {
                if(inRight.getFirstMember() == FeedType.LIVE) {
                    return 1;
                }
            }
            return new CompareToBuilder().append(inLeft.getSecondMember().getTimeMillis(),inRight.getSecondMember().getTimeMillis()).toComparison();
        }
        /**
         * static, threadsafe instance
         */
        private static final SnapshotComparator INSTANCE = new SnapshotComparator();
    }
    /**
     * Compares two providers to determine which is the preferred.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since 2.4.0
     */
    @ClassVersion("$Id$")
    private static class ProviderComparator
            implements Comparator<MarketDataProvider>
    {
        /* (non-Javadoc)
         * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
         */
        @Override
        public int compare(MarketDataProvider inLeft,
                           MarketDataProvider inRight)
        {
            if(inLeft.getFeedType() == FeedType.LIVE) {
                if(inRight.getFeedType() != FeedType.LIVE) {
                    return -1;
                }
            }
            if(inRight.getFeedType() == FeedType.LIVE) {
                return 1;
            }
            return new CompareToBuilder().append(inLeft.getProviderName(),inRight.getProviderName()).toComparison();
        }
        /**
         * static, threadsafe instance
         */
        private static final ProviderComparator INSTANCE = new ProviderComparator();
    }
    /**
     * stores active providers by their desirability
     */
    private SortedSet<MarketDataProvider> providersByPriority = Sets.newTreeSet(ProviderComparator.INSTANCE);
    /**
     * static instance used to provide access to non-Spring objects
     */
    private static MarketDataManagerImpl instance;
    /**
     * tracks all active providers by provider name
     */
    @GuardedBy("requestLockObject")
    private final Map<String,MarketDataProvider> activeProvidersByName = new HashMap<String,MarketDataProvider>();
    /**
     * used to control access to critical data
     */
    private final ReadWriteLock requestLockObject = new ReentrantReadWriteLock();
    /**
     * tracks market data tokens by token id
     */
    private final Map<Long,MarketDataRequestToken> tokensByTokenId = new HashMap<Long,MarketDataRequestToken>();
    /**
     * associates each request token to the provider or providers to which it was directed
     */
    private final Multimap<MarketDataRequestToken,MarketDataProvider> providersByToken = HashMultimap.create();
    /**
     * used to generate unique ids
     */
    private final AtomicLong requestCounter = new AtomicLong(0);
    /**
     * used to set up MX interfaces
     */
    private final MBeanServer mbeanServer = ManagementFactory.getPlatformMBeanServer();
    /**
     * provider name for market data provider modules
     */
    private static final String MDATA = "mdata";
    /**
     * used to generate unique proxy processor identifiers
     */
    private static final AtomicInteger proxyCounter = new AtomicInteger(0);
}
