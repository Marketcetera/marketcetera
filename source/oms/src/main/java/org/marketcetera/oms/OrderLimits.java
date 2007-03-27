package org.marketcetera.oms;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.quickfix.FIXMessageUtil;
import quickfix.Message;
import quickfix.FieldNotFound;
import quickfix.field.Price;
import quickfix.field.Symbol;
import quickfix.field.OrderQty;
import quickfix.field.OrdType;

import java.math.BigDecimal;

/**
 * @author toli
 * @version $Id$
 */

@ClassVersion("$Id$")
public class OrderLimits {
    // Share-related
    private BigDecimal maxQuantityPerOrder;
    private BigDecimal maxNotionalPerOrder; // share price * numShares
    private BigDecimal minPrice;
    private BigDecimal maxPrice;

    private boolean disallowMarketOrders = false;

    public BigDecimal getMaxQuantityPerOrder() {
        return maxQuantityPerOrder;
    }

    public void setMaxQuantityPerOrder(BigDecimal maxQuantityPerOrder) {
        this.maxQuantityPerOrder = maxQuantityPerOrder;
    }

    public BigDecimal getMaxNotionalPerOrder() {
        return maxNotionalPerOrder;
    }

    public void setMaxNotionalPerOrder(BigDecimal maxNotionalPerOrder) {
        this.maxNotionalPerOrder = maxNotionalPerOrder;
    }

    public BigDecimal getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(BigDecimal minPrice) {
        this.minPrice = minPrice;
    }

    public BigDecimal getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(BigDecimal maxPrice) {
        this.maxPrice = maxPrice;
    }

    public boolean isDisallowMarketOrders() {
        return disallowMarketOrders;
    }

    public void setDisallowMarketOrders(boolean disallowMarketOrders) {
        this.disallowMarketOrders = disallowMarketOrders;
    }

    /** Goes through all the order limits and verifies them
     *
     * @param inMessage Message on which to verify order limits
     * @throws OrderLimitException in case the order limits are violated.
     */
    public void verifyOrderLimits(Message inMessage) throws OrderLimitException, FieldNotFound {
        if (FIXMessageUtil.isOrderSingle(inMessage) ||
            FIXMessageUtil.isCancelReplaceRequest(inMessage)) {
            if(disallowMarketOrders) {
                verifyMarketOrder(inMessage, OMSMessageKey.ERROR_OL_MARKET_NOT_ALLOWED);
            }
            verifyMaxQty(inMessage);
            verifyMaxNotional(inMessage);
            verifyMinPrice(inMessage);
            verifyMaxPrice(inMessage);
        }
    }

    protected void verifyMaxPrice(Message inMessage) throws FieldNotFound, OrderLimitException {
        BigDecimal maxPrice = getMaxPrice();
        if(maxPrice != null) {
            verifyMarketOrder(inMessage, OMSMessageKey.ERROR_OL_MARKET_NOT_ALLOWED_PRICE);
            BigDecimal price = new BigDecimal(inMessage.getString(Price.FIELD));
            if(maxPrice.compareTo(price) < 0) {
                throw OrderLimitException.createMaxPriceException(price, maxPrice, inMessage.getString(Symbol.FIELD));
            }
        }
    }

    protected void verifyMinPrice(Message inMessage) throws FieldNotFound, OrderLimitException {
        BigDecimal minPrice = getMinPrice();
        if(minPrice != null) {
            verifyMarketOrder(inMessage, OMSMessageKey.ERROR_OL_MARKET_NOT_ALLOWED_PRICE);
            BigDecimal price = new BigDecimal(inMessage.getString(Price.FIELD));
            if(minPrice.compareTo(price) > 0) {
                throw OrderLimitException.createMinPriceException(price, minPrice, inMessage.getString(Symbol.FIELD));
            }
        }
    }

    protected void verifyMaxNotional(Message inMessage) throws FieldNotFound, OrderLimitException {
        BigDecimal maxNotional = getMaxNotionalPerOrder();
        if(maxNotional != null) {
            verifyMarketOrder(inMessage, OMSMessageKey.ERROR_OL_MARKET_NOT_ALLOWED_PRICE);
            BigDecimal price = new BigDecimal(inMessage.getString(Price.FIELD));
            BigDecimal qty = new BigDecimal(inMessage.getString(OrderQty.FIELD));
            BigDecimal notional = price.multiply(qty);

            if(maxNotional.compareTo(notional) < 0) {
                throw OrderLimitException.createMaxNotionalException(notional, maxNotional, inMessage.getString(Symbol.FIELD));
            }
        }
    }

    protected void verifyMaxQty(Message inMessage)  throws FieldNotFound, OrderLimitException {
        BigDecimal maxQty = getMaxQuantityPerOrder();
        if(maxQty != null) {
            BigDecimal qty = new BigDecimal(inMessage.getString(OrderQty.FIELD));

            if(maxQty.compareTo(qty) < 0) {
                throw OrderLimitException.createMaxQuantityException(qty, maxQty, inMessage.getString(Symbol.FIELD));
            }
        }
    }

    protected void verifyMarketOrder(Message inMessage, OMSMessageKey msgKey) throws OrderLimitException, FieldNotFound {
        if(OrdType.MARKET == inMessage.getChar(OrdType.FIELD)) {
            throw new OrderLimitException(msgKey.getLocalizedMessage(inMessage.getString(Symbol.FIELD)));
        }
    }


}