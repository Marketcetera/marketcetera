package org.marketcetera.trade.dao;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

/* $License$ */

/**
 * Provides datastore access to <code>OutgoingMessage</code> objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface PersistentOutgoingMessageDao
        extends JpaRepository<PersistentOutgoingMessage,Long>,JpaSpecificationExecutor<PersistentOutgoingMessage>,QuerydslPredicateExecutor<PersistentOutgoingMessage>
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
    
    /**
     * Finds outgoing messages by broker ID, message sequence number, and message type with pagination.
     *
     * @param inBrokerId a <code>String</code> value for the broker ID
     * @param inMsgSeqNum an <code>int</code> value for the message sequence number
     * @param inMsgType a <code>String</code> value for the message type
     * @param pageable a <code>Pageable</code> value for pagination
     * @return a <code>List&lt;PersistentOutgoingMessage&gt;</code> value
     */
    List<PersistentOutgoingMessage> findByBrokerIdAndMsgSeqNumAndMsgType(String inBrokerId,
                                                                        int inMsgSeqNum,
                                                                        String inMsgType,
                                                                        Pageable pageable);
}