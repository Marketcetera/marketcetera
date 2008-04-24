package org.marketcetera.event;

import quickfix.Message;

/**
 * Base class for events involving a <code>Symbol</code> and an <code>Exchange</code>.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 0.43-SNAPSHOT
 */
public abstract class SymbolExchangeEvent
    extends EventBase
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
     * Create a new QuoteEvent instance.
     *
     * @param inMessageID a <code>long</code> value uniquely identifying this event
     * @param inTimestamp a <code>long</code> value containing the number of milliseconds since <code>EPOCH</code>
     *   in GMT
     * @param inFixMessage a <code>Message</code> value containing the <code>FIX</code> message describing this event
     * @param inSymbol a <code>String</code> value containing the symbol quoted in this event
     * @param inExchange a <code>String</code> value containing the exchange on which the quote occurred 
     */    
    protected SymbolExchangeEvent(long inMessageID,
                                  long inTimestamp,
                                  Message inFixMessage,
                                  String inSymbol,
                                  String inExchange)
    {
        super(inMessageID, 
              inTimestamp, 
              inFixMessage);
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
}
