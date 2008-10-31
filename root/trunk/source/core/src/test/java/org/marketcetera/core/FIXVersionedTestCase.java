package org.marketcetera.core;

import junit.framework.TestCase;
import org.marketcetera.quickfix.*;

/**
 * Subclass of the regular {@link junit.framework.TestCase} that also knows
 * about FIXVersions and has a {@link org.marketcetera.quickfix.FIXMessageFactory}
 * @author toli
 * @version $Id$
 */

@ClassVersion("$Id$") //$NON-NLS-1$
public abstract class FIXVersionedTestCase extends TestCase {
    protected FIXMessageFactory msgFactory;
    protected FIXVersion fixVersion;
    protected FIXDataDictionary fixDD;

    public FIXVersionedTestCase(String inName, FIXVersion version) {
        super(inName);
        msgFactory = version.getMessageFactory();
        fixVersion = version;
    }


    protected void setUp() throws Exception {
        super.setUp();
        fixDD = FIXDataDictionaryManager.getFIXDataDictionary(fixVersion);
        if(fixDD == null) {
            FIXDataDictionaryManager.initialize(fixVersion, fixVersion.getDataDictionaryURL());
            fixDD = FIXDataDictionaryManager.getFIXDataDictionary(fixVersion);
        }
        CurrentFIXDataDictionary.setCurrentFIXDataDictionary(fixDD);
    }

    public String getName() {
        return super.getName()+"_"+msgFactory.getBeginString(); //$NON-NLS-1$
    }

    // helper methods
    public static boolean version42orBelow(FIXMessageFactory msgFactory)
    {
        return(FIXVersion.FIX40.toString().equals(msgFactory.getBeginString()) ||
           FIXVersion.FIX41.toString().equals(msgFactory.getBeginString()) ||
           FIXVersion.FIX42.toString().equals(msgFactory.getBeginString()));    
    }


}
