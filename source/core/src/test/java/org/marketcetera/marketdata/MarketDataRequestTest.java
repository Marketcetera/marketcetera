package org.marketcetera.marketdata;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.marketcetera.marketdata.MarketDataRequest.FULL_BOOK;
import static org.marketcetera.marketdata.MarketDataRequest.NO_EXCHANGE;
import static org.marketcetera.marketdata.MarketDataRequest.TOP_OF_BOOK;
import static org.marketcetera.marketdata.Messages.INVALID_DEPTH;
import static org.marketcetera.marketdata.Messages.INVALID_ID;
import static org.marketcetera.marketdata.Messages.INVALID_STRING_VALUE;
import static org.marketcetera.marketdata.Messages.INVALID_SYMBOLS;
import static org.marketcetera.marketdata.Messages.MISSING_REQUEST_TYPE;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.marketcetera.core.ExpectedTestFailure;
import org.marketcetera.core.Util;
import org.marketcetera.marketdata.DataRequestTest.MockDataRequest;
import org.marketcetera.marketdata.MarketDataRequest.RequestType;
import org.marketcetera.marketdata.MarketDataRequest.UpdateType;

/* $License$ */

/**
 * Tests {@link MarketDataRequest}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 1.0.0
 */
public class MarketDataRequestTest
{
    @Before
    public void setup()
    {
        assertNotNull(DataRequest.TYPE_KEY);
    }
    @Test
    public void newFullBookRequest()
        throws Exception
    {
        new ExpectedTestFailure(IllegalArgumentException.class,
                                INVALID_SYMBOLS.getText("null")) {
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
    public void newFullBookSnapshotRequest()
        throws Exception
    {
        new ExpectedTestFailure(IllegalArgumentException.class,
                                INVALID_SYMBOLS.getText("null")) {
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
                      RequestType.SNAPSHOT,
                      NO_EXCHANGE,
                      new String[] { "JAVA" },
                      UpdateType.FULL_REFRESH,
                      MarketDataRequest.newFullBookSnapshotRequest("JAVA"));
        // multiple symbols
        verifyRequest(FULL_BOOK,
                      RequestType.SNAPSHOT,
                      NO_EXCHANGE,
                      new String[] { "GOOG", "MSFT", "YHOO" },
                      UpdateType.FULL_REFRESH,
                      MarketDataRequest.newFullBookSnapshotRequest("GOOG",
                                                                   "MSFT",
                                                                   "YHOO"));
    }
    @Test
    public void newTopOfBookRequest()
        throws Exception
    {
        new ExpectedTestFailure(IllegalArgumentException.class,
                                INVALID_SYMBOLS.getText("null")) {
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
        new ExpectedTestFailure(IllegalArgumentException.class,
                                INVALID_SYMBOLS.getText("null")) {
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
        new ExpectedTestFailure(IllegalArgumentException.class,
                                INVALID_DEPTH.getText("-2147483648")) {
            @Override
            protected void execute()
                    throws Throwable
            {
                MarketDataRequest.newSpecifedDepthRequest(Integer.MAX_VALUE + 1,
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
                DataRequest.newRequestFromString(null);
            }
        }.run();
        new ExpectedTestFailure(IllegalArgumentException.class,
                                INVALID_SYMBOLS.getText("null")) {
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
                      DataRequest.newRequestFromString(constructStringRepresentationOfMarketDataRequest(null,
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
        // add the id
        verifyRequest(FULL_BOOK,
                      RequestType.SUBSCRIBE,
                      NO_EXCHANGE,
                      new String[] { "METC", "AAPL" },
                      UpdateType.INCREMENTAL_REFRESH,
                      MarketDataRequest.newRequestFromString(constructStringRepresentationOfMarketDataRequest("100010",
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
                                                                                                              exchange + Util.KEY_VALUE_DELIMITER + extraStuff,
                                                                                                              "METC",
                                                                                                              null,
                                                                                                              null)));
        // test the symbol delimiter
        final String symbolString = exchange + MarketDataRequest.SYMBOL_DELIMITER + extraStuff;
        new ExpectedTestFailure(IllegalArgumentException.class,
                                INVALID_STRING_VALUE.getText(symbolString)) {
            @Override
            protected void execute()
                    throws Throwable
            {
                MarketDataRequest.newRequestFromString(constructStringRepresentationOfMarketDataRequest(null,
                                                                                                        null,
                                                                                                        symbolString,
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
                                                                                                              exchange + Util.KEY_VALUE_DELIMITER + extraStuff,
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
                                INVALID_ID.getText("blap")) {
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
        // valid depth
        verifyRequest(100,
                      RequestType.SUBSCRIBE,
                      NO_EXCHANGE,
                      new String[] { "METC" },
                      UpdateType.INCREMENTAL_REFRESH,
                      DataRequest.newRequestFromString(constructStringRepresentationOfMarketDataRequest(null,
                                                                                                        "100",
                                                                                                        null,
                                                                                                        "METC",
                                                                                                        null,
                                                                                                        null)));
        // invalid depth
        new ExpectedTestFailure(IllegalArgumentException.class,
                                INVALID_DEPTH.getText("-5000")) {
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
                                INVALID_DEPTH.getText("foobar")) {
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
        new ExpectedTestFailure(IllegalArgumentException.class) {
            @Override
            protected void execute()
                    throws Throwable
            {
                MarketDataRequest.newRequestFromString(constructStringRepresentationOfMarketDataRequest(null,
                                                                                                        null,
                                                                                                        null,
                                                                                                        Util.KEY_VALUE_DELIMITER,
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
        // missing request type
        new ExpectedTestFailure(IllegalArgumentException.class,
                                MISSING_REQUEST_TYPE.getText()) {
            @Override
            protected void execute()
                    throws Throwable
            {
                MarketDataRequest.newRequestFromString("symbols=METC,AAPL");
            }
        }.run();
    }
    @Test
    public void addCurrentAttributesValues()
        throws Exception
    {
        final MarketDataRequest request = MarketDataRequest.newFullBookRequest("METC");
        new ExpectedTestFailure(NullPointerException.class) {
            @Override
            protected void execute()
                    throws Throwable
            {
                request.addCurrentAttributesValues(null);
            }
        }.run();
        Properties properties = new Properties();
        request.addCurrentAttributesValues(properties);
        assertEquals(request.getDepth(),
                     Integer.parseInt(properties.getProperty(MarketDataRequest.DEPTH_KEY)));
        assertEquals(request.getExchange(),
                     properties.getProperty(MarketDataRequest.EXCHANGE_KEY));
        assertEquals(request.getRequestType(),
                     RequestType.valueOf(properties.getProperty(MarketDataRequest.REQUEST_TYPE_KEY)));
        assertTrue(Arrays.equals(request.getSymbols(),
                                 properties.getProperty(MarketDataRequest.SYMBOLS_KEY).split(MarketDataRequest.SYMBOL_DELIMITER)));
        assertEquals(request.getUpdateType(),
                     UpdateType.valueOf(properties.getProperty(MarketDataRequest.UPDATE_TYPE_KEY)));
    }
    @Test
    public void doEquals()
        throws Exception
    {
        MarketDataRequest r1 = MarketDataRequest.newFullBookRequest("METC");
        MarketDataRequest r2 = MarketDataRequest.newFullBookRequest("ORCL");
        assertFalse(r1.equals(null));
        assertFalse(r1.equals(this));
        assertFalse(r1.hashCode() == this.hashCode());
        assertFalse(r1.equals(r2));
        assertFalse(r1.hashCode() == r2.hashCode());
        assertEquals(r1,
                     r1);
    }
    @Test
    public void equivalent()
        throws Exception
    {
        MarketDataRequest r1 = MarketDataRequest.newFullBookRequest("METC");
        MarketDataRequest r2 = MarketDataRequest.newFullBookRequest("ORCL");
        MarketDataRequest r3 = MarketDataRequest.newFullBookRequest("ORCL");
        MockDataRequest.doRegister();
        MockDataRequest r4 = (MockDataRequest)DataRequest.newRequestFromString(DataRequestTest.constructStringRepresentationOfDataRequest(null,
                                                                                                                                          MockDataRequest.TYPE,
                                                                                                                                          "-1",
                                                                                                                                          "false",
                                                                                                                                          "booya"));
        MarketDataRequest r5 = MarketDataRequest.newTopOfBookRequest("ORCL");
        MarketDataRequest r6 = (MarketDataRequest)DataRequest.newRequestFromString(constructStringRepresentationOfMarketDataRequest(null, 
                                                                                                                                    Integer.toString(r2.getDepth()),
                                                                                                                                    "some exchange which is not the same",
                                                                                                                                    "ORCL",
                                                                                                                                    r2.getUpdateType().toString(),
                                                                                                                                    r2.getRequestType().toString()));
        MarketDataRequest r7 = (MarketDataRequest)DataRequest.newRequestFromString(constructStringRepresentationOfMarketDataRequest(null, 
                                                                                                                                    Integer.toString(r2.getDepth()),
                                                                                                                                    r2.getExchange(),
                                                                                                                                    "ORCL",
                                                                                                                                    UpdateType.FULL_REFRESH.toString(),
                                                                                                                                    r2.getRequestType().toString()));
        MarketDataRequest r8 = (MarketDataRequest)DataRequest.newRequestFromString(constructStringRepresentationOfMarketDataRequest(null, 
                                                                                                                                    Integer.toString(r2.getDepth()),
                                                                                                                                    r2.getExchange(),
                                                                                                                                    "ORCL",
                                                                                                                                    r2.getUpdateType().toString(),
                                                                                                                                    RequestType.SNAPSHOT.toString()));
        assertTrue(r1.equivalent(r1));
        assertTrue(r2.equivalent(r3));
        assertFalse(r1.equivalent(null));
        assertFalse(r1.equivalent(r2));
        assertFalse(r1.equivalent(r4));
        assertFalse(r2.equivalent(r5));
        assertFalse(r2.equivalent(r6));
        assertFalse(r2.equivalent(r7));
        assertFalse(r2.equivalent(r8));
    }
    @Test
    public void validateAdnSetRequestDefaultsIfNecessary()
        throws Exception
    {
        new ExpectedTestFailure(NullPointerException.class) {
            @Override
            protected void execute()
                    throws Throwable
            {
                MarketDataRequest.validateAndSetRequestDefaultsIfNecessary(null);
            }
        }.run();
        Properties properties = new Properties();
        MarketDataRequest.validateAndSetRequestDefaultsIfNecessary(properties);
        assertTrue(Long.parseLong(properties.getProperty(DataRequest.ID_KEY)) > 0);
        assertEquals(FULL_BOOK,
                     Integer.parseInt(properties.getProperty(MarketDataRequest.DEPTH_KEY)));
        assertEquals(RequestType.SUBSCRIBE,
                     RequestType.valueOf(properties.getProperty(MarketDataRequest.REQUEST_TYPE_KEY)));
        assertEquals(UpdateType.INCREMENTAL_REFRESH,
                     UpdateType.valueOf(properties.getProperty(MarketDataRequest.UPDATE_TYPE_KEY)));
        assertEquals(NO_EXCHANGE,
                     properties.getProperty(MarketDataRequest.EXCHANGE_KEY));
        // no default for type or symbols
        assertNull(properties.getProperty(DataRequest.TYPE_KEY));
        assertNull(properties.getProperty(MarketDataRequest.SYMBOLS_KEY));
        // now, make sure the properties are not overridden if already specified
        // first, change properties to non-default values
        assertTrue(Long.parseLong(properties.getProperty(DataRequest.ID_KEY)) != 0);
        properties.setProperty(DataRequest.ID_KEY,
                               "0");
        assertFalse(properties.getProperty(MarketDataRequest.DEPTH_KEY).equals("100"));
        properties.setProperty(MarketDataRequest.DEPTH_KEY,
                               "100");
        assertFalse(properties.getProperty(MarketDataRequest.REQUEST_TYPE_KEY).equals(RequestType.SNAPSHOT.toString()));
        properties.setProperty(MarketDataRequest.REQUEST_TYPE_KEY,
                               RequestType.SNAPSHOT.toString());
        assertFalse(properties.getProperty(MarketDataRequest.UPDATE_TYPE_KEY).equals(UpdateType.FULL_REFRESH.toString()));
        properties.setProperty(MarketDataRequest.UPDATE_TYPE_KEY,
                               UpdateType.FULL_REFRESH.toString());
        assertFalse(properties.getProperty(MarketDataRequest.EXCHANGE_KEY).equals("some other exchange"));
        properties.setProperty(MarketDataRequest.EXCHANGE_KEY,
                               "some other exchange");
        MarketDataRequest.validateAndSetRequestDefaultsIfNecessary(properties);
        assertEquals(100,
                     Integer.parseInt(properties.getProperty(MarketDataRequest.DEPTH_KEY)));
        assertEquals(RequestType.SNAPSHOT,
                     RequestType.valueOf(properties.getProperty(MarketDataRequest.REQUEST_TYPE_KEY)));
        assertEquals(UpdateType.FULL_REFRESH,
                     UpdateType.valueOf(properties.getProperty(MarketDataRequest.UPDATE_TYPE_KEY)));
        assertEquals("some other exchange",
                     properties.getProperty(MarketDataRequest.EXCHANGE_KEY));
        // no default for type or symbols
        assertNull(properties.getProperty(DataRequest.TYPE_KEY));
        assertNull(properties.getProperty(MarketDataRequest.SYMBOLS_KEY));
    }
    @Test
    public void validateStringValue()
        throws Exception
    {
        new ExpectedTestFailure(NullPointerException.class) {
            @Override
            protected void execute()
                    throws Throwable
            {
                MarketDataRequest.validateStringValue(null);
            }
        }.run();
        final String[] values = new String[1];
        // don't have to test all parent issues as they're already tested, but do test one to make sure
        //  the parent validation gets called
        values[0] = "some stuff" + Util.KEY_VALUE_DELIMITER + " some other stuff";
        new ExpectedTestFailure(IllegalArgumentException.class,
                                INVALID_STRING_VALUE.getText(values[0])) {
            @Override
            protected void execute()
                    throws Throwable
            {
                MarketDataRequest.validateStringValue(values[0]);
            }
        }.run();
        values[0] = "some stuff" + MarketDataRequest.SYMBOL_DELIMITER + " some other stuff";
        new ExpectedTestFailure(IllegalArgumentException.class,
                                INVALID_STRING_VALUE.getText(values[0])) {
            @Override
            protected void execute()
                    throws Throwable
            {
                MarketDataRequest.validateStringValue(values[0]);
            }
        }.run();
        assertEquals("some stuff",
                     MarketDataRequest.validateStringValue("some stuff"));
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
        requestAsString.append(DataRequest.TYPE_KEY).append("=").append(MarketDataRequest.TYPE).append(":");
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
     * @param marketDataRequest a <code>MarketDataRequest</code> value against which to compare the expected values
     * @throws Exception if one of the tests fails
     */
    private void verifyRequest(int inExpectedDepth,
                               RequestType inExpectedRequestType,
                               String inExpectedExchange,
                               String[] inExpectedSymbols,
                               UpdateType inExpectedUpdateType,
                               DataRequest inActualRequest)
        throws Exception
    {
        MarketDataRequest marketDataRequest = (MarketDataRequest)inActualRequest;
        long id = marketDataRequest.getId();
        assertTrue(id > 0);
        assertFalse("ID " + id + " has already been used",
                    idsSeenSoFar.contains(id));
        idsSeenSoFar.add(id);
        assertEquals(inExpectedDepth,
                     marketDataRequest.getDepth());
        assertEquals(inExpectedRequestType,
                     marketDataRequest.getRequestType());
        assertEquals(inExpectedExchange,
                     marketDataRequest.getExchange());
        assertTrue(Arrays.equals(inExpectedSymbols,
                                 marketDataRequest.getSymbols()));
        assertEquals(inExpectedUpdateType,
                     marketDataRequest.getUpdateType());
        // test the ability to write this object to a string and read it back
        assertEquals(marketDataRequest,
                     MarketDataRequest.newRequestFromString(marketDataRequest.toString()));
    }
    /**
     * used to record ids used so far
     */
    private final Set<Long> idsSeenSoFar = new HashSet<Long>();
}
