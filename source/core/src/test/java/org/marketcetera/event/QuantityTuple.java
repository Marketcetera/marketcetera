package org.marketcetera.event;

import java.math.BigDecimal;

/**
 * A wrapper class for the quantities associated with a symbol event.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: AbstractEventTranslatorTest.java 9909 2008-10-25 01:05:34Z colin $
 * @since 0.5.0
 */
public class QuantityTuple
{
    /**
     * the price associated with the event
     */
    private final BigDecimal mPrice;
    /**
     * the size associated with the event
     */
    private final BigDecimal mSize;
    /**
     * Create a new QuantityTuple instance.
     *
     * @param inPrice a <code>BigDecimal</code> value
     * @param inSize a <code>BigDecimal</code> value
     */
    public QuantityTuple(BigDecimal inPrice,
                         BigDecimal inSize)
    {
        mPrice = inPrice;
        mSize = inSize;
    }
    /**
     * Gets the price associated with the event.
     *
     * @return a <code>BigDecimal</code> value
     */
    public BigDecimal getPrice()
    {
        return mPrice;
    }
    /**
     * Gets the size associated with the event.
     *
     * @return a <code>BigDecimal</code> value
     */
    public BigDecimal getSize()
    {
        return mSize;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((mPrice == null) ? 0 : mPrice.hashCode());
        result = prime * result + ((mSize == null) ? 0 : mSize.hashCode());
        return result;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final QuantityTuple other = (QuantityTuple) obj;
        if (mPrice == null) {
            if (other.mPrice != null)
                return false;
        } else if (!mPrice.equals(other.mPrice))
            return false;
        if (mSize == null) {
            if (other.mSize != null)
                return false;
        } else if (!mSize.equals(other.mSize))
            return false;
        return true;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return new StringBuilder().append(getSize()).append(" ").append(getPrice()).toString();
    }
}