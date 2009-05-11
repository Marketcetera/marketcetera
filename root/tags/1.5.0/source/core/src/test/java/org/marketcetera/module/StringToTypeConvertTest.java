package org.marketcetera.module;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.test.UnicodeData;
import org.marketcetera.core.LoggerConfiguration;
import org.junit.Test;
import org.junit.BeforeClass;
import static org.junit.Assert.*;

import java.io.File;
import java.net.URL;
import java.math.BigInteger;
import java.math.BigDecimal;
import java.util.Map;
import java.util.List;
import java.util.Date;
import java.util.Properties;

/* $License$ */
/**
 * Tests various aspects of {@link StringToTypeConverter}
 *
 * @author anshul@marketcetera.com
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class StringToTypeConvertTest {
    @BeforeClass
    public static void initLogger() {
        LoggerConfiguration.logSetup();
    }
    /**
     * Tests the {@link StringToTypeConverter#isSupported(String)} and
     *  {@link StringToTypeConverter#isSupported(Class)} API. 
     */
    @Test
    public void isSupported() {
        assertSupported(Boolean.TYPE);
        assertSupported(Boolean.class);
        assertSupported(Byte.TYPE);
        assertSupported(Byte.class);
        assertSupported(Character.TYPE);
        assertSupported(Character.class);
        assertSupported(Short.TYPE);
        assertSupported(Short.class);
        assertSupported(Integer.TYPE);
        assertSupported(Integer.class);
        assertSupported(Float.TYPE);
        assertSupported(Float.class);
        assertSupported(Long.TYPE);
        assertSupported(Long.class);
        assertSupported(Double.TYPE);
        assertSupported(Double.class);

        assertSupported(BigInteger.class);
        assertSupported(BigDecimal.class);

        assertSupported(String.class);
        assertSupported(File.class);
        assertSupported(URL.class);
        assertSupported(Date.class);
        assertSupported(Properties.class);

        assertSupported(ModuleURN.class);
    }

    /**
     * Tests unsupported data types
     */
    @Test
    public void notSupported() {
        assertNotSupported(Map.class);
        assertNotSupported(List.class);
        assertFalse(StringToTypeConverter.isSupported("Not A Class"));
    }

    /**
     * Tests conversion of supported types.
     *
     * @throws Exception if there were unexpected errors
     */
    @Test
    public void convert() throws Exception {
        verifyConversion(Boolean.TYPE, "true", true);
        verifyConversion(Boolean.TYPE, "false", false);

        verifyConversion(Boolean.class, "true", true);
        verifyConversion(Boolean.class, "false", false);

        verifyConversion(Byte.TYPE, String.valueOf(Byte.MAX_VALUE), Byte.MAX_VALUE);
        verifyConversion(Byte.TYPE, String.valueOf(Byte.MIN_VALUE), Byte.MIN_VALUE);

        verifyConversion(Byte.class, String.valueOf(Byte.MAX_VALUE), Byte.MAX_VALUE);
        verifyConversion(Byte.class, String.valueOf(Byte.MIN_VALUE), Byte.MIN_VALUE);

        verifyConversion(Character.TYPE, "A",'A');
        verifyConversion(Character.TYPE, "@",'@');

        verifyConversion(Character.class, " ",' ');
        verifyConversion(Character.class, "\\",'\\');

        verifyConversion(Short.TYPE, String.valueOf(Short.MAX_VALUE), Short.MAX_VALUE);
        verifyConversion(Short.TYPE, String.valueOf(Short.MIN_VALUE), Short.MIN_VALUE);

        verifyConversion(Short.class, String.valueOf(Short.MAX_VALUE), Short.MAX_VALUE);
        verifyConversion(Short.class, String.valueOf(Short.MIN_VALUE), Short.MIN_VALUE);

        verifyConversion(Integer.TYPE,String.valueOf(Integer.MAX_VALUE), Integer.MAX_VALUE);
        verifyConversion(Integer.TYPE,String.valueOf(Integer.MIN_VALUE), Integer.MIN_VALUE);

        verifyConversion(Integer.class,String.valueOf(Integer.MAX_VALUE), Integer.MAX_VALUE);
        verifyConversion(Integer.class,String.valueOf(Integer.MIN_VALUE), Integer.MIN_VALUE);

        verifyConversion(Float.TYPE,String.valueOf(Float.MAX_VALUE), Float.MAX_VALUE);
        verifyConversion(Float.TYPE,String.valueOf(Float.MIN_VALUE), Float.MIN_VALUE);

        verifyConversion(Float.class,String.valueOf(Float.MAX_VALUE), Float.MAX_VALUE);
        verifyConversion(Float.class,String.valueOf(Float.MIN_VALUE), Float.MIN_VALUE);

        verifyConversion(Long.TYPE,String.valueOf(Long.MAX_VALUE), Long.MAX_VALUE);
        verifyConversion(Long.TYPE,String.valueOf(Long.MIN_VALUE), Long.MIN_VALUE);

        verifyConversion(Long.class,String.valueOf(Long.MAX_VALUE), Long.MAX_VALUE);
        verifyConversion(Long.class,String.valueOf(Long.MIN_VALUE), Long.MIN_VALUE);

        verifyConversion(Double.TYPE,String.valueOf(Double.MAX_VALUE), Double.MAX_VALUE);
        verifyConversion(Double.TYPE,String.valueOf(Double.MIN_VALUE), Double.MIN_VALUE);

        verifyConversion(Double.class,String.valueOf(Double.MAX_VALUE), Double.MAX_VALUE);
        verifyConversion(Double.class,String.valueOf(Double.MIN_VALUE), Double.MIN_VALUE);

        verifyConversion(BigInteger.class, String.valueOf(Long.MAX_VALUE),
                new BigInteger(String.valueOf(Long.MAX_VALUE)));
        verifyConversion(BigInteger.class, String.valueOf(Long.MIN_VALUE),
                new BigInteger(String.valueOf(Long.MIN_VALUE)));

        verifyConversion(BigDecimal.class, String.valueOf(Double.MAX_VALUE),
                new BigDecimal(String.valueOf(Double.MAX_VALUE)));
        verifyConversion(BigDecimal.class, String.valueOf(Double.MIN_VALUE),
                new BigDecimal(String.valueOf(Double.MIN_VALUE)));

        verifyConversion(String.class, UnicodeData.COMBO, UnicodeData.COMBO);

        verifyConversion(File.class, "/testfile", new File("/testfile"));

        verifyConversion(URL.class, "http://testfile", new URL("http://testfile"));

        verifyConversion(ModuleURN.class, "metc:blah:blue:green",
                new ModuleURN("metc:blah:blue:green"));
        
        Properties expected = new Properties();
        expected.put("one", "to");
        expected.put("three", "flour");
        verifyConversion(Properties.class, "one=to:three=flour", expected);
        verifyConversion(Properties.class, "", null);
        verifyConversion(Properties.class, "   ", new Properties());
        verifyConversion(Properties.class, "abc", new Properties());
        verifyConversion(Properties.class, "abc:pqr", new Properties());
    }

    /**
     * Tests conversion failures for known data types.
     *
     * @throws Exception if there were unexpected failures.
     */
    @Test
    public void convertFail() throws Exception {
        verifyConvertFail(Boolean.TYPE, "what");
        verifyConvertFail(Boolean.TYPE, null);
        verifyConvertFail(Boolean.class, "what");
        verifyConvertFail(Boolean.class, null);

        verifyConvertFail(Byte.TYPE, "1234");
        verifyConvertFail(Byte.TYPE, null);
        verifyConvertFail(Byte.class, "1234");
        verifyConvertFail(Byte.class, null);

        verifyConvertFail(Short.TYPE,"42345");
        verifyConvertFail(Short.TYPE,null);
        verifyConvertFail(Short.class,"32768");
        verifyConvertFail(Short.class,null);

        verifyConvertFail(Integer.TYPE, "3000000000");
        verifyConvertFail(Integer.TYPE, null);
        verifyConvertFail(Integer.class, "3000000000");
        verifyConvertFail(Integer.class, null);

        verifyConvertFail(Float.TYPE, "123.3213u");
        verifyConvertFail(Float.TYPE, null);
        verifyConvertFail(Float.class, "123.3213u");
        verifyConvertFail(Float.class, null);

        verifyConvertFail(Long.TYPE, "999999999999999999999999");
        verifyConvertFail(Long.TYPE, null);
        verifyConvertFail(Long.class, "999999999999999999999999");
        verifyConvertFail(Long.class, null);

        verifyConvertFail(Double.TYPE, "blah");
        verifyConvertFail(Double.TYPE, null);
        verifyConvertFail(Double.class, "gah");
        verifyConvertFail(Double.class, null);

        verifyConvertFail(BigInteger.class, "123123.231411");
        verifyConvertFail(BigInteger.class, null);
        verifyConvertFail(BigDecimal.class, "this is not a number");
        verifyConvertFail(BigDecimal.class, null);

        verifyConvertFail(URL.class, "udipi://mission");
        verifyConvertFail(URL.class, null);

        verifyConvertFail(ModuleURN.class, "");
        verifyConvertFail(ModuleURN.class, null);

        verifyConvertFail(Properties.class, null);

        //Invalid type
        assertTrue(new ExpectedFailure<IllegalArgumentException>(null){
            protected void run() throws Exception {
                StringToTypeConverter.convert("Not a Class", "Don't Matter");
            }
        }.getException().getCause() instanceof ClassNotFoundException);
    }

    /**
     * Verifies failure to convert a string value to the supplied java type.
     *
     * @param inType the target type.
     * @param inValue the string value.
     *
     * @throws Exception if there were unexpected errors.
     */
    private static void verifyConvertFail(final Class inType,
                                          final String inValue)
            throws Exception {
        new ExpectedFailure<IllegalArgumentException>(
                Messages.STRING_CONVERSION_ERROR.getText(inValue,
                        inType.getName(),""), false){
            protected void run() throws Exception {
                StringToTypeConverter.convert(inType, inValue);
            }
        };
        new ExpectedFailure<IllegalArgumentException>(
                Messages.STRING_CONVERSION_ERROR.getText(inValue,
                        inType.getName(),""), false){
            protected void run() throws Exception {
                StringToTypeConverter.convert(inType.getName(), inValue);
            }
        };
    }

    /**
     * Verifies conversion of specified string value to the supplied java
     * type.
     * @param inType the target type.
     * @param inValue the string value.
     * @param inExpected the expected value.
     */
    private static void verifyConversion(Class inType,
                                         String inValue,
                                         Object inExpected) {
        assertEquals(inExpected, StringToTypeConverter.convert(
                inType, inValue));
        assertEquals(inExpected, StringToTypeConverter.convert(
                inType.getName(), inValue));
    }

    /**
     * Verifies if conversion to the specified java type is supported.
     *
     * @param inType if the specified java type is supported
     */
    private static void assertSupported(Class inType) {
        assertTrue(StringToTypeConverter.isSupported(inType));
        assertTrue(StringToTypeConverter.isSupported(inType.getName()));
    }
    /**
     * Verifies if conversion to the specified java type is not supported.
     *
     * @param inType if the specified java type is not supported
     */
    private static void assertNotSupported(Class inType) {
        assertFalse(StringToTypeConverter.isSupported(inType));
        assertFalse(StringToTypeConverter.isSupported(inType.getName()));
    }
}
