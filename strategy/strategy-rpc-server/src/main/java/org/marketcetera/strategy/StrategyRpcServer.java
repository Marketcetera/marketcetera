//
// this file is automatically generated
//
package org.marketcetera.strategy;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.Optional;

import javax.annotation.PostConstruct;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.Validate;
import org.marketcetera.admin.UserFactory;
import org.marketcetera.admin.service.AuthorizationService;
import org.marketcetera.core.PlatformServices;
import org.marketcetera.core.Preserve;
import org.marketcetera.core.notifications.INotification.Severity;
import org.marketcetera.persist.CollectionPageResponse;
import org.marketcetera.persist.PageRequest;
import org.marketcetera.rpc.base.BaseRpc;
import org.marketcetera.rpc.base.BaseRpcUtil;
import org.marketcetera.rpc.paging.PagingRpcUtil;
import org.marketcetera.rpc.server.AbstractRpcService;
import org.marketcetera.strategy.StrategyRpc.DeleteAllStrategyMessagesRequest;
import org.marketcetera.strategy.StrategyRpc.DeleteAllStrategyMessagesResponse;
import org.marketcetera.strategy.StrategyRpc.DeleteStrategyMessageRequest;
import org.marketcetera.strategy.StrategyRpc.DeleteStrategyMessageResponse;
import org.marketcetera.strategy.StrategyRpc.FileUploadResponse;
import org.marketcetera.strategy.events.StrategyEvent;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.ws.stateful.SessionHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.protobuf.ByteString;

import io.grpc.stub.StreamObserver;

/* $License$ */

/**
 * Provides an RPC Server for StrategyRpc services.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Preserve
@AutoConfiguration
public class StrategyRpcServer<SessionClazz>
        extends AbstractRpcService<SessionClazz,StrategyRpcServiceGrpc.StrategyRpcServiceImplBase>
{
    /**
     * Validate and start the object.
     */
    @PostConstruct
    public void start()
            throws Exception
    {
        service = new Service();
        super.start();
    }
    /* (non-Javadoc)
     * @see AbstractRpcService#getServiceDescription()
     */
    @Override
    protected String getServiceDescription()
    {
        return description;
    }
    /* (non-Javadoc)
     * @see AbstractRpcService#getService()
     */
    @Override
    protected StrategyRpcServiceGrpc.StrategyRpcServiceImplBase getService()
    {
        return service;
    }
    /**
     * Write to the appropriate output file with the given upload metadata or file chunk.
     * 
     * @param inStrategyPath a <code>Path</code> value 
     * @return an <code>OutputStream</code> value
     * @throws IOException if an error occurs creating the file
     */
    private OutputStream getOutstreamFromFilePath(Path inStrategyPath)
            throws IOException
    {
        return Files.newOutputStream(inStrategyPath,
                                     StandardOpenOption.CREATE,
                                     StandardOpenOption.APPEND);
    }
    /**
     * Writes the given file chunk to the given file.
     *
     * @param inWriter an <code>OutputStream</code> value
     * @param inContent a <cod>ByteString</code> value
     * @throws IOException if an error occurs writing to the file
     */
    private void writeFile(OutputStream inWriter,
                           ByteString inContent)
            throws IOException
    {
        inWriter.write(inContent.toByteArray());
        inWriter.flush();
    }
    /**
     * Closes the given file writer.
     *
     * @param inWriter an <code>OutputStream</code> value
     * @throws IOException if an error occurs closing the file
     */
    private void closeFile(OutputStream inWriter)
            throws IOException
    {
        inWriter.close();
    }
    /**
     * Verify an uploaded strategy file matches the given expected attributes.
     *
     * @param inStrategyFile a <code>Path</code> vlaue
     * @param inNonce a <code>String</code> value
     * @param inName a <code>String</code> value
     * @throws NoSuchAlgorithmException if the file cannot be hashed
     * @throws IOException if the file cannot be read
     */
    private void verifyAndMoveFile(Path inStrategyFile,
                                   String inNonce,
                                   String inName)
            throws NoSuchAlgorithmException, IOException
    {
        SLF4JLoggerProxy.debug(this,
                               "Verifying the uploaded strategy with the name '{}'",
                               inName);
        // find the incoming upload
        Optional<? extends StrategyInstance> strategyInstanceOption = strategyService.findByName(inName);
        Validate.isTrue(strategyInstanceOption.isPresent(),
                        "No strategy instance with name '" + inName + "' found");
        StrategyInstance strategyInstance = strategyInstanceOption.get();
        Validate.isTrue(inNonce.equals(strategyInstance.getNonce()),
                        "Strategy upload nonce does not match");
        String hash = PlatformServices.getFileChecksum(inStrategyFile.toFile());
        Validate.isTrue(hash.equals(strategyInstance.getHash()),
                        "Strategy upload hash does not match");
        Path strategyInstanceDirectory = strategyService.getIncomingStrategyDirectory();
        Path strategyFileTarget = strategyInstanceDirectory.resolve(inStrategyFile.getFileName());
        SLF4JLoggerProxy.debug(this,
                               "Verified the uploaded strategy with the name '{}', moving to strategy directory {}",
                               inName,
                               strategyFileTarget);
        FileUtils.moveFile(inStrategyFile.toFile(),
                           strategyFileTarget.toFile());
    }
    /**
     * StrategyRpc Service implementation.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    private class Service
            extends StrategyRpcServiceGrpc.StrategyRpcServiceImplBase
    {
        /* (non-Javadoc)
         * @see StrategyRpcServiceGrpc.StrategyRpcServiceImplBase#login(BaseRpc.LoginRequest, StreamObserver)
         */
        @Override
        public void login(BaseRpc.LoginRequest inRequest,StreamObserver<BaseRpc.LoginResponse> inResponseObserver)
        {
            StrategyRpcServer.this.doLogin(inRequest,inResponseObserver);
        }
        /* (non-Javadoc)
         * @see StrategyRpcServiceGrpc.StrategyRpcServiceImplBase#logout(BaseRpc.LogoutRequest, StreamObserver)
         */
        @Override
        public void logout(BaseRpc.LogoutRequest inRequest,StreamObserver<BaseRpc.LogoutResponse> inResponseObserver)
        {
            StrategyRpcServer.this.doLogout(inRequest,inResponseObserver);
        }
        /* (non-Javadoc)
         * @see StrategyRpcServiceGrpc.StrategyRpcServiceImplBase#heartbeat(BaseRpc.HeartbeatRequest, StreamObserver)
         */
        @Override
        public void heartbeat(BaseRpc.HeartbeatRequest inRequest,StreamObserver<BaseRpc.HeartbeatResponse> inResponseObserver)
        {
            StrategyRpcServer.this.doHeartbeat(inRequest,inResponseObserver);
        }
        /* (non-Javadoc)
         * @see StrategyRpcServiceGrpc.StrategyRpcServiceImplBase#getStrategyInstances(StrategyRpc.ReadStrategyInstancesRequest ,StreamObserver)
         */
        @Override
        public void getStrategyInstances(StrategyRpc.ReadStrategyInstancesRequest inReadStrategyInstancesRequest,StreamObserver<StrategyRpc.ReadStrategyInstancesResponse> inResponseObserver)
        {
            try {
                SLF4JLoggerProxy.trace(StrategyRpcServer.this,"Received {}",inReadStrategyInstancesRequest);
                SessionHolder<SessionClazz> sessionHolder = validateAndReturnSession(inReadStrategyInstancesRequest.getSessionId());
                authzService.authorize(sessionHolder.getUser(),StrategyPermissions.ReadStrategyAction.name());
                StrategyRpc.ReadStrategyInstancesResponse.Builder responseBuilder = StrategyRpc.ReadStrategyInstancesResponse.newBuilder();
                Collection<? extends StrategyInstance> serviceData = strategyService.getStrategyInstances(sessionHolder.getUser());
                serviceData.forEach(strategyInstance -> StrategyRpcUtil.getRpcStrategyInstance(strategyInstance).ifPresent(rpcStrategyInstance -> responseBuilder.addStrategyInstances(rpcStrategyInstance)));
                StrategyRpc.ReadStrategyInstancesResponse response = responseBuilder.build();
                SLF4JLoggerProxy.trace(StrategyRpcServer.this,"Responding {}",response);
                inResponseObserver.onNext(response);
                inResponseObserver.onCompleted();
            } catch (Exception e) {
                handleError(e,inResponseObserver);
            }
        }
        /* (non-Javadoc)
         * @see StrategyRpcServiceGrpc.StrategyRpcServiceImplBase#getStrategyMessages(StrategyRpc.ReadStrategyMessagesRequest ,io.grpc.stub.StreamObserver)
         */
        @Override
        public void getStrategyMessages(StrategyRpc.ReadStrategyMessagesRequest inReadStrategyMessagesRequest,io.grpc.stub.StreamObserver<StrategyRpc.ReadStrategyMessagesResponse> inResponseObserver)
        {
            try {
                SLF4JLoggerProxy.trace(StrategyRpcServer.this,"Received {}",inReadStrategyMessagesRequest);
                SessionHolder<SessionClazz> sessionHolder = validateAndReturnSession(inReadStrategyMessagesRequest.getSessionId());
                authzService.authorize(sessionHolder.getUser(),StrategyPermissions.ReadStrategyMessagesAction.name());
                StrategyRpc.ReadStrategyMessagesResponse.Builder responseBuilder = StrategyRpc.ReadStrategyMessagesResponse.newBuilder();
                String strategyName = inReadStrategyMessagesRequest.getStrategyName();
                Severity severity = StrategyRpcUtil.getStrategyMessageSeverity(inReadStrategyMessagesRequest.getSeverity()).orElse(null);
                PageRequest pageRequest = inReadStrategyMessagesRequest.hasPageRequest()?PagingRpcUtil.getPageRequest(inReadStrategyMessagesRequest.getPageRequest()):PageRequest.ALL;
                CollectionPageResponse<? extends StrategyMessage> serviceData = strategyService.getStrategyMessages(strategyName,
                                                                                                                    severity,
                                                                                                                    pageRequest);
                for(StrategyMessage strategyMessage : serviceData.getElements()) {
                    StrategyRpcUtil.getRpcStrategyMessage(strategyMessage).ifPresent(rpcStrategyMessage -> responseBuilder.addStrategyMessages(rpcStrategyMessage));
                }
                responseBuilder.setPageResponse(PagingRpcUtil.getPageResponse(pageRequest,serviceData));
                StrategyRpc.ReadStrategyMessagesResponse response = responseBuilder.build();
                SLF4JLoggerProxy.trace(StrategyRpcServer.this,"Responding {}",response);
                inResponseObserver.onNext(response);
                inResponseObserver.onCompleted();
            } catch (Exception e) {
                handleError(e,inResponseObserver);
            }
        }
        /* (non-Javadoc)
         * @see StrategyRpcServiceGrpc.StrategyRpcServiceImplBase#startStrategyInstance(StrategyRpc.StartStrategyInstanceRequest ,io.grpc.stub.StreamObserver)
         */
        @Override
        public void startStrategyInstance(StrategyRpc.StartStrategyInstanceRequest inStartStrategyInstanceRequest,io.grpc.stub.StreamObserver<StrategyRpc.StartStrategyInstanceResponse> inResponseObserver)
        {
            try {
                SLF4JLoggerProxy.trace(StrategyRpcServer.this,"Received {}",inStartStrategyInstanceRequest);
                SessionHolder<SessionClazz> sessionHolder = validateAndReturnSession(inStartStrategyInstanceRequest.getSessionId());
                authzService.authorize(sessionHolder.getUser(),StrategyPermissions.StartStrategyAction.name());
                StrategyRpc.StartStrategyInstanceResponse.Builder responseBuilder = StrategyRpc.StartStrategyInstanceResponse.newBuilder();
                String strategyInstanceName = inStartStrategyInstanceRequest.getName();
                strategyService.startStrategyInstance(strategyInstanceName);
                StrategyRpc.StartStrategyInstanceResponse response = responseBuilder.build();
                SLF4JLoggerProxy.trace(StrategyRpcServer.this,"Responding {}",response);
                inResponseObserver.onNext(response);
                inResponseObserver.onCompleted();
            } catch (Exception e) {
                handleError(e,inResponseObserver);
            }
        }
        /* (non-Javadoc)
         * @see StrategyRpcServiceGrpc.StrategyRpcServiceImplBase#stopStrategyInstance(StrategyRpc.StopStrategyInstanceRequest ,io.grpc.stub.StreamObserver)
         */
        @Override
        public void stopStrategyInstance(StrategyRpc.StopStrategyInstanceRequest inStopStrategyInstanceRequest,io.grpc.stub.StreamObserver<StrategyRpc.StopStrategyInstanceResponse> inResponseObserver)
        {
            try {
                SLF4JLoggerProxy.trace(StrategyRpcServer.this,"Received {}",inStopStrategyInstanceRequest);
                SessionHolder<SessionClazz> sessionHolder = validateAndReturnSession(inStopStrategyInstanceRequest.getSessionId());
                authzService.authorize(sessionHolder.getUser(),StrategyPermissions.StopStrategyAction.name());
                StrategyRpc.StopStrategyInstanceResponse.Builder responseBuilder = StrategyRpc.StopStrategyInstanceResponse.newBuilder();
                String strategyInstanceName = inStopStrategyInstanceRequest.getName();
                strategyService.stopStrategyInstance(strategyInstanceName);
                StrategyRpc.StopStrategyInstanceResponse response = responseBuilder.build();
                SLF4JLoggerProxy.trace(StrategyRpcServer.this,"Responding {}",response);
                inResponseObserver.onNext(response);
                inResponseObserver.onCompleted();
            } catch (Exception e) {
                handleError(e,inResponseObserver);
            }
        }
        /* (non-Javadoc)
         * @see StrategyRpcServiceGrpc.StrategyRpcServiceImplBase#findByName(StrategyRpc.FindStrategyInstanceByNameRequest ,io.grpc.stub.StreamObserver)
         */
        @Override
        public void findByName(StrategyRpc.FindStrategyInstanceByNameRequest inFindStrategyInstanceByNameRequest,io.grpc.stub.StreamObserver<StrategyRpc.FindStrategyInstanceByNameResponse> inResponseObserver)
        {
            try {
                SLF4JLoggerProxy.trace(StrategyRpcServer.this,"Received findByNameRequest for {}",inFindStrategyInstanceByNameRequest);
                SessionHolder<SessionClazz> sessionHolder = validateAndReturnSession(inFindStrategyInstanceByNameRequest.getSessionId());
                authzService.authorize(sessionHolder.getUser(),StrategyPermissions.ReadStrategyAction.name());
                StrategyRpc.FindStrategyInstanceByNameResponse.Builder responseBuilder = StrategyRpc.FindStrategyInstanceByNameResponse.newBuilder();
                String name = inFindStrategyInstanceByNameRequest.getName();
                Optional<? extends StrategyInstance> serviceData = strategyService.findByName(name);
                serviceData.ifPresent(strategyInstance -> StrategyRpcUtil.getRpcStrategyInstance(strategyInstance).ifPresent(rpcStrategyInstance -> responseBuilder.setStrategyInstance(rpcStrategyInstance)));
                StrategyRpc.FindStrategyInstanceByNameResponse response = responseBuilder.build();
                SLF4JLoggerProxy.trace(StrategyRpcServer.this,"Responding {}",response);
                inResponseObserver.onNext(response);
                inResponseObserver.onCompleted();
            } catch (Exception e) {
                handleError(e,inResponseObserver);
            }
        }
        /* (non-Javadoc)
         * @see org.marketcetera.strategy.StrategyRpcServiceGrpc.StrategyRpcServiceImplBase#deleteStrategyMessage(org.marketcetera.strategy.StrategyRpc.DeleteStrategyMessageRequest, io.grpc.stub.StreamObserver)
         */
        @Override
        public void deleteStrategyMessage(DeleteStrategyMessageRequest inRequest,
                                          StreamObserver<DeleteStrategyMessageResponse> inResponseObserver)
        {
            try {
                SLF4JLoggerProxy.trace(StrategyRpcServer.this,
                                       "Received {}",
                                       inRequest);
                SessionHolder<SessionClazz> sessionHolder = validateAndReturnSession(inRequest.getSessionId());
                authzService.authorize(sessionHolder.getUser(),StrategyPermissions.DeleteStrategyMessagesAction.name());
                StrategyRpc.DeleteStrategyMessageResponse.Builder responseBuilder = StrategyRpc.DeleteStrategyMessageResponse.newBuilder();
                long strategyMessageId = inRequest.getStrategyMessageId();
                strategyService.deleteStrategyMessage(strategyMessageId);
                StrategyRpc.DeleteStrategyMessageResponse response = responseBuilder.build();
                SLF4JLoggerProxy.trace(StrategyRpcServer.this,
                                       "Responding {}",
                                       response);
                inResponseObserver.onNext(response);
                inResponseObserver.onCompleted();
            } catch (Exception e) {
                handleError(e,inResponseObserver);
            }
        }
        /* (non-Javadoc)
         * @see org.marketcetera.strategy.StrategyRpcServiceGrpc.StrategyRpcServiceImplBase#deleteAllStrategyMessages(org.marketcetera.strategy.StrategyRpc.DeleteAllStrategyMessagesRequest, io.grpc.stub.StreamObserver)
         */
        @Override
        public void deleteAllStrategyMessages(DeleteAllStrategyMessagesRequest inRequest,
                                              StreamObserver<DeleteAllStrategyMessagesResponse> inResponseObserver)
        {
            try {
                SLF4JLoggerProxy.trace(StrategyRpcServer.this,
                                       "Received {}",
                                       inRequest);
                SessionHolder<SessionClazz> sessionHolder = validateAndReturnSession(inRequest.getSessionId());
                authzService.authorize(sessionHolder.getUser(),StrategyPermissions.DeleteStrategyMessagesAction.name());
                StrategyRpc.DeleteAllStrategyMessagesResponse.Builder responseBuilder = StrategyRpc.DeleteAllStrategyMessagesResponse.newBuilder();
                String strategyInstanceName = inRequest.getStrategyInstanceName();
                strategyService.deleteAllStrategyMessages(strategyInstanceName);
                StrategyRpc.DeleteAllStrategyMessagesResponse response = responseBuilder.build();
                SLF4JLoggerProxy.trace(StrategyRpcServer.this,
                                       "Responding {}",
                                       response);
                inResponseObserver.onNext(response);
                inResponseObserver.onCompleted();
            } catch (Exception e) {
                handleError(e,inResponseObserver);
            }
        }
        /* (non-Javadoc)
         * @see StrategyRpcServiceGrpc.StrategyRpcServiceImplBase#loadStrategyInstance(StrategyRpc.LoadStrategyInstanceRequest ,StreamObserver)
         */
        @Override
        public void loadStrategyInstance(StrategyRpc.LoadStrategyInstanceRequest inLoadStrategyInstanceRequest,StreamObserver<StrategyRpc.LoadStrategyInstanceResponse> inResponseObserver)
        {
            try {
                SLF4JLoggerProxy.trace(StrategyRpcServer.this,"Received {}",inLoadStrategyInstanceRequest);
                SessionHolder<SessionClazz> sessionHolder = validateAndReturnSession(inLoadStrategyInstanceRequest.getSessionId());
                authzService.authorize(sessionHolder.getUser(),StrategyPermissions.LoadStrategyAction.name());
                StrategyRpc.LoadStrategyInstanceResponse.Builder responseBuilder = StrategyRpc.LoadStrategyInstanceResponse.newBuilder();
                StrategyInstance strategyInstance = StrategyRpcUtil.getStrategyInstance(inLoadStrategyInstanceRequest.getStrategyInstance(),strategyInstanceFactory,userFactory).orElse(null);
                StrategyStatus serviceData = strategyService.loadStrategyInstance(strategyInstance);
                StrategyRpcUtil.getRpcStrategyStatus(serviceData).ifPresent(rpcStatus -> responseBuilder.setStatus(rpcStatus));
                StrategyRpc.LoadStrategyInstanceResponse response = responseBuilder.build();
                SLF4JLoggerProxy.trace(StrategyRpcServer.this,"Responding {}",response);
                inResponseObserver.onNext(response);
                inResponseObserver.onCompleted();
            } catch (Exception e) {
                handleError(e,inResponseObserver);
            }
        }
        /* (non-Javadoc)
         * @see StrategyRpcServiceGrpc.StrategyRpcServiceImplBase#unloadStrategyInstance(StrategyRpc.UnloadStrategyInstanceRequest ,io.grpc.stub.StreamObserver)
         */
        @Override
        public void unloadStrategyInstance(StrategyRpc.UnloadStrategyInstanceRequest inUnloadStrategyInstanceRequest,io.grpc.stub.StreamObserver<StrategyRpc.UnloadStrategyInstanceResponse> inResponseObserver)
        {
            try {
                SLF4JLoggerProxy.trace(StrategyRpcServer.this,"Received {}",inUnloadStrategyInstanceRequest);
                SessionHolder<SessionClazz> sessionHolder = validateAndReturnSession(inUnloadStrategyInstanceRequest.getSessionId());
                authzService.authorize(sessionHolder.getUser(),StrategyPermissions.UnloadStrategyAction.name());
                StrategyRpc.UnloadStrategyInstanceResponse.Builder responseBuilder = StrategyRpc.UnloadStrategyInstanceResponse.newBuilder();
                String strategyInstanceName = inUnloadStrategyInstanceRequest.getName();
                strategyService.unloadStrategyInstance(strategyInstanceName);
                StrategyRpc.UnloadStrategyInstanceResponse response = responseBuilder.build();
                SLF4JLoggerProxy.trace(StrategyRpcServer.this,"Responding {}",response);
                inResponseObserver.onNext(response);
                inResponseObserver.onCompleted();
            } catch (Exception e) {
                handleError(e,inResponseObserver);
            }
        }
        /* (non-Javadoc)
         * @see org.marketcetera.strategy.StrategyRpcServiceGrpc.StrategyRpcServiceImplBase#createStrategyMessage(org.marketcetera.strategy.StrategyRpc.CreateStrategyMessageRequest, io.grpc.stub.StreamObserver)
         */
        @Override
        public void createStrategyMessage(StrategyRpc.CreateStrategyMessageRequest inRequest,
                                          StreamObserver<StrategyRpc.CreateStrategyMessageResponse> inResponseObserver)
        {
            try {
                SLF4JLoggerProxy.trace(StrategyRpcServer.this,
                                       "Received {}",
                                       inRequest);
                SessionHolder<SessionClazz> sessionHolder = validateAndReturnSession(inRequest.getSessionId());
                authzService.authorize(sessionHolder.getUser(),StrategyPermissions.CreateStrategyMessagesAction.name());
                StrategyRpc.CreateStrategyMessageResponse.Builder responseBuilder = StrategyRpc.CreateStrategyMessageResponse.newBuilder();
                StrategyMessage strategyMessage = StrategyRpcUtil.getStrategyMessage(inRequest.getStrategyMessage(),
                                                                                     strategyMessageFactory,
                                                                                     strategyInstanceFactory,
                                                                                     userFactory).orElse(null);
                strategyService.createStrategyMessage(strategyMessage);
                StrategyRpc.CreateStrategyMessageResponse response = responseBuilder.build();
                SLF4JLoggerProxy.trace(StrategyRpcServer.this,
                                       "Responding {}",
                                       response);
                inResponseObserver.onNext(response);
                inResponseObserver.onCompleted();
            } catch (Exception e) {
                handleError(e,
                            inResponseObserver);
            }
        }
        /* (non-Javadoc)
         * @see StrategyRpcServiceGrpc.StrategyRpcServiceImplBase#uploadFile(StreamObserver)
         */
        @Override
        public StreamObserver<StrategyRpc.FileUploadRequest> uploadFile(StreamObserver<StrategyRpc.FileUploadResponse> inResponseObserver)
        {
            return new StreamObserver<StrategyRpc.FileUploadRequest>() {
                @Override
                public void onNext(StrategyRpc.FileUploadRequest inFileUploadRequest)
                {
                    try {
                        switch(inFileUploadRequest.getRequestCase()) {
                            case FILE:
                                writeFile(writer,
                                          inFileUploadRequest.getFile().getContent());
                                break;
                            case METADATA:
                                uploadNonce = inFileUploadRequest.getMetadata().getNonce();
                                instanceName = inFileUploadRequest.getMetadata().getName();
                                // determined to be JAR files right now, might be nice to include file type in the upload request
                                String fileName = inFileUploadRequest.getMetadata().getNonce() + ".jar";
                                strategyPath = strategyService.getTemporaryStrategyDirectory().resolve(fileName);
                                writer = getOutstreamFromFilePath(strategyPath);
                                break;
                            case REQUEST_NOT_SET:
                            default:
                                throw new UnsupportedOperationException("Request case not set in file upload request");
                        }
                    } catch (IOException e) {
                        SLF4JLoggerProxy.error(StrategyRpcServer.this,
                                               e);
                        this.onError(e);
                    }
                }
                @Override
                public void onError(Throwable throwable)
                {
                    status = StrategyTypesRpc.FileUploadStatus.FAILED;
                    this.onCompleted();
                }
                @Override
                public void onCompleted()
                {
                    try {
                        closeFile(writer);
                        verifyAndMoveFile(strategyPath,
                                          uploadNonce,
                                          instanceName);
                        status = StrategyTypesRpc.FileUploadStatus.SUCCESS;
                    } catch (Exception e) {
                        SLF4JLoggerProxy.warn(StrategyRpcServer.this,
                                              e);
                        status = StrategyTypesRpc.FileUploadStatus.FAILED;
                    }
                    FileUploadResponse response = FileUploadResponse.newBuilder().setStatus(status).build();
                    inResponseObserver.onNext(response);
                    inResponseObserver.onCompleted();
                }
                private String instanceName;
                private String uploadNonce;
                private Path strategyPath;
                private OutputStream writer;
                private StrategyTypesRpc.FileUploadStatus status = StrategyTypesRpc.FileUploadStatus.IN_PROGRESS;
            };
        }
        /* (non-Javadoc)
         * @see org.marketcetera.trade.rpc.TradeRpcServiceGrpc.TradeRpcServiceImplBase#addStrategyEventListener(org.marketcetera.trade.rpc.TradeRpc.AddStrategyEventListenerRequest, io.grpc.stub.StreamObserver)
         */
        @Override
        public void addStrategyEventListener(StrategyRpc.AddStrategyEventListenerRequest inRequest,
                                             StreamObserver<StrategyRpc.StrategyEventListenerResponse> inResponseObserver)
        {
            try {
                validateAndReturnSession(inRequest.getSessionId());
                SLF4JLoggerProxy.trace(StrategyRpcServer.this,
                                       "Received add strategy event listener request {}",
                                       inRequest);
                String listenerId = inRequest.getListenerId();
                BaseRpcUtil.AbstractServerListenerProxy<?> StrategyEventListenerProxy = listenerProxiesById.getIfPresent(listenerId);
                if(StrategyEventListenerProxy == null) {
                    StrategyEventListenerProxy = new StrategyEventListenerProxy(listenerId,
                                                                                inResponseObserver);
                    listenerProxiesById.put(StrategyEventListenerProxy.getId(),
                                            StrategyEventListenerProxy);
                    strategyService.addStrategyEventListener((StrategyEventListener)StrategyEventListenerProxy);
                }
            } catch (Exception e) {
                handleError(e,
                            inResponseObserver);
            }
        }
        /* (non-Javadoc)
         * @see org.marketcetera.trade.rpc.TradeRpcServiceGrpc.TradeRpcServiceImplBase#removeStrategyEventListener(org.marketcetera.trade.rpc.TradeRpc.RemoveStrategyEventListenerRequest, io.grpc.stub.StreamObserver)
         */
        @Override
        public void removeStrategyEventListener(StrategyRpc.RemoveStrategyEventListenerRequest inRequest,
                                                StreamObserver<StrategyRpc.RemoveStrategyEventListenerResponse> inResponseObserver)
        {
            try {
                validateAndReturnSession(inRequest.getSessionId());
                SLF4JLoggerProxy.trace(StrategyRpcServer.this,
                                       "Received remove strategy event listener request {}",
                                       inRequest);
                String listenerId = inRequest.getListenerId();
                BaseRpcUtil.AbstractServerListenerProxy<?> StrategyEventListenerProxy = listenerProxiesById.getIfPresent(listenerId);
                listenerProxiesById.invalidate(listenerId);
                if(StrategyEventListenerProxy != null) {
                    strategyService.removeStrategyEventListener((StrategyEventListener)StrategyEventListenerProxy);
                    StrategyEventListenerProxy.close();
                }
                StrategyRpc.RemoveStrategyEventListenerResponse.Builder responseBuilder = StrategyRpc.RemoveStrategyEventListenerResponse.newBuilder();
                StrategyRpc.RemoveStrategyEventListenerResponse response = responseBuilder.build();
                SLF4JLoggerProxy.trace(StrategyRpcServer.this,
                                       "Returning {}",
                                       response);
                inResponseObserver.onNext(response);
                inResponseObserver.onCompleted();
            } catch (Exception e) {
                handleError(e,
                            inResponseObserver);
            }
        }
    }
    /**
     * Wraps a {@link StrategyEventListener} with the RPC call from the client.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    private static class StrategyEventListenerProxy
            extends BaseRpcUtil.AbstractServerListenerProxy<StrategyRpc.StrategyEventListenerResponse>
            implements StrategyEventListener
    {
        /* (non-Javadoc)
         * @see org.marketcetera.trade.StrategyEventListener#receiveStrategyEvent(org.marketcetera.trade.StrategyEvent)
         */
        @Override
        public void receiveStrategyEvent(StrategyEvent inStrategyEvent)
        {
            StrategyRpcUtil.setStrategyEvent(inStrategyEvent,
                                             responseBuilder);
            StrategyRpc.StrategyEventListenerResponse response = responseBuilder.build();
            SLF4JLoggerProxy.trace(StrategyRpcServer.class,
                                   "{} received strategy event {}, sending {}",
                                   getId(),
                                   inStrategyEvent,
                                   response);
            // TODO does the user have permissions (including supervisor) to view this strategy event?
            getObserver().onNext(response);
            responseBuilder.clear();
        }
        /**
         * Create a new StrategyEventListenerProxy instance.
         *
         * @param inId a <code>String</code> value
         * @param inObserver a <code>StreamObserver&lt;StrategyEventListenerResponse&gt;</code> value
         */
        private StrategyEventListenerProxy(String inId,
                                          StreamObserver<StrategyRpc.StrategyEventListenerResponse> inObserver)
        {
            super(inId,
                  inObserver);
        }
        /**
         * builder used to construct messages
         */
        private final StrategyRpc.StrategyEventListenerResponse.Builder responseBuilder = StrategyRpc.StrategyEventListenerResponse.newBuilder();
    }
    /**
     * holds trade message listeners by id
     */
    private final Cache<String,BaseRpcUtil.AbstractServerListenerProxy<?>> listenerProxiesById = CacheBuilder.newBuilder().build();
    /**
     * Creates new StrategyMessage objects
     */
    @Autowired
    private StrategyMessageFactory strategyMessageFactory;
    /**
     * Creates new StrategyInstance objects
     */
    @Autowired
    private StrategyInstanceFactory strategyInstanceFactory;
    /**
     * Creates new User objects
     */
    @Autowired
    private UserFactory userFactory;
    /**
     * provides services for Strategy
     */
    @Autowired
    private StrategyService strategyService;
    /**
     * provides access to authorization services
     */
    @Autowired
    private AuthorizationService authzService;
    /**
     * provides the RPC service
     */
    private Service service;
    /**
     * description of this service
     */
    private final static String description = "Strategy RPC Service";
}
