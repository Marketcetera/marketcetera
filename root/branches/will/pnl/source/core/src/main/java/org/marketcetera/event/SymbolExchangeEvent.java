package org.marketcetera.event;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.trade.MSymbol;

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
{
    private static final long serialVersionUID = 1L;
    /**
     * the symbol for this event
     */
    private final MSymbol mSymbol;
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
     * @param inSymbol an <code>MSymbol</code> value containing the symbol quoted in this event
     * @param inExchange a <code>String</code> value containing the exchange on which the quote occurred 
     */    
    protected SymbolExchangeEvent(long inMessageID,
                                  long inTimestamp,
                                  MSymbol inSymbol,
                                  String inExchange)
    {
        super(inMessageID, 
              inTimestamp);
        if(inSymbol == null) {
            throw new NullPointerException();
        }
        mSymbol = inSymbol;
        mExchange = inExchange;
    }
    /**
     * Gets the symbol associated with this event.
     *
     * @return an <code>MSymbol</code> value
     */
    public MSymbol getSymbol()
    {
        return mSymbol;
    }
    /**
     * Gets the symbol associated with this event expressed as a <code>String</code>. 
     *
     * @return a <code>String</code> value
     */
    public String getSymbolAsString()
    {
        return mSymbol.getFullSymbol();
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
