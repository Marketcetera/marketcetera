package org.marketcetera.util.misc;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import org.apache.commons.lang.ArrayUtils;
import org.junit.Test;
import org.marketcetera.util.except.I18NInterruptedRuntimeException;
import org.marketcetera.util.test.TestCaseBase;

import static org.junit.Assert.*;
import static org.marketcetera.util.test.CollectionAssert.*;

/**
 * @author tlerios@marketcetera.com
 * @since 0.6.0
 * @version $Id$
 */

/* $License$ */

public class ReflectUtilsTest
    extends TestCaseBase
{
    private static final String OBJECT_NAME=
        Object.class.getName();
    private static final String EMPTY_NAME=
        Empty.class.getName();
    private static final String SIMPLE_NAME=
        Simple.class.getName();
    private static final String DERIVED_NAME=
        Derived.class.getName();
    private static final String IFACE_NAME=
        IFace.class.getName();
    private static final String COMPLEX_BASE_NAME=
        ComplexBase.class.getName();
    private static final String COMPLEX_DERIVED_NAME=
        ComplexDerived.class.getName();

    private static final String SIMPLE_FIELD=
        "mSimple";
    private static final String DERIVED_FIELD=
        "mDerived";
    private static final String IFACE_FIELD=
        "mIFace";
    private static final String COMPLEX_BASE_FIELD=
        "mComplexBase";
    private static final String COMPLEX_DERIVED_FIELD=
        "mComplexDerived";


    private static class Empty {}

    private static class Simple
    {
        int mSimple;
    }

    private static class Derived
        extends Simple
    {
        int mDerived;
    }

    private static interface IFace
    {
        int mIFace=0;
    }

    private static class ComplexBase
        extends Simple
        implements IFace
    {
        int mComplexBase;
    }

    private static class ComplexDerived
        extends ComplexBase
        implements IFace
    {
        int mComplexDerived;
    }

    private static class IntObject
    {
        private int mValue;

        public IntObject
            (int value)
        {
            if (value==0) {
                throw new I18NInterruptedRuntimeException();
            }
            mValue=value;
        }

        public int getValue()
        {
            return mValue;
        }
    }


    private static void assertNamesMatch
        (String[] expectedNames,
         Field[] fields)
    {
        String[] actualNames=new String[fields.length];
        for (int i=0;i<fields.length;i++) {
            actualNames[i]=fields[i].getName();
        }
        assertArrayPermutation(expectedNames,actualNames);
    }

    private static void assertNamesMatch
        (String[] expectedNames,
         Class<?>[] classes)
    {
        String[] actualNames=new String[classes.length];
        for (int i=0;i<classes.length;i++) {
            actualNames[i]=classes[i].getName();
        }
        assertArrayPermutation(expectedNames,actualNames);
    }


    @Test
    public void classes()
    {
        assertNamesMatch
            (new String[] {
                OBJECT_NAME,
                EMPTY_NAME
            },ReflectUtils.getAllClasses(Empty.class));
        assertNamesMatch
            (new String[] {
                OBJECT_NAME,
                SIMPLE_NAME
            },ReflectUtils.getAllClasses(Simple.class));
        assertNamesMatch
            (new String[] {
                OBJECT_NAME,
                SIMPLE_NAME,
                DERIVED_NAME
            },ReflectUtils.getAllClasses(Derived.class));
        assertNamesMatch
            (new String[] {
                IFACE_NAME
            },ReflectUtils.getAllClasses(IFace.class));
        assertNamesMatch
            (new String[] {
                OBJECT_NAME,
                SIMPLE_NAME,
                IFACE_NAME,
                COMPLEX_BASE_NAME
            },ReflectUtils.getAllClasses(ComplexBase.class));
        assertNamesMatch
            (new String[] {
                OBJECT_NAME,
                SIMPLE_NAME,
                IFACE_NAME,
                COMPLEX_BASE_NAME,
                COMPLEX_DERIVED_NAME
            },ReflectUtils.getAllClasses(ComplexDerived.class));
    }

    @Test
    public void fields()
    {
        assertNamesMatch
            (ArrayUtils.EMPTY_STRING_ARRAY,           
             ReflectUtils.getAllFields(Empty.class));
        assertNamesMatch
            (new String[] {
                SIMPLE_FIELD
            },ReflectUtils.getAllFields(Simple.class));
        assertNamesMatch
            (new String[] {
                SIMPLE_FIELD,
                DERIVED_FIELD
            },ReflectUtils.getAllFields(Derived.class));
        assertNamesMatch
            (new String[] {
                IFACE_FIELD
            },ReflectUtils.getAllFields(IFace.class));
        assertNamesMatch
            (new String[] {
                SIMPLE_FIELD,
                IFACE_FIELD,
                COMPLEX_BASE_FIELD
            },ReflectUtils.getAllFields(ComplexBase.class));
        assertNamesMatch
            (new String[] {
                SIMPLE_FIELD,
                IFACE_FIELD,
                COMPLEX_BASE_FIELD,
                COMPLEX_DERIVED_FIELD
            },ReflectUtils.getAllFields(ComplexDerived.class));
    }

    @Test
    public void instance()
        throws Exception
    {
        assertEquals(1,((IntObject)
                        (ReflectUtils.getInstance
                         (IntObject.class.getName(),
                          new Class[] {Integer.TYPE},
                          new Object[] {new Integer(1)}))).getValue());
        try {
            ReflectUtils.getInstance("NonExistent",null,null);
            fail();
        } catch (ClassNotFoundException ex) {
            assertFalse(Thread.interrupted());
        }
        try {
            ReflectUtils.getInstance(IntObject.class.getName(),
                                     new Class[] {Integer.TYPE},
                                     new Object[] {new Integer(0)});
            fail();
        } catch (InvocationTargetException ex) {
            assertTrue(Thread.interrupted());
            assertEquals(I18NInterruptedRuntimeException.class,
                         ex.getCause().getClass());
        }
    }
}
