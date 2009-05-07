package org.marketcetera.trade;

import org.marketcetera.util.misc.ClassVersion;

import javax.xml.bind.annotation.XmlRootElement;

/* $License$ */
/**
 * Backing object for an order to trade a security. This class is public
 * for the sake of JAXB and is not intended for general use.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$")
@XmlRootElement
public class OrderSingleImpl extends NewOrReplaceOrderImpl implements OrderSingle {

    @Override
    public OrderSingle clone() {
        try {
            // Since this instance has no modifiable fields, 
            // simply return the clone
            return (OrderSingleImpl) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new IllegalStateException(e);
    }
    }

    @Override
    public String toString() {
        return Messages.ORDER_SINGLE_TO_STRING.getText(
                String.valueOf(getAccount()),
                String.valueOf(getCustomFields()),
                String.valueOf(getBrokerID()),
                String.valueOf(getOrderCapacity()),
                String.valueOf(getOrderID()),
                String.valueOf(getOrderType()),
                String.valueOf(getPositionEffect()),
                String.valueOf(getPrice()),
                String.valueOf(getQuantity()),
                String.valueOf(getSecurityType()),
                String.valueOf(getSide()),
                String.valueOf(getSymbol()),
                String.valueOf(getTimeInForce())
        );
    }

    /**
     * Creates an uninitialized instance. This constructor is meant to be
     * used by JAXB.
     */
    OrderSingleImpl() {
    }

    private static final long serialVersionUID = 1L;
}
