package org.marketcetera.trade;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.core.LoggerConfiguration;
import org.marketcetera.quickfix.*;
import org.marketcetera.event.HasFIXMessage;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import org.junit.BeforeClass;

import java.util.Map;
import java.util.HashMap;
import java.util.Date;
import java.math.BigDecimal;

import quickfix.field.HandlInst;
import quickfix.field.TransactTime;
import quickfix.field.MsgType;
import quickfix.field.ClOrdID;
import quickfix.*;

/* $License$ */
/**
 * Base class for testing various enums based on FIX enum values.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class TypesTestBase {
    /**
     * Initialize Logger and system FIX dictionary.
     *
     * @throws Exception if there were errors
     */
    @BeforeClass
    public static void before() throws Exception {
        LoggerConfiguration.logSetup();
        FIXDataDictionaryManager.initialize(FIXVersion.FIX_SYSTEM,
                FIXVersion.FIX_SYSTEM.getDataDictionaryURL());
    }

    /**
     * Logs the fields, header and trailer of the provided message, with
     * human readable field names to the log.
     *
     * @param msg The FIX Message.
     *
     * @throws FieldNotFound if there were errors.
     */
    public static void logFields(Message msg) throws FieldNotFound {
        FIXVersion v = FIXVersion.getFIXVersion(msg);
        FIXDataDictionary dict = FIXDataDictionaryManager.getFIXDataDictionary(v);
        String fields = FIXMessageUtil.toPrettyString(msg, dict);
        SLF4JLoggerProxy.debug(TypesTestBase.class,  fields);
    }

    public static void assertOrderFIXEquals(FIXOrder inOrder1,
                                             FIXOrder inOrder2) {
        assertOrderFIXEquals(inOrder1, inOrder2, false);
    }
    public static void assertOrderFIXEquals(FIXOrder inOrder1,
                                            FIXOrder inOrder2,
                                            boolean inIgnoreClOrdID) {
        if (checkForNull(inOrder1, inOrder2)) return;
        assertOrderEquals(inOrder1,  inOrder2);
        if(inIgnoreClOrdID) {
            //Get the two maps, remove the field and then compare
            Map<Integer, String> map1 = new HashMap<Integer, String>(inOrder1.getFields());
            map1.remove(ClOrdID.FIELD);
            Map<Integer, String> map2 = new HashMap<Integer, String>(inOrder2.getFields());
            map2.remove(ClOrdID.FIELD);
            assertEquals(map1, map2);
        } else {
            assertEquals(inOrder1.getFields(), inOrder2.getFields());
        }
    }

    public static void assertOrderSingleEquals(OrderSingle inOrder1,
                                                OrderSingle inOrder2) {
        assertOrderSingleEquals(inOrder1, inOrder2, false);
    }
    public static void assertOrderSingleEquals(OrderSingle inOrder1,
                                               OrderSingle inOrder2,
                                               boolean inIgnoreOrderID) {
        if (checkForNull(inOrder1, inOrder2)) return;
        assertOrderBaseEquals(inOrder1, inOrder2, inIgnoreOrderID);
        assertNROrderEquals(inOrder1, inOrder2);
    }

    public static void assertOrderReplaceEquals(OrderReplace inOrder1,
                                                 OrderReplace inOrder2) {
        assertOrderReplaceEquals(inOrder1, inOrder2, false);
    }
    public static void assertOrderReplaceEquals(OrderReplace inOrder1,
                                                OrderReplace inOrder2,
                                                boolean inIgnoreOrderID) {
        if (checkForNull(inOrder1, inOrder2)) return;
        assertRelatedOrderEquals(inOrder1, inOrder2, inIgnoreOrderID);
        assertNROrderEquals(inOrder1, inOrder2);
    }

    public static void assertOrderCancelEquals(OrderCancel inOrder1,
                                                OrderCancel inOrder2) {
        assertOrderCancelEquals(inOrder1, inOrder2, false);
    }
    public static void assertOrderCancelEquals(OrderCancel inOrder1,
                                               OrderCancel inOrder2,
                                               boolean inIgnoreOrderID) {
        if (checkForNull(inOrder1, inOrder2)) return;
        assertRelatedOrderEquals(inOrder1, inOrder2, inIgnoreOrderID);
    }
    public static void assertOrderSuggestionEquals(OrderSingleSuggestion inSuggest1,
                                                   OrderSingleSuggestion inSuggest2) {
        assertOrderSuggestionEquals(inSuggest1, inSuggest2, false);
    }
    public static void assertOrderSuggestionEquals(OrderSingleSuggestion inSuggest1,
                                                   OrderSingleSuggestion inSuggest2,
                                                   boolean inIgnoreOrderID) {
        if (checkForNull(inSuggest1, inSuggest2)) return;
        assertSuggestionEquals(inSuggest1,  inSuggest2);
        assertOrderSingleEquals(inSuggest1.getOrder(),
                inSuggest2.getOrder(), inIgnoreOrderID);
    }

    public static void assertExecReportEquals(ExecutionReport inReport1,
                                               ExecutionReport inReport2) {
        if (checkForNull(inReport1, inReport2)) return;
        assertReportBaseEquals(inReport1, inReport2);
        assertEquals(inReport1.getAccount(), inReport2.getAccount());
        assertEquals(inReport1.getAveragePrice(), inReport2.getAveragePrice());
        assertEquals(inReport1.getCumulativeQuantity(), inReport2.getCumulativeQuantity());
        assertEquals(inReport1.getExecutionID(), inReport2.getExecutionID());
        assertEquals(inReport1.getExecutionType(), inReport2.getExecutionType());
        assertEquals(inReport1.getLastMarket(), inReport2.getLastMarket());
        assertEquals(inReport1.getLastPrice(), inReport2.getLastPrice());
        assertEquals(inReport1.getLastQuantity(), inReport2.getLastQuantity());
        assertEquals(inReport1.getLeavesQuantity(), inReport2.getLeavesQuantity());
        assertEquals(inReport1.getOrderQuantity(), inReport2.getOrderQuantity());
        assertEquals(inReport1.getOrderType(), inReport2.getOrderType());
        assertEquals(inReport1.getSide(), inReport2.getSide());
        assertEquals(inReport1.getSymbol(), inReport2.getSymbol());
        assertEquals(inReport1.getTimeInForce(), inReport2.getTimeInForce());
        assertEquals(inReport1.getTransactTime(), inReport2.getTransactTime());
        assertEquals(inReport1.isCancelable(), inReport2.isCancelable());
    }

    public static void assertCancelRejectEquals(OrderCancelReject inReport1,
                                               OrderCancelReject inReport2) {
        if (checkForNull(inReport1, inReport1)) return;
        assertReportBaseEquals(inReport1, inReport2);
    }

    public static void assertFIXEquals(ReportBase inReport1, ReportBase inReport2) {
        if (checkForNull(inReport1, inReport1)) return;
        HasFIXMessage fix1 = (HasFIXMessage) inReport1;
        HasFIXMessage fix2 = (HasFIXMessage) inReport2;
        assertEquals(fix1.getMessage().toString(), fix2.getMessage().toString());
    }

    protected static void assertSuggestionEquals(Suggestion inSuggest1,
                                                 Suggestion inSuggest2) {
        assertEquals(inSuggest1.getIdentifier(), inSuggest2.getIdentifier());
        assertEquals(inSuggest1.getScore(), inSuggest2.getScore());
    }

    protected static void assertOrderEquals(Order inOrder1, Order inOrder2) {
        assertEquals(inOrder1.getBrokerID(), inOrder2.getBrokerID());
        assertEquals(inOrder1.getSecurityType(), inOrder2.getSecurityType());
    }

    protected static void assertOrderBaseEquals(OrderBase inOrder1,
                                              OrderBase inOrder2) {
        assertOrderBaseEquals(inOrder1, inOrder2, false);
    }
    protected static void assertOrderBaseEquals(OrderBase inOrder1,
                                                OrderBase inOrder2,
                                                boolean inIgnoreOrderID) {
        assertOrderEquals(inOrder1, inOrder2);
        assertEquals(inOrder1.getAccount(), inOrder2.getAccount());
        assertEquals(inOrder1.getCustomFields(), inOrder2.getCustomFields());
        assertEquals(inOrder1.getBrokerID(), inOrder2.getBrokerID());
        if (!inIgnoreOrderID) {
        assertEquals(inOrder1.getOrderID(), inOrder2.getOrderID());
        }
        assertEquals(inOrder1.getQuantity(), inOrder2.getQuantity());
        assertEquals(inOrder1.getSecurityType(), inOrder2.getSecurityType());
        assertEquals(inOrder1.getSide(), inOrder2.getSide());
        assertEquals(inOrder1.getSymbol(), inOrder2.getSymbol());
    }

    protected static void assertNROrderEquals(NewOrReplaceOrder inOrder1,
                                            NewOrReplaceOrder inOrder2) {
        assertEquals(inOrder1.getOrderType(), inOrder2.getOrderType());
        assertEquals(inOrder1.getPrice(), inOrder2.getPrice());
        assertEquals(inOrder1.getTimeInForce(), inOrder2.getTimeInForce());
    }

    protected static void assertRelatedOrderEquals(RelatedOrder inOrder1,
                                                 RelatedOrder inOrder2) {
        assertRelatedOrderEquals(inOrder1,  inOrder2,  false);
    }
    protected static void assertRelatedOrderEquals(RelatedOrder inOrder1,
                                                   RelatedOrder inOrder2,
                                                   boolean inIgnoreOrderID) {
        assertOrderBaseEquals(inOrder1, inOrder2, inIgnoreOrderID);
        assertEquals(inOrder1.getOriginalOrderID(),
                inOrder2.getOriginalOrderID());
        assertEquals(inOrder1.getBrokerOrderID(),
                inOrder2.getBrokerOrderID());
    }

    protected static void assertReportBaseEquals(ReportBase inReport1,
                                               ReportBase inReport2) {
        assertEquals(inReport1.getBrokerID(), inReport2.getBrokerID());
        assertEquals(inReport1.getOrderID(), inReport2.getOrderID());
        assertEquals(inReport1.getOrderStatus(), inReport2.getOrderStatus());
        assertEquals(inReport1.getOriginalOrderID(), inReport2.getOriginalOrderID());
        assertEquals(inReport1.getSendingTime(), inReport2.getSendingTime());
        assertEquals(inReport1.getText(), inReport2.getText());
        assertEquals(inReport1.getBrokerOrderID(),
                inReport2.getBrokerOrderID());
        assertEquals(inReport1.getOriginator(), inReport2.getOriginator());
        assertEquals(inReport1.getActorID(), inReport2.getActorID());
        assertEquals(inReport1.getViewerID(), inReport2.getViewerID());
    }

    /**
     * Returns the message factory for creating system FIX messages.
     *
     * @return the message factory creating system FIX messages.
     */
    protected static FIXMessageFactory getSystemMessageFactory() {
        return FIXVersion.FIX_SYSTEM.getMessageFactory();
    }

    /**
     * Returns the data dictionary for creating system FIX messages.
     *
     * @return the data dictionary creating system FIX messages.
     */
    protected static FIXDataDictionary getSystemMessageDictionary() {
        return FIXDataDictionaryManager.getFIXDataDictionary(FIXVersion.FIX_SYSTEM);
    }

    protected static void checkOrderSetters(Order inOrder) {
        BrokerID id = new BrokerID("whatever");
        inOrder.setBrokerID(id);
        assertEquals(id, inOrder.getBrokerID());
        inOrder.setBrokerID(null);
        assertEquals(null, inOrder.getBrokerID());
    }

    protected static void checkOrderBaseSetters(OrderBase inOrder) {
        OrderID orderID = new OrderID("ord-id");
        inOrder.setOrderID(orderID);
        assertEquals(orderID, inOrder.getOrderID());
        inOrder.setOrderID(null);
        assertEquals(null, inOrder.getOrderID());

        String account = "my account";
        inOrder.setAccount(account);
        assertEquals(account, inOrder.getAccount());
        inOrder.setAccount(null);
        assertEquals(null, inOrder.getAccount());

        Map<String,String> custom = new HashMap<String, String>();
        custom.put("yes","no");
        custom.put("true","false");
        inOrder.setCustomFields(custom);
        assertEquals(custom, inOrder.getCustomFields());
        assertNotSame(custom, inOrder.getCustomFields());
        // Update the map supplied to set() and verify that the returned
        // map is not modified.
        @SuppressWarnings("unchecked")
        HashMap<String,String>oldValue = (HashMap<String,String>)
                ((HashMap<String,String>)custom).clone();
        custom.put("new","field");
        assertEquals(oldValue, inOrder.getCustomFields());
        // Update the map received from get() and verify that modifying
        // it does not change its value.
        custom = inOrder.getCustomFields();
        custom.put("new","field");
        assertEquals(oldValue, inOrder.getCustomFields());
        //empty map
        custom = new HashMap<String, String>();
        inOrder.setCustomFields(custom);
        assertEquals(custom, inOrder.getCustomFields());
        //null map
        inOrder.setCustomFields(null);
        assertEquals(null, inOrder.getCustomFields());

        BigDecimal qty = new BigDecimal("123456.7890");
        inOrder.setQuantity(qty);
        assertEquals(qty, inOrder.getQuantity());
        inOrder.setQuantity(null);
        assertEquals(null, inOrder.getQuantity());

        MSymbol symbol = new MSymbol("IBM");
        inOrder.setSymbol(symbol);
        assertEquals(symbol, inOrder.getSymbol());
        assertEquals(null, inOrder.getSecurityType());
        symbol = new MSymbol("IBM", SecurityType.CommonStock);
        inOrder.setSymbol(symbol);
        assertEquals(symbol, inOrder.getSymbol());
        assertEquals(SecurityType.CommonStock, inOrder.getSecurityType());
        inOrder.setSymbol(null);
        assertEquals(null, inOrder.getSymbol());
        assertEquals(null, inOrder.getSecurityType());
    }

    protected static void checkRelatedOrderSetters(RelatedOrder inOrder) {
        inOrder.setOriginalOrderID(null);
        assertEquals(null, inOrder.getOriginalOrderID());
        OrderID orderID = new OrderID("blah");
        inOrder.setOriginalOrderID(orderID);
        assertEquals(orderID, inOrder.getOriginalOrderID());
        inOrder.setOriginalOrderID(null);
        assertEquals(null, inOrder.getOriginalOrderID());
    }

    protected static void checkNRSetters(NewOrReplaceOrder inOrder) {
        inOrder.setOrderType(OrderType.Limit);
        assertEquals(OrderType.Limit, inOrder.getOrderType());
        inOrder.setOrderType(null);
        assertEquals(null, inOrder.getOrderType());

        BigDecimal price = new BigDecimal("9876.12345");
        inOrder.setPrice(price);
        assertEquals(price, inOrder.getPrice());
        inOrder.setPrice(null);
        assertEquals(null, inOrder.getPrice());

        inOrder.setTimeInForce(TimeInForce.FillOrKill);
        assertEquals(TimeInForce.FillOrKill, inOrder.getTimeInForce());
        inOrder.setTimeInForce(null);
        assertEquals(null, inOrder.getTimeInForce());

        inOrder.setOrderCapacity(OrderCapacity.Agency);
        assertEquals(OrderCapacity.Agency, inOrder.getOrderCapacity());
        inOrder.setOrderCapacity(null);
        assertEquals(null, inOrder.getOrderCapacity());

        inOrder.setPositionEffect(PositionEffect.Open);
        assertEquals(PositionEffect.Open, inOrder.getPositionEffect());
        inOrder.setPositionEffect(null);
        assertEquals(null, inOrder.getPositionEffect());
    }

    protected static void checkSuggestionSetters(Suggestion inSuggestion) {
        inSuggestion.setIdentifier(null);
        assertEquals(null, inSuggestion.getIdentifier());
        String ident = "what?";
        inSuggestion.setIdentifier(ident);
        assertEquals(ident, inSuggestion.getIdentifier());
        inSuggestion.setIdentifier(null);
        assertEquals(null, inSuggestion.getIdentifier());

        inSuggestion.setScore(null);
        assertEquals(null, inSuggestion.getScore());
        BigDecimal score = new BigDecimal("3435.34");
        inSuggestion.setScore(score);
        assertEquals(score, inSuggestion.getScore());
        inSuggestion.setScore(null);
        assertEquals(null, inSuggestion.getScore());
    }

    protected static void assertOrderBaseValues(OrderBase inOrder,
                                                //Supply NOT_NULL value to test if it's not null
                                                OrderID inOrderID,
                                                String inAccount,
                                                Map<String, String> inCustomFields,
                                                BigDecimal inQuantity,
                                                Side inSide,
                                                MSymbol inSymbol) {
        if (NOT_NULL == inOrderID) {
            assertNotNull(inOrder.getOrderID());
        } else {
            assertEquals(inOrderID, inOrder.getOrderID());
        }
        assertEquals(inAccount, inOrder.getAccount());
        Map<String, String> map = inOrder.getCustomFields();
        if (map != null) {
            for(int ignoreField: MAP_COMPARE_IGNORE_FIELDS) {
                map.remove(String.valueOf(ignoreField));
            }
            if(map.isEmpty()) {
                map = null;
            }
        }
        assertEquals(inCustomFields, map);
        assertEquals(inQuantity, inOrder.getQuantity());
        assertEquals(inSide, inOrder.getSide());
        assertEquals(inSymbol, inOrder.getSymbol());
    }

    protected static void assertOrderValues(Order inOrder,
                                    BrokerID inBrokerID,
                                    SecurityType inType) {
        assertEquals(inBrokerID, inOrder.getBrokerID());
        assertEquals(inType, inOrder.getSecurityType());
    }

    protected static void assertNROrderValues(NewOrReplaceOrder inOrder,
                                     OrderType inOrderType,
                                     BigDecimal inPrice,
                                              TimeInForce inTIF,
                                              OrderCapacity inOrderCapacity,
                                              PositionEffect inPositionEffect) {
        assertEquals(inOrderType, inOrder.getOrderType());
        assertEquals(inPrice, inOrder.getPrice());
        assertEquals(inTIF, inOrder.getTimeInForce());
        assertEquals(inOrderCapacity, inOrder.getOrderCapacity());
        assertEquals(inPositionEffect, inOrder.getPositionEffect());
    }

    protected static void assertRelatedOrderValues(RelatedOrder inOrder,
                                                   OrderID inOrigOrderID,
                                                   String inBrokerOrderID) {
        assertEquals(inOrigOrderID,  inOrder.getOriginalOrderID());
        assertEquals(inBrokerOrderID,  inOrder.getBrokerOrderID());
    }

    protected static void assertReportBaseValues(ReportBase inReport,
                                                 BrokerID inBrokerID,
                                                 OrderID inOrderID,
                                                 OrderStatus inOrderStatus,
                                                 OrderID inOrigOrderID,
                                                 Date inSendingTime,
                                                 String inText,
                                                 String inBrokerOrderID,
                                                 Originator inOriginator,
                                                 UserID inActorID,
                                                 UserID inViewerID) {
        assertEquals(inBrokerID, inReport.getBrokerID());
        assertEquals(inOrderID, inReport.getOrderID());
        assertEquals(inOrderStatus, inReport.getOrderStatus());
        assertEquals(inOrigOrderID,  inReport.getOriginalOrderID());
        assertEquals(inSendingTime,  inReport.getSendingTime());
        assertEquals(inText, inReport.getText());
        assertEquals(inBrokerOrderID, inReport.getBrokerOrderID());
        assertEquals(inOriginator, inReport.getOriginator());
        assertEquals(inActorID, inReport.getActorID());
        assertEquals(inViewerID, inReport.getViewerID());
    }

    protected static void assertExecReportValues(ExecutionReport inReport,
                                                 String inAccount,
                                                 BigDecimal inAvgPrice,
                                                 BigDecimal inCumQty,
                                                 String inExecID,
                                                 ExecutionType inExecType,
                                                 String inLastMarket,
                                                 BigDecimal inLastPrice,
                                                 BigDecimal inLastShares,
                                                 BigDecimal inLeavesQty,
                                                 BigDecimal inOrderQty,
                                                 OrderType inOrderType,
                                                 Side inSide,
                                                 MSymbol inSymbol,
                                                 TimeInForce inTimeInForce,
                                                 Date inTransactTime,
                                                 OrderCapacity inOrderCapacity,
                                                 PositionEffect inPositionEffect,
                                                 boolean inIsCancelable) {
        assertEquals(inAccount, inReport.getAccount());
        assertEquals(inAvgPrice, inReport.getAveragePrice());
        assertEquals(inCumQty, inReport.getCumulativeQuantity());
        assertEquals(inExecID, inReport.getExecutionID());
        assertEquals(inExecType, inReport.getExecutionType());
        assertEquals(inLastMarket, inReport.getLastMarket());
        assertEquals(inLastPrice, inReport.getLastPrice());
        assertEquals(inLastShares, inReport.getLastQuantity());
        assertEquals(inLeavesQty, inReport.getLeavesQuantity());
        assertEquals(inOrderQty, inReport.getOrderQuantity());
        assertEquals(inOrderType, inReport.getOrderType());
        assertEquals(inSide, inReport.getSide());
        assertEquals(inSymbol, inReport.getSymbol());
        assertEquals(inTimeInForce, inReport.getTimeInForce());
        assertEquals(inTransactTime, inReport.getTransactTime());
        assertEquals(inOrderCapacity, inReport.getOrderCapacity());
        assertEquals(inPositionEffect, inReport.getPositionEffect());
        assertEquals(inIsCancelable, inReport.isCancelable());
    }

    protected static void assertSuggestionValues(Suggestion inSuggestion,
                                                 Object inIdentifier,
                                                 Object inScore) {
        assertEquals(inIdentifier, inSuggestion.getIdentifier());
        assertEquals(inScore, inSuggestion.getScore());
    }

    protected static Message createEmptyExecReport() {
        return getSystemMessageFactory().
                getUnderlyingMessageFactory().create(
                FIXDataDictionary.FIX_4_2_BEGIN_STRING,
                MsgType.EXECUTION_REPORT);
    }

    private static boolean checkForNull(Object inObj1, Object inObj2) {
        if(inObj1 == null ^ inObj2 == null) {
            fail(new StringBuilder().append("expected<").append(inObj1).
                    append(">, actual<").append(inObj2).append(">").toString());
        }
        return inObj1 == null;
    }

    public static void assertOrderSingle(OrderSingle inOrder,
                                         OrderID inOrderID,
                                         Side inSide,
                                         BigDecimal inQty,
                                         BigDecimal inPrice,
                                         TimeInForce inTimeInForce,
                                         OrderType inOrderType,
                                         MSymbol inSymbol,
                                         SecurityType inSecurityType,
                                         String inAccount,
                                         OrderCapacity inOrderCapacity,
                                         PositionEffect inPositionEffect,
                                         BrokerID inBrokerID,
                                         Map<String, String> inCustomFields) {
        assertNotNull(inOrder);
        assertOrderValues(inOrder, inBrokerID, inSecurityType);
        assertOrderBaseValues(inOrder, inOrderID, inAccount, inCustomFields, inQty,
                inSide, inSymbol);
        assertNROrderValues(inOrder, inOrderType, inPrice,
                inTimeInForce, inOrderCapacity,
                inPositionEffect);
    }

    public static void assertOrderCancel(OrderCancel inOrder,
                                         OrderID inOrderID,
                                         OrderID inOriginalOrderID,
                                         Side inSide,
                                         MSymbol inSymbol,
                                         SecurityType inSecurityType,
                                         BigDecimal inQty,
                                         String inDestOrderID,
                                         String inAccount,
                                         BrokerID inBrokerID,
                                         Map<String, String> inCustomFields) {
        assertNotNull(inOrder);
        assertOrderValues(inOrder, inBrokerID, inSecurityType);
        assertOrderBaseValues(inOrder, inOrderID, inAccount,
                inCustomFields, inQty, inSide, inSymbol);
        assertRelatedOrderValues(inOrder, inOriginalOrderID, inDestOrderID);
    }

    public static void assertOrderReplace(OrderReplace inOrder,
                                          OrderID inOrderID,
                                          OrderID inOrigOrderID,
                                          String inDestOrderID,
                                          OrderType inOrderType,
                                          Side inSide,
                                          BigDecimal inQty,
                                          BigDecimal inPrice,
                                          MSymbol inSymbol,
                                          SecurityType inSecurityType,
                                          TimeInForce inTif,
                                          String inAccount,
                                          BrokerID inBrokerID,
                                          PositionEffect inPositionEffect,
                                          OrderCapacity inOrderCapacity,
                                          Map<String, String> inCustomFields) {
        assertOrderValues(inOrder, inBrokerID, inSecurityType);
        assertOrderBaseValues(inOrder, inOrderID, inAccount, inCustomFields, inQty,
                inSide, inSymbol);
        assertNROrderValues(inOrder, inOrderType,
                inPrice,
                inTif, inOrderCapacity,
                inPositionEffect);
        assertRelatedOrderValues(inOrder, inOrigOrderID, inDestOrderID);
    }

    /**
     * Sentinel OrderID value to indicate to the assert methods
     * that it should only check if the orderID is not null.
     */
    public static final OrderID NOT_NULL = new OrderID("notnull");

    /**
     * The factory instance that can be used for testing by all subclasses.
     */
    protected static final Factory sFactory = Factory.getInstance();
    /**
     * Set of fields to ignore when comparing maps that contain FIX values.
     */
    protected static final int[] MAP_COMPARE_IGNORE_FIELDS = new int[]{
            HandlInst.FIELD, TransactTime.FIELD};
}
