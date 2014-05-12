/**
 * $License$
 *
 * $Id$
 */
package org.marketcetera.photon.model.marketdata;

import java.math.BigDecimal;

import org.marketcetera.util.misc.ClassVersion;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Marketstat</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.marketcetera.photon.model.marketdata.MDMarketstat#getClosePrice <em>Close Price</em>}</li>
 *   <li>{@link org.marketcetera.photon.model.marketdata.MDMarketstat#getCloseDate <em>Close Date</em>}</li>
 *   <li>{@link org.marketcetera.photon.model.marketdata.MDMarketstat#getPreviousClosePrice <em>Previous Close Price</em>}</li>
 *   <li>{@link org.marketcetera.photon.model.marketdata.MDMarketstat#getPreviousCloseDate <em>Previous Close Date</em>}</li>
 *   <li>{@link org.marketcetera.photon.model.marketdata.MDMarketstat#getHighPrice <em>High Price</em>}</li>
 *   <li>{@link org.marketcetera.photon.model.marketdata.MDMarketstat#getLowPrice <em>Low Price</em>}</li>
 *   <li>{@link org.marketcetera.photon.model.marketdata.MDMarketstat#getVolumeTraded <em>Volume Traded</em>}</li>
 *   <li>{@link org.marketcetera.photon.model.marketdata.MDMarketstat#getOpenPrice <em>Open Price</em>}</li>
 *   <li>{@link org.marketcetera.photon.model.marketdata.MDMarketstat#getVolume <em>Volume</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.marketcetera.photon.model.marketdata.MDPackage#getMDMarketstat()
 * @model
 * @generated
 * @since 2.1.0
 */
@ClassVersion("$Id$")
public interface MDMarketstat
        extends MDItem
{
    /**
     * Returns the value of the '<em><b>Close Price</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the value of the '<em>Close Price</em>' attribute.
     * @see org.marketcetera.photon.model.marketdata.MDPackage#getMDMarketstat_ClosePrice()
     * @model suppressedSetVisibility="true"
     * @generated
     */
    BigDecimal getClosePrice();

    /**
     * Returns the value of the '<em><b>Close Date</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the value of the '<em>Close Date</em>' attribute.
     * @see org.marketcetera.photon.model.marketdata.MDPackage#getMDMarketstat_CloseDate()
     * @model suppressedSetVisibility="true"
     * @generated
     */
    String getCloseDate();

    /**
     * Returns the value of the '<em><b>Previous Close Price</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the value of the '<em>Previous Close Price</em>' attribute.
     * @see org.marketcetera.photon.model.marketdata.MDPackage#getMDMarketstat_PreviousClosePrice()
     * @model suppressedSetVisibility="true"
     * @generated
     */
    BigDecimal getPreviousClosePrice();

    /**
     * Returns the value of the '<em><b>Previous Close Date</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the value of the '<em>Previous Close Date</em>' attribute.
     * @see org.marketcetera.photon.model.marketdata.MDPackage#getMDMarketstat_PreviousCloseDate()
     * @model suppressedSetVisibility="true"
     * @generated
     */
    String getPreviousCloseDate();

    /**
     * Returns the value of the '<em><b>High Price</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the value of the '<em>High Price</em>' attribute.
     * @see #setHighPrice(BigDecimal)
     * @see org.marketcetera.photon.model.marketdata.MDPackage#getMDMarketstat_HighPrice()
     * @model
     * @generated
     */
    BigDecimal getHighPrice();

    /**
     * Sets the value of the '{@link org.marketcetera.photon.model.marketdata.MDMarketstat#getHighPrice <em>High Price</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>High Price</em>' attribute.
     * @see #getHighPrice()
     * @generated
     */
    void setHighPrice(BigDecimal value);

    /**
     * Returns the value of the '<em><b>Low Price</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the value of the '<em>Low Price</em>' attribute.
     * @see #setLowPrice(BigDecimal)
     * @see org.marketcetera.photon.model.marketdata.MDPackage#getMDMarketstat_LowPrice()
     * @model
     * @generated
     */
    BigDecimal getLowPrice();

    /**
     * Sets the value of the '{@link org.marketcetera.photon.model.marketdata.MDMarketstat#getLowPrice <em>Low Price</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Low Price</em>' attribute.
     * @see #getLowPrice()
     * @generated
     */
    void setLowPrice(BigDecimal value);

    /**
     * Returns the value of the '<em><b>Volume Traded</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the value of the '<em>Volume Traded</em>' attribute.
     * @see #setVolumeTraded(BigDecimal)
     * @see org.marketcetera.photon.model.marketdata.MDPackage#getMDMarketstat_VolumeTraded()
     * @model
     * @generated
     */
    BigDecimal getVolumeTraded();

    /**
     * Sets the value of the '{@link org.marketcetera.photon.model.marketdata.MDMarketstat#getVolumeTraded <em>Volume Traded</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Volume Traded</em>' attribute.
     * @see #getVolumeTraded()
     * @generated
     */
    void setVolumeTraded(BigDecimal value);

    /**
     * Returns the value of the '<em><b>Open Price</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the value of the '<em>Open Price</em>' attribute.
     * @see #setOpenPrice(BigDecimal)
     * @see org.marketcetera.photon.model.marketdata.MDPackage#getMDMarketstat_OpenPrice()
     * @model
     * @generated
     */
    BigDecimal getOpenPrice();

    /**
     * Sets the value of the '{@link org.marketcetera.photon.model.marketdata.MDMarketstat#getOpenPrice <em>Open Price</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Open Price</em>' attribute.
     * @see #getOpenPrice()
     * @generated
     */
    void setOpenPrice(BigDecimal value);

    /**
     * Returns the value of the '<em><b>Volume</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Volume</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Volume</em>' attribute.
     * @see #setVolume(BigDecimal)
     * @see org.marketcetera.photon.model.marketdata.MDPackage#getMDMarketstat_Volume()
     * @model
     * @generated
     */
    BigDecimal getVolume();

    /**
     * Sets the value of the '{@link org.marketcetera.photon.model.marketdata.MDMarketstat#getVolume <em>Volume</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Volume</em>' attribute.
     * @see #getVolume()
     * @generated
     */
    void setVolume(BigDecimal value);
} // MDMarketstat
