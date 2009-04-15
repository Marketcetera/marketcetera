package org.marketcetera.photon.internal.marketdata;

import org.marketcetera.photon.model.marketdata.impl.MDTopOfBookImpl;
import org.marketcetera.util.misc.ClassVersion;

import com.google.inject.ImplementedBy;

/* $License$ */

/**
 * Interface for a manger of top of book market data flows.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.5.0
 */
@ClassVersion("$Id$")
@ImplementedBy(TopOfBookManager.class)
public interface ITopOfBookManager extends IDataFlowManager<MDTopOfBookImpl, TopOfBookKey> {
}
