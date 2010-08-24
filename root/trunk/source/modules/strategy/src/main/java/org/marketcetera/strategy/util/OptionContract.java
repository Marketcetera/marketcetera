package org.marketcetera.strategy.util;

import java.math.BigDecimal;

import javax.annotation.concurrent.ThreadSafe;

import org.apache.commons.lang.StringUtils;
import org.marketcetera.event.*;
import org.marketcetera.event.beans.OptionBean;
import org.marketcetera.event.util.MarketstatEventCache;
import org.marketcetera.options.ExpirationType;
import org.marketcetera.trade.Instrument;
import org.marketcetera.trade.Option;
import org.marketcetera.trade.OptionType;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Represents an option contract and its most recent market data, if available.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 2.0.0
 */
@ThreadSafe
@ClassVersion("$Id$")
public final class OptionContract
{
    /**
     * Get the underlyingInstrument value.
     *
     * @return an <code>Instrument</code> value
     */
    public Instrument getUnderlyingInstrument()
    {
        return option.getUnderlyingInstrument();
    }
    /**
     * Gets the option instrument.
     *
     * @return an <code>Option</code> value
     */
    public Option getInstrument()
    {
        return option.getInstrument();
    }
    /**
     * Get the expirationType value.
     *
     * @return an <code>ExpirationType</code> value
     */
    public ExpirationType getExpirationType()
    {
        return option.getExpirationType();
    }
    /**
     * Get the multiplier value.
     *
     * @return a <code>BigDecimal</code> value
     */
    public BigDecimal getMultiplier()
    {
        return option.getMultiplier();
    }
    /**
     * Get the hasDeliverable value.
     *
     * @return a <code>boolean</code> value
     */
    public boolean hasDeliverable()
    {
        return option.hasDeliverable();
    }
    /**
     * Get the latest bid value.
     *
     * @return a <code>BidEvent</code> value or <code>null</code>
     */
    public BidEvent getLatestBid()
    {
        return latestBid;
    }
    /**
     * Get the latest ask value.
     *
     * @return an <code>AskEvent</code> value or <code>null</code>
     */
    public AskEvent getLatestAsk()
    {
        return latestAsk;
    }
    /**
     * Get the latest trade value.
     *
     * @return a <code>TradeEvent</code> value or <code>null</code>
     */
    public TradeEvent getLatestTrade()
    {
        return latestTrade;
    }
    /**
     * Get the latest marketstat value.
     *
     * @return a <code>MarketstatEvent</code> value or <code>null</code>
     */
    public MarketstatEvent getLatestMarketstat()
    {
        return latestMarketstat.get();
    }
    /**
     * Get the providerSymbol value.
     *
     * @return a <code>String</code> value
     */
    public String getProviderSymbol()
    {
        return providerSymbol;
    }
    /**
     * Create a new OptionContract instance.
     * 
     * @param inUnderlyingInstrument an <code>Instrument</code> value
     * @param inInstrument an <code>Option</code> value
     * @param inType an <code>OptionType</code> value
     * @param inExpirationType an <code>ExpirationType</code> value
     * @param inHasDeliverable a <code>boolean</code> value
     * @param inMultiplier a <code>BigDecimal</code> value
     * @param inProviderSymbol a <code>String</code> value or <code>null</code>
     * @throws IllegalArgumentException if <code>Instrument</code> is <code>null</code>
     * @throws IllegalArgumentException if <code>UnderlyingInstrument</code> is <code>null</code>
     * @throws IllegalArgumentException if <code>ExpirationType</code> is <code>null</code>
     */
    OptionContract(Instrument inUnderlyingInstrument,
                   Option inInstrument,
                   OptionType inType,
                   ExpirationType inExpirationType,
                   boolean inHasDeliverable,
                   BigDecimal inMultiplier,
                   String inProviderSymbol)
    {
        latestMarketstat = new MarketstatEventCache(inInstrument);
        option.setInstrument(new Option(inInstrument.getSymbol(),
                                        inInstrument.getExpiry(),
                                        inInstrument.getStrikePrice(),
                                        inType));
        option.setUnderlyingInstrument(inUnderlyingInstrument);
        option.setExpirationType(inExpirationType);
        option.setHasDeliverable(inHasDeliverable);
        option.setMultiplier(inMultiplier);
        option.validate();
        providerSymbol = StringUtils.trimToNull(inProviderSymbol);
    }
    /**
     * Processes the given <code>OptionEvent</code> for market data.
     * 
     * <p>The given event is assumed to be applicable for this option contract.
     * No validation is done to make sure the given <code>OptionEvent</code>
     * actually applies to this option contract.  The event will be discarded only
     * if it is of the wrong type.
     *
     * @param inOptionEvent an <code>OptionEvent</code> value
     * @return a <code>boolean</code> value indicating if this event was applied
     * to the option contract
     */
    boolean process(OptionEvent inOptionEvent)
    {
        if(inOptionEvent instanceof BidEvent) {
            setLatestBid((BidEvent)inOptionEvent);
            return true;
        }
        if(inOptionEvent instanceof AskEvent) {
            setLatestAsk((AskEvent)inOptionEvent);
            return true;
        }
        if(inOptionEvent instanceof TradeEvent) {
            setLatestTrade((TradeEvent)inOptionEvent);
            return true;
        }
        if(inOptionEvent instanceof MarketstatEvent) {
            setLatestMarketstat((MarketstatEvent)inOptionEvent);
            return true;
        }
        return false;
    }
    /**
     * Sets the latest bid value.
     *
     * @param a <code>BidEvent</code> value or <code>null</code>
     */
    private void setLatestBid(BidEvent inLatestBid)
    {
        latestBid = inLatestBid;
    }
    /**
     * Sets the latest ask value.
     *
     * @param an <code>AskEvent</code> value or <code>null</code>
     */
    private void setLatestAsk(AskEvent inLatestAsk)
    {
        latestAsk = inLatestAsk;
    }
    /**
     * Sets the latest trade value.
     *
     * @param a <code>TradeEvent</code> value or <code>null</code>
     */
    private void setLatestTrade(TradeEvent inLatestTrade)
    {
        latestTrade = inLatestTrade;
    }
    /**
     * Sets the latest marketstat value.
     *
     * @param a <code>MarketstatEvent</code> value or <code>null</code>
     */
    private void setLatestMarketstat(MarketstatEvent inLatestMarketstat)
    {
        latestMarketstat.cache(inLatestMarketstat);
    }
    /**
     * option info
     */
    private final OptionBean option = new OptionBean();
    /**
     * the latest bid for this contract, may be <code>null</code>
     */
    private volatile BidEvent latestBid = null;
    /**
     * the latest ask for this contract, may be <code>null</code>
     */
    private volatile AskEvent latestAsk = null;
    /**
     * the latest trade for this contract, may be <code>null</code>
     */
    private volatile TradeEvent latestTrade = null;
    /**
     * the latest marketstat for this contract, may be <code>null</code>
     */
    private final MarketstatEventCache latestMarketstat;
    /**
     * the actual symbol used by the provider, may be <code>null</code>
     */
    private final String providerSymbol;
}
