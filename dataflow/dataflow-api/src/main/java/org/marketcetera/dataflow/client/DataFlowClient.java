package org.marketcetera.dataflow.client;

import java.util.List;

import org.marketcetera.core.BaseClient;
import org.marketcetera.module.DataFlowID;
import org.marketcetera.module.DataFlowInfo;
import org.marketcetera.module.DataRequest;
import org.marketcetera.module.ModuleInfo;
import org.marketcetera.module.ModuleURN;
import org.marketcetera.persist.CollectionPageResponse;
import org.marketcetera.persist.PageRequest;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Provides access to data flow services.
 *
 * @author anshul@marketcetera.com
 * @author colin@marketcetera.com
 * @version $Id: SEClient.java 17242 2016-09-02 16:46:48Z colin $
 * @since 2.0.0
 */
@ClassVersion("$Id: SEClient.java 17242 2016-09-02 16:46:48Z colin $")
public interface DataFlowClient
        extends BaseClient,DataPublisher
{
    /**
     * Returns the list of providers available.
     * 
     * <p>If no providers are available this list is empty.
     * 
     * @return the list of providers available.
     */
    List<ModuleURN> getProviders();
    /**
     * Returns the list of module instances available.
     *
     * <p>If no instances are found, an empty list is returned.
     *
     * @param inProviderURN the URN of the provider whose instances should be returned. If null, all available instances are returned.
     * @return the list of module instances available.
     */
    List<ModuleURN> getInstances(ModuleURN inProviderURN);
    /**
     * Returns the module information for the module instance with the supplied URN.
     *
     * @param inURN the module instance URN. Cannot be null.
     * @return the module info.
     */
    ModuleInfo getModuleInfo(ModuleURN inURN);
    /**
     * Starts the module instance having the supplied URN.
     * 
     * @param inURN the URN of the module that needs to be started. Cannot be null.
     */
    void startModule(ModuleURN inURN);
    /**
     * Stops the module instance having the supplied URN.
     * 
     * @param inURN the URN of the module that needs to be stopped. Cannot be null.
     */
    void stopModule(ModuleURN inURN);
    /**
     * Deletes the module instance having the supplied URN.
     * 
     * @param inURN the URN of the module that needs to be deleted. Cannot be null.
     */
    void deleteModule(ModuleURN inURN);
    /**
     * Create the module instance having the supplied URN.
     * 
     * @param inURN a <code>ModuleURN</code> value
     * @param inParameters an <code>Object[]</code> value
     * @return a <code>ModuleURN</code> value
     */
    ModuleURN createModule(ModuleURN inURN,
                           Object...inParameters);
    /**
     * Create the data flow described by the given data requests.
     *
     * @param inDataRequest a <code>List&lt;DataRequest&gt;</code> value
     * @param inAppendDataSink a <code>boolean</code> value
     * @return a <code>DataFlowID</code> value
     */
    DataFlowID createDataFlow(List<DataRequest> inDataRequest,
                              boolean inAppendDataSink);
    /**
     * Create the data flow described by the given data requests.
     *
     * @param inDataRequest a <code>List&lt;DataRequest&gt;</code> value
     * @return a <code>DataFlowID</code> value
     */
    DataFlowID createDataFlow(List<DataRequest> inDataRequest);
    /**
     * Cancel the data flow with the given id.
     *
     * @param inDataFlowId a <code>DataFlowID</code> value
     */
    void cancelDataFlow(DataFlowID inDataFlowId);
    /**
     * Get the data flow info for the given data flow id.
     *
     * @param inDataFlowId a <code>DataFlowID</code> value
     * @return a <code>DataFlowInfo</code> value
     */
    DataFlowInfo getDataFlowInfo(DataFlowID inDataFlowId);
    /**
     * Get active data flows.
     *
     * @return a <code>List&lt;DataFlowID&gt;</code> value
     */
    List<DataFlowID> getDataFlows();
    /**
     * Get active data flows.
     *
     * @param inPageRequest a <code>PageRequest</code> value
     * @return a <code>CollectionPageResponse&lt;DataFlowID&gt;</code> value
     */
    CollectionPageResponse<DataFlowID> getDataFlows(PageRequest inPageRequest);
    /**
     * Get inactive data flows.
     *
     * @param inPageRequest a <code>PageRequest</code> value
     * @return a <code>CollectionPageResponse&lt;DataFlowInfo&gt;</code> value
     */
    CollectionPageResponse<DataFlowInfo> getDataFlowHistory(PageRequest inPageRequest);
    /**
     * Get inactive data flows.
     *
     * @return a <code>List&lt;DataFlowInfo&gt;</code> value
     */
    List<DataFlowInfo> getDataFlowHistory();
    /**
     * Sends the given object to the server where registered listeners will receive it.
     *
     * @param inData an <code>Object</code> value
     */
    void sendData(Object inData);
    // TODO deploy JAR
    // TODO undeploy JAR
}
