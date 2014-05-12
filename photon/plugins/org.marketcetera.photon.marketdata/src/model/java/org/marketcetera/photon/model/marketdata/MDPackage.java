/**
 * $License$
 *
 * $Id$
 */
package org.marketcetera.photon.model.marketdata;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

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
 * @since 2.1.0
 */
@ClassVersion("$Id$")
public interface MDPackage
        extends EPackage
{
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
     * The feature id for the '<em><b>Instrument</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MD_ITEM__INSTRUMENT = 0;

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
     * The feature id for the '<em><b>Instrument</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MD_LATEST_TICK__INSTRUMENT = MD_ITEM__INSTRUMENT;

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
     * The feature id for the '<em><b>Multiplier</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MD_LATEST_TICK__MULTIPLIER = MD_ITEM_FEATURE_COUNT + 2;

    /**
     * The number of structural features of the '<em>Latest Tick</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MD_LATEST_TICK_FEATURE_COUNT = MD_ITEM_FEATURE_COUNT + 3;

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
     * The feature id for the '<em><b>Instrument</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MD_TOP_OF_BOOK__INSTRUMENT = MD_ITEM__INSTRUMENT;

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
     * The meta object id for the '{@link org.marketcetera.photon.model.marketdata.impl.MDMarketstatImpl <em>Marketstat</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.marketcetera.photon.model.marketdata.impl.MDMarketstatImpl
     * @see org.marketcetera.photon.model.marketdata.impl.MDPackageImpl#getMDMarketstat()
     * @generated
     */
    int MD_MARKETSTAT = 3;

    /**
     * The feature id for the '<em><b>Instrument</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MD_MARKETSTAT__INSTRUMENT = MD_ITEM__INSTRUMENT;

    /**
     * The feature id for the '<em><b>Close Price</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MD_MARKETSTAT__CLOSE_PRICE = MD_ITEM_FEATURE_COUNT + 0;

    /**
     * The feature id for the '<em><b>Close Date</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MD_MARKETSTAT__CLOSE_DATE = MD_ITEM_FEATURE_COUNT + 1;

    /**
     * The feature id for the '<em><b>Previous Close Price</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MD_MARKETSTAT__PREVIOUS_CLOSE_PRICE = MD_ITEM_FEATURE_COUNT + 2;

    /**
     * The feature id for the '<em><b>Previous Close Date</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MD_MARKETSTAT__PREVIOUS_CLOSE_DATE = MD_ITEM_FEATURE_COUNT + 3;

    /**
     * The feature id for the '<em><b>High Price</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */

    int MD_MARKETSTAT__HIGH_PRICE = MD_ITEM_FEATURE_COUNT + 4;

    /**
     * The feature id for the '<em><b>Low Price</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */

    int MD_MARKETSTAT__LOW_PRICE = MD_ITEM_FEATURE_COUNT + 5;

    /**
     * The feature id for the '<em><b>Volume Traded</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MD_MARKETSTAT__VOLUME_TRADED = MD_ITEM_FEATURE_COUNT + 6;

    /**
     * The feature id for the '<em><b>Open Price</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MD_MARKETSTAT__OPEN_PRICE = MD_ITEM_FEATURE_COUNT + 7;

    /**
     * The feature id for the '<em><b>Volume</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MD_MARKETSTAT__VOLUME = MD_ITEM_FEATURE_COUNT + 8;

    /**
     * The number of structural features of the '<em>Marketstat</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */

    int MD_MARKETSTAT_FEATURE_COUNT = MD_ITEM_FEATURE_COUNT + 9;

    /**
     * The meta object id for the '{@link org.marketcetera.photon.model.marketdata.impl.MDDepthOfBookImpl <em>Depth Of Book</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.marketcetera.photon.model.marketdata.impl.MDDepthOfBookImpl
     * @see org.marketcetera.photon.model.marketdata.impl.MDPackageImpl#getMDDepthOfBook()
     * @generated
     */
    int MD_DEPTH_OF_BOOK = 4;

    /**
     * The feature id for the '<em><b>Instrument</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MD_DEPTH_OF_BOOK__INSTRUMENT = MD_ITEM__INSTRUMENT;

    /**
     * The feature id for the '<em><b>Product</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MD_DEPTH_OF_BOOK__PRODUCT = MD_ITEM_FEATURE_COUNT + 0;

    /**
     * The feature id for the '<em><b>Bids</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MD_DEPTH_OF_BOOK__BIDS = MD_ITEM_FEATURE_COUNT + 1;

    /**
     * The feature id for the '<em><b>Asks</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MD_DEPTH_OF_BOOK__ASKS = MD_ITEM_FEATURE_COUNT + 2;

    /**
     * The number of structural features of the '<em>Depth Of Book</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MD_DEPTH_OF_BOOK_FEATURE_COUNT = MD_ITEM_FEATURE_COUNT + 3;

    /**
     * The meta object id for the '{@link org.marketcetera.photon.model.marketdata.impl.MDQuoteImpl <em>Quote</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.marketcetera.photon.model.marketdata.impl.MDQuoteImpl
     * @see org.marketcetera.photon.model.marketdata.impl.MDPackageImpl#getMDQuote()
     * @generated
     */
    int MD_QUOTE = 5;

    /**
     * The feature id for the '<em><b>Instrument</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MD_QUOTE__INSTRUMENT = MD_ITEM__INSTRUMENT;

    /**
     * The feature id for the '<em><b>Price</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MD_QUOTE__PRICE = MD_ITEM_FEATURE_COUNT + 0;

    /**
     * The feature id for the '<em><b>Size</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MD_QUOTE__SIZE = MD_ITEM_FEATURE_COUNT + 1;

    /**
     * The feature id for the '<em><b>Source</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MD_QUOTE__SOURCE = MD_ITEM_FEATURE_COUNT + 2;

    /**
     * The feature id for the '<em><b>Time</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MD_QUOTE__TIME = MD_ITEM_FEATURE_COUNT + 3;

    /**
     * The number of structural features of the '<em>Quote</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MD_QUOTE_FEATURE_COUNT = MD_ITEM_FEATURE_COUNT + 4;

    /**
     * The meta object id for the '<em>Depth Of Book Product</em>' data type.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.marketcetera.marketdata.Content
     * @see org.marketcetera.photon.model.marketdata.impl.MDPackageImpl#getDepthOfBookProduct()
     * @generated
     */
    int DEPTH_OF_BOOK_PRODUCT = 6;

    /**
     * The meta object id for the '<em>Instrument</em>' data type.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.marketcetera.trade.Instrument
     * @see org.marketcetera.photon.model.marketdata.impl.MDPackageImpl#getInstrument()
     * @generated
     */
    int INSTRUMENT = 7;

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
     * Returns the meta object for the attribute '{@link org.marketcetera.photon.model.marketdata.MDItem#getInstrument <em>Instrument</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Instrument</em>'.
     * @see org.marketcetera.photon.model.marketdata.MDItem#getInstrument()
     * @see #getMDItem()
     * @generated
     */
    EAttribute getMDItem_Instrument();

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
     * Returns the meta object for the attribute '{@link org.marketcetera.photon.model.marketdata.MDLatestTick#getMultiplier <em>Multiplier</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Multiplier</em>'.
     * @see org.marketcetera.photon.model.marketdata.MDLatestTick#getMultiplier()
     * @see #getMDLatestTick()
     * @generated
     */
    EAttribute getMDLatestTick_Multiplier();

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
     * Returns the meta object for class '{@link org.marketcetera.photon.model.marketdata.MDMarketstat <em>Marketstat</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Marketstat</em>'.
     * @see org.marketcetera.photon.model.marketdata.MDMarketstat
     * @generated
     */
    EClass getMDMarketstat();

    /**
     * Returns the meta object for the attribute '{@link org.marketcetera.photon.model.marketdata.MDMarketstat#getClosePrice <em>Close Price</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Close Price</em>'.
     * @see org.marketcetera.photon.model.marketdata.MDMarketstat#getClosePrice()
     * @see #getMDMarketstat()
     * @generated
     */
    EAttribute getMDMarketstat_ClosePrice();

    /**
     * Returns the meta object for the attribute '{@link org.marketcetera.photon.model.marketdata.MDMarketstat#getCloseDate <em>Close Date</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Close Date</em>'.
     * @see org.marketcetera.photon.model.marketdata.MDMarketstat#getCloseDate()
     * @see #getMDMarketstat()
     * @generated
     */
    EAttribute getMDMarketstat_CloseDate();

    /**
     * Returns the meta object for the attribute '{@link org.marketcetera.photon.model.marketdata.MDMarketstat#getPreviousClosePrice <em>Previous Close Price</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Previous Close Price</em>'.
     * @see org.marketcetera.photon.model.marketdata.MDMarketstat#getPreviousClosePrice()
     * @see #getMDMarketstat()
     * @generated
     */
    EAttribute getMDMarketstat_PreviousClosePrice();

    /**
     * Returns the meta object for the attribute '{@link org.marketcetera.photon.model.marketdata.MDMarketstat#getPreviousCloseDate <em>Previous Close Date</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Previous Close Date</em>'.
     * @see org.marketcetera.photon.model.marketdata.MDMarketstat#getPreviousCloseDate()
     * @see #getMDMarketstat()
     * @generated
     */
    EAttribute getMDMarketstat_PreviousCloseDate();

    /**
     * Returns the meta object for the attribute '{@link org.marketcetera.photon.model.marketdata.MDMarketstat#getHighPrice <em>High Price</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>High Price</em>'.
     * @see org.marketcetera.photon.model.marketdata.MDMarketstat#getHighPrice()
     * @see #getMDMarketstat()
     * @generated
     */
    EAttribute getMDMarketstat_HighPrice();

    /**
     * Returns the meta object for the attribute '{@link org.marketcetera.photon.model.marketdata.MDMarketstat#getLowPrice <em>Low Price</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Low Price</em>'.
     * @see org.marketcetera.photon.model.marketdata.MDMarketstat#getLowPrice()
     * @see #getMDMarketstat()
     * @generated
     */
    EAttribute getMDMarketstat_LowPrice();

    /**
     * Returns the meta object for the attribute '{@link org.marketcetera.photon.model.marketdata.MDMarketstat#getVolumeTraded <em>Volume Traded</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Volume Traded</em>'.
     * @see org.marketcetera.photon.model.marketdata.MDMarketstat#getVolumeTraded()
     * @see #getMDMarketstat()
     * @generated
     */
    EAttribute getMDMarketstat_VolumeTraded();

    /**
     * Returns the meta object for the attribute '{@link org.marketcetera.photon.model.marketdata.MDMarketstat#getOpenPrice <em>Open Price</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Open Price</em>'.
     * @see org.marketcetera.photon.model.marketdata.MDMarketstat#getOpenPrice()
     * @see #getMDMarketstat()
     * @generated
     */
    EAttribute getMDMarketstat_OpenPrice();

    /**
     * Returns the meta object for the attribute '{@link org.marketcetera.photon.model.marketdata.MDMarketstat#getVolume <em>Volume</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Volume</em>'.
     * @see org.marketcetera.photon.model.marketdata.MDMarketstat#getVolume()
     * @see #getMDMarketstat()
     * @generated
     */
    EAttribute getMDMarketstat_Volume();

    /**
     * Returns the meta object for class '{@link org.marketcetera.photon.model.marketdata.MDDepthOfBook <em>Depth Of Book</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Depth Of Book</em>'.
     * @see org.marketcetera.photon.model.marketdata.MDDepthOfBook
     * @generated
     */
    EClass getMDDepthOfBook();

    /**
     * Returns the meta object for the attribute '{@link org.marketcetera.photon.model.marketdata.MDDepthOfBook#getProduct <em>Product</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Product</em>'.
     * @see org.marketcetera.photon.model.marketdata.MDDepthOfBook#getProduct()
     * @see #getMDDepthOfBook()
     * @generated
     */
    EAttribute getMDDepthOfBook_Product();

    /**
     * Returns the meta object for the containment reference list '{@link org.marketcetera.photon.model.marketdata.MDDepthOfBook#getBids <em>Bids</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Bids</em>'.
     * @see org.marketcetera.photon.model.marketdata.MDDepthOfBook#getBids()
     * @see #getMDDepthOfBook()
     * @generated
     */
    EReference getMDDepthOfBook_Bids();

    /**
     * Returns the meta object for the containment reference list '{@link org.marketcetera.photon.model.marketdata.MDDepthOfBook#getAsks <em>Asks</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Asks</em>'.
     * @see org.marketcetera.photon.model.marketdata.MDDepthOfBook#getAsks()
     * @see #getMDDepthOfBook()
     * @generated
     */
    EReference getMDDepthOfBook_Asks();

    /**
     * Returns the meta object for class '{@link org.marketcetera.photon.model.marketdata.MDQuote <em>Quote</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Quote</em>'.
     * @see org.marketcetera.photon.model.marketdata.MDQuote
     * @generated
     */
    EClass getMDQuote();

    /**
     * Returns the meta object for the attribute '{@link org.marketcetera.photon.model.marketdata.MDQuote#getPrice <em>Price</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Price</em>'.
     * @see org.marketcetera.photon.model.marketdata.MDQuote#getPrice()
     * @see #getMDQuote()
     * @generated
     */
    EAttribute getMDQuote_Price();

    /**
     * Returns the meta object for the attribute '{@link org.marketcetera.photon.model.marketdata.MDQuote#getSize <em>Size</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Size</em>'.
     * @see org.marketcetera.photon.model.marketdata.MDQuote#getSize()
     * @see #getMDQuote()
     * @generated
     */
    EAttribute getMDQuote_Size();

    /**
     * Returns the meta object for the attribute '{@link org.marketcetera.photon.model.marketdata.MDQuote#getSource <em>Source</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Source</em>'.
     * @see org.marketcetera.photon.model.marketdata.MDQuote#getSource()
     * @see #getMDQuote()
     * @generated
     */
    EAttribute getMDQuote_Source();

    /**
     * Returns the meta object for the attribute '{@link org.marketcetera.photon.model.marketdata.MDQuote#getTime <em>Time</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Time</em>'.
     * @see org.marketcetera.photon.model.marketdata.MDQuote#getTime()
     * @see #getMDQuote()
     * @generated
     */
    EAttribute getMDQuote_Time();

    /**
     * Returns the meta object for data type '{@link org.marketcetera.marketdata.Content <em>Depth Of Book Product</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for data type '<em>Depth Of Book Product</em>'.
     * @see org.marketcetera.marketdata.Content
     * @model instanceClass="org.marketcetera.marketdata.Content"
     * @generated
     */
    EDataType getDepthOfBookProduct();

    /**
     * Returns the meta object for data type '{@link org.marketcetera.trade.Instrument <em>Instrument</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for data type '<em>Instrument</em>'.
     * @see org.marketcetera.trade.Instrument
     * @model instanceClass="org.marketcetera.trade.Instrument"
     * @generated
     */
    EDataType getInstrument();

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
    interface Literals
    {
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
         * The meta object literal for the '<em><b>Instrument</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute MD_ITEM__INSTRUMENT = eINSTANCE.getMDItem_Instrument();

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
         * The meta object literal for the '<em><b>Multiplier</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute MD_LATEST_TICK__MULTIPLIER = eINSTANCE.getMDLatestTick_Multiplier();

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

        /**
         * The meta object literal for the '{@link org.marketcetera.photon.model.marketdata.impl.MDMarketstatImpl <em>Marketstat</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.marketcetera.photon.model.marketdata.impl.MDMarketstatImpl
         * @see org.marketcetera.photon.model.marketdata.impl.MDPackageImpl#getMDMarketstat()
         * @generated
         */
        EClass MD_MARKETSTAT = eINSTANCE.getMDMarketstat();

        /**
         * The meta object literal for the '<em><b>Close Price</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute MD_MARKETSTAT__CLOSE_PRICE = eINSTANCE.getMDMarketstat_ClosePrice();

        /**
         * The meta object literal for the '<em><b>Close Date</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute MD_MARKETSTAT__CLOSE_DATE = eINSTANCE.getMDMarketstat_CloseDate();

        /**
         * The meta object literal for the '<em><b>Open Price</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute MD_MARKETSTAT__OPEN_PRICE = eINSTANCE.getMDMarketstat_OpenPrice();

        /**
         * The meta object literal for the '<em><b>Volume</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute MD_MARKETSTAT__VOLUME = eINSTANCE.getMDMarketstat_Volume();

        /**
         * The meta object literal for the '<em><b>Low Price</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */

        EAttribute MD_MARKETSTAT__LOW_PRICE = eINSTANCE.getMDMarketstat_LowPrice();

        /**
         * The meta object literal for the '<em><b>Volume Traded</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute MD_MARKETSTAT__VOLUME_TRADED = eINSTANCE.getMDMarketstat_VolumeTraded();

        /**
         * The meta object literal for the '<em><b>High Price</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute MD_MARKETSTAT__HIGH_PRICE = eINSTANCE.getMDMarketstat_HighPrice();

        /**
         * The meta object literal for the '<em><b>Previous Close Price</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute MD_MARKETSTAT__PREVIOUS_CLOSE_PRICE = eINSTANCE.getMDMarketstat_PreviousClosePrice();

        /**
         * The meta object literal for the '<em><b>Previous Close Date</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute MD_MARKETSTAT__PREVIOUS_CLOSE_DATE = eINSTANCE.getMDMarketstat_PreviousCloseDate();

        /**
         * The meta object literal for the '{@link org.marketcetera.photon.model.marketdata.impl.MDDepthOfBookImpl <em>Depth Of Book</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.marketcetera.photon.model.marketdata.impl.MDDepthOfBookImpl
         * @see org.marketcetera.photon.model.marketdata.impl.MDPackageImpl#getMDDepthOfBook()
         * @generated
         */
        EClass MD_DEPTH_OF_BOOK = eINSTANCE.getMDDepthOfBook();

        /**
         * The meta object literal for the '<em><b>Product</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute MD_DEPTH_OF_BOOK__PRODUCT = eINSTANCE.getMDDepthOfBook_Product();

        /**
         * The meta object literal for the '<em><b>Bids</b></em>' containment reference list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference MD_DEPTH_OF_BOOK__BIDS = eINSTANCE.getMDDepthOfBook_Bids();

        /**
         * The meta object literal for the '<em><b>Asks</b></em>' containment reference list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference MD_DEPTH_OF_BOOK__ASKS = eINSTANCE.getMDDepthOfBook_Asks();

        /**
         * The meta object literal for the '{@link org.marketcetera.photon.model.marketdata.impl.MDQuoteImpl <em>Quote</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.marketcetera.photon.model.marketdata.impl.MDQuoteImpl
         * @see org.marketcetera.photon.model.marketdata.impl.MDPackageImpl#getMDQuote()
         * @generated
         */
        EClass MD_QUOTE = eINSTANCE.getMDQuote();

        /**
         * The meta object literal for the '<em><b>Price</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute MD_QUOTE__PRICE = eINSTANCE.getMDQuote_Price();

        /**
         * The meta object literal for the '<em><b>Size</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute MD_QUOTE__SIZE = eINSTANCE.getMDQuote_Size();

        /**
         * The meta object literal for the '<em><b>Source</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute MD_QUOTE__SOURCE = eINSTANCE.getMDQuote_Source();

        /**
         * The meta object literal for the '<em><b>Time</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute MD_QUOTE__TIME = eINSTANCE.getMDQuote_Time();

        /**
         * The meta object literal for the '<em>Depth Of Book Product</em>' data type.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.marketcetera.marketdata.Content
         * @see org.marketcetera.photon.model.marketdata.impl.MDPackageImpl#getDepthOfBookProduct()
         * @generated
         */
        EDataType DEPTH_OF_BOOK_PRODUCT = eINSTANCE.getDepthOfBookProduct();

        /**
         * The meta object literal for the '<em>Instrument</em>' data type.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.marketcetera.trade.Instrument
         * @see org.marketcetera.photon.model.marketdata.impl.MDPackageImpl#getInstrument()
         * @generated
         */
        EDataType INSTRUMENT = eINSTANCE.getInstrument();

    }

} //MDPackage