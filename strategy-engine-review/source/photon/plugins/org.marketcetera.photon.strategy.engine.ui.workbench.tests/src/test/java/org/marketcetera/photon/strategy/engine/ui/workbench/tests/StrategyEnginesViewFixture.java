package org.marketcetera.photon.strategy.engine.ui.workbench.tests;

import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.ui.PlatformUI;
import org.marketcetera.photon.internal.strategy.engine.ui.workbench.StrategyEnginesView;
import org.marketcetera.photon.strategy.engine.IStrategyEngines;
import org.marketcetera.photon.strategy.engine.model.core.StrategyEngine;
import org.marketcetera.photon.strategy.engine.ui.StrategyEngineColors;
import org.marketcetera.photon.strategy.engine.ui.workbench.StrategyEngineWorkbenchUI;
import org.marketcetera.photon.test.AbstractUIRunner;
import org.marketcetera.photon.test.OSGITestUtil;
import org.marketcetera.photon.test.AbstractUIRunner.ThrowableRunnable;
import org.osgi.framework.ServiceRegistration;

/* $License$ */

/**
 * Helper for testing the Strategy Engines view.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
public class StrategyEnginesViewFixture {

    public static StrategyEnginesViewFixture openView() throws Exception {
        return new StrategyEnginesViewFixture();
    }

    private final SWTWorkbenchBot mBot = new SWTWorkbenchBot();
    private final SWTBotView mView;
    private volatile StrategyEnginesView mRealView;
    private volatile WritableList mEngines;
    private volatile ServiceRegistration mMockService;

    public StrategyEnginesViewFixture() throws Exception {
        AbstractUIRunner.syncRun(new ThrowableRunnable() {
            @Override
            public void run() throws Exception {
                StrategyEngineColors.init();
                mRealView = (StrategyEnginesView) PlatformUI
                        .getWorkbench()
                        .getActiveWorkbenchWindow()
                        .getActivePage()
                        .showView(
                                StrategyEngineWorkbenchUI.STRATEGY_ENGINES_VIEW_ID);
            }
        });
        mView = mBot
                .viewById(StrategyEngineWorkbenchUI.STRATEGY_ENGINES_VIEW_ID);
    }

    public SWTBotView getView() {
        return mView;
    }

    /*
     * Warning: returned reference needs to be used on UI thread.
     */
    public StrategyEnginesView getRealView() {
        return mRealView;
    }

    public void setModel(WritableList engines) {
        unregister();
        mEngines = engines;
        if (engines != null) {
            mMockService = OSGITestUtil.registerMockService(
                    IStrategyEngines.class, new IStrategyEngines() {
                        @Override
                        public IObservableList getStrategyEngines() {
                            return mEngines;
                        }

                        @Override
                        public StrategyEngine addEngine(StrategyEngine engine) {
                            mEngines.add(engine);
                            return engine;
                        }

                        @Override
                        public void removeEngine(StrategyEngine engine) {
                            mEngines.remove(engine);
                        }
                    });
        }
    }

    public void close() {
        unregister();
        mView.close();
    }

    private void unregister() {
        if (mMockService != null) {
            mMockService.unregister();
            mMockService = null;
        }
    }
}
