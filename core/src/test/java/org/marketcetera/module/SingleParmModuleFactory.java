package org.marketcetera.module;

import org.marketcetera.util.misc.ClassVersion;

import java.net.URI;

/* $License$ */
/**
 * A module factory that supports single instance only and does require
 * parameters to instantiate the instance.
 *
 * @author anshul@marketcetera.com
 */
@ClassVersion("$Id: SingleParmModuleFactory.java 82384 2012-07-20 19:09:59Z colin $")
public class SingleParmModuleFactory extends ModuleFactory {
    @Override
    public SingletonModule create(Object... parameters)
            throws ModuleCreationException {
        return new SingletonModule(INSTANCE_URN);
    }
    public SingleParmModuleFactory() {
        super(PROVIDER_URN, TestMessages.SINGLE_1_PROVIDER, false, false, URI.class);
    }

    static final ModuleURN PROVIDER_URN = new ModuleURN("metc:test:singleparm");
    static final ModuleURN INSTANCE_URN = new ModuleURN(PROVIDER_URN,"single");
}