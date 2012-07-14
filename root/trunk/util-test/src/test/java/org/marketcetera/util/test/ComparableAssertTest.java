package org.marketcetera.util.test;

import org.junit.Test;

import static org.junit.Assert.*;
import static org.marketcetera.util.test.ComparableAssert.*;

/**
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id$
 */

/* $License$ */

public class ComparableAssertTest
{
    private static final String TEST_MESSAGE=
        "testMessage";


    private static class Correct
        implements Comparable<Correct>
    {       
        private int mValue;

        public Correct
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
            (Correct other)
        {
            if (other==null) {
                throw new NullPointerException(TEST_MESSAGE);
            }
            return getValue()-other.getValue();
        }

        @Override
        public String toString()
        {
            return "I am "+getValue();
        }
    }

    private static final class SelfUnequals
        extends Correct
    {
        public SelfUnequals
            (int value)
        {
            super(value);
        }

        @Override
        public int compareTo
            (Correct other)
        {
            return 1;
        }
    }

    private static final class CopyUnequals
        extends Correct
    {
        public CopyUnequals
            (int value)
        {
            super(value);
        }

        @Override
        public int compareTo
            (Correct other)
        {
            return ((this==other)?0:1);
        }
    }

    private static final class ReverseIncorrect
        extends Correct
    {
        public ReverseIncorrect
            (int value)
        {
            super(value);
        }

        @Override
        public int compareTo
            (Correct other)
        {
            if (other==null) {
                throw new NullPointerException(TEST_MESSAGE);
            }
            if (other instanceof ReverseIncorrect) {
                return getValue()-other.getValue();
            } else {
                return other.getValue()-getValue();
            }
        }
    }


    @Test
    public void correct()
    {
        assertComparable(new Correct(0),new Correct(0),new Correct(1),
                         TEST_MESSAGE);
        assertComparable(new Correct(0),new Correct(0),new Correct(1));
    }

    @Test
    public void selfUnequals0()
    {
        try {
            assertComparable(new SelfUnequals(0),new Correct(1),new Correct(2));
        } catch (AssertionError ex) {
            assertEquals("'I am 0' unequal to self",ex.getMessage());
            return;
        }
        fail();
    }

    @Test
    public void selfUnequals1()
    {
        try {
            assertComparable(new Correct(0),new SelfUnequals(1),new Correct(2));
        } catch (AssertionError ex) {
            assertEquals("'I am 1' unequal to self",ex.getMessage());
            return;
        }
        fail();
    }

    @Test
    public void selfUnequals2()
    {
        try {
            assertComparable(new Correct(0),new Correct(1),new SelfUnequals(2));
        } catch (AssertionError ex) {
            assertEquals("'I am 2' unequal to self",ex.getMessage());
            return;
        }
        fail();
    }

    @Test
    public void copyUnequals0()
    {
        try {
            assertComparable(new Correct(0),new Correct(1),new Correct(2));
        } catch (AssertionError ex) {
            assertEquals("'I am 0' unequal to 'I am 1'",ex.getMessage());
            return;
        }
        fail();
    }

    @Test
    public void copyUnequals1()
    {
        try {
            assertComparable(new Correct(0),new CopyUnequals(0),new Correct(2));
        } catch (AssertionError ex) {
            assertEquals("'I am 0' unequal to 'I am 0'",ex.getMessage());
            return;
        }
        fail();
    }

    @Test
    public void same()
    {
        Correct c=new Correct(0);
        try {
            assertComparable(c,c,c);
        } catch (AssertionError ex) {
            assertEquals("'I am 0' same as 'I am 0'",ex.getMessage());
            return;
        }
        fail();
    }

    @Test
    public void noGreater1()
    {
        try {
            assertComparable(new Correct(0),new Correct(0),new Correct(0));
        } catch (AssertionError ex) {
            assertEquals("'I am 0' no less than 'I am 0'",ex.getMessage());
            return;
        }
        fail();
    }

    @Test
    public void noGreater2()
    {
        try {
            assertComparable(new Correct(0),new ReverseIncorrect(0),
                             new Correct(1));
        } catch (AssertionError ex) {
            assertEquals("'I am 0' no less than 'I am 1'",ex.getMessage());
            return;
        }
        fail();
    }

    @Test
    public void noLesser1()
    {
        try {
            assertComparable(new Correct(0),new Correct(0),
                             new ReverseIncorrect(1));
        } catch (AssertionError ex) {
            assertEquals("'I am 1' no more than 'I am 0'",ex.getMessage());
            return;
        }
        fail();
    }

    @Test
    public void noLesser2()
    {
        try {
            assertComparable(new ReverseIncorrect(0),new Correct(0),
                             new ReverseIncorrect(1));
        } catch (AssertionError ex) {
            assertEquals("'I am 1' no more than 'I am 0'",ex.getMessage());
            return;
        }
        fail();
    }

    @Test
    public void badMessage()
    {
        String expected=TEST_MESSAGE+"x";
        try {
            assertComparable(new Correct(0),new Correct(0),new Correct(1),
                             expected);
        } catch (AssertionError ex) {
            assertEquals("expected message '"+expected+
                         "' does not match actual '"+TEST_MESSAGE+"'",
                         ex.getMessage());
            return;
        }
        fail();
    }

    @Test
    public void prefix()
    {
        try {
            assertComparable("Right now,",new SelfUnequals(0),new Correct(1),
                             new Correct(2));
        } catch (AssertionError ex) {
            assertEquals("Right now, 'I am 0' unequal to self",ex.getMessage());
            return;
        }
        fail();
    }

    @Test
    public void prefixBadMessage()
    {
        String expected=TEST_MESSAGE+"x";
        try {
            assertComparable("Right now,",new Correct(0),new Correct(0),
                             new Correct(1),expected);
        } catch (AssertionError ex) {
            assertEquals("Right now, expected message '"+expected+
                         "' does not match actual '"+TEST_MESSAGE+"'",
                         ex.getMessage());
            return;
        }
        fail();
    }
}
