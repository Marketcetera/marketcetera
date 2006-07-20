package org.marketcetera.core;

import java.util.prefs.BackingStoreException;
import java.math.BigDecimal;

/** Wrapper interface on top of various implementations of storing configuration data
 * The options are to use Java {@link java.util.prefs.Preferences} or a file-based
 * implementation.
 * None of the accessor functions should throw exceptions since we provide defauts for
 * all the getter methods. Defaults will be used in case the data cannot be parsed accordingly. 
 */

@ClassVersion("$Id$")
public interface ConfigData {


    /**
     * Returns the value associated with the specified key in this preference node.
     */
    public abstract String get(String arg0, String arg1);

    /**
     * Returns the boolean value represented by the string associated with the specified key in this preference node.
     */
    public abstract boolean getBoolean(String arg0, boolean arg1);

    /**
     * Returns the byte array value represented by the string associated with the specified key in this preference node.
     */
    public abstract byte[] getByteArray(String arg0, byte[] arg1);


    /**
     * Returns the double value represented by the string associated with the specified key in this preference node. 
     */
    public abstract double getDouble(String arg0, double arg1);


    /**
     * Returns the float value represented by the string associated with the specified key in this preference node. 
     */
    public abstract float getFloat(String arg0, float arg1);

    /**
     * Returns the float value represented by the string associated with the specified key in this preference node.
     */
    public abstract BigDecimal getBigDecimal(String arg0, BigDecimal arg1);

    /**
     * Returns the int value represented by the string associated with the specified key in this preference node.
     */
    public abstract int getInt(String arg0, int arg1);


    /**
     * Returns the long value represented by the string associated with the specified key in this preference node.
     */
    public abstract long getLong(String arg0, long arg1);

    public abstract String[] keys() throws BackingStoreException;
}
