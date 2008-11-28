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

    /**
     * Creates an instance.
     *
     * @param inMessage the FIX Message.
     * @param inDestinationID the broker / destinationID from which this
     * FIX Message was received.
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

    private final DestinationID mDestinationID;
    private static final long serialVersionUID = 1L;
}
