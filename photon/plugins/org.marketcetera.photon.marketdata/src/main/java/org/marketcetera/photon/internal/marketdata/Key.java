package org.marketcetera.photon.internal.marketdata;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.marketcetera.trade.Instrument;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Base class for keys that identify unique market data flows.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.5.0
 */
@ClassVersion("$Id$")
public abstract class Key
{
    /**
     * Create a new Key instance.
     *
     * @param inInstrument an <code>Instrument</code> value
     */
    public Key(final Instrument inInstrument)
    {
        Validate.notNull(inInstrument);
        instrument = inInstrument;
    }
    /**
     * Get the requestId value.
     *
     * @return a <code>long</code> value
     */
    public long getRequestId()
    {
        return requestId;
    }
    /**
     * Sets the requestId value.
     *
     * @param inRequestId a <code>long</code> value
     */
    public void setRequestId(long inRequestId)
    {
        requestId = inRequestId;
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
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("Key [").append(requestId).append(" ").append(instrument).append("]");
        return builder.toString();
    }
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        return new HashCodeBuilder().append(requestId).toHashCode();
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
        if (!(obj instanceof Key)) {
            return false;
        }
        Key other = (Key) obj;
        return new EqualsBuilder().append(requestId,other.getRequestId()).isEquals();
    }
    /**
     * 
     */
    private final Instrument instrument;
    /**
     * 
     */
    private long requestId;
}
