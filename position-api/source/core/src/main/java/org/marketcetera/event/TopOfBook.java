package org.marketcetera.event;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.marketdata.DateUtils;
import org.marketcetera.trade.MSymbol;

/* $License$ */

/**
 * Represents the Top-of-Book or Best-Bid-and-Offer for a specific security at a specific point in time.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 1.5.0
 */
@ClassVersion("$Id$")
public class TopOfBook
    extends AggregateEvent
{
    /**
     * Create a new <code>TopOfBook</code> instance.
     * 
     * <p>Either <code>inBid</code> or <code>inAsk</code> or both may be null.  A null value
     * indicates that the book in question has no entry on the corresponding side.
     *
     * @param inBid a <code>BidEvent</code> value or null
     * @param inAsk an <code>AskEvent</code> value or null
     * @param inTimestamp a <code>Date</code> value indicating when the event occurred
     * @param inSymbol an <code>MSymbol</code> value containing the symbol for which the event occurred
     * @throws IllegalArgumentException if <code>inTimestamp</code> &lt; 0
     * @throws IllegalArgumentException if the symbol of <code>inBid</code> or <code>inAsk</code> does not match <code>inSymbol</code>
     * @throws NullPointerException if <code>inSymbol</code> or <code>inTimestamp</code> is null
     */
    public TopOfBook(BidEvent inBid,
                     AskEvent inAsk,
                     Date inTimestamp,
                     MSymbol inSymbol)
    {
        super(inTimestamp,
              inSymbol);
        bid = inBid;
        ask = inAsk;
        if((bid != null &&
            !bid.getSymbol().equals(inSymbol)) ||
           (ask != null &&
            !ask.getSymbol().equals(inSymbol))) {
            throw new IllegalArgumentException();
        }
    }
    /**
     * Get the bid value.
     *
     * @return a <code>BidEvent</code> value
     */
    public BidEvent getBid()
    {
        return bid;
    }
    /**
     * Get the ask value.
     *
     * @return a <code>AskEvent</code> value
     */
    public AskEvent getAsk()
    {
        return ask;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.AggregateEvent#decompose()
     */
    @Override
    public List<EventBase> decompose()
    {
        List<EventBase> events = new ArrayList<EventBase>();
        if(bid != null) {
            events.add(bid);
        }
        if(ask != null) {
            events.add(ask);
        }
        return Collections.unmodifiableList(events);
    }
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((ask == null) ? 0 : ask.hashCode());
        result = prime * result + ((bid == null) ? 0 : bid.hashCode());
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
        TopOfBook other = (TopOfBook) obj;
        if (ask == null) {
            if (other.ask != null)
                return false;
        } else if (!ask.equals(other.ask))
            return false;
        if (bid == null) {
            if (other.bid != null)
                return false;
        } else if (!bid.equals(other.bid))
            return false;
        return true;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return String.format("TopOfBook for %s at %s: %s %s-%s %sx%s %s", //$NON-NLS-1$
                             getSymbol(),
                             DateUtils.dateToString(getTimestampAsDate()),
                             SymbolExchangeEvent.getExchange(bid),
                             SymbolExchangeEvent.getPriceAsString(bid),
                             SymbolExchangeEvent.getPriceAsString(ask),
                             SymbolExchangeEvent.getSizeAsString(bid),
                             SymbolExchangeEvent.getSizeAsString(ask),
                             SymbolExchangeEvent.getExchange(ask));
    }
    /**
     * the top-of-the-book bid
     */
    private final BidEvent bid;
    /**
     * the top-of-the-book ask
     */
    private final AskEvent ask;
    private static final long serialVersionUID = 1L;
}
