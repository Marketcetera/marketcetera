package org.marketcetera.fix.store;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/* $License$ */

/**
 * Provides a persistent implementation of a message for {@link HibernateMessageStore}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Entity
@Table(name="message_store_messages")
public class MessageStoreMessage
        extends AbstractMessageStoreEntity
{
    /**
     * Get the msgSeqNum value.
     *
     * @return an <code>int</code> value
     */
    public int getMsgSeqNum()
    {
        return msgSeqNum;
    }
    /**
     * Sets the msgSeqNum value.
     *
     * @param inMsgSeqNum an <code>int</code> value
     */
    public void setMsgSeqNum(int inMsgSeqNum)
    {
        msgSeqNum = inMsgSeqNum;
    }
    /**
     * Get the message value.
     *
     * @return a <code>String</code> value
     */
    public String getMessage()
    {
        return message;
    }
    /**
     * Sets the message value.
     *
     * @param inMessage a <code>String</code> value
     */
    public void setMessage(String inMessage)
    {
        message = inMessage;
    }
    /**
     * message sequence number value
     */
    @Column(name="msg_seq_num",nullable=false)
    private int msgSeqNum;
    /**
     * message value
     */
    @Column(name="message",nullable=false,length=8192,unique=false)
    private String message;
    private static final long serialVersionUID = 1841808209379892266L;
}
