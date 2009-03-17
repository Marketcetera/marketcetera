package org.marketcetera.marketdata;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.marketcetera.marketdata.MarketDataRequest.CONTENT_KEY;
import static org.marketcetera.marketdata.MarketDataRequest.DATE_KEY;
import static org.marketcetera.marketdata.MarketDataRequest.EXCHANGE_KEY;
import static org.marketcetera.marketdata.MarketDataRequest.PROVIDER_KEY;
import static org.marketcetera.marketdata.MarketDataRequest.SYMBOLS_KEY;
import static org.marketcetera.marketdata.MarketDataRequest.TYPE_KEY;
import static org.marketcetera.marketdata.MarketDataRequest.Content.LEVEL_2;
import static org.marketcetera.marketdata.MarketDataRequest.Content.OHLC;
import static org.marketcetera.marketdata.MarketDataRequest.Content.TOP_OF_BOOK;
import static org.marketcetera.marketdata.MarketDataRequest.Type.SNAPSHOT;
import static org.marketcetera.marketdata.MarketDataRequest.Type.SUBSCRIPTION;
import static org.marketcetera.marketdata.Messages.INVALID_CONTENT;
import static org.marketcetera.marketdata.Messages.INVALID_DATE;
import static org.marketcetera.marketdata.Messages.INVALID_REQUEST;
import static org.marketcetera.marketdata.Messages.INVALID_SYMBOLS;
import static org.marketcetera.marketdata.Messages.INVALID_TYPE;
import static org.marketcetera.marketdata.Messages.MISSING_CONTENT;
import static org.marketcetera.marketdata.Messages.MISSING_PROVIDER;
import static org.marketcetera.marketdata.Messages.MISSING_SYMBOLS;
import static org.marketcetera.marketdata.Messages.MISSING_TYPE;
import static org.marketcetera.marketdata.Messages.OHLC_NO_DATE;

import java.text.DateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;

import org.junit.BeforeClass;
import org.junit.Test;
import org.marketcetera.core.Util;
import org.marketcetera.marketdata.MarketDataRequest.Content;
import org.marketcetera.marketdata.MarketDataRequest.Type;
import org.marketcetera.module.ExpectedFailure;
import org.marketcetera.util.log.SLF4JLoggerProxy;

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
    private static final String[] symbols = new String[] { null, "", " ", "METC", "GOOG,ORCL" };
    private static final String[][] symbolArrays = new String[][] { {}, { ""," " }, { "METC" }, { "GOOG","ORCL" } };
    private static final String[] providers = new String[] { null, "", "invalid-provider", "bogus" };
    private static final String[] exchanges = new String[] { null, "", "invalid-exchange", "Q" };
    private static final Content[] contents = new Content[] { null, TOP_OF_BOOK, OHLC };
    private static final Type[] types = new Type[] { null, SNAPSHOT, SUBSCRIPTION };
    private static final Date[] dates = new Date[] { null, new Date(0), new Date(System.currentTimeMillis()-250) };
    // alternate representation of values
    private static final String[] contentStrings = new String[] { null, "", "not-a-content", "TOP_OF_BOOK", "level_2", "OhLc" };
    private static final String[] typeStrings = new String[] { null, "", "snapshot", "SNAPSHOT", "SuBsCripTioN" };
    private static final long[] dateLongs = new long[] { 0l, -100l, System.currentTimeMillis()-250 };
    private static final String[] dateStrings = new String[] { null, "", MarketDataRequest.DateUtils.dateToString(new Date(0)), MarketDataRequest.DateUtils.dateToString(new Date(System.currentTimeMillis()-250)) };
    // variations of keys
    private static final int keyCount = 4;
    private static final String[] symbolKeys = new String[] { "symbols", "SYMBOLS", "SyMbOlS", MarketDataRequest.SYMBOLS_KEY };
    private static final String[] providerKeys = new String[] { "provider", "PROVIDER", "PrOvIdEr", MarketDataRequest.PROVIDER_KEY };
    private static final String[] exchangeKeys = new String[] { "exchange", "EXCHANGE", "ExChAnGe", MarketDataRequest.EXCHANGE_KEY };
    private static final String[] contentKeys = new String[] { "content", "CONTENT", "CoNtEnT", MarketDataRequest.CONTENT_KEY };
    private static final String[] typeKeys = new String[] { "type", "TYPE", "TyPe", MarketDataRequest.TYPE_KEY };
    private static final String[] dateKeys = new String[] { "date", "DATE", "DaTe", MarketDataRequest.DATE_KEY };
    private static final Set<String> ALL_CONTENTS = new HashSet<String>();
    private static final Set<String> ALL_TYPES = new HashSet<String>();
    private static DateFormat testDateFormat;
    /**
     * Initialization that needs to be run once for all tests.
     */
    @BeforeClass
    public static void runOnce()
    {
        for(Content content : Content.values()) {
            ALL_CONTENTS.add(content.toString());
        }
        for(Type type : Type.values()) {
            ALL_TYPES.add(type.toString());
        }
        testDateFormat = DateFormat.getDateTimeInstance(DateFormat.FULL,
                                                        DateFormat.FULL,
                                                        Locale.US);
        testDateFormat.setTimeZone(TimeZone.getTimeZone("America/New_York"));
    }
    /**
     * Tests the variations of keys that {@link MarketDataRequest#newRequestFromString(String)} can accept.
     *
     * @throws Exception if an error occurs
     */
    @Test
    public void requestsFromStringKeyVariations()
        throws Exception
    {
        assertEquals(keyCount,
                     symbolKeys.length);
        assertEquals(keyCount,
                     providerKeys.length);
        assertEquals(keyCount,
                     exchangeKeys.length);
        assertEquals(keyCount,
                     contentKeys.length);
        assertEquals(keyCount,
                     typeKeys.length);
        assertEquals(keyCount,
                     dateKeys.length);
        String symbols = "METC";
        String provider = "provider";
        String exchange = "exchange";
        Type type = SNAPSHOT;
        Content content = OHLC;
        Date date = new Date(System.currentTimeMillis()-250);
        for(int keyCounter=0;keyCounter<keyCount;keyCounter++) {
            String requestString = String.format("%s=%s:%s=%s:%s=%s:%s=%s:%s=%s:%s=%s",
                                                 symbolKeys[keyCounter],
                                                 symbols,
                                                 providerKeys[keyCounter],
                                                 provider,
                                                 exchangeKeys[keyCounter],
                                                 exchange,
                                                 contentKeys[keyCounter],
                                                 content,
                                                 typeKeys[keyCounter],
                                                 type,
                                                 dateKeys[keyCounter],
                                                 MarketDataRequest.DateUtils.dateToString(date));
            verifyRequest(MarketDataRequest.newRequestFromString(requestString),
                          provider,
                          exchange,
                          content,
                          type,
                          date,
                          symbols);
        }
    }
    /**
     * Tests the creation of a {@link MarketDataRequest} from a <code>String</code>.
     *
     * @throws Exception if an error occurs
     */
    @Test
    public void requestsFromString()
        throws Exception
    {
        new ExpectedFailure<MarketDataRequestException>(INVALID_REQUEST) {
            protected void run()
                throws Exception
            {
                MarketDataRequest.newRequestFromString(null);
            }
        };
        new ExpectedFailure<MarketDataRequestException>(INVALID_REQUEST) {
            protected void run()
                throws Exception
            {
                MarketDataRequest.newRequestFromString("");
            }
        };
        for(int contentsCounter=0;contentsCounter<contents.length;contentsCounter++) {
            for(int providersCounter=0;providersCounter<providers.length;providersCounter++) {
                for(int symbolsCounter=0;symbolsCounter<symbols.length;symbolsCounter++) {
                    for(int exchangesCounter=0;exchangesCounter<exchanges.length;exchangesCounter++) {
                        for(int datesCounter=0;datesCounter<dates.length;datesCounter++) {
                            for(int typesCounter=0;typesCounter<types.length;typesCounter++) {
                                SLF4JLoggerProxy.debug(this,
                                                       String.format("%d:%d:%d:%d:%d:%d",
                                                                     contentsCounter,
                                                                     providersCounter,
                                                                     symbolsCounter,
                                                                     exchangesCounter,
                                                                     datesCounter,
                                                                     typesCounter));
                                final String requestString = marketDataRequestStringFromComponents(providers[providersCounter],
                                                                                                   exchanges[exchangesCounter],
                                                                                                   contents[contentsCounter],
                                                                                                   types[typesCounter],
                                                                                                   dates[datesCounter],
                                                                                                   symbols[symbolsCounter]);
                                // catch-all for a totally empty request
                                if(requestString.isEmpty()) {
                                    new ExpectedFailure<MarketDataRequestException>(INVALID_REQUEST) {
                                        protected void run()
                                            throws Exception
                                        {
                                            MarketDataRequest.newRequestFromString(requestString);
                                        }
                                    };
                                    continue;
                                }
                                // symbols error conditions
                                if(isEmptySymbolList(symbols[symbolsCounter])) {
                                    new ExpectedFailure<MarketDataRequestException>(MISSING_SYMBOLS) {
                                        protected void run()
                                            throws Exception
                                        {
                                            MarketDataRequest.newRequestFromString(requestString);
                                        }
                                    };
                                    continue;
                                }
                                // providers error conditions
                                if(providers[providersCounter] == null ||
                                   providers[providersCounter].isEmpty()) {
                                    new ExpectedFailure<MarketDataRequestException>(MISSING_PROVIDER) {
                                        protected void run()
                                            throws Exception
                                        {
                                            MarketDataRequest.newRequestFromString(requestString);
                                        }
                                    };
                                    continue;
                                }
                                // contents error condition
                                if(contents[contentsCounter] == OHLC &&
                                   dates[datesCounter] == null) {
                                    new ExpectedFailure<MarketDataRequestException>(OHLC_NO_DATE) {
                                        protected void run()
                                            throws Exception
                                        {
                                            MarketDataRequest.newRequestFromString(requestString);
                                        }
                                    };
                                    continue;
                                }
                                MarketDataRequest request = MarketDataRequest.newRequestFromString(requestString);
                                verifyRequest(request,
                                              providers[providersCounter],
                                              exchanges[exchangesCounter],
                                              contentsCounter == 0 ? TOP_OF_BOOK : contents[contentsCounter],
                                              typesCounter == 0 ? SUBSCRIPTION : types[typesCounter],
                                              dates[datesCounter],
                                              symbols[symbolsCounter]);
                            }
                        }
                    }
                }
            }
        }
    }
    /**
     * Tests {@link MarketDataRequest#ofType(Type)} and {@link MarketDataRequest#ofType(String)}.
     *
     * @throws Exception if an error occurs
     */
    @Test
    public void type()
        throws Exception
    {
        final String symbols = "METC";
        final String provider = "provider";
        // test strings version of ofType
        for(int typeCounter=0;typeCounter<typeStrings.length;typeCounter++) {
            final String typeString = typeStrings[typeCounter];
            if(typeString == null ||
               typeString.isEmpty() ||
               !ALL_TYPES.contains(typeString.toUpperCase())) {
                new ExpectedFailure<MarketDataRequestException>(INVALID_TYPE) {
                    protected void run()
                        throws Exception
                    {
                        MarketDataRequest.newRequest().fromProvider(provider).withSymbols(symbols).ofType(typeString);                    
                    }
                };
            } else {
                MarketDataRequest request = MarketDataRequest.newRequest().fromProvider(provider).withSymbols(symbols).ofType(typeString); 
                verifyRequest(request,
                              provider,
                              null,
                              TOP_OF_BOOK,
                              Type.valueOf(typeString.toUpperCase()),
                              null,
                              symbols);
            }
        }
        // test enum version of ofType
        for(int typeCounter=0;typeCounter<types.length;typeCounter++) {
            final Type type = types[typeCounter];
            if(type == null) {
                new ExpectedFailure<MarketDataRequestException>(MISSING_TYPE) {
                    protected void run()
                        throws Exception
                    {
                        MarketDataRequest.newRequest().fromProvider(provider).withSymbols(symbols).ofType(type);                    
                    }
                };
            } else {
                MarketDataRequest request = MarketDataRequest.newRequest().fromProvider(provider).withSymbols(symbols).ofType(type); 
                verifyRequest(request,
                              provider,
                              null,
                              TOP_OF_BOOK,
                              type,
                              null,
                              symbols);
            }
        }
        // test that changing the source doesn't change the held value
        Type type = SUBSCRIPTION;
        MarketDataRequest request = MarketDataRequest.newRequest().ofType(type);
        assertEquals(SUBSCRIPTION,
                     request.getType());
        type = SNAPSHOT;
        assertEquals(SUBSCRIPTION,
                     request.getType());
        // test that changing the destination doesn't change the held value
        type = request.getType();
        assertEquals(SUBSCRIPTION,
                     type);
        type = SNAPSHOT;
        assertEquals(SUBSCRIPTION,
                     request.getType());
    }
    /**
     * Tests {@link MarketDataRequest#withContent(Content)} and {@link MarketDataRequest#withContent(String)}.
     *
     * @throws Exception if an error occurs
     */
    @Test
    public void content()
        throws Exception
    {
        final String symbols = "METC";
        final String provider = "provider";
        final Date date = new Date();
        // test strings version of withContent
        for(int contentCounter=0;contentCounter<contentStrings.length;contentCounter++) {
            final String contentString = contentStrings[contentCounter];
            if(contentString == null ||
               contentString.isEmpty() ||
               !ALL_CONTENTS.contains(contentString.toUpperCase())) {
                new ExpectedFailure<MarketDataRequestException>(INVALID_CONTENT) {
                    protected void run()
                        throws Exception
                    {
                        MarketDataRequest.newRequest().fromProvider(provider).withSymbols(symbols).withContent(contentString).asOf(date);                    
                    }
                };
            } else {
                MarketDataRequest request = MarketDataRequest.newRequest().fromProvider(provider).withSymbols(symbols).withContent(contentString).asOf(date); 
                verifyRequest(request,
                              provider,
                              null,
                              Content.valueOf(contentString.toUpperCase()),
                              SUBSCRIPTION,
                              date,
                              symbols);
            }
        }
        // test enum version of withContent
        for(int contentCounter=0;contentCounter<contents.length;contentCounter++) {
            final Content content = contents[contentCounter];
            if(content == null) {
                new ExpectedFailure<MarketDataRequestException>(MISSING_CONTENT) {
                    protected void run()
                        throws Exception
                    {
                        MarketDataRequest.newRequest().fromProvider(provider).withSymbols(symbols).withContent(content).asOf(date);                    
                    }
                };
            } else {
                MarketDataRequest request = MarketDataRequest.newRequest().fromProvider(provider).withSymbols(symbols).withContent(content).asOf(date); 
                verifyRequest(request,
                              provider,
                              null,
                              content,
                              SUBSCRIPTION,
                              date,
                              symbols);
            }
        }
        // test that changing the source doesn't change the held value
        Content content = LEVEL_2;
        MarketDataRequest request = MarketDataRequest.newRequest().withContent(content);
        assertEquals(LEVEL_2,
                     request.getContent());
        content = TOP_OF_BOOK;
        assertEquals(LEVEL_2,
                     request.getContent());
        // test that changing the destination doesn't change the held value
        content = request.getContent();
        assertEquals(LEVEL_2,
                     content);
        content = TOP_OF_BOOK;
        assertEquals(LEVEL_2,
                     request.getContent());
    }
    /**
     * Tests the ability to set and retrieve symbols. 
     *
     * @throws Exception if an error occurs
     */
    @Test
    public void symbol()
        throws Exception
    {
        final String provider = "provider";
        // test array version of withSymbols
        for(int symbolCounter=0;symbolCounter<symbolArrays.length;symbolCounter++) {
            final String[] symbolArray = symbolArrays[symbolCounter];
            if(isEmptySymbolList(symbolArray)) {
                new ExpectedFailure<MarketDataRequestException>(MISSING_SYMBOLS) {
                    protected void run()
                        throws Exception
                    {
                        MarketDataRequest.newRequest().fromProvider(provider).withSymbols(symbolArray);                    
                    }
                };
            } else {
                MarketDataRequest request = MarketDataRequest.newRequest().fromProvider(provider).withSymbols(symbolArray);
                verifyRequest(request,
                              provider,
                              null,
                              TOP_OF_BOOK,
                              SUBSCRIPTION,
                              null,
                              symbolArrayToString(symbolArray));
            }
        }
        // test single, comma-delimited version
        for(int symbolCounter=0;symbolCounter<symbols.length;symbolCounter++) {
            final String symbol = symbols[symbolCounter];
            if(isEmptySymbolList(symbol)) {
                new ExpectedFailure<MarketDataRequestException>(MISSING_SYMBOLS) {
                    protected void run()
                        throws Exception
                    {
                        MarketDataRequest.newRequest().fromProvider(provider).withSymbols(symbol);                    
                    }
                };
            } else if(symbol == null ||
                      symbol.trim().isEmpty()) {
                new ExpectedFailure<MarketDataRequestException>(INVALID_SYMBOLS) {
                    protected void run()
                        throws Exception
                    {
                        MarketDataRequest.newRequest().fromProvider(provider).withSymbols(symbol);                    
                    }
                };
            } else {
                verifyRequest(MarketDataRequest.newRequest().fromProvider(provider).withSymbols(symbol),
                              provider,
                              null,
                              TOP_OF_BOOK,
                              SUBSCRIPTION,
                              null,
                              symbol);
            }
        }
        // check for white-space around the symbol
        String startingSymbols = "METC, ORCL, GOOG ";
        MarketDataRequest request = MarketDataRequest.newRequest().fromProvider(provider).withSymbols(startingSymbols);
        String[] actualSymbols = request.getSymbols();
        assertEquals("METC",
                     actualSymbols[0]);
        assertEquals("ORCL",
                     actualSymbols[1]);
        assertEquals("GOOG",
                     actualSymbols[2]);
        // make sure that changing the source doesn't change the held values
        String testSymbol = "METC";
        request.withSymbols(testSymbol);
        assertArrayEquals(new String[] { "METC" },
                          request.getSymbols());
        testSymbol = "GOOG";
        assertArrayEquals(new String[] { "METC" },
                          request.getSymbols());
        // make sure that changing the destination doesn't change the held values
        actualSymbols = request.getSymbols();
        assertArrayEquals(new String[] { "METC" },
                          actualSymbols);
        assertArrayEquals(new String[] { "METC" },
                          request.getSymbols());
        actualSymbols = new String[] { "ORCL" };
        assertArrayEquals(new String[] { "METC" },
                          request.getSymbols());
    }
    /**
     * Tests the ability to set and retrieve provider values.
     *
     * @throws Exception if an error occurs
     */
    @Test
    public void provider()
        throws Exception
    {
        final String symbol = "METC";
        for(int providerCounter=0;providerCounter<providers.length;providerCounter++) {
            final String provider = providers[providerCounter];
            if(provider == null ||
               provider.isEmpty()) {
                new ExpectedFailure<MarketDataRequestException>(MISSING_PROVIDER) {
                    protected void run()
                        throws Exception
                    {
                        MarketDataRequest.newRequest().fromProvider(provider).withSymbols(symbol); 
                    }
                };
            } else {
                verifyRequest(MarketDataRequest.newRequest().fromProvider(provider).withSymbols(symbol),
                              provider,
                              null,
                              TOP_OF_BOOK,
                              SUBSCRIPTION,
                              null,
                              symbol);
            }
        }
        // test that changing the source doesn't change the held value
        String provider = "provider1";
        MarketDataRequest request = MarketDataRequest.newRequest().fromProvider(provider);
        assertEquals(provider,
                     request.getProvider());
        provider = "provider2";
        assertEquals("provider1",
                     request.getProvider());
        // test that changing the destination doesn't change the held value
        provider = request.getProvider();
        assertEquals("provider1",
                     provider);
        provider = "provider3";
        assertEquals("provider1",
                     request.getProvider());
    }
    /**
     * Tests the ability to get and set exchange values. 
     *
     * @throws Exception if an error occurs
     */
    @Test
    public void exchange()
        throws Exception
    {
        String symbol = "METC";
        String provider = "provider";
        for(int exchangeCounter=0;exchangeCounter<exchanges.length;exchangeCounter++) {
            verifyRequest(MarketDataRequest.newRequest().fromProvider(provider).withSymbols(symbol).fromExchange(exchanges[exchangeCounter]),
                          provider,
                          exchanges[exchangeCounter],
                          TOP_OF_BOOK,
                          SUBSCRIPTION,
                          null,
                          symbol);
        }
        // test that changing the source doesn't change the held value
        String exchange = "exchange1";
        MarketDataRequest request = MarketDataRequest.newRequest().fromExchange(exchange);
        assertEquals(exchange,
                     request.getExchange());
        exchange = "exchange2";
        assertEquals("exchange1",
                     request.getExchange());
        // test that changing the destination doesn't change the held value
        exchange = request.getExchange();
        assertEquals("exchange1",
                     exchange);
        exchange = "exchange3";
        assertEquals("exchange1",
                     request.getExchange());
    }
    /**
     * Tests retrieval of null and empty values.
     *
     * @throws Exception if an error occurs
     */
    @Test
    public void nulls()
        throws Exception
    {
        MarketDataRequest request = MarketDataRequest.newRequest();
        assertNotNull(request.getSymbols());
        assertEquals(0,
                     request.getSymbols().length);
        assertNull(request.getProvider());
        assertNull(request.getExchange());
        request.fromExchange("");
        assertNull(request.getExchange());
        assertEquals(TOP_OF_BOOK,
                     request.getContent());
        assertNull(request.getDate());
        assertEquals(SUBSCRIPTION,
                     request.getType());
        new ExpectedFailure<MarketDataRequestException>(INVALID_REQUEST) {
            protected void run()
                throws Exception
            {
                MarketDataRequest.validate(null);
            }
        };
    }
    /**
     * Tests the ability to set and retrieve dates.
     *
     * @throws Exception if an error occurs
     */
    @Test
    public void checkDate()
        throws Exception
    {
        final String symbol = "METC";
        final String provider = "provider";
        final MarketDataRequest request = MarketDataRequest.newRequest().withSymbols(symbol).fromProvider(provider);
        // test asOf(long)
        for(int dateCounter=0;dateCounter<dateLongs.length;dateCounter++) {
            final long testTime = dateLongs[dateCounter];
            if(testTime < 0) {
                new ExpectedFailure<MarketDataRequestException>(INVALID_DATE) {
                    protected void run()
                        throws Exception
                    {
                        request.asOf(-1);
                    }
                };
            } else {
                request.asOf(testTime);
                verifyRequest(request,
                              provider,
                              null,
                              TOP_OF_BOOK,
                              SUBSCRIPTION,
                              new Date(testTime),
                              symbol);
            }
        }
        // test asOf(Date)
        for(int dateCounter=0;dateCounter<dates.length;dateCounter++) {
            final Date testTime = dates[dateCounter];
            request.asOf(testTime);
            verifyRequest(request,
                          provider,
                          null,
                          TOP_OF_BOOK,
                          SUBSCRIPTION,
                          testTime,
                          symbol);
        }
        // test asOf(String)
        for(int dateCounter=0;dateCounter<dateStrings.length;dateCounter++) {
            final String testTime = dateStrings[dateCounter];
            if(testTime == null ||
               testTime.isEmpty()) {
                new ExpectedFailure<MarketDataRequestException>(INVALID_DATE) {
                    protected void run()
                        throws Exception
                    {
                        request.asOf(testTime);
                    }
                };
            } else {
                request.asOf(testTime);
                verifyRequest(request,
                              provider,
                              null,
                              TOP_OF_BOOK,
                              SUBSCRIPTION,
                              MarketDataRequest.DateUtils.stringToDate(testTime),
                              symbol);
            }
        }
        // test that changing the source doesn't change the held value
        long time = System.currentTimeMillis()-250;
        Date date = new Date(time);
        request.asOf(date);
        assertEquals(date,
                     request.getDate());
        date = new Date(time+250);
        assertEquals(new Date(time),
                     request.getDate());
        // test that changing the destination doesn't change the held value
        date = request.getDate();
        assertEquals(new Date(time),
                     date);
        date = null;
        assertEquals(new Date(time),
                     request.getDate());
    }
    /**
     * Tests {@link MarketDataRequest.DateUtils#stringToDate(String)}.
     * 
     * <p>Note that the tests are intentionally not exhaustive as the vast number of permutations
     * would far exceed time available to run them.  Additionally, since the parsing of the dates
     * is actually handled by an external library, to a certain extent, the behavior can be assumed
     * to be valid. 
     *
     * @throws Exception if an error occurs
     */
    @Test
    public void stringToDate()
        throws Exception
    {
        // test some obvious stinkers
        new ExpectedFailure<MarketDataRequestException>(INVALID_DATE) {
            protected void run()
                throws Exception
            {
                MarketDataRequest.DateUtils.stringToDate(null);
            }
        };
        new ExpectedFailure<MarketDataRequestException>(INVALID_DATE) {
            protected void run()
                throws Exception
            {
                MarketDataRequest.DateUtils.stringToDate("");
            }
        };
        // now build a few (incomplete) lists of invalid and valid components
        String[] invalidDateStrings = new String[] { "", "1", "11", "111", "1111", "11111", "111111", "x111111", "-123-4-5", "00000000", "20091301", "20090132", "19960230", "21000229" }; // haha, 2100 will *not* be a leap year
        String[] invalidTimeStrings = new String[] { "", "1", "11", "111", "-123", "-1-2", "12345", "1234567", "12345678", "2500", "2461", "240061" };
        String[] invalidTimeZoneStrings = new String[] { "X", "ZZ", "/1000", "+2500", "-0061" };
        String[] validDateStrings = new String[] { "00000101", "20040229", "99990531" };
        String[] validTimeStrings = new String[] { "0000", "000000", "000000000" };
        String[] validTimeZoneStrings = new String[] { "Z", "z", "+0000", "-1000", "+0530" };
        // iterate over the failure cases
        for(int dateCounter=0;dateCounter<invalidDateStrings.length;dateCounter++) {
            for(int timeCounter=0;timeCounter<invalidTimeStrings.length;timeCounter++) {
                for(int tzCounter=0;tzCounter<invalidTimeZoneStrings.length;tzCounter++) {
                    final String dateString = invalidDateStrings[dateCounter];
                    final String timeString = invalidTimeStrings[timeCounter];
                    final String tzString = invalidTimeZoneStrings[tzCounter];
                    // date alone
                    new ExpectedFailure<MarketDataRequestException>(INVALID_DATE) {
                        protected void run()
                            throws Exception
                        {
                            MarketDataRequest.DateUtils.stringToDate(dateString);
                        }
                    };
                    // date & tz
                    new ExpectedFailure<MarketDataRequestException>(INVALID_DATE) {
                        protected void run()
                            throws Exception
                        {
                            MarketDataRequest.DateUtils.stringToDate(String.format("%s%s",
                                                                                   dateString,
                                                                                   tzString));
                        }
                    };
                    // date & time
                    new ExpectedFailure<MarketDataRequestException>(INVALID_DATE) {
                        protected void run()
                            throws Exception
                        {
                            MarketDataRequest.DateUtils.stringToDate(String.format("%sT%s",
                                                                                   dateString,
                                                                                   timeString));
                        }
                    };
                    // date, time, & tz
                    new ExpectedFailure<MarketDataRequestException>(INVALID_DATE) {
                        protected void run()
                            throws Exception
                        {
                            MarketDataRequest.DateUtils.stringToDate(String.format("%sT%s%s",
                                                                                   dateString,
                                                                                   timeString,
                                                                                   tzString));
                        }
                    };
                }
            }
        }
        // iterate over the success cases (success is measured by the ability to return a date rather than throw an exception:
        //  calculating the expected dates without using the library that already calculates them would require significant
        //  complexity.
        for(int dateCounter=0;dateCounter<validDateStrings.length;dateCounter++) {
            for(int timeCounter=0;timeCounter<validTimeStrings.length;timeCounter++) {
                for(int tzCounter=0;tzCounter<invalidTimeZoneStrings.length;tzCounter++) {
                    String dateString = validDateStrings[dateCounter];
                    String timeString = validTimeStrings[timeCounter];
                    String tzString = validTimeZoneStrings[tzCounter];
                    // date only
                    MarketDataRequest.DateUtils.stringToDate(dateString);
                    // date & tz
                    MarketDataRequest.DateUtils.stringToDate(String.format("%s%s",
                                                                           dateString,
                                                                           tzString));
                    // date & time
                    MarketDataRequest.DateUtils.stringToDate(String.format("%sT%s",
                                                                           dateString,
                                                                           timeString));
                    // date, time, & tz
                    MarketDataRequest.DateUtils.stringToDate(String.format("%sT%s%s",
                                                                           dateString,
                                                                           timeString,
                                                                           tzString));
                }
            }
        }
        // check a few dates
        // UTC date
        doDateTest("20090319T120000000Z",
                   "20090319T120000000Z",
                   "Thursday, March 19, 2009 8:00:00 AM EDT");
        // PST date
        doDateTest("19700319T0800-0800",
                   "19700319T160000000Z",
                   "Thursday, March 19, 1970 11:00:00 AM EST");
        // no TZ (assumed to be UTC)
        doDateTest("19880319T0000",
                   "19880319T000000000Z",
                   "Friday, March 18, 1988 7:00:00 PM EST");
    }
    /**
     * Verifies the given ISO 8601 date is parsed correctly.
     *
     * @param inDateToTest a <code>String</code> value containing a valid ISO 8601 date as defined in
     *  {@link MarketDataRequest.DateUtils#stringToDate(String)} 
     * @param inExpectedUTCDate a <code>String</code> value containing the given test date expressed in
     *  ISO 8601 to millisecond precision in UTC
     * @param inExpectedEasternDate a <code>String</code> value containing the given test date expressed
     *  in {@link DateFormat#FULL} format in US Eastern time
     * @throws Exception if an error occurs
     */
    private static void doDateTest(String inDateToTest,
                                   String inExpectedUTCDate,
                                   String inExpectedEasternDate)
        throws Exception
    {
        Date date = MarketDataRequest.DateUtils.stringToDate(inDateToTest);
        assertEquals(inExpectedUTCDate,
                     MarketDataRequest.DateUtils.dateToString(date));
        assertEquals(testDateFormat.parse(inExpectedEasternDate),
                     date);
    }
    /**
     * Creates a composite <code>String</code> representation of a <code>String[]</code> value.
     *
     * @param inSymbolArray a <code>String[]</code> value
     * @return a <code>String</code> value representing the passed <code>String</code> values delimited by
     *  {@link MarketDataRequest#SYMBOL_DELIMITER}.
     */
    private static String symbolArrayToString(String...inSymbolArray)
    {
        StringBuilder buffer = new StringBuilder();
        boolean delimiterNeeded = false;
        for(String symbol:inSymbolArray) {
            if(symbol != null &&
               !symbol.isEmpty()) {
                if(delimiterNeeded) {
                    buffer.append(MarketDataRequest.SYMBOL_DELIMITER);
                }
                buffer.append(symbol);
                delimiterNeeded = true;
            }
        }
        return buffer.toString();
    }
    /**
     * Creates a <code>String</code> representation of a {@link MarketDataRequest} with the
     * given attributes. 
     *
     * @param inProvider a <code>String</code> value containing a <code>Provider</code> or <code>null</code>
     * @param inExchange a <code>String</code> value containing an <code>Exchange</code> or <code>null</code>
     * @param inContent a <code>Content</code> value or <code>null</code>
     * @param inType a <code>Type</code> value or <code>null</code>
     * @param inDate a <code>Date</code> value or <code>null</code>
     * @param inSymbols a <code>String</code> value containing a symbol or symbols delimited by 
     *  {@link MarketDataRequest#SYMBOL_DELIMITER} or <code>null</code>
     * @return a <code>String</code> value
     */
    private static String marketDataRequestStringFromComponents(String inProvider,
                                                                String inExchange,
                                                                Content inContent,
                                                                Type inType,
                                                                Date inDate,
                                                                String inSymbols)
        throws Exception
    {
        StringBuilder request = new StringBuilder();
        boolean delimiterNeeded = false;
        if(inProvider != null) {
            if(delimiterNeeded) {
                request.append(Util.KEY_VALUE_DELIMITER);
            }
            request.append(PROVIDER_KEY).append(Util.KEY_VALUE_SEPARATOR).append(inProvider);
            delimiterNeeded = true;
        }
        if(inExchange != null) {
            if(delimiterNeeded) {
                request.append(Util.KEY_VALUE_DELIMITER);
            }
            request.append(EXCHANGE_KEY).append(Util.KEY_VALUE_SEPARATOR).append(inExchange);
            delimiterNeeded = true;
        }
        if(inContent != null) {
            if(delimiterNeeded) {
                request.append(Util.KEY_VALUE_DELIMITER);
            }
            request.append(CONTENT_KEY).append(Util.KEY_VALUE_SEPARATOR).append(inContent);
            delimiterNeeded = true;
        }
        if(inType != null) {
            if(delimiterNeeded) {
                request.append(Util.KEY_VALUE_DELIMITER);
            }
            request.append(TYPE_KEY).append(Util.KEY_VALUE_SEPARATOR).append(inType);
            delimiterNeeded = true;
        }
        if(inDate != null) {
            if(delimiterNeeded) {
                request.append(Util.KEY_VALUE_DELIMITER);
            }
            request.append(DATE_KEY).append(Util.KEY_VALUE_SEPARATOR).append(MarketDataRequest.DateUtils.dateToString(inDate));
            delimiterNeeded = true;
        }
        if(inSymbols != null &&
           !inSymbols.isEmpty()) {
            if(delimiterNeeded) {
                request.append(Util.KEY_VALUE_DELIMITER);
            }
            request.append(SYMBOLS_KEY).append(Util.KEY_VALUE_SEPARATOR).append(inSymbols);
            delimiterNeeded = true;
        }
        // add a key/value pair that is totally extraneous and should be ignored
        if(delimiterNeeded) {
            request.append(Util.KEY_VALUE_DELIMITER);
        }
        request.append("IGNORANCE=BLISS");
        return request.toString();
    }
    /**
     * Verifies that the given <code>MarketDataRequest</code> matches the expected attributes.
     *
     * @param inActualRequest a <code>MarketDataRequest</code> value containing the actual request
     * @param inProvider a <code>String</code> value containing the expected <code>Provider</code>
     * @param inExchange a <code>String</code> value containing the expected <code>Exchange</code>
     * @param inContent a <code>Content</code> value containing the expected <code>Content</code>
     * @param inType a <code>Type</code> value containing the expected <code>Type</code>
     * @param inDate a <code>Date</code> value containing the expected <code>Date</code>
     * @param inSymbols a <code>String[]</code> value containing the expected <code>Symbol</code> values
     * @throws Exception if an error occurs
     */
    private static void verifyRequest(MarketDataRequest inActualRequest,
                                      String inProvider,
                                      String inExchange,
                                      Content inContent,
                                      Type inType,
                                      Date inDate,
                                      String inSymbols)
        throws Exception
    {
        assertNotNull(inActualRequest);
        // test provider (making sure that the returned value cannot be modified)
        String provider = inActualRequest.getProvider();
        assertEquals(inProvider,
                     provider);
        if(provider != null) {
            provider += "-" + System.nanoTime();
            assertFalse(provider.equals(inProvider));
            assertEquals(inProvider,
                         inActualRequest.getProvider());
        }
        // test exchange (making sure the returned value cannot be modified)
        String exchange = inActualRequest.getExchange();
        assertEquals((inExchange == null || inExchange.isEmpty()) ? null : inExchange,
                     exchange);
        if(exchange != null) {
            exchange += "-" + System.nanoTime();
            assertFalse(exchange.equals(inExchange));
            assertEquals(inExchange,
                         inActualRequest.getExchange());
        }
        // test content
        Content content = inActualRequest.getContent();
        assertEquals(inContent,
                     content);
        if(content != null) {
            content = Content.values()[(inContent.ordinal() + 1) % Content.values().length];
            assertFalse(content.equals(inContent));
            assertEquals(inContent,
                         inActualRequest.getContent());
        }
        // test type
        Type type = inActualRequest.getType();
        assertEquals(inType,
                     type);
        if(type != null) {
            type = Type.values()[(inType.ordinal() + 1) % Type.values().length];
            assertFalse(type.equals(inType));
            assertEquals(inType,
                         inActualRequest.getType());
        }
        // test date
        Date date = inActualRequest.getDate();
        assertEquals(inDate,
                     date);
        if(date != null) {
           date = new Date(System.currentTimeMillis() + 250);
            assertFalse(date.equals(inDate));
            assertEquals(inDate,
                         inActualRequest.getDate());
        }
        // test symbols
        String[] expectedSymbols = inSymbols.split(MarketDataRequest.SYMBOL_DELIMITER);
        String[] actualSymbols = inActualRequest.getSymbols();
        assertArrayEquals(String.format("Expected: %s Actual: %s",
                                        Arrays.toString(expectedSymbols),
                                        Arrays.toString(actualSymbols)),
                          expectedSymbols,
                          actualSymbols);
        if(actualSymbols != null) {
            actualSymbols = new String[] { "some", "symbols", "here"};
            assertFalse(Arrays.equals(expectedSymbols,
                                      actualSymbols));
            assertArrayEquals(expectedSymbols,
                              inActualRequest.getSymbols());
        }
        // check validation
        MarketDataRequest.validate(inActualRequest);
        // check toString-newRequestFromString round-trip
        String stringValue = inActualRequest.toString();
        assertNotNull(stringValue);
        MarketDataRequest roundTripRequest = MarketDataRequest.newRequestFromString(stringValue); 
        assertEquals(String.format("Expected: %s Actual: %s",
                                   inActualRequest,
                                   roundTripRequest),
                     inActualRequest,
                     roundTripRequest);
    }
    /**
     * Checks to see if the given <code>String</code> represents an empty symbol list.
     * 
     * <p>The list is considered empty if it is empty or if all symbols in the list are whitespace or empty.
     *
     * @param inSymbols a <code>String</code> value allegedly containing a list of symbols delimited by {@link MarketDataRequest#SYMBOL_DELIMITER}
     * @return a <code>boolean</code>value
     */
    private boolean isEmptySymbolList(String inSymbols)
    {
        if(inSymbols == null ||
           inSymbols.isEmpty()) {
            return true;
        }
        return isEmptySymbolList(inSymbols.split(MarketDataRequest.SYMBOL_DELIMITER));
    }
    /**
     * Checks to see if the given <code>String[]</code> value represents an empty symbol list.
     * 
     * <p>The list is considered empty if the array is empty or contains only null or whitespace values.
     *
     * @param inSymbols a <code>String[]</code> value
     * @return a <code>boolean</cod> value
     */
    private boolean isEmptySymbolList(String[] inSymbols)
    {
        if(inSymbols == null ||
           inSymbols.length == 0) {
            return true;
        }
        for(String symbol : inSymbols) {
            if(symbol != null &&
               !symbol.trim().isEmpty()) {
                return false;
            }
        }
        return true;
    }
}
