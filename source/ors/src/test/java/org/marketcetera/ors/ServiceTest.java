package org.marketcetera.ors;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import org.junit.Test;
import org.marketcetera.client.Service;
import org.marketcetera.client.dest.DestinationStatus;
import org.marketcetera.core.MSymbol;
import org.marketcetera.symbology.SymbolScheme;
import org.marketcetera.trade.DestinationID;
import org.marketcetera.trade.ExecutionReport;
import org.marketcetera.trade.OrderCancelReject;
import org.marketcetera.trade.Originator;
import org.marketcetera.trade.ReportBaseImpl;
import org.marketcetera.trade.SecurityType;

import static org.junit.Assert.*;

/**
 * @author tlerios@marketcetera.com
 * @since $Release$
 * @version $Id: ClientServerTest.java 9962 2008-10-29 09:16:23Z tlerios $
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

        List<DestinationStatus> ds=
            s.getDestinationsStatus(getORSClientContext()).getDestinations();
        assertEquals(2,ds.size());
        DestinationStatus d=ds.get(0);
        assertEquals("Marketcetera Exchange 1",d.getName());
        assertEquals("metc1",d.getId().getValue());
        d=ds.get(1);
        assertEquals("Marketcetera Exchange 2",d.getName());
        assertEquals("metc2",d.getId().getValue());

        ReportBaseImpl[] rs=s.getReportsSince
            (getORSClientContext(),new Date(0));
        assertEquals(2,rs.length);

        DestinationID dID=new DestinationID("me");
        ExecutionReport er=(ExecutionReport)rs[0];
        assertEquals(dID,er.getDestinationID());
        assertEquals(Originator.Server,er.getOriginator());
        assertEquals("42",er.getOriginalOrderID().getValue());
        OrderCancelReject ocr=(OrderCancelReject)rs[1];
        assertEquals(dID,ocr.getDestinationID());
        assertEquals("43",ocr.getOriginalOrderID().getValue());

        assertEquals(BigDecimal.TEN,s.getPositionAsOf
                     (getORSClientContext(),new Date(10),TEST_SYMBOL));

        String id=s.getNextOrderID(getORSClientContext());
        assertNotNull(id);
        assertFalse(id.equals(s.getNextOrderID(getORSClientContext())));

        stopORS();
    }
}
