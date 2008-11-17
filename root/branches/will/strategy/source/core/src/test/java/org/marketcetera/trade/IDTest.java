package org.marketcetera.trade;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.test.EqualityAssert;
import org.marketcetera.module.ExpectedFailure;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

/* $License$ */
/**
 * Tests all the ID classes in this package. Tested classes
 * include {@link OrderID} & {@link DestinationID}
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class IDTest {
    /**
     * Verify {@link OrderID}
     *
     * @throws Exception if there were errors
     */
    @Test
    public void orderID() throws Exception {
        new ExpectedFailure<NullPointerException>(null){
            protected void run() throws Exception {
                new OrderID(null);
            }
        };
        EqualityAssert.assertEquality(new OrderID("ord-132"),
                new OrderID("ord-132"),
                new OrderID(""), new OrderID("ora-132"),
                new OrderID("ord-133"), new OrderID("xyzkji3948992"));
        
        OrderID id = new OrderID("yes");
        assertEquals("yes", id.getValue());
        assertEquals("yes", id.toString());
    }

    /**
     * Verify {@link DestinationID}
     *
     * @throws Exception if there were errors.
     */
    @Test
    public void destinationID() throws Exception {
        new ExpectedFailure<NullPointerException>(null){
            protected void run() throws Exception {
                new DestinationID(null);
            }
        };
        EqualityAssert.assertEquality(new DestinationID("broke-132"),
                new DestinationID("broke-132"),
                new DestinationID(""), new DestinationID("brokr-132"),
                new DestinationID("broke-133"),
                new DestinationID("xyzkji3948992"));

        DestinationID id = new DestinationID("yes");
        assertEquals("yes", id.getValue());
        assertEquals("yes", id.toString());
    }
}
