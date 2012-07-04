package org.marketcetera.core.quickfix;

import org.marketcetera.core.attributes.ClassVersion;
import org.marketcetera.util.log.SLF4JLoggerProxy;

/* $License$ */
/**
 * Abstraction for keeping track of the current fix version for an
 * application.
 * <p>
 * This abstraction is has been created to help move away from the
 * notion of having a current FIX version for all applications. This
 * will be carried out in two steps.
 * <p>
 * First step is to switch the current version for all use-cases/tests
 * to the system FIX version 0.0.
 * <p>
 * Second step is to move away all the applications from using
 * FIX messages and have them use the FIX agnostic API at which point
 * the applications will not need to have a notion of current FIX version.
 * When that happens this class can be dispensed or may still be retained
 * as a unit testing class because many unit tests may still depend on it. 
 *
 * @author anshul@marketcetera.com
 * @version $Id: CurrentFIXDataDictionary.java 16063 2012-01-31 18:21:55Z colin $
 * @since 1.0.0
 */
@ClassVersion("$Id: CurrentFIXDataDictionary.java 16063 2012-01-31 18:21:55Z colin $") //$NON-NLS-1$
public class CurrentFIXDataDictionary {
    /**
     * Gets the current FIX Data dictionary instance.
     *
     * @return the current FIX Data dictionary instance.
     */
    public static FIXDataDictionary getCurrentFIXDataDictionary() {
        return sCurrent;
    }

    /**
     * Sets the current data dictionary instance.
     *
     * @param inFDD the current data dictionary instance.
     */
    public static void setCurrentFIXDataDictionary(FIXDataDictionary inFDD) {
        SLF4JLoggerProxy.debug(LOGGER_NAME,
                "Setting the FIX dd to {}", //$NON-NLS-1$
                inFDD.getDictionary().getVersion());

        sCurrent = inFDD;
    }

    private volatile static FIXDataDictionary sCurrent;
    private static final String LOGGER_NAME =
            CurrentFIXDataDictionary.class.getName();
}
