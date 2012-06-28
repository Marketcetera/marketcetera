package org.marketcetera.module;

import org.marketcetera.core.attributes.ClassVersion;

/* $License$ */
/**
 * A Factory that creates modules that are capable of
 * requesting data flows.
 *
 * @author anshul@marketcetera.com
 */
@ClassVersion("$Id: FlowRequesterModuleFactory.java 82330 2012-04-10 16:29:13Z colin $")
public class FlowRequesterModuleFactory
        extends ModuleFactory {
    public FlowRequesterModule create(Object... parameters)
            throws ModuleCreationException {
        return new FlowRequesterModule((ModuleURN)parameters[0]);
    }
    public FlowRequesterModuleFactory() {
        super(PROVIDER_URN, TestMessages.FLOW_REQUESTER_PROVIDER,
                true, false, ModuleURN.class);
    }

    static final ModuleURN PROVIDER_URN = new ModuleURN("metc:flow:single");
}