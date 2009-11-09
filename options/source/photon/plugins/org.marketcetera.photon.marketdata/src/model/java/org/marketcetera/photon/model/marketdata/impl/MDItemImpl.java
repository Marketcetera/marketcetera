/**
 * $License$
 *
 * $Id$
 */
package org.marketcetera.photon.model.marketdata.impl;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;

import org.marketcetera.photon.model.marketdata.MDItem;
import org.marketcetera.photon.model.marketdata.MDPackage;

import org.marketcetera.trade.Instrument;
import org.marketcetera.util.misc.ClassVersion;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Item</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.marketcetera.photon.model.marketdata.impl.MDItemImpl#getInstrument <em>Instrument</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 * @since $Release$
 */
@ClassVersion("$Id$")
public abstract class MDItemImpl extends EObjectImpl implements MDItem {
    /**
     * The default value of the '{@link #getInstrument() <em>Instrument</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getInstrument()
     * @generated
     * @ordered
     */
    protected static final Instrument INSTRUMENT_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getInstrument() <em>Instrument</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getInstrument()
     * @generated
     * @ordered
     */
    protected volatile Instrument instrument = INSTRUMENT_EDEFAULT;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public MDItemImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return MDPackage.Literals.MD_ITEM;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Instrument getInstrument() {
        return instrument;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setInstrument(Instrument newInstrument) {
        Instrument oldInstrument = instrument;
        instrument = newInstrument;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET,
                    MDPackage.MD_ITEM__INSTRUMENT, oldInstrument, instrument));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Object eGet(int featureID, boolean resolve, boolean coreType) {
        switch (featureID) {
        case MDPackage.MD_ITEM__INSTRUMENT:
            return getInstrument();
        }
        return super.eGet(featureID, resolve, coreType);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void eSet(int featureID, Object newValue) {
        switch (featureID) {
        case MDPackage.MD_ITEM__INSTRUMENT:
            setInstrument((Instrument) newValue);
            return;
        }
        super.eSet(featureID, newValue);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void eUnset(int featureID) {
        switch (featureID) {
        case MDPackage.MD_ITEM__INSTRUMENT:
            setInstrument(INSTRUMENT_EDEFAULT);
            return;
        }
        super.eUnset(featureID);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public boolean eIsSet(int featureID) {
        switch (featureID) {
        case MDPackage.MD_ITEM__INSTRUMENT:
            return INSTRUMENT_EDEFAULT == null ? instrument != null
                    : !INSTRUMENT_EDEFAULT.equals(instrument);
        }
        return super.eIsSet(featureID);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public String toString() {
        if (eIsProxy())
            return super.toString();

        StringBuffer result = new StringBuffer(super.toString());
        result.append(" (instrument: "); //$NON-NLS-1$
        result.append(instrument);
        result.append(')');
        return result.toString();
    }

} //MDItemImpl
