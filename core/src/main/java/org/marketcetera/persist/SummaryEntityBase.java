package org.marketcetera.persist;

import java.io.Serializable;

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
     * Gets the object version value.
     * 
     * @return an <code>int</code> value
     */
    public int getVersion();
}
