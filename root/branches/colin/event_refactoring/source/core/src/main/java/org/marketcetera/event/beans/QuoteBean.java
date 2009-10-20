package org.marketcetera.event.beans;

import java.math.BigDecimal;

import org.marketcetera.event.util.QuoteAction;
import org.marketcetera.trade.Instrument;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class QuoteBean
{
    /**
     * Get the price value.
     *
     * @return a <code>BigDecimal</code> value
     */
    public BigDecimal getPrice()
    {
        return marketData.getPrice();
    }
    /**
     * Sets the price value.
     *
     * @param a <code>BigDecimal</code> value
     */
    public void setPrice(BigDecimal inPrice)
    {
        marketData.setPrice(inPrice);
    }
    /**
     * Get the size value.
     *
     * @return a <code>BigDecimal</code> value
     */
    public BigDecimal getSize()
    {
        return marketData.getSize();
    }
    /**
     * Sets the size value.
     *
     * @param a <code>BigDecimal</code> value
     */
    public void setSize(BigDecimal inSize)
    {
        marketData.setSize(inSize);
    }
    /**
     * Get the quoteTime value.
     *
     * @return a <code>String</code> value
     */
    public String getQuoteTime()
    {
        return marketData.getExchangeTimestamp();
    }
    /**
     * Sets the quoteTime value.
     *
     * @param a <code>String</code> value
     */
    public void setQuoteTime(String inQuoteTime)
    {
        marketData.setExchangeTimestamp(inQuoteTime);
    }
    /**
     * Get the exchange value.
     *
     * @return a <code>String</code> value
     */
    public String getExchange()
    {
        return marketData.getExchange();
    }
    /**
     * Sets the exchange value.
     *
     * @param a <code>String</code> value
     */
    public void setExchange(String inExchange)
    {
        marketData.setExchange(inExchange);
    }
    /**
     * Get the action value.
     *
     * @return a <code>QuoteAction</code> value
     */
    public QuoteAction getAction()
    {
        return action;
    }
    /**
     * Sets the action value.
     *
     * @param a <code>QuoteAction</code> value
     */
    public void setAction(QuoteAction inAction)
    {
        action = inAction;
    }
    /**
     * Get the instrument value.
     *
     * @return an <code>Instrument</code> value
     */
    public Instrument getInstrument()
    {
        return instrument.getInstrument();
    }
    /**
     * Sets the instrument value.
     *
     * @param an <code>Instrument</code> value
     */
    public void setInstrument(Instrument inInstrument)
    {
        instrument.setInstrument(inInstrument);
    }
    /**
     * 
     */
    private QuoteAction action;
    /**
     * 
     */
    private final InstrumentBean instrument = new InstrumentBean();
    /**
     * 
     */
    private final MarketDataBean marketData = new MarketDataBean();
}
