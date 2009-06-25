package org.marketcetera.ors;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import org.junit.Test;
import org.marketcetera.client.Client;
import org.marketcetera.client.brokers.BrokerStatus;
import org.marketcetera.trade.Factory;
import org.marketcetera.trade.MSymbol;
import org.marketcetera.trade.ReportBase;
import org.marketcetera.trade.SecurityType;

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
    private static final MSymbol TEST_SYMBOL=new MSymbol
        ("IBM",SecurityType.CommonStock);

    @Test
    public void services()
        throws Exception
    {
        startORS();

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
                     (new Date(),TEST_SYMBOL));

        assertTrue(c.getPositionsAsOf(new Date()).isEmpty());

        String id=Factory.getInstance().createOrderSingle().
            getOrderID().getValue();
        assertNotNull(id);
        assertFalse(id.equals(Factory.getInstance().createOrderSingle().
                              getOrderID().getValue()));

        stopORS();
    }
}
