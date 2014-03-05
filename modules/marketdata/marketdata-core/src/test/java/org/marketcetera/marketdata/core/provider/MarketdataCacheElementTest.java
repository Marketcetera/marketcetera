package org.marketcetera.marketdata.core.provider;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.marketcetera.core.LoggerConfiguration;
import org.marketcetera.event.*;
import org.marketcetera.event.impl.QuoteEventBuilder;
import org.marketcetera.event.impl.TopOfBookEventBuilder;
import org.marketcetera.marketdata.Content;
import org.marketcetera.marketdata.DateUtils;
import org.marketcetera.marketdata.OrderBook;
import org.marketcetera.trade.Equity;
import org.marketcetera.trade.Instrument;

/* $License$ */

/**
 * Tests {@link MarketdataCacheElement}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class MarketdataCacheElementTest
{
    /**
     * Runs once before all tests.
     *
     * @throws Exception if an unexpected error occurs
     */
    public static void once()
            throws Exception
    {
        LoggerConfiguration.logSetup();
    }
    /**
     * Runs before each test.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Before
    public void setup()
            throws Exception
    {
        equityCache = new MarketdataCacheElement(equity);
        equityOrderbook = new OrderBook(equity);
    }
    /**
     * Tests {@link MarketdataCacheElement#update(org.marketcetera.marketdata.Content, org.marketcetera.event.Event...) update top of book}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void updateTopOfBook()
            throws Exception
    {
        // top of book is currently empty
        assertNull(equityCache.getSnapshot(Content.TOP_OF_BOOK));
        AskEvent ask1 = generateAsk(equity);
        BidEvent bid1 = generateBid(equity);
        AskEvent ask2 = generateAsk(equity);
        BidEvent bid2 = generateBid(equity);
        assertFalse(ask1.equals(ask2));
        assertFalse(bid1.equals(bid2));
        // no existing top
        equityOrderbook.processAll(equityCache.update(Content.TOP_OF_BOOK,
                                                      ask1));
        verifyTopOfBook(generateTopOfBook(null,
                                          ask1),
                        equityOrderbook.getTopOfBook());
        // existing top with ask only (new ask better)
        equityOrderbook.processAll(equityCache.update(Content.TOP_OF_BOOK,
                                                      ask2));
        verifyTopOfBook(generateTopOfBook(null,
                                          ask2),
                        equityOrderbook.getTopOfBook());
        // existing top with bid and ask
        equityOrderbook.processAll(equityCache.update(Content.TOP_OF_BOOK,
                                                      bid1));
        verifyTopOfBook(generateTopOfBook(bid1,
                                          ask2),
                        equityOrderbook.getTopOfBook());
        // replace bid and ask at the same time
        equityOrderbook.processAll(equityCache.update(Content.TOP_OF_BOOK,
                                                      bid2,ask1));
        verifyTopOfBook(generateTopOfBook(bid2,
                                          ask1),
                        equityOrderbook.getTopOfBook());
        // remove bid
        BidEvent bid3 = QuoteEventBuilder.delete(bid2);
        equityOrderbook.processAll(equityCache.update(Content.TOP_OF_BOOK,
                                                      bid3));
        verifyTopOfBook(generateTopOfBook(null,
                                          ask1),
                        equityOrderbook.getTopOfBook());
        // remove ask
        AskEvent ask3 = QuoteEventBuilder.delete(ask1);
        equityOrderbook.processAll(equityCache.update(Content.TOP_OF_BOOK,
                                                      ask3));
        verifyTopOfBook(null,
                        equityOrderbook.getTopOfBook());
    }
    /**
     * Generates a <code>TopOfBookEvent</code> with the given bid and ask.
     *
     * @param inBid a <code>BidEvent</code> value or <code>null</code>
     * @param inAsk an <code>AskEvent</code> value or <code>null</code>
     * @return a <code>TopOfBookEvent</code> or <code>null</code> if both the bid and ask are <code>null</code>
     * @throws Exception if an unexpected error occurs
     */
    private TopOfBookEvent generateTopOfBook(BidEvent inBid,
                                             AskEvent inAsk)
            throws Exception
    {
        Instrument instrument = null;
        if(inBid != null){
            instrument = inBid.getInstrument();
        } else if(inAsk != null) {
            instrument = inAsk.getInstrument();
        }
        if(instrument == null) {
            return null;
        }
        TopOfBookEventBuilder builder = TopOfBookEventBuilder.topOfBookEvent()
                .withInstrument(instrument)
                .withAsk(inAsk)
                .withBid(inBid);
        return builder.create();
    }
    /**
     * Verifies that the given expected quote matches the given actual quote.
     *
     * @param inExpectedQuote a <code>QuoteEvent</code> value
     * @param inActualQuote a <code>QuoteEvent</code> value
     * @throws Exception if an unexpected error occurs
     */
    private void verifyQuote(QuoteEvent inExpectedQuote,
                             QuoteEvent inActualQuote)
            throws Exception
    {
        if(inExpectedQuote == null) {
            assertNull(inActualQuote);
            return;
        }
        assertNotNull(inExpectedQuote);
        assertNotNull(inActualQuote);
        assertEquals(inExpectedQuote.getAction(),
                     inActualQuote.getAction());
        assertEquals(inExpectedQuote.getExchange(),
                     inActualQuote.getExchange());
        assertEquals(inExpectedQuote.getExchangeTimestamp(),
                     inActualQuote.getExchangeTimestamp());
        assertEquals(inExpectedQuote.getInstrument(),
                     inActualQuote.getInstrument());
        assertEquals(inExpectedQuote.getMessageId(),
                     inActualQuote.getMessageId());
        assertEquals(inExpectedQuote.getPrice(),
                     inActualQuote.getPrice());
        assertEquals(inExpectedQuote.getProvider(),
                     inActualQuote.getProvider());
        assertEquals(inExpectedQuote.getQuoteDate(),
                     inActualQuote.getQuoteDate());
        assertEquals(inExpectedQuote.getSize(),
                     inActualQuote.getSize());
        assertEquals(inExpectedQuote.getSource(),
                     inActualQuote.getSource());
        assertEquals(inExpectedQuote.getTimestamp(),
                     inActualQuote.getTimestamp());
    }
    /**
     * Verifies that the given expected <code>TopOfBookEvent</code> matches the given actual <code>TopOfBookEvent</code> value. 
     *
     * @param inExpectedTop a <code>TopOfBookEvent</code> value
     * @param inActualTop a <code>TopOfBookEvent</code> value
     * @throws Exception if an unexpected error occurs
     */
    private void verifyTopOfBook(TopOfBookEvent inExpectedTop,
                                 TopOfBookEvent inActualTop)
            throws Exception
    {
        if(inExpectedTop == null) {
            assertNull(inActualTop.getAsk());
            assertNull(inActualTop.getBid());
            return;
        }
        assertNotNull(inActualTop);
        verifyQuote(inExpectedTop.getBid(),
                    inActualTop.getBid());
    }
    /**
     * Generates an <code>AskEvent</code> for the given <code>Instrument</code>.
     *
     * @param inInstrument an <code>Instrument</code> value
     * @return an <code>AskEvent</code> value
     */
    private AskEvent generateAsk(Instrument inInstrument)
    {
        QuoteEventBuilder<AskEvent> builder = QuoteEventBuilder.askEvent(inInstrument)
                .withAction(QuoteAction.ADD)
                .withExchange("Q")
                .withPrice(EventTestBase.generateDecimalValue())
                .withSize(EventTestBase.generateDecimalValue())
                .withQuoteDate(DateUtils.dateToString(new Date()));
        return builder.create();
    }
    /**
     * Generates a <code>BidEvent</code> for the given <code>Instrument</code>.
     *
     * @param inInstrument an <code>Instrument</code> value
     * @return a <code>BidEvent</code> value
     */
    private BidEvent generateBid(Instrument inInstrument)
    {
        QuoteEventBuilder<BidEvent> builder = QuoteEventBuilder.bidEvent(inInstrument)
                .withAction(QuoteAction.ADD)
                .withExchange("Q")
                .withPrice(EventTestBase.generateDecimalValue())
                .withSize(EventTestBase.generateDecimalValue())
                .withQuoteDate(DateUtils.dateToString(new Date()));
        return builder.create();
    }
    /**
     * test cache element value
     */
    private MarketdataCacheElement equityCache;
    /**
     * test orderbook used to verify events
     */
    private OrderBook equityOrderbook;
    /**
     * test equity
     */
    private Equity equity = new Equity("METC");
}
