package org.marketcetera.ors.history;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.*;

import org.marketcetera.event.HasFIXMessage;
import org.marketcetera.ors.Principals;
import org.marketcetera.ors.security.SimpleUser;
import org.marketcetera.persist.EntityBase;
import org.marketcetera.persist.PersistenceException;
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
@ClassVersion("$Id$")
@Entity
@Table(name = "reports")
@NamedQueries( { @NamedQuery(name="forOrderID",query="select e from PersistentReport e where e.orderID = :orderID"),
                 @NamedQuery(name="since",query="select e from PersistentReport e where e.sendingTime < :target") })
public class PersistentReport
        extends EntityBase
{

    /**
     * Creates an instance, given a report.
     *
     * @param inReport the report instance.
     *
     * @throws PersistenceException if there were errors creating the
     * instance.
     */
    PersistentReport(ReportBase inReport)
        throws PersistenceException
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
//        if (inReport.getActorID()!=null) {
//            setActor(new SingleSimpleUserQuery
//                     (inReport.getActorID().getValue()).fetch());
//        }
//        if (inReport.getViewerID()!=null) {
//            setViewer(new SingleSimpleUserQuery
//                      (inReport.getViewerID().getValue()).fetch());
//        }
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
     *
     * @throws ReportPersistenceException if there were errors converting
     * the message from its persistent representation to system report
     * instance.
     */
    ReportBase toReport() throws ReportPersistenceException {
        ReportBase returnValue = null;
        String fixMsgString = null;
        try {
            fixMsgString = getFixMessage();
            Message fixMessage;
            try {
            	fixMessage = new Message(fixMsgString);
			} catch (InvalidMessage e) {
				fixMessage =  new Message(fixMsgString,false); // log the validation exception and create message without validation.
				SLF4JLoggerProxy.warn(PersistentReport.class, e);    
			}
            switch(mReportType) {
                case ExecutionReport:
                    returnValue =  Factory.getInstance().createExecutionReport(
                            fixMessage, getBrokerID(),
                            getOriginator(), getActorID(), getViewerID());
                    break;
                case CancelReject:
                    returnValue =  Factory.getInstance().createOrderCancelReject(
                            fixMessage, getBrokerID(), getOriginator(), getActorID(), getViewerID());
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

    private Originator getOriginator() {
        return mOriginator;
    }

    private void setOriginator(Originator inOriginator) {
        mOriginator = inOriginator;
    }

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name="value",
                    column = @Column(name = "orderID", nullable = false))})
    OrderID getOrderID() {
        return mOrderID;
    }

    private void setOrderID(OrderID inOrderID) {
        mOrderID = inOrderID;
    }

    @ManyToOne
    public SimpleUser getActor() {
        return mActor;
    }

    private void setActor(SimpleUser inActor) {
        mActor = inActor;
    }

    @Transient
    UserID getActorID() {
        if (getActor()==null) {
            return null;
        }
        return getActor().getUserID();
    }

    @ManyToOne
    public SimpleUser getViewer() {
        return mViewer;
    }

    private void setViewer(SimpleUser inViewer) {
        mViewer = inViewer;
    }

    @Transient
    UserID getViewerID() {
        if (getViewer()==null) {
            return null;
        }
        return getViewer().getUserID();
    }

    @Transient
    BrokerID getBrokerID() {
        return mBrokerID;
    }

    private void setBrokerID(BrokerID inBrokerID) {
        mBrokerID = inBrokerID;
    }
    @Column(name = "brokerID")
    private String getBrokerIDAsString() {
        return getBrokerID() == null
                ? null
                : getBrokerID().toString();
    }
    @SuppressWarnings("unused")
    private void setBrokerIDAsString(String inValue) {
        setBrokerID(inValue == null
                ? null
                : new BrokerID(inValue));
    }

    @Transient
    ReportID getReportID() {
        return mReportID;
    }

    private void setReportID(ReportID inReportID) {
        mReportID = inReportID;
    }
    @Column(name = "reportID", nullable = false)
    private long getReportIDAsLong() {
        return getReportID().longValue();
    }
    @SuppressWarnings("unused")
    private void setReportIDAsLong(long inValue) {
        setReportID(new ReportID(inValue));
    }

    @Lob
    @Column(nullable = false)
    private String getFixMessage() {
        return mFixMessage;
    }

    private void setFixMessage(String inFIXMessage) {
        mFixMessage = inFIXMessage;
    }

    @Column(nullable = false)
    private Date getSendingTime() {
        return mSendingTime;
    }

    private void setSendingTime(Date inSendingTime) {
        mSendingTime = inSendingTime;
    }

    @Column(nullable = false)
    private ReportType getReportType() {
        return mReportType;
    }

    @SuppressWarnings("unused")
    private void setReportType(ReportType inReportType) {
        mReportType = inReportType;
    }

    /**
     * Declared to get JPA to work.
     */
    PersistentReport() {
    }

    /**
     * The attribute sending time used in JPQL queries
     */
    static final String ATTRIBUTE_SENDING_TIME = "sendingTime";  //$NON-NLS-1$
    /**
     * The attribute actor used in JPQL queries
     */
    static final String ATTRIBUTE_ACTOR = "actor";  //$NON-NLS-1$
    /**
     * The attribute viewer used in JPQL queries
     */
    static final String ATTRIBUTE_VIEWER = "viewer";  //$NON-NLS-1$
    /**
     * The entity name as is used in various JPQL Queries
     */
    static final String ENTITY_NAME = PersistentReport.class.getSimpleName();

    private Originator mOriginator;
    private OrderID mOrderID;
    private SimpleUser mActor; 
    private SimpleUser mViewer; 
    private BrokerID mBrokerID;
    private ReportID mReportID;
    private String mFixMessage;
    private Date mSendingTime;
    private ReportType mReportType;
    private ReportBase mReportBase;
    private static final long serialVersionUID = 1;
}
