package org.marketcetera.photon.strategy.engine.model.core.util;

import org.marketcetera.photon.strategy.engine.model.core.Strategy;
import org.marketcetera.util.misc.ClassVersion;

import com.google.common.collect.ImmutableMap;

/* $License$ */

/**
 * An immutable version of {@link Strategy}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public class ImmutableStrategy {

    private final String mScriptPath;
    private final String mClassName;
    private final String mLanguage;
    private final String mInstanceName;
    private final boolean mRouteOrdersToServer;
    private final ImmutableMap<String, String> mParameters;

    /**
     * Constructor.
     * 
     * @param strategy
     *            the mutable Strategy from which to initialize the object
     */
    public ImmutableStrategy(Strategy strategy) {
        mScriptPath = strategy.getScriptPath();
        mClassName = strategy.getClassName();
        mLanguage = strategy.getLanguage();
        mInstanceName = strategy.getInstanceName();
        mRouteOrdersToServer = strategy.isRouteOrdersToServer();
        mParameters = ImmutableMap.copyOf(strategy.getParameters().map());
    }

    /**
     * Returns the script path.
     * 
     * @return the script path
     */
    public final String getScriptPath() {
        return mScriptPath;
    }

    /**
     * Returns the class name.
     * 
     * @return the class name
     */
    public final String getClassName() {
        return mClassName;
    }

    /**
     * Returns the language.
     * 
     * @return the language
     */
    public final String getLanguage() {
        return mLanguage;
    }

    /**
     * Returns the instance name.
     * 
     * @return the instance name
     */
    public final String getInstanceName() {
        return mInstanceName;
    }

    /**
     * Returns whether to route orders to server.
     * 
     * @return whether to route orders to server
     */
    public final boolean isRouteOrdersToServer() {
        return mRouteOrdersToServer;
    }

    /**
     * Returns the parameters.
     * 
     * @return the parameters
     */
    public final ImmutableMap<String, String> getParameters() {
        return mParameters;
    }

    /**
     * Applies this object's state to the provided mutable {@link Strategy}.
     * 
     * @param strategy
     *            the strategy to fill
     * @return the same strategy passed in
     */
    public Strategy fill(Strategy strategy) {
        strategy.setScriptPath(mScriptPath);
        strategy.setClassName(mClassName);
        strategy.setLanguage(mLanguage);
        strategy.setInstanceName(mInstanceName);
        strategy.setRouteOrdersToServer(mRouteOrdersToServer);
        strategy.getParameters().putAll(mParameters);
        return strategy;
    }
}
