package org.marketcetera.event;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.marketcetera.module.ExpectedFailure;
import org.marketcetera.trade.Equity;
import org.marketcetera.trade.Instrument;
import org.marketcetera.util.test.CollectionAssert;

/* $License$ */

/**
 * Tests {@link AggregateEvent}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 1.5.0
 */
public class AggregateEventTest
{
    /**
     * Tests constructing an {@link AggregateEvent}.
     *
     * @throws Exception if an error occurs
     */
    @Test
    public void constructor()
        throws Exception
    {
        final Date now = new Date();
        final Instrument metc = new Equity("METC");
        new ExpectedFailure<NullPointerException>(null) {
            @Override
            protected void run()
                    throws Exception
            {
                new MockAggregateEvent(null,
                                       metc);
            }
        };
        new ExpectedFailure<NullPointerException>(null) {
            @Override
            protected void run()
                    throws Exception
            {
                new MockAggregateEvent(now,
                                       null);
            }
        };
        MockAggregateEvent test = new MockAggregateEvent(now,
                                                         metc);
        assertEquals(metc,
                     test.getInstrument());
        assertEquals(now,
                     test.getTimestampAsDate());
        Instrument returnedSymbol = test.getInstrument();
        assertEquals(metc,
                     returnedSymbol);
        returnedSymbol = new Equity("goog");
        assertFalse(metc.equals(returnedSymbol));
        assertEquals(metc,
                     test.getInstrument());
    }
    /**
     * Verifies that the given <code>AggregateEvent</code> decomposes into the
     * given expected events.
     * 
     * <p>No guarantee is made as to the order of the events.
     *
     * @param inActualEvent an <code>AggregateEvent</code> value
     * @param inExpectedEvents a <code>List&lt;EventBase&gt;</code> value
     * @throws Exception if an error occurs
     */
    final static void verifyDecomposedEvents(AggregateEvent inActualEvent,
                                             List<EventBase> inExpectedEvents)
        throws Exception
    {
        CollectionAssert.assertArrayPermutation(inExpectedEvents.toArray(),
                                                inActualEvent.decompose().toArray());
    }
    /**
     * Extends {@link AggregateEvent} in order to test the parent class.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since 1.5.0
     */
    public static class MockAggregateEvent
        extends AggregateEvent
    {
        /**
         * Create a new TestEvent instance.
         *
         * @param inTimestamp a <code>Date</code> value
         * @param inInstrument an <code>Instrument</code> value
         */
        public MockAggregateEvent(Date inTimestamp,
                                  Instrument inInstrument)
        {
            super(inTimestamp,
                  inInstrument);
        }
        /**
         * Create a new MockAggregateEvent instance.
         *
         * @param inCompositeEvents a <code>List&lt;EventBase&gt;</code> value containing the events to which this event should decompose
         */
        public MockAggregateEvent(List<EventBase> inCompositeEvents)
        {
            this(new Date(),
                 new Equity("METC"));
            compositeEvents.addAll(inCompositeEvents);
        }
        /* (non-Javadoc)
         * @see org.marketcetera.event.AggregateEvent#decompose()
         */
        @Override
        public List<EventBase> decompose()
        {
            return compositeEvents;
        }
        /**
         * stores the events to which this event should decompose
         */
        private final List<EventBase> compositeEvents = new ArrayList<EventBase>();
        private static final long serialVersionUID = 1L;
    }
}
