package org.marketcetera.quickfix;

import junit.framework.Test;
import junit.framework.TestCase;
import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.MarketceteraTestSuite;
import org.marketcetera.trade.Equity;
import org.marketcetera.util.time.DateService;

import quickfix.DataDictionary;
import quickfix.Message;
import quickfix.field.*;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author toli
 * @version $Id$
 */

@ClassVersion("$Id$") //$NON-NLS-1$
public class FIXVersionsTest extends TestCase {
    public FIXVersionsTest(String inName) {
        super(inName);
    }

    public static Test suite() {
        return new MarketceteraTestSuite(FIXVersionsTest.class);
    }

    public void testNOS() throws Exception {
        DataDictionary dict0 = new DataDictionary(FIXVersion.FIX40.getDataDictionaryName());
        DataDictionary dict1 = new DataDictionary(FIXVersion.FIX41.getDataDictionaryName());
        DataDictionary dict2 = new DataDictionary(FIXVersion.FIX42.getDataDictionaryName());
        DataDictionary dict3 = new DataDictionary(FIXVersion.FIX43.getDataDictionaryName());
        DataDictionary dict4 = new DataDictionary(FIXVersion.FIX44.getDataDictionaryName());
        DataDictionary dict5 = new DataDictionary(FIXVersion.FIX_SYSTEM.getDataDictionaryName());

        dict0.validate(createNOSHelper(FIXVersion.FIX40, "toli", 33, Side.BUY), true); //$NON-NLS-1$
        dict1.validate(createNOSHelper(FIXVersion.FIX41, "toli", 33, Side.BUY), true); //$NON-NLS-1$
        dict2.validate(createNOSHelper(FIXVersion.FIX42, "toli", 33, Side.BUY), true); //$NON-NLS-1$
        dict3.validate(createNOSHelper(FIXVersion.FIX43, "toli", 33, Side.BUY), true); //$NON-NLS-1$
        dict4.validate(createNOSHelper(FIXVersion.FIX44, "toli", 33, Side.BUY), true); //$NON-NLS-1$
        dict5.validate(createNOSHelper(FIXVersion.FIX_SYSTEM, "toli", 33, Side.BUY), true); //$NON-NLS-1$


    }

    private Message createNOSHelper(FIXVersion version, String inSymbol, int qty, char inSide) throws Exception
    {
        long suffix = System.currentTimeMillis();
        Message newSingle = version.getMessageFactory().newMarketOrder("123"+suffix, inSide, new BigDecimal(qty), new Equity(inSymbol), //$NON-NLS-1$
                TimeInForce.DAY, "testAccount"); //$NON-NLS-1$
        newSingle.setField(new TimeInForce(TimeInForce.DAY));
        newSingle.setField(new Account("testAccount")); //$NON-NLS-1$
        assertSame(version, FIXVersion.getFIXVersion(newSingle));
        // Add fields that are not added for system fix messages as they
        // are not added by the clients, they are only added by ORS. 
        if(version == FIXVersion.FIX_SYSTEM) {
            newSingle.setField(new TransactTime(DateService.toUtcDateTime(new Date())));
            newSingle.setField(new HandlInst(HandlInst.AUTOMATED_EXECUTION_ORDER_PRIVATE_NO_BROKER_INTERVENTION));
        }
        return newSingle;
    }
    



}
