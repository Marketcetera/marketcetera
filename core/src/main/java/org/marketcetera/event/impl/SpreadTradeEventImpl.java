package org.marketcetera.event.impl;

import javax.annotation.concurrent.ThreadSafe;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.marketcetera.event.SpreadEvent;
import org.marketcetera.event.TradeEvent;
import org.marketcetera.event.beans.SpreadBean;
import org.marketcetera.event.beans.TradeBean;
import org.marketcetera.trade.DeliveryType;
import org.marketcetera.trade.FutureType;
import org.marketcetera.trade.FutureUnderlyingAssetType;
import org.marketcetera.trade.Spread;
import org.marketcetera.trade.StandardType;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Provides a Spread implementation of {@link TradeEvent}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: SpreadTradeEventImpl.java 17054 2015-09-29 13:59:20Z colin $
 * @since 2.1.0
 */
@ThreadSafe
@XmlRootElement(name="SpreadTrade")
@XmlAccessorType(XmlAccessType.NONE)
@ClassVersion("$Id: SpreadTradeEventImpl.java 17054 2015-09-29 13:59:20Z colin $")
public class SpreadTradeEventImpl
        extends AbstractTradeEventImpl
        implements SpreadEvent
{
    /* (non-Javadoc)
     * @see org.marketcetera.event.SpreadEvent#getProviderSymbol()
     */
    @Override
    public String getProviderSymbol()
    {
        return spread.getProviderSymbol();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.HasSpread#getInstrument()
     */
    @Override
    public Spread getInstrument()
    {
        return (Spread)super.getInstrument();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.SpreadEvent#getLeg1Type()
     */
    @Override
    public FutureType getLeg1Type()
    {
        return spread.getLeg1Bean().getFutureType();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.SpreadEvent#getLeg1UnderylingAssetType()
     */
    @Override
    public FutureUnderlyingAssetType getLeg1UnderylingAssetType()
    {
        return spread.getLeg1Bean().getUnderlyingAssetType();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.SpreadEvent#getLeg1DeliveryType()
     */
    @Override
    public DeliveryType getLeg1DeliveryType()
    {
        return spread.getLeg1Bean().getDeliveryType();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.SpreadEvent#getLeg1StandardType()
     */
    @Override
    public StandardType getLeg1StandardType()
    {
        return spread.getLeg1Bean().getStandardType();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.SpreadEvent#getLeg1ContractSize()
     */
    @Override
    public int getLeg1ContractSize()
    {
        return spread.getLeg1Bean().getContractSize();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.SpreadEvent#getLeg2Type()
     */
    @Override
    public FutureType getLeg2Type()
    {
        return spread.getLeg2Bean().getFutureType();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.SpreadEvent#getLeg2UnderylingAssetType()
     */
    @Override
    public FutureUnderlyingAssetType getLeg2UnderylingAssetType()
    {
        return spread.getLeg2Bean().getUnderlyingAssetType();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.SpreadEvent#getLeg2DeliveryType()
     */
    @Override
    public DeliveryType getLeg2DeliveryType()
    {
        return spread.getLeg2Bean().getDeliveryType();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.SpreadEvent#getLeg2StandardType()
     */
    @Override
    public StandardType getLeg2StandardType()
    {
        return spread.getLeg2Bean().getStandardType();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.SpreadEvent#getLeg2ContractSize()
     */
    @Override
    public int getLeg2ContractSize()
    {
        return spread.getLeg2Bean().getContractSize();
    }
    /**
     * Create a new SpreadTradeEventImpl instance.
     *
     * @param inTradeData a <code>TradeBean</code> value
     * @param inSpread a <code>SpreadBean</code> value
     * @throws IllegalArgumentException if <code>MessageId</code> &lt; 0
     * @throws IllegalArgumentException if <code>Timestamp</code> is <code>null</code>
     * @throws IllegalArgumentException if <code>Instrument</code> is <code>null</code>
     * @throws IllegalArgumentException if <code>Price</code> is <code>null</code>
     * @throws IllegalArgumentException if <code>Size</code> is <code>null</code>
     * @throws IllegalArgumentException if <code>Exchange</code> is <code>null</code> or empty
     * @throws IllegalArgumentException if <code>ExchangeTimestamp</code> is <code>null</code> or empty
     */
    SpreadTradeEventImpl(TradeBean inTradeData,
                         SpreadBean inSpread)
    {
        super(inTradeData);
        spread = inSpread;
        spread.validate();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.impl.AbstractQuoteEventImpl#getDescription()
     */
    @Override
    protected String getDescription()
    {
        return description;
    }
    /**
     * Create a new SpreadTradeEventImpl instance.
     * 
     * <p>This constructor is intended to be used by JAXB.
     */
    @SuppressWarnings("unused")
    private SpreadTradeEventImpl()
    {
        spread = new SpreadBean();
    }
    /**
     * provides a human-readable description of this event type (does not need to be localized)
     */
    private static final String description = "Spread Trade"; //$NON-NLS-1$
    /**
     * the Spread attributes 
     */
    @XmlElement
    private final SpreadBean spread;
    private static final long serialVersionUID = -6937392594904661902L;
}
