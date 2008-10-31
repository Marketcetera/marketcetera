package org.marketcetera.quickfix;

import org.marketcetera.core.ClassVersion;

import java.util.HashMap;
import java.util.Map;

/**
 * Singleton class that is a mapping of all {@link FIXVersion} to
 * their corresponding {@link quickfix.DataDictionary} classes.
 *
 * @author toli
 * @author anshul@marketcetera.com
 * 
 * @version $Id$
 */

@ClassVersion("$Id$") //$NON-NLS-1$
public class FIXDataDictionaryManager {

    /**
     * Initializes the FIX data dictionary and returns it.
     * <p>
     * Note that this method is used for spring configuration
     *
     * @param version The FIX Version.
     * @param location the location of the dictionary.
     *
     * @return the data dictionary instance.
     *
     * @throws FIXFieldConverterNotAvailable if there were errors.
     */
    public static FIXDataDictionary initialize(FIXVersion version,
                                               String location)
            throws FIXFieldConverterNotAvailable {
        FIXDataDictionary fixDD = new FIXDataDictionary(location);
        synchronized (LOCK) {
            fddMap.put(version, fixDD);
        }
        return fixDD;
    }

    /**
     * Initializes the manager with the supplied table of FIX Versions
     * and the URLs to their data dictionaries.
     *
     * @param urlMap The map with FIX Versions as keys and URLs to their
     * data dictionaries as values.
     *
     * @throws FIXFieldConverterNotAvailable if there were errors.
     */
    public static void initialize(Map<FIXVersion, String> urlMap)
            throws FIXFieldConverterNotAvailable {
        synchronized (LOCK) {
            for (FIXVersion version : urlMap.keySet()) {
                fddMap.put(version, new FIXDataDictionary(urlMap.get(version)));
            }
        }
    }

    /**
     * Returns the data dictionary instance given the FIX Version.
     *
     * @param version The FIX Version value.
     *
     * @return the data dictionary instance for the supplied FIX version.
     * Null, if no data dictionary instance is available. 
     */
    public static FIXDataDictionary getFIXDataDictionary(FIXVersion version) {
        synchronized (LOCK) {
            return fddMap.get(version);
        }
    }

    private FIXDataDictionaryManager() {
        //A utility class. No instances should be created.
    }

    private static final HashMap<FIXVersion, FIXDataDictionary> fddMap =
            new HashMap<FIXVersion, FIXDataDictionary>();
    private static final Object LOCK = new Object();
}
