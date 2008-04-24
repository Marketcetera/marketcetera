package org.marketcetera.event;

import java.math.BigDecimal;

import quickfix.Message;

/**
 * Represents a Quote for a given security at a specific time.
 *
 * @author gmiller
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 */
public class QuoteEvent 
    extends SymbolExchangeEvent 
{
	/**
     * the price of the bid on the quote 
	 */
    private final BigDecimal bidPrice;
    /**
     * the size of the bid on the quote 
     */
	private final BigDecimal bidSize;
    /**
     * the price of the ask on the quote 
     */
	private final BigDecimal askPrice;
    /**
     * the size of the ask on the quote 
     */
	private final BigDecimal askSize;

	/**
     * Create a new QuoteEvent instance.
     *
     * @param messageID a <code>long</code> value uniquely identifying this event
     * @param timestamp a <code>long</code> value containing the number of milliseconds since <code>EPOCH</code>
     *   in GMT
     * @param symbol a <code>String</code> value containing the symbol quoted in this event
     * @param bidPrice a <code>BigDecimal</code> value containing the bid price 
     * @param bidSize a <code>BigDecimal</code> value containing the bid size
     * @param askPrice a <code>BigDecimal</code> value containing the ask price
     * @param askSize a <code>BigDecimal</code> value containing the ask size
	 */
    public QuoteEvent(long messageID, 
                      long timestamp, 
			          String symbol,
			          BigDecimal bidPrice,
			          BigDecimal bidSize,
			          BigDecimal askPrice,
			          BigDecimal askSize) 
    {
		this(messageID, 
             timestamp, 
             symbol, 
             bidPrice, 
             bidSize, 
             askPrice, 
             askSize, 
             null);
	}
    
    /**
     * Create a new QuoteEvent instance.
     *
     * @param messageID a <code>long</code> value uniquely identifying this event
     * @param timestamp a <code>long</code> value containing the number of milliseconds since <code>EPOCH</code>
     *   in GMT
     * @param symbol a <code>String</code> value containing the symbol quoted in this event
     * @param bidPrice a <code>BigDecimal</code> value containing the bid price 
     * @param bidSize a <code>BigDecimal</code> value containing the bid size
     * @param askPrice a <code>BigDecimal</code> value containing the ask price
     * @param askSize a <code>BigDecimal</code> value containing the ask size
     * @param fixMessage a <code>Message</code> value containing the <code>FIX</code> message describing this event
     */
	public QuoteEvent(long messageID, 
                      long timestamp, 
                      String symbol,
                      BigDecimal bidPrice,
                      BigDecimal bidSize,
                      BigDecimal askPrice,
                      BigDecimal askSize,
                      Message fixMessage) 
    {
        this(messageID,
             timestamp,
             symbol,
             null,
             bidPrice,
             bidSize,
             askPrice,
             askSize,
             fixMessage);
	}
	
    /**
     * Create a new QuoteEvent instance.
     *
     * @param messageID a <code>long</code> value uniquely identifying this event
     * @param timestamp a <code>long</code> value containing the number of milliseconds since <code>EPOCH</code>
     *   in GMT
     * @param symbol a <code>String</code> value containing the symbol quoted in this event
     * @param inExchange a <code>String</code> value containing the exchange on which the quote occurred 
     * @param bidPrice a <code>BigDecimal</code> value containing the bid price 
     * @param bidSize a <code>BigDecimal</code> value containing the bid size
     * @param askPrice a <code>BigDecimal</code> value containing the ask price
     * @param askSize a <code>BigDecimal</code> value containing the ask size
     * @param fixMessage a <code>Message</code> value containing the <code>FIX</code> message describing this event
     */
    public QuoteEvent(long messageID, 
                      long timestamp, 
                      String symbol,
                      String inExchange,
                      BigDecimal bidPrice,
                      BigDecimal bidSize,
                      BigDecimal askPrice,
                      BigDecimal askSize,
                      Message fixMessage) 
    {
        super(messageID, 
              timestamp, 
              fixMessage,
              symbol,
              inExchange);
        this.bidPrice = bidPrice;
        this.bidSize = bidSize;
        this.askPrice = askPrice;
        this.askSize = askSize;
    }

	/**
     * Gets the bid price. 
     *
     * @return a <code>BigDecimal</code> value
	 */
    public BigDecimal getBidPrice() 
    {
		return bidPrice;
	}
    
    /**
     * Gets the ask price. 
     *
     * @return a <code>BigDecimal</code> value
     */
	public BigDecimal getAskPrice() 
    {
		return askPrice;
	}

    /**
     * Gets the bid size. 
     *
     * @return a <code>BigDecimal</code> value
     */
	public BigDecimal getBidSize() 
    {
		return bidSize;
	}

    /**
     * Gets the ask size. 
     *
     * @return a <code>BigDecimal</code> value
     */
	public BigDecimal getAskSize() 
    {
		return askSize;
	}	
	
	/**
     * Indicates if this event includes a bid price. 
     *
     * @return a <code>boolean</code> value
	 */
    public boolean hasBidPrice()
    {
		return bidPrice != null;
	}

    /**
     * Indicates if this event includes an ask price. 
     *
     * @return a <code>boolean</code> value
     */
	public boolean hasAskPrice()
    {
		return askPrice != null;
	}
	
    /**
     * Indicates if this event includes a bid size. 
     *
     * @return a <code>boolean</code> value
     */
	public boolean hasBidSize()
    {
		return bidSize != null;
	}

    /**
     * Indicates if this event includes an ask size. 
     *
     * @return a <code>boolean</code> value
     */
	public boolean hasAskSize()
    {
		return askSize != null;
	}		
    
    public String toString()
    {
        StringBuffer output = new StringBuffer();
        output.append("Quote for ").append(getSymbol()).append(": ").append(getBidPrice()).append("-").append(getAskPrice());
        output.append(" ").append(getBidSize()).append("x").append(getAskSize()).append(" ");
        output.append(getSymbol()).append(" ").append(getExchange()).append(" at ").append(getTimestampAsDate());
        return output.toString();
    }
}
