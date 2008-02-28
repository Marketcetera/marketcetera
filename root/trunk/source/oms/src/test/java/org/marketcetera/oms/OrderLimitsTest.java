package org.marketcetera.oms;

import junit.framework.Test;
import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.ExpectedTestFailure;
import org.marketcetera.core.FIXVersionTestSuite;
import org.marketcetera.core.FIXVersionedTestCase;
import org.marketcetera.quickfix.FIXMessageUtilTest;
import org.marketcetera.quickfix.FIXVersion;
import quickfix.Message;
import quickfix.field.OrderQty;
import quickfix.field.Price;
import quickfix.field.Side;

import java.math.BigDecimal;

/**
 * @author toli
 * @version $Id$
 */

@ClassVersion("$Id$")
public class OrderLimitsTest extends FIXVersionedTestCase {
    public OrderLimitsTest(String inName, FIXVersion version) {
        super(inName, version);
    }

    public static Test suite() {
        return new FIXVersionTestSuite(OrderLimitsTest.class, OrderManagementSystem.OMS_MESSAGE_BUNDLE_INFO,
                FIXVersionTestSuite.ALL_VERSIONS);
    }

    public void testMaxQty() throws Exception {
        final OrderLimits limits = createOrderLimits(new BigDecimal(100), null, null, null, false);
        final Message nos = FIXMessageUtilTest.createNOS("TOLI", new BigDecimal("23.40"), new BigDecimal("100"), Side.BUY, msgFactory);
        limits.verifyOrderLimits(nos);

        nos.setField(new OrderQty(99));
        limits.verifyOrderLimits(nos);

        nos.setField(new OrderQty(111));
        new ExpectedTestFailure(OrderLimitException.class,
                OrderLimitException.createMaxQuantityException(new BigDecimal(111), new BigDecimal(100), "TOLI").getLocalizedMessage()) {
            protected void execute() throws Throwable {
                limits.verifyOrderLimits(nos);
            }
        }.run();
    }

    public void testMaxNotional() throws Exception {
        final OrderLimits limits = createOrderLimits(null, new BigDecimal(10000), null, null, false);
        final Message nos = FIXMessageUtilTest.createNOS("TOLI", new BigDecimal("23.40"), new BigDecimal("100"), Side.BUY, msgFactory);
        limits.verifyOrderLimits(nos);

        nos.setField(new OrderQty(1000));
        new ExpectedTestFailure(OrderLimitException.class, "notional") {
            protected void execute() throws Throwable {
                limits.verifyOrderLimits(nos);
            }
        }.run();
    }

    public void testMaxPrice() throws Exception {
        final OrderLimits limits = createOrderLimits(null, null, null, new BigDecimal(100), false);
        final Message nos = FIXMessageUtilTest.createNOS("TOLI", new BigDecimal("23.40"), new BigDecimal("100"), Side.BUY, msgFactory);
        limits.verifyOrderLimits(nos);

        nos.setField(new Price(1000));
        new ExpectedTestFailure(OrderLimitException.class, "max price") {
            protected void execute() throws Throwable {
                limits.verifyOrderLimits(nos);
            }
        }.run();
    }

    public void testMinPrice() throws Exception {
        final OrderLimits limits = createOrderLimits(null, null, new BigDecimal(100), null, false);
        final Message nos = FIXMessageUtilTest.createNOS("TOLI", new BigDecimal("234"), new BigDecimal("100"), Side.BUY, msgFactory);
        limits.verifyOrderLimits(nos);

        nos.setField(new Price(10));
        new ExpectedTestFailure(OrderLimitException.class, "min price") {
            protected void execute() throws Throwable {
                limits.verifyOrderLimits(nos);
            }
        }.run();
    }

    public void testMarketWithMinPrice() throws Exception {
        final OrderLimits limits = createOrderLimits(null, null, new BigDecimal(100), null, false);
        final Message nos = FIXMessageUtilTest.createMarketNOS("TOLI", new BigDecimal("50"), Side.BUY, msgFactory);
        new ExpectedTestFailure(OrderLimitException.class,
                OrderLimitException.createMarketOrderWithPriceException("TOLI").getLocalizedMessage()) {
            protected void execute() throws Throwable {
                limits.verifyOrderLimits(nos);
            }
        }.run();
    }

    public void testMarketWithNoPrice() throws Exception {
        Message nos = FIXMessageUtilTest.createMarketNOS("TOLI", new BigDecimal("50"), Side.BUY, msgFactory);
        // order limits w/out price should let market order through
        OrderLimits limits_no_price = createOrderLimits(new BigDecimal(200), null, null, null, false);
        limits_no_price.verifyOrderLimits(nos);

        limits_no_price = createBasicOrderLimits();
        limits_no_price.verifyOrderLimits(nos);
    }

    public void testMarketWithMaxPrice() throws Exception {
        final OrderLimits limits = createOrderLimits(null, null, null, new BigDecimal(100), false);
        final Message nos = FIXMessageUtilTest.createMarketNOS("TOLI", new BigDecimal("50"), Side.BUY, msgFactory);
        new ExpectedTestFailure(OrderLimitException.class,
                OrderLimitException.createMarketOrderWithPriceException("TOLI").getLocalizedMessage()) {
            protected void execute() throws Throwable {
                limits.verifyOrderLimits(nos);
            }
        }.run();
    }

    public void testMarketWithMaxNotional() throws Exception {
        final OrderLimits limits = createOrderLimits(null, new BigDecimal(100), null, null, false);
        final Message nos = FIXMessageUtilTest.createMarketNOS("TOLI", new BigDecimal("50"), Side.BUY, msgFactory);
        new ExpectedTestFailure(OrderLimitException.class,
                OrderLimitException.createMarketOrderWithPriceException("TOLI").getLocalizedMessage()) {
            protected void execute() throws Throwable {
                limits.verifyOrderLimits(nos);
            }
        }.run();
    }

    public void testMarket() throws Exception {
        final OrderLimits limits = createOrderLimits(null, null, null, null, true);
        final Message nos = FIXMessageUtilTest.createMarketNOS("TOLI", new BigDecimal("50"), Side.BUY, msgFactory);
        new ExpectedTestFailure(OrderLimitException.class,
                OrderLimitException.createMarketOrderException("TOLI").getLocalizedMessage()) {
            protected void execute() throws Throwable {
                limits.verifyOrderLimits(nos);
            }
        }.run();
    }

    public void testPassing() throws Exception {
        OrderLimits limits = createOrderLimits(new BigDecimal(100), new BigDecimal(1000), BigDecimal.ZERO, new BigDecimal(100), false);
        limits.verifyOrderLimits(FIXMessageUtilTest.createNOS("TOLI", new BigDecimal("10"), new BigDecimal("5"), Side.BUY, msgFactory));

        limits = createOrderLimits(new BigDecimal(100), null, null, null, false);
        limits.verifyOrderLimits(FIXMessageUtilTest.createMarketNOS("TOLI", new BigDecimal("10"), Side.BUY, msgFactory));
    }

    /** Creates an empty order limit set where market orders are allowed */
    public static OrderLimits createBasicOrderLimits() {
        return createOrderLimits(null, null, null, null, false);
    }
    public static OrderLimits createOrderLimits(BigDecimal maxShares, BigDecimal maxNotional,
                                                BigDecimal minSharePrice, BigDecimal maxSharePrice, boolean disallowMarketOrders)
    {
        OrderLimits limits = new OrderLimits();
        limits.setDisallowMarketOrders(disallowMarketOrders);
        limits.setMaxQuantityPerOrder(maxShares);
        limits.setMaxNotionalPerOrder(maxNotional);
        limits.setMaxPrice(maxSharePrice);
        limits.setMinPrice(minSharePrice);
        return limits;
    }
}
