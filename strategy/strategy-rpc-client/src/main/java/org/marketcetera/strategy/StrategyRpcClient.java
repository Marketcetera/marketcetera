 //
// this file is automatically generated
//
package org.marketcetera.strategy;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.Callable;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.joda.time.Period;
import org.marketcetera.admin.User;
import org.marketcetera.admin.UserFactory;
import org.marketcetera.admin.rpc.AdminRpcUtil;
import org.marketcetera.core.ApplicationVersion;
import org.marketcetera.core.PlatformServices;
import org.marketcetera.core.Preserve;
import org.marketcetera.core.Util;
import org.marketcetera.core.VersionInfo;
import org.marketcetera.core.notifications.INotification.Severity;
import org.marketcetera.core.time.TimeFactoryImpl;
import org.marketcetera.persist.CollectionPageResponse;
import org.marketcetera.persist.PageRequest;
import org.marketcetera.rpc.base.BaseRpc;
import org.marketcetera.rpc.base.BaseRpcUtil;
import org.marketcetera.rpc.base.BaseRpcUtil.AbstractClientListenerProxy;
import org.marketcetera.rpc.client.AbstractRpcClient;
import org.marketcetera.rpc.paging.PagingRpcUtil;
import org.marketcetera.strategy.events.StrategyEvent;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.ws.tags.AppId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Lists;
import com.google.protobuf.ByteString;

import io.grpc.stub.StreamObserver;

/* $License$ */

/**
 * Provides an RPC Client for StrategyRpc services.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Preserve
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class StrategyRpcClient
        extends AbstractRpcClient<StrategyRpcServiceGrpc.StrategyRpcServiceBlockingStub,StrategyRpcServiceGrpc.StrategyRpcServiceStub,StrategyRpcClientParameters>
        implements StrategyClient
{
    /* (non-Javadoc)
     * @see StrategyClient#findByName(String)
     */
    @Override
    public Optional<? extends StrategyInstance> findByName(String inName)
    {
        return executeCall(new Callable<Optional<? extends StrategyInstance>>() {
            @Override
            public Optional<? extends StrategyInstance> call()
                    throws Exception
            {
                StrategyRpc.FindStrategyInstanceByNameRequest.Builder requestBuilder = StrategyRpc.FindStrategyInstanceByNameRequest.newBuilder();
                requestBuilder.setSessionId(getSessionId().getValue());
                requestBuilder.setName(inName);
                StrategyRpc.FindStrategyInstanceByNameRequest request = requestBuilder.build();
                SLF4JLoggerProxy.trace(StrategyRpcClient.this,"{} sending {}",getSessionId(),request);
                StrategyRpc.FindStrategyInstanceByNameResponse response = getBlockingStub().findByName(request);
                SLF4JLoggerProxy.trace(StrategyRpcClient.this,"{} received {}",getSessionId(),response);
                if(response.hasStrategyInstance()) {
                    return StrategyRpcUtil.getStrategyInstance(response.getStrategyInstance(),
                                                               strategyInstanceFactory,
                                                               userFactory);
                } else {
                    return Optional.empty();
                }
            }}
        );
    }
    /* (non-Javadoc)
     * @see StrategyClient#getStrategyInstances()
     */
    @Override
    public Collection<? extends StrategyInstance> getStrategyInstances()
    {
        return executeCall(new Callable<Collection<? extends StrategyInstance>>() {
            @Override
            public Collection<? extends StrategyInstance> call()
                    throws Exception
            {
                StrategyRpc.ReadStrategyInstancesRequest.Builder requestBuilder = StrategyRpc.ReadStrategyInstancesRequest.newBuilder();
                requestBuilder.setSessionId(getSessionId().getValue());
                StrategyRpc.ReadStrategyInstancesRequest request = requestBuilder.build();
                SLF4JLoggerProxy.trace(StrategyRpcClient.this,"{} sending {}",getSessionId(),request);
                StrategyRpc.ReadStrategyInstancesResponse response = getBlockingStub().getStrategyInstances(request);
                SLF4JLoggerProxy.trace(StrategyRpcClient.this,"{} received {}",getSessionId(),response);
                Collection<StrategyInstance> results = Lists.newArrayList();
                response.getStrategyInstancesList().forEach(rpcStrategyInstance -> StrategyRpcUtil.getStrategyInstance(rpcStrategyInstance,
                                                                                                                       strategyInstanceFactory,
                                                                                                                       userFactory).ifPresent(strategyInstance -> results.add(strategyInstance)));
                return results;
            }}
        );
    }
    /* (non-Javadoc)
     * @see StrategyClient#getStrategyMessages(String,String,PageRequest)
     */
    @Override
    public CollectionPageResponse<? extends StrategyMessage> getStrategyMessages(String inStrategyName,
                                                                                 Severity inSeverity,
                                                                                 PageRequest inPageRequest)
    {
        return executeCall(new Callable<CollectionPageResponse<? extends StrategyMessage>>() {
            @Override
            public CollectionPageResponse<? extends StrategyMessage> call()
                    throws Exception
            {
                StrategyRpc.ReadStrategyMessagesRequest.Builder requestBuilder = StrategyRpc.ReadStrategyMessagesRequest.newBuilder();
                requestBuilder.setSessionId(getSessionId().getValue());
                if(inStrategyName != null) {
                    requestBuilder.setStrategyName(inStrategyName);
                }
                StrategyRpcUtil.getRpcStrategyMessageSeverity(inSeverity).ifPresent(rpcSeverity -> requestBuilder.setSeverity(rpcSeverity));
                requestBuilder.setPageRequest(PagingRpcUtil.buildPageRequest(inPageRequest));
                StrategyRpc.ReadStrategyMessagesRequest request = requestBuilder.build();
                SLF4JLoggerProxy.trace(StrategyRpcClient.this,"{} sending {}",getSessionId(),request);
                StrategyRpc.ReadStrategyMessagesResponse response = getBlockingStub().getStrategyMessages(request);
                SLF4JLoggerProxy.trace(StrategyRpcClient.this,"{} received {}",getSessionId(),response);
                CollectionPageResponse<StrategyMessage> results = new CollectionPageResponse<>();
                response.getStrategyMessagesList().forEach(rpcStrategyMessage -> StrategyRpcUtil.getStrategyMessage(rpcStrategyMessage,
                                                                                                                     strategyMessageFactory,
                                                                                                                     strategyInstanceFactory,
                                                                                                                     userFactory).ifPresent(strategyMessage -> results.getElements().add(strategyMessage)));
                PagingRpcUtil.setPageResponse(inPageRequest,
                                              response.getPageResponse(),
                                              results);
                return results;
            }}
        );
    }
    /* (non-Javadoc)
     * @see StrategyClient#unloadStrategyInstance(String)
     */
    @Override
    public void unloadStrategyInstance(String inStrategyInstanceName)
    {
        executeCall(new Callable<Void>() {
            @Override
            public Void call()
                    throws Exception
            {
                StrategyRpc.UnloadStrategyInstanceRequest.Builder requestBuilder = StrategyRpc.UnloadStrategyInstanceRequest.newBuilder();
                requestBuilder.setSessionId(getSessionId().getValue());
                requestBuilder.setName(inStrategyInstanceName);
                StrategyRpc.UnloadStrategyInstanceRequest request = requestBuilder.build();
                SLF4JLoggerProxy.trace(StrategyRpcClient.this,"{} sending {}",getSessionId(),request);
                StrategyRpc.UnloadStrategyInstanceResponse response = getBlockingStub().unloadStrategyInstance(request);
                SLF4JLoggerProxy.trace(StrategyRpcClient.this,"{} received {}",getSessionId(),response);
                return null;
            }}
        );
    }
    /* (non-Javadoc)
     * @see StrategyClient#loadStrategyInstance(StrategyInstance)
     */
    @Override
    public StrategyStatus loadStrategyInstance(StrategyInstance inStrategyInstance)
    {
        return executeCall(new Callable<StrategyStatus>() {
            @Override
            public StrategyStatus call()
                    throws Exception
            {
                StrategyRpc.LoadStrategyInstanceRequest.Builder requestBuilder = StrategyRpc.LoadStrategyInstanceRequest.newBuilder();
                requestBuilder.setSessionId(getSessionId().getValue());
                StrategyRpcUtil.getRpcStrategyInstance(inStrategyInstance).ifPresent(rpcValue->requestBuilder.setStrategyInstance(rpcValue));
                StrategyRpc.LoadStrategyInstanceRequest request = requestBuilder.build();
                SLF4JLoggerProxy.trace(StrategyRpcClient.this,"{} sending {}",getSessionId(),request);
                StrategyRpc.LoadStrategyInstanceResponse response = getBlockingStub().loadStrategyInstance(request);
                SLF4JLoggerProxy.trace(StrategyRpcClient.this,"{} received {}",getSessionId(),response);
                return StrategyRpcUtil.getStrategyStatus(response.getStatus()).orElse(null);
            }}
        );
    }
    /* (non-Javadoc)
     * @see StrategyClient#startStrategyInstance(String)
     */
    @Override
    public void startStrategyInstance(String inStrategyInstanceName)
    {
        executeCall(new Callable<Void>() {
            @Override
            public Void call()
                    throws Exception
            {
                StrategyRpc.StartStrategyInstanceRequest.Builder requestBuilder = StrategyRpc.StartStrategyInstanceRequest.newBuilder();
                requestBuilder.setSessionId(getSessionId().getValue());
                requestBuilder.setName(inStrategyInstanceName);
                StrategyRpc.StartStrategyInstanceRequest request = requestBuilder.build();
                SLF4JLoggerProxy.trace(StrategyRpcClient.this,"{} sending {}",getSessionId(),request);
                StrategyRpc.StartStrategyInstanceResponse response = getBlockingStub().startStrategyInstance(request);
                SLF4JLoggerProxy.trace(StrategyRpcClient.this,"{} received {}",getSessionId(),response);
                return null;
            }}
        );
    }
    /* (non-Javadoc)
     * @see StrategyClient#stopStrategyInstance(String)
     */
    @Override
    public void stopStrategyInstance(String inStrategyInstanceName)
    {
        executeCall(new Callable<Void>() {
            @Override
            public Void call()
                    throws Exception
            {
                StrategyRpc.StopStrategyInstanceRequest.Builder requestBuilder = StrategyRpc.StopStrategyInstanceRequest.newBuilder();
                requestBuilder.setSessionId(getSessionId().getValue());
                requestBuilder.setName(inStrategyInstanceName);
                StrategyRpc.StopStrategyInstanceRequest request = requestBuilder.build();
                SLF4JLoggerProxy.trace(StrategyRpcClient.this,"{} sending {}",getSessionId(),request);
                StrategyRpc.StopStrategyInstanceResponse response = getBlockingStub().stopStrategyInstance(request);
                SLF4JLoggerProxy.trace(StrategyRpcClient.this,"{} received {}",getSessionId(),response);
                return null;
            }}
        );
    }
    /* (non-Javadoc)
     * @see StrategyClient#uploadFile(FileUploadRequest)
     */
    @Override
    public void uploadFile(FileUploadRequest inRequest)
            throws IOException, NoSuchAlgorithmException
    {
        SLF4JLoggerProxy.trace(this,
                               "Preparing {}",
                               inRequest);
        // TODO this wants to be async, maybe
        FileUploadObserver fileUploadObserver = new FileUploadObserver(inRequest);
        // request observer
        StreamObserver<StrategyRpc.FileUploadRequest> streamObserver = getAsyncStub().uploadFile(fileUploadObserver);
        File uploadFile = new File(inRequest.getFilePath());
        Validate.isTrue(uploadFile.canRead());
        String fileHash = PlatformServices.getFileChecksum(uploadFile);
        StrategyInstance newStrategyInstance = strategyInstanceFactory.create();
        newStrategyInstance.setFilename(uploadFile.getPath());
        newStrategyInstance.setHash(fileHash);
        newStrategyInstance.setName(inRequest.getName());
        newStrategyInstance.setUser(inRequest.getOwner());
        newStrategyInstance.setNonce(inRequest.getNonce());
        // send the strategy instance upload to let them know the file is coming with the proper hash and nonce
        loadStrategyInstance(newStrategyInstance);
        StrategyRpc.FileUploadRequest requestMetadata = StrategyRpc.FileUploadRequest.newBuilder().setMetadata(StrategyTypesRpc.FileUploadMetaData.newBuilder()
            .setName(inRequest.getName())
            .setFilename(inRequest.getFilePath())
            .setHash(fileHash)
            .setNonce(inRequest.getNonce())
            .setOwner(AdminRpcUtil.getRpcUser(inRequest.getOwner()).get())
            .setRequestTimestamp(BaseRpcUtil.getTimestampValue(new Date()).get())
            .build()).build();
        SLF4JLoggerProxy.trace(this,
                               "Submitting {}",
                               requestMetadata);
        long startTimeMillis = System.currentTimeMillis();
        streamObserver.onNext(requestMetadata);
        Path filePath = Paths.get(inRequest.getFilePath());
        long fileSize = Files.size(filePath);
        fileUploadObserver.setFileSize(fileSize);
        // upload file as chunk
        InputStream inputStream = Files.newInputStream(filePath);
        byte[] bytes = new byte[4096];
        int size;
        while((size = inputStream.read(bytes)) > 0) {
            StrategyRpc.FileUploadRequest uploadRequest = StrategyRpc.FileUploadRequest.newBuilder().setFile(StrategyTypesRpc.UploadFile.newBuilder().setContent(ByteString.copyFrom(bytes,0,size)).build()).build();
            streamObserver.onNext(uploadRequest);
            fileUploadObserver.incrementBytesUploaded(size);
        }
        // close the stream
        inputStream.close();
        streamObserver.onCompleted();
        long endTimeMillis = System.currentTimeMillis();
        long elapsedTimeMillis = endTimeMillis - startTimeMillis;
        Period fileUploadPeriod = new Period(elapsedTimeMillis);
        SLF4JLoggerProxy.trace(this,
                               "File upload completed in {}, status: {}",
                               TimeFactoryImpl.periodFormatter.print(fileUploadPeriod),
                               fileUploadObserver.currentStatus);
    }
    /* (non-Javadoc)
     * @see StrategyClient#addStrategyEventListener(StrategyEventListener)
     */
    @Override
    public void addStrategyEventListener(StrategyEventListener inListener)
    {
        // check to see if this listener is already registered
        if(listenerProxies.asMap().containsKey(inListener)) {
            return;
        }
        // make sure that this listener wasn't just whisked out from under us
        final AbstractClientListenerProxy<?,?,?> listener = listenerProxies.getUnchecked(inListener);
        if(listener == null) {
            return;
        }
        executeCall(new Callable<Void>() {
            @Override
            public Void call()
                    throws Exception
            {
                SLF4JLoggerProxy.trace(StrategyRpcClient.this,
                                       "{} adding strategy event listener",
                                       getSessionId());
                StrategyRpc.AddStrategyEventListenerRequest.Builder requestBuilder = StrategyRpc.AddStrategyEventListenerRequest.newBuilder();
                requestBuilder.setSessionId(getSessionId().getValue());
                requestBuilder.setListenerId(listener.getId());
                StrategyRpc.AddStrategyEventListenerRequest addStrategyEventListenerRequest = requestBuilder.build();
                SLF4JLoggerProxy.trace(StrategyRpcClient.this,
                                       "{} sending {}",
                                       getSessionId(),
                                       addStrategyEventListenerRequest);
                getAsyncStub().addStrategyEventListener(addStrategyEventListenerRequest,
                                                        (StrategyEventListenerProxy)listener);
                return null;
            }
        });
    }
    /* (non-Javadoc)
     * @see StrategyClient#removeStrategyEventListener(StrategyEventListener)
     */
    @Override
    public void removeStrategyEventListener(StrategyEventListener inListener)
    {
        final AbstractClientListenerProxy<?,?,?> proxy = listenerProxies.getIfPresent(inListener);
        listenerProxies.invalidate(inListener);
        if(proxy == null) {
            return;
        }
        listenerProxiesById.invalidate(proxy.getId());
        executeCall(new Callable<Void>() {
            @Override
            public Void call()
                    throws Exception
            {
                SLF4JLoggerProxy.trace(StrategyRpcClient.this,
                                       "{} removing report listener",
                                       getSessionId());
                StrategyRpc.RemoveStrategyEventListenerRequest.Builder requestBuilder = StrategyRpc.RemoveStrategyEventListenerRequest.newBuilder();
                requestBuilder.setSessionId(getSessionId().getValue());
                requestBuilder.setListenerId(proxy.getId());
                StrategyRpc.RemoveStrategyEventListenerRequest removeStrategyEventListenerRequest = requestBuilder.build();
                SLF4JLoggerProxy.trace(StrategyRpcClient.this,
                                       "{} sending {}",
                                       getSessionId(),
                                       removeStrategyEventListenerRequest);
                StrategyRpc.RemoveStrategyEventListenerResponse response = getBlockingStub().removeStrategyEventListener(removeStrategyEventListenerRequest);
                SLF4JLoggerProxy.trace(StrategyRpcClient.this,
                                       "{} received {}",
                                       getSessionId(),
                                       response);
                return null;
            }
        });
    }
    /* (non-Javadoc)
     * @see StrategyClient#emitMessage(org.marketcetera.core.notifications.INotification.Severity, java.lang.String)
     */
    @Override
    public void emitMessage(Severity inSeverity,
                            String inMessage)
    {
        if(strategyInstanceHolder == null) {
            throw new UnsupportedOperationException("No strategy instance holder provided");
        }
        executeCall(new Callable<Void>() {
            @Override
            public Void call()
                    throws Exception
            {
                StrategyRpc.CreateStrategyMessageRequest.Builder requestBuilder = StrategyRpc.CreateStrategyMessageRequest.newBuilder();
                requestBuilder.setSessionId(getSessionId().getValue());
                StrategyTypesRpc.StrategyMessage.Builder strategyMessageBuilder = StrategyTypesRpc.StrategyMessage.newBuilder();
                StrategyRpcUtil.getRpcStrategyInstance(strategyInstanceHolder.getStrategyInstance()).ifPresent(rpcStrategyInstance -> strategyMessageBuilder.setStrategyInstance(rpcStrategyInstance));
                StrategyRpcUtil.getRpcStrategyMessageSeverity(inSeverity).ifPresent(rpcSeverity -> strategyMessageBuilder.setSeverity(rpcSeverity));
                String message = StringUtils.trimToNull(inMessage);
                if(message != null) {
                    strategyMessageBuilder.setMessage(message);
                }
                requestBuilder.setStrategyMessage(strategyMessageBuilder.build());
                StrategyRpc.CreateStrategyMessageRequest request = requestBuilder.build();
                SLF4JLoggerProxy.trace(StrategyRpcClient.this,"{} sending {}",getSessionId(),request);
                StrategyRpc.CreateStrategyMessageResponse response = getBlockingStub().createStrategyMessage(request);
                SLF4JLoggerProxy.trace(StrategyRpcClient.this,"{} received {}",getSessionId(),response);
                return null;
            }}
        );
    }
    /* (non-Javadoc)
     * @see StrategyClient#deleteStrategyMessage(long)
     */
    @Override
    public void deleteStrategyMessage(long inStrategyMessageId)
    {
        executeCall(new Callable<Void>() {
            @Override
            public Void call()
                    throws Exception
            {
                StrategyRpc.DeleteStrategyMessageRequest.Builder requestBuilder = StrategyRpc.DeleteStrategyMessageRequest.newBuilder();
                requestBuilder.setSessionId(getSessionId().getValue());
                requestBuilder.setStrategyMessageId(inStrategyMessageId);
                StrategyRpc.DeleteStrategyMessageRequest request = requestBuilder.build();
                SLF4JLoggerProxy.trace(StrategyRpcClient.this,"{} sending {}",getSessionId(),request);
                StrategyRpc.DeleteStrategyMessageResponse response = getBlockingStub().deleteStrategyMessage(request);
                SLF4JLoggerProxy.trace(StrategyRpcClient.this,"{} received {}",getSessionId(),response);
                return null;
            }}
        );
    }
    /* (non-Javadoc)
     * @see StrategyClient#deleteAllStrategyMessages(java.lang.String)
     */
    @Override
    public void deleteAllStrategyMessages(String inStrategyInstanceName)
    {
        executeCall(new Callable<Void>() {
            @Override
            public Void call()
                    throws Exception
            {
                StrategyRpc.DeleteAllStrategyMessagesRequest.Builder requestBuilder = StrategyRpc.DeleteAllStrategyMessagesRequest.newBuilder();
                requestBuilder.setSessionId(getSessionId().getValue());
                requestBuilder.setStrategyInstanceName(inStrategyInstanceName);
                StrategyRpc.DeleteAllStrategyMessagesRequest request = requestBuilder.build();
                SLF4JLoggerProxy.trace(StrategyRpcClient.this,"{} sending {}",getSessionId(),request);
                StrategyRpc.DeleteAllStrategyMessagesResponse response = getBlockingStub().deleteAllStrategyMessages(request);
                SLF4JLoggerProxy.trace(StrategyRpcClient.this,"{} received {}",getSessionId(),response);
                return null;
            }}
        );
    }
    /**
     * Provides an interface between trade message stream listeners and their handlers.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    private class StrategyEventListenerProxy
            extends BaseRpcUtil.AbstractClientListenerProxy<StrategyRpc.StrategyEventListenerResponse,StrategyEvent,StrategyEventListener>
    {
        /* (non-Javadoc)
         * @see org.marketcetera.trade.rpc.StrategyRpcClient.AbstractListenerProxy#translateMessage(java.lang.Object)
         */
        @Override
        protected StrategyEvent translateMessage(StrategyRpc.StrategyEventListenerResponse inResponse)
        {
            return StrategyRpcUtil.getStrategyEvent(inResponse,
                                                    strategyInstanceFactory,
                                                    userFactory);
        }
        /* (non-Javadoc)
         * @see org.marketcetera.trade.rpc.StrategyRpcClient.AbstractListenerProxy#sendMessage(java.lang.Object, java.lang.Object)
         */
        @Override
        protected void sendMessage(StrategyEventListener inMessageListener,
                                   StrategyEvent inMessage)
        {
            inMessageListener.receiveStrategyEvent(inMessage);
        }
        /**
         * Create a new StrategyEventListenerProxy instance.
         *
         * @param inStrategyEventListener a <code>StrategyEventListener</code> value
         */
        protected StrategyEventListenerProxy(StrategyEventListener inStrategyEventListener)
        {
            super(inStrategyEventListener);
        }
    }
    /**
     * Observes the file upload process.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    private static class FileUploadObserver
            implements StreamObserver<StrategyRpc.FileUploadResponse>
    {
        /**
         * Create a new FileUploadObserver instance.
         *
         * @param inRequest a <code>FileUploadRequest</code> value
         */
        public FileUploadObserver(FileUploadRequest inRequest)
        {
            request = inRequest;
        }
        /**
         * Increment the bytes uploaded value.
         *
         * @param inSize an <code>int</code> value
         */
        public void incrementBytesUploaded(int inSize)
        {
            bytesUploaded += inSize;
            double percentComplete = bytesUploaded / fileSize;
            request.onProgress(percentComplete);
        }
        /**
         * Sets the size of the file to be uploaded.
         *
         * @param inFileSize a <code>long</code> value
         */
        private void setFileSize(long inFileSize)
        {
            fileSize = inFileSize;
        }
        /* (non-Javadoc)
         * @see io.grpc.stub.StreamObserver#onNext(java.lang.Object)
         */
        @Override
        public void onNext(StrategyRpc.FileUploadResponse inFileUploadResponse)
        {
            currentStatus = inFileUploadResponse.getStatus();
            SLF4JLoggerProxy.trace(StrategyRpcClient.class,
                                   "File upload status: {}",
                                   currentStatus);
        }
        /* (non-Javadoc)
         * @see io.grpc.stub.StreamObserver#onError(java.lang.Throwable)
         */
        @Override
        public void onError(Throwable inError)
        {
            request.onError(inError);
            request.onStatus(FileUploadStatus.FAILED);
        }
        /* (non-Javadoc)
         * @see io.grpc.stub.StreamObserver#onCompleted()
         */
        @Override
        public void onCompleted()
        {
            completed = true;
            SLF4JLoggerProxy.trace(StrategyRpcClient.class,
                                   "File upload completed, status is: {}, error is: {}",
                                   currentStatus,
                                   uploadError);
            request.onStatus(uploadError == null ? FileUploadStatus.SUCCESS : FileUploadStatus.FAILED);
        }
        /**
         * indicates the total number of bytes uploaded so far
         */
        private double bytesUploaded = 0;
        /**
         * indicates the total size of the file to be uploaded
         */
        private double fileSize;
        /**
         * holds the file upload request
         */
        private final FileUploadRequest request;
        /**
         * indicates if the file upload has completed or not
         */
        @SuppressWarnings("unused")
        private boolean completed = false;
        /**
         * holds the error that occurred during upload, if any
         */
        private Throwable uploadError;
        /**
         * holds the current status of the upload
         */
        private StrategyTypesRpc.FileUploadStatus currentStatus = StrategyTypesRpc.FileUploadStatus.UNRECOGNIZED;
    }    
    /* (non-Javadoc)
     * @see AbstractRpcClient#getBlockingStub(io.grpc.Channel)
     */
    @Override
    protected StrategyRpcServiceGrpc.StrategyRpcServiceBlockingStub getBlockingStub(io.grpc.Channel inChannel)
    {
        return StrategyRpcServiceGrpc.newBlockingStub(inChannel);
    }
    /* (non-Javadoc)
     * @see AbstractRpcClient#getAsyncStub(io.grpc.Channel)
     */
    @Override
    protected StrategyRpcServiceGrpc.StrategyRpcServiceStub getAsyncStub(io.grpc.Channel inChannel)
    {
        return StrategyRpcServiceGrpc.newStub(inChannel);
    }
    /* (non-Javadoc)
     * @see AbstractRpcClient#executeLogin(BaseRpc.LoginRequest)
     */
    @Override
    protected BaseRpc.LoginResponse executeLogin(BaseRpc.LoginRequest inRequest)
    {
        return getBlockingStub().login(inRequest);
    }
    /* (non-Javadoc)
     * @see AbstractRpcClient#executeLogout(BaseRpc.LogoutRequest)
     */
    @Override
    protected BaseRpc.LogoutResponse executeLogout(BaseRpc.LogoutRequest inRequest)
    {
        return getBlockingStub().logout(inRequest);
    }
    /* (non-Javadoc)
     * @see AbstractRpcClient#executeHeartbeat(BaseRpc.HeartbeatRequest)
     */
    @Override
    protected BaseRpc.HeartbeatResponse executeHeartbeat(BaseRpc.HeartbeatRequest inRequest)
    {
        return getBlockingStub().heartbeat(inRequest);
    }
    /* (non-Javadoc)
     * @see AbstractRpcClient#getAppId()
     */
    @Override
    protected AppId getAppId()
    {
        return APP_ID;
    }
    /* (non-Javadoc)
     * @see AbstractRpcClient#getVersionInfo()
     */
    @Override
    protected VersionInfo getVersionInfo()
    {
        return APP_ID_VERSION;
    }
    /**
     * Create a new StrategyRpc instance.
     *
     * @param inParameters a <code>StrategyRpcClientParameters</code> value
     */
    protected StrategyRpcClient(StrategyRpcClientParameters inParameters)
    {
        super(inParameters);
    }
    /**
     * Creates the appropriate proxy for the given listener.
     *
     * @param inListener an <code>Object</code> value
     * @return an <code>AbstractListenerProxy&lt;?,?,?&gt;</code> value
     */
    private AbstractClientListenerProxy<?,?,?> getListenerFor(Object inListener)
    {
        if(inListener instanceof StrategyEventListener) {
            return new StrategyEventListenerProxy((StrategyEventListener)inListener);
        } else {
            throw new UnsupportedOperationException();
        }
    }
    /**
     * holds report listeners by their id
     */
    private final Cache<String,BaseRpcUtil.AbstractClientListenerProxy<?,?,?>> listenerProxiesById = CacheBuilder.newBuilder().build();
    /**
     * holds listener proxies keyed by the listener
     */
    private final LoadingCache<Object,BaseRpcUtil.AbstractClientListenerProxy<?,?,?>> listenerProxies = CacheBuilder.newBuilder().build(new CacheLoader<Object,AbstractClientListenerProxy<?,?,?>>() {
        @Override
        public BaseRpcUtil.AbstractClientListenerProxy<?,?,?> load(Object inKey)
                throws Exception
        {
            BaseRpcUtil.AbstractClientListenerProxy<?,?,?> proxy = getListenerFor(inKey);
            listenerProxiesById.put(proxy.getId(),
                                    proxy);
            return proxy;
        }}
    );
    /**
     * provides access to the current strategy instance
     */
    @Autowired(required=false)
    private StrategyInstanceHolder strategyInstanceHolder;
    /**
     * creates new {@link StrategyMessage} objects
     */
    @Autowired
    private StrategyMessageFactory strategyMessageFactory;
    /**
     * creates new {@link StrategyInstance} objects
     */
    @Autowired
    private StrategyInstanceFactory strategyInstanceFactory;
    /**
     * creates new {@link User} objects
     */
    @Autowired
    private UserFactory userFactory;
    /**
     * The client's application ID: the application name.
     */
    public static final String APP_ID_NAME = "StrategyRpc"; //$NON-NLS-1$
    /**
     * The client's application ID: the version.
     */
    public static final VersionInfo APP_ID_VERSION = ApplicationVersion.getVersion(StrategyClient.class);
    /**
     * The client's application ID: the ID.
     */
    public static final AppId APP_ID = Util.getAppId(APP_ID_NAME,APP_ID_VERSION.getVersionInfo());
}
