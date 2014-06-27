package org.marketcetera.event.util;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

import org.marketcetera.event.MarketstatEvent;
import org.marketcetera.event.OptionMarketstatEvent;
import org.marketcetera.event.impl.MarketstatEventBuilder;
import org.marketcetera.trade.Instrument;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Provides a thread-safe cache of {@link MarketstatEvent market statistics}
 * for a given {@link Instrument}.
 * 
 * <p>The cache retains the most recent non-null attributes available.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 2.0.0
 */
@ThreadSafe
@ClassVersion("$Id$")
public class MarketstatEventCache
{
    /**
     * Create a new MarketstatEventCache instance.
     * 
     * <p>This class will organize {@link MarketstatEvent events} for
     * a given {@link Instrument}, guaranteeing a view with the composite
     * of all the received non-null attributes with a bias towards the most
     * recently received value.  The timestamp, id, and source on the returned event
     * will reflect the most recently received update.
     * 
     * @param inInstrument an <code>Instrument</code> value
     */
    public MarketstatEventCache(Instrument inInstrument)
    {
        this(inInstrument,
             false);
    }
    /**
     * Create a new MarketstatEventCache instance.
     * 
     * <p>This class will organize {@link MarketstatEvent events} for
     * a given {@link Instrument}, guaranteeing a view with the composite
     * of all the received non-null attributes with a bias towards the most
     * recently received value.  The timestamp, id, and source on the returned event
     * will reflect the most recently received update.
     * 
     * <p>Passing lenient=true will disable instrument checks.
     * 
     * @param inInstrument an <code>Instrument</code> value
     * @param inLenient a <code>boolean</code> value
     */
    public MarketstatEventCache(Instrument inInstrument,
                                boolean inLenient)
    {
        if(inInstrument == null) {
            throw new NullPointerException();
        }
        builder = MarketstatEventBuilder.marketstat(inInstrument);
        instrument = inInstrument;
        lenient = inLenient;
    }
    /**
     * Adds the given {@link MarketstatEvent event} to the cache.
     * 
     * <p>Any non-null attributes on the given event will replace the
     * cached attribute.
     *
     * @param inEvent a <code>MarketstatEvent</code> value
     * @return a <code>MarketstatEvent</code> value
     * @throws IllegalArgumentException if the given event <code>Instrument</code> does not
     *  match the <code>Instrument</code> for which this cache was created
     */
    public synchronized MarketstatEvent cache(MarketstatEvent inEvent)
    {
        if(!lenient && !inEvent.getInstrument().equals(instrument)) {
            throw new IllegalArgumentException();
        }
        receivedData = true;
        // these values should always be transferred
        builder.withMessageId(inEvent.getMessageId())
               .withTimestamp(inEvent.getTimestamp())
               .withSource(inEvent.getSource())
               .withEventType(inEvent.getEventType());
        // these values should be transferred only if non-null
        if(inEvent.getClose() != null) {
            builder.withClosePrice(inEvent.getClose());
        }
        if(inEvent.getCloseDate() != null) {
            builder.withCloseDate(inEvent.getCloseDate());
        }
        if(inEvent.getCloseExchange() != null) {
            builder.withCloseExchange(inEvent.getCloseExchange());
        }
        if(inEvent.getHigh() != null) {
            builder.withHighPrice(inEvent.getHigh());
        }
        if(inEvent.getHighExchange() != null) {
            builder.withHighExchange(inEvent.getHighExchange());
        }
        if(inEvent.getLow() != null) {
            builder.withLowPrice(inEvent.getLow());
        }
        if(inEvent.getLowExchange() != null) {
            builder.withLowExchange(inEvent.getLowExchange());
        }
        if(inEvent.getOpen() != null) {
            builder.withOpenPrice(inEvent.getOpen());
        }
        if(inEvent.getOpenExchange() != null) {
            builder.withOpenExchange(inEvent.getOpenExchange());
        }
        if(inEvent.getPreviousClose() != null) {
            builder.withPreviousClosePrice(inEvent.getPreviousClose());
        }
        if(inEvent.getPreviousCloseDate() != null) {
            builder.withPreviousCloseDate(inEvent.getPreviousCloseDate());
        }
        if(inEvent.getTradeHighTime() != null) {
            builder.withTradeHighTime(inEvent.getTradeHighTime());
        }
        if(inEvent.getTradeLowTime() != null) {
            builder.withTradeLowTime(inEvent.getTradeLowTime());
        }
        if(inEvent.getVolume() != null) {
            builder.withVolume(inEvent.getVolume());
        }
        if(inEvent.getValue() != null) {
            builder.withValue(inEvent.getValue());
        }
        if(inEvent instanceof OptionMarketstatEvent) {
            OptionMarketstatEvent optionEvent = (OptionMarketstatEvent)inEvent;
            builder.hasDeliverable(optionEvent.hasDeliverable());
            if(optionEvent.getExpirationType() != null) {
                builder.withExpirationType(optionEvent.getExpirationType());
            }
            if(optionEvent.getMultiplier() != null) {
                builder.withMultiplier(optionEvent.getMultiplier());
            }
            if(optionEvent.getProviderSymbol() != null) {
                builder.withProviderSymbol(optionEvent.getProviderSymbol());
            }
            if(optionEvent.getUnderlyingInstrument() != null) {
                builder.withUnderlyingInstrument(optionEvent.getUnderlyingInstrument());
            }
            if(optionEvent.getVolumeChange() != null) {
                builder.withVolumeChange(optionEvent.getVolumeChange());
            }
            if(optionEvent.getInterestChange() != null) {
                builder.withInterestChange(optionEvent.getInterestChange());
            }
        }
        return get();
    }
    /**
     * Returns a view that reflects the most recent attributes available. 
     *
     * @return a <code>MarketstatEvent</code> value or <code>null</code> if no data has
     *  been added to the cache
     */
    public synchronized MarketstatEvent get()
    {
        if(!receivedData) {
            return null;
        }
        return builder.create();
    }
    /**
     * the event builder used to cache the values
     */
    @GuardedBy("this")
    private final MarketstatEventBuilder builder;
    /**
     * the instrument for this cache
     */
    private final Instrument instrument;
    /**
     * indicates if the cache has received any data so far
     */
    private boolean receivedData = false;
    /**
     * indicates if instrument checks should be performed
     */
    private final boolean lenient;
}
