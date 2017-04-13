package org.marketcetera.tensorflow.model.impl;

import org.marketcetera.module.DataRequest;
import org.marketcetera.tensorflow.model.TensorFlowRunner;
import org.tensorflow.Tensor;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class NoOpTensorFlowRunner
        implements TensorFlowRunner
{
    /* (non-Javadoc)
     * @see org.marketcetera.tensorflow.model.TensorFlowRunner#fetch(org.marketcetera.module.DataRequest, org.tensorflow.Tensor)
     */
    @Override
    public Object fetch(DataRequest inDataRequest,
                        Tensor inInput)
    {
        return result;
    }
    /**
     * 
     */
    private final String result = "noop";
}
