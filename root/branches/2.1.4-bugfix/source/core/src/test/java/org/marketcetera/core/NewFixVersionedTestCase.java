package org.marketcetera.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.marketcetera.quickfix.*;
import org.marketcetera.util.log.SLF4JLoggerProxy;

/* $License$ */

/**
 * Provides a test base that exercises test conditions in all FIX versions.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@RunWith(Parameterized.class)
public abstract class NewFixVersionedTestCase
{
    /**
     * Run once before all tests.
     *
     * @throws Exception if an unexpected error occurs
     */
    @BeforeClass
    public static void once()
            throws Exception
    {
        LoggerConfiguration.logSetup();
    }
    /**
     * Generates the parameters necessary to run test conditions in all FIX versions.
     *
     * @return a <code>Collection&lt;Object[]&gt;</code> value
     */
    @Parameters
    public static Collection<Object[]> generateParamters()
    {
        List<Object[]> parameters = new ArrayList<Object[]>();
        for(FIXVersion version : FIXVersion.values()) {
            parameters.add(new Object[] { version } );
        }
        return parameters;
    }
    /**
     * Runs once before each test. 
     *
     * @throws Exception if an unexpected error occurs
     */
    @Before
    public void setup()
            throws Exception
    {
        msgFactory = fixVersion.getMessageFactory();
        fixDD = FIXDataDictionaryManager.getFIXDataDictionary(fixVersion);
        if(fixDD == null) {
            FIXDataDictionaryManager.initialize(fixVersion,
                                                fixVersion.getDataDictionaryURL());
            fixDD = FIXDataDictionaryManager.getFIXDataDictionary(fixVersion);
        }
        CurrentFIXDataDictionary.setCurrentFIXDataDictionary(fixDD);
        SLF4JLoggerProxy.info(this,
                              "Running test with FIX version {}",
                              fixVersion);
    }
    /**
     * Create a new NewFixVersionedTestCase instance.
     *
     * @param inVersion a <code>FIXVersion</code> value
     */
    protected NewFixVersionedTestCase(FIXVersion inVersion)
    {
        fixVersion = inVersion;
    }
    /**
     * Get the msgFactory value.
     *
     * @return a <code>FIXMessageFactory</code> value
     */
    protected FIXMessageFactory getMsgFactory()
    {
        return msgFactory;
    }
    /**
     * Get the fixVersion value.
     *
     * @return a <code>FIXVersion</code> value
     */
    protected FIXVersion getFixVersion()
    {
        return fixVersion;
    }
    /**
     * Get the fixDD value.
     *
     * @return a <code>FIXDataDictionary</code> value
     */
    protected FIXDataDictionary getFixDD()
    {
        return fixDD;
    }
    /**
     * fix message factory value
     */
    private FIXMessageFactory msgFactory;
    /**
     * fix version value
     */
    private FIXVersion fixVersion;
    /**
     * fix data dictionary value
     */
    private FIXDataDictionary fixDD;
}
