package org.marketcetera.marketdata.bogus;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.marketcetera.marketdata.MarketDataRequest.Content.LATEST_TICK;
import static org.marketcetera.marketdata.MarketDataRequest.Content.LEVEL_2;
import static org.marketcetera.marketdata.MarketDataRequest.Content.OPEN_BOOK;
import static org.marketcetera.marketdata.MarketDataRequest.Content.TOP_OF_BOOK;
import static org.marketcetera.marketdata.MarketDataRequest.Content.TOTAL_VIEW;

import java.util.Collections;
import java.util.EnumSet;
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
        return Collections.unmodifiableSet(EnumSet.of(TOP_OF_BOOK,LEVEL_2,OPEN_BOOK,TOTAL_VIEW,LATEST_TICK));
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
                                  Type inExpectedType,
                                  String[] inExpectedSymbols)
            throws Exception
    {
        assertEquals(inExpectedExchange == null || inExpectedExchange.isEmpty() ? null : inExpectedExchange,
                inActualResponse.getExchange());
        assertArrayEquals(inExpectedContent,
                          inActualResponse.getContent().toArray(new Content[inActualResponse.getContent().size()]));
        assertEquals(inExpectedType,
                     inActualResponse.getType());
        assertArrayEquals(inExpectedSymbols,
                          inActualResponse.getSymbols());
    }
}
