package org.marketcetera.trade.service.impl;

import org.marketcetera.trade.dao.DatabaseIdDao;
import org.marketcetera.trade.dao.PersistentDatabaseID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/* $License$ */

/**
 * Provides business-level services for database-backed ids.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: DatabaseIdService.java 17266 2017-04-28 14:58:00Z colin $
 * @since 2.4.2
 */
@Service
public class DatabaseIdService
{
    /**
     * Allocates an id block of the given size and returns the resulting id.
     *
     * @param inBlockSize a <code>long</code> value
     * @return a <code>PersistentDatabaseID</code> value
     */
    @Transactional(readOnly=false,propagation=Propagation.REQUIRED)
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
     * Get the databaseIdDao value.
     *
     * @return a <code>DatabaseIdDao</code> value
     */
    public DatabaseIdDao getDatabaseIdDao()
    {
        return databaseIdDao;
    }
    /**
     * Sets the databaseIdDao value.
     *
     * @param inDatabaseIdDao a <code>DatabaseIdDao</code> value
     */
    public void setDatabaseIdDao(DatabaseIdDao inDatabaseIdDao)
    {
        databaseIdDao = inDatabaseIdDao;
    }
    /**
     * provides datastore access to database ids 
     */
    @Autowired
    private DatabaseIdDao databaseIdDao;
}
