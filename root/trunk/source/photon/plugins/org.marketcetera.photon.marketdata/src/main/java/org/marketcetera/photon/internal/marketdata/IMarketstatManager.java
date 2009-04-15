package org.marketcetera.photon.internal.marketdata;

import org.marketcetera.photon.model.marketdata.impl.MDMarketstatImpl;
import org.marketcetera.util.misc.ClassVersion;

import com.google.inject.ImplementedBy;

/* $License$ */

/**
 * Interface for a manger of market statistic data flows.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
@ImplementedBy(MarketstatManager.class)
public interface IMarketstatManager extends IDataFlowManager<MDMarketstatImpl, MarketstatKey> {
}
