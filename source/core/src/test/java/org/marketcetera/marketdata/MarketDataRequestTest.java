package org.marketcetera.marketdata;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.marketcetera.marketdata.MarketDataRequest.ASSETCLASS_KEY;
import static org.marketcetera.marketdata.MarketDataRequest.CONTENT_KEY;
import static org.marketcetera.marketdata.MarketDataRequest.EXCHANGE_KEY;
import static org.marketcetera.marketdata.MarketDataRequest.PROVIDER_KEY;
import static org.marketcetera.marketdata.MarketDataRequest.SYMBOLS_KEY;
import static org.marketcetera.marketdata.MarketDataRequest.SYMBOL_DELIMITER;
import static org.marketcetera.marketdata.MarketDataRequest.UNDERLYINGSYMBOLS_KEY;
import static org.marketcetera.marketdata.MarketDataRequest.AssetClass.EQUITY;
import static org.marketcetera.marketdata.MarketDataRequest.AssetClass.OPTION;
import static org.marketcetera.marketdata.MarketDataRequest.Content.DIVIDEND;
import static org.marketcetera.marketdata.MarketDataRequest.Content.LATEST_TICK;
import static org.marketcetera.marketdata.MarketDataRequest.Content.LEVEL_2;
import static org.marketcetera.marketdata.MarketDataRequest.Content.MARKET_STAT;
import static org.marketcetera.marketdata.MarketDataRequest.Content.OPEN_BOOK;
import static org.marketcetera.marketdata.MarketDataRequest.Content.TOP_OF_BOOK;
import static org.marketcetera.marketdata.MarketDataRequest.Content.TOTAL_VIEW;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.junit.BeforeClass;
import org.junit.Test;
import org.marketcetera.core.Util;
import org.marketcetera.event.AskEvent;
import org.marketcetera.event.BidEvent;
import org.marketcetera.event.DividendEvent;
import org.marketcetera.event.Event;
import org.marketcetera.event.LogEvent;
import org.marketcetera.event.MarketstatEvent;
import org.marketcetera.event.TradeEvent;
import org.marketcetera.marketdata.MarketDataRequest.AssetClass;
import org.marketcetera.marketdata.MarketDataRequest.Content;
import org.marketcetera.module.ExpectedFailure;
import org.marketcetera.util.log.SLF4JLoggerProxy;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

/* $License$ */

/**
 * Tests {@link MarketDataRequest}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 1.0.0
 */
public class MarketDataRequestTest
    implements Messages
{
    private static final String[] symbols = new String[] { null, "", " ", "METC", "GOOG,ORCL" };
    private static final String[][] symbolArrays = new String[][] { {}, { ""," ", "MSFT" }, { "METC" }, { "GOOG","ORCL" } };
    private static final String[] providers = new String[] { null, "", "invalid-provider", "bogus" };
    private static final String[] exchanges = new String[] { null, "", "invalid-exchange", "Q" };
    private static final Content[] contents = new Content[] { null, TOP_OF_BOOK, MARKET_STAT, LATEST_TICK, OPEN_BOOK, TOTAL_VIEW, LEVEL_2, DIVIDEND };
    private static final Content[][] contentArrays = new Content[][] { {}, { null }, { TOP_OF_BOOK }, { LATEST_TICK, OPEN_BOOK }, { null, LEVEL_2 } };
    private static final String[][] contentStringArrays = new String[][] { {}, { null }, { "" }, { null, "", LATEST_TICK.toString() }, { TOP_OF_BOOK.toString() }, { OPEN_BOOK.toString(), MARKET_STAT.toString().toLowerCase() }, { "invalid-content" } };
    // alternate representation of values
    private static final String[] contentStrings = new String[] { null, "", "not-a-content", "TOP_OF_BOOK", "level_2", "StATisTicS" };
    private static final AssetClass[] assetClasses = new AssetClass[] { null, EQUITY, OPTION };
    private static final String[] assetClassStrings = new String[] { null, "", "not-an-asset-class", "equity", "OpTiOn" };
    // variations of keys
    private static final int keyCount = 4;
    private static final String[] symbolKeys = new String[] { "symbols", "SYMBOLS", "SyMbOlS", MarketDataRequest.SYMBOLS_KEY };
    private static final String[] underlyingSymbolsKeys = new String[] { "underlyingsymbols", "UNDERLYINGSYMBOLS", "UnDeRlYiNgSyMbOlS", MarketDataRequest.UNDERLYINGSYMBOLS_KEY };
    private static final String[] providerKeys = new String[] { "provider", "PROVIDER", "PrOvIdEr", MarketDataRequest.PROVIDER_KEY };
    private static final String[] exchangeKeys = new String[] { "exchange", "EXCHANGE", "ExChAnGe", MarketDataRequest.EXCHANGE_KEY };
    private static final String[] contentKeys = new String[] { "content", "CONTENT", "CoNtEnT", MarketDataRequest.CONTENT_KEY };
    private static final String[] assetClassKeys = new String[] { "assetclass", "ASSETCLASS", "AsSeTcLaSs", MarketDataRequest.ASSETCLASS_KEY };
    private static final Set<String> ALL_CONTENTS = new HashSet<String>();
    private static final Set<String> ALL_ASSET_CLASSES = new HashSet<String>();
    /**
     * Initialization that needs to be run once for all tests.
     */
    @BeforeClass
    public static void runOnce()
    {
        for(Content content : Content.values()) {
            ALL_CONTENTS.add(content.toString());
        }
        for(AssetClass assetClass : AssetClass.values()) {
            ALL_ASSET_CLASSES.add(assetClass.toString());
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
                     underlyingSymbolsKeys.length);
        assertEquals(keyCount,
                     providerKeys.length);
        assertEquals(keyCount,
                     exchangeKeys.length);
        assertEquals(keyCount,
                     contentKeys.length);
        assertEquals(keyCount,
                     assetClassKeys.length);
        String symbols = "METC";
        String underlyingSymbols = "GOOG";
        String provider = "provider";
        String exchange = "exchange";
        Content content = MARKET_STAT;
        for(int keyCounter=0;keyCounter<keyCount;keyCounter++) {
            // some combinations are not allowed - this is why we specify symbols or underlying symbols
            //  but not both
            // first, test symbols
            AssetClass assetClass = EQUITY;
            String requestString = String.format("%s=%s:%s=%s:%s=%s:%s=%s:%s=%s",
                                                 symbolKeys[keyCounter],
                                                 symbols,
                                                 providerKeys[keyCounter],
                                                 provider,
                                                 exchangeKeys[keyCounter],
                                                 exchange,
                                                 contentKeys[keyCounter],
                                                 content,
                                                 assetClassKeys[keyCounter],
                                                 assetClass);
            verifyRequest(MarketDataRequest.newRequestFromString(requestString),
                          provider,
                          exchange,
                          content,
                          EQUITY,
                          symbols,
                          null);
            // now, test underlying symbols
            assetClass = OPTION;
            requestString = String.format("%s=%s:%s=%s:%s=%s:%s=%s:%s=%s",
                                          underlyingSymbolsKeys[keyCounter],
                                          underlyingSymbols,
                                          providerKeys[keyCounter],
                                          provider,
                                          exchangeKeys[keyCounter],
                                          exchange,
                                          contentKeys[keyCounter],
                                          content,
                                          assetClassKeys[keyCounter],
                                          assetClass);
            verifyRequest(MarketDataRequest.newRequestFromString(requestString),
                          provider,
                          exchange,
                          content,
                          OPTION,
                          null,
                          underlyingSymbols);
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
            @Override
            protected void run()
                throws Exception
            {
                MarketDataRequest.newRequestFromString(null);
            }
        };
        new ExpectedFailure<IllegalArgumentException>(INVALID_REQUEST.getText()) {
            @Override
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
                        SLF4JLoggerProxy.debug(this,
                                               String.format("%d:%d:%d:%d",
                                                             contentsCounter,
                                                             providersCounter,
                                                             symbolsCounter,
                                                             exchangesCounter));
                        final String symbolRequestString = marketDataRequestStringFromComponents(providers[providersCounter],
                                                                                                 exchanges[exchangesCounter],
                                                                                                 contents[contentsCounter],
                                                                                                 EQUITY,
                                                                                                 symbols[symbolsCounter],
                                                                                                 null);
                        // catch-all for a totally empty request
                        if(symbolRequestString.isEmpty()) {
                            new ExpectedFailure<IllegalArgumentException>(INVALID_REQUEST.getText()) {
                                @Override
                                protected void run()
                                    throws Exception
                                {
                                    MarketDataRequest.newRequestFromString(symbolRequestString);
                                }
                            };
                            continue;
                        }
                        // symbols error conditions
                        if(isEmptyList(symbols[symbolsCounter])) {
                            // the message isn't checked because the ingoing request string won't equal the incoming one, so the message contents are different 
                            new ExpectedFailure<IllegalArgumentException>(null) {
                                @Override
                                protected void run()
                                    throws Exception
                                {
                                    MarketDataRequest.newRequestFromString(symbolRequestString);
                                }
                            };
                            continue;
                        }
                        MarketDataRequest request = MarketDataRequest.newRequestFromString(symbolRequestString);
                        verifyRequest(request,
                                      providers[providersCounter],
                                      exchanges[exchangesCounter],
                                      contentsCounter == 0 ? TOP_OF_BOOK : contents[contentsCounter],
                                      EQUITY,
                                      symbols[symbolsCounter],
                                      null);
                        // check again, but use underlying symbols instead of symbols
                        final String underlyingSymbolRequestString = marketDataRequestStringFromComponents(providers[providersCounter],
                                                                                                           exchanges[exchangesCounter],
                                                                                                           contents[contentsCounter],
                                                                                                           OPTION,
                                                                                                           null,
                                                                                                           symbols[symbolsCounter]);
                        if(isEmptyList(symbols[symbolsCounter])) {
                            // the message isn't checked because the ingoing request string won't equal the incoming one, so the message contents are different 
                            new ExpectedFailure<IllegalArgumentException>(null) {
                                @Override
                                protected void run()
                                    throws Exception
                                {
                                    MarketDataRequest.newRequestFromString(underlyingSymbolRequestString);
                                }
                            };
                            continue;
                        }
                        if(contents[contentsCounter] == DIVIDEND) {
                            new ExpectedFailure<IllegalArgumentException>(null) {
                                @Override
                                protected void run()
                                    throws Exception
                                {
                                    MarketDataRequest.newRequestFromString(underlyingSymbolRequestString);
                                }
                            };
                            continue;
                        }
                        request = MarketDataRequest.newRequestFromString(underlyingSymbolRequestString);
                        verifyRequest(request,
                                      providers[providersCounter],
                                      exchanges[exchangesCounter],
                                      contentsCounter == 0 ? TOP_OF_BOOK : contents[contentsCounter],
                                      OPTION,
                                      null,
                                      symbols[symbolsCounter]);
                        
                    }
                }
            }
        }
    }
    /**
     * Tests {@link MarketDataRequest#ofAssetClass(AssetClass)} and {@link MarketDataRequest#ofAssetClass(String)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void assetClass()
        throws Exception
    {
        final String symbols = "METC";
        final String provider = "provider";
        // test strings version of ofAssetClass
        for(int assetClassCounter=0;assetClassCounter<assetClassStrings.length;assetClassCounter++) {
            final String assetClassString = assetClassStrings[assetClassCounter];
            if(assetClassString == null ||
               assetClassString.isEmpty()) {
                new ExpectedFailure<IllegalArgumentException>(null) {
                    @Override
                    protected void run()
                        throws Exception
                    {
                        MarketDataRequest.newRequest().fromProvider(provider).withSymbols(symbols).ofAssetClass(assetClassString);                    
                    }
                };
                continue;
            } 
            if(!ALL_ASSET_CLASSES.contains(assetClassString.toUpperCase())) {
                new ExpectedFailure<IllegalArgumentException>(INVALID_ASSET_CLASS.getText(assetClassString)) {
                    @Override
                    protected void run()
                        throws Exception
                    {
                        MarketDataRequest.newRequest().fromProvider(provider).withSymbols(symbols).ofAssetClass(assetClassString);                    
                    }
                };
                continue;
            } 
            MarketDataRequest request = MarketDataRequest.newRequest().fromProvider(provider).withSymbols(symbols).ofAssetClass(assetClassString); 
            verifyRequest(request,
                          provider,
                          null,
                          TOP_OF_BOOK,
                          AssetClass.valueOf(assetClassString.toUpperCase()),
                          symbols,
                          null);
        }
        // test enum version of ofAssetClass
        for(int assetClassCounter=0;assetClassCounter<assetClasses.length;assetClassCounter++) {
            final AssetClass assetClass = assetClasses[assetClassCounter];
            if(assetClass == null) {
                new ExpectedFailure<IllegalArgumentException>(null) {
                    @Override
                    protected void run()
                        throws Exception
                    {
                        MarketDataRequest.newRequest().fromProvider(provider).withSymbols(symbols).ofAssetClass(assetClass);
                    }
                };
            } else {
                MarketDataRequest request = MarketDataRequest.newRequest().fromProvider(provider).withSymbols(symbols).ofAssetClass(assetClass);
                verifyRequest(request,
                              provider,
                              null,
                              TOP_OF_BOOK,
                              assetClass,
                              symbols,
                              null);
            }
        }
    }
    /**
     * Tests {@link MarketDataRequest#validate(MarketDataRequest)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void wholeRequestValidation()
        throws Exception
    {
        final MarketDataRequest[] request = new MarketDataRequest[] { new MarketDataRequest() };
        // test neither symbols nor underlying symbols
        assertTrue(request[0].getSymbols().length == 0);
        assertTrue(request[0].getUnderlyingSymbols().length == 0);
        new ExpectedFailure<IllegalArgumentException>(NEITHER_SYMBOLS_NOR_UNDERLYING_SYMBOLS_SPECIFIED.getText(request[0])) {
            @Override
            protected void run()
                throws Exception
            {
                MarketDataRequest.validate(request[0]);
            }
        };
        // test specifying both symbols and underlying symbols
        request[0].withSymbols("METC");
        request[0].withUnderlyingSymbols("GOOG");
        assertTrue(Arrays.equals(new String[] { "METC" },
                                 request[0].getSymbols()));
        assertTrue(Arrays.equals(new String[] { "GOOG" },
                                 request[0].getUnderlyingSymbols()));
        new ExpectedFailure<IllegalArgumentException>(BOTH_SYMBOLS_AND_UNDERLYING_SYMBOLS_SPECIFIED.getText(request[0])) {
            @Override
            protected void run()
                throws Exception
            {
                MarketDataRequest.validate(request[0]);
            }
        };
        // underlying symbols and asset class != option
        request[0] = new MarketDataRequest();
        request[0].withUnderlyingSymbols("METC");
        request[0].ofAssetClass(EQUITY);
        assertTrue(Arrays.equals(new String[] { "METC" },
                                 request[0].getUnderlyingSymbols()));
        assertEquals(EQUITY,
                     request[0].getAssetClass());
        new ExpectedFailure<IllegalArgumentException>(OPTION_ASSET_CLASS_REQUIRED.getText(request[0],
                                                                                          request[0].getAssetClass())) {
            @Override
            protected void run()
                throws Exception
            {
                MarketDataRequest.validate(request[0]);
            }
        };
        // dividend content requires symbols
        request[0].withContent(DIVIDEND);
        request[0].ofAssetClass(OPTION);
        assertTrue(Arrays.equals(new String[] { "METC" },
                                 request[0].getUnderlyingSymbols()));
        assertTrue(Arrays.equals(new Content[] { DIVIDEND },
                                 request[0].getContent().toArray(new Content[1])));
        assertEquals(OPTION,
                     request[0].getAssetClass());
        new ExpectedFailure<IllegalArgumentException>(DIVIDEND_REQUIRES_SYMBOLS.getText(request[0])) {
            @Override
            protected void run()
                throws Exception
            {
                MarketDataRequest.validate(request[0]);
            }
        };
    }
    /**
     * Tests {@link MarketDataRequest#hashCode()} and {@link MarketDataRequest#equals(Object)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void hashCodeEquals()
        throws Exception
    {
       MarketDataRequest request1 = new MarketDataRequest();
       MarketDataRequest request2 = new MarketDataRequest();
       assertEquals(request1,
                    request1);
       assertFalse(request1.equals(null));
       assertFalse(request1.equals(this));
       assertEquals(request1,
                    request2);
       assertTrue(request1.hashCode() == request2.hashCode());
       // differ by content
       request1.withContent(TOTAL_VIEW);
       assertFalse(request1.getContent().equals(request2.getContent()));
       assertFalse(request1.hashCode() == request2.hashCode());
       assertFalse(request1.equals(request2));
       // differ by exchange
       request1.withContent(request2.getContent().toArray(new Content[1]));
       assertEquals(request1,
                    request2);
       request1.fromExchange("exchange");
       assertNull(request2.getExchange());
       assertFalse(request1.equals(request2));
       assertFalse(request2.equals(request1));
       request2.fromExchange("other exchange");
       assertFalse(request1.getExchange().equals(request2.getExchange()));
       assertFalse(request1.equals(request2));
       assertFalse(request2.equals(request1));
       // differ by provider
       request1.fromExchange(request2.getExchange());
       assertEquals(request1,
                    request2);
       request1.fromProvider("provider");
       assertNull(request2.getProvider());
       assertFalse(request1.equals(request2));
       assertFalse(request2.equals(request1));
       request2.fromProvider("other provider");
       assertFalse(request1.getProvider().equals(request2.getProvider()));
       assertFalse(request1.equals(request2));
       assertFalse(request2.equals(request1));
       // differ by asset class
       request1.fromProvider(request2.getProvider());
       assertEquals(request1,
                    request2);
       assertEquals(EQUITY,
                    request1.getAssetClass());
       request2.ofAssetClass(OPTION);
       assertFalse(request1.equals(request2));
       assertFalse(request2.equals(request1));
       // differ by symbols
       request1.ofAssetClass(request2.getAssetClass());
       assertEquals(request1,
                    request2);
       assertTrue(request1.getSymbols().length == 0);
       assertTrue(request2.getSymbols().length == 0);
       request1.withSymbols("METC,GOOG");
       assertFalse(request1.equals(request2));
       assertFalse(request2.equals(request1));
       // differ by underlyingsymbols
       request1 = new MarketDataRequest();
       request2 = new MarketDataRequest();
       assertEquals(request1,
                    request2);
       assertTrue(request1.getUnderlyingSymbols().length == 0);
       assertTrue(request2.getUnderlyingSymbols().length == 0);
       request1.withUnderlyingSymbols("METC,GOOG");
       assertFalse(request1.equals(request2));
       assertFalse(request2.equals(request1));
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
                    @Override
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
                    @Override
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
                          EQUITY,
                          symbols,
                          null);
        }
        // test enum version of withContent
        for(int contentCounter=0;contentCounter<contents.length;contentCounter++) {
            final Content content = contents[contentCounter];
            if(content == null) {
                new ExpectedFailure<IllegalArgumentException>(INVALID_CONTENT.getText(Arrays.toString(new Content[] { null }))) {
                    @Override
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
                              EQUITY,
                              symbols,
                              null);
            }
        }
        // test multiple enum version of withContent
        for(int contentCounter=0;contentCounter<contentArrays.length;contentCounter++) {
            final Content[] content = contentArrays[contentCounter];
            if(content == null ||
               content.length == 0) {
                new ExpectedFailure<IllegalArgumentException>(MISSING_CONTENT.getText()) {
                    @Override
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
                    @Override
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
                          EQUITY,
                          symbols,
                          null);
        }
        // test multiple string version of withContent
        for(int contentCounter=0;contentCounter<contentStringArrays.length;contentCounter++) {
            final String[] content = contentStringArrays[contentCounter];
            if(content == null ||
               content.length == 0 ||
               isEmptyStringList(content) ||
               !isValidStringList(content)) {
                new ExpectedFailure<IllegalArgumentException>(null) {
                    @Override
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
                    @Override
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
                          EQUITY,
                          symbols,
                          null);
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
            @Override
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
                    @Override
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
                              EQUITY,
                              symbolArrayToString(symbolArray),
                              null);
            }
        }
        // test single, comma-delimited version
        for(int symbolCounter=0;symbolCounter<symbols.length;symbolCounter++) {
            final String symbol = symbols[symbolCounter];
            if(isEmptyList(symbol)) {
                new ExpectedFailure<IllegalArgumentException>(MISSING_SYMBOLS.getText()) {
                    @Override
                    protected void run()
                        throws Exception
                    {
                        MarketDataRequest.newRequest().fromProvider(provider).withSymbols(symbol);                    
                    }
                };
            } else if(symbol == null ||
                      symbol.trim().isEmpty()) {
                new ExpectedFailure<IllegalArgumentException>(INVALID_SYMBOLS) {
                    @Override
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
                              EQUITY,
                              symbol,
                              null);
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
     * Tests the ability to set and retrieve underlying symbols. 
     *
     * @throws Exception if an error occurs
     */
    @Test
    public void underlyingSymbol()
        throws Exception
    {
        final String provider = "provider";
        // test array version of withUnderlyingSymbols
        for(int symbolCounter=0;symbolCounter<symbolArrays.length;symbolCounter++) {
            final String[] symbolArray = symbolArrays[symbolCounter];
            if(isEmptyStringList(symbolArray)) {
                new ExpectedFailure<IllegalArgumentException>(MISSING_UNDERLYING_SYMBOLS.getText()) {
                    @Override
                    protected void run()
                        throws Exception
                    {
                        MarketDataRequest.newRequest().fromProvider(provider).withUnderlyingSymbols(symbolArray).ofAssetClass(OPTION);                    
                    }
                };
            } else {
                MarketDataRequest request = MarketDataRequest.newRequest().fromProvider(provider).withUnderlyingSymbols(symbolArray).ofAssetClass(OPTION);
                verifyRequest(request,
                              provider,
                              null,
                              TOP_OF_BOOK,
                              OPTION,
                              null,
                              symbolArrayToString(symbolArray));
            }
        }
        // test single, comma-delimited version
        for(int symbolCounter=0;symbolCounter<symbols.length;symbolCounter++) {
            final String symbol = symbols[symbolCounter];
            if(isEmptyList(symbol)) {
                new ExpectedFailure<IllegalArgumentException>(MISSING_UNDERLYING_SYMBOLS.getText()) {
                    @Override
                    protected void run()
                        throws Exception
                    {
                        MarketDataRequest.newRequest().fromProvider(provider).withUnderlyingSymbols(symbol).ofAssetClass(OPTION);                    
                    }
                };
            } else if(symbol == null ||
                      symbol.trim().isEmpty()) {
                new ExpectedFailure<IllegalArgumentException>(INVALID_UNDERLYING_SYMBOLS) {
                    @Override
                    protected void run()
                        throws Exception
                    {
                        MarketDataRequest.newRequest().fromProvider(provider).withUnderlyingSymbols(symbol).ofAssetClass(OPTION);                    
                    }
                };
            } else {
                verifyRequest(MarketDataRequest.newRequest().fromProvider(provider).withUnderlyingSymbols(symbol).ofAssetClass(OPTION),
                              provider,
                              null,
                              TOP_OF_BOOK,
                              OPTION,
                              null,
                              symbol);
            }
        }
        // check for white-space around the symbol
        String startingSymbols = "METC, ORCL, GOOG ";
        MarketDataRequest request = MarketDataRequest.newRequest().fromProvider(provider).withUnderlyingSymbols(startingSymbols).ofAssetClass(OPTION);
        String[] actualSymbols = request.getUnderlyingSymbols();
        assertEquals("METC",
                     actualSymbols[0]);
        assertEquals("ORCL",
                     actualSymbols[1]);
        assertEquals("GOOG",
                     actualSymbols[2]);
        // make sure that changing the source doesn't change the held values
        String testSymbol = "METC";
        request.withUnderlyingSymbols(testSymbol);
        assertArrayEquals(new String[] { "METC" },
                          request.getUnderlyingSymbols());
        testSymbol = "GOOG";
        assertArrayEquals(new String[] { "METC" },
                          request.getUnderlyingSymbols());
        // make sure that changing the destination doesn't change the held values
        actualSymbols = request.getUnderlyingSymbols();
        assertArrayEquals(new String[] { "METC" },
                          actualSymbols);
        assertArrayEquals(new String[] { "METC" },
                          request.getUnderlyingSymbols());
        actualSymbols = new String[] { "ORCL" };
        assertArrayEquals(new String[] { "METC" },
                          request.getUnderlyingSymbols());
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
                          EQUITY,
                          symbol,
                          null);
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
                          EQUITY,
                          symbol,
                          null);
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
        new ExpectedFailure<NullPointerException>(null) {
            @Override
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
                                                                                                                 EQUITY,
                                                                                                                 "METC",
                                                                                                                 null));
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
                              EQUITY,
                              "METC",
                              null);
            } else {
                new ExpectedFailure<IllegalArgumentException>(INVALID_CONTENT.getText(null)) {
                    @Override
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
                                                                                                                 EQUITY,
                                                                                                                 "METC",
                                                                                                                 null));
            requestString.append(Util.KEY_VALUE_DELIMITER);
            requestString.append(CONTENT_KEY).append(Util.KEY_VALUE_SEPARATOR);
            boolean delimiterNeeded = false;
            boolean errorExpected = false;
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
                              EQUITY,
                              "METC",
                              null);
            } else {
                new ExpectedFailure<IllegalArgumentException>(null) {
                    @Override
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
     * Tests that {@link MarketDataRequest.Content#isRelevantTo(Class)} works as expected.
     *
     * @throws Exception if an error occurs
     */
    @Test
    public void eventRelevance()
        throws Exception
    {
        new ExpectedFailure<NullPointerException>(null) {
            @Override
            protected void run()
                throws Exception
            {
                TOP_OF_BOOK.isRelevantTo(null);
            }
        };
        Set<Class<? extends Event>> eventTypes = new HashSet<Class<? extends Event>>();
        eventTypes.add(AskEvent.class);
        eventTypes.add(BidEvent.class);
        eventTypes.add(MarketstatEvent.class);
        eventTypes.add(TradeEvent.class);
        eventTypes.add(LogEvent.class);
        Multimap<Content,Class<? extends Event>> relevantTypes = HashMultimap.create();
        relevantTypes.put(TOP_OF_BOOK,
                          BidEvent.class);
        relevantTypes.put(TOP_OF_BOOK,
                          AskEvent.class);
        relevantTypes.put(OPEN_BOOK,
                          BidEvent.class);
        relevantTypes.put(OPEN_BOOK,
                          AskEvent.class);
        relevantTypes.put(TOTAL_VIEW,
                          BidEvent.class);
        relevantTypes.put(TOTAL_VIEW,
                          AskEvent.class);
        relevantTypes.put(LEVEL_2,
                          BidEvent.class);
        relevantTypes.put(LEVEL_2,
                          AskEvent.class);
        relevantTypes.put(LATEST_TICK,
                          TradeEvent.class);
        relevantTypes.put(MARKET_STAT,
                          MarketstatEvent.class);
        relevantTypes.put(DIVIDEND,
                          DividendEvent.class);
        Set<Content> contents = EnumSet.allOf(Content.class);
        for(Content content : contents) {
            for(Class<? extends Event> eventType : eventTypes) {
                assertEquals(relevantTypes.get(content).contains(eventType),
                             content.isRelevantTo(eventType));
            }
        }
    }
    /**
     * Test the function of {@link MarketDataRequest.Content#getAsCapability()}. 
     *
     * @throws Exception if an error occurs
     */
    @Test
    public void getAsCapability()
        throws Exception
    {
        Map<Content,Capability> expectedCapabilities = new HashMap<Content,Capability>();
        expectedCapabilities.put(LATEST_TICK,
                                 Capability.LATEST_TICK);
        expectedCapabilities.put(LEVEL_2,
                                 Capability.LEVEL_2);
        expectedCapabilities.put(MARKET_STAT,
                                 Capability.MARKET_STAT);
        expectedCapabilities.put(OPEN_BOOK,
                                 Capability.OPEN_BOOK);
        expectedCapabilities.put(TOP_OF_BOOK,
                                 Capability.TOP_OF_BOOK);
        expectedCapabilities.put(TOTAL_VIEW,
                                 Capability.TOTAL_VIEW);
        for(Entry<Content,Capability> entry : expectedCapabilities.entrySet()) {
            Content content = entry.getKey();
            Capability capability = entry.getValue();
            // verify the mapping
            assertEquals(capability,
                         content.getAsCapability());
            // anti-verify the complement of the capability (this content is *not* all the other capabilities)
            EnumSet<Capability> capabilityComplement = EnumSet.complementOf(EnumSet.of(capability));
            for(Capability other : capabilityComplement) {
                assertFalse(other.equals(content.getAsCapability()));
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
     * @param inAssetClass an <code>AssetClass</code> value containing the expected <code>AssetClass</code>
     * @param inSymbols a <code>String</code> value containing a symbol or symbols delimited by 
     *  {@link MarketDataRequest#SYMBOL_DELIMITER} or <code>null</code>
     * @param inUnderlyingSymbols a <code>String</code> value containing a symbol or symbols delimited by 
     *  {@link MarketDataRequest#SYMBOL_DELIMITER} or <code>null</code>
     * @return a <code>String</code> value
     */
    private static String marketDataRequestStringFromComponents(String inProvider,
                                                                String inExchange,
                                                                Content inContent,
                                                                AssetClass inAssetClass,
                                                                String inSymbols,
                                                                String inUnderlyingSymbols)
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
        if(inSymbols != null &&
           !inSymbols.isEmpty()) {
            if(delimiterNeeded) {
                request.append(Util.KEY_VALUE_DELIMITER);
            }
            request.append(SYMBOLS_KEY).append(Util.KEY_VALUE_SEPARATOR).append(inSymbols);
            delimiterNeeded = true;
        }
        if(inUnderlyingSymbols != null &&
          !inUnderlyingSymbols.isEmpty()) {
            if(delimiterNeeded) {
                request.append(Util.KEY_VALUE_DELIMITER);
            }
            request.append(UNDERLYINGSYMBOLS_KEY).append(Util.KEY_VALUE_SEPARATOR).append(inUnderlyingSymbols);
            delimiterNeeded = true;
        }
        if(inAssetClass != null) {
            if(delimiterNeeded) {
                request.append(Util.KEY_VALUE_DELIMITER);
            }
            request.append(ASSETCLASS_KEY).append(Util.KEY_VALUE_SEPARATOR).append(inAssetClass);
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
     * @param inAssetClass an <code>AssetClass</code> value containing the expected <code>AssetClass</code>
     * @param inSymbols a <code>String[]</code> value containing the expected <code>Symbol</code> values
     * @param inUnderlyingSymbols a <code>String[]</code> value containing the expected <code>UnderlyingSymbol</code> values
     * @throws Exception if an error occurs
     */
    private static void verifyRequest(final MarketDataRequest inActualRequest,
                                      String inProvider,
                                      String inExchange,
                                      Content[] inContent,
                                      AssetClass inAssetClass,
                                      String inSymbols,
                                      String inUnderlyingSymbols)
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
        // test asset class
        AssetClass assetClass = inActualRequest.getAssetClass();
        assertNotNull(assetClass);
        assertEquals(inAssetClass,
                     assetClass);
        // test symbols
        if(inSymbols != null) {
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
        } else {
            assertTrue(inActualRequest.getSymbols().length == 0);
        }
        if(inUnderlyingSymbols != null) {
            // test underlying symbols
            String[] expectedSymbols = inUnderlyingSymbols.split(MarketDataRequest.SYMBOL_DELIMITER);
            String[] actualSymbols = inActualRequest.getUnderlyingSymbols();
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
                                  inActualRequest.getUnderlyingSymbols());
            }
        } else {
            assertTrue(inActualRequest.getUnderlyingSymbols().length == 0);
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
     * @param inAssetClass an <code>AssetClass</code> value containing the expected <code>AssetClass</code>
     * @param inSymbols a <code>String</code> value containing the expected <code>Symbol</code> values
     * @param inUnderlyingSymbols a <code>String</code> value containing the expected <code>UnderlyingSymbol</code> values
     * @throws Exception if an error occurs
     */
    private static void verifyRequest(MarketDataRequest inActualRequest,
                                      String inProvider,
                                      String inExchange,
                                      Content inContent,
                                      AssetClass inAssetClass,
                                      String inSymbols,
                                      String inUnderlyingSymbols)
        throws Exception
    {
        verifyRequest(inActualRequest,
                      inProvider,
                      inExchange,
                      new Content[] { inContent },
                      inAssetClass,
                      inSymbols,
                      inUnderlyingSymbols);
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
            if(symbol == null ||
               symbol.trim().isEmpty()) {
                return true;
            }
        }
        return false;
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
