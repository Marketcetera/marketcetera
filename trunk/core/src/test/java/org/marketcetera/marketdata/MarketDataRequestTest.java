package org.marketcetera.marketdata;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.marketcetera.marketdata.AssetClass.EQUITY;
import static org.marketcetera.marketdata.AssetClass.FUTURE;
import static org.marketcetera.marketdata.AssetClass.OPTION;
import static org.marketcetera.marketdata.AssetClass.CURRENCY;
import static org.marketcetera.marketdata.Content.*;
import static org.marketcetera.marketdata.MarketDataRequestBuilder.SYMBOL_DELIMITER;

import java.util.*;

import org.junit.Test;
import org.marketcetera.module.ExpectedFailure;
import org.marketcetera.util.log.I18NBoundMessage1P;
import org.marketcetera.util.log.I18NBoundMessage2P;
import org.marketcetera.util.test.EqualityAssert;
import org.marketcetera.util.test.UnicodeData;

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
    /**
     * Tests {@link MarketDataRequestBuilder#newRequestFromString(String)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void newRequestFromStringInvalid()
            throws Exception
    {
        new ExpectedFailure<NullPointerException>() {
            @Override
            protected void run()
                    throws Exception
            {
                MarketDataRequestBuilder.newRequestFromString(null);
            }
        };
        final String[] invalidRequests = new String[] {
                                                        "",
                                                        "not-a-key=value",
                                                        "provider=provider",
                                                        "symbols=A:underlyingsymbols=B"
                                                      };
        for(final String request : invalidRequests) {
            new ExpectedFailure<IllegalArgumentException>() {
                @Override
                protected void run()
                        throws Exception
                {
                    MarketDataRequestBuilder.newRequestFromString(request);
                }
            };
        }
    }
    /**
     * Tests building <code>MarketDataRequest</code> objects from String
     * focusing on symbols. 
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void newRequestFromStringSymbols()
            throws Exception
    {
        AssetClass defaultAssetClass = EQUITY;
        // empty symbol list
        new ExpectedFailure<IllegalArgumentException>(new I18NBoundMessage1P(INVALID_SYMBOLS,
                                                                             String.valueOf(Arrays.asList(new String[] { "" } ))).getText()) {
            @Override
            protected void run()
                    throws Exception
            {
                MarketDataRequestBuilder.newRequestFromString("symbols=");
            }
        };
        // single symbol list
        verifyRequest(MarketDataRequestBuilder.newRequestFromString("symbols=GOOG"),
                      null,
                      null,
                      defaultContent,
                      defaultAssetClass,
                      new HashMap<String,String>(),
                      new LinkedHashSet<String>(Arrays.asList(new String[] { "GOOG" })),
                      new HashSet<String>());
        // multiple symbol list
        verifyRequest(MarketDataRequestBuilder.newRequestFromString("symbols=GOOG,ORCL"),
                      null,
                      null,
                      defaultContent,
                      defaultAssetClass,
                      new HashMap<String,String>(),
                      new LinkedHashSet<String>(Arrays.asList(new String[] { "GOOG", "ORCL" })),
                      new HashSet<String>());
        // duplicate symbol list
        verifyRequest(MarketDataRequestBuilder.newRequestFromString("symbols=GOOG,ORCL,GOOG"),
                      null,
                      null,
                      defaultContent,
                      defaultAssetClass,
                      new HashMap<String,String>(),
                      new LinkedHashSet<String>(Arrays.asList(new String[] { "GOOG", "ORCL" })),
                      new HashSet<String>());
        // UPPER case key
        verifyRequest(MarketDataRequestBuilder.newRequestFromString("SYMBOLS=GOOG"),
                      null,
                      null,
                      defaultContent,
                      defaultAssetClass,
                      new HashMap<String,String>(),
                      new LinkedHashSet<String>(Arrays.asList(new String[] { "GOOG" })),
                      new HashSet<String>());
        // MiXeD case key
        verifyRequest(MarketDataRequestBuilder.newRequestFromString("SyMbOlS=GOOG"),
                      null,
                      null,
                      defaultContent,
                      defaultAssetClass,
                      new HashMap<String,String>(),
                      new LinkedHashSet<String>(Arrays.asList(new String[] { "GOOG" })),
                      new HashSet<String>());
        // extra whitespace, both key and value
        verifyRequest(MarketDataRequestBuilder.newRequestFromString("  symbols  =  GOOG  "),
                      null,
                      null,
                      defaultContent,
                      defaultAssetClass,
                      new HashMap<String,String>(),
                      new LinkedHashSet<String>(Arrays.asList(new String[] { "GOOG" })),
                      new HashSet<String>());
    }
    /**
     * Tests building <code>MarketDataRequest</code> objects from String
     * focusing on underlying symbols. 
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void newRequestFromStringUnderlyingSymbols()
            throws Exception
    {
        for(AssetClass assetClass : AssetClass.values()) {
            if(assetClass.isValidForUnderlyingSymbols()) {
                doUnderlyingSymbolTest(assetClass);
            }
        }
    }
    /**
     * Tests building <code>MarketDataRequest</code> objects from String
     * focusing on provider. 
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void newRequestFromStringProvider()
            throws Exception
    {
        Set<String> symbols = new LinkedHashSet<String>(Arrays.asList(new String[] { "METC" }));
        AssetClass defaultAssetClass = EQUITY;
        // empty (default) provider
        verifyRequest(MarketDataRequestBuilder.newRequestFromString("symbols=METC"),
                      null,
                      null,
                      defaultContent,
                      defaultAssetClass,
                      new HashMap<String,String>(),
                      symbols,
                      new HashSet<String>());
        // empty (specified) provider
        verifyRequest(MarketDataRequestBuilder.newRequestFromString("symbols=METC:provider="),
                      "",
                      null,
                      defaultContent,
                      defaultAssetClass,
                      new HashMap<String,String>(),
                      symbols,
                      new HashSet<String>());
        // specified provider
        verifyRequest(MarketDataRequestBuilder.newRequestFromString("symbols=METC:provider=provider"),
                      "provider",
                      null,
                      defaultContent,
                      defaultAssetClass,
                      new HashMap<String,String>(),
                      symbols,
                      new HashSet<String>());
        // non-ASCII provider
        verifyRequest(MarketDataRequestBuilder.newRequestFromString("symbols=METC:provider=" + UnicodeData.HELLO_GR),
                      UnicodeData.HELLO_GR,
                      null,
                      defaultContent,
                      defaultAssetClass,
                      new HashMap<String,String>(),
                      symbols,
                      new HashSet<String>());
        // UPPER-case key
        verifyRequest(MarketDataRequestBuilder.newRequestFromString("symbols=METC:PROVIDER=provider"),
                      "provider",
                      null,
                      defaultContent,
                      defaultAssetClass,
                      new HashMap<String,String>(),
                      symbols,
                      new HashSet<String>());
        // MiXeD-case key
        verifyRequest(MarketDataRequestBuilder.newRequestFromString("symbols=METC:PrOvIdEr=provider"),
                      "provider",
                      null,
                      defaultContent,
                      defaultAssetClass,
                      new HashMap<String,String>(),
                      symbols,
                      new HashSet<String>());
        // specified provider with whitepsace (key and value)
        verifyRequest(MarketDataRequestBuilder.newRequestFromString("symbols=METC:  provider  =  some provider  "),
                      "some provider",
                      null,
                      defaultContent,
                      defaultAssetClass,
                      new HashMap<String,String>(),
                      symbols,
                      new HashSet<String>());
    }
    /**
     * Tests building <code>MarketDataRequest</code> objects from String
     * focusing on exchange. 
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void newRequestFromStringExchange()
            throws Exception
    {
        Set<String> symbols = new LinkedHashSet<String>(Arrays.asList(new String[] { "METC" }));
        AssetClass defaultAssetClass = EQUITY;
        // empty (default) exchange
        verifyRequest(MarketDataRequestBuilder.newRequestFromString("symbols=METC"),
                      null,
                      null,
                      defaultContent,
                      defaultAssetClass,
                      new HashMap<String,String>(),
                      symbols,
                      new HashSet<String>());
        // empty (specified) exchange
        verifyRequest(MarketDataRequestBuilder.newRequestFromString("symbols=METC:exchange="),
                      null,
                      "",
                      defaultContent,
                      defaultAssetClass,
                      new HashMap<String,String>(),
                      symbols,
                      new HashSet<String>());
        // specified exchange
        verifyRequest(MarketDataRequestBuilder.newRequestFromString("symbols=METC:exchange=exchange"),
                      null,
                      "exchange",
                      defaultContent,
                      defaultAssetClass,
                      new HashMap<String,String>(),
                      symbols,
                      new HashSet<String>());
        // non-ASCII exchange
        verifyRequest(MarketDataRequestBuilder.newRequestFromString("symbols=METC:exchange=" + UnicodeData.HELLO_GR),
                      null,
                      UnicodeData.HELLO_GR,
                      defaultContent,
                      defaultAssetClass,
                      new HashMap<String,String>(),
                      symbols,
                      new HashSet<String>());
        // UPPER-case key
        verifyRequest(MarketDataRequestBuilder.newRequestFromString("symbols=METC:EXCHANGE=exchange"),
                      null,
                      "exchange",
                      defaultContent,
                      defaultAssetClass,
                      new HashMap<String,String>(),
                      symbols,
                      new HashSet<String>());
        // MiXeD-case key
        verifyRequest(MarketDataRequestBuilder.newRequestFromString("symbols=METC:ExChAnGe=EXCHANGE"),
                      null,
                      "EXCHANGE",
                      defaultContent,
                      defaultAssetClass,
                      new HashMap<String,String>(),
                      symbols,
                      new HashSet<String>());
        // specified exchange with whitepsace (key and value)
        verifyRequest(MarketDataRequestBuilder.newRequestFromString("symbols=METC:  exchange  =  some exchange  "),
                      null,
                      "some exchange",
                      defaultContent,
                      defaultAssetClass,
                      new HashMap<String,String>(),
                      symbols,
                      new HashSet<String>());
    }
    /**
     * Tests building <code>MarketDataRequest</code> objects from String
     * focusing on asset class. 
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void newRequestFromStringAssetClass()
            throws Exception
    {
        Set<String> symbols = new LinkedHashSet<String>(Arrays.asList(new String[] { "METC" }));
        AssetClass defaultAssetClass = EQUITY;
        // empty (default) asset class
        verifyRequest(MarketDataRequestBuilder.newRequestFromString("symbols=METC"),
                      null,
                      null,
                      defaultContent,
                      defaultAssetClass,
                      new HashMap<String,String>(),
                      symbols,
                      new HashSet<String>());
        // specified but invalid asset class
        new ExpectedFailure<IllegalArgumentException>(new I18NBoundMessage1P(INVALID_ASSET_CLASS,
                                                                             String.valueOf("not-an-asset-class")).getText()) {
            @Override
            protected void run()
                    throws Exception
            {
                MarketDataRequestBuilder.newRequestFromString("symbols=METC:assetClass=not-an-asset-class");
            }
        };
        // empty (specified) asset class (uses default)
        verifyRequest(MarketDataRequestBuilder.newRequestFromString("symbols=METC:assetClass="),
                      null,
                      null,
                      defaultContent,
                      EQUITY,
                      new HashMap<String,String>(),
                      symbols,
                      new HashSet<String>());
        for(AssetClass assetClass : EnumSet.allOf(AssetClass.class)) {
            // specified asset class
            verifyRequest(MarketDataRequestBuilder.newRequestFromString("symbols=METC:assetClass=" + assetClass),
                          null,
                          null,
                          defaultContent,
                          assetClass,
                          new HashMap<String,String>(),
                          symbols,
                          new HashSet<String>());
            // UPPER-case key and value
            verifyRequest(MarketDataRequestBuilder.newRequestFromString("symbols=METC:ASSETCLASS=" + assetClass.toString().toUpperCase()),
                          null,
                          null,
                          defaultContent,
                          assetClass,
                          new HashMap<String,String>(),
                          symbols,
                          new HashSet<String>());
            // lower-case key and value
            verifyRequest(MarketDataRequestBuilder.newRequestFromString("symbols=METC:assetclass=" + assetClass.toString().toLowerCase()),
                          null,
                          null,
                          defaultContent,
                          assetClass,
                          new HashMap<String,String>(),
                          symbols,
                          new HashSet<String>());
            // MiXeD-case key
            verifyRequest(MarketDataRequestBuilder.newRequestFromString("symbols=METC:AsSeTcLaSs=" + assetClass),
                          null,
                          null,
                          defaultContent,
                          assetClass,
                          new HashMap<String,String>(),
                          symbols,
                          new HashSet<String>());
            // specified asset class with whitepsace (key and value)
            verifyRequest(MarketDataRequestBuilder.newRequestFromString("symbols=METC:  assetclass  =  " + assetClass + "  "),
                          null,
                          null,
                          defaultContent,
                          assetClass,
                          new HashMap<String,String>(),
                          symbols,
                          new HashSet<String>());
        }
    }
    /**
     * Tests building <code>MarketDataRequest</code> objects from String
     * focusing on content. 
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void newRequestFromStringContent()
            throws Exception
    {
        Set<String> symbols = new LinkedHashSet<String>(Arrays.asList(new String[] { "METC" }));
        AssetClass defaultAssetClass = EQUITY;
        // empty (default) content
        verifyRequest(MarketDataRequestBuilder.newRequestFromString("symbols=METC"),
                      null,
                      null,
                      defaultContent,
                      defaultAssetClass,
                      new HashMap<String,String>(),
                      symbols,
                      new HashSet<String>());
        // empty (specified) content
        new ExpectedFailure<IllegalArgumentException>(new I18NBoundMessage1P(INVALID_CONTENT,
                                                                             String.valueOf("")).getText()) {
            @Override
            protected void run()
                    throws Exception
            {
                MarketDataRequestBuilder.newRequestFromString("symbols=METC:content=");
            }
        };
        // specified but invalid content
        new ExpectedFailure<IllegalArgumentException>(new I18NBoundMessage1P(INVALID_CONTENT,
                                                                             String.valueOf("not-a-content")).getText()) {
            @Override
            protected void run()
                    throws Exception
            {
                MarketDataRequestBuilder.newRequestFromString("symbols=METC:content=not-a-content");
            }
        };
        for(Content content : EnumSet.allOf(Content.class)) {
            // specified asset class
            verifyRequest(MarketDataRequestBuilder.newRequestFromString("symbols=METC:content=" + content),
                          null,
                          null,
                          EnumSet.of(content),
                          defaultAssetClass,
                          new HashMap<String,String>(),
                          symbols,
                          new HashSet<String>());
            // UPPER-case key and value
            verifyRequest(MarketDataRequestBuilder.newRequestFromString("symbols=METC:CONTENT=" + content.toString().toUpperCase()),
                          null,
                          null,
                          EnumSet.of(content),
                          defaultAssetClass,
                          new HashMap<String,String>(),
                          symbols,
                          new HashSet<String>());
            // lower-case key and value
            verifyRequest(MarketDataRequestBuilder.newRequestFromString("symbols=METC:content=" + content.toString().toLowerCase()),
                          null,
                          null,
                          EnumSet.of(content),
                          defaultAssetClass,
                          new HashMap<String,String>(),
                          symbols,
                          new HashSet<String>());
            // MiXeD-case key
            verifyRequest(MarketDataRequestBuilder.newRequestFromString("symbols=METC:CoNtEnT=" + content),
                          null,
                          null,
                          EnumSet.of(content),
                          defaultAssetClass,
                          new HashMap<String,String>(),
                          symbols,
                          new HashSet<String>());
            // specified asset class with whitepsace (key and value)
            verifyRequest(MarketDataRequestBuilder.newRequestFromString("symbols=METC:  content  =  " + content + "  "),
                          null,
                          null,
                          EnumSet.of(content),
                          defaultAssetClass,
                          new HashMap<String,String>(),
                          symbols,
                          new HashSet<String>());
        }
        // multiple contents
        verifyRequest(MarketDataRequestBuilder.newRequestFromString("symbols=METC:content=" + TOP_OF_BOOK + "," + LATEST_TICK + "," + MARKET_STAT),
                      null,
                      null,
                      EnumSet.of(TOP_OF_BOOK,
                                 LATEST_TICK,
                                 MARKET_STAT),
                      defaultAssetClass,
                      new HashMap<String,String>(),
                      symbols,
                      new HashSet<String>());
    }
    /**
     * Tests building <code>MarketDataRequest</code> objects from String
     * focusing on parameters. 
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void newRequestFromStringParameters()
            throws Exception
    {
        Set<String> symbols = new LinkedHashSet<String>(Arrays.asList(new String[] { "METC" }));
        AssetClass defaultAssetClass = EQUITY;
        Map<String,String> expectedParameters = new HashMap<String,String>();
        // missing value
        expectedParameters.put("key",
                               "");
        verifyRequest(MarketDataRequestBuilder.newRequestFromString("symbols=METC:parameters=key\\="),
                      null,
                      null,
                      defaultContent,
                      defaultAssetClass,
                      expectedParameters,
                      symbols,
                      new HashSet<String>());
        // missing key
        expectedParameters.clear();
        expectedParameters.put("",
                               "value");
        verifyRequest(MarketDataRequestBuilder.newRequestFromString("symbols=METC:parameters=\\=value"),
                      null,
                      null,
                      defaultContent,
                      defaultAssetClass,
                      expectedParameters,
                      symbols,
                      new HashSet<String>());
        // duplicates
        expectedParameters.clear();
        expectedParameters.put("key",
                               "value");
        verifyRequest(MarketDataRequestBuilder.newRequestFromString("symbols=METC:parameters=key\\=value\\:key\\=value"),
                      null,
                      null,
                      defaultContent,
                      defaultAssetClass,
                      expectedParameters,
                      symbols,
                      new HashSet<String>());
        // more than one distinct
        expectedParameters.clear();
        expectedParameters.put("key",
                               "value");
        expectedParameters.put("key2",
                               "value2");
        verifyRequest(MarketDataRequestBuilder.newRequestFromString("symbols=METC:parameters=key\\=value\\:key\\=value\\:key2\\=value2"),
                      null,
                      null,
                      defaultContent,
                      defaultAssetClass,
                      expectedParameters,
                      symbols,
                      new HashSet<String>());
    }
    /**
     * Tests {@link MarketDataRequestBuilder#withSymbols(String)}, {@link MarketDataRequestBuilder#withSymbols(String...)}, and
     * {@link MarketDataRequestBuilder#withSymbols(Collection)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void withSymbols()
            throws Exception
    {
        final MarketDataRequestBuilder builder = MarketDataRequestBuilder.newRequest();
        // set defaults to make our "toString" look like the "toString" with defaults
        builder.withContent(defaultContent);
        // the first conditions are going to test the ability to set the symbol list to empty in various ways
        // in order to test this without triggering an error condition, since the symbol list will be empty,
        //  set underlying symbols and asset class
        Set<String> symbolSet = new LinkedHashSet<String>(Arrays.asList(new String[] { "METC", "GOOG" } ));
        builder.withUnderlyingSymbols(symbolSet).withAssetClass(OPTION);
        // test null array
        builder.withSymbols((String[])null);
        verifyRequest(builder.create(),
                      null,
                      null,
                      defaultContent,
                      OPTION,
                      new HashMap<String,String>(),
                      null,
                      symbolSet);
        // test empty array
        builder.withSymbols(new String[0]);
        verifyRequest(builder.create(),
                      null,
                      null,
                      defaultContent,
                      OPTION,
                      new HashMap<String,String>(),
                      null,
                      symbolSet);
        // test null string
        builder.withSymbols((String)null);
        verifyRequest(builder.create(),
                      null,
                      null,
                      defaultContent,
                      OPTION,
                      new HashMap<String,String>(),
                      null,
                      symbolSet);
        // test empty string
        builder.withSymbols("");
        verifyRequest(builder.create(),
                      null,
                      null,
                      defaultContent,
                      OPTION,
                      new HashMap<String,String>(),
                      null,
                      symbolSet);
        // test null collection
        builder.withSymbols((Collection<String>)null);
        verifyRequest(builder.create(),
                      null,
                      null,
                      defaultContent,
                      OPTION,
                      new HashMap<String,String>(),
                      null,
                      symbolSet);
        // test empty collection
        builder.withSymbols(new ArrayList<String>());
        verifyRequest(builder.create(),
                      null,
                      null,
                      defaultContent,
                      OPTION,
                      new HashMap<String,String>(),
                      null,
                      symbolSet);
        // test non-empty versions
        builder.withUnderlyingSymbols("");
        // array version
        verifyRequest(builder.withSymbols(symbolSet.toArray(new String[0])).create(),
                      null,
                      null,
                      defaultContent,
                      OPTION,
                      new HashMap<String,String>(),
                      symbolSet,
                      null);
        builder.withSymbols("");
        // string version
        StringBuilder symbolString = new StringBuilder();
        for(String symbol : symbolSet) {
            symbolString.append(symbol).append(SYMBOL_DELIMITER);
        }
        verifyRequest(builder.withSymbols(symbolString.toString()).create(),
                      null,
                      null,
                      defaultContent,
                      OPTION,
                      new HashMap<String,String>(),
                      symbolSet,
                      null);
        // collection version
        builder.withSymbols("");
        verifyRequest(builder.withSymbols(symbolSet).create(),
                      null,
                      null,
                      defaultContent,
                      OPTION,
                      new HashMap<String,String>(),
                      symbolSet,
                      null);
    }
    /**
     * Tests {@link MarketDataRequestBuilder#withUnderlyingSymbols(String)}, {@link MarketDataRequestBuilder#withUnderlyingSymbols(String...)}, and
     * {@link MarketDataRequestBuilder#withUnderlyingSymbols(Collection)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void withUnderlyingSymbols()
            throws Exception
    {
        for(AssetClass assetClass : AssetClass.values()) {
            if(assetClass.isValidForUnderlyingSymbols()) {
                doWithUnderlyingSymbolTest(assetClass);
            }
        }
    }
    /**
     * Tests {@link MarketDataRequestBuilder#withProvider(String)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void withProvider()
            throws Exception
    {
        final MarketDataRequestBuilder builder = MarketDataRequestBuilder.newRequest();
        Set<String> symbols = new LinkedHashSet<String>(Arrays.asList(new String[] { "METC", "GOOG" } ));
        // set defaults
        builder.withContent(defaultContent).withAssetClass(EQUITY).withSymbols(symbols);
        // null provider
        verifyRequest(builder.withProvider(null).create(),
                      null,
                      null,
                      defaultContent,
                      EQUITY,
                      new HashMap<String,String>(),
                      symbols,
                      null);
        // empty provider
        verifyRequest(builder.withProvider("").create(),
                      "",
                      null,
                      defaultContent,
                      EQUITY,
                      new HashMap<String,String>(),
                      symbols,
                      null);
        // non-null provider
        verifyRequest(builder.withProvider("provider").create(),
                      "provider",
                      null,
                      defaultContent,
                      EQUITY,
                      new HashMap<String,String>(),
                      symbols,
                      null);
    }
    /**
     * Tests {@link MarketDataRequestBuilder#withExchange(String)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void withExchange()
            throws Exception
    {
        final MarketDataRequestBuilder builder = MarketDataRequestBuilder.newRequest();
        Set<String> symbols = new LinkedHashSet<String>(Arrays.asList(new String[] { "METC", "GOOG" } ));
        // set defaults
        builder.withContent(defaultContent).withAssetClass(EQUITY).withSymbols(symbols);
        // null exchange
        verifyRequest(builder.withExchange(null).create(),
                      null,
                      null,
                      defaultContent,
                      EQUITY,
                      new HashMap<String,String>(),
                      symbols,
                      null);
        // empty exchange
        verifyRequest(builder.withExchange("").create(),
                      null,
                      "",
                      defaultContent,
                      EQUITY,
                      new HashMap<String,String>(),
                      symbols,
                      null);
        // non-null exchange
        verifyRequest(builder.withExchange("exchange").create(),
                      null,
                      "exchange",
                      defaultContent,
                      EQUITY,
                      new HashMap<String,String>(),
                      symbols,
                      null);
    }
    /**
     * Tests {@link MarketDataRequestBuilder#withAssetClass(AssetClass)} and
     * {@link MarketDataRequestBuilder#withAssetClass(String)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void withAssetClass()
            throws Exception
    {
        final MarketDataRequestBuilder builder = MarketDataRequestBuilder.newRequest();
        Set<String> symbols = new LinkedHashSet<String>(Arrays.asList(new String[] { "METC", "GOOG", "USD/INR" } ));
        // set defaults
        builder.withContent(defaultContent).withSymbols(symbols);
        // null asset class (enum) (sets to default)
        verifyRequest(builder.withAssetClass((AssetClass)null).create(),
                      null,
                      null,
                      defaultContent,
                      EQUITY,
                      new HashMap<String,String>(),
                      symbols,
                      null);
        builder.withAssetClass(OPTION);
        // null asset class (String) (sets to default)
        verifyRequest(builder.withAssetClass((String)null).create(),
                      null,
                      null,
                      defaultContent,
                      EQUITY,
                      new HashMap<String,String>(),
                      symbols,
                      null);
        builder.withAssetClass(OPTION);
        // empty asset class (sets to default)
        verifyRequest(builder.withAssetClass((String)"").create(),
                      null,
                      null,
                      defaultContent,
                      EQUITY,
                      new HashMap<String,String>(),
                      symbols,
                      null);
        builder.withAssetClass(OPTION);
        // non-null exchange (enum)
        verifyRequest(builder.withAssetClass(EQUITY).create(),
                      null,
                      null,
                      defaultContent,
                      EQUITY,
                      new HashMap<String,String>(),
                      symbols,
                      null);
        verifyRequest(builder.withAssetClass(OPTION).create(),
                      null,
                      null,
                      defaultContent,
                      OPTION,
                      new HashMap<String,String>(),
                      symbols,
                      null);
        verifyRequest(builder.withAssetClass(FUTURE).create(),
                      null,
                      null,
                      defaultContent,
                      FUTURE,
                      new HashMap<String,String>(),
                      symbols,
                      null);
        verifyRequest(builder.withAssetClass(CURRENCY).create(),
        			  null,
        			  null,
        			  defaultContent,
        			  CURRENCY,
        			  new HashMap<String,String>(),
        			  symbols,
        			  null);
        // non-null exchange (invalid String)
        new ExpectedFailure<IllegalArgumentException>(new I18NBoundMessage1P(INVALID_ASSET_CLASS,
                                                                             String.valueOf("not-an-asset-class")).getText()) {
            @Override
            protected void run()
                    throws Exception
            {
                MarketDataRequestBuilder.newRequestFromString("symbols=METC:assetclass=not-an-asset-class");
            }
        };
        // non-null asset class (lower-case String)
        verifyRequest(builder.withAssetClass(OPTION.toString().toLowerCase()).create(),
                      null,
                      null,
                      defaultContent,
                      OPTION,
                      new HashMap<String,String>(),
                      symbols,
                      null);
        verifyRequest(builder.withAssetClass(FUTURE.toString().toLowerCase()).create(),
                      null,
                      null,
                      defaultContent,
                      FUTURE,
                      new HashMap<String,String>(),
                      symbols,
                      null);
        verifyRequest(builder.withAssetClass(CURRENCY.toString().toLowerCase()).create(),
        			  null,
        			  null,
        			  defaultContent,
        			  CURRENCY,
        			  new HashMap<String,String>(),
                	  symbols,
                	  null);
        // non-null asset class (upper-case String)
        verifyRequest(builder.withAssetClass(EQUITY.toString().toUpperCase()).create(),
                      null,
                      null,
                      defaultContent,
                      EQUITY,
                      new HashMap<String,String>(),
                      symbols,
                      null);
        // non-null asset class (mixed-case String)
        verifyRequest(builder.withAssetClass("OpTiOn").create(),
                      null,
                      null,
                      defaultContent,
                      OPTION,
                      new HashMap<String,String>(),
                      symbols,
                      null);
        verifyRequest(builder.withAssetClass("FuTuRe").create(),
                      null,
                      null,
                      defaultContent,
                      FUTURE,
                      new HashMap<String,String>(),
                      symbols,
                      null);
        verifyRequest(builder.withAssetClass("CuRREncy").create(),
        			 null,
        			 null,
        			 defaultContent,
        			 CURRENCY,
        			 new HashMap<String,String>(),
        			 symbols,
        			 null);
    }
    /**
     * Tests {@link MarketDataRequestBuilder#withContent(String)}, {@link MarketDataRequestBuilder#withContent(String...)}, 
     * {@link MarketDataRequestBuilder#withContent(Content...), and {@link MarketDataRequestBuilder#withContent(Collection)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void withContent()
            throws Exception
    {
        final MarketDataRequestBuilder builder = MarketDataRequestBuilder.newRequest();
        // set defaults
        Set<String> symbolSet = new LinkedHashSet<String>(Arrays.asList(new String[] { "METC", "GOOG" } ));
        builder.withSymbols(symbolSet).withAssetClass(OPTION);
        // test null String (uses default)
        builder.withContent((String)null);
        verifyRequest(builder.create(),
                      null,
                      null,
                      defaultContent,
                      OPTION,
                      new HashMap<String,String>(),
                      symbolSet,
                      null);
        // test empty String (uses default)
        builder.withContent("");
        verifyRequest(builder.create(),
                      null,
                      null,
                      defaultContent,
                      OPTION,
                      new HashMap<String,String>(),
                      symbolSet,
                      null);
        // test null Content array
        builder.withContent((Content[])null);
        verifyRequest(builder.create(),
                      null,
                      null,
                      defaultContent,
                      OPTION,
                      new HashMap<String,String>(),
                      symbolSet,
                      null);
        // test empty Content array
        builder.withContent(new Content[0]);
        verifyRequest(builder.create(),
                      null,
                      null,
                      defaultContent,
                      OPTION,
                      new HashMap<String,String>(),
                      symbolSet,
                      null);
        // test null content collection
        builder.withContent((Collection<Content>)null);
        verifyRequest(builder.create(),
                      null,
                      null,
                      defaultContent,
                      OPTION,
                      new HashMap<String,String>(),
                      symbolSet,
                      null);
        // test empty content collection
        builder.withContent(new ArrayList<Content>());
        verifyRequest(builder.create(),
                      null,
                      null,
                      defaultContent,
                      OPTION,
                      new HashMap<String,String>(),
                      symbolSet,
                      null);
        // test null String array
        builder.withContent((String[])null);
        verifyRequest(builder.create(),
                      null,
                      null,
                      defaultContent,
                      OPTION,
                      new HashMap<String,String>(),
                      symbolSet,
                      null);
        // test empty String array
        builder.withContent(new String[0]);
        verifyRequest(builder.create(),
                      null,
                      null,
                      defaultContent,
                      OPTION,
                      new HashMap<String,String>(),
                      symbolSet,
                      null);
        // non-null, non-empty String
        String contentString = MARKET_STAT + "," + LEVEL_2;
        builder.withContent(contentString);
        verifyRequest(builder.create(),
                      null,
                      null,
                      EnumSet.of(MARKET_STAT,
                                 LEVEL_2),
                      OPTION,
                      new HashMap<String,String>(),
                      symbolSet,
                      null);
        // non-null, non-empty String with duplicate
        contentString += "," + MARKET_STAT + "," + LEVEL_2;
        builder.withContent(contentString);
        verifyRequest(builder.create(),
                      null,
                      null,
                      EnumSet.of(MARKET_STAT,
                                 LEVEL_2),
                      OPTION,
                      new HashMap<String,String>(),
                      symbolSet,
                      null);
        // invalid content (with a valid one)
        new ExpectedFailure<IllegalArgumentException>(new I18NBoundMessage1P(INVALID_CONTENT,
                                                                             String.valueOf("not-a-content")).getText()) {
            @Override
            protected void run()
                    throws Exception
            {
                builder.withContent("MARKET_STAT,not-a-content");
            }
        };
        // test non-null Content array
        builder.withContent(new Content[] { LATEST_TICK, MARKET_STAT } );
        verifyRequest(builder.create(),
                      null,
                      null,
                      EnumSet.of(LATEST_TICK,
                                 MARKET_STAT),
                      OPTION,
                      new HashMap<String,String>(),
                      symbolSet,
                      null);
        // test non-null Content array (with duplicates)
        builder.withContent(new Content[] { LATEST_TICK, MARKET_STAT, MARKET_STAT, LATEST_TICK } );
        verifyRequest(builder.create(),
                      null,
                      null,
                      EnumSet.of(LATEST_TICK,
                                 MARKET_STAT),
                      OPTION,
                      new HashMap<String,String>(),
                      symbolSet,
                      null);
        // test non-null Content array (with null)
        Content[] contents = new Content[] { LATEST_TICK, MARKET_STAT, null }; 
        builder.withContent(contents);
        new ExpectedFailure<IllegalArgumentException>(new I18NBoundMessage1P(INVALID_CONTENT,
                                                                             String.valueOf(Arrays.toString(contents))).getText()) {
            @Override
            protected void run()
                    throws Exception
            {
                builder.create();
            }
        };
        // test non-null content collection
        builder.withContent(EnumSet.of(LATEST_TICK,
                                       MARKET_STAT));
        verifyRequest(builder.create(),
                      null,
                      null,
                      EnumSet.of(LATEST_TICK,
                                 MARKET_STAT),
                      OPTION,
                      new HashMap<String,String>(),
                      symbolSet,
                      null);
        // test non-null content collection (with duplicates)
        builder.withContent(EnumSet.of(LATEST_TICK,
                                       MARKET_STAT,
                                       LATEST_TICK,
                                       BBO10));
        verifyRequest(builder.create(),
                      null,
                      null,
                      EnumSet.of(LATEST_TICK,
                                 MARKET_STAT,
                                 BBO10),
                      OPTION,
                      new HashMap<String,String>(),
                      symbolSet,
                      null);
        // test non-null content collection (with null)
        Set<Content> contentCollection = new LinkedHashSet<Content>(Arrays.asList(new Content[] { LATEST_TICK,null } ));
        builder.withContent(contentCollection);
        new ExpectedFailure<IllegalArgumentException>(new I18NBoundMessage1P(INVALID_CONTENT,
                                                                             String.valueOf(contentCollection)).getText()) {
            @Override
            protected void run()
                    throws Exception
            {
                builder.create();
            }
        };
        // test non-null String array
        builder.withContent(new String[] { LATEST_TICK.toString(), MARKET_STAT.toString() } );
        verifyRequest(builder.create(),
                      null,
                      null,
                      EnumSet.of(LATEST_TICK,
                                 MARKET_STAT),
                      OPTION,
                      new HashMap<String,String>(),
                      symbolSet,
                      null);
        // test non-null String array (with duplicates)
        builder.withContent(new String[] { LATEST_TICK.toString(), MARKET_STAT.toString(), MARKET_STAT.toString() } );
        verifyRequest(builder.create(),
                      null,
                      null,
                      EnumSet.of(LATEST_TICK,
                                 MARKET_STAT),
                      OPTION,
                      new HashMap<String,String>(),
                      symbolSet,
                      null);
        // test non-null String array (with invalid)
        new ExpectedFailure<IllegalArgumentException>(new I18NBoundMessage1P(INVALID_CONTENT,
                                                                             String.valueOf("not-a-content")).getText()) {
            @Override
            protected void run()
                    throws Exception
            {
                builder.withContent(new String[] { LATEST_TICK.toString(), MARKET_STAT.toString(), "not-a-content" } );
            }
        };
        // test non-null String array (with empty)
        new ExpectedFailure<IllegalArgumentException>(new I18NBoundMessage1P(INVALID_CONTENT,
                                                                             String.valueOf("")).getText()) {
            @Override
            protected void run()
                    throws Exception
            {
                builder.withContent(new String[] { LATEST_TICK.toString(), MARKET_STAT.toString(), "" } );
            }
        };
        // test non-null String array (with null)
        new ExpectedFailure<IllegalArgumentException>(new I18NBoundMessage1P(INVALID_CONTENT,
                                                                             null).getText()) {
            @Override
            protected void run()
                    throws Exception
            {
                builder.withContent(new String[] { LATEST_TICK.toString(), MARKET_STAT.toString(), null } );
            }
        };
    }
    /**
     * Tests {@link MarketDataRequestBuilder#withParameter(String, String)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void withParameter()
            throws Exception
    {
        final MarketDataRequestBuilder builder = MarketDataRequestBuilder.newRequest();
        Map<String,String> expectedResults = new HashMap<String,String>();
        // set defaults
        Set<String> symbolSet = new LinkedHashSet<String>(Arrays.asList(new String[] { "METC", "GOOG" } ));
        builder.withSymbols(symbolSet).withAssetClass(EQUITY).withContent(defaultContent);
        // null key
        new ExpectedFailure<NullPointerException>() {
            @Override
            protected void run()
                    throws Exception
            {
                builder.withParameter(null,
                                      "value");
            }
        };
        // null value
        new ExpectedFailure<NullPointerException>() {
            @Override
            protected void run()
                    throws Exception
            {
                builder.withParameter("key",
                                      null);
            }
        };
        // non-null, single param
        builder.withParameter("key",
                              "value");
        expectedResults.put("key",
                            "value");
        verifyRequest(builder.create(),
                      null,
                      null,
                      defaultContent,
                      EQUITY,
                      expectedResults,
                      symbolSet,
                      null);
        // add a second param
        builder.withParameter("key2",
                              "value2");
        expectedResults.put("key2",
                            "value2");
        verifyRequest(builder.create(),
                      null,
                      null,
                      defaultContent,
                      EQUITY,
                      expectedResults,
                      symbolSet,
                      null);
        // add a third param with whitespace
        builder.withParameter("  key3  ",
                              "  value3  ");
        expectedResults.put("key3",
                            "value3");
        verifyRequest(builder.create(),
                      null,
                      null,
                      defaultContent,
                      EQUITY,
                      expectedResults,
                      symbolSet,
                      null);
    }
    /**
     * Tests validation of {@link MarketDataRequest} objects beyond simple validation
     * of syntax.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void businessLogicValidation()
            throws Exception
    {
        final MarketDataRequestBuilder builder = MarketDataRequestBuilder.newRequest();
        // set defaults
        Set<String> symbolSet = new LinkedHashSet<String>(Arrays.asList(new String[] { "METC", "GOOG" } ));
        builder.withContent(defaultContent);
        // asset class
        // can't test missing asset class because it has a default value
        // underlying symbols and asset class equity
        builder.withUnderlyingSymbols(symbolSet).withAssetClass(EQUITY);
        new ExpectedFailure<IllegalArgumentException>(new I18NBoundMessage2P(VALID_UNDERLYING_ASSET_CLASS_REQUIRED,
                                                                             builder.toString(),
                                                                             EQUITY).getText()) {
            @Override
            protected void run()
                    throws Exception
            {
                builder.create();
            }
        };
        // content
        // can't test empty content because it has a default value
        // invalid content (this is tested elsewhere, but doesn't hurt to be tested here, too)
        builder.withAssetClass(EQUITY).withSymbols(symbolSet).withUnderlyingSymbols("");
        builder.withContent(new Content[] { null });
        new ExpectedFailure<IllegalArgumentException>(new I18NBoundMessage1P(INVALID_CONTENT,
                                                                             String.valueOf(Arrays.asList(new Content[] { null } ))).getText()) {
            @Override
            protected void run()
                    throws Exception
            {
                builder.create();
            }
        };
        // dividend requires symbols
        builder.withSymbols("").withUnderlyingSymbols(symbolSet).withContent(DIVIDEND).withAssetClass(OPTION);
        new ExpectedFailure<IllegalArgumentException>(new I18NBoundMessage1P(DIVIDEND_REQUIRES_SYMBOLS,
                                                                             builder.toString()).getText()) {
            @Override
            protected void run()
                    throws Exception
            {
                builder.create();
            }
        };
        // common symbol validation
        // must specify either symbols or underlying symbols
        builder.withSymbols("").withUnderlyingSymbols("").withContent(defaultContent);
        new ExpectedFailure<IllegalArgumentException>(new I18NBoundMessage1P(NEITHER_SYMBOLS_NOR_UNDERLYING_SYMBOLS_SPECIFIED,
                                                                             builder.toString()).getText()) {
            @Override
            protected void run()
                    throws Exception
            {
                builder.create();
            }
        };
        // can't specify both symbols and underlying symbols
        builder.withSymbols(symbolSet).withUnderlyingSymbols(symbolSet);
        new ExpectedFailure<IllegalArgumentException>(new I18NBoundMessage1P(BOTH_SYMBOLS_AND_UNDERLYING_SYMBOLS_SPECIFIED,
                                                                             builder.toString()).getText()) {
            @Override
            protected void run()
                    throws Exception
            {
                builder.create();
            }
        };
        // symbols validation
        Set<String> badSymbols1 = new LinkedHashSet<String>(Arrays.asList(new String[] { "GOOG", null } ));
        Set<String> badSymbols2 = new LinkedHashSet<String>(Arrays.asList(new String[] { "GOOG", "" } ));
        Set<String> badSymbols3 = new LinkedHashSet<String>(Arrays.asList(new String[] { "GOOG", "    " } ));
        // null symbol
        builder.withSymbols(badSymbols1).withUnderlyingSymbols("");
        new ExpectedFailure<IllegalArgumentException>(new I18NBoundMessage1P(INVALID_SYMBOLS,
                                                                             String.valueOf(badSymbols1)).getText()) {
            @Override
            protected void run()
                    throws Exception
            {
                builder.create();
            }
        };
        // empty symbol
        builder.withSymbols(badSymbols2);
        new ExpectedFailure<IllegalArgumentException>(new I18NBoundMessage1P(INVALID_SYMBOLS,
                                                                             String.valueOf(badSymbols2)).getText()) {
            @Override
            protected void run()
                    throws Exception
            {
                builder.create();
            }
        };
        // whitespace symbol
        builder.withSymbols(badSymbols3);
        new ExpectedFailure<IllegalArgumentException>(new I18NBoundMessage1P(INVALID_SYMBOLS,
                                                                             String.valueOf(badSymbols3)).getText()) {
            @Override
            protected void run()
                    throws Exception
            {
                builder.create();
            }
        };
        // underlying symbols
        // null symbol
        builder.withSymbols("").withUnderlyingSymbols(badSymbols1);
        new ExpectedFailure<IllegalArgumentException>(new I18NBoundMessage1P(INVALID_UNDERLYING_SYMBOLS,
                                                                             String.valueOf(badSymbols1)).getText()) {
            @Override
            protected void run()
                    throws Exception
            {
                builder.create();
            }
        };
        // empty symbol
        builder.withUnderlyingSymbols(badSymbols2);
        new ExpectedFailure<IllegalArgumentException>(new I18NBoundMessage1P(INVALID_UNDERLYING_SYMBOLS,
                                                                             String.valueOf(badSymbols2)).getText()) {
            @Override
            protected void run()
                    throws Exception
            {
                builder.create();
            }
        };
        // whitespace symbol
        builder.withUnderlyingSymbols(badSymbols3);
        new ExpectedFailure<IllegalArgumentException>(new I18NBoundMessage1P(INVALID_UNDERLYING_SYMBOLS,
                                                                             String.valueOf(badSymbols3)).getText()) {
            @Override
            protected void run()
                    throws Exception
            {
                builder.create();
            }
        };
    }
    /**
     * Executes a single iteration of the underlying symbol test with the given asset class.
     *
     * @param inAssetClass an <code>AssetClass</code> value
     * @throws Exception if an unexpected error occurs
     */
    private void doUnderlyingSymbolTest(AssetClass inAssetClass)
            throws Exception
    {
        final String assetClassAsString = inAssetClass.name();
        // empty underlying symbol list
        new ExpectedFailure<IllegalArgumentException>(new I18NBoundMessage1P(INVALID_UNDERLYING_SYMBOLS,
                                                                             String.valueOf(Arrays.asList(new String[] { "" } ))).getText()) {
            @Override
            protected void run()
            throws Exception
            {
                MarketDataRequestBuilder.newRequestFromString("underlyingsymbols=:assetClass=" + assetClassAsString);
            }
        };
        // single underlying symbol list
        verifyRequest(MarketDataRequestBuilder.newRequestFromString("underlyingsymbols=GOOG:assetClass=" + assetClassAsString),
                      null,
                      null,
                      defaultContent,
                      inAssetClass,
                      new HashMap<String,String>(),
                      new HashSet<String>(),
                      new LinkedHashSet<String>(Arrays.asList(new String[] { "GOOG" })));
        // multiple underlying symbol list
        verifyRequest(MarketDataRequestBuilder.newRequestFromString("underlyingsymbols=GOOG,ORCL:assetClass=" + assetClassAsString),
                      null,
                      null,
                      defaultContent,
                      inAssetClass,
                      new HashMap<String,String>(),
                      new HashSet<String>(),
                      new LinkedHashSet<String>(Arrays.asList(new String[] { "GOOG", "ORCL" })));
        // duplicate underlying symbol list
        verifyRequest(MarketDataRequestBuilder.newRequestFromString("underlyingsymbols=GOOG,ORCL,GOOG:assetClass=" + assetClassAsString),
                      null,
                      null,
                      defaultContent,
                      inAssetClass,
                      new HashMap<String,String>(),
                      new HashSet<String>(),
                      new LinkedHashSet<String>(Arrays.asList(new String[] { "GOOG", "ORCL" })));
        // UPPER case key
        verifyRequest(MarketDataRequestBuilder.newRequestFromString("UNDERLYINGSYMBOLS=GOOG:assetClass=" + assetClassAsString),
                      null,
                      null,
                      defaultContent,
                      inAssetClass,
                      new HashMap<String,String>(),
                      new HashSet<String>(),
                      new LinkedHashSet<String>(Arrays.asList(new String[] { "GOOG" })));
        // MiXeD case key
        verifyRequest(MarketDataRequestBuilder.newRequestFromString("UnDeRlYiNgSyMbOlS=GOOG:assetClass=" + assetClassAsString),
                      null,
                      null,
                      defaultContent,
                      inAssetClass,
                      new HashMap<String,String>(),
                      new HashSet<String>(),
                      new LinkedHashSet<String>(Arrays.asList(new String[] { "GOOG" })));
        // extra whitespace, both key and value
        verifyRequest(MarketDataRequestBuilder.newRequestFromString("  underlyingsymbols  =  GOOG  :assetClass=" + assetClassAsString),
                      null,
                      null,
                      defaultContent,
                      inAssetClass,
                      new HashMap<String,String>(),
                      new HashSet<String>(),
                      new LinkedHashSet<String>(Arrays.asList(new String[] { "GOOG" })));
    }
    /**
     * Executes one permutation of <code>withUnderlyingSymbolTest</code> with the given asset class.
     *
     * @param inAssetClass an <code>AssetClass</code> value
     * @throws Exception if an unexpected error occurs
     */
    private void doWithUnderlyingSymbolTest(AssetClass inAssetClass)
            throws Exception
    {
        final MarketDataRequestBuilder builder = MarketDataRequestBuilder.newRequest();
        // set defaults to make our "toString" look like the "toString" with defaults
        builder.withContent(defaultContent);
        // the first conditions are going to test the ability to set the symbol list to empty in various ways
        // in order to test this without triggering an error condition, since the symbol list will be empty,
        //  set symbols and asset class
        Set<String> symbolSet = new LinkedHashSet<String>(Arrays.asList(new String[] { "METC", "GOOG" } ));
        builder.withSymbols(symbolSet).withAssetClass(inAssetClass);
        // test null array
        builder.withUnderlyingSymbols((String[])null);
        verifyRequest(builder.create(),
                      null,
                      null,
                      defaultContent,
                      inAssetClass,
                      new HashMap<String,String>(),
                      symbolSet,
                      null);
        // test empty array
        builder.withUnderlyingSymbols(new String[0]);
        verifyRequest(builder.create(),
                      null,
                      null,
                      defaultContent,
                      inAssetClass,
                      new HashMap<String,String>(),
                      symbolSet,
                      null);
        // test null string
        builder.withUnderlyingSymbols((String)null);
        verifyRequest(builder.create(),
                      null,
                      null,
                      defaultContent,
                      inAssetClass,
                      new HashMap<String,String>(),
                      symbolSet,
                      null);
        // test empty string
        builder.withUnderlyingSymbols("");
        verifyRequest(builder.create(),
                      null,
                      null,
                      defaultContent,
                      inAssetClass,
                      new HashMap<String,String>(),
                      symbolSet,
                      null);
        // test null collection
        builder.withUnderlyingSymbols((Collection<String>)null);
        verifyRequest(builder.create(),
                      null,
                      null,
                      defaultContent,
                      inAssetClass,
                      new HashMap<String,String>(),
                      symbolSet,
                      null);
        // test empty collection
        builder.withUnderlyingSymbols(new ArrayList<String>());
        verifyRequest(builder.create(),
                      null,
                      null,
                      defaultContent,
                      inAssetClass,
                      new HashMap<String,String>(),
                      symbolSet,
                      null);
        // test non-empty versions
        builder.withSymbols("");
        // array version
        verifyRequest(builder.withUnderlyingSymbols(symbolSet.toArray(new String[0])).create(),
                      null,
                      null,
                      defaultContent,
                      inAssetClass,
                      new HashMap<String,String>(),
                      null,
                      symbolSet);
        builder.withUnderlyingSymbols("");
        // string version
        StringBuilder symbolString = new StringBuilder();
        for(String symbol : symbolSet) {
            symbolString.append(symbol).append(SYMBOL_DELIMITER);
        }
        verifyRequest(builder.withUnderlyingSymbols(symbolString.toString()).create(),
                      null,
                      null,
                      defaultContent,
                      inAssetClass,
                      new HashMap<String,String>(),
                      null,
                      symbolSet);
        // collection version
        builder.withUnderlyingSymbols("");
        verifyRequest(builder.withUnderlyingSymbols(symbolSet).create(),
                      null,
                      null,
                      defaultContent,
                      inAssetClass,
                      new HashMap<String,String>(),
                      null,
                      symbolSet);
    }
    /**
     * Verifies that the given <code>MarketDataRequest</code> matches the expected attributes.
     *
     * @param inActualRequest a <code>MarketDataRequest</code> value containing the actual request
     * @param inProvider a <code>String</code> value containing the expected <code>Provider</code>
     * @param inExchange a <code>String</code> value containing the expected <code>Exchange</code>
     * @param inExpectedContent a <code>Set&lt;Content&gt;</code> value containing the expected <code>Content</code>
     * @param inAssetClass an <code>AssetClass</code> value containing the expected <code>AssetClass</code>
     * @param inExpectedParameters a <code>Map&lt;String,String&gt;</code> value containing the expected parameters
     * @param inExpectedSymbols a <code>Set&lt;String&gt;</code> value containing the expected <code>Symbol</code> values
     * @param inExpectedUnderlyingSymbols a <code>Set&lt;String&gt;</code> value containing the expected <code>UnderlyingSymbol</code> values
     * @throws Exception if an error occurs
     */
    private static void verifyRequest(final MarketDataRequest inActualRequest,
                                      String inProvider,
                                      String inExchange,
                                      Set<Content> inExpectedContent,
                                      AssetClass inAssetClass,
                                      Map<String, String> inExpectedParameters,
                                      Set<String> inExpectedSymbols,
                                      Set<String> inExpectedUnderlyingSymbols)
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
        assertEquals(inExchange,
                     exchange);
        if(exchange != null) {
            exchange += "-" + System.nanoTime();
            assertFalse(exchange.equals(inExchange));
            assertEquals(inExchange,
                         inActualRequest.getExchange());
        }
        // test content
        final Set<Content> content = inActualRequest.getContent();
        assertEquals(inExpectedContent,
                     content);
        if(content != null &&
           !content.isEmpty()) {
            new ExpectedFailure<UnsupportedOperationException>() {
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
        if(inExpectedSymbols != null) {
            Set<String> actualSymbols = inActualRequest.getSymbols();
            assertEquals(String.format("Expected: %s Actual: %s",
                                            inExpectedSymbols,
                                            actualSymbols),
                         inExpectedSymbols,
                         actualSymbols);
            if(actualSymbols != null) {
                actualSymbols = new LinkedHashSet<String>(Arrays.asList(new String[] { "some", "symbols", "here" }));
                assertFalse(inExpectedSymbols.equals(actualSymbols));
                assertEquals(inExpectedSymbols,
                             inActualRequest.getSymbols());
            }
        } else {
            assertTrue(inActualRequest.getSymbols().isEmpty());
        }
        if(inExpectedUnderlyingSymbols != null) {
            // test underlying symbols
            Set<String> actualUnderlyingSymbols = inActualRequest.getUnderlyingSymbols();
            assertEquals(String.format("Expected: %s Actual: %s",
                                       inExpectedUnderlyingSymbols,
                                       actualUnderlyingSymbols),
                         inExpectedUnderlyingSymbols,
                         actualUnderlyingSymbols);
            if(actualUnderlyingSymbols != null) {
                actualUnderlyingSymbols = new LinkedHashSet<String>(Arrays.asList(new String[] { "some", "symbols", "here"} ));
                assertFalse(inExpectedUnderlyingSymbols.equals(actualUnderlyingSymbols));
                assertEquals(inExpectedUnderlyingSymbols,
                             inActualRequest.getUnderlyingSymbols());
            }
        } else {
            assertTrue(inActualRequest.getUnderlyingSymbols().isEmpty());
        }
        // test parameters
        final Map<String,String> actualParameters = inActualRequest.getParameters();
        assertEquals(String.format("Expected: %s Actual: %s",
                                   inExpectedParameters,
                                   actualParameters),
                     inExpectedParameters,
                     actualParameters);
        new ExpectedFailure<UnsupportedOperationException>() {
            @Override
            protected void run()
                throws Exception
            {
                actualParameters.clear();
            }
        };
        // check toString-newRequestFromString round-trip
        String stringValue = inActualRequest.toString();
        assertNotNull(stringValue);
        MarketDataRequest roundTripRequest = MarketDataRequestBuilder.newRequestFromString(stringValue);
        assertEquals(String.format("Expected: %s Actual: %s",
                                   inActualRequest,
                                   roundTripRequest),
                     inActualRequest,
                     roundTripRequest);
        // test content validation
        // compare with null
        new ExpectedFailure<NullPointerException>() {
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
        assertTrue(inActualRequest.validateWithCapabilities(inExpectedContent.toArray(new Content[0])));
        // compare with does not intersect
        Set<Content> compliment = new HashSet<Content>(Arrays.asList(Content.values()));
        compliment.removeAll(inExpectedContent);
        assertFalse(inActualRequest.validateWithCapabilities(compliment.toArray(new Content[compliment.size()])));
        // equals and hashcode
        MarketDataRequestBuilder builder = MarketDataRequestBuilder.newRequest().withProvider(inProvider)
                                                                                .withExchange(inExchange)
                                                                                .withContent(inExpectedContent)
                                                                                .withAssetClass(inAssetClass)
                                                                                .withSymbols(inExpectedSymbols)
                                                                                .withUnderlyingSymbols(inExpectedUnderlyingSymbols);
        for(Map.Entry<String,String> entry : inExpectedParameters.entrySet()) {
            builder.withParameter(entry.getKey(),
                                  entry.getValue());
        }
        assertNotNull(builder.toString());
        EqualityAssert.assertEquality(inActualRequest,
                                      builder.create(),
                                      null,
                                      MarketDataRequestTest.class);
    }
    /**
     * default content if none is specified
     */
    private static final Set<Content> defaultContent = EnumSet.of(TOP_OF_BOOK);
}
