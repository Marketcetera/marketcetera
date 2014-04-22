package org.marketcetera.core;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.util.HashSet;

/**
 * @author Graham Miller
 * @version $Id$
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class MemoizedHashCombinatorTest extends TestCase {
    class TestCombinator extends MemoizedHashCombinator<String, Integer> {
        public TestCombinator(String s, Integer i){
            super(s, i);
        }
    }

    public MemoizedHashCombinatorTest(String inName) {
        super(inName);
    }

    public static Test suite() {
        return new TestSuite(MemoizedHashCombinatorTest.class);
    }

    public void testEquals()
    {
        TestCombinator tc1 = new TestCombinator("QWER", 123); //$NON-NLS-1$
        TestCombinator tc2 = new TestCombinator("QWER", 123); //$NON-NLS-1$
        TestCombinator tc3 = new TestCombinator("QWER", 128); //$NON-NLS-1$

        assertEquals(tc1, tc2);
        assertEquals(tc2, tc1);
        assertEquals(tc1, tc1);
        assertFalse(tc2.equals(tc3));
        assertFalse(tc3.equals(tc2));
        assertFalse(tc3.equals((Integer)7));
    }
    public void testHash()
    {
        TestCombinator tc1 = new TestCombinator("QWER", 123); //$NON-NLS-1$
        TestCombinator tc2 = new TestCombinator("QWER", 123); //$NON-NLS-1$
        TestCombinator tc3 = new TestCombinator("QWER", 128); //$NON-NLS-1$

        HashSet<TestCombinator> set = new HashSet<TestCombinator>();
        set.add(tc1);
        set.add(tc2);
        set.add(tc3);
        assertEquals(2, set.size());
        assertTrue(set.contains(tc1));
        assertTrue(set.contains(tc2));
        assertTrue(set.contains(tc3));
    }
    

}
