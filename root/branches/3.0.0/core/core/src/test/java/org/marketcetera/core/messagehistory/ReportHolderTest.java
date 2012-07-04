package org.marketcetera.core.messagehistory;

import java.math.BigDecimal;

import org.junit.Test;
import org.marketcetera.core.quickfix.FIXVersion;
import org.marketcetera.core.trade.BrokerID;
import org.marketcetera.core.trade.Equity;
import org.marketcetera.core.trade.ExecutionReport;
import org.marketcetera.core.trade.Factory;
import org.marketcetera.core.trade.MessageCreationException;
import org.marketcetera.core.trade.OrderID;
import org.marketcetera.core.trade.Originator;
import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.field.OrdStatus;
import quickfix.field.Side;

import static org.junit.Assert.assertEquals;

/* $License$ */

/**
 * Tests {@link org.marketcetera.core.messagehistory.ReportHolder}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id: ReportHolderTest.java 16063 2012-01-31 18:21:55Z colin $
 * @since 1.0.0
 */
public class ReportHolderTest {

    @Test
    public void testGetMessage() throws Exception {
        Message message = createMessage();
        ExecutionReport report = createReport(message);
        ReportHolder holder = new ReportHolder(report, null);
        assertEquals(message, holder.getMessage());
    }

    @Test
    public void testGetUnderlying() throws Exception {
        assertEquals("ABC", new ReportHolder(createReport(createMessage()),
                "ABC").getUnderlying());
        assertEquals("123", new ReportHolder(createReport(createMessage()),
                "123", new OrderID("1")).getUnderlying());
    }

    private Message createMessage() throws FieldNotFound {
        Message message = FIXVersion.FIX42.getMessageFactory()
                .newExecutionReport("asdf", "asdf", "asdf", OrdStatus.CANCELED,
                        Side.BUY, BigDecimal.TEN, BigDecimal.ONE,
                        BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE,
                        BigDecimal.ONE, new Equity("123"), "asdf", "text");
        return message;
    }

    private ExecutionReport createReport(Message message)
            throws MessageCreationException {
        ExecutionReport report = Factory.getInstance().createExecutionReport(
                message, new BrokerID("ABC"), Originator.Server, null, null);
        return report;
    }
}
