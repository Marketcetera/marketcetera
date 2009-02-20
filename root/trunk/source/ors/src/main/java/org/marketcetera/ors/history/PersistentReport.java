package org.marketcetera.ors.history;

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
     * Saves the supplied report to the database.
     *
     * @param inReport The report to be saved.
     *
     * @throws PersistenceException if there were errors saving the
     * report to the database.
     */
    static void save(ReportBase inReport) throws PersistenceException {
        PersistentReport report = new PersistentReport(inReport);
        report.saveRemote(null);
        ReportBaseImpl.assignReportID((ReportBaseImpl) inReport,
                new ReportID(report.getId()));
    }
    /**
     * Creates an instance, given a report.
     *
     * @param inReport the report instance.
     */
    PersistentReport(ReportBase inReport) {
        mReportBase = inReport;
        setBrokerID(inReport.getBrokerID());
        setSendingTime(inReport.getSendingTime());
        if(inReport instanceof HasFIXMessage) {
            setFixMessage(((HasFIXMessage) inReport).getMessage().toString());
        }
        setOriginator(inReport.getOriginator());
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
                            getOriginator());
                    break;
                case CancelReject:
                    returnValue =  Factory.getInstance().createOrderCancelReject(
                            fixMessage, getBrokerID(), getOriginator());
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
     * The attribute employee ID used in JPQL queries
     */
    static final String ATTRIBUTE_SENDING_TIME = "sendingTime";  //$NON-NLS-1$
    /**
     * The entity name as is used in various JPQL Queries
     */
    static final String ENTITY_NAME = PersistentReport.class.getSimpleName();

    private Originator mOriginator;
    private BrokerID mBrokerID;
    private String mFixMessage;
    private Date mSendingTime;
    private ReportType mReportType;
    private ReportBase mReportBase;
    private static final long serialVersionUID = 1;
}
