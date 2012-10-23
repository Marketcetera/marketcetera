package org.marketcetera.marketdata.marketcetera;

import static org.junit.Assert.assertEquals;
import static org.marketcetera.core.marketdata.Content.LATEST_TICK;
import static org.marketcetera.core.marketdata.Content.TOP_OF_BOOK;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.marketcetera.core.ExpectedFailure;
import org.marketcetera.core.marketdata.Content;
import org.marketcetera.core.marketdata.DataRequestTranslator;
import org.marketcetera.core.marketdata.MarketDataMessageTranslatorTestBase;
import org.marketcetera.marketdata.marketcetera.MarketceteraFeed.Request;
import org.marketcetera.core.quickfix.FIXVersion;

import quickfix.FieldNotFound;
import quickfix.Group;
import quickfix.Message;
import quickfix.field.*;


/* $License$ */

/**
 * Tests {@link MarketceteraFeedMessageTranslator}.
 *
 * @version $Id: MarketceteraFeedMessageTranslatorTest.java 16063 2012-01-31 18:21:55Z colin $
 * @since 1.5.0
 */
public class MarketceteraFeedMessageTranslatorTest
        extends MarketDataMessageTranslatorTestBase<MarketceteraFeed.Request>
{
    private static final FIXVersion DEFAULT_MESSAGE_FACTORY = FIXVersion.FIX44;
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.MarketDataMessageTranslatorTestBase#getCapabilities()
     */
    @Override
    protected Set<Content> getCapabilities()
    {
        return new HashSet<Content>(Arrays.asList(TOP_OF_BOOK,LATEST_TICK));
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.MarketDataMessageTranslatorTestBase#getTranslator()
     */
    @Override
    protected DataRequestTranslator<Request> getTranslator()
    {
        return MarketceteraFeedMessageTranslator.getInstance();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.MarketDataMessageTranslatorTestBase#verifyResponse(java.lang.Object, java.lang.String, org.marketcetera.marketdata.MarketDataRequest.Content, org.marketcetera.marketdata.MarketDataRequest.Type, java.lang.String[])
     */
    @Override
    protected void verifyResponse(Request inActualResponse,
                                  String inExpectedExchange,
                                  Content[] inExpectedContent,
                                  String[] inExpectedSymbols)
            throws Exception
    {
        Message theMessage = inActualResponse.getMessage();
        for(int i=0;i<inExpectedSymbols.length;i++) {
            // check symbol
            final Group symbolGroup = DEFAULT_MESSAGE_FACTORY.getMessageFactory().createGroup(MsgType.MARKET_DATA_REQUEST,
                                                                                              NoRelatedSym.FIELD);
            theMessage.getGroup(i+1,
                                      symbolGroup);
            assertEquals(inExpectedSymbols[i],
                         symbolGroup.getString(Symbol.FIELD));
            // check exchange
            if(inExpectedExchange == null ||
               inExpectedExchange.isEmpty()) {
                new ExpectedFailure<FieldNotFound>() {
                    protected void run()
                        throws Exception
                    {
                        symbolGroup.getString(SecurityExchange.FIELD);
                    }
                };
            } else {
                assertEquals(inExpectedExchange,
                             symbolGroup.getString(SecurityExchange.FIELD));
            }
        }
        // check subscription type
        assertEquals(SubscriptionRequestType.SNAPSHOT_PLUS_UPDATES,
                     theMessage.getChar(SubscriptionRequestType.FIELD));
    }
}
