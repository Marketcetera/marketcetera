package org.marketcetera.core.trade;

import javax.xml.bind.annotation.XmlRootElement;
import org.marketcetera.api.attributes.ClassVersion;

/* $License$ */
/**
 * ReplaceOrder Implementation. This class is public for the sake of JAXB
 * and is not intended for general use.
 *
 * @author anshul@marketcetera.com
 * @version $Id: OrderReplaceImpl.java 16063 2012-01-31 18:21:55Z colin $
 * @since 1.0.0
 */
@ClassVersion("$Id: OrderReplaceImpl.java 16063 2012-01-31 18:21:55Z colin $")
@XmlRootElement
public class OrderReplaceImpl extends NewOrReplaceOrderImpl implements OrderReplace {

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
        return mDestOrderID;
    }

    @Override
    public void setBrokerOrderID(String inDestOrderID) {
        mDestOrderID = inDestOrderID;
    }

    @Override
    public String toString() {
        return Messages.ORDER_REPLACE_TO_STRING.getText(
                String.valueOf(getAccount()),
                String.valueOf(getCustomFields()),
                String.valueOf(getBrokerID()),
                String.valueOf(getOrderCapacity()),
                String.valueOf(getOrderID()),
                String.valueOf(getOrderType()),
                String.valueOf(getOriginalOrderID()),
                String.valueOf(getPositionEffect()),
                String.valueOf(getPrice()),
                String.valueOf(getQuantity()),
                String.valueOf(getSecurityType()),
                String.valueOf(getSide()),
                String.valueOf(getInstrument()),
                String.valueOf(getTimeInForce()),
                String.valueOf(getBrokerOrderID()),
                String.valueOf(getText())
        );
    }

    /**
     * Creates an uninitialized instance. This constructor is meant to be
     * used by JAXB.
     */
    OrderReplaceImpl() {
    }

    private OrderID mOriginalOrderID;
    private static final long serialVersionUID = 1L;
    private String mDestOrderID;
}
