package org.marketcetera.photon.internal.marketdata;

import org.marketcetera.photon.model.marketdata.MDLatestTick;
import org.marketcetera.util.misc.ClassVersion;

import com.google.inject.ImplementedBy;

/* $License$ */

/**
 * Interface for a manger of latest tick market data flows.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
@ImplementedBy(LatestTickManager.class)
public interface ILatestTickManager extends IDataFlowManager<MDLatestTick, LatestTickKey> {
}
