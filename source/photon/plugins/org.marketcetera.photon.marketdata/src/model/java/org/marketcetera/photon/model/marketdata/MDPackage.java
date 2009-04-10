package org.marketcetera.photon.model.marketdata;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.marketcetera.util.misc.ClassVersion;

/**
 * <!-- begin-user-doc -->
 * The <b>Package</b> for the model.
 * It contains accessors for the meta objects to represent
 * <ul>
 *   <li>each class,</li>
 *   <li>each feature of each class,</li>
 *   <li>each enum,</li>
 *   <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * @see org.marketcetera.photon.model.marketdata.MDFactory
 * @model kind="package"
 * @generated
 */
@ClassVersion("$Id$")
public interface MDPackage extends EPackage {
	/**
	 * The package name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNAME = "marketdata"; //$NON-NLS-1$

	/**
	 * The package namespace URI.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_URI = "http://www.marketcetera.org/models/marketdata"; //$NON-NLS-1$

	/**
	 * The package namespace name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_PREFIX = "mdata"; //$NON-NLS-1$

	/**
	 * The singleton instance of the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	MDPackage eINSTANCE = org.marketcetera.photon.model.marketdata.impl.MDPackageImpl.init();

	/**
	 * The meta object id for the '{@link org.marketcetera.photon.model.marketdata.impl.MDItemImpl <em>Item</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.marketcetera.photon.model.marketdata.impl.MDItemImpl
	 * @see org.marketcetera.photon.model.marketdata.impl.MDPackageImpl#getMDItem()
	 * @generated
	 */
	int MD_ITEM = 0;

	/**
	 * The feature id for the '<em><b>Symbol</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MD_ITEM__SYMBOL = 0;

	/**
	 * The number of structural features of the '<em>Item</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MD_ITEM_FEATURE_COUNT = 1;

	/**
	 * The meta object id for the '{@link org.marketcetera.photon.model.marketdata.impl.MDLatestTickImpl <em>Latest Tick</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.marketcetera.photon.model.marketdata.impl.MDLatestTickImpl
	 * @see org.marketcetera.photon.model.marketdata.impl.MDPackageImpl#getMDLatestTick()
	 * @generated
	 */
	int MD_LATEST_TICK = 1;

	/**
	 * The feature id for the '<em><b>Symbol</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MD_LATEST_TICK__SYMBOL = MD_ITEM__SYMBOL;

	/**
	 * The feature id for the '<em><b>Price</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MD_LATEST_TICK__PRICE = MD_ITEM_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Size</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MD_LATEST_TICK__SIZE = MD_ITEM_FEATURE_COUNT + 1;

	/**
	 * The number of structural features of the '<em>Latest Tick</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MD_LATEST_TICK_FEATURE_COUNT = MD_ITEM_FEATURE_COUNT + 2;

	/**
	 * The meta object id for the '{@link org.marketcetera.photon.model.marketdata.impl.MDTopOfBookImpl <em>Top Of Book</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.marketcetera.photon.model.marketdata.impl.MDTopOfBookImpl
	 * @see org.marketcetera.photon.model.marketdata.impl.MDPackageImpl#getMDTopOfBook()
	 * @generated
	 */
	int MD_TOP_OF_BOOK = 2;

	/**
	 * The feature id for the '<em><b>Symbol</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MD_TOP_OF_BOOK__SYMBOL = MD_ITEM__SYMBOL;

	/**
	 * The feature id for the '<em><b>Bid Size</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MD_TOP_OF_BOOK__BID_SIZE = MD_ITEM_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Bid Price</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MD_TOP_OF_BOOK__BID_PRICE = MD_ITEM_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Ask Size</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MD_TOP_OF_BOOK__ASK_SIZE = MD_ITEM_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Ask Price</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MD_TOP_OF_BOOK__ASK_PRICE = MD_ITEM_FEATURE_COUNT + 3;

	/**
	 * The number of structural features of the '<em>Top Of Book</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MD_TOP_OF_BOOK_FEATURE_COUNT = MD_ITEM_FEATURE_COUNT + 4;

	/**
	 * Returns the meta object for class '{@link org.marketcetera.photon.model.marketdata.MDItem <em>Item</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Item</em>'.
	 * @see org.marketcetera.photon.model.marketdata.MDItem
	 * @generated
	 */
	EClass getMDItem();

	/**
	 * Returns the meta object for the attribute '{@link org.marketcetera.photon.model.marketdata.MDItem#getSymbol <em>Symbol</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Symbol</em>'.
	 * @see org.marketcetera.photon.model.marketdata.MDItem#getSymbol()
	 * @see #getMDItem()
	 * @generated
	 */
	EAttribute getMDItem_Symbol();

	/**
	 * Returns the meta object for class '{@link org.marketcetera.photon.model.marketdata.MDLatestTick <em>Latest Tick</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Latest Tick</em>'.
	 * @see org.marketcetera.photon.model.marketdata.MDLatestTick
	 * @generated
	 */
	EClass getMDLatestTick();

	/**
	 * Returns the meta object for the attribute '{@link org.marketcetera.photon.model.marketdata.MDLatestTick#getPrice <em>Price</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Price</em>'.
	 * @see org.marketcetera.photon.model.marketdata.MDLatestTick#getPrice()
	 * @see #getMDLatestTick()
	 * @generated
	 */
	EAttribute getMDLatestTick_Price();

	/**
	 * Returns the meta object for the attribute '{@link org.marketcetera.photon.model.marketdata.MDLatestTick#getSize <em>Size</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Size</em>'.
	 * @see org.marketcetera.photon.model.marketdata.MDLatestTick#getSize()
	 * @see #getMDLatestTick()
	 * @generated
	 */
	EAttribute getMDLatestTick_Size();

	/**
	 * Returns the meta object for class '{@link org.marketcetera.photon.model.marketdata.MDTopOfBook <em>Top Of Book</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Top Of Book</em>'.
	 * @see org.marketcetera.photon.model.marketdata.MDTopOfBook
	 * @generated
	 */
	EClass getMDTopOfBook();

	/**
	 * Returns the meta object for the attribute '{@link org.marketcetera.photon.model.marketdata.MDTopOfBook#getBidSize <em>Bid Size</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Bid Size</em>'.
	 * @see org.marketcetera.photon.model.marketdata.MDTopOfBook#getBidSize()
	 * @see #getMDTopOfBook()
	 * @generated
	 */
	EAttribute getMDTopOfBook_BidSize();

	/**
	 * Returns the meta object for the attribute '{@link org.marketcetera.photon.model.marketdata.MDTopOfBook#getBidPrice <em>Bid Price</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Bid Price</em>'.
	 * @see org.marketcetera.photon.model.marketdata.MDTopOfBook#getBidPrice()
	 * @see #getMDTopOfBook()
	 * @generated
	 */
	EAttribute getMDTopOfBook_BidPrice();

	/**
	 * Returns the meta object for the attribute '{@link org.marketcetera.photon.model.marketdata.MDTopOfBook#getAskSize <em>Ask Size</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Ask Size</em>'.
	 * @see org.marketcetera.photon.model.marketdata.MDTopOfBook#getAskSize()
	 * @see #getMDTopOfBook()
	 * @generated
	 */
	EAttribute getMDTopOfBook_AskSize();

	/**
	 * Returns the meta object for the attribute '{@link org.marketcetera.photon.model.marketdata.MDTopOfBook#getAskPrice <em>Ask Price</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Ask Price</em>'.
	 * @see org.marketcetera.photon.model.marketdata.MDTopOfBook#getAskPrice()
	 * @see #getMDTopOfBook()
	 * @generated
	 */
	EAttribute getMDTopOfBook_AskPrice();

	/**
	 * Returns the factory that creates the instances of the model.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	MDFactory getMDFactory();

	/**
	 * <!-- begin-user-doc -->
	 * Defines literals for the meta objects that represent
	 * <ul>
	 *   <li>each class,</li>
	 *   <li>each feature of each class,</li>
	 *   <li>each enum,</li>
	 *   <li>and each data type</li>
	 * </ul>
	 * <!-- end-user-doc -->
	 * @generated
	 */
	interface Literals {
		/**
		 * The meta object literal for the '{@link org.marketcetera.photon.model.marketdata.impl.MDItemImpl <em>Item</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.marketcetera.photon.model.marketdata.impl.MDItemImpl
		 * @see org.marketcetera.photon.model.marketdata.impl.MDPackageImpl#getMDItem()
		 * @generated
		 */
		EClass MD_ITEM = eINSTANCE.getMDItem();

		/**
		 * The meta object literal for the '<em><b>Symbol</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute MD_ITEM__SYMBOL = eINSTANCE.getMDItem_Symbol();

		/**
		 * The meta object literal for the '{@link org.marketcetera.photon.model.marketdata.impl.MDLatestTickImpl <em>Latest Tick</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.marketcetera.photon.model.marketdata.impl.MDLatestTickImpl
		 * @see org.marketcetera.photon.model.marketdata.impl.MDPackageImpl#getMDLatestTick()
		 * @generated
		 */
		EClass MD_LATEST_TICK = eINSTANCE.getMDLatestTick();

		/**
		 * The meta object literal for the '<em><b>Price</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute MD_LATEST_TICK__PRICE = eINSTANCE.getMDLatestTick_Price();

		/**
		 * The meta object literal for the '<em><b>Size</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute MD_LATEST_TICK__SIZE = eINSTANCE.getMDLatestTick_Size();

		/**
		 * The meta object literal for the '{@link org.marketcetera.photon.model.marketdata.impl.MDTopOfBookImpl <em>Top Of Book</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.marketcetera.photon.model.marketdata.impl.MDTopOfBookImpl
		 * @see org.marketcetera.photon.model.marketdata.impl.MDPackageImpl#getMDTopOfBook()
		 * @generated
		 */
		EClass MD_TOP_OF_BOOK = eINSTANCE.getMDTopOfBook();

		/**
		 * The meta object literal for the '<em><b>Bid Size</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute MD_TOP_OF_BOOK__BID_SIZE = eINSTANCE.getMDTopOfBook_BidSize();

		/**
		 * The meta object literal for the '<em><b>Bid Price</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute MD_TOP_OF_BOOK__BID_PRICE = eINSTANCE.getMDTopOfBook_BidPrice();

		/**
		 * The meta object literal for the '<em><b>Ask Size</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute MD_TOP_OF_BOOK__ASK_SIZE = eINSTANCE.getMDTopOfBook_AskSize();

		/**
		 * The meta object literal for the '<em><b>Ask Price</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute MD_TOP_OF_BOOK__ASK_PRICE = eINSTANCE.getMDTopOfBook_AskPrice();

	}

} //MDPackage
