package org.marketcetera.trade.dao;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.marketcetera.admin.User;
import org.marketcetera.admin.user.PersistentUser;
import org.marketcetera.fix.FixMessage;
import org.marketcetera.fix.dao.PersistentFixMessage;
import org.marketcetera.persist.EntityBase;
import org.marketcetera.trade.BrokerID;
import org.marketcetera.trade.OutgoingMessage;

import quickfix.FieldNotFound;
import quickfix.InvalidMessage;
import quickfix.Message;
import quickfix.SessionID;
import quickfix.field.MsgSeqNum;
import quickfix.field.MsgType;
import quickfix.field.SenderCompID;
import quickfix.field.TargetCompID;

/* $License$ */

/**
 * Persistent <code>OutgoingMessage</code> implementation.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Entity(name="OutgoingMessage")
@Table(name="outgoing_messages")
public class PersistentOutgoingMessage
        extends EntityBase
        implements OutgoingMessage
{
    /**
     * Create a new PersistentOutgoingMessage instance.
     */
    public PersistentOutgoingMessage() 
    {
    }
    /**
     * Create a new PersistentOutgoingMessage instance.
     *
     * @param inMessage a <code>Message</code> value
     * @param inBrokerId a <code>BrokerID</code> value
     * @param inSessionId a <code>SessionID</code> value
     * @param inActor a <code>User</code> value
     */
    public PersistentOutgoingMessage(Message inMessage,
                                     BrokerID inBrokerId,
                                     SessionID inSessionId,
                                     User inActor)
    {
        messageValue.setMessage(inMessage.toString());
        sessionIdValue = inSessionId.toString();
        brokerId = inBrokerId;
        actor = (PersistentUser)inActor;
        try {
            messageType = inMessage.getHeader().getString(MsgType.FIELD);
            if(inMessage.getHeader().isSetField(MsgSeqNum.FIELD)) {
                msgSeqNum = inMessage.getHeader().getInt(MsgSeqNum.FIELD);
            }
            if(inMessage.getHeader().isSetField(SenderCompID.FIELD)) {
                senderCompId = inMessage.getHeader().getString(SenderCompID.FIELD);
            }
            if(inMessage.getHeader().isSetField(TargetCompID.FIELD)) {
                targetCompId = inMessage.getHeader().getString(TargetCompID.FIELD);
            }
            if(inMessage.isSetField(quickfix.field.ClOrdID.FIELD)) {
                orderId = inMessage.getString(quickfix.field.ClOrdID.FIELD);
            } else if(inMessage.isSetField(quickfix.field.OrderID.FIELD)) {
                orderId = inMessage.getString(quickfix.field.OrderID.FIELD);
            }
        } catch (FieldNotFound e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * Create a new PersistentOutgoingMessage instance.
     *
     * @param inOutgoingMessage an <code>OutgoingMessage</code> value
     */
    public PersistentOutgoingMessage(OutgoingMessage inOutgoingMessage)
    {
        actor = (PersistentUser)inOutgoingMessage.getActor();
        brokerId = inOutgoingMessage.getBrokerId();
        messageType = inOutgoingMessage.getMessageType();
        messageValue.setMessage(inOutgoingMessage.getMessage().toString());
        msgSeqNum = inOutgoingMessage.getMsgSeqNum();
        orderId = inOutgoingMessage.getOrderId();
        senderCompId = inOutgoingMessage.getSenderCompId();
        sessionIdValue = inOutgoingMessage.getSessionID().toString();
        targetCompId = inOutgoingMessage.getTargetCompId();
    }
    /* (non-Javadoc)
     * @see com.marketcetera.ors.domain.OutgoingMessage#getBrokerId()
     */
    @Override
    public BrokerID getBrokerId()
    {
        return brokerId;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.ors.domain.OutgoingMessage#getSenderCompId()
     */
    @Override
    public String getSenderCompId()
    {
        return senderCompId;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.ors.domain.OutgoingMessage#getTargetCompId()
     */
    @Override
    public String getTargetCompId()
    {
        return targetCompId;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.ors.domain.OutgoingMessage#getMessage()
     */
    @Override
    public Message getMessage()
    {
        if(messageValue == null) {
            return null;
        }
        try {
            return new Message(messageValue.getMessage());
        } catch (InvalidMessage e) {
            throw new RuntimeException(e);
        }
    }
    /* (non-Javadoc)
     * @see com.marketcetera.ors.domain.OutgoingMessage#getSessionID()
     */
    @Override
    public SessionID getSessionID()
    {
        if(sessionIdValue == null) {
            return null;
        }
        return new SessionID(sessionIdValue);
    }
    /* (non-Javadoc)
     * @see com.marketcetera.ors.domain.OutgoingMessage#getMessageType()
     */
    @Override
    public String getMessageType()
    {
        return messageType;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.ors.domain.OutgoingMessage#getMsgSeqNum()
     */
    @Override
    public int getMsgSeqNum()
    {
        return msgSeqNum;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.ors.domain.OutgoingMessage#getActor()
     */
    @Override
    public PersistentUser getActor()
    {
        return actor;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.ors.domain.OutgoingMessage#getOrderId()
     */
    @Override
    public String getOrderId()
    {
        return orderId;
    }
    /**
     * broker id value
     */
    @Embedded
    @AttributeOverrides({@AttributeOverride(name="mValue",column=@Column(name="broker_id",nullable=false))})
    private BrokerID brokerId;
    /**
     * sender comp id value
     */
    @Column(name="sender_comp_id",nullable=false)
    private String senderCompId;
    /**
     * target comp id value
     */
    @Column(name="target_comp_id",nullable=false)
    private String targetCompId;
    /**
     * raw FIX message value
     */
    @JoinColumn(name="fix_message_id")
    @OneToOne(cascade={CascadeType.ALL},optional=false,orphanRemoval=true,fetch=FetchType.EAGER,targetEntity=PersistentFixMessage.class)
    private FixMessage messageValue = new PersistentFixMessage();
    /**
     * session ID value
     */
    @Column(name="session_id",nullable=false)
    private String sessionIdValue;
    /**
     * message type value
     */
    @Column(name="message_type",nullable=false)
    private String messageType;
    /**
     * MsgSeqNum value
     */
    @Column(name="msg_seq_num")
    private int msgSeqNum;
    /**
     * actor value
     */
    @ManyToOne
    @JoinColumn(name="actor_id",nullable=false)
    private PersistentUser actor; 
    /**
     * order Id value
     */
    @Column(name="order_id",nullable=true)
    private String orderId;
    private static final long serialVersionUID = 6645547576221081314L;
}
