package org.marketcetera.quickfix.messagefactory;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.MarketceteraTestSuite;
import junit.framework.TestCase;
import junit.framework.Test;
import quickfix.Message;
import quickfix.StringField;
import quickfix.fix41.NewOrderSingle;
import quickfix.field.*;

import java.math.BigDecimal;

/**
 * @author toli
 * @version $Id$
 */

@ClassVersion("$Id$")
public class FIXMessageAugmentor_40Test extends TestCase {
    public FIXMessageAugmentor_40Test(String inName) {
        super(inName);
    }

    public static Test suite() {
        return new MarketceteraTestSuite(FIXMessageAugmentor_40Test.class);
    }

    public void testCountTT_applicableTypes() throws Exception {
        assertEquals(4, new FIXMessageAugmentor_40().getApplicableMsgTypes().size());
    }

}
