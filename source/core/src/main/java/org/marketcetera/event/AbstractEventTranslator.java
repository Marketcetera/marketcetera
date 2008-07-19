package org.marketcetera.event;

import java.util.HashMap;
import java.util.Map;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.LoggerAdapter;

import quickfix.Group;
import quickfix.Message;
import quickfix.field.MDEntryPx;
import quickfix.field.MDEntrySize;
import quickfix.field.MDEntryType;
import quickfix.field.MDMkt;
import quickfix.field.NoMDEntries;
import quickfix.field.Symbol;
import quickfix.fix44.MarketDataSnapshotFullRefresh;

/* $License$ */

/**
 * Base class for <code>IEventTranslator</code> implementations.
 * 
 * <p>This class provides utilities and a framework for <code>IEventTranslator</code>
 * instances.
 * 
 * <p>Implementors are <em>strongly</em> recommended to call {@link #updateEventFixMessageSnapshot(EventBase)}
 * before returning <code>EventBase</code> values from {@link IEventTranslator#translate(Object)}.
 * This guarantees that the most recent snapshot information is available on the <code>EventBase</code>
 * value.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 0.5.0
 */
@ClassVersion("$Id$")
public abstract class AbstractEventTranslator
        implements IEventTranslator
{
    /**
     * Updates the given <code>EventBase</code> value's FIX component.
     *
     * <p>The incoming event represents a single component of the Bid, Ask, Trade
     * 3-tuple.  The event will be updated with a FIX message that represents
     * the most recent version of each component for the given symbol, if available.
     *
     * @param inEvent an <code>EventBase</code> value
     * @throws NullPointerException if <code>inEvent</code> is null
     */
    protected final void updateEventFixMessageSnapshot(EventBase inEvent)
    {
        if(inEvent == null) {
            throw new NullPointerException();
        }
        // first, update the snapshot record for this symbol
        updateSnapshot(inEvent);
        // get the updated snapshot
        Message message = getSnapshot(inEvent);
        if(message != null) {
            inEvent.updateFixMessage(message);
        }
    }
    /**
     * tracks the most recent event aggregation by symbol 
     */
    private final Map<String,EventTuple> mSnapshots = new HashMap<String,EventTuple>();
    /**
     * Returns the most comprehensive snapshot available for the symbol represented by the
     * given event.
     *
     * @param inEvent an <code>EventBase</code> value
     * @return a <code>Message</code> value or null if <code>inEvent</code> does not contain any symbol information
     */
    private Message getSnapshot(EventBase inEvent)
    {
        if(!(inEvent instanceof SymbolExchangeEvent)) {
            return null;
        }
        SymbolExchangeEvent event = (SymbolExchangeEvent)inEvent;
        synchronized(mSnapshots) {
            EventTuple tuple = mSnapshots.get(event.getSymbol());
            if(tuple == null) {
                throw new NullPointerException();
            }
            return tuple.getSnapshot();
        }
    }
    /**
     * Updates the stored snapshot for the symbol represented by the given <code>EventBase</code> value.
     *
     * @param inEvent an <code>EventBase</code> value
     */
    private void updateSnapshot(EventBase inEvent)
    {
        // mSnapshots stores a FIX message that is a full snapshot refresh for each symbol
        // the passed Event represents one piece of the 3-tuple that is the information that
        //  is needed for the symbol.  update the stored FIX message with the piece of info
        //  the passed event represents
        synchronized(mSnapshots) {
            // pay attention only to this sub-family of events.  other events don't have a symbol and
            //  can't, therefore, be interesting
            if(inEvent instanceof SymbolExchangeEvent) {
                String symbol = ((SymbolExchangeEvent)inEvent).getSymbol();
                // look to see if we're already storing some info about this symbol
                EventTuple tuple = mSnapshots.get(symbol);
                if(tuple == null) {
                    // nothing yet, this is the first event we're getting on this symbol - create a new entry
                    tuple = new EventTuple(symbol);
                    mSnapshots.put(symbol, 
                                   tuple);
                }
                // record the new event in the tuple
                tuple.setEvent(inEvent);
            } else {
                if(LoggerAdapter.isDebugEnabled(this)) {
                    LoggerAdapter.debug(String.format("Received an unknown event type: %s",
                                                      inEvent),
                                        this);
                }
            }
        }
    }
    /**
     * Encapsulates the parts of a market data snapshot for a symbol.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since 0.5.0
     */
    @ClassVersion("$Id$")
    private static class EventTuple
    {
        /**
         * symbol for which data is collected
         */
        private final String mSymbol;
        /**
         * the bid element of the snapshot
         */
        private BidEvent mBidEvent = null;
        /**
         * the ask element of the snapshot
         */
        private AskEvent mAskEvent = null;
        /**
         * the trade element of the snapshot
         */
        private TradeEvent mTradeEvent = null;
        /**
         * Create a new EventTuple instance.
         *
         * @param inSymbol a <code>String</code> value
         */
        private EventTuple(String inSymbol)
        {
            mSymbol = inSymbol;
        }
        /**
         * Takes the passed <code>EventBase</code> value and maps it
         * to a component of a market data refresh snapshot.
         *
         * <p>If the passed <code>EventBase</code> value does not correspond
         * to a component of a market data refresh snapshot, or is null, it will be ignored.
         * 
         * @param inEvent an <code>EventBase</code> value
         */
        private void setEvent(EventBase inEvent)
        {
            if(inEvent == null) {
                return;
            }
            if(inEvent instanceof BidEvent) {
                mBidEvent = (BidEvent)inEvent;
            }
            if(inEvent instanceof AskEvent) {
                mAskEvent = (AskEvent)inEvent;
            }
            if(inEvent instanceof TradeEvent) {
                mTradeEvent = (TradeEvent)inEvent;
            }
        }
        /**
         * Constructs a market data refresh snapshot based on information collected so far for
         * the symbol associated with this object.
         *
         * @return a <code>Message</code> value
         */
        private Message getSnapshot()
        {
            // construct a new market data snapshot based on the collected information for the symbol this object
            //  represents
            // TODO this needs to be constructed in FIXMessageFactory to hide the specific version being used
            Message snapshot = new MarketDataSnapshotFullRefresh();
            snapshot.setField(new Symbol(mSymbol));
            int groupCounter = 0;
            // if present, set the OFFER section of the snapshot
            if(mAskEvent != null) {
                Group group = new MarketDataSnapshotFullRefresh.NoMDEntries();
                group.setField(new MDEntryType(MDEntryType.OFFER));
                group.setField(new MDEntryPx(mAskEvent.getPrice()));
                group.setField(new MDEntrySize(mAskEvent.getSize()));
                group.setField(new MDMkt(mAskEvent.getExchange()));
                snapshot.addGroup(group);
                groupCounter += 1;
            }
            // if present, set the BID section of the snapshot
            if(mBidEvent != null) {
                Group group = new MarketDataSnapshotFullRefresh.NoMDEntries();
                group.setField(new MDEntryType(MDEntryType.BID));
                group.setField(new MDEntryPx(mBidEvent.getPrice()));
                group.setField(new MDEntrySize(mBidEvent.getSize()));
                group.setField(new MDMkt(mBidEvent.getExchange()));
                snapshot.addGroup(group);
                groupCounter += 1;
            }
            // if present, set the TRADE section of the snapshot
            if(mTradeEvent != null) {
                Group group = new MarketDataSnapshotFullRefresh.NoMDEntries();
                group.setField(new MDEntryType(MDEntryType.TRADE));
                group.setField(new MDEntryPx(mTradeEvent.getPrice()));
                group.setField(new MDEntrySize(mTradeEvent.getSize()));
                group.setField(new MDMkt(mTradeEvent.getExchange()));
                snapshot.addGroup(group);
                groupCounter += 1;
            }
            snapshot.setField(new NoMDEntries(groupCounter));
            return snapshot;
        }
        /* (non-Javadoc)
         * @see java.lang.Object#hashCode()
         */
        @Override
        public int hashCode()
        {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((mSymbol == null) ? 0 : mSymbol.hashCode());
            return result;
        }
        /* (non-Javadoc)
         * @see java.lang.Object#equals(java.lang.Object)
         */
        @Override
        public boolean equals(Object obj)
        {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            final EventTuple other = (EventTuple) obj;
            if (mSymbol == null) {
                if (other.mSymbol != null)
                    return false;
            } else if (!mSymbol.equals(other.mSymbol))
                return false;
            return true;
        }
    }
}
