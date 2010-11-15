package org.marketcetera.photon.internal.module;

import static org.ops4j.peaberry.Peaberry.osgiModule;
import static org.ops4j.peaberry.Peaberry.service;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.marketcetera.core.notifications.INotification;
import org.marketcetera.core.notifications.INotificationManager;
import org.marketcetera.core.notifications.NotificationManager;
import org.marketcetera.module.ModuleException;
import org.marketcetera.module.ModuleManager;
import org.marketcetera.photon.module.IDataFlowLabelProvider;
import org.marketcetera.photon.module.IModuleAttributeSupport;
import org.marketcetera.photon.module.ISinkDataManager;
import org.marketcetera.util.misc.ClassVersion;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.service.prefs.BackingStoreException;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Provider;

/* $License$ */

/**
 * Controls the plug-in life cycle.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.1.0
 */
@ClassVersion("$Id$")
public final class Activator implements BundleActivator {

    /**
     * The plug-in ID
     */
    public static final String PLUGIN_ID = "org.marketcetera.photon.module"; //$NON-NLS-1$

    /**
     * The shared instance
     */
    private static Activator sInstance;

    /**
     * ModuleManager singleton
     */
    private ModuleManager mModuleManager;

    /**
     * Configures module creation attribute defaults
     */
    private IModuleAttributeSupport mModuleAttributeSupport;

    /**
     * Multiplexes sink data
     */
    private SinkDataManager mSinkDataManager;

    /**
     * Labels source of data.
     */
    private Provider<IDataFlowLabelProvider> mDataFlowLabelProvider;

    @Override
    public void start(BundleContext context) throws Exception {
        sInstance = this;
        mModuleManager = new ModuleManager();
        mModuleAttributeSupport = new ModuleAttributeSupport(
                new PreferenceAttributeDefaults());
        mModuleManager
                .setConfigurationProvider(new EclipseModuleConfigurationProvider(
                        mModuleAttributeSupport));
        mSinkDataManager = new SinkDataManager();
        Module module = new AbstractModule() {
            @Override
            protected void configure() {
                bind(IDataFlowLabelProvider.class)
                        .toProvider(
                                service(IDataFlowLabelProvider.class).single()
                                        .direct());
                bind(INotificationManager.class).toInstance(
                        NotificationManager.getNotificationManager());
            }
        };
        Injector injector = Guice.createInjector(osgiModule(context), module);
        mDataFlowLabelProvider = injector
                .getProvider(IDataFlowLabelProvider.class);
        NotificationHandler handler = injector
                .getInstance(NotificationHandler.class);
        mSinkDataManager.register(handler, INotification.class);
        mModuleManager.addSinkListener(mSinkDataManager);
        mModuleManager.init();
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        sInstance = null;
        mModuleAttributeSupport = null;
        mSinkDataManager = null;
        try {
            mModuleManager.stop();
        } catch (ModuleException e) {
            Messages.ACTIVATOR_FAILED_TO_STOP_MODULE_MANAGER.error(this, e);
        }
        mModuleManager = null;
        try {
            new InstanceScope().getNode(Activator.PLUGIN_ID).flush();
        } catch (BackingStoreException e) {
            Messages.ACTIVATOR_FAILED_TO_SAVE_PREFERENCES.error(this, e);
        }
        mDataFlowLabelProvider = null;
    }

    /**
     * Returns the shared instance
     * 
     * @return the shared instance
     */
    public static Activator getDefault() {
        return sInstance;
    }

    /**
     * Returns the core {@link ModuleManager} instance.
     * 
     * @return the core module manager
     */
    public ModuleManager getModuleManager() {
        return mModuleManager;
    }

    /**
     * Returns the {@link IModuleAttributeSupport} instance.
     * 
     * @return the module attribute support
     */
    public IModuleAttributeSupport getModuleAttributeSupport() {
        return mModuleAttributeSupport;
    }

    /**
     * Returns the {@link ISinkDataManager} instance.
     * 
     * @return the sink data manager
     */
    public ISinkDataManager getSinkDataManager() {
        return mSinkDataManager;
    }

    /**
     * Returns the {@link IDataFlowLabelProvider} instance.
     * 
     * @return the data flow label provider
     */
    public Provider<IDataFlowLabelProvider> getDataFlowLabelProvider() {
        return mDataFlowLabelProvider;
    }
}
