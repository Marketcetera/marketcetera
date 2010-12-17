package org.marketcetera.photon.java.internal;

import org.eclipse.jdt.internal.ui.text.JavaColorManager;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.marketcetera.util.misc.ClassVersion;
import org.osgi.framework.BundleContext;

/* $License$ */

/**
 * Manages the lifecycle for this plugin.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 2.0.0
 */
@ClassVersion("$Id$")
public class Activator extends AbstractUIPlugin {

    private static volatile Activator sCurrent;

    private JavaColorManager mColorManager;

    @Override
    public void start(BundleContext context) throws Exception {
        synchronized (Activator.class) {
            super.start(context);
            sCurrent = this;
        }
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        synchronized (Activator.class) {
            try {
                sCurrent = null;
                if (mColorManager != null) {
                    mColorManager.dispose();
                    mColorManager = null;
                }
            } finally {
                super.stop(context);
            }
        }
    }

    /**
     * Returns the current instance
     * 
     * @return the current instance
     */
    static Activator getCurrent() {
        return sCurrent;
    }

    /**
     * Returns the Java editor color manager.
     * 
     * @return the color manager
     */
    public JavaColorManager getColorManager() {
        synchronized (Activator.class) {
            if (sCurrent == null) {
                /*
                 * Bundle has been stopped.
                 */
                return null;
            }
            if (mColorManager == null) {
                mColorManager = new JavaColorManager(true);
            }
            return mColorManager;
        }
    }

}
