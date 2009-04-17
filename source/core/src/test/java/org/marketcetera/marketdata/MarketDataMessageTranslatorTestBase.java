package org.marketcetera.marketdata;

import static org.junit.Assert.assertNotNull;
import static org.marketcetera.marketdata.MarketDataRequest.Content.LATEST_TICK;
import static org.marketcetera.marketdata.MarketDataRequest.Content.LEVEL_2;
import static org.marketcetera.marketdata.MarketDataRequest.Content.OPEN_BOOK;
import static org.marketcetera.marketdata.MarketDataRequest.Content.TOP_OF_BOOK;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.marketcetera.core.CoreException;
import org.marketcetera.marketdata.MarketDataRequest.Content;
import org.marketcetera.module.ExpectedFailure;

/* $License$ */

/**
 * Base class for market data message translator tests.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 1.5.0
 */
public abstract class MarketDataMessageTranslatorTestBase<ResponseType>
    implements Messages
{
    /**
     * Verifies that the actual response matches the expected values. 
     *
     * @param inActualResponse a <code>ResponseType</code> value
     * @param inExpectedExchange a <code>String</code> value
     * @param inExpectedContent a <code>Content[]</code> value
     * @param inExpectedSymbols a <code>String[]</code> value
     * @throws Exception if an error occurs
     */
    protected abstract void verifyResponse(ResponseType inActualResponse,
                                           String inExpectedExchange,
                                           Content[] inExpectedContent,
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
        new ExpectedFailure<IllegalArgumentException>(MISSING_SYMBOLS.getText()) {
            protected void run()
                throws Exception
            {
                doMarketDataTest(null,
                                 "");
            }
        };
        new ExpectedFailure<IllegalArgumentException>(MISSING_SYMBOLS.getText()) {
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
     * Indicates if the underlying adapter supports the given contents.
     *
     * <p>Subclasses may override this method.  The default implementation indicates
     * if the given content is part of the capabilities asserted by {@link #getCapabilities()}.
     * Typically, this method would be overridden if the underlying adapter provides a dynamic
     * set of capabilities dependent on logging in to the actual source.
     * 
     * <p>The semantics of this method are that the underlying adapter has to support all of
     * the given contents.
     *
     * @param inContent a <code>Set&lt;Content&gt;</code> value
     * @return a <code>boolean</code> value
     */
    protected boolean supports(Set<Content> inContent)
    {
        return getCapabilities().containsAll(inContent);
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
        List<List<Content>> contents = new ArrayList<List<Content>>();
        // first, generate a list of single contents of all types
        for(Content content : Content.values()) {
            contents.add(new ArrayList<Content>(EnumSet.of(content)));
        }
        // next, generate a few permutations
        contents.add(Arrays.asList(new Content[] { TOP_OF_BOOK,LATEST_TICK } ));
        contents.add(Arrays.asList(new Content[] { TOP_OF_BOOK,LATEST_TICK,OPEN_BOOK } ));
        contents.add(Arrays.asList(new Content[] { TOP_OF_BOOK,LATEST_TICK,OPEN_BOOK,LEVEL_2 } ));
        for(List<Content> content : contents) {
            final MarketDataRequest request = MarketDataRequest.newRequest().withSymbols(inSecurityList).withContent(content.toArray(new Content[content.size()]));
            if(inExchange != null &&
               !inExchange.isEmpty()) {
                request.fromExchange(inExchange);
            }
            if(supports(new HashSet<Content>(content))) {
                verifyResponse(translator.fromDataRequest(request),
                               inExchange,
                               content.toArray(new Content[content.size()]),
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
