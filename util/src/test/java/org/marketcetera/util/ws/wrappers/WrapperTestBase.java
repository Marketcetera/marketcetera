package org.marketcetera.util.ws.wrappers;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.marketcetera.util.test.EqualityAssert.assertEquality;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Locale;

import javax.xml.bind.JAXBContext;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.SerializationUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.junit.Before;
import org.marketcetera.util.except.I18NException;
import org.marketcetera.util.except.I18NThrowable;
import org.marketcetera.util.log.ActiveLocale;
import org.marketcetera.util.log.I18NBoundMessage0P;
import org.marketcetera.util.log.I18NBoundMessage1P;
import org.marketcetera.util.log.I18NLoggerProxy;
import org.marketcetera.util.log.I18NMessage0P;
import org.marketcetera.util.log.I18NMessageProvider;
import org.marketcetera.util.test.TestCaseBase;

/**
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id$
 */

/* $License$ */

public class WrapperTestBase
    extends TestCaseBase
{
    protected static final String TEST_VALUE=
        "testValue";
    protected static final String TEST_VALUE_D=
        "testValueD";

    protected static final String TEST_MESSAGE=
        "testMessage";

    protected static final I18NBoundMessage1P TEST_I18N_MESSAGE=
        new I18NBoundMessage1P(TestMessages.BOUND,TEST_MESSAGE);
    protected static final I18NBoundMessage1P TEST_NONSER_MESSAGE=
        new I18NBoundMessage1P(TestMessages.BOUND,
                               new TestUnserializableInteger(1));
    protected static final I18NBoundMessage0P TEST_NONDESER_MESSAGE=
        new I18NBoundMessage0P
        (new I18NMessage0P
         (new I18NLoggerProxy
          (new I18NMessageProvider("nonexistent_prv")),"any"));

    protected static final Throwable TEST_THROWABLE=
        new TestThrowable(TEST_MESSAGE);
    protected static final I18NException TEST_I18N_THROWABLE=
        new I18NException(TEST_THROWABLE,TEST_I18N_MESSAGE);
    protected static final Throwable TEST_NONSER_THROWABLE=
        new TestUnserializableThrowable(TEST_MESSAGE);
    protected static final I18NException TEST_NONDESER_THROWABLE=
        new I18NException(TEST_THROWABLE,TEST_NONDESER_MESSAGE);


    protected static class TestInteger
        implements Serializable
    {
        private static final long serialVersionUID=1L;

        private int mValue;

        public TestInteger
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
            TestInteger o=(TestInteger)other;
            return (getValue()==o.getValue());
        }
    }

    protected static class TestUnserializableInteger
        extends TestInteger
    {
        private static final long serialVersionUID=1L;

        public TestUnserializableInteger
            (int value)
        {
            super(value);
        }

        private void writeObject(ObjectOutputStream out)
            throws IOException
        {
            throw new IOException();
        }
    }

    protected static class TestThrowable
        extends Throwable
    {
        private static final long serialVersionUID=1L;

        public TestThrowable
            (String message)
        {
            super(message);
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
            return ObjectUtils.equals(toString(),other.toString());
        }
    }

    protected static class TestUnserializableThrowable
        extends Throwable
    {
        private static final long serialVersionUID=1L;

        public TestUnserializableThrowable
            (String message)
        {
            super(message);
        }

        private void writeObject(ObjectOutputStream out)
            throws IOException
        {
            throw new IOException();
        }
    }


    @Before
    public void setupWrapperTestBase()
    {
        ActiveLocale.setProcessLocale(Locale.ROOT);
    }


    @SuppressWarnings("unchecked")
    protected static <T> T roundTripJAXB
        (T object)
        throws Exception
    {
        JAXBContext context=JAXBContext.newInstance
            (RootElementWrapper.class,object.getClass());
        StringWriter writer=new StringWriter();
        context.createMarshaller().marshal
            (new RootElementWrapper<T>(object),writer);
        return (T)(((RootElementWrapper<?>)
                    (context.createUnmarshaller().unmarshal
                     (new StringReader(writer.toString())))).getObject());
    }

    @SuppressWarnings("unchecked")
    protected static <T extends Serializable> T roundTripJava
        (T object)
    {
        return (T)SerializationUtils.deserialize
            (SerializationUtils.serialize(object));
    }

    protected static <T> void single
        (BaseWrapper<T> wrapper,
         BaseWrapper<T> copy,
         BaseWrapper<T> empty,
         BaseWrapper<T> nullArg,
         String stringValue)
        throws Exception
    {
        assertEquality(wrapper,copy,empty,nullArg);
        assertEquality(empty,nullArg,wrapper,copy);

        assertEquals(stringValue,wrapper.toString());

        assertNull(empty.getValue());
        assertNull(nullArg.getValue());

        assertEquals(wrapper,roundTripJAXB(wrapper));
    }

    protected static <R,M> void dual
        (DualWrapper<R,M> wrapper,
         DualWrapper<R,M> copy,
         DualWrapper<R,M> empty,
         DualWrapper<R,M> nullArg,
         String stringValue,
         R rawValue,
         M marshalledValue)
        throws Exception
    {
        single(wrapper,copy,empty,nullArg,stringValue);

        assertEquals(wrapper,roundTripJava(wrapper));
        
        assertNull(empty.getRaw());
        assertNull(empty.getMarshalled());

        assertNull(nullArg.getRaw());
        assertNull(nullArg.getMarshalled());

        wrapper.setMarshalled(null);
        assertNull(wrapper.getRaw());
        assertNull(wrapper.getMarshalled());
        assertEquals(wrapper,empty);

        wrapper.setMarshalled(marshalledValue);
        assertTrue(ArrayUtils.isEquals(rawValue,wrapper.getRaw()));
        assertTrue(ArrayUtils.isEquals
                   (marshalledValue,wrapper.getMarshalled()));

        wrapper.setRaw(null);
        assertNull(wrapper.getRaw());
        assertNull(wrapper.getMarshalled());
        assertEquals(wrapper,empty);

        wrapper.setRaw(rawValue);
        assertTrue(ArrayUtils.isEquals(rawValue,wrapper.getRaw()));
        assertTrue(ArrayUtils.isEquals
                   (marshalledValue,wrapper.getMarshalled()));
    }

    protected void prepareSerWrapperFailure
        (String category)
    {
    }

    protected void prepareSerWrapperFailure()
    {
        prepareSerWrapperFailure(SerWrapper.class.getName());
    }

    protected void assertSerWrapperDeSerFailure
        (SerWrapper<?> wrapper,
         String category)
    {
        assertNotNull(wrapper.getDeserializationException());
        assertNull(wrapper.getRaw());
        assertNull(wrapper.getMarshalled());
    }

    protected void assertSerWrapperDeSerFailure
        (SerWrapper<?> wrapper)
    {
        assertSerWrapperDeSerFailure(wrapper,SerWrapper.class.getName());
    }

    protected void assertSerWrapperSerFailure
        (SerWrapper<?> wrapper,
         String category)
    {
        assertNotNull(wrapper.getSerializationException());
        assertNull(wrapper.getRaw());
        assertNull(wrapper.getMarshalled());
    }

    protected void assertSerWrapperSerFailure
        (SerWrapper<?> wrapper)
    {
        assertSerWrapperSerFailure(wrapper,SerWrapper.class.getName());
    }

    protected <T extends Serializable> void serialization
        (SerWrapper<T> wrapper,
         SerWrapper<T> copy,
         SerWrapper<T> empty,
         SerWrapper<T> nullArg,
         String stringValue,
         T value,
         T unserializableValue,
         String category)
        throws Exception
    {
        dual(wrapper,copy,empty,nullArg,stringValue,value,
             SerializationUtils.serialize(value));
        assertNull(wrapper.getSerializationException());
        assertNull(wrapper.getDeserializationException());

        prepareSerWrapperFailure(category);
        wrapper.setRaw(unserializableValue);
        assertSerWrapperSerFailure(wrapper,category);
        prepareSerWrapperFailure(category);
        wrapper.setMarshalled(ArrayUtils.EMPTY_BYTE_ARRAY);
        assertSerWrapperDeSerFailure(wrapper,category);
    }

    protected <T> T assertRoundTripJAXB
        (T object)
        throws Exception
    {
        prepareSerWrapperFailure();
        T result=roundTripJAXB(object);
        assertEquals(object,result);
        return result;
    }

    protected <T extends Serializable> T assertRoundTripJava
        (T object)
    {
        prepareSerWrapperFailure();
        T result=roundTripJava(object);
        assertEquals(object,result);
        return result;
    }

    protected static void assertThrowable
        (Throwable expected,
         Throwable actual,
         boolean proxyUsed)
    {
        if ((actual==null) || (expected==null)) {
            assertEquals(expected,actual);
            return;
        }
        if (proxyUsed) {
            assertEquals(RemoteProxyException.class,actual.getClass());
            if (expected instanceof I18NThrowable) {
                assertEquals(((I18NThrowable)expected).getLocalizedDetail(),
                             actual.getMessage());
                ActiveLocale.setProcessLocale(Locale.FRENCH);
                assertEquals(((I18NThrowable)expected).getLocalizedDetail(),
                             actual.getMessage());
                ActiveLocale.setProcessLocale(Locale.ROOT);
            } else {
                assertEquals(expected.getLocalizedMessage(),
                             actual.getMessage());
            }
        } else {
            assertEquals(expected.getClass(),actual.getClass());
            assertEquals(expected.getMessage(),actual.getMessage());
        }
        assertEquals(expected.toString(),actual.toString());
        assertArrayEquals(ExceptionUtils.getStackFrames(expected),
                          ExceptionUtils.getStackFrames(actual));
    }
}
