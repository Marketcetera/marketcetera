/**
 * $License$
 *
 * $Id$
 */
package org.marketcetera.photon.model.marketdata.impl;

import java.math.BigDecimal;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

import org.marketcetera.photon.model.marketdata.MDPackage;
import org.marketcetera.photon.model.marketdata.MDTopOfBook;

import org.marketcetera.util.misc.ClassVersion;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Top Of Book</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.marketcetera.photon.model.marketdata.impl.MDTopOfBookImpl#getBidSize <em>Bid Size</em>}</li>
 *   <li>{@link org.marketcetera.photon.model.marketdata.impl.MDTopOfBookImpl#getBidPrice <em>Bid Price</em>}</li>
 *   <li>{@link org.marketcetera.photon.model.marketdata.impl.MDTopOfBookImpl#getAskSize <em>Ask Size</em>}</li>
 *   <li>{@link org.marketcetera.photon.model.marketdata.impl.MDTopOfBookImpl#getAskPrice <em>Ask Price</em>}</li>
 *   <li>{@link org.marketcetera.photon.model.marketdata.impl.MDTopOfBookImpl#getBidExchange <em>Bid Exchange</em>}</li>
 *   <li>{@link org.marketcetera.photon.model.marketdata.impl.MDTopOfBookImpl#getAskExchange <em>Ask Exchange</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 * @since 2.1.0
 */
@ClassVersion("$Id$")
public class MDTopOfBookImpl
        extends MDItemImpl
        implements MDTopOfBook
{
    /**
     * The default value of the '{@link #getBidSize() <em>Bid Size</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getBidSize()
     * @generated
     * @ordered
     */
    protected static final BigDecimal BID_SIZE_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getBidSize() <em>Bid Size</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getBidSize()
     * @generated
     * @ordered
     */
    protected volatile BigDecimal bidSize = BID_SIZE_EDEFAULT;

    /**
     * The default value of the '{@link #getBidPrice() <em>Bid Price</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getBidPrice()
     * @generated
     * @ordered
     */
    protected static final BigDecimal BID_PRICE_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getBidPrice() <em>Bid Price</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getBidPrice()
     * @generated
     * @ordered
     */
    protected volatile BigDecimal bidPrice = BID_PRICE_EDEFAULT;

    /**
     * The default value of the '{@link #getAskSize() <em>Ask Size</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getAskSize()
     * @generated
     * @ordered
     */
    protected static final BigDecimal ASK_SIZE_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getAskSize() <em>Ask Size</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getAskSize()
     * @generated
     * @ordered
     */
    protected volatile BigDecimal askSize = ASK_SIZE_EDEFAULT;

    /**
     * The default value of the '{@link #getAskPrice() <em>Ask Price</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getAskPrice()
     * @generated
     * @ordered
     */
    protected static final BigDecimal ASK_PRICE_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getAskPrice() <em>Ask Price</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getAskPrice()
     * @generated
     * @ordered
     */
    protected volatile BigDecimal askPrice = ASK_PRICE_EDEFAULT;

    /**
     * The default value of the '{@link #getBidExchange() <em>Bid Exchange</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getBidExchange()
     * @generated
     * @ordered
     */
    protected static final String BID_EXCHANGE_EDEFAULT = ""; //$NON-NLS-1$

    /**
     * The cached value of the '{@link #getBidExchange() <em>Bid Exchange</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getBidExchange()
     * @generated
     * @ordered
     */
    protected volatile String bidExchange = BID_EXCHANGE_EDEFAULT;

    /**
     * The default value of the '{@link #getAskExchange() <em>Ask Exchange</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getAskExchange()
     * @generated
     * @ordered
     */
    protected static final String ASK_EXCHANGE_EDEFAULT = ""; //$NON-NLS-1$

    /**
     * The cached value of the '{@link #getAskExchange() <em>Ask Exchange</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getAskExchange()
     * @generated
     * @ordered
     */
    protected volatile String askExchange = ASK_EXCHANGE_EDEFAULT;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public MDTopOfBookImpl()
    {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass()
    {
        return MDPackage.Literals.MD_TOP_OF_BOOK;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public BigDecimal getBidSize()
    {
        return bidSize;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setBidSize(BigDecimal newBidSize)
    {
        BigDecimal oldBidSize = bidSize;
        bidSize = newBidSize;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this,
                                          Notification.SET,
                                          MDPackage.MD_TOP_OF_BOOK__BID_SIZE,
                                          oldBidSize,
                                          bidSize));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public BigDecimal getBidPrice()
    {
        return bidPrice;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setBidPrice(BigDecimal newBidPrice)
    {
        BigDecimal oldBidPrice = bidPrice;
        bidPrice = newBidPrice;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this,
                                          Notification.SET,
                                          MDPackage.MD_TOP_OF_BOOK__BID_PRICE,
                                          oldBidPrice,
                                          bidPrice));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public BigDecimal getAskSize()
    {
        return askSize;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setAskSize(BigDecimal newAskSize)
    {
        BigDecimal oldAskSize = askSize;
        askSize = newAskSize;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this,
                                          Notification.SET,
                                          MDPackage.MD_TOP_OF_BOOK__ASK_SIZE,
                                          oldAskSize,
                                          askSize));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public BigDecimal getAskPrice()
    {
        return askPrice;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setAskPrice(BigDecimal newAskPrice)
    {
        BigDecimal oldAskPrice = askPrice;
        askPrice = newAskPrice;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this,
                                          Notification.SET,
                                          MDPackage.MD_TOP_OF_BOOK__ASK_PRICE,
                                          oldAskPrice,
                                          askPrice));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getBidExchange()
    {
        return bidExchange;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setBidExchange(String newBidExchange)
    {
        String oldBidExchange = bidExchange;
        bidExchange = newBidExchange;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this,
                                          Notification.SET,
                                          MDPackage.MD_TOP_OF_BOOK__BID_EXCHANGE,
                                          oldBidExchange,
                                          bidExchange));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getAskExchange()
    {
        return askExchange;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setAskExchange(String newAskExchange)
    {
        String oldAskExchange = askExchange;
        askExchange = newAskExchange;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this,
                                          Notification.SET,
                                          MDPackage.MD_TOP_OF_BOOK__ASK_EXCHANGE,
                                          oldAskExchange,
                                          askExchange));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Object eGet(int featureID,
                       boolean resolve,
                       boolean coreType)
    {
        switch (featureID) {
            case MDPackage.MD_TOP_OF_BOOK__BID_SIZE:
                return getBidSize();
            case MDPackage.MD_TOP_OF_BOOK__BID_PRICE:
                return getBidPrice();
            case MDPackage.MD_TOP_OF_BOOK__ASK_SIZE:
                return getAskSize();
            case MDPackage.MD_TOP_OF_BOOK__ASK_PRICE:
                return getAskPrice();
            case MDPackage.MD_TOP_OF_BOOK__BID_EXCHANGE:
                return getBidExchange();
            case MDPackage.MD_TOP_OF_BOOK__ASK_EXCHANGE:
                return getAskExchange();
        }
        return super.eGet(featureID,
                          resolve,
                          coreType);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void eSet(int featureID,
                     Object newValue)
    {
        switch (featureID) {
            case MDPackage.MD_TOP_OF_BOOK__BID_SIZE:
                setBidSize((BigDecimal) newValue);
                return;
            case MDPackage.MD_TOP_OF_BOOK__BID_PRICE:
                setBidPrice((BigDecimal) newValue);
                return;
            case MDPackage.MD_TOP_OF_BOOK__ASK_SIZE:
                setAskSize((BigDecimal) newValue);
                return;
            case MDPackage.MD_TOP_OF_BOOK__ASK_PRICE:
                setAskPrice((BigDecimal) newValue);
                return;
            case MDPackage.MD_TOP_OF_BOOK__BID_EXCHANGE:
                setBidExchange((String) newValue);
                return;
            case MDPackage.MD_TOP_OF_BOOK__ASK_EXCHANGE:
                setAskExchange((String) newValue);
                return;
        }
        super.eSet(featureID,
                   newValue);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void eUnset(int featureID)
    {
        switch (featureID) {
            case MDPackage.MD_TOP_OF_BOOK__BID_SIZE:
                setBidSize(BID_SIZE_EDEFAULT);
                return;
            case MDPackage.MD_TOP_OF_BOOK__BID_PRICE:
                setBidPrice(BID_PRICE_EDEFAULT);
                return;
            case MDPackage.MD_TOP_OF_BOOK__ASK_SIZE:
                setAskSize(ASK_SIZE_EDEFAULT);
                return;
            case MDPackage.MD_TOP_OF_BOOK__ASK_PRICE:
                setAskPrice(ASK_PRICE_EDEFAULT);
                return;
            case MDPackage.MD_TOP_OF_BOOK__BID_EXCHANGE:
                setBidExchange(BID_EXCHANGE_EDEFAULT);
                return;
            case MDPackage.MD_TOP_OF_BOOK__ASK_EXCHANGE:
                setAskExchange(ASK_EXCHANGE_EDEFAULT);
                return;
        }
        super.eUnset(featureID);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public boolean eIsSet(int featureID)
    {
        switch (featureID) {
            case MDPackage.MD_TOP_OF_BOOK__BID_SIZE:
                return BID_SIZE_EDEFAULT == null ? bidSize != null : !BID_SIZE_EDEFAULT.equals(bidSize);
            case MDPackage.MD_TOP_OF_BOOK__BID_PRICE:
                return BID_PRICE_EDEFAULT == null ? bidPrice != null : !BID_PRICE_EDEFAULT.equals(bidPrice);
            case MDPackage.MD_TOP_OF_BOOK__ASK_SIZE:
                return ASK_SIZE_EDEFAULT == null ? askSize != null : !ASK_SIZE_EDEFAULT.equals(askSize);
            case MDPackage.MD_TOP_OF_BOOK__ASK_PRICE:
                return ASK_PRICE_EDEFAULT == null ? askPrice != null : !ASK_PRICE_EDEFAULT.equals(askPrice);
            case MDPackage.MD_TOP_OF_BOOK__BID_EXCHANGE:
                return BID_EXCHANGE_EDEFAULT == null ? bidExchange != null : !BID_EXCHANGE_EDEFAULT.equals(bidExchange);
            case MDPackage.MD_TOP_OF_BOOK__ASK_EXCHANGE:
                return ASK_EXCHANGE_EDEFAULT == null ? askExchange != null : !ASK_EXCHANGE_EDEFAULT.equals(askExchange);
        }
        return super.eIsSet(featureID);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public String toString()
    {
        if (eIsProxy())
            return super.toString();

        StringBuffer result = new StringBuffer(super.toString());
        result.append(" (bidSize: "); //$NON-NLS-1$
        result.append(bidSize);
        result.append(", bidPrice: "); //$NON-NLS-1$
        result.append(bidPrice);
        result.append(", askSize: "); //$NON-NLS-1$
        result.append(askSize);
        result.append(", askPrice: "); //$NON-NLS-1$
        result.append(askPrice);
        result.append(", bidExchange: "); //$NON-NLS-1$
        result.append(bidExchange);
        result.append(", askExchange: "); //$NON-NLS-1$
        result.append(askExchange);
        result.append(')');
        return result.toString();
    }

} //MDTopOfBookImpl
