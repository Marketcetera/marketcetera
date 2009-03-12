package org.marketcetera.messagehistory;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;

import org.junit.Test;
import org.marketcetera.quickfix.FIXVersion;
import org.marketcetera.trade.BrokerID;
import org.marketcetera.trade.ExecutionReport;
import org.marketcetera.trade.Factory;
import org.marketcetera.trade.MSymbol;
import org.marketcetera.trade.Originator;

import quickfix.Message;
import quickfix.field.OrdStatus;
import quickfix.field.Side;

/* $License$ */

/**
 * Tests {@link ReportHolder}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.0.0
 */
public class ReportHolderTest {

    @Test
    public void testGetMessage() throws Exception {
        Message message = FIXVersion.FIX42.getMessageFactory()
                .newExecutionReport("asdf", "asdf", "asdf", OrdStatus.CANCELED,
                        Side.BUY, BigDecimal.TEN, BigDecimal.ONE,
                        BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE,
                        BigDecimal.ONE, new MSymbol("123"), "asdf");
        ExecutionReport report = Factory.getInstance().createExecutionReport(
                message, new BrokerID("ABC"), Originator.Server, null, null);
        ReportHolder holder = new ReportHolder(report);
        assertEquals(message, holder.getMessage());
    }
}
