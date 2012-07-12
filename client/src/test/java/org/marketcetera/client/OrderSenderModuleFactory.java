package org.marketcetera.client;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.module.ModuleFactory;
import org.marketcetera.module.Module;
import org.marketcetera.module.ModuleCreationException;
import org.marketcetera.module.ModuleURN;

/* $License$ */
/**
 * Creates modules, that emit orders that are supplied during
 * module creation.
 * The first parameter is the name of the module instance and the
 * second parameter is the array of orders to emit.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class OrderSenderModuleFactory extends ModuleFactory {
    /**
     * Creates an instance.
     *
     */
    public OrderSenderModuleFactory() {
        super(PROVIDER_URN, TestMessages.PROVIDER_ORDER_SENDER,
                true, false, String.class, Object[].class);
    }

    public Module create(Object[] inParameters) throws ModuleCreationException {
        return new OrderSenderModule(new ModuleURN(PROVIDER_URN,
                (String)inParameters[0]),
                (Object[]) inParameters[1]);
    }
    public static final ModuleURN PROVIDER_URN = new ModuleURN("metc:test:ordersender");
}
