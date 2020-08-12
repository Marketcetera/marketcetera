package org.marketcetera.trade;

import java.util.Date;

import javax.xml.bind.annotation.XmlSeeAlso;

import org.marketcetera.util.misc.ClassVersion;

import quickfix.Message;

/* $License$ */
/**
 * The base class for reports. This class is public for the sake of
 * JAXB and is not intended for general use.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.0.0
 */
@XmlSeeAlso
    ({ExecutionReportImpl.class,
      OrderCancelRejectImpl.class})
@ClassVersion("$Id$")
public class ReportBaseImpl
        extends FIXMessageWrapper
        implements ReportBase,HasMutableReportID
{

    @Override
    public synchronized OrderID getOrderID() {
        return FIXUtil.getOrderID(getMessage());
    }

    @Override
    public synchronized OrderID getOriginalOrderID() {
        return FIXUtil.getOriginalOrderID(getMessage());
    }

    @Override
    public synchronized OrderStatus getOrderStatus() {
        return FIXUtil.getOrderStatus(getMessage());
    }

    @Override
    public synchronized String getText() {
        return FIXUtil.getText(getMessage());
    }

    @Override
    public BrokerID getBrokerId() {
        return mBrokerID;
    }

    @Override
    public synchronized java.time.LocalDateTime getSendingTime() {
        return FIXUtil.getSendingTime(getMessage());
    }

    @Override
    public synchronized String getBrokerOrderID() {
        return FIXUtil.getBrokerOrderID(getMessage());
    }

    @Override
    public ReportID getReportID() {
        return mReportID;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.HasMutableReportID#setReportID(org.marketcetera.trade.ReportID)
     */
    @Override
    public void setReportID(ReportID inReportId)
    {
        mReportID = inReportId;
    }
    @Override
    public Originator getOriginator() {
        return mOriginator;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.ReportBase#getHierarchy()
     */
    @Override
    public Hierarchy getHierarchy()
    {
        return hierarchy;
    }
    @Override
    public UserID getActorID() {
        return mActorID;
    }

    @Override
    public UserID getViewerID() {
        return mViewerID;
    }

    /**
     * This method is provided to assign ReportIDs to the instances
     * after they have been persisted. This method is an implementation
     * artifact and is not meant to be used by the clients of this API.
     *
     * @param inInstance The report instance.
     * @param inReportID The reportID that needs to be assigned to the instance.
     */
    public static void assignReportID(ReportBaseImpl inInstance,
                                      ReportID inReportID) {
        inInstance.mReportID = inReportID;
    }

    /**
     * Creates an instance.
     *
     * @param inMessage the FIX Message.
     * @param inBrokerID the brokerID from which this report originated.
     * @param inOriginator the originator of this report.
     * @param inHierarchy a <code>Hierarchy</code> value
     * @param inActorID the ID of the actor user of this report. It may be null.
     * @param inViewerID the ID of the viewer user of this report. It may be null.
     */
    protected ReportBaseImpl(Message inMessage,
                             BrokerID inBrokerID,
                             Originator inOriginator,
                             Hierarchy inHierarchy,
                             UserID inActorID,
                             UserID inViewerID) {
        super(inMessage);
        mBrokerID = inBrokerID;
        mOriginator = inOriginator;
        hierarchy = inHierarchy;
        mActorID = inActorID;
        mViewerID = inViewerID;
    }

    /**
     * Creates an instance. This empty constructor is intended for use
     * by JAXB.
     */

    protected ReportBaseImpl() {
        mBrokerID = null;
        mOriginator = null;
        hierarchy = null;
        mActorID = null;
        mViewerID = null;
    }

    private ReportID mReportID = null;
    private final BrokerID mBrokerID;
    private static final long serialVersionUID = 2L;
    private final Originator mOriginator;
    private final Hierarchy hierarchy;
    private final UserID mActorID;
    private final UserID mViewerID;
}
