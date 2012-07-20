package org.marketcetera.trade;

import org.junit.Test;
import org.marketcetera.util.test.TestCaseBase;
import quickfix.Message;
import quickfix.field.MsgType;
import quickfix.field.OrigClOrdID;
import quickfix.fix44.ExecutionReport;

import static org.junit.Assert.*;

/**
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id$
 */

/* $License$ */

public class FIXMessageAdapterTest
    extends TestCaseBase
{
    @Test
    public void all()
        throws Exception
    {
        FIXMessageAdapter adapter=new FIXMessageAdapter();
        assertNull(adapter.unmarshal(adapter.marshal(null)));

        ExecutionReport er=new ExecutionReport();
        er.set(new OrigClOrdID("42"));
        Message msg=adapter.unmarshal(adapter.marshal(er));
        assertEquals(MsgType.EXECUTION_REPORT,
                     msg.getHeader().getString(MsgType.FIELD));
        assertEquals("42",msg.getString(OrigClOrdID.FIELD));
    }
}
