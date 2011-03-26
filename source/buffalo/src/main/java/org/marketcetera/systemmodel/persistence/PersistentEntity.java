package org.marketcetera.systemmodel.persistence;

import java.util.Date;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface PersistentEntity
{
    /**
     * 
     *
     *
     * @return
     */
    public long getID();
    /**
     * 
     *
     *
     * @return
     */
    public Date getLastUpdated();
    /**
     * 
     *
     *
     * @return
     */
    public int getUpdateCount();
}
