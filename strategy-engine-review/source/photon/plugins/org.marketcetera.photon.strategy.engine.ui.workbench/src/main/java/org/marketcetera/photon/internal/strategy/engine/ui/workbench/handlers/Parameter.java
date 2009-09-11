package org.marketcetera.photon.internal.strategy.engine.ui.workbench.handlers;

import java.util.List;

import org.marketcetera.photon.strategy.engine.model.core.DeployedStrategy;
import org.marketcetera.photon.strategy.engine.model.core.StrategyEngineConnection;
import org.marketcetera.util.misc.ClassVersion;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;

/* $License$ */

/**
 * Helper for handlers that need to process multiple UI objects in the
 * background.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
class Parameter<T> {
    private final StrategyEngineConnection mConnection;
    private final T mObject;
    private final String mName;

    /**
     * @param connection
     * @param object
     * @param name
     */
    public Parameter(StrategyEngineConnection connection, T object, String name) {
        mConnection = connection;
        mObject = object;
        mName = name;
    }

    /**
     * Returns the connection.
     * 
     * @return the connection
     */
    public StrategyEngineConnection getConnection() {
        return mConnection;
    }

    /**
     * Returns the object.
     * 
     * @return the object
     */
    public T getObject() {
        return mObject;
    }

    /**
     * Returns the object name.
     * 
     * @return the name
     */
    public String getName() {
        return mName;
    }

    /**
     * Builds a list of parameters from an original list and predicate.
     * 
     * @param <T>
     *            the type
     * @param originalList
     *            the original list
     * @param predicate
     *            the predicate for filtering
     * @param function
     *            function to map from T to Parameter<T>
     * @return a list of parameters from the filtered original list
     */
    public static <T> List<Parameter<T>> build(List<T> originalList,
            Predicate<T> predicate, Function<T, Parameter<T>> function) {
        return ImmutableList.copyOf(Collections2.transform(Collections2.filter(
                originalList, predicate), function));
    }

    /**
     * Builds a list of {@link DeployedStrategy} parameters.
     * 
     * @param originalList
     *            the original list
     * @param predicate
     *            the predicate for filtering
     * @return a list of parameters from the filtered original list
     */
    public static List<Parameter<DeployedStrategy>> build(
            List<DeployedStrategy> originalList,
            Predicate<DeployedStrategy> predicate) {
        return build(originalList, predicate,
                new Function<DeployedStrategy, Parameter<DeployedStrategy>>() {
                    @Override
                    public Parameter<DeployedStrategy> apply(
                            DeployedStrategy from) {
                        return new Parameter<DeployedStrategy>(from.getEngine()
                                .getConnection(), from, from.getInstanceName());
                    }
                });
    }

}