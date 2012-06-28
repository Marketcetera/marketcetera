package org.marketcetera.module;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.marketcetera.core.ExpectedFailure;
import org.marketcetera.core.attributes.ClassVersion;

/* $License$ */
/**
 * Tests various ID classes used in this package
 *
 * @author anshul@marketcetera.com
 */
@ClassVersion("$Id: IDTest.java 82330 2012-04-10 16:29:13Z colin $")
public class IDTest extends ModuleTestBase {
    /**
     * Tests the {@link RequestID}.
     *
     * @throws Exception if there were unexpected errors.
     */
    @Test
    public void requestID() throws Exception {
        new ExpectedFailure<NullPointerException>(){
            protected void run() throws Exception {
                new RequestID(null);
            }
        };
        RequestID id = new RequestID("value");
        RequestID id2 = new RequestID("value2");
        RequestID id3 = new RequestID("value");
        RequestID id4 = new RequestID("value");
        assertFalse(id.equals(null));
        assertFalse(id.equals(new Object()));
        assertFalse(id.equals(id2));
        assertTrue(id.hashCode() != id2.hashCode());
        assertEquals(id,id3);
        assertTrue(id.hashCode() == id3.hashCode());
        assertEquals(id,id4);
        assertTrue(id.hashCode() == id4.hashCode());
        assertEquals(id3,id4);
        assertTrue(id3.hashCode() == id3.hashCode());
        assertEquals("value", id4.toString());
    }

    /**
     * Tests the {@link DataFlowID}
     *
     * @throws Exception if there were unexpected errors.
     */
    @Test
    public void flowID() throws Exception {
        new ExpectedFailure<NullPointerException>(){
            protected void run() throws Exception {
                new DataFlowID(null);
            }
        };
        DataFlowID id1 = new DataFlowID("value");
        DataFlowID id2 = new DataFlowID("another");
        DataFlowID id3 = new DataFlowID("value");
        assertFalse(id1.equals(null));
        assertFalse(id1.equals(new Object()));
        assertFalse(id1.equals(id2));
        assertTrue(id1.hashCode() != id2.hashCode());
        assertEquals(id1,id3);
        assertTrue(id1.hashCode() == id3.hashCode());
        assertEquals("another",id2.toString());
    }
}
