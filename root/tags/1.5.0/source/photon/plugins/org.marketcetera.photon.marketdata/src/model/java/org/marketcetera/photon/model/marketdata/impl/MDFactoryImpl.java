package org.marketcetera.photon.model.marketdata.impl;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.impl.EFactoryImpl;
import org.eclipse.emf.ecore.plugin.EcorePlugin;
import org.marketcetera.marketdata.MarketDataRequest.Content;
import org.marketcetera.photon.model.marketdata.MDDepthOfBook;
import org.marketcetera.photon.model.marketdata.MDFactory;
import org.marketcetera.photon.model.marketdata.MDLatestTick;
import org.marketcetera.photon.model.marketdata.MDMarketstat;
import org.marketcetera.photon.model.marketdata.MDPackage;
import org.marketcetera.photon.model.marketdata.MDQuote;
import org.marketcetera.photon.model.marketdata.MDTopOfBook;
import org.marketcetera.util.misc.ClassVersion;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Factory</b>.
 * <!-- end-user-doc -->
 * @generated
 */
@ClassVersion("$Id$")
public class MDFactoryImpl extends EFactoryImpl implements MDFactory {
	/**
	 * Creates the default factory implementation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static MDFactory init() {
		try {
			MDFactory theMDFactory = (MDFactory) EPackage.Registry.INSTANCE
					.getEFactory("http://www.marketcetera.org/models/marketdata"); //$NON-NLS-1$ 
			if (theMDFactory != null) {
				return theMDFactory;
			}
		} catch (Exception exception) {
			EcorePlugin.INSTANCE.log(exception);
		}
		return new MDFactoryImpl();
	}

	/**
	 * Creates an instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public MDFactoryImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EObject create(EClass eClass) {
		switch (eClass.getClassifierID()) {
		case MDPackage.MD_LATEST_TICK:
			return createMDLatestTick();
		case MDPackage.MD_TOP_OF_BOOK:
			return createMDTopOfBook();
		case MDPackage.MD_MARKETSTAT:
			return createMDMarketstat();
		case MDPackage.MD_DEPTH_OF_BOOK:
			return createMDDepthOfBook();
		case MDPackage.MD_QUOTE:
			return createMDQuote();
		default:
			throw new IllegalArgumentException(
					"The class '" + eClass.getName() + "' is not a valid classifier"); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object createFromString(EDataType eDataType, String initialValue) {
		switch (eDataType.getClassifierID()) {
		case MDPackage.DEPTH_OF_BOOK_PRODUCT:
			return createDepthOfBookProductFromString(eDataType, initialValue);
		default:
			throw new IllegalArgumentException(
					"The datatype '" + eDataType.getName() + "' is not a valid classifier"); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String convertToString(EDataType eDataType, Object instanceValue) {
		switch (eDataType.getClassifierID()) {
		case MDPackage.DEPTH_OF_BOOK_PRODUCT:
			return convertDepthOfBookProductToString(eDataType, instanceValue);
		default:
			throw new IllegalArgumentException(
					"The datatype '" + eDataType.getName() + "' is not a valid classifier"); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public MDLatestTick createMDLatestTick() {
		MDLatestTickImpl mdLatestTick = new MDLatestTickImpl();
		return mdLatestTick;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public MDTopOfBook createMDTopOfBook() {
		MDTopOfBookImpl mdTopOfBook = new MDTopOfBookImpl();
		return mdTopOfBook;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public MDMarketstat createMDMarketstat() {
		MDMarketstatImpl mdMarketstat = new MDMarketstatImpl();
		return mdMarketstat;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public MDDepthOfBook createMDDepthOfBook() {
		MDDepthOfBookImpl mdDepthOfBook = new MDDepthOfBookImpl();
		return mdDepthOfBook;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public MDQuote createMDQuote() {
		MDQuoteImpl mdQuote = new MDQuoteImpl();
		return mdQuote;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Content createDepthOfBookProductFromString(EDataType eDataType, String initialValue) {
		return (Content) super.createFromString(eDataType, initialValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertDepthOfBookProductToString(EDataType eDataType, Object instanceValue) {
		return super.convertToString(eDataType, instanceValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public MDPackage getMDPackage() {
		return (MDPackage) getEPackage();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @deprecated
	 * @generated
	 */
	@Deprecated
	public static MDPackage getPackage() {
		return MDPackage.eINSTANCE;
	}

} //MDFactoryImpl
