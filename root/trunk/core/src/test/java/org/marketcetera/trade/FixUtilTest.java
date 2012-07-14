package org.marketcetera.trade;

import java.math.BigDecimal;

import junit.framework.Test;

import org.marketcetera.core.FIXVersionTestSuite;
import org.marketcetera.core.FIXVersionedTestCase;
import org.marketcetera.quickfix.FIXVersion;

import quickfix.Message;
import quickfix.field.LeavesQty;
import quickfix.field.OrdStatus;
import quickfix.field.Side;

public class FixUtilTest extends FIXVersionedTestCase{
    public FixUtilTest(String inName, FIXVersion version) {
        super(inName, version);
    }
    public static Test suite() {
        return new FIXVersionTestSuite(FixUtilTest.class,
                FIXVersionTestSuite.ALL_FIX_VERSIONS);
    }
    
    public void testAllFixExecutionReport()throws Exception{
        
        Message message = msgFactory.newExecutionReport("clordid1", "clordid1",
                "execido1", OrdStatus.NEW, Side.BUY, new BigDecimal(300), null,
                new BigDecimal(0), new BigDecimal(0), new BigDecimal(0),
                new BigDecimal(0), new Equity("IBM"), "account", "text");
        message.setField(new LeavesQty(90.0));
       
        ExecutionType execType = FIXUtil.getExecOrExecTransType(message);
        assertEquals(ExecutionType.New, execType);
       
        Message message2 = msgFactory.newExecutionReport("clordid1",
                "clordid1", "execido1", OrdStatus.PARTIALLY_FILLED, Side.BUY,
                new BigDecimal(300), new BigDecimal(111),null, new BigDecimal(
                        111), new BigDecimal(110), new BigDecimal(111),
                new Equity("IBM"), "account", "text");
        message2.setField(new LeavesQty(190.0));
        
        execType = FIXUtil.getExecOrExecTransType(message2);
        assertEquals(ExecutionType.PartialFill, execType);
        
        Message message3 = msgFactory.newExecutionReport("clordid1",
                "clordid1", "execido1", OrdStatus.FILLED, Side.BUY,
                new BigDecimal(300), new BigDecimal(55), new BigDecimal(190), new BigDecimal(55),
                new BigDecimal(300), new BigDecimal(55), new Equity("IBM"),
                "account", "text");
        
        execType = FIXUtil.getExecOrExecTransType(message3);
        assertEquals(ExecutionType.Fill, execType);
        
    }
   
}
