package org.marketcetera.core;

import junit.framework.TestCase;
import org.marketcetera.quickfix.FIXDataDictionaryManager;
import org.marketcetera.quickfix.FIXMessageFactory;
import org.marketcetera.quickfix.FIXVersion;

/**
 * Subclass of the regular {@link junit.framework.TestCase} that also knows
 * about FIXVersions and has a {@link org.marketcetera.quickfix.FIXMessageFactory}
 * @author toli
 * @version $Id$
 */

@ClassVersion("$Id$")
public abstract class FIXVersionedTestCase extends TestCase {
    protected FIXMessageFactory msgFactory;
    protected FIXVersion fixVersion;

    public FIXVersionedTestCase(String inName, FIXVersion version) {
        super(inName);
        msgFactory = version.getMessageFactory();
        fixVersion = version;
    }


    protected void setUp() throws Exception {
        super.setUp();
        FIXDataDictionaryManager.setDataDictionary(fixVersion.getDataDictionaryURL());
    }

    public String getName() {
        return super.getName()+"_"+msgFactory.getBeginString();
    }

    // helper methods
    public static boolean version42orBelow(FIXMessageFactory msgFactory)
    {
        return(FIXVersion.FIX40.toString().equals(msgFactory.getBeginString()) ||
           FIXVersion.FIX41.toString().equals(msgFactory.getBeginString()) ||
           FIXVersion.FIX42.toString().equals(msgFactory.getBeginString()));    
    }


}
