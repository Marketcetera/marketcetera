package org.marketcetera.photon.internal.strategy.engine.ui.workbench;

import static org.eclipse.swtbot.swt.finder.SWTBotAssert.assertText;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.marketcetera.photon.strategy.engine.model.core.test.StrategyEngineCoreTestUtil.buildEngines;
import static org.marketcetera.photon.strategy.engine.model.core.test.StrategyEngineCoreTestUtil.createConnectedEngine;
import static org.marketcetera.photon.strategy.engine.model.core.test.StrategyEngineCoreTestUtil.createDeployedStrategy;
import static org.marketcetera.photon.strategy.engine.model.core.test.StrategyEngineCoreTestUtil.createEngine;
import static org.marketcetera.photon.test.SWTBotConditions.itemImageIsReplaced;
import static org.marketcetera.photon.test.SWTBotConditions.treeItemForegroundIs;
import static org.marketcetera.photon.test.SWTBotConditions.widgetTextIs;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.eclipse.ui.handlers.IHandlerService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.marketcetera.photon.commons.ui.EclipseImages;
import org.marketcetera.photon.strategy.engine.model.core.ConnectionState;
import org.marketcetera.photon.strategy.engine.model.core.DeployedStrategy;
import org.marketcetera.photon.strategy.engine.model.core.StrategyEngine;
import org.marketcetera.photon.strategy.engine.model.core.StrategyState;
import org.marketcetera.photon.strategy.engine.ui.StrategyEngineImage;
import org.marketcetera.photon.strategy.engine.ui.StrategyEngineColors.StrategyEngineColor;
import org.marketcetera.photon.strategy.engine.ui.workbench.StrategyEngineWorkbenchUI;
import org.marketcetera.photon.strategy.engine.ui.workbench.tests.StrategyEnginesViewFixture;
import org.marketcetera.photon.test.AbstractUIRunner;
import org.marketcetera.photon.test.JFaceAsserts;
import org.marketcetera.photon.test.MenuState;
import org.marketcetera.photon.test.SWTTestUtil;
import org.marketcetera.photon.test.WorkbenchRunner;
import org.marketcetera.photon.test.AbstractUIRunner.ThrowableRunnable;
import org.marketcetera.photon.test.AbstractUIRunner.UI;

/* $License$ */

/**
 * Test {@link StrategyEnginesView}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@RunWith(WorkbenchRunner.class)
public class StrategyEnginesViewTest {

    private volatile WritableList mModel;
    private volatile StrategyEngine mEngine1;
    private volatile SWTBot mViewBot;
    private volatile StrategyEnginesViewFixture mFixture;

    @Before
    public void before() throws Exception {
        AbstractUIRunner.syncRun(new ThrowableRunnable() {
            @Override
            public void run() {
                mEngine1 = createConnectedEngine("Engine 1");
                mModel = new WritableList(buildEngines(mEngine1),
                        StrategyEngine.class);
            }
        });
        mFixture = StrategyEnginesViewFixture.openView();
        mFixture.setModel(mModel);
        mViewBot = mFixture.getView().bot();
    }

    @After
    public void after() throws Exception {
        mFixture.close();
        AbstractUIRunner.syncRun(new ThrowableRunnable() {
            @Override
            public void run() {
                mModel.dispose();
            }
        });
    }

    @Test
    public void testTitle() throws Exception {
        assertThat(mFixture.getView().getTitle(), is("Strategy Engines"));
    }

    @Test
    @UI
    public void testTitleImage() throws Exception {
        JFaceAsserts.assertImage(mFixture.getRealView().getTitleImage(),
                EclipseImages.VIEW.getImageDescriptor(
                        StrategyEngineWorkbenchUI.PLUGIN_ID,
                        "strategy_engines.gif"));
    }

    @Test
    public void testDynamicContent() throws Exception {
        SWTBotTreeItem[] allItems = mViewBot.tree().getAllItems();
        assertThat(allItems.length, is(1));
        assertText("Engine 1", allItems[0]);

        mFixture.setModel(null);
        AbstractUIRunner.syncRun(new ThrowableRunnable() {
            @Override
            public void run() throws Exception {
                mModel.dispose();
            }
        });
        allItems = mViewBot.tree().getAllItems();
        assertThat(allItems.length, is(0));
        AbstractUIRunner.syncRun(new ThrowableRunnable() {
            @Override
            public void run() throws Exception {
                mModel = new WritableList(
                        buildEngines(createEngine("Strategy Engine 2")),
                        StrategyEngine.class);
            }
        });
        mFixture.setModel(mModel);
        allItems = mViewBot.tree().getAllItems();
        assertThat(allItems.length, is(1));
        assertText("Strategy Engine 2", allItems[0]);

        AbstractUIRunner.syncRun(new ThrowableRunnable() {
            @Override
            public void run() throws Exception {
                mModel.addAll(buildEngines(createEngine("Strategy Engine 3"),
                        createDeployedStrategy("strat1")));
            }
        });
        allItems = mViewBot.tree().getAllItems();
        assertThat(allItems.length, is(2));
        SWTBotTreeItem engine2 = allItems[0];
        assertText("Strategy Engine 2", engine2);
        assertThat(engine2.getItems().length, is(0));
        SWTBotTreeItem engine3 = allItems[1];
        assertText("Strategy Engine 3", engine3);
        engine3.expand();
        SWTBotTreeItem[] children = engine3.getItems();
        assertThat(children.length, is(1));
        assertText("strat1", children[0]);

        AbstractUIRunner.syncRun(new ThrowableRunnable() {
            @Override
            public void run() throws Exception {
                ((StrategyEngine) mModel.get(0)).setName("Strategy Engine 2b");
            }
        });
        mViewBot.waitUntil(widgetTextIs(engine2, "Strategy Engine 2b"));
    }

    @Test
    public void testDisposedModel() throws Exception {
        AbstractUIRunner.syncRun(new ThrowableRunnable() {
            @Override
            public void run() throws Exception {
                mModel.dispose();
            }
        });
        SWTBotTreeItem[] allItems = mViewBot.tree().getAllItems();
        assertThat(allItems.length, is(0));
    }

    @Test
    public void testRootContextMenu() throws Exception {
        final SWTBotTree tree = mViewBot.tree();
        tree.unselect();
        assertThat(SWTTestUtil.getMenuItems(tree).size(), is(0));
    }

    @Test
    public void testEngineConnectionState() throws Exception {
        SWTBotTree tree = mViewBot.tree();
        SWTBotTreeItem item = tree.getAllItems()[0];
        assertEngineConnected(tree, item);
        AbstractUIRunner.syncRun(new ThrowableRunnable() {
            @Override
            public void run() throws Exception {
                mEngine1.setConnectionState(ConnectionState.DISCONNECTED);
            }
        });
        assertEngineDisconnected(item, tree);
        AbstractUIRunner.syncRun(new ThrowableRunnable() {
            @Override
            public void run() throws Exception {
                mEngine1.setConnectionState(ConnectionState.CONNECTED);
            }
        });
        assertEngineConnected(tree, item);
    }

    @Test
    public void testConnectCommandHandled() throws Exception {
        SWTBotTree tree = mViewBot.tree();
        SWTBotTreeItem item = tree.getAllItems()[0];
        activateMockHandler("org.marketcetera.photon.strategy.engine.ui.workbench.connect");
        item.select();
        singleEngineSelectionHelper(tree, true, true, true, true, true);
        assertThat(SWTTestUtil.getMenuItems(tree).get("Connect").isEnabled(),
                is(false));
    }

    @Test
    public void testDisconnectCommandHandled() throws Exception {
        SWTBotTree tree = mViewBot.tree();
        SWTBotTreeItem item = tree.getAllItems()[0];
        activateMockHandler("org.marketcetera.photon.strategy.engine.ui.workbench.disconnect");
        item.select();
        singleEngineSelectionHelper(tree, true, true, true, true, true);
        assertThat(
                SWTTestUtil.getMenuItems(tree).get("Disconnect").isEnabled(),
                is(false));
    }

    @Test
    public void testConnectAndDisconnectHandled() throws Exception {
        SWTBotTree tree = mViewBot.tree();
        SWTBotTreeItem item = tree.getAllItems()[0];
        activateMockHandler("org.marketcetera.photon.strategy.engine.ui.workbench.connect");
        activateMockHandler("org.marketcetera.photon.strategy.engine.ui.workbench.disconnect");
        item.select();
        assertThat(SWTTestUtil.getMenuItems(tree).get("Connect").isEnabled(),
                is(false));
        assertThat(
                SWTTestUtil.getMenuItems(tree).get("Disconnect").isEnabled(),
                is(false));
    }

    @Test
    public void testDeleteCommandHandled() throws Exception {
        SWTBotTree tree = mViewBot.tree();
        SWTBotTreeItem item = tree.getAllItems()[0];
        activateMockHandler("org.eclipse.ui.edit.delete");
        item.select();
        singleEngineSelectionHelper(tree, true, true, true, true, true);
        assertThat(SWTTestUtil.getMenuItems(tree).get("Delete\tDelete")
                .isEnabled(), is(false));
    }

    @Test
    @UI
    public void testIStrategyEnginesAPI() throws Exception {
        assertThat(mFixture.getRealView().getStrategyEngines(),
                is((IObservableList) mModel));
        int size = mModel.size();
        StrategyEngine newEngine = createEngine("asdf");
        mFixture.getRealView().addEngine(newEngine);
        assertThat(mModel.size(), is(size + 1));
        assertThat(mModel.get(size), is((Object) newEngine));
        mFixture.getRealView().removeEngine(newEngine);
        assertThat(mModel.size(), is(size));
    }

    @Test
    public void testDoubleClick() throws Exception {
        SWTBotTreeItem item = mViewBot.tree().getAllItems()[0];
        item.doubleClick();
        SWTBot bot = new SWTBot();
        bot.shell("Properties for Engine 1").close();
    }

    private StrategyEngine mEngine2;
    private DeployedStrategy mStrategy1;
    private DeployedStrategy mStrategy2;

    @Test
    public void testMultipleSelection() throws Exception {
        AbstractUIRunner.syncRun(new ThrowableRunnable() {
            @Override
            public void run() throws Exception {
                mEngine2 = createConnectedEngine("Engine 2");
                mModel.add(mEngine2);
            }
        });
        final SWTBotTree tree = mViewBot.tree();
        tree.select(0, 1);
        multiEngineSelectionHelper(tree, true, true, true);

        AbstractUIRunner.syncRun(new ThrowableRunnable() {
            @Override
            public void run() throws Exception {
                mEngine1.setConnectionState(ConnectionState.DISCONNECTED);
            }
        });
        multiEngineSelectionHelper(tree, false, false, false);

        AbstractUIRunner.syncRun(new ThrowableRunnable() {
            @Override
            public void run() throws Exception {
                mEngine2.setConnectionState(ConnectionState.DISCONNECTED);
            }
        });
        multiEngineSelectionHelper(tree, false, false, false);

        AbstractUIRunner.syncRun(new ThrowableRunnable() {
            @Override
            public void run() throws Exception {
                mEngine1.setConnectionState(ConnectionState.CONNECTED);
                mStrategy1 = createDeployedStrategy("strat1");
                mStrategy2 = createDeployedStrategy("strat2");
                mEngine1.getDeployedStrategies().add(mStrategy1);
                mEngine1.getDeployedStrategies().add(mStrategy2);
            }
        });
        final SWTBotTreeItem parent = tree.getAllItems()[0];
        parent.expand();
        parent.select("strat1", "strat2");
        multiStrategySelectionHelper(tree, true, true, false, true, false);

        AbstractUIRunner.syncRun(new ThrowableRunnable() {
            @Override
            public void run() throws Exception {
                mStrategy1.setState(StrategyState.RUNNING);
            }
        });
        multiStrategySelectionHelper(tree, true, true, true, true, true);

        AbstractUIRunner.syncRun(new ThrowableRunnable() {
            @Override
            public void run() throws Exception {
                mStrategy1.setState(StrategyState.RUNNING);
                mStrategy2.setState(StrategyState.RUNNING);
            }
        });
        multiStrategySelectionHelper(tree, true, false, true, true, true);

        /*
         * Would like to test combination of engine/strategy selection but can't
         * figure out how to do that with SWT Bot.
         */
    }

    @Test
    public void testStrategyState() throws Exception {
        AbstractUIRunner.syncRun(new ThrowableRunnable() {
            @Override
            public void run() throws Exception {
                mStrategy1 = createDeployedStrategy("strat1");
                mEngine1.getDeployedStrategies().add(mStrategy1);
            }
        });
        SWTBotTree tree = mViewBot.tree();
        final SWTBotTreeItem parent = tree.getAllItems()[0];
        parent.expand();
        SWTBotTreeItem item = parent.getItems()[0];
        assertStrategyStopped(tree, item);
        AbstractUIRunner.syncRun(new ThrowableRunnable() {
            @Override
            public void run() throws Exception {
                mStrategy1.setState(StrategyState.RUNNING);
            }
        });
        assertStrategyStarted(tree, item);
        AbstractUIRunner.syncRun(new ThrowableRunnable() {
            @Override
            public void run() throws Exception {
                mStrategy1.setState(StrategyState.STOPPED);
            }
        });
        assertStrategyStopped(tree, item);
    }

    private void activateMockHandler(final String commandId) throws Exception {
        AbstractUIRunner.syncRun(new ThrowableRunnable() {
            @Override
            public void run() throws Exception {
                IHandlerService service = (IHandlerService) mFixture
                        .getRealView().getSite().getService(
                                IHandlerService.class);
                AbstractHandler mockHandler = mock(AbstractHandler.class);
                when(mockHandler.isHandled()).thenReturn(true);
                service.activateHandler(commandId, mockHandler);
            }
        });
    }

    private void assertStrategyStarted(SWTBotTree tree, SWTBotTreeItem item)
            throws Exception {
        item.select();
        mViewBot.waitUntil(itemImageIsReplaced(item,
                StrategyEngineImage.STRATEGY_OBJ.getImageDescriptor(),
                StrategyEngineImage.STRATEGY_RUNNING_OBJ.getImageDescriptor()));
        singleStrategySelectionHelper(tree, true, false, true, true, true, true);
    }

    private void assertStrategyStopped(SWTBotTree tree, SWTBotTreeItem item)
            throws Exception {
        item.select();
        mViewBot.waitUntil(itemImageIsReplaced(item,
                StrategyEngineImage.STRATEGY_OBJ.getImageDescriptor(),
                StrategyEngineImage.STRATEGY_STOPPED_OBJ.getImageDescriptor()));
        singleStrategySelectionHelper(tree, true, true, false, true, true, false);
    }

    private void assertEngineConnected(SWTBotTree tree, SWTBotTreeItem item)
            throws Exception {
        item.select();
        mViewBot.waitUntil(itemImageIsReplaced(item,
                StrategyEngineImage.ENGINE_OBJ.getImageDescriptor(),
                StrategyEngineImage.ENGINE_CONNECTED_OBJ.getImageDescriptor()));
        singleEngineSelectionHelper(tree, true, true, true, true, true);
    }

    private void assertEngineDisconnected(SWTBotTreeItem item, SWTBotTree tree)
            throws Exception {
        mViewBot.waitUntil(itemImageIsReplaced(item,
                StrategyEngineImage.ENGINE_OBJ.getImageDescriptor(),
                StrategyEngineImage.ENGINE_DISCONNECTED_OBJ
                        .getImageDescriptor()));
        mViewBot.waitUntil(treeItemForegroundIs(item,
                StrategyEngineColor.ENGINE_DISCONNECTED.getColor()));
        singleEngineSelectionHelper(tree, false, false, false, false, true);
    }

    private void singleEngineSelectionHelper(SWTBotTree tree,
            boolean deployEnabled, boolean startAllEnabled,
            boolean stopAllEnabled, boolean refreshEnabled,
            boolean propertiesEnabled) throws Exception {
        Map<String, MenuState> items = SWTTestUtil.getMenuItems(tree);
        assertThat(items.get("Deploy...").isEnabled(), is(deployEnabled));
        assertThat(items.get("Start All").isEnabled(), is(startAllEnabled));
        assertThat(items.get("Stop All").isEnabled(), is(stopAllEnabled));
        assertThat(items.get("Refresh\tF5").isEnabled(), is(refreshEnabled));
        assertThat(items.get("Properties\tAlt+Enter").isEnabled(),
                is(propertiesEnabled));
    }

    private void singleStrategySelectionHelper(SWTBotTree tree,
            boolean undeployEnabled, boolean startEnabled, boolean stopEnabled,
            boolean refreshEnabled, boolean propertiesEnabled, boolean restartEnabled) throws Exception {
        Map<String, MenuState> items = SWTTestUtil.getMenuItems(tree);
        assertThat(items.get("Undeploy").isEnabled(), is(undeployEnabled));
        assertThat(items.get("Start").isEnabled(), is(startEnabled));
        assertThat(items.get("Stop").isEnabled(), is(stopEnabled));
        assertThat(items.get("Refresh\tF5").isEnabled(), is(refreshEnabled));
        assertThat(items.get("Properties\tAlt+Enter").isEnabled(),
                is(propertiesEnabled));
        assertThat(items.get("Restart").isEnabled(), is(restartEnabled));
    }

    private void multiEngineSelectionHelper(SWTBotTree tree,
            boolean startAllEnabled, boolean stopAllEnabled,
            boolean refreshEnabled) throws Exception {
        Map<String, MenuState> items = SWTTestUtil.getMenuItems(tree);
        assertThat(items.get("Start All").isEnabled(), is(startAllEnabled));
        assertThat(items.get("Stop All").isEnabled(), is(stopAllEnabled));
        assertThat(items.get("Refresh\tF5").isEnabled(), is(refreshEnabled));
    }

    private void multiStrategySelectionHelper(SWTBotTree tree,
            boolean undeployEnabled, boolean startEnabled, boolean stopEnabled,
            boolean refreshEnabled, boolean restartEnabled) throws Exception {
        Map<String, MenuState> items = SWTTestUtil.getMenuItems(tree);
        assertThat(items.get("Undeploy").isEnabled(), is(undeployEnabled));
        assertThat(items.get("Start").isEnabled(), is(startEnabled));
        assertThat(items.get("Stop").isEnabled(), is(stopEnabled));
        assertThat(items.get("Refresh\tF5").isEnabled(), is(refreshEnabled));
        assertThat(items.get("Restart").isEnabled(), is(restartEnabled));
    }
}
