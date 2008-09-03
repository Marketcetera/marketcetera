package org.marketcetera.quickfix;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.util.log.SLF4JLoggerProxy;

import java.util.HashMap;
import java.util.Map;

/**
 * Singleton class that is a mapping of all {@link FIXVersion} to their corresponding
 * {@link quickfix.DataDictionary} classes.
 * This may need to be rewritten once we support multiple FIX versions at once
 *
 * @author toli
 * @version $Id$
 */

@ClassVersion("$Id$") //$NON-NLS-1$
public class FIXDataDictionaryManager {
    private static final String LOGGER_CATEGORY = FIXDataDictionaryManager.class.getName();
    private static HashMap<FIXVersion, FIXDataDictionary> fddMap = new HashMap<FIXVersion, FIXDataDictionary>();
    private static FIXDataDictionary sCurrent;

    public static void initialize(FIXVersion version, String location) throws FIXFieldConverterNotAvailable {
        /*waste result*/ new FIXDataDictionaryManager(version, location);
    }

    public static void initialize(FIXVersion version, FIXDataDictionary fixDD) throws FIXFieldConverterNotAvailable {
        fddMap.put(version, fixDD);
        setCurrentFIXDataDictionary(fixDD);
    }

    /** need this for Spring contructor */
    public FIXDataDictionaryManager(FIXVersion version, String location) throws FIXFieldConverterNotAvailable {
        FIXDataDictionary fixDD = new FIXDataDictionary(location);
        fddMap.put(version, fixDD);
        setCurrentFIXDataDictionary(fixDD);
    }

    public FIXDataDictionaryManager(Map<FIXVersion, String> urlMap) throws FIXFieldConverterNotAvailable {
        fddMap = new HashMap<FIXVersion, FIXDataDictionary>();
        for (FIXVersion version : urlMap.keySet()) {
            fddMap.put(version, new FIXDataDictionary(urlMap.get(version)));
        }
    }

    public static FIXDataDictionary getFIXDataDictionary(FIXVersion version) {
        return fddMap.get(version);
    }

    private static void setCurrentFIXDataDictionary(FIXDataDictionary inFDD) {
        SLF4JLoggerProxy.debug(LOGGER_CATEGORY, "Setting the FIX dd to {}", inFDD.getDictionary().getVersion()); //$NON-NLS-1$
        
        sCurrent = inFDD;
    }
    public static void setCurrentFIXDataDictionary(FIXVersion inVersion) {
        sCurrent = getFIXDataDictionary(inVersion);
    }

    public static FIXDataDictionary getCurrentFIXDataDictionary() {
        return sCurrent;
    }
}
