package org.marketcetera.photon.model.marketdata;

import java.math.BigDecimal;

import org.marketcetera.util.misc.ClassVersion;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Latest Tick</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.marketcetera.photon.model.marketdata.MDLatestTick#getPrice <em>Price</em>}</li>
 *   <li>{@link org.marketcetera.photon.model.marketdata.MDLatestTick#getSize <em>Size</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.marketcetera.photon.model.marketdata.MDPackage#getMDLatestTick()
 * @model
 * @generated
 */
@ClassVersion("$Id$")
public interface MDLatestTick extends MDItem {
	/**
	 * Returns the value of the '<em><b>Price</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Price</em>' attribute.
	 * @see org.marketcetera.photon.model.marketdata.MDPackage#getMDLatestTick_Price()
	 * @model suppressedSetVisibility="true"
	 * @generated
	 */
	BigDecimal getPrice();

	/**
	 * Returns the value of the '<em><b>Size</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Size</em>' attribute.
	 * @see org.marketcetera.photon.model.marketdata.MDPackage#getMDLatestTick_Size()
	 * @model suppressedSetVisibility="true"
	 * @generated
	 */
	BigDecimal getSize();

} // MDLatestTick
