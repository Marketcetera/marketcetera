/* $License$ */

/**
 * This package is the API for this plug-in, which manages access to market data functionality.
 * It provides two high level services:
 * <ul>
 * <li>Information about available market data feeds, and methods to manage them</li>
 * <li>Access to EMF (Eclipse Modeling Framework) objects reflecting dynamic market data</li>
 * </ul>
 * When the plug-in is activated, an instance of {@link org.marketcetera.photon.marketdata.IMarketDataManager} is registered as an
 * OSGi service. This service is the entry point into this plug-in's functionality.
 * <p>
 * Although this plug-in manages multiple market data feeds, it maintains a single "active"
 * feed. See {@link org.marketcetera.photon.marketdata.IMarketDataManager} for more details.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.0.0
 */
package org.marketcetera.photon.marketdata;

