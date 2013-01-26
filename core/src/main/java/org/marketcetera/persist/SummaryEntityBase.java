package org.marketcetera.persist;

import java.io.Serializable;
import java.util.Date;

import org.marketcetera.core.ClassVersion;

/* $License$ */

/**
 * Provides a read-only view of a system object.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public interface SummaryEntityBase
        extends Serializable
{
    /**
     * Gets the ID value.
     *
     * @return a <code>long</code> value
     */
    public long getId();
    /**
     * Gets the object version value.
     * 
     * @return an <code>int</code> value
     */
    public int getVersion();
    /**
     * Gets the updated value.
     * 
     * <p>Indicates the last time this value was updated.
     *
     * @return a <code>Date</code> value
     */
    public Date getUpdated();
    /**
     * Gets the created value.
     * 
     * <p>Indicates the time this object was created.
     *
     * @return a <code>Date</code> value
     */
    public Date getCreated();
}
