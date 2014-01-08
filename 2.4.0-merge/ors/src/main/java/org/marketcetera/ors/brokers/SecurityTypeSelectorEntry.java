package org.marketcetera.ors.brokers;

import org.apache.commons.lang.Validate;
import org.marketcetera.trade.Order;
import org.marketcetera.trade.SecurityType;
import org.springframework.beans.factory.InitializingBean;

/* $License$ */

/**
 * Selects a target broker by {@link SecurityType}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class SecurityTypeSelectorEntry
        implements SpringSelectorEntry, InitializingBean
{
    /* (non-Javadoc)
     * @see org.marketcetera.ors.brokers.SpringSelectorEntry#routeToBroker(org.marketcetera.trade.Order)
     */
    @Override
    public boolean routeToBroker(Order inOrder)
    {
        SecurityType orderType = inOrder.getSecurityType();
        if((orderType != null) && orderType != SecurityType.Unknown) {
            if(targetType.equals(orderType)) {
                return true;
            }
        }
        return false;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.ors.brokers.SpringSelectorEntry#setBroker(org.marketcetera.ors.brokers.SpringBroker)
     */
    @Override
    public void setBroker(SpringBroker inBroker)
    {
        broker = inBroker;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.ors.brokers.SpringSelectorEntry#getBroker()
     */
    @Override
    public SpringBroker getBroker()
    {
        return broker;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.ors.brokers.SpringSelectorEntry#setSkipIfUnavailable(boolean)
     */
    @Override
    public void setSkipIfUnavailable(boolean inSkipIfUnavailable)
    {
        skipIfUnavailable = inSkipIfUnavailable;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.ors.brokers.SpringSelectorEntry#getSkipIfUnavailable()
     */
    @Override
    public boolean getSkipIfUnavailable()
    {
        return skipIfUnavailable;
    }
    /* (non-Javadoc)
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    @Override
    public void afterPropertiesSet()
            throws Exception
    {
        Validate.notNull(targetType);
        Validate.notNull(broker);
    }
    /**
     * Sets the targetType value.
     *
     * @param inTargetType a <code>String</code> value
     */
    public void setTargetType(String inTargetType)
    {
        targetType = SecurityType.getInstanceForFIXValue(inTargetType);
    }
    /**
     * target broker
     */
    private SpringBroker broker;
    /**
     * indicates if this routing should be skipped if unavailable
     */
    private boolean skipIfUnavailable = true;
    /**
     * target security type
     */
    private SecurityType targetType;
}
