package com.marketcetera.admin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.marketcetera.core.ApplicationVersion;
import org.marketcetera.core.Util;
import org.marketcetera.core.VersionInfo;
import org.marketcetera.persist.CollectionPageResponse;
import org.marketcetera.persist.PageRequest;
import org.marketcetera.persist.PageResponse;
import org.marketcetera.persist.Sort;
import org.marketcetera.persist.SortDirection;
import org.marketcetera.rpc.BaseRpcClient;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.rpc.BaseRpc;
import org.marketcetera.util.rpc.BaseRpc.LoginRequest;
import org.marketcetera.util.rpc.BaseRpc.LoginResponse;
import org.marketcetera.util.rpc.BaseRpc.LogoutRequest;
import org.marketcetera.util.rpc.BaseRpc.LogoutResponse;
import org.marketcetera.util.ws.tags.AppId;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Lists;
import com.google.protobuf.RpcController;
import com.google.protobuf.ServiceException;
import com.googlecode.protobuf.pro.duplex.RpcClientChannel;
import com.marketcetera.admin.AdminRpc.AdminRpcService.BlockingInterface;
import com.marketcetera.admin.impl.SimpleFixSessionAttributeDescriptor;
import com.marketcetera.admin.impl.SimpleInstanceData;
import com.marketcetera.fix.ActiveFixSession;
import com.marketcetera.fix.FixSession;
import com.marketcetera.fix.FixSessionAttributeDescriptor;
import com.marketcetera.fix.FixSessionStatus;
import com.marketcetera.fix.SimpleFixSession;

/* $License$ */

/**
 * Provides an RPC-based {@link AdminClient} implementation.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class AdminRpcClient
        extends BaseRpcClient<AdminRpc.AdminRpcService.BlockingInterface>
        implements AdminClient
{
    /* (non-Javadoc)
     * @see com.marketcetera.admin.AdminClient#createFixSession(com.marketcetera.fix.FixSession)
     */
    @Override
    public FixSession createFixSession(FixSession inFixSession)
    {
        validateSession();
        AdminRpc.CreateFixSessionRequest.Builder requestBuilder = AdminRpc.CreateFixSessionRequest.newBuilder();
        try {
            SLF4JLoggerProxy.trace(this,
                                   "{} requesting create FIX session for {}",
                                   getSessionId(),
                                   inFixSession);
            requestBuilder.setSessionId(getSessionId().getValue());
            if(inFixSession != null) {
                AdminRpc.FixSession.Builder fixSessionBuilder = AdminRpc.FixSession.newBuilder();
                fixSessionBuilder.setAcceptor(inFixSession.isAcceptor());
                fixSessionBuilder.setAffinity(inFixSession.getAffinity());
                if(inFixSession.getBrokerId() != null) {
                    fixSessionBuilder.setBrokerId(inFixSession.getBrokerId());
                }
                if(inFixSession.getDescription() != null) {
                    fixSessionBuilder.setDescription(inFixSession.getDescription());
                }
                if(inFixSession.getHost() != null) {
                    fixSessionBuilder.setHost(inFixSession.getHost());
                }
                if(inFixSession.getName() != null) {
                    fixSessionBuilder.setName(inFixSession.getName());
                }
                fixSessionBuilder.setPort(inFixSession.getPort());
                if(inFixSession.getSessionId() != null) {
                    fixSessionBuilder.setSessionId(inFixSession.getSessionId());
                }
                BaseRpc.Properties.Builder propertiesBuilder = BaseRpc.Properties.newBuilder();
                for(Map.Entry<String,String> entry : inFixSession.getSessionSettings().entrySet()) {
                    BaseRpc.Property.Builder propertyBuilder = BaseRpc.Property.newBuilder();
                    if(entry.getKey() != null) {
                        propertyBuilder.setKey(entry.getKey());
                        propertyBuilder.setValue(entry.getValue());
                        propertiesBuilder.addProperty(propertyBuilder.build());
                    }
                    propertyBuilder.clear();
                }
                fixSessionBuilder.setSessionSettings(propertiesBuilder.build());
                requestBuilder.setFixSession(fixSessionBuilder.build());
            }
            AdminRpc.CreateFixSessionResponse response = getClientService().createFixSession(getController(),
                                                                                             requestBuilder.build());
            SimpleFixSession result = null;
            if(response.getStatus().getFailed()) {
                throw new RuntimeException(response.getStatus().getMessage());
            }
            if(response.hasFixSession()) {
                // TODO fields
            }
            SLF4JLoggerProxy.trace(this,
                                   "{} returning {}",
                                   getSessionId(),
                                   result);
            return result;
        } catch (ServiceException e) {
            throw new RuntimeException(e);
        }
    }
    /* (non-Javadoc)
     * @see com.marketcetera.admin.AdminRpcClient#readFixSessions()
     */
    @Override
    public List<ActiveFixSession> readFixSessions()
    {
        validateSession();
        AdminRpc.ReadFixSessionsRequest.Builder requestBuilder = AdminRpc.ReadFixSessionsRequest.newBuilder();
        try {
            SLF4JLoggerProxy.trace(this,
                                   "{} requesting FIX sessions",
                                   getSessionId());
            requestBuilder.setSessionId(getSessionId().getValue());
            AdminRpc.ReadFixSessionsResponse response = getClientService().readFixSessions(getController(),
                                                                                           requestBuilder.build());
            List<ActiveFixSession> results = new ArrayList<>();
            if(response.getStatus().getFailed()) {
                throw new RuntimeException(response.getStatus().getMessage());
            }
            for(AdminRpc.ActiveFixSession rpcFixSession : response.getFixSessionList()) {
                SimpleFixSession fixSession = new SimpleFixSession();
                if(rpcFixSession.getFixSession().hasAcceptor()) {
                    fixSession.setIsAcceptor(rpcFixSession.getFixSession().getAcceptor());
                }
                if(rpcFixSession.getFixSession().hasAffinity()) {
                    fixSession.setAffinity(rpcFixSession.getFixSession().getAffinity());
                }
                if(rpcFixSession.getFixSession().hasBrokerId()) {
                    fixSession.setBrokerId(rpcFixSession.getFixSession().getBrokerId());
                }
                if(rpcFixSession.getFixSession().hasDescription()) {
                    fixSession.setDescription(rpcFixSession.getFixSession().getDescription());
                }
                if(rpcFixSession.getFixSession().hasHost()) {
                    fixSession.setHost(rpcFixSession.getFixSession().getHost());
                }
                if(rpcFixSession.hasInstance()) {
                    fixSession.setInstance(rpcFixSession.getInstance());
                }
                if(rpcFixSession.hasSenderSeqNum()) {
                    fixSession.setSenderSequenceNumber(rpcFixSession.getSenderSeqNum());
                }
                if(rpcFixSession.hasTargetSeqNum()) {
                    fixSession.setTargetSequenceNumber(rpcFixSession.getTargetSeqNum());
                }
                if(rpcFixSession.getFixSession().hasName()) {
                    fixSession.setName(rpcFixSession.getFixSession().getName());
                }
                if(rpcFixSession.getFixSession().hasPort()) {
                    fixSession.setPort(rpcFixSession.getFixSession().getPort());
                }
                if(rpcFixSession.getFixSession().hasSessionId()) {
                    fixSession.setSessionId(rpcFixSession.getFixSession().getSessionId());
                }
                if(rpcFixSession.hasStatus()) {
                    fixSession.setStatus(FixSessionStatus.valueOf(rpcFixSession.getStatus()));
                }
                if(rpcFixSession.getFixSession().hasSessionSettings()) {
                    for(BaseRpc.Property property : rpcFixSession.getFixSession().getSessionSettings().getPropertyList()) {
                        String key = property.hasKey()?property.getKey():null;
                        String value = property.hasValue()?property.getValue():null;
                        if(key != null) {
                            fixSession.getSessionSettings().put(key,value);
                        }
                    }
                }
                results.add(fixSession);
            }
            SLF4JLoggerProxy.trace(this,
                                   "{} returning {}",
                                   getSessionId(),
                                   results);
            return results;
        } catch (Exception e) {
            SLF4JLoggerProxy.warn(this,
                                  e);
            if(e instanceof RuntimeException) {
                throw (RuntimeException)e;
            }
            throw new RuntimeException(e);
        }
    }
    /* (non-Javadoc)
     * @see com.marketcetera.admin.AdminClient#readFixSessions(org.marketcetera.core.PageRequest)
     */
    @Override
    public CollectionPageResponse<ActiveFixSession> readFixSessions(PageRequest inPageRequest)
    {
        validateSession();
        AdminRpc.ReadFixSessionsRequest.Builder requestBuilder = AdminRpc.ReadFixSessionsRequest.newBuilder();
        try {
            SLF4JLoggerProxy.trace(this,
                                   "{} requesting FIX sessions",
                                   getSessionId());
            requestBuilder.setSessionId(getSessionId().getValue());
            requestBuilder.setPage(buildPageRequest(inPageRequest));
            AdminRpc.ReadFixSessionsResponse response = getClientService().readFixSessions(getController(),
                                                                                           requestBuilder.build());
            List<ActiveFixSession> dataElements = new ArrayList<>();
            if(response.getStatus().getFailed()) {
                throw new RuntimeException(response.getStatus().getMessage());
            }
            for(AdminRpc.ActiveFixSession rpcFixSession : response.getFixSessionList()) {
                SimpleFixSession fixSession = new SimpleFixSession();
                if(rpcFixSession.getFixSession().hasAcceptor()) {
                    fixSession.setIsAcceptor(rpcFixSession.getFixSession().getAcceptor());
                }
                if(rpcFixSession.getFixSession().hasAffinity()) {
                    fixSession.setAffinity(rpcFixSession.getFixSession().getAffinity());
                }
                if(rpcFixSession.getFixSession().hasBrokerId()) {
                    fixSession.setBrokerId(rpcFixSession.getFixSession().getBrokerId());
                }
                if(rpcFixSession.getFixSession().hasDescription()) {
                    fixSession.setDescription(rpcFixSession.getFixSession().getDescription());
                }
                if(rpcFixSession.getFixSession().hasHost()) {
                    fixSession.setHost(rpcFixSession.getFixSession().getHost());
                }
                if(rpcFixSession.hasInstance()) {
                    fixSession.setInstance(rpcFixSession.getInstance());
                }
                if(rpcFixSession.hasSenderSeqNum()) {
                    fixSession.setSenderSequenceNumber(rpcFixSession.getSenderSeqNum());
                }
                if(rpcFixSession.hasTargetSeqNum()) {
                    fixSession.setTargetSequenceNumber(rpcFixSession.getTargetSeqNum());
                }
                if(rpcFixSession.getFixSession().hasName()) {
                    fixSession.setName(rpcFixSession.getFixSession().getName());
                }
                if(rpcFixSession.getFixSession().hasPort()) {
                    fixSession.setPort(rpcFixSession.getFixSession().getPort());
                }
                if(rpcFixSession.getFixSession().hasSessionId()) {
                    fixSession.setSessionId(rpcFixSession.getFixSession().getSessionId());
                }
                if(rpcFixSession.hasStatus()) {
                    fixSession.setStatus(FixSessionStatus.valueOf(rpcFixSession.getStatus()));
                }
                if(rpcFixSession.getFixSession().hasSessionSettings()) {
                    for(BaseRpc.Property property : rpcFixSession.getFixSession().getSessionSettings().getPropertyList()) {
                        String key = property.hasKey()?property.getKey():null;
                        String value = property.hasValue()?property.getValue():null;
                        if(key != null) {
                            fixSession.getSessionSettings().put(key,value);
                        }
                    }
                }
                dataElements.add(fixSession);
            }
            CollectionPageResponse<ActiveFixSession> result = new CollectionPageResponse<>();
            if(response.hasPage()) {
                addPageToResponse(response.getPage(),
                                  result);
            }
            result.setElements(dataElements);
            SLF4JLoggerProxy.trace(this,
                                   "{} returning {}",
                                   getSessionId(),
                                   result);
            return result;
        } catch (ServiceException e) {
            throw new RuntimeException(e);
        }
    }
    /* (non-Javadoc)
     * @see com.marketcetera.admin.AdminClient#updateFixSession(java.lang.String, com.marketcetera.fix.FixSession)
     */
    @Override
    public void updateFixSession(String inIncomingName,
                                 FixSession inFixSession)
    {
        validateSession();
        AdminRpc.UpdateFixSessionRequest.Builder requestBuilder = AdminRpc.UpdateFixSessionRequest.newBuilder();
        try {
            SLF4JLoggerProxy.trace(this,
                                   "{} update FIX session {}: {}",
                                   getSessionId(),
                                   inIncomingName,
                                   inFixSession);
            requestBuilder.setSessionId(getSessionId().getValue());
            requestBuilder.setName(inIncomingName);
            if(inFixSession != null) {
                AdminRpc.FixSession.Builder fixSessionBuilder = AdminRpc.FixSession.newBuilder();
                fixSessionBuilder.setAcceptor(inFixSession.isAcceptor());
                fixSessionBuilder.setAffinity(inFixSession.getAffinity());
                if(inFixSession.getBrokerId() != null) {
                    fixSessionBuilder.setBrokerId(inFixSession.getBrokerId());
                }
                if(inFixSession.getDescription() != null) {
                    fixSessionBuilder.setDescription(inFixSession.getDescription());
                }
                if(inFixSession.getHost() != null) {
                    fixSessionBuilder.setHost(inFixSession.getHost());
                }
                if(inFixSession.getName() != null) {
                    fixSessionBuilder.setName(inFixSession.getName());
                }
                fixSessionBuilder.setPort(inFixSession.getPort());
                if(inFixSession.getSessionId() != null) {
                    fixSessionBuilder.setSessionId(inFixSession.getSessionId());
                }
                BaseRpc.Properties.Builder propertiesBuilder = BaseRpc.Properties.newBuilder();
                for(Map.Entry<String,String> entry : inFixSession.getSessionSettings().entrySet()) {
                    BaseRpc.Property.Builder propertyBuilder = BaseRpc.Property.newBuilder();
                    if(entry.getKey() != null) {
                        propertyBuilder.setKey(entry.getKey());
                        propertyBuilder.setValue(entry.getValue());
                        propertiesBuilder.addProperty(propertyBuilder.build());
                    }
                    propertyBuilder.clear();
                }
                fixSessionBuilder.setSessionSettings(propertiesBuilder.build());
                requestBuilder.setFixSession(fixSessionBuilder.build());
            }
            AdminRpc.UpdateFixSessionResponse response = getClientService().updateFixSession(getController(),
                                                                                             requestBuilder.build());
            if(response.getStatus().getFailed()) {
                throw new RuntimeException(response.getStatus().getMessage());
            }
        } catch (Exception e) {
            SLF4JLoggerProxy.warn(this,
                                  e);
            if(e instanceof RuntimeException) {
                throw (RuntimeException)e;
            }
            throw new RuntimeException(e);
        }
    }
    /* (non-Javadoc)
     * @see com.marketcetera.admin.AdminClient#enableFixSession(java.lang.String)
     */
    @Override
    public void enableFixSession(String inName)
    {
        validateSession();
        AdminRpc.EnableFixSessionRequest.Builder requestBuilder = AdminRpc.EnableFixSessionRequest.newBuilder();
        try {
            SLF4JLoggerProxy.trace(this,
                                   "{} enable FIX session {}",
                                   getSessionId(),
                                   inName);
            requestBuilder.setSessionId(getSessionId().getValue());
            requestBuilder.setName(inName);
            AdminRpc.EnableFixSessionResponse response = getClientService().enableFixSession(getController(),
                                                                                             requestBuilder.build());
            if(response.getStatus().getFailed()) {
                throw new RuntimeException(response.getStatus().getMessage());
            }
        } catch (Exception e) {
            SLF4JLoggerProxy.warn(this,
                                  e);
            if(e instanceof RuntimeException) {
                throw (RuntimeException)e;
            }
            throw new RuntimeException(e);
        }
    }
    /* (non-Javadoc)
     * @see com.marketcetera.admin.AdminClient#disableFixSession(java.lang.String)
     */
    @Override
    public void disableFixSession(String inName)
    {
        validateSession();
        AdminRpc.DisableFixSessionRequest.Builder requestBuilder = AdminRpc.DisableFixSessionRequest.newBuilder();
        try {
            SLF4JLoggerProxy.trace(this,
                                   "{} disable FIX session {}",
                                   getSessionId(),
                                   inName);
            requestBuilder.setSessionId(getSessionId().getValue());
            requestBuilder.setName(inName);
            AdminRpc.DisableFixSessionResponse response = getClientService().disableFixSession(getController(),
                                                                                               requestBuilder.build());
            if(response.getStatus().getFailed()) {
                throw new RuntimeException(response.getStatus().getMessage());
            }
        } catch (Exception e) {
            SLF4JLoggerProxy.warn(this,
                                  e);
            if(e instanceof RuntimeException) {
                throw (RuntimeException)e;
            }
            throw new RuntimeException(e);
        }
    }
    /* (non-Javadoc)
     * @see com.marketcetera.admin.AdminClient#deleteFixSession(java.lang.String)
     */
    @Override
    public void deleteFixSession(String inName)
    {
        validateSession();
        AdminRpc.DeleteFixSessionRequest.Builder requestBuilder = AdminRpc.DeleteFixSessionRequest.newBuilder();
        try {
            SLF4JLoggerProxy.trace(this,
                                   "{} delete FIX session {}",
                                   getSessionId(),
                                   inName);
            requestBuilder.setSessionId(getSessionId().getValue());
            requestBuilder.setName(inName);
            AdminRpc.DeleteFixSessionResponse response = getClientService().deleteFixSession(getController(),
                                                                                             requestBuilder.build());
            if(response.getStatus().getFailed()) {
                throw new RuntimeException(response.getStatus().getMessage());
            }
        } catch (Exception e) {
            SLF4JLoggerProxy.warn(this,
                                  e);
            if(e instanceof RuntimeException) {
                throw (RuntimeException)e;
            }
            throw new RuntimeException(e);
        }
    }
    /* (non-Javadoc)
     * @see com.marketcetera.admin.AdminClient#stopFixSession(java.lang.String)
     */
    @Override
    public void stopFixSession(String inName)
    {
        validateSession();
        AdminRpc.StopFixSessionRequest.Builder requestBuilder = AdminRpc.StopFixSessionRequest.newBuilder();
        try {
            SLF4JLoggerProxy.trace(this,
                                   "{} stop FIX session {}",
                                   getSessionId(),
                                   inName);
            requestBuilder.setSessionId(getSessionId().getValue());
            requestBuilder.setName(inName);
            AdminRpc.StopFixSessionResponse response = getClientService().stopFixSession(getController(),
                                                                                         requestBuilder.build());
            if(response.getStatus().getFailed()) {
                throw new RuntimeException(response.getStatus().getMessage());
            }
        } catch (Exception e) {
            SLF4JLoggerProxy.warn(this,
                                  e);
            if(e instanceof RuntimeException) {
                throw (RuntimeException)e;
            }
            throw new RuntimeException(e);
        }
    }
    /* (non-Javadoc)
     * @see com.marketcetera.admin.AdminClient#startFixSession(java.lang.String)
     */
    @Override
    public void startFixSession(String inName)
    {
        validateSession();
        AdminRpc.StartFixSessionRequest.Builder requestBuilder = AdminRpc.StartFixSessionRequest.newBuilder();
        try {
            SLF4JLoggerProxy.trace(this,
                                   "{} start FIX session {}",
                                   getSessionId(),
                                   inName);
            requestBuilder.setSessionId(getSessionId().getValue());
            requestBuilder.setName(inName);
            AdminRpc.StartFixSessionResponse response = getClientService().startFixSession(getController(),
                                                                                           requestBuilder.build());
            if(response.getStatus().getFailed()) {
                throw new RuntimeException(response.getStatus().getMessage());
            }
        } catch (Exception e) {
            SLF4JLoggerProxy.warn(this,
                                  e);
            if(e instanceof RuntimeException) {
                throw (RuntimeException)e;
            }
            throw new RuntimeException(e);
        }
    }
    /* (non-Javadoc)
     * @see com.marketcetera.admin.AdminClient#updateSequenceNumbers(java.lang.String, int, int)
     */
    @Override
    public void updateSequenceNumbers(String inSessionName,
                                      int inSenderSequenceNumber,
                                      int inTargetSequenceNumber)
    {
        validateSession();
        AdminRpc.UpdateSequenceNumbersRequest.Builder requestBuilder = AdminRpc.UpdateSequenceNumbersRequest.newBuilder();
        try {
            SLF4JLoggerProxy.trace(this,
                                   "{} update sequence numbers for FIX session {} to {}:{}",
                                   getSessionId(),
                                   inSessionName,
                                   inSenderSequenceNumber,
                                   inTargetSequenceNumber);
            requestBuilder.setSessionId(getSessionId().getValue());
            requestBuilder.setName(inSessionName);
            requestBuilder.setSenderSequenceNumber(inSenderSequenceNumber);
            requestBuilder.setTargetSequenceNumber(inTargetSequenceNumber);
            AdminRpc.UpdateSequenceNumbersResponse response = getClientService().updateSequenceNumbers(getController(),
                                                                                                       requestBuilder.build());
            if(response.getStatus().getFailed()) {
                throw new RuntimeException(response.getStatus().getMessage());
            }
        } catch (Exception e) {
            SLF4JLoggerProxy.warn(this,
                                  e);
            if(e instanceof RuntimeException) {
                throw (RuntimeException)e;
            }
            throw new RuntimeException(e);
        }
    }
    /* (non-Javadoc)
     * @see com.marketcetera.admin.AdminClient#changeSenderSequenceNumber(java.lang.String, int)
     */
    @Override
    public void updateSenderSequenceNumber(String inSessionName,
                                           int inSenderSequenceNumber)
    {
        validateSession();
        AdminRpc.UpdateSequenceNumbersRequest.Builder requestBuilder = AdminRpc.UpdateSequenceNumbersRequest.newBuilder();
        try {
            SLF4JLoggerProxy.trace(this,
                                   "{} update sender sequence number for FIX session {} to {}",
                                   getSessionId(),
                                   inSessionName,
                                   inSenderSequenceNumber);
            requestBuilder.setSessionId(getSessionId().getValue());
            requestBuilder.setName(inSessionName);
            requestBuilder.setSenderSequenceNumber(inSenderSequenceNumber);
            AdminRpc.UpdateSequenceNumbersResponse response = getClientService().updateSequenceNumbers(getController(),
                                                                                                       requestBuilder.build());
            if(response.getStatus().getFailed()) {
                throw new RuntimeException(response.getStatus().getMessage());
            }
        } catch (Exception e) {
            SLF4JLoggerProxy.warn(this,
                                  e);
            if(e instanceof RuntimeException) {
                throw (RuntimeException)e;
            }
            throw new RuntimeException(e);
        }
    }
    /* (non-Javadoc)
     * @see com.marketcetera.admin.AdminClient#changeTargetSequenceNumber(java.lang.String, int)
     */
    @Override
    public void updateTargetSequenceNumber(String inSessionName,
                                           int inTargetSequenceNumber)
    {
        validateSession();
        AdminRpc.UpdateSequenceNumbersRequest.Builder requestBuilder = AdminRpc.UpdateSequenceNumbersRequest.newBuilder();
        try {
            SLF4JLoggerProxy.trace(this,
                                   "{} update target sequence number for FIX session {} to {}",
                                   getSessionId(),
                                   inSessionName,
                                   inTargetSequenceNumber);
            requestBuilder.setSessionId(getSessionId().getValue());
            requestBuilder.setName(inSessionName);
            requestBuilder.setTargetSequenceNumber(inTargetSequenceNumber);
            AdminRpc.UpdateSequenceNumbersResponse response = getClientService().updateSequenceNumbers(getController(),
                                                                                                       requestBuilder.build());
            if(response.getStatus().getFailed()) {
                throw new RuntimeException(response.getStatus().getMessage());
            }
        } catch (Exception e) {
            SLF4JLoggerProxy.warn(this,
                                  e);
            if(e instanceof RuntimeException) {
                throw (RuntimeException)e;
            }
            throw new RuntimeException(e);
        }
    }
    /* (non-Javadoc)
     * @see com.marketcetera.admin.AdminClient#getInstanceData(int)
     */
    @Override
    public InstanceData getInstanceData(int inAffinity)
    {
        validateSession();
        AdminRpc.InstanceDataRequest.Builder requestBuilder = AdminRpc.InstanceDataRequest.newBuilder();
        try {
            SLF4JLoggerProxy.trace(this,
                                   "{} requesting instance data",
                                   getSessionId());
            requestBuilder.setSessionId(getSessionId().getValue());
            requestBuilder.setAffinity(inAffinity);
            AdminRpc.InstanceDataResponse response = getClientService().getInstanceData(getController(),
                                                                                        requestBuilder.build());
            if(response.getStatus().getFailed()) {
                throw new RuntimeException(response.getStatus().getMessage());
            }
            SimpleInstanceData result = new SimpleInstanceData();
            if(response.hasInstanceData()) {
                AdminRpc.InstanceData rpcInstanceData = response.getInstanceData();
                if(rpcInstanceData.hasHostname()) {
                    result.setHostname(rpcInstanceData.getHostname());
                }
                result.setPort(rpcInstanceData.getPort());
            }
            SLF4JLoggerProxy.trace(this,
                                   "{} returning {}",
                                   getSessionId(),
                                   result);
            return result;
        } catch (ServiceException e) {
            throw new RuntimeException(e);
        }
        
    }
    /* (non-Javadoc)
     * @see com.marketcetera.admin.AdminClient#getFixSessionAttributeDescriptors()
     */
    @Override
    public Collection<FixSessionAttributeDescriptor> getFixSessionAttributeDescriptors()
    {
        validateSession();
        AdminRpc.ReadFixSessionAttributeDescriptorsRequest.Builder requestBuilder = AdminRpc.ReadFixSessionAttributeDescriptorsRequest.newBuilder();
        try {
            SLF4JLoggerProxy.trace(this,
                                   "{} requesting FIX session attribute descriptors",
                                   getSessionId());
            requestBuilder.setSessionId(getSessionId().getValue());
            AdminRpc.ReadFixSessionAttributeDescriptorsResponse response = getClientService().readFixSessionAttributeDescriptors(getController(),
                                                                                                                                 requestBuilder.build());
            Collection<FixSessionAttributeDescriptor> results = new ArrayList<>();
            if(response.getStatus().getFailed()) {
                throw new RuntimeException(response.getStatus().getMessage());
            }
            for(AdminRpc.FixSessionAttributeDescriptor rpcDescriptor : response.getFixSessionAttributeDescriptorsList()) {
                SimpleFixSessionAttributeDescriptor descriptor = new SimpleFixSessionAttributeDescriptor();
                if(rpcDescriptor.hasAdvice()) {
                    descriptor.setAdvice(rpcDescriptor.getAdvice());
                }
                if(rpcDescriptor.hasDefaultValue()) {
                    descriptor.setDefaultValue(rpcDescriptor.getDefaultValue());
                }
                if(rpcDescriptor.hasDescription()) {
                    descriptor.setDescription(rpcDescriptor.getDescription());
                }
                if(rpcDescriptor.hasName()) {
                    descriptor.setName(rpcDescriptor.getName());
                }
                if(rpcDescriptor.hasPattern()) {
                    descriptor.setPattern(rpcDescriptor.getPattern());
                }
                if(rpcDescriptor.hasRequired()) {
                    descriptor.setRequired(rpcDescriptor.getRequired());
                }
                results.add(descriptor);
            }
            SLF4JLoggerProxy.trace(this,
                                   "{} returning {}",
                                   getSessionId(),
                                   results);
            return results;
        } catch (ServiceException e) {
            throw new RuntimeException(e);
        }
    }
    /* (non-Javadoc)
     * @see com.marketcetera.admin.AdminRpcClient#getPermissionsForUsername()
     */
    @Override
    public Set<String> getPermissionsForCurrentUser()
    {
        validateSession();
        AdminRpc.PermissionsForUsernameRequest.Builder requestBuilder = AdminRpc.PermissionsForUsernameRequest.newBuilder();
        try {
            SLF4JLoggerProxy.trace(this,
                                   "{} requesting permissions",
                                   getSessionId());
            requestBuilder.setSessionId(getSessionId().getValue());
            AdminRpc.PermissionsForUsernameResponse response = getClientService().getPermissionsForUsername(getController(),
                                                                                                       requestBuilder.build());
            Set<String> results = new HashSet<>();
            if(response.getStatus().getFailed()) {
                throw new RuntimeException(response.getStatus().getMessage());
            }
            results.addAll(response.getPermissionsList());
            SLF4JLoggerProxy.trace(this,
                                   "{} returning {}",
                                   getSessionId(),
                                   results);
            return results;
        } catch (ServiceException e) {
            throw new RuntimeException(e);
        }
    }
    /* (non-Javadoc)
     * @see com.marketcetera.admin.AdminRpcClient#changeUserPassword(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public void changeUserPassword(String inUsername,
                                   String inOldPassword,
                                   String inNewPassword)
    {
        validateSession();
        AdminRpc.ChangeUserPasswordRequest.Builder requestBuilder = AdminRpc.ChangeUserPasswordRequest.newBuilder();
        try {
            SLF4JLoggerProxy.trace(this,
                                   "{} requesting change user password for {}",
                                   getSessionId(),
                                   inUsername);
            requestBuilder.setSessionId(getSessionId().getValue());
            inUsername = StringUtils.trimToNull(inUsername);
            if(inUsername != null) {
                requestBuilder.setUsername(inUsername);
            }
            inOldPassword = StringUtils.trimToNull(inOldPassword);
            if(inOldPassword != null) {
                requestBuilder.setOldPassword(inOldPassword);
            }
            inNewPassword = StringUtils.trimToNull(inNewPassword);
            if(inOldPassword != null) {
                requestBuilder.setNewPassword(inNewPassword);
            }
            AdminRpc.ChangeUserPasswordResponse response = getClientService().changeUserPassword(getController(),
                                                                                            requestBuilder.build());
            if(response.getStatus().getFailed()) {
                throw new RuntimeException(response.getStatus().getMessage());
            }
            SLF4JLoggerProxy.trace(this,
                                   "{} change password for {} succeeded",
                                   getSessionId(),
                                   inUsername);
        } catch (ServiceException e) {
            throw new RuntimeException(e);
        }
    }
    /* (non-Javadoc)
     * @see com.marketcetera.admin.AdminClient#createUser(com.marketcetera.admin.User, java.lang.String)
     */
    @Override
    public User createUser(User inNewUser,
                           String inPassword)
    {
        validateSession();
        AdminRpc.CreateUserRequest.Builder requestBuilder = AdminRpc.CreateUserRequest.newBuilder();
        try {
            SLF4JLoggerProxy.trace(this,
                                   "{} requesting create user for {}",
                                   getSessionId(),
                                   inNewUser);
            requestBuilder.setSessionId(getSessionId().getValue());
            if(inNewUser != null) {
                AdminRpc.User.Builder userBuilder = AdminRpc.User.newBuilder();
                userBuilder.setActive(inNewUser.isActive());
                if(inNewUser.getDescription() != null) {
                    userBuilder.setDescription(inNewUser.getDescription());
                }
                if(inNewUser.getName() != null) {
                    userBuilder.setName(inNewUser.getName());
                }
                requestBuilder.setUser(userBuilder.build());
            }
            inPassword = StringUtils.trimToNull(inPassword);
            if(inPassword != null) {
                requestBuilder.setPassword(inPassword);
            }
            AdminRpc.CreateUserResponse response = getClientService().createUser(getController(),
                                                                                 requestBuilder.build());
            User result = null;
            if(response.getStatus().getFailed()) {
                throw new RuntimeException(response.getStatus().getMessage());
            }
            if(response.hasUser()) {
                AdminRpc.User rpcUser = response.getUser();
                boolean isActive = false;
                String description = null;
                String name = null;
                String password = "********";
                if(rpcUser.hasActive()) {
                    isActive = rpcUser.getActive();
                }
                if(rpcUser.hasDescription()) {
                    description = rpcUser.getDescription();
                }
                if(rpcUser.hasName()) {
                    name = rpcUser.getName();
                }
                result = userFactory.create(name,
                                            password,
                                            description,
                                            isActive);
            }
            SLF4JLoggerProxy.trace(this,
                                   "{} returning {}",
                                   getSessionId(),
                                   result);
            return result;
        } catch (ServiceException e) {
            throw new RuntimeException(e);
        }
    }
    /* (non-Javadoc)
     * @see com.marketcetera.admin.AdminClient#updateUser(java.lang.String, com.marketcetera.admin.User)
     */
    @Override
    public User updateUser(String inUsername,
                           User inUpdatedUser)
    {
        validateSession();
        AdminRpc.UpdateUserRequest.Builder requestBuilder = AdminRpc.UpdateUserRequest.newBuilder();
        try {
            SLF4JLoggerProxy.trace(this,
                                   "{} requesting user update for {}: {}",
                                   getSessionId(),
                                   inUsername,
                                   inUpdatedUser);
            requestBuilder.setSessionId(getSessionId().getValue());
            inUsername = StringUtils.trimToNull(inUsername);
            if(inUsername != null) {
                requestBuilder.setUsername(inUsername);
            }
            if(inUpdatedUser != null) {
                AdminRpc.User.Builder userBuilder = AdminRpc.User.newBuilder();
                userBuilder.setActive(inUpdatedUser.isActive());
                if(inUpdatedUser.getDescription() != null) {
                    userBuilder.setDescription(inUpdatedUser.getDescription());
                }
                if(inUpdatedUser.getName() != null) {
                    userBuilder.setName(inUpdatedUser.getName());
                }
                requestBuilder.setUser(userBuilder.build());
            }
            AdminRpc.UpdateUserResponse response = getClientService().updateUser(getController(),
                                                                            requestBuilder.build());
            User result = null;
            if(response.getStatus().getFailed()) {
                throw new RuntimeException(response.getStatus().getMessage());
            }
            if(response.hasUser()) {
                AdminRpc.User rpcUser = response.getUser();
                boolean isActive = false;
                String description = null;
                String name = null;
                String password = "********";
                if(rpcUser.hasActive()) {
                    isActive = rpcUser.getActive();
                }
                if(rpcUser.hasDescription()) {
                    description = rpcUser.getDescription();
                }
                if(rpcUser.hasName()) {
                    name = rpcUser.getName();
                }
                result = userFactory.create(name,
                                            password,
                                            description,
                                            isActive);
            }
            SLF4JLoggerProxy.trace(this,
                                   "{} returning {}",
                                   getSessionId(),
                                   result);
            return result;
        } catch (ServiceException e) {
            throw new RuntimeException(e);
        }
    }
    /* (non-Javadoc)
     * @see com.marketcetera.admin.AdminRpcClient#deleteUser(java.lang.String)
     */
    @Override
    public void deleteUser(String inUsername)
    {
        validateSession();
        AdminRpc.DeleteUserRequest.Builder requestBuilder = AdminRpc.DeleteUserRequest.newBuilder();
        try {
            SLF4JLoggerProxy.trace(this,
                                   "{} requesting user delete for {}",
                                   getSessionId(),
                                   inUsername);
            requestBuilder.setSessionId(getSessionId().getValue());
            inUsername = StringUtils.trimToNull(inUsername);
            if(inUsername != null) {
                requestBuilder.setUsername(inUsername);
            }
            AdminRpc.DeleteUserResponse response = getClientService().deleteUser(getController(),
                                                                            requestBuilder.build());
            if(response.getStatus().getFailed()) {
                throw new RuntimeException(response.getStatus().getMessage());
            }
            SLF4JLoggerProxy.trace(this,
                                   "{} delete user for {} succeeded",
                                   getSessionId(),
                                   inUsername);
        } catch (ServiceException e) {
            throw new RuntimeException(e);
        }
    }
    /* (non-Javadoc)
     * @see com.marketcetera.admin.AdminClient#deactivateUser(java.lang.String)
     */
    @Override
    public void deactivateUser(String inName)
    {
        validateSession();
        AdminRpc.DeactivateUserRequest.Builder requestBuilder = AdminRpc.DeactivateUserRequest.newBuilder();
        try {
            SLF4JLoggerProxy.trace(this,
                                   "{} requesting deactivate user for {}",
                                   getSessionId(),
                                   inName);
            requestBuilder.setSessionId(getSessionId().getValue());
            inName = StringUtils.trimToNull(inName);
            if(inName != null) {
                requestBuilder.setUsername(inName);
            }
            AdminRpc.DeactivateUserResponse response = getClientService().deactivateUser(getController(),
                                                                                         requestBuilder.build());
            if(response.getStatus().getFailed()) {
                throw new RuntimeException(response.getStatus().getMessage());
            }
            SLF4JLoggerProxy.trace(this,
                                   "{} deactivate user for {} succeeded",
                                   getSessionId(),
                                   inName);
        } catch (ServiceException e) {
            throw new RuntimeException(e);
        }
    }
    /* (non-Javadoc)
     * @see com.marketcetera.admin.AdminClient#createRole(com.marketcetera.admin.Role)
     */
    @Override
    public Role createRole(Role inRole)
    {
        validateSession();
        AdminRpc.CreateRoleRequest.Builder requestBuilder = AdminRpc.CreateRoleRequest.newBuilder();
        try {
            SLF4JLoggerProxy.trace(this,
                                   "{} requesting create role for {}",
                                   getSessionId(),
                                   inRole);
            requestBuilder.setSessionId(getSessionId().getValue());
            if(inRole != null) {
                AdminRpc.Role.Builder roleBuilder = AdminRpc.Role.newBuilder();
                if(inRole.getDescription() != null) {
                    roleBuilder.setDescription(inRole.getDescription());
                }
                if(inRole.getName() != null) {
                    roleBuilder.setName(inRole.getName());
                }
                for(Permission permission : inRole.getPermissions()) {
                    requestBuilder.addPermissionName(permission.getName());
                }
                for(User user : inRole.getSubjects()) {
                    requestBuilder.addUsername(user.getName());
                }
                requestBuilder.setRole(roleBuilder.build());
            }
            AdminRpc.CreateRoleResponse response = getClientService().createRole(getController(),
                                                                                 requestBuilder.build());
            Role result = null;
            if(response.getStatus().getFailed()) {
                throw new RuntimeException(response.getStatus().getMessage());
            }
            if(response.hasRole()) {
                AdminRpc.Role rpcRole = response.getRole();
                String name = null;
                String description = null;
                if(rpcRole.hasDescription()) {
                    description = rpcRole.getDescription();
                }
                if(rpcRole.hasName()) {
                    name = rpcRole.getName();
                }
                result = roleFactory.create(name,
                                            description);
                for(AdminRpc.Permission rpcPermission : rpcRole.getPermissionList()) {
                    description = null;
                    name = null;
                    if(rpcPermission.hasDescription()) {
                        description = rpcPermission.getDescription();
                    }
                    if(rpcPermission.hasName()) {
                        name = rpcPermission.getName();
                    }
                    result.getPermissions().add(permissionFactory.create(name,
                                                                         description));
                }
                for(AdminRpc.User rpcUser : rpcRole.getUserList()) {
                    description = null;
                    name = null;
                    if(rpcUser.hasDescription()) {
                        description = rpcUser.getDescription();
                    }
                    if(rpcUser.hasName()) {
                        name = rpcUser.getName();
                    }
                    result.getSubjects().add(userFactory.create(name,
                                                                "********",
                                                                description,
                                                                rpcUser.getActive()));
                }
            }
            SLF4JLoggerProxy.trace(this,
                                   "{} returning {}",
                                   getSessionId(),
                                   result);
            return result;
        } catch (ServiceException e) {
            throw new RuntimeException(e);
        }
    }
    /* (non-Javadoc)
     * @see com.marketcetera.admin.AdminClient#readRoles()
     */
    @Override
    public List<Role> readRoles()
    {
        validateSession();
        AdminRpc.ReadRolesRequest.Builder requestBuilder = AdminRpc.ReadRolesRequest.newBuilder();
        try {
            SLF4JLoggerProxy.trace(this,
                                   "{} requesting roles",
                                   getSessionId());
            requestBuilder.setSessionId(getSessionId().getValue());
            AdminRpc.ReadRolesResponse response = getClientService().readRoles(getController(),
                                                                               requestBuilder.build());
            List<Role> results = new ArrayList<>();
            if(response.getStatus().getFailed()) {
                throw new RuntimeException(response.getStatus().getMessage());
            }
            for(AdminRpc.Role rpcRole : response.getRoleList()) {
                String description = null;
                String name = null;
                if(rpcRole.hasDescription()) {
                    description = rpcRole.getDescription();
                }
                if(rpcRole.hasName()) {
                    name = rpcRole.getName();
                }
                Role role = roleFactory.create(name,
                                               description);
                for(AdminRpc.Permission rpcPermission : rpcRole.getPermissionList()) {
                    description = null;
                    name = null;
                    if(rpcPermission.hasDescription()) {
                        description = rpcPermission.getDescription();
                    }
                    if(rpcPermission.hasName()) {
                        name = rpcPermission.getName();
                    }
                    role.getPermissions().add(permissionFactory.create(name,
                                                                       description));
                }
                for(AdminRpc.User rpcUser : rpcRole.getUserList()) {
                    description = null;
                    name = null;
                    if(rpcUser.hasDescription()) {
                        description = rpcUser.getDescription();
                    }
                    if(rpcUser.hasName()) {
                        name = rpcUser.getName();
                    }
                    User subject = userFactory.create(name,
                                                      "********",
                                                      description,
                                                      rpcUser.getActive());
                    role.getSubjects().add(subject);
                }
                results.add(role);
            }
            SLF4JLoggerProxy.trace(this,
                                   "{} returning {}",
                                   getSessionId(),
                                   results);
            return results;
        } catch (ServiceException e) {
            throw new RuntimeException(e);
        }
    }
    /* (non-Javadoc)
     * @see com.marketcetera.admin.AdminClient#readRoles(org.marketcetera.core.PageRequest)
     */
    @Override
    public CollectionPageResponse<Role> readRoles(PageRequest inPageRequest)
    {
        validateSession();
        AdminRpc.ReadRolesRequest.Builder requestBuilder = AdminRpc.ReadRolesRequest.newBuilder();
        try {
            SLF4JLoggerProxy.trace(this,
                                   "{} requesting roles",
                                   getSessionId());
            requestBuilder.setSessionId(getSessionId().getValue());
            requestBuilder.setPage(buildPageRequest(inPageRequest));
            AdminRpc.ReadRolesResponse response = getClientService().readRoles(getController(),
                                                                               requestBuilder.build());
            List<Role> results = new ArrayList<>();
            if(response.getStatus().getFailed()) {
                throw new RuntimeException(response.getStatus().getMessage());
            }
            for(AdminRpc.Role rpcRole : response.getRoleList()) {
                String description = null;
                String name = null;
                if(rpcRole.hasDescription()) {
                    description = rpcRole.getDescription();
                }
                if(rpcRole.hasName()) {
                    name = rpcRole.getName();
                }
                Role role = roleFactory.create(name,
                                               description);
                for(AdminRpc.Permission rpcPermission : rpcRole.getPermissionList()) {
                    description = null;
                    name = null;
                    if(rpcPermission.hasDescription()) {
                        description = rpcPermission.getDescription();
                    }
                    if(rpcPermission.hasName()) {
                        name = rpcPermission.getName();
                    }
                    role.getPermissions().add(permissionFactory.create(name,
                                                                       description));
                }
                for(AdminRpc.User rpcUser : rpcRole.getUserList()) {
                    description = null;
                    name = null;
                    if(rpcUser.hasDescription()) {
                        description = rpcUser.getDescription();
                    }
                    if(rpcUser.hasName()) {
                        name = rpcUser.getName();
                    }
                    User subject = userFactory.create(name,
                                                      "********",
                                                      description,
                                                      rpcUser.getActive());
                    role.getSubjects().add(subject);
                }
                results.add(role);
            }
            CollectionPageResponse<Role> result = new CollectionPageResponse<>();
            if(response.hasPage()) {
                addPageToResponse(response.getPage(),
                                  result);
            }
            result.setElements(results);
            SLF4JLoggerProxy.trace(this,
                                   "{} returning {}",
                                   getSessionId(),
                                   result);
            return result;
        } catch (ServiceException e) {
            throw new RuntimeException(e);
        }
    }
    /* (non-Javadoc)
     * @see com.marketcetera.admin.AdminClient#updateRole(java.lang.String, com.marketcetera.admin.Role)
     */
    @Override
    public Role updateRole(String inName,
                           Role inRole)
    {
        validateSession();
        AdminRpc.UpdateRoleRequest.Builder requestBuilder = AdminRpc.UpdateRoleRequest.newBuilder();
        try {
            SLF4JLoggerProxy.trace(this,
                                   "{} requesting role update for {}: {}",
                                   getSessionId(),
                                   inName,
                                   inRole);
            requestBuilder.setSessionId(getSessionId().getValue());
            inName = StringUtils.trimToNull(inName);
            if(inName != null) {
                requestBuilder.setRoleName(inName);
            }
            if(inRole != null) {
                AdminRpc.Role.Builder roleBuilder = AdminRpc.Role.newBuilder();
                if(inRole.getDescription() != null) {
                    roleBuilder.setDescription(inRole.getDescription());
                }
                if(inRole.getName() != null) {
                    roleBuilder.setName(inRole.getName());
                }
                requestBuilder.setRole(roleBuilder.build());
                for(Permission permission : inRole.getPermissions()) {
                    requestBuilder.addPermissionName(permission.getName());
                }
                for(User user : inRole.getSubjects()) {
                    requestBuilder.addUsername(user.getName());
                }
            }
            AdminRpc.UpdateRoleResponse response = getClientService().updateRole(getController(),
                                                                                 requestBuilder.build());
            Role result = null;
            if(response.getStatus().getFailed()) {
                throw new RuntimeException(response.getStatus().getMessage());
            }
            if(response.hasRole()) {
                AdminRpc.Role rpcRole = response.getRole();
                String roleName = null;
                String description = null;
                if(rpcRole.hasDescription()) {
                    description = rpcRole.getDescription();
                }
                if(rpcRole.hasName()) {
                    roleName = rpcRole.getDescription();
                }
                result = roleFactory.create(roleName,
                                            description);
                for(AdminRpc.Permission rpcPermission : rpcRole.getPermissionList()) {
                    description = null;
                    String name = null;
                    if(rpcPermission.hasDescription()) {
                        description = rpcPermission.getDescription();
                    }
                    if(rpcPermission.hasName()) {
                        name = rpcPermission.getName();
                    }
                    result.getPermissions().add(permissionFactory.create(name,
                                                                         description));
                }
                for(AdminRpc.User rpcUser : rpcRole.getUserList()) {
                    description = null;
                    String name = null;
                    if(rpcUser.hasDescription()) {
                        description = rpcUser.getDescription();
                    }
                    if(rpcUser.hasName()) {
                        name = rpcUser.getName();
                    }
                    result.getSubjects().add(userFactory.create(name,
                                                                "********",
                                                                description,
                                                                rpcUser.getActive()));
                }
            }
            SLF4JLoggerProxy.trace(this,
                                   "{} returning {}",
                                   getSessionId(),
                                   result);
            return result;
        } catch (ServiceException e) {
            throw new RuntimeException(e);
        }
    }
    /* (non-Javadoc)
     * @see com.marketcetera.admin.AdminClient#deleteRole(java.lang.String)
     */
    @Override
    public void deleteRole(String inName)
    {
        validateSession();
        AdminRpc.DeleteRoleRequest.Builder requestBuilder = AdminRpc.DeleteRoleRequest.newBuilder();
        try {
            SLF4JLoggerProxy.trace(this,
                                   "{} requesting role delete for {}",
                                   getSessionId(),
                                   inName);
            requestBuilder.setSessionId(getSessionId().getValue());
            inName = StringUtils.trimToNull(inName);
            if(inName != null) {
                requestBuilder.setRoleName(inName);
            }
            AdminRpc.DeleteRoleResponse response = getClientService().deleteRole(getController(),
                                                                                 requestBuilder.build());
            if(response.getStatus().getFailed()) {
                throw new RuntimeException(response.getStatus().getMessage());
            }
            SLF4JLoggerProxy.trace(this,
                                   "{} delete role for {} succeeded",
                                   getSessionId(),
                                   inName);
        } catch (ServiceException e) {
            throw new RuntimeException(e);
        }
    }
    /* (non-Javadoc)
     * @see com.marketcetera.admin.AdminRpcClient#getUsers()
     */
    @Override
    public List<User> readUsers()
    {
        validateSession();
        AdminRpc.ReadUsersRequest.Builder requestBuilder = AdminRpc.ReadUsersRequest.newBuilder();
        try {
            SLF4JLoggerProxy.trace(this,
                                   "{} requesting users",
                                   getSessionId());
            requestBuilder.setSessionId(getSessionId().getValue());
            AdminRpc.ReadUsersResponse response = getClientService().readUsers(getController(),
                                                                               requestBuilder.build());
            List<User> results = new ArrayList<>();
            if(response.getStatus().getFailed()) {
                throw new RuntimeException(response.getStatus().getMessage());
            }
            for(AdminRpc.User rpcUser : response.getUserList()) {
                results.add(createUserFrom(rpcUser));
            }
            SLF4JLoggerProxy.trace(this,
                                   "{} returning {}",
                                   getSessionId(),
                                   results);
            return results;
        } catch (ServiceException e) {
            throw new RuntimeException(e);
        }
    }
    /* (non-Javadoc)
     * @see com.marketcetera.admin.AdminClient#readUsers(org.marketcetera.core.PageRequest)
     */
    @Override
    public CollectionPageResponse<User> readUsers(PageRequest inPageRequest)
    {
        validateSession();
        AdminRpc.ReadUsersRequest.Builder requestBuilder = AdminRpc.ReadUsersRequest.newBuilder();
        try {
            SLF4JLoggerProxy.trace(this,
                                   "{} requesting users",
                                   getSessionId());
            requestBuilder.setSessionId(getSessionId().getValue());
            requestBuilder.setPage(buildPageRequest(inPageRequest));
            AdminRpc.ReadUsersResponse response = getClientService().readUsers(getController(),
                                                                               requestBuilder.build());
            List<User> dataElements = new ArrayList<>();
            if(response.getStatus().getFailed()) {
                throw new RuntimeException(response.getStatus().getMessage());
            }
            for(AdminRpc.User rpcUser : response.getUserList()) {
                boolean isActive = false;
                String description = null;
                String name = null;
                String password = "********";
                if(rpcUser.hasActive()) {
                    isActive = rpcUser.getActive();
                }
                if(rpcUser.hasDescription()) {
                    description = rpcUser.getDescription();
                }
                if(rpcUser.hasName()) {
                    name = rpcUser.getName();
                }
                User user = userFactory.create(name,
                                               password,
                                               description,
                                               isActive);
                dataElements.add(user);
            }
            CollectionPageResponse<User> result = new CollectionPageResponse<>();
            if(response.hasPage()) {
                addPageToResponse(response.getPage(),
                                  result);
            }
            result.setElements(dataElements);
            SLF4JLoggerProxy.trace(this,
                                   "{} returning {}",
                                   getSessionId(),
                                   result);
            return result;
        } catch (ServiceException e) {
            throw new RuntimeException(e);
        }
    }
    /* (non-Javadoc)
     * @see com.marketcetera.admin.AdminClient#createPermission(com.marketcetera.admin.Permission)
     */
    @Override
    public Permission createPermission(Permission inPermission)
    {
        validateSession();
        AdminRpc.CreatePermissionRequest.Builder requestBuilder = AdminRpc.CreatePermissionRequest.newBuilder();
        try {
            SLF4JLoggerProxy.trace(this,
                                   "{} requesting create permission for {}",
                                   getSessionId(),
                                   inPermission);
            requestBuilder.setSessionId(getSessionId().getValue());
            AdminRpc.Permission.Builder permissionBuilder = AdminRpc.Permission.newBuilder();
            String description = StringUtils.trimToNull(inPermission.getDescription());
            if(description != null) {
                permissionBuilder.setDescription(description);
            }
            String permissionName = StringUtils.trimToNull(inPermission.getName());
            if(permissionName != null) {
                permissionBuilder.setName(permissionName);
            }
            requestBuilder.setPermission(permissionBuilder.build());
            AdminRpc.CreatePermissionResponse response = getClientService().createPermission(getController(),
                                                                                             requestBuilder.build());
            Permission result = null;
            if(response.getStatus().getFailed()) {
                throw new RuntimeException(response.getStatus().getMessage());
            }
            if(response.hasPermission()) {
                AdminRpc.Permission rpcPermission = response.getPermission();
                permissionName = null;
                description = null;
                if(rpcPermission.hasDescription()) {
                    description = rpcPermission.getDescription();
                }
                if(rpcPermission.hasName()) {
                    permissionName = rpcPermission.getName();
                }
                result = permissionFactory.create(permissionName,
                                                  description);
            }
            SLF4JLoggerProxy.trace(this,
                                   "{} returning {}",
                                   getSessionId(),
                                   result);
            return result;
        } catch (ServiceException e) {
            throw new RuntimeException(e);
        }
    }
    /* (non-Javadoc)
     * @see com.marketcetera.admin.AdminRpcClient#readPermissions()
     */
    @Override
    public List<Permission> readPermissions()
    {
        validateSession();
        AdminRpc.ReadPermissionsRequest.Builder requestBuilder = AdminRpc.ReadPermissionsRequest.newBuilder();
        try {
            SLF4JLoggerProxy.trace(this,
                                   "{} requesting permissions",
                                   getSessionId());
            requestBuilder.setSessionId(getSessionId().getValue());
            AdminRpc.ReadPermissionsResponse response = getClientService().readPermissions(getController(),
                                                                                           requestBuilder.build());
            List<Permission> results = new ArrayList<>();
            if(response.getStatus().getFailed()) {
                throw new RuntimeException(response.getStatus().getMessage());
            }
            for(AdminRpc.Permission rpcPermission : response.getPermissionList()) {
                try {
                    String permissionName = null;
                    String description = null;
                    if(rpcPermission.hasName()) {
                        permissionName = rpcPermission.getName();
                    }
                    if(rpcPermission.hasDescription()) {
                        description = rpcPermission.getDescription();
                    }
                    Permission permission = permissionFactory.create(permissionName,
                                                                     description);
                    results.add(permission);
                } catch (Exception e) {
                    String message = ExceptionUtils.getRootCauseMessage(e);
                    if(SLF4JLoggerProxy.isDebugEnabled(this)) {
                        SLF4JLoggerProxy.warn(this,
                                              e,
                                              "Unable to read {}: {}",
                                              rpcPermission,
                                              message);
                    } else {
                        SLF4JLoggerProxy.warn(this,
                                              "Unable to read {}: {}",
                                              rpcPermission,
                                              message);
                    }
                }
            }
            SLF4JLoggerProxy.trace(this,
                                   "{} returning {}",
                                   getSessionId(),
                                   results);
            return results;
        } catch (ServiceException e) {
            throw new RuntimeException(e);
        }
    }
    /* (non-Javadoc)
     * @see com.marketcetera.admin.AdminClient#readPermissions(org.marketcetera.core.PageRequest)
     */
    @Override
    public CollectionPageResponse<Permission> readPermissions(PageRequest inPageRequest)
    {
        validateSession();
        AdminRpc.ReadPermissionsRequest.Builder requestBuilder = AdminRpc.ReadPermissionsRequest.newBuilder();
        try {
            SLF4JLoggerProxy.trace(this,
                                   "{} requesting permissions",
                                   getSessionId());
            requestBuilder.setSessionId(getSessionId().getValue());
            requestBuilder.setPage(buildPageRequest(inPageRequest));
            AdminRpc.ReadPermissionsResponse response = getClientService().readPermissions(getController(),
                                                                                           requestBuilder.build());
            Collection<Permission> dataElements = new ArrayList<>();
            if(response.getStatus().getFailed()) {
                throw new RuntimeException(response.getStatus().getMessage());
            }
            for(AdminRpc.Permission rpcPermission : response.getPermissionList()) {
                try {
                    String permissionName = null;
                    String description = null;
                    if(rpcPermission.hasName()) {
                        permissionName = rpcPermission.getName();
                    }
                    if(rpcPermission.hasDescription()) {
                        description = rpcPermission.getDescription();
                    }
                    Permission permission = permissionFactory.create(permissionName,
                                                                     description);
                    dataElements.add(permission);
                } catch (Exception e) {
                    String message = ExceptionUtils.getRootCauseMessage(e);
                    if(SLF4JLoggerProxy.isDebugEnabled(this)) {
                        SLF4JLoggerProxy.warn(this,
                                              e,
                                              "Unable to read {}: {}",
                                              rpcPermission,
                                              message);
                    } else {
                        SLF4JLoggerProxy.warn(this,
                                              "Unable to read {}: {}",
                                              rpcPermission,
                                              message);
                    }
                }
            }
            CollectionPageResponse<Permission> result = new CollectionPageResponse<>();
            if(response.hasPage()) {
                addPageToResponse(response.getPage(),
                                  result);
            }
            result.setElements(dataElements);
            SLF4JLoggerProxy.trace(this,
                                   "{} returning {}",
                                   getSessionId(),
                                   result);
            return result;
        } catch (ServiceException e) {
            throw new RuntimeException(e);
        }
    }
    /* (non-Javadoc)
     * @see com.marketcetera.admin.AdminRpcClient#updatePermission(java.lang.String, com.marketcetera.admin.Permission)
     */
    @Override
    public Permission updatePermission(String inPermissionName,
                                       Permission inUpdatedPermission)
    {
        validateSession();
        AdminRpc.UpdatePermissionRequest.Builder requestBuilder = AdminRpc.UpdatePermissionRequest.newBuilder();
        try {
            SLF4JLoggerProxy.trace(this,
                                   "{} requesting permission update for {}: {}",
                                   getSessionId(),
                                   inPermissionName,
                                   inUpdatedPermission);
            requestBuilder.setSessionId(getSessionId().getValue());
            inPermissionName = StringUtils.trimToNull(inPermissionName);
            if(inPermissionName != null) {
                requestBuilder.setPermissionName(inPermissionName);
            }
            if(inUpdatedPermission != null) {
                AdminRpc.Permission.Builder permissionBuilder = AdminRpc.Permission.newBuilder();
                if(inUpdatedPermission.getDescription() != null) {
                    permissionBuilder.setDescription(inUpdatedPermission.getDescription());
                }
                if(inUpdatedPermission.getName() != null) {
                    permissionBuilder.setName(inUpdatedPermission.getName());
                }
                requestBuilder.setPermission(permissionBuilder.build());
            }
            AdminRpc.UpdatePermissionResponse response = getClientService().updatePermission(getController(),
                                                                                        requestBuilder.build());
            Permission result = null;
            if(response.getStatus().getFailed()) {
                throw new RuntimeException(response.getStatus().getMessage());
            }
            if(response.hasPermission()) {
                AdminRpc.Permission rpcPermission = response.getPermission();
                String permissionName = null;
                String description = null;
                if(rpcPermission.hasDescription()) {
                    description = rpcPermission.getDescription();
                }
                if(rpcPermission.hasName()) {
                    permissionName = rpcPermission.getDescription();
                }
                result = permissionFactory.create(permissionName,
                                                  description);
            }
            SLF4JLoggerProxy.trace(this,
                                   "{} returning {}",
                                   getSessionId(),
                                   result);
            return result;
        } catch (ServiceException e) {
            throw new RuntimeException(e);
        }
    }
    /* (non-Javadoc)
     * @see com.marketcetera.admin.AdminRpcClient#deletePermission(java.lang.String)
     */
    @Override
    public void deletePermission(String inPermissionName)
    {
        validateSession();
        AdminRpc.DeletePermissionRequest.Builder requestBuilder = AdminRpc.DeletePermissionRequest.newBuilder();
        try {
            SLF4JLoggerProxy.trace(this,
                                   "{} requesting permission delete for {}",
                                   getSessionId(),
                                   inPermissionName);
            requestBuilder.setSessionId(getSessionId().getValue());
            inPermissionName = StringUtils.trimToNull(inPermissionName);
            if(inPermissionName != null) {
                requestBuilder.setPermissionName(inPermissionName);
            }
            AdminRpc.DeletePermissionResponse response = getClientService().deletePermission(getController(),
                                                                                             requestBuilder.build());
            if(response.getStatus().getFailed()) {
                throw new RuntimeException(response.getStatus().getMessage());
            }
            SLF4JLoggerProxy.trace(this,
                                   "{} delete permission for {} succeeded",
                                   getSessionId(),
                                   inPermissionName);
        } catch (ServiceException e) {
            throw new RuntimeException(e);
        }
    }
    /* (non-Javadoc)
     * @see com.marketcetera.admin.AdminClient#getUserAttribute(java.lang.String, com.marketcetera.admin.UserAttributeType)
     */
    @Override
    public UserAttribute getUserAttribute(String inUsername,
                                          UserAttributeType inAttributeType)
    {
        validateSession();
        AdminRpc.ReadUserAttributeRequest.Builder requestBuilder = AdminRpc.ReadUserAttributeRequest.newBuilder();
        try {
            SLF4JLoggerProxy.trace(this,
                                   "{} requesting user attribute {} for {}",
                                   getSessionId(),
                                   inAttributeType,
                                   inUsername);
            requestBuilder.setSessionId(getSessionId().getValue());
            requestBuilder.setAttributeType(inAttributeType.name());
            requestBuilder.setUsername(inUsername);
            AdminRpc.ReadUserAttributeResponse response = getClientService().readUserAttribute(getController(),
                                                                                               requestBuilder.build());
            if(response.getStatus().getFailed()) {
                throw new RuntimeException(response.getStatus().getMessage());
            }
            UserAttribute userAttribute = null;
            if(response.hasUserAttribute()) {
                AdminRpc.UserAttribute rpcUserAttribute = response.getUserAttribute();
                User user = null;
                if(rpcUserAttribute.hasUser()) {
                    user = createUserFrom(rpcUserAttribute.getUser());
                }
                String attribute = null;
                if(rpcUserAttribute.hasAttribute()) {
                    attribute = rpcUserAttribute.getAttribute();
                }
                UserAttributeType userAttributeType = null;
                if(rpcUserAttribute.hasAttributeType()) {
                    userAttributeType = UserAttributeType.valueOf(rpcUserAttribute.getAttributeType());
                }
                userAttribute = userAttributeFactory.create(user,
                                                            userAttributeType,
                                                            attribute);
            }
            SLF4JLoggerProxy.trace(this,
                                   "{} returning {}",
                                   getSessionId(),
                                   userAttribute);
            return userAttribute;
        } catch (ServiceException e) {
            throw new RuntimeException(e);
        }
    }
    /* (non-Javadoc)
     * @see com.marketcetera.admin.AdminClient#setUserAttribute(java.lang.String, com.marketcetera.admin.UserAttributeType, java.lang.String)
     */
    @Override
    public void setUserAttribute(String inUsername,
                                 UserAttributeType inAttributeType,
                                 String inAttribute)
    {
        validateSession();
        AdminRpc.WriteUserAttributeRequest.Builder requestBuilder = AdminRpc.WriteUserAttributeRequest.newBuilder();
        try {
            SLF4JLoggerProxy.trace(this,
                                   "{} requesting write user attribute {} for {}: {}",
                                   getSessionId(),
                                   inAttributeType,
                                   inUsername,
                                   inAttribute);
            requestBuilder.setSessionId(getSessionId().getValue());
            requestBuilder.setAttributeType(inAttributeType.name());
            requestBuilder.setUsername(inUsername);
            requestBuilder.setAttribute(inAttribute==null?"":inAttribute);
            AdminRpc.WriteUserAttributeResponse response = getClientService().writeUserAttribute(getController(),
                                                                                                 requestBuilder.build());
            if(response.getStatus().getFailed()) {
                throw new RuntimeException(response.getStatus().getMessage());
            }
        } catch (ServiceException e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * Validate and start the object.
     */
    @Override
    @PostConstruct
    public void start()
    {
        Validate.notNull(permissionFactory,
                         "Permission factory required");
        Validate.notNull(userFactory,
                         "User factory required");
        try {
            super.start();
        } catch (Exception e) {
            SLF4JLoggerProxy.warn(this,
                                  e);
            if(e instanceof RuntimeException) {
                throw (RuntimeException)e;
            }
            throw new RuntimeException(e);
        }
    }
    /**
     * Get the permissionFactory value.
     *
     * @return a <code>PermissionFactory</code> value
     */
    public PermissionFactory getPermissionFactory()
    {
        return permissionFactory;
    }
    /**
     * Sets the permissionFactory value.
     *
     * @param inPermissionFactory a <code>PermissionFactory</code> value
     */
    public void setPermissionFactory(PermissionFactory inPermissionFactory)
    {
        permissionFactory = inPermissionFactory;
    }
    /**
     * Get the roleFactory value.
     *
     * @return a <code>RoleFactory</code> value
     */
    public RoleFactory getRoleFactory()
    {
        return roleFactory;
    }
    /**
     * Sets the roleFactory value.
     *
     * @param inRoleFactory a <code>RoleFactory</code> value
     */
    public void setRoleFactory(RoleFactory inRoleFactory)
    {
        roleFactory = inRoleFactory;
    }
    /**
     * Get the userFactory value.
     *
     * @return a <code>UserFactory</code> value
     */
    public UserFactory getUserFactory()
    {
        return userFactory;
    }
    /**
     * Sets the userFactory value.
     *
     * @param inUserFactory a <code>UserFactory</code> value
     */
    public void setUserFactory(UserFactory inUserFactory)
    {
        userFactory = inUserFactory;
    }
    /**
     * Get the userAttributeFactory value.
     *
     * @return a <code>UserAttributeFactory</code> value
     */
    public UserAttributeFactory getUserAttributeFactory()
    {
        return userAttributeFactory;
    }
    /**
     * Sets the userAttributeFactory value.
     *
     * @param inUserAttributeFactory a <code>UserAttributeFactory</code> value
     */
    public void setUserAttributeFactory(UserAttributeFactory inUserAttributeFactory)
    {
        userAttributeFactory = inUserAttributeFactory;
    }
    /**
     * Create a new AdminRpcClientImpl instance.
     */
    AdminRpcClient() {}
    /* (non-Javadoc)
     * @see com.marketcetera.admin.BaseRpcClient#getAppId()
     */
    @Override
    protected AppId getAppId()
    {
        return APP_ID;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.admin.BaseRpcClient#getVersionInfo()
     */
    @Override
    protected VersionInfo getVersionInfo()
    {
        return APP_ID_VERSION;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.admin.BaseRpcClient#createClient(com.googlecode.protobuf.pro.duplex.RpcClientChannel)
     */
    @Override
    protected BlockingInterface createClient(RpcClientChannel inChannel)
    {
        return AdminRpc.AdminRpcService.newBlockingStub(inChannel);
    }
    /* (non-Javadoc)
     * @see com.marketcetera.admin.BaseRpcClient#executeLogin(com.google.protobuf.RpcController, org.marketcetera.util.rpc.BaseRpc.LoginRequest)
     */
    @Override
    protected LoginResponse executeLogin(RpcController inController,
                                         LoginRequest inRequest)
            throws ServiceException
    {
        return getClientService().login(inController,
                                        inRequest);
    }
    /* (non-Javadoc)
     * @see com.marketcetera.admin.BaseRpcClient#executeLogout(com.google.protobuf.RpcController, org.marketcetera.util.rpc.BaseRpc.LogoutRequest)
     */
    @Override
    protected LogoutResponse executeLogout(RpcController inController,
                                           LogoutRequest inRequest)
            throws ServiceException
    {
        return getClientService().logout(inController,
                                         inRequest);
    }
    /**
     * Create a user from the given RPC value.
     *
     * @param inRpcUser an <code>AdminRpc.User</code> value
     * @return a <code>User</code> value
     */
    private User createUserFrom(AdminRpc.User inRpcUser)
    {
        boolean isActive = false;
        String description = null;
        String name = null;
        String password = "********";
        if(inRpcUser.hasActive()) {
            isActive = inRpcUser.getActive();
        }
        if(inRpcUser.hasDescription()) {
            description = inRpcUser.getDescription();
        }
        if(inRpcUser.hasName()) {
            name = inRpcUser.getName();
        }
        User user = userFactory.create(name,
                                       password,
                                       description,
                                       isActive);
        return user;
    }
    /**
     * Add the results from the given RPC page to the given response object.
     *
     * @param inRpcPage a <code>BaseRpc.PageResponse</code> value
     * @param inResponse a <code>PageResponse</code> value
     */
    private void addPageToResponse(BaseRpc.PageResponse inRpcPage,
                                   PageResponse inResponse)
    {
        if(inRpcPage.hasPageMaxSize()) {
            inResponse.setPageMaxSize(inRpcPage.getPageMaxSize());
        }
        if(inRpcPage.hasPageNumber()) {
            inResponse.setPageNumber(inRpcPage.getPageNumber());
        }
        if(inRpcPage.hasPageSize()) {
            inResponse.setPageSize(inRpcPage.getPageSize());
        }
        if(inRpcPage.hasTotalPages()) {
            inResponse.setTotalPages(inRpcPage.getTotalPages());
        }
        if(inRpcPage.hasTotalSize()) {
            inResponse.setTotalSize(inRpcPage.getTotalSize());
        }
        if(inRpcPage.hasSortOrder()) {
            List<Sort> sortOrder = Lists.newArrayList();
            for(BaseRpc.Sort rpcSort : inRpcPage.getSortOrder().getSortList()) {
                Sort sort = new Sort();
                if(rpcSort.hasDirection()) {
                    sort.setDirection(rpcSort.getDirection()==BaseRpc.SortDirection.ASCENDING?SortDirection.ASCENDING:SortDirection.DESCENDING);
                }
                if(rpcSort.hasProperty()) {
                    sort.setProperty(rpcSort.getProperty());
                }
                sortOrder.add(sort);
            }
            inResponse.setSortOrder(sortOrder);
        }
    }
    /**
     * Build an RPC page request from the given request object.
     *
     * @param inPageRequest a <code>PageRequest</code> value
     * @return a <code>BaseRpc.PageRequest</code> value
     */
    private BaseRpc.PageRequest buildPageRequest(PageRequest inPageRequest)
    {
        BaseRpc.PageRequest.Builder pageRequestBuilder = BaseRpc.PageRequest.newBuilder();
        pageRequestBuilder.setPage(inPageRequest.getPageNumber());
        pageRequestBuilder.setSize(inPageRequest.getPageSize());
        if(inPageRequest.getSortOrder() != null && !inPageRequest.getSortOrder().isEmpty()) {
            BaseRpc.SortOrder.Builder sortOrderBuilder = BaseRpc.SortOrder.newBuilder();
            BaseRpc.Sort.Builder sortBuilder = BaseRpc.Sort.newBuilder();
            for(Sort sort : inPageRequest.getSortOrder()) {
                sortBuilder.setDirection(sort.getDirection()==SortDirection.ASCENDING?BaseRpc.SortDirection.ASCENDING:BaseRpc.SortDirection.DESCENDING);
                sortBuilder.setProperty(sort.getProperty());
                sortOrderBuilder.addSort(sortBuilder.build());
                sortBuilder.clear();
            }
            pageRequestBuilder.setSortOrder(sortOrderBuilder.build());
        }
        return pageRequestBuilder.build();
    }
    /**
     * creates {@link UserAttributeFactory} objects
     */
    @Autowired
    private UserAttributeFactory userAttributeFactory;
    /**
     * creates {@link Permission} objects
     */
    @Autowired
    private PermissionFactory permissionFactory;
    /**
     * creates {@link User} objects
     */
    @Autowired
    private UserFactory userFactory;
    /**
     * creates {@link Role} objects
     */
    @Autowired
    private RoleFactory roleFactory;
    /**
     * The client's application ID: the application name.
     */
    public static final String APP_ID_NAME = "AdminRpcClient"; //$NON-NLS-1$
    /**
     * The client's application ID: the version.
     */
    public static final VersionInfo APP_ID_VERSION = ApplicationVersion.getVersion(AdminClient.class);
    /**
     * The client's application ID: the ID.
     */
    public static final AppId APP_ID = Util.getAppId(APP_ID_NAME,APP_ID_VERSION.getVersionInfo());
}
