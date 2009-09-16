package org.marketcetera.photon.internal.strategy.engine.strategyagent.ui.workbench;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;

import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.marketcetera.photon.strategy.engine.model.core.ConnectionState;
import org.marketcetera.photon.strategy.engine.model.core.StrategyEngine;
import org.marketcetera.photon.strategy.engine.model.strategyagent.StrategyAgentEngine;
import org.marketcetera.photon.strategy.engine.model.strategyagent.impl.StrategyAgentEngineImpl;
import org.marketcetera.photon.strategy.engine.ui.workbench.tests.StrategyEnginesViewFixture;
import org.marketcetera.photon.test.AbstractUIRunner;
import org.marketcetera.photon.test.ContextMenuHelper;
import org.marketcetera.photon.test.WorkbenchRunner;
import org.marketcetera.photon.test.AbstractUIRunner.ThrowableRunnable;
import org.marketcetera.photon.test.AbstractUIRunner.UI;

import com.google.common.collect.Lists;

/* $License$ */

/**
 * Tests {@link DeleteHandler}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@RunWith(WorkbenchRunner.class)
public class DeleteHandlerTest {

    private volatile WritableList mEngines;
    private volatile StrategyEnginesViewFixture mView;
    private volatile StrategyAgentEngine mEngine;

    @Before
    @UI
    public void before() throws Exception {
        mEngine = new StrategyAgentEngineImpl() {
            @Override
            public void disconnect() throws Exception {
                setConnectionState(ConnectionState.DISCONNECTED);
            }
        };
        mEngine.setName("My Engine");
        mEngines = new WritableList(Lists.newArrayList(mEngine),
                StrategyEngine.class);
        mView = StrategyEnginesViewFixture.openView();
        mView.setModel(mEngines);
    }

    @After
    public void after() throws Exception {
        mView.close();
    }

    @Test
    public void testDelete() throws Exception {
        SWTBotTree tree = mView.getView().bot().tree();
        tree.select("My Engine");
        ContextMenuHelper.clickContextMenu(tree, "Delete");
        SWTBot bot = new SWTBot();
        bot.shell("Confirm Delete");
        bot.label("Do you want to delete 'My Engine'?");
        bot.button("OK").click();
        Thread.sleep(500);
        AbstractUIRunner.syncRun(new ThrowableRunnable() {
            @Override
            public void run() throws Throwable {
                assertThat(mEngines.size(), is(0));
            }
        });
    }

    @Test
    public void testCancel() throws Exception {
        SWTBotTree tree = mView.getView().bot().tree();
        tree.select("My Engine");
        ContextMenuHelper.clickContextMenu(tree, "Delete");
        SWTBot bot = new SWTBot();
        bot.shell("Confirm Delete");
        bot.label("Do you want to delete 'My Engine'?");
        bot.button("Cancel").click();
        Thread.sleep(500);
        AbstractUIRunner.syncRun(new ThrowableRunnable() {
            @Override
            public void run() throws Throwable {
                assertThat(mEngines.get(0), sameInstance((Object) mEngine));
            }
        });
    }

    @Test
    public void testConnected() throws Exception {
        AbstractUIRunner.syncRun(new ThrowableRunnable() {
            @Override
            public void run() throws Throwable {
                mEngine.setConnectionState(ConnectionState.CONNECTED);
            }
        });
        SWTBotTree tree = mView.getView().bot().tree();
        tree.select("My Engine");
        ContextMenuHelper.clickContextMenu(tree, "Delete");
        SWTBot bot = new SWTBot();
        bot.shell("Confirm Delete");
        bot.label("Do you want to delete 'My Engine'?");
        bot.button("OK").click();
        Thread.sleep(500);
        assertThat(mEngine.getConnectionState(),
                is(ConnectionState.DISCONNECTED));
        AbstractUIRunner.syncRun(new ThrowableRunnable() {
            @Override
            public void run() throws Throwable {
                assertThat(mEngines.size(), is(0));
            }
        });
    }

}
