package org.marketcetera.event.beans;

import java.math.BigDecimal;
import java.util.Date;

import javax.annotation.concurrent.NotThreadSafe;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.marketcetera.event.Messages;
import org.marketcetera.event.QuoteAction;
import org.marketcetera.event.QuoteEvent;
import org.marketcetera.event.util.EventServices;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Stores the attributes necessary for {@link QuoteEvent}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 2.0.0
 */
@NotThreadSafe
@XmlRootElement(name="quote")
@XmlAccessorType(XmlAccessType.NONE)
@ClassVersion("$Id$")
public final class QuoteBean
        extends MarketDataBean
{
    /**
     * Creates a shallow copy of the given <code>QuoteBean</code>.
     *
     * @param inBean a <code>QuoteBean</code> value
     * @return a <code>QuoteBean</code> value
     */
    public static QuoteBean copy(QuoteBean inBean)
    {
        QuoteBean newBean = new QuoteBean();
        copyAttributes(inBean,
                       newBean);
        return newBean;
    }
    /**
     * Builds a <code>QuoteBean</code> based on the values of
     * the given event and attributes.
     *
     * @param inQuoteEvent a <code>QuoteEvent</code> value
     * @param inTimestamp a <code>Date</code> value to use instead of the timestamp from the quote
     * @param inSize a <code>BigDecimal</code> value to use instead of the size from the quote
     * @param inQuoteAction a <code>QuoteAction</code> value to use instead of the action from the quote
     * @return a <code>QuoteBean</code> value
     */
    public static QuoteBean getQuoteBeanFromEvent(QuoteEvent inQuoteEvent,
                                                  Date inTimestamp,
                                                  BigDecimal inSize,
                                                  QuoteAction inQuoteAction)
    {
        if(inTimestamp == null) {
            throw new NullPointerException();
        }
        if(inSize == null) {
            throw new NullPointerException();
        }
        if(inQuoteAction == null) {
            throw new NullPointerException();
        }
        QuoteBean quote = new QuoteBean();
        quote.setMessageId(inQuoteEvent.getMessageId());
        quote.setTimestamp(inTimestamp);
        quote.setInstrument(inQuoteEvent.getInstrument());
        quote.setExchange(inQuoteEvent.getExchange());
        quote.setPrice(inQuoteEvent.getPrice());
        quote.setProcessedTimestamp(inQuoteEvent.getProcessedTimestamp());
        quote.setReceivedTimestamp(inQuoteEvent.getReceivedTimestamp());
        quote.setSize(inSize);
        quote.setExchangeTimestamp(inQuoteEvent.getExchangeTimestamp());
        quote.setAction(inQuoteAction);
        quote.setSource(inQuoteEvent.getSource());
        quote.setEventType(inQuoteEvent.getEventType());
        return quote;
    }
    /**
     * Builds a <code>QuoteBean</code> based on the values of
     * the given event.
     *
     * @param inQuoteEvent a <code>QuoteEvent</code> value
     * @param inQuoteAction a <code>QuoteAction</code> value to use instead of the action from the quote
     * @return a <code>QuoteBean</code> value
     */
    public static QuoteBean getQuoteBeanFromEvent(QuoteEvent inQuoteEvent,
                                                  QuoteAction inQuoteAction)
    {
        return getQuoteBeanFromEvent(inQuoteEvent,
                                     inQuoteEvent.getTimestamp(),
                                     inQuoteEvent.getSize(),
                                     inQuoteAction);
    }
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
     * @param inAction a <code>QuoteAction</code> value
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
     * @throws IllegalArgumentException if <code>MessageId</code> &lt; 0
     * @throws IllegalArgumentException if <code>Timestamp</code> is <code>null</code>
     * @throws IllegalArgumentException if <code>Instrument</code> is <code>null</code>
     * @throws IllegalArgumentException if <code>Price</code> is <code>null</code>
     * @throws IllegalArgumentException if <code>Size</code> is <code>null</code>
     * @throws IllegalArgumentException if <code>Exchange</code> is <code>null</code> or empty
     * @throws IllegalArgumentException if <code>ExchangeTimestamp</code> is <code>null</code> or empty
     * @throws IllegalArgumentException if <code>Action</code> is <code>null</code>
     */
    @Override
    public void validate()
    {
        super.validate();
        if(action == null) {
            EventServices.error(Messages.VALIDATION_NULL_QUOTE_ACTION);
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.beans.EventBean#setDefaults()
     */
    @Override
    public void setDefaults()
    {
        super.setDefaults();
        if(action == null) {
            action = QuoteAction.ADD;
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
        result = prime * result + ((action == null) ? 0 : action.hashCode());
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
        if (!(obj instanceof QuoteBean)) {
            return false;
        }
        QuoteBean other = (QuoteBean) obj;
        if (action == null) {
            if (other.action != null) {
                return false;
            }
        } else if (!action.equals(other.action)) {
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
        return String.format("Quote: [action=%s, getExchange()=%s, getExchangeTimestamp()=%s, getInstrument()=%s, getPrice()=%s, getSize()=%s [%s with source %s at %s]]", //$NON-NLS-1$
                             action,
                             getExchange(),
                             getExchangeTimestamp(),
                             getInstrument(),
                             getPrice(),
                             getSize(),
                             getMessageId(),
                             getSource(),
                             getTimestamp());
    }
    /**
     * Copies all member attributes from the donor to the recipient.
     *
     * @param inDonor a <code>QuoteBean</code> value
     * @param inRecipient a <code>QuoteBean</code> value
     */
    protected static void copyAttributes(QuoteBean inDonor,
                                         QuoteBean inRecipient)
    {
        MarketDataBean.copyAttributes(inDonor,
                                      inRecipient);
        inRecipient.setAction(inDonor.getAction());
    }
    /**
     * the action of the quote
     */
    @XmlAttribute(name="action")
    private QuoteAction action;
    private static final long serialVersionUID = 1L;
}
