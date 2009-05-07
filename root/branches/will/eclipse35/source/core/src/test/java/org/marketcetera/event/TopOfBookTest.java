package org.marketcetera.event;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;
import org.marketcetera.module.ExpectedFailure;
import org.marketcetera.trade.MSymbol;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.test.EqualityAssert;

/* $License$ */

/**
 * Tests {@link TopOfBook}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 1.5.0
 */
public class TopOfBookTest
{
    private final MSymbol metc = new MSymbol("metc");
    private final MSymbol goog = new MSymbol("goog");
    private final String exchange1 = "TEST1";
    private final String exchange2 = "TEST2";
    /**
     * Tests the ability to construct <code>TopOfBook</code> objects.
     *
     * @throws Exception if an error occurs
     */
    @Test
    public void constructor()
        throws Exception
    {
        BidEvent[] bids = new BidEvent[] { null,
                                           EventBaseTest.generateBidEvent(metc,
                                                                          exchange1),
                                           EventBaseTest.generateBidEvent(metc,
                                                                          exchange2),
                                           EventBaseTest.generateBidEvent(goog,
                                                                          exchange1),
                                           EventBaseTest.generateBidEvent(goog,
                                                                          exchange2) };
        AskEvent[] asks = new AskEvent[] { null,
                                           EventBaseTest.generateAskEvent(metc,
                                                                          exchange1),
                                           EventBaseTest.generateAskEvent(metc,
                                                                          exchange2),
                                           EventBaseTest.generateAskEvent(goog,
                                                                          exchange1),
                                           EventBaseTest.generateAskEvent(goog,
                                                                          exchange2) };
        Date[] timestamps = new Date[] { null, new Date(-1), new Date(0), new Date(1), new Date(), new Date(System.currentTimeMillis() + 10000) };
        MSymbol[] symbols = new MSymbol[] { null, metc, goog };
        for(int bidCounter=0;bidCounter<bids.length;bidCounter++) {
            for(int askCounter=0;askCounter<asks.length;askCounter++) {
                for(int timestampCounter=0;timestampCounter<timestamps.length;timestampCounter++) {
                    for(int symbolCounter=0;symbolCounter<symbols.length;symbolCounter++) {
                        final BidEvent bid = bids[bidCounter];
                        final AskEvent ask = asks[askCounter];
                        final Date timestamp = timestamps[timestampCounter];
                        final MSymbol symbol = symbols[symbolCounter];
                        SLF4JLoggerProxy.debug(this,
                                               "{} {} {} {}",
                                               bid,
                                               ask,
                                               timestamp,
                                               symbol);
                        if(timestamp != null &&
                           timestamp.getTime() < 0) {
                            new ExpectedFailure<IllegalArgumentException>(null) {
                                @Override
                                protected void run()
                                    throws Exception
                                {
                                    new TopOfBook(bid,
                                                  ask,
                                                  timestamp,
                                                  symbol);
                                }
                            };
                            continue;
                        }
                        if(timestamp == null ||
                           symbol == null) {
                            new ExpectedFailure<NullPointerException>(null) {
                                @Override
                                protected void run()
                                        throws Exception
                                {
                                    new TopOfBook(bid,
                                                  ask,
                                                  timestamp,
                                                  symbol);
                                }
                            };
                            continue;
                        }
                        if((bid != null &&
                            !bid.getSymbol().equals(symbol)) ||
                           (ask != null &&
                            !ask.getSymbol().equals(symbol))) {
                            new ExpectedFailure<IllegalArgumentException>(null) {
                                @Override
                                protected void run()
                                    throws Exception
                                {
                                    new TopOfBook(bid,
                                                  ask,
                                                  timestamp,
                                                  symbol);
                                }
                            };
                            continue;
                        }
                        TopOfBook top = new TopOfBook(bid,
                                                      ask,
                                                      timestamp,
                                                      symbol);
                        assertEquals(bid,
                                     top.getBid());
                        assertEquals(ask,
                                     top.getAsk());
                        assertEquals(timestamp,
                                     top.getTimestampAsDate());
                        assertEquals(symbol,
                                     top.getSymbol());
                        assertNotNull(top.toString());
                        List<EventBase> expectedEvents = new LinkedList<EventBase>();
                        if(bid != null) {
                            expectedEvents.add(bid);
                        }
                        if(ask != null) {
                            expectedEvents.add(ask);
                        }
                        AggregateEventTest.verifyDecomposedEvents(top,
                                                                  expectedEvents);
                    }
                }
            }
        }
    }
    @Test
    public void hashcodeAndEquals()
        throws Exception
    {
        BidEvent bid1 = EventBaseTest.generateBidEvent(metc,
                                                       exchange1);
        BidEvent bid2 = EventBaseTest.generateBidEvent(metc,
                                                       exchange1);
        assertFalse(bid1.equals(bid2));
        AskEvent ask1 = EventBaseTest.generateAskEvent(metc,
                                                       exchange1);
        AskEvent ask2 = EventBaseTest.generateAskEvent(metc,
                                                       exchange1);
        assertFalse(ask1.equals(ask2));
        TopOfBook top1 = new TopOfBook(bid1,
                                       ask1,
                                       new Date(),
                                       metc);
        TopOfBook top2 = new TopOfBook(bid1,
                                       ask1,
                                       new Date(top1.getTimeMillis()+1),
                                       metc);
        TopOfBook top3 = new TopOfBook(bid2,
                                       ask1,
                                       top1.getTimestampAsDate(),
                                       metc);
        TopOfBook top4 = new TopOfBook(bid1,
                                       ask2,
                                       top1.getTimestampAsDate(),
                                       metc);
        TopOfBook top5 = new TopOfBook(null,
                                       ask1,
                                       top1.getTimestampAsDate(),
                                       metc);
        TopOfBook top6 = new TopOfBook(bid1,
                                       null,
                                       top1.getTimestampAsDate(),
                                       metc);
        EqualityAssert.assertEquality(top1,
                                      top2,
                                      this,
                                      top3,
                                      top4,
                                      top5,
                                      top6);
    }
}
