package org.marketcetera.trade;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.core.MSymbol;

import java.math.BigDecimal;
import java.util.Map;
import java.util.HashMap;

/* $License$ */
/**
 * Base class for orders. 
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$") //$NON-NLS-1$
class OrderBaseImpl implements OrderBase {
    @Override
    public OrderID getOrderID() {
        return mOrderID;
    }

    @Override
    public void setOrderID(OrderID inOrderID) {
        mOrderID = inOrderID;
    }

    @Override
    public Side getSide() {
        return mSide;
    }

    @Override
    public void setSide(Side inSide) {
        mSide = inSide;
    }

    @Override
    public MSymbol getSymbol() {
        return mSymbol;
    }

    @Override
    public void setSymbol(MSymbol inSymbol) {
        mSymbol = inSymbol;
    }

    @Override
    public BigDecimal getQuantity() {
        return mQuantity;
    }

    @Override
    public void setQuantity(BigDecimal inQuantity) {
        mQuantity = inQuantity;
    }

    @Override
    public Map<String, String> getCustomFields() {
        return mCustomFields == null
                ? null
                : new HashMap<String,String>(mCustomFields);
    }

    @Override
    public void setCustomFields(Map<String, String> inCustomFields) {
        mCustomFields = inCustomFields == null
                ? null
                : new HashMap<String,String>(inCustomFields);
    }

    @Override
    public SecurityType getSecurityType() {
        return mSymbol == null
                ? null
                : mSymbol.getSecurityType(); 
    }

    @Override
    public DestinationID getDestinationID() {
        return mCountepartyID;
    }

    @Override
    public void setDestinationID(DestinationID inDestinationID) {
        mCountepartyID = inDestinationID;
    }

    @Override
    public String getAccount() {
        return mAccount;
    }

    @Override
    public void setAccount(String inAccount) {
        mAccount = inAccount;
    }
    private OrderID mOrderID;
    private Side mSide;
    private BigDecimal mQuantity;
    private Map<String,String> mCustomFields;
    private DestinationID mCountepartyID;
    private String mAccount;
    private MSymbol mSymbol;
    private static final long serialVersionUID = 1L;
}
