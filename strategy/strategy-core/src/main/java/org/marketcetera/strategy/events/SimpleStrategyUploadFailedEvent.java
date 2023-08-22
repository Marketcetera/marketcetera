//
// this file is automatically generated
//
package org.marketcetera.strategy.events;

import org.marketcetera.core.Preserve;
import org.marketcetera.strategy.StrategyInstance;

/* $License$ */

/**
 * Indicates that a strategy upload failed.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Preserve
public class SimpleStrategyUploadFailedEvent
        implements StrategyUploadFailedEvent
{
    /**
     * Create a new SimpleStrategyUploadFailedEvent instance.
     */
    public SimpleStrategyUploadFailedEvent() {}
    /**
     * Create a new SimpleStrategyUploadFailedEvent instance.
     *
     * @param inStrategyInstance a <code>StrategyInstance</code> value
     * @param inMessage a <code>String</code> value
     */
    public SimpleStrategyUploadFailedEvent(StrategyInstance inStrategyInstance,
                                           String inMessage)
    {
        strategyInstance = inStrategyInstance;
        errorMessage = inMessage;
    }
    /**
     * Get the strategyInstance value.
     *
     * @return an <code>org.marketcetera.strategy.StrategyInstance</code> value
     */
    @Override
    public org.marketcetera.strategy.StrategyInstance getStrategyInstance()
    {
        return strategyInstance;
    }
    /**
     * Set the strategyInstance value.
     *
     * @param inStrategyInstance an <code>org.marketcetera.strategy.StrategyInstance</code> value
     */
    public void setStrategyInstance(org.marketcetera.strategy.StrategyInstance inStrategyInstance)
    {
        strategyInstance = inStrategyInstance;
    }
    /**
     * Get the errorMessage value.
     *
     * @return a <code>String</code> value
     */
    @Override
    public String getErrorMessage()
    {
        return errorMessage;
    }
    /**
     * Set the errorMessage value.
     *
     * @param inErrorMessage a <code>String</code> value
     */
    public void setErrorMessage(String inErrorMessage)
    {
        errorMessage = org.apache.commons.lang.StringUtils.trimToNull(inErrorMessage);
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("StrategyUploadFailedEvent [")
            .append("strategyInstance=").append(strategyInstance)
            .append(", errorMessage=").append(errorMessage).append("]");
        return builder.toString();
    }
    /**
     * strategy instance which was successfully uploaded
     */
    private org.marketcetera.strategy.StrategyInstance strategyInstance;
    /**
     * describes the error that occurred
     */
    private String errorMessage;
}
