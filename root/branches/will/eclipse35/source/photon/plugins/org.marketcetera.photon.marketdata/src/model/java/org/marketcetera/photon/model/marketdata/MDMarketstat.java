package org.marketcetera.photon.model.marketdata;

import java.math.BigDecimal;

import java.util.Date;

import org.marketcetera.util.misc.ClassVersion;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Symbol Statistic</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.marketcetera.photon.model.marketdata.MDMarketstat#getClosePrice <em>Close Price</em>}</li>
 *   <li>{@link org.marketcetera.photon.model.marketdata.MDMarketstat#getCloseDate <em>Close Date</em>}</li>
 *   <li>{@link org.marketcetera.photon.model.marketdata.MDMarketstat#getPreviousClosePrice <em>Previous Close Price</em>}</li>
 *   <li>{@link org.marketcetera.photon.model.marketdata.MDMarketstat#getPreviousCloseDate <em>Previous Close Date</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.marketcetera.photon.model.marketdata.MDPackage#getMDMarketstat()
 * @model
 * @generated
 */
@ClassVersion("$Id$")
public interface MDMarketstat extends MDItem {
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
	Date getCloseDate();

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
	Date getPreviousCloseDate();

} // MDSymbolStatistic
