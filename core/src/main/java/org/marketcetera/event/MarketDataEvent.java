package org.marketcetera.event;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Represents a single market data message from a market data provider.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 2.0.0
 */
@ClassVersion("$Id$")
public interface MarketDataEvent
        extends Event, HasInstrument, HasEventType, HasTimestamps
{
    /**
     * Gets the exchange on which the market data event occurred.
     *
     * @return a <code>String</code> value
     */
    String getExchange();
    /**
     * Gets the price of the market data event. 
     *
     * @return a <code>BigDecimal</code> value
     */
    BigDecimal getPrice();
    /**
     * Gets the size of the market data event.
     *
     * @return a <code>BigDecimal</code> value
     */
    BigDecimal getSize();
    /**
     * Gets the time the event occurred. 
     *
     * <p>The format of the returned value is dependent on the
     * originating market data provider.
     *
     * @return a <code>LocalDateTime</code> value
     */
    LocalDateTime getExchangeTimestamp();
    /**
     * Gets the type of the event.
     *
     * @return an <code>EventMetaType</code> value
     */
    EventType getEventType();
    /**
     * Sets the type of the event.
     *
     * @param inEventType an <code>EventMetaType</code> value
     */
    void setEventType(EventType inEventType);
}
