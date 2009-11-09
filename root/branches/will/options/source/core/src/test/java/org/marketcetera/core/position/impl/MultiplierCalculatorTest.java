package org.marketcetera.core.position.impl;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;
import org.marketcetera.core.position.MockTrade;
import org.marketcetera.trade.Equity;

/* $License$ */

/**
 * Test {@link MultiplierCalculator}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
public class MultiplierCalculatorTest {

    private PositionMetricsCalculator mMockCalculator;
    private MockTrade<Equity> mTrade1;
    private MockTrade<Equity> mTrade2;

    @Before
    public void before() {
        PositionMetricsImpl m1 = new PositionMetricsImpl(new BigDecimal("1"),
                new BigDecimal("2"), new BigDecimal("3"), new BigDecimal("4"),
                new BigDecimal("5"), new BigDecimal("6"), new BigDecimal("7"));
        PositionMetricsImpl m2 = new PositionMetricsImpl(new BigDecimal("10"),
                new BigDecimal("20"), new BigDecimal("30"),
                new BigDecimal("40"), new BigDecimal("50"),
                new BigDecimal("60"), new BigDecimal("70"));
        mTrade1 = MockTrade.createEquityTrade("ABC", "ABC", "ABC", "1", "1", 0);
        mTrade2 = MockTrade
                .createEquityTrade("ABC2", "ABC", "ABC", "1", "1", 0);

        mMockCalculator = mock(PositionMetricsCalculator.class);
        when(mMockCalculator.tick(BigDecimal.ONE)).thenReturn(m1);
        when(mMockCalculator.tick(BigDecimal.TEN)).thenReturn(m2);
        when(mMockCalculator.trade(mTrade1)).thenReturn(m1);
        when(mMockCalculator.trade(mTrade2)).thenReturn(m2);
    }

    @Test
    public void testTick() {
        MultiplierCalculator fixture = new MultiplierCalculator(
                mMockCalculator, 100);
        PositionMetricsImplTest.assertPositionMetrics(fixture
                .tick(BigDecimal.ONE), "1", "2", "300", "400", "500", "600",
                "700");
        PositionMetricsImplTest.assertPositionMetrics(fixture
                .tick(BigDecimal.TEN), "10", "20", "3000", "4000", "5000",
                "6000", "7000");
    }

    @Test
    public void testTickNoMultiplier() {
        MultiplierCalculator fixture = new MultiplierCalculator(
                mMockCalculator, null);
        PositionMetricsImplTest.assertPositionMetrics(fixture
                .tick(BigDecimal.ONE), "1", "2", null, null, null, null, null);
        PositionMetricsImplTest
                .assertPositionMetrics(fixture.tick(BigDecimal.TEN), "10",
                        "20", null, null, null, null, null);
    }

    @Test
    public void testTrade() {
        MultiplierCalculator fixture = new MultiplierCalculator(
                mMockCalculator, 100);
        PositionMetricsImplTest.assertPositionMetrics(fixture.trade(mTrade1),
                "1", "2", "300", "400", "500", "600", "700");
        PositionMetricsImplTest.assertPositionMetrics(fixture.trade(mTrade2),
                "10", "20", "3000", "4000", "5000", "6000", "7000");
    }

    @Test
    public void testTradeNoMultiplier() {
        MultiplierCalculator fixture = new MultiplierCalculator(
                mMockCalculator, null);
        PositionMetricsImplTest.assertPositionMetrics(fixture.trade(mTrade1),
                "1", "2", null, null, null, null, null);
        PositionMetricsImplTest.assertPositionMetrics(fixture.trade(mTrade2),
                "10", "20", null, null, null, null, null);
    }

}
