package org.marketcetera.quickfix;

import junit.framework.Test;
import junit.framework.TestCase;
import org.marketcetera.core.*;
import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.field.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.prefs.BackingStoreException;

/**
 * @author Graham Miller
 * @version $Id$
 */
@ClassVersion("$Id$")
public class OrderRouteManagerTest extends TestCase
{
    private FIXMessageFactory msgFactory = FIXVersion.FIX42.getMessageFactory();
    public OrderRouteManagerTest(String name)
    {
        super(name);
    }

    public static Test suite() {
        return new MarketceteraTestSuite(OrderRouteManagerTest.class);
    }
        
    public void testModifyOrderSeparateSuffix() throws BackingStoreException, MarketceteraException, FieldNotFound
    {
        OrderRouteManager routeManager = getORMWithOrderRouting();

        Message message =msgFactory.newLimitOrder("12345",
            Side.BUY,
            new BigDecimal(1000),
            new MSymbol("BRK/A.N"),
            new BigDecimal("123.45"),
            TimeInForce.DAY,
            null
            );
        routeManager.modifyOrder(message);

        assertEquals("12345", message.getField(new ClOrdID()).getValue());
        assertEquals(Side.BUY, message.getField(new Side()).getValue());
        assertEquals(1000.0, message.getField(new OrderQty()).getValue(), .0001);
        assertEquals("BRK", message.getField(new Symbol()).getValue());
        assertEquals("A", message.getField(new SymbolSfx()).getValue());
        assertEquals("SIGMA", message.getField(new ExDestination()).getValue());


        message =msgFactory.newLimitOrder("12346",
            Side.SELL,
            new BigDecimal(100),
            new MSymbol("BRK/B"),
            new BigDecimal("54.32"),
            TimeInForce.DAY,
            null
            );
        routeManager.modifyOrder(message);

        assertEquals("12346", message.getField(new ClOrdID()).getValue());
        assertEquals(Side.SELL, message.getField(new Side()).getValue());
        assertEquals(100.0, message.getField(new OrderQty()).getValue(), .0001);
        assertEquals("BRK", message.getField(new Symbol()).getValue());
        assertEquals("B", message.getField(new SymbolSfx()).getValue());
        final Message outerMessage1 = message;
        new ExpectedTestFailure(FieldNotFound.class) {
            protected void execute() throws Throwable {
                outerMessage1.getField(new ExDestination()).getValue();
            }
        }.run();

        message = message =msgFactory.newLimitOrder("12347",
            Side.SELL_SHORT,
            new BigDecimal(2000),
            new MSymbol("VOD/.LN"),
            new BigDecimal("111.11"),
            TimeInForce.AT_THE_OPENING,
            null
            );
        routeManager.modifyOrder(message);

        assertEquals("12347", message.getField(new ClOrdID()).getValue());
        assertEquals(Side.SELL_SHORT, message.getField(new Side()).getValue());
        assertEquals(2000.0, message.getField(new OrderQty()).getValue());
        assertEquals("VOD/", message.getField(new Symbol()).getValue());
        final Message outerMessage2 = message;
        new ExpectedTestFailure(FieldNotFound.class) {
            protected void execute() throws Throwable {
                outerMessage2.getField(new SymbolSfx()).getValue();
            }
        }.run();

        new ExpectedTestFailure(FieldNotFound.class) {
            protected void execute() throws Throwable {
                outerMessage2.getField(new ExDestination()).getValue();
            }
        }.run();

    }

    public void testModifyOrderAttachedSuffix() throws BackingStoreException, MarketceteraException, FieldNotFound
    {
        OrderRouteManager routeManager = new OrderRouteManager();
        routeManager.setSeparateSuffix(false);
        routeManager.addOneRoute("N", "SIGMA");
        routeManager.setRouteMethod(OrderRouteManager.FIELD_100_METHOD);

        Message message =msgFactory.newLimitOrder("12345",
            Side.BUY,
            new BigDecimal(1000),
            new MSymbol("BRK/A.N"),
            new BigDecimal("123.45"),
            TimeInForce.DAY,
            null
            );
        routeManager.modifyOrder(message);

        assertEquals("12345", message.getField(new ClOrdID()).getValue());
        assertEquals(Side.BUY, message.getField(new Side()).getValue());
        assertEquals(1000.0, message.getField(new OrderQty()).getValue(), .0001);
        assertEquals("BRK/A", message.getField(new Symbol()).getValue());
        final Message outerMessage = message;
        new ExpectedTestFailure(FieldNotFound.class) {
            protected void execute() throws Throwable {
                outerMessage.getField(new SymbolSfx()).getValue();
            }
        }.run();
        assertEquals("SIGMA", message.getField(new ExDestination()).getValue());


        message =msgFactory.newLimitOrder("12346",
            Side.SELL,
            new BigDecimal(100),
            new MSymbol("BRK/B"),
            new BigDecimal("54.32"),
            TimeInForce.DAY,
            null
            );
        routeManager.modifyOrder(message);

        assertEquals("12346", message.getField(new ClOrdID()).getValue());
        assertEquals(Side.SELL, message.getField(new Side()).getValue());
        assertEquals(100.0, message.getField(new OrderQty()).getValue(),.0001);
        assertEquals("BRK/B", message.getField(new Symbol()).getValue());
        final Message outerMessage2 = message;
        new ExpectedTestFailure(FieldNotFound.class) {
            protected void execute() throws Throwable {
                outerMessage2.getField(new SymbolSfx()).getValue();
            }
        }.run();
        new ExpectedTestFailure(FieldNotFound.class) {
            protected void execute() throws Throwable {
                outerMessage2.getField(new ExDestination()).getValue();
            }
        }.run();

        message =msgFactory.newLimitOrder("12347",
            Side.SELL_SHORT,
            new BigDecimal(2000),
            new MSymbol("IBM"),
            new BigDecimal("111.11"),
            TimeInForce.AT_THE_OPENING,
            null
            );
        routeManager.modifyOrder(message);

        assertEquals("12347", message.getField(new ClOrdID()).getValue());
        assertEquals(Side.SELL_SHORT, message.getField(new Side()).getValue());
        assertEquals(2000.0, message.getField(new OrderQty()).getValue(), .0001);
        assertEquals("IBM", message.getField(new Symbol()).getValue());
        final Message outerMessage3 = message;
        new ExpectedTestFailure(FieldNotFound.class) {
            protected void execute() throws Throwable {
                outerMessage3.getField(new SymbolSfx()).getValue();
            }
        }.run();
        new ExpectedTestFailure(FieldNotFound.class) {
            protected void execute() throws Throwable {
                outerMessage3.getField(new ExDestination()).getValue();
            }
        }.run();

        message =msgFactory.newLimitOrder("12347",
            Side.SELL_SHORT,
            new BigDecimal(2000),
            new MSymbol("VOD/"),
            new BigDecimal("111.11"),
            TimeInForce.AT_THE_OPENING,
            null
            );
        routeManager.modifyOrder(message);

        assertEquals("12347", message.getField(new ClOrdID()).getValue());
        assertEquals(Side.SELL_SHORT, message.getField(new Side()).getValue());
        assertEquals(2000.0, message.getField(new OrderQty()).getValue(),.0001);
        assertEquals("VOD/", message.getField(new Symbol()).getValue());
        final Message outerMessage4 = message;
        new ExpectedTestFailure(FieldNotFound.class) {
            protected void execute() throws Throwable {
                outerMessage4.getField(new SymbolSfx()).getValue();
            }
        }.run();
        new ExpectedTestFailure(FieldNotFound.class) {
            protected void execute() throws Throwable {
                outerMessage4.getField(new ExDestination()).getValue();
            }
        }.run();
    }

    public void testAddOneRoute() throws Exception
    {
        OrderRouteManager orMgr = new OrderRouteManager();
        orMgr.addOneRoute("A","B");
        orMgr.addOneRoute("S", "SIGMA");
        assertEquals("A", "B", orMgr.getRoutesMap().get("A"));
        assertEquals("S", "SIGMA", orMgr.getRoutesMap().get("S"));
    }

    /** tests when routes come from properties */
    public void testRouteParsingPropsBased() throws Exception
    {
        OrderRouteManager mgr = new OrderRouteManager();
        mgr.addOneRoute("A", "B");
        mgr.addOneRoute("S", "SIGMA");
        mgr.addOneRoute("N", "M");
        mgr.addOneRoute("NnoSpaceM", "");

        Map<String,String> map = mgr.getRoutesMap();
        assertEquals(3, map.size());
        assertEquals("A", "B", map.get("A"));
        assertEquals("S", "SIGMA", map.get("S"));
        assertEquals("N", "M", map.get("N"));
    }

    public void testUnrecognizedRoute() throws Exception
    {
        (new ExpectedTestFailure(IllegalArgumentException.class, "bob") {
            protected void execute() throws Throwable
            {
                OrderRouteManager routeManager = new OrderRouteManager();
                routeManager.setSeparateSuffix(false);
                routeManager.setRouteMethod("bob");
            }
        }).run();
    }

    /**
     * Creates a basic OrderRouteManager using the {@link OrderRouteManager#FIELD_100_METHOD}
     * # enable class share separate
     * separate.suffix=true
     * order.route.type=field:100
     * # routes
     * 1. N SIGMA
     * 2. IM Milan
     * 3. A B
     */
    public static OrderRouteManager getORMWithOrderRouting() {
        OrderRouteManager orm = new OrderRouteManager();
        orm.setRouteMethod(OrderRouteManager.FIELD_100_METHOD);
        orm.setSeparateSuffix(true);
        HashMap<String, String> routesMap = new HashMap<String, String>();
        routesMap.put("N", "SIGMA");
        routesMap.put("IM", "Milan");
        routesMap.put("A", "B");
        orm.setRoutes(routesMap);
        return orm;
    }
}
