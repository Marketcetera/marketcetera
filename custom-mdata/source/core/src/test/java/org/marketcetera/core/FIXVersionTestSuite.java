package org.marketcetera.core;

import junit.framework.Assert;
import junit.framework.Test;
import org.marketcetera.quickfix.FIXVersion;
import org.marketcetera.quickfix.FIXDataDictionaryManager;
import org.marketcetera.quickfix.FIXFieldConverterNotAvailable;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;
import java.util.HashMap;

/**
 * Extends the {@link MarketceteraTestSuite} to run the unit test
 * through the set of all specified FIX versions as well
 *
 * You can specify an option set of "exception" methods that are only to be run
 * with a different set of versions. This is useful when some test cases are applicable
 * only under certain but not all versions.
 *
 * @author toli
 * @version $Id$
 */

@ClassVersion("$Id$") //$NON-NLS-1$
public class FIXVersionTestSuite extends MarketceteraTestSuite {
    /**
     * All versions of FIX, including the "System" FIX version used
     * for FIX Agnostic messages.
     */
    public static final FIXVersion[] ALL_VERSIONS =
            new FIXVersion[]{FIXVersion.FIX40, FIXVersion.FIX41,
                    FIXVersion.FIX42, FIXVersion.FIX43,
                    FIXVersion.FIX44, FIXVersion.FIX_SYSTEM};
    /**
     * All versions of FIX, excluding the "System" FIX Version.
     */
    public static final FIXVersion[] ALL_FIX_VERSIONS =
            new FIXVersion[]{FIXVersion.FIX40, FIXVersion.FIX41,
                    FIXVersion.FIX42, FIXVersion.FIX43,
                    FIXVersion.FIX44};
    public static final FIXVersion[] FIX42_PLUS_VERSIONS =
            new FIXVersion[]{FIXVersion.FIX42, FIXVersion.FIX43,
                    FIXVersion.FIX44, FIXVersion.FIX_SYSTEM};
	private String suiteName;

    public FIXVersionTestSuite() {
    }

    public FIXVersionTestSuite(Class aClass, FIXVersion[] inVersions) {
        super();
        addTestForEachVersion(aClass, inVersions, new HashSet<String>(), new FIXVersion[0]);
        suiteName = aClass.getName();
    }

    public FIXVersionTestSuite(Class aClass, FIXVersion[] inVersions,
                               Set<String> exceptionMethods, FIXVersion[] exceptionVersions) {
        super();
        addTestForEachVersion(aClass, inVersions, exceptionMethods, exceptionVersions);
        suiteName = aClass.getName();
    }
    
    /** Class to introspect, and the set of versions to apply to all tests in that class
     * Can also have a set of excpetions and a subset of versions to apply to the exceptions
     * The exceptions should be used for when you have testXXX methods that are only applicable
     * to a subset of FIX versions, such as MARKET_DATA_REQUESTs
     */
    private void addTestForEachVersion(Class aClass, FIXVersion[] inVersions, Set<String> exceptionMethods, FIXVersion[] exceptionVersions) {
        String[] testNames = getTestNames(aClass);

        for (String name : testNames) {
            try {
                Constructor constructor = aClass.getConstructor(String.class, FIXVersion.class);
                if (exceptionMethods.contains(name)) {
                    addTestWithVersion(constructor, name, exceptionVersions);
                } else {
                    addTestWithVersion(constructor, name, inVersions);
                }
            } catch (Exception ex) {
                Assert.fail("Creation of test suite LOGIN_FAILED: " + ex.getMessage()); //$NON-NLS-1$
            }
        }
    }

    public static void initializeFIXDataDictionaryManager(FIXVersion[] inVersions)
            throws FIXFieldConverterNotAvailable
    {
        HashMap<FIXVersion, String> map = new HashMap<FIXVersion, String>();
        for (FIXVersion version : inVersions) {
            map.put(version, version.getDataDictionaryURL());
        }
        FIXDataDictionaryManager.initialize(map);
    }

    private void addTestWithVersion(Constructor cons, String testName, FIXVersion[] versions) throws Exception {
        for (FIXVersion version : versions) {
            addTest((Test) cons.newInstance(testName, version));
        }
    }

    private String[] getTestNames(Class inClass)
    {
        Vector<String> testNames = new Vector<String>();
        Method[] methods= inClass.getDeclaredMethods();
        for (Method method : methods) {
            if(method.getName().startsWith("test")) { //$NON-NLS-1$
                testNames.add(method.getName());
            }
        }
        return testNames.toArray(new String[testNames.size()]);
    }

    @Override
    public String getName() {
    	return suiteName == null ? super.getName() : suiteName;
    }
}
