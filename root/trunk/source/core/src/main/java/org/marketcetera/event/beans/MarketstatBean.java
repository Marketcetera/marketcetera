package org.marketcetera.event.beans;

import java.math.BigDecimal;

import javax.annotation.concurrent.NotThreadSafe;

import org.marketcetera.event.MarketstatEvent;
import org.marketcetera.event.util.EventServices;
import org.marketcetera.trade.Instrument;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Stores the attributes necessary for {@link MarketstatEvent}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 2.0.0
 */
@NotThreadSafe
@ClassVersion("$Id$")
public final class MarketstatBean
        extends EventBean
{
    /**
     * Creates a shallow copy of the given <code>MarketstatBean</code>.
     *
     * @param inBean a <code>MarketstatBean</code> value
     * @return a <code>MarketstatBean</code> value
     */
    public static MarketstatBean copy(MarketstatBean inBean)
    {
        MarketstatBean newBean = new MarketstatBean();
        copyAttributes(inBean,
                       newBean);
        return newBean;
    }
    /**
     * Get the openPrice value.
     *
     * @return a <code>BigDecimal</code> value
     */
    public BigDecimal getOpen()
    {
        return openPrice;
    }
    /**
     * Sets the openPrice value.
     *
     * @param inOpenPrice a <code>BigDecimal</code> value
     */
    public void setOpen(BigDecimal inOpenPrice)
    {
        openPrice = inOpenPrice;
    }
    /**
     * Get the highPrice value.
     *
     * @return a <code>BigDecimal</code> value
     */
    public BigDecimal getHigh()
    {
        return highPrice;
    }
    /**
     * Sets the highPrice value.
     *
     * @param inHighPrice a <code>BigDecimal</code> value
     */
    public void setHigh(BigDecimal inHighPrice)
    {
        highPrice = inHighPrice;
    }
    /**
     * Get the lowPrice value.
     *
     * @return a <code>BigDecimal</code> value
     */
    public BigDecimal getLow()
    {
        return lowPrice;
    }
    /**
     * Sets the lowPrice value.
     *
     * @param inLowPrice a <code>BigDecimal</code> value
     */
    public void setLow(BigDecimal inLowPrice)
    {
        lowPrice = inLowPrice;
    }
    /**
     * Get the closePrice value.
     *
     * @return a <code>BigDecimal</code> value
     */
    public BigDecimal getClose()
    {
        return closePrice;
    }
    /**
     * Sets the closePrice value.
     *
     * @param inClosePrice a <code>BigDecimal</code> value
     */
    public void setClose(BigDecimal inClosePrice)
    {
        closePrice = inClosePrice;
    }
    /**
     * Get the previousClosePrice value.
     *
     * @return a <code>BigDecimal</code> value
     */
    public BigDecimal getPreviousClose()
    {
        return previousClosePrice;
    }
    /**
     * Sets the previousClosePrice value.
     *
     * @param inPreviousClosePrice a <code>BigDecimal</code> value
     */
    public void setPreviousClose(BigDecimal inPreviousClosePrice)
    {
        previousClosePrice = inPreviousClosePrice;
    }
    /**
     * Get the volume value.
     *
     * @return a <code>BigDecimal</code> value
     */
    public BigDecimal getVolume()
    {
        return volume;
    }
    /**
     * Sets the volume value.
     *
     * @param inVolume a <code>BigDecimal</code> value
     */
    public void setVolume(BigDecimal inVolume)
    {
        volume = inVolume;
    }
    /**
     * Get the closeDate value.
     *
     * @return a <code>String</code> value
     */
    public String getCloseDate()
    {
        return closeDate;
    }
    /**
     * Sets the closeDate value.
     *
     * @param inCloseDate a <code>String</code> value
     */
    public void setCloseDate(String inCloseDate)
    {
        closeDate = inCloseDate;
    }
    /**
     * Get the previousCloseDate value.
     *
     * @return a <code>String</code> value
     */
    public String getPreviousCloseDate()
    {
        return previousCloseDate;
    }
    /**
     * Sets the previousCloseDate value.
     *
     * @param inPreviousCloseDate a <code>String</code> value
     */
    public void setPreviousCloseDate(String inPreviousCloseDate)
    {
        previousCloseDate = inPreviousCloseDate;
    }
    /**
     * Get the tradeHighTime value.
     *
     * @return a <code>String</code> value
     */
    public String getTradeHighTime()
    {
        return tradeHighTime;
    }
    /**
     * Sets the tradeHighTime value.
     *
     * @param inTradeHighTime a <code>String</code> value
     */
    public void setTradeHighTime(String inTradeHighTime)
    {
        tradeHighTime = inTradeHighTime;
    }
    /**
     * Get the tradeLowTime value.
     *
     * @return a <code>String</code> value
     */
    public String getTradeLowTime()
    {
        return tradeLowTime;
    }
    /**
     * Sets the tradeLowTime value.
     *
     * @param inTradeLowTime a <code>String</code> value
     */
    public void setTradeLowTime(String inTradeLowTime)
    {
        tradeLowTime = inTradeLowTime;
    }
    /**
     * Get the openExchange value.
     *
     * @return a <code>String</code> value
     */
    public String getOpenExchange()
    {
        return openExchange;
    }
    /**
     * Sets the openExchange value.
     *
     * @param inOpenExchange a <code>String</code> value
     */
    public void setOpenExchange(String inOpenExchange)
    {
        openExchange = inOpenExchange;
    }
    /**
     * Get the highExchange value.
     *
     * @return a <code>String</code> value
     */
    public String getHighExchange()
    {
        return highExchange;
    }
    /**
     * Sets the highExchange value.
     *
     * @param inHighExchange a <code>String</code> value
     */
    public void setHighExchange(String inHighExchange)
    {
        highExchange = inHighExchange;
    }
    /**
     * Get the lowExchange value.
     *
     * @return a <code>String</code> value
     */
    public String getLowExchange()
    {
        return lowExchange;
    }
    /**
     * Sets the lowExchange value.
     *
     * @param inLowExchange a <code>String</code> value
     */
    public void setLowExchange(String inLowExchange)
    {
        lowExchange = inLowExchange;
    }
    /**
     * Get the closeExchange value.
     *
     * @return a <code>String</code> value
     */
    public String getCloseExchange()
    {
        return closeExchange;
    }
    /**
     * Sets the closeExchange value.
     *
     * @param inCloseExchange a <code>String</code> value
     */
    public void setCloseExchange(String inCloseExchange)
    {
        closeExchange = inCloseExchange;
    }
    /**
     * Get the instrument value.
     *
     * @return an <code>Instrument</code> value
     */
    public Instrument getInstrument()
    {
        return instrument;
    }
    /**
     * Gets the instrument value as a <code>String</code>.
     *
     * @return a <code>String</code> value or <code>null</code>
     */
    public String getInstrumentAsString()
    {
        if(instrument == null) {
            return null;
        }
        return instrument.getSymbol();
    }
    /**
     * Set the instrument value.
     *
     * @param inInstrument an <code>Instrument</code> value
     */
    public void setInstrument(Instrument inInstrument)
    {
        instrument = inInstrument;
    }
    /**
     * Performs validation of the attributes.
     *
     * <p>Subclasses should override this method to validate
     * their attributes and invoke the parent method.
     * @throws IllegalArgumentException if <code>MessageId</code> &lt; 0
     * @throws IllegalArgumentException if <code>Timestamp</code> is <code>null</code>
     * @throws IllegalArgumentException if <code>Instrument</code> is <code>null</code>
     */
    @Override
    public void validate()
    {
        super.validate();
        if(instrument == null) {
            EventServices.error(VALIDATION_NULL_INSTRUMENT);
        }
    }
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((closeDate == null) ? 0 : closeDate.hashCode());
        result = prime * result + ((closeExchange == null) ? 0 : closeExchange.hashCode());
        result = prime * result + ((closePrice == null) ? 0 : closePrice.hashCode());
        result = prime * result + ((highExchange == null) ? 0 : highExchange.hashCode());
        result = prime * result + ((highPrice == null) ? 0 : highPrice.hashCode());
        result = prime * result + ((instrument == null) ? 0 : instrument.hashCode());
        result = prime * result + ((lowExchange == null) ? 0 : lowExchange.hashCode());
        result = prime * result + ((lowPrice == null) ? 0 : lowPrice.hashCode());
        result = prime * result + ((openExchange == null) ? 0 : openExchange.hashCode());
        result = prime * result + ((openPrice == null) ? 0 : openPrice.hashCode());
        result = prime * result + ((previousCloseDate == null) ? 0 : previousCloseDate.hashCode());
        result = prime * result + ((previousClosePrice == null) ? 0 : previousClosePrice.hashCode());
        result = prime * result + ((tradeHighTime == null) ? 0 : tradeHighTime.hashCode());
        result = prime * result + ((tradeLowTime == null) ? 0 : tradeLowTime.hashCode());
        result = prime * result + ((volume == null) ? 0 : volume.hashCode());
        return result;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (!(obj instanceof MarketstatBean)) {
            return false;
        }
        MarketstatBean other = (MarketstatBean) obj;
        if (closeDate == null) {
            if (other.closeDate != null) {
                return false;
            }
        } else if (!closeDate.equals(other.closeDate)) {
            return false;
        }
        if (closeExchange == null) {
            if (other.closeExchange != null) {
                return false;
            }
        } else if (!closeExchange.equals(other.closeExchange)) {
            return false;
        }
        if (closePrice == null) {
            if (other.closePrice != null) {
                return false;
            }
        } else if (!closePrice.equals(other.closePrice)) {
            return false;
        }
        if (highExchange == null) {
            if (other.highExchange != null) {
                return false;
            }
        } else if (!highExchange.equals(other.highExchange)) {
            return false;
        }
        if (highPrice == null) {
            if (other.highPrice != null) {
                return false;
            }
        } else if (!highPrice.equals(other.highPrice)) {
            return false;
        }
        if (instrument == null) {
            if (other.instrument != null) {
                return false;
            }
        } else if (!instrument.equals(other.instrument)) {
            return false;
        }
        if (lowExchange == null) {
            if (other.lowExchange != null) {
                return false;
            }
        } else if (!lowExchange.equals(other.lowExchange)) {
            return false;
        }
        if (lowPrice == null) {
            if (other.lowPrice != null) {
                return false;
            }
        } else if (!lowPrice.equals(other.lowPrice)) {
            return false;
        }
        if (openExchange == null) {
            if (other.openExchange != null) {
                return false;
            }
        } else if (!openExchange.equals(other.openExchange)) {
            return false;
        }
        if (openPrice == null) {
            if (other.openPrice != null) {
                return false;
            }
        } else if (!openPrice.equals(other.openPrice)) {
            return false;
        }
        if (previousCloseDate == null) {
            if (other.previousCloseDate != null) {
                return false;
            }
        } else if (!previousCloseDate.equals(other.previousCloseDate)) {
            return false;
        }
        if (previousClosePrice == null) {
            if (other.previousClosePrice != null) {
                return false;
            }
        } else if (!previousClosePrice.equals(other.previousClosePrice)) {
            return false;
        }
        if (tradeHighTime == null) {
            if (other.tradeHighTime != null) {
                return false;
            }
        } else if (!tradeHighTime.equals(other.tradeHighTime)) {
            return false;
        }
        if (tradeLowTime == null) {
            if (other.tradeLowTime != null) {
                return false;
            }
        } else if (!tradeLowTime.equals(other.tradeLowTime)) {
            return false;
        }
        if (volume == null) {
            if (other.volume != null) {
                return false;
            }
        } else if (!volume.equals(other.volume)) {
            return false;
        }
        return true;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return String.format("Marketstat: [closeDate=%s, closeExchange=%s, closePrice=%s, highExchange=%s, highPrice=%s, instrument=%s, lowExchange=%s, lowPrice=%s, openExchange=%s, openPrice=%s, previousCloseDate=%s, previousClosePrice=%s, tradeHighTime=%s, tradeLowTime=%s, volume=%s [%s with source %s at %s]]", //$NON-NLS-1$
                             closeDate,
                             closeExchange,
                             closePrice,
                             highExchange,
                             highPrice,
                             instrument,
                             lowExchange,
                             lowPrice,
                             openExchange,
                             openPrice,
                             previousCloseDate,
                             previousClosePrice,
                             tradeHighTime,
                             tradeLowTime,
                             volume,
                             getMessageId(),
                             getSource(),
                             getTimestamp());
    }
    /**
     * Copies all member attributes from the donor to the recipient.
     *
     * @param inDonor a <code>MarketstatBean</code> value
     * @param inRecipient a <code>MarketstatBean</code> value
     */
    protected static void copyAttributes(MarketstatBean inDonor,
                                         MarketstatBean inRecipient)
    {
        EventBean.copyAttributes(inDonor,
                                 inRecipient);
        inRecipient.setClose(inDonor.getClose());
        inRecipient.setCloseDate(inDonor.getCloseDate());
        inRecipient.setCloseExchange(inDonor.getCloseExchange());
        inRecipient.setHigh(inDonor.getHigh());
        inRecipient.setHighExchange(inDonor.getHighExchange());
        inRecipient.setInstrument(inDonor.getInstrument());
        inRecipient.setLow(inDonor.getLow());
        inRecipient.setLowExchange(inDonor.getLowExchange());
        inRecipient.setOpen(inDonor.getOpen());
        inRecipient.setOpenExchange(inDonor.getOpenExchange());
        inRecipient.setPreviousClose(inDonor.getPreviousClose());
        inRecipient.setPreviousCloseDate(inDonor.getPreviousCloseDate());
        inRecipient.setTradeHighTime(inDonor.getTradeHighTime());
        inRecipient.setTradeLowTime(inDonor.getTradeLowTime());
        inRecipient.setVolume(inDonor.getVolume());
    }
    /**
     * the open price for the current or most recent session
     */
    private BigDecimal openPrice;
    /**
     * the high price for the current or most recent session
     */
    private BigDecimal highPrice;
    /**
     * the low price for the current or most recent session
     */
    private BigDecimal lowPrice;
    /**
     * the close price for the current or most recent session
     */
    private BigDecimal closePrice;
    /**
     * the close price from the previous session
     */
    private BigDecimal previousClosePrice;
    /**
     * the cumulative volume for the current or most recent session
     */
    private BigDecimal volume;
    /**
     * the market close date - format is dependent on the market data provider
     */
    private String closeDate;
    /**
     * the market previous close date - format is dependent on the market data provider
     */
    private String previousCloseDate;
    /**
     * the time of the high trade for the current or most recent session - format is dependent on the market data provider 
     */
    private String tradeHighTime;
    /**
     * the time of the low trade for the current or most recent session - format is dependent on the market data provider 
     */
    private String tradeLowTime;
    /**
     * the exchange for which the open price was reported 
     */
    private String openExchange;
    /**
     * the exchange for which the high price was reported 
     */
    private String highExchange;
    /**
     * the exchange for which the low price was reported 
     */
    private String lowExchange;
    /**
     * the exchange for which the close price was reported 
     */
    private String closeExchange;
    /**
     * the instrument
     */
    private Instrument instrument;
    private static final long serialVersionUID = 1L;
}
