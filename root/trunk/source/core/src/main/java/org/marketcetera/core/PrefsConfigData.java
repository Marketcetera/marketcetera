package org.marketcetera.core;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import java.math.BigDecimal;

/** Impelement the ConfigData conversion for objects stored using
 * Java's {@link Preferences} classes.
 * 
 */
@ClassVersion("$Id$")
public class PrefsConfigData implements ConfigData {
    Preferences preferencesNode;

    public PrefsConfigData(Preferences backing)
    {
        preferencesNode = backing;
    }

    /* (non-Javadoc)
      * @see org.marketcetera.core.TempIF#get(java.lang.String, java.lang.String)
      */
    public String get(String arg0, String arg1) {
        return preferencesNode.get(arg0, arg1);
    }

    /* (non-Javadoc)
      * @see org.marketcetera.core.TempIF#getBoolean(java.lang.String, boolean)
      */
    public boolean getBoolean(String arg0, boolean arg1) {
        return preferencesNode.getBoolean(arg0, arg1);
    }

    /* (non-Javadoc)
      * @see org.marketcetera.core.TempIF#getByteArray(java.lang.String, byte[])
      */
    public byte[] getByteArray(String arg0, byte[] arg1) {
        return preferencesNode.getByteArray(arg0, arg1);
    }

    /* (non-Javadoc)
      * @see org.marketcetera.core.TempIF#getDouble(java.lang.String, double)
      */
    public double getDouble(String arg0, double arg1) {
        return preferencesNode.getDouble(arg0, arg1);
    }

    /* (non-Javadoc)
      * @see org.marketcetera.core.TempIF#getFloat(java.lang.String, float)
      */
    public float getFloat(String arg0, float arg1) {
        return preferencesNode.getFloat(arg0, arg1);
    }

    /* (non-Javadoc)
      * @see org.marketcetera.core.TempIF#getInt(java.lang.String, int)
      */
    public int getInt(String arg0, int arg1) {
        return preferencesNode.getInt(arg0, arg1);
    }

    /* (non-Javadoc)
      * @see org.marketcetera.core.TempIF#getLong(java.lang.String, long)
      */
    public long getLong(String arg0, long arg1) {
        return preferencesNode.getLong(arg0, arg1);
    }

    public BigDecimal getBigDecimal(String key, BigDecimal def) {
        BigDecimal val = def;
        try {
            val = new BigDecimal(preferencesNode.get(key, def.toString()));
        } catch (NumberFormatException ex) {
        }
        return val;
    }

    /* (non-Javadoc)
      * @see org.marketcetera.core.TempIF#keys()
      */
    public String[] keys() throws BackingStoreException {
        return preferencesNode.keys();
    }

    public Preferences getPreferencesNode() {
        return preferencesNode;
    }


}
