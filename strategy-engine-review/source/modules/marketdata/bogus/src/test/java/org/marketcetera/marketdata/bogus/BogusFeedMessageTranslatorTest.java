package org.marketcetera.marketdata.bogus;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

import org.marketcetera.marketdata.DataRequestTranslator;
import org.marketcetera.marketdata.MarketDataMessageTranslatorTestBase;
import org.marketcetera.marketdata.MarketDataRequest;
import org.marketcetera.marketdata.MarketDataRequest.Content;

/* $License$ */

/**
 * Tests {@link BogusFeedMessageTranslator}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 1.5.0
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
        return Collections.unmodifiableSet(EnumSet.allOf(Content.class));
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
                                  Content[] inExpectedContent,
                                  String[] inExpectedSymbols)
            throws Exception
    {
        assertEquals(inExpectedExchange == null || inExpectedExchange.isEmpty() ? null : inExpectedExchange,
                inActualResponse.getExchange());
        assertArrayEquals(inExpectedContent,
                          inActualResponse.getContent().toArray(new Content[inActualResponse.getContent().size()]));
        assertArrayEquals(inExpectedSymbols,
                          inActualResponse.getSymbols());
    }
}
