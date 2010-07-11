package org.marketcetera.trade;

import org.marketcetera.util.misc.ClassVersion;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlSeeAlso;
import java.math.BigDecimal;
import java.util.Map;
import java.util.HashMap;

/* $License$ */
/**
 * Base class for orders. This class is public for the sake of
 * JAXB and is not intended for general use.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$") //$NON-NLS-1$
/*
 * Use field accessors otherwise custom fields do not get unmarshalled. This
 * happens because JAXB expects to be able to modify the Map after supplying it
 * to setCustomFields()!  See JAXB Bug # 596
 * https://jaxb.dev.java.net/issues/show_bug.cgi?id=596
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlSeeAlso
    ({OrderCancelImpl.class,
      OrderReplaceImpl.class,
      OrderSingleImpl.class,
      Equity.class,
      Option.class,
      Future.class})
//todo: figure out a way to dynamic add instrument types to the XmlSeeAlso list
public class OrderBaseImpl implements OrderBase {
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
    public Instrument getInstrument() {
        return mInstrument;
    }

    @Override
    public void setInstrument(Instrument inInstrument) {
        mInstrument = inInstrument;
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
        return mInstrument == null? null: mInstrument.getSecurityType(); 
    }

    @Override
    public BrokerID getBrokerID() {
        return mBrokerID;
    }

    @Override
    public void setBrokerID(BrokerID inBrokerID) {
        mBrokerID = inBrokerID;
    }

    @Override
    public String getAccount() {
        return mAccount;
    }

    @Override
    public void setAccount(String inAccount) {
        mAccount = inAccount;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.OrderBase#getText()
     */
    @Override
    public String getText()
    {
        return mText;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.OrderBase#setText(java.lang.String)
     */
    @Override
    public void setText(String inText)
    {
        mText = inText;
    }
    private OrderID mOrderID;
    private Side mSide;
    private BigDecimal mQuantity;
    private Map<String,String> mCustomFields;
    private BrokerID mBrokerID;
    private String mAccount;
    private String mText;
    private Instrument mInstrument;
    private static final long serialVersionUID = 1L;
}
