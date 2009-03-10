package org.marketcetera.ors.history;

import org.marketcetera.ors.security.SimpleUser;
import org.marketcetera.ors.security.SingleSimpleUserQuery;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.log.I18NBoundMessage1P;
import org.marketcetera.persist.*;
import org.marketcetera.persist.PersistenceException;
import org.marketcetera.trade.*;
import org.marketcetera.event.HasFIXMessage;

import javax.persistence.*;
import java.util.Date;

import quickfix.Message;
import quickfix.InvalidMessage;

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
class PersistentReport extends EntityBase {
    /**
     * Saves the supplied report to the database. Returns the ID of
     * the regular user who may view this report.
     *
     * @param inReport The report to be saved.
     *
     * @return The viewer ID. It may be null.
     *
     * @throws PersistenceException if there were errors saving the
     * report to the database.
     */
    static UserID save(ReportBase inReport) throws PersistenceException {
        PersistentReport report = new PersistentReport(inReport);
        report.saveRemote(null);
        ReportBaseImpl.assignReportID((ReportBaseImpl) inReport,
                new ReportID(report.getId()));
        return report.getViewerUserID();
    }
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
        if (inReport.getActorID()!=null) {
            setActor(new SingleSimpleUserQuery
                     (inReport.getActorID().getValue()).fetch());
        }
        setViewer(getActor()); // TODO.
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
            Message fixMessage = new Message(fixMsgString);
            switch(mReportType) {
                case ExecutionReport:
                    returnValue =  Factory.getInstance().createExecutionReport(
                            fixMessage, getBrokerID(),
                            getOriginator(), getActorUserID());
                    break;
                case CancelReject:
                    returnValue =  Factory.getInstance().createOrderCancelReject(
                            fixMessage, getBrokerID(), getOriginator(), getActor().getUserID());
                    break;
                default:
                    //You added new report types but forgot to update the code
                    //to persist them.
                    throw new IllegalArgumentException();
            }
            ReportBaseImpl.assignReportID((ReportBaseImpl)returnValue,
                    new ReportID(getId()));
            return returnValue;
        } catch (InvalidMessage e) {
            throw new ReportPersistenceException(e, new I18NBoundMessage1P(
                    Messages.ERROR_RECONSTITUTE_FIX_MSG, fixMsgString));
        } catch (MessageCreationException e) {
            throw new ReportPersistenceException(e, new I18NBoundMessage1P(
                    Messages.ERROR_RECONSTITUTE_FIX_MSG, fixMsgString));
        }
    }

    @Override
    protected void postSaveLocal(EntityManager em,
                                 EntityBase merged,
                                 PersistContext context)
            throws PersistenceException {
        super.postSaveLocal(em, merged, context);
        PersistentReport mergedReport = (PersistentReport) merged;
        //Save the summary if the report is an execution report.
        if(mergedReport.getReportType() == ReportType.ExecutionReport) {
            new ExecutionReportSummary(
                    (ExecutionReport) mReportBase,
                    mergedReport).localSave(em, context);
        }
    }

    private Originator getOriginator() {
        return mOriginator;
    }

    private void setOriginator(Originator inOriginator) {
        mOriginator = inOriginator;
    }

    @ManyToOne
    public SimpleUser getActor() {
        return mActor;
    }

    private void setActor(SimpleUser inActor) {
        mActor = inActor;
    }

    @Transient
    private UserID getActorUserID() {
        if (getActor()!=null) {
            return getActor().getUserID();
        }
        return null;
    }

    @ManyToOne
    public SimpleUser getViewer() {
        return mViewer;
    }

    private void setViewer(SimpleUser inViewer) {
        mViewer = inViewer;
    }

    @Transient
    private UserID getViewerUserID() {
        if (getViewer()!=null) {
            return getViewer().getUserID();
        }
        return null;
    }

    @Transient
    private BrokerID getBrokerID() {
        return mBrokerID;
    }

    private void setBrokerID(BrokerID inBrokerID) {
        mBrokerID = inBrokerID;
    }
    @Column(name = "brokerID", nullable = false)
    private String getBrokerIDAsString() {
        return getBrokerID() == null
                ? null
                : getBrokerID().toString();
    }
    private void setBrokerIDAsString(String inValue) {
        setBrokerID(inValue == null
                ? null
                : new BrokerID(inValue));
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
    private SimpleUser mActor; 
    private SimpleUser mViewer; 
    private BrokerID mBrokerID;
    private String mFixMessage;
    private Date mSendingTime;
    private ReportType mReportType;
    private ReportBase mReportBase;
    private static final long serialVersionUID = 1;
}
