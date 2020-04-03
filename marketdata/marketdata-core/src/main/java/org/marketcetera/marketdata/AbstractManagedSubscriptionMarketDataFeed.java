package org.marketcetera.marketdata;

import static com.codahale.metrics.MetricRegistry.name;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.marketcetera.core.CloseableLock;
import org.marketcetera.event.Event;
import org.marketcetera.trade.Instrument;
import org.marketcetera.util.log.SLF4JLoggerProxy;

import com.codahale.metrics.Meter;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public abstract class AbstractManagedSubscriptionMarketDataFeed
        extends NewAbstractMarketDataFeed
{
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.NewAbstractMarketDataFeed#doMarketDataRequest(org.marketcetera.marketdata.MarketDataRequest, java.lang.String, org.marketcetera.marketdata.MarketDataListener)
     */
    @Override
    protected void doMarketDataRequest(MarketDataRequest request,
                                       String requestId,
                                       MarketDataListener listener)
    {
        for(String symbol : request.getSymbols()) {
            Instrument instrument = symbolResolverService.resolveSymbol(symbol);
            if(instrument == null) {
                listener.onError(new RuntimeException("Cannot resolve '" + symbol + "' - check your symbol resolver service configuration and make sure it covers this type of symbol"));
                continue;
            }
            for(Content content : request.getContent()) {
                String exchangeToUse = StringUtils.trimToNull(request.getExchange());
                if(exchangeToUse == null && usesExchangeWildcard()) {
                    exchangeToUse = getExchangeWildcard();
                }
                final MarketDataKey key = new MarketDataKey(instrument,
                                                            content,
                                                            exchangeToUse);
                SLF4JLoggerProxy.debug(this,
                                       "Adding subscription key {} for symbol {} for request {}",
                                       key,
                                       symbol,
                                       requestId);
                MarketDataSubscribers subscribers = subscribersByKey.getUnchecked(key);
                final MarketDataSubscriber subscriber = new MarketDataSubscriber(subscribers,
                                                                                 key,
                                                                                 requestId,
                                                                                 listener);
                subscribersByMarketDataRequestId.put(requestId,
                                            subscriber);
                try(CloseableLock subscriberLock = CloseableLock.create(subscribers.lock.writeLock())) {
                    subscriberLock.lock();
                    subscribers.subscribers.add(subscriber);
                }
            }
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.NewAbstractMarketDataFeed#doCancel(java.lang.String)
     */
    @Override
    protected void doCancel(String inMarketDataRequestId)
    {
        MarketDataSubscriber subscriber = subscribersByMarketDataRequestId.getIfPresent(inMarketDataRequestId);
        if(subscriber == null) {
            SLF4JLoggerProxy.warn(this,
                                  "Could not cancel market data request for {}",
                                  inMarketDataRequestId);
        } else {
            try(CloseableLock lock = CloseableLock.create(subscriber.parent.lock.writeLock())) {
                lock.lock();
                subscriber.parent.subscribers.remove(subscriber);
            }
        }
        subscribersByMarketDataRequestId.invalidate(inMarketDataRequestId);
    }
    protected void sendEvent(Event event,
                             Instrument instrument,
                             Content content,
                             String exchange)
    {
            MarketDataKey key = new MarketDataKey(instrument,
                                                  content,
                                                  exchange);
            Set<MarketDataSubscriber> subscribersToNotify = Sets.newLinkedHashSet();
            MarketDataSubscribers subscribers = subscribersByKey.getIfPresent(key);
            if(subscribers != null) {
                try(CloseableLock lock = CloseableLock.create(subscribers.lock.readLock())) {
                    lock.lock();
                    for(MarketDataSubscriber subscriber : subscribers.subscribers) {
                        subscribersToNotify.add(subscriber);
                    }
                }
            }
            if(usesExchangeWildcard()) {
                MarketDataKey wildcardKey = new MarketDataKey(instrument,
                                                              content,
                                                              getExchangeWildcard());
                subscribers = subscribersByKey.getIfPresent(wildcardKey);
                if(subscribers != null) {
                    try(CloseableLock lock = CloseableLock.create(subscribers.lock.readLock())) {
                        lock.lock();
                        for(MarketDataSubscriber subscriber : subscribers.subscribers) {
                            subscribersToNotify.add(subscriber);
                        }
                    }
                }
            }
            for(MarketDataSubscriber subscriber : subscribersToNotify) {
                try(CloseableLock sendLock = CloseableLock.create(subscriber.getSendLock().writeLock())) {
                    sendLock.lock();
                    SLF4JLoggerProxy.trace(this,
                                           "Emitting {} to {}",
                                           event,
                                           subscriber.marketDataRequestId);
                    subscriber.send(event);
                } catch (Exception e) {
                    SLF4JLoggerProxy.warn(this,
                                          e);
                }
            }
    }
    protected boolean usesExchangeWildcard()
    {
        return false;
    }
    protected String getExchangeWildcard()
    {
        return null;
    }
    /**
     * Holds subscribers for a particular market data key.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    protected static class MarketDataSubscribers
    {
        /**
         * Create a new MarketDataSubscribers instance.
         *
         * @param inKey a <code>MarketDataKey</code> value
         */
        private MarketDataSubscribers(MarketDataKey inKey)
        {
            key = inKey;
        }
        /**
         * uniquely identifies a market data combination
         */
        @SuppressWarnings("unused")
        private final MarketDataKey key;
        /**
         * guards access to {@link #subscribers}
         */
        private final ReadWriteLock lock = new ReentrantReadWriteLock();
        /**
         * holds subscribers for this market data key
         */
        private final Collection<MarketDataSubscriber> subscribers = Lists.newArrayList();
    }
    /**
     * Holds information for a single market data subscriber.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    private class MarketDataSubscriber
    {
        /* (non-Javadoc)
         * @see java.lang.Object#hashCode()
         */
        @Override
        public int hashCode()
        {
            return new HashCodeBuilder().append(marketDataRequestId).toHashCode();
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
            if (!(obj instanceof MarketDataSubscriber)) {
                return false;
            }
            MarketDataSubscriber other = (MarketDataSubscriber) obj;
            return new EqualsBuilder().append(other.marketDataRequestId,marketDataRequestId).isEquals();
        }
        /* (non-Javadoc)
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString()
        {
            StringBuilder builder = new StringBuilder();
            builder.append("MarketDataSubscriber [key=").append(key).append(", marketDataRequestId=")
                    .append(marketDataRequestId).append("]");
            return builder.toString();
        }
        /**
         * Send the given event.
         *
         * @param inEvent an <code>Event</code> value
         */
        private void send(Event inEvent)
        {
            incomingMessagesMeter.mark();
            subscriber.receiveMarketData(inEvent);
        }
        /**
         * Get the lock that guards access for sending events.
         *
         * @return a <code>ReadWriteLock</code> value
         */
        private ReadWriteLock getSendLock()
        {
            return sendLock;
        }
        /**
         * Create a new MarketDataSubscriber instance.
         *
         * @param inParent a <code>MarketDataSubscribers</code> value
         * @param inKey a <code>MarketDataKey</code> value
         * @param inRequestId a <code>String</code> value
         * @param inListener a <code>MarketDataListener</code> value
         */
        private MarketDataSubscriber(MarketDataSubscribers inParent,
                                     MarketDataKey inKey,
                                     String inRequestId,
                                     MarketDataListener inListener)
        {
            parent = inParent;
            key = inKey;
            marketDataRequestId = inRequestId;
            subscriber = inListener;
            String metricName = name(getClass(),
                                     inKey.toString(),
                                     inRequestId,
                                     "incomingMessages",
                                     "meter");
            metricNames.add(metricName);
            incomingMessagesMeter = metricsService.getMetrics().meter(metricName);
        }
        /**
         * indicates the parent collection for this type of subscription
         */
        private final MarketDataSubscribers parent;
        /**
         * identifies the market data tuple for this subscriber
         */
        private final MarketDataKey key;
        /**
         * market data request id
         */
        private final String marketDataRequestId;
        /**
         * market data subscriber
         */
        private final MarketDataListener subscriber;
        /**
         * measures incoming message rate
         */
        private final Meter incomingMessagesMeter;
        /**
         * guards access to the data emitter
         */
        private final ReadWriteLock sendLock = new ReentrantReadWriteLock();
    }
    /**
     * Uniquely identifies a market data item.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    protected static class MarketDataKey
    {
        /* (non-Javadoc)
         * @see java.lang.Object#hashCode()
         */
        @Override
        public int hashCode()
        {
            return new HashCodeBuilder().append(instrument).append(content).append(exchange).toHashCode();
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
            if (!(obj instanceof MarketDataKey)) {
                return false;
            }
            MarketDataKey other = (MarketDataKey) obj;
            return new EqualsBuilder().append(instrument,other.instrument).append(content,other.content).append(exchange,other.exchange).isEquals();
        }
        /* (non-Javadoc)
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString()
        {
            StringBuilder builder = new StringBuilder();
            builder.append("MarketDataKey [instrument=").append(instrument).append(", content=").append(content)
                    .append(", exchange=").append(exchange).append("]");
            return builder.toString();
        }
        /**
         * Create a new MarketDataKey instance.
         *
         * @param inInstrument an <code>Instrument</code> value
         * @param inContent a <code>Content</code> value
         * @param inExchange a <code>String</code> value
         */
        private MarketDataKey(Instrument inInstrument,
                              Content inContent,
                              String inExchange)
        {
            instrument = inInstrument;
            content = inContent;
            exchange = inExchange;
        }
        /**
         * instrument value
         */
        private final Instrument instrument;
        /**
         * content value
         */
        private final Content content;
        /**
         * exchange value
         */
        private final String exchange;
    }
    /**
     * holds market data subscribers by subscription key
     */
    private final LoadingCache<MarketDataKey,MarketDataSubscribers> subscribersByKey = CacheBuilder.newBuilder().build(new CacheLoader<MarketDataKey,MarketDataSubscribers>() {
        @Override
        public MarketDataSubscribers load(MarketDataKey inKey)
                throws Exception
        {
            return new MarketDataSubscribers(inKey);
        }}
    );
    /**
     * holds market data subscribers by market data request id
     */
    private final Cache<String,MarketDataSubscriber> subscribersByMarketDataRequestId = CacheBuilder.newBuilder().build();
}
