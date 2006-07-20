package org.marketcetera.core;

import java.util.Properties;
import java.math.BigDecimal;

/**
 * Implementation of the {@link ConfigData} interface using the file-based config
 * loaded into a {@link java.util.Properties} structure
 *
 * Most of the implementation for the underlying functions have been copied from
 * {@link java.util.prefs.AbstractPreferences}
 * 
 * @author Toli Kuznets
 * @version $Id$
 */
@ClassVersion("$Id$")
public class PropertiesConfigData implements ConfigData
{
    private Properties mProps;

    public PropertiesConfigData(Properties inPrefs)
    {
        mProps = inPrefs;
    }


    public String get(String key, String def)
    {
        Object result = mProps.get(key);
        if(result == null) {
            return def;
        } else {
            return result.toString();
        }
    }

    /** Need to overrie the default implementation since the {@link #get} function above
     * will return the bytes[].toString() back which is not what we want
     * (it'll actually be a java pointer instead).
     * So here we excplicitly check if the value coming back is a byte[] and treat it differently.
     */
    public byte[] getByteArray(String key, byte[] def) {
        byte[] result = def;
        Object value = mProps.get(key);
        try {
            if (value != null) {
                if(value instanceof byte[]) {
                    result = (byte[]) value;
                } else {
                    result = value.toString().getBytes();
                }
            }
        }
        catch (RuntimeException e) {
        }

        return result;
    }

    public String[] keys()
    {
        return (String[]) mProps.keySet().toArray(new String[0]);
    }

    public Properties getProperties() { return mProps; }


    public int getInt(String key, int def) {
        int result = def;
        try {
            String value = get(key, null);
            if (value != null)
                result = Integer.parseInt(value);
        } catch (NumberFormatException e) {
        }

        return result;
    }

    public long getLong(String key, long def) {
        long result = def;
        try {
            String value = get(key, null);
            if (value != null)
                result = Long.parseLong(value);
        } catch (NumberFormatException e) {
        }

        return result;
    }

    public boolean getBoolean(String key, boolean def) {
        boolean result = def;
        String value = get(key, null);
        if (value != null) {
            if (value.equalsIgnoreCase("true"))
                result = true;
            else if (value.equalsIgnoreCase("false"))
                result = false;
        }

        return result;
    }

    public float getFloat(String key, float def) {
        float result = def;
        try {
            String value = get(key, null);
            if (value != null)
                result = Float.parseFloat(value);
        } catch (NumberFormatException e) {
        }

        return result;
    }

    public double getDouble(String key, double def) {
        double result = def;
        try {
            String value = get(key, null);
            if (value != null)
                result = Double.parseDouble(value);
        } catch (NumberFormatException e) {
        }

        return result;
    }

    public BigDecimal getBigDecimal(String key, BigDecimal def) {
        BigDecimal result = def;
        try {
            String value = get(key, null);
            if (value != null)
                result = new BigDecimal(value);
        } catch (NumberFormatException e) {
        }
        return result;
    }
}
