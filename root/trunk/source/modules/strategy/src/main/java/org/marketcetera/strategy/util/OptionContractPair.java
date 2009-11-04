package org.marketcetera.strategy.util;

import java.math.BigDecimal;

import javax.annotation.concurrent.ThreadSafe;

import org.marketcetera.event.OptionEvent;
import org.marketcetera.trade.Option;
import org.marketcetera.trade.OptionType;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Represents the put and call of a specific option and their most recent market data, if available.
 * 
 * <p>The put and the call in this pair are guaranteed to be for the same symbol, expiry, and strike.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ThreadSafe
@ClassVersion("$Id$")
public final class OptionContractPair
        implements Comparable<OptionContractPair>
{
    /**
     * Gets the put <code>OptionContract</code> value.
     *
     * @return an <code>OptionContract</code> value or <code>null</code>
     */
    public OptionContract getPut()
    {
        return put;
    }
    /**
     * Gets the call <code>OptionContract</code> value.
     *
     * @return an <code>OptionContract</code> value or <code>null</code>
     */
    public OptionContract getCall()
    {
        return call;
    }
    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(OptionContractPair inPair)
    {
        return getKey().compareTo(inPair.getKey());
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.Pair#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object inOther)
    {
        if(inOther == null) {
            return false;
        }
        if(!(inOther instanceof OptionContractPair)) {
            return false;
        }
        return compareTo((OptionContractPair)inOther) == 0;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.Pair#hashCode()
     */
    @Override
    public int hashCode()
    {
        // this implementation must be kept in sync with OptionContractPair#compareTo
        final int prime = 31;
        int result = 1;
        if(getPut() == null &&
           getCall() == null) {
            return prime * result + 0;
        }
        Option option = getPut() == null ? getCall().getInstrument() : getPut().getInstrument();
        result = prime * result + ((option == null || option.getSymbol() == null) ? 0 : option.getSymbol().hashCode());
        result = prime * result + ((option == null || option.getExpiry() == null) ? 0 : option.getExpiry().hashCode());
        result = prime * result + ((option == null || option.getStrikePrice() == null) ? 0 : option.getStrikePrice().hashCode());
        return result;
    }
    /**
     * Create a new OptionContractPair instance.
     *
     * @param inOptionEvent an <code>OptionEvent</code> value
     * @throws IllegalArgumentException if <code>Instrument</code> is <code>null</code>
     * @throws IllegalArgumentException if <code>UnderlyingInstrument</code> is <code>null</code>
     * @throws IllegalArgumentException if <code>ExpirationType</code> is <code>null</code>
     */
    OptionContractPair(OptionEvent inOptionEvent)
    {
        key = getOptionContractPairKey(inOptionEvent.getInstrument());
        process(inOptionEvent);
    }
    /**
     * Process the given <code>OptionEvent</code> to update one side of the
     * option pair or the other.
     * 
     * <p>The given <code>OptionEvent</code> is assumed to apply to this contract pair.  The caller
     * must ascertain that this is the correct contract pair to apply the event to.  No further validation
     * will be done; the values of the given event will be applied to the appropriate side of the contract
     * pair.
     *
     * @param inOptionEvent an <code>OptionEvent</code> that pertains to this option contract pair
     */
    synchronized boolean process(OptionEvent inOptionEvent)
    {
        if(inOptionEvent.getInstrument().getType() == OptionType.Call) {
            if(call == null) {
                call = new OptionContract(inOptionEvent.getUnderlyingInstrument(),
                                          inOptionEvent.getInstrument(),
                                          inOptionEvent.getInstrument().getType(),
                                          inOptionEvent.getExpirationType(),
                                          inOptionEvent.hasDeliverable(),
                                          inOptionEvent.getMultiplier());
            }
            return call.process(inOptionEvent);
        }
        if(inOptionEvent.getInstrument().getType() == OptionType.Put) {
            if(put == null) {
                put = new OptionContract(inOptionEvent.getUnderlyingInstrument(),
                                         inOptionEvent.getInstrument(),
                                         inOptionEvent.getInstrument().getType(),
                                         inOptionEvent.getExpirationType(),
                                         inOptionEvent.hasDeliverable(),
                                         inOptionEvent.getMultiplier());
            }
            return put.process(inOptionEvent);
        }
        throw new UnsupportedOperationException();
    }
    /**
     * Gets an <code>OptionContractPairKey</code> corresponding to the given <code>Option</code>. 
     *
     * @param inOption an <code>Option</code> value
     * @return an <code>OptionContractPairKey</code> value
     */
    static OptionContractPairKey getOptionContractPairKey(Option inOption)
    {
        return new OptionContractPairKey(inOption.getSymbol(),
                                         inOption.getExpiry(),
                                         inOption.getStrikePrice());
    }
    /**
     * Gets the key for this object. 
     *
     * @return an <code>OptionContractPairKey</code> value
     */
    private OptionContractPairKey getKey()
    {
        return key;
    }
    /**
     * the put contract, may be null
     */
    private volatile OptionContract put;
    /**
     * the call contract, may be null
     */
    private volatile OptionContract call;
    /**
     * the key of this object is the essential components of the {@link OptionContract} pair
     */
    private final OptionContractPairKey key;
    /**
     * Encapsulates the components of an <code>OptionContractPair</code> that correspond to
     * the unique key.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    @ClassVersion("$Id$")
    static class OptionContractPairKey
            implements Comparable<OptionContractPairKey>
    {
        /* (non-Javadoc)
         * @see java.lang.Object#hashCode()
         */
        @Override
        public int hashCode()
        {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((symbol == null) ? 0 : symbol.hashCode());
            result = prime * result + ((expiry == null) ? 0 : expiry.hashCode());
            result = prime * result + ((strike == null) ? 0 : strike.hashCode());
            return result;
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
            if (!(obj instanceof OptionContractPairKey)) {
                return false;
            }
            OptionContractPairKey other = (OptionContractPairKey) obj;
            if (!symbol.equals(other.symbol)) {
                return false;
            }
            if (!expiry.equals(other.expiry)) {
                return false;
            }
            if (!strike.equals(other.strike)) {
                return false;
            }
            return true;
        }
        /* (non-Javadoc)
         * @see java.lang.Comparable#compareTo(java.lang.Object)
         */
        @Override
        public int compareTo(OptionContractPairKey inOther)
        {
            // this method must be kept in sync with OptionContractPair#hashCode
            // key is option symbol, expiry, strike
            int result = getSymbol().compareTo(inOther.getSymbol());
            if(result != 0) {
                return result;
            }
            result = getExpiry().compareTo(inOther.getExpiry());
            if(result != 0) {
                return result;
            }
            return getStrike().compareTo(inOther.getStrike());
        }
        /**
         * Create a new OptionContrctPairKey instance.
         *
         * @param inSymbol a <code>String</code> value
         * @param inExpiry a <code>String</code> value
         * @param inStrike a <code>BigDecimal</code> value
         */
        private OptionContractPairKey(String inSymbol,
                                      String inExpiry,
                                      BigDecimal inStrike)
        {
            symbol = inSymbol;
            expiry = inExpiry;
            strike = inStrike;
        }
        /**
         * Get the symbol value.
         *
         * @return a <code>String</code> value
         */
        private String getSymbol()
        {
            return symbol;
        }
        /**
         * Get the expiry value.
         *
         * @return a <code>String</code> value
         */
        private String getExpiry()
        {
            return expiry;
        }
        /**
         * Get the strike value.
         *
         * @return a <code>BigDecimal</code> value
         */
        private BigDecimal getStrike()
        {
            return strike;
        }
        /**
         * the symbol
         */
        private final String symbol;
        /**
         * the expiry
         */
        private final String expiry;
        /**
         * the strike
         */
        private final BigDecimal strike;
    }
}
