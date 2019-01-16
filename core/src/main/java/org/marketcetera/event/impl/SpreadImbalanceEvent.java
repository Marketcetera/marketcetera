package org.marketcetera.event.impl;

import javax.annotation.concurrent.ThreadSafe;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.marketcetera.event.SpreadEvent;
import org.marketcetera.event.beans.SpreadBean;
import org.marketcetera.event.beans.ImbalanceBean;
import org.marketcetera.trade.*;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Provides an <code>ImbalanceEvent</code> implementation for a <code>Spread</code> instrument.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: SpreadImbalanceEvent.java 16901 2014-05-11 16:14:11Z colin $
 * @since $Release$
 */
@ThreadSafe
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name="SpreadImbalance")
@ClassVersion("$Id: SpreadImbalanceEvent.java 16901 2014-05-11 16:14:11Z colin $")
public class SpreadImbalanceEvent
        extends AbstractImbalanceEvent
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
     * Create a new SpreadImbalanceEvent instance.
     *
     * @param inImbalanceBean a <code>ImbalanceBean</code> value
     * @throws IllegalArgumentException if <code>MessageId</code> &lt; 0
     * @throws IllegalArgumentException if <code>Timestamp</code> is <code>null</code>
     * @throws IllegalArgumentException if <code>Instrument</code> is <code>null</code>
     */
    SpreadImbalanceEvent(ImbalanceBean inImbalance,
                         SpreadBean inSpread)
    {
        super(inImbalance);
        spread = inSpread;
        spread.validate();
    }
    /**
     * Create a new SpreadImbalanceEvent instance.
     * 
     * <p>This constructor is intended to be used by JAXB.
     */
    @SuppressWarnings("unused")
    private SpreadImbalanceEvent()
    {
        spread = new SpreadBean();
    }
    /**
     * the Spread attributes 
     */
    @XmlElement
    private final SpreadBean spread;
    private static final long serialVersionUID = 7309976472991744496L;
}
