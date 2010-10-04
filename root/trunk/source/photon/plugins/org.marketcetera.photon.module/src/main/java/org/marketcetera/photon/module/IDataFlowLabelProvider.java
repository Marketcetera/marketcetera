package org.marketcetera.photon.module;

import org.marketcetera.module.DataFlowID;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Interface for a service that provides custom labels for data arriving on a
 * particular data flow.
 * <p>
 * This is used to identify the source of data that is send to the
 * {@link ISinkDataHandler sink data handler}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 2.1.0
 */
@ClassVersion("$Id$")
public interface IDataFlowLabelProvider {

    /**
     * Returns the label to be used for the data, or null if no custom label is
     * available.
     * 
     * @param dataFlowId
     *            the id of the data flow from which the data originated
     * @return the label, or null
     */
    String getLabel(DataFlowID dataFlowId);
}
