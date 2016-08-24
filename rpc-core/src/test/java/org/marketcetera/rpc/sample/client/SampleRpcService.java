package org.marketcetera.rpc.sample.client;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.marketcetera.rpc.base.BaseRpc;
import org.marketcetera.rpc.base.BaseRpc.HeartbeatRequest;
import org.marketcetera.rpc.base.BaseRpc.HeartbeatResponse;
import org.marketcetera.rpc.base.BaseRpc.LoginRequest;
import org.marketcetera.rpc.base.BaseRpc.LoginResponse;
import org.marketcetera.rpc.base.BaseRpc.LogoutRequest;
import org.marketcetera.rpc.base.BaseRpc.LogoutResponse;
import org.marketcetera.rpc.sample.SampleRpcServiceGrpc;
import org.marketcetera.util.log.SLF4JLoggerProxy;

import io.grpc.stub.StreamObserver;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class SampleRpcService
        extends SampleRpcServiceGrpc.SampleRpcServiceImplBase
{
    /* (non-Javadoc)
     * @see org.marketcetera.rpc.sample.SampleRpcServiceGrpc.SampleRpcServiceImplBase#login(org.marketcetera.rpc.base.BaseRpc.LoginRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    public void login(LoginRequest inRequest,
                      StreamObserver<LoginResponse> inResponseObserver)
    {
        // TODO identify service
        SLF4JLoggerProxy.trace(this,
                               "{} received {}",
                               this,
                               inRequest);
      BaseRpc.LoginResponse.Builder responseBuilder = BaseRpc.LoginResponse.newBuilder();
      BaseRpc.Status.Builder statusBuilder = BaseRpc.Status.newBuilder();
      statusBuilder.setFailed(false);
      String username = inRequest.getUsername();
      if(validLogins.contains(username)) {
          String sessionId = UUID.randomUUID().toString();
          statusBuilder.setSessionId(sessionId);
          responseBuilder.setSessionId(sessionId);
      } else {
          statusBuilder.setFailed(true);
          statusBuilder.setMessage(inRequest.getUsername() + " is not a valid user");
      }
      BaseRpc.LoginResponse response = responseBuilder.build();
      inResponseObserver.onNext(response);
      inResponseObserver.onCompleted();
    }
    private Set<String> validLogins = new HashSet<>();
    /* (non-Javadoc)
     * @see org.marketcetera.rpc.sample.SampleRpcServiceGrpc.SampleRpcServiceImplBase#logout(org.marketcetera.rpc.base.BaseRpc.LogoutRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    public void logout(LogoutRequest inRequest,
                       StreamObserver<LogoutResponse> inResponseObserver)
    {
        throw new UnsupportedOperationException(); // TODO
    }
    /* (non-Javadoc)
     * @see org.marketcetera.rpc.sample.SampleRpcServiceGrpc.SampleRpcServiceImplBase#heartbeat(org.marketcetera.rpc.base.BaseRpc.HeartbeatRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    public void heartbeat(HeartbeatRequest inRequest,
                          StreamObserver<HeartbeatResponse> inResponseObserver)
    {
        throw new UnsupportedOperationException(); // TODO
    }
}
//private static class SampleService
//extends SampleRpcServiceGrpc.SampleRpcServiceImplBase
//{
///* (non-Javadoc)
//* @see org.marketcetera.util.rpc.SampleRpcServiceGrpc.SampleRpcServiceImplBase#login(org.marketcetera.util.rpc.SampleRpc.LoginRequest, io.grpc.stub.StreamObserver)
//*/
//@Override
//public void login(LoginRequest inRequest,
//            StreamObserver<LoginResponse> inResponseObserver)
//{
//System.out.println("COLIN: login invoked: " + inRequest);
//SampleRpc.LoginResponse.Builder responseBuilder = SampleRpc.LoginResponse.newBuilder();
//SampleRpc.Status.Builder statusBuilder = SampleRpc.Status.newBuilder();
//statusBuilder.setFailed(false);
//try {
////  SessionId sessionId = serverServices.login(new RpcCredentials(inRequest.getUsername(),
////                                                                inRequest.getPassword(),
////                                                                inRequest.getAppId(),
////                                                                inRequest.getClientId(),
////                                                                inRequest.getVersionId(),
////                                                                new Locale(inRequest.getLocale().getLanguage(),
////                                                                           inRequest.getLocale().getCountry(),
////                                                                           inRequest.getLocale().getVariant())));
//  String sessionId = UUID.randomUUID().toString();
//  statusBuilder.setSessionId(sessionId);
//  responseBuilder.setSessionId(sessionId);
//} catch (Exception e) {
////  String message = ExceptionUtils.getRootCauseMessage(e);
////  if(SLF4JLoggerProxy.isDebugEnabled(this)) {
////      SLF4JLoggerProxy.warn(this,
////                            e,
////                            message);
////  } else {
////      SLF4JLoggerProxy.warn(this,
////                            message);
////  }
//  String message = e.getMessage();
//  statusBuilder.setFailed(true);
//  statusBuilder.setMessage(message);
//  responseBuilder.setSessionId("null"); //$NON-NLS-1$
//}
//responseBuilder.setStatus(statusBuilder.build());
//SampleRpc.LoginResponse response = responseBuilder.build();
////SLF4JLoggerProxy.trace(this,
////                     "Returning {} for {}",
////                     response,
////                     inRequest.getUsername());
//inResponseObserver.onNext(response);
//inResponseObserver.onCompleted();
//}
///* (non-Javadoc)
//* @see org.marketcetera.util.rpc.SampleRpcServiceGrpc.SampleRpcServiceImplBase#logout(org.marketcetera.util.rpc.SampleRpc.LogoutRequest, io.grpc.stub.StreamObserver)
//*/
//@Override
//public void logout(LogoutRequest inRequest,
//             StreamObserver<LogoutResponse> inResponseObserver)
//{
//throw new UnsupportedOperationException(); // TODO
//}
///* (non-Javadoc)
//* @see org.marketcetera.util.rpc.SampleRpcServiceGrpc.SampleRpcServiceImplBase#heartbeat(org.marketcetera.util.rpc.SampleRpc.HeartbeatRequest, io.grpc.stub.StreamObserver)
//*/
//@Override
//public void heartbeat(HeartbeatRequest inRequest,
//                StreamObserver<HeartbeatResponse> inResponseObserver)
//{
//throw new UnsupportedOperationException(); // TODO
//}
///* (non-Javadoc)
//* @see org.marketcetera.util.rpc.SampleRpcServiceGrpc.SampleRpcServiceImplBase#newHeartbeat(org.marketcetera.util.rpc.SampleRpc.HeartbeatRequest, io.grpc.stub.StreamObserver)
//*/
//@Override
//public void newHeartbeat(HeartbeatRequest inRequest,
//                   StreamObserver<HeartbeatResponse> inResponseObserver)
//{
///*
//SLF4JLoggerProxy.trace(this,
//                 "{} received hearbeat request: {}", //$NON-NLS-1$
//                 getDescription(),
//                 inRequest.getId());
//SampleRpc.HeartbeatResponse.Builder responseBuilder = SampleRpc.HeartbeatResponse.newBuilder();
//SampleRpc.Status.Builder statusBuilder = SampleRpc.Status.newBuilder();
//statusBuilder.setFailed(false);
//try {
//responseBuilder.setId(inRequest.getId());
//} catch (Exception e) {
//String message = ExceptionUtils.getRootCauseMessage(e);
//if(SLF4JLoggerProxy.isDebugEnabled(this)) {
//  SLF4JLoggerProxy.warn(this,
//                        e,
//                        message);
//} else {
//  SLF4JLoggerProxy.warn(this,
//                        message);
//}
//statusBuilder.setFailed(true);
//statusBuilder.setMessage(message);
//}
//responseBuilder.setStatus(statusBuilder.build());
//SampleRpc.HeartbeatResponse response = responseBuilder.build();
//SLF4JLoggerProxy.trace(this,
//                 "Returning {}: {}",
//                 response,
//                 inRequest.getId());
//return response;
//*/
//try {
//  for(int i=0;i<10;i++) {
//      SampleRpc.HeartbeatResponse.Builder responseBuilder = SampleRpc.HeartbeatResponse.newBuilder();
//      SampleRpc.Status.Builder statusBuilder = SampleRpc.Status.newBuilder();
//      statusBuilder.setFailed(false);
//      responseBuilder.setId(inRequest.getId());
//      responseBuilder.setStatus(statusBuilder.build());
//      SampleRpc.HeartbeatResponse response = responseBuilder.build();
//      inResponseObserver.onNext(response);
//      Thread.sleep(1000);
//  }
//} catch (InterruptedException e) {
//  throw new RuntimeException(e);
//}
//inResponseObserver.onCompleted();
//}
///* (non-Javadoc)
//* @see org.marketcetera.util.rpc.SampleRpcServiceGrpc.SampleRpcServiceImplBase#bindService()
//*/
//@Override
//public ServerServiceDefinition bindService()
//{
//System.out.println("COLIN: bindService invoked");
//return super.bindService();
//}
//}
