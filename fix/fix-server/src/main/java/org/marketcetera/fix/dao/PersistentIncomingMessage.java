package org.marketcetera.fix.dao;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.joda.time.DateTime;
import org.marketcetera.fix.IncomingMessage;

import quickfix.FieldNotFound;
import quickfix.InvalidMessage;
import quickfix.Message;
import quickfix.SessionID;
import quickfix.field.ClOrdID;
import quickfix.field.ExecID;
import quickfix.field.MsgSeqNum;
import quickfix.field.MsgType;
import quickfix.field.SendingTime;

/* $License$ */

/**
 * Provides a persistent {@link IncomingMessage} implementation.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Table(name="incoming_fix_messages")
@Entity(name="IncomingMessage")
public class PersistentIncomingMessage
        implements IncomingMessage, Serializable
{
    /**
     * Create a new PersistentIncomingMessage instance.
     */
    public PersistentIncomingMessage()
    {
    }
    /**
     * Create a new PersistentIncomingMessage instance.
     *
     * @param inSessionId a <code>SessionID</code> value
     * @param inMessage a <code>Message</code> value
     */
    public PersistentIncomingMessage(SessionID inSessionId,
                                     Message inMessage)
    {
        setSessionId(inSessionId);
        setMessage(inMessage);
        try {
            setMsgSeqNum(inMessage.getHeader().getInt(MsgSeqNum.FIELD));
            setMsgType(inMessage.getHeader().getString(MsgType.FIELD));
            setSendingTime(inMessage.getHeader().getUtcTimeStamp(SendingTime.FIELD));
            if(inMessage.isSetField(ExecID.FIELD)) {
                setExecId(inMessage.getString(ExecID.FIELD));
            }
            if(inMessage.isSetField(ClOrdID.FIELD)) {
                setClOrdId(inMessage.getString(ClOrdID.FIELD));
            }
        } catch (FieldNotFound e) {
            throw new RuntimeException(e);
        }
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("IncomingMessage ").append(sessionId).append(' ').append(msgSeqNum)
                .append(' ').append(msgType).append(' ').append(new DateTime(sendingTime));
        return builder.toString();
    }
    /* (non-Javadoc)
     * @see com.marketcetera.ors.quickfix.IncomingMessage#getSessionId()
     */
    @Override
    public String getSessionId()
    {
        return sessionId;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.ors.quickfix.IncomingMessage#getMsgSeqNum()
     */
    @Override
    public int getMsgSeqNum()
    {
        return msgSeqNum;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.ors.quickfix.IncomingMessage#getSendingTime()
     */
    @Override
    public Date getSendingTime()
    {
        return sendingTime;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.ors.quickfix.IncomingMessage#getMsgType()
     */
    @Override
    public String getMsgType()
    {
        return msgType;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.ors.quickfix.IncomingMessage#getMessage()
     */
    @Override
    public Message getMessage()
    {
        if(message == null) {
            return null;
        }
        try {
            return new Message(message);
        } catch (InvalidMessage e) {
            throw new RuntimeException(e);
        }
    }
    /* (non-Javadoc)
     * @see com.marketcetera.ors.quickfix.IncomingMessage#getId()
     */
    @Override
    public long getId()
    {
        return id;
    }
    /**
     * Sets the sessionId value.
     *
     * @param inSessionId a <code>SessionID</code> value
     */
    public void setSessionId(SessionID inSessionId)
    {
        sessionId = inSessionId.toString();
    }
    /**
     * Sets the message value.
     *
     * @param inMessage a <code>Message</code> value
     */
    public void setMessage(Message inMessage)
    {
        if(inMessage == null) {
            message = null;
        } else {
            message = inMessage.toString();
        }
    }
    /**
     * Sets the msgSeqNum value.
     *
     * @param inMsgSeqNum a <code>int</code> value
     */
    public void setMsgSeqNum(int inMsgSeqNum)
    {
        msgSeqNum = inMsgSeqNum;
    }
    /**
     * Sets the sendTime value.
     *
     * @param inSendTime a <code>Date</code> value
     */
    public void setSendingTime(Date inSendTime)
    {
        sendingTime = inSendTime;
    }
    /**
     * Sets the msgType value.
     *
     * @param inMsgType a <code>String</code> value
     */
    public void setMsgType(String inMsgType)
    {
        msgType = inMsgType;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.ors.quickfix.IncomingMessage#getExecId()
     */
    @Override
    public String getExecId()
    {
        return execId;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.ors.quickfix.IncomingMessage#getClOrdId()
     */
    @Override
    public String getClOrdId()
    {
        return clOrdId;
    }
    /**
     * Sets the execId value.
     *
     * @param inExecId a <code>String</code> value
     */
    public void setExecId(String inExecId)
    {
        execId = inExecId;
    }
    /**
     * Sets the clOrdId value.
     *
     * @param inClOrdId a <code>String</code> value
     */
    public void setClOrdId(String inClOrdId)
    {
        clOrdId = inClOrdId;
    }
    /**
     * id value
     */
    @Id
    @GeneratedValue
    @Column(name="id",nullable=false)
    private long id;
    /**
     * session id value
     */
    @Column(name="fix_session",nullable=false)
    private String sessionId;
    /**
     * FIX message value
     */
    @Column(name="message",length=4000,nullable=false)
    private String message;
    /**
     * message sequence number of the most recent message
     */
    @Column(name="msg_seq_num",nullable=false)
    private int msgSeqNum;
    /**
     * sending time value
     */
    @Column(name="sending_time",nullable=false)
    private Date sendingTime;
    /**
     * msg type of the most recent message
     */
    @Column(name="msg_type",nullable=false)
    private String msgType;
    /**
     * exec id of the incoming message, if present
     */
    @Column(name="execid",nullable=true)
    private String execId;
    /**
     * cl ord id of the incoming message, if present
     */
    @Column(name="clordid",nullable=true)
    private String clOrdId;
    private static final long serialVersionUID = 2818985153287642587L;
}
