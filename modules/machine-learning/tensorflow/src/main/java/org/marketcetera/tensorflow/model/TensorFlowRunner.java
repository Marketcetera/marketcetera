package org.marketcetera.tensorflow.model;

import org.marketcetera.module.DataRequest;
import org.tensorflow.Tensor;

/* $License$ */

/**
 * Renders an output tensor for a particular data flow to an object.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface TensorFlowRunner
{
    /**
     * Fetch an output object from the given Tensor for the given data flow request.
     *
     * @param inDataRequest a <code>DataRequest</code> value
     * @param inInput a <code>Tensor</code> value
     * @return an <code>Object</code> value
     */
    Object fetch(DataRequest inDataRequest,
                 Tensor inInput);
}
