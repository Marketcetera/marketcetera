package org.marketcetera.fix;

import java.util.Map;
import java.util.Optional;

import org.marketcetera.cluster.InstanceData;
import org.marketcetera.rpc.base.BaseRpcUtil;

/* $License$ */

/**
 *
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
     * @param inInstanceData
     * @return
     */
    public static InstanceData getInstanceData(FixAdminRpc.InstanceData inInstanceData)
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
        builder.setEnabled(inActiveFixSession.isEnabled());
        builder.setInstance(inActiveFixSession.getHost());
//        builder.setSenderSeqNum(inFixSession.get)
//        builder.setStatus(inFixSession.getS)
        return Optional.of(builder.build());
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
        builder.setMappedBrokerId(inFixSession.getMappedBrokerId());
        builder.setName(inFixSession.getName());
        builder.setPort(inFixSession.getPort());
        builder.setSessionId(inFixSession.getSessionId());
        builder.setSessionSettings(BaseRpcUtil.getRpcMap(inFixSession.getSessionSettings()));
        return Optional.of(builder.build());
    }
    /**
     *
     *
     * @param inAcceptorSessionAttributes
     * @return
     */
    public static FixAdminRpc.InstanceData getRpcInstanceData(AcceptorSessionAttributes inAcceptorSessionAttributes)
    {
        throw new UnsupportedOperationException(); // TODO
        
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
}
