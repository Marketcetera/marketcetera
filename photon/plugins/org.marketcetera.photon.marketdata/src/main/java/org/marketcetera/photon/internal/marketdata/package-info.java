/* $License$ */

/**
 * Internal implementation of the API provided in {@link org.marketcetera.photon.marketdata}.
 * <p>
 * This package sits above the {@link org.marketcetera.module} and
 * {@link org.marketcetera.marketdata} packages to provide simple, shared access to the platform
 * marketdata. It abstracts away details about the underlying module framework and makes an effort to
 * maximize resource sharing since many clients may request identical market data.
 * <p>
 * Refer to {@link org.marketcetera.photon.internal.marketdata.MarketDataManager} for information
 * about market data module management and to {@link org.marketcetera.photon.internal.marketdata.MarketData} for
 * information about market data.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.0.0
 */
package org.marketcetera.photon.internal.marketdata;

