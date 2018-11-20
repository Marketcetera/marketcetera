package org.marketcetera.trade.dao;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.marketcetera.persist.EntityBase;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Supplies a persistent implementation of the next id in a series.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: PersistentDatabaseID.java 16522 2014-12-31 16:33:08Z colin $
 * @since 2.4.2
 */
@Entity
@Table(name="id_repository")
@NamedQuery(name="getNextId",query="select e from PersistentDatabaseID e")
@ClassVersion("$Id: PersistentDatabaseID.java 16522 2014-12-31 16:33:08Z colin $")
public class PersistentDatabaseID
        extends EntityBase
{
    /**
     * Get the nextAllowedId value.
     *
     * @return a <code>long</code> value
     */
    public long getNextAllowedId()
    {
        return nextAllowedId;
    }
    /**
     * Sets the nextAllowedId value.
     *
     * @param a <code>long</code> value
     */
    public void setNextAllowedId(long inNextAllowedId)
    {
        nextAllowedId = inNextAllowedId;
    }
    /**
     * the next allowed id
     */
    @Column(name="next_id")
    private long nextAllowedId;
    private static final long serialVersionUID = -3056482782155189377L;
}
