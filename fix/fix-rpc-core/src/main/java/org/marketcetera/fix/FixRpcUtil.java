package org.marketcetera.fix;

import java.util.Optional;

import org.marketcetera.algo.BrokerAlgoSpec;
import org.marketcetera.algo.BrokerAlgoTag;
import org.marketcetera.cluster.ClusterRpcUtil;
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
     * Get the value from the given RPC value.
     *
     * @param inFixSession a <code>FixAdminRpc.FixSession</code> value
     * @return an <code>Optional&lt;FixSession&gt;</code> value
     */
    public static Optional<FixSession> getFixSession(FixAdminRpc.FixSession inFixSession)
    {
        if(inFixSession == null) {
            return Optional.empty();
        }
        throw new UnsupportedOperationException(); // TODO
    }
    /**
     * Get the value from the given RPC value.
     *
     * @param inRpcFixSession a <code>FixAdminRpc.ActiveFixSession</code> value
     * @param inActiveFixSessionFactory a <code>MutableActiveFixSessionFactory</code> value
     * @return an <code>Optional&lt;ActiveFixSession&gt;</code> value
     */
    public static Optional<ActiveFixSession> getActiveFixSession(FixAdminRpc.ActiveFixSession inRpcFixSession,
                                                                 MutableActiveFixSessionFactory inActiveFixSessionFactory)
    {
        if(inRpcFixSession == null) {
            return Optional.empty();
        }
        MutableActiveFixSession activeFixSession = inActiveFixSessionFactory.create();
        inRpcFixSession.getBrokerAlgoSpecsList().stream().forEach(rpcBrokerAlgoSpec->getBrokerAlgoSpec(rpcBrokerAlgoSpec).ifPresent(brokerAlgoSpec->activeFixSession.getBrokerAlgos().add(brokerAlgoSpec)));
        if(inRpcFixSession.hasClusterData()) {
            ClusterRpcUtil.getClusterData(inRpcFixSession.getClusterData()).ifPresent(clusterData->activeFixSession.setClusterData(clusterData));
        }
        if(inRpcFixSession.hasFixSession()) {
            getFixSession(inRpcFixSession.getFixSession()).ifPresent(fixSession->activeFixSession.setFixSession(fixSession));
        }
        activeFixSession.setSenderSequenceNumber(inRpcFixSession.getSenderSeqNum());
        activeFixSession.setStatus(FixSessionStatus.values()[inRpcFixSession.getFixSessionStatus().ordinal()]);
        activeFixSession.setTargetSequenceNumber(inRpcFixSession.getTargetSeqNum());
        return Optional.of(activeFixSession);
    }
    /**
     *
     *
     * @return
     */
    public static Optional<BrokerAlgoSpec> getBrokerAlgoSpec(FixAdminRpc.BrokerAlgoSpec inRpcBrokerAlgoSpec)
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
        inActiveFixSession.getBrokerAlgos().stream().forEach(brokerAlgo->getRpcBrokerAlgo(brokerAlgo).ifPresent(rpcBrokerAlgo->builder.addBrokerAlgoSpecs(rpcBrokerAlgo)));
        if(inActiveFixSession.getClusterData() != null) {
            ClusterRpcUtil.getRpcClusterData(inActiveFixSession.getClusterData()).ifPresent(rpcClusterData->builder.setClusterData(rpcClusterData));
        }
        if(inActiveFixSession.getFixSession() != null) {
            getRpcFixSession(inActiveFixSession.getFixSession()).ifPresent(rpcFixSession->builder.setFixSession(rpcFixSession));
        }
        builder.setFixSessionStatus(FixAdminRpc.FixSessionStatus.values()[inActiveFixSession.getStatus().ordinal()]);
        builder.setSenderSeqNum(inActiveFixSession.getSenderSequenceNumber());
        builder.setTargetSeqNum(inActiveFixSession.getTargetSequenceNumber());
        return Optional.of(builder.build());
    }
    /**
     * Get the RPC value from the given value.
     *
     * @param inBrokerAlgo a <code>BrokerAlgoSpec<code> value
     * @return an <code>Optional&lt;FixAdminRpc.BrokerAlgoSpec&gt;</code> value
     */
    public static Optional<FixAdminRpc.BrokerAlgoSpec> getRpcBrokerAlgo(BrokerAlgoSpec inBrokerAlgo)
    {
        throw new UnsupportedOperationException(); // TODO
        
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
     * @param inStatus
     * @param inResponseBuilder
     */
    public static void setActiveFixSession(ActiveFixSession inStatus,
                                           FixAdminRpc.BrokerStatusListenerResponse.Builder inResponseBuilder)
    {
        throw new UnsupportedOperationException(); // TODO
        
    }
    /**
     *
     *
     * @param inRpcBrokerAlgoTag
     * @return
     */
    public static Optional<BrokerAlgoTag> getBrokerAlgoTag(FixAdminRpc.BrokerAlgoTag inRpcBrokerAlgoTag)
    {
        throw new UnsupportedOperationException(); // TODO
    }
//    /**
//     * Set the broker algos value on the given RPC builder.
//     *
//     * @param inActiveFixSession an <code>ActiveFixSession</code> value
//     * @param inBrokerStatusBuilder a <code>FixAdminRpc.BrokerStatus.Builder</code> value
//     */
//    public static void setBrokerAlgos(ActiveFixSession inActiveFixSession,
//                                      FixAdminRpc.BrokerStatus.Builder inBrokerStatusBuilder)
//    {
//        if(inActiveFixSession.getBrokerAlgos().isEmpty()) {
//            return;
//        }
//        FixAdminRpc.BrokerAlgo.Builder brokerAlgoBuilder = FixAdminRpc.BrokerAlgo.newBuilder();
//        FixAdminRpc.BrokerAlgoTagSpec.Builder brokerAlgoTagSpecBuilder = FixAdminRpc.BrokerAlgoTagSpec.newBuilder();
//        for(BrokerAlgoSpec brokerAlgo : inActiveFixSession.getBrokerAlgos()) {
//            for(BrokerAlgoTagSpec tagSpec : brokerAlgo.getAlgoTagSpecs()) {
//                brokerAlgoTagSpecBuilder.setAdvice(tagSpec.getAdvice());
//                brokerAlgoTagSpecBuilder.setDefaultValue(tagSpec.getDefaultValue());
//                brokerAlgoTagSpecBuilder.setDescription(tagSpec.getDescription());
//                brokerAlgoTagSpecBuilder.setIsReadOnly(tagSpec.isReadOnly());
//                brokerAlgoTagSpecBuilder.setLabel(tagSpec.getLabel());
//                brokerAlgoTagSpecBuilder.setMandatory(tagSpec.getIsMandatory());
//                brokerAlgoTagSpecBuilder.setOptions(BaseRpcUtil.getRpcMap(tagSpec.getOptions()));
//                brokerAlgoTagSpecBuilder.setPattern(tagSpec.getPattern());
//                brokerAlgoTagSpecBuilder.setTag(tagSpec.getTag());
//                brokerAlgoTagSpecBuilder.setValidator(tagSpec.getValidator()==null?null:tagSpec.getValidator().getClass().getName());
//                brokerAlgoBuilder.addAlgoTagSpecs(brokerAlgoTagSpecBuilder.build());
//                brokerAlgoTagSpecBuilder.clear();
//            }
//            brokerAlgoBuilder.setName(brokerAlgo.getName());
//            inBrokerStatusBuilder.addBrokerAlgos(brokerAlgoBuilder.build());
//            brokerAlgoBuilder.clear();
//        }
//    }
}
