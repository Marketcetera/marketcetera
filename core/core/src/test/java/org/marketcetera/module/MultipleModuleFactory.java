package org.marketcetera.module;

import org.marketcetera.core.attributes.ClassVersion;

/* $License$ */
/**
 * This provider supports multiple module instances that
 * can be auto-instantiated and auto-started
 *
 * @author anshul@marketcetera.com
 */
@ClassVersion("$Id: MultipleModuleFactory.java 82330 2012-04-10 16:29:13Z colin $")
public class MultipleModuleFactory extends ModuleFactory {
    @Override
    public MultipleModule create(Object... parameters)
            throws ModuleCreationException {
        return new MultipleModule((ModuleURN) parameters[0], true);
    }
    public MultipleModuleFactory() {
        super(PROVIDER_URN, TestMessages.MULTIPLE_1_PROVIDER,
                true, true, ModuleURN.class);
    }

    static final ModuleURN PROVIDER_URN = new ModuleURN("metc:test:multiple1");
}
