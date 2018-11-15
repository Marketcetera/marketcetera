package org.marketcetera.fix.store;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.joda.time.DateTime;

/* $License$ */

/**
 * Provides a persistent implementation of a session for {@link HibernateMessageStore}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Entity
@Table(name="message_store_sessions")
public class MessageStoreSession
        extends AbstractMessageStoreEntity
{
    /**
     * Get the creationTime value.
     *
     * @return a <code>Date</code> value
     */
    public Date getCreationTime()
    {
        return creationTime;
    }
    /**
     * Sets the creationTime value.
     *
     * @param inCreationTime a <code>Date</code> value
     */
    public void setCreationTime(Date inCreationTime)
    {
        creationTime = inCreationTime;
    }
    /**
     * Get the targetSeqNum value.
     *
     * @return an <code>int</code> value
     */
    public int getTargetSeqNum()
    {
        return targetSeqNum;
    }
    /**
     * Sets the targetSeqNum value.
     *
     * @param inTargetSeqNum an <code>int</code> value
     */
    public void setTargetSeqNum(int inTargetSeqNum)
    {
        targetSeqNum = inTargetSeqNum;
    }
    /**
     * Get the senderSeqNum value.
     *
     * @return an <code>int</code> value
     */
    public int getSenderSeqNum()
    {
        return senderSeqNum;
    }
    /**
     * Sets the senderSeqNum value.
     *
     * @param inSenderSeqNum an <code>int</code> value
     */
    public void setSenderSeqNum(int inSenderSeqNum)
    {
        senderSeqNum = inSenderSeqNum;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("MessageStoreSession [").append(getSessionId()).append(" targetSeqNum=")
                .append(targetSeqNum).append(", senderSeqNum=").append(senderSeqNum).append(", creationTime")
                .append(new DateTime(getCreationTime())).append("]");
        return builder.toString();
    }
    /**
     * time session was actually created
     */
    @Column(name="creation_time",nullable=false)
    private Date creationTime;
    /**
     * target sequence number value
     */
    @Column(name="target_seq_num",nullable=false)
    private int targetSeqNum;
    /**
     * sender sequence number vlue
     */
    @Column(name="sender_seq_num",nullable=false)
    private int senderSeqNum;
    private static final long serialVersionUID = -5336560074189778942L;
}
