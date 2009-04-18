package org.marketcetera.photon.model.marketdata;

import org.eclipse.emf.ecore.EFactory;
import org.marketcetera.photon.marketdata.IMarketData;
import org.marketcetera.util.misc.ClassVersion;

/**
 * <!-- begin-user-doc -->
 * A factory for creating objects from this package.
 * <p>
 * This class is <b>not</b> API and should not be used outside of this plugin, except for testing
 * purposes. Market data items should be obtained only via the {@link IMarketData} interface. 
 * <!-- end-user-doc -->
 * @see org.marketcetera.photon.model.marketdata.MDPackage
 * @generated
 */
@ClassVersion("$Id$")
public interface MDFactory extends EFactory {
	/**
	 * The singleton instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	MDFactory eINSTANCE = org.marketcetera.photon.model.marketdata.impl.MDFactoryImpl.init();

	/**
	 * Returns a new object of class '<em>Latest Tick</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Latest Tick</em>'.
	 * @generated
	 */
	MDLatestTick createMDLatestTick();

	/**
	 * Returns a new object of class '<em>Top Of Book</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Top Of Book</em>'.
	 * @generated
	 */
	MDTopOfBook createMDTopOfBook();

	/**
	 * Returns a new object of class '<em>Marketstat</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Marketstat</em>'.
	 * @generated
	 */
	MDMarketstat createMDMarketstat();

	/**
	 * Returns a new object of class '<em>Depth Of Book</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Depth Of Book</em>'.
	 * @generated
	 */
	MDDepthOfBook createMDDepthOfBook();

	/**
	 * Returns a new object of class '<em>Quote</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Quote</em>'.
	 * @generated
	 */
	MDQuote createMDQuote();

	/**
	 * Returns the package supported by this factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the package supported by this factory.
	 * @generated
	 */
	MDPackage getMDPackage();

} //MDFactory
