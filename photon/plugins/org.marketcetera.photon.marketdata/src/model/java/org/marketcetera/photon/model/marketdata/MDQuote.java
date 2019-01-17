/**
 * $License$
 *
 * $Id$
 */
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
 *   <li>{@link org.marketcetera.photon.model.marketdata.MDQuote#getExchange <em>Exchange</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.marketcetera.photon.model.marketdata.MDPackage#getMDQuote()
 * @model
 * @generated
 * @since 2.1.0
 */
@ClassVersion("$Id$")
public interface MDQuote
        extends MDItem
{
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

    /**
     * Returns the value of the '<em><b>Exchange</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Exchange</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Exchange</em>' attribute.
     * @see #setExchange(String)
     * @see org.marketcetera.photon.model.marketdata.MDPackage#getMDQuote_Exchange()
     * @model
     * @generated
     */
    String getExchange();

    /**
     * Sets the value of the '{@link org.marketcetera.photon.model.marketdata.MDQuote#getExchange <em>Exchange</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Exchange</em>' attribute.
     * @see #getExchange()
     * @generated
     */
    void setExchange(String value);

} // MDQuote
