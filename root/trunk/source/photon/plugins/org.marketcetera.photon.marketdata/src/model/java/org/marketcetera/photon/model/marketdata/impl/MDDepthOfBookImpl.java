package org.marketcetera.photon.model.marketdata.impl;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;
import org.marketcetera.marketdata.MarketDataRequest.Content;
import org.marketcetera.photon.model.marketdata.MDDepthOfBook;
import org.marketcetera.photon.model.marketdata.MDPackage;
import org.marketcetera.photon.model.marketdata.MDQuote;
import org.marketcetera.util.misc.ClassVersion;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Order Book</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.marketcetera.photon.model.marketdata.impl.MDDepthOfBookImpl#getProduct <em>Product</em>}</li>
 *   <li>{@link org.marketcetera.photon.model.marketdata.impl.MDDepthOfBookImpl#getBids <em>Bids</em>}</li>
 *   <li>{@link org.marketcetera.photon.model.marketdata.impl.MDDepthOfBookImpl#getAsks <em>Asks</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
@ClassVersion("$Id$")
public class MDDepthOfBookImpl extends MDItemImpl implements MDDepthOfBook {
	/**
	 * The default value of the '{@link #getProduct() <em>Product</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getProduct()
	 * @generated
	 * @ordered
	 */
	protected static final Content PRODUCT_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getProduct() <em>Product</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getProduct()
	 * @generated
	 * @ordered
	 */
	protected Content product = PRODUCT_EDEFAULT;

	/**
	 * The cached value of the '{@link #getBids() <em>Bids</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getBids()
	 * @generated
	 * @ordered
	 */
	protected EList<MDQuote> bids;

	/**
	 * The cached value of the '{@link #getAsks() <em>Asks</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getAsks()
	 * @generated
	 * @ordered
	 */
	protected EList<MDQuote> asks;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public MDDepthOfBookImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return MDPackage.Literals.MD_DEPTH_OF_BOOK;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Content getProduct() {
		return product;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setProduct(Content newProduct) {
		Content oldProduct = product;
		product = newProduct;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET,
					MDPackage.MD_DEPTH_OF_BOOK__PRODUCT, oldProduct, product));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<MDQuote> getBids() {
		if (bids == null) {
			bids = new EObjectContainmentEList<MDQuote>(MDQuote.class, this,
					MDPackage.MD_DEPTH_OF_BOOK__BIDS);
		}
		return bids;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<MDQuote> getAsks() {
		if (asks == null) {
			asks = new EObjectContainmentEList<MDQuote>(MDQuote.class, this,
					MDPackage.MD_DEPTH_OF_BOOK__ASKS);
		}
		return asks;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID,
			NotificationChain msgs) {
		switch (featureID) {
		case MDPackage.MD_DEPTH_OF_BOOK__BIDS:
			return ((InternalEList<?>) getBids()).basicRemove(otherEnd, msgs);
		case MDPackage.MD_DEPTH_OF_BOOK__ASKS:
			return ((InternalEList<?>) getAsks()).basicRemove(otherEnd, msgs);
		}
		return super.eInverseRemove(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
		case MDPackage.MD_DEPTH_OF_BOOK__PRODUCT:
			return getProduct();
		case MDPackage.MD_DEPTH_OF_BOOK__BIDS:
			return getBids();
		case MDPackage.MD_DEPTH_OF_BOOK__ASKS:
			return getAsks();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
		case MDPackage.MD_DEPTH_OF_BOOK__PRODUCT:
			setProduct((Content) newValue);
			return;
		case MDPackage.MD_DEPTH_OF_BOOK__BIDS:
			getBids().clear();
			getBids().addAll((Collection<? extends MDQuote>) newValue);
			return;
		case MDPackage.MD_DEPTH_OF_BOOK__ASKS:
			getAsks().clear();
			getAsks().addAll((Collection<? extends MDQuote>) newValue);
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
		case MDPackage.MD_DEPTH_OF_BOOK__PRODUCT:
			setProduct(PRODUCT_EDEFAULT);
			return;
		case MDPackage.MD_DEPTH_OF_BOOK__BIDS:
			getBids().clear();
			return;
		case MDPackage.MD_DEPTH_OF_BOOK__ASKS:
			getAsks().clear();
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
		case MDPackage.MD_DEPTH_OF_BOOK__PRODUCT:
			return PRODUCT_EDEFAULT == null ? product != null : !PRODUCT_EDEFAULT.equals(product);
		case MDPackage.MD_DEPTH_OF_BOOK__BIDS:
			return bids != null && !bids.isEmpty();
		case MDPackage.MD_DEPTH_OF_BOOK__ASKS:
			return asks != null && !asks.isEmpty();
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
		if (eIsProxy()) return super.toString();

		StringBuffer result = new StringBuffer(super.toString());
		result.append(" (product: "); //$NON-NLS-1$
		result.append(product);
		result.append(')');
		return result.toString();
	}

} //MDOrderBookImpl
