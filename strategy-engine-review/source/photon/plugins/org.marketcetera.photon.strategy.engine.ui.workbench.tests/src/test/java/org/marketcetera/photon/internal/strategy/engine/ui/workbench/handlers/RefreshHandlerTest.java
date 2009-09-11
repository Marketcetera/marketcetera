package org.marketcetera.photon.internal.strategy.engine.ui.workbench.handlers;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.marketcetera.photon.strategy.engine.model.core.test.StrategyEngineCoreTestUtil.buildEngines;
import static org.marketcetera.photon.strategy.engine.model.core.test.StrategyEngineCoreTestUtil.createConnectedEngine;
import static org.marketcetera.photon.strategy.engine.model.core.test.StrategyEngineCoreTestUtil.createDeployedStrategy;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.marketcetera.photon.strategy.engine.model.core.DeployedStrategy;
import org.marketcetera.photon.strategy.engine.model.core.StrategyEngine;
import org.marketcetera.photon.strategy.engine.model.core.test.StrategyEngineCoreTestUtil.MockConnection;
import org.marketcetera.photon.strategy.engine.ui.workbench.tests.StrategyEnginesViewFixture;
import org.marketcetera.photon.test.ContextMenuHelper;
import org.marketcetera.photon.test.PhotonTestBase;
import org.marketcetera.photon.test.WorkbenchRunner;
import org.marketcetera.photon.test.AbstractUIRunner.UI;

import com.google.common.collect.Lists;

/* $License$ */

/**
 * Tests {@link RefreshHandler}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@RunWith(WorkbenchRunner.class)
public class RefreshHandlerTest extends PhotonTestBase {

    private volatile DeployedStrategy mStrategy1;
    private volatile DeployedStrategy mStrategy2;
    private volatile StrategyEnginesViewFixture mView;
    private volatile StrategyEngine mEngine1;
    private volatile StrategyEngine mEngine2;
    private volatile Connection mConnection1;
    private volatile Connection mConnection2;

    @Before
    @UI
    public void before() throws Exception {
        mStrategy1 = createDeployedStrategy("deployed");
        mStrategy2 = createDeployedStrategy("deployed 2");
        mEngine1 = createConnectedEngine("My Engine");
        mConnection1 = new Connection(mEngine1);
        mEngine2 = createConnectedEngine("My Engine 2");
        mConnection2 = new Connection(mEngine2);
        mView = StrategyEnginesViewFixture.openView();
        mView.setModel(new WritableList(buildEngines(mEngine1, mStrategy1,
                mStrategy2, mEngine2), StrategyEngine.class));
    }

    @After
    public void after() throws Exception {
        mView.close();
    }

    @Test
    public void testRefreshEngines() throws Exception {
        SWTBotTree tree = mView.getView().bot().tree();
        tree.select(0, 1);
        ContextMenuHelper.clickContextMenu(tree, "Refresh");
        Thread.sleep(500);
        assertThat(mConnection1.getRefreshCount(), is(1));
        assertThat(mConnection1.getRefreshed().size(), is(0));
        assertThat(mConnection2.getRefreshCount(), is(1));
        assertThat(mConnection2.getRefreshed().size(), is(0));
    }

    @Test
    public void testRefreshStrategies() throws Exception {
        SWTBotTree tree = mView.getView().bot().tree();
        SWTBotTreeItem engine = tree.getTreeItem("My Engine");
        engine.expand();
        engine.select("deployed", "deployed 2");
        ContextMenuHelper.clickContextMenu(tree, "Refresh");
        Thread.sleep(500);
        assertThat(mConnection1.getRefreshCount(), is(0));
        assertThat(mConnection1.getRefreshed().size(), is(2));
        assertThat(mConnection1.getRefreshed(), is(Arrays.asList(mStrategy1,
                mStrategy2)));
    }

    private static class Connection extends MockConnection {

        private volatile int mRefreshCount;
        private final List<DeployedStrategy> mRefreshed = Collections
                .synchronizedList(Lists.<DeployedStrategy> newLinkedList());

        public Connection(StrategyEngine engine) {
            setEngine(engine);
        }

        @Override
        public void refresh() throws Exception {
            mRefreshCount++;
        }

        @Override
        public void refresh(DeployedStrategy strategy) throws Exception {
            mRefreshed.add(strategy);
        }

        public int getRefreshCount() {
            return mRefreshCount;
        }

        public List<DeployedStrategy> getRefreshed() {
            return mRefreshed;
        }
    }
}
