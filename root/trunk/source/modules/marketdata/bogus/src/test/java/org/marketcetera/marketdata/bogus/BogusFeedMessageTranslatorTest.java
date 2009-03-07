package org.marketcetera.marketdata.bogus;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.marketcetera.marketdata.MarketDataRequest.Content.TOP_OF_BOOK;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.marketcetera.marketdata.DataRequestTranslator;
import org.marketcetera.marketdata.MarketDataMessageTranslatorTestBase;
import org.marketcetera.marketdata.MarketDataRequest;
import org.marketcetera.marketdata.MarketDataRequest.Content;
import org.marketcetera.marketdata.MarketDataRequest.Type;

/* $License$ */

/**
 * Tests {@link BogusFeedMessageTranslator}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class BogusFeedMessageTranslatorTest
    extends MarketDataMessageTranslatorTestBase<MarketDataRequest>
{
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.MarketDataMessageTranslatorTestBase#getCapabilities()
     */
    @Override
    protected Set<Content> getCapabilities()
    {
        return new HashSet<Content>(Arrays.asList(TOP_OF_BOOK));
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.MarketDataMessageTranslatorTestBase#getTranslator()
     */
    @Override
    protected DataRequestTranslator<MarketDataRequest> getTranslator()
    {
        return BogusFeedMessageTranslator.getInstance();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.MarketDataMessageTranslatorTestBase#verifyResponse(java.lang.Object, java.lang.String, org.marketcetera.marketdata.MarketDataRequest.Content, org.marketcetera.marketdata.MarketDataRequest.Type, java.lang.String[])
     */
    @Override
    protected void verifyResponse(MarketDataRequest inActualResponse,
                                  String inExpectedExchange,
                                  Content inExpectedContent,
                                  Type inExpectedType,
                                  String[] inExpectedSymbols)
            throws Exception
    {
        assertEquals(inExpectedExchange == null || inExpectedExchange.isEmpty() ? null : inExpectedExchange,
                inActualResponse.getExchange());
        assertEquals(inExpectedContent,
                     inActualResponse.getContent());
        assertEquals(inExpectedType,
                     inActualResponse.getType());
        assertArrayEquals(inExpectedSymbols,
                          inActualResponse.getSymbols());
    }
}
