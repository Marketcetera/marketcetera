package org.marketcetera.tensorflow.service.impl;

import org.marketcetera.tensorflow.GraphContainer;
import org.marketcetera.tensorflow.dao.GraphContainerDao;
import org.marketcetera.tensorflow.service.TensorFlowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/* $License$ */

/**
 * Provides Tensor Flow services.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Component
public class TensorFlowServiceImpl
        implements TensorFlowService
{
    /* (non-Javadoc)
     * @see org.marketcetera.tensorflow.service.TensorFlowService#findByName(java.lang.String)
     */
    @Override
    @Transactional(readOnly=true,propagation=Propagation.REQUIRED)
    public GraphContainer findByName(String inName)
    {
        return graphContainerDao.findByName(inName);
    }
    /**
     * provides data store access to graph containers
     */
    @Autowired
    private GraphContainerDao graphContainerDao;
}
