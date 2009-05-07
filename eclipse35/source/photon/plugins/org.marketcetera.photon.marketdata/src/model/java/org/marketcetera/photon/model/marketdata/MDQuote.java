package org.marketcetera.photon.model.marketdata;

import java.math.BigDecimal;

import org.eclipse.emf.ecore.EObject;
import org.marketcetera.util.misc.ClassVersion;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Quote</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.marketcetera.photon.model.marketdata.MDQuote#getPrice <em>Price</em>}</li>
 *   <li>{@link org.marketcetera.photon.model.marketdata.MDQuote#getSize <em>Size</em>}</li>
 *   <li>{@link org.marketcetera.photon.model.marketdata.MDQuote#getSource <em>Source</em>}</li>
 *   <li>{@link org.marketcetera.photon.model.marketdata.MDQuote#getTime <em>Time</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.marketcetera.photon.model.marketdata.MDPackage#getMDQuote()
 * @model
 * @generated
 */
@ClassVersion("$Id$")
public interface MDQuote extends EObject {
	/**
	 * Returns the value of the '<em><b>Price</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Price</em>' attribute.
	 * @see org.marketcetera.photon.model.marketdata.MDPackage#getMDQuote_Price()
	 * @model suppressedSetVisibility="true"
	 * @generated
	 */
	BigDecimal getPrice();

	/**
	 * Returns the value of the '<em><b>Size</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Size</em>' attribute.
	 * @see org.marketcetera.photon.model.marketdata.MDPackage#getMDQuote_Size()
	 * @model suppressedSetVisibility="true"
	 * @generated
	 */
	BigDecimal getSize();

	/**
	 * Returns the value of the '<em><b>Source</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Source</em>' attribute.
	 * @see org.marketcetera.photon.model.marketdata.MDPackage#getMDQuote_Source()
	 * @model suppressedSetVisibility="true"
	 * @generated
	 */
	String getSource();

	/**
	 * Returns the value of the '<em><b>Time</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Time</em>' attribute.
	 * @see org.marketcetera.photon.model.marketdata.MDPackage#getMDQuote_Time()
	 * @model suppressedSetVisibility="true"
	 * @generated
	 */
	long getTime();

} // MDQuote
