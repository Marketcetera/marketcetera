package org.marketcetera.quickfix.messagefactory;

import junit.framework.Test;
import junit.framework.TestCase;

import org.marketcetera.core.MarketceteraTestSuite;

/**
 * @author toli
 * @version $Id$
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
