package org.marketcetera.quickfix;

import junit.framework.Test;
import junit.framework.TestCase;
import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.MSymbol;
import org.marketcetera.core.MarketceteraTestSuite;
import quickfix.DataDictionary;
import quickfix.Message;
import quickfix.field.Account;
import quickfix.field.HandlInst;
import quickfix.field.Side;
import quickfix.field.TimeInForce;

import java.math.BigDecimal;

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
        DataDictionary dict0 = FIXDataDictionaryManager.getDataDictionary(FIXVersion.FIX40);
        DataDictionary dict1 = FIXDataDictionaryManager.getDataDictionary(FIXVersion.FIX41);
        DataDictionary dict2 = FIXDataDictionaryManager.getDataDictionary(FIXVersion.FIX42);
        DataDictionary dict3 = FIXDataDictionaryManager.getDataDictionary(FIXVersion.FIX43);
        DataDictionary dict4 = FIXDataDictionaryManager.getDataDictionary(FIXVersion.FIX44);

        dict0.validate(createNOSHelper(FIXVersion.FIX40, "toli", 33, Side.BUY));
        dict1.validate(createNOSHelper(FIXVersion.FIX41, "toli", 33, Side.BUY));
        dict2.validate(createNOSHelper(FIXVersion.FIX42, "toli", 33, Side.BUY));
        dict3.validate(createNOSHelper(FIXVersion.FIX43, "toli", 33, Side.BUY));
        dict4.validate(createNOSHelper(FIXVersion.FIX44, "toli", 33, Side.BUY));


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
