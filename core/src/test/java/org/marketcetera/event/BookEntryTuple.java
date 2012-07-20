package org.marketcetera.event;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 1.5.0
 */
public class BookEntryTuple
{
    private final QuantityTuple bid;
    private final QuantityTuple ask;
    public BookEntryTuple(QuantityTuple inBid,
                          QuantityTuple inAsk)
    {
        bid = inBid;
        ask = inAsk;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return String.format("%s-%s %sx%s",
                             bid == null ? "---" : bid.getPrice().toPlainString(),
                             ask == null ? "---" : ask.getPrice().toPlainString(),
                             bid == null ? "---" : bid.getSize().toPlainString(),
                             ask == null ? "---" : ask.getSize().toPlainString());
    }
    /**
     * Get the bid value.
     *
     * @return a <code>QuantityTuple</code> value
     */
    public QuantityTuple getBid()
    {
        return bid;
    }
    /**
     * Get the ask value.
     *
     * @return a <code>QuantityTuple</code> value
     */
    public QuantityTuple getAsk()
    {
        return ask;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((ask == null) ? 0 : ask.hashCode());
        result = prime * result + ((bid == null) ? 0 : bid.hashCode());
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
        BookEntryTuple other = (BookEntryTuple) obj;
        if (ask == null) {
            if (other.ask != null)
                return false;
        } else if (!ask.equals(other.ask))
            return false;
        if (bid == null) {
            if (other.bid != null)
                return false;
        } else if (!bid.equals(other.bid))
            return false;
        return true;
    }
}
