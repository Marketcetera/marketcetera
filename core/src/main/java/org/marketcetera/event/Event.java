package org.marketcetera.event;

import java.io.Serializable;
import java.util.Date;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Represents a business event at a particular point in time.
 * 
 * <p>Implements must adhere to the following contract:
 * <ol>
 *   <li>Every event must have a <code>MessageId</code> unique from all other events within the same process.  Note that
 *       an event may have the same <code>Message</code> as another if the second event is an edit to the first.  For
 *       example, a <code>QuoteEvent</code> may have a <code>QuoteAction</code> or <code>CHANGE</code> or <code>DELETE</code>
 *       which would require the <code>MessageId</code> to match a previous <code>MessageId</code>.</li>
 *   <li>Event equality must be determined using the <code>MessageId</code>
 *   <li>Events must be {@link Serializable}</li>
 *   <li>The <code>Timestamp</code> should be the time when the <code>Event</code> was created, not necessarily
 *       the time when the thing the event is reporting happened</li>
 *   <li>Events should make every effort to be immutable and thread-safe but neither characteristic
 *       is specifically mandated by this contract</li>
 * </ol>
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 2.0.0
 */
@XmlJavaTypeAdapter(AnyTypeAdapter.class)
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
     * @param inSource an <code>Object</code> value or null
     */
    public void setSource(Object inSource);
    /**
     * Gets the provider value.
     *
     * @return a <code>String</code> value
     */
    public String getProvider();
    /**
     * Sets the provider value.
     *
     * @param inProvider a <code>String</code> value
     */
    public void setProvider(String inProvider);
}
