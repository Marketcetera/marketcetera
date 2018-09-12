package com.marketcetera.fix.store;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

/* $License$ */

/**
 * Provides data store access to {@link MessageStoreMessage} objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface MessageStoreMessageDao
        extends JpaRepository<MessageStoreMessage,Long>,QuerydslPredicateExecutor<MessageStoreMessage>
{
    /**
     * Delete all messages for the given session id.
     *
     * @param inSessionId a <code>String</code> value
     */
    @Modifying
    @Query("delete from MessageStoreMessage where sessionId=?1")
    void deleteBySessionId(String inSessionId);
}
