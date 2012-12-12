package org.marketcetera.marketdata.webservices;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.marketcetera.core.event.Event;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@XmlRootElement(name="event")
@XmlAccessorType(XmlAccessType.NONE)
public class WebServicesEvent
        implements Event
{
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.TimestampCarrier#getTimeMillis()
     */
    @Override
    public long getTimeMillis()
    {
        throw new UnsupportedOperationException(); // TODO
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.Event#getMessageId()
     */
    @Override
    public long getMessageId()
    {
        throw new UnsupportedOperationException(); // TODO
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.Event#getTimestamp()
     */
    @Override
    public Date getTimestamp()
    {
        throw new UnsupportedOperationException(); // TODO
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.Event#getSource()
     */
    @Override
    public Object getSource()
    {
        throw new UnsupportedOperationException(); // TODO
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.Event#setSource(java.lang.Object)
     */
    @Override
    public void setSource(Object inSource)
    {
        throw new UnsupportedOperationException(); // TODO
    }
    private static final long serialVersionUID = 1L;
}
