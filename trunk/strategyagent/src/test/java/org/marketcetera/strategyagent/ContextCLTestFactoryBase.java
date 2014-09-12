package org.marketcetera.strategyagent;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.log.I18NMessage0P;
import org.marketcetera.module.*;

import java.lang.reflect.InvocationTargetException;

/* $License$ */
/**
 * Provider that will help us test the the context class loader is correctly
 * set.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.5.0
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class ContextCLTestFactoryBase extends ModuleFactory
        implements ContextCLFactoryMXBean {

    public static ClassLoader sConstructClassLoader;
    public static ClassLoader sCreateClassLoader;
    public static ClassLoader sFactoryGetAttributeLoader;
    public static ClassLoader sFactorySetAttributeLoader;
    public static ClassLoader sFactoryOperationLoader;
    @Override
    public String getAttribute() {
        sFactoryGetAttributeLoader = Thread.currentThread().getContextClassLoader();
        return null;
    }

    @Override
    public void setAttribute(String inValue) {
        sFactorySetAttributeLoader = Thread.currentThread().getContextClassLoader();
    }

    @Override
    public void operation() {
        sFactoryOperationLoader = Thread.currentThread().getContextClassLoader();
    }
    /**
     * Creates an instance.
     *
     */
    public ContextCLTestFactoryBase() {
        super(PROVIDER_URN, new I18NMessage0P(Messages.LOGGER, "provider"),
                true, true, ModuleURN.class);
        sConstructClassLoader = Thread.currentThread().getContextClassLoader();
    }

    @Override
    public Module create(Object... inParameters)
            throws ModuleCreationException {
        sCreateClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            return (Module) sCreateClassLoader.loadClass(MODULE_SUBCLASS_NAME).
                    getConstructor(ModuleURN.class).newInstance(inParameters[0]);
        } catch (Exception e) {
            throw new ModuleCreationException(e, Messages.LOG_APP_COPYRIGHT);
        }
    }

    /**
     * The Provider URN.
     */
    public static final ModuleURN PROVIDER_URN =
            new ModuleURN("metc:test:contextcl");
    public static String MODULE_SUBCLASS_NAME = ContextCLTestFactoryBase.class.
            getPackage().getName() + ".ContextCLModule"; 
}