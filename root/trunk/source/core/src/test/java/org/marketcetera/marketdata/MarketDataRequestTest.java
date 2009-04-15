package org.marketcetera.marketdata;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.marketcetera.marketdata.MarketDataRequest.CONTENT_KEY;
import static org.marketcetera.marketdata.MarketDataRequest.EXCHANGE_KEY;
import static org.marketcetera.marketdata.MarketDataRequest.PROVIDER_KEY;
import static org.marketcetera.marketdata.MarketDataRequest.SYMBOLS_KEY;
import static org.marketcetera.marketdata.MarketDataRequest.SYMBOL_DELIMITER;
import static org.marketcetera.marketdata.MarketDataRequest.TYPE_KEY;
import static org.marketcetera.marketdata.MarketDataRequest.Content.LATEST_TICK;
import static org.marketcetera.marketdata.MarketDataRequest.Content.LEVEL_2;
import static org.marketcetera.marketdata.MarketDataRequest.Content.OPEN_BOOK;
import static org.marketcetera.marketdata.MarketDataRequest.Content.MARKET_STAT;
import static org.marketcetera.marketdata.MarketDataRequest.Content.TOP_OF_BOOK;
import static org.marketcetera.marketdata.MarketDataRequest.Content.TOTAL_VIEW;
import static org.marketcetera.marketdata.MarketDataRequest.Type.SNAPSHOT;
import static org.marketcetera.marketdata.MarketDataRequest.Type.SUBSCRIPTION;
import static org.marketcetera.marketdata.Messages.INVALID_CONTENT;
import static org.marketcetera.marketdata.Messages.INVALID_REQUEST;
import static org.marketcetera.marketdata.Messages.INVALID_SYMBOLS;
import static org.marketcetera.marketdata.Messages.INVALID_TYPE;
import static org.marketcetera.marketdata.Messages.MISSING_CONTENT;
import static org.marketcetera.marketdata.Messages.MISSING_SYMBOLS;
import static org.marketcetera.marketdata.Messages.MISSING_TYPE;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

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
    private static final Content[] contents = new Content[] { null, TOP_OF_BOOK, MARKET_STAT, LATEST_TICK, OPEN_BOOK, TOTAL_VIEW, LEVEL_2 };
    private static final Content[][] contentArrays = new Content[][] { {}, { null }, { TOP_OF_BOOK }, { LATEST_TICK, OPEN_BOOK }, { null, LEVEL_2 } };
    private static final String[][] contentStringArrays = new String[][] { {}, { null }, { "" }, { null, "", LATEST_TICK.toString() }, { TOP_OF_BOOK.toString() }, { OPEN_BOOK.toString(), MARKET_STAT.toString().toLowerCase() }, { "invalid-content" } };
    private static final Type[] types = new Type[] { null, SNAPSHOT, SUBSCRIPTION };
    // alternate representation of values
    private static final String[] contentStrings = new String[] { null, "", "not-a-content", "TOP_OF_BOOK", "level_2", "StATisTicS" };
    private static final String[] typeStrings = new String[] { null, "", "snapshot", "SNAPSHOT", "SuBsCripTioN" };
    // variations of keys
    private static final int keyCount = 4;
    private static final String[] symbolKeys = new String[] { "symbols", "SYMBOLS", "SyMbOlS", MarketDataRequest.SYMBOLS_KEY };
    private static final String[] providerKeys = new String[] { "provider", "PROVIDER", "PrOvIdEr", MarketDataRequest.PROVIDER_KEY };
    private static final String[] exchangeKeys = new String[] { "exchange", "EXCHANGE", "ExChAnGe", MarketDataRequest.EXCHANGE_KEY };
    private static final String[] contentKeys = new String[] { "content", "CONTENT", "CoNtEnT", MarketDataRequest.CONTENT_KEY };
    private static final String[] typeKeys = new String[] { "type", "TYPE", "TyPe", MarketDataRequest.TYPE_KEY };
    private static final Set<String> ALL_CONTENTS = new HashSet<String>();
    private static final Set<String> ALL_TYPES = new HashSet<String>();
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
        String symbols = "METC";
        String provider = "provider";
        String exchange = "exchange";
        Type type = SNAPSHOT;
        Content content = MARKET_STAT;
        for(int keyCounter=0;keyCounter<keyCount;keyCounter++) {
            String requestString = String.format("%s=%s:%s=%s:%s=%s:%s=%s:%s=%s",
                                                 symbolKeys[keyCounter],
                                                 symbols,
                                                 providerKeys[keyCounter],
                                                 provider,
                                                 exchangeKeys[keyCounter],
                                                 exchange,
                                                 contentKeys[keyCounter],
                                                 content,
                                                 typeKeys[keyCounter],
                                                 type);
            verifyRequest(MarketDataRequest.newRequestFromString(requestString),
                          provider,
                          exchange,
                          content,
                          type,
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
        new ExpectedFailure<IllegalArgumentException>(INVALID_REQUEST.getText()) {
            protected void run()
                throws Exception
            {
                MarketDataRequest.newRequestFromString(null);
            }
        };
        new ExpectedFailure<IllegalArgumentException>(INVALID_REQUEST.getText()) {
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
                        for(int typesCounter=0;typesCounter<types.length;typesCounter++) {
                            SLF4JLoggerProxy.debug(this,
                                                   String.format("%d:%d:%d:%d:%d",
                                                                 contentsCounter,
                                                                 providersCounter,
                                                                 symbolsCounter,
                                                                 exchangesCounter,
                                                                 typesCounter));
                            final String requestString = marketDataRequestStringFromComponents(providers[providersCounter],
                                                                                               exchanges[exchangesCounter],
                                                                                               contents[contentsCounter],
                                                                                               types[typesCounter],
                                                                                               symbols[symbolsCounter]);
                            // catch-all for a totally empty request
                            if(requestString.isEmpty()) {
                                new ExpectedFailure<IllegalArgumentException>(INVALID_REQUEST.getText()) {
                                    protected void run()
                                        throws Exception
                                    {
                                        MarketDataRequest.newRequestFromString(requestString);
                                    }
                                };
                                continue;
                            }
                            // symbols error conditions
                            if(isEmptyList(symbols[symbolsCounter])) {
                                new ExpectedFailure<IllegalArgumentException>(MISSING_SYMBOLS.getText()) {
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
                                          symbols[symbolsCounter]);
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
                new ExpectedFailure<IllegalArgumentException>(INVALID_TYPE.getText(typeString)) {
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
                              symbols);
            }
        }
        // test enum version of ofType
        for(int typeCounter=0;typeCounter<types.length;typeCounter++) {
            final Type type = types[typeCounter];
            if(type == null) {
                new ExpectedFailure<IllegalArgumentException>(MISSING_TYPE.getText()) {
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
        // test strings version of withContent
        for(int contentCounter=0;contentCounter<contentStrings.length;contentCounter++) {
            final String contentString = contentStrings[contentCounter];
            if(contentString == null ||
               contentString.isEmpty()) {
                new ExpectedFailure<IllegalArgumentException>(MISSING_CONTENT.getText()) {
                    protected void run()
                        throws Exception
                    {
                        MarketDataRequest.newRequest().fromProvider(provider).withSymbols(symbols).withContent(contentString);                    
                    }
                };
                continue;
            } 
            if(!ALL_CONTENTS.contains(contentString.toUpperCase())) {
                new ExpectedFailure<IllegalArgumentException>(INVALID_CONTENT.getText(contentString)) {
                    protected void run()
                        throws Exception
                    {
                        MarketDataRequest.newRequest().fromProvider(provider).withSymbols(symbols).withContent(contentString);                    
                    }
                };
                continue;
            } 
            MarketDataRequest request = MarketDataRequest.newRequest().fromProvider(provider).withSymbols(symbols).withContent(contentString); 
            verifyRequest(request,
                          provider,
                          null,
                          Content.valueOf(contentString.toUpperCase()),
                          SUBSCRIPTION,
                          symbols);
        }
        // test enum version of withContent
        for(int contentCounter=0;contentCounter<contents.length;contentCounter++) {
            final Content content = contents[contentCounter];
            if(content == null) {
                new ExpectedFailure<IllegalArgumentException>(INVALID_CONTENT.getText(Arrays.toString(new Content[] { null }))) {
                    protected void run()
                        throws Exception
                    {
                        MarketDataRequest.newRequest().fromProvider(provider).withSymbols(symbols).withContent(content);
                    }
                };
            } else {
                MarketDataRequest request = MarketDataRequest.newRequest().fromProvider(provider).withSymbols(symbols).withContent(content);
                verifyRequest(request,
                              provider,
                              null,
                              content,
                              SUBSCRIPTION,
                              symbols);
            }
        }
        // test multiple enum version of withContent
        for(int contentCounter=0;contentCounter<contentArrays.length;contentCounter++) {
            final Content[] content = contentArrays[contentCounter];
            if(content == null ||
               content.length == 0) {
                new ExpectedFailure<IllegalArgumentException>(MISSING_CONTENT.getText()) {
                    protected void run()
                        throws Exception
                    {
                        MarketDataRequest.newRequest().fromProvider(provider).withSymbols(symbols).withContent(content);
                    }
                };
                continue;
            }
            if(isEmptyEnumList(content) ||
               !isValidEnumList(content)) {
                new ExpectedFailure<IllegalArgumentException>(INVALID_CONTENT.getText(Arrays.toString(content))) {
                    protected void run()
                        throws Exception
                    {
                        MarketDataRequest.newRequest().fromProvider(provider).withSymbols(symbols).withContent(content);
                    }
                };
                continue;
            }
            MarketDataRequest request = MarketDataRequest.newRequest().fromProvider(provider).withSymbols(symbols).withContent(content);
            verifyRequest(request,
                          provider,
                          null,
                          content,
                          SUBSCRIPTION,
                          symbols);
        }
        // test multiple string version of withContent
        for(int contentCounter=0;contentCounter<contentStringArrays.length;contentCounter++) {
            final String[] content = contentStringArrays[contentCounter];
            if(content == null ||
               content.length == 0 ||
               isEmptyStringList(content) ||
               !isValidStringList(content)) {
                new ExpectedFailure<IllegalArgumentException>(null) {
                    protected void run()
                        throws Exception
                    {
                        MarketDataRequest.newRequest().fromProvider(provider).withSymbols(symbols).withContent(content);
                    }
                };
                continue;
            }
            boolean willFail = false;
            List<Content> expectedContents = new ArrayList<Content>();
            for(String contentString : content) {
                if(!ALL_CONTENTS.contains(contentString.toUpperCase())) {
                    willFail = true;
                    break;
                } else {
                    expectedContents.add(Content.valueOf(contentString.toUpperCase()));
                }
            }
            if(willFail) {
                new ExpectedFailure<IllegalArgumentException>(null) {
                    protected void run()
                        throws Exception
                    {
                        MarketDataRequest.newRequest().fromProvider(provider).withSymbols(symbols).withContent(content);                    
                    }
                };
                continue;
            }
            MarketDataRequest request = MarketDataRequest.newRequest().fromProvider(provider).withSymbols(symbols).withContent(content);
            verifyRequest(request,
                          provider,
                          null,
                          expectedContents.toArray(new Content[expectedContents.size()]),
                          SUBSCRIPTION,
                          symbols);
        }
        // test that changing the source doesn't change the held value
        Content content = LEVEL_2;
        final MarketDataRequest request = MarketDataRequest.newRequest().withContent(content);
        assertEquals(new LinkedHashSet<Content>(Arrays.asList(new Content[] { LEVEL_2 } )),
                     request.getContent());
        content = TOP_OF_BOOK;
        assertEquals(new LinkedHashSet<Content>(Arrays.asList(new Content[] { LEVEL_2 } )),
                     request.getContent());
        // test that changing the destination doesn't change the held value
        new ExpectedFailure<UnsupportedOperationException>(null) {
            protected void run()
                throws Exception
            {
                request.getContent().clear();
            }
        };
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
            if(isEmptyStringList(symbolArray)) {
                new ExpectedFailure<IllegalArgumentException>(MISSING_SYMBOLS.getText()) {
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
                              symbolArrayToString(symbolArray));
            }
        }
        // test single, comma-delimited version
        for(int symbolCounter=0;symbolCounter<symbols.length;symbolCounter++) {
            final String symbol = symbols[symbolCounter];
            if(isEmptyList(symbol)) {
                new ExpectedFailure<IllegalArgumentException>(MISSING_SYMBOLS.getText()) {
                    protected void run()
                        throws Exception
                    {
                        MarketDataRequest.newRequest().fromProvider(provider).withSymbols(symbol);                    
                    }
                };
            } else if(symbol == null ||
                      symbol.trim().isEmpty()) {
                new ExpectedFailure<IllegalArgumentException>(INVALID_SYMBOLS) {
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
            verifyRequest(MarketDataRequest.newRequest().fromProvider(provider).withSymbols(symbol),
                          provider,
                          null,
                          TOP_OF_BOOK,
                          SUBSCRIPTION,
                          symbol);
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
        assertEquals(new LinkedHashSet<Content>(Arrays.asList(TOP_OF_BOOK)),
                     request.getContent());
        assertEquals(SUBSCRIPTION,
                     request.getType());
        new ExpectedFailure<NullPointerException>(null) {
            protected void run()
                throws Exception
            {
                MarketDataRequest.validate(null);
            }
        };
    }
    /**
     * Tests the ability to specify multiple contents in a single string.
     *
     * @throws Exception if an error occurs
     */
    @Test
    public void multipleContentsFromString()
        throws Exception
    {
        // test multiple contents
        for(Content[] content : contentArrays) {
            final StringBuilder requestString = new StringBuilder().append(marketDataRequestStringFromComponents("provider",
                                                                                                                 null,
                                                                                                                 null,
                                                                                                                 null,
                                                                                                                 "METC"));
            requestString.append(Util.KEY_VALUE_DELIMITER);
            requestString.append(CONTENT_KEY).append(Util.KEY_VALUE_SEPARATOR);
            boolean delimiterNeeded = false;
            boolean errorExpected = false;
            List<Content> expectedContents = new ArrayList<Content>();
            for(Content subcontent : content) {
                if(delimiterNeeded) {
                    requestString.append(SYMBOL_DELIMITER);
                }
                requestString.append(subcontent);
                delimiterNeeded = true;
                if(subcontent == null) {
                    errorExpected = true;
                } else {
                    expectedContents.add(subcontent);
                }
            }
            if(expectedContents.isEmpty()) {
                expectedContents.add(TOP_OF_BOOK);
            }
            if(!errorExpected) {
                verifyRequest(MarketDataRequest.newRequestFromString(requestString.toString()),
                              "provider",
                              null,
                              expectedContents.toArray(new Content[expectedContents.size()]),
                              SUBSCRIPTION,
                              "METC");
            } else {
                new ExpectedFailure<IllegalArgumentException>(INVALID_CONTENT.getText(null)) {
                    protected void run()
                        throws Exception
                    {
                        MarketDataRequest.newRequestFromString(requestString.toString());
                    }
                };
            }
        }
        // test multiple strings
        for(String[] content : contentStringArrays) {
            final StringBuilder requestString = new StringBuilder().append(marketDataRequestStringFromComponents("provider",
                                                                                                                 null,
                                                                                                                 null,
                                                                                                                 null,
                                                                                                                 "METC"));
            requestString.append(Util.KEY_VALUE_DELIMITER);
            requestString.append(CONTENT_KEY).append(Util.KEY_VALUE_SEPARATOR);
            boolean delimiterNeeded = false;
            boolean errorExpected = false;
            String invalidContent = null;
            List<Content> expectedContents = new ArrayList<Content>();
            for(String subcontent : content) {
                if(delimiterNeeded) {
                    requestString.append(SYMBOL_DELIMITER);
                }
                requestString.append(subcontent);
                delimiterNeeded = true;
                if(subcontent == null ||
                   !ALL_CONTENTS.contains(subcontent.trim().toUpperCase()) &&
                   !subcontent.trim().isEmpty()) {
                    errorExpected = true;
                    invalidContent = subcontent;
                } else {
                    if(!subcontent.trim().isEmpty()) {
                        expectedContents.add(Content.valueOf(subcontent.trim().toUpperCase()));
                    }
                }
            }
            if(expectedContents.isEmpty()) {
                expectedContents.add(TOP_OF_BOOK);
            }
            if(!errorExpected) {
                verifyRequest(MarketDataRequest.newRequestFromString(requestString.toString()),
                              "provider",
                              null,
                              expectedContents.toArray(new Content[expectedContents.size()]),
                              SUBSCRIPTION,
                              "METC");
            } else {
                new ExpectedFailure<IllegalArgumentException>(INVALID_CONTENT.getText(invalidContent)) {
                    protected void run()
                        throws Exception
                    {
                        MarketDataRequest.newRequestFromString(requestString.toString());
                    }
                };
            }
        }
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
     * @param inSymbols a <code>String</code> value containing a symbol or symbols delimited by 
     *  {@link MarketDataRequest#SYMBOL_DELIMITER} or <code>null</code>
     * @return a <code>String</code> value
     */
    private static String marketDataRequestStringFromComponents(String inProvider,
                                                                String inExchange,
                                                                Content inContent,
                                                                Type inType,
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
     * @param inContent a <code>Content[]</code> value containing the expected <code>Content</code>
     * @param inType a <code>Type</code> value containing the expected <code>Type</code>
     * @param inSymbols a <code>String[]</code> value containing the expected <code>Symbol</code> values
     * @throws Exception if an error occurs
     */
    private static void verifyRequest(final MarketDataRequest inActualRequest,
                                      String inProvider,
                                      String inExchange,
                                      Content[] inContent,
                                      Type inType,
                                      String inSymbols)
        throws Exception
    {
        assertNotNull(inActualRequest);
        // test provider (making sure that the returned value cannot be modified)
        String provider = inActualRequest.getProvider();
        assertEquals((inProvider == null || inProvider.isEmpty()) ? null : inProvider,
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
        final Set<Content> content = inActualRequest.getContent();
        assertEquals(new LinkedHashSet<Content>(Arrays.asList(inContent)),
                     content);
        if(content != null &&
           !content.isEmpty()) {
            new ExpectedFailure<UnsupportedOperationException>(null) {
                @Override
                protected void run()
                    throws Exception
                {
                    content.clear();
                }
            };
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
        // test content validation
        // compare with null
        new ExpectedFailure<NullPointerException>(null) {
            @Override
            protected void run()
                throws Exception
            {
                inActualRequest.validateWithCapabilities((Content[])null);
            }
        };
        // compare with empty
        assertFalse(inActualRequest.validateWithCapabilities(new Content[0]));
        // compare with intersects completely
        assertTrue(inActualRequest.validateWithCapabilities(inContent));
        // compare with does not intersect
        Set<Content> compliment = new HashSet<Content>(Arrays.asList(Content.values()));
        compliment.removeAll(Arrays.asList(inContent));
        assertFalse(inActualRequest.validateWithCapabilities(compliment.toArray(new Content[compliment.size()])));
    }
    /**
     * Verifies that the given <code>MarketDataRequest</code> matches the expected attributes.
     *
     * @param inActualRequest a <code>MarketDataRequest</code> value containing the actual request
     * @param inProvider a <code>String</code> value containing the expected <code>Provider</code>
     * @param inExchange a <code>String</code> value containing the expected <code>Exchange</code>
     * @param inContent a <code>Content</code> value containing the expected <code>Content</code>
     * @param inType a <code>Type</code> value containing the expected <code>Type</code>
     * @param inSymbols a <code>String[]</code> value containing the expected <code>Symbol</code> values
     * @throws Exception if an error occurs
     */
    private static void verifyRequest(MarketDataRequest inActualRequest,
                                      String inProvider,
                                      String inExchange,
                                      Content inContent,
                                      Type inType,
                                      String inSymbols)
        throws Exception
    {
        verifyRequest(inActualRequest,
                      inProvider,
                      inExchange,
                      new Content[] { inContent },
                      inType,
                      inSymbols);
    }
    /**
     * Checks to see if the given <code>String</code> represents an empty list.
     * 
     * <p>The list is considered empty if it is empty or if all tokens in the list are whitespace or empty.
     *
     * @param inStrings a <code>String</code> value allegedly containing a list delimited by {@link MarketDataRequest#SYMBOL_DELIMITER}
     * @return a <code>boolean</code>value
     */
    private boolean isEmptyList(String inStrings)
    {
        if(inStrings == null ||
           inStrings.isEmpty()) {
            return true;
        }
        return isEmptyStringList(inStrings.split(MarketDataRequest.SYMBOL_DELIMITER));
    }
    /**
     * Checks to see if the given <code>String[]</code> value represents an empty list.
     * 
     * <p>The list is considered empty if the array is empty or contains only null or whitespace values.
     *
     * @param inStrings a <code>String[]</code> value
     * @return a <code>boolean</cod> value
     */
    private boolean isEmptyStringList(String[] inStrings)
    {
        if(inStrings == null ||
           inStrings.length == 0) {
            return true;
        }
        for(String symbol : inStrings) {
            if(symbol != null &&
               !symbol.trim().isEmpty()) {
                return false;
            }
        }
        return true;
    }
    /**
     * Checks to see if the given <code>Enum&lt;?&gt;[]</code> value represents an empty list.
     *
     * @param inEnums an <code>Enum&lt;?&gt;[]</code> value
     * @return a <code>boolean</code> value
     */
    private static boolean isEmptyEnumList(Enum<?>[] inEnums)
    {
        if(inEnums == null ||
           inEnums.length == 0) {
            return true;
        }
        for(Enum<?> e : inEnums) {
            if(e != null) {
                return false;
            }
        }
        return true;
    }
    private static <T extends Enum<T>> boolean isValidEnumList(Enum<T>[] inEnums)
    {
        for(Enum<T> e : inEnums) {
            if(e == null) {
                return false;
            }
        }
        return true;
    }
    private static boolean isValidStringList(String[] inStrings)
    {
        for(String string : inStrings) {
            if(string == null) {
                return false;
            }
        }
        return true;
    }
}
