package org.marketcetera.core.trade;

import javax.xml.bind.annotation.XmlRootElement;
import org.marketcetera.api.attributes.ClassVersion;

/* $License$ */
/**
 * Order Cancel message implementation. This class is public for the sake
 * of JAXB and is not intended for general use.
 *
 * @author anshul@marketcetera.com
 * @version $Id: OrderCancelImpl.java 16063 2012-01-31 18:21:55Z colin $
 * @since 1.0.0
 */
@ClassVersion("$Id: OrderCancelImpl.java 16063 2012-01-31 18:21:55Z colin $")
@XmlRootElement
public class OrderCancelImpl extends OrderBaseImpl implements OrderCancel {
    @Override
    public OrderID getOriginalOrderID() {
        return mOriginalOrderID;
    }

    @Override
    public void setOriginalOrderID(OrderID inOrderID) {
        mOriginalOrderID = inOrderID;
    }

    @Override
    public String getBrokerOrderID() {
        return mBrokerOrderID;
    }

    @Override
    public void setBrokerOrderID(String inDestOrderID) {
        mBrokerOrderID = inDestOrderID;
    }

    @Override
    public String toString() {
        return Messages.ORDER_CANCEL_TO_STRING.getText(
                String.valueOf(getAccount()),
                String.valueOf(getCustomFields()),
                String.valueOf(getBrokerID()),
                String.valueOf(getOrderID()),
                String.valueOf(getOriginalOrderID()),
                String.valueOf(getQuantity()),
                String.valueOf(getSecurityType()),
                String.valueOf(getSide()),
                String.valueOf(getInstrument()),
                String.valueOf(getBrokerOrderID()),
                String.valueOf(getText())
        );
    }

    /**
     * Creates an uninitialized instance. This constructor is meant to be
     * used by JAXB.
     */
    OrderCancelImpl() {
    }

    private OrderID mOriginalOrderID;
    private String mBrokerOrderID;
    private static final long serialVersionUID = 1L;
}
