package org.marketcetera.orderloader;

import junit.framework.Test;
import junit.framework.TestCase;
import org.marketcetera.core.*;
import org.marketcetera.quickfix.FIXDataDictionaryManager;
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
@ClassVersion("$Id$")
public class OrderLoaderTest extends TestCase
{
    private OrderLoader mLoader;

    public OrderLoaderTest(String inName)
    {
        super(inName);
    }

    public static Test suite()
    {
/*
        TestSuite suite = new TestSuite();
        suite.addTest(new OrderLoaderTest("testEndToEndCustom"));
        suite.addTest(new OrderLoaderTest("testWithCustomField"));
        return suite;
*/
        return new MarketceteraTestSuite(OrderLoaderTest.class, OrderLoader.OL_MESSAGE_BUNDLE_INFO);
    }

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        mLoader = new MyOrderLoader(false);
        mLoader.createApplicationContext(new String[]{"orderloader.xml"}, false);
        FIXDataDictionaryManager.loadDictionary("FIX42-orderloader-test.xml", true);
    }

    public void testGetSide()
    {
        assertEquals(Side.UNDISCLOSED, (Object)mLoader.getSide(null));
        assertEquals(Side.UNDISCLOSED, (Object)mLoader.getSide(""));
        assertEquals(Side.UNDISCLOSED, (Object)mLoader.getSide("asdf"));
        assertEquals(Side.BUY, (Object)mLoader.getSide("b"));
        assertEquals(Side.BUY, (Object)mLoader.getSide("B"));
        assertEquals(Side.SELL, (Object)mLoader.getSide("S"));
        assertEquals(Side.SELL, (Object)mLoader.getSide("s"));
        assertEquals(Side.SELL_SHORT, (Object)mLoader.getSide("SS"));
        assertEquals(Side.SELL_SHORT, (Object)mLoader.getSide("ss"));
        assertEquals(Side.SELL_SHORT_EXEMPT, (Object)mLoader.getSide("SSE"));
        assertEquals(Side.SELL_SHORT_EXEMPT, (Object)mLoader.getSide("sse"));
    }

    public void testAddDefaults() throws Exception
    {
        Message msg = new Message();
        mLoader.addDefaults(msg);
        assertEquals("msgType", MsgType.ORDER_SINGLE, msg.getHeader().getString(MsgType.FIELD));
        assertEquals("orderType", OrdType.LIMIT, msg.getChar(OrdType.FIELD));
        assertEquals("handlInst", HandlInst.MANUAL_ORDER, msg.getChar(HandlInst.FIELD));
    }

    public void testBasicFieldParsing() throws Exception
    {
        final Message msg =new Message();
        assertEquals(""+Side.UNDISCLOSED, mLoader.parseMessageValue(new Side(), "side", "z", msg));
        assertEquals(""+Side.BUY, mLoader.parseMessageValue(new Side(), "side", "B", msg));
        assertEquals(""+Side.SELL, mLoader.parseMessageValue(new Side(), "side", "S", msg));
    }

    public void testPriceParsing() throws Exception
    {
        final Message msg =new Message();
        assertEquals(null, mLoader.parseMessageValue(new Price(), "Price", "MKT", msg));
        assertEquals(OrdType.MARKET, msg.getChar(OrdType.FIELD));
        assertEquals("42", mLoader.parseMessageValue(new Price(), "Price", "42", msg));
        assertEquals("42.42", mLoader.parseMessageValue(new Price(), "Price", "42.42", msg));
        (new ExpectedTestFailure(OrderParsingException.class, "must be positive") {
            protected void execute() throws Throwable
            {
                mLoader.parseMessageValue(new Price(), "price", "-42", msg);
            }
        }).run();
        (new ExpectedTestFailure(OrderParsingException.class, "must be a valid number") {
            protected void execute() throws Throwable
            {
                mLoader.parseMessageValue(new Price(), "price", "toli", msg);
            }
        }).run();
    }

    public void testQuantityParsing() throws Exception
    {
        final Message msg =new Message();
        assertEquals("42", mLoader.parseMessageValue(new OrderQty(), "OrderQty", "42", msg));
        (new ExpectedTestFailure(OrderParsingException.class, "must be a positive") {
            protected void execute() throws Throwable
            {
                mLoader.parseMessageValue(new OrderQty(), "OrderQty", "-42", msg);
            }
        }).run();
        (new ExpectedTestFailure(OrderParsingException.class, "must be an integer") {
            protected void execute() throws Throwable
            {
                mLoader.parseMessageValue(new OrderQty(), "OrderQty", "toli", msg);
            }
        }).run();
        (new ExpectedTestFailure(OrderParsingException.class, "must be an integer") {
            protected void execute() throws Throwable
            {
                mLoader.parseMessageValue(new OrderQty(), "OrderQty", "42.2", msg);
            }
        }).run();
    }

    public void testGetQuickfixFieldFromName() throws OrderParsingException
    {
        assertEquals(new Side(), mLoader.getQuickFixFieldFromName("Side"));
        assertEquals(new OrderQty(), mLoader.getQuickFixFieldFromName("OrderQty"));
        assertEquals(new CustomField(1234, null), mLoader.getQuickFixFieldFromName("1234"));
        (new ExpectedTestFailure(OrderParsingException.class, "ToliField") {
            protected void execute() throws Throwable
            {
                mLoader.getQuickFixFieldFromName("ToliField");
            }
        }).run();
    }

    // verify that we get the right field order back when passed in
    public void testGetFieldOrder() throws Exception
    {
        doVerifyFieldOrder(new Field[] {new Symbol(), new Side(), new OrderQty(), new Price(), new TimeInForce(), new Account()},
                           new String[] {"Symbol", "Side", "OrderQty", "Price", "TimeInForce", "Account"});

        // reorder the fields
        doVerifyFieldOrder(new Field[] {new OrderQty(), new Price(), new TimeInForce(), new Symbol(), new Side(), new Account()},
                           new String[] {"OrderQty", "Price", "TimeInForce", "Symbol", "Side", "Account"});

        // custom field
        doVerifyFieldOrder(new Field[] {new OrderQty(), new Price(), new TimeInForce(), new Symbol(), new Side(), new CustomField(1234, null)},
                           new String[] {"OrderQty", "Price", "TimeInForce", "Symbol", "Side", "1234"});

        // custom field in the middle
        doVerifyFieldOrder(new Field[] {new OrderQty(), new CustomField(1234, null), new Symbol(), new Side()},
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

    private void doVerifyFieldOrder(Field[] inFields, String[] inHeaders) throws Exception
    {
        assertEquals(new Vector<Field>(Arrays.asList(inFields)), mLoader.getFieldOrder(inHeaders));
    }

    /** use a pre-specified giant order and make sure the right messages are sent
     * Should produce:
     * 2 blanks (skipped)
     * 7 errors
     */
    public void testEndToEnd() throws Exception
    {
        String order =
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
        mLoader.parseAndSendOrders(new ByteArrayInputStream(order.getBytes()));

        assertEquals(2, mLoader.numBlankLines);
        assertEquals(9, mLoader.numProcessedOrders);
        assertEquals(9, mLoader.getNumProcessedOrders());
        assertEquals(8, mLoader.failedOrders.size());
        assertEquals(8, mLoader.getFailedOrders().size());
    }

    public void testCommentedLins() throws Exception
    {
        String order =
            "Symbol,Side,OrderQty,Price,TimeInForce,Account\n"+
            "#IBM,B,100,12.1,DAY,123-ASDF-234\n"+
            "IBM,SS,100,12.22,DAY,123-ASDF-234\n"+
            "EFA,SSE,100,MKT,DAY,9182379812\n"+
            "#EFA,SSE,100,MKT,FILL_OR_KILL,9182379812\n";
        mLoader.parseAndSendOrders(new ByteArrayInputStream(order.getBytes()));

        assertEquals(0, mLoader.numBlankLines);
        assertEquals(2, mLoader.numProcessedOrders);
        assertEquals(0, mLoader.failedOrders.size());
        assertEquals(2, mLoader.numComments);
    }
    public void testWrongNumberOfFields() throws Exception
    {
        String order =
            "Symbol,Side,OrderQty,Price,TimeInForce,Account\n"+
            "IBM,B,100,12.1,DAY,123-ASDF-234\n"+
            "IBM,SS,100,123-ASDF-234\n";
        mLoader.parseAndSendOrders(new ByteArrayInputStream(order.getBytes()));

        assertEquals(0, mLoader.numBlankLines);
        assertEquals(1, mLoader.numProcessedOrders);
        assertEquals(1, mLoader.failedOrders.size());
        assertEquals(0, mLoader.numComments);
    }

    public void testEndToEndCustom() throws Exception
    {
        String order =
            "Symbol,Side,OrderQty,Price,58,Account\n"+
            "IBM,B,100,12.1,DAY,123-ASDF-234\n"+
            "IBM,S,100,12.22,12,123-ASDF-234\n"+
            "IBM,S,100,12.22,12.45,123-ASDF-234\n";
        mLoader.parseAndSendOrders(new ByteArrayInputStream(order.getBytes()));

        assertEquals(0, mLoader.numBlankLines);
        assertEquals(3, mLoader.numProcessedOrders);
        assertEquals(0, mLoader.failedOrders.size());
    }

    public void testValidMessage() throws Exception
    {
        MyOrderLoader myLoader =  new MyOrderLoader(false);
        Vector<Field> headerFields =  new Vector<Field>(Arrays.asList(new Symbol(), new Side(),
                new OrderQty(), new Price(), new TimeInForce(), new Account()));
        String[] headerNames = {"Symbol", "Side", "OrderQty", "Price", "TimeInForce", "Account"};
        myLoader.sendOneOrder(headerFields, headerNames, new String[] {"IBM","SS","100","12.22","DAY","123-ASDF-234"});

        // manually construct message: {55=IBM, 40=2, 38=100, 21=3, 11=666, 1=123-ASDF-234, 54=5, 59=0, 44=12.22}
        assertEquals("IBM",myLoader.mMessage.getString(Symbol.FIELD));
        assertEquals(Side.SELL_SHORT,myLoader.mMessage.getChar(Side.FIELD));
        assertEquals("100",myLoader.mMessage.getString(OrderQty.FIELD));
        assertEquals("12.22",myLoader.mMessage.getString(Price.FIELD));
        assertEquals(TimeInForce.DAY,myLoader.mMessage.getChar(TimeInForce.FIELD));
        assertEquals("123-ASDF-234",myLoader.mMessage.getString(Account.FIELD));

    }

    public void testWithCustomField() throws Exception
    {
        final MyOrderLoader myLoader =  new MyOrderLoader(false);
        final Vector<Field> headerFields =  new Vector<Field>(Arrays.asList(new Symbol(), new Side(),
                new OrderQty(), new Price(), new CustomField(9999, null), new Account()));
        final String[] headerNames = {"Symbol", "Side", "OrderQty", "Price", "9999", "Account"};
        myLoader.sendOneOrder(headerFields, headerNames, new String[] {"IBM","SS","100","12.22","customValue","123-ASDF-234"});
        assertNull(myLoader.mMessage);
        assertEquals(1, myLoader.getFailedOrders().size());

        // manually construct message: {55=IBM, 40=2, 38=100, 21=3, 11=666, 1=123-ASDF-234, 54=5, 59=0, 44=12.22}
        myLoader.sendOneOrder(headerFields, headerNames, new String[] {"IBM","SS","100","12.22","123","123-ASDF-234"});

        assertEquals("IBM", myLoader.mMessage.getString(Symbol.FIELD) );
        assertEquals(Side.SELL_SHORT, myLoader.mMessage.getChar(Side.FIELD) );
        assertEquals("100", myLoader.mMessage.getString(OrderQty.FIELD) );
        assertEquals("12.22", myLoader.mMessage.getString(Price.FIELD) );
        assertEquals("123", myLoader.mMessage.getString(9999) );
        assertEquals("123-ASDF-234", myLoader.mMessage.getString(Account.FIELD) );

        // integer, id+1
        // manually construct message: {55=IBM, 40=2, 38=100, 21=3, 11=666, 1=123-ASDF-234, 54=5, 59=0, 44=12.22}
        myLoader.sendOneOrder(headerFields, headerNames, new String[] {"IBM","SS","100","12.22","12345","123-ASDF-234"});
        assertEquals("IBM", myLoader.mMessage.getString(Symbol.FIELD) );
        assertEquals(Side.SELL_SHORT, myLoader.mMessage.getChar(Side.FIELD) );
        assertEquals("100", myLoader.mMessage.getString(OrderQty.FIELD) );
        assertEquals("12.22", myLoader.mMessage.getString(Price.FIELD) );
        assertEquals("12345", myLoader.mMessage.getString(9999) );
        assertEquals("123-ASDF-234", myLoader.mMessage.getString(Account.FIELD) );


        // strategy directive: custom fields are 5900,9623,5084,5083
//        Vector<Field> headerFields2 = new Vector<Field>(Arrays.asList(new Field[]{new Symbol(), new Side(),
//                new OrderQty(), new Price(), new TimeInForce(), new Account(),
//                new CustomField(5900, null), new CustomField(9623, null),
//                new CustomField(5084, null),
//                new CustomField(5083, null)}));
//        String[] headerNames2 = new String[]{"Symbol", "Side", "OrderQty", "Price", "TimeInForce", "Account",
//                "5900", "9623", "5084", "5083"};
//        // manually construct message: TWX,BUY,1500,18.11,DAY,TOLI,NCentsWide,0.01,200,5
//        myLoader.sendOneOrder(headerFields2, headerNames2, new String[] {"TWX","B","1500","18.11","DAY","TOLI",
//                                    "NCentsWide","0.01","200","5"});
//        assertEquals("TWX", myLoader.mMessage.getString(Symbol.FIELD) );
//        assertEquals(Side.BUY, myLoader.mMessage.getChar(Side.FIELD) );
//        assertEquals("1500", myLoader.mMessage.getString(OrderQty.FIELD) );
//        assertEquals("18.11", myLoader.mMessage.getString(Price.FIELD) );
//        assertEquals(TimeInForce.DAY, myLoader.mMessage.getString(TimeInForce.FIELD) );
//        assertEquals("123-ASDF-234", myLoader.mMessage.getString(Account.FIELD) );
//        assertEquals("[40=2, 55=TWX, 11=669, 5083=5, 9623=0.01, 54=1, 44=18.11, 59=0, 5900=NCentsWide, 21=3, 38=1500, 1=TOLI, 5084=200]",
//                     myLoader.mMessage.toString());
    }

    private class MyOrderLoader extends OrderLoader {
        private Message mMessage;
        private boolean mSendPassThrough = false;

        public MyOrderLoader(boolean fSendPassThrough) throws Exception
        {
            super(null);
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
    }
}
