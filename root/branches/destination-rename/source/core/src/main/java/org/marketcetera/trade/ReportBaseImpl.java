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
 * @since $Release$
 */
@XmlSeeAlso
    ({ExecutionReportImpl.class,
      OrderCancelRejectImpl.class})
@ClassVersion("$Id$") //$NON-NLS-1$
public class ReportBaseImpl extends FIXMessageWrapper implements ReportBase {

    @Override
    public OrderID getOrderID() {
        return FIXUtil.getOrderID(getMessage());
    }

    @Override
    public OrderID getOriginalOrderID() {
        return FIXUtil.getOriginalOrderID(getMessage());
    }

    @Override
    public OrderStatus getOrderStatus() {
        return FIXUtil.getOrderStatus(getMessage());
    }

    @Override
    public String getText() {
        return FIXUtil.getText(getMessage());
    }

    @Override
    public DestinationID getDestinationID() {
        return mDestinationID;
    }

    @Override
    public Date getSendingTime() {
        return FIXUtil.getSendingTime(getMessage());
    }

    @Override
    public String getDestinationOrderID() {
        return FIXUtil.getDestinationOrderID(getMessage());
    }

    @Override
    public ReportID getReportID() {
        return mReportID;
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
     * @param inDestinationID the broker / destinationID from which this
     */
    protected ReportBaseImpl(Message inMessage,
                             DestinationID inDestinationID) {
        super(inMessage);
        mDestinationID = inDestinationID;
    }

    /**
     * Creates an instance. This empty constructor is intended for use
     * by JAXB.
     */

    protected ReportBaseImpl() {
        mDestinationID = null;
    }
    private ReportID mReportID = null;
    private final DestinationID mDestinationID;
    private static final long serialVersionUID = 1L;
}
