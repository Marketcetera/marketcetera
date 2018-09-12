package org.marketcetera.fix.store;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

/* $License$ */

/**
 * Provides data store access to {@link MessageStoreSession} objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface MessageStoreSessionDao
        extends JpaRepository<MessageStoreSession,Long>,QuerydslPredicateExecutor<MessageStoreSession>
{
    /**
     * Find the session with the given session id.
     *
     * @param inSessionId a <code>String</code> value
     * @return a <code>MessageStoreSession</code> value or <code>null</code>
     */
    MessageStoreSession findBySessionId(String inSessionId);
    @Query("select distinct a.sessionId from MessageStoreSession a")
    Set<String> findSessionId();
    /**
     * Delete the session with the given session id.
     *
     * @param inString a <code>String</code> value
     */
    @Modifying
    @Query("delete from MessageStoreSession where sessionId=?1")
    void deleteBySessionId(String inString);
}
