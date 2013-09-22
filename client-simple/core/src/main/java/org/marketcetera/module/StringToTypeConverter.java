package org.marketcetera.module;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.log.I18NBoundMessage1P;
import org.marketcetera.core.Util;
import org.apache.commons.beanutils.ConvertUtilsBean;
import org.apache.commons.beanutils.Converter;
import org.apache.commons.beanutils.ConversionException;

import java.util.Hashtable;
import java.util.Properties;

/* $License$ */
/**
 * A Utility class to convert strings to object types.
 * Following types of instances can be converted to from strings
 * <ul>
 * <li>Java Primtive Types</li>
 * <li>BigDecimal</li>
 * <li>BigInteger</li>
 * <li>Strings</li>
 * <li>File</li>
 * <li>URL</li>
 * <li>ModuleURN</li>
 * </ul>
 *
 * This class is a thin wrapper around
 * <a href="http://commons.apache.org/beanutils/apidocs/org/apache/commons/beanutils/ConvertUtilsBean.html">
 * ConvertUtilsBean</a>
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$")  //$NON-NLS-1$
class StringToTypeConverter {
    /**
     * Returns true if conversion from the supplied type
     * name is supported. The type name is either the name
     * of the class or the name of one of the primitive types.
     *
     * If the supplied type name is not a valid java type, or
     * if the class corresponding to the supplied java type
     * cannot be loaded, a false value is returned.
     *
     * @param inTypeName the type name to check
     *
     * @return true if the converter supports conversion of strings
     * to instances of the supplied type.
     */
    public static boolean isSupported(String inTypeName) {
        try {
            return isSupported(toClass(inTypeName));
        } catch (IllegalArgumentException ignored) {
        }
        return false;
    }
    /**
     * Returns true if conversion from the supplied type
     * is supported. The type name is either the name
     * of the class or the name of one of the primitive types.
     *
     * @param inType the type to check
     *
     * @return true if the converter supports conversion of strings
     * to instances of the supplied type.
     */
    public static boolean isSupported(Class inType) {
        return sConverter.lookup(inType) != null;
    }

    /**
     * Converts the supplied string value to the instance of the
     * specified type.
     *
     * Calls to this method should only be made if {@link #isSupported(String)}
     * returns true for the <code>inTypeName</code>, otherwise this method
     * will throw an <code>IllegalArgumentException</code>.
     *
     * @param inTypeName the type of instance to return
     * @param inValue the string value to convert to instance
     *
     * @return the instance converted from the string.
     *
     * @throws IllegalArgumentException if the supplied type name is
     * not supported OR if there were errors converting
     * the string to a numeric type.
     */
    public static Object convert(String inTypeName, String inValue)
            throws IllegalArgumentException {
        return convert(toClass(inTypeName), inValue);
    }

    /**
     * Converts the supplied string value to the instance of the
     * specified type.
     *
     * Calls to this method should only be made if {@link #isSupported(String)}
     * returns true for the <code>inTypeName</code>, otherwise this method
     * will throw an <code>IllegalArgumentException</code>.
     *
     * @param inType the type of instance to return
     * @param inValue the string value to convert to instance
     *
     * @return the instance converted from the string.
     *
     * @throws IllegalArgumentException if the supplied type name is
     * not supported OR if there were errors converting
     * the string to a numeric type.
     */
    public static Object convert(Class inType, String inValue)
            throws IllegalArgumentException {
        try {
            return sConverter.convert(inValue, inType);
        } catch (ConversionException e) {
            throw new IllegalArgumentException(
                    Messages.STRING_CONVERSION_ERROR.getText(inValue,
                            inType.getName(),e.getMessage()));
        }
    }

    /**
     * Converts the supplied type name to class instance. This method
     * ensures that the correct class objects are returned for primitive
     * types.
     *
     * @param inTypeName the type name
     *
     * @return the class object corresponding to the type name.
     */
    private static Class<?> toClass(String inTypeName) {
        if(sPrimitiveTypeMap.containsKey(inTypeName)) {
            return sPrimitiveTypeMap.get(inTypeName);
        }
        try {
            return Class.forName(inTypeName);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException(inTypeName,e);
        }
    }

    /**
     * This method is used during class initialization to initialize
     * the table for mapping primitive type names to their class instances.
     *
     * @param inClass the primitive type class to register. 
     */
    private static void registerPrimitive(Class inClass) {
        sPrimitiveTypeMap.put(inClass.getName(), inClass);
    }

    private static final ConvertUtilsBean sConverter = new ConvertUtilsBean();
    private static final  Hashtable<String,Class> sPrimitiveTypeMap =
            new Hashtable<String, Class>();

    /**
     * A converter class for converting string to module URN.
     */
    private static final class ModuleURNConverter implements Converter {
        public Object convert(Class inClass, Object o) {
            if(ModuleURN.class.equals(inClass)) {
                try {
                    if(o instanceof ModuleURN) {
                        return o;
                    } else if(o instanceof String) {
                        return new ModuleURN((String)o);
                    } else if(o != null) {
                        return new ModuleURN(o.toString());
                    }
                } catch (IllegalArgumentException e) {
                    throw new ConversionException(new I18NBoundMessage1P(
                            Messages.CANNOT_CONVERT_TO_MODULE_URN,
                            o.toString()).getText(),e);
                }
            }
            throw new ConversionException(new I18NBoundMessage1P(
                    Messages.CANNOT_CONVERT_TO_MODULE_URN,
                    o == null
                            ? null
                            : o.toString()).getText());
        }
    }
    private static final class PropertiesConverter implements Converter {
        public Object convert(Class inClass, Object o) {
            if(Properties.class.equals(inClass)) {
                if(o instanceof Properties) {
                    return o;
                } else if(o instanceof String) {
                    return Util.propertiesFromString((String)o);
                }
            }
            throw new ConversionException(new I18NBoundMessage1P(
                    Messages.CANNOT_CONVERT_TO_PROPERTIES,
                    o == null
                            ? null
                            : o.toString()).getText());
        }
    }
    static {
        //Reset the converter to throw exceptions for conversion errors
        sConverter.register(true, true, 0);
        //register the module URN converter
        sConverter.register(new ModuleURNConverter(), ModuleURN.class);
        //register the Properties converter
        sConverter.register(new PropertiesConverter(), Properties.class);
        //initialize the table convert primitive type names to their
        //respective class instances.
        registerPrimitive(Boolean.TYPE);
        registerPrimitive(Byte.TYPE);
        registerPrimitive(Character.TYPE);
        registerPrimitive(Short.TYPE);
        registerPrimitive(Integer.TYPE);
        registerPrimitive(Float.TYPE);
        registerPrimitive(Long.TYPE);
        registerPrimitive(Double.TYPE);
    }
}
