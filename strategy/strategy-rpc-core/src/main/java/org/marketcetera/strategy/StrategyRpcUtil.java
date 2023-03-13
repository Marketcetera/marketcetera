//
// this file is automatically generated
//
package org.marketcetera.strategy;

import java.util.Optional;

import org.marketcetera.admin.UserFactory;
import org.marketcetera.core.Preserve;
import org.marketcetera.strategy.events.SimpleStrategyStatusChangedEvent;
import org.marketcetera.strategy.events.SimpleStrategyUnloadedEvent;
import org.marketcetera.strategy.events.SimpleStrategyUploadFailedEvent;
import org.marketcetera.strategy.events.SimpleStrategyUploadSucceededEvent;
import org.marketcetera.strategy.events.StrategyEvent;
import org.marketcetera.strategy.events.StrategyStatusChangedEvent;
import org.marketcetera.strategy.events.StrategyUnloadedEvent;
import org.marketcetera.strategy.events.StrategyUploadFailedEvent;
import org.marketcetera.strategy.events.StrategyUploadSucceededEvent;

/* $License$ */

/**
 * Provides common behavior for Strategy services.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Preserve
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
    /**
     *
     *
     * @param inStrategyEvent
     * @param inResponseBuilder
     */
    public static void setStrategyEvent(StrategyEvent inStrategyEvent,
                                        StrategyRpc.StrategyEventListenerResponse.Builder inResponseBuilder)
    {
        StrategyTypesRpc.StrategyEvent.Builder rpcEventBuilder = StrategyTypesRpc.StrategyEvent.newBuilder();
        rpcEventBuilder.setEventType(inStrategyEvent.getClass().getSimpleName());
        if(inStrategyEvent.getStrategyInstance() != null) {
            getRpcStrategyInstance(inStrategyEvent.getStrategyInstance()).ifPresent(rpcStrategyInstance -> rpcEventBuilder.setStrategyInstance(rpcStrategyInstance));
        }
        rpcEventBuilder.setEventType(inStrategyEvent.getClass().getSimpleName());
        if(inStrategyEvent instanceof StrategyUploadFailedEvent) {
            StrategyUploadFailedEvent failedEvent = (StrategyUploadFailedEvent)inStrategyEvent;
            rpcEventBuilder.setMessage(failedEvent.getErrorMessage());
        } else if(inStrategyEvent instanceof StrategyUploadSucceededEvent) {
        } else if(inStrategyEvent instanceof StrategyUnloadedEvent) {
        } else if(inStrategyEvent instanceof StrategyStatusChangedEvent) {
            StrategyStatusChangedEvent statusChangedEvent = (StrategyStatusChangedEvent)inStrategyEvent;
            getRpcStrategyStatus(statusChangedEvent.getNewValue()).ifPresent(rpcStrategyStatus -> rpcEventBuilder.setNewStatusValue(rpcStrategyStatus));
            getRpcStrategyStatus(statusChangedEvent.getOldValue()).ifPresent(rpcStrategyStatus -> rpcEventBuilder.setOldStatusValue(rpcStrategyStatus));
        } else {
            throw new UnsupportedOperationException("Unexpected strategy event type: " + inStrategyEvent.getClass().getSimpleName());
        }
        inResponseBuilder.setEvent(rpcEventBuilder.build());
    }
    /**
     *
     *
     * @param inResponse
     * @return
     */
    public static StrategyEvent getStrategyEvent(StrategyRpc.StrategyEventListenerResponse inResponse,
                                                 StrategyInstanceFactory inStrategyInstanceFactory,
                                                 UserFactory inUserFactory)
    {
        if(inResponse.hasEvent()) {
            StrategyTypesRpc.StrategyEvent rpcEvent = inResponse.getEvent();
            if(rpcEvent.hasStrategyInstance()) {
                Optional<? extends StrategyInstance> strategyInstanceOption = getStrategyInstance(rpcEvent.getStrategyInstance(),
                                                                                                  inStrategyInstanceFactory,
                                                                                                  inUserFactory);
                if(strategyInstanceOption.isPresent()) {
                    StrategyInstance strategyInstance = strategyInstanceOption.get();
                    switch(rpcEvent.getEventType()) {
                        case "SimpleStrategyUploadFailedEvent":
                            SimpleStrategyUploadFailedEvent failedEvent = new SimpleStrategyUploadFailedEvent();
                            failedEvent.setErrorMessage(rpcEvent.getMessage());
                            failedEvent.setStrategyInstance(strategyInstance);
                            return failedEvent;
                        case "SimpleStrategyUploadSucceededEvent":
                            SimpleStrategyUploadSucceededEvent succeededEvent = new SimpleStrategyUploadSucceededEvent();
                            succeededEvent.setStrategyInstance(strategyInstance);
                            return succeededEvent;
                        case "SimpleStrategyStatusChangedEvent":
                            SimpleStrategyStatusChangedEvent statusChangedEvent = new SimpleStrategyStatusChangedEvent();
                            statusChangedEvent.setStrategyInstance(strategyInstance);
                            getStrategyStatus(rpcEvent.getNewStatusValue()).ifPresent(strategyStatus -> statusChangedEvent.setNewValue(strategyStatus));
                            getStrategyStatus(rpcEvent.getOldStatusValue()).ifPresent(strategyStatus -> statusChangedEvent.setOldValue(strategyStatus));
                            return statusChangedEvent;
                        case "SimpleStrategyUnloadedEvent":
                            SimpleStrategyUnloadedEvent unloadedEvent = new SimpleStrategyUnloadedEvent();
                            unloadedEvent.setStrategyInstance(strategyInstance);
                            return unloadedEvent;
                        default:
                            throw new UnsupportedOperationException("Unexpected strategy event type: " + rpcEvent.getEventType());
                    }
                }
            }
        }
        return null;
    }
}
