package org.marketcetera.rpc.sample;

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
    comments = "Source: rpc_sample.proto")
public class SampleRpcServiceGrpc {

  private SampleRpcServiceGrpc() {}

  public static final String SERVICE_NAME = "SampleRpcService";

  // Static method descriptors that strictly reflect the proto.
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<org.marketcetera.rpc.base.BaseRpc.LoginRequest,
      org.marketcetera.rpc.base.BaseRpc.LoginResponse> METHOD_LOGIN =
      io.grpc.MethodDescriptor.create(
          io.grpc.MethodDescriptor.MethodType.UNARY,
          generateFullMethodName(
              "SampleRpcService", "login"),
          io.grpc.protobuf.ProtoUtils.marshaller(org.marketcetera.rpc.base.BaseRpc.LoginRequest.getDefaultInstance()),
          io.grpc.protobuf.ProtoUtils.marshaller(org.marketcetera.rpc.base.BaseRpc.LoginResponse.getDefaultInstance()));
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<org.marketcetera.rpc.base.BaseRpc.LogoutRequest,
      org.marketcetera.rpc.base.BaseRpc.LogoutResponse> METHOD_LOGOUT =
      io.grpc.MethodDescriptor.create(
          io.grpc.MethodDescriptor.MethodType.UNARY,
          generateFullMethodName(
              "SampleRpcService", "logout"),
          io.grpc.protobuf.ProtoUtils.marshaller(org.marketcetera.rpc.base.BaseRpc.LogoutRequest.getDefaultInstance()),
          io.grpc.protobuf.ProtoUtils.marshaller(org.marketcetera.rpc.base.BaseRpc.LogoutResponse.getDefaultInstance()));
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<org.marketcetera.rpc.base.BaseRpc.HeartbeatRequest,
      org.marketcetera.rpc.base.BaseRpc.HeartbeatResponse> METHOD_HEARTBEAT =
      io.grpc.MethodDescriptor.create(
          io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING,
          generateFullMethodName(
              "SampleRpcService", "heartbeat"),
          io.grpc.protobuf.ProtoUtils.marshaller(org.marketcetera.rpc.base.BaseRpc.HeartbeatRequest.getDefaultInstance()),
          io.grpc.protobuf.ProtoUtils.marshaller(org.marketcetera.rpc.base.BaseRpc.HeartbeatResponse.getDefaultInstance()));

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static SampleRpcServiceStub newStub(io.grpc.Channel channel) {
    return new SampleRpcServiceStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static SampleRpcServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new SampleRpcServiceBlockingStub(channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary and streaming output calls on the service
   */
  public static SampleRpcServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new SampleRpcServiceFutureStub(channel);
  }

  /**
   */
  public static abstract class SampleRpcServiceImplBase implements io.grpc.BindableService {

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
            asyncServerStreamingCall(
              new MethodHandlers<
                org.marketcetera.rpc.base.BaseRpc.HeartbeatRequest,
                org.marketcetera.rpc.base.BaseRpc.HeartbeatResponse>(
                  this, METHODID_HEARTBEAT)))
          .build();
    }
  }

  /**
   */
  public static final class SampleRpcServiceStub extends io.grpc.stub.AbstractStub<SampleRpcServiceStub> {
    private SampleRpcServiceStub(io.grpc.Channel channel) {
      super(channel);
    }

    private SampleRpcServiceStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected SampleRpcServiceStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new SampleRpcServiceStub(channel, callOptions);
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
      asyncServerStreamingCall(
          getChannel().newCall(METHOD_HEARTBEAT, getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class SampleRpcServiceBlockingStub extends io.grpc.stub.AbstractStub<SampleRpcServiceBlockingStub> {
    private SampleRpcServiceBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private SampleRpcServiceBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected SampleRpcServiceBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new SampleRpcServiceBlockingStub(channel, callOptions);
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
    public java.util.Iterator<org.marketcetera.rpc.base.BaseRpc.HeartbeatResponse> heartbeat(
        org.marketcetera.rpc.base.BaseRpc.HeartbeatRequest request) {
      return blockingServerStreamingCall(
          getChannel(), METHOD_HEARTBEAT, getCallOptions(), request);
    }
  }

  /**
   */
  public static final class SampleRpcServiceFutureStub extends io.grpc.stub.AbstractStub<SampleRpcServiceFutureStub> {
    private SampleRpcServiceFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private SampleRpcServiceFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected SampleRpcServiceFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new SampleRpcServiceFutureStub(channel, callOptions);
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
  }

  private static final int METHODID_LOGIN = 0;
  private static final int METHODID_LOGOUT = 1;
  private static final int METHODID_HEARTBEAT = 2;

  private static class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final SampleRpcServiceImplBase serviceImpl;
    private final int methodId;

    public MethodHandlers(SampleRpcServiceImplBase serviceImpl, int methodId) {
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
