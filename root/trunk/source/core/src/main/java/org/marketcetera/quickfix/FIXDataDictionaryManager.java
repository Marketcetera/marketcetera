package org.marketcetera.quickfix;

import org.marketcetera.core.ClassVersion;

import java.util.HashMap;

/**
 * Singleton class that is a mapping of all {@link FIXVersion} to their corresponding
 * {@link quickfix.DataDictionary} classes.
 * This may need to be rewritten once we support multiple FIX versions at once
 *
 * @author toli
 * @version $Id$
 */

@ClassVersion("$Id$")
public class FIXDataDictionaryManager {
    private static HashMap<FIXVersion, FIXDataDictionary> fddMap = new HashMap<FIXVersion, FIXDataDictionary>();
    private static FIXDataDictionary sCurrent;

    public FIXDataDictionaryManager(HashMap<FIXVersion, String> urlMap) throws FIXFieldConverterNotAvailable {
        fddMap = new HashMap<FIXVersion, FIXDataDictionary>();
        for (FIXVersion version : urlMap.keySet()) {
            fddMap.put(version, new FIXDataDictionary(urlMap.get(version)));
        }
    }

    public static FIXDataDictionary getFIXDatDictionary(FIXVersion version) {
        return fddMap.get(version);
    }

    public static void setCurrentFIXDataDictionary(FIXDataDictionary inFDD) {
        sCurrent = inFDD;
    }
    public static void setCurrentFIXDataDictionary(FIXVersion inVersion) {
        sCurrent = getFIXDatDictionary(inVersion);
    }

    public static FIXDataDictionary getCurrentFixDataDictionary() {
        return sCurrent;
    }
}
