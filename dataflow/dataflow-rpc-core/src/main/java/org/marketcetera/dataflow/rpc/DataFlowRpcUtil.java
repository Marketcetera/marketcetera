package org.marketcetera.dataflow.rpc;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Date;
import java.util.List;

import javax.annotation.concurrent.GuardedBy;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.lang.StringUtils;
import org.marketcetera.module.DataCoupling;
import org.marketcetera.module.DataFlowExceptionHandler;
import org.marketcetera.module.DataFlowID;
import org.marketcetera.module.DataFlowInfo;
import org.marketcetera.module.DataFlowStep;
import org.marketcetera.module.DataRequest;
import org.marketcetera.module.ModuleInfo;
import org.marketcetera.module.ModuleURN;
import org.marketcetera.module.StringDataRequest;

import com.google.common.collect.Lists;
import com.google.protobuf.util.Timestamps;

/* $License$ */

/**
 * Provides data flow RPC utility methods.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public abstract class DataFlowRpcUtil
{
    /**
     * Get the RPC module URN from the given module URN.
     *
     * @param inModuleURN a <code>ModuleURN</code> value
     * @return a <code>DataFLowClientRpc.ModuleURN</code> value
     */
    public static DataFlowRpc.ModuleURN getRpcModuleUrn(ModuleURN inModuleURN)
    {
        DataFlowRpc.ModuleURN.Builder builder = DataFlowRpc.ModuleURN.newBuilder();
        builder.setValue(inModuleURN.getValue());
        return builder.build();
    }
    /**
     * Get the module URN from the given RPC value.
     *
     * @param inRpcUrn a <code>DataFlowClientRpc.ModuleURN</code> value
     * @return a <code>ModuleURN</code> value
     */
    public static ModuleURN getModuleUrn(DataFlowRpc.ModuleURN inRpcUrn)
    {
        return new ModuleURN(inRpcUrn.getValue());
    }
    /**
     * Get the RPC module info from the given value.
     *
     * @param inInfo a <code>ModuleInfo</code> value
     * @return a <code>DataFlowRpc.ModuleInfo</code> value
     */
    public static DataFlowRpc.ModuleInfo getRpcModuleInfo(ModuleInfo inInfo)
    {
        DataFlowRpc.ModuleInfo.Builder builder = DataFlowRpc.ModuleInfo.newBuilder();
        try {
            builder.setPayload(marshall(inInfo));
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
        return builder.build();
    }
    /**
     * Get the module info from the given RPC value.
     *
     * @param inInfo a <code>DataFlowRpc.ModuleInfo</code> value
     * @return a <code>ModuleInfo</code> value
     */
    public static ModuleInfo getModuleInfo(DataFlowRpc.ModuleInfo inInfo)
    {
        try {
            return unmarshall(inInfo.getPayload());
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * Get the RPC value of the given parameter.
     *
     * @param inParam an <code>Object</code> value
     * @return a <code>String</code> value
     */
    public static String getRpcParameter(Object inParam)
    {
        try {
            return marshall(new XmlValue(inParam));
        } catch (JAXBException e) {
            return String.valueOf(inParam);
        }
    }
    /**
     * Get the object encoded in the given RPC parameter.
     *
     * @param inParam a <code>String</code>value
     * @return an <code>Object</code> value
     */
    public static Object getParameter(String inParam)
    {
        try {
            XmlValue xmlValue = unmarshall(inParam);
            return xmlValue.getValue();
        } catch (JAXBException e) {
            return inParam;
        }
    }
    /**
     * Get the data flow ID from the given RPC value.
     *
     * @param inDataFlowId a <code>String</code> value
     * @return a <code>DataFlowID</code> value
     */
    public static DataFlowID getDataFlowId(String inDataFlowId)
    {
        return new DataFlowID(inDataFlowId);
    }
    /**
     * Get the RPC data flow ID from the given value.
     *
     * @param inDataFlowId a <code>DataFlowID</code> value
     * @return a <code>String</code> value
     */
    public static String getRpcDataFlowId(DataFlowID inDataFlowId)
    {
        return inDataFlowId.getValue();
    }
    /**
     * Get the data request from the given RPC value.
     *
     * @param inDataRequest a <code>DataFlowRpc.DataRequest</code> value
     * @return a <code>DataRequest</code>value
     */
    public static DataRequest getDataRequest(DataFlowRpc.DataRequest inDataRequest)
    {
        DataCoupling dataCoupling = getDataCoupling(inDataRequest.getDataCoupling());
        String exceptionHandlerClassName = StringUtils.trimToNull(inDataRequest.getDataFlowExceptionHandler());
        DataFlowExceptionHandler exceptionHandler = null;
        if(exceptionHandlerClassName != null) {
            try {
                exceptionHandler = (DataFlowExceptionHandler)Class.forName(exceptionHandlerClassName).newInstance();
            } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        ModuleURN requestUrn = getModuleUrn(inDataRequest.getRequestUrn());
        String rawData = StringUtils.trimToNull(inDataRequest.getData());
        Object data = null;
        if(rawData != null) {
            data = getParameter(rawData);
        }
        return new DataRequest(requestUrn,
                               dataCoupling,
                               exceptionHandler,
                               data);
    }
    /**
     * Set the given object on the given response.
     *
     * @param inObject an <code>Object</code> value
     * @param inBuilder a <code>DataFlowRpc.DataReceiverResponse.Builder</code> value
     */
    public static void setData(Object inObject,
                               DataFlowRpc.DataReceiverResponse.Builder inBuilder)
    {
        inBuilder.setData(getRpcParameter(inObject));
    }
    /**
     * Get the data flow coupling from the given RPC value.
     *
     * @param inDataCoupling a <code>DataFlowRpc.DataCoupling</code> value
     * @return a <code>DataCoupling</code> value
     */
    public static DataCoupling getDataCoupling(DataFlowRpc.DataCoupling inDataCoupling)
    {
        switch(inDataCoupling) {
            case ASYNC_DATA_COUPLING:
                return DataCoupling.ASYNC;
            case SYNC_DATA_COUPLING:
            case UNRECOGNIZED:
            default:
                return DataCoupling.SYNC;
        }
    }
    /**
     * Get the RPC data request from the given data request value.
     *
     * @param inDataRequest a <code>DataRequest</code> value
     * @return a <code>DataFlowRpc.DataRequest</code> value
     */
    public static DataFlowRpc.DataRequest getRpcDataRequest(DataRequest inDataRequest)
    {
        DataFlowRpc.DataRequest.Builder requestBuilder = DataFlowRpc.DataRequest.newBuilder();
        if(inDataRequest.getData() != null) {
            requestBuilder.setData(getRpcParameter(inDataRequest.getData()));
        }
        requestBuilder.setDataCoupling(getRpcDataCoupling(inDataRequest.getCoupling()));
        if(inDataRequest.getExceptionHandler() != null) {
            requestBuilder.setDataFlowExceptionHandler(inDataRequest.getExceptionHandler().getClass().getName());
        }
        requestBuilder.setRequestUrn(getRpcModuleUrn(inDataRequest.getRequestURN()));
        return requestBuilder.build();
    }
    /**
     * Get the RPC data request from the given data request value.
     *
     * @param inDataRequest a <code>StringDataRequest</code> value
     * @return a <code>DataFlowRpc.DataRequest</code> value
     */
    public static DataFlowRpc.DataRequest getRpcDataRequest(StringDataRequest inDataRequest)
    {
        DataFlowRpc.DataRequest.Builder requestBuilder = DataFlowRpc.DataRequest.newBuilder();
        if(inDataRequest.getData() != null) {
            requestBuilder.setData(getRpcParameter(inDataRequest.getData()));
        }
        requestBuilder.setDataCoupling(getRpcDataCoupling(inDataRequest.getCoupling()));
        requestBuilder.setRequestUrn(getRpcModuleUrn(inDataRequest.getRequestURN()));
        return requestBuilder.build();
    }
    /**
     * Get the RPC value from the given data coupling.
     *
     * @param inCoupling a <code>DataCoupling</code> value
     * @return a <code>DataFlowRpc.DataCoupling</code> value
     */
    public static DataFlowRpc.DataCoupling getRpcDataCoupling(DataCoupling inCoupling)
    {
        switch(inCoupling) {
            case ASYNC:
                return DataFlowRpc.DataCoupling.ASYNC_DATA_COUPLING;
            case SYNC:
                return DataFlowRpc.DataCoupling.SYNC_DATA_COUPLING;
            default:
                throw new UnsupportedOperationException(inCoupling.name());
        }
    }
    /**
     * Get the data flow info from the given RPC value.
     *
     * @param inDataFlowInfo a <code>DataFlowRpc.DataFlowInfo</code> value
     * @return a <code>DataFlowInfo</code> value
     */
    public static DataFlowInfo getDataFlowInfo(DataFlowRpc.DataFlowInfo inDataFlowInfo)
    {
        Date created = null;
        if(inDataFlowInfo.hasCreated()) {
            created = new Date(Timestamps.toMillis(inDataFlowInfo.getCreated()));
        }
        DataFlowID dataFlowId = getDataFlowId(inDataFlowInfo.getFlowId());
        List<DataFlowStep> dataFlowStepBuilder = Lists.newArrayList();
        for(DataFlowRpc.DataFlowStep rpcDataFlowStep : inDataFlowInfo.getFlowStepsList()) {
            dataFlowStepBuilder.add(getDataFlowStep(rpcDataFlowStep));
        }
        ModuleURN requesterUrn = null;
        if(inDataFlowInfo.hasRequesterUrn()) {
            requesterUrn = getModuleUrn(inDataFlowInfo.getRequesterUrn());
        }
        Date stopped = null;
        if(inDataFlowInfo.hasStopped()) {
            stopped = new Date(Timestamps.toMillis(inDataFlowInfo.getStopped()));
        }
        ModuleURN stopperUrn = null;
        if(inDataFlowInfo.hasStopperUrn()) {
            stopperUrn = getModuleUrn(inDataFlowInfo.getStopperUrn());
        }
        return new DataFlowInfo(dataFlowStepBuilder.toArray(new DataFlowStep[dataFlowStepBuilder.size()]),
                                dataFlowId,
                                requesterUrn,
                                stopperUrn,
                                created,
                                stopped);
    }
    /**
     * Get the RPC data flow info from the given value.
     *
     * @param inDataFlowInfo a <code>DataFlowInfo</code> value
     * @return a <code>DataFlowRpc.DataFlowInfo</code> value
     */
    public static DataFlowRpc.DataFlowInfo getRpcDataFlowInfo(DataFlowInfo inDataFlowInfo)
    {
        DataFlowRpc.DataFlowInfo.Builder builder = DataFlowRpc.DataFlowInfo.newBuilder();
        if(inDataFlowInfo.getCreated() != null) {
            builder.setCreated(Timestamps.fromMillis(inDataFlowInfo.getCreated().getTime()));
        }
        if(inDataFlowInfo.getFlowID() != null) {
            builder.setFlowId(getRpcDataFlowId(inDataFlowInfo.getFlowID()));
        }
        if(inDataFlowInfo.getFlowSteps() != null) {
            for(DataFlowStep dataFlowStep : inDataFlowInfo.getFlowSteps()) {
                builder.addFlowSteps(getRpcDataFlowStep(dataFlowStep));
            }
        }
        if(inDataFlowInfo.getRequesterURN() != null) {
            builder.setRequesterUrn(getRpcModuleUrn(inDataFlowInfo.getRequesterURN()));
        }
        if(inDataFlowInfo.getStopped() != null) {
            builder.setStopped(Timestamps.fromMillis(inDataFlowInfo.getStopped().getTime()));
        }
        if(inDataFlowInfo.getStopperURN() != null) {
            builder.setStopperUrn(getRpcModuleUrn(inDataFlowInfo.getStopperURN()));
        }
        return builder.build();
    }
    /**
     * Get the RPC data flow step from the given value.
     *
     * @param inDataFlowStep a <code>DataFlowStep</code> value
     * @return a <code>DataFlowRpc.DataFlowStep</code> value
     */
    public static DataFlowRpc.DataFlowStep getRpcDataFlowStep(DataFlowStep inDataFlowStep)
    {
        DataFlowRpc.DataFlowStep.Builder builder = DataFlowRpc.DataFlowStep.newBuilder();
        if(inDataFlowStep.getRequest() != null) {
            builder.setDataRequest(getRpcDataRequest(inDataFlowStep.getRequest()));
        }
        builder.setEmitter(inDataFlowStep.isEmitter());
        if(inDataFlowStep.getLastEmitError() != null) {
            builder.setLastEmitError(inDataFlowStep.getLastEmitError());
        }
        if(inDataFlowStep.getLastReceiveError() != null) {
            builder.setLastReceiveError(inDataFlowStep.getLastReceiveError());
        }
        if(inDataFlowStep.getModuleURN() != null) {
            builder.setModuleUrn(getRpcModuleUrn(inDataFlowStep.getModuleURN()));
        }
        builder.setNumEmitErrors(inDataFlowStep.getNumEmitErrors());
        builder.setNumEmitted(inDataFlowStep.getNumEmitted());
        builder.setNumReceived(inDataFlowStep.getNumReceived());
        builder.setNumReceiveErrors(inDataFlowStep.getNumReceiveErrors());
        builder.setReceiver(inDataFlowStep.isReceiver());
        return builder.build();
    }
    /**
     * Get the data step from the given RPC value.
     *
     * @param inRpcDataFlowStep a <code>DataFlowRpc.DataFlowStep</code> value
     * @return a <code>DataFlowStep</code> value
     */
    public static DataFlowStep getDataFlowStep(DataFlowRpc.DataFlowStep inRpcDataFlowStep)
    {
        DataRequest dataRequest = getDataRequest(inRpcDataFlowStep.getDataRequest());
        boolean emitter = inRpcDataFlowStep.getEmitter();
        String lastEmitError = inRpcDataFlowStep.getLastEmitError();
        String lastReceiveError = inRpcDataFlowStep.getLastReceiveError();
        ModuleURN moduleUrn = getModuleUrn(inRpcDataFlowStep.getModuleUrn());
        long numEmitted = inRpcDataFlowStep.getNumEmitted();
        long numEmitErrors = inRpcDataFlowStep.getNumEmitErrors();
        long numReceived = inRpcDataFlowStep.getNumReceived();
        long numReceiveErrors = inRpcDataFlowStep.getNumReceiveErrors();
        boolean receiver = inRpcDataFlowStep.getReceiver();
        StringDataRequest stringDataRequest = new StringDataRequest(dataRequest.getRequestURN(),
                                                                    dataRequest.getCoupling(),
                                                                    String.valueOf(dataRequest.getData()));
        return new DataFlowStep(stringDataRequest,
                                moduleUrn,
                                emitter,
                                receiver,
                                numEmitted,
                                numReceived,
                                numEmitErrors,
                                numReceiveErrors,
                                lastEmitError,
                                lastReceiveError);
    }
    /**
     * Marshals the given object to an XML stream.
     *
     * @param inObject an <code>Object</code> value
     * @return a <code>String</code> value
     * @throws JAXBException if an error occurs marshalling the data
     */
    private static String marshall(Object inObject)
            throws JAXBException
    {
        StringWriter output = new StringWriter();
        synchronized(contextLock) {
            marshaller.marshal(inObject,
                               output);
        }
        return output.toString();
    }
    /**
     * Unmarshals an object from the given XML stream.
     *
     * @param inData a <code>String</code> value
     * @return a <code>Clazz</code> value
     * @throws JAXBException if an error occurs unmarshalling the data
     */
    @SuppressWarnings("unchecked")
    private static <Clazz> Clazz unmarshall(String inData)
            throws JAXBException
    {
        synchronized(contextLock) {
            return (Clazz)unmarshaller.unmarshal(new StringReader(inData));
        }
    }
    /**
     * guards access to JAXB context objects
     */
    private static final Object contextLock = new Object();
    /**
     * context used to serialize and unserialize messages as necessary
     */
    @GuardedBy("contextLock")
    private static JAXBContext context;
    /**
     * marshals messages
     */
    @GuardedBy("contextLock")
    private static Marshaller marshaller;
    /**
     * unmarshals messages
     */
    @GuardedBy("contextLock")
    private static Unmarshaller unmarshaller;
    /**
     * Initialize static members
     */
    static {
        try {
            synchronized(contextLock) {
                context = JAXBContext.newInstance(new DataFlowContextClassProvider().getContextClasses());
                marshaller = context.createMarshaller();
                unmarshaller = context.createUnmarshaller();
            }
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }
}
