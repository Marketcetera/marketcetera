package org.marketcetera.trade.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

/* $License$ */

/**
 * Provides datastore access to <code>OutgoingMessage</code> objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface PersistentOutgoingMessageDao
        extends JpaRepository<PersistentOutgoingMessage,Long>,QueryDslPredicateExecutor<PersistentOutgoingMessage>
{
    /**
     * Finds the outgoing message with the given attributes.
     *
     * @param inSenderCompId a <code>String</code> value
     * @param inTargetCompId a <code>String</code> value
     * @param inSeqNum an <code>int</code> value
     * @return a <code>List&lt;PersistentOutgoingMessage&gt;</code> value
     */
    List<PersistentOutgoingMessage> findBySenderCompIdAndTargetCompIdAndMsgSeqNumOrderByIdDesc(String inSenderCompId,
                                                                                               String inTargetCompId,
                                                                                               int inSeqNum);
}
