package org.marketcetera.core.quickfix.messagefactory;

import junit.framework.Test;
import junit.framework.TestCase;
import org.marketcetera.core.MarketceteraTestSuite;

/**
 * @version $Id: FIXMessageAugmentor_42Test.java 16063 2012-01-31 18:21:55Z colin $
 */

public class FIXMessageAugmentor_42Test extends TestCase {
    public FIXMessageAugmentor_42Test(String inName) {
        super(inName);
    }

    public static Test suite() {
        return new MarketceteraTestSuite(FIXMessageAugmentor_42Test.class);
    }

    public void testCountTT_applicableTypes() throws Exception {
        assertEquals(18, new FIXMessageAugmentor_42().getApplicableMsgTypes().size());
    }

}
