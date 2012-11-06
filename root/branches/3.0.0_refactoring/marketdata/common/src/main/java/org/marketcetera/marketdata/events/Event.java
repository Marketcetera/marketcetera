package org.marketcetera.marketdata.events;

import java.io.Serializable;
import java.util.Date;

import org.marketcetera.api.systemmodel.TimestampCarrier;

/* $License$ */

/**
 * Represents a business event at a particular point in time.
 * 
 * <p>Implements must adhere to the following contract:
 * <ol>
 *   <li>Every event must have a <code>MessageId</code> unique from all other events within the same process.  Note that
 *       an event may have the same <code>MessageId</code> as another if the second event is an edit to the first.  For
 *       example, a <code>QuoteEvent</code> may have a <code>QuoteAction</code> of <code>CHANGE</code> or <code>DELETE</code>
 *       which would require the <code>MessageId</code> to match a previous <code>MessageId</code>.</li>
 *   <li>Event equality must be determined using the <code>MessageId</code>
 *   <li>Events must be {@link Serializable}</li>
 *   <li>The <code>Timestamp</code> should be the time when the <code>Event</code> was created, not necessarily
 *       the time when the thing the event is reporting happened</li>
 *   <li>Events should make every effort to be thread-safe but this isn't specifically mandated by this contract</li>
 * </ol>
 *
 * @version $Id: Event.java 16323 2012-10-25 17:35:43Z colin $
 * @since 2.0.0
 */
public interface Event
        extends TimestampCarrier, Serializable
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
     * @return an <code>Object</code> value or <code>null</code>
     */
    public Object getSource();
}
