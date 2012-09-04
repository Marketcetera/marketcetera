package org.marketcetera.core.module;

import java.util.concurrent.locks.Lock;

import org.marketcetera.core.util.log.I18NMessage0P;

/* $License$ */
/**
 * Factory for {@link ConcurrentTestModule}.
 *
 * @version $Id: ConcurrentTestFactory.java 82330 2012-04-10 16:29:13Z colin $
 * @since 1.1.0
 */
public class ConcurrentTestFactory extends ModuleFactory {
    public ConcurrentTestFactory() {
        super(PROVIDER_URN, new I18NMessage0P(Messages.LOGGER, "provider"),
                !sSingleton, sAutoCreate, ModuleURN.class);
    }

    public Module create(Object... inParameters) throws ModuleCreationException {
        if(sNextCreateLock != null) {
            sNextCreateLock.lock();
        }
        return new ConcurrentTestModule((ModuleURN)inParameters[0], sAutoCreate);
    }

    /**
     * Configures the factory to be a singleton.
     *
     * @param inSingleton if the factory should be a singleton.
     */
    public static void setSingleton(boolean inSingleton) {
        sSingleton = inSingleton;
    }

    /**
     * Configures the factory to auto-create module instances.
     *
     * @param inAutoCreate if the modules should be auto created.
     */
    public static void setAutoCreate(boolean inAutoCreate) {
        sAutoCreate = inAutoCreate;
    }

    /**
     * Sets the lock that should be acquired before creating the next module
     * instance.
     *
     * @param inNextCreateLock the lock instance to use. Can be null, if
     * no lock needs to be acquired.
     */
    public static void setNextCreateLock(Lock inNextCreateLock) {
        sNextCreateLock = inNextCreateLock;
    }

    /**
     * Clears up all the state for the factory.
     */
    public static void clear() {
        setSingleton(false);
        setAutoCreate(false);
        setNextCreateLock(null);
    }

    static final ModuleURN PROVIDER_URN = new ModuleURN("metc:test:concurrent");
    public static final ModuleURN INSTANCE_URN = new ModuleURN(PROVIDER_URN, "default");
    private static boolean sSingleton = false;
    private static boolean sAutoCreate = false;
    private static Lock sNextCreateLock = null;
}