package org.marketcetera.ors;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import org.junit.Test;
import org.marketcetera.client.Service;
import org.marketcetera.client.brokers.BrokerStatus;
import org.marketcetera.core.MSymbol;
import org.marketcetera.symbology.SymbolScheme;
import org.marketcetera.trade.ReportBaseImpl;
import org.marketcetera.trade.SecurityType;

import static org.junit.Assert.*;

/**
 * @author tlerios@marketcetera.com
 * @since $Release$
 * @version $Id$
 */

/* $License$ */

public class ServiceTest
    extends ORSTestBase
{
    private static final MSymbol TEST_SYMBOL=new MSymbol
        ("IBM",SymbolScheme.ISIN,SecurityType.CommonStock);

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
        assertEquals("Marketcetera Exchange 1",b.getName());
        assertEquals("metc1",b.getId().getValue());
        b=bs.get(1);
        assertEquals("Marketcetera Exchange 2",b.getName());
        assertEquals("metc2",b.getId().getValue());

        ReportBaseImpl[] rs=s.getReportsSince
            (getORSClientContext(),new Date());
        assertNull(rs);

        assertEquals(BigDecimal.ZERO,s.getPositionAsOf
                     (getORSClientContext(),new Date(),TEST_SYMBOL));

        String id=s.getNextOrderID(getORSClientContext());
        assertNotNull(id);
        assertFalse(id.equals(s.getNextOrderID(getORSClientContext())));

        stopORS();
    }
}
