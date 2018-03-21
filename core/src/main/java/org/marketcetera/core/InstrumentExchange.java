package org.marketcetera.core;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.marketcetera.trade.Instrument;

/* $License$ */

/**
 * Provides a unique key of {@link Instrument} and <code>Exchange</code> values.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class InstrumentExchange
        implements Comparable<InstrumentExchange>
{
    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(InstrumentExchange inO)
    {
        return new CompareToBuilder().append(inO.instrument.getFullSymbol(),instrument.getFullSymbol()).append(inO.exchange,exchange).toComparison();
    }
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        return hashCode;
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
        if (!(obj instanceof InstrumentExchange)) {
            return false;
        }
        InstrumentExchange other = (InstrumentExchange) obj;
        return new EqualsBuilder().append(other.instrument,instrument).append(other.exchange,exchange).isEquals();
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("InstrumentExchange [instrument=").append(instrument).append(", exchange=").append(exchange)
                .append("]");
        return builder.toString();
    }
    /**
     * Get the instrument value.
     *
     * @return an <code>Instrument</code> value
     */
    public Instrument getInstrument()
    {
        return instrument;
    }
    /**
     * Get the exchange value.
     *
     * @return a <code>String</code> value
     */
    public String getExchange()
    {
        return exchange;
    }
    /**
     * Create a new InstrumentExchange instance.
     *
     * @param inInstrument an <code>Instrument</code> value
     * @param inExchange a <code>String</code> value
     */
    public InstrumentExchange(Instrument inInstrument,
                              String inExchange)
    {
        Validate.notNull(inInstrument);
        Validate.notNull(inExchange);
        instrument = inInstrument;
        exchange = inExchange;
        hashCode = new HashCodeBuilder().append(instrument).append(exchange).toHashCode();
    }
    /**
     * hashcode value
     */
    private final int hashCode;
    /**
     * instrument value
     */
    private final Instrument instrument;
    /**
     * exchange value
     */
    private final String exchange;
}
