package org.marketcetera.photon.internal.strategy;

import javax.annotation.concurrent.GuardedBy;

import org.eclipse.swt.widgets.Display;
import org.marketcetera.photon.commons.ui.SWTUtils;
import org.marketcetera.photon.strategy.engine.IStrategyEngines;
import org.marketcetera.photon.strategy.engine.ui.AbstractStrategyEnginesSupport;
import org.marketcetera.util.misc.ClassVersion;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/* $License$ */

/**
 * The activator class controls the plug-in life cycle and manages services that
 * are tied to that life cycle.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$")
public final class Activator implements BundleActivator {

    /**
     * The current singleton instance.
     */
    private static volatile Activator sInstance;

    /**
     * The {@link TradeSuggestionManager}, confined to the UI thread.
     */
    private TradeSuggestionManager mTradeSuggestionManager;

    /**
     * The bundle's context, used for service registration.
     */
    @GuardedBy("Activator.class")
    private BundleContext mBundleContext;

    /**
     * Manages the {@link IStrategyEngines} service provided by this bundle.
     */
    @GuardedBy("Activator.class")
    private AbstractStrategyEnginesSupport mSupport;

    @Override
    public void start(BundleContext context) throws Exception {
        synchronized (Activator.class) {
            mBundleContext = context;
            sInstance = this;
        }
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        synchronized (Activator.class) {
            sInstance = null;
            mBundleContext = null;
            if (mSupport != null) {
                mSupport.dispose();
                mSupport = null;
            }
        }
    }

    /**
     * Returns the current instance
     * 
     * @return the current instance, or null if the bundle is not active
     */
    static Activator getCurrent() {
        return sInstance;
    }

    /**
     * Returns the {@link TradeSuggestionManager} singleton for this plug-in.
     * Typically, this should be accessed through
     * {@link TradeSuggestionManager#getCurrent()}.
     * <p>
     * This must be called from the UI thread and the returned object is
     * confined to the UI thread.
     * 
     * @return the current TradeSuggestionManager singleton
     * @throws IllegalStateException
     *             if called from a non UI thread
     */
    TradeSuggestionManager getTradeSuggestionManager() {
        SWTUtils.checkThread();
        synchronized (Activator.class) {
            if (mTradeSuggestionManager == null) {
                mTradeSuggestionManager = new TradeSuggestionManager();
            }
            return mTradeSuggestionManager;
        }
    }

    private void internalInitEngines() {
        synchronized (Activator.class) {
            mSupport = new StrategyEnginesSupport(mBundleContext);
        }
    }

    /**
     * Initializes the {@link IStrategyEngines} service on the current display.
     * 
     * @throws IllegalStateException
     *             if called from a non UI thread
     */
    public static void initEngines() {
        SWTUtils.checkThread();
        final Display display = Display.getCurrent();
        display.asyncExec(new Runnable() {
            public void run() {
                if (display.isDisposed()) {
                    return;
                }
                synchronized (Activator.class) {
                    if (sInstance == null) {
                        return;
                    }
                    sInstance.internalInitEngines();
                }
            }
        });
    }
}
