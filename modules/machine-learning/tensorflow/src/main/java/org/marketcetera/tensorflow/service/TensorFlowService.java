package org.marketcetera.tensorflow.service;

import org.marketcetera.tensorflow.GraphContainer;

/* $License$ */

/**
 * Provides TensorFlow services.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface TensorFlowService
{
    /**
     * Find the <code>GraphContainer</code> with the given name.
     *
     * @param inModelName a <code>String</code> value
     * @return a <code>GraphContainer</code> value or <code>null</code>
     */
    GraphContainer findByName(String inModelName);
}
