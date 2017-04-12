package org.marketcetera.tensorflow.service.impl;

import org.marketcetera.tensorflow.dao.GraphContainerDao;
import org.marketcetera.tensorflow.dao.PersistentGraphContainer;
import org.marketcetera.tensorflow.service.TensorFlowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.tensorflow.Graph;

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
    public PersistentGraphContainer findByName(String inName)
    {
        return graphContainerDao.findByName(inName);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.tensorflow.service.TensorFlowService#createContainer(org.tensorflow.Graph, java.lang.String, java.lang.String)
     */
    @Override
    @Transactional(readOnly=false,propagation=Propagation.REQUIRED)
    public PersistentGraphContainer createContainer(Graph inGraph,
                                                    String inModelName,
                                                    String inDescription)
    {
        PersistentGraphContainer graphContainer = new PersistentGraphContainer();
        graphContainer.setName(inModelName);
        graphContainer.setDescription(inDescription);
        graphContainer.writeGraph(inGraph);
        graphContainer = graphContainerDao.save(graphContainer);
        return graphContainer;
    }
    /**
     * provides data store access to graph containers
     */
    @Autowired
    private GraphContainerDao graphContainerDao;
}
