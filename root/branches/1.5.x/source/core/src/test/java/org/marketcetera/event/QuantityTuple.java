package org.marketcetera.event;

import java.math.BigDecimal;
import java.util.Comparator;

/**
 * A wrapper class for the quantities associated with a symbol event.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
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
     * the type of the event
     */
    private final Class<? extends SymbolExchangeEvent> type;
    /**
     * Create a new QuantityTuple instance.
     *
     * @param inPrice a <code>BigDecimal</code> value
     * @param inSize a <code>BigDecimal</code> value
     */
    public QuantityTuple(BigDecimal inPrice,
                         BigDecimal inSize,
                         Class<? extends SymbolExchangeEvent> inType)
    {
        mPrice = inPrice;
        mSize = inSize;
        type = inType;
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
    /**
     * Get the type value.
     *
     * @return a <code>Class<? extends SymbolExchangeEvent></code> value
     */
    public Class<? extends SymbolExchangeEvent> getType()
    {
        return type;
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
        result = prime * result + ((type == null) ? 0 : type.hashCode());
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
        QuantityTuple other = (QuantityTuple) obj;
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
        if (type == null) {
            if (other.type != null)
                return false;
        } else if (!type.equals(other.type))
            return false;
        return true;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return new StringBuilder().append(type.getSimpleName()).append(" ").append(getSize()).append(" ").append(getPrice()).toString();
    }
    /**
     * Compares two {@link QuantityTuple} objects based on {@link QuantityTuple#getPrice()}.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since 1.5.0
     */
    public static class PriceComparator
        implements Comparator<QuantityTuple>
    {
        public static final PriceComparator ASCENDING = new PriceComparator(true);
        public static final PriceComparator DESCENDING = new PriceComparator(false);
        private final boolean ascending;
        private PriceComparator(boolean inAscending)
        {
            ascending = inAscending;
        }
        /* (non-Javadoc)
         * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
         */
        @Override
        public int compare(QuantityTuple inO1,
                           QuantityTuple inO2)
        {
            return inO1.getPrice().compareTo(inO2.getPrice()) * (ascending ? 1 : -1);
        }
    }
}