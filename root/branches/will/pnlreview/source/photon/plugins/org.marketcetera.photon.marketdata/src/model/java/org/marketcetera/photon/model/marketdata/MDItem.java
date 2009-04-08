package org.marketcetera.photon.model.marketdata;

import org.eclipse.emf.ecore.EObject;
import org.marketcetera.util.misc.ClassVersion;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Item</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.marketcetera.photon.model.marketdata.MDItem#getSymbol <em>Symbol</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.marketcetera.photon.model.marketdata.MDPackage#getMDItem()
 * @model abstract="true"
 * @generated
 */
@ClassVersion("$Id$")
public interface MDItem extends EObject {
	/**
	 * Returns the value of the '<em><b>Symbol</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Symbol</em>' attribute.
	 * @see org.marketcetera.photon.model.marketdata.MDPackage#getMDItem_Symbol()
	 * @model required="true" suppressedSetVisibility="true"
	 * @generated
	 */
	String getSymbol();

} // MDItem
