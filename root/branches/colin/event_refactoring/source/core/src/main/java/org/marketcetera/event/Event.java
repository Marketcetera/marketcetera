package org.marketcetera.event;

import java.io.Serializable;
import java.util.Date;

import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Represents a business event at a particular point in time.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public interface Event
        extends TimestampCarrier, Serializable, Messages
{
    /**
     * Returns the unique message identifier.
     *
     * @return a <code>long</code> value
     */
    public long getMessageId();
    /**
     * Returns the time the event took place expressed as a <code>Date</code>.
     *
     * @return a <code>Date</code> value
     */
    public Date getTimestamp();
    /**
     * Get the source value.
     *
     * @return an <code>Object</code> value or null
     */
    public Object getSource();
    /**
     * Sets the source value.
     *
     * @param an <code>Object</code> value or null
     */
    public void setSource(Object inSource);
}
