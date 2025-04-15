package org.marketcetera.event.impl;

import javax.xml.bind.annotation.XmlElement;

import org.marketcetera.event.BidEvent;
import org.marketcetera.event.SpreadEvent;
import org.marketcetera.event.beans.QuoteBean;
import org.marketcetera.event.beans.SpreadBean;
import org.marketcetera.trade.DeliveryType;
import org.marketcetera.trade.FutureType;
import org.marketcetera.trade.FutureUnderlyingAssetType;
import org.marketcetera.trade.Spread;
import org.marketcetera.trade.StandardType;

/* $License$ */

/**
 * Provides a spread implementation of {@link BidEvent}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class SpreadBidEventImpl
        extends AbstractQuoteEventImpl
        implements BidEvent,SpreadEvent
{
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
    /* (non-Javadoc)
     * @see org.marketcetera.event.HasProviderSymbol#getProviderSymbol()
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
    /**
     * Create a new SpreadBidEventImpl instance.
     *
     * @param inQuote a <code>QuoteBean</code> value
     * @param inSpread a <cod>SpreadBean</code> value
     */
    SpreadBidEventImpl(QuoteBean inQuote,
                       SpreadBean inSpread)
    {
        super(inQuote);
        spread = inSpread;
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
     * provides a human-readable description of this event type (does not need to be localized)
     */
    private static final String description = "Spread Bid"; //$NON-NLS-1$
    /**
     * spread attributes 
     */
    @XmlElement
    private final SpreadBean spread;
    private static final long serialVersionUID = -3631017201894429373L;
}
