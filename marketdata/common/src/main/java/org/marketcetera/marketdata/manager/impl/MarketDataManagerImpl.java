package org.marketcetera.marketdata.manager.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.marketcetera.api.systemmodel.Subscriber;
import org.marketcetera.core.util.log.SLF4JLoggerProxy;
import org.marketcetera.marketdata.FeedStatus;
import org.marketcetera.marketdata.manager.*;
import org.marketcetera.marketdata.provider.MarketDataProvider;
import org.marketcetera.marketdata.request.MarketDataRequest;
import org.marketcetera.marketdata.request.MarketDataRequestToken;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

/* $License$ */

/**
 * Routes market data requests to available providers.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ThreadSafe
public class MarketDataManagerImpl
        implements MarketDataManager, MarketDataProviderRegistry
{
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.manager.MarketDataProviderRegistry#setStatus(org.marketcetera.marketdata.provider.MarketDataProvider, org.marketcetera.marketdata.FeedStatus)
     */
    @Override
    public void setStatus(MarketDataProvider inProvider,
                          FeedStatus inStatus)
    {
        SLF4JLoggerProxy.info(this,
                              "Market data provider {} reports status {}", // TODO
                              inProvider.getProviderName(),
                              inStatus);
        Lock statusUpdateLock = requestLockObject.writeLock();
        try {
            statusUpdateLock.lockInterruptibly();
            if(inStatus == FeedStatus.AVAILABLE) {
                // TODO check for duplicate provider name and warn
                activeProvidersByName.put(inProvider.getProviderName(),
                                          inProvider);
            } else {
                activeProvidersByName.remove(inProvider.getProviderName());
            }
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
        } finally {
            statusUpdateLock.unlock();
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.manager.MarketDataManager#requestMarketData(org.marketcetera.marketdata.request.MarketDataRequest, org.marketcetera.api.systemmodel.Subscriber)
     */
    @Override
    public long requestMarketData(MarketDataRequest inRequest,
                                  Subscriber inSubscriber)
    {
        SLF4JLoggerProxy.debug(this,
                               "Received: {}",
                               inRequest);
        // route the request to available providers or to a particular provider
        Lock requestLock = requestLockObject.writeLock();
        try {
            requestLock.lockInterruptibly();
            MarketDataRequestToken token = new Token(inSubscriber,
                                                     inRequest);
            if(inRequest.getProvider() != null) {
                // a specific provider was requested - use that provider only
                MarketDataProvider provider = activeProvidersByName.get(inRequest.getProvider());
                if(provider == null) {
                    throw new MarketDataProviderNotAvailable();
                }
                SLF4JLoggerProxy.debug(this,
                                       "Submitting {} to {}",
                                       token,
                                       provider);
                provider.requestMarketData(token);
                providersByToken.put(token,
                                     provider);
            } else {
                boolean submitted = false;
                for(MarketDataProvider provider : activeProvidersByName.values()) {
                    try {
                        SLF4JLoggerProxy.debug(this,
                                               "Submitting {} to {}",
                                               token,
                                               provider);
                        provider.requestMarketData(token);
                        providersByToken.put(token,
                                             provider);
                        submitted = true;
                    } catch (MarketDataException e) {
                        SLF4JLoggerProxy.warn(this,
                                              e,
                                              "Unable to request market data {} from {}", // TODO
                                              inRequest,
                                              provider.getProviderName());
                        // continue to try from the next provider
                    }
                }
                if(!submitted) {
                    throw new NoMarketDataProvidersAvailable();
                }
            }
            tokensByTokenId.put(token.getId(),
                                token);
            SLF4JLoggerProxy.debug(this,
                                   "Returning {} for {}",
                                   token.getId(),
                                   inRequest);
            return token.getId();
        } catch (InterruptedException e) {
            SLF4JLoggerProxy.warn(this,
                                  "Market data request {} interrupted", // TODO
                                  inRequest);
            throw new MarketDataRequestTimedOut(e);
        } finally {
            requestLock.unlock();
        }
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
    @Immutable
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
        public Subscriber getSubscriber()
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
            return new ToStringBuilder(this,ToStringStyle.SHORT_PREFIX_STYLE).append(id).append(request).toString();
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
         * @param inSubscriber
         * @param inRequest
         */
        private Token(Subscriber inSubscriber,
                      MarketDataRequest inRequest)
        {
            subscriber = inSubscriber;
            request = inRequest;
        }
        private final long id = requestCounter.incrementAndGet();
        private final Subscriber subscriber;
        private final MarketDataRequest request;
        private static final long serialVersionUID = 1L;
    }
    @GuardedBy("requestLockObject")
    private final Map<String,MarketDataProvider> activeProvidersByName = new HashMap<String,MarketDataProvider>();
    private final ReadWriteLock requestLockObject = new ReentrantReadWriteLock();
    private final Map<Long,MarketDataRequestToken> tokensByTokenId = new HashMap<Long,MarketDataRequestToken>();
    private final Multimap<MarketDataRequestToken,MarketDataProvider> providersByToken = HashMultimap.create();
    private final AtomicLong requestCounter = new AtomicLong(0);
}
