/**
 * $License$
 *
 * $Id$
 */
package org.marketcetera.photon.model.marketdata;

import org.eclipse.emf.ecore.EObject;

import org.marketcetera.trade.Instrument;
import org.marketcetera.util.misc.ClassVersion;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Item</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.marketcetera.photon.model.marketdata.MDItem#getInstrument <em>Instrument</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.marketcetera.photon.model.marketdata.MDPackage#getMDItem()
 * @model abstract="true"
 * @generated
 * @since 2.0.0
 */
@ClassVersion("$Id$")
public interface MDItem extends EObject {
    /**
     * Returns the value of the '<em><b>Instrument</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the value of the '<em>Instrument</em>' attribute.
     * @see org.marketcetera.photon.model.marketdata.MDPackage#getMDItem_Instrument()
     * @model dataType="org.marketcetera.photon.model.marketdata.Instrument" required="true" suppressedSetVisibility="true"
     * @generated
     */
    Instrument getInstrument();

} // MDItem
