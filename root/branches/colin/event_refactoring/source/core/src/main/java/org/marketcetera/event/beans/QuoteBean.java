package org.marketcetera.event.beans;

import javax.annotation.concurrent.NotThreadSafe;

import org.marketcetera.event.QuoteEvent;
import org.marketcetera.event.util.EventValidationServices;
import org.marketcetera.event.util.QuoteAction;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Stores the attributes necessary for {@link QuoteEvent}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@NotThreadSafe
@ClassVersion("$Id$")
public final class QuoteBean
        extends MarketDataBean
{
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
     * Performs validation of the attributes.
     *
     * <p>Subclasses should override this method to validate
     * their attributes and invoke the parent method.
     * @throws illegalargumentexception if {@link #gettimestamp()} is <code>null</code>
     * @throws illegalargumentexception if {@link #getmessageid()} &lt; 0
     * @throws IllegalArgumentException if {@link #getInstrument()} is <code>null</code>
     * @throws IllegalArgumentException if {@link #getPrice()} is <code>null</code>
     * @throws IllegalArgumentException if {@link #getSize()} is <code>null</code>
     * @throws IllegalArgumentException if {@link #getExchange()} is <code>null</code>
     * @throws IllegalArgumentException if {@link #getExchangeTimestamp()} is <code>null</code>
     * @throws IllegalArgumentException if {@link #quoteAction} is <code>null</code>
     */
    @Override
    public void validate()
    {
        super.validate();
        if(action == null) {
            EventValidationServices.error(VALIDATION_NULL_QUOTE_ACTION);
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.beans.EventBean#setDefaults()
     */
    @Override
    public void setDefaults()
    {
        super.setDefaults();
        action = QuoteAction.ADD;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((action == null) ? 0 : action.hashCode());
        return result;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        QuoteBean other = (QuoteBean) obj;
        if (action == null) {
            if (other.action != null)
                return false;
        } else if (!action.equals(other.action))
            return false;
        return true;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("QuoteBean [action=").append(action).append(", getExchange()=").append(getExchange()) //$NON-NLS-1$ //$NON-NLS-2$
                .append(", getExchangeTimestamp()=").append(getExchangeTimestamp()).append(", getInstrument()=") //$NON-NLS-1$ //$NON-NLS-2$
                .append(getInstrument()).append(", getPrice()=").append(getPrice()).append(", getSize()=") //$NON-NLS-1$ //$NON-NLS-2$
                .append(getSize()).append(", toString()=").append(super.toString()).append(", getMessageId()=") //$NON-NLS-1$ //$NON-NLS-2$
                .append(getMessageId()).append(", getSource()=").append(getSource()).append(", getTimestamp()=") //$NON-NLS-1$ //$NON-NLS-2$
                .append(getTimestamp()).append("]"); //$NON-NLS-1$
        return builder.toString();
    }
    /**
     * the action of the quote
     */
    private QuoteAction action;
    private static final long serialVersionUID = 1L;
}
