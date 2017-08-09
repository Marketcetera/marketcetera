package org.marketcetera.fix.dao;

import java.util.Date;
import java.util.Set;

import org.marketcetera.fix.IncomingMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

/* $License$ */

/**
 * Provides datastore access to {@link IncomingMessage} values.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface IncomingMessageDao
        extends JpaRepository<PersistentIncomingMessage,Long>,QueryDslPredicateExecutor<PersistentIncomingMessage>
{
    /**
     * Gets the incoming messages with the given ids.
     *
     * @param inIds a <code>Set&lt;Long&gt;</code> value
     * @param inPageRequest a <code>Pageable</code> value
     * @return a <code>Page&lt;PersistentIncomingMessage&gt;</code> value
     */
    Page<PersistentIncomingMessage> findByIdIn(Set<Long> inIds,
                                               Pageable inPageRequest);
    /**
     * Find the most recent message for the given session of one of the given message types.
     *
     * @param inSessionId a <code>String</code> value
     * @param inMsgTypes a <code>Set&lt;String&gt;</code> value
     * @return a <code>PersistentIncomingMessage</code> value or <code>null</code>
     */
    @Query("select a from IncomingMessage a where a.id=(select max(b.id) from IncomingMessage b where b.sessionId=?1 and b.msgType in (?2))")
    PersistentIncomingMessage findMostRecentMessage(String inSessionId,
                                                    Set<String> inMsgTypes);
    /**
     * Get the approximate number of distinct executions for the given session since the given time.
     *
     * <p>This method will return an approximation of the number of executions received. Some executions are intentionally omitted from the counting for simplicity/
     *
     * @param inSessionId a <code>String</code> value
     * @param inSince a <code>Date</code> value
     * @return a <code>long</code> value
     */
    @Query(value="select count(*) from (select distinct clordid,execid from incoming_fix_messages where execid is not null and msg_type='8' and fix_session=?1 and sending_time>=?2) message_count",nativeQuery=true)
    long getExecutionCount(String inSessionId,
                           Date inSince);
    /**
     * Delete incoming messages sent before the given time.
     *
     * @param inTime a <code>Date</code> value
     * @return an <code>int</code> value
     */
    @Modifying
    @Query(value="delete from IncomingMessage where sendingTime <= ?1")
    int deleteBefore(Date inTime);
    /**
     * Find the incoming message with the given attributes.
     *
     * @param inSessionId a <code>String</code> value
     * @param inMsgSeqNum an <code>int</code> value
     * @param inSendingTime a <code>Date</code> value
     * @return a <code>PersistentIncomingMessage</code> value or <code>null</code>
     */
    PersistentIncomingMessage findBySessionIdAndMsgSeqNumAndSendingTime(String inSessionId,
                                                                        int inMsgSeqNum,
                                                                        Date inSendingTime);
}
