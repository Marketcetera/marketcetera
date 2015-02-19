package org.marketcetera.module;

import org.marketcetera.util.misc.ClassVersion;

import java.util.HashMap;

/* $License$ */
/**
 * Base module class for testing.
 *
 * @author anshul@marketcetera.com
 */
@ClassVersion("$Id$")  //$NON-NLS-1$
public abstract class ModuleBase extends Module {

    @Override
    protected void preStart() throws ModuleException {
        mStartInvoked = true;
    }

    @Override
    protected void preStop() throws ModuleException {
        mStopInvoked = true;
    }

    /**
     * If the {@link #preStart()} method has been invoked.
     *
     * @return if the preStart method has been invoked.
     */
    public boolean isStartInvoked() {
        return mStartInvoked;
    }

    /**
     * If the {@link #preStop()} method has been invoked.
     *
     * @return if the preStop method has been invoked.
     */
    public boolean isStopInvoked() {
        return mStopInvoked;
    }

    /**
     * Fetches the instance of this module, given the instance URN.
     *
     * @param inURN the module's instance URN.
     *
     * @return the module instance.
     */
    public static ModuleBase getInstance(ModuleURN inURN) {
        return sModules.get(inURN);
    }

    /**
     * Number of instances of this classes that have been created so far.
     *
     * @return number of instances of this class.
     */
    public static int getNumInstances() {
        return sModules.size();
    }

    /**
     * Clears the table that tracks all the instances of this class.
     */
    public static void clearInstances() {
        sModules.clear();
    }

    protected ModuleBase(ModuleURN inURN) {
        this(inURN, false);
    }
    protected ModuleBase(ModuleURN inURN, boolean inAutoStart) {
        super(inURN, inAutoStart);
        sModules.put(inURN, this);
    }

    private boolean mStartInvoked = false;
    private boolean mStopInvoked = false;
    protected static final HashMap<ModuleURN,ModuleBase> sModules =
            new HashMap<ModuleURN, ModuleBase>();
}
