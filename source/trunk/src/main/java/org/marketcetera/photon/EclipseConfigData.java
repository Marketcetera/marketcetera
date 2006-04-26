/**
 * 
 */
package org.marketcetera.photon;

import java.math.BigDecimal;
import java.util.prefs.BackingStoreException;

import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.ConfigData;

@ClassVersion("$Id$")
public class EclipseConfigData implements ConfigData {
    ScopedPreferenceStore mStore;
    public EclipseConfigData(ScopedPreferenceStore store){
        mStore = store;
    }
    public String get(String key, String pDefault) {
        if (mStore.contains(key))
            return mStore.getString(key);
        else
            return pDefault;
    }
    public boolean getBoolean(String key, boolean pDefault) {
        if (mStore.contains(key))
            return mStore.getBoolean(key);
        else
            return pDefault;
    }

    public byte[] getByteArray(String key, byte[] pDefault) {
        if (mStore.contains(key))
            return mStore.getString(key).getBytes();
        else
            return pDefault;
    }
    public double getDouble(String key, double pDefault) {
        if (mStore.contains(key))
            return mStore.getDouble(key);
        else
            return pDefault;
    }
    public float getFloat(String key, float pDefault) {
        if (mStore.contains(key))
            return mStore.getFloat(key);
        else
            return pDefault;
    }
    public int getInt(String key, int pDefault) {
        if (mStore.contains(key))
            return mStore.getInt(key);
        else
            return pDefault;
    }
    public long getLong(String key, long pDefault) {
        if (mStore.contains(key))
            return mStore.getLong(key);
        else
            return pDefault;
    }
    
    public String[] keys() throws BackingStoreException {
        return new String[0];
    }
    
	public BigDecimal getBigDecimal(String key, BigDecimal pDefault) {
        if (mStore.contains(key))
        	try {
        		return new BigDecimal(mStore.getString(key));
        	} catch (NumberFormatException ex) {
        		return pDefault;
        	}
        else
            return pDefault;
	}

}