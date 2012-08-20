package org.marketcetera.core.module;

import org.marketcetera.api.attributes.ClassVersion;


/* $License$ */
/**
 * A singleton module factory for {@link EmitterModule}
 *
 * @author anshul@marketcetera.com
 */
@ClassVersion("$Id: EmitterModuleFactory.java 82330 2012-04-10 16:29:13Z colin $")
public class EmitterModuleFactory extends ModuleFactory {
    public EmitterModuleFactory() {
        super(PROVIDER_URN, TestMessages.EMIT_PROVIDER, false, false);
    }
    public EmitterModule create(Object... parameters)
            throws ModuleCreationException {
        return new EmitterModule();
    }
    static final ModuleURN PROVIDER_URN = new ModuleURN("metc:emit:single");
    public static final ModuleURN INSTANCE_URN = new ModuleURN(PROVIDER_URN, "default");
}
