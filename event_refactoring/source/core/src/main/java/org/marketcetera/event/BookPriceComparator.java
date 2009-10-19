package org.marketcetera.event;

import java.util.Comparator;
import java.util.Set;

import org.marketcetera.util.misc.ClassVersion;

/**
 * Compares orders for the purpose of sorting bids and asks according to their price order.
 * 
 * <p>Note that this <code>Comparator</code> breaks the {@link Set} contract.  The result of
 * {@link #compare(QuoteEvent, QuoteEvent)} is not the same as the result of
 * {@link Event#equals(Object)}.  This is OK, but it should be noted.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: QuoteEvent.java 10808 2009-10-12 21:33:18Z anshul $
 * @since 0.6.0
 */
@ClassVersion("$Id: QuoteEvent.java 10808 2009-10-12 21:33:18Z anshul $")
public final class BookPriceComparator
    implements Comparator<QuoteEvent>
{
    public static BookPriceComparator getBidComparator()
    {
        return new BookPriceComparator(false);
    }
    public static BookPriceComparator getAskComparator()
    {
        return new BookPriceComparator(true);
    }
    /**
     * indicates whether to sort ascending or descending
     */
    private final boolean mIsAscending;
    /**
     * Create a new BookComparator instance.
     *
     * @param inIsAscending a <code>boolean</code> value
     */
    private BookPriceComparator(boolean inIsAscending)
    {
        mIsAscending = inIsAscending;
    }
    /* (non-Javadoc)
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    @Override
    public int compare(QuoteEvent inO1,
                       QuoteEvent inO2)
    {
        if(inO1 == inO2) {
            return 0;
        }
        // the first key is price, either ascending or descending
        int result = inO1.getPrice().compareTo(inO2.getPrice());
        if(result == 0) {
            // prices are equal
            // secondary sort should be on the timestamp
            result = new Long(inO1.getTimeMillis()).compareTo(inO2.getTimeMillis());
        }
        // invert the result to be returned if necessary to get a descending sort 
        return result * (mIsAscending ? 1 : -1);
    }
}
