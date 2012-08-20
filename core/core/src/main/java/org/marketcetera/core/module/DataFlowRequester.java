package org.marketcetera.core.module;

import org.marketcetera.api.attributes.ClassVersion;


/* $License$ */
/**
 * An interface that is implemented by modules that are capable
 * of requesting data flows.
 *
 * @author anshul@marketcetera.com
 * @version $Id: DataFlowRequester.java 16063 2012-01-31 18:21:55Z colin $
 * @since 1.0.0
 */
@ClassVersion("$Id: DataFlowRequester.java 16063 2012-01-31 18:21:55Z colin $")  //$NON-NLS-1$
public interface DataFlowRequester {
    /**
     * Supplies the support instance that can be used to request
     * data flows dynamically. This method is invoked right after
     * the module has been created and is guaranteed to be invoked
     * before its started.
     *
     * @param inSupport the data request support instance.
     */
    public void setFlowSupport(DataFlowSupport inSupport);
}
