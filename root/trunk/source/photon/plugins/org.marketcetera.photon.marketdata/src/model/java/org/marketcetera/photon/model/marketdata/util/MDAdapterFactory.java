package org.marketcetera.photon.model.marketdata.util;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notifier;

import org.eclipse.emf.common.notify.impl.AdapterFactoryImpl;

import org.eclipse.emf.ecore.EObject;

import org.marketcetera.photon.model.marketdata.*;
import org.marketcetera.util.misc.ClassVersion;

/**
 * <!-- begin-user-doc -->
 * The <b>Adapter Factory</b> for the model.
 * It provides an adapter <code>createXXX</code> method for each class of the model.
 * <!-- end-user-doc -->
 * @see org.marketcetera.photon.model.marketdata.MDPackage
 * @generated
 */
@ClassVersion("$Id$")
public class MDAdapterFactory extends AdapterFactoryImpl {
	/**
	 * The cached model package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected static MDPackage modelPackage;

	/**
	 * Creates an instance of the adapter factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public MDAdapterFactory() {
		if (modelPackage == null) {
			modelPackage = MDPackage.eINSTANCE;
		}
	}

	/**
	 * Returns whether this factory is applicable for the type of the object.
	 * <!-- begin-user-doc -->
	 * This implementation returns <code>true</code> if the object is either the model's package or is an instance object of the model.
	 * <!-- end-user-doc -->
	 * @return whether this factory is applicable for the type of the object.
	 * @generated
	 */
	@Override
	public boolean isFactoryForType(Object object) {
		if (object == modelPackage) {
			return true;
		}
		if (object instanceof EObject) {
			return ((EObject) object).eClass().getEPackage() == modelPackage;
		}
		return false;
	}

	/**
	 * The switch that delegates to the <code>createXXX</code> methods.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected MDSwitch<Adapter> modelSwitch = new MDSwitch<Adapter>() {
		@Override
		public Adapter caseMDItem(MDItem object) {
			return createMDItemAdapter();
		}

		@Override
		public Adapter caseMDLatestTick(MDLatestTick object) {
			return createMDLatestTickAdapter();
		}

		@Override
		public Adapter caseMDTopOfBook(MDTopOfBook object) {
			return createMDTopOfBookAdapter();
		}

		@Override
		public Adapter caseMDMarketstat(MDMarketstat object) {
			return createMDMarketstatAdapter();
		}

		@Override
		public Adapter caseMDDepthOfBook(MDDepthOfBook object) {
			return createMDDepthOfBookAdapter();
		}

		@Override
		public Adapter caseMDQuote(MDQuote object) {
			return createMDQuoteAdapter();
		}

		@Override
		public Adapter defaultCase(EObject object) {
			return createEObjectAdapter();
		}
	};

	/**
	 * Creates an adapter for the <code>target</code>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param target the object to adapt.
	 * @return the adapter for the <code>target</code>.
	 * @generated
	 */
	@Override
	public Adapter createAdapter(Notifier target) {
		return modelSwitch.doSwitch((EObject) target);
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.marketcetera.photon.model.marketdata.MDItem <em>Item</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.marketcetera.photon.model.marketdata.MDItem
	 * @generated
	 */
	public Adapter createMDItemAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.marketcetera.photon.model.marketdata.MDLatestTick <em>Latest Tick</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.marketcetera.photon.model.marketdata.MDLatestTick
	 * @generated
	 */
	public Adapter createMDLatestTickAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.marketcetera.photon.model.marketdata.MDTopOfBook <em>Top Of Book</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.marketcetera.photon.model.marketdata.MDTopOfBook
	 * @generated
	 */
	public Adapter createMDTopOfBookAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.marketcetera.photon.model.marketdata.MDMarketstat <em>Marketstat</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.marketcetera.photon.model.marketdata.MDMarketstat
	 * @generated
	 */
	public Adapter createMDMarketstatAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.marketcetera.photon.model.marketdata.MDDepthOfBook <em>Depth Of Book</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.marketcetera.photon.model.marketdata.MDDepthOfBook
	 * @generated
	 */
	public Adapter createMDDepthOfBookAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.marketcetera.photon.model.marketdata.MDQuote <em>Quote</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.marketcetera.photon.model.marketdata.MDQuote
	 * @generated
	 */
	public Adapter createMDQuoteAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for the default case.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @generated
	 */
	public Adapter createEObjectAdapter() {
		return null;
	}

} //MDAdapterFactory
