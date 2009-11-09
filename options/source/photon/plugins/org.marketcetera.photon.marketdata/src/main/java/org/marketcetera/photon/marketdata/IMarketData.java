package org.marketcetera.photon.marketdata;

import org.eclipse.emf.ecore.EObject;
import org.marketcetera.marketdata.MarketDataRequest.Content;
import org.marketcetera.photon.internal.marketdata.MarketData;
import org.marketcetera.photon.model.marketdata.MDDepthOfBook;
import org.marketcetera.photon.model.marketdata.MDLatestTick;
import org.marketcetera.photon.model.marketdata.MDMarketstat;
import org.marketcetera.photon.model.marketdata.MDTopOfBook;
import org.marketcetera.trade.Instrument;
import org.marketcetera.util.misc.ClassVersion;

import com.google.inject.ImplementedBy;

/* $License$ */

/**
 * Interface for accessing common market data. Market data is returned as a disposable reference to
 * an EMF object. It is important to {@link IMarketDataReference#dispose() dispose} the references
 * when they are no longer needed to release the associated resources.
 * <p>
 * The EMF objects returned are read-only and setters have been suppressed. Note that although it is
 * possible to mutate them via the reflective
 * {@link EObject#eSet(org.eclipse.emf.ecore.EStructuralFeature, Object)}, this is
 * <strong>not</strong> supported and could corrupt other clients of the data.
 * <p>
 * Market data EObjects provide fine grained change notification, making them well-suited for UI
 * display. For example, the following snippet shows how to register for notifications when the
 * latest tick changes (generally, such low level code can be avoided by using the databinding
 * framework):
 * 
 * <pre>
 * IMarketData marketData = ...
 * IMarketDataReference ref = marketData.getLatestTick(new Equity(&quot;METC&quot;));
 * ref.get().eAdapters().add(new AdapterImpl() {
 *   &#064;Override
 *   public void notifyChanged(Notification msg) {
 *     if (msg.getEventType() == Notification.SET &amp;&amp; 
 *         msg.getFeature() == MDPackage.Literals.MD_LATEST_TICK__PRICE) {
 *       handle((BigDecimal) msg.getNewValue());
 *     }
 *   }
 * });
 * </pre>
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.5.0
 */
@ClassVersion("$Id$")
@ImplementedBy(MarketData.class)
public interface IMarketData {

    /**
     * Returns a reference to the latest tick data for the given instrument. If
     * the data does not exist, it will be created and wired up. The
     * {@link IMarketDataReference#dispose() dispose} method should be called
     * when the data is no longer needed.
     * 
     * @param instrument
     *            the instrument
     * @return a reference to the data
     * @throws IllegalArgumentException
     *             if instrument is null
     * @throws IllegalStateException
     *             if the module framework is in an unexpected state, or if an
     *             unrecoverable error occurs
     */
    IMarketDataReference<MDLatestTick> getLatestTick(Instrument instrument);

    /**
     * Returns a reference to the top of book data for the given instrument. If
     * the data does not exist, it will be created and wired up. The
     * {@link IMarketDataReference#dispose() dispose} method should be called
     * when the data is no longer needed.
     * 
     * @param instrument
     *            the instrument
     * @return a reference to the data
     * @throws IllegalArgumentException
     *             if instrument is null
     * @throws IllegalStateException
     *             if the module framework is in an unexpected state, or if an
     *             unrecoverable error occurs
     */
    IMarketDataReference<MDTopOfBook> getTopOfBook(Instrument instrument);

    /**
     * Returns a reference to the market statistic data for the given
     * instrument. If the data does not exist, it will be created and wired up.
     * The {@link IMarketDataReference#dispose() dispose} method should be
     * called when the data is no longer needed.
     * 
     * @param instrument
     *            the instrument
     * @return a reference to the data
     * @throws IllegalArgumentException
     *             if instrument is null
     * @throws IllegalStateException
     *             if the module framework is in an unexpected state, or if an
     *             unrecoverable error occurs
     */
    IMarketDataReference<MDMarketstat> getMarketstat(Instrument instrument);

    /**
     * Returns a reference to the market depth data for the given instrument and
     * product. If the data does not exist, it will be created and wired up. The
     * {@link IMarketDataReference#dispose() dispose} method should be called
     * when the data is no longer needed.
     * 
     * @param instrument
     *            the instrument
     * @param product
     *            the product
     * @return a reference to the data
     * @throws IllegalArgumentException
     *             if instrument or product is null, or if product is not a
     *             valid market depth product
     * @throws IllegalStateException
     *             if the module framework is in an unexpected state, or if an
     *             unrecoverable error occurs
     */
    IMarketDataReference<MDDepthOfBook> getDepthOfBook(Instrument instrument,
            Content product);
}