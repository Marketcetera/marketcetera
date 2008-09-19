package org.marketcetera.event;

import org.marketcetera.core.ClassVersion;

import quickfix.Message;

/* $License$ */

/**
 * Base class for events involving a <code>Symbol</code> and an <code>Exchange</code>.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 0.5.0
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public abstract class SymbolExchangeEvent
    extends EventBase
    implements HasFIXMessage
{
    /**
     * the symbol for this event
     */
    private final String mSymbol;
    /**
     * the exchange on which the event occurred
     */
    private final String mExchange;
    /**
     * underlying latest tick FIX message for this market event
     */
    private Message mLatestTick;
    /**
     * Create a new QuoteEvent instance.
     *
     * @param inMessageID a <code>long</code> value uniquely identifying this event
     * @param inTimestamp a <code>long</code> value containing the number of milliseconds since <code>EPOCH</code>
     *   in GMT
     * @param inSymbol a <code>String</code> value containing the symbol quoted in this event
     * @param inExchange a <code>String</code> value containing the exchange on which the quote occurred 
     */    
    protected SymbolExchangeEvent(long inMessageID,
                                  long inTimestamp,
                                  String inSymbol,
                                  String inExchange)
    {
        super(inMessageID, 
              inTimestamp);
        mSymbol = inSymbol;
        mExchange = inExchange;
    }
    /**
     * Gets the symbol associated with this event.
     *
     * @return a <code>String</code> value
     */
    public String getSymbol()
    {
        return mSymbol;
    }
    /**
     * Gets the exchange associated with this event.
     *
     * @return a <code>String</code> value
     */
    public String getExchange()
    {
        return mExchange;
    }    
    /**
     * Returns the underlying latest tick FIX message for this event.
     *
     * @return a <code>Message</code> value or null if no <code>Message</code> was specified at creation
     */
    @Deprecated
    public Message getLatestTick()
    {
        return mLatestTick;
    }
    /**
     * Clears the <code>FIX</code> messages stored on this event.
     */
    @Deprecated
    public void clearFIXMessages()
    {
        mLatestTick = null;
    }
    /**
     * Updates the latest tick for this event.
     *
     * @param inMessage a <code>Message</code> value to replace the current one
     */
    final void updateLatestTick(Message inMessage)
    {
        mLatestTick = inMessage;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.HasFIXMessage#getMessage()
     */
    @Override
    public Message getMessage()
    {
        return getLatestTick();
    }
}
