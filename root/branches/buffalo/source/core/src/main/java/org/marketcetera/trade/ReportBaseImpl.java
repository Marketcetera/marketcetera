package org.marketcetera.trade;

import org.marketcetera.util.misc.ClassVersion;
import quickfix.Message;

import java.util.Date;

import javax.xml.bind.annotation.XmlSeeAlso;

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
@ClassVersion("$Id$") //$NON-NLS-1$
public class ReportBaseImpl extends FIXMessageWrapper implements ReportBase {

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
    public BrokerID getBrokerID() {
        return mBrokerID;
    }

    @Override
    public synchronized Date getSendingTime() {
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

    @Override
    public Originator getOriginator() {
        return mOriginator;
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
     * @param inActorID the ID of the actor user of this report. It may be null.
     * @param inViewerID the ID of the viewer user of this report. It may be null.
     */
    protected ReportBaseImpl(Message inMessage,
                             BrokerID inBrokerID,
                             Originator inOriginator,
                             UserID inActorID,
                             UserID inViewerID) {
        super(inMessage);
        mBrokerID = inBrokerID;
        mOriginator = inOriginator;
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
        mActorID = null;
        mViewerID = null;
    }

    private ReportID mReportID = null;
    private final BrokerID mBrokerID;
    private static final long serialVersionUID = 1L;
    private final Originator mOriginator;
    private final UserID mActorID;
    private final UserID mViewerID;
}
