package org.marketcetera.marketdata.marketcetera;

import static org.junit.Assert.assertEquals;
import static org.marketcetera.marketdata.MarketDataRequest.Content.LATEST_TICK;
import static org.marketcetera.marketdata.MarketDataRequest.Content.TOP_OF_BOOK;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.marketcetera.marketdata.DataRequestTranslator;
import org.marketcetera.marketdata.MarketDataMessageTranslatorTestBase;
import org.marketcetera.marketdata.MarketDataRequest.Content;
import org.marketcetera.module.ExpectedFailure;
import org.marketcetera.quickfix.FIXVersion;

import quickfix.FieldNotFound;
import quickfix.Group;
import quickfix.Message;
import quickfix.field.MsgType;
import quickfix.field.NoRelatedSym;
import quickfix.field.SecurityExchange;
import quickfix.field.SubscriptionRequestType;
import quickfix.field.Symbol;


/* $License$ */

/**
 * Tests {@link MarketceteraFeedMessageTranslator}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 1.5.0
 */
public class MarketceteraFeedMessageTranslatorTest
        extends MarketDataMessageTranslatorTestBase<Message>
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
    protected DataRequestTranslator<Message> getTranslator()
    {
        return MarketceteraFeedMessageTranslator.getInstance();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.MarketDataMessageTranslatorTestBase#verifyResponse(java.lang.Object, java.lang.String, org.marketcetera.marketdata.MarketDataRequest.Content, org.marketcetera.marketdata.MarketDataRequest.Type, java.lang.String[])
     */
    @Override
    protected void verifyResponse(Message inActualResponse,
                                  String inExpectedExchange,
                                  Content[] inExpectedContent,
                                  String[] inExpectedSymbols)
            throws Exception
    {
        for(int i=0;i<inExpectedSymbols.length;i++) {
            // check symbol
            final Group symbolGroup =  DEFAULT_MESSAGE_FACTORY.getMessageFactory().createGroup(MsgType.MARKET_DATA_REQUEST,
                                                                                               NoRelatedSym.FIELD);
            inActualResponse.getGroup(i+1,
                                      symbolGroup);
            assertEquals(inExpectedSymbols[i],
                         symbolGroup.getString(Symbol.FIELD));
            // check exchange
            if(inExpectedExchange == null ||
               inExpectedExchange.isEmpty()) {
                new ExpectedFailure<FieldNotFound>(null) {
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
                     inActualResponse.getChar(SubscriptionRequestType.FIELD));
    }
}
