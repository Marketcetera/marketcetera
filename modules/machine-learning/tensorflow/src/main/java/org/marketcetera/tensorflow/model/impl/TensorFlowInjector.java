package org.marketcetera.tensorflow.model.impl;

import java.util.Collection;
import java.util.List;

import org.marketcetera.core.QueueProcessor;
import org.marketcetera.module.DataFlowID;
import org.marketcetera.module.DataRequest;
import org.marketcetera.module.ModuleManager;
import org.marketcetera.module.ModuleURN;
import org.marketcetera.modules.headwater.HeadwaterModule;
import org.marketcetera.modules.headwater.HeadwaterModuleFactory;
import org.marketcetera.tensorflow.converter.TensorFlowConverterModuleFactory;
import org.marketcetera.tensorflow.model.TensorFlowModelModuleFactory;
import org.marketcetera.tensorflow.model.TensorFlowRunner;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Lists;

/* $License$ */

/**
 * Injects data into a trained Tensor Flow model. 
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class TensorFlowInjector
        extends QueueProcessor<Object>
{
    /**
     * Get the tensorFlowRunner value.
     *
     * @return a <code>TensorFlowRunner</code> value
     */
    public TensorFlowRunner getTensorFlowRunner()
    {
        return tensorFlowRunner;
    }
    /**
     * Sets the tensorFlowRunner value.
     *
     * @param inTensorFlowRunner a <code>TensorFlowRunner</code> value
     */
    public void setTensorFlowRunner(TensorFlowRunner inTensorFlowRunner)
    {
        tensorFlowRunner = inTensorFlowRunner;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.QueueProcessor#add(java.lang.Object)
     */
    @Override
    public void add(Object inData)
    {
        super.add(inData);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.QueueProcessor#addAll(java.util.Collection)
     */
    @Override
    public void addAll(Collection<Object> inData)
    {
        super.addAll(inData);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.QueueProcessor#onStart()
     */
    @Override
    protected void onStart()
            throws Exception
    {
        initiateDataFlows();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.QueueProcessor#onStop()
     */
    @Override
    protected void onStop()
            throws Exception
    {
        cancelDataFlows();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.QueueProcessor#processData(java.lang.Object)
     */
    @Override
    protected void processData(Object inData)
            throws Exception
    {
        if(dataFlow != null) {
            headwaterModule.emit(inData,
                                 dataFlow);
        }
    }
    /**
     * Create the data flows for this object.
     */
    private void initiateDataFlows()
    {
        List<DataRequest> dataRequestBuilder = Lists.newArrayList();
        String headwaterInstance = "hw"+System.nanoTime();
        ModuleURN headwaterUrn = moduleManager.createModule(HeadwaterModuleFactory.PROVIDER_URN,
                                                            headwaterInstance);
        headwaterModule = HeadwaterModule.getInstance(headwaterInstance);
        dataRequestBuilder.add(new DataRequest(headwaterUrn));
        dataRequestBuilder.add(new DataRequest(TensorFlowConverterModuleFactory.INSTANCE_URN));
        dataRequestBuilder.add(new DataRequest(TensorFlowModelModuleFactory.INSTANCE_URN,
                                               tensorFlowRunner));
        DataRequest[] finalRequest = dataRequestBuilder.toArray(new DataRequest[dataRequestBuilder.size()]);
        dataFlow = moduleManager.createDataFlow(finalRequest);
        SLF4JLoggerProxy.info(this,
                              "Initiated iRouter data flow: {}",
                              dataFlow);
    }
    /**
     * Stop the data flows for this object.
     */
    private void cancelDataFlows()
    {
        if(dataFlow != null) {
            moduleManager.cancel(dataFlow);
            dataFlow = null;
        }
        if(headwaterUrn != null) {
            moduleManager.stop(headwaterUrn);
            moduleManager.deleteModule(headwaterUrn);
            headwaterUrn = null;
        }
    }
    /**
     * provides access to the module framework
     */
    @Autowired
    private ModuleManager moduleManager;
    /**
     * Tensor Flow runner used in this data flow, can be set from configuration to a meaningful runner
     */
    private TensorFlowRunner tensorFlowRunner = new NoOpTensorFlowRunner();
    /**
     * module used to initiate the TensorFlow data flow
     */
    private HeadwaterModule headwaterModule;
    /**
     * module URN used to initiate the TensorFlow data flow
     */
    private ModuleURN headwaterUrn;
    /**
     * data flow for TensorFlow operations
     */
    private DataFlowID dataFlow;
}
