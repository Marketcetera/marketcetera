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
     * Creates an instance.
     *
     * @param inReportID the unique ID for this report
     * @param inMessage the FIX Message.
     * @param inDestinationID the broker / destinationID from which this
     */
    protected ReportBaseImpl(ReportID inReportID, Message inMessage,
                             DestinationID inDestinationID) {
        super(inMessage);
        mReportID = inReportID;
        mDestinationID = inDestinationID;
    }

    /**
     * Creates an instance. This empty constructor is intended for use
     * by JAXB.
     */

    protected ReportBaseImpl() {
        mDestinationID = null;
        mReportID = null;
    }
    private final ReportID mReportID;
    private final DestinationID mDestinationID;
    private static final long serialVersionUID = 1L;
}
