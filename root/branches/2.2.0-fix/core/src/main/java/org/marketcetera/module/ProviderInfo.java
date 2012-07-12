package org.marketcetera.module;

import org.marketcetera.util.misc.ClassVersion;

import java.util.List;
import java.util.ArrayList;
import java.io.Serializable;
import java.beans.ConstructorProperties;

/* $License$ */
/**
 * This class provides detailed information on a module provider.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$")  //$NON-NLS-1$
public final class ProviderInfo implements Serializable {

    /**
     * The parameter types that need to be supplied to the provider
     * to instantiate a new module instance.
     *
     * @return the parameter types that need to be supplied to the
     * provider to instantiate a new module instance
     *
     * @throws ClassNotFoundException if there were errors finding
     * the classes for the parameter type names.
     */
    public Class[] parameterTypes() throws ClassNotFoundException {
        if(mParameterTypes == null) {
            mParameterTypes = new Class[mParameterTypeNames.size()];
            int i = 0;
            for(String s: mParameterTypeNames) {
                mParameterTypes[i++] = Class.forName(s);
            }
        }
        return mParameterTypes;
    }

    /**
     * Returns the provider URN as string.
     *
     * @return the provider URN as string.
     */
    public ModuleURN getURN() {
        return mURN;
    }

    /**
     * Returns the type names of the parameters that need to be
     * supplied to the provider to instantiate new module instances.
     *
     * @return the type names of the parameters that need to be
     * supplied to the provider to instantiate new module
     * instances.
     */
    public List<String> getParameterTypeNames() {
        return mParameterTypeNames;
    }

    /**
     * Returns true, if this provider supports multiple instances
     * of modules.
     *
     * @return if this provider supports multiple module instances.
     */
    public boolean isMultipleInstances() {
        return mMultipleInstances;
    }

    /**
     * Returns true, if this provider supports auto-instantiable modules.
     *
     * @return if this provider supports auto-instantiable modules.
     */
    public boolean isAutoInstantiate() {
        return mAutoInstantiate;
    }

    /**
     * The description of the module in the server's locale.
     *
     * @return the description of the module in the server's locale.
     */
    public String getDescription() {
        return mDescription;
    }

    /**
     * Returns true if the factory is locked. This field is
     * exposed only for diagnostics.
     *
     * @return true if the factory is locked.
     */
    public boolean isLocked() {
        return mLocked;
    }

    /**
     * The number of threads waiting to acquire the factory lock.
     *
     * @return number of threads waiting to acquire the factory lock.
     */
    public int getLockQueueLength() {
        return mLockQueueLength;
    }

    /**
     * Creates an instance. This constructor is meant to be used by
     * JMX to reconstruct this bean. Use the other constructor to
     * create instances from code.
     *
     * @param inURN the provider URN
     * @param inDescription the user-friendly module description
     * @param inParameterTypeNames the list of type name of
     * parameters needed to create new instances of modules
     * @param inMultipleInstances if this provider supports
     * multiple instances of modules
     * @param inAutoInstantiate if this provider supports
     * auto-instantiable modules
     * @param inLocked if the factory is locked.
     * @param inLockQueueLength the number of threads waiting to acquire
     * the factory lock.
     */
    @ConstructorProperties({
            "URN",                    //$NON-NLS-1$
            "description",            //$NON-NLS-1$
            "parameterTypeNames",     //$NON-NLS-1$
            "multipleInstances",      //$NON-NLS-1$
            "autoInstantiate",        //$NON-NLS-1$
            "locked",                 //$NON-NLS-1$
            "lockQueueLength"         //$NON-NLS-1$
            })
    public ProviderInfo(ModuleURN inURN,
                        String inDescription,
                        List<String> inParameterTypeNames,
                        boolean inMultipleInstances,
                        boolean inAutoInstantiate,
                        boolean inLocked,
                        int inLockQueueLength) {
        mURN = inURN;
        mParameterTypeNames = inParameterTypeNames;
        mMultipleInstances = inMultipleInstances;
        mAutoInstantiate = inAutoInstantiate;
        mDescription = inDescription;
        mLocked = inLocked;
        mLockQueueLength = inLockQueueLength;
    }

    /**
     * Creates an instance.
     *
     * @param inURN the provider URN
     * @param inParameterTypes the list of types of parameters needed
     * to create new instances of modules.
     * @param inMultipleInstances if this provider supports multiple
     * instances of modules
     * @param inAutoInstantiate if this provider supports
     * auto-instantiable modules.
     * @param inDescription the user-friendly module description.
     * @param inLocked if the factory is locked.
     * @param inLockQueueLength the number of threads waiting to acquire
     * the factory lock.
     */
    public ProviderInfo(ModuleURN inURN,
                        Class[] inParameterTypes,
                        boolean inMultipleInstances,
                        boolean inAutoInstantiate,
                        String inDescription,
                        boolean inLocked,
                        int inLockQueueLength) {
        mURN = inURN;
        mParameterTypes = inParameterTypes;
        mMultipleInstances = inMultipleInstances;
        mAutoInstantiate = inAutoInstantiate;
        mDescription = inDescription;
        mLocked = inLocked;
        mLockQueueLength = inLockQueueLength;
        mParameterTypeNames = new ArrayList<String>(mParameterTypes.length);
        for(Class c: mParameterTypes) {
            mParameterTypeNames.add(c.getName());
        }
    }

    private transient Class[] mParameterTypes;
    private final ModuleURN mURN;
    private final List<String> mParameterTypeNames;
    private final boolean mMultipleInstances;
    private final boolean mAutoInstantiate;
    private final String mDescription;
    private final boolean mLocked;
    private final int mLockQueueLength;
    private static final long serialVersionUID = -3130986112217202526L;
}
