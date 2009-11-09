package org.marketcetera.event.util;

import java.util.Comparator;

import javax.annotation.concurrent.Immutable;

import org.marketcetera.event.QuoteEvent;
import org.marketcetera.util.misc.ClassVersion;

/**
 * Compares orders for the purpose of sorting bids and asks according to their price order.
 * 
 * <p>Note that this <code>Comparator</code> is not consistent with the result of
 * <code>equals(Object)</code>.  This is OK, but it should be noted.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 0.6.0
 */
@Immutable
@ClassVersion("$Id$")
public final class BookPriceComparator
    implements Comparator<QuoteEvent>
{
    /**
     * a <code>Comparator</code> suitable for sorting bids in an order book
     */
    public static final BookPriceComparator bidComparator = new BookPriceComparator(false);
    /**
     * a <code>Comparator</code> suitable for sorting asks in an order book
     */
    public static final BookPriceComparator askComparator = new BookPriceComparator(true);
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
