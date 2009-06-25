package org.marketcetera.photon.model.marketdata;

import java.math.BigDecimal;

import org.marketcetera.util.misc.ClassVersion;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Top Of Book</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.marketcetera.photon.model.marketdata.MDTopOfBook#getBidSize <em>Bid Size</em>}</li>
 *   <li>{@link org.marketcetera.photon.model.marketdata.MDTopOfBook#getBidPrice <em>Bid Price</em>}</li>
 *   <li>{@link org.marketcetera.photon.model.marketdata.MDTopOfBook#getAskSize <em>Ask Size</em>}</li>
 *   <li>{@link org.marketcetera.photon.model.marketdata.MDTopOfBook#getAskPrice <em>Ask Price</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.marketcetera.photon.model.marketdata.MDPackage#getMDTopOfBook()
 * @model
 * @generated
 */
@ClassVersion("$Id$")
public interface MDTopOfBook extends MDItem {
	/**
	 * Returns the value of the '<em><b>Bid Size</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Bid Size</em>' attribute.
	 * @see org.marketcetera.photon.model.marketdata.MDPackage#getMDTopOfBook_BidSize()
	 * @model suppressedSetVisibility="true"
	 * @generated
	 */
	BigDecimal getBidSize();

	/**
	 * Returns the value of the '<em><b>Bid Price</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Bid Price</em>' attribute.
	 * @see org.marketcetera.photon.model.marketdata.MDPackage#getMDTopOfBook_BidPrice()
	 * @model suppressedSetVisibility="true"
	 * @generated
	 */
	BigDecimal getBidPrice();

	/**
	 * Returns the value of the '<em><b>Ask Size</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Ask Size</em>' attribute.
	 * @see org.marketcetera.photon.model.marketdata.MDPackage#getMDTopOfBook_AskSize()
	 * @model suppressedSetVisibility="true"
	 * @generated
	 */
	BigDecimal getAskSize();

	/**
	 * Returns the value of the '<em><b>Ask Price</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Ask Price</em>' attribute.
	 * @see org.marketcetera.photon.model.marketdata.MDPackage#getMDTopOfBook_AskPrice()
	 * @model suppressedSetVisibility="true"
	 * @generated
	 */
	BigDecimal getAskPrice();

} // MDTopOfBook
