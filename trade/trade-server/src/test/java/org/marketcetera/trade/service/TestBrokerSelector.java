package org.marketcetera.trade.service;

import org.marketcetera.brokers.Selector;
import org.marketcetera.trade.BrokerID;
import org.marketcetera.trade.Order;

/* $License$ */

/**
 * Provides a test {@link Selector} implementation.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class TestBrokerSelector
        implements Selector
{
    /* (non-Javadoc)
     * @see org.marketcetera.brokers.Selector#chooseBroker(org.marketcetera.trade.Order)
     */
    @Override
    public BrokerID chooseBroker(Order inOrder)
    {
        if(chooseBrokerException != null) {
            throw chooseBrokerException;
        }
        return selectedBrokerId;
    }
    /**
     * Get the selectedBrokerId value.
     *
     * @return a <code>BrokerID</code> value
     */
    public BrokerID getSelectedBrokerId()
    {
        return selectedBrokerId;
    }
    /**
     * Sets the selectedBrokerId value.
     *
     * @param inSelectedBrokerId a <code>BrokerID</code> value
     */
    public void setSelectedBrokerId(BrokerID inSelectedBrokerId)
    {
        selectedBrokerId = inSelectedBrokerId;
    }
    /**
     * Get the chooseBrokerException value.
     *
     * @return a <code>RuntimeException</code> value
     */
    public RuntimeException getChooseBrokerException()
    {
        return chooseBrokerException;
    }
    /**
     * Sets the chooseBrokerException value.
     *
     * @param inChooseBrokerException a <code>RuntimeException</code> value
     */
    public void setChooseBrokerException(RuntimeException inChooseBrokerException)
    {
        chooseBrokerException = inChooseBrokerException;
    }
    /**
     * optional broker id to return
     */
    private BrokerID selectedBrokerId;
    /**
     * optional exception to throw during selection
     */
    private RuntimeException chooseBrokerException;
}
