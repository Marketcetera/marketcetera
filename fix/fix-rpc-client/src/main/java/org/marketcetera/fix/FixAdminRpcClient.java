package org.marketcetera.fix;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import org.apache.commons.lang3.StringUtils;
import org.marketcetera.brokers.BrokerStatusListener;
import org.marketcetera.cluster.ClusterData;
import org.marketcetera.cluster.ClusterDataFactory;
import org.marketcetera.core.ApplicationVersion;
import org.marketcetera.core.Util;
import org.marketcetera.core.VersionInfo;
import org.marketcetera.fix.FixAdminRpc.BrokerStatusListenerResponse;
import org.marketcetera.fix.FixAdminRpcServiceGrpc.FixAdminRpcServiceBlockingStub;
import org.marketcetera.fix.FixAdminRpcServiceGrpc.FixAdminRpcServiceStub;
import org.marketcetera.persist.CollectionPageResponse;
import org.marketcetera.persist.PageRequest;
import org.marketcetera.rpc.base.BaseRpc;
import org.marketcetera.rpc.base.BaseRpc.HeartbeatRequest;
import org.marketcetera.rpc.base.BaseRpc.LoginResponse;
import org.marketcetera.rpc.base.BaseRpc.LogoutResponse;
import org.marketcetera.rpc.base.BaseRpcUtil;
import org.marketcetera.rpc.base.BaseRpcUtil.AbstractClientListenerProxy;
import org.marketcetera.rpc.client.AbstractRpcClient;
import org.marketcetera.rpc.paging.PagingRpcUtil;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.ws.tags.AppId;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import io.grpc.Channel;

/* $License$ */

/**
 * Provides an RPC-based {@link FixAdminClient} implementation.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class FixAdminRpcClient
        extends AbstractRpcClient<FixAdminRpcServiceBlockingStub,FixAdminRpcServiceStub,FixAdminRpcClientParameters>
        implements FixAdminClient
{
    /* (non-Javadoc)
     * @see org.marketcetera.fix.FixAdminClient#createFixSession(org.marketcetera.fix.FixSession)
     */
    @Override
    public FixSession createFixSession(FixSession inFixSession)
    {
        return executeCall(new Callable<FixSession>() {
            @Override
            public FixSession call()
                    throws Exception
            {
                SLF4JLoggerProxy.trace(FixAdminRpcClient.this,
                                       "{} create FIX session {}",
                                       getSessionId(),
                                       inFixSession);
                FixAdminRpc.CreateFixSessionRequest.Builder requestBuilder = FixAdminRpc.CreateFixSessionRequest.newBuilder();
                requestBuilder.setSessionId(getSessionId().getValue());
                FixRpcUtil.getRpcFixSession(inFixSession).ifPresent(rpcFixSession->requestBuilder.setFixSession(rpcFixSession));
                FixAdminRpc.CreateFixSessionRequest request = requestBuilder.build();
                SLF4JLoggerProxy.trace(FixAdminRpcClient.this,
                                       "{} sending {}",
                                       getSessionId(),
                                       request);
                FixAdminRpc.CreateFixSessionResponse response = getBlockingStub().createFixSession(request);
                SLF4JLoggerProxy.trace(FixAdminRpcClient.this,
                                       "{} received {}",
                                       getSessionId(),
                                       response);
                FixSession result = null;
                if(response.hasFixSession()) {
                    result = FixRpcUtil.getFixSession(response.getFixSession(),fixSessionFactory).orElse(null);
                }
                SLF4JLoggerProxy.trace(FixAdminRpcClient.this,
                                       "{} returning {}",
                                       getSessionId(),
                                       result);
                return result;
            }
        });
    }
    /* (non-Javadoc)
     * @see org.marketcetera.rpc.client.AbstractRpcClient#stop()
     */
    @Override
    public void stop()
            throws Exception
    {
        for(Object listener : listenerProxies.asMap().keySet()) {
            try {
                removeBrokerStatusListener((BrokerStatusListener)listener);
            } catch (Exception ignored) {
                ignored.printStackTrace();
            }
        }

        super.stop();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.fix.FixAdminClient#readFixSessions()
     */
    @Override
    public List<ActiveFixSession> readFixSessions()
    {
        return Lists.newArrayList(readFixSessions(new PageRequest(0,
                                                                  Integer.MAX_VALUE)).getElements());
    }
    /* (non-Javadoc)
     * @see org.marketcetera.fix.FixAdminClient#readFixSessions(org.marketcetera.persist.PageRequest)
     */
    @Override
    public CollectionPageResponse<ActiveFixSession> readFixSessions(PageRequest inPageRequest)
    {
        return executeCall(new Callable<CollectionPageResponse<ActiveFixSession>>() {
            @Override
            public CollectionPageResponse<ActiveFixSession> call()
                    throws Exception
            {
                SLF4JLoggerProxy.trace(FixAdminRpcClient.this,
                                       "{} read FIX sessions",
                                       getSessionId());
                FixAdminRpc.ReadFixSessionsRequest.Builder requestBuilder = FixAdminRpc.ReadFixSessionsRequest.newBuilder();
                requestBuilder.setSessionId(getSessionId().getValue());
                requestBuilder.setPage(PagingRpcUtil.buildPageRequest(inPageRequest));
                FixAdminRpc.ReadFixSessionsRequest request = requestBuilder.build();
                SLF4JLoggerProxy.trace(FixAdminRpcClient.this,
                                       "{} sending {}",
                                       getSessionId(),
                                       request);
                FixAdminRpc.ReadFixSessionsResponse response = getBlockingStub().readFixSessions(request);
                SLF4JLoggerProxy.trace(FixAdminRpcClient.this,
                                       "{} received {}",
                                       getSessionId(),
                                       response);
                CollectionPageResponse<ActiveFixSession> results = new CollectionPageResponse<>();
                for(FixAdminRpc.ActiveFixSession rpcFixSession : response.getFixSessionList()) {
                    FixRpcUtil.getActiveFixSession(rpcFixSession,
                                                   activeFixSessionFactory,
                                                   fixSessionFactory,
                                                   clusterDataFactory).ifPresent(activeFixSession->results.getElements().add(activeFixSession));
                }
                PagingRpcUtil.setPageResponse(inPageRequest,
                                              response.getPage(),
                                              results);
                SLF4JLoggerProxy.trace(FixAdminRpcClient.this,
                                       "{} returning {}",
                                       getSessionId(),
                                       results);
                return results;
            }
        });
    }
    /* (non-Javadoc)
     * @see org.marketcetera.fix.FixAdminClient#updateFixSession(java.lang.String, org.marketcetera.fix.FixSession)
     */
    @Override
    public void updateFixSession(String inIncomingName,
                                 FixSession inFixSession)
    {
        executeCall(new Callable<Void>() {
            @Override
            public Void call()
                    throws Exception
            {
                SLF4JLoggerProxy.trace(FixAdminRpcClient.this,
                                       "{} update FIX session {}: {}",
                                       getSessionId(),
                                       inIncomingName,
                                       inFixSession);
                FixAdminRpc.UpdateFixSessionRequest.Builder requestBuilder = FixAdminRpc.UpdateFixSessionRequest.newBuilder();
                requestBuilder.setSessionId(getSessionId().getValue());
                requestBuilder.setName(StringUtils.trimToNull(inIncomingName));
                FixRpcUtil.getRpcFixSession(inFixSession).ifPresent(rpcFixSession->requestBuilder.setFixSession(rpcFixSession));
                FixAdminRpc.UpdateFixSessionRequest request = requestBuilder.build();
                SLF4JLoggerProxy.trace(FixAdminRpcClient.this,
                                       "{} sending {}",
                                       getSessionId(),
                                       request);
                FixAdminRpc.UpdateFixSessionResponse response = getBlockingStub().updateFixSession(request);
                SLF4JLoggerProxy.trace(FixAdminRpcClient.this,
                                       "{} received {}",
                                       getSessionId(),
                                       response);
                return null;
            }
        });
    }
    /* (non-Javadoc)
     * @see org.marketcetera.fix.FixAdminClient#enableFixSession(java.lang.String)
     */
    @Override
    public void enableFixSession(String inName)
    {
        executeCall(new Callable<Void>() {
            @Override
            public Void call()
                    throws Exception
            {
                SLF4JLoggerProxy.trace(FixAdminRpcClient.this,
                                       "{} enable FIX session {}",
                                       getSessionId(),
                                       inName);
                FixAdminRpc.EnableFixSessionRequest.Builder requestBuilder = FixAdminRpc.EnableFixSessionRequest.newBuilder();
                requestBuilder.setSessionId(getSessionId().getValue());
                requestBuilder.setName(StringUtils.trimToNull(inName));
                FixAdminRpc.EnableFixSessionRequest request = requestBuilder.build();
                SLF4JLoggerProxy.trace(FixAdminRpcClient.this,
                                       "{} sending {}",
                                       getSessionId(),
                                       request);
                FixAdminRpc.EnableFixSessionResponse response = getBlockingStub().enableFixSession(request);
                SLF4JLoggerProxy.trace(FixAdminRpcClient.this,
                                       "{} received {}",
                                       getSessionId(),
                                       response);
                return null;
            }
        });
    }
    /* (non-Javadoc)
     * @see org.marketcetera.fix.FixAdminClient#disableFixSession(java.lang.String)
     */
    @Override
    public void disableFixSession(String inName)
    {
        executeCall(new Callable<Void>() {
            @Override
            public Void call()
                    throws Exception
            {
                SLF4JLoggerProxy.trace(FixAdminRpcClient.this,
                                       "{} disable FIX session {}",
                                       getSessionId(),
                                       inName);
                FixAdminRpc.DisableFixSessionRequest.Builder requestBuilder = FixAdminRpc.DisableFixSessionRequest.newBuilder();
                requestBuilder.setSessionId(getSessionId().getValue());
                requestBuilder.setName(StringUtils.trimToNull(inName));
                FixAdminRpc.DisableFixSessionRequest request = requestBuilder.build();
                SLF4JLoggerProxy.trace(FixAdminRpcClient.this,
                                       "{} sending {}",
                                       getSessionId(),
                                       request);
                FixAdminRpc.DisableFixSessionResponse response = getBlockingStub().disableFixSession(request);
                SLF4JLoggerProxy.trace(FixAdminRpcClient.this,
                                       "{} received {}",
                                       getSessionId(),
                                       response);
                return null;
            }
        });
    }
    /* (non-Javadoc)
     * @see org.marketcetera.fix.FixAdminClient#deleteFixSession(java.lang.String)
     */
    @Override
    public void deleteFixSession(String inName)
    {
        executeCall(new Callable<Void>() {
            @Override
            public Void call()
                    throws Exception
            {
                SLF4JLoggerProxy.trace(FixAdminRpcClient.this,
                                       "{} delete FIX session {}",
                                       getSessionId(),
                                       inName);
                FixAdminRpc.DeleteFixSessionRequest.Builder requestBuilder = FixAdminRpc.DeleteFixSessionRequest.newBuilder();
                requestBuilder.setSessionId(getSessionId().getValue());
                requestBuilder.setName(StringUtils.trimToNull(inName));
                FixAdminRpc.DeleteFixSessionRequest request = requestBuilder.build();
                SLF4JLoggerProxy.trace(FixAdminRpcClient.this,
                                       "{} sending {}",
                                       getSessionId(),
                                       request);
                FixAdminRpc.DeleteFixSessionResponse response = getBlockingStub().deleteFixSession(request);
                SLF4JLoggerProxy.trace(FixAdminRpcClient.this,
                                       "{} received {}",
                                       getSessionId(),
                                       response);
                return null;
            }
        });
    }
    /* (non-Javadoc)
     * @see org.marketcetera.fix.FixAdminClient#stopFixSession(java.lang.String)
     */
    @Override
    public void stopFixSession(String inName)
    {
        executeCall(new Callable<Void>() {
            @Override
            public Void call()
                    throws Exception
            {
                SLF4JLoggerProxy.trace(FixAdminRpcClient.this,
                                       "{} stop FIX session {}",
                                       getSessionId(),
                                       inName);
                FixAdminRpc.StopFixSessionRequest.Builder requestBuilder = FixAdminRpc.StopFixSessionRequest.newBuilder();
                requestBuilder.setSessionId(getSessionId().getValue());
                requestBuilder.setName(StringUtils.trimToNull(inName));
                FixAdminRpc.StopFixSessionRequest request = requestBuilder.build();
                SLF4JLoggerProxy.trace(FixAdminRpcClient.this,
                                       "{} sending {}",
                                       getSessionId(),
                                       request);
                FixAdminRpc.StopFixSessionResponse response = getBlockingStub().stopFixSession(request);
                SLF4JLoggerProxy.trace(FixAdminRpcClient.this,
                                       "{} received {}",
                                       getSessionId(),
                                       response);
                return null;
            }
        });
    }
    /* (non-Javadoc)
     * @see org.marketcetera.fix.FixAdminClient#startFixSession(java.lang.String)
     */
    @Override
    public void startFixSession(String inName)
    {
        executeCall(new Callable<Void>() {
            @Override
            public Void call()
                    throws Exception
            {
                SLF4JLoggerProxy.trace(FixAdminRpcClient.this,
                                       "{} start FIX session {}",
                                       getSessionId(),
                                       inName);
                FixAdminRpc.StartFixSessionRequest.Builder requestBuilder = FixAdminRpc.StartFixSessionRequest.newBuilder();
                requestBuilder.setSessionId(getSessionId().getValue());
                requestBuilder.setName(StringUtils.trimToNull(inName));
                FixAdminRpc.StartFixSessionRequest request = requestBuilder.build();
                SLF4JLoggerProxy.trace(FixAdminRpcClient.this,
                                       "{} sending {}",
                                       getSessionId(),
                                       request);
                FixAdminRpc.StartFixSessionResponse response = getBlockingStub().startFixSession(request);
                SLF4JLoggerProxy.trace(FixAdminRpcClient.this,
                                       "{} received {}",
                                       getSessionId(),
                                       response);
                return null;
            }
        });
    }
    /* (non-Javadoc)
     * @see org.marketcetera.fix.FixAdminClient#getFixSessionAttributeDescriptors()
     */
    @Override
    public Collection<FixSessionAttributeDescriptor> getFixSessionAttributeDescriptors()
    {
        return executeCall(new Callable<Collection<FixSessionAttributeDescriptor>>() {
            @Override
            public Collection<FixSessionAttributeDescriptor> call()
                    throws Exception
            {
                SLF4JLoggerProxy.trace(FixAdminRpcClient.this,
                                       "{} get fix session attribute descriptors",
                                       getSessionId());
                FixAdminRpc.ReadFixSessionAttributeDescriptorsRequest.Builder requestBuilder = FixAdminRpc.ReadFixSessionAttributeDescriptorsRequest.newBuilder();
                requestBuilder.setSessionId(getSessionId().getValue());
                FixAdminRpc.ReadFixSessionAttributeDescriptorsRequest request = requestBuilder.build();
                SLF4JLoggerProxy.trace(FixAdminRpcClient.this,
                                       "{} sending {}",
                                       getSessionId(),
                                       request);
                FixAdminRpc.ReadFixSessionAttributeDescriptorsResponse response = getBlockingStub().readFixSessionAttributeDescriptors(request);
                SLF4JLoggerProxy.trace(FixAdminRpcClient.this,
                                       "{} received {}",
                                       getSessionId(),
                                       response);
                Collection<FixSessionAttributeDescriptor> results = Lists.newArrayList();
                response.getFixSessionAttributeDescriptorsList().stream()
                    .forEach(rpcFixSessionAttributeDescriptor->FixRpcUtil.getFixSessionAttributeDescriptor(rpcFixSessionAttributeDescriptor,fixSessionAttributeDescriptorFactory)
                    .ifPresent(fixSessionAttributeDescriptor->results.add(fixSessionAttributeDescriptor)));
                SLF4JLoggerProxy.trace(FixAdminRpcClient.this,
                                       "{} returning {}",
                                       getSessionId(),
                                       results);
                return results;
            }
        });
    }
    /* (non-Javadoc)
     * @see org.marketcetera.fix.FixAdminClient#updateSequenceNumbers(java.lang.String, int, int)
     */
    @Override
    public void updateSequenceNumbers(String inSessionName,
                                      int inSenderSequenceNumber,
                                      int inTargetSequenceNumber)
    {
        executeCall(new Callable<Void>() {
            @Override
            public Void call()
                    throws Exception
            {
                SLF4JLoggerProxy.trace(FixAdminRpcClient.this,
                                       "{} update sequence numbers for {} to {}/{}",
                                       getSessionId(),
                                       inSessionName,
                                       inSenderSequenceNumber,
                                       inTargetSequenceNumber);
                FixAdminRpc.UpdateSequenceNumbersRequest.Builder requestBuilder = FixAdminRpc.UpdateSequenceNumbersRequest.newBuilder();
                requestBuilder.setSessionId(getSessionId().getValue());
                requestBuilder.setName(StringUtils.trimToNull(inSessionName));
                requestBuilder.setSenderSequenceNumber(inSenderSequenceNumber);
                requestBuilder.setTargetSequenceNumber(inTargetSequenceNumber);
                FixAdminRpc.UpdateSequenceNumbersRequest request = requestBuilder.build();
                SLF4JLoggerProxy.trace(FixAdminRpcClient.this,
                                       "{} sending {}",
                                       getSessionId(),
                                       request);
                FixAdminRpc.UpdateSequenceNumbersResponse response = getBlockingStub().updateSequenceNumbers(request);
                SLF4JLoggerProxy.trace(FixAdminRpcClient.this,
                                       "{} received {}",
                                       getSessionId(),
                                       response);
                return null;
            }
        });
    }
    /* (non-Javadoc)
     * @see org.marketcetera.fix.FixAdminClient#updateSenderSequenceNumber(java.lang.String, int)
     */
    @Override
    public void updateSenderSequenceNumber(String inSessionName,
                                           int inSenderSequenceNumber)
    {
        executeCall(new Callable<Void>() {
            @Override
            public Void call()
                    throws Exception
            {
                SLF4JLoggerProxy.trace(FixAdminRpcClient.this,
                                       "{} update sender sequence number for {} to {}",
                                       getSessionId(),
                                       inSessionName,
                                       inSenderSequenceNumber);
                FixAdminRpc.UpdateSequenceNumbersRequest.Builder requestBuilder = FixAdminRpc.UpdateSequenceNumbersRequest.newBuilder();
                requestBuilder.setSessionId(getSessionId().getValue());
                requestBuilder.setName(StringUtils.trimToNull(inSessionName));
                requestBuilder.setSenderSequenceNumber(inSenderSequenceNumber);
                requestBuilder.setTargetSequenceNumber(-1);
                FixAdminRpc.UpdateSequenceNumbersRequest request = requestBuilder.build();
                SLF4JLoggerProxy.trace(FixAdminRpcClient.this,
                                       "{} sending {}",
                                       getSessionId(),
                                       request);
                FixAdminRpc.UpdateSequenceNumbersResponse response = getBlockingStub().updateSequenceNumbers(request);
                SLF4JLoggerProxy.trace(FixAdminRpcClient.this,
                                       "{} received {}",
                                       getSessionId(),
                                       response);
                return null;
            }
        });
    }
    /* (non-Javadoc)
     * @see org.marketcetera.fix.FixAdminClient#updateTargetSequenceNumber(java.lang.String, int)
     */
    @Override
    public void updateTargetSequenceNumber(String inSessionName,
                                           int inTargetSequenceNumber)
    {
        executeCall(new Callable<Void>() {
            @Override
            public Void call()
                    throws Exception
            {
                SLF4JLoggerProxy.trace(FixAdminRpcClient.this,
                                       "{} update target sequence number for {} to {}",
                                       getSessionId(),
                                       inSessionName,
                                       inTargetSequenceNumber);
                FixAdminRpc.UpdateSequenceNumbersRequest.Builder requestBuilder = FixAdminRpc.UpdateSequenceNumbersRequest.newBuilder();
                requestBuilder.setSessionId(getSessionId().getValue());
                requestBuilder.setName(StringUtils.trimToNull(inSessionName));
                requestBuilder.setSenderSequenceNumber(-1);
                requestBuilder.setTargetSequenceNumber(inTargetSequenceNumber);
                FixAdminRpc.UpdateSequenceNumbersRequest request = requestBuilder.build();
                SLF4JLoggerProxy.trace(FixAdminRpcClient.this,
                                       "{} sending {}",
                                       getSessionId(),
                                       request);
                FixAdminRpc.UpdateSequenceNumbersResponse response = getBlockingStub().updateSequenceNumbers(request);
                SLF4JLoggerProxy.trace(FixAdminRpcClient.this,
                                       "{} received {}",
                                       getSessionId(),
                                       response);
                return null;
            }
        });
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.client.TradingClient#addBrokerStatusListener(org.marketcetera.brokers.BrokerStatusListener)
     */
    @Override
    public void addBrokerStatusListener(BrokerStatusListener inBrokerStatusListener)
    {
        // check to see if this listener is already registered
        if(listenerProxies.asMap().containsKey(inBrokerStatusListener)) {
            return;
        }
        // make sure that this listener wasn't just whisked out from under us
        final AbstractClientListenerProxy<?,?,?> listener = listenerProxies.getUnchecked(inBrokerStatusListener);
        if(listener == null) {
            return;
        }
        executeCall(new Callable<Void>() {
            @Override
            public Void call()
                    throws Exception
            {
                SLF4JLoggerProxy.trace(FixAdminRpcClient.this,
                                       "{} adding broker status listener",
                                       getSessionId());
                FixAdminRpc.AddBrokerStatusListenerRequest.Builder requestBuilder = FixAdminRpc.AddBrokerStatusListenerRequest.newBuilder();
                requestBuilder.setSessionId(getSessionId().getValue());
                requestBuilder.setListenerId(listener.getId());
                FixAdminRpc.AddBrokerStatusListenerRequest addBrokerStatusListenerRequest = requestBuilder.build();
                SLF4JLoggerProxy.trace(FixAdminRpcClient.this,
                                       "{} sending {}",
                                       getSessionId(),
                                       addBrokerStatusListenerRequest);
                getAsyncStub().addBrokerStatusListener(addBrokerStatusListenerRequest,
                                                       (FixSessionStatusListenerProxy)listener);
                return null;
            }
        });
    }
    /* (non-Javadoc)
     * @see org.marketcetera.fix.FixAdminClient#getInstanceData(int)
     */
    @Override
    public FixSessionInstanceData getFixSessionInstanceData(int inAffinity)
    {
        return executeCall(new Callable<FixSessionInstanceData>() {
            @Override
            public FixSessionInstanceData call()
                    throws Exception
            {
                SLF4JLoggerProxy.trace(FixAdminRpcClient.this,
                                       "{} get instance data {}",
                                       getSessionId(),
                                       inAffinity);
                FixAdminRpc.InstanceDataRequest.Builder requestBuilder = FixAdminRpc.InstanceDataRequest.newBuilder();
                requestBuilder.setSessionId(getSessionId().getValue());
                requestBuilder.setAffinity(inAffinity);
                FixAdminRpc.InstanceDataRequest request = requestBuilder.build();
                SLF4JLoggerProxy.trace(FixAdminRpcClient.this,
                                       "{} sending {}",
                                       getSessionId(),
                                       request);
                FixAdminRpc.InstanceDataResponse response = getBlockingStub().getInstanceData(request);
                SLF4JLoggerProxy.trace(FixAdminRpcClient.this,
                                       "{} received {}",
                                       getSessionId(),
                                       response);
                FixSessionInstanceData result = FixRpcUtil.getInstanceData(response.getInstanceData()).get();
                SLF4JLoggerProxy.trace(FixAdminRpcClient.this,
                                       "{} returning {}",
                                       getSessionId(),
                                       result);
                return result;
            }
        });
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.client.TradingClient#removeBrokerStatusListener(org.marketcetera.brokers.BrokerStatusListener)
     */
    @Override
    public void removeBrokerStatusListener(BrokerStatusListener inBrokerStatusListener)
    {
        if(!listenerProxies.asMap().containsKey(inBrokerStatusListener)) {
            return;
        }
        AbstractClientListenerProxy<?,?,?> listenerProxy = listenerProxies.getUnchecked(inBrokerStatusListener);
        listenerProxies.invalidate(inBrokerStatusListener);
        listenerProxiesById.invalidate(listenerProxy.getId());
        executeCall(new Callable<Void>() {
            @Override
            public Void call()
                    throws Exception
            {
                SLF4JLoggerProxy.trace(FixAdminRpcClient.this,
                                       "{} removing broker status listener",
                                       getSessionId());
                FixAdminRpc.RemoveBrokerStatusListenerRequest.Builder requestBuilder = FixAdminRpc.RemoveBrokerStatusListenerRequest.newBuilder();
                requestBuilder.setSessionId(getSessionId().getValue());
                requestBuilder.setListenerId(listenerProxy.getId());
                FixAdminRpc.RemoveBrokerStatusListenerRequest addBrokerStatusListenerRequest = requestBuilder.build();
                SLF4JLoggerProxy.trace(FixAdminRpcClient.this,
                                       "{} sending {}",
                                       getSessionId(),
                                       addBrokerStatusListenerRequest);
                getBlockingStub().removeBrokerStatusListener(addBrokerStatusListenerRequest);
                return null;
            }
        });
    }
    /**
     * Get the activeFixSessionFactory value.
     *
     * @return a <code>MutableActiveFixSessionFactory</code> value
     */
    public MutableActiveFixSessionFactory getActiveFixSessionFactory()
    {
        return activeFixSessionFactory;
    }
    /**
     * Sets the activeFixSessionFactory value.
     *
     * @param inActiveFixSessionFactory a <code>MutableActiveFixSessionFactory</code> value
     */
    public void setActiveFixSessionFactory(MutableActiveFixSessionFactory inActiveFixSessionFactory)
    {
        activeFixSessionFactory = inActiveFixSessionFactory;
    }
    /**
     * Get the fixSessionFactory value.
     *
     * @return a <code>MutableFixSessionFactory</code> value
     */
    public MutableFixSessionFactory getFixSessionFactory()
    {
        return fixSessionFactory;
    }
    /**
     * Sets the fixSessionFactory value.
     *
     * @param inFixSessionFactory a <code>MutableFixSessionFactory</code> value
     */
    public void setFixSessionFactory(MutableFixSessionFactory inFixSessionFactory)
    {
        fixSessionFactory = inFixSessionFactory;
    }
    /**
     * Get the fixSessionAttributeDescriptorFactory value.
     *
     * @return a <code>FixSessionAttributeDescriptorFactory</code> value
     */
    public FixSessionAttributeDescriptorFactory getFixSessionAttributeDescriptorFactory()
    {
        return fixSessionAttributeDescriptorFactory;
    }
    /**
     * Sets the fixSessionAttributeDescriptorFactory value.
     *
     * @param inFixSessionAttributeDescriptorFactory a <code>FixSessionAttributeDescriptorFactory</code> value
     */
    public void setFixSessionAttributeDescriptorFactory(FixSessionAttributeDescriptorFactory inFixSessionAttributeDescriptorFactory)
    {
        fixSessionAttributeDescriptorFactory = inFixSessionAttributeDescriptorFactory;
    }
    /**
     * Get the clusterDataFactory value.
     *
     * @return a <code>ClusterDataFactory</code> value
     */
    public ClusterDataFactory getClusterDataFactory()
    {
        return clusterDataFactory;
    }
    /**
     * Sets the clusterDataFactory value.
     *
     * @param inClusterDataFactory a <code>ClusterDataFactory</code> value
     */
    public void setClusterDataFactory(ClusterDataFactory inClusterDataFactory)
    {
        clusterDataFactory = inClusterDataFactory;
    }
    /**
     * Create a new FixAdminRpcClient instance.
     *
     * @param inParameters a <code>FixAdminRpcClientParameters</code> value
     */
    FixAdminRpcClient(FixAdminRpcClientParameters inParameters)
    {
        super(inParameters);
    }
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
     * @see org.marketcetera.rpc.client.AbstractRpcClient#getBlockingStub(io.grpc.Channel)
     */
    @Override
    protected FixAdminRpcServiceBlockingStub getBlockingStub(Channel inChannel)
    {
        return FixAdminRpcServiceGrpc.newBlockingStub(inChannel);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.rpc.client.AbstractRpcClient#getAsyncStub(io.grpc.Channel)
     */
    @Override
    protected FixAdminRpcServiceStub getAsyncStub(Channel inChannel)
    {
        return FixAdminRpcServiceGrpc.newStub(inChannel);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.rpc.client.AbstractRpcClient#executeLogin(org.marketcetera.rpc.base.BaseRpc.LoginRequest)
     */
    @Override
    protected LoginResponse executeLogin(BaseRpc.LoginRequest inRequest)
    {
        return getBlockingStub().login(inRequest);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.rpc.client.AbstractRpcClient#executeLogout(org.marketcetera.rpc.base.BaseRpc.LogoutRequest)
     */
    @Override
    protected LogoutResponse executeLogout(BaseRpc.LogoutRequest inRequest)
    {
        return getBlockingStub().logout(inRequest);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.rpc.client.AbstractRpcClient#executeHeartbeat(org.marketcetera.rpc.base.BaseRpc.HeartbeatRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    protected BaseRpc.HeartbeatResponse executeHeartbeat(HeartbeatRequest inRequest)
    {
        return getBlockingStub().heartbeat(inRequest);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.rpc.client.AbstractRpcClient#onStatusChange(boolean)
     */
    @Override
    protected void onStatusChange(boolean inIsConnected)
    {
        if(inIsConnected) {
            Map<Object,AbstractClientListenerProxy<?,?,?>> existingProxies = Maps.newHashMap(listenerProxies.asMap());
            existingProxies.entrySet().forEach(listenerProxyEntry -> removeBrokerStatusListener((BrokerStatusListener)listenerProxyEntry.getKey()));
            existingProxies.entrySet().forEach(listenerProxyEntry -> addBrokerStatusListener((BrokerStatusListener)listenerProxyEntry.getKey()));
        }
    }
    /**
     * Creates the appropriate proxy for the given listener.
     *
     * @param inListener an <code>Object</code> value
     * @return an <code>AbstractListenerProxy&lt;?,?,?&gt;</code> value
     */
    private AbstractClientListenerProxy<?,?,?> getListenerFor(Object inListener)
    {
        if(inListener instanceof BrokerStatusListener) {
            return new FixSessionStatusListenerProxy((BrokerStatusListener)inListener);
        } else {
            throw new UnsupportedOperationException();
        }
    }
    /**
     * Provides an interface between session status message stream listeners and their handlers.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    private class FixSessionStatusListenerProxy
            extends BaseRpcUtil.AbstractClientListenerProxy<BrokerStatusListenerResponse,ActiveFixSession,BrokerStatusListener>
    {
        /**
         * Create a new BrokerStatusListenerProxy instance.
         *
         * @param inMessageListener a <code>BrokerStatusListener</code> value
         */
        protected FixSessionStatusListenerProxy(BrokerStatusListener inMessageListener)
        {
            super(inMessageListener);
        }
        /* (non-Javadoc)
         * @see org.marketcetera.trading.rpc.FixAdminRpcClient.AbstractListenerProxy#translateMessage(java.lang.Object)
         */
        @Override
        protected ActiveFixSession translateMessage(BrokerStatusListenerResponse inResponse)
        {
            return FixRpcUtil.getActiveFixSession(inResponse,activeFixSessionFactory,fixSessionFactory,clusterDataFactory).orElse(null);
        }
        /* (non-Javadoc)
         * @see org.marketcetera.trading.rpc.FixAdminRpcClient.AbstractListenerProxy#sendMessage(java.lang.Object, java.lang.Object)
         */
        @Override
        protected void sendMessage(BrokerStatusListener inMessageListener,
                                   ActiveFixSession inMessage)
        {
            inMessageListener.receiveBrokerStatus(inMessage);
        }
    }
    /**
     * creates {@link ClusterData} objects
     */
    private ClusterDataFactory clusterDataFactory;
    /**
     * creates {@link ActiveFixSession} objects
     */
    private MutableActiveFixSessionFactory activeFixSessionFactory;
    /**
     * creates {@link FixSession} objects
     */
    private MutableFixSessionFactory fixSessionFactory;
    /**
     * creates {@link FixSessionAttributeDescriptor} objects
     */
    private FixSessionAttributeDescriptorFactory fixSessionAttributeDescriptorFactory;
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
     * The client's application ID: the application name.
     */
    public static final String APP_ID_NAME = "FixAdminRpcClient"; //$NON-NLS-1$
    /**
     * The client's application ID: the version.
     */
    public static final VersionInfo APP_ID_VERSION = ApplicationVersion.getVersion(FixAdminClient.class);
    /**
     * The client's application ID: the ID.
     */
    public static final AppId APP_ID = Util.getAppId(APP_ID_NAME,APP_ID_VERSION.getVersionInfo());
}
