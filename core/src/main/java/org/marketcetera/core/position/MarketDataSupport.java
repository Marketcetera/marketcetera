package org.marketcetera.core.position;

import java.math.BigDecimal;
import java.util.EventObject;

import org.marketcetera.trade.Future;
import org.marketcetera.trade.Instrument;
import org.marketcetera.trade.Option;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Interface providing market data for position-related calculations.
 * 
 * Listeners can subscribe to be notified when the trade price or close price
 * changes for an instrument. For options, listeners will also be notified of
 * changes to the multiplier.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.5.0
 */
@ClassVersion("$Id$")
public interface MarketDataSupport {

    /**
     * Returns the price of the last trade for the given instrument.
     * 
     * @param instrument
     *            the instrument in question, will not be null
     * @return the last trade price, or null if unknown
     */
    BigDecimal getLastTradePrice(Instrument instrument);

    /**
     * Returns the closing price for the given instrument. This is the closing
     * price that applies to the incoming position provided by
     * {@link IncomingPositionSupport}. The position engine does not have any
     * notion of a trading day so it is the responsibility of the implementor of
     * this class and IncomingPositionSupport to make sure that values match.
     * 
     * @param instrument
     *            the instrument in question, will not be null
     * @return the closing price, or null if unknown
     */
    BigDecimal getClosingPrice(Instrument instrument);

    /**
     * Returns the option multiplier for the given option.
     * 
     * @param option
     *            the option in question, will not be null
     * @return the option multiplier, or null if unknown
     */
    BigDecimal getOptionMultiplier(Option option);
    
    /**
     * Returns the future multiplier for the given future.
     * 
     * @param future
     *            the future in question, will not be null
     * @return the future multiplier, or null if unknown
     */
    BigDecimal getFutureMultiplier(Future future);

    /**
     * Adds a listener to be notified when the market data for a given
     * instrument has changed. This method has no effect if the listener has
     * already been added.
     * 
     * @param instrument
     *            the instrument to listen for
     * @param listener
     *            the listener to add
     */
    void addInstrumentMarketDataListener(Instrument instrument,
            InstrumentMarketDataListener listener);

    /**
     * Removes a listener. This has no effect if the listener does not exist.
     * 
     * @param instrument
     *            the instrument being listened to
     * @param listener
     *            the listener to remove
     */
    void removeInstrumentMarketDataListener(Instrument instrument,
            InstrumentMarketDataListener listener);

    /**
     * Disposes the provider and releases all resources. The provider will no
     * longer be used after this is called.
     */
    void dispose();

    /**
     * Interface to notify listeners of changes. Instead of implementing this
     * interface, extend {@link InstrumentMarketDataListenerBase}.
     */
    @ClassVersion("$Id$")
    public interface InstrumentMarketDataListener
    {
        /**
         * Callback for receiving trade notifications.
         * 
         * @param inEvent an <code>InstrumentMarketDataEvent</code> describing the change
         */
        void symbolTraded(InstrumentMarketDataEvent inEvent);
        /**
         * Callback for receiving bid notifications.
         * 
         * @param inEvent an <code>InstrumentMarketDataEvent</code> describing the change
         */
        void bidChanged(InstrumentMarketDataEvent inEvent);
        /**
         * Callback for receiving ask notifications.
         * 
         * @param inEvent an <code>InstrumentMarketDataEvent</code> describing the change
         */
        void askChanged(InstrumentMarketDataEvent inEvent);
        /**
         * Callback for receiving close price change notifications.
         * 
         * @param inEvent an <code>InstrumentMarketDataEvent</code> describing the change
         */
        void closePriceChanged(InstrumentMarketDataEvent inEvent);
        /**
         * Callback for receiving the option multiplier.
         * 
         * @param inEvent an <code>InstrumentMarketDataEvent</code> describing the change
         */
        void optionMultiplierChanged(InstrumentMarketDataEvent inEvent);
        
        /**
         * Callback for receiving the future multiplier.
         * 
         * @param inEvent an <code>InstrumentMarketDataEvent</code> describing the change
         */
        void futureMultiplierChanged(InstrumentMarketDataEvent inEvent);
    }

    /**
     * No-op implementation of {@link InstrumentMarketDataListener}. Subclasses can
     * extend callbacks they care about.
     */
    @ClassVersion("$Id$")
    public abstract class InstrumentMarketDataListenerBase
            implements InstrumentMarketDataListener
    {
        /* (non-Javadoc)
         * @see org.marketcetera.core.position.MarketDataSupport.InstrumentMarketDataListener#symbolTraded(org.marketcetera.core.position.MarketDataSupport.InstrumentMarketDataEvent)
         */
        @Override
        public void symbolTraded(InstrumentMarketDataEvent inEvent)
        {
        }
        /* (non-Javadoc)
         * @see org.marketcetera.core.position.MarketDataSupport.InstrumentMarketDataListener#closePriceChanged(org.marketcetera.core.position.MarketDataSupport.InstrumentMarketDataEvent)
         */
        @Override
        public void closePriceChanged(InstrumentMarketDataEvent inEvent)
        {
        }
        /* (non-Javadoc)
         * @see org.marketcetera.core.position.MarketDataSupport.InstrumentMarketDataListener#optionMultiplierChanged(org.marketcetera.core.position.MarketDataSupport.InstrumentMarketDataEvent)
         */
        @Override
        public void optionMultiplierChanged(InstrumentMarketDataEvent inEvent)
        {
        }
        /* (non-Javadoc)
         * @see org.marketcetera.core.position.MarketDataSupport.InstrumentMarketDataListener#futureMultiplierChanged(org.marketcetera.core.position.MarketDataSupport.InstrumentMarketDataEvent)
         */
        @Override
        public void futureMultiplierChanged(InstrumentMarketDataEvent inEvent)
        {
        }
        /* (non-Javadoc)
         * @see org.marketcetera.core.position.MarketDataSupport.InstrumentMarketDataListener#quoteChanged(org.marketcetera.core.position.MarketDataSupport.InstrumentMarketDataEvent)
         */
        @Override
        public void bidChanged(InstrumentMarketDataEvent inEvent)
        {
        }
        /* (non-Javadoc)
         * @see org.marketcetera.core.position.MarketDataSupport.InstrumentMarketDataListener#askChanged(org.marketcetera.core.position.MarketDataSupport.InstrumentMarketDataEvent)
         */
        @Override
        public void askChanged(InstrumentMarketDataEvent inEvent)
        {
        }
    }
    /**
     * Event object for {@link InstrumentMarketDataListener}.
     */
    @ClassVersion("$Id$")
    public static class InstrumentMarketDataEvent
            extends EventObject
    {
        /**
         * Constructor.
         * 
         * @param inSource the object on which the Event initially occurred
         * @param inNewPrice the new value for the symbol price, may be null to indicate market data is no longer available
         */
        public InstrumentMarketDataEvent(Object inSource,
                                         BigDecimal inNewPrice)
        {
            super(inSource);
            newPrice = inNewPrice;
        }
        /**
         * The new amount for the market data.
         * 
         * @return the new amount, may be null to indicate market data is no longer available
         */
        public BigDecimal getNewPrice()
        {
            return newPrice;
        }
        /**
         * new price value
         */
        private BigDecimal newPrice;
        private static final long serialVersionUID = 5777912812671561191L;
    }
}
