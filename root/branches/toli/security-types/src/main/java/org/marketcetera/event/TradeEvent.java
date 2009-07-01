package org.marketcetera.event;

import java.math.BigDecimal;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.marketdata.DateUtils;
import org.marketcetera.trade.MSymbol;

/* $License$ */

/**
 * Represents a Trade for a given security at a specific time.
 *
 * @author gmiller
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 0.5.0
 */
@ClassVersion("$Id$")
public class TradeEvent 
    extends SymbolExchangeEvent
{
    private static final long serialVersionUID = 1L;
    /**
     * Create a new TradeEvent instance.
     *
     * @param inMessageID a <code>long</code> value uniquely identifying this event
     * @param inTimestamp a <code>long</code> value containing the number of milliseconds since <code>EPOCH</code>
     *   in GMT
     * @param inSymbol an <code>MSymbol</code> value containing the symbol quoted in this event
     * @param inExchange a <code>String</code> value containing the exchange on which the quote occurred 
     * @param inPrice a <code>BigDecimal</code> value containing the price of this event
     * @param inSize a <code>BigDecimal</code> value containing the size of this event
     * @throws IllegalArgumentException if <code>inMessageID</code> or <code>inTimestamp</code> &lt; 0
     * @throws IllegalArgumentException if <code>inExchange</code> is non-null but empty
     * @throws NullPointerException if <code>inSymbol</code>, <code>inExchange</code>, <code>inPrice</code>, or
     *  <code>inSize</code> is null
    */
    public TradeEvent(long inMessageID, 
                      long inTimestamp, 
                      MSymbol inSymbol,
                      String inExchange,
                      BigDecimal inPrice,
                      BigDecimal inSize) 
    {
        super(inMessageID, 
              inTimestamp,
              inSymbol,
              inExchange,
              inPrice,
              inSize);
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuffer output = new StringBuffer();
        output.append("Trade for ").append(getSymbol()).append(": ").append(getPrice()).append(" ").append(getSize()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        output.append(" ").append(getSymbol()).append(" ").append(getExchange()).append(" at ").append(DateUtils.dateToString(getTimestampAsDate())); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        return output.toString();
    }
}
