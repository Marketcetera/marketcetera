package org.marketcetera.core;

import java.util.List;

import javax.persistence.*;

import org.marketcetera.persist.EntityBase;
import org.marketcetera.persist.PersistContext;
import org.marketcetera.persist.PersistenceException;
import org.marketcetera.persist.Transaction;

/* $License$ */

/**
 * Supplies a persistent implementation of the next id in a series.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Entity
@Table(name="id_repository")
@NamedQuery(name="getNextId",query="select e from PersistentDatabaseID e")
public class PersistentDatabaseID
        extends EntityBase
{
    /**
     * Gets the next ID to allocate.
     *
     * @return a <code>PersistentDatabaseID</code> value
     * @throws PersistenceException if an error occurs retrieving the next id
     */
    static PersistentDatabaseID getPersistentID()
            throws PersistenceException
    {
        return executeRemote(new Transaction<PersistentDatabaseID>() {
            @Override
            public PersistentDatabaseID execute(EntityManager inEntityManager,
                                PersistContext inContext)
                    throws PersistenceException
            {
                Query query = inEntityManager.createNamedQuery("getNextId"); //$NON-NLS-1$
                List<?> list = query.getResultList();
                PersistentDatabaseID id = null;
                if(list.isEmpty()) {
                    id = new PersistentDatabaseID();
                    id.setNextAllowedId(1);
                } else {
                    id = (PersistentDatabaseID)list.get(0);
                }
                return id;
            }
            private static final long serialVersionUID = 1L;
        },null);
    }
    /**
     * Saves the given value.
     *
     * @param inId a <code>PersistentDatabaseID</code> value
     * @throws PersistenceException if an error occurs writing the new value to the database
     */
    static void save(PersistentDatabaseID inId)
            throws PersistenceException
    {
        inId.saveRemote(null);
    }
    /**
     * Get the nextAllowedId value.
     *
     * @return a <code>long</code> value
     */
    @Column(name="next_id")
    public long getNextAllowedId()
    {
        return nextAllowedId;
    }
    /**
     * Sets the nextAllowedId value.
     *
     * @param a <code>long</code> value
     */
    void setNextAllowedId(long inNextAllowedId)
    {
        nextAllowedId = inNextAllowedId;
    }
    /**
     * the next allowed id
     */
    private long nextAllowedId;
    private static final long serialVersionUID = -3056482782155189377L;
}
