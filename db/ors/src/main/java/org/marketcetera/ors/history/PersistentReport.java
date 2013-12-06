package org.marketcetera.ors.history;

import java.util.Date;

import javax.persistence.*;

import org.marketcetera.event.HasFIXMessage;
import org.marketcetera.ors.security.SimpleUser;
import org.marketcetera.persist.EntityBase;
import org.marketcetera.trade.*;
import org.marketcetera.util.log.I18NBoundMessage1P;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.misc.ClassVersion;

import quickfix.InvalidMessage;
import quickfix.Message;

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
@NamedQueries( { @NamedQuery(name="PersistentReport.findSince",query="select e from PersistentReport e where e.sendingTime>?1") })
@ClassVersion("$Id$")
public class PersistentReport
        extends EntityBase
{
    /**
     * Creates an instance, given a report.
     *
     * @param inReport a <code>ReportBase</code> value
     * @param inActor a <code>SimpleUser</code> value
     * @param inViewer a <code>SimpleUser</code> value
     */
    public PersistentReport(ReportBase inReport,
                            SimpleUser inActor,
                            SimpleUser inViewer)
    {
        mReportBase = inReport;
        setBrokerID(inReport.getBrokerID());
        setSendingTime(inReport.getSendingTime());
        if(inReport instanceof HasFIXMessage) {
            setFixMessage(((HasFIXMessage) inReport).getMessage().toString());
        }
        setOriginator(inReport.getOriginator());
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
                                                                               getActorID(),
                                                                               getViewerID());
                    break;
                case CancelReject:
                    returnValue =  Factory.getInstance().createOrderCancelReject(fixMessage,
                                                                                 getBrokerID(),
                                                                                 getOriginator(),
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
            throw new ReportPersistenceException(e, new I18NBoundMessage1P(
                    Messages.ERROR_RECONSTITUTE_FIX_MSG, fixMsgString));
        } catch (MessageCreationException e) {
            throw new ReportPersistenceException(e, new I18NBoundMessage1P(
                    Messages.ERROR_RECONSTITUTE_FIX_MSG, fixMsgString));
        }
    }
    /**
     * Gets the actor id of the report.
     *
     * @return a <code>UserID</code> value or <code>null</code>
     */
    public UserID getActorID()
    {
        if (getActor()==null) {
            return null;
        }
        return getActor().getUserID();
    }
    /**
     * Gets the viewer id of the report.
     *
     * @return a <code>UserID</code> value or <code>null</code>
     */
    public UserID getViewerID()
    {
        if (getViewer()==null) {
            return null;
        }
        return getViewer().getUserID();
    }
    /**
     * Get the orderID value.
     *
     * @return an <code>OrderID</code> value
     */
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
    /**
     * Get the actor value.
     *
     * @return a <code>SimpleUser</code> value
     */
    public SimpleUser getActor()
    {
        return mActor;
    }
    /**
     * Sets the actor value.
     *
     * @param inActor a <code>SimpleUser</code> value
     */
    public void setActor(SimpleUser inActor)
    {
        mActor = inActor;
    }
    /**
     * Get the viewer value.
     *
     * @return a <code>SimpleUser</code> value
     */
    public SimpleUser getViewer()
    {
        return viewer;
    }
    /**
     * Sets the viewer value.
     *
     * @param inViewer a <code>SimpleUser</code> value
     */
    public void setViewer(SimpleUser inViewer)
    {
        viewer = inViewer;
    }
    /**
     * Get the fixMessage value.
     *
     * @return a <code>String</code> value
     */
    public String getFixMessage()
    {
        return mFixMessage;
    }
    /**
     * Sets the fixMessage value.
     *
     * @param inFixMessage a <code>String</code> value
     */
    public void setFixMessage(String inFixMessage)
    {
        mFixMessage = inFixMessage;
    }
    /**
     * Get the sendingTime value.
     *
     * @return a <code>Date</code> value
     */
    public Date getSendingTime()
    {
        return sendingTime;
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
    /**
     * Get the reportType value.
     *
     * @return a <code>ReportType</code> value
     */
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
    /**
     * Get the brokerID value.
     *
     * @return a <code>BrokerID</code> value
     */
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
    /**
     * Get the reportID value.
     *
     * @return a <code>ReportID</code> value
     */
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
    /**
     * Get the originator value.
     *
     * @return an <code>Originator</code> value
     */
    public Originator getOriginator()
    {
        return mOriginator;
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
     * Create a new PersistentReport instance.
     */
    public PersistentReport() {}
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
    private SimpleUser mActor; 
    /**
     * viewer value
     */
    @ManyToOne
    @JoinColumn(name="viewer_id")
    private SimpleUser viewer; 
    /**
     * raw FIX message value
     */
    @Column(name="message",nullable=false,length=8192)
    private String mFixMessage;
    /**
     * sending time value
     */
    @Column(name="send_time",nullable=false)
    private Date sendingTime;
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
     * root report base value
     */
    @Transient
    private transient ReportBase mReportBase;
    private static final long serialVersionUID = 1;
}
