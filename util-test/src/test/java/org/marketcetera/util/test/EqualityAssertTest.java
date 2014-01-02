package org.marketcetera.util.test;

import org.apache.commons.lang.math.NumberUtils;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.marketcetera.util.test.EqualityAssert.*;

/**
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id$
 */

/* $License$ */

public class EqualityAssertTest
{
    private static class Correct
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
            Correct o=(Correct)other;
            return (getValue()==o.getValue());
        }
    }

    private static final class BadHashCode
        extends Correct
    {
        private static int sNextCode=0;

        public BadHashCode
            (int value)
        {
            super(value);
        }

        @Override
        public int hashCode()
        {
            return sNextCode++;
        }
    }

    private static final class EqualsNull
        extends Correct
    {
        public EqualsNull
            (int value)
        {
            super(value);
        }

        @Override
        public boolean equals
            (Object other)
        {
            return ((other==null) || super.equals(other));
        }
    }

    private static final class EqualsZero
        extends Correct
    {
        public EqualsZero
            (int value)
        {
            super(value);
        }

        @Override
        public boolean equals
            (Object other)
        {
            return ((other==NumberUtils.INTEGER_ZERO) || super.equals(other));
        }
    }

    private static final class SelfUnequals
    {
        @Override
        public String toString()
        {
            return "I am self";
        }

        @Override
        public int hashCode()
        {
            return 0;
        }

        @Override
        public boolean equals
            (Object other)
        {
            return false;
        }
    }

    private static final class CopyUnequals
    {
        @Override
        public String toString()
        {
            return "I am copy";
        }

        @Override
        public int hashCode()
        {
            return 0;
        }

        @Override
        public boolean equals
            (Object other)
        {
            return (this==other);
        }
    }


    @Test
    public void correct()
    {
        assertEquality(new Correct(0),new Correct(0),
                       new Correct(1),new Correct(2),null);
        assertEquality(new Correct(0),new Correct(0),(Object[])null);
        assertEquality(new Correct(0),1,new Object[]
            {new Correct(1),
             new Correct(0),
             new Correct(2)});
    }

    @Test
    public void selfUnequals()
    {
        try {
            assertEquality(new SelfUnequals(),new SelfUnequals());
        } catch (AssertionError ex) {
            assertEquals("'I am self' unequal to self",ex.getMessage());
            return;
        }
        fail();
    }

    @Test
    public void copyUnequals()
    {
        try {
            assertEquality(new CopyUnequals(),new CopyUnequals());
        } catch (AssertionError ex) {
            assertEquals("'I am copy' unequal to 'I am copy'",ex.getMessage());
            return;
        }
        fail();
    }

    @Test
    public void same()
    {
        Correct c=new Correct(0);
        try {
            assertEquality(c,c);
        } catch (AssertionError ex) {
            assertEquals("'I am 0' same as 'I am 0'",ex.getMessage());
            return;
        }
        fail();
    }

    @Test
    public void noDiff()
    {
        try {
            assertEquality(new Correct(0),new Correct(0),new Correct(0));
        } catch (AssertionError ex) {
            assertEquals("'I am 0' equal to 'I am 0'",ex.getMessage());
            return;
        }
        fail();
    }

    @Test
    public void nullInList()
    {
        try {
            assertEquality(new EqualsNull(0),new EqualsNull(0),
                           new Object[]{null});
        } catch (AssertionError ex) {
            assertEquals("'I am 0' equal to 'null'",ex.getMessage());
            return;
        }
        fail();
    }

    @Test
    public void nullIsolated()
    {
        try {
            assertEquality(new EqualsNull(0),new EqualsNull(0));
        } catch (AssertionError ex) {
            assertEquals("'I am 0' equal to null",ex.getMessage());
            return;
        }
        fail();
    }

    @Test
    public void zero()
    {
        try {
            assertEquality(new EqualsZero(0),new EqualsZero(0));
        } catch (AssertionError ex) {
            assertEquals("'I am 0' equal to zero",ex.getMessage());
            return;
        }
        fail();
    }

    @Test
    public void badHashCode()
    {
        try {
            assertEquality(new BadHashCode(0),new BadHashCode(0));
        } catch (AssertionError ex) {
            assertEquals("'I am 0' hash code unequal to copy's 'I am 0'",
                         ex.getMessage());
            return;
        }
        fail();
    }

    @Test
    public void arrayIncorrect()
    {
        try {
            assertEquality(new Correct(0),0,new Object[]
                {new Correct(1),new Correct(0),new Correct(2)});
        } catch (AssertionError ex) {
            assertEquals("'I am 0' unequal to 'I am 1'",ex.getMessage());
            return;
        }
        fail();
    }

    @Test
    public void message()
    {
        try {
            assertEquality("Right now,",new SelfUnequals(),new SelfUnequals());
        } catch (AssertionError ex) {
            assertEquals("Right now, 'I am self' unequal to self",
                         ex.getMessage());
            return;
        }
        fail();
    }

    @Test
    public void arrayMessage()
    {
        try {
            assertEquality("Right now,",new Correct(0),0,new Object[]
                {new Correct(1),new Correct(0),new Correct(2)});
        } catch (AssertionError ex) {
            assertEquals("Right now, 'I am 0' unequal to 'I am 1'",
                         ex.getMessage());
            return;
        }
        fail();
    }
}
