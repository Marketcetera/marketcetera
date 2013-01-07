package org.marketcetera.core;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.marketcetera.persist.EntityBase;

/* $License$ */

/**
 * Provides access to a persistent ID range.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Entity
@Table(name="id_repository")
@ClassVersion("$Id: SimpleUser.java 16154 2012-07-14 16:34:05Z colin $")
public class IDRepository
        extends EntityBase
{
    /**
     * Get the nextAllowedId value.
     *
     * @return a <code>long</code> value
     */
    @Column(nullable=false,name="nextAllowedID")
    public long getNextAllowedId()
    {
        return nextAllowedId;
    }
    /**
     * Sets the nextAllowedId value.
     *
     * @param inNextAllowedId a <code>long</code> value
     */
    public void setNextAllowedId(long inNextAllowedId)
    {
        nextAllowedId = inNextAllowedId;
    }
    /**
     * the next allowedId value
     */
    private long nextAllowedId;
    private static final long serialVersionUID = 1L;
}
