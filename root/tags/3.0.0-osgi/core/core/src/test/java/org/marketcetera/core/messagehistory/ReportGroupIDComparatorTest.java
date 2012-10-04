package org.marketcetera.core.messagehistory;

import org.junit.Test;
import org.marketcetera.core.trade.OrderID;

import static org.junit.Assert.assertEquals;


/* $License$ */

/**
 * Test {@link org.marketcetera.core.messagehistory.ReportGroupIDComparator}.
 *
 * @version $Id: ReportGroupIDComparatorTest.java 16063 2012-01-31 18:21:55Z colin $
 * @since 1.0.0
 */
public class ReportGroupIDComparatorTest {
    
    @Test
    public void testComparator() throws Exception {
        ReportGroupIDComparator comparator = new ReportGroupIDComparator();
        ReportHolder mha = new ReportHolder(null, null, new OrderID("A")); //$NON-NLS-1$
        ReportHolder mhc = new ReportHolder(null, null, new OrderID("C")); //$NON-NLS-1$
        ReportHolder mha2 = new ReportHolder(null, null, new OrderID("A")); //$NON-NLS-1$
        ReportHolder mhnull = new ReportHolder(null, null, null);
        assertEquals(-2, comparator.compare(mha, mhc));
        assertEquals(0, comparator.compare(mha, mha2));
        assertEquals(2, comparator.compare(mhc, mha));
        assertEquals(1, comparator.compare(mha, null));
        assertEquals(-1, comparator.compare(null, mha));
        assertEquals(1, comparator.compare(mha, mhnull));
        assertEquals(-1, comparator.compare(mhnull, mha));
    }

}
