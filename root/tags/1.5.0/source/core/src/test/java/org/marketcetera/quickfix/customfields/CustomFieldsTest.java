package org.marketcetera.quickfix.customfields;

import junit.framework.Test;
import junit.framework.TestCase;
import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.MarketceteraTestSuite;
import org.marketcetera.quickfix.FIXMessageFactory;
import org.marketcetera.quickfix.FIXVersion;
import org.marketcetera.trade.MSymbol;

import quickfix.DataDictionary;
import quickfix.Message;
import quickfix.field.SubscriptionRequestType;

import java.util.Arrays;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Verify that custom fields are created and verified by data dictionary
 * @author toli
 * @version $Id$
 */

@ClassVersion("$Id$") //$NON-NLS-1$
public class CustomFieldsTest extends TestCase {
    public CustomFieldsTest(String inName) {
        super(inName);
    }

    public static Test suite() {
        return new MarketceteraTestSuite(CustomFieldsTest.class);
    }

    public void testHistoricalMarketDataRequest() throws Exception {
        FIXMessageFactory msgFactory = FIXVersion.FIX44.getMessageFactory();
        Message request = msgFactory.newMarketDataRequest("123", Arrays.asList(new MSymbol("IFLI"))); //$NON-NLS-1$ //$NON-NLS-2$
        request.setField(new SubscriptionRequestType(CustomFIXFieldConstants.SUBSCRIPTION_REQUEST_TYPE_HISTORICAL));
        request.setField(new DateFrom(new GregorianCalendar(2001, 4, 1).getTime()));
        request.setField(new DateTo(new Date()));

        DataDictionary dict = new DataDictionary(FIXVersion.FIX44.getDataDictionaryURL());
        dict.validate(request, true);

        // now round-trip it
        Message rountrip = new Message(request.toString(), dict, true);
        dict.validate(rountrip, true);
    }
}
