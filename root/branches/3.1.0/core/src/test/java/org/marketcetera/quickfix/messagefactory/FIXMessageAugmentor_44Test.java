package org.marketcetera.quickfix.messagefactory;

import junit.framework.Test;
import junit.framework.TestCase;

import org.marketcetera.core.MarketceteraTestSuite;

/**
 * @author toli
 * @version $Id$
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
