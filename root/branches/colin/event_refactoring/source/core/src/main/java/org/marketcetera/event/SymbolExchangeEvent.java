package org.marketcetera.event;

import java.math.BigDecimal;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.trade.Instrument;

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
    implements HasInstrument
{
    /**
     * Create a new SymbolExchangeEvent instance.
     *
     * @param inMessageID a <code>long</code> value uniquely identifying this event
     * @param inTimestamp a <code>long</code> value containing the number of milliseconds since <code>EPOCH</code>
     *   in GMT
     * @param inInstrument an <code>Instrument</code> value specifying the instrument quoted in this event
     * @param inExchange a <code>String</code> value containing the exchange on which the quote occurred
     * @param inPrice a <code>BigDecimal</code> value containing the price of this event
     * @param inSize a <code>BigDecimal</code> value containing the size of this event
     * @throws IllegalArgumentException if <code>inMessageID</code> or <code>inTimestamp</code> &lt; 0
     * OR if <code>inExchange</code> is non-null but empty
     * @throws NullPointerException if <code>inSymbol</code>, <code>inExchange</code>, <code>inPrice</code>, or
     *  <code>inSize</code> is null
     */    
    protected SymbolExchangeEvent(long inMessageID,
                                  long inTimestamp,
                                  Instrument inInstrument,
                                  String inExchange,
                                  BigDecimal inPrice,
                                  BigDecimal inSize)
    {
        super(inMessageID, 
              inTimestamp);
        if(inInstrument == null ||
           inExchange == null ||
           inPrice == null ||
           inSize == null) {
            throw new NullPointerException();
        }
        if(inExchange.isEmpty()) {
            throw new IllegalArgumentException();
        }
        mInstrument = inInstrument;
        mExchange = inExchange;
        mPrice = inPrice;
        mSize = inSize;
    }
    /**
     * Gets the instrument associated with this event.
     *
     * @return an <code>Instrument</code> value
     */
    @Override
    public Instrument getInstrument()
    {
        return mInstrument;
    }
    /**
     * Gets the symbol associated with this event expressed as a <code>String</code>. 
     *
     * @return a <code>String</code> value
     */
    public String getSymbolAsString()
    {
        return mInstrument.getSymbol();
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
     * Get the price value.
     *
     * @return a <code>BigDecimal</code> value
     */
    public BigDecimal getPrice()
    {
        return mPrice;
    }
    /**
     * Get the size value.
     *
     * @return a <code>BigDecimal</code> value
     */
    public BigDecimal getSize()
    {
        return mSize;
    }    
    /**
     * Returns the exchange of the given event or null. 
     *
     * @param inEvent a <code>SymbolExchangeEvent</code> value
     * @return a <code>String</code> value
     */
    static String getExchange(SymbolExchangeEvent inEvent)
    {
        if(inEvent != null) {
            return inEvent.getExchange();
        }
        return null;
    }
    /**
     * Returns the price of the given event as a <code>String</code>.
     *
     * @param inEvent a <code>SymbolExchangeEvent</code> value
     * @return a <code>String</code> value
     */
    static String getPriceAsString(SymbolExchangeEvent inEvent)
    {
        if(inEvent != null) {
            return inEvent.getPrice().toPlainString();
        }
        return NO_QUANTITY;
    }
    /**
     * Returns the size of the given event as a <code>String</code>.
     *
     * @param inEvent a <code>SymbolExchangeEvent</code> value
     * @return a <code>String</code> value
     */
    static String getSizeAsString(SymbolExchangeEvent inEvent)
    {
        if(inEvent != null) {
            return inEvent.getSize().toPlainString();
        }
        return NO_QUANTITY;
    }
    /**
     * the instrument for this event
     */
    private final Instrument mInstrument;
    /**
     * the exchange on which the event occurred
     */
    private final String mExchange;
    /**
     * the price of the event 
     */
    private final BigDecimal mPrice;
    /**
     * the size of the event 
     */
    private final BigDecimal mSize;
    /**
     * value displayed for a number if there is no entry on that side of the book
     */
    private static String NO_QUANTITY = "---"; //$NON-NLS-1$
    private static final long serialVersionUID = 2L;
}
