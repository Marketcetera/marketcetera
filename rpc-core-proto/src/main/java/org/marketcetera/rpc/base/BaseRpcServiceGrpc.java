package org.marketcetera.rpc.base;

import static io.grpc.stub.ClientCalls.asyncUnaryCall;
import static io.grpc.stub.ClientCalls.asyncServerStreamingCall;
import static io.grpc.stub.ClientCalls.asyncClientStreamingCall;
import static io.grpc.stub.ClientCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ClientCalls.blockingUnaryCall;
import static io.grpc.stub.ClientCalls.blockingServerStreamingCall;
import static io.grpc.stub.ClientCalls.futureUnaryCall;
import static io.grpc.MethodDescriptor.generateFullMethodName;
import static io.grpc.stub.ServerCalls.asyncUnaryCall;
import static io.grpc.stub.ServerCalls.asyncServerStreamingCall;
import static io.grpc.stub.ServerCalls.asyncClientStreamingCall;
import static io.grpc.stub.ServerCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedStreamingCall;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.0.0)",
    comments = "Source: rpc_base.proto")
public class BaseRpcServiceGrpc {

  private BaseRpcServiceGrpc() {}

  public static final String SERVICE_NAME = "BaseRpcService";

  // Static method descriptors that strictly reflect the proto.
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<org.marketcetera.rpc.base.BaseRpc.LoginRequest,
      org.marketcetera.rpc.base.BaseRpc.LoginResponse> METHOD_LOGIN =
      io.grpc.MethodDescriptor.create(
          io.grpc.MethodDescriptor.MethodType.UNARY,
          generateFullMethodName(
              "BaseRpcService", "login"),
          io.grpc.protobuf.ProtoUtils.marshaller(org.marketcetera.rpc.base.BaseRpc.LoginRequest.getDefaultInstance()),
          io.grpc.protobuf.ProtoUtils.marshaller(org.marketcetera.rpc.base.BaseRpc.LoginResponse.getDefaultInstance()));
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<org.marketcetera.rpc.base.BaseRpc.LogoutRequest,
      org.marketcetera.rpc.base.BaseRpc.LogoutResponse> METHOD_LOGOUT =
      io.grpc.MethodDescriptor.create(
          io.grpc.MethodDescriptor.MethodType.UNARY,
          generateFullMethodName(
              "BaseRpcService", "logout"),
          io.grpc.protobuf.ProtoUtils.marshaller(org.marketcetera.rpc.base.BaseRpc.LogoutRequest.getDefaultInstance()),
          io.grpc.protobuf.ProtoUtils.marshaller(org.marketcetera.rpc.base.BaseRpc.LogoutResponse.getDefaultInstance()));
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<org.marketcetera.rpc.base.BaseRpc.HeartbeatRequest,
      org.marketcetera.rpc.base.BaseRpc.HeartbeatResponse> METHOD_HEARTBEAT =
      io.grpc.MethodDescriptor.create(
          io.grpc.MethodDescriptor.MethodType.UNARY,
          generateFullMethodName(
              "BaseRpcService", "heartbeat"),
          io.grpc.protobuf.ProtoUtils.marshaller(org.marketcetera.rpc.base.BaseRpc.HeartbeatRequest.getDefaultInstance()),
          io.grpc.protobuf.ProtoUtils.marshaller(org.marketcetera.rpc.base.BaseRpc.HeartbeatResponse.getDefaultInstance()));

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static BaseRpcServiceStub newStub(io.grpc.Channel channel) {
    return new BaseRpcServiceStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static BaseRpcServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new BaseRpcServiceBlockingStub(channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary and streaming output calls on the service
   */
  public static BaseRpcServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new BaseRpcServiceFutureStub(channel);
  }

  /**
   */
  public static abstract class BaseRpcServiceImplBase implements io.grpc.BindableService {

    /**
     */
    public void login(org.marketcetera.rpc.base.BaseRpc.LoginRequest request,
        io.grpc.stub.StreamObserver<org.marketcetera.rpc.base.BaseRpc.LoginResponse> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_LOGIN, responseObserver);
    }

    /**
     */
    public void logout(org.marketcetera.rpc.base.BaseRpc.LogoutRequest request,
        io.grpc.stub.StreamObserver<org.marketcetera.rpc.base.BaseRpc.LogoutResponse> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_LOGOUT, responseObserver);
    }

    /**
     */
    public void heartbeat(org.marketcetera.rpc.base.BaseRpc.HeartbeatRequest request,
        io.grpc.stub.StreamObserver<org.marketcetera.rpc.base.BaseRpc.HeartbeatResponse> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_HEARTBEAT, responseObserver);
    }

    @java.lang.Override public io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            METHOD_LOGIN,
            asyncUnaryCall(
              new MethodHandlers<
                org.marketcetera.rpc.base.BaseRpc.LoginRequest,
                org.marketcetera.rpc.base.BaseRpc.LoginResponse>(
                  this, METHODID_LOGIN)))
          .addMethod(
            METHOD_LOGOUT,
            asyncUnaryCall(
              new MethodHandlers<
                org.marketcetera.rpc.base.BaseRpc.LogoutRequest,
                org.marketcetera.rpc.base.BaseRpc.LogoutResponse>(
                  this, METHODID_LOGOUT)))
          .addMethod(
            METHOD_HEARTBEAT,
            asyncUnaryCall(
              new MethodHandlers<
                org.marketcetera.rpc.base.BaseRpc.HeartbeatRequest,
                org.marketcetera.rpc.base.BaseRpc.HeartbeatResponse>(
                  this, METHODID_HEARTBEAT)))
          .build();
    }
  }

  /**
   */
  public static final class BaseRpcServiceStub extends io.grpc.stub.AbstractStub<BaseRpcServiceStub> {
    private BaseRpcServiceStub(io.grpc.Channel channel) {
      super(channel);
    }

    private BaseRpcServiceStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected BaseRpcServiceStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new BaseRpcServiceStub(channel, callOptions);
    }

    /**
     */
    public void login(org.marketcetera.rpc.base.BaseRpc.LoginRequest request,
        io.grpc.stub.StreamObserver<org.marketcetera.rpc.base.BaseRpc.LoginResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_LOGIN, getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void logout(org.marketcetera.rpc.base.BaseRpc.LogoutRequest request,
        io.grpc.stub.StreamObserver<org.marketcetera.rpc.base.BaseRpc.LogoutResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_LOGOUT, getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void heartbeat(org.marketcetera.rpc.base.BaseRpc.HeartbeatRequest request,
        io.grpc.stub.StreamObserver<org.marketcetera.rpc.base.BaseRpc.HeartbeatResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_HEARTBEAT, getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class BaseRpcServiceBlockingStub extends io.grpc.stub.AbstractStub<BaseRpcServiceBlockingStub> {
    private BaseRpcServiceBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private BaseRpcServiceBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected BaseRpcServiceBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new BaseRpcServiceBlockingStub(channel, callOptions);
    }

    /**
     */
    public org.marketcetera.rpc.base.BaseRpc.LoginResponse login(org.marketcetera.rpc.base.BaseRpc.LoginRequest request) {
      return blockingUnaryCall(
          getChannel(), METHOD_LOGIN, getCallOptions(), request);
    }

    /**
     */
    public org.marketcetera.rpc.base.BaseRpc.LogoutResponse logout(org.marketcetera.rpc.base.BaseRpc.LogoutRequest request) {
      return blockingUnaryCall(
          getChannel(), METHOD_LOGOUT, getCallOptions(), request);
    }

    /**
     */
    public org.marketcetera.rpc.base.BaseRpc.HeartbeatResponse heartbeat(org.marketcetera.rpc.base.BaseRpc.HeartbeatRequest request) {
      return blockingUnaryCall(
          getChannel(), METHOD_HEARTBEAT, getCallOptions(), request);
    }
  }

  /**
   */
  public static final class BaseRpcServiceFutureStub extends io.grpc.stub.AbstractStub<BaseRpcServiceFutureStub> {
    private BaseRpcServiceFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private BaseRpcServiceFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected BaseRpcServiceFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new BaseRpcServiceFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<org.marketcetera.rpc.base.BaseRpc.LoginResponse> login(
        org.marketcetera.rpc.base.BaseRpc.LoginRequest request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_LOGIN, getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<org.marketcetera.rpc.base.BaseRpc.LogoutResponse> logout(
        org.marketcetera.rpc.base.BaseRpc.LogoutRequest request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_LOGOUT, getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<org.marketcetera.rpc.base.BaseRpc.HeartbeatResponse> heartbeat(
        org.marketcetera.rpc.base.BaseRpc.HeartbeatRequest request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_HEARTBEAT, getCallOptions()), request);
    }
  }

  private static final int METHODID_LOGIN = 0;
  private static final int METHODID_LOGOUT = 1;
  private static final int METHODID_HEARTBEAT = 2;

  private static class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final BaseRpcServiceImplBase serviceImpl;
    private final int methodId;

    public MethodHandlers(BaseRpcServiceImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_LOGIN:
          serviceImpl.login((org.marketcetera.rpc.base.BaseRpc.LoginRequest) request,
              (io.grpc.stub.StreamObserver<org.marketcetera.rpc.base.BaseRpc.LoginResponse>) responseObserver);
          break;
        case METHODID_LOGOUT:
          serviceImpl.logout((org.marketcetera.rpc.base.BaseRpc.LogoutRequest) request,
              (io.grpc.stub.StreamObserver<org.marketcetera.rpc.base.BaseRpc.LogoutResponse>) responseObserver);
          break;
        case METHODID_HEARTBEAT:
          serviceImpl.heartbeat((org.marketcetera.rpc.base.BaseRpc.HeartbeatRequest) request,
              (io.grpc.stub.StreamObserver<org.marketcetera.rpc.base.BaseRpc.HeartbeatResponse>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    return new io.grpc.ServiceDescriptor(SERVICE_NAME,
        METHOD_LOGIN,
        METHOD_LOGOUT,
        METHOD_HEARTBEAT);
  }

}
