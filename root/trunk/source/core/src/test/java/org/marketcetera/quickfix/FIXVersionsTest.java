package org.marketcetera.quickfix;

import junit.framework.Test;
import junit.framework.TestCase;
import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.MarketceteraTestSuite;
import org.marketcetera.core.MSymbol;
import quickfix.DataDictionary;
import quickfix.Message;
import quickfix.field.*;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author toli
 * @version $Id$
 */

@ClassVersion("$Id$")
public class FIXVersionsTest extends TestCase {
    public FIXVersionsTest(String inName) {
        super(inName);
    }

    public static Test suite() {
        return new MarketceteraTestSuite(FIXVersionsTest.class);
    }

    public void testNOS() throws Exception {
        DataDictionary dict0 = FIXDataDictionaryManager.getDataDictionary(FIXDataDictionaryManager.FIX_4_0_BEGIN_STRING);
        DataDictionary dict1 = FIXDataDictionaryManager.getDataDictionary(FIXDataDictionaryManager.FIX_4_1_BEGIN_STRING);
        DataDictionary dict2 = FIXDataDictionaryManager.getDataDictionary(FIXDataDictionaryManager.FIX_4_2_BEGIN_STRING);
        DataDictionary dict3 = FIXDataDictionaryManager.getDataDictionary(FIXDataDictionaryManager.FIX_4_3_BEGIN_STRING);
        DataDictionary dict4 = FIXDataDictionaryManager.getDataDictionary(FIXDataDictionaryManager.FIX_4_4_BEGIN_STRING);

        dict0.validate(createNOSHelper(FIXVersion.FIX40, "toli", 33, Side.BUY/*, true*/));
        dict1.validate(createNOSHelper(FIXVersion.FIX41, "toli", 33, Side.BUY/*, true*/));
        dict2.validate(createNOSHelper(FIXVersion.FIX42, "toli", 33, Side.BUY/*, true*/));
        dict3.validate(createNOSHelper(FIXVersion.FIX43, "toli", 33, Side.BUY/*, true*/));
        dict4.validate(createNOSHelper(FIXVersion.FIX44, "toli", 33, Side.BUY/*, true*/));


    }

    private Message createNOSHelper(FIXVersion version, String inSymbol, double qty, char inSide)
    {
        long suffix = System.currentTimeMillis();
        Message newSingle = version.getMessageFactory().newMarketOrder("123"+suffix, inSide, new BigDecimal(qty), new MSymbol(inSymbol),
                TimeInForce.DAY, "testAccount");
        newSingle.setField(new HandlInst(HandlInst.MANUAL_ORDER));
        newSingle.setField(new TimeInForce(TimeInForce.DAY));
        newSingle.setField(new Account("testAccount"));
        return newSingle;
    }
    



}
