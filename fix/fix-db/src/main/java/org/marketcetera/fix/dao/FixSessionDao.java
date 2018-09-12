package org.marketcetera.fix.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

/* $License$ */

/**
 * Provides access to the Fix Session data store.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface FixSessionDao
        extends JpaRepository<PersistentFixSession,Long>,QuerydslPredicateExecutor<PersistentFixSession>
{
    /**
     * Finds the FIX session with the given name.
     *
     * @param inName a <code>String</code> value
     * @return a <code>PersistentFixSession</code> or <code>null</code>
     */
    PersistentFixSession findByNameAndIsDeletedFalse(String inName);
    /**
     * Finds FIX sessions of the given connection type.
     *
     * @param inIsAcceptor a <code>boolean</code> value
     * @return a <code>List&lt;PersistentFixSession&gt;</code> value
     */
    List<PersistentFixSession> findByIsAcceptorAndIsDeletedFalseOrderByAffinityAsc(boolean inIsAcceptor);
    /**
     * Finds the FIX session with the given session id.
     *
     * @param inString a <code>String</code> value
     * @return a <code>PersistentFixSession</code> or <code>null</code>
     */
    PersistentFixSession findBySessionIdAndIsDeletedFalse(String inString);
    /**
     * Finds the FIX session with the given broker id.
     *
     * @param inBrokerId a <code>String</code> value
     * @return a <code>PersistentFixSession</code> or <code>null</code>
     */
    PersistentFixSession findByBrokerIdAndIsDeletedFalse(String inBrokerId);
}
