package org.marketcetera.tensorflow.service;

import org.marketcetera.tensorflow.GraphContainer;
import org.tensorflow.Graph;

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
    /**
     * Create and persist a <code>GraphContainer</code> with the given attributes.
     * 
     * <p>The name value must be unique among all persisted <code>GraphContainer</code> objects.
     * 
     * <p>The resulting <code>GraphContainer</code> will be persisted and can be retrieved using {@link #findByName(String)}.
     *
     * @param inGraph a <code>Graph</code> value
     * @param inModelName a <code>String</code> value
     * @param inDescription a <code>String</code> value or <code>null</code>
     * @return a <code>GraphContainer</code>value
     */
    GraphContainer createContainer(Graph inGraph,
                                   String inModelName,
                                   String inDescription);
}
