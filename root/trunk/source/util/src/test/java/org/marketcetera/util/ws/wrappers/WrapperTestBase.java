package org.marketcetera.util.ws.wrappers;

import java.io.Serializable;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Locale;
import javax.xml.bind.JAXBContext;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.SerializationUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.Level;
import org.junit.Before;
import org.marketcetera.util.except.I18NThrowable;
import org.marketcetera.util.log.ActiveLocale;
import org.marketcetera.util.log.I18NBoundMessage0P;
import org.marketcetera.util.log.I18NLoggerProxy;
import org.marketcetera.util.log.I18NMessage0P;
import org.marketcetera.util.log.I18NMessageProvider;
import org.marketcetera.util.test.TestCaseBase;

import static org.junit.Assert.*;
import static org.marketcetera.util.test.EqualityAssert.*;

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


    @Before
    public void setupWrapperTestBase()
    {
        ActiveLocale.setProcessLocale(Locale.ROOT);
        setDefaultLevel(Level.OFF);
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
        setLevel(category,Level.WARN);
    }

    protected void prepareSerWrapperFailure()
    {
        prepareSerWrapperFailure(SerWrapper.class.getName());
    }

    protected void assertSerWrapperFailure
        (SerWrapper<?> wrapper,
         String category)
    {
        assertNotNull(wrapper.getDeserializationException());
        assertSingleEvent(Level.WARN,category,
             "A serialization error has occured; the object will be assumed "+
             "to have a null value",SerWrapper.class.getName());
        assertNull(wrapper.getRaw());
        assertNull(wrapper.getMarshalled());
    }

    protected void assertSerWrapperFailure
        (SerWrapper<?> wrapper)
    {
        assertSerWrapperFailure(wrapper,SerWrapper.class.getName());
    }

    protected <T extends Serializable> void serialization
        (SerWrapper<T> wrapper,
         SerWrapper<T> copy,
         SerWrapper<T> empty,
         SerWrapper<T> nullArg,
         String stringValue,
         T value,
         String category)
        throws Exception
    {
        dual(wrapper,copy,empty,nullArg,stringValue,value,
             SerializationUtils.serialize(value));
        assertNull(wrapper.getDeserializationException());

        prepareSerWrapperFailure(category);
        wrapper.setMarshalled(ArrayUtils.EMPTY_BYTE_ARRAY);
        assertSerWrapperFailure(wrapper,category);
    }

    protected static I18NBoundMessage0P createBadProviderMessage()
    {
        return new I18NBoundMessage0P
            (new I18NMessage0P
             (new I18NLoggerProxy
              (new I18NMessageProvider("nonexistent_prv")),"any"));
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
