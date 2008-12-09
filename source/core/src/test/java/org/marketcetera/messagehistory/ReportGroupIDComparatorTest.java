package org.marketcetera.messagehistory;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.marketcetera.trade.OrderID;


/* $License$ */

/**
 * Test {@link ReportGroupIDComparator}.
 *
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.0.0
 */
public class ReportGroupIDComparatorTest {
    
    @Test
    public void testComparator() throws Exception {
        ReportGroupIDComparator comparator = new ReportGroupIDComparator();
        ReportHolder mha = new ReportHolder(null, new OrderID("A")); //$NON-NLS-1$
        ReportHolder mhc = new ReportHolder(null, new OrderID("C")); //$NON-NLS-1$
        ReportHolder mha2 = new ReportHolder(null, new OrderID("A")); //$NON-NLS-1$
        ReportHolder mhnull = new ReportHolder(null, null);
        assertEquals(-2, comparator.compare(mha, mhc));
        assertEquals(0, comparator.compare(mha, mha2));
        assertEquals(2, comparator.compare(mhc, mha));
        assertEquals(1, comparator.compare(mha, null));
        assertEquals(-1, comparator.compare(null, mha));
        assertEquals(1, comparator.compare(mha, mhnull));
        assertEquals(-1, comparator.compare(mhnull, mha));
    }

}
