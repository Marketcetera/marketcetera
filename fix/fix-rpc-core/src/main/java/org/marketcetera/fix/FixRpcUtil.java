package org.marketcetera.fix;

import java.util.Collection;
import java.util.Optional;

import org.marketcetera.fix.FixAdminRpc.BrokerStatus;
import org.marketcetera.fix.FixAdminRpc.BrokerStatusListenerResponse;
import org.marketcetera.rpc.base.BaseRpcUtil;

/* $License$ */

/**
 * Provides FIX RPC utilities.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class FixRpcUtil
{
    /**
     *
     *
     * @param inFixSession
     * @return
     */
    public static FixSession getFixSession(FixAdminRpc.FixSession inFixSession)
    {
        throw new UnsupportedOperationException(); // TODO
    }
    /**
     *
     *
     * @param inRpcFixSession
     * @return
     */
    public static ActiveFixSession getActiveFixSession(FixAdminRpc.ActiveFixSession inRpcFixSession)
    {
        throw new UnsupportedOperationException(); // TODO
        
    }
    /**
     *
     *
     * @param inRpcFixSessionAttributeDescriptor
     * @return
     */
    public static FixSessionAttributeDescriptor getFixSessionAttributeDescriptor(FixAdminRpc.FixSessionAttributeDescriptor inRpcFixSessionAttributeDescriptor)
    {
        throw new UnsupportedOperationException(); // TODO
    }
    /**
     * Get the RPC value from the given value.
     *
     * @param inActiveFixSession an <code>ActiveFixSession</code> value
     * @return an <code>Optional&lt;FixAdminRpc.ActiveFixSession&gt;</code> value
     */
    public static Optional<FixAdminRpc.ActiveFixSession> getRpcActiveFixSession(ActiveFixSession inActiveFixSession)
    {
        if(inActiveFixSession == null) {
            return Optional.empty();
        }
        FixAdminRpc.ActiveFixSession.Builder builder = FixAdminRpc.ActiveFixSession.newBuilder();
//        builder.setEnabled(inActiveFixSession.isEnabled());
//        builder.setInstance(inActiveFixSession.getHost());
//        builder.setSenderSeqNum(inActiveFixSession.getSenderSequenceNumber());
//        FixRpcUtil.getRpcFixSessionStatus(inActiveFixSession.getStatus()).ifPresent(rpcFixSessionStatus->builder.setStatus(rpcFixSessionStatus));
//        getRpcFixSession(inActiveFixSession).ifPresent(rpcFixSession->builder.setFixSession(rpcFixSession));
        return Optional.of(builder.build());
    }
    /**
     * Get the RPC value from the given value.
     *
     * @param inStatus a <code>FixSessionStatus</code> value
     * @return an <code>Optional&lt;String&gt;</code> value
     */
    public static Optional<String> getRpcFixSessionStatus(FixSessionStatus inStatus)
    {
        if(inStatus == null) {
            return Optional.empty();
        }
        return Optional.of(inStatus.name());
    }
    /**
     * Get the RPC value from the given value.
     *
     * @param inFixSession a <code>FixSession</code> value
     * @return an <code>Optional&lt;FixAdminRpc.FixSession&gt;</code> value
     */
    public static Optional<FixAdminRpc.FixSession> getRpcFixSession(FixSession inFixSession)
    {
        if(inFixSession == null) {
            return Optional.empty();
        }
        FixAdminRpc.FixSession.Builder builder = FixAdminRpc.FixSession.newBuilder();
        builder.setAcceptor(inFixSession.isAcceptor());
        builder.setAffinity(inFixSession.getAffinity());
        builder.setBrokerId(inFixSession.getBrokerId());
        builder.setDescription(inFixSession.getDescription());
        builder.setHost(inFixSession.getHost());
        if(inFixSession.getMappedBrokerId() != null) { 
            builder.setMappedBrokerId(inFixSession.getMappedBrokerId());
        }
        builder.setName(inFixSession.getName());
        builder.setPort(inFixSession.getPort());
        builder.setSessionId(inFixSession.getSessionId());
        builder.setSessionSettings(BaseRpcUtil.getRpcMap(inFixSession.getSessionSettings()));
        return Optional.of(builder.build());
    }
    /**
     *
     *
     * @param inDescriptor
     * @return
     */
    public static FixAdminRpc.FixSessionAttributeDescriptor getRpcFixSessionAttributeDescriptor(FixSessionAttributeDescriptor inDescriptor)
    {
        throw new UnsupportedOperationException(); // TODO
    }
    /**
     *
     *
     * @param inStatus
     * @param inResponseBuilder
     */
    public static void setBrokerStatus(BrokerStatus inStatus,
                                       org.marketcetera.fix.FixAdminRpc.BrokerStatusListenerResponse.Builder inResponseBuilder)
    {
        throw new UnsupportedOperationException(); // TODO
        
    }
    /**
     *
     *
     * @param inResponse
     * @return
     */
    public static Optional<FixSessionStatus> getBrokerStatus(BrokerStatusListenerResponse inResponse)
    {
        throw new UnsupportedOperationException(); // TODO
        
    }
    /**
     *
     *
     * @param inRpcBrokerStatus
     * @return
     */
    public static Optional<ActiveFixSession> getActiveFixSession(org.marketcetera.fix.FixAdminRpc.BrokerStatus inRpcBrokerStatus)
    {
        throw new UnsupportedOperationException(); // TODO
        
    }
    /**
     *
     *
     * @param inResponse
     * @return
     */
    public static Optional<ActiveFixSession> getActiveFixSession(BrokerStatusListenerResponse inResponse)
    {
        throw new UnsupportedOperationException(); // TODO
        
    }
    /**
     *
     *
     * @param inActiveFixSessions
     * @param inResponseBuilder
     */
    public static void setActiveFixSessions(Collection<ActiveFixSession> inActiveFixSessions,
                                            FixAdminRpc.BrokersStatusResponse.Builder inResponseBuilder)
    {
        throw new UnsupportedOperationException(); // TODO
        
    }
    /**
     *
     *
     * @param inStatus
     * @param inResponseBuilder
     */
    public static void setActiveFixSession(ActiveFixSession inStatus,
                                           FixAdminRpc.BrokerStatusListenerResponse.Builder inResponseBuilder)
    {
        throw new UnsupportedOperationException(); // TODO
        
    }
}
