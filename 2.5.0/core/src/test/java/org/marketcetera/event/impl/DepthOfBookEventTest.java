package org.marketcetera.event.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.marketcetera.event.AskEvent;
import org.marketcetera.event.BidEvent;
import org.marketcetera.event.DepthOfBookEvent;
import org.marketcetera.event.Event;
import org.marketcetera.event.EventTestBase;
import org.marketcetera.event.QuoteAction;
import org.marketcetera.module.ExpectedFailure;
import org.marketcetera.trade.Equity;
import org.marketcetera.trade.Instrument;
import org.marketcetera.trade.Option;
import org.marketcetera.trade.OptionType;

/* $License$ */

/**
 * Tests {@link DepthOfBookEventBuilder} and {@link DepthOfBookEventImpl}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 2.0.0
 */
public class DepthOfBookEventTest
        extends AbstractEventBuilderTestBase<DepthOfBookEvent,DepthOfBookEventBuilder>
{
    /**
     * Run before each test.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Before
    public void setup()
            throws Exception
    {
        equity = new Equity("METC");
        option = new Option("MSFT",
                            "20100319",
                            BigDecimal.ONE,
                            OptionType.Call);
        builder = setDefaults(DepthOfBookEventBuilder.depthOfBook());
    }
    /**
     * Tests {@link DepthOfBookEventBuilder#withBids(List)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void withBids()
            throws Exception
    {
        // null bids
        builder.withBids(null);
        assertEquals(new ArrayList<BidEvent>(),
                     builder.getBids());
        // empty bids
        builder.withBids(new ArrayList<BidEvent>());
        assertEquals(new ArrayList<BidEvent>(),
                     builder.getBids());
        // real bids
        builder.withBids(bids);
        assertEquals(bids,
                     builder.getBids());
        verify(builder);
    }
    /**
     * Tests {@link DepthOfBookEventBuilder#withAsks(List)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void withAsks()
            throws Exception
    {
        // null asks
        builder.withAsks(null);
        assertEquals(new ArrayList<AskEvent>(),
                     builder.getAsks());
        // empty asks
        builder.withAsks(new ArrayList<AskEvent>());
        assertEquals(new ArrayList<AskEvent>(),
                     builder.getAsks());
        // real asks
        builder.withAsks(asks);
        assertEquals(asks,
                     builder.getAsks());
        verify(builder);
    }
    /**
     * Tests {@link DepthOfBookEventBuilder#withInstrument(Instrument)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void withInstrument()
            throws Exception
    {
        builder.withInstrument(null);
        assertNull(builder.getInstrument());
        builder.withInstrument(equity);
        assertEquals(equity,
                     builder.getInstrument());
        builder.withInstrument(option);
        assertEquals(option,
                     builder.getInstrument());
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
        builder.withInstrument(null);
        new ExpectedFailure<IllegalArgumentException>(VALIDATION_NULL_INSTRUMENT.getText()) {
            @Override
            protected void run()
                    throws Exception
            {
                builder.create();
            }
        };
        setDefaults(builder);
        // asks contains null
        List<AskEvent> newAsks = new ArrayList<AskEvent>(builder.getAsks());
        newAsks.add(null);
        builder.withAsks(newAsks);
        new ExpectedFailure<IllegalArgumentException>(VALIDATION_LIST_CONTAINS_NULL.getText(String.valueOf(newAsks))) {
            @Override
            protected void run()
                    throws Exception
            {
                builder.create();
            }
        };
        setDefaults(builder);
        // bids contains null
        List<BidEvent> newBids = new ArrayList<BidEvent>(builder.getBids());
        newBids.add(null);
        builder.withBids(newBids);
        new ExpectedFailure<IllegalArgumentException>(VALIDATION_LIST_CONTAINS_NULL.getText(String.valueOf(newBids))) {
            @Override
            protected void run()
                    throws Exception
            {
                builder.create();
            }
        };
        setDefaults(builder);
        // contains a bid with the wrong instrument
        builder.withInstrument(option);
        new ExpectedFailure<IllegalArgumentException>(VALIDATION_LIST_INCORRECT_INSTRUMENT.getText(builder.getBids().get(0),
                                                                                                   option)) {
            @Override
            protected void run()
                    throws Exception
            {
                builder.create();
            }
        };
        setDefaults(builder);
        // contains an ask with the wrong instrument (a little harder to set up because bids are checked first)
        newAsks = new ArrayList<AskEvent>(builder.getAsks());
        AskEvent badAsk = EventTestBase.generateOptionAskEvent(option,
                                                               QuoteAction.ADD);
        newAsks.add(badAsk);
        builder.withAsks(newAsks);
        new ExpectedFailure<IllegalArgumentException>(VALIDATION_LIST_INCORRECT_INSTRUMENT.getText(badAsk,
                                                                                                   equity)) {
            @Override
            protected void run()
                    throws Exception
            {
                builder.create();
            }
        };
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.impl.AbstractEventBuilderTestBase#verify(org.marketcetera.event.impl.AbstractEventBuilderImpl)
     */
    @Override
    protected DepthOfBookEvent verify(DepthOfBookEventBuilder inBuilder)
            throws Exception
    {
        DepthOfBookEvent event = super.verify(inBuilder);
        if(inBuilder.getAsks() == null) {
            assertEquals(new ArrayList<AskEvent>(),
                         event.getAsks());
        } else {
            assertEquals(inBuilder.getAsks(),
                         event.getAsks());
        }
        if(inBuilder.getBids() == null) {
            assertEquals(new ArrayList<BidEvent>(),
                         event.getBids());
        } else {
            assertEquals(inBuilder.getBids(),
                         event.getBids());
        }
        assertEquals(inBuilder.getInstrument(),
                     event.getInstrument());
        assertEquals(inBuilder.getInstrument().getSymbol(),
                     event.getInstrumentAsString());
        List<Event> expectedDecomposedEvents = new ArrayList<Event>();
        expectedDecomposedEvents.addAll(inBuilder.getBids());
        expectedDecomposedEvents.addAll(inBuilder.getAsks());
        assertEquals(expectedDecomposedEvents,
                     event.decompose());
        return event;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.impl.AbstractEventBuilderTestBase#getBuilder()
     */
    @Override
    protected DepthOfBookEventBuilder getBuilder()
    {
        return builder;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.impl.AbstractEventBuilderTestBase#setDefaults(org.marketcetera.event.impl.AbstractEventBuilderImpl)
     */
    @Override
    protected DepthOfBookEventBuilder setDefaults(DepthOfBookEventBuilder inBuilder)
            throws Exception
    {
        super.setDefaults(inBuilder);
        asks = EventTestBase.generateEquityAskEvents(equity,
                                                     "exchange",
                                                     5);
        bids = EventTestBase.generateEquityBidEvents(equity,
                                                     "exchange",
                                                     5);
        inBuilder.withAsks(asks)
                 .withBids(bids)
                 .withInstrument(equity);
        return inBuilder;
    }
    /**
     * test builder
     */
    private DepthOfBookEventBuilder builder;
    /**
     * test list of asks
     */
    private List<AskEvent> asks;
    /**
     * test list of bids
     */
    private List<BidEvent> bids;
    /**
     * test equity
     */
    private Equity equity;
    /**
     * test option
     */
    private Option option;
}
