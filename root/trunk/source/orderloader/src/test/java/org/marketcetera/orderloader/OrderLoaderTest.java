package org.marketcetera.orderloader;

import junit.framework.Test;
import junit.framework.TestCase;
import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.ExpectedTestFailure;
import org.marketcetera.core.MarketceteraTestSuite;
import org.marketcetera.quickfix.FIXDataDictionaryManager;
import org.marketcetera.quickfix.FIXVersion;
import org.marketcetera.quickfix.CurrentFIXDataDictionary;
import org.marketcetera.util.unicode.SignatureCharset;
import quickfix.Field;
import quickfix.Message;
import quickfix.field.*;

import javax.jms.JMSException;
import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.Vector;

/**
 * @author Toli Kuznets
 * @version $Id$
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class OrderLoaderTest 
    extends TestCase
    implements Messages
{
    private MyOrderLoader mLoader;

    public OrderLoaderTest(String inName)
    {
        super(inName);
    }

    public static Test suite()
    {
/*
        TestSuite suite = new TestSuite();
        suite.addTest(new OrderLoaderTest("testWithCustomField"));
        return suite;
*/
        return new MarketceteraTestSuite(OrderLoaderTest.class);
    }

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        mLoader = new MyOrderLoader(false);
        // includes the custom 9999 field as INT
        CurrentFIXDataDictionary.setCurrentFIXDataDictionary(
                FIXDataDictionaryManager.initialize(FIXVersion.FIX42,
                        "FIX42-orderloader-test.xml")); //$NON-NLS-1$
    }

    public void testGetSide()
    {
        assertEquals(Side.UNDISCLOSED, mLoader.getSide(null));
        assertEquals(Side.UNDISCLOSED, mLoader.getSide("")); //$NON-NLS-1$
        assertEquals(Side.UNDISCLOSED, mLoader.getSide("asdf")); //$NON-NLS-1$
        assertEquals(Side.BUY, mLoader.getSide("b")); //$NON-NLS-1$
        assertEquals(Side.BUY, mLoader.getSide("B")); //$NON-NLS-1$
        assertEquals(Side.SELL, mLoader.getSide("S")); //$NON-NLS-1$
        assertEquals(Side.SELL, mLoader.getSide("s")); //$NON-NLS-1$
        assertEquals(Side.SELL_SHORT, mLoader.getSide("SS")); //$NON-NLS-1$
        assertEquals(Side.SELL_SHORT, mLoader.getSide("ss")); //$NON-NLS-1$
        assertEquals(Side.SELL_SHORT_EXEMPT, mLoader.getSide("SSE")); //$NON-NLS-1$
        assertEquals(Side.SELL_SHORT_EXEMPT, mLoader.getSide("sse")); //$NON-NLS-1$
    }

    public void testAddDefaults() throws Exception
    {
        Message msg = new Message();
        mLoader.addDefaults(msg);
        assertEquals("msgType", MsgType.ORDER_SINGLE, msg.getHeader().getString(MsgType.FIELD)); //$NON-NLS-1$
        assertEquals("orderType", OrdType.LIMIT, msg.getChar(OrdType.FIELD)); //$NON-NLS-1$
        assertEquals("handlInst", HandlInst.AUTOMATED_EXECUTION_ORDER_PRIVATE, msg.getChar(HandlInst.FIELD)); //$NON-NLS-1$
    }

    public void testBasicFieldParsing() throws Exception
    {
        final Message msg =new Message();
        assertEquals(""+Side.UNDISCLOSED, mLoader.parseMessageValue(new Side(), "side", "z", msg)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        assertEquals(""+Side.BUY, mLoader.parseMessageValue(new Side(), "side", "B", msg)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        assertEquals(""+Side.SELL, mLoader.parseMessageValue(new Side(), "side", "S", msg)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }

    public void testPriceParsing() throws Exception
    {
        final Message msg =new Message();
        assertEquals(null, mLoader.parseMessageValue(new Price(), "Price", "MKT", msg)); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(OrdType.MARKET, msg.getChar(OrdType.FIELD));
        assertEquals("42", mLoader.parseMessageValue(new Price(), "Price", "42", msg)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        assertEquals("42.42", mLoader.parseMessageValue(new Price(), "Price", "42.42", msg)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        (new ExpectedTestFailure(OrderParsingException.class,
                                 PARSING_PRICE_POSITIVE.getText("-42")) { //$NON-NLS-1$
            protected void execute() throws Throwable
            {
                mLoader.parseMessageValue(new Price(), "price", "-42", msg); //$NON-NLS-1$ //$NON-NLS-2$
            }
        }).run();
        (new ExpectedTestFailure(OrderParsingException.class,
                                 PARSING_PRICE_VALID_NUM.getText("toli")) { //$NON-NLS-1$
            protected void execute() throws Throwable
            {
                mLoader.parseMessageValue(new Price(), "price", "toli", msg); //$NON-NLS-1$ //$NON-NLS-2$
            }
        }).run();
    }

    public void testQuantityParsing() throws Exception
    {
        final Message msg =new Message();
        assertEquals("42", mLoader.parseMessageValue(new OrderQty(), "OrderQty", "42", msg)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        (new ExpectedTestFailure(OrderParsingException.class,
                                 PARSING_QTY_POS_INT.getText("-42")) { //$NON-NLS-1$
            protected void execute() throws Throwable
            {
                mLoader.parseMessageValue(new OrderQty(), "OrderQty", "-42", msg); //$NON-NLS-1$ //$NON-NLS-2$
            }
        }).run();
        (new ExpectedTestFailure(OrderParsingException.class,
                                 PARSING_QTY_INT.getText("toli")) { //$NON-NLS-1$
            protected void execute() throws Throwable
            {
                mLoader.parseMessageValue(new OrderQty(), "OrderQty", "toli", msg); //$NON-NLS-1$ //$NON-NLS-2$
            }
        }).run();
        (new ExpectedTestFailure(OrderParsingException.class,
                                 PARSING_QTY_INT.getText("42.2")) { //$NON-NLS-1$
            protected void execute() throws Throwable
            {
                mLoader.parseMessageValue(new OrderQty(), "OrderQty", "42.2", msg); //$NON-NLS-1$ //$NON-NLS-2$
            }
        }).run();
    }

    public void testGetQuickfixFieldFromName() throws OrderParsingException
    {
        assertEquals(new Side(), mLoader.getQuickFixFieldFromName("Side")); //$NON-NLS-1$
        assertEquals(new OrderQty(), mLoader.getQuickFixFieldFromName("OrderQty")); //$NON-NLS-1$
        assertEquals(new CustomField(1234, null), mLoader.getQuickFixFieldFromName("1234")); //$NON-NLS-1$
        (new ExpectedTestFailure(OrderParsingException.class, "ToliField") { //$NON-NLS-1$
            protected void execute() throws Throwable
            {
                mLoader.getQuickFixFieldFromName("ToliField"); //$NON-NLS-1$
            }
        }).run();
    }

    // verify that we get the right field order back when passed in
    public void testGetFieldOrder() throws Exception
    {
        doVerifyFieldOrder(new Field[] {new Symbol(), new Side(), new OrderQty(), new Price(), new TimeInForce(), new Account()},
                           new String[] {"Symbol", "Side", "OrderQty", "Price", "TimeInForce", "Account"}); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$

        // reorder the fields
        doVerifyFieldOrder(new Field[] {new OrderQty(), new Price(), new TimeInForce(), new Symbol(), new Side(), new Account()},
                           new String[] {"OrderQty", "Price", "TimeInForce", "Symbol", "Side", "Account"}); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$

        // custom field
        doVerifyFieldOrder(new Field[] {new OrderQty(), new Price(), new TimeInForce(), new Symbol(), new Side(), new CustomField(1234, null)},
                           new String[] {"OrderQty", "Price", "TimeInForce", "Symbol", "Side", "1234"}); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$

        // custom field in the middle
        doVerifyFieldOrder(new Field[] {new OrderQty(), new CustomField(1234, null), new Symbol(), new Side()},
                           new String[] {"OrderQty", "1234", "Symbol", "Side"}); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$

        // unknown field
        (new ExpectedTestFailure(OrderParsingException.class, "ToliField") { //$NON-NLS-1$
            protected void execute() throws Throwable
            {
                doVerifyFieldOrder(new Field[] {new Symbol(), new Side(), new OrderQty(), new Price(), new TimeInForce(), new Account()},
                                   new String[] {"Symbol", "Side", "ToliField", "Price", "TimeInForce", "Account"}); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$

            }
        }).run();
    }

    private void doVerifyFieldOrder(Field<?>[] inFields, String[] inHeaders) throws Exception
    {
        assertEquals(new Vector<Field<?>>(Arrays.asList(inFields)), mLoader.getFieldOrder(inHeaders));
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
    public void testEndToEndNativeEncoding() throws Exception
    {
        mLoader.parseAndSendOrders(new ByteArrayInputStream(
                ORDER_EXAMPLE.getBytes()));

        verifyOrderExampleProcessing();
    }

    /**
     * Tests {@link #ORDER_EXAMPLE} processing when its supplied in
     * UTF8 Encoding
     *
     * @throws Exception if there were errors
     */
    public void testEndToEndUTF8Encoding() throws Exception
    {
        mLoader.parseAndSendOrders(new ByteArrayInputStream(
                SignatureCharset.UTF8_UTF8.encode(ORDER_EXAMPLE)));
        verifyOrderExampleProcessing();
    }

    /**
     * Tests {@link #ORDER_EXAMPLE} processing when its supplied in UTF32
     * encoding
     *
     * @throws Exception if there were errors
     */
    public void testEndToEndUTF32Encoding() throws Exception
    {
        mLoader.parseAndSendOrders(new ByteArrayInputStream(
                SignatureCharset.UTF32BE_UTF32BE.encode(ORDER_EXAMPLE)));
        verifyOrderExampleProcessing();
    }

    /**
     * Verifies results of processing of {@link #ORDER_EXAMPLE}
     */
    private void verifyOrderExampleProcessing() {
        assertEquals(2, mLoader.numBlankLines);
        assertEquals(9, mLoader.numProcessedOrders);
        assertEquals(9, mLoader.getNumProcessedOrders());
        assertEquals(8, mLoader.failedOrders.size());
        assertEquals(8, mLoader.getFailedOrders().size());
    }

    public void testCommentedLines() throws Exception
    {
        String order =
            "#Opening comment\n" + //$NON-NLS-1$
            "Symbol,Side,OrderQty,Price,TimeInForce,Account\n"+ //$NON-NLS-1$
            "#IBM,B,100,12.1,DAY,123-ASDF-234\n"+ //$NON-NLS-1$
            "IBM,SS,100,12.22,DAY,123-ASDF-234\n"+ //$NON-NLS-1$
            "EFA,SSE,100,MKT,DAY,9182379812\n"+ //$NON-NLS-1$
            "#EFA,SSE,100,MKT,FILL_OR_KILL,9182379812\n"; //$NON-NLS-1$
        mLoader.parseAndSendOrders(new ByteArrayInputStream(order.getBytes()));

        assertEquals(0, mLoader.numBlankLines);
        assertEquals(2, mLoader.numProcessedOrders);
        assertEquals(0, mLoader.failedOrders.size());
        assertEquals(2, mLoader.numComments);
    }
    public void testWrongNumberOfFields() throws Exception
    {
        String order =
            "Symbol,Side,OrderQty,Price,TimeInForce,Account\n"+ //$NON-NLS-1$
            "IBM,B,100,12.1,DAY,123-ASDF-234\n"+ //$NON-NLS-1$
            "IBM,SS,100,123-ASDF-234\n"; //$NON-NLS-1$
        mLoader.parseAndSendOrders(new ByteArrayInputStream(order.getBytes()));

        assertEquals(0, mLoader.numBlankLines);
        assertEquals(1, mLoader.numProcessedOrders);
        assertEquals(1, mLoader.failedOrders.size());
        assertEquals(0, mLoader.numComments);
    }

    public void testEndToEndCustom() throws Exception
    {
        String order =
            "Symbol,Side,OrderQty,Price,58,Account\n"+ //$NON-NLS-1$
            "IBM,B,100,12.1,DAY,123-ASDF-234\n"+ //$NON-NLS-1$
            "IBM,S,100,12.22,12,123-ASDF-234\n"+ //$NON-NLS-1$
            "IBM,S,100,12.22,12.45,123-ASDF-234\n"; //$NON-NLS-1$
        mLoader.parseAndSendOrders(new ByteArrayInputStream(order.getBytes()));

        assertEquals(0, mLoader.numBlankLines);
        assertEquals(3, mLoader.numProcessedOrders);
        assertEquals(0, mLoader.failedOrders.size());
    }

    public void testValidMessage() throws Exception
    {
        MyOrderLoader myLoader =  new MyOrderLoader(false);
        Vector<Field<?>> headerFields =  new Vector<Field<?>>(Arrays.asList(new Field<?>[] { new Symbol(),
                                                                                             new Side(),
                                                                                             new OrderQty(),
                                                                                             new Price(),
                                                                                             new TimeInForce(),
                                                                                             new Account() }));
        String[] headerNames = {"Symbol", "Side", "OrderQty", "Price", "TimeInForce", "Account"}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
        myLoader.sendOneOrder(headerFields, headerNames, new String[] {"IBM","SS","100","12.22","DAY","123-ASDF-234"}); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$

        // manually construct message: {55=IBM, 40=2, 38=100, 21=3, 11=666, 1=123-ASDF-234, 54=5, 59=0, 44=12.22}
        assertEquals("IBM",myLoader.mMessage.getString(Symbol.FIELD)); //$NON-NLS-1$
        assertEquals(Side.SELL_SHORT,myLoader.mMessage.getChar(Side.FIELD)); //$NON-NLS-1$
        assertEquals("100",myLoader.mMessage.getString(OrderQty.FIELD)); //$NON-NLS-1$
        assertEquals("12.22",myLoader.mMessage.getString(Price.FIELD)); //$NON-NLS-1$
        assertEquals(TimeInForce.DAY,myLoader.mMessage.getChar(TimeInForce.FIELD));
        assertEquals("123-ASDF-234",myLoader.mMessage.getString(Account.FIELD)); //$NON-NLS-1$

    }

    public void testNonAscii()
        throws Exception
    {
        final Vector<Field<?>> headerFields =  new Vector<Field<?>>(Arrays.asList(new Field<?>[] { new Symbol(),
                                                                                                   new Side(),
                                                                                                   new OrderQty(),
                                                                                                   new Price(),
                                                                                                   new HandlInst(),
                                                                                                   new Account() }));
        final String[] headerNames = { "Symbol", "Side", "OrderQty", "Price", "HandlInst", "Account" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
//        String symbol = I18n.generateNativeString();
        String symbol = "some string"; //$NON-NLS-1$
        mLoader.sendOneOrder(headerFields,
                             headerNames,
                             new String[] { symbol,"SS","100","12.22","3","123-ASDF-234"}); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
        assertNotNull("message didn't go through", //$NON-NLS-1$
                      mLoader.mMessage);
        assertEquals(symbol,
                     mLoader.mMessage.getString(Symbol.FIELD));
        assertEquals(Side.SELL_SHORT,
                     mLoader.mMessage.getChar(Side.FIELD));
        assertEquals("100", //$NON-NLS-1$
                     mLoader.mMessage.getString(OrderQty.FIELD));
        assertEquals("12.22", //$NON-NLS-1$
                     mLoader.mMessage.getString(Price.FIELD));
        assertEquals("3", //$NON-NLS-1$
                     mLoader.mMessage.getString(HandlInst.FIELD));
        assertEquals("123-ASDF-234", //$NON-NLS-1$
                     mLoader.mMessage.getString(Account.FIELD));

//        symbol = I18n.generateUnicodeString();
        symbol = "some other string"; //$NON-NLS-1$
        mLoader.sendOneOrder(headerFields,
                             headerNames,
                             new String[] { symbol,"SS","100","12.22","3","123-ASDF-234"}); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
        assertNotNull("message didn't go through", //$NON-NLS-1$
                      mLoader.mMessage);
        assertEquals(symbol,
                     mLoader.mMessage.getString(Symbol.FIELD));
        assertEquals(Side.SELL_SHORT,
                     mLoader.mMessage.getChar(Side.FIELD));
        assertEquals("100", //$NON-NLS-1$
                     mLoader.mMessage.getString(OrderQty.FIELD));
        assertEquals("12.22", //$NON-NLS-1$
                     mLoader.mMessage.getString(Price.FIELD));
        assertEquals("3", //$NON-NLS-1$
                     mLoader.mMessage.getString(HandlInst.FIELD));
        assertEquals("123-ASDF-234", //$NON-NLS-1$
                     mLoader.mMessage.getString(Account.FIELD));
    }

    /** Verify that HandlInst, if set, overwrites the default one in the NOS message factory */
    public void testHandlInstField() throws Exception
    {
        final Vector<Field<?>> headerFields =  new Vector<Field<?>>(Arrays.asList(new Field<?>[] { new Symbol(),
                                                                                                   new Side(),
                                                                                                   new OrderQty(), 
                                                                                                   new Price(),
                                                                                                   new HandlInst(),
                                                                                                   new Account() }));
        final String[] headerNames = {"Symbol", "Side", "OrderQty", "Price", "HandlInst", "Account"}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
        mLoader.sendOneOrder(headerFields, headerNames, new String[] {"IBM","SS","100","12.22","3","123-ASDF-234"}); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$

        assertFalse("default handlInst is same as what we set so this test is pointless", //$NON-NLS-1$
                FIXVersion.FIX42.getMessageFactory().newBasicOrder().getString(HandlInst.FIELD).equals("3")); //$NON-NLS-1$
        assertNotNull("message didn't go through", mLoader.mMessage); //$NON-NLS-1$
        assertEquals("IBM", mLoader.mMessage.getString(Symbol.FIELD) ); //$NON-NLS-1$
        assertEquals(Side.SELL_SHORT, mLoader.mMessage.getChar(Side.FIELD) );
        assertEquals("100", mLoader.mMessage.getString(OrderQty.FIELD) ); //$NON-NLS-1$
        assertEquals("12.22", mLoader.mMessage.getString(Price.FIELD) ); //$NON-NLS-1$
        assertEquals("3", mLoader.mMessage.getString(HandlInst.FIELD) ); //$NON-NLS-1$
        assertEquals("123-ASDF-234", mLoader.mMessage.getString(Account.FIELD) ); //$NON-NLS-1$
    }

    public void testWithCustomField() throws Exception
    {
        final Vector<Field<?>> headerFields =  new Vector<Field<?>>(Arrays.asList(new Field<?>[] { new Symbol(),
                                                                                                   new Side(),
                                                                                                   new OrderQty(),
                                                                                                   new Price(),
                                                                                                   new CustomField(9999,
                                                                                                                   null),
                                                                                                                   new Account() }));
        final String[] headerNames = {"Symbol", "Side", "OrderQty", "Price", "9999", "Account"}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
        mLoader.sendOneOrder(headerFields, headerNames, new String[] {"IBM","SS","100","12.22","customValue","123-ASDF-234"}); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
        assertNull("message with malformed custom field went through", mLoader.mMessage); //$NON-NLS-1$
        assertEquals(1, mLoader.getFailedOrders().size());

        // manually construct message: {55=IBM, 40=2, 38=100, 21=3, 11=666, 1=123-ASDF-234, 54=5, 59=0, 44=12.22}
        mLoader.sendOneOrder(headerFields, headerNames, new String[] {"IBM","SS","100","12.22","123","123-ASDF-234"}); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$

        assertNotNull("message didn't go through", mLoader.mMessage); //$NON-NLS-1$
        assertEquals("IBM", mLoader.mMessage.getString(Symbol.FIELD) ); //$NON-NLS-1$
        assertEquals(Side.SELL_SHORT, mLoader.mMessage.getChar(Side.FIELD) );
        assertEquals("100", mLoader.mMessage.getString(OrderQty.FIELD) ); //$NON-NLS-1$
        assertEquals("12.22", mLoader.mMessage.getString(Price.FIELD) ); //$NON-NLS-1$
        assertEquals("123", mLoader.mMessage.getString(9999) ); //$NON-NLS-1$
        assertEquals("123-ASDF-234", mLoader.mMessage.getString(Account.FIELD) ); //$NON-NLS-1$

        // integer, id+1
        // manually construct message: {55=IBM, 40=2, 38=100, 21=3, 11=666, 1=123-ASDF-234, 54=5, 59=0, 44=12.22}
        mLoader.sendOneOrder(headerFields, headerNames, new String[] {"IBM","SS","100","12.22","12345","123-ASDF-234"}); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
        assertEquals("IBM", mLoader.mMessage.getString(Symbol.FIELD) ); //$NON-NLS-1$
        assertEquals(Side.SELL_SHORT, mLoader.mMessage.getChar(Side.FIELD) );
        assertEquals("100", mLoader.mMessage.getString(OrderQty.FIELD) ); //$NON-NLS-1$
        assertEquals("12.22", mLoader.mMessage.getString(Price.FIELD) ); //$NON-NLS-1$
        assertEquals("12345", mLoader.mMessage.getString(9999) ); //$NON-NLS-1$
        assertEquals("123-ASDF-234", mLoader.mMessage.getString(Account.FIELD) ); //$NON-NLS-1$


        // strategy directive: custom fields are 5900,9623,5084,5083
//        Vector<Field> headerFields2 = new Vector<Field>(Arrays.asList(new Field[]{new Symbol(), new Side(),
//                new OrderQty(), new Price(), new TimeInForce(), new Account(),
//                new CustomField(5900, null), new CustomField(9623, null),
//                new CustomField(5084, null),
//                new CustomField(5083, null)}));
//        String[] headerNames2 = new String[]{"Symbol", "Side", "OrderQty", "Price", "TimeInForce", "Account",
//                "5900", "9623", "5084", "5083"};
//        // manually construct message: TWX,BUY,1500,18.11,DAY,TOLI,NCentsWide,0.01,200,5
//        mLoader.sendOneOrder(headerFields2, headerNames2, new String[] {"TWX","B","1500","18.11","DAY","TOLI",
//                                    "NCentsWide","0.01","200","5"});
//        assertEquals("TWX", mLoader.mMessage.getString(Symbol.FIELD) );
//        assertEquals(Side.BUY, mLoader.mMessage.getChar(Side.FIELD) );
//        assertEquals("1500", mLoader.mMessage.getString(OrderQty.FIELD) );
//        assertEquals("18.11", mLoader.mMessage.getString(Price.FIELD) );
//        assertEquals(TimeInForce.DAY, mLoader.mMessage.getString(TimeInForce.FIELD) );
//        assertEquals("123-ASDF-234", mLoader.mMessage.getString(Account.FIELD) );
//        assertEquals("[40=2, 55=TWX, 11=669, 5083=5, 9623=0.01, 54=1, 44=18.11, 59=0, 5900=NCentsWide, 21=3, 38=1500, 1=TOLI, 5084=200]",
//                     mLoader.mMessage.toString());
    }

    /** Test using both header and trailer and message custom fields */
    public void testWithMixedCustomFields() throws Exception {
        final Vector<Field<?>> headerFields =  new Vector<Field<?>>(Arrays.asList(new Field<?>[] { new Symbol(),
                                                                                                   new Side(),
                                                                                                   new OrderQty(),
                                                                                                   new Price(),
                                                                                                   new CustomField(9999,
                                                                                                                   null),
                                                                                                   new SenderSubID(),
                                                                                                   new SignatureLength(),
                                                                                                   new Signature() }));
        final String[] headerNames = {"Symbol", "Side", "OrderQty", "Price", "9999", "SenderSubID", "SignatureLength", "Signature"}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$

        // manually construct message: {55=IBM, Side=SS, OrderQty=100, Price=12.22, 9999=custom, SenderSubID=sub1, 93=3, 89=sig}
        mLoader.sendOneOrder(headerFields, headerNames, new String[] {"IBM","SS","100","12.22","1234","sub1", "sig".length()+"", "sig"}); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$
        assertEquals(0, mLoader.getFailedOrders().size());
        assertEquals("IBM", mLoader.mMessage.getString(Symbol.FIELD) ); //$NON-NLS-1$
        assertEquals(Side.SELL_SHORT, mLoader.mMessage.getChar(Side.FIELD) );
        assertEquals("100", mLoader.mMessage.getString(OrderQty.FIELD) ); //$NON-NLS-1$
        assertEquals("12.22", mLoader.mMessage.getString(Price.FIELD) ); //$NON-NLS-1$
        assertEquals("1234", mLoader.mMessage.getString(9999)); //$NON-NLS-1$
        assertEquals("sub1", mLoader.mMessage.getHeader().getString(SenderSubID.FIELD)); //$NON-NLS-1$
        assertEquals(3, mLoader.mMessage.getTrailer().getInt(SignatureLength.FIELD));
        assertEquals("sig", mLoader.mMessage.getTrailer().getString(Signature.FIELD)); //$NON-NLS-1$
    }

    /** Try sending a message with key not in dictionary */
    public void testFieldNotInDictionary() throws Exception {
        final Vector<Field<?>> headerFields =  new Vector<Field<?>>(Arrays.asList(new Field<?>[] { new Symbol(),
                                                                                                   new Side(),
                                                                                                   new OrderQty(),
                                                                                                   new Price(),
                                                                                                   new CustomField(7654,
                                                                                                                   null) } ));
        final String[] headerNames = {"Symbol", "Side", "OrderQty", "Price", "NotInDict"}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$

        // manually construct message: {55=IBM, Side=SS, OrderQty=100, Price=12.22, 9999=custom, SenderSubID=sub1, 93=3, 89=sig}
        mLoader.sendOneOrder(headerFields, headerNames, new String[] {"IBM","SS","100","12.22","1234"}); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
        assertEquals(1, mLoader.getFailedOrders().size());
        assertTrue(mLoader.failedOrders.get(0) + " does not end with "+PARSING_FIELD_NOT_IN_DICT.getText(7654, 1234), //$NON-NLS-1$
                mLoader.failedOrders.get(0).endsWith(PARSING_FIELD_NOT_IN_DICT.getText("7654", "1234"))); //$NON-NLS-1$ //$NON-NLS-2$
    }

    private class MyOrderLoader extends OrderLoader {
        private Message mMessage;
        private boolean mSendPassThrough = false;

        public MyOrderLoader(boolean fSendPassThrough) throws Exception
        {
            super("admin","admin"); //$NON-NLS-1$ //$NON-NLS-2$
            mSendPassThrough = fSendPassThrough;
        }

        @Override
        protected void sendMessage(Message message) throws JMSException
        {
            if(!mSendPassThrough) {
                mMessage = message;
            } else {
                super.sendMessage(message);
            }
        }


        protected String getConfigName() {
            return "orderloader-test.xml"; //$NON-NLS-1$
        }
    }

    /**
     * Example order for testing.
     */
    private static final String ORDER_EXAMPLE =
            "Symbol,Side,OrderQty,Price,TimeInForce,Account\n"+ //$NON-NLS-1$
            "IBM,B,100,12.1,DAY,123-ASDF-234\n"+ //$NON-NLS-1$
            "IBM,SS,100,12.22,DAY,123-ASDF-234\n"+ //$NON-NLS-1$
            "EFA,SSE,100,MKT,DAY,9182379812\n"+ //$NON-NLS-1$
            "EFA,SSE,100,MKT,FILL_OR_KILL,9182379812\n"+ //$NON-NLS-1$
            "---,SSE,100,MKT,DAY,9182379812\n"+ //$NON-NLS-1$
            "EFA,---,100,MKT,DAY,9182379812\n"+ //$NON-NLS-1$
            "EFA,SSE,---,MKT,DAY,9182379812\n"+ //$NON-NLS-1$
            "\n"+ //$NON-NLS-1$
            "EFA,SSE,100,---,DAY,9182379812\n"+ //$NON-NLS-1$
            "EFA,SSE,100,MKT,---,9182379812\n"+ //$NON-NLS-1$
            "EFA,SSE,100,MKT,---,---\n"+ //$NON-NLS-1$
            "EFA,SSE,100,MKT,DAY,---\n"+ //$NON-NLS-1$
            "EFA,SSE,100.1,MKT,DAY,9182379812\n"+ //$NON-NLS-1$
            "IBM,SS,100,-12.22,DAY,123-ASDF-234\n"+ //$NON-NLS-1$
            "IBM,SS,-100,12.22,DAY,123-ASDF-234\n"+ //$NON-NLS-1$
            "//do nothing\n"+ //$NON-NLS-1$
            "\n"+ //$NON-NLS-1$
            "IBM,SS,100,12.22,DAY,123-ASDF-234\n"+ //$NON-NLS-1$
            "IBM,S,100,12.22,DAY,123-ASDF-234\n"; //$NON-NLS-1$
}
