package org.marketcetera.dataflow.client.rpc;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;

import javax.annotation.concurrent.GuardedBy;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.marketcetera.core.Util;
import org.marketcetera.core.Version;
import org.marketcetera.core.VersionInfo;
import org.marketcetera.dataflow.client.DataFlowClient;
import org.marketcetera.dataflow.client.DataReceiver;
import org.marketcetera.dataflow.client.XmlValue;
import org.marketcetera.dataflow.rpc.DataFlowClientRpc;
import org.marketcetera.dataflow.rpc.DataFlowClientServiceRpcGrpc;
import org.marketcetera.dataflow.rpc.DataFlowClientServiceRpcGrpc.DataFlowClientServiceRpcBlockingStub;
import org.marketcetera.dataflow.rpc.DataFlowClientServiceRpcGrpc.DataFlowClientServiceRpcStub;
import org.marketcetera.module.ModuleInfo;
import org.marketcetera.module.ModuleURN;
import org.marketcetera.rpc.base.BaseRpc;
import org.marketcetera.rpc.client.AbstractRpcClient;
import org.marketcetera.util.ws.ContextClassProvider;
import org.marketcetera.util.ws.tags.AppId;

import com.google.common.collect.Lists;

import io.grpc.Channel;
import io.grpc.stub.StreamObserver;

/* $License$ */

/**
 * Provides an RPC {@link DataFlowClient} interface.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class DataFlowRpcClient
        extends AbstractRpcClient<DataFlowClientServiceRpcBlockingStub,DataFlowClientServiceRpcStub,DataFlowRpcClientParameters>
        implements DataFlowClient
{
    /**
     * Create a new SERpcClient instance.
     *
     * @param inParameters an <code>SERpcClientParameters</code> value
     */
    DataFlowRpcClient(DataFlowRpcClientParameters inParameters)
    {
        super(inParameters);
        contextClassProvider = inParameters.getContextClassProvider();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.rpc.client.AbstractRpcClient#start()
     */
    @Override
    public void start()
            throws Exception
    {
        synchronized(contextLock) {
            context = JAXBContext.newInstance(contextClassProvider==null?new Class<?>[0]:contextClassProvider.getContextClasses());
            marshaller = context.createMarshaller();
            unmarshaller = context.createUnmarshaller();
        }
        super.start();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.SEClient.SEClient#getProviders()
     */
    @Override
    public List<ModuleURN> getProviders()
    {
        return executeCall(new Callable<List<ModuleURN>>(){
            @Override
            public List<ModuleURN> call()
                    throws Exception
            {
                DataFlowClientRpc.ProvidersResponse response = getBlockingStub().getProviders(DataFlowClientRpc.ProvidersRequest.newBuilder().setSessionId(getSessionId().getValue()).build());
                List<ModuleURN> providers = Lists.newArrayList();
                for(DataFlowClientRpc.ModuleURN provider : response.getProviderList()) {
                    providers.add(new ModuleURN(provider.getValue()));
                }
                return providers;
            }
        });
    }
    /* (non-Javadoc)
     * @see org.marketcetera.SEClient.SEClient#getInstances(org.marketcetera.module.ModuleURN)
     */
    @Override
    public List<ModuleURN> getInstances(ModuleURN inProviderURN)
    {
        return executeCall(new Callable<List<ModuleURN>>(){
            @Override
            public List<ModuleURN> call()
                    throws Exception
            {
                DataFlowClientRpc.InstancesResponse response = getBlockingStub().getInstances(DataFlowClientRpc.InstancesRequest.newBuilder().setSessionId(getSessionId().getValue())
                                                                                        .setProvider(DataFlowClientRpc.ModuleURN.newBuilder().setValue(inProviderURN.getValue())).build());
                List<ModuleURN> instances = Lists.newArrayList();
                for(DataFlowClientRpc.ModuleURN instance : response.getInstanceList()) {
                    instances.add(new ModuleURN(instance.getValue()));
                }
                return instances;
            }
        });
    }
    /* (non-Javadoc)
     * @see org.marketcetera.SEClient.SEClient#getModuleInfo(org.marketcetera.module.ModuleURN)
     */
    @Override
    public ModuleInfo getModuleInfo(ModuleURN inURN)
    {
        return executeCall(new Callable<ModuleInfo>(){
            @Override
            public ModuleInfo call()
                    throws Exception
            {
                DataFlowClientRpc.ModuleInfoResponse response = getBlockingStub().getModuleInfo(DataFlowClientRpc.ModuleInfoRequest.newBuilder().setSessionId(getSessionId().getValue())
                                                                                          .setInstance(DataFlowClientRpc.ModuleURN.newBuilder().setValue(inURN.getValue())).build());
                ModuleInfo info = null;
                if(response.hasInfo()) {
                    info = unmarshal(response.getInfo().getPayload());
                }
                return info;
            }
        });
    }
    /* (non-Javadoc)
     * @see org.marketcetera.SEClient.SEClient#start(org.marketcetera.module.ModuleURN)
     */
    @Override
    public void start(final ModuleURN inURN)
    {
        executeCall(new Callable<Void>(){
            @Override
            public Void call()
                    throws Exception
            {
                getBlockingStub().start(DataFlowClientRpc.StartRequest.newBuilder().setSessionId(getSessionId().getValue())
                                        .setInstance(DataFlowClientRpc.ModuleURN.newBuilder().setValue(inURN.getValue())).build());
                return null;
            }
        });
    }
    /* (non-Javadoc)
     * @see org.marketcetera.SEClient.SEClient#stop(org.marketcetera.module.ModuleURN)
     */
    @Override
    public void stop(ModuleURN inURN)
    {
        executeCall(new Callable<Void>(){
            @Override
            public Void call()
                    throws Exception
            {
                getBlockingStub().stop(DataFlowClientRpc.StopRequest.newBuilder().setSessionId(getSessionId().getValue())
                                       .setInstance(DataFlowClientRpc.ModuleURN.newBuilder().setValue(inURN.getValue())).build());
                return null;
            }
        });
    }
    /* (non-Javadoc)
     * @see org.marketcetera.SEClient.SEClient#delete(org.marketcetera.module.ModuleURN)
     */
    @Override
    public void delete(ModuleURN inURN)
    {
        executeCall(new Callable<Void>(){
            @Override
            public Void call()
                    throws Exception
            {
                getBlockingStub().delete(DataFlowClientRpc.DeleteRequest.newBuilder().setSessionId(getSessionId().getValue())
                                         .setInstance(DataFlowClientRpc.ModuleURN.newBuilder().setValue(inURN.getValue())).build());
                return null;
            }
        });
    }
    /* (non-Javadoc)
     * @see org.marketcetera.SEClient.SEClient#sendData(java.lang.Object)
     */
    @Override
    public void sendData(Object inData)
    {
        executeCall(new Callable<Void>(){
            @Override
            public Void call()
                    throws Exception
            {
                // note that inData must be JAXB marshallable
                getBlockingStub().sendData(DataFlowClientRpc.SendDataRequest.newBuilder().setSessionId(getSessionId().getValue())
                                           .setPayload(marshal(new XmlValue(inData))).build());
                return null;
            }
        });
    }
    /* (non-Javadoc)
     * @see org.marketcetera.SEClient.SEClient#addDataReceiver(org.marketcetera.SEClient.DataReceiver)
     */
    @Override
    public void addDataReceiver(DataReceiver inReceiver)
    {
        if(inReceiver == null) {
            throw new NullPointerException();
        }
        synchronized (receivers) {
            receivers.addFirst(inReceiver);
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.SEClient.SEClient#removeDataReciever(org.marketcetera.SEClient.DataReceiver)
     */
    @Override
    public void removeDataReceiver(DataReceiver inReceiver)
    {
        if(inReceiver == null) {
            throw new NullPointerException();
        }
        synchronized (receivers) {
            receivers.removeFirstOccurrence(inReceiver);
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.rpc.client.AbstractRpcClient#getBlockingStub(io.grpc.Channel)
     */
    @Override
    protected DataFlowClientServiceRpcBlockingStub getBlockingStub(Channel inChannel)
    {
        return DataFlowClientServiceRpcGrpc.newBlockingStub(inChannel);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.rpc.client.AbstractRpcClient#getAsyncStub(io.grpc.Channel)
     */
    @Override
    protected DataFlowClientServiceRpcStub getAsyncStub(Channel inChannel)
    {
        return DataFlowClientServiceRpcGrpc.newStub(inChannel);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.rpc.client.AbstractRpcClient#executeLogin(org.marketcetera.rpc.base.BaseRpc.LoginRequest)
     */
    @Override
    protected BaseRpc.LoginResponse executeLogin(BaseRpc.LoginRequest inRequest)
    {
        return getBlockingStub().login(inRequest);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.rpc.client.AbstractRpcClient#executeLogout(org.marketcetera.rpc.base.BaseRpc.LogoutRequest)
     */
    @Override
    protected BaseRpc.LogoutResponse executeLogout(BaseRpc.LogoutRequest inRequest)
    {
        return getBlockingStub().logout(inRequest);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.rpc.client.AbstractRpcClient#executeHeartbeat(org.marketcetera.rpc.base.BaseRpc.HeartbeatRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    protected void executeHeartbeat(BaseRpc.HeartbeatRequest inRequest,
                                    StreamObserver<BaseRpc.HeartbeatResponse> inObserver)
    {
        getAsyncStub().heartbeat(inRequest,
                                 inObserver);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.rpc.client.AbstractRpcClient#getAppId()
     */
    @Override
    protected AppId getAppId()
    {
        return APP_ID;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.rpc.client.AbstractRpcClient#getVersionInfo()
     */
    @Override
    protected VersionInfo getVersionInfo()
    {
        return APP_ID_VERSION;
    }
    /**
     * Marshals the given object to an XML stream.
     *
     * @param inObject an <code>Object</code> value
     * @return a <code>String</code> value
     * @throws JAXBException if an error occurs marshalling the data
     */
    private String marshal(Object inObject)
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
    private <Clazz> Clazz unmarshal(String inData)
            throws JAXBException
    {
        synchronized(contextLock) {
            return (Clazz)unmarshaller.unmarshal(new StringReader(inData));
        }
    }
    /**
     * guards access to JAXB context objects
     */
    private final Object contextLock = new Object();
    /**
     * context used to serialize and unserialize messages as necessary
     */
    @GuardedBy("contextLock")
    private JAXBContext context;
    /**
     * marshals messages
     */
    @GuardedBy("contextLock")
    private Marshaller marshaller;
    /**
     * unmarshals messages
     */
    @GuardedBy("contextLock")
    private Unmarshaller unmarshaller;
    /**
     * receivers of remove data
     */
    private final Deque<DataReceiver> receivers = new LinkedList<DataReceiver>();
    /**
     * provides context classes for marshalling/unmarshalling, may be <code>null</code>
     */
    private ContextClassProvider contextClassProvider;
    /**
     * The client's application ID: the application name.
     */
    private static final String APP_ID_NAME = DataFlowRpcClient.class.getSimpleName();
    /**
     * The client's application ID: the version.
     */
    private static final VersionInfo APP_ID_VERSION = new VersionInfo(Version.pomversion);
    /**
     * The client's application ID: the ID.
     */
    private static final AppId APP_ID = Util.getAppId(APP_ID_NAME,APP_ID_VERSION.getVersionInfo());
}
