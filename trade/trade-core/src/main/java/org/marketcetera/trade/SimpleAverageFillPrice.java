package org.marketcetera.trade;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/* $License$ */

/**
 * Provides a POJO {@link AverageFillPrice} implementation.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name="AverageFillPrice")
public class SimpleAverageFillPrice
        implements AverageFillPrice
{
    /* (non-Javadoc)
     * @see org.marketcetera.event.HasInstrument#getInstrument()
     */
    @Override
    public Instrument getInstrument()
    {
        return instrument;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.HasInstrument#getInstrumentAsString()
     */
    @Override
    public String getInstrumentAsString()
    {
        return instrument.getFullSymbol();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.HasSide#getSide()
     */
    @Override
    public Side getSide()
    {
        return side;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.AverageFillPrice#getCumulativeQuantity()
     */
    @Override
    public BigDecimal getCumulativeQuantity()
    {
        return cumulativeQuantity;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.AverageFillPrice#getAveragePrice()
     */
    @Override
    public BigDecimal getAveragePrice()
    {
        return averagePrice;
    }
    /**
     * Sets the instrument value.
     *
     * @param inInstrument an <code>Instrument</code> value
     */
    public void setInstrument(Instrument inInstrument)
    {
        instrument = inInstrument;
    }
    /**
     * Sets the cumulativeQuantity value.
     *
     * @param inCumulativeQuantity a <code>BigDecimal</code> value
     */
    public void setCumulativeQuantity(BigDecimal inCumulativeQuantity)
    {
        cumulativeQuantity = inCumulativeQuantity;
    }
    /**
     * Sets the averagePrice value.
     *
     * @param inAveragePrice a <code>BigDecimal</code> value
     */
    public void setAveragePrice(BigDecimal inAveragePrice)
    {
        averagePrice = inAveragePrice;
    }
    /**
     * Sets the side value.
     *
     * @param inSide a <code>Side</code> value
     */
    public void setSide(Side inSide)
    {
        side = inSide;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("SimpleAverageFillPrice [instrument=").append(instrument).append(", cumulativeQuantity=")
                .append(cumulativeQuantity).append(", averagePrice=").append(averagePrice).append(", side=")
                .append(side).append("]");
        return builder.toString();
    }
    /**
     * Create a new SimpleAverageFillPrice instance.
     *
     * @param inInstrument an <code>Instrument</code> value
     * @param inSide a <code>Side</code> value
     * @param inCumulativeQuantity a <code>BigDecimal</code> value
     * @param inAveragePrice a <code>BigDecimal</code> value
     */
    public SimpleAverageFillPrice(Instrument inInstrument,
                                  Side inSide,
                                  BigDecimal inCumulativeQuantity,
                                  BigDecimal inAveragePrice)
    {
        instrument = inInstrument;
        side = inSide;
        cumulativeQuantity = inCumulativeQuantity;
        averagePrice = inAveragePrice;
    }
    /**
     * Create a new SimpleAverageFillPrice instance.
     */
    public SimpleAverageFillPrice() {}
    /**
     * instrument value
     */
    @XmlElement
    private Instrument instrument;
    /**
     * cumulative quantity value
     */
    @XmlAttribute
    private BigDecimal cumulativeQuantity;
    /**
     * average price value
     */
    @XmlAttribute
    private BigDecimal averagePrice;
    /**
     * side value
     */
    @XmlAttribute
    private Side side;
}
