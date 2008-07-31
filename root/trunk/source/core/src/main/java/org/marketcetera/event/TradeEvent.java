package org.marketcetera.event;

import java.math.BigDecimal;
import quickfix.Message;

/**
 * Represents a Trade for a given security at a specific time.
 *
 * @author gmiller
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 */
public class TradeEvent 
    extends SymbolExchangeEvent
{
	/**
     * the price of the trade 
	 */
    private final BigDecimal price;
    /**
     * the size of the trade
     */
	private final BigDecimal size;

    /**
     * Create a new TradeEvent instance.
     *
     * @param messageID a <code>long</code> value uniquely identifying this event
     * @param timestamp a <code>long</code> value containing the number of milliseconds since <code>EPOCH</code>
     *   in GMT
     * @param symbol a <code>String</code> value containing the symbol quoted in this event
     * @param price a <code>BigDecimal</code> value containing the price 
     * @param size a <code>BigDecimal</code> value containing the size
     */
	public TradeEvent(long messageID, 
                      long timestamp, 
                      String symbol,
                      BigDecimal price,
                      BigDecimal size)
    {
		this(messageID, 
             timestamp, 
             symbol, 
             price, 
             size, 
             null);
	}

    /**
     * Create a new TradeEvent instance.
     *
     * @param messageID a <code>long</code> value uniquely identifying this event
     * @param timestamp a <code>long</code> value containing the number of milliseconds since <code>EPOCH</code>
     *   in GMT
     * @param symbol a <code>String</code> value containing the symbol quoted in this event
     * @param price a <code>BigDecimal</code> value containing the price 
     * @param size a <code>BigDecimal</code> value containing the size
     * @param fixMessage a <code>Message</code> value containing the <code>FIX</code> message describing this event
     */
	public TradeEvent(long messageID, 
                      long timestamp, 
                      String symbol,
                      BigDecimal price,
                      BigDecimal size, 
                      Message fixMessage) 
    {
        this(messageID, 
                 timestamp, 
                 symbol,
                 null,
                 price, 
                 size, 
                 fixMessage);
	}

    /**
     * Create a new TradeEvent instance.
     *
     * @param messageID a <code>long</code> value uniquely identifying this event
     * @param timestamp a <code>long</code> value containing the number of milliseconds since <code>EPOCH</code>
     *   in GMT
     * @param symbol a <code>String</code> value containing the symbol quoted in this event
     * @param inExchange a <code>String</code> value containing the exchange on which the trade occurred 
     * @param price a <code>BigDecimal</code> value containing the price 
     * @param size a <code>BigDecimal</code> value containing the size
     * @param fixMessage a <code>Message</code> value containing the <code>FIX</code> message describing this event
     */
    public TradeEvent(long messageID, 
                      long timestamp, 
                      String symbol,
                      String inExchange,
                      BigDecimal price,
                      BigDecimal size, 
                      Message fixMessage) 
    {
        super(messageID, 
              timestamp, 
              fixMessage,
              symbol,
              inExchange);
        this.price = price;
        this.size = size;
    }
    
	/**
     * Gets the price of the trade. 
     *
     * @return a <code>BigDecimal</code> value
	 */
    public BigDecimal getPrice() 
    {
		return price;
	}

	/**
     * Gets the size of the trade. 
     *
     * @return a <code>BigDecimal</code> value
	 */
    public BigDecimal getSize() 
    {
		return size;
	}

	/**
     * Indicates if the event has a price. 
     *
     * @return a <code>boolean</code> value
	 */
    public boolean hasPrice()
    {
		return price != null;
	}

    /**
     * Indicates if the event has a size. 
     *
     * @return a <code>boolean</code> value
     */
	public boolean hasSize()
    {        
		return size != null;
	}

    public String toString()
    {
        StringBuffer output = new StringBuffer();
        output.append("Trade for ").append(getSymbol()).append(": ").append(getPrice()).append(" ").append(getSize()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        output.append(" ").append(getSymbol()).append(" ").append(getExchange()).append(" at ").append(getTimestampAsDate()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        return output.toString();
    }
}
