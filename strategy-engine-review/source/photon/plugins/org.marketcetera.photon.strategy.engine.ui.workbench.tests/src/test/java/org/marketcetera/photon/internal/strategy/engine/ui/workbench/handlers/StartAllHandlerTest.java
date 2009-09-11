package org.marketcetera.photon.internal.strategy.engine.ui.workbench.handlers;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.marketcetera.photon.strategy.engine.model.core.test.StrategyEngineCoreTestUtil.buildEngines;
import static org.marketcetera.photon.strategy.engine.model.core.test.StrategyEngineCoreTestUtil.createConnectedEngine;
import static org.marketcetera.photon.strategy.engine.model.core.test.StrategyEngineCoreTestUtil.createDeployedStrategy;

import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.marketcetera.photon.strategy.engine.model.core.DeployedStrategy;
import org.marketcetera.photon.strategy.engine.model.core.StrategyEngine;
import org.marketcetera.photon.strategy.engine.model.core.StrategyState;
import org.marketcetera.photon.strategy.engine.ui.tests.MockUIConnection;
import org.marketcetera.photon.strategy.engine.ui.workbench.tests.StrategyEnginesViewFixture;
import org.marketcetera.photon.test.AbstractUIRunner;
import org.marketcetera.photon.test.ContextMenuHelper;
import org.marketcetera.photon.test.PhotonTestBase;
import org.marketcetera.photon.test.WorkbenchRunner;
import org.marketcetera.photon.test.AbstractUIRunner.ThrowableRunnable;
import org.marketcetera.photon.test.AbstractUIRunner.UI;

/* $License$ */

/**
 * Tests {@link StartAllHandler}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@RunWith(WorkbenchRunner.class)
public class StartAllHandlerTest extends PhotonTestBase {

    private volatile WritableList mEngines;
    private volatile DeployedStrategy mStrategy1;
    private volatile DeployedStrategy mStrategy2;
    private volatile DeployedStrategy mStrategy3;
    private volatile StrategyEnginesViewFixture mView;

    @Before
    @UI
    public void before() throws Exception {
        mStrategy1 = createDeployedStrategy("deployed");
        mStrategy2 = createDeployedStrategy("deployed2");
        mStrategy3 = createDeployedStrategy("deployed3");
        mStrategy3.setState(StrategyState.RUNNING);
        StrategyEngine engine = createConnectedEngine("My Engine");
        engine.setConnection(new MockUIConnection());
        mEngines = new WritableList(buildEngines(engine, mStrategy1,
                mStrategy2, mStrategy3), StrategyEngine.class);
        mView = StrategyEnginesViewFixture.openView();
        mView.setModel(mEngines);
    }

    @After
    public void after() throws Exception {
        mView.close();
    }

    @Test
    public void test() throws Exception {
        SWTBotTree tree = mView.getView().bot().tree();
        tree.select("My Engine");
        ContextMenuHelper.clickContextMenu(tree, "Start All");
        Thread.sleep(500);
        AbstractUIRunner.syncRun(new ThrowableRunnable() {
            @Override
            public void run() throws Throwable {
                assertThat(mStrategy1.getState(), is(StrategyState.RUNNING));
                assertThat(mStrategy2.getState(), is(StrategyState.RUNNING));
                assertThat(mStrategy3.getState(), is(StrategyState.RUNNING));
            }
        });
    }
}
