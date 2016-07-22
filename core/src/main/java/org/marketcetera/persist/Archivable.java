package org.marketcetera.persist;

/* $License$ */

/**
 * Indicates if an object has been archived and should be excluded.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface Archivable
{
    /**
     * Indicate if an object has been archived.
     *
     * @return a <code>boolean</code> value
     */
    boolean isArchived();
}
