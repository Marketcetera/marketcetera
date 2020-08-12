package org.marketcetera.event;

import java.time.LocalDateTime;

import org.marketcetera.core.time.TimeFactory;
import org.marketcetera.core.time.TimeFactoryImpl;
import org.marketcetera.util.time.DateService;

/* $License$ */

/**
 * Generates a timestamp value in the default timezone.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class LocalTimezoneTimestampGenerator
        implements TimestampGenerator
{
    /* (non-Javadoc)
     * @see org.marketcetera.event.TimestampGenerator#generateTimestamp(org.marketcetera.event.TradeEvent)
     */
    @Override
    public LocalDateTime generateTimestamp(TradeEvent inTrade)
    {
        return inTrade.getTradeDate();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.TimestampGenerator#generateTimestamp(org.marketcetera.event.QuoteEvent)
     */
    @Override
    public LocalDateTime generateTimestamp(QuoteEvent inQuote)
    {
        return inQuote.getQuoteDate();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.TimestampGenerator#generateTimestamp(java.lang.String)
     */
    @Override
    public LocalDateTime generateTimestamp(String inTimestamp)
    {
        try {
            return generateTimestampFrom(Long.parseLong(inTimestamp));
        } catch (Exception e) {
            return timeFactory.create(inTimestamp);
        }
    }
    /**
     * Get the timestampProvider value.
     *
     * @return a <code>TimestampProvider</code> value
     */
    public TimestampProvider getTimestampProvider()
    {
        return timestampProvider;
    }
    /**
     * Sets the timestampProvider value.
     *
     * @param inTimestampProvider a <code>TimestampProvider</code> value
     */
    public void setTimestampProvider(TimestampProvider inTimestampProvider)
    {
        timestampProvider = inTimestampProvider;
    }
    /**
     * Get the timeFactory value.
     *
     * @return a <code>TimeFactory</code> value
     */
    public TimeFactory getTimeFactory()
    {
        return timeFactory;
    }
    /**
     * Sets the timeFactory value.
     *
     * @param inTimeFactory a <code>TimeFactory</code> value
     */
    public void setTimeFactory(TimeFactory inTimeFactory)
    {
        timeFactory = inTimeFactory;
    }
    /**
     * Generates a timestamp from the given millis.
     *
     * @param inStartingTime a <code>long</code> value
     * @return a <code>LocalDateTime</code> value
     */
    private LocalDateTime generateTimestampFrom(long inStartingTime)
    {
        long activMMSS = inStartingTime % millisHour;
        long utcTime = timestampProvider.getTimestamp();
        long utcHH = (long)(utcTime / millisHour ) * millisHour;
        long utcTimeStamp = utcHH + activMMSS;
        long utcMMSS = utcTime % millisHour;
        if(utcMMSS < activMMSS) {
            utcTimeStamp = utcTimeStamp - millisHour;
        }
        // if timestamp is more than 30min old, add an hour back. this is to catch timestamps that were erroneously projected into the past because of clock drift of a few more millis
        if(utcTimeStamp < utcTime - millisHalfHour) {
            utcTimeStamp = utcTimeStamp + millisHour;
        }
        return DateService.toLocalDateTime(utcTimeStamp);
    }
    /**
     * Provides the current time.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since 1.4.11
     */
    public interface TimestampProvider
    {
        /**
         * Gets the current epoch time.
         *
         * @return a <code>long</code> value
         */
        long getTimestamp();
    }
    /**
     * provides the current epoch time
     */
    private TimestampProvider timestampProvider = new TimestampProvider() {
        @Override
        public long getTimestamp()
        {
            return System.currentTimeMillis();
        }
    };
    /**
     * number of milliseconds in an hour
     */
    private static final long millisHour = 1000 * 60 * 60;
    /**
     * number of milliseconds in a half-hour
     */
    private static final long millisHalfHour = 1000 * 60 * 30;
    /**
     * translates time stamps
     */
    private TimeFactory timeFactory = new TimeFactoryImpl();
}
