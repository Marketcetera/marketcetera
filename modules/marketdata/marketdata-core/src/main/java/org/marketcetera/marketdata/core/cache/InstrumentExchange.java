package org.marketcetera.marketdata.core.cache;

import java.util.UUID;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.marketcetera.trade.Instrument;

/* $License$ */

/**
 * Provides an instrument exchange tuple.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class InstrumentExchange
        implements Comparable<InstrumentExchange>
{
    /**
     * Create a new InstrumentExchange instance.
     *
     * @param inInstrument an <code>Instrument</code> value
     * @param inExchange a <code>String</code> value
     */
    public InstrumentExchange(Instrument inInstrument,
                              String inExchange)
    {
        instrument = inInstrument;
        exchange = inExchange==null?NO_EXCHANGE:inExchange;
    }
    /**
     * Create a new InstrumentExchange instance.
     *
     * @param inInstrument an <code>Instrument</code> value
     */
    public InstrumentExchange(Instrument inInstrument)
    {
        this(inInstrument,
             NO_EXCHANGE);
    }
    /**
     * Get the instrument full symbol value.
     *
     * @return a <code>String</code> value
     */
    public String getFullSymbol()
    {
        return instrument.getFullSymbol();
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
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append(instrument.getFullSymbol()).append(" [").append(exchange).append("]");
        return builder.toString();
    }
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        return new HashCodeBuilder().append(instrument).append(exchange).toHashCode();
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
        return new EqualsBuilder().append(instrument,other.instrument).append(exchange,other.exchange).isEquals();
    }
    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(InstrumentExchange inO)
    {
        return new CompareToBuilder().append(instrument,inO.instrument).append(exchange,inO.exchange).toComparison();
    }
    /**
     * instrument value
     */
    private final Instrument instrument;
    /**
     * exchange value
     */
    private final String exchange;
    /**
     * indicates no exchange is in use
     */
    private static final String NO_EXCHANGE = UUID.randomUUID().toString();
}
