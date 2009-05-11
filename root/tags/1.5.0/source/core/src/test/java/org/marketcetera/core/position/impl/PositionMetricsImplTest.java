package org.marketcetera.core.position.impl;

import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.marketcetera.core.position.impl.OrderingComparison.comparesEqualTo;

import java.math.BigDecimal;

import org.junit.Test;
import org.marketcetera.core.position.PositionMetrics;
import org.marketcetera.module.ExpectedFailure;

/* $License$ */

/**
 * Test {@link PositionMetricsImpl}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.5.0
 */
public class PositionMetricsImplTest {

    @Test
    public void testConstructor() throws Exception {
        assertPositionMetrics(new PositionMetricsImpl(), "0", "0", null, null, null, null, null);
        assertPositionMetrics(new PositionMetricsImpl(convert("-10")), "-10", "-10", null, null, null, null, null);
        String incomingPosition = "17";
        String position = "10";
        String positional = "15";
        String trading = "12";
        String realized = "11";
        String unrealized = "14";
        String total = "20";
        assertPositionMetrics(createMetrics(incomingPosition, position, positional, trading, realized, unrealized, total),
                incomingPosition, position, positional, trading, realized, unrealized, total);
        incomingPosition = "-16.9";
        position = "10.8";
        positional = "15.9";
        trading = "12.5";
        realized = "11.3";
        unrealized = "14.55";
        total = "20.02";
        assertPositionMetrics(createMetrics(incomingPosition, position, positional, trading, realized, unrealized, total),
                incomingPosition, position, positional, trading, realized, unrealized, total);
        new ExpectedFailure<IllegalArgumentException>(null) {

            @Override
            protected void run() throws Exception {
                createMetrics(null, "0", "0", "0", "0", "0", "0");
            }
        };
        new ExpectedFailure<IllegalArgumentException>(null) {

            @Override
            protected void run() throws Exception {
                createMetrics("0", null, "0", "0", "0", "0", "0");
            }
        };
    }

    public static PositionMetrics createMetrics(String incomingPosition, String position, String positional,
            String trading, String realized, String unrealized, String total) {
        return new PositionMetricsImpl(convert(incomingPosition), convert(position), convert(positional),
                convert(trading), convert(realized), convert(unrealized), convert(total));
    }

    public static void assertPositionMetrics(PositionMetrics pnl, String incomingPosition, String position,
            String positional, String trading, String realized, String unrealized, String total) {
        assertBigDecimal(incomingPosition, pnl.getIncomingPosition());
        assertBigDecimal(position, pnl.getPosition());
        assertBigDecimal(positional, pnl.getPositionPL());
        assertBigDecimal(trading, pnl.getTradingPL());
        assertBigDecimal(realized, pnl.getRealizedPL());
        assertBigDecimal(unrealized, pnl.getUnrealizedPL());
        assertBigDecimal(total, pnl.getTotalPL());
    }

    private static void assertBigDecimal(String expected, BigDecimal actual) {
        if (expected == null)
            assertThat(actual, nullValue());
        else
            assertThat(actual, comparesEqualTo(convert(expected)));
    }

    private static BigDecimal convert(String string) {
        return string == null ? null : new BigDecimal(string);
    }

}
