package org.marketcetera.core;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/* $License$ */

/**
 *
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
        Iterable<PersistentDatabaseID> idIterator = databaseIdRepository.findAll();
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
        databaseIdRepository.save(id);
        return id;
    }
    /**
     * 
     */
    @Autowired
    private DatabaseIdRepository databaseIdRepository;
}
