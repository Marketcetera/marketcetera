package org.marketcetera.photon.internal.strategy.engine.ui.workbench;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.marketcetera.photon.strategy.engine.model.core.test.StrategyEngineCoreTestUtil.assertDeployedStrategy;
import static org.marketcetera.photon.strategy.engine.model.core.test.StrategyEngineCoreTestUtil.createDeployedStrategy;
import static org.marketcetera.photon.strategy.engine.model.core.test.StrategyEngineCoreTestUtil.createEngine;

import java.util.concurrent.CountDownLatch;

import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.results.VoidResult;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotCheckBox;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotText;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.dialogs.PropertyDialog;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.marketcetera.module.ModuleURN;
import org.marketcetera.photon.commons.ui.JFaceUtilsTest.ErrorDialogFixture;
import org.marketcetera.photon.commons.ui.workbench.ProgressUtilsTest.ProgressDialogFixture;
import org.marketcetera.photon.internal.strategy.engine.ui.workbench.NewPropertyInputDialogTest.NewPropertyInputDialogTestFixture;
import org.marketcetera.photon.strategy.engine.model.core.DeployedStrategy;
import org.marketcetera.photon.strategy.engine.model.core.Strategy;
import org.marketcetera.photon.strategy.engine.model.core.StrategyEngine;
import org.marketcetera.photon.strategy.engine.model.core.StrategyState;
import org.marketcetera.photon.strategy.engine.ui.tests.MockUIConnection;
import org.marketcetera.photon.strategy.engine.ui.workbench.StrategyEngineWorkbenchUI;
import org.marketcetera.photon.test.AbstractUIRunner;
import org.marketcetera.photon.test.ContextMenuHelper;
import org.marketcetera.photon.test.SWTTestUtil;
import org.marketcetera.photon.test.WorkbenchRunner;
import org.marketcetera.photon.test.AbstractUIRunner.ThrowableRunnable;
import org.marketcetera.photon.test.AbstractUIRunner.UI;

import com.google.common.collect.ImmutableMap;

/* $License$ */

/**
 * Tests {@link DeployedStrategyConfigurationPropertyPage}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
// see http://bugs.eclipse.org/231619
@SuppressWarnings("restriction")
@RunWith(WorkbenchRunner.class)
public class DeployedStrategyConfigurationPropertyPageTest {

    private static final int WAIT_TIME = 800;
    private final SWTBot mBot = new SWTBot();
    private volatile DeployedStrategy mStrategy;
    private PropertyDialog mDialog;
    private volatile StrategyEngine mEngine;

    @After
    @UI
    public void after() {
        if (mDialog != null) {
            mDialog.close();
        }
    }

    private void openDialog() throws Exception {
        AbstractUIRunner.syncRun(new ThrowableRunnable() {
            @Override
            public void run() throws Throwable {
                mStrategy = createDeployedStrategy("Strategy1");
                mStrategy.setUrn(new ModuleURN("metc:strategy:system:Strategy1"));
                mStrategy.setClassName("Claz");
                mStrategy.setLanguage("Lang");
                mStrategy.setScriptPath("c:\\path");
                mStrategy.setRouteOrdersToServer(true);
                mEngine = createEngine("Engine");
                mEngine.setConnection(new MockUIConnection());
                mEngine.getDeployedStrategies().add(mStrategy);
                mDialog = PropertyDialog
                        .createDialogOn(
                                PlatformUI.getWorkbench()
                                        .getActiveWorkbenchWindow().getShell(),
                                StrategyEngineWorkbenchUI.DEPLOYED_STRATEGY_CONFIGURATION_PROPERTY_PAGE_ID,
                                mStrategy);
                mDialog.setBlockOnOpen(false);
                mDialog.open();
            }
        });
        mBot.shell("Properties for Strategy1 on Engine");
        SWTTestUtil.assertButtonDoesNotExist("Apply");
        SWTTestUtil.assertButtonDoesNotExist("Restore Defaults");
        SWTTestUtil.testReadOnlyText(mBot.textWithLabel("Instance Name:"),
                "Strategy1");
        SWTTestUtil.testReadOnlyText(mBot.textWithLabel("Class:"), "Claz");
        SWTTestUtil.testReadOnlyText(mBot.textWithLabel("Language:"), "Lang");
        SWTTestUtil.testReadOnlyText(mBot.textWithLabel("Script:"), "c:\\path");
    }

    @Test
    public void testChangeProperties() throws Exception {
        openDialog();
        SWTBotCheckBox checkBox = mBot.checkBox("Route orders to server");
        assertThat(checkBox.isChecked(), is(true));
        checkBox.click();
        mBot.button("Add New Property").click();
        new NewPropertyInputDialogTestFixture("key", "value").inputData();
        mBot.button("Add New Property").click();
        new NewPropertyInputDialogTestFixture("key2", "value2").inputData();
        mBot.button("OK").click();
        Thread.sleep(WAIT_TIME);
        AbstractUIRunner.syncRun(new ThrowableRunnable() {
            @Override
            public void run() throws Throwable {
                assertDeployedStrategy(mStrategy, mEngine, StrategyState.STOPPED,
                        "Strategy1", "Claz", "Lang", "c:\\path",
                        false, ImmutableMap.of("key", "value",
                                "key2", "value2"));
            }
        });
    }

    @Test
    public void testDisabledForRunningStrategy() throws Exception {
        AbstractUIRunner.syncRun(new ThrowableRunnable() {
            @Override
            public void run() throws Throwable {
                mStrategy = createDeployedStrategy("Strategy1");
                mStrategy.setClassName("Claz");
                mStrategy.setLanguage("Lang");
                mStrategy.setScriptPath("c:\\path");
                mStrategy.setRouteOrdersToServer(true);
                mStrategy.setState(StrategyState.RUNNING);
                mEngine = createEngine("Engine");
                mEngine.setConnection(new MockUIConnection());
                mEngine.getDeployedStrategies().add(mStrategy);
                mDialog = PropertyDialog
                        .createDialogOn(
                                PlatformUI.getWorkbench()
                                        .getActiveWorkbenchWindow().getShell(),
                                StrategyEngineWorkbenchUI.DEPLOYED_STRATEGY_CONFIGURATION_PROPERTY_PAGE_ID,
                                mStrategy);
                mDialog.setBlockOnOpen(false);
                mDialog.open();
            }
        });
        mBot.shell("Properties for Strategy1 on Engine");
        testDisabled(mBot.textWithLabel("Instance Name:"), "Strategy1");
        testDisabled(mBot.textWithLabel("Class:"), "Claz");
        testDisabled(mBot.textWithLabel("Language:"), "Lang");
        testDisabled(mBot.textWithLabel("Script:"), "c:\\path");
        assertThat(mBot.checkBox("Route orders to server").isEnabled(),
                is(false));
        assertThat(mBot.checkBox("Route orders to server").isChecked(),
                is(true));
        // use index 1 since first tree is property page navigation tree
        assertThat(mBot.tree(1).isEnabled(), is(false));
        assertThat(mBot.button("Add New Property").isEnabled(), is(false));
    }

    private void testDisabled(SWTBotText text, String value) {
        assertThat(text.isEnabled(), is(false));
        assertThat(text.getText(), is(value));
    }

    @Test
    public void testCancelChange() throws Exception {
        openDialog();
        mBot.checkBox("Route orders to server").click();
        mBot.button("Add New Property").click();
        new NewPropertyInputDialogTestFixture("key2", "value2").inputData();
        mBot.button("Cancel").click();
        Thread.sleep(WAIT_TIME);
        AbstractUIRunner.syncRun(new ThrowableRunnable() {
            @Override
            public void run() throws Throwable {
                assertDeployedStrategy(mStrategy, mEngine, StrategyState.STOPPED,
                        "Strategy1", "Claz", "Lang", "c:\\path",
                        true, null);
            }
        });
    }

    @Test
    public void testInlineEditing() throws Exception {
        openDialog();
        mBot.button("Add New Property").click();
        new NewPropertyInputDialogTestFixture("key2", "value2").inputData();
        // use index 1 since first tree is property page navigation tree
        CustomTree tree = new CustomTree(mBot.tree(1).widget);
        tree.click(0, 1);
        mBot.sleep(500);
        mBot.text("value2").setText("value3");
        mBot.button("OK").click();
        Thread.sleep(WAIT_TIME);
        AbstractUIRunner.syncRun(new ThrowableRunnable() {
            @Override
            public void run() throws Throwable {
                assertDeployedStrategy(mStrategy, mEngine, StrategyState.STOPPED,
                        "Strategy1", "Claz", "Lang", "c:\\path",
                        true, ImmutableMap.of("key2", "value3"));
            }
        });
    }

    @Test
    public void testPropertiesContextMenu() throws Exception {
        openDialog();
        // use index 1 since first tree is property page navigation tree
        SWTBotTree tree = mBot.tree(1);
        ContextMenuHelper.clickContextMenu(tree, "Add");
        new NewPropertyInputDialogTestFixture("keyx", "valuex").inputData();
        ContextMenuHelper.clickContextMenu(tree, "Add");
        new NewPropertyInputDialogTestFixture("keyz", "valuez").inputData();
        tree.getTreeItem("keyz").select();
        ContextMenuHelper.clickContextMenu(tree, "Delete");
        tree.unselect();
        // delete should not show up when the selection is empty
        assertThat(SWTTestUtil.getMenuItems(tree).get("Delete"), nullValue());
        mBot.button("OK").click();
        Thread.sleep(WAIT_TIME);
        AbstractUIRunner.syncRun(new ThrowableRunnable() {
            @Override
            public void run() throws Throwable {
                assertDeployedStrategy(mStrategy, mEngine, StrategyState.STOPPED,
                        "Strategy1", "Claz", "Lang", "c:\\path",
                        true, ImmutableMap.of("keyx", "valuex"));
            }
        });
    }

    @Test
    public void testProgressAndErrorReported() throws Exception {
        openDialog();
        final CountDownLatch latch = new CountDownLatch(1);
        try {
            AbstractUIRunner.syncRun(new ThrowableRunnable() {
                @Override
                public void run() throws Throwable {
                    mEngine.setConnection(new MockUIConnection() {
                        @Override
                        public void update(DeployedStrategy strategy,
                                Strategy newConfiguration) throws Exception {
                            latch.await();
                            throw new Exception("Update Failed");
                        }
                    });
                }
            });
            mBot.button("OK").click();
            ProgressDialogFixture fixture = new ProgressDialogFixture();
            fixture.assertTask("Updating strategy configuration on 'Engine'...");
            latch.countDown();
            ErrorDialogFixture errorDialog = new ErrorDialogFixture();
            errorDialog.assertError("Update Failed");
            errorDialog.dismiss();
        } finally {
            latch.countDown();
        }
    }

    /**
     * A tree that lets me click a cell.
     */
    private static class CustomTree extends SWTBotTree {

        public CustomTree(Tree tree) throws WidgetNotFoundException {
            super(tree);
        }

        public void click(final int row, final int column) {
            setFocus();
            select(row);
            asyncExec(new VoidResult() {
                public void run() {
                    TreeItem item = widget.getItem(row);
                    Rectangle cellBounds = item.getBounds(column);
                    clickXY(cellBounds.x + (cellBounds.width / 2), cellBounds.y
                            + (cellBounds.height / 2));
                }
            });
        }

    }

}
