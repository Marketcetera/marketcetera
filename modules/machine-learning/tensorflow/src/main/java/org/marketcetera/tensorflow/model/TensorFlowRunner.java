package org.marketcetera.tensorflow.model;

import org.tensorflow.Session;
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
    Tensor fetch(Session inSession,
                 Tensor inInput);
}
