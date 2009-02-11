package org.marketcetera.orderloader.fix;

import org.marketcetera.util.unicode.SignatureCharset;
import static org.marketcetera.orderloader.Messages.*;
import org.marketcetera.orderloader.*;
import org.marketcetera.trade.Order;
import org.marketcetera.trade.BrokerID;
import org.marketcetera.trade.FIXOrder;
import org.marketcetera.quickfix.FIXVersion;
import org.marketcetera.core.ExpectedTestFailure;
import org.marketcetera.core.LoggerConfiguration;
import org.junit.Test;
import org.junit.BeforeClass;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import quickfix.field.*;
import quickfix.Message;
import quickfix.Field;

import java.util.List;
import java.util.Vector;
import java.util.Arrays;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

/* $License$ */
/**
 * Tests {@link FIXProcessor}
 *
 * @author Toli Kuznets
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.0.0
 */
public class FIXProcessorTest {
    @BeforeClass
    public static void setupLogger() {
        LoggerConfiguration.logSetup();
    }
    @Test
    public void getSide()
    {
        assertEquals(Side.UNDISCLOSED, FIXProcessor.getSide(null));
        assertEquals(Side.UNDISCLOSED, FIXProcessor.getSide(""));
        assertEquals(Side.UNDISCLOSED, FIXProcessor.getSide("asdf"));
        assertEquals(Side.BUY, FIXProcessor.getSide("b"));
        assertEquals(Side.BUY, FIXProcessor.getSide("B"));
        assertEquals(Side.SELL, FIXProcessor.getSide("S"));
        assertEquals(Side.SELL, FIXProcessor.getSide("s"));
        assertEquals(Side.SELL_SHORT, FIXProcessor.getSide("SS"));
        assertEquals(Side.SELL_SHORT, FIXProcessor.getSide("ss"));
        assertEquals(Side.SELL_SHORT_EXEMPT, FIXProcessor.getSide("SSE"));
        assertEquals(Side.SELL_SHORT_EXEMPT, FIXProcessor.getSide("sse"));
    }
    @Test
    public void addDefaults() throws Exception
    {
        Message msg = new Message();
        FIXProcessor.addDefaults(msg);
        assertEquals("msgType", MsgType.ORDER_SINGLE, msg.getHeader().getString(MsgType.FIELD));
        assertEquals("orderType", OrdType.LIMIT, msg.getChar(OrdType.FIELD));
        assertEquals("handlInst", HandlInst.AUTOMATED_EXECUTION_ORDER_PRIVATE, msg.getChar(HandlInst.FIELD));
    }
    @Test
    public void basicFieldParsing() throws Exception
    {
        final Message msg =new Message();
        FIXProcessor proc = create();
        assertEquals(""+Side.UNDISCLOSED, proc.parseMessageValue(new Side(), "side", "z", msg));
        assertEquals(""+Side.BUY, proc.parseMessageValue(new Side(), "side", "B", msg));
        assertEquals(""+Side.SELL, proc.parseMessageValue(new Side(), "side", "S", msg));
    }
    @Test
    public void priceParsing() throws Exception
    {
        final Message msg =new Message();
        final FIXProcessor proc = create();
        assertEquals(null, proc.parseMessageValue(new Price(), "Price", "MKT", msg));
        assertEquals(OrdType.MARKET, msg.getChar(OrdType.FIELD));
        assertEquals("42", proc.parseMessageValue(new Price(), "Price", "42", msg));
        assertEquals("42.42", proc.parseMessageValue(new Price(), "Price", "42.42", msg));
        (new ExpectedTestFailure(OrderParsingException.class,
                                 PARSING_PRICE_POSITIVE.getText("-42")) {
            protected void execute() throws Throwable
            {
                proc.parseMessageValue(new Price(), "price", "-42", msg);
            }
        }).run();
        (new ExpectedTestFailure(OrderParsingException.class,
                                 PARSING_PRICE_VALID_NUM.getText("toli")) {
            protected void execute() throws Throwable
            {
                proc.parseMessageValue(new Price(), "price", "toli", msg);
            }
        }).run();
    }
    @Test
    public void quantityParsing() throws Exception
    {
        final FIXProcessor proc = create();
        final Message msg =new Message();
        assertEquals("42", proc.parseMessageValue(new OrderQty(), "OrderQty", "42", msg));
        (new ExpectedTestFailure(OrderParsingException.class,
                                 PARSING_QTY_POS_INT.getText("-42")) {
            protected void execute() throws Throwable
            {
                proc.parseMessageValue(new OrderQty(), "OrderQty", "-42", msg);
            }
        }).run();
        (new ExpectedTestFailure(OrderParsingException.class,
                                 PARSING_QTY_INT.getText("toli")) {
            protected void execute() throws Throwable
            {
                proc.parseMessageValue(new OrderQty(), "OrderQty", "toli", msg);
            }
        }).run();
        (new ExpectedTestFailure(OrderParsingException.class,
                                 PARSING_QTY_INT.getText("42.2")) {
            protected void execute() throws Throwable
            {
                proc.parseMessageValue(new OrderQty(), "OrderQty", "42.2", msg);
            }
        }).run();
    }
    @Test
    public void getQuickfixFieldFromName() throws Exception
    {
        final FIXProcessor proc = create();
        assertEquals(new Side(), proc.getQuickFixFieldFromName("Side"));
        assertEquals(new OrderQty(), proc.getQuickFixFieldFromName("OrderQty"));
        assertEquals(new CustomField<String>(1234, null), proc.getQuickFixFieldFromName("1234"));
        (new ExpectedTestFailure(OrderParsingException.class, "ToliField") {
            protected void execute() throws Throwable
            {
                proc.getQuickFixFieldFromName("ToliField");
            }
        }).run();
    }
    @Test
    // verify that we get the right field order back when passed in
    public void getFieldOrder() throws Exception
    {
        doVerifyFieldOrder(new Field[] {new Symbol(), new Side(), new OrderQty(), new Price(), new TimeInForce(), new Account()},
                           new String[] {"Symbol", "Side", "OrderQty", "Price", "TimeInForce", "Account"});

        // reorder the fields
        doVerifyFieldOrder(new Field[] {new OrderQty(), new Price(), new TimeInForce(), new Symbol(), new Side(), new Account()},
                           new String[] {"OrderQty", "Price", "TimeInForce", "Symbol", "Side", "Account"});

        // custom field
        doVerifyFieldOrder(new Field[] {new OrderQty(), new Price(), new TimeInForce(), new Symbol(), new Side(), new CustomField<String>(1234, null)},
                           new String[] {"OrderQty", "Price", "TimeInForce", "Symbol", "Side", "1234"});

        // custom field in the middle
        doVerifyFieldOrder(new Field[] {new OrderQty(), new CustomField<String>(1234, null), new Symbol(), new Side()},
                           new String[] {"OrderQty", "1234", "Symbol", "Side"});

        // unknown field
        (new ExpectedTestFailure(OrderParsingException.class, "ToliField") {
            protected void execute() throws Throwable
            {
                doVerifyFieldOrder(new Field[] {new Symbol(), new Side(), new OrderQty(), new Price(), new TimeInForce(), new Account()},
                                   new String[] {"Symbol", "Side", "ToliField", "Price", "TimeInForce", "Account"});

            }
        }).run();
    }
    private void doVerifyFieldOrder(Field<?>[] inFields, String[] inHeaders) throws Exception
    {
        final FIXProcessor proc = create();
        proc.initialize(inHeaders);
        assertEquals(new Vector<Field<?>>(Arrays.asList(inFields)), proc.getHeaderFields());
        assertArrayEquals(inHeaders, proc.getHeaderNames());
    }

    /**
     * Use a pre-specified giant order and make sure the right
     * messages are sent.
     *
     * Tests {@link #ORDER_EXAMPLE} processing when its supplied
     * in native encoding
     *
     * @throws Exception if there were errors
     */
    @Test
    public void endToEndNativeEncoding() throws Exception
    {
        verifyOrderExampleProcessing(new ByteArrayInputStream(
                ORDER_EXAMPLE.getBytes()));
    }

    /**
     * Tests {@link #ORDER_EXAMPLE} processing when its supplied in
     * UTF8 Encoding
     *
     * @throws Exception if there were errors
     */
    @Test
    public void endToEndUTF8Encoding() throws Exception
    {
        verifyOrderExampleProcessing(new ByteArrayInputStream(
                SignatureCharset.UTF8_UTF8.encode(ORDER_EXAMPLE)));
    }

    /**
     * Tests {@link #ORDER_EXAMPLE} processing when its supplied in UTF32
     * encoding
     *
     * @throws Exception if there were errors
     */
    @Test
    public void endToEndUTF32Encoding() throws Exception
    {
        verifyOrderExampleProcessing(new ByteArrayInputStream(
                SignatureCharset.UTF32BE_UTF32BE.encode(ORDER_EXAMPLE)));
    }

    /**
     * Verifies results of processing of {@link #ORDER_EXAMPLE}
     *
     * @param inStream the input stream to read orders from
     *
     * @throws Exception if there were errors.
     */
    private void verifyOrderExampleProcessing(InputStream inStream)
            throws Exception {
        verifyOrderParsing(inStream, 2, 0, 9, 8);
    }

    @Test
    public void commentedLines() throws Exception
    {
        String order =
            "#Opening comment\n" +
            "Symbol,Side,OrderQty,Price,TimeInForce,Account\n"+
            "#IBM,B,100,12.1,DAY,123-ASDF-234\n"+
            "IBM,SS,100,12.22,DAY,123-ASDF-234\n"+
            "EFA,SSE,100,MKT,DAY,9182379812\n"+
            "#EFA,SSE,100,MKT,FILL_OR_KILL,9182379812\n";
        verifyOrderParsing(new ByteArrayInputStream(order.getBytes()), 0, 3, 2, 0);
    }
    @Test
    public void wrongNumberOfFields() throws Exception
    {
        String order =
            "Symbol,Side,OrderQty,Price,TimeInForce,Account\n"+
            "IBM,B,100,12.1,DAY,123-ASDF-234\n"+
            "IBM,SS,100,123-ASDF-234\n";
        verifyOrderParsing(new ByteArrayInputStream(order.getBytes()), 0, 0, 1, 1);
    }

    @Test
    public void endToEndCustom() throws Exception
    {
        String order =
            "Symbol,Side,OrderQty,Price,58,Account\n"+
            "IBM,B,100,12.1,DAY,123-ASDF-234\n"+
            "IBM,S,100,12.22,12,123-ASDF-234\n"+
            "IBM,S,100,12.22,12.45,123-ASDF-234\n";
        verifyOrderParsing(new ByteArrayInputStream(order.getBytes()), 0, 0, 3, 0);
    }
    @Test
    public void validMessage() throws Exception
    {
        RowProcessor processor = create();
        String[] headerNames = {"Symbol", "Side", "OrderQty", "Price", "TimeInForce", "Account"};
        processor.initialize(headerNames);
        // manually construct message: {55=IBM, 40=2, 38=100, 21=3, 11=666, 1=123-ASDF-234, 54=5, 59=0, 44=12.22}
        Message message = parseOrder(processor, true, "IBM", "SS", "100", "12.22", "DAY", "123-ASDF-234");

        assertEquals("IBM",message.getString(Symbol.FIELD));
        assertEquals(Side.SELL_SHORT,message.getChar(Side.FIELD));
        assertEquals("100",message.getString(OrderQty.FIELD));
        assertEquals("12.22",message.getString(Price.FIELD));
        assertEquals(TimeInForce.DAY,message.getChar(TimeInForce.FIELD));
        assertEquals("123-ASDF-234",message.getString(Account.FIELD));

    }
    private Message parseOrder(RowProcessor processor, boolean inVerify, String... inRow) {
        processor.processOrder(1, inRow);
        if (inVerify) {
            assertEquals(1, processor.getNumSuccess());
            assertEquals(0, processor.getNumFailed());
            assertEquals(0, processor.getFailedOrders().size());
            return verifySingleOrder();
        } else {
            return null;
        }
    }

    private Message verifySingleOrder() {
        List<Order> orders = mProcessor.getOrders();
        assertEquals(1, orders.size());

        FIXOrder order = (FIXOrder) orders.get(0);
        assertEquals(BROKER_ID, order.getBrokerID());
        assertNull(order.getSecurityType());
        return order.getMessage();
    }

    @Test
    public void nonAscii()
        throws Exception
    {
        final String[] headerNames = { "Symbol", "Side", "OrderQty", "Price", "HandlInst", "Account" };
        RowProcessor processor = create();
        processor.initialize(headerNames);
        String symbol = "some string";
        Message message = parseOrder(processor, true, symbol, "SS", "100", "12.22", "3", "123-ASDF-234");
        assertEquals(symbol,
                     message.getString(Symbol.FIELD));
        assertEquals(Side.SELL_SHORT,
                     message.getChar(Side.FIELD));
        assertEquals("100",
                     message.getString(OrderQty.FIELD));
        assertEquals("12.22",
                     message.getString(Price.FIELD));
        assertEquals("3",
                     message.getString(HandlInst.FIELD));
        assertEquals("123-ASDF-234",
                     message.getString(Account.FIELD));

        symbol = "some other string";
        processor = create();
        processor.initialize(headerNames);
        message = parseOrder(processor, true, symbol, "SS", "100", "12.22", "3", "123-ASDF-234");
        assertEquals(symbol,
                     message.getString(Symbol.FIELD));
        assertEquals(Side.SELL_SHORT,
                     message.getChar(Side.FIELD));
        assertEquals("100",
                     message.getString(OrderQty.FIELD));
        assertEquals("12.22",
                     message.getString(Price.FIELD));
        assertEquals("3",
                     message.getString(HandlInst.FIELD));
        assertEquals("123-ASDF-234",
                     message.getString(Account.FIELD));
    }

    /** Verify that HandlInst, if set, overwrites the default one in the NOS message factory */
    @Test
    public void handlInstField() throws Exception
    {
        RowProcessor processor = create();
        final String[] headerNames = {"Symbol", "Side", "OrderQty", "Price", "HandlInst", "Account"};
        processor.initialize(headerNames);
        Message message = parseOrder(processor, true, "IBM", "SS", "100", "12.22", "3", "123-ASDF-234");

        assertFalse("default handlInst is same as what we set so this test is pointless",
                FIXVersion.FIX42.getMessageFactory().newBasicOrder().
                        getString(HandlInst.FIELD).equals(""+HandlInst.MANUAL_ORDER));
        assertEquals("IBM", message.getString(Symbol.FIELD) );
        assertEquals(Side.SELL_SHORT, message.getChar(Side.FIELD) );
        assertEquals("100", message.getString(OrderQty.FIELD) );
        assertEquals("12.22", message.getString(Price.FIELD) );
        assertEquals("3", message.getString(HandlInst.FIELD) );
        assertEquals("123-ASDF-234", message.getString(Account.FIELD) );
    }

    @Test
    public void withCustomField() throws Exception
    {
        final String[] headerNames = {"Symbol", "Side", "OrderQty", "Price", "9999", "Account"};
        RowProcessor processor = create();
        processor.initialize(headerNames);
        parseOrder(processor, false, "IBM", "SS", "100", "12.22", "customValue", "123-ASDF-234");
        assertTrue(mProcessor.getOrders().isEmpty());
        assertEquals(1, processor.getNumFailed());
        assertEquals(1, processor.getFailedOrders().size());

        processor = create();
        processor.initialize(headerNames);
        // manually construct message: {55=IBM, 40=2, 38=100, 21=3, 11=666, 1=123-ASDF-234, 54=5, 59=0, 44=12.22}
        Message message = parseOrder(processor, true, "IBM", "SS", "100", "12.22", "123", "123-ASDF-234");

        assertEquals("IBM", message.getString(Symbol.FIELD) );
        assertEquals(Side.SELL_SHORT, message.getChar(Side.FIELD) );
        assertEquals("100", message.getString(OrderQty.FIELD) );
        assertEquals("12.22", message.getString(Price.FIELD) );
        assertEquals("123", message.getString(9999) );
        assertEquals("123-ASDF-234", message.getString(Account.FIELD) );

        processor = create();
        processor.initialize(headerNames);
        // integer, id+1
        // manually construct message: {55=IBM, 40=2, 38=100, 21=3, 11=666, 1=123-ASDF-234, 54=5, 59=0, 44=12.22}
        message = parseOrder(processor, true, "IBM", "SS", "100", "12.22", "12345", "123-ASDF-234");

        assertEquals("IBM", message.getString(Symbol.FIELD) );
        assertEquals(Side.SELL_SHORT, message.getChar(Side.FIELD) );
        assertEquals("100", message.getString(OrderQty.FIELD) );
        assertEquals("12.22", message.getString(Price.FIELD) );
        assertEquals("12345", message.getString(9999) );
        assertEquals("123-ASDF-234", message.getString(Account.FIELD) );
    }

    /** Test using both header and trailer and message custom fields */
    @Test
    public void withMixedCustomFields() throws Exception {
        RowProcessor processor = create();
        final String[] headerNames = {"Symbol", "Side", "OrderQty", "Price", "9999", "SenderSubID", "SignatureLength", "Signature"};
        processor.initialize(headerNames);

        // manually construct message: {55=IBM, Side=SS, OrderQty=100, Price=12.22, 9999=custom, SenderSubID=sub1, 93=3, 89=sig}
        Message message = parseOrder(processor, true, "IBM", "SS", "100", "12.22", "1234", "sub1", "sig".length()+"", "sig");

        assertEquals("IBM", message.getString(Symbol.FIELD) );
        assertEquals(Side.SELL_SHORT, message.getChar(Side.FIELD) );
        assertEquals("100", message.getString(OrderQty.FIELD) );
        assertEquals("12.22", message.getString(Price.FIELD) );
        assertEquals("1234", message.getString(9999));
        assertEquals("sub1", message.getHeader().getString(SenderSubID.FIELD));
        assertEquals(3, message.getTrailer().getInt(SignatureLength.FIELD));
        assertEquals("sig", message.getTrailer().getString(Signature.FIELD));
    }

    /** Try sending a message with key not in dictionary */
    @Test
    public void fieldNotInDictionary() throws Exception {
        RowProcessor processor = create();
        final String[] headerNames = {"Symbol", "Side", "OrderQty", "Price", "7654"};
        processor.initialize(headerNames);

        // manually construct message: {55=IBM, Side=SS, OrderQty=100, Price=12.22, 9999=custom, SenderSubID=sub1, 93=3, 89=sig}
        parseOrder(processor, false, "IBM", "SS", "100", "12.22", "1234");
        assertEquals(1, processor.getNumFailed());
        List<FailedOrderInfo> infoList = processor.getFailedOrders();
        assertEquals(1, infoList.size());
        FailedOrderInfo info = infoList.get(0);
        assertTrue(info.getException().toString(), info.getException().getLocalizedMessage().endsWith(PARSING_FIELD_NOT_IN_DICT.getText("7654", "1234")));
    }
    @Test
    public void marketPrice() throws Exception {
        RowProcessor processor = create();
        String[] headerNames = {"Symbol", "Side", "OrderQty", "Price", "TimeInForce"};
        processor.initialize(headerNames);
        // manually construct message: {55=IBM, 40=2, 38=100, 21=3, 11=666, 54=5, 59=0}
        Message message = parseOrder(processor, true, "IBM", "SS", "100", FIXProcessor.MKT_PRICE, "DAY");

        assertEquals("IBM",message.getString(Symbol.FIELD));
        assertEquals(Side.SELL_SHORT,message.getChar(Side.FIELD));
        assertEquals("100",message.getString(OrderQty.FIELD));
        assertEquals(OrdType.MARKET,message.getChar(OrdType.FIELD));
        assertFalse(message.isSetField(Price.FIELD));
        assertEquals(TimeInForce.DAY,message.getChar(TimeInForce.FIELD));
    }
    private void verifyOrderParsing(InputStream inStream, int inBlanks,
                                    int inComments, int inSuccess,
                                    int inFailed) throws Exception {
        RowProcessor processor = create();
        OrderParser parser = new OrderParser(processor);
        parser.parseOrders(inStream);
        assertEquals(inBlanks, parser.getNumBlankLines());
        assertEquals(inComments, parser.getNumComments());
        assertEquals(inSuccess, processor.getNumSuccess());
        assertEquals(inFailed, processor.getNumFailed());
        assertEquals(inFailed + inSuccess, processor.getTotal());
        assertEquals(inFailed, processor.getFailedOrders().size());
    }
    private FIXProcessor create() throws Exception {
        mProcessor.getOrders().clear();
        return new FIXProcessor(mProcessor,
                BROKER_ID, FIXVersion.FIX42);
    }
    private final MockOrderProcessor mProcessor = new MockOrderProcessor();
    /**
     * Example order for testing.
     */
    public static final String ORDER_EXAMPLE =
            "Symbol,Side,OrderQty,Price,TimeInForce,Account\n"+
            "IBM,B,100,12.1,DAY,123-ASDF-234\n"+
            "IBM,SS,100,12.22,DAY,123-ASDF-234\n"+
            "EFA,SSE,100,MKT,DAY,9182379812\n"+
            "EFA,SSE,100,MKT,FILL_OR_KILL,9182379812\n"+
            "---,SSE,100,MKT,DAY,9182379812\n"+
            "EFA,---,100,MKT,DAY,9182379812\n"+
            "EFA,SSE,---,MKT,DAY,9182379812\n"+
            "\n"+
            "EFA,SSE,100,---,DAY,9182379812\n"+
            "EFA,SSE,100,MKT,---,9182379812\n"+
            "EFA,SSE,100,MKT,---,---\n"+
            "EFA,SSE,100,MKT,DAY,---\n"+
            "EFA,SSE,100.1,MKT,DAY,9182379812\n"+
            "IBM,SS,100,-12.22,DAY,123-ASDF-234\n"+
            "IBM,SS,-100,12.22,DAY,123-ASDF-234\n"+
            "//do nothing\n"+
            "\n"+
            "IBM,SS,100,12.22,DAY,123-ASDF-234\n"+
            "IBM,S,100,12.22,DAY,123-ASDF-234\n";
    private static final BrokerID BROKER_ID = new BrokerID("Yes");
}
