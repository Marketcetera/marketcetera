package org.marketcetera.photon.internal.marketdata;

import org.marketcetera.photon.model.marketdata.impl.MDDepthOfBookImpl;
import org.marketcetera.util.misc.ClassVersion;

import com.google.inject.ImplementedBy;

/* $License$ */

/**
 * Interface for a manger of market depth data flows.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id: ILatestTickManager.java 10495 2009-04-15 21:37:09Z will $
 * @since 1.5.0
 */
@ClassVersion("$Id$")
@ImplementedBy(DepthOfBookManager.class)
public interface IDepthOfBookManager extends IDataFlowManager<MDDepthOfBookImpl, DepthOfBookKey> {
}
