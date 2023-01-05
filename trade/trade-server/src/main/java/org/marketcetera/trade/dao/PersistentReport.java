package org.marketcetera.trade.dao;

import java.util.Date;

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
import javax.persistence.Transient;

import org.marketcetera.admin.user.PersistentUser;
import org.marketcetera.event.HasFIXMessage;
import org.marketcetera.fix.FixMessage;
import org.marketcetera.fix.dao.PersistentFixMessage;
import org.marketcetera.persist.EntityBase;
import org.marketcetera.persist.PersistenceException;
import org.marketcetera.trade.BrokerID;
import org.marketcetera.trade.ExecutionReport;
import org.marketcetera.trade.Factory;
import org.marketcetera.trade.HasTradeMessage;
import org.marketcetera.trade.Hierarchy;
import org.marketcetera.trade.MessageCreationException;
import org.marketcetera.trade.OrderCancelReject;
import org.marketcetera.trade.OrderID;
import org.marketcetera.trade.Originator;
import org.marketcetera.trade.Report;
import org.marketcetera.trade.ReportBase;
import org.marketcetera.trade.ReportBaseImpl;
import org.marketcetera.trade.ReportID;
import org.marketcetera.trade.ReportType;
import org.marketcetera.trade.TradeMessage;
import org.marketcetera.trade.UserID;
import org.marketcetera.trade.service.Messages;
import org.marketcetera.util.log.I18NBoundMessage1P;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.time.DateService;

import quickfix.FieldNotFound;
import quickfix.InvalidMessage;
import quickfix.Message;
import quickfix.SessionID;
import quickfix.field.BeginString;
import quickfix.field.MsgSeqNum;
import quickfix.field.SenderCompID;
import quickfix.field.TargetCompID;

/* $License$ */
/**
 * A persistent report. The report instance is persisted to maintain
 * history. The reports can be retrieved filtered / sorted by the timestamp
 * of when they were sent.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.0.0
 */
@Entity
@Table(name="reports")
@ClassVersion("$Id$")
public class PersistentReport
        extends EntityBase
        implements Report,HasTradeMessage
{
    /**
     * Creates an instance, given a report.
     *
     * @param inReport a <code>ReportBase</code> value
     * @param inActor a <code>SimpleUser</code> value
     * @param inViewer a <code>SimpleUser</code> value
     */
    public PersistentReport(ReportBase inReport,
                            PersistentUser inActor,
                            PersistentUser inViewer)
    {
        mReportBase = inReport;
        setBrokerID(inReport.getBrokerId());
        setSendingTime(inReport.getSendingTime());
        if(inReport instanceof HasFIXMessage) {
            quickfix.Message fixMessage = ((HasFIXMessage)inReport).getMessage();
            setFixMessage(fixMessage.toString());
            if(fixMessage.isSetField(quickfix.field.TransactTime.FIELD)) {
                try {
                    setTransactTime(DateService.toLocalDate(fixMessage.getUtcTimeStamp(quickfix.field.TransactTime.FIELD)));
                } catch (quickfix.FieldNotFound ignored) {}
            } else {
                setTransactTime(inReport.getSendingTime());
            }
        }
        setText(inReport.getText());
        setOriginator(inReport.getOriginator());
        setHierarchy(inReport.getHierarchy());
        setOrderID(inReport.getOrderID());
        setReportID(inReport.getReportID());
        setActor(inActor);
        setViewer(inViewer);
        if(inReport instanceof ExecutionReport) {
            mReportType = ReportType.ExecutionReport;
        } else if (inReport instanceof OrderCancelReject) {
            mReportType = ReportType.CancelReject;
        } else {
            //You added new report types but forgot to update the code
            //to persist them.
            throw new IllegalArgumentException();
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.HasTradeMessage#getTradeMessage()
     */
    @Override
    public TradeMessage getTradeMessage()
    {
        return (TradeMessage)toReport();
    }
    /**
     * Converts the report into a system report instance.
     *
     * @return the system report instance.
     * @throws ReportPersistenceException if there were errors converting the message from its persistent representation to system report instance
     */
    public ReportBase toReport()
    {
        ReportBase returnValue = null;
        String fixMsgString = null;
        try {
            fixMsgString = getFixMessage();
            Message fixMessage;
            try {
                fixMessage = new Message(fixMsgString);
            } catch (InvalidMessage e) {
                fixMessage =  new Message(fixMsgString,
                                          false); // log the validation exception and create message without validation.
                SLF4JLoggerProxy.warn(PersistentReport.class,
                                      e);
            }
            switch(mReportType) {
                case ExecutionReport:
                    returnValue =  Factory.getInstance().createExecutionReport(fixMessage,
                                                                               getBrokerID(),
                                                                               getOriginator(),
                                                                               getHierarchy(),
                                                                               getActorID(),
                                                                               getViewerID());
                    break;
                case CancelReject:
                    returnValue =  Factory.getInstance().createOrderCancelReject(fixMessage,
                                                                                 getBrokerID(),
                                                                                 getOriginator(),
                                                                                 getHierarchy(),
                                                                                 getActorID(),
                                                                                 getViewerID());
                    break;
                default:
                    //You added new report types but forgot to update the code
                    //to persist them.
                    throw new IllegalArgumentException();
            }
            ReportBaseImpl.assignReportID((ReportBaseImpl)returnValue,
                                          getReportID());
            return returnValue;
        } catch (InvalidMessage e) {
            throw new PersistenceException(e, new I18NBoundMessage1P(
                    Messages.ERROR_RECONSTITUTE_FIX_MSG, fixMsgString));
        } catch (MessageCreationException e) {
            throw new PersistenceException(e, new I18NBoundMessage1P(
                    Messages.ERROR_RECONSTITUTE_FIX_MSG, fixMsgString));
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.dao.Report#getActorID()
     */
    @Override
    public UserID getActorID()
    {
        if (getActor()==null) {
            return null;
        }
        return getActor().getUserID();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.dao.Report#getViewerID()
     */
    @Override
    public UserID getViewerID()
    {
        if (getViewer()==null) {
            return null;
        }
        return getViewer().getUserID();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.dao.Report#getOrderID()
     */
    @Override
    public OrderID getOrderID()
    {
        return orderID;
    }
    /**
     * Sets the orderID value.
     *
     * @param inOrderID an <code>OrderID</code> value
     */
    public void setOrderID(OrderID inOrderID)
    {
        orderID = inOrderID;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.dao.Report#getActor()
     */
    @Override
    public PersistentUser getActor()
    {
        return mActor;
    }
    /**
     * Sets the actor value.
     *
     * @param inActor a <code>SimpleUser</code> value
     */
    public void setActor(PersistentUser inActor)
    {
        mActor = inActor;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.dao.Report#getViewer()
     */
    @Override
    public PersistentUser getViewer()
    {
        return viewer;
    }
    /**
     * Sets the viewer value.
     *
     * @param inViewer a <code>SimpleUser</code> value
     */
    public void setViewer(PersistentUser inViewer)
    {
        viewer = inViewer;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.dao.Report#getFixMessage()
     */
    @Override
    public String getFixMessage()
    {
        return mFixMessage.getMessage();
    }
    /**
     * Sets the fixMessage value.
     *
     * @param inFixMessage a <code>String</code> value
     */
    public void setFixMessage(String inFixMessage)
    {
        try {
            Message message = new Message(inFixMessage);
            // swap the target and sender in order to reflect what the session will look like in a query
            SessionID sessionId = new SessionID(message.getHeader().getString(BeginString.FIELD),
                                                message.getHeader().getString(TargetCompID.FIELD),
                                                message.getHeader().getString(SenderCompID.FIELD));
            sessionIdValue = sessionId.toString();
            msgSeqNum = message.getHeader().getInt(MsgSeqNum.FIELD);
        } catch (InvalidMessage | FieldNotFound e) {
            throw new RuntimeException(e);
        }
        mFixMessage.setMessage(inFixMessage);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.dao.Report#getSessionId()
     */
    @Override
    public SessionID getSessionId()
    {
        if(sessionIdValue == null) {
            return null;
        }
        return new SessionID(sessionIdValue);
    }
    /**
     * Sets the sessionId value.
     *
     * @param inSessionId a <code>SessionID</code> value
     */
    public void setSessionId(SessionID inSessionId)
    {
        sessionIdValue = inSessionId.toString();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.dao.Report#getMsgSeqNum()
     */
    @Override
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
    /* (non-Javadoc)
     * @see org.marketcetera.trade.dao.Report#getSendingTime()
     */
    @Override
    public Date getSendingTime()
    {
        return sendingTime;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.Report#getTransactTime()
     */
    @Override
    public Date getTransactTime()
    {
        return transactTime;
    }
    /**
     * Sets the sendingTime value.
     *
     * @param inSendingTime a <code>Date</code> value
     */
    public void setSendingTime(Date inSendingTime)
    {
        sendingTime = inSendingTime;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.dao.Report#getReportType()
     */
    @Override
    public ReportType getReportType()
    {
        return mReportType;
    }
    /**
     * Sets the reportType value.
     *
     * @param inReportType a <code>ReportType</code> value
     */
    public void setReportType(ReportType inReportType)
    {
        mReportType = inReportType;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.dao.Report#getBrokerID()
     */
    @Override
    public BrokerID getBrokerID()
    {
        return brokerID;
    }
    /**
     * Sets the brokerID value.
     *
     * @param inBrokerID a <code>BrokerID</code> value
     */
    public void setBrokerID(BrokerID inBrokerID)
    {
        brokerID = inBrokerID;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.dao.Report#getReportID()
     */
    @Override
    public ReportID getReportID()
    {
        return reportID;
    }
    /**
     * Sets the reportID value.
     *
     * @param inReportID a <code>ReportID</code> value
     */
    public void setReportID(ReportID inReportID)
    {
        reportID = inReportID;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.dao.Report#getOriginator()
     */
    @Override
    public Originator getOriginator()
    {
        return mOriginator;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("PersistentReport [orderID=").append(orderID).append(", mActor=").append(mActor)
                .append(", viewer=").append(viewer).append(", mFixMessage=").append(mFixMessage)
                .append(", sendingTime=").append(sendingTime).append(", transactTime=").append(transactTime)
                .append(", mReportType=").append(mReportType).append(", brokerID=").append(brokerID)
                .append(", reportID=").append(reportID).append(", mOriginator=").append(mOriginator)
                .append(", hierarchy=").append(hierarchy).append(", sessionIdValue=").append(sessionIdValue)
                .append(", msgSeqNum=").append(msgSeqNum).append(", text=").append(text).append("]");
        return builder.toString();
    }
    /**
     * Sets the originator value.
     *
     * @param inOriginator an <code>Originator</code> value
     */
    public void setOriginator(Originator inOriginator)
    {
        mOriginator = inOriginator;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.dao.Report#getHierarchy()
     */
    @Override
    public Hierarchy getHierarchy()
    {
        return hierarchy;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.Report#getText()
     */
    @Override
    public String getText()
    {
        return text;
    }
    /**
     * Sets the hierarchy value.
     *
     * @param inHierarchy a <code>Hierarchy</code> value
     */
    public void setHierarchy(Hierarchy inHierarchy)
    {
        hierarchy = inHierarchy;
    }
    /**
     * Get the reportBase value.
     *
     * @return a <code>ReportBase</code> value
     */
    public ReportBase getReportBase()
    {
        return mReportBase;
    }
    /**
     * Sets the reportBase value.
     *
     * @param inReportBase a <code>ReportBase</code> value
     */
    public void setReportBase(ReportBase inReportBase)
    {
        mReportBase = inReportBase;
    }
    /**
     * Set the transact time value.
     *
     * @param inTransactTime a <code>Date</code> value
     */
    public void setTransactTime(Date inTransactTime)
    {
        transactTime = inTransactTime;
    }
    /**
     * Sets the text value.
     *
     * @param inText a <code>String</code> value
     */
    public void setText(String inText)
    {
        text = inText;
    }
    /**
     * Create a new PersistentReport instance.
     */
    public PersistentReport()
    {
    }
    /**
     * order id value
     */
    @Embedded
    @AttributeOverrides({@AttributeOverride(name="mValue",column=@Column(name="order_id",nullable=false))})
    private OrderID orderID;
    /**
     * actor value
     */
    @ManyToOne
    @JoinColumn(name="actor_id")
    private PersistentUser mActor; 
    /**
     * viewer value
     */
    @ManyToOne
    @JoinColumn(name="viewer_id")
    private PersistentUser viewer; 
    /**
     * raw FIX message value
     */
    @JoinColumn(name="fix_message_id")
    @OneToOne(cascade={CascadeType.ALL},optional=false,orphanRemoval=true,fetch=FetchType.EAGER,targetEntity=PersistentFixMessage.class)
    private FixMessage mFixMessage = new PersistentFixMessage();
    /**
     * sending time value
     */
    @Column(name="send_time",nullable=false)
    private Date sendingTime;
    /**
     * transact time value
     */
    @Column(name="transact_time",nullable=false)
    private Date transactTime;
    /**
     * report type value
     */
    @Column(name="report_type",nullable=false)
    private ReportType mReportType;
    /**
     * broker ID value
     */
    @Embedded
    @AttributeOverrides({@AttributeOverride(name="mValue",column=@Column(name="broker_id"))})
    private BrokerID brokerID;
    /**
     * report ID value
     */
    @Embedded
    @AttributeOverrides({@AttributeOverride(name="mValue",column=@Column(name="report_id",nullable=false,unique=true))})
    private ReportID reportID;
    /**
     * report originator value
     */
    @Column(name="originator")
    private Originator mOriginator;
    /**
     * report hierarchy value
     */
    @Column(name="hierarchy")
    private Hierarchy hierarchy;
    /**
     * session ID value
     */
    @Column(name="session_id",nullable=false)
    private String sessionIdValue;
    /**
     * msg seq num value
     */
    @Column(name="msg_seq_num",nullable=false)
    private int msgSeqNum;
    /**
     * text value
     */
    @Column(name="text",nullable=true,length=255)
    private String text;
    /**
     * root report base value
     */
    @Transient
    private transient ReportBase mReportBase;
    private static final long serialVersionUID = -673167076047766659L;
}
