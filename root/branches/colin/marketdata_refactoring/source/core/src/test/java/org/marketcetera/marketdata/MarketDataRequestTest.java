package org.marketcetera.marketdata;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.marketcetera.marketdata.MarketDataRequest.FULL_BOOK;
import static org.marketcetera.marketdata.MarketDataRequest.NO_EXCHANGE;
import static org.marketcetera.marketdata.MarketDataRequest.TOP_OF_BOOK;
import static org.marketcetera.marketdata.Messages.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.marketcetera.core.ExpectedTestFailure;
import org.marketcetera.marketdata.MarketDataRequest.RequestType;
import org.marketcetera.marketdata.MarketDataRequest.UpdateType;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id:$
 * @since $Release$
 */
public class MarketDataRequestTest
{
    @Test
    public void newFullBookRequest()
        throws Exception
    {
        new ExpectedTestFailure(NullPointerException.class) {
            @Override
            protected void execute()
                    throws Throwable
            {
                MarketDataRequest.newFullBookRequest((String[])null);
            }
        }.run();
        new ExpectedTestFailure(IllegalArgumentException.class,
                                INVALID_SYMBOLS.getText("[]")) {
            @Override
            protected void execute()
                    throws Throwable
            {
                MarketDataRequest.newFullBookRequest(new String[0]);
            }
        }.run();
        // single symbol
        verifyRequest(FULL_BOOK,
                      RequestType.SUBSCRIBE,
                      NO_EXCHANGE,
                      new String[] { "JAVA" },
                      UpdateType.INCREMENTAL_REFRESH,
                      MarketDataRequest.newFullBookRequest("JAVA"));
        // multiple symbols
        verifyRequest(FULL_BOOK,
                      RequestType.SUBSCRIBE,
                      NO_EXCHANGE,
                      new String[] { "GOOG", "MSFT", "YHOO" },
                      UpdateType.INCREMENTAL_REFRESH,
                      MarketDataRequest.newFullBookRequest("GOOG",
                                                   "MSFT",
                                                   "YHOO"));
    }
    @Test
    public void newTopOfBookRequest()
        throws Exception
    {
        new ExpectedTestFailure(NullPointerException.class) {
            @Override
            protected void execute()
                    throws Throwable
            {
                MarketDataRequest.newTopOfBookRequest((String[])null);
            }
        }.run();
        new ExpectedTestFailure(IllegalArgumentException.class,
                                INVALID_SYMBOLS.getText("[]")) {
            @Override
            protected void execute()
                    throws Throwable
            {
                MarketDataRequest.newTopOfBookRequest(new String[0]);
            }
        }.run();
        // single symbol
        verifyRequest(TOP_OF_BOOK,
                      RequestType.SUBSCRIBE,
                      NO_EXCHANGE,
                      new String[] { "JAVA" },
                      UpdateType.INCREMENTAL_REFRESH,
                      MarketDataRequest.newTopOfBookRequest("JAVA"));
        // multiple symbols
        verifyRequest(TOP_OF_BOOK,
                      RequestType.SUBSCRIBE,
                      NO_EXCHANGE,
                      new String[] { "GOOG", "MSFT", "YHOO" },
                      UpdateType.INCREMENTAL_REFRESH,
                      MarketDataRequest.newTopOfBookRequest("GOOG",
                                                            "MSFT",
                                                            "YHOO"));
    }
    @Test
    public void newSpecifiedDepthRequest()
        throws Exception
    {
        new ExpectedTestFailure(NullPointerException.class) {
            @Override
            protected void execute()
                    throws Throwable
            {
                MarketDataRequest.newSpecifedDepthRequest(FULL_BOOK,
                                                          (String[])null);
            }
        }.run();
        new ExpectedTestFailure(IllegalArgumentException.class,
                                INVALID_SYMBOLS.getText("[]")) {
            @Override
            protected void execute()
                    throws Throwable
            {
                MarketDataRequest.newSpecifedDepthRequest(FULL_BOOK,
                                                          new String[0]);
            }
        }.run();
        new ExpectedTestFailure(IllegalArgumentException.class,
                                INVALID_DEPTH.getText(-1)) {
            @Override
            protected void execute()
                    throws Throwable
            {
                MarketDataRequest.newSpecifedDepthRequest(-1,
                                                          "ORCL");
            }
        }.run();
        // single symbol
        verifyRequest(FULL_BOOK,
                      RequestType.SUBSCRIBE,
                      NO_EXCHANGE,
                      new String[] { "JAVA" },
                      UpdateType.INCREMENTAL_REFRESH,
                      MarketDataRequest.newSpecifedDepthRequest(FULL_BOOK,
                                                                "JAVA"));
        verifyRequest(TOP_OF_BOOK,
                      RequestType.SUBSCRIBE,
                      NO_EXCHANGE,
                      new String[] { "JAVA" },
                      UpdateType.INCREMENTAL_REFRESH,
                      MarketDataRequest.newSpecifedDepthRequest(TOP_OF_BOOK,
                                                                "JAVA"));
        verifyRequest(100,
                      RequestType.SUBSCRIBE,
                      NO_EXCHANGE,
                      new String[] { "JAVA" },
                      UpdateType.INCREMENTAL_REFRESH,
                      MarketDataRequest.newSpecifedDepthRequest(100,
                                                                "JAVA"));
        // multiple symbols
        verifyRequest(FULL_BOOK,
                      RequestType.SUBSCRIBE,
                      NO_EXCHANGE,
                      new String[] { "GOOG", "MSFT", "YHOO" },
                      UpdateType.INCREMENTAL_REFRESH,
                      MarketDataRequest.newSpecifedDepthRequest(FULL_BOOK,
                                                                "GOOG",
                                                                "MSFT",
                                                                "YHOO"));
        verifyRequest(TOP_OF_BOOK,
                      RequestType.SUBSCRIBE,
                      NO_EXCHANGE,
                      new String[] { "GOOG", "MSFT", "YHOO" },
                      UpdateType.INCREMENTAL_REFRESH,
                      MarketDataRequest.newSpecifedDepthRequest(TOP_OF_BOOK,
                                                                "GOOG",
                                                                "MSFT",
                                                                "YHOO"));
        verifyRequest(1000,
                      RequestType.SUBSCRIBE,
                      NO_EXCHANGE,
                      new String[] { "GOOG", "MSFT", "YHOO" },
                      UpdateType.INCREMENTAL_REFRESH,
                      MarketDataRequest.newSpecifedDepthRequest(1000,
                                                                "GOOG",
                                                                "MSFT",
                                                                "YHOO"));
    }
    /**
     * Tests the ability to generate <code>MarketDataRequest</code> objects from <code>String</code> objects.
     *
     * <p>Note that this method tests mostly negative conditions as {@link #verifyRequest(int, RequestType, String, String[], UpdateType, MarketDataRequest)}
     * tests the positive conditions when it takes the actual results, converts it to a <code>String</code>, and back.
     * 
     * @throws Exception if one of the tests fails
     */
    @Test
    public void newRequestFromString()
        throws Exception
    {
        new ExpectedTestFailure(NullPointerException.class) {
            @Override
            protected void execute()
                    throws Throwable
            {
                MarketDataRequest.newRequestFromString(null);
            }
        }.run();
        new ExpectedTestFailure(NullPointerException.class) {
            @Override
            protected void execute()
                    throws Throwable
            {
                MarketDataRequest.newRequestFromString(constructStringRepresentationOfMarketDataRequest(null,
                                                                                                        null,
                                                                                                        null,
                                                                                                        null,
                                                                                                        null,
                                                                                                        null));
            }
        }.run();
        // construct absolutely minimal representation (symbols only)
        verifyRequest(FULL_BOOK,
                      RequestType.SUBSCRIBE,
                      NO_EXCHANGE,
                      new String[] { "METC" },
                      UpdateType.INCREMENTAL_REFRESH,
                      MarketDataRequest.newRequestFromString(constructStringRepresentationOfMarketDataRequest(null,
                                                                                                              null,
                                                                                                              null,
                                                                                                              "METC",
                                                                                                              null,
                                                                                                              null)));
        verifyRequest(FULL_BOOK,
                      RequestType.SUBSCRIBE,
                      NO_EXCHANGE,
                      new String[] { "METC", "AAPL" },
                      UpdateType.INCREMENTAL_REFRESH,
                      MarketDataRequest.newRequestFromString(constructStringRepresentationOfMarketDataRequest(null,
                                                                                                              null,
                                                                                                              null,
                                                                                                              "METC,AAPL",
                                                                                                              null,
                                                                                                              null)));
        // due to how the string gets parsed, it's impossible to detect the presence of the KEY_VALUE_DELIMITER before the parsing operation removes it
        // therefore, we will just verify the expected result as documented
        final String exchange = "Exchange";
        final String extraStuff = "extra stuff";
        // test the key/value delimiter
        verifyRequest(FULL_BOOK,
                      RequestType.SUBSCRIBE,
                      exchange,
                      new String[] { "METC" },
                      UpdateType.INCREMENTAL_REFRESH,
                      MarketDataRequest.newRequestFromString(constructStringRepresentationOfMarketDataRequest(null,
                                                                                                              null,
                                                                                                              exchange + MarketDataRequest.KEY_VALUE_DELIMITER + extraStuff,
                                                                                                              "METC",
                                                                                                              null,
                                                                                                              null)));
        // test the symbol delimiter
        new ExpectedTestFailure(IllegalArgumentException.class) {
            @Override
            protected void execute()
                    throws Throwable
            {
                MarketDataRequest.newRequestFromString(constructStringRepresentationOfMarketDataRequest(null,
                                                                                                        null,
                                                                                                        exchange + MarketDataRequest.SYMBOL_DELIMITER + extraStuff,
                                                                                                        "METC",
                                                                                                        null,
                                                                                                        null));
            }
        }.run();
        // test the line separator
        new ExpectedTestFailure(IllegalArgumentException.class,
                                LINE_SEPARATOR_NOT_ALLOWED.getText()) {
            @Override
            protected void execute()
                    throws Throwable
            {
                MarketDataRequest.newRequestFromString(constructStringRepresentationOfMarketDataRequest(null,
                                                                                                        null,
                                                                                                        exchange + System.getProperty("line.separator") + extraStuff,
                                                                                                        "METC",
                                                                                                        null,
                                                                                                        null));
            }
        }.run();
        verifyRequest(FULL_BOOK,
                      RequestType.SUBSCRIBE,
                      exchange,
                      new String[] { "METC" },
                      UpdateType.INCREMENTAL_REFRESH,
                      MarketDataRequest.newRequestFromString(constructStringRepresentationOfMarketDataRequest(null,
                                                                                                              null,
                                                                                                              exchange + MarketDataRequest.KEY_VALUE_DELIMITER + extraStuff,
                                                                                                              "METC",
                                                                                                              null,
                                                                                                              null)));
        // test invalid values
        // invalid id
        new ExpectedTestFailure(IllegalArgumentException.class,
                                INVALID_ID.getText(-1)) {
            @Override
            protected void execute()
                    throws Throwable
            {
                MarketDataRequest.newRequestFromString(constructStringRepresentationOfMarketDataRequest("-1",
                                                                                                        null,
                                                                                                        null,
                                                                                                        "METC",
                                                                                                        null,
                                                                                                        null));
            }
        }.run();
        new ExpectedTestFailure(IllegalArgumentException.class,
                                "java.lang.NumberFormatException") {
            @Override
            protected void execute()
                    throws Throwable
            {
                MarketDataRequest.newRequestFromString(constructStringRepresentationOfMarketDataRequest("blap",
                                                                                                        null,
                                                                                                        null,
                                                                                                        "METC",
                                                                                                        null,
                                                                                                        null));
            }
        }.run();
        // invalid depth
        new ExpectedTestFailure(IllegalArgumentException.class,
                                INVALID_DEPTH.getText(-5000)) {
            @Override
            protected void execute()
                    throws Throwable
            {
                MarketDataRequest.newRequestFromString(constructStringRepresentationOfMarketDataRequest(null,
                                                                                                        "-5000",
                                                                                                        null,
                                                                                                        "METC",
                                                                                                        null,
                                                                                                        null));
            }
        }.run();
        new ExpectedTestFailure(IllegalArgumentException.class,
                                "java.lang.NumberFormatException") {
            @Override
            protected void execute()
                    throws Throwable
            {
                MarketDataRequest.newRequestFromString(constructStringRepresentationOfMarketDataRequest(null,
                                                                                                        "foobar",
                                                                                                        null,
                                                                                                        "METC",
                                                                                                        null,
                                                                                                        null));
            }
        }.run();
        // invalid update type
        new ExpectedTestFailure(IllegalArgumentException.class) {
            @Override
            protected void execute()
                    throws Throwable
            {
                MarketDataRequest.newRequestFromString(constructStringRepresentationOfMarketDataRequest(null,
                                                                                                        null,
                                                                                                        null,
                                                                                                        "METC",
                                                                                                        "not a valid update type",
                                                                                                        null));
            }
        }.run();
        // valid update types
        verifyRequest(FULL_BOOK,
                      RequestType.SUBSCRIBE,
                      NO_EXCHANGE,
                      new String[] { "METC" },
                      UpdateType.FULL_REFRESH,
                      MarketDataRequest.newRequestFromString(constructStringRepresentationOfMarketDataRequest(null,
                                                                                                              null,
                                                                                                              null,
                                                                                                              "METC",
                                                                                                              "Full_RefreSH",
                                                                                                              null)));
        verifyRequest(FULL_BOOK,
                      RequestType.SUBSCRIBE,
                      NO_EXCHANGE,
                      new String[] { "METC" },
                      UpdateType.INCREMENTAL_REFRESH,
                      MarketDataRequest.newRequestFromString(constructStringRepresentationOfMarketDataRequest(null,
                                                                                                              null,
                                                                                                              null,
                                                                                                              "METC",
                                                                                                              "INcremENTAL_REfreSH",
                                                                                                              null)));
        // invalid request type
        new ExpectedTestFailure(IllegalArgumentException.class) {
            @Override
            protected void execute()
                    throws Throwable
            {
                MarketDataRequest.newRequestFromString(constructStringRepresentationOfMarketDataRequest(null,
                                                                                                        null,
                                                                                                        null,
                                                                                                        "METC",
                                                                                                        null,
                                                                                                        "not a valid request type"));
            }
        }.run();
        // valid request types
        verifyRequest(FULL_BOOK,
                      RequestType.SNAPSHOT,
                      NO_EXCHANGE,
                      new String[] { "METC" },
                      UpdateType.INCREMENTAL_REFRESH,
                      MarketDataRequest.newRequestFromString(constructStringRepresentationOfMarketDataRequest(null,
                                                                                                              null,
                                                                                                              null,
                                                                                                              "METC",
                                                                                                              null,
                                                                                                              "SnApShOt")));
        verifyRequest(FULL_BOOK,
                      RequestType.SUBSCRIBE,
                      NO_EXCHANGE,
                      new String[] { "METC" },
                      UpdateType.INCREMENTAL_REFRESH,
                      MarketDataRequest.newRequestFromString(constructStringRepresentationOfMarketDataRequest(null,
                                                                                                              null,
                                                                                                              null,
                                                                                                              "METC",
                                                                                                              null,
                                                                                                              "sUbScRiBe")));
        verifyRequest(FULL_BOOK,
                      RequestType.CANCEL,
                      NO_EXCHANGE,
                      new String[] { "METC" },
                      UpdateType.INCREMENTAL_REFRESH,
                      MarketDataRequest.newRequestFromString(constructStringRepresentationOfMarketDataRequest(null,
                                                                                                              null,
                                                                                                              null,
                                                                                                              "METC",
                                                                                                              null,
                                                                                                              "canCEL")));
        // invalid symbols
        new ExpectedTestFailure(IllegalArgumentException.class,
                                INVALID_SYMBOLS.getText("[]")) {
            @Override
            protected void execute()
                    throws Throwable
            {
                MarketDataRequest.newRequestFromString(constructStringRepresentationOfMarketDataRequest(null,
                                                                                                        null,
                                                                                                        null,
                                                                                                        MarketDataRequest.SYMBOL_DELIMITER,
                                                                                                        null,
                                                                                                        null));
                }
        }.run();
        new ExpectedTestFailure(IllegalArgumentException.class,
                                INVALID_SYMBOLS.getText("[]")) {
            @Override
            protected void execute()
                    throws Throwable
            {
                MarketDataRequest.newRequestFromString(constructStringRepresentationOfMarketDataRequest(null,
                                                                                                        null,
                                                                                                        null,
                                                                                                        MarketDataRequest.KEY_VALUE_DELIMITER,
                                                                                                        null,
                                                                                                        null));
                }
        }.run();
        new ExpectedTestFailure(IllegalArgumentException.class,
                                LINE_SEPARATOR_NOT_ALLOWED.getText()) {
            @Override
            protected void execute()
                    throws Throwable
            {
                MarketDataRequest.newRequestFromString(constructStringRepresentationOfMarketDataRequest(null,
                                                                                                        null,
                                                                                                        null,
                                                                                                        System.getProperty("line.separator"),
                                                                                                        null,
                                                                                                        null));
                }
        }.run();
        new ExpectedTestFailure(IllegalArgumentException.class,
                                INVALID_SYMBOLS.getText("[, METC]")) {
            @Override
            protected void execute()
                    throws Throwable
            {
                MarketDataRequest.newRequestFromString(constructStringRepresentationOfMarketDataRequest(null,
                                                                                                        null,
                                                                                                        null,
                                                                                                        ",METC", // empty first symbol
                                                                                                        null,
                                                                                                        null));
                }
        }.run();
        // TODO
//        // valid with non-ASCII symbol and exchange
//        verifyRequest(FULL_BOOK,
//                      RequestType.SUBSCRIBE,
//                      UnicodeData.GOODBYE_JA,
//                      new String[] { UnicodeData.HELLO_GR },
//                      UpdateType.INCREMENTAL_REFRESH,
//                      MarketDataRequest.newRequestFromString(constructStringRepresentationOfMarketDataRequest(null,
//                                                                                                              null,
//                                                                                                              UnicodeData.GOODBYE_JA,
//                                                                                                              UnicodeData.HELLO_GR,
//                                                                                                              null,
//                                                                                                              null)));
    }
    @Test
    public void testEquals()
        throws Exception
    {
        MarketDataRequest r1 = MarketDataRequest.newFullBookRequest("METC");
        MarketDataRequest r2 = MarketDataRequest.newFullBookRequest("ORCL");
        assertFalse(r1.equals(null));
        assertFalse(r1.equals(this));
        assertFalse(r1.equals(r2));
        assertEquals(r1,
                     r1);
    }
    /**
     * Creates a <code>String</code> containing the given elements for use as a <code>MarketDataRequest</code>.
     *
     * @param inId a <code>String</code> value containing the string representation of a <code>long</code> or null
     * @param inDepth a <code>String</code> value containing the string representation of an <code>int</code> or null
     * @param inExchange a <code>String</code> value or null
     * @param inSymbols a <code>String</code> value or null
     * @param inUpdateType a <code>String</code> value containing the string representation of an <code>UpdateType</code> or null
     * @param inRequestType a <code>String</code> value containing the string representation of an <code>RequestType</code> or null
     * @return a <code>String</code> value suitable for use as a <code>MarketDataRequest</code>
     */
    private String constructStringRepresentationOfMarketDataRequest(String inId,
                                                                    String inDepth,
                                                                    String inExchange,
                                                                    String inSymbols,
                                                                    String inUpdateType,
                                                                    String inRequestType)
    {
        StringBuilder requestAsString = new StringBuilder();
        if(inId != null) {
            requestAsString.append("id=").append(inId).append(":");
        }
        if(inDepth != null) {
            requestAsString.append("depth=").append(inDepth).append(":");
        }
        if(inExchange != null) {
            requestAsString.append("exchange=").append(inExchange).append(":");
        }
        if(inSymbols != null) {
            requestAsString.append("symbols=").append(inSymbols).append(":");
        }
        if(inUpdateType != null) {
            requestAsString.append("updateType=").append(inUpdateType).append(":");
        }
        if(inRequestType != null) {
            requestAsString.append("requestType=").append(inRequestType).append(":");
        }
        return requestAsString.toString();
    }
    /**
     * Verifies the actual <code>MarketDataRequest</code> matches the expected values.
     *
     * @param inExpectedDepth an <code>int</code> value indicating the depth of the order book to request
     * @param inExpectedRequestType a <code>RequestType</code> value indicating whether to subscribe to the results or just get a single snapshot
     * @param inExpectedExchange a <code>String</code> value indicating the exchange from which to request the data
     * @param inExpectedSymbols a <code>String[]</code> value containing the symbols for which to request data
     * @param inExpectedUpdateType an <code>UpdateType</code> value indicating how updates should be received
     * @param inActualRequest a <code>MarketDataRequest</code> value against which to compare the expected values
     * @throws Exception if one of the tests fails
     */
    private void verifyRequest(int inExpectedDepth,
                               RequestType inExpectedRequestType,
                               String inExpectedExchange,
                               String[] inExpectedSymbols,
                               UpdateType inExpectedUpdateType,
                               MarketDataRequest inActualRequest)
        throws Exception
    {
        long id = inActualRequest.getId();
        assertTrue(id > 0);
        assertFalse("ID " + id + " has already been used",
                    idsSeenSoFar.contains(id));
        idsSeenSoFar.add(id);
        assertEquals(inExpectedDepth,
                     inActualRequest.getDepth());
        assertEquals(inExpectedRequestType,
                     inActualRequest.getRequestType());
        assertEquals(inExpectedExchange,
                     inActualRequest.getExchange());
        assertTrue(Arrays.equals(inExpectedSymbols,
                                 inActualRequest.getSymbols()));
        assertEquals(inExpectedUpdateType,
                     inActualRequest.getUpdateType());
        // test the ability to write this object to a string and read it back
        assertEquals(inActualRequest,
                     MarketDataRequest.newRequestFromString(inActualRequest.toString()));
    }
    /**
     * used to record ids used so far
     */
    private final Set<Long> idsSeenSoFar = new HashSet<Long>();
}
