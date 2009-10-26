package org.marketcetera.event.util;

import java.util.Comparator;

import javax.annotation.concurrent.Immutable;

import org.marketcetera.event.QuoteEvent;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Compares two <code>QuoteEvent</code> values based on their price and size
 * only.
 * 
 * <p>Price is compared first, followed by size, if necessary.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: QuoteEvent.java 10808 2009-10-12 21:33:18Z anshul $
 * @since 1.5.0
 */
@Immutable
@ClassVersion("$Id: QuoteEvent.java 10808 2009-10-12 21:33:18Z anshul $")
public class PriceAndSizeComparator
        implements Comparator<QuoteEvent>
{
    /**
     * the instance to use for comparisons
     */
    public static final PriceAndSizeComparator instance = new PriceAndSizeComparator();
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
        int result;
        if((result = inO1.getPrice().compareTo(inO2.getPrice())) != 0) {
            return result;
        }
        return inO1.getSize().compareTo(inO2.getSize());
    }
    /**
     * Create a new PriceAndSizeComparator instance.
     */
    private PriceAndSizeComparator() {}
}
