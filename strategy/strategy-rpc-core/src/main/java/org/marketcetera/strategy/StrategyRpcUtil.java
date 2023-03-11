//
// this file is automatically generated
//
package org.marketcetera.strategy;

/* $License$ */

/**
 * Provides common behavior for Strategy services.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public abstract class StrategyRpcUtil
{
    /**
     * Get the RPC object from the given value.
     *
     * @param inStrategyInstance a <code>org.marketcetera.strategy.StrategyInstance</code> value
     * @return a java.util.Optional<StrategyTypesRpc.StrategyInstance> value
     */
    public static java.util.Optional<StrategyTypesRpc.StrategyInstance> getRpcStrategyInstance(org.marketcetera.strategy.StrategyInstance inStrategyInstance)
    {
        if(inStrategyInstance == null) {
            return java.util.Optional.empty();
        }
        StrategyTypesRpc.StrategyInstance.Builder builder = StrategyTypesRpc.StrategyInstance.newBuilder();
        org.marketcetera.admin.rpc.AdminRpcUtil.getRpcUser(inStrategyInstance.getUser()).ifPresent(value->builder.setUser(value));
        if(inStrategyInstance.getName() != null) {
            builder.setName(inStrategyInstance.getName());
        }
        if(inStrategyInstance.getFilename() != null) {
            builder.setFilename(inStrategyInstance.getFilename());
        }
        if(inStrategyInstance.getHash() != null) {
            builder.setHash(inStrategyInstance.getHash());
        }
        if(inStrategyInstance.getNonce() != null) {
            builder.setNonce(inStrategyInstance.getNonce());
        }
        org.marketcetera.rpc.base.BaseRpcUtil.getTimestampValue(inStrategyInstance.getStarted()).ifPresent(value->builder.setStarted(value));
        getRpcStrategyStatus(inStrategyInstance.getStatus()).ifPresent(value->builder.setStatus(value));
        return java.util.Optional.of(builder.build());
    }
    /**
     * Get the object from the given RPC value.
     *
     * @param inStrategyStatusan <code>org.marketcetera.strategy.StrategyTypesRpc.StrategyStatus</code> value
     * @return an org.marketcetera.strategy.StrategyStatus value
     */
    public static java.util.Optional<org.marketcetera.strategy.StrategyStatus> getStrategyStatus(org.marketcetera.strategy.StrategyTypesRpc.StrategyStatus inStrategyStatus)
    {
        if(inStrategyStatus == null) {
            return java.util.Optional.empty();
        }
        return java.util.Optional.of(org.marketcetera.strategy.StrategyStatus.values()[inStrategyStatus.getNumber()]);
    }
    /**
     * Get the RPC value from the given object.
     *
     * @param inRpcStrategyStatus a <code>org.marketcetera.strategy.StrategyStatus</code> value
     * @return an org.marketcetera.strategy.StrategyTypesRpc.StrategyStatus value
     */
    public static java.util.Optional<org.marketcetera.strategy.StrategyTypesRpc.StrategyStatus> getRpcStrategyStatus(org.marketcetera.strategy.StrategyStatus inRpcStrategyStatus)
    {
        if(inRpcStrategyStatus == null) {
            return java.util.Optional.empty();
        }
        return java.util.Optional.of(org.marketcetera.strategy.StrategyTypesRpc.StrategyStatus.forNumber(inRpcStrategyStatus.ordinal()));
    }
    /**
     * Get the object from the given RPC value.
     *
     * @param inStrategyInstance an <code>org.marketcetera.strategy.StrategyTypesRpc.StrategyInstance</code> value
     * @param inStrategyInstanceFactory an <code>org.marketcetera.strategy.StrategyInstanceFactory</code> value
     * @param inUserFactory an <code>org.marketcetera.admin.UserFactory</code> value
     * @return an org.marketcetera.strategy.StrategyInstance value
     */
    public static java.util.Optional<org.marketcetera.strategy.StrategyInstance> getStrategyInstance(org.marketcetera.strategy.StrategyTypesRpc.StrategyInstance inStrategyInstance,org.marketcetera.strategy.StrategyInstanceFactory inStrategyInstanceFactory,org.marketcetera.admin.UserFactory inUserFactory)
    {
        if(inStrategyInstance == null) {
            return java.util.Optional.empty();
        }
        org.marketcetera.strategy.StrategyInstance strategyInstance = inStrategyInstanceFactory.create();
        org.marketcetera.admin.rpc.AdminRpcUtil.getUser(inStrategyInstance.getUser(),inUserFactory).ifPresent(value->strategyInstance.setUser(value));
        strategyInstance.setName(inStrategyInstance.getName());
        strategyInstance.setFilename(inStrategyInstance.getFilename());
        strategyInstance.setHash(inStrategyInstance.getHash());
        strategyInstance.setNonce(inStrategyInstance.getNonce());
        org.marketcetera.rpc.base.BaseRpcUtil.getDateValue(inStrategyInstance.getStarted()).ifPresent(value->strategyInstance.setStarted(value));
        getStrategyStatus(inStrategyInstance.getStatus()).ifPresent(value->strategyInstance.setStatus(value));
        return java.util.Optional.of(strategyInstance);
    }
}
