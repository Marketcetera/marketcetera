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

import org.marketcetera.photon.model.marketdata.MDMarketstat;
import org.marketcetera.photon.model.marketdata.MDPackage;

import org.marketcetera.util.misc.ClassVersion;

/**
 * <!-- begin-user-doc --> An implementation of the model object '
 * <em><b>Marketstat</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>
 * {@link org.marketcetera.photon.model.marketdata.impl.MDMarketstatImpl#getClosePrice
 * <em>Close Price</em>}</li>
 * <li>
 * {@link org.marketcetera.photon.model.marketdata.impl.MDMarketstatImpl#getCloseDate
 * <em>Close Date</em>}</li>
 * <li>
 * {@link org.marketcetera.photon.model.marketdata.impl.MDMarketstatImpl#getPreviousClosePrice
 * <em>Previous Close Price</em>}</li>
 * <li>
 * {@link org.marketcetera.photon.model.marketdata.impl.MDMarketstatImpl#getPreviousCloseDate
 * <em>Previous Close Date</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 * @since 2.0.0
 */
@ClassVersion("$Id$")
public class MDMarketstatImpl extends MDItemImpl implements MDMarketstat {
    /**
     * The default value of the '{@link #getClosePrice() <em>Close Price</em>}'
     * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see #getClosePrice()
     * @generated
     * @ordered
     */
    protected static final BigDecimal CLOSE_PRICE_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getClosePrice() <em>Close Price</em>}'
     * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see #getClosePrice()
     * @generated
     * @ordered
     */
    protected volatile BigDecimal closePrice = CLOSE_PRICE_EDEFAULT;

    /**
     * The default value of the '{@link #getCloseDate() <em>Close Date</em>}'
     * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see #getCloseDate()
     * @generated
     * @ordered
     */
    protected static final String CLOSE_DATE_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getCloseDate() <em>Close Date</em>}'
     * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see #getCloseDate()
     * @generated
     * @ordered
     */
    protected volatile String closeDate = CLOSE_DATE_EDEFAULT;

    /**
     * The default value of the '{@link #getPreviousClosePrice()
     * <em>Previous Close Price</em>}' attribute. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @see #getPreviousClosePrice()
     * @generated
     * @ordered
     */
    protected static final BigDecimal PREVIOUS_CLOSE_PRICE_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getPreviousClosePrice()
     * <em>Previous Close Price</em>}' attribute. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @see #getPreviousClosePrice()
     * @generated
     * @ordered
     */
    protected volatile BigDecimal previousClosePrice = PREVIOUS_CLOSE_PRICE_EDEFAULT;

    /**
     * The default value of the '{@link #getPreviousCloseDate()
     * <em>Previous Close Date</em>}' attribute. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @see #getPreviousCloseDate()
     * @generated
     * @ordered
     */
    protected static final String PREVIOUS_CLOSE_DATE_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getPreviousCloseDate()
     * <em>Previous Close Date</em>}' attribute. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @see #getPreviousCloseDate()
     * @generated
     * @ordered
     */
    protected static final BigDecimal OPEN_PRICE_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getOpenPrice()
     * <em>Previous Close Price</em>}' attribute. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @see #getOpenPrice()
     * @generated
     * @ordered
     */

    protected static final BigDecimal HIGH_PRICE_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getHighPrice() <em>High Price</em>}'
     * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see #getHigh()
     * @generated
     * @ordered
     */

    protected static final BigDecimal LOW_PRICE_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getLowPrice() <em>Low Price</em>}'
     * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see #getLowPrice()
     * @generated
     * @ordered
     */

    protected static final BigDecimal VOLUME_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getVolume() <em>Volume</em>}' attribute.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see #getVolume()
     * @generated
     * @ordered
     */

    protected volatile String previousCloseDate = PREVIOUS_CLOSE_DATE_EDEFAULT;

    /**
     * The cached value of the '{@link #getPreviousCloseDate()
     * <em>Open Price</em>}' attribute. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @see #getOpenPrice()
     * @generated
     * @ordered
     */
    protected volatile BigDecimal OpenPrice = OPEN_PRICE_EDEFAULT;

    /**
     * The cached value of the '{@link #getPreviousCloseDate()
     * <em>High Price</em>}' attribute. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @see #getHighPrice()
     * @generated
     * @ordered
     */
    protected volatile BigDecimal HighPrice = HIGH_PRICE_EDEFAULT;

    /**
     * The cached value of the '{@link #getPreviousCloseDate()
     * <em>Low Price</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc
     * -->
     * 
     * @see #getHighPrice()
     * @generated
     * @ordered
     */
    protected volatile BigDecimal LowPrice = LOW_PRICE_EDEFAULT;

    /**
     * The cached value of the '{@link #getPreviousCloseDate() <em>Volume</em>}'
     * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see #getHighPrice()
     * @generated
     * @ordered
     */
    protected volatile BigDecimal Volume = VOLUME_EDEFAULT;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public MDMarketstatImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return MDPackage.Literals.MD_MARKETSTAT;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public BigDecimal getClosePrice() {
        return closePrice;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public void setClosePrice(BigDecimal newClosePrice) {
        BigDecimal oldClosePrice = closePrice;
        closePrice = newClosePrice;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET,
                    MDPackage.MD_MARKETSTAT__CLOSE_PRICE, oldClosePrice,
                    closePrice));
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public String getCloseDate() {
        return closeDate;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public void setCloseDate(String newCloseDate) {
        String oldCloseDate = closeDate;
        closeDate = newCloseDate;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET,
                    MDPackage.MD_MARKETSTAT__CLOSE_DATE, oldCloseDate,
                    closeDate));
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public BigDecimal getPreviousClosePrice() {
        return previousClosePrice;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public void setPreviousClosePrice(BigDecimal newPreviousClosePrice) {
        BigDecimal oldPreviousClosePrice = previousClosePrice;
        previousClosePrice = newPreviousClosePrice;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET,
                    MDPackage.MD_MARKETSTAT__PREVIOUS_CLOSE_PRICE,
                    oldPreviousClosePrice, previousClosePrice));
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public String getPreviousCloseDate() {
        return previousCloseDate;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public void setPreviousCloseDate(String newPreviousCloseDate) {
        String oldPreviousCloseDate = previousCloseDate;
        previousCloseDate = newPreviousCloseDate;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET,
                    MDPackage.MD_MARKETSTAT__PREVIOUS_CLOSE_DATE,
                    oldPreviousCloseDate, previousCloseDate));
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public Object eGet(int featureID, boolean resolve, boolean coreType) {
        switch (featureID) {
        case MDPackage.MD_MARKETSTAT__CLOSE_PRICE:
            return getClosePrice();
        case MDPackage.MD_MARKETSTAT__CLOSE_DATE:
            return getCloseDate();
        case MDPackage.MD_MARKETSTAT__PREVIOUS_CLOSE_PRICE:
            return getPreviousClosePrice();
        case MDPackage.MD_MARKETSTAT__PREVIOUS_CLOSE_DATE:
            return getPreviousCloseDate();
        case MDPackage.MD_MARKETSTAT__OPEN_PRICE:
            return getOpenPrice();
        case MDPackage.MD_MARKETSTAT__HIGH_PRICE:
            return getHighPrice();
        case MDPackage.MD_MARKETSTAT__LOW_PRICE:
            return getLowPrice();
        case MDPackage.MD_MARKETSTAT__VOLUME:
            return getVolumeTraded();
        }
        return super.eGet(featureID, resolve, coreType);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public void eSet(int featureID, Object newValue) {
        switch (featureID) {
        case MDPackage.MD_MARKETSTAT__CLOSE_PRICE:
            setClosePrice((BigDecimal) newValue);
            return;
        case MDPackage.MD_MARKETSTAT__CLOSE_DATE:
            setCloseDate((String) newValue);
            return;
        case MDPackage.MD_MARKETSTAT__PREVIOUS_CLOSE_PRICE:
            setPreviousClosePrice((BigDecimal) newValue);
            return;
        case MDPackage.MD_MARKETSTAT__PREVIOUS_CLOSE_DATE:
            setPreviousCloseDate((String) newValue);
            return;
        case MDPackage.MD_MARKETSTAT__OPEN_PRICE:
            setOpenPrice((BigDecimal) newValue);
            return;
        case MDPackage.MD_MARKETSTAT__HIGH_PRICE:
            setHighPrice((BigDecimal) newValue);
            return;
        case MDPackage.MD_MARKETSTAT__LOW_PRICE:
            setLowPrice((BigDecimal) newValue);
            return;
        case MDPackage.MD_MARKETSTAT__VOLUME:
            setVolumeTraded((BigDecimal) newValue);
            return;
        }
        super.eSet(featureID, newValue);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public void eUnset(int featureID) {
        switch (featureID) {
        case MDPackage.MD_MARKETSTAT__CLOSE_PRICE:
            setClosePrice(CLOSE_PRICE_EDEFAULT);
            return;
        case MDPackage.MD_MARKETSTAT__CLOSE_DATE:
            setCloseDate(CLOSE_DATE_EDEFAULT);
            return;
        case MDPackage.MD_MARKETSTAT__PREVIOUS_CLOSE_PRICE:
            setPreviousClosePrice(PREVIOUS_CLOSE_PRICE_EDEFAULT);
            return;
        case MDPackage.MD_MARKETSTAT__PREVIOUS_CLOSE_DATE:
            setPreviousCloseDate(PREVIOUS_CLOSE_DATE_EDEFAULT);
            return;
        case MDPackage.MD_MARKETSTAT__OPEN_PRICE:
            setOpenPrice(OPEN_PRICE_EDEFAULT);
            return;
        case MDPackage.MD_MARKETSTAT__HIGH_PRICE:
            setHighPrice(HIGH_PRICE_EDEFAULT);
            return;
        case MDPackage.MD_MARKETSTAT__LOW_PRICE:
            setLowPrice(LOW_PRICE_EDEFAULT);
            return;
        case MDPackage.MD_MARKETSTAT__VOLUME:
            setVolumeTraded(VOLUME_EDEFAULT);
            return;
        }
        super.eUnset(featureID);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public boolean eIsSet(int featureID) {
        switch (featureID) {
        case MDPackage.MD_MARKETSTAT__CLOSE_PRICE:
            return CLOSE_PRICE_EDEFAULT == null ? closePrice != null
                    : !CLOSE_PRICE_EDEFAULT.equals(closePrice);
        case MDPackage.MD_MARKETSTAT__CLOSE_DATE:
            return CLOSE_DATE_EDEFAULT == null ? closeDate != null
                    : !CLOSE_DATE_EDEFAULT.equals(closeDate);
        case MDPackage.MD_MARKETSTAT__PREVIOUS_CLOSE_PRICE:
            return PREVIOUS_CLOSE_PRICE_EDEFAULT == null ? previousClosePrice != null
                    : !PREVIOUS_CLOSE_PRICE_EDEFAULT.equals(previousClosePrice);
        case MDPackage.MD_MARKETSTAT__PREVIOUS_CLOSE_DATE:
            return PREVIOUS_CLOSE_DATE_EDEFAULT == null ? previousCloseDate != null
                    : !PREVIOUS_CLOSE_DATE_EDEFAULT.equals(previousCloseDate);
        case MDPackage.MD_MARKETSTAT__OPEN_PRICE:
            return OPEN_PRICE_EDEFAULT == null ? OpenPrice != null
                    : !OPEN_PRICE_EDEFAULT.equals(OpenPrice);
        case MDPackage.MD_MARKETSTAT__HIGH_PRICE:
            return HIGH_PRICE_EDEFAULT == null ? HighPrice != null
                    : !HIGH_PRICE_EDEFAULT.equals(HighPrice);
        case MDPackage.MD_MARKETSTAT__LOW_PRICE:
            return LOW_PRICE_EDEFAULT == null ? LowPrice != null
                    : !LOW_PRICE_EDEFAULT.equals(LowPrice);
        case MDPackage.MD_MARKETSTAT__VOLUME:
            return VOLUME_EDEFAULT == null ? Volume != null : !VOLUME_EDEFAULT
                    .equals(Volume);
        }
        return super.eIsSet(featureID);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public String toString() {
        if (eIsProxy())
            return super.toString();
        StringBuffer result = new StringBuffer(super.toString());
        result.append(" (closePrice: "); //$NON-NLS-1$
        result.append(closePrice);
        result.append(", closeDate: "); //$NON-NLS-1$
        result.append(closeDate);
        result.append(", previousClosePrice: "); //$NON-NLS-1$
        result.append(previousClosePrice);
        result.append(", previousCloseDate: "); //$NON-NLS-1$
        result.append(previousCloseDate);
        result.append(", previousCloseDate: "); //$NON-NLS-1$
        result.append(", openPrice: ");
        result.append(OpenPrice);
        result.append(", HighPrice: ");
        result.append(HighPrice);
        result.append(", LowPrice: ");
        result.append(LowPrice);
        result.append(", Volume: ");
        result.append(Volume);
        result.append(')');
        return result.toString();
    }

    @Override
    public BigDecimal getHighPrice() {
        return HighPrice;
    }

    public void setHighPrice(BigDecimal newHighPrice) {
        BigDecimal oldHighPrice = HighPrice;
        HighPrice = newHighPrice;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET,
                    MDPackage.MD_MARKETSTAT__HIGH_PRICE, oldHighPrice,
                    HighPrice));
    }

    @Override
    public BigDecimal getLowPrice() {
        return LowPrice;
    }

    public void setLowPrice(BigDecimal newLowPrice) {
        BigDecimal oldLowPrice = LowPrice;
        LowPrice = newLowPrice;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET,
                    MDPackage.MD_MARKETSTAT__LOW_PRICE, oldLowPrice, LowPrice));
    }

    @Override
    public BigDecimal getVolumeTraded() {
        return Volume;
    }

    public void setVolumeTraded(BigDecimal newVolumeTraded) {
        BigDecimal oldVolume = Volume;
        Volume = newVolumeTraded;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET,
                    MDPackage.MD_MARKETSTAT__VOLUME, oldVolume, Volume));
    }

    @Override
    public BigDecimal getOpenPrice() {
        return OpenPrice;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public void setOpenPrice(BigDecimal newOpenPrice) {
        BigDecimal oldOpenPrice = OpenPrice;
        OpenPrice = newOpenPrice;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET,
                    MDPackage.MD_MARKETSTAT__OPEN_PRICE, oldOpenPrice,
                    OpenPrice));
    }

} // MDMarketstatImpl

