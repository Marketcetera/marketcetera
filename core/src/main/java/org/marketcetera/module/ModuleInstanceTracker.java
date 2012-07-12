package org.marketcetera.module;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.log.I18NBoundMessage3P;

import java.util.HashMap;
import java.util.Map;

/* $License$ */
/**
 * Tracks module instances and searches through them.
 *
 * <p>
 * This class is thread-safe. All of its methods employ locking
 * to ensure that the concurrent modifications to the instance's state
 * do not corrupt it.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$")  //$NON-NLS-1$
class ModuleInstanceTracker {

    @Override
    public synchronized String toString() {
        return mModules.toString();
    }
    /**
     * Adds a new module instance to be tracked.
     *
     * @param inModule the module instance to track.
     */
    synchronized void add(Module inModule) {
        mModules.put(inModule.getURN(), inModule);
    }

    /**
     * All the module URNs tracked by this class.
     *
     * @return the module URNs tracked by this class.
     */
    synchronized ModuleURN[] getAllURNs() {
        return mModules.keySet().toArray(
                new ModuleURN[mModules.size()]);
    }

    /**
     * The module instance corresponding to the supplied URN.
     *
     * @param inURN the module URN
     *
     * @return the module instance corresponding to the supplied URN, null
     * if no module with the supplied URN was found.
     */
    synchronized Module get(ModuleURN inURN) {
        return mModules.get(inURN);
    }

    /**
     * if a module with the specified URN exists.
     *
     * @param inURN the module URN to test.
     *
     * @return if a module with the specified URN exists.
     */
    synchronized boolean has(ModuleURN inURN) {
        return mModules.containsKey(inURN);
    }

    /**
     * Removes the module with the specified URN and returns it.
     *
     * @param inURN the module URN
     *
     * @return the removed module instance. null, if no module instance
     * corresponding to the supplied module URN was found.
     */
    synchronized Module remove(ModuleURN inURN) {
        return mModules.remove(inURN);
    }

    /**
     * Searches for a module matching the supplied URN.
     * if a module instance that matches the supplied URN exactly is found
     * it is returned.
     *
     * Otherwise, an attempt is made to find the first module that has
     * the same URN elements as the ones that are specified in the supplied
     * URN and return it.
     *
     * For example, if the supplied URN is <code>metc:mytype</code>,
     * this method will return the first module that is found to have a
     * URN with the providerType 'mytype'.
     *
     * Do note that this method doesn't do implement an efficient search
     * as it iterates through the URNs of all the modules. This may not
     * be a huge issue, if the number of modules is small. However, if the
     * number of modules grows, we might want to use different data
     * structures to make the search more efficient.
     *
     * @param inURN the module URN
     *
     * @return a module matching the supplied URN, null if no matching module
     * could be found
     *
     * @throws ModuleNotFoundException if multiple modules matching
     * the supplied URN were found.
     */
    synchronized Module search(ModuleURN inURN) throws ModuleNotFoundException {
        Module m;
        //Look for an exact match
        m = get(inURN);
        if(m != null) {
            return m;
        }
        //Figure out which elements of the URN are specified.
        boolean noProviderType = inURN.providerType() == null ||
                inURN.providerType().isEmpty();
        boolean noProviderName = inURN.providerName() == null ||
                inURN.providerName().isEmpty();
        boolean noInstanceName = inURN.instanceName() == null ||
                inURN.instanceName().isEmpty();
        //Match the first instance that matches all the specified fields
        //of the URN
        Module returnValue = null;
        for(ModuleURN i: mModules.keySet()) {
            if((noProviderType ||
                    inURN.providerType().equals(i.providerType())) &&
                    (noProviderName ||
                            inURN.providerName().equals(i.providerName())) &&
                    (noInstanceName
                            || inURN.instanceName().equals(i.instanceName()))) {
                if (returnValue == null) {
                    returnValue = get(i);
                } else {
                    throw new ModuleNotFoundException(new I18NBoundMessage3P(
                            Messages.MULTIPLE_MODULES_MATCH_URN,
                            inURN.getValue(),
                            returnValue.getURN().getValue(),
                            get(i).getURN().getValue()));
                }
            }
        }
        return returnValue;
    }

    private final Map<ModuleURN,Module> mModules =
            new HashMap<ModuleURN, Module>();
}
