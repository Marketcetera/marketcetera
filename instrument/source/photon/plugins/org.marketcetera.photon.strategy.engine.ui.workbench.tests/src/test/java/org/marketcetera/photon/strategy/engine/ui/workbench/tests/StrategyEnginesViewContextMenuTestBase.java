package org.marketcetera.photon.strategy.engine.ui.workbench.tests;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.ObjectUtils;
import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.marketcetera.photon.internal.strategy.engine.ui.workbench.handlers.StartHandler;
import org.marketcetera.photon.strategy.engine.model.core.DeployedStrategy;
import org.marketcetera.photon.strategy.engine.model.core.StrategyEngine;
import org.marketcetera.photon.strategy.engine.model.core.impl.StrategyEngineConnectionImpl;
import org.marketcetera.photon.test.ContextMenuHelper;
import org.marketcetera.photon.test.PhotonTestBase;
import org.marketcetera.photon.test.WorkbenchRunner;
import org.marketcetera.photon.test.AbstractUIRunner.UI;

/* $License$ */

/**
 * Tests {@link StartHandler}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@RunWith(WorkbenchRunner.class)
public abstract class StrategyEnginesViewContextMenuTestBase extends
        PhotonTestBase {

    private final String mMenuItem;
    protected volatile WritableList mEngines;
    protected volatile StrategyEnginesViewFixture mView;

    public StrategyEnginesViewContextMenuTestBase(String menuItem) {
        mMenuItem = menuItem;
    }

    @Before
    @UI
    public void before() throws Exception {
        mEngines = new WritableList(createModel(), StrategyEngine.class);
        mView = StrategyEnginesViewFixture.openView();
        mView.setModel(mEngines);
    }

    protected abstract List<? extends StrategyEngine> createModel();

    @After
    public void after() throws Exception {
        mView.close();
    }

    protected abstract class TestTemplate {
        public TestTemplate() throws Exception {
            run();
        }

        public void run() throws Exception {
            SWTBotTree tree = mView.getView().bot().tree();
            select(tree);
            ContextMenuHelper.clickContextMenu(tree, mMenuItem);
            validate();
        }

        abstract protected void validate() throws Exception;

        abstract protected void select(SWTBotTree tree);
    }

    protected static class BlockingConnection extends
            StrategyEngineConnectionImpl {

        private BlockingQueue<Object> mStart = new SynchronousQueue<Object>();
        private BlockingQueue<Object> mStop = new SynchronousQueue<Object>();
        private BlockingQueue<Object> mUndeploy = new SynchronousQueue<Object>();
        private BlockingQueue<Object> mRefresh = new SynchronousQueue<Object>();
        private Object REFRESH_ENGINE = new Object();

        public BlockingConnection() {
        }

        @Override
        public void start(DeployedStrategy strategy) throws Exception {
            pollHelper(mStart, strategy);
        }

        public void acceptStart(Object object) throws Exception {
            mStart.offer(object, 5, TimeUnit.SECONDS);
        }

        @Override
        public void stop(DeployedStrategy strategy) throws Exception {
            pollHelper(mStop, strategy);
        }

        public void acceptStop(Object object) throws Exception {
            mStop.offer(object, 5, TimeUnit.SECONDS);
        }

        @Override
        public void undeploy(DeployedStrategy strategy) throws Exception {
            pollHelper(mUndeploy, strategy);
        }

        public void acceptUndeploy(Object object) throws Exception {
            mUndeploy.offer(object, 5, TimeUnit.SECONDS);
        }

        @Override
        public void refresh(DeployedStrategy strategy) throws Exception {
            pollHelper(mRefresh, strategy);
        }

        @Override
        public void refresh() throws Exception {
            pollHelper(mRefresh, REFRESH_ENGINE);
        }

        public void acceptRefresh(Object object) throws Exception {
            mRefresh.offer(ObjectUtils.defaultIfNull(object, REFRESH_ENGINE),
                    5, TimeUnit.SECONDS);
        }

        private void pollHelper(BlockingQueue<Object> queue, Object expected)
                throws Exception {
            Object queued = queue.poll(5, TimeUnit.SECONDS);
            if (queued instanceof Exception) {
                throw (Exception) queued;
            } else {
                assertThat(queued, is(expected));
            }
        }
    }
}
