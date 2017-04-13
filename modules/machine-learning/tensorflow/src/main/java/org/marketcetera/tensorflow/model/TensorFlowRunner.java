package org.marketcetera.tensorflow.model;

import org.marketcetera.module.DataRequest;
import org.tensorflow.Tensor;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface TensorFlowRunner
{
    Object fetch(DataRequest inDataRequest,
                 Tensor inInput);
}
