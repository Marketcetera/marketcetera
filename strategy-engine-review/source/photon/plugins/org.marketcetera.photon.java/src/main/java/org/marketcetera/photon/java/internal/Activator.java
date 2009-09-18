package org.marketcetera.photon.java.internal;

import org.eclipse.jdt.internal.ui.text.JavaColorManager;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.marketcetera.util.misc.ClassVersion;
import org.osgi.framework.BundleContext;

/* $License$ */

/**
 * The activator class controls the plug-in life cycle
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public class Activator extends AbstractUIPlugin {

    // The plug-in ID
    public static final String PLUGIN_ID = "org.marketcetera.photon.java"; //$NON-NLS-1$

    // The shared instance
    private static Activator plugin;

    private JavaColorManager colorManager;

    /**
     * The constructor
     */
    public Activator() {
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext
     * )
     */
    public void start(BundleContext context) throws Exception {
        super.start(context);
        plugin = this;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext
     * )
     */
    public void stop(BundleContext context) throws Exception {
        try {
            plugin = null;
            if (colorManager != null) {
                colorManager.dispose();
                colorManager = null;
            }
        } finally {
            super.stop(context);
        }
    }

    /**
     * Returns the shared instance
     * 
     * @return the shared instance
     */
    public static Activator getDefault() {
        return plugin;
    }

    /**
     * Returns the Java editor color manager.
     * 
     * @return the color manager
     */
    public synchronized JavaColorManager getColorManager() {
        if (colorManager == null) {
            colorManager = new JavaColorManager(true);
        }
        return colorManager;
    }

}
