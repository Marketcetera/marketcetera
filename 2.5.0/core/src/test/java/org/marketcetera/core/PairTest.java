package org.marketcetera.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import org.junit.Test;

/* $License$ */

/**
 * Tests {@link Pair}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 1.5.0
 */
public class PairTest
{
    /**
     * Tests creation and equality of {@link Pair} objects.
     *
     * @throws Exception if an error occurs
     */
    @Test
    public void theOnlyTest()
        throws Exception
    {
        String object1 = "string";
        String object2 = "other-string";
        String object3 = "string";
        Integer object4 = 4;
        Pair<String,String> p1 = new Pair<String,String>(null,
                                                         null);
        Pair<String,String> p2 = new Pair<String,String>(null,
                                                         null);
        Pair<String,String> p3 = new Pair<String,String>(object1,
                                                         null);
        Pair<String,String> p4 = new Pair<String,String>(object1,
                                                         object2);
        Pair<String,String> p5 = new Pair<String,String>(object1,
                                                         object3);
        Pair<String,String> p6 = new Pair<String,String>(object1,
                                                         object3);
        Pair<String,Integer> p7 = new Pair<String,Integer>(object1,
                                                           object4);
        Pair<Object,Object> p8 = new Pair<Object,Object>(object1,
                                                         object4);
        verifyEquals(p1,
                     p1);
        verifySame(p1,
                   p1);
        verifyEquals(p1,
                     p2);
        verifyNotEqual(p1,
                       p3);
        verifyNotEqual(p3,
                       p1);
        verifyNotEqual(p3,
                       p4);
        verifyNotEqual(p4,
                       p3);
        verifyNotEqual(p4,
                       p5);
        verifyEquals(p5,
                     p6);
        verifyNotEqual(p1,
                       p7);
        verifyNotEqual(p6,
                       p7);
        verifyEquals(p7,
                     p8);
    }
    /**
     * Verifies that the given <code>Pair</code> objects are equal.
     *
     * @param inP1 a <code>Pair&lt;?,?&gt;</code> value
     * @param inP2 a <code>Pair&lt;?,?&gt;</code> value
     * @throws Exception if an error occurs
     */
    private void verifyEquals(Pair<?,?> inP1,
                              Pair<?,?> inP2)
        throws Exception
    {
        assertEquals(inP1,
                     inP2);
        assertEquals(inP1.hashCode(),
                     inP2.hashCode());
        assertFalse(inP1.equals(null));
        assertFalse(inP1.equals(this));
        assertNotNull(inP1.toString());
        assertNotNull(inP2.toString());
    }
    /**
     * Verifies that the given <code>Pair</code> objects are not equal.
     *
     * @param inP1 a <code>Pair&lt;?,?&gt;</code> value
     * @param inP2 a <code>Pair&lt;?,?&gt;</code> value
     * @throws Exception if an error occurs
     */
    private void verifyNotEqual(Pair<?,?> inP1,
                                Pair<?,?> inP2)
        throws Exception
    {
        assertFalse(inP1.equals(inP2));
        // note that inequal objects do not always hash to inequal values
        assertFalse(inP1.hashCode() == inP2.hashCode());
    }
    /**
     * Verifies that the given <code>Pair</code> objects are the same.
     *
     * @param inP1 a <code>Pair&lt;?,?&gt;</code> value
     * @param inP2 a <code>Pair&lt;?,?&gt;</code> value
     * @throws Exception if an error occurs
     */
    private void verifySame(Pair<?,?> inP1,
                            Pair<?,?> inP2)
        throws Exception
    {
        assertSame(inP1,
                   inP2);
    }
}
