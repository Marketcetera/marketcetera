package org.marketcetera.event.impl;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.marketcetera.event.AskEvent;
import org.marketcetera.event.BidEvent;
import org.marketcetera.event.Event;
import org.marketcetera.event.EventTestBase;
import org.marketcetera.event.QuoteAction;
import org.marketcetera.event.TopOfBookEvent;
import org.marketcetera.module.ExpectedFailure;
import org.marketcetera.trade.Equity;
import org.marketcetera.trade.Instrument;
import org.marketcetera.trade.Option;
import org.marketcetera.trade.OptionType;

/* $License$ */

/**
 * Tests {@link TopOfBookEventBuilder} and {@link TopOfBookEventImpl}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 2.0.0
 */
public class TopOfBookEventTest
        extends AbstractEventBuilderTestBase<TopOfBookEvent,TopOfBookEventBuilder>
{
    /**
     * Run before each test.
     *
     * @throws Exception if an error occurs
     */
    @Before
    public void setup()
            throws Exception
    {
        useEquity = true;
    }
    /**
     * Tests {@link TopOfBookEventBuilder#withAsk(org.marketcetera.event.AskEvent)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void withAsk()
            throws Exception
    {
        useEquity = true;
        TopOfBookEventBuilder builder = setDefaults(getBuilder());
        AskEvent ask = null;
        builder.withAsk(ask);
        assertEquals(ask,
                     builder.getAsk());
        ask = generateAsk();
        builder.withAsk(ask);
        assertEquals(ask,
                     builder.getAsk());
        useEquity = false;
        builder.withAsk(ask);
        assertEquals(ask,
                     builder.getAsk());
        verify(builder);
    }
    /**
     * Tests {@link TopOfBookEventBuilder#withBid(org.marketcetera.event.BidEvent)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void withBid()
            throws Exception
    {
        useEquity = true;
        TopOfBookEventBuilder builder = setDefaults(getBuilder());
        BidEvent bid = null;
        builder.withBid(bid);
        assertEquals(bid,
                     builder.getBid());
        bid = generateBid();
        builder.withBid(bid);
        assertEquals(bid,
                     builder.getBid());
        useEquity = false;
        builder.withBid(bid);
        assertEquals(bid,
                     builder.getBid());
        verify(builder);
    }
    /**
     * Tests {@link TopOfBookEventBuilder#withInstrument(org.marketcetera.trade.Instrument)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void withInstrument()
            throws Exception
    {
        TopOfBookEventBuilder builder = setDefaults(getBuilder());
        Instrument instrument = null;
        builder.withInstrument(instrument);
        assertEquals(instrument,
                     builder.getInstrument());
        instrument = equity;
        builder.withInstrument(instrument);
        assertEquals(instrument,
                     builder.getInstrument());
        useEquity = false;
        builder = setDefaults(getBuilder());
        instrument = option;
        builder.withInstrument(instrument);
        assertEquals(instrument,
                     builder.getInstrument());
        verify(builder);
    }
    /**
     * Tests {@link DepthOfBookEventImpl} validation.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void validation()
            throws Exception
    {
        super.validation();
        // null instrument
        final TopOfBookEventBuilder builder = setDefaults(getBuilder()).withInstrument(null);
        new ExpectedFailure<IllegalArgumentException>(VALIDATION_NULL_INSTRUMENT.getText()) {
            @Override
            protected void run()
                    throws Exception
            {
                builder.create();
            }
        };
        // ask null (ok)
        setDefaults(builder).withAsk(null);
        verify(builder);
        // bid null (ok)
        setDefaults(builder).withBid(null);
        verify(builder);
        // TODO make a positive test
//        // bid instrument does not match top-of-book instrument
//        setDefaults(builder).withInstrument(option);
//        new ExpectedFailure<IllegalArgumentException>(VALIDATION_BID_INCORRECT_INSTRUMENT.getText(equity,
//                                                                                                  option)) {
//            @Override
//            protected void run()
//                    throws Exception
//            {
//                builder.create();
//            }
//        };
//        // ask instrument does not match top-of-book instrument (takes a little more set-up because bid is validated first)
//        AskEvent optionAsk = EventTestBase.generateOptionAskEvent(option,
//                                                                  QuoteAction.ADD);
//        setDefaults(builder).withAsk(optionAsk);
//        new ExpectedFailure<IllegalArgumentException>(VALIDATION_ASK_INCORRECT_INSTRUMENT.getText(option,
//                                                                                                  equity)) {
//            @Override
//            protected void run()
//                    throws Exception
//            {
//                builder.create();
//            }
//        };
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.impl.AbstractEventBuilderTestBase#setDefaults(org.marketcetera.event.impl.AbstractEventBuilderImpl)
     */
    @Override
    protected TopOfBookEventBuilder setDefaults(TopOfBookEventBuilder inBuilder)
            throws Exception
    {
        inBuilder = super.setDefaults(inBuilder);
        if(useEquity) {
            inBuilder.withAsk(EventTestBase.generateEquityAskEvent(equity,
                                                                   QuoteAction.ADD));
            inBuilder.withBid(EventTestBase.generateEquityBidEvent(equity,
                                                                   QuoteAction.ADD));
            inBuilder.withInstrument(equity);
        } else {
            inBuilder.withAsk(EventTestBase.generateOptionAskEvent(option,
                                                                   QuoteAction.ADD));
            inBuilder.withBid(EventTestBase.generateOptionBidEvent(option,
                                                                   QuoteAction.ADD));
            inBuilder.withInstrument(option);
        }
        return inBuilder;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.impl.AbstractEventBuilderTestBase#verify(org.marketcetera.event.impl.AbstractEventBuilderImpl)
     */
    @Override
    protected TopOfBookEvent verify(TopOfBookEventBuilder inBuilder)
            throws Exception
    {
        TopOfBookEvent event = super.verify(inBuilder);
        assertEquals(inBuilder.getAsk(),
                     event.getAsk());
        assertEquals(inBuilder.getBid(),
                     event.getBid());
        assertEquals(inBuilder.getInstrument(),
                     event.getInstrument());
        assertEquals(inBuilder.getInstrument().getSymbol(),
                     event.getInstrumentAsString());
        List<Event> expectedDecomposedEvents = new ArrayList<Event>();
        if(inBuilder.getBid() != null) {
            expectedDecomposedEvents.add(inBuilder.getBid());
        }
        if(inBuilder.getAsk() != null) {
            expectedDecomposedEvents.add(inBuilder.getAsk());
        }
        assertEquals(expectedDecomposedEvents,
                     event.decompose());
        return event;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.impl.AbstractEventBuilderTestBase#getBuilder()
     */
    @Override
    protected TopOfBookEventBuilder getBuilder()
    {
        return TopOfBookEventBuilder.topOfBookEvent();
    }
    /**
     * Generates an <code>AskEvent</code>.
     *
     * @return an <code>AskEvent</code> value
     * @throws Exception if an unexpected error occurs
     */
    private AskEvent generateAsk()
            throws Exception
    {
        if(useEquity) {
            return EventTestBase.generateEquityAskEvent(equity,
                                                        QuoteAction.ADD);
        } else {
            return EventTestBase.generateOptionAskEvent(option,
                                                        QuoteAction.ADD);
        }
    }
    /**
     * Generates a <code>BidEvent</code>.
     *
     * @return a <code>BidEvent</code> value
     * @throws Exception if an unexpected error occurs
     */
    private BidEvent generateBid()
            throws Exception
    {
        if(useEquity) {
            return EventTestBase.generateEquityBidEvent(equity,
                                                        QuoteAction.ADD);
        } else {
            return EventTestBase.generateOptionBidEvent(option,
                                                        QuoteAction.ADD);
        }
    }
    /**
     * indicates whether to use EQUITY or OPTION instrument types to create builders
     */
    private boolean useEquity = true;
    /**
     * test instrument
     */
    private final Equity equity = new Equity("METC");
    /**
     * test option
     */
    private final Option option = new Option("MSFT",
                                             "20100319",
                                             BigDecimal.ONE,
                                             OptionType.Call);
}
