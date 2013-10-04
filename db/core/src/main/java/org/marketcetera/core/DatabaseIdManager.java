package org.marketcetera.core;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/* $License$ */

/**
 * Provides business-level services for database-backed ids.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class DatabaseIdManager
{
    /**
     * 
     *
     *
     * @param inBlockSize
     * @return
     */
    @Transactional(readOnly=false)
    public PersistentDatabaseID allocateIdBlock(long inBlockSize)
    {
        Iterable<PersistentDatabaseID> idIterator = databaseIdDao.findAll();
        PersistentDatabaseID id;
        if(idIterator.iterator().hasNext()) {
            id = idIterator.iterator().next();
        } else {
            id = new PersistentDatabaseID();
            id.setNextAllowedId(1);
        }
        long nextID = id.getNextAllowedId();
        long upTo = nextID + inBlockSize;
        id.setNextAllowedId(upTo);
        databaseIdDao.save(id);
        return id;
    }
    /**
     * 
     */
    @Autowired
    private DatabaseIdDao databaseIdDao;
}
