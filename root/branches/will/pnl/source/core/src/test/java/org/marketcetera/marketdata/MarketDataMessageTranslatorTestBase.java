package org.marketcetera.marketdata;

import static org.junit.Assert.assertNotNull;
import static org.marketcetera.marketdata.MarketDataRequest.Content.OHLC;
import static org.marketcetera.marketdata.Messages.MISSING_SYMBOLS;
import static org.marketcetera.marketdata.Messages.UNSUPPORTED_REQUEST;

import java.util.Date;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.marketcetera.core.CoreException;
import org.marketcetera.marketdata.MarketDataRequest.Content;
import org.marketcetera.marketdata.MarketDataRequest.Type;
import org.marketcetera.module.ExpectedFailure;

/* $License$ */

/**
 * Base class for market data message translator tests.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public abstract class MarketDataMessageTranslatorTestBase<ResponseType>
{
    /**
     * Verifies that the actual response matches the expected values. 
     *
     * @param inActualResponse a <code>ResponseType</code> value
     * @param inExpectedExchange a <code>String</code> value
     * @param inExpectedContent a <code>Content</code> value
     * @param inExpectedType a <code>Type</code> value
     * @param inExpectedSymbols a <code>String[]</code> value
     * @throws Exception if an error occurs
     */
    protected abstract void verifyResponse(ResponseType inActualResponse,
                                           String inExpectedExchange,
                                           Content inExpectedContent,
                                           Type inExpectedType,
                                           String[] inExpectedSymbols)
        throws Exception;
    /**
     * Gets a translator of the proper type.
     *
     * @return a <code>DataRequestTranslator&lt;ResponseType&gt;</code> value
     */
    protected abstract DataRequestTranslator<ResponseType> getTranslator();
    /**
     * Gets the capabilities this translator is expected to support and should be tested.
     *
     * @return a <code>Set&lt;Content&gt;</code> value
     */
    protected abstract Set<Content> getCapabilities();
    /**
     * the translator to use for these tests
     */
    protected DataRequestTranslator<ResponseType> translator;
    /**
     * Sets up data before each test.
     */
    @Before
    public void setup()
    {        
        translator = getTranslator();
    }
    /**
     * Tests that the translator instance is instantiated properly.
     *
     * @throws Exception if an error occurs
     */
    @Test
    public void getInstance()
        throws Exception
    {
        assertNotNull(translator);
    }
    /**
     * Tests the ability of the translator to translate messages.
     *
     * @throws Exception if an error occurs
     */
    @Test
    public void translateMarketDataRequest()
        throws Exception
    {
        new ExpectedFailure<MarketDataRequestException>(MISSING_SYMBOLS) {
            protected void run()
                throws Exception
            {
                doMarketDataTest(null,
                                 "");
            }
        };
        new ExpectedFailure<MarketDataRequestException>(MISSING_SYMBOLS) {
            protected void run()
                throws Exception
            {
                doMarketDataTest(null,
                                 (String[])null);
            }
        };
        String[][] symbols = new String[][] { { "GOOG" }, { "GOOG","YHOO","JAVA" } };
        String[] exchanges = new String[] { null, "", "Q", "METC" };
        for(int symbolCounter=0;symbolCounter<symbols.length;symbolCounter++) {
            for(int exchangeCounter=0;exchangeCounter<exchanges.length;exchangeCounter++) {
                doMarketDataTest(exchanges[exchangeCounter],
                                 symbols[symbolCounter]);
            }
        }
    }
    /**
     * Executes a test iteration for the given exchange and security list.
     * 
     * @param inExchange a <code>String</code> value containing an exchange to use or <code>null</code>
     * @param inSecurityList a <code>String[]</code> value containing symbols
     *
     * @throws Exception if an error occurs
     */
    private void doMarketDataTest(String inExchange,
                                  String... inSecurityList)
        throws Exception
    {
        for(Content content : Content.values()) {
            for(Type type : Type.values()) {
                Date ohlcDate = new Date();
                final MarketDataRequest request = MarketDataRequest.newRequest().withSymbols(inSecurityList).withContent(content).ofType(type);
                if(content.equals(OHLC)) {
                    request.asOf(ohlcDate);
                }
                if(inExchange != null &&
                   !inExchange.isEmpty()) {
                    request.fromExchange(inExchange);
                }
                if(getCapabilities().contains(content)) {
                    verifyResponse(translator.fromDataRequest(request),
                                   inExchange,
                                   content,
                                   type,
                                   inSecurityList);
                } else {
                    new ExpectedFailure<CoreException>(UNSUPPORTED_REQUEST) {
                        protected void run()
                            throws Exception
                        {
                            translator.fromDataRequest(request);
                        }
                    };
                }
            }
        }
    }
}
