package org.marketcetera.fix;

import java.util.Optional;

import org.marketcetera.algo.BrokerAlgo;
import org.marketcetera.algo.BrokerAlgoSpec;
import org.marketcetera.algo.BrokerAlgoTag;
import org.marketcetera.algo.BrokerAlgoTagSpec;
import org.marketcetera.cluster.ClusterRpcUtil;
import org.marketcetera.core.Validator;
import org.marketcetera.rpc.base.BaseRpcUtil;
import org.marketcetera.util.log.SLF4JLoggerProxy;

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
     * @param inRpcFixSession a <code>FixAdminRpc.FixSession</code> value
     * @param inFixSessionFactory a <code>MutableFixSessionFactory</code> value
     * @return an <code>Optional&lt;FixSession&gt;</code> value
     */
    public static Optional<FixSession> getFixSession(FixAdminRpc.FixSession inRpcFixSession,
                                                     MutableFixSessionFactory inFixSessionFactory)
    {
        if(inRpcFixSession == null) {
            return Optional.empty();
        }
        MutableFixSession fixSession = inFixSessionFactory.create();
        fixSession.setAffinity(inRpcFixSession.getAffinity());
        fixSession.setBrokerId(inRpcFixSession.getBrokerId());
        fixSession.setDescription(inRpcFixSession.getDescription());
        fixSession.setHost(inRpcFixSession.getHost());
        fixSession.setIsAcceptor(inRpcFixSession.getAcceptor());
        fixSession.setIsEnabled(inRpcFixSession.getAcceptor());
        fixSession.setMappedBrokerId(inRpcFixSession.getMappedBrokerId());
        fixSession.setName(inRpcFixSession.getName());
        fixSession.setPort(inRpcFixSession.getPort());
        fixSession.setSessionId(inRpcFixSession.getSessionId());
        fixSession.getSessionSettings().putAll(BaseRpcUtil.getMap(inRpcFixSession.getSessionSettings()));
        return Optional.of(fixSession);
    }
    /**
     * Get the value from the given RPC value.
     *
     * @param inRpcFixSession a <code>FixAdminRpc.ActiveFixSession</code> value
     * @param inActiveFixSessionFactory a <code>MutableActiveFixSessionFactory</code> value
     * @return an <code>Optional&lt;ActiveFixSession&gt;</code> value
     */
    public static Optional<ActiveFixSession> getActiveFixSession(FixAdminRpc.ActiveFixSession inRpcFixSession,
                                                                 MutableActiveFixSessionFactory inActiveFixSessionFactory,
                                                                 MutableFixSessionFactory inFixSessionFactory)
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
            getFixSession(inRpcFixSession.getFixSession(),inFixSessionFactory).ifPresent(fixSession->activeFixSession.setFixSession(fixSession));
        }
        activeFixSession.setSenderSequenceNumber(inRpcFixSession.getSenderSeqNum());
        activeFixSession.setStatus(FixSessionStatus.values()[inRpcFixSession.getFixSessionStatus().ordinal()]);
        activeFixSession.setTargetSequenceNumber(inRpcFixSession.getTargetSeqNum());
        return Optional.of(activeFixSession);
    }
    /**
     * Get the value from the given RPC value.
     *
     * @param inRpcBrokerAlgoSpec a <code>FixAdminRpc.BrokerAlgoSpec</code> value
     * @return an <code>Optional&lt;BrokerAlgoSpec&gt;</code> value
     */
    @SuppressWarnings("unchecked")
    public static Optional<BrokerAlgoSpec> getBrokerAlgoSpec(FixAdminRpc.BrokerAlgoSpec inRpcBrokerAlgoSpec)
    {
        if(inRpcBrokerAlgoSpec == null) {
            return Optional.empty();
        }
        BrokerAlgoSpec brokerAlgoSpec = new BrokerAlgoSpec();
        inRpcBrokerAlgoSpec.getBrokerAlgoTagSpecsList().stream()
            .forEach(rpcBrokerAlgoTag->getBrokerAlgoTagSpec(rpcBrokerAlgoTag)
            .ifPresent(brokerAlgoTagSpec->brokerAlgoSpec.getAlgoTagSpecs().add(brokerAlgoTagSpec)));
        brokerAlgoSpec.setName(inRpcBrokerAlgoSpec.getName());
        if(inRpcBrokerAlgoSpec.getValidator() != null) {
            String validatorName = inRpcBrokerAlgoSpec.getValidator();
            try {
                Class<?> validatorClass = Class.forName(validatorName);
                brokerAlgoSpec.setValidator((Validator<BrokerAlgo>)validatorClass.newInstance());
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
                SLF4JLoggerProxy.warn(FixRpcUtil.class,
                                      e,
                                      "Unable to instantiate validator of class {} for {}",
                                      validatorName,
                                      inRpcBrokerAlgoSpec);
            }
        }
        return Optional.of(brokerAlgoSpec);
    }
    /**
     * Get the value from the given RPC value.
     *
     * @param inRpcBrokerAlgoTag a <code>FixAdminRpc.BrokerAlgoTagSpec</code> value
     * @return an <code>Optional&lt;BrokerAlgoTagSpec&gt;</code> value
     */
    @SuppressWarnings("unchecked")
    public static Optional<BrokerAlgoTagSpec> getBrokerAlgoTagSpec(FixAdminRpc.BrokerAlgoTagSpec inRpcBrokerAlgoTag)
    {
        if(inRpcBrokerAlgoTag == null) {
            return Optional.empty();
        }
        BrokerAlgoTagSpec brokerAlgoTagSpec = new BrokerAlgoTagSpec();
        brokerAlgoTagSpec.setAdvice(inRpcBrokerAlgoTag.getAdvice());
        brokerAlgoTagSpec.setDefaultValue(inRpcBrokerAlgoTag.getDefaultValue());
        brokerAlgoTagSpec.setDescription(inRpcBrokerAlgoTag.getDescription());
        brokerAlgoTagSpec.setIsMandatory(inRpcBrokerAlgoTag.getMandatory());
        brokerAlgoTagSpec.setIsReadOnly(inRpcBrokerAlgoTag.getIsReadOnly());
        brokerAlgoTagSpec.setLabel(inRpcBrokerAlgoTag.getLabel());
        brokerAlgoTagSpec.setOptions(BaseRpcUtil.getMap(inRpcBrokerAlgoTag.getOptions()));
        brokerAlgoTagSpec.setPattern(inRpcBrokerAlgoTag.getPattern());
        brokerAlgoTagSpec.setTag(inRpcBrokerAlgoTag.getTag());
        if(inRpcBrokerAlgoTag.getValidator() != null) {
            String validatorName = inRpcBrokerAlgoTag.getValidator();
            try {
                Class<?> validatorClass = Class.forName(validatorName);
                brokerAlgoTagSpec.setValidator((Validator<BrokerAlgoTag>)validatorClass.newInstance());
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
                SLF4JLoggerProxy.warn(FixRpcUtil.class,
                                      e,
                                      "Unable to instantiate validator of class {} for {}",
                                      validatorName,
                                      inRpcBrokerAlgoTag);
            }
        }
        return Optional.of(brokerAlgoTagSpec);
    }
    /**
     * Get the value from the given RPC value.
     *
     * @param inRpcFixSessionAttributeDescriptor a <code>FixAdminRpc.FixSessionAttributeDescriptor</code> value
     * @return an <code>Optional&lt;FixSessionAttributeDescriptor&gt;</code> value
     */
    public static Optional<FixSessionAttributeDescriptor> getFixSessionAttributeDescriptor(FixAdminRpc.FixSessionAttributeDescriptor inRpcFixSessionAttributeDescriptor,
                                                                                           FixSessionAttributeDescriptorFactory inFixSessionAttributeDescriptorFactory)
    {
        if(inRpcFixSessionAttributeDescriptor == null) {
            return Optional.empty();
        }
        FixSessionAttributeDescriptor fixSessionAttributeDescriptor = inFixSessionAttributeDescriptorFactory.create(inRpcFixSessionAttributeDescriptor.getName(),
                                                                                                                    inRpcFixSessionAttributeDescriptor.getDescription(),
                                                                                                                    inRpcFixSessionAttributeDescriptor.getDefaultValue(),
                                                                                                                    inRpcFixSessionAttributeDescriptor.getPattern(),
                                                                                                                    inRpcFixSessionAttributeDescriptor.getRequired());
        return Optional.of(fixSessionAttributeDescriptor);
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
     * @param inBrokerAlgoSpec a <code>BrokerAlgoSpec<code> value
     * @return an <code>Optional&lt;FixAdminRpc.BrokerAlgoSpec&gt;</code> value
     */
    public static Optional<FixAdminRpc.BrokerAlgoSpec> getRpcBrokerAlgo(BrokerAlgoSpec inBrokerAlgoSpec)
    {
        if(inBrokerAlgoSpec == null) {
            return Optional.empty();
        }
        FixAdminRpc.BrokerAlgoSpec.Builder builder = FixAdminRpc.BrokerAlgoSpec.newBuilder();
        inBrokerAlgoSpec.getAlgoTagSpecs().stream()
            .forEach(algoTagSpec->getRpcBrokerAlgoTagSpec(algoTagSpec)
            .ifPresent(rpcAlgoTagSpec->builder.addBrokerAlgoTagSpecs(rpcAlgoTagSpec)));
        builder.setName(inBrokerAlgoSpec.getName());
        if(inBrokerAlgoSpec.getValidator() != null) {
            builder.setValidator(inBrokerAlgoSpec.getValidator().getClass().getName());
        }
        return Optional.of(builder.build());
    }
    /**
     * Get the RPC value from the given value.
     *
     * @param inAlgoTagSpec a <code>BrokerAlgoTagSpec</code> value
     * @return an <code>Optional&lt;FixAdminRpc.BrokerAlgoTagSpec&gt;</code> value
     */
    public static Optional<FixAdminRpc.BrokerAlgoTagSpec> getRpcBrokerAlgoTagSpec(BrokerAlgoTagSpec inAlgoTagSpec)
    {
        if(inAlgoTagSpec == null) {
            return Optional.empty();
        }
        FixAdminRpc.BrokerAlgoTagSpec.Builder builder = FixAdminRpc.BrokerAlgoTagSpec.newBuilder();
        builder.setAdvice(inAlgoTagSpec.getAdvice());
        builder.setDefaultValue(inAlgoTagSpec.getDefaultValue());
        builder.setDescription(inAlgoTagSpec.getDescription());
        builder.setIsReadOnly(inAlgoTagSpec.isReadOnly());
        builder.setLabel(inAlgoTagSpec.getLabel());
        builder.setMandatory(inAlgoTagSpec.getIsMandatory());
        builder.setPattern(inAlgoTagSpec.getPattern());
        builder.setTag(inAlgoTagSpec.getTag());
        if(inAlgoTagSpec.getValidator() != null) {
            builder.setValidator(inAlgoTagSpec.getValidator().getClass().getName());
        }
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
     * Get the RPC value from the given value.
     *
     * @param inDescriptor a <code>FixSessionAttributeDescriptor</code> value
     * @return an <code>Optional&lt;FixAdminRpc.FixSessionAttributeDescriptor&gt;</code> value
     */
    public static Optional<FixAdminRpc.FixSessionAttributeDescriptor> getRpcFixSessionAttributeDescriptor(FixSessionAttributeDescriptor inDescriptor)
    {
        if(inDescriptor == null) {
            return Optional.empty();
        }
        FixAdminRpc.FixSessionAttributeDescriptor.Builder builder = FixAdminRpc.FixSessionAttributeDescriptor.newBuilder();
        builder.setAdvice(inDescriptor.getAdvice());
        builder.setDefaultValue(inDescriptor.getDefaultValue());
        builder.setDescription(inDescriptor.getDescription());
        builder.setName(inDescriptor.getName());
        builder.setPattern(inDescriptor.getPattern());
        builder.setRequired(inDescriptor.isRequired());
        return Optional.of(builder.build());
    }
    /**
     * Get the value from the given RPC value.
     *
     * @param inResponse a <code>FixAdminRpc.BrokerStatusListenerResponse</code> value
     * @param inActiveFixSessionFactory a <code>MutableActiveFixSessionFactory</code> value
     * @param inFixSessionFactory a <code>MutableFixSessionFactory</code> value
     * @return an <code>Optional&lt;ActiveFixSession&gt;</code> value
     */
    public static Optional<ActiveFixSession> getActiveFixSession(FixAdminRpc.BrokerStatusListenerResponse inResponse,
                                                                 MutableActiveFixSessionFactory inActiveFixSessionFactory,
                                                                 MutableFixSessionFactory inFixSessionFactory)
    {
        if(inResponse == null || !inResponse.hasActiveFixSession()) {
            return Optional.empty();
        }
        FixAdminRpc.ActiveFixSession rpcActiveFixSession = inResponse.getActiveFixSession();
        MutableActiveFixSession activeFixSession = inActiveFixSessionFactory.create();
        rpcActiveFixSession.getBrokerAlgoSpecsList().stream()
            .forEach(rpcBrokerAlgoSpec->getBrokerAlgoSpec(rpcBrokerAlgoSpec)
            .ifPresent(brokerAlgoSpec->activeFixSession.getBrokerAlgos().add(brokerAlgoSpec)));
        if(rpcActiveFixSession.hasClusterData()) {
            ClusterRpcUtil.getClusterData(rpcActiveFixSession.getClusterData()).ifPresent(clusterData->activeFixSession.setClusterData(clusterData));
        }
        if(rpcActiveFixSession.hasFixSession()) {
            getFixSession(rpcActiveFixSession.getFixSession(),inFixSessionFactory).ifPresent(fixSession->activeFixSession.setFixSession(fixSession));
        }
        activeFixSession.setSenderSequenceNumber(rpcActiveFixSession.getSenderSeqNum());
        activeFixSession.setStatus(FixSessionStatus.values()[rpcActiveFixSession.getFixSessionStatus().ordinal()]);
        activeFixSession.setTargetSequenceNumber(rpcActiveFixSession.getTargetSeqNum());
        return Optional.of(activeFixSession);
    }
    /**
     * Set the given active FIX session on the given builder.
     *
     * @param inActiveFixSession an <code>ActiveFixSession</code> value
     * @param inResponseBuilder a <code>FixAdminRpc.BrokerStatusListenerResponse.Builder</code> value
     */
    public static void setActiveFixSession(ActiveFixSession inActiveFixSession,
                                           FixAdminRpc.BrokerStatusListenerResponse.Builder inResponseBuilder)
    {
        if(inActiveFixSession == null) {
            return;
        }
        getRpcActiveFixSession(inActiveFixSession).ifPresent(rpcActiveFixSession->inResponseBuilder.setActiveFixSession(rpcActiveFixSession));
    }
    /**
     * Get the value from the given RPC value.
     *
     * @param inRpcBrokerAlgoTag a <code>FixAdminRpc.BrokerAlgoTag</code> value
     * @return an <code>Optional&lt;BrokerAlgoTag&gt;</code> value
     */
    public static Optional<BrokerAlgoTag> getBrokerAlgoTag(FixAdminRpc.BrokerAlgoTag inRpcBrokerAlgoTag)
    {
        if(inRpcBrokerAlgoTag == null) {
            return Optional.empty();
        }
        BrokerAlgoTag brokerAlgoTag = new BrokerAlgoTag();
        if(inRpcBrokerAlgoTag.hasBrokerAlgoTagSpec()) {
            getBrokerAlgoTagSpec(inRpcBrokerAlgoTag.getBrokerAlgoTagSpec()).ifPresent(brokerAlgoTagSpec->brokerAlgoTag.setTagSpec(brokerAlgoTagSpec));
        }
        brokerAlgoTag.setValue(inRpcBrokerAlgoTag.getValue());
        return Optional.of(brokerAlgoTag);
    }
}
