/**
 * 
 */
package org.marketcetera.photon;

import java.math.BigDecimal;
import java.util.prefs.BackingStoreException;

import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.ConfigData;


/**
 * An implementation of the ConfigData contract that uses the Eclipse preference
 * store as a backing store.
 * 
 * @author gmiller
 * 
 */
@ClassVersion("$Id$")
public class EclipseConfigData implements ConfigData {
    ScopedPreferenceStore mStore;
    /**
     * Creates a new EclipseConfigData with the specified store
     * object as the backing store.
     * @param store the backing store
     */
    public EclipseConfigData(ScopedPreferenceStore store){
        mStore = store;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.ConfigData#get(java.lang.String, java.lang.String)
     */
    public String get(String key, String pDefault) {
        if (mStore.contains(key))
            return mStore.getString(key);
        else
            return pDefault;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.ConfigData#getBoolean(java.lang.String, boolean)
     */
    public boolean getBoolean(String key, boolean pDefault) {
        if (mStore.contains(key))
            return mStore.getBoolean(key);
        else
            return pDefault;
    }

    /* (non-Javadoc)
     * @see org.marketcetera.core.ConfigData#getByteArray(java.lang.String, byte[])
     */
    public byte[] getByteArray(String key, byte[] pDefault) {
        if (mStore.contains(key))
            return mStore.getString(key).getBytes();
        else
            return pDefault;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.ConfigData#getDouble(java.lang.String, double)
     */
    public double getDouble(String key, double pDefault) {
        if (mStore.contains(key))
            return mStore.getDouble(key);
        else
            return pDefault;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.ConfigData#getFloat(java.lang.String, float)
     */
    public float getFloat(String key, float pDefault) {
        if (mStore.contains(key))
            return mStore.getFloat(key);
        else
            return pDefault;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.ConfigData#getInt(java.lang.String, int)
     */
    public int getInt(String key, int pDefault) {
        if (mStore.contains(key))
            return mStore.getInt(key);
        else
            return pDefault;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.ConfigData#getLong(java.lang.String, long)
     */
    public long getLong(String key, long pDefault) {
        if (mStore.contains(key))
            return mStore.getLong(key);
        else
            return pDefault;
    }
    
    /* (non-Javadoc)
     * @see org.marketcetera.core.ConfigData#keys()
     */
    public String[] keys() throws BackingStoreException {
        return new String[0];
    }
    
	/* (non-Javadoc)
	 * @see org.marketcetera.core.ConfigData#getBigDecimal(java.lang.String, java.math.BigDecimal)
	 */
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