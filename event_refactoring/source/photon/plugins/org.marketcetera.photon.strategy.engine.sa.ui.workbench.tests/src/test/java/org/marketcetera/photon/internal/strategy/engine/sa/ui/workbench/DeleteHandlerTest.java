package org.marketcetera.photon.internal.strategy.engine.sa.ui.workbench;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;
import java.util.concurrent.Callable;

import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.utils.SWTBotPreferences;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.eclipse.swtbot.swt.finder.waits.ICondition;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.marketcetera.photon.commons.ui.JFaceUtilsTest.ErrorDialogFixture;
import org.marketcetera.photon.internal.strategy.engine.sa.ui.workbench.DeleteHandler;
import org.marketcetera.photon.internal.strategy.engine.sa.ui.workbench.StrategyAgentHandlerTestBase.BlockingEngine;
import org.marketcetera.photon.strategy.engine.model.core.ConnectionState;
import org.marketcetera.photon.strategy.engine.model.core.StrategyEngine;
import org.marketcetera.photon.strategy.engine.ui.workbench.tests.StrategyEnginesViewContextMenuTestBase;
import org.marketcetera.photon.test.AbstractUIRunner;
import org.marketcetera.photon.test.WorkbenchRunner;
import org.marketcetera.photon.test.AbstractUIRunner.ThrowableRunnable;

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
public class DeleteHandlerTest extends StrategyEnginesViewContextMenuTestBase {

    protected final BlockingEngine mEngine1;
    protected final BlockingEngine mEngine2;
    protected final BlockingEngine mEngine3;

    public DeleteHandlerTest() {
        super("Delete");
        mEngine1 = new BlockingEngine("My Engine");
        mEngine2 = new BlockingEngine("My Engine 2");
        mEngine3 = new BlockingEngine("My Engine 3");
        mEngine3.setConnectionState(ConnectionState.CONNECTED);
    }

    @Override
    protected List<? extends StrategyEngine> createModel() {
        return Lists.newArrayList(mEngine1, mEngine2, mEngine3);
    }

    @Test
    public void testDelete() throws Exception {
        new TestTemplate() {
            @Override
            protected void select(SWTBotTree tree) {
                tree.select("My Engine");
            }

            @Override
            protected void validate() throws Exception {
                SWTBot bot = new SWTBot();
                bot.shell("Confirm Delete");
                bot.label("Do you want to delete 'My Engine'?");
                bot.button("OK").click();
                bot.waitUntil(enginesSize(2));
                AbstractUIRunner.syncRun(new ThrowableRunnable() {
                    @Override
                    public void run() throws Throwable {
                        assertThat(mEngines.get(0), is((Object) mEngine2));
                        assertThat(mEngines.get(1), is((Object) mEngine3));
                    }
                });
            }
        };
    }

    @Test
    public void testCancel() throws Exception {
        new TestTemplate() {
            @Override
            protected void select(SWTBotTree tree) {
                tree.select("My Engine");
            }

            @Override
            protected void validate() throws Exception {
                SWTBot bot = new SWTBot();
                bot.shell("Confirm Delete");
                bot.label("Do you want to delete 'My Engine'?");
                bot.button("Cancel").click();
                Thread.sleep(SWTBotPreferences.DEFAULT_POLL_DELAY);
                AbstractUIRunner.syncRun(new ThrowableRunnable() {
                    @Override
                    public void run() throws Throwable {
                        assertThat(mEngines.size(), is(3));
                    }
                });
            }
        };
    }

    @Test
    public void testConnected() throws Exception {
        new TestTemplate() {
            @Override
            protected void select(SWTBotTree tree) {
                tree.select("My Engine 3");
            }

            @Override
            protected void validate() throws Exception {
                SWTBot bot = new SWTBot();
                bot.shell("Confirm Delete");
                bot.label("Do you want to delete 'My Engine 3'?");
                bot.button("OK").click();
                mEngine3.acceptDisconnect(null);
                bot.waitUntil(enginesSize(2));
                AbstractUIRunner.syncRun(new ThrowableRunnable() {
                    @Override
                    public void run() throws Throwable {
                        assertThat(mEngines.get(0), is((Object) mEngine1));
                        assertThat(mEngines.get(1), is((Object) mEngine2));
                    }
                });
            }
        };
    }

    @Test
    public void testMultipleSelection() throws Exception {
        new TestTemplate() {
            @Override
            protected void select(SWTBotTree tree) {
                tree.select("My Engine", "My Engine 2", "My Engine 3");
            }

            @Override
            protected void validate() throws Exception {
                SWTBot bot = new SWTBot();
                bot.shell("Confirm Delete");
                bot.label("Do you want to delete the selected engines?");
                bot.button("OK").click();
                mEngine3.acceptDisconnect(null);
                bot.waitUntil(enginesSize(0));
            }
        };
    }

    @Test
    public void testDisconnectError() throws Exception {
        new TestTemplate() {
            @Override
            protected void select(SWTBotTree tree) {
                tree.select("My Engine", "My Engine 2", "My Engine 3");
            }

            @Override
            protected void validate() throws Exception {
                SWTBot bot = new SWTBot();
                bot.shell("Confirm Delete");
                bot.label("Do you want to delete the selected engines?");
                bot.button("OK").click();
                mEngine3.acceptDisconnect(new Exception("Error"));
                ErrorDialogFixture errorDialog = new ErrorDialogFixture();
                errorDialog.assertError("Error");
                errorDialog.dismiss();
                bot.waitUntil(enginesSize(1));
                AbstractUIRunner.syncRun(new ThrowableRunnable() {
                    @Override
                    public void run() throws Throwable {
                        assertThat(mEngines.get(0), is((Object) mEngine3));
                    }
                });
            }
        };
    }

    private ICondition enginesSize(final int i) {
        return new DefaultCondition() {
            @Override
            public boolean test() throws Exception {
                return AbstractUIRunner.syncCall(new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        return mEngines.size() == i;
                    }
                });
            }

            @Override
            public String getFailureMessage() {
                return "waiting for engine deletion";
            }
        };
    }

}
