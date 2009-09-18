package org.marketcetera.photon.strategy.engine.strategyagent.ui;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.marketcetera.photon.commons.Validate;
import org.marketcetera.photon.commons.emf.IEMFPersistence;
import org.marketcetera.photon.core.ICredentialsService;
import org.marketcetera.photon.core.ILogoutService;
import org.marketcetera.photon.strategy.engine.IStrategyEngines;
import org.marketcetera.photon.strategy.engine.model.core.StrategyEngine;
import org.marketcetera.photon.strategy.engine.model.strategyagent.StrategyAgentEngine;
import org.marketcetera.photon.strategy.engine.strategyagent.StrategyAgentEngines;
import org.marketcetera.photon.strategy.engine.ui.AbstractStrategyEnginesSupport;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.misc.ClassVersion;
import org.osgi.framework.BundleContext;

/* $License$ */

/**
 * Helper for bundles that provide an {@link IStrategyEngines} service to a list
 * of {@link StrategyAgentEngine} engines.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public class StrategyAgentEnginesSupport extends AbstractStrategyEnginesSupport {

    private final ICredentialsService mCredentialsService;

    private final ILogoutService mLogoutService;

    private final PersistenceHelper mPersistenceHelper;

    /**
     * Constructor.
     * 
     * @param context
     *            the context with which to obtain and register services
     * @param persistenceService
     *            service to use to save and restore model state, if null then
     *            no persistence will be provided
     * @throws IllegalArgumentException
     *             if context is null
     * @throws IllegalStateException
     *             if called from a non UI thread
     */
    public StrategyAgentEnginesSupport(BundleContext context,
            IEMFPersistence persistenceService) {
        Validate.notNull(context, "context"); //$NON-NLS-1$
        mPersistenceHelper = new PersistenceHelper(persistenceService);
        mCredentialsService = (ICredentialsService) context.getService(context
                .getServiceReference(ICredentialsService.class.getName()));
        mLogoutService = (ILogoutService) context.getService(context
                .getServiceReference(ILogoutService.class.getName()));
        init(context);
    }

    @Override
    protected void initList(List<StrategyEngine> engines) {
        mPersistenceHelper.restore();
    }

    @Override
    protected StrategyEngine doAddEngine(List<StrategyEngine> engines,
            final StrategyEngine engine) {
        StrategyAgentEngine newEngine = internalAdd(engines, engine);
        mPersistenceHelper.save();
        return newEngine;
    }

    private StrategyAgentEngine internalAdd(List<StrategyEngine> engines,
            final StrategyEngine engine) {
        StrategyAgentEngine newEngine = StrategyAgentEngines
                .createStrategyAgentEngine((StrategyAgentEngine) engine,
                        getGuiExecutor(), mCredentialsService, mLogoutService);
        newEngine.eAdapters().add(mPersistenceHelper);
        engines.add(newEngine);
        return newEngine;
    }

    @Override
    protected void doRemoveEngine(List<StrategyEngine> engines,
            final StrategyEngine engine) {
        engine.eAdapters().remove(mPersistenceHelper);
        engines.remove(engine);
        mPersistenceHelper.save();
    }

    /**
     * Helper to save strategy agent engine configurations.
     */
    @ClassVersion("$Id$")
    private class PersistenceHelper extends AdapterImpl {

        private final IEMFPersistence mPersistenceService;

        /**
         * Constructor.
         * 
         * @param persistenceService
         *            service to use to save and restore model state, if null
         *            then no persistence will be provided
         */
        public PersistenceHelper(IEMFPersistence persistenceService) {
            mPersistenceService = persistenceService;
        }

        @Override
        public void notifyChanged(Notification msg) {
            if (!msg.isTouch() && msg.getFeature() instanceof EAttribute) {
                save();
            }
        }

        /**
         * Restores the state of the model.
         */
        public void restore() {
            if (mPersistenceService != null) {
                try {
                    List<? extends EObject> restoredEngines = mPersistenceService
                            .restore();
                    for (EObject object : restoredEngines) {
                        if (object instanceof StrategyAgentEngine) {
                            internalAdd(getGenericEngines(),
                                    (StrategyEngine) object);
                        } else {
                            Messages.STRATEGY_AGENT_ENGINES_SUPPORT_UNEXPECTED_OBJECT
                                    .warn(StrategyAgentEnginesSupport.this,
                                            object);
                        }
                    }
                } catch (FileNotFoundException e) {
                    SLF4JLoggerProxy
                            .debug(StrategyAgentEnginesSupport.this,
                                    "No persisted engine restored because the file does not exist"); //$NON-NLS-1$
                } catch (IOException e) {
                    Messages.STRATEGY_AGENT_ENGINES_SUPPORT_RESTORE_FAILED
                            .error(StrategyAgentEnginesSupport.this, e);
                }
            }
        }

        /**
         * Saves the state of the model.
         */
        public void save() {
            if (mPersistenceService != null) {
                try {
                    mPersistenceService.save(getGenericEngines());
                } catch (IOException e) {
                    Messages.STRATEGY_AGENT_ENGINES_SUPPORT_SAVE_FAILED.error(
                            StrategyAgentEnginesSupport.this, e);
                }
            }
        };
    }
}