package org.marketcetera.trade;

import java.math.BigDecimal;

import org.marketcetera.util.misc.ClassVersion;

/* $License$ */
/**
 * Orders sent out by the system to Brokers. Orders of this type can 
 * be sent to any broker. The system will translate this order to the 
 * appropriate messages / protocol used by the broker. 
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$")
public interface OrderBase
        extends Order,HasCustomFields
{
    /**
     * The client assigned OrderID for this order. The factory
     * assigns orderIDs when creating orders. 
     *
     * @return the client assigned orderID for this order.
     *
     */
    OrderID getOrderID();

    /**
     * Sets the client assigned OrderID for this order.
     *
     * @param inOrderID the client assigned orderID for this order.
     *
     * @see #getOrderID() 
     */
    void setOrderID(OrderID inOrderID);
    /**
     * Gets the Side for the Order.
     *
     * @return the order Side.
     */
    Side getSide();

    /**
     * Sets the Side for the Order.
     *
     * @param inSide the order Side.
     */
    void setSide(Side inSide);
    
    /**
     * Gets the instrument.
     *
     * @return the instrument.
     */
    public Instrument getInstrument();

    /**
     * Sets the instrument.
     *
     * @param inInstrument the instrument.
     */
    public void setInstrument(Instrument inInstrument);

    /**
     * Gets the quantity for the Order.
     *
     * @return the quantity.
     */
    BigDecimal getQuantity();

    /**
     * Sets the quantity for the Order.
     *
     * @param inQuantity the quantity.
     */
    void setQuantity(BigDecimal inQuantity);
    /**
     * Gets the account for the order. An account may be optionally
     * specified for an order.
     *
     * @return the account.
     */
    String getAccount();

    /**
     * Sets the account for the order.
     *
     * @param inAccount the account.
     */
    void setAccount(String inAccount);
    /**
     * Gets the text for the order. Text may be optionally
     * specified for an order.
     *
     * @return the text.
     */
    String getText();
    /**
     * Sets the text for the order.
     *
     * @param inText the text.
     */
    void setText(String inText);
}
