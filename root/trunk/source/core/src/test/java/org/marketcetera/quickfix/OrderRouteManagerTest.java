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
import java.util.Properties;
import java.util.prefs.BackingStoreException;

/**
 * @author Graham Miller
 * @version $Id$
 */
@ClassVersion("$Id$")
public class OrderRouteManagerTest extends TestCase
{
    public OrderRouteManagerTest(String name)
    {
        super(name);
    }

    public static Test suite() {
        return new MarketceteraTestSuite(OrderRouteManagerTest.class);
    }
        
    public void testModifyOrderSeparateSuffix() throws BackingStoreException, MarketceteraException, FieldNotFound
    {
        OrderRouteManager routeManager = new OrderRouteManager();
        ConfigData data = getPropsWithOrderRouting();
        routeManager.init(data);

        Message message =FIXMessageUtil.newLimitOrder(
            new InternalID(""+12345),
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


        message = FIXMessageUtil.newLimitOrder(
            new InternalID(""+12346),
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

        message = FIXMessageUtil.newLimitOrder(
            new InternalID(""+12347),
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
        Properties props = new Properties();

        props.put(OrderRouteManager.ORDER_ROUTE_TYPE, OrderRouteManager.FIELD_100_METHOD);
        props.put(OrderRouteManager.SEPARATE_SUFFIX_KEY, false);
        props.put(OrderRouteManager.ROUTES_NODE_KEY+".1", "N SIGMA");

        PropertiesConfigData data = new PropertiesConfigData(props);
        routeManager.init(data);

        Message message =FIXMessageUtil.newLimitOrder(
            new InternalID(""+12345),
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


        message =FIXMessageUtil.newLimitOrder(
            new InternalID(""+12346),
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

        message = FIXMessageUtil.newLimitOrder(
            new InternalID(""+12347),
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

        message = FIXMessageUtil.newLimitOrder(
            new InternalID(""+12347),
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
        HashMap<String,String> map = new HashMap<String, String>();

        orMgr.addOneRoute("A B", map);
        orMgr.addOneRoute("S SIGMA", map);
        assertEquals("A", "B", map.get("A"));
        assertEquals("S", "SIGMA", map.get("S"));
    }

    /** tests when routes come from properties */
    public void testRouteParsingPropsBased() throws Exception
    {
        Properties props = new Properties();
        props.setProperty(OrderRouteManager.ROUTES_NODE_KEY+".1", "A B");
        props.setProperty(OrderRouteManager.ROUTES_NODE_KEY+".2", "S SIGMA");
        props.setProperty(OrderRouteManager.ROUTES_NODE_KEY+".3", "N M");
        props.setProperty(OrderRouteManager.ROUTES_NODE_KEY+".4", "NnoSpaceM");

        OrderRouteManager mgr = new OrderRouteManager();
        mgr.init(new PropertiesConfigData(props));

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
                Properties props = new Properties();

                props.put(OrderRouteManager.ORDER_ROUTE_TYPE, "bob");
                props.put(OrderRouteManager.SEPARATE_SUFFIX_KEY, false);

                routeManager.init(new PropertiesConfigData(props));
            }
        }).run();
    }

    /**
     * Creates a basic route using the {@link OrderRouteManager.FIELD_100_METHOD}
     * # enable class share separate
     * separate.suffix=true
     * order.route.type=field:100
     * # routes
     * routes.1=N SIGMA
     * routes.2=IM Milan
     * routes.3=A B
     */
    public static ConfigData getPropsWithOrderRouting() {
        Properties props = new Properties();

        props.put(OrderRouteManager.ORDER_ROUTE_TYPE, OrderRouteManager.FIELD_100_METHOD);
        props.put(OrderRouteManager.SEPARATE_SUFFIX_KEY, true);
        props.put(OrderRouteManager.ROUTES_NODE_KEY + ".1", "N SIGMA");
        props.put(OrderRouteManager.ROUTES_NODE_KEY + ".2", "IM Milan");
        props.put(OrderRouteManager.ROUTES_NODE_KEY + ".3", "A B");

        return new PropertiesConfigData(props);
    }
}
