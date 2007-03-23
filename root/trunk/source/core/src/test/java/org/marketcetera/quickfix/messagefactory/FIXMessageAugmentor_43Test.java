package org.marketcetera.quickfix.messagefactory;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.MarketceteraTestSuite;
import junit.framework.TestCase;
import junit.framework.Test;

/**
 * @author toli
 * @version $Id$
 */

@ClassVersion("$Id$")
public class FIXMessageAugmentor_43Test extends TestCase {
    public FIXMessageAugmentor_43Test(String inName) {
        super(inName);
    }

    public static Test suite() {
        return new MarketceteraTestSuite(FIXMessageAugmentor_43Test.class);
    }

    public void testCountTT_applicableTypes() throws Exception {
        assertEquals(30, new FIXMessageAugmentor_43().getApplicableMsgTypes().size());
    }

}
