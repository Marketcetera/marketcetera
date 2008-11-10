package org.marketcetera.trade;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.core.MSymbol;
import org.marketcetera.core.LoggerConfiguration;
import org.marketcetera.quickfix.FIXMessageFactory;
import org.marketcetera.quickfix.FIXVersion;
import org.marketcetera.quickfix.FIXDataDictionary;
import org.marketcetera.quickfix.FIXDataDictionaryManager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import org.junit.BeforeClass;

import java.util.Map;
import java.util.HashMap;
import java.util.Date;
import java.util.Iterator;
import java.math.BigDecimal;

import quickfix.field.HandlInst;
import quickfix.field.TransactTime;
import quickfix.field.MsgType;
import quickfix.*;

/* $License$ */
/**
 * Base class for testing various enums based on FIX enum values.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since $Release$
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
        HashMap<String, String> fields = fieldsToMap(msg, dict);
        fields.put("HEADER", fieldsToMap(msg.getHeader(), dict).toString());
        fields.put("TRAILER", fieldsToMap(msg.getTrailer(), dict).toString());
        SLF4JLoggerProxy.error(TypesTestBase.class,  fields.toString());
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
        DestinationID id = new DestinationID("whatever");
        inOrder.setDestinationID(id);
        assertEquals(id, inOrder.getDestinationID());
        inOrder.setDestinationID(null);
        assertEquals(null, inOrder.getDestinationID());
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
                                                OrderID inOrderID,
                                                String inAccount,
                                                Map<String, String> inCustomFields,
                                                BigDecimal inQuantity,
                                                Side inSide,
                                                MSymbol inSymbol) {
        assertEquals(inOrderID, inOrder.getOrderID());
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
                                    DestinationID inDestinationID,
                                    SecurityType inType) {
        assertEquals(inDestinationID, inOrder.getDestinationID());
        assertEquals(inType, inOrder.getSecurityType());
    }

    protected static void assertNROrderValues(NewOrReplaceOrder inOrder,
                                     OrderType inOrderType,
                                     BigDecimal inPrice,
                                     TimeInForce inTIF) {
        assertEquals(inOrderType, inOrder.getOrderType());
        assertEquals(inPrice, inOrder.getPrice());
        assertEquals(inTIF, inOrder.getTimeInForce());
    }

    protected static void assertRelatedOrderValues(RelatedOrder inOrder,
                                                OrderID inOrigOrderID) {
        assertEquals(inOrigOrderID,  inOrder.getOriginalOrderID());
    }

    protected static void assertReportBaseValues(ReportBase inReport,
                                              DestinationID inDestinationID,
                                              OrderID inOrderID,
                                              OrderStatus inOrderStatus,
                                              OrderID inOrigOrderID,
                                              String inText) {
        assertEquals(inDestinationID, inReport.getDestinationID());
        assertEquals(inOrderID, inReport.getOrderID());
        assertEquals(inOrderStatus, inReport.getOrderStatus());
        assertEquals(inOrigOrderID,  inReport.getOriginalOrderID());
        assertEquals(inText, inReport.getText());
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
                                                 Date inSendingTime,
                                                 Side inSide,
                                                 MSymbol inSymbol,
                                                 TimeInForce inTimeInForce,
                                                 Date inTransactTime) {
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
        assertEquals(inSendingTime, inReport.getSendingTime());
        assertEquals(inSide, inReport.getSide());
        assertEquals(inSymbol, inReport.getSymbol());
        assertEquals(inTimeInForce, inReport.getTimeInForce());
        assertEquals(inTransactTime, inReport.getTransactTime());
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

    /**
     * Converts the supplied FieldMap to a map with human readable field
     * names (based on the supplied dictionary) as keys and field values
     * as string values.
     *
     * @param msg The FIX Message.
     * @param inDict The FIX data dictionary.
     *
     * @return The map containing supplied fieldMap's keys & values.
     */
    private static HashMap<String, String> fieldsToMap(FieldMap msg,
                                                       FIXDataDictionary inDict) {
        HashMap<String, String> fields = new HashMap<String, String>();
        Iterator<Field<?>> iterator = msg.iterator();
        while(iterator.hasNext()) {
            Field<?> f = iterator.next();
            String value;
            if(f instanceof StringField) {
                value = ((StringField)f).getValue();
            } else {
                value = String.valueOf(f.getObject());
            }
            String name = null;
            if (inDict != null) {
                name = inDict.getHumanFieldName(f.getTag());
            }
            if(name == null) {
                name = String.valueOf(f.getTag());
            } else {
                name += "(" + f.getTag() + ")";
            }
            fields.put(name,value);
        }
        return fields;
    }

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
