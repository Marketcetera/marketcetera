package org.marketcetera.module;

import org.marketcetera.util.misc.ClassVersion;

/* $License$ */
/**
 * A singleton module for testing.
 *
 * @author anshul@marketcetera.com
 */
@ClassVersion("$Id: SingletonModule.java 82330 2012-04-10 16:29:13Z colin $")
public class SingletonModule extends ModuleBase {

    public SingletonModule(ModuleURN inModuleURN) {
        super(inModuleURN);
        sInstance = this;
        sInstances++;
    }

    public static SingletonModule getInstance() {
        return sInstance;
    }

    public static int getInstances() {
        return sInstances;
    }
    public static void clearInstances() {
        sInstance = null;
        sInstances = 0;
    }

    protected static int sInstances = 0;
    protected static SingletonModule sInstance;
}
