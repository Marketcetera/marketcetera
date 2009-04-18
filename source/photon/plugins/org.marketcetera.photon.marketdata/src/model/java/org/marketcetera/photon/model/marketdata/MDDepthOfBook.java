package org.marketcetera.photon.model.marketdata;

import org.eclipse.emf.common.util.EList;
import org.marketcetera.marketdata.MarketDataRequest.Content;
import org.marketcetera.util.misc.ClassVersion;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Order Book</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.marketcetera.photon.model.marketdata.MDDepthOfBook#getProduct <em>Product</em>}</li>
 *   <li>{@link org.marketcetera.photon.model.marketdata.MDDepthOfBook#getBids <em>Bids</em>}</li>
 *   <li>{@link org.marketcetera.photon.model.marketdata.MDDepthOfBook#getAsks <em>Asks</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.marketcetera.photon.model.marketdata.MDPackage#getMDDepthOfBook()
 * @model
 * @generated
 */
@ClassVersion("$Id$")
public interface MDDepthOfBook extends MDItem {
	/**
	 * Returns the value of the '<em><b>Product</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Product</em>' attribute.
	 * @see org.marketcetera.photon.model.marketdata.MDPackage#getMDDepthOfBook_Product()
	 * @model dataType="org.marketcetera.photon.model.marketdata.DepthOfBookProduct" suppressedSetVisibility="true"
	 * @generated
	 */
	Content getProduct();

	/**
	 * Returns the value of the '<em><b>Bids</b></em>' containment reference list.
	 * The list contents are of type {@link org.marketcetera.photon.model.marketdata.MDQuote}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Bids</em>' containment reference list.
	 * @see org.marketcetera.photon.model.marketdata.MDPackage#getMDDepthOfBook_Bids()
	 * @model containment="true" suppressedSetVisibility="true"
	 * @generated
	 */
	EList<MDQuote> getBids();

	/**
	 * Returns the value of the '<em><b>Asks</b></em>' containment reference list.
	 * The list contents are of type {@link org.marketcetera.photon.model.marketdata.MDQuote}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Asks</em>' containment reference list.
	 * @see org.marketcetera.photon.model.marketdata.MDPackage#getMDDepthOfBook_Asks()
	 * @model containment="true" suppressedSetVisibility="true"
	 * @generated
	 */
	EList<MDQuote> getAsks();

} // MDOrderBook
