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
 *   <li>{@link org.marketcetera.photon.model.marketdata.impl.MDMarketstatImpl#getClosePrice <em>Close Price</em>}</li>
 *   <li>{@link org.marketcetera.photon.model.marketdata.impl.MDMarketstatImpl#getCloseDate <em>Close Date</em>}</li>
 *   <li>{@link org.marketcetera.photon.model.marketdata.impl.MDMarketstatImpl#getPreviousClosePrice <em>Previous Close Price</em>}</li>
 *   <li>{@link org.marketcetera.photon.model.marketdata.impl.MDMarketstatImpl#getPreviousCloseDate <em>Previous Close Date</em>}</li>
 *   <li>{@link org.marketcetera.photon.model.marketdata.impl.MDMarketstatImpl#getHighPrice <em>High Price</em>}</li>
 *   <li>{@link org.marketcetera.photon.model.marketdata.impl.MDMarketstatImpl#getLowPrice <em>Low Price</em>}</li>
 *   <li>{@link org.marketcetera.photon.model.marketdata.impl.MDMarketstatImpl#getVolumeTraded <em>Volume Traded</em>}</li>
 *   <li>{@link org.marketcetera.photon.model.marketdata.impl.MDMarketstatImpl#getOpenPrice <em>Open Price</em>}</li>
 *   <li>{@link org.marketcetera.photon.model.marketdata.impl.MDMarketstatImpl#getVolume <em>Volume</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 * @since 2.1.0
 */
@ClassVersion("$Id$")
public class MDMarketstatImpl
        extends MDItemImpl
        implements MDMarketstat
{
    /**
     * The default value of the '{@link #getClosePrice() <em>Close Price</em>}' attribute.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @see #getClosePrice()
     * @generated
     * @ordered
     */
    protected static final BigDecimal CLOSE_PRICE_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getClosePrice() <em>Close Price</em>}' attribute.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @see #getClosePrice()
     * @generated
     * @ordered
     */
    protected volatile BigDecimal closePrice = CLOSE_PRICE_EDEFAULT;

    /**
     * The default value of the '{@link #getCloseDate() <em>Close Date</em>}' attribute.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @see #getCloseDate()
     * @generated
     * @ordered
     */
    protected static final String CLOSE_DATE_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getCloseDate() <em>Close Date</em>}' attribute.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @see #getCloseDate()
     * @generated
     * @ordered
     */
    protected volatile String closeDate = CLOSE_DATE_EDEFAULT;

    /**
     * The default value of the '{@link #getPreviousClosePrice() <em>Previous Close Price</em>}' attribute.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @see #getPreviousClosePrice()
     * @generated
     * @ordered
     */
    protected static final BigDecimal PREVIOUS_CLOSE_PRICE_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getPreviousClosePrice() <em>Previous Close Price</em>}' attribute.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @see #getPreviousClosePrice()
     * @generated
     * @ordered
     */
    protected volatile BigDecimal previousClosePrice = PREVIOUS_CLOSE_PRICE_EDEFAULT;

    /**
     * The default value of the '{@link #getPreviousCloseDate() <em>Previous Close Date</em>}' attribute.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @see #getPreviousCloseDate()
     * @generated
     * @ordered
     */
    protected static final String PREVIOUS_CLOSE_DATE_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getPreviousCloseDate() <em>Previous Close Date</em>}' attribute.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @see #getPreviousCloseDate()
     * @generated
     * @ordered
     */

    protected volatile String previousCloseDate = PREVIOUS_CLOSE_DATE_EDEFAULT;

    /**
     * The default value of the '{@link #getHighPrice() <em>High Price</em>}' attribute.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @see #getHighPrice()
     * @generated
     * @ordered
     */

    protected static final BigDecimal HIGH_PRICE_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getHighPrice() <em>High Price</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getHighPrice()
     * @generated
     * @ordered
     */
    protected volatile BigDecimal highPrice = HIGH_PRICE_EDEFAULT;

    /**
     * The default value of the '{@link #getLowPrice() <em>Low Price</em>}' attribute.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @see #getLowPrice()
     * @generated
     * @ordered
     */

    protected static final BigDecimal LOW_PRICE_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getLowPrice() <em>Low Price</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getLowPrice()
     * @generated
     * @ordered
     */
    protected volatile BigDecimal lowPrice = LOW_PRICE_EDEFAULT;

    /**
     * The default value of the '{@link #getVolumeTraded() <em>Volume Traded</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getVolumeTraded()
     * @generated
     * @ordered
     */
    protected static final BigDecimal VOLUME_TRADED_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getVolumeTraded() <em>Volume Traded</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getVolumeTraded()
     * @generated
     * @ordered
     */
    protected volatile BigDecimal volumeTraded = VOLUME_TRADED_EDEFAULT;

    /**
     * The default value of the '{@link #getOpenPrice() <em>Open Price</em>}' attribute.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @see #getOpenPrice()
     * @generated
     * @ordered
     */
    protected static final BigDecimal OPEN_PRICE_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getOpenPrice() <em>Open Price</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getOpenPrice()
     * @generated
     * @ordered
     */
    protected volatile BigDecimal openPrice = OPEN_PRICE_EDEFAULT;

    /**
     * The default value of the '{@link #getVolume() <em>Volume</em>}' attribute.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @see #getVolume()
     * @generated
     * @ordered
     */

    protected static final BigDecimal VOLUME_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getVolume() <em>Volume</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getVolume()
     * @generated
     * @ordered
     */
    protected volatile BigDecimal volume = VOLUME_EDEFAULT;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public MDMarketstatImpl()
    {
        super();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass()
    {
        return MDPackage.Literals.MD_MARKETSTAT;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public BigDecimal getClosePrice()
    {
        return closePrice;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public void setClosePrice(BigDecimal newClosePrice)
    {
        BigDecimal oldClosePrice = closePrice;
        closePrice = newClosePrice;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this,
                                          Notification.SET,
                                          MDPackage.MD_MARKETSTAT__CLOSE_PRICE,
                                          oldClosePrice,
                                          closePrice));
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public String getCloseDate()
    {
        return closeDate;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public void setCloseDate(String newCloseDate)
    {
        String oldCloseDate = closeDate;
        closeDate = newCloseDate;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this,
                                          Notification.SET,
                                          MDPackage.MD_MARKETSTAT__CLOSE_DATE,
                                          oldCloseDate,
                                          closeDate));
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public BigDecimal getPreviousClosePrice()
    {
        return previousClosePrice;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public void setPreviousClosePrice(BigDecimal newPreviousClosePrice)
    {
        BigDecimal oldPreviousClosePrice = previousClosePrice;
        previousClosePrice = newPreviousClosePrice;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this,
                                          Notification.SET,
                                          MDPackage.MD_MARKETSTAT__PREVIOUS_CLOSE_PRICE,
                                          oldPreviousClosePrice,
                                          previousClosePrice));
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public String getPreviousCloseDate()
    {
        return previousCloseDate;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public void setPreviousCloseDate(String newPreviousCloseDate)
    {
        String oldPreviousCloseDate = previousCloseDate;
        previousCloseDate = newPreviousCloseDate;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this,
                                          Notification.SET,
                                          MDPackage.MD_MARKETSTAT__PREVIOUS_CLOSE_DATE,
                                          oldPreviousCloseDate,
                                          previousCloseDate));
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Object eGet(int featureID,
                       boolean resolve,
                       boolean coreType)
    {
        switch (featureID) {
            case MDPackage.MD_MARKETSTAT__CLOSE_PRICE:
                return getClosePrice();
            case MDPackage.MD_MARKETSTAT__CLOSE_DATE:
                return getCloseDate();
            case MDPackage.MD_MARKETSTAT__PREVIOUS_CLOSE_PRICE:
                return getPreviousClosePrice();
            case MDPackage.MD_MARKETSTAT__PREVIOUS_CLOSE_DATE:
                return getPreviousCloseDate();
            case MDPackage.MD_MARKETSTAT__HIGH_PRICE:
                return getHighPrice();
            case MDPackage.MD_MARKETSTAT__LOW_PRICE:
                return getLowPrice();
            case MDPackage.MD_MARKETSTAT__VOLUME_TRADED:
                return getVolumeTraded();
            case MDPackage.MD_MARKETSTAT__OPEN_PRICE:
                return getOpenPrice();
            case MDPackage.MD_MARKETSTAT__VOLUME:
                return getVolume();
        }
        return super.eGet(featureID,
                          resolve,
                          coreType);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void eSet(int featureID,
                     Object newValue)
    {
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
            case MDPackage.MD_MARKETSTAT__HIGH_PRICE:
                setHighPrice((BigDecimal) newValue);
                return;
            case MDPackage.MD_MARKETSTAT__LOW_PRICE:
                setLowPrice((BigDecimal) newValue);
                return;
            case MDPackage.MD_MARKETSTAT__VOLUME_TRADED:
                setVolumeTraded((BigDecimal) newValue);
                return;
            case MDPackage.MD_MARKETSTAT__OPEN_PRICE:
                setOpenPrice((BigDecimal) newValue);
                return;
            case MDPackage.MD_MARKETSTAT__VOLUME:
                setVolume((BigDecimal) newValue);
                return;
        }
        super.eSet(featureID,
                   newValue);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void eUnset(int featureID)
    {
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
            case MDPackage.MD_MARKETSTAT__HIGH_PRICE:
                setHighPrice(HIGH_PRICE_EDEFAULT);
                return;
            case MDPackage.MD_MARKETSTAT__LOW_PRICE:
                setLowPrice(LOW_PRICE_EDEFAULT);
                return;
            case MDPackage.MD_MARKETSTAT__VOLUME_TRADED:
                setVolumeTraded(VOLUME_TRADED_EDEFAULT);
                return;
            case MDPackage.MD_MARKETSTAT__OPEN_PRICE:
                setOpenPrice(OPEN_PRICE_EDEFAULT);
                return;
            case MDPackage.MD_MARKETSTAT__VOLUME:
                setVolume(VOLUME_EDEFAULT);
                return;
        }
        super.eUnset(featureID);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    @Override
    public boolean eIsSet(int featureID)
    {
        switch (featureID) {
            case MDPackage.MD_MARKETSTAT__CLOSE_PRICE:
                return CLOSE_PRICE_EDEFAULT == null ? closePrice != null : !CLOSE_PRICE_EDEFAULT.equals(closePrice);
            case MDPackage.MD_MARKETSTAT__CLOSE_DATE:
                return CLOSE_DATE_EDEFAULT == null ? closeDate != null : !CLOSE_DATE_EDEFAULT.equals(closeDate);
            case MDPackage.MD_MARKETSTAT__PREVIOUS_CLOSE_PRICE:
                return PREVIOUS_CLOSE_PRICE_EDEFAULT == null ? previousClosePrice != null
                        : !PREVIOUS_CLOSE_PRICE_EDEFAULT.equals(previousClosePrice);
            case MDPackage.MD_MARKETSTAT__PREVIOUS_CLOSE_DATE:
                return PREVIOUS_CLOSE_DATE_EDEFAULT == null ? previousCloseDate != null : !PREVIOUS_CLOSE_DATE_EDEFAULT
                        .equals(previousCloseDate);
            case MDPackage.MD_MARKETSTAT__HIGH_PRICE:
                return HIGH_PRICE_EDEFAULT == null ? highPrice != null : !HIGH_PRICE_EDEFAULT.equals(highPrice);
            case MDPackage.MD_MARKETSTAT__LOW_PRICE:
                return LOW_PRICE_EDEFAULT == null ? lowPrice != null : !LOW_PRICE_EDEFAULT.equals(lowPrice);
            case MDPackage.MD_MARKETSTAT__VOLUME_TRADED:
                return VOLUME_TRADED_EDEFAULT == null ? volumeTraded != null : !VOLUME_TRADED_EDEFAULT
                        .equals(volumeTraded);
            case MDPackage.MD_MARKETSTAT__OPEN_PRICE:
                return OPEN_PRICE_EDEFAULT == null ? openPrice != null : !OPEN_PRICE_EDEFAULT.equals(openPrice);
            case MDPackage.MD_MARKETSTAT__VOLUME:
                return VOLUME_EDEFAULT == null ? volume != null : !VOLUME_EDEFAULT.equals(volume);
        }
        return super.eIsSet(featureID);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    @Override
    public String toString()
    {
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
        result.append(", highPrice: "); //$NON-NLS-1$
        result.append(highPrice);
        result.append(", lowPrice: "); //$NON-NLS-1$
        result.append(lowPrice);
        result.append(", volumeTraded: "); //$NON-NLS-1$
        result.append(volumeTraded);
        result.append(", openPrice: "); //$NON-NLS-1$
        result.append(openPrice);
        result.append(", volume: "); //$NON-NLS-1$
        result.append(volume);
        result.append(')');
        return result.toString();
    }

    @Override
    public BigDecimal getHighPrice()
    {
        return highPrice;
    }

    public void setHighPrice(BigDecimal newHighPrice)
    {
        BigDecimal oldHighPrice = highPrice;
        highPrice = newHighPrice;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this,
                                          Notification.SET,
                                          MDPackage.MD_MARKETSTAT__HIGH_PRICE,
                                          oldHighPrice,
                                          highPrice));
    }

    @Override
    public BigDecimal getLowPrice()
    {
        return lowPrice;
    }

    public void setLowPrice(BigDecimal newLowPrice)
    {
        BigDecimal oldLowPrice = lowPrice;
        lowPrice = newLowPrice;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this,
                                          Notification.SET,
                                          MDPackage.MD_MARKETSTAT__LOW_PRICE,
                                          oldLowPrice,
                                          lowPrice));
    }

    @Override
    public BigDecimal getVolumeTraded()
    {
        return volume;
    }

    public void setVolumeTraded(BigDecimal newVolumeTraded)
    {
        BigDecimal oldVolume = volume;
        volume = newVolumeTraded;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this,
                                          Notification.SET,
                                          MDPackage.MD_MARKETSTAT__VOLUME,
                                          oldVolume,
                                          volume));
    }

    @Override
    public BigDecimal getOpenPrice()
    {
        return openPrice;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public void setOpenPrice(BigDecimal newOpenPrice)
    {
        BigDecimal oldOpenPrice = openPrice;
        openPrice = newOpenPrice;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this,
                                          Notification.SET,
                                          MDPackage.MD_MARKETSTAT__OPEN_PRICE,
                                          oldOpenPrice,
                                          openPrice));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public BigDecimal getVolume()
    {
        return volume;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setVolume(BigDecimal newVolume)
    {
        BigDecimal oldVolume = volume;
        volume = newVolume;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this,
                                          Notification.SET,
                                          MDPackage.MD_MARKETSTAT__VOLUME,
                                          oldVolume,
                                          volume));
    }

} // MDMarketstatImpl

