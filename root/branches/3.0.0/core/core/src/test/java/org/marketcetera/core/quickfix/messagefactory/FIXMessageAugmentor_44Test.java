package org.marketcetera.core.quickfix.messagefactory;

import junit.framework.Test;
import junit.framework.TestCase;
import org.marketcetera.core.MarketceteraTestSuite;

/**
 * @author toli
 * @version $Id: FIXMessageAugmentor_44Test.java 16063 2012-01-31 18:21:55Z colin $
 */

public class FIXMessageAugmentor_44Test extends TestCase {
    public FIXMessageAugmentor_44Test(String inName) {
        super(inName);
    }

    public static Test suite() {
        return new MarketceteraTestSuite(FIXMessageAugmentor_44Test.class);
    }

    public void testCountTT_applicableTypes() throws Exception {
        assertEquals(44, new FIXMessageAugmentor_44().getApplicableMsgTypes().size());
    }

}
