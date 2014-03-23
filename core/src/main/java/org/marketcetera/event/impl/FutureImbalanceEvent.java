package org.marketcetera.event.impl;

import javax.annotation.concurrent.ThreadSafe;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.marketcetera.event.FutureEvent;
import org.marketcetera.event.beans.FutureBean;
import org.marketcetera.event.beans.ImbalanceBean;
import org.marketcetera.trade.*;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Provides an <code>ImbalanceEvent</code> implementation for a <code>Future</code> instrument.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ThreadSafe
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name="futureImbalance")
@ClassVersion("$Id$")
public class FutureImbalanceEvent
        extends AbstractImbalanceEvent
        implements FutureEvent
{
    /* (non-Javadoc)
     * @see org.marketcetera.event.FutureEvent#getDeliveryType()
     */
    @Override
    public DeliveryType getDeliveryType()
    {
        return future.getDeliveryType();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.FutureEvent#getProviderSymbol()
     */
    @Override
    public String getProviderSymbol()
    {
        return future.getProviderSymbol();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.FutureEvent#getStandardType()
     */
    @Override
    public StandardType getStandardType()
    {
        return future.getStandardType();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.FutureEvent#getType()
     */
    @Override
    public FutureType getType()
    {
        return future.getType();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.FutureEvent#getUnderylingAssetType()
     */
    @Override
    public FutureUnderlyingAssetType getUnderylingAssetType()
    {
        return future.getUnderlyingAssetType();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.HasFuture#getInstrument()
     */
    @Override
    public Future getInstrument()
    {
        return (Future)super.getInstrument();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.FutureEvent#getContractSize()
     */
    @Override
    public int getContractSize()
    {
        return future.getContractSize();
    }
    /**
     * Create a new FutureImbalanceEvent instance.
     *
     * @param inImbalanceBean a <code>ImbalanceBean</code> value
     * @throws IllegalArgumentException if <code>MessageId</code> &lt; 0
     * @throws IllegalArgumentException if <code>Timestamp</code> is <code>null</code>
     * @throws IllegalArgumentException if <code>Instrument</code> is <code>null</code>
     */
    FutureImbalanceEvent(ImbalanceBean inImbalance,
                         FutureBean inFuture)
    {
        super(inImbalance);
        future = inFuture;
        future.validate();
    }
    /**
     * Create a new FutureImbalanceEvent instance.
     * 
     * <p>This constructor is intended to be used by JAXB.
     */
    @SuppressWarnings("unused")
    private FutureImbalanceEvent()
    {
        future = new FutureBean();
    }
    /**
     * the future attributes 
     */
    @XmlElement
    private final FutureBean future;
    private static final long serialVersionUID = 1L;
}
