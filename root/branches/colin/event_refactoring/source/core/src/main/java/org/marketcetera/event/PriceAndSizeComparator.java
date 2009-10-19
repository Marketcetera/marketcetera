package org.marketcetera.event;

import java.util.Comparator;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class PriceAndSizeComparator
        implements Comparator<QuoteEvent>
{
    /**
     * 
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
