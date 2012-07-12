package org.marketcetera.photon.strategy.engine.ui;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.core.databinding.observable.Observables;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.swt.widgets.Display;
import org.marketcetera.photon.commons.Validate;
import org.marketcetera.photon.commons.ui.DisplayThreadExecutor;
import org.marketcetera.photon.commons.ui.SWTUtils;
import org.marketcetera.photon.strategy.engine.IStrategyEngines;
import org.marketcetera.photon.strategy.engine.model.core.StrategyEngine;
import org.marketcetera.util.misc.ClassVersion;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceException;
import org.osgi.framework.ServiceRegistration;

/* $License$ */

/**
 * Convenience base class for bundles that provide a {@link IStrategyEngines}
 * service. The service is confined to the thread on which this object was
 * constructed. The service is registered by {@link #init(BundleContext)}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 2.0.0
 */
@ClassVersion("$Id$")
public abstract class AbstractStrategyEnginesSupport {

    private ExecutorService mGuiExecutor;

    private WritableList mEngines;

    private ServiceRegistration mRegistration;

    private final AtomicBoolean mDisposed = new AtomicBoolean();

    /**
     * Initializes the service. Must only be called once.
     * 
     * @param context
     *            the context with which to register the service
     * @throws IllegalArgumentException
     *             if context is null
     * @throws IllegalStateException
     *             if called from a non UI thread
     */
    public final void init(BundleContext context) {
        Validate.notNull(context, "context"); //$NON-NLS-1$
        SWTUtils.checkThread();
        mGuiExecutor = DisplayThreadExecutor.getInstance(Display.getCurrent());
        mEngines = WritableList.withElementType(StrategyEngine.class);
        initList(getGenericEngines());
        mRegistration = context.registerService(IStrategyEngines.class
                .getName(), new IStrategyEngines() {
            @Override
            public IObservableList getStrategyEngines() throws ServiceException {
                SWTUtils.checkThread();
                checkDisposed();
                return Observables.unmodifiableObservableList(mEngines);
            }

            @Override
            public StrategyEngine addEngine(final StrategyEngine engine) {
                Validate.notNull(engine, "engine"); //$NON-NLS-1$
                SWTUtils.checkThread();
                checkDisposed();
                return doAddEngine(getGenericEngines(), engine);
            }

            @Override
            public void removeEngine(final StrategyEngine engine) {
                Validate.notNull(engine, "engine"); //$NON-NLS-1$
                SWTUtils.checkThread();
                checkDisposed();
                doRemoveEngine(getGenericEngines(), engine);
            }
        }, null);
    }

    /**
     * Returns the list of engines.
     * 
     * @return the list of engines
     */
    @SuppressWarnings("unchecked")
    protected final List<StrategyEngine> getGenericEngines() {
        /*
         * This is for convenience and safety. WritableList is not generic, but
         * mEngines must only contain StrategyEngine objects.
         */
        return mEngines;
    }

    private void checkDisposed() throws IllegalStateException {
        if (mDisposed.get()) {
            throw new IllegalStateException(
                    Messages.ABSTRACT_STRATEGY_ENGINES_SUPPORT_SERVICE_NOT_AVAILABLE
                            .getText());
        }
    }

    /**
     * Returns the executor that can be used to safely update the model backing
     * the service.
     * 
     * @return the executor service
     */
    protected final ExecutorService getGuiExecutor() {
        return mGuiExecutor;
    }

    /**
     * Initialize the list of engines after it has been created but before the
     * service has been registered.
     * <p>
     * Must be called from the UI thread for the model
     * 
     * @param engines
     *            the list of engines
     */
    protected void initList(List<StrategyEngine> engines) {
    }

    /**
     * Adds an engine to the list of engines.
     * <p>
     * Must be called from the UI thread of the model.
     * 
     * @param engines
     *            the list of engines
     * @param engine
     *            the new engine to add
     * @return the engine that was added, not necessarily the engine passed in
     */
    protected abstract StrategyEngine doAddEngine(List<StrategyEngine> engines,
            final StrategyEngine engine);

    /**
     * Removes an engine from the list of engines.
     * <p>
     * Must be called from the UI thread of the model.
     * 
     * @param engines
     *            the list of engines
     * @param engine
     *            the new engine to add
     */
    protected abstract void doRemoveEngine(List<StrategyEngine> engines,
            final StrategyEngine engine);

    /**
     * Unregisters the service and cleans up the model, after which this object
     * should no longer be used.
     */
    public final void dispose() {
        if (mDisposed.compareAndSet(false, true)) {
            mRegistration.unregister();
            mEngines.dispose();
        }
    }

}