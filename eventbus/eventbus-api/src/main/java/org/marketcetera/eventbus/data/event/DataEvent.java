//
// this file is automatically generated
//
package org.marketcetera.eventbus.data.event;

import org.marketcetera.core.Preserve;

/* $License$ */

/**
 * indicates that a data object has been added, changed, or deleted.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Preserve
public interface DataEvent
{
    /**
     * Get the id value.
     *
     * @return a <code>long</code> value
     */
    long getId();
    /**
     * Set the id value.
     *
     * @param inId a <code>long</code> value
     */
    void setId(long inId);
    /**
     * Get the timestamp value.
     *
     * @return a <code>java.util.Date</code> value
     */
    java.util.Date getTimestamp();
    /**
     * Set the timestamp value.
     *
     * @param inTimestamp a <code>java.util.Date</code> value
     */
    void setTimestamp(java.util.Date inTimestamp);
    /**
     * Get the type value.
     *
     * @return a <code>java.lang.Class&lt;?&gt;</code> value
     */
    java.lang.Class<?> getType();
    /**
     * Get the changeType value.
     *
     * @return a <code>org.marketcetera.eventbus.data.event.DataEventChangeType</code> value
     */
    org.marketcetera.eventbus.data.event.DataEventChangeType getChangeType();
    /**
     * Set the changeType value.
     *
     * @param inChangeType a <code>org.marketcetera.eventbus.data.event.DataEventChangeType</code> value
     */
    void setChangeType(org.marketcetera.eventbus.data.event.DataEventChangeType inChangeType);
}
