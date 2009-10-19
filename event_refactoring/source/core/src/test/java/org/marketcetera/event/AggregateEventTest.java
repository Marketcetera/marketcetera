package org.marketcetera.event;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.marketcetera.event.beans.InstrumentBean;
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
                     test.getTimestamp());
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
     * @param inExpectedEvents a <code>List&lt;Event&gt;</code> value
     * @throws Exception if an error occurs
     */
    final static void verifyDecomposedEvents(AggregateEvent inActualEvent,
                                             List<Event> inExpectedEvents)
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
        implements AggregateEvent
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
            event = new EventImpl(System.nanoTime(),
                                  new Date());
            instrument.setInstrument(inInstrument);
        }
        /**
         * Create a new MockAggregateEvent instance.
         *
         * @param inCompositeEvents a <code>List&lt;Event&gt;</code> value containing the events to which this event should decompose
         */
        public MockAggregateEvent(List<Event> inCompositeEvents)
        {
            this(new Date(),
                 new Equity("METC"));
            compositeEvents.addAll(inCompositeEvents);
        }
        /* (non-Javadoc)
         * @see org.marketcetera.event.Event#getMessageId()
         */
        @Override
        public long getMessageId()
        {
            return event.getMessageId();
        }
        /* (non-Javadoc)
         * @see org.marketcetera.event.Event#getSource()
         */
        @Override
        public Object getSource()
        {
            return event.getSource();
        }
        /* (non-Javadoc)
         * @see org.marketcetera.event.Event#getTimestamp()
         */
        @Override
        public Date getTimestamp()
        {
            return event.getTimestamp();
        }
        /* (non-Javadoc)
         * @see org.marketcetera.event.Event#setSource(java.lang.Object)
         */
        @Override
        public void setSource(Object inSource)
        {
            event.setSource(inSource);
        }
        /* (non-Javadoc)
         * @see org.marketcetera.event.TimestampCarrier#getTimeMillis()
         */
        @Override
        public long getTimeMillis()
        {
            return getTimestamp().getTime();
        }
        /* (non-Javadoc)
         * @see org.marketcetera.event.AggregateEvent#decompose()
         */
        @Override
        public List<Event> decompose()
        {
            return compositeEvents;
        }
        /**
         * Get the instrument value.
         *
         * @return a <code>InstrumentBean</code> value
         */
        public Instrument getInstrument()
        {
            return instrument.getInstrument();
        }
        /**
         * stores the events to which this event should decompose
         */
        private final List<Event> compositeEvents = new ArrayList<Event>();
        /**
         * 
         */
        private final EventImpl event;
        private final InstrumentBean instrument = new InstrumentBean();
        private static final long serialVersionUID = 1L;
    }
}
