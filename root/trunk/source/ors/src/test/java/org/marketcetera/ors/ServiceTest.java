package org.marketcetera.ors;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import org.junit.Test;
import org.marketcetera.client.Service;
import org.marketcetera.client.brokers.BrokerStatus;
import org.marketcetera.trade.MSymbol;
import org.marketcetera.trade.ReportBaseImpl;
import org.marketcetera.trade.SecurityType;
import org.marketcetera.util.ws.wrappers.DateWrapper;

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
    public void startStopORS()
        throws Exception
    {
        startORS(new String[0]);
        Service s=getORSService();

        List<BrokerStatus> bs=
            s.getBrokersStatus(getORSClientContext()).getBrokers();
        assertEquals(2,bs.size());
        BrokerStatus b=bs.get(0);
        assertEquals("Broker 1",b.getName());
        assertEquals("broker1",b.getId().getValue());
        b=bs.get(1);
        assertEquals("Broker 2",b.getName());
        assertEquals("broker2",b.getId().getValue());

        ReportBaseImpl[] rs=s.getReportsSince
            (getORSClientContext(),new DateWrapper(new Date()));
        assertNull(rs);

        assertEquals(BigDecimal.ZERO,s.getPositionAsOf
                     (getORSClientContext(),new DateWrapper(new Date()),
                      TEST_SYMBOL));

        assertTrue(s.getPositionsAsOf
                   (getORSClientContext(),
                    new DateWrapper(new Date())).getMap().isEmpty());

        String id=s.getNextOrderID(getORSClientContext());
        assertNotNull(id);
        assertFalse(id.equals(s.getNextOrderID(getORSClientContext())));

        stopORS();
    }
}
