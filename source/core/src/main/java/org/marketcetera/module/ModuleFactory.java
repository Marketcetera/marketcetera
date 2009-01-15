package org.marketcetera.module;

import org.marketcetera.util.log.I18NBoundMessage;
import org.marketcetera.util.log.ActiveLocale;
import org.marketcetera.util.misc.ClassVersion;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/* $License$ */
/**
 * A factory for creating module instance(s). Each module
 * factory must have an public constructor that doesn't
 * accept any arguments.
 * <p>
 * Instances of module factory are created automatically
 * by the {@link ModuleManager} when {@link ModuleManager#init() initializing}.
 * The module manager uses the {@link java.util.ServiceLoader} mechanism for
 * discovering <code>ModuleFactory</code> and loading implementations.
 * <p>
 * Only a single instance of <code>ModuleFactory</code> implementation exists
 * within the module manager. However, do note that multiple instances
 * may be created and then discarded when
 * {@link org.marketcetera.module.ModuleManager#refresh() reloading}
 * the factories.
 * <p>
 * If the factory does not support
 * {@link #isMultipleInstances() multiple}
 * module instances, a singleton module instance is
 * created, when the module factory is loaded, <b>if</b> the
 * factory doesn't need any parameters to create module
 * instance. If the factory does need parameters to create
 * module instance, the instance needs to manually created
 * by the user via the <code>ModuleManager</code> by supplying the
 * value of those parameters.
 * <p>
 * If the factory supports modules that can be
 * {@link #isAutoInstantiate() auto-instantiated} its
 * create method should be able to create new module
 * instances by accepting the module instance URN as the
 * only parameter to its {@link #create(Object[])} method.
 *
 * And the {@link #getParameterTypes()} method should return
 * a List that contains <code>ModuleURN.class</code> as its first element,
 * otherwise an error is thrown when initializing the factory.
 * <p>
 * {@link #isAutoInstantiate()} value is ignored when the factory
 * doesn't support multiple instances. 
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$")  //$NON-NLS-1$
public abstract class ModuleFactory {
    /**
     * Creates an instance of the factory, given the parameters.
     * <p>
     * This operation is never invoked concurrently. The module framework
     * ensures that only a single instance of this operation is active in
     * a thread at any point in time. If this operation is slow, it may delay
     * creation of other modules from the same provider. 
     *
     * @param inParameters the parameters for creating the module, the types
     * of these parameters match those returned by
     * {@link #getParameterTypes()}
     *
     * @return the module instance
     *
     * @throws ModuleCreationException if there were errors creating
     * the module instance.
     */
    public abstract Module create(Object... inParameters)
            throws ModuleCreationException;

    /**
     * Returns the list of parameter types as expected by the
     * {@link #create(Object[])} method above.
     *
     * @return the list of parameter types, cannot be null.
     */
    public final Class[] getParameterTypes() {
        return mParameterTypes.clone();
    }

    /**
     * Returns true if factory supports multiple module instances
     *
     * @return true if the factory supports multiple module instances
     */
    public final boolean isMultipleInstances() {
        return mMultipleInstances;
    }

    /**
     * Returns true if the factory supports auto-instantiated modules.
     * ie. modules that are automatically instantiated by the framework
     * when a request to create a data flow references their URN
     * (with the instance name) when they do not exist.
     * The framework extracts the instance name and supplies it to the
     * factory to instantiate the module.
     * <p>
     * If a factory supports auto-instantiated modules, it should be
     * able to create new module instances by accepting a single string
     * attribute the value of which is the module instance URN as
     * requested in the original data flow request.
     * Such factory classes's {@link #getParameterTypes()} should always
     * return a list of size 1, only containing <code>Module.class</code>.
     * 
     * This condition is verified when the factory is loaded by the
     * module framework, and if this condition is not found to be true,
     * the module framework fails to load the factory class. 
     *
     * @return if the factory supports auto-instantiated modules.
     */
    public final boolean isAutoInstantiate() {
        return mAutoInstantiate;
    }

    /**
     * Returns the name of the module provider. A module
     * provider URI has the following form.<br/>
     * <code>metc:provType:providerName</code>
     *
     * @return the provider's URN
     */
    public final ModuleURN getProviderURN() {
        return mURN;
    }

    /**
     * Returns the localized description of this module.
     *
     * @return the localized description
     */
    public final I18NBoundMessage getProviderDescription() {
        return mDescription;
    }

    /**
     * Creates an instance.
     *
     * @param inURN the provider URN.
     * @param inDescription the provider's description
     * @param inMultipleInstances if the factory supports multiple
     * module instances
     * @param inAutoInstantiate if the factory supports auto-instantiated
     * modules. This parameter is ignored if
     * <code>inSupportsMultipleInstances</code> parameter is false. If this
     * parameter is true, the factory should only need a parameter of
     * type {@link ModuleURN} when creating new instances.
     * @param inParameterTypes the types of parameters expected when creating
     * new module instances via {@link #create(Object[])}  
     */
    protected ModuleFactory(ModuleURN inURN,
                            I18NBoundMessage inDescription,
                            boolean inMultipleInstances,
                            boolean inAutoInstantiate,
                            Class... inParameterTypes) {
        mURN = inURN;
        mDescription = inDescription;
        mMultipleInstances = inMultipleInstances;
        mAutoInstantiate = inAutoInstantiate;
        mParameterTypes =
                inParameterTypes == null
                        ? new Class[0]
                        : inParameterTypes.clone();
    }

    /**
     * Returns the provider info describing the factory.
     *
     * @return the provider info for the factory.
     */
    final ProviderInfo getProviderInfo() {
        return new ProviderInfo(
                getProviderURN(),
                getParameterTypes(),
                isMultipleInstances(),
                isAutoInstantiate(),
                getProviderDescription().getText(ActiveLocale.getLocale()),
                mLock.isLocked(),
                mLock.getQueueLength());

    }

    /**
     * Returns the lock that should be used for serializing
     * factory operations.
     *
     * @return the factory lock.
     */
    final Lock getLock() {
        return mLock;
    }

    private final ReentrantLock mLock = new ReentrantLock();
    private final ModuleURN mURN;
    private final I18NBoundMessage mDescription;
    private final boolean mMultipleInstances;
    private final boolean mAutoInstantiate;
    private final Class[] mParameterTypes;
}
