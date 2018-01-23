package org.marketcetera.quickfix.customfields;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.MarketceteraTestSuite;
import org.marketcetera.quickfix.FIXMessageFactory;
import org.marketcetera.quickfix.FIXVersion;
import org.marketcetera.trade.Equity;
import org.marketcetera.trade.Instrument;

import quickfix.DataDictionary;
import quickfix.Message;
import quickfix.field.SubscriptionRequestType;

import com.google.common.collect.Lists;

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
        List<Instrument> list = Lists.newArrayList();
        list.add(new Equity("IFLI"));
        Message request = msgFactory.newMarketDataRequest("123",list); //$NON-NLS-1$ //$NON-NLS-2$
        request.setField(new SubscriptionRequestType(CustomFIXFieldConstants.SUBSCRIPTION_REQUEST_TYPE_HISTORICAL));
        request.setField(new DateFrom(new GregorianCalendar(2001, 4, 1).getTime()));
        request.setField(new DateTo(new Date()));

        DataDictionary dict = new DataDictionary(FIXVersion.FIX44.getDataDictionaryName());
        dict.validate(request, true);

        // now round-trip it
        Message rountrip = new Message(request.toString(), dict, true);
        dict.validate(rountrip, true);
    }
}
