package org.marketcetera.marketdata.webservices;

import java.util.Date;

import javax.xml.bind.annotation.*;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.marketcetera.core.event.Event;

/* $License$ */

/**
 * Provides a web-services capable implementation of an <code>Event</code>
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@XmlRootElement(name="event")
@XmlAccessorType(XmlAccessType.NONE)
@XmlSeeAlso({ WebServicesAskEvent.class,WebServicesTradeEvent.class,WebServicesMarketstatEvent.class,WebServicesConvertibleSecurityMarketstatEvent.class,WebServicesConvertibleSecurityTradeEvent.class })
public class WebServicesEvent
        implements Event
{
    public WebServicesEvent(Event inEvent)
    {
        messageId = inEvent.getMessageId();
        source = String.valueOf(inEvent.getSource());
        timestamp = inEvent.getTimestamp();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.TimestampCarrier#getTimeMillis()
     */
    @Override
    public long getTimeMillis()
    {
        return timestamp == null ? -1 : timestamp.getTime();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.Event#getMessageId()
     */
    @Override
    public long getMessageId()
    {
        return messageId;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.Event#getTimestamp()
     */
    @Override
    public Date getTimestamp()
    {
        return timestamp;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.Event#getSource()
     */
    @Override
    public Object getSource()
    {
        return source;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.Event#setSource(java.lang.Object)
     */
    @Override
    public void setSource(Object inSource)
    {
        source = String.valueOf(inSource);
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return new ToStringBuilder(this,ToStringStyle.SHORT_PREFIX_STYLE).append(messageId).append(timestamp).append(source).toString();
    }
    /**
     * Create a new WebServicesEvent instance.
     */
    protected WebServicesEvent() {}
    /**
     * message ID value
     */
    @XmlAttribute
    private long messageId;
    /**
     * timestamp value
     */
    @XmlAttribute
    private Date timestamp;
    /**
     * source value
     */
    @XmlAttribute
    private String source;
    private static final long serialVersionUID = 1L;
}
