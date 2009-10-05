package org.marketcetera.photon.commons.ui.workbench;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.IExecutableExtensionFactory;
import org.marketcetera.util.misc.ClassVersion;
import org.ops4j.peaberry.eclipse.GuiceExtensionFactory;

/* $License$ */

/**
 * Works around a bug in GuiceExtensionFactory. It caches the configuration
 * element to pass to the factory-instantiated object as required by the
 * {@link IExecutableExtensionFactory} contract.
 * <p>
 * See <a
 * href="http://code.google.com/p/peaberry/issues/detail?id=37">http://code
 * .google.com/p/peaberry/issues/detail?id=37</a>
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public final class WorkaroundGuiceExtensionFactory implements IExecutableExtension,
        IExecutableExtensionFactory {

    private final GuiceExtensionFactory mGuiceFactory;
    private IConfigurationElement mConfigElement;

    /**
     * Constructor.
     */
    public WorkaroundGuiceExtensionFactory() {
        mGuiceFactory = new GuiceExtensionFactory();
    }

    @Override
    public void setInitializationData(IConfigurationElement config,
            String propertyName, Object data) throws CoreException {
        mConfigElement = config;
        mGuiceFactory.setInitializationData(config, propertyName, data);
    }

    @Override
    public Object create() throws CoreException {
        Object object = mGuiceFactory.create();
        if (object instanceof IExecutableExtension) {
            ((IExecutableExtension) object).setInitializationData(
                    mConfigElement, null, null);
        }
        return object;
    }
}
