package org.marketcetera.util.test;

import java.io.Serializable;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.SerializationException;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.marketcetera.util.test.SerializableAssert.*;

/**
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id$
 */

/* $License$ */

public class SerializableAssertTest
{
    private static final class TransientData
        implements Serializable
    {
        private static final long serialVersionUID=1L;

        private transient int mValue;

        public TransientData
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
            TransientData o=(TransientData)other;
            return (getValue()==o.getValue());
        }
    }

    private static final class NonSerializableMember
    {
        private int mValue;

        public NonSerializableMember
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
            NonSerializableMember o=(NonSerializableMember)other;
            return (getValue()==o.getValue());
        }
    }

    private static final class NonSerializableClass
        implements Serializable
    {
        private static final long serialVersionUID=1L;

        private NonSerializableMember mValue;

        public NonSerializableClass
            (NonSerializableMember value)
        {
            mValue=value;
        }

        public NonSerializableMember getValue()
        {
            return mValue;
        }

        @Override
        public String toString()
        {
            return ObjectUtils.toString(getValue());
        }

        @Override
        public int hashCode()
        {
            return ObjectUtils.hashCode(getValue());
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
            NonSerializableClass o=(NonSerializableClass)other;
            return ObjectUtils.equals(getValue(),o.getValue());
        }
    }


    @Test
    public void serializable()
    {
        assertSerializable(0);
    }

    @Test
    public void transientData()
    {
        try {
            assertSerializable(new TransientData(1));
        } catch (AssertionError ex) {
            assertEquals("expected object is 'I am 1' actual is 'I am 0'",
                         ex.getMessage());
            assertNull(ex.getCause());
            return;
        }
        fail();
    }

    @Test
    public void nonSerializable()
    {
        try {
            assertSerializable(new NonSerializableClass
                                (new NonSerializableMember(1)));
        } catch (AssertionError ex) {
            assertEquals("de/serialization failed",ex.getMessage());
            assertEquals(SerializationException.class,ex.getCause().getClass());
            return;
        }
        fail();
    }

    @Test
    public void message()
    {
        try {
            assertSerializable("Right now,",new TransientData(1));
        } catch (AssertionError ex) {
            assertEquals
                ("Right now, expected object is 'I am 1' actual is 'I am 0'",
                 ex.getMessage());
            assertNull(ex.getCause());
            return;
        }
        fail();
    }
}
