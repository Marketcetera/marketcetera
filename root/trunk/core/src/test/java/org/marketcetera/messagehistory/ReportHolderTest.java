package org.marketcetera.messagehistory;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;

import org.junit.Test;
import org.marketcetera.quickfix.FIXVersion;
import org.marketcetera.trade.BrokerID;
import org.marketcetera.trade.Equity;
import org.marketcetera.trade.ExecutionReport;
import org.marketcetera.trade.Factory;
import org.marketcetera.trade.MessageCreationException;
import org.marketcetera.trade.OrderID;
import org.marketcetera.trade.Originator;

import quickfix.FieldNotFound;
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
