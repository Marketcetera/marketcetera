package org.marketcetera.orderloader.system;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.log.I18NBoundMessage1P;
import org.marketcetera.util.log.I18NBoundMessage2P;
import org.marketcetera.core.LoggerConfiguration;
import org.marketcetera.orderloader.*;
import org.marketcetera.orderloader.Messages;
import static org.marketcetera.orderloader.system.SystemProcessor.*;
import static org.marketcetera.orderloader.system.InstrumentFromRow.*;
import static org.marketcetera.orderloader.system.OptionFromRow.*;
import org.marketcetera.trade.*;
import org.marketcetera.module.ExpectedFailure;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.*;
import java.math.BigDecimal;

/* $License$ */
/**
 * SystemProcessorTest
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$")
public class SystemProcessorTest {
    @BeforeClass
    public static void setupLogger() {
        LoggerConfiguration.logSetup();
    }
    @Test
    public void constructor() throws Exception {
        //Order Processor cannot be null
        new ExpectedFailure<NullPointerException>() {
            protected void run() throws Exception {
                new SystemProcessor(null, new BrokerID("what"));
            }
        };
        //can supply null brokerID ID
        new SystemProcessor(mProcessor, null);
        //Verify initial state
        OrderParserTest.assertProcessor(create(null), 0, 0);
    }
    @Test
    public void missingRequiredHeaders() throws Exception {
        //Order Type
        new ExpectedFailure<OrderParsingException>(
                Messages.MISSING_REQUIRED_FIELD, FIELD_ORDER_TYPE){
            protected void run() throws Exception {
                create(null).initialize(FIELD_QUANTITY, FIELD_SIDE, FIELD_SYMBOL);
            }
        };
        //Quantity
        new ExpectedFailure<OrderParsingException>(
                Messages.MISSING_REQUIRED_FIELD, FIELD_QUANTITY){
            protected void run() throws Exception {
                create(null).initialize(FIELD_ORDER_TYPE, FIELD_SIDE, FIELD_SYMBOL);
            }
        };
        //Side
        new ExpectedFailure<OrderParsingException>(
                Messages.MISSING_REQUIRED_FIELD, FIELD_SIDE){
            protected void run() throws Exception {
                create(null).initialize(FIELD_ORDER_TYPE, FIELD_QUANTITY, FIELD_SYMBOL);
            }
        };
        //Symbol
        new ExpectedFailure<OrderParsingException>(
                Messages.MISSING_REQUIRED_FIELD, FIELD_SYMBOL){
            protected void run() throws Exception {
                create(null).initialize(FIELD_ORDER_TYPE, FIELD_QUANTITY, FIELD_SIDE);
            }
        };
        //verify success when all fields are supplied
        create(null).initialize(FIELD_ORDER_TYPE, FIELD_QUANTITY, FIELD_SIDE, FIELD_SYMBOL);
    }
    @Test
    public void duplicateHeaders() throws Exception {
        //Duplicate order type
        new ExpectedFailure<OrderParsingException>(
                Messages.DUPLICATE_HEADER, FIELD_ORDER_TYPE, 0, 4){
            protected void run() throws Exception {
                create(null).initialize(FIELD_ORDER_TYPE, FIELD_QUANTITY, FIELD_SIDE, FIELD_SYMBOL, FIELD_ORDER_TYPE);
            }
        };
        //Duplicate custom field
        new ExpectedFailure<OrderParsingException>(
                Messages.DUPLICATE_HEADER, "9001", 1, 5){
            protected void run() throws Exception {
                create(null).initialize(FIELD_ORDER_TYPE, "9001", FIELD_QUANTITY, FIELD_SIDE, FIELD_SYMBOL, "9001");
            }
        };
        //Duplicate custom field with same numeric value but different string value
        new ExpectedFailure<OrderParsingException>(
                Messages.DUPLICATE_HEADER, "009001", 1, 5){
            protected void run() throws Exception {
                create(null).initialize(FIELD_ORDER_TYPE, "9001", FIELD_QUANTITY, FIELD_SIDE, FIELD_SYMBOL, "009001");
            }
        };
    }
    @Test
    public void unknownHeader() throws Exception {
        new ExpectedFailure<OrderParsingException>(
                Messages.INVALID_CUSTOM_HEADER, "What", 1){
            protected void run() throws Exception {
                create(null).initialize(FIELD_ORDER_TYPE, "What", FIELD_QUANTITY, FIELD_SIDE, FIELD_SYMBOL);
            }
        };
    }

    /**
     * Tests order parsing with minimal set of fields
     *
     * @throws Exception if there were errors.
     */
    @Test
    public void minimalFields() throws Exception {
        RowProcessor processor = create(null);
        processor.initialize(FIELD_SIDE, FIELD_SYMBOL, FIELD_ORDER_TYPE, FIELD_QUANTITY);
        processor.processOrder(0, Side.Buy.toString(), "ubm", OrderType.Market.toString(), "34.34");
        processor.processOrder(1, Side.Sell.toString(), "nubm", OrderType.Limit.toString(), "12.23");
        OrderParserTest.assertProcessor(processor, 2, 0);
        List<Order> list = mProcessor.getOrders();
        assertEquals(2, list.size());
        TypesTestBase.assertOrderSingle((OrderSingle)list.get(0),
                TypesTestBase.NOT_NULL, Side.Buy, new BigDecimal("34.34"),
                null, null, OrderType.Market, new Equity("ubm"), SecurityType.CommonStock, null,
                null, null, null, null, null);
        TypesTestBase.assertOrderSingle((OrderSingle)list.get(1),
                TypesTestBase.NOT_NULL, Side.Sell, new BigDecimal("12.23"),
                null, null, OrderType.Limit, new Equity("nubm"), SecurityType.CommonStock, null,
                null, null, null, null, null);
    }

    /**
     * Test custom fields.
     *
     * @throws Exception if there were errors.
     */
    @Test
    public void customFields() throws Exception {
        RowProcessor processor = create(null);
        processor.initialize(FIELD_SIDE, FIELD_SYMBOL, FIELD_ORDER_TYPE,
                FIELD_QUANTITY, "4001", "5001");
        processor.processOrder(0, Side.SellShort.toString(), "zoog",
                OrderType.Market.toString(), "1", "yes", "no");
        processor.processOrder(1, Side.SellShort.toString(), "zoo",
                OrderType.Market.toString(), "10", "", "no");
        processor.processOrder(2, Side.Sell.toString(), "moog",
                OrderType.Limit.toString(), "10", "moo", null);
        OrderParserTest.assertProcessor(processor, 3, 0);
        List<Order> list = mProcessor.getOrders();
        assertEquals(3, list.size());
        Map<String,String> map = new HashMap<String,String>();
        map.put("4001", "yes");
        map.put("5001", "no");

        TypesTestBase.assertOrderSingle((OrderSingle)list.get(0),
                TypesTestBase.NOT_NULL, Side.SellShort, BigDecimal.ONE,
                null, null, OrderType.Market, new Equity("zoog"), SecurityType.CommonStock, null,
                null, null, null, null, map);
        map.clear();
        map.put("4001", "");
        map.put("5001", "no");
        TypesTestBase.assertOrderSingle((OrderSingle)list.get(1),
                TypesTestBase.NOT_NULL, Side.SellShort, BigDecimal.TEN,
                null, null, OrderType.Market, new Equity("zoo"), SecurityType.CommonStock, null,
                null, null, null, null, map);
        map.clear();
        map.put("4001", "moo");
        map.put("5001", null);
        TypesTestBase.assertOrderSingle((OrderSingle)list.get(2),
                TypesTestBase.NOT_NULL, Side.Sell, BigDecimal.TEN,
                null, null, OrderType.Limit, new Equity("moog"), SecurityType.CommonStock, null,
                null, null, null, null, map);
    }
    @Test
    public void allFields() throws Exception {
        BrokerID brokerID = new BrokerID("broke");
        RowProcessor processor = create(brokerID);
        processor.initialize(FIELD_ACCOUNT, FIELD_ORDER_CAPACITY,
                FIELD_ORDER_TYPE, FIELD_POSITION_EFFECT, "666",
                FIELD_PRICE, FIELD_QUANTITY, FIELD_SECURITY_TYPE,
                FIELD_SIDE, FIELD_SYMBOL, FIELD_TIME_IN_FORCE,
                FIELD_EXPIRY, FIELD_STRIKE_PRICE,
                FIELD_OPTION_TYPE, "2112");
        processor.processOrder(1, "mine", "",
                OrderType.Market.toString(), "", "beast", "", "3.33",
                SecurityType.CommonStock.toString(),
                Side.SellShortExempt.toString(), "vsft",
                TimeInForce.Day.toString(), null, null, null, "steel");
        //fails due to incorrect price value
        String[] failRow1 = {"mine", "",
                OrderType.Market.toString(), "", "beast", "nice", "3.33",
                SecurityType.CommonStock.toString(),
                Side.SellShortExempt.toString(), "vsft",
                TimeInForce.Day.toString(), null, null, null, "steel"};
        processor.processOrder(2, failRow1);
        processor.processOrder(3, "yours", OrderCapacity.Individual.toString(),
                OrderType.Limit.toString(), PositionEffect.Close.toString(),
                "number", "22.2", "9.99",
                SecurityType.Option.toString(),
                Side.Buy.toString(), "soft",
                TimeInForce.FillOrKill.toString(), "20101010", "12.34", "Call", "of");
        //fails due to incorrect OrderCapacity Value
        String[] failRow2 = {"yours", OrderCapacity.Unknown.toString(),
                OrderType.Limit.toString(), PositionEffect.Close.toString(),
                "number", "22.2", "9.99",
                SecurityType.Option.toString(),
                Side.Buy.toString(), "soft",
                TimeInForce.Day.toString(), "20101010", "12.34", "Call", "of"};
        processor.processOrder(4, failRow2);
        //fails due to incorrect OptionType Value
        String[] failRow3 = {"yours", OrderCapacity.Agency.toString(),
                OrderType.Limit.toString(), PositionEffect.Close.toString(),
                "number", "22.2", "9.99",
                SecurityType.Option.toString(),
                Side.Buy.toString(), "soft",
                TimeInForce.Day.toString(), "20101010", "12.34", "Puff", "of"};
        processor.processOrder(5, failRow3);
        //fails due to incorrect strike price Value
        String[] failRow4 = {"yours", OrderCapacity.Agency.toString(),
                OrderType.Limit.toString(), PositionEffect.Close.toString(),
                "number", "22.2", "9.99",
                SecurityType.Option.toString(),
                Side.Buy.toString(), "soft",
                TimeInForce.Day.toString(), "20101010", "nostrike", "Call", "of"};
        processor.processOrder(6, failRow4);
        OrderParserTest.assertProcessor(processor, 2, 4);
        //Verify successful orders
        List<Order> list = mProcessor.getOrders();
        assertEquals(2, list.size());
        Map<String,String> map = new HashMap<String,String>();
        map.put("666", "beast");
        map.put("2112", "steel");
        TypesTestBase.assertOrderSingle((OrderSingle)list.get(0),
                TypesTestBase.NOT_NULL, Side.SellShortExempt,
                new BigDecimal("3.33"), null, TimeInForce.Day, OrderType.Market,
                new Equity("vsft"),
                SecurityType.CommonStock, "mine", null, null, null, brokerID, map);
        map.clear();
        map.put("666", "number");
        map.put("2112", "of");
        TypesTestBase.assertOrderSingle((OrderSingle)list.get(1),
                TypesTestBase.NOT_NULL, Side.Buy, new BigDecimal("9.99"),
                new BigDecimal("22.2"), TimeInForce.FillOrKill, OrderType.Limit,
                new Option("soft", "20101010", new BigDecimal("12.34"), OptionType.Call),
                SecurityType.Option, "yours", null, OrderCapacity.Individual,
                PositionEffect.Close, brokerID, map);
        //Verify failures.
        List<FailedOrderInfo> failed = processor.getFailedOrders();
        assertEquals(4, failed.size());
        FailedOrderInfo info = failed.get(0);
        assertEquals(2, info.getIndex());
        assertArrayEquals(failRow1, info.getRow());
        assertEquals(new I18NBoundMessage1P(Messages.INVALID_PRICE_VALUE,
                "nice"), ((OrderParsingException)info.getException()).
                getI18NBoundMessage());

        info = failed.get(1);
        assertEquals(4, info.getIndex());
        assertArrayEquals(failRow2, info.getRow());
        //Valid Values
        Set<OrderCapacity> validValues = EnumSet.allOf(OrderCapacity.class);
        validValues.remove(OrderCapacity.Unknown);
        assertEquals(new I18NBoundMessage2P(Messages.INVALID_ORDER_CAPACITY,
                OrderCapacity.Unknown.toString(), validValues.toString()), 
                ((OrderParsingException)info.getException()).
                getI18NBoundMessage());

        info = failed.get(2);
        assertEquals(5, info.getIndex());
        assertArrayEquals(failRow3, info.getRow());
        Set<OptionType> validOpTypes = EnumSet.allOf(OptionType.class);
        validOpTypes.remove(OptionType.Unknown);
        assertEquals(new I18NBoundMessage2P(Messages.INVALID_OPTION_TYPE,
                "Puff", validOpTypes.toString()),
                ((OrderParsingException)info.getException()).
                getI18NBoundMessage());

        info = failed.get(3);
        assertEquals(6, info.getIndex());
        assertArrayEquals(failRow4, info.getRow());
        assertEquals(new I18NBoundMessage1P(Messages.INVALID_STRIKE_PRICE_VALUE,
                "nostrike"),
                ((OrderParsingException)info.getException()).
                getI18NBoundMessage());
    }

    private RowProcessor create(BrokerID inBrokerID) throws Exception {
        mProcessor.getOrders().clear();
        return new SystemProcessor(mProcessor, inBrokerID);
    }
    private final MockOrderProcessor mProcessor = new MockOrderProcessor();
}
