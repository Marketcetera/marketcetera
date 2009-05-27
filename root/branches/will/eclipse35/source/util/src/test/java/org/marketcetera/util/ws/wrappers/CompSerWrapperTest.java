package org.marketcetera.util.ws.wrappers;

import java.io.Serializable;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.marketcetera.util.test.ComparableAssert.*;

/**
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id$
 */

/* $License$ */

public class CompSerWrapperTest
    extends WrapperTestBase
{
    private static final TestComparable TEST_COMPARABLE1=
        new TestComparable(1);
    private static final TestComparable TEST_COMPARABLE2=
        new TestComparable(2);


    private static final class TestComparable
        implements Serializable,
                   Comparable<TestComparable>
    {       
        private static final long serialVersionUID=1L;

        private int mValue;

        public TestComparable
            (int value)
        {
            mValue=value;
        }

        public int getValue()
        {
            return mValue;
        }

        @Override
        public int compareTo
            (TestComparable other)
        {
            if (other==null) {
                throw new NullPointerException();
            }
            return getValue()-other.getValue();
        }

        @Override
        public String toString()
        {
            return "I am "+getValue();
        }

        @Override
        public int hashCode()
        {
            return getValue();
        }

        @Override
        public boolean equals
            (Object other)
        {
            if (this==other) {
                return true;
            }
            if ((other==null) || !getClass().equals(other.getClass())) {
                return false;
            }
            TestComparable o=(TestComparable)other;
            return (getValue()==o.getValue());
        }
    }


    @Test
    public void all()
    {
        CompSerWrapper<TestComparable> empty=
            new CompSerWrapper<TestComparable>();
        serialization(new CompSerWrapper<TestComparable>(TEST_COMPARABLE1),
                      new CompSerWrapper<TestComparable>(TEST_COMPARABLE1),
                      empty,
                      new CompSerWrapper<TestComparable>(null),
                      "I am 1",TEST_COMPARABLE1,
                      CompSerWrapper.class.getName());

        assertComparable(TEST_COMPARABLE1,
                         new TestComparable(1),
                         TEST_COMPARABLE2);

        CompSerWrapper<TestComparable> w1=
            new CompSerWrapper<TestComparable>(TEST_COMPARABLE1);
        assertComparable
            (w1,
             new CompSerWrapper<TestComparable>(TEST_COMPARABLE1),
             new CompSerWrapper<TestComparable>(TEST_COMPARABLE2),
             "Argument is null");

        try {
            empty.compareTo(w1);
            fail();
        } catch (NullPointerException ex) {
            assertEquals("Receiver wraps a null value",ex.getMessage());
        }

        try {
            w1.compareTo(empty);
            fail();
        } catch (NullPointerException ex) {
            assertEquals("Argument wraps a null value",ex.getMessage());
        }
    }
}
