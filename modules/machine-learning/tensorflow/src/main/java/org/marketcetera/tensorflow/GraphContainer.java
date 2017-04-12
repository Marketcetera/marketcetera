package org.marketcetera.tensorflow;

import org.marketcetera.persist.SummaryNDEntityBase;
import org.tensorflow.Graph;

/* $License$ */

/**
 * Represents a Tensor Flow Graph object.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface GraphContainer
        extends SummaryNDEntityBase
{
    /**
     * Read a <code>Graph</code> object from the container.
     *
     * @return a <code>Graph</code> value
     */
    Graph readGraph();
    /**
     * Write a <code>Graph</code> object to the container.
     *
     * @param inGraph a <code>Graph</code> value
     */
    void writeGraph(Graph inGraph);
}
