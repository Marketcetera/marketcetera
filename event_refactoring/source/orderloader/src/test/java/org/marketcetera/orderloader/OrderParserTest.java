package org.marketcetera.orderloader;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.log.I18NMessage0P;
import org.marketcetera.util.log.I18NBoundMessage2P;
import org.marketcetera.util.unicode.SignatureCharset;
import org.marketcetera.util.except.I18NException;
import org.marketcetera.trade.Order;
import org.marketcetera.trade.BrokerID;
import org.marketcetera.trade.Factory;
import org.marketcetera.module.ExpectedFailure;
import static org.marketcetera.orderloader.Messages.ERROR_NO_ORDERS;
import org.marketcetera.core.LoggerConfiguration;
import org.junit.Test;
import org.junit.BeforeClass;
import static org.junit.Assert.*;
import static org.junit.Assert.assertArrayEquals;
import org.apache.commons.lang.SystemUtils;

import java.util.List;
import java.util.LinkedList;
import java.util.regex.Pattern;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.IOException;

/* $License$ */
/**
 * Tests {@link OrderParser} & {@link RowProcessor}
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$")
public class OrderParserTest {
    @BeforeClass
    public static void setupLogger() {
        LoggerConfiguration.logSetup();
    }
    @Test
    public void nullRowProcessor() throws Exception {
        new ExpectedFailure<NullPointerException>(null){
            protected void run() throws Exception {
                new OrderParser(null);
            }
        };
    }
    @Test
    public void nullOrderProcessor() throws Exception {
        new ExpectedFailure<NullPointerException>(null){
            protected void run() throws Exception {
                new MockRowProcessor(new BrokerID("yyz"), null);
            }
        };
    }
    @Test
    public void noInput() throws Exception {
        MockOrderProcessor orderProcessor = new MockOrderProcessor();
        final MockRowProcessor rowProcessor = new MockRowProcessor(null, orderProcessor);
        //No lines
        new ExpectedFailure<OrderParsingException>(ERROR_NO_ORDERS){
            protected void run() throws Exception {
                OrderParser parser = new OrderParser(rowProcessor);
                parser.parseOrders(new ByteArrayInputStream("".getBytes()));
            }
        };
        //empty lines
        new ExpectedFailure<OrderParsingException>(ERROR_NO_ORDERS){
            protected void run() throws Exception {
                OrderParser parser = new OrderParser(rowProcessor);
                parser.parseOrders(new ByteArrayInputStream(arrayToLines("",
                        "  ", " \t").getBytes()));
            }
        };
        //comments only
        new ExpectedFailure<OrderParsingException>(ERROR_NO_ORDERS){
            protected void run() throws Exception {
                OrderParser parser = new OrderParser(rowProcessor);
                parser.parseOrders(new ByteArrayInputStream(arrayToLines("# uno",
                        "#dos", "#tres").getBytes()));
            }
        };
        //stuff
        new ExpectedFailure<OrderParsingException>(ERROR_NO_ORDERS){
            protected void run() throws Exception {
                OrderParser parser = new OrderParser(rowProcessor);
                parser.parseOrders(new ByteArrayInputStream(arrayToLines("#",
                        "  ", "# yes", " \t", "#blue").getBytes()));
            }
        };

    }
    @Test
    public void sampleInput() throws Exception {
        //Native encoding
        verifySampleInput(new ByteArrayInputStream(arrayToLines(
                SAMPLE_INPUT).getBytes()));
        //UTF8 encoding
        verifySampleInput(new ByteArrayInputStream(SignatureCharset.UTF8_UTF8.
                encode(arrayToLines(SAMPLE_INPUT))));
        //UTF32 encoding
        verifySampleInput(new ByteArrayInputStream(SignatureCharset.
                UTF32BE_UTF32BE.encode(arrayToLines(SAMPLE_INPUT))));
    }

    @Test
    public void misMatchedHeaderRow() throws Exception {
        MockOrderProcessor orderProcessor = new MockOrderProcessor();
        MockRowProcessor rowProcessor = new MockRowProcessor(null, orderProcessor);
        OrderParser parser = new OrderParser(rowProcessor);
        assertParser(parser, 0, 0, 0);
        String failRow = "duck1,duck2,duck4";
        parser.parseOrders(new ByteArrayInputStream(arrayToLines(
                "head1,head2,head3,head4",
                "cow1,cow2,cow3,cow4",
                failRow,
                "deer1,deer2,deer3,deer4"
        ).getBytes()));
        assertParser(parser, 4, 0, 0);
        assertProcessor(rowProcessor, 2, 1);
        List<FailedOrderInfo> list = rowProcessor.getFailedOrders();
        assertEquals(1, list.size());
        FailedOrderInfo info = list.get(0);
        assertEquals(3, info.getIndex());
        assertArrayEquals(failRow.split(","),info.getRow());
        assertEquals(new I18NBoundMessage2P(Messages.HEADER_ROW_MISMATCH,4, 3),
                ((I18NException) info.getException()).
                getI18NBoundMessage());
    }
    @Test
    public void orderProcessorFailure() throws Exception {
        MockOrderProcessor orderProcessor = new MockOrderProcessor();
        orderProcessor.setFail(true);
        MockRowProcessor rowProcessor = new MockRowProcessor(null, orderProcessor);
        OrderParser parser = new OrderParser(rowProcessor);
        assertParser(parser, 0, 0, 0);
        String failRow = "value1,value2,value3";
        parser.parseOrders(new ByteArrayInputStream(arrayToLines(
                "head1,head2,header",
                failRow
        ).getBytes()));
        assertParser(parser, 2, 0, 0);
        assertProcessor(rowProcessor, 0, 1);
        List<FailedOrderInfo> list = rowProcessor.getFailedOrders();
        assertEquals(1, list.size());
        FailedOrderInfo info = list.get(0);
        assertEquals(2, info.getIndex());
        assertArrayEquals(failRow.split(","),info.getRow());
        assertTrue(info.getException().toString(),
                info.getException() instanceof IllegalArgumentException);
        assertEquals(MockOrderProcessor.ORDER_FAILURE_STRING,
                info.getException().getMessage());
    }
    @Test
    public void headerProcessingFailure() throws Exception {
        MockOrderProcessor orderProcessor = new MockOrderProcessor();
        MockRowProcessor rowProcessor = new MockRowProcessor(null, orderProcessor);
        rowProcessor.setHeaderFail(true);
        final OrderParser parser = new OrderParser(rowProcessor);
        assertParser(parser, 0, 0, 0);
        new ExpectedFailure<OrderParsingException>(HEADER_FAILURE){
            protected void run() throws Exception {
                parser.parseOrders(new ByteArrayInputStream(arrayToLines(
                        "head1,head2,header",
                        "value1,value2,value3"
                ).getBytes()));
            }
        };
    }
    @Test
    public void rowProcessingFailure() throws Exception {
        MockOrderProcessor orderProcessor = new MockOrderProcessor();
        MockRowProcessor rowProcessor = new MockRowProcessor(null, orderProcessor);
        //Set all rows beginning with 'a' to fail.
        rowProcessor.setParseFail(Pattern.compile("^a.*"));
        OrderParser parser = new OrderParser(rowProcessor);
        assertParser(parser, 0, 0, 0);
        parser.parseOrders(new ByteArrayInputStream(arrayToLines(
                SAMPLE_INPUT).getBytes()));
        assertParser(parser, 17, 6, 4);
        assertProcessor(rowProcessor, 4, 2);
        List<FailedOrderInfo> list = rowProcessor.getFailedOrders();
        assertEquals(2, list.size());
        FailedOrderInfo info = list.get(0);
        assertEquals(7, info.getIndex());
        assertArrayEquals(SAMPLE_INPUT[6].split(","),info.getRow());
        assertEquals(PARSE_FAILURE,
                ((OrderParsingException)info.getException()).getI18NBoundMessage());
        info = list.get(1);
        assertEquals(11, info.getIndex());
        assertArrayEquals(SAMPLE_INPUT[10].split(","),info.getRow());
        assertEquals(PARSE_FAILURE,
                ((OrderParsingException)info.getException()).getI18NBoundMessage());

    }
    private void verifySampleInput(InputStream inInputStream)
            throws IOException, OrderParsingException {
        MockOrderProcessor orderProcessor = new MockOrderProcessor();
        MockRowProcessor rowProcessor = new MockRowProcessor(null, orderProcessor);
        OrderParser parser = new OrderParser(rowProcessor);
        assertParser(parser, 0, 0, 0);
        parser.parseOrders(inInputStream);
        assertParser(parser, 17, 6, 4);
        assertProcessor(rowProcessor, 6, 0);
        //verify headers
        assertArrayEquals(SAMPLE_INPUT[3].split(","), rowProcessor.getHeaders());
        List<String[]> rows = rowProcessor.getRows();
        assertEquals(6, rows.size());
        assertArrayEquals(SAMPLE_INPUT[5].split(","), rows.get(0));
        assertArrayEquals(SAMPLE_INPUT[6].split(","), rows.get(1));
        assertArrayEquals(SAMPLE_INPUT[8].split(","), rows.get(2));
        assertArrayEquals(SAMPLE_INPUT[10].split(","), rows.get(3));
        assertArrayEquals(SAMPLE_INPUT[11].split(","), rows.get(4));
        assertArrayEquals(SAMPLE_INPUT[13].split(","), rows.get(5));
        assertEquals(6, orderProcessor.getOrders().size());
    }

    @Test
    public void brokerID() throws Exception {
        MockOrderProcessor orderProcessor = new MockOrderProcessor();
        MockRowProcessor rowProcessor = new MockRowProcessor(null, orderProcessor);
        assertEquals(null, rowProcessor.geBrokerID());

        BrokerID brokerID = new BrokerID("SFO");
        rowProcessor = new MockRowProcessor(brokerID, orderProcessor);
        assertEquals(brokerID, rowProcessor.geBrokerID());
    }
    public static void assertParser(OrderParser inParser, int inNumLines,
                                    int inNumBlankLines, int inNumComments) {
        assertEquals(inNumLines, inParser.getNumLines());
        assertEquals(inNumBlankLines, inParser.getNumBlankLines());
        assertEquals(inNumComments, inParser.getNumComments());
    }

    public static void assertProcessor(RowProcessor inProcessor,
                                       int inSuccess, int inFailed) {
        assertEquals(inSuccess, inProcessor.getNumSuccess());
        assertEquals(inFailed, inProcessor.getNumFailed());
        assertEquals(inFailed, inProcessor.getFailedOrders().size());
        assertEquals(inFailed + inSuccess, inProcessor.getTotal());
    }

    private static class MockRowProcessor extends RowProcessor {
        protected MockRowProcessor(BrokerID inBrokerID,
                                   OrderProcessor inProcessor) {
            super(inProcessor, inBrokerID);
        }
        protected void setHeaders(String[] inHeaders) throws OrderParsingException {
            mHeaders = inHeaders;
            if(mHeaderFail) {
                throw new OrderParsingException(HEADER_FAILURE);
            }
        }
        protected Order parseOrder(String[] inRow) throws OrderParsingException {
            mRows.add(inRow);
            if(mParseFail != null) {
                if(mParseFail.matcher(inRow[0]).matches()) {
                    throw new OrderParsingException(PARSE_FAILURE);
                }
            }
            return Factory.getInstance().createOrderSingle();
        }

        public String[] getHeaders() {
            return mHeaders;
        }

        public List<String[]> getRows() {
            return mRows;
        }

        public void setHeaderFail(boolean inHeaderFail) {
            mHeaderFail = inHeaderFail;
        }

        public void setParseFail(Pattern inParseFail) {
            mParseFail = inParseFail;
        }

        private boolean mHeaderFail = false;
        private Pattern mParseFail = null;
        private List<String[]> mRows = new LinkedList<String[]>();
        private String[] mHeaders;
    }

    static String arrayToLines(String... inLines) {
        StringBuilder builder = new StringBuilder();
        for(String line: inLines) {
            builder.append(line).append(SystemUtils.LINE_SEPARATOR);
        }
        return builder.toString();
    }
    private static final I18NMessage0P PARSE_FAILURE = new I18NMessage0P(
            Messages.LOGGER, "parseFailure");
    private static final I18NMessage0P HEADER_FAILURE = new I18NMessage0P(
            Messages.LOGGER, "headerFailure");
    private static final String[] SAMPLE_INPUT = new String[]{
            "# comment followed by empty lines",
            "",
            "  ",
            "head1,head2,head3,head4",
            "",
            "value1,value2,value3,value4",
            "avalue1,avalue2,avalue3,avalue4",
            "# another comment",
            "bvalue1,bvalue2,bvalue3,bvalue4",
            "  ",
            "a1value1,a1value2,a1value3,a1value4",
            "b1value1,b1value2,b1value3,b1value4",
            "# commentary",
            "cvalue1,cvalue2,cvalue3,cvalue4",
            "# Trailing comment and empty lines",
            "  ",
            ""
    };
}
