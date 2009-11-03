package org.marketcetera.ors;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import org.junit.Test;
import org.marketcetera.client.Client;
import org.marketcetera.client.brokers.BrokerStatus;
import org.marketcetera.trade.Equity;
import org.marketcetera.trade.Factory;
import org.marketcetera.trade.Option;
import org.marketcetera.trade.OptionType;
import org.marketcetera.trade.ReportBase;

import static org.junit.Assert.*;

/**
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id$
 */

/* $License$ */

public class ServiceTest
    extends ORSTestBase
{
    private static final Equity TEST_EQUITY =new Equity
        ("IBM");
    private static final Option TEST_OPTION =new Option
        ("IBM", "20101010", BigDecimal.TEN, OptionType.Call);


    @Test
    public void services()
        throws Exception
    {
        Client c=getAdminClient().getClient();

        List<BrokerStatus> bs=c.getBrokersStatus().getBrokers();
        assertEquals(2,bs.size());
        BrokerStatus b=bs.get(0);
        assertEquals("Broker 1",b.getName());
        assertEquals("broker1",b.getId().getValue());
        b=bs.get(1);
        assertEquals("Broker 2",b.getName());
        assertEquals("broker2",b.getId().getValue());

        ReportBase[] rs=c.getReportsSince(new Date());
        assertEquals(0,rs.length);

        assertEquals(BigDecimal.ZERO,c.getPositionAsOf
                     (new Date(), TEST_EQUITY));

        assertTrue(c.getPositionsAsOf(new Date()).isEmpty());

        String id=Factory.getInstance().createOrderSingle().
            getOrderID().getValue();
        assertNotNull(id);
        assertFalse(id.equals(Factory.getInstance().createOrderSingle().
                              getOrderID().getValue()));

        assertArrayEquals(new String[]{"JUL", "JUP", "JUQ", "JUS", "JUX"},
                c.getOptionRoots("JNPR").toArray(new String[2]));
        assertEquals("JNPR", c.getUnderlying("JUX"));

        assertEquals(BigDecimal.ZERO,c.getOptionPositionAsOf(new Date(),
                TEST_OPTION));
        assertEquals(0,c.getAllOptionPositionsAsOf(new Date()).size());
        assertEquals(0,c.getOptionPositionsAsOf(new Date(), TEST_OPTION.getSymbol()).size());
    }
}
