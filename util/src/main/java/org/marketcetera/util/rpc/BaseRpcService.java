package org.marketcetera.util.rpc;

import java.util.Locale;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.rpc.BaseRpc.HeartbeatRequest;
import org.marketcetera.util.rpc.BaseRpc.HeartbeatResponse;
import org.marketcetera.util.rpc.BaseRpc.LoginRequest;
import org.marketcetera.util.rpc.BaseRpc.LoginResponse;
import org.marketcetera.util.rpc.BaseRpc.LogoutRequest;
import org.marketcetera.util.rpc.BaseRpc.LogoutResponse;
import org.marketcetera.util.ws.tags.SessionId;

import com.google.protobuf.RpcController;
import com.google.protobuf.ServiceException;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public abstract class BaseRpcService<SessionClazz>
        implements RpcServiceSpec<SessionClazz>,BaseRpc.BaseRpcService.BlockingInterface
{
    /* (non-Javadoc)
     * @see org.marketcetera.util.rpc.BaseRpc.BaseRpcService.BlockingInterface#login(com.google.protobuf.RpcController, org.marketcetera.util.rpc.BaseRpc.LoginRequest)
     */
    @Override
    public LoginResponse login(RpcController inController,
                               LoginRequest inRequest)
            throws ServiceException
    {
        SLF4JLoggerProxy.trace(this,
                               "{} received authentication request for {}", //$NON-NLS-1$
                               getDescription(),
                               inRequest.getUsername());
        BaseRpc.LoginResponse.Builder responseBuilder = BaseRpc.LoginResponse.newBuilder();
        BaseRpc.Status.Builder statusBuilder = BaseRpc.Status.newBuilder();
        statusBuilder.setFailed(false);
        try {
            SessionId sessionId = serverServices.login(new RpcCredentials(inRequest.getUsername(),
                                                                          inRequest.getPassword(),
                                                                          inRequest.getAppId(),
                                                                          inRequest.getClientId(),
                                                                          inRequest.getVersionId(),
                                                                          new Locale(inRequest.getLocale().getLanguage(),
                                                                                     inRequest.getLocale().getCountry(),
                                                                                     inRequest.getLocale().getVariant())));
            statusBuilder.setSessionId(sessionId.getValue());
            responseBuilder.setSessionId(sessionId.getValue());
        } catch (Exception e) {
            String message = ExceptionUtils.getRootCauseMessage(e);
            if(SLF4JLoggerProxy.isDebugEnabled(this)) {
                SLF4JLoggerProxy.warn(this,
                                      e,
                                      message);
            } else {
                SLF4JLoggerProxy.warn(this,
                                      message);
            }
            statusBuilder.setFailed(true);
            statusBuilder.setMessage(message);
            responseBuilder.setSessionId("null"); //$NON-NLS-1$
        }
        responseBuilder.setStatus(statusBuilder.build());
        BaseRpc.LoginResponse response = responseBuilder.build();
        SLF4JLoggerProxy.trace(this,
                               "Returning {} for {}",
                               response,
                               inRequest.getUsername());
        return response;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.util.rpc.BaseRpc.BaseRpcService.BlockingInterface#logout(com.google.protobuf.RpcController, org.marketcetera.util.rpc.BaseRpc.LogoutRequest)
     */
    @Override
    public LogoutResponse logout(RpcController inController,
                                 LogoutRequest inRequest)
            throws ServiceException
    {
        SLF4JLoggerProxy.trace(this,
                               "{} received logout request for {}", //$NON-NLS-1$
                               getDescription(),
                               inRequest.getSessionId());
        BaseRpc.LogoutResponse.Builder responseBuilder = BaseRpc.LogoutResponse.newBuilder();
        BaseRpc.Status.Builder statusBuilder = BaseRpc.Status.newBuilder();
        statusBuilder.setFailed(false);
        statusBuilder.setSessionId(inRequest.getSessionId());
        try {
            serverServices.logout(inRequest.getSessionId());
        } catch (Exception e) {
            if(SLF4JLoggerProxy.isDebugEnabled(this)) {
                SLF4JLoggerProxy.warn(this,
                                      e);
            } else {
                SLF4JLoggerProxy.warn(this,
                                      ExceptionUtils.getRootCauseMessage(e));
            }
            statusBuilder.setFailed(true);
            statusBuilder.setMessage(ExceptionUtils.getRootCauseMessage(e));
        }
        responseBuilder.setStatus(statusBuilder.build());
        BaseRpc.LogoutResponse response = responseBuilder.build();
        SLF4JLoggerProxy.trace(this,
                               "Returning {} for {}",
                               response,
                               inRequest.getSessionId());
        return response;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.util.rpc.BaseRpc.BaseRpcService.BlockingInterface#heartbeat(com.google.protobuf.RpcController, org.marketcetera.util.rpc.BaseRpc.HeartbeatRequest)
     */
    @Override
    public HeartbeatResponse heartbeat(RpcController inController,
                                       HeartbeatRequest inRequest)
            throws ServiceException
    {
        SLF4JLoggerProxy.trace(this,
                               "{} received hearbeat request: {}", //$NON-NLS-1$
                               getDescription(),
                               inRequest.getId());
        BaseRpc.HeartbeatResponse.Builder responseBuilder = BaseRpc.HeartbeatResponse.newBuilder();
        try {
            responseBuilder.setId(inRequest.getId());
        } catch (Exception e) {
            if(SLF4JLoggerProxy.isDebugEnabled(this)) {
                SLF4JLoggerProxy.warn(this,
                                      e);
            } else {
                SLF4JLoggerProxy.warn(this,
                                      ExceptionUtils.getRootCauseMessage(e));
            }
        }
        BaseRpc.HeartbeatResponse response = responseBuilder.build();
        SLF4JLoggerProxy.trace(this,
                               "Returning {}: {}",
                               response,
                               inRequest.getId());
        return response;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.util.rpc.RpcServiceSpec#setRpcServerServices(org.marketcetera.util.rpc.RpcServerServices)
     */
    @Override
    public void setRpcServerServices(RpcServerServices<SessionClazz> inServerServices)
    {
        serverServices = inServerServices;
    }
    /**
     * Get the serverServices value.
     *
     * @return a <code>RpcServerServices<SessionClazz></code> value
     */
    protected RpcServerServices<SessionClazz> getServerServices()
    {
        return serverServices;
    }
    /**
     * Sets the serverServices value.
     *
     * @param inServerServices a <code>RpcServerServices<SessionClazz></code> value
     */
    protected void setServerServices(RpcServerServices<SessionClazz> inServerServices)
    {
        serverServices = inServerServices;
    }
    /**
     * provides RPC Server services
     */
    private RpcServerServices<SessionClazz> serverServices;
}
