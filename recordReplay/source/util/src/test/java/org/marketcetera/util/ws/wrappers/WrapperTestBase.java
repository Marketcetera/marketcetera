package org.marketcetera.util.ws.wrappers;

import java.io.Serializable;
import java.util.Locale;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.SerializationUtils;
import org.apache.log4j.Level;
import org.junit.Before;
import org.marketcetera.util.log.ActiveLocale;
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
    }


    protected static <T> void single
        (BaseWrapper<T> wrapper,
         BaseWrapper<T> copy,
         BaseWrapper<T> empty,
         BaseWrapper<T> nullArg,
         String stringValue)
    {
        assertEquality(wrapper,copy,empty,nullArg);
        assertEquality(empty,nullArg,wrapper,copy);

        assertEquals(stringValue,wrapper.toString());

        assertNull(empty.getValue());
        assertNull(nullArg.getValue());
    }

    protected static <R,M> void dual
        (DualWrapper<R,M> wrapper,
         DualWrapper<R,M> copy,
         DualWrapper<R,M> empty,
         DualWrapper<R,M> nullArg,
         String stringValue,
         R rawValue,
         M marshalledValue)
    {
        single(wrapper,copy,empty,nullArg,stringValue);
        
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

    protected <T extends Serializable> void serialization
        (SerWrapper<T> wrapper,
         SerWrapper<T> copy,
         SerWrapper<T> empty,
         SerWrapper<T> nullArg,
         String stringValue,
         T value,
         String category)
    {
        dual(wrapper,copy,empty,nullArg,stringValue,value,
             SerializationUtils.serialize(value));

        setLevel(category,Level.ERROR);
        wrapper.setMarshalled(ArrayUtils.EMPTY_BYTE_ARRAY);
        assertSingleEvent(Level.ERROR,category,
             "A serialization error has occured; the object will be assumed "+
             "to have a null value",SerWrapper.class.getName());
        assertNull(wrapper.getRaw());
        assertNull(wrapper.getMarshalled());
    }
}
