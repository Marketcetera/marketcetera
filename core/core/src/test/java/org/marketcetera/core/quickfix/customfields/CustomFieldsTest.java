package org.marketcetera.core.quickfix.customfields;

import java.util.Arrays;
import java.util.Date;
import java.util.GregorianCalendar;

import junit.framework.Test;
import junit.framework.TestCase;
import org.marketcetera.core.MarketceteraTestSuite;
import org.marketcetera.core.quickfix.FIXMessageFactory;
import org.marketcetera.core.quickfix.FIXVersion;
import org.marketcetera.core.trade.Equity;
import quickfix.DataDictionary;
import quickfix.Message;
import quickfix.field.SubscriptionRequestType;

/**
 * Verify that custom fields are created and verified by data dictionary
 * @version $Id: CustomFieldsTest.java 16063 2012-01-31 18:21:55Z colin $
 */

public class CustomFieldsTest extends TestCase {
    public CustomFieldsTest(String inName) {
        super(inName);
    }

    public static Test suite() {
        return new MarketceteraTestSuite(CustomFieldsTest.class);
    }

    public void testHistoricalMarketDataRequest() throws Exception {
        FIXMessageFactory msgFactory = FIXVersion.FIX44.getMessageFactory();
        Message request = msgFactory.newMarketDataRequest("123", Arrays.asList(new Equity("IFLI"))); //$NON-NLS-1$ //$NON-NLS-2$
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
