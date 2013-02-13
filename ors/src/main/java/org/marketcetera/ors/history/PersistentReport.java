package org.marketcetera.ors.history;

import java.util.Date;

import javax.persistence.*;

import org.marketcetera.event.HasFIXMessage;
import org.marketcetera.ors.Principals;
import org.marketcetera.ors.security.User;
import org.marketcetera.ors.security.UserService;
import org.marketcetera.persist.ApplicationContextRepository;
import org.marketcetera.persist.EntityBase;
import org.marketcetera.trade.*;
import org.marketcetera.util.log.I18NBoundMessage1P;
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
@Access(AccessType.FIELD)
@NamedQuery(name = "forOrderID",query = "select e from PersistentReport e where e.orderID = :orderID")
@ClassVersion("$Id$")
class PersistentReport
        extends EntityBase
{
    /**
     * Returns the principals associated with the report with given
     * order ID.
     *
     * @param orderID The order ID.
     *
     * @return The principals. If no report with the given order ID
     * exists, {@link Principals#UNKNOWN} is returned, and no
     * exception is thrown.
     *
     * @throws PersistenceException if there were errors accessing the
     * report.
     */
    static Principals getPrincipals(final OrderID orderID)
    {
        
//        return executeRemote(new Transaction<Principals>() {
//            private static final long serialVersionUID=1L;
//
//            @Override
//            public Principals execute
//                (EntityManager em,
//                 PersistContext context)
//            {
//                Query query=em.createNamedQuery("forOrderID"); //$NON-NLS-1$
//                query.setParameter("orderID",orderID); //$NON-NLS-1$
//                List<?> list=query.getResultList();
//                if (list.isEmpty()) {
//                    return Principals.UNKNOWN;
//                }
//                PersistentReport report=(PersistentReport)(list.get(0));
//                return new Principals(report.getActorID(),
//                                      report.getViewerID());
//            }
//        },null);
        throw new UnsupportedOperationException(); // TODO COLIN
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
        UserService userService = ApplicationContextRepository.getInstance().getBean(UserService.class);
        if(inReport.getActorID()!=null) {
            setActor(userService.findOne(inReport.getActorID().getValue()));
        }
        if (inReport.getViewerID()!=null) {
            setActor(userService.findOne(inReport.getViewerID().getValue()));
        }
        if(inReport instanceof ExecutionReport) {
            mReportType = ReportType.ExecutionReport;
        } else if (inReport instanceof OrderCancelReject) {
            mReportType = ReportType.CancelReject;
        } else {
            //You added new report types but forgot to update the code
            //to persist them.
            throw new IllegalArgumentException();
        }
        throw new UnsupportedOperationException(); // TODO COLIN
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
    ReportBase toReport()
            throws ReportPersistenceException
    {
        ReportBase returnValue = null;
        String fixMsgString = null;
        try {
            fixMsgString = getFixMessage();
            Message fixMessage = new Message(fixMsgString);
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
//    @Override
//    protected void postSaveLocal(EntityManager em,
//                                 EntityBase merged,
//                                 PersistContext context)
//    {
//        super.postSaveLocal(em, merged, context);
//        PersistentReport mergedReport = (PersistentReport) merged;
//        //Save the summary if the report is an execution report.
//        if(mergedReport.getReportType() == ReportType.ExecutionReport) {
//            new ExecutionReportSummary((ExecutionReport)mReportBase,
//                                       mergedReport).localSave(em,
//                                                               context);
//        }
//    }
    private Originator getOriginator()
    {
        return mOriginator;
    }
    private void setOriginator(Originator inOriginator)
    {
        mOriginator = inOriginator;
    }
    OrderID getOrderID()
    {
        return mOrderID;
    }
    private void setOrderID(OrderID inOrderID)
    {
        mOrderID = inOrderID;
    }
    public User getActor()
    {
        return actor;
    }
    private void setActor(User inActor)
    {
        actor = inActor;
    }
    UserID getActorID()
    {
        if(getActor()==null) {
            return null;
        }
        return getActor().getUserID();
    }

    public User getViewer() {
        return mViewer;
    }

    private void setViewer(User inViewer) {
        mViewer = inViewer;
    }

    @Transient
    UserID getViewerID() {
        if (getViewer()==null) {
            return null;
        }
        return getViewer().getUserID();
    }

    BrokerID getBrokerID() {
        return mBrokerID;
    }

    private void setBrokerID(BrokerID inBrokerID) {
        mBrokerID = inBrokerID;
    }
    @Column(name = "brokerID")
    @SuppressWarnings("unused")
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
    @SuppressWarnings("unused")
    private long getReportIDAsLong() {
        return getReportID().longValue();
    }
    @SuppressWarnings("unused")
    private void setReportIDAsLong(long inValue) {
        setReportID(new ReportID(inValue));
    }

    private String getFixMessage() {
        return mFixMessage;
    }

    private void setFixMessage(String inFIXMessage) {
        mFixMessage = inFIXMessage;
    }

    @SuppressWarnings("unused")
    private Date getSendingTime() {
        return mSendingTime;
    }

    private void setSendingTime(Date inSendingTime) {
        mSendingTime = inSendingTime;
    }

    private ReportType getReportType() {
        return mReportType;
    }

    @SuppressWarnings("unused")
    private void setReportType(ReportType inReportType) {
        mReportType = inReportType;
    }
    /**
     * Create a new PersistentReport instance.
     */
    @SuppressWarnings("unused")
    private PersistentReport() {}
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
    @Embedded
    @AttributeOverrides( { @AttributeOverride(name="value",column=@Column(name="orderID",nullable=false)) } )
    private OrderID mOrderID;
    @ManyToOne
    private User actor; 
    @ManyToOne
    private User mViewer; 
    @Lob
    @Column(nullable=false)
    private String mFixMessage;
    @Column(nullable=false)
    private Date mSendingTime;
    @Column(nullable = false)
    private ReportType mReportType;
    @Transient
    private ReportBase mReportBase;
    @Transient
    private BrokerID mBrokerID;
    @Transient
    private ReportID mReportID;
    @Transient
    private Originator mOriginator;
    private static final long serialVersionUID = 1;
}
