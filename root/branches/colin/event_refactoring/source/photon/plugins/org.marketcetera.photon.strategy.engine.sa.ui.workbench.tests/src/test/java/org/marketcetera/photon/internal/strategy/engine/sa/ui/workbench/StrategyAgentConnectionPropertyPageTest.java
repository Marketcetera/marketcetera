package org.marketcetera.photon.internal.strategy.engine.sa.ui.workbench;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.dialogs.PropertyDialog;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.marketcetera.photon.strategy.engine.model.core.ConnectionState;
import org.marketcetera.photon.strategy.engine.model.sa.StrategyAgentEngine;
import org.marketcetera.photon.strategy.engine.sa.tests.StrategyAgentEngineTestUtil;
import org.marketcetera.photon.strategy.engine.sa.ui.workbench.StrategyAgentEngineWorkbenchUI;
import org.marketcetera.photon.strategy.engine.ui.workbench.StrategyEngineWorkbenchUI;
import org.marketcetera.photon.test.AbstractUIRunner;
import org.marketcetera.photon.test.SWTTestUtil;
import org.marketcetera.photon.test.WorkbenchRunner;
import org.marketcetera.photon.test.AbstractUIRunner.ThrowableRunnable;
import org.marketcetera.photon.test.AbstractUIRunner.UI;

/* $License$ */

/**
 * 
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@SuppressWarnings("restriction")
@RunWith(WorkbenchRunner.class)
public class StrategyAgentConnectionPropertyPageTest {

    private final SWTBot mBot = new SWTBot();
    private volatile StrategyAgentEngine mStrategyAgentEngine;
    private volatile PropertyDialog mDialog;

    @Before
    public void beforeClass() {
        /*
         * Hack to ensure this plugin is loaded since it's adapters are needed.
         * It a normal scenario the plugin will always be loaded before the
         * property page can be launched, but here we are side stepping the
         * typical initialization.
         */
        assertThat(StrategyEngineWorkbenchUI.class.getName(), not(is(String
                .valueOf(hashCode()))));
    }

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
                if (mStrategyAgentEngine == null) {
                    mStrategyAgentEngine = StrategyAgentEngineTestUtil
                            .createStrategyAgentEngine(null, null, "url1",
                                    "host1", 1);
                }
                mStrategyAgentEngine.setName("SAEngine");
                mDialog = PropertyDialog
                        .createDialogOn(
                                PlatformUI.getWorkbench()
                                        .getActiveWorkbenchWindow().getShell(),
                                StrategyAgentEngineWorkbenchUI.STRATEGY_AGENT_CONNECTION_PROPERTY_PAGE_ID,
                                mStrategyAgentEngine);
                mDialog.setBlockOnOpen(false);
                mDialog.open();
            }
        });
        mBot.shell("Properties for SAEngine");
        SWTTestUtil.assertButtonDoesNotExist("Apply");
        SWTTestUtil.assertButtonDoesNotExist("Restore Defaults");
    }

    @Test
    public void testChangeProperties() throws Exception {
        openDialog();
        mBot.textWithLabel("JMS URL:").setText("url2");
        mBot.textWithLabel("Web Service Hostname:").setText("host2");
        mBot.textWithLabel("Web Service Port:").setText("2");
        mBot.button("OK").click();
        StrategyAgentEngineTestUtil.assertStrategyAgentEngine(
                mStrategyAgentEngine, "SAEngine", null, "url2", "host2", 2,
                ConnectionState.DISCONNECTED);
    }

    @Test
    public void testCancelChange() throws Exception {
        openDialog();
        mBot.textWithLabel("JMS URL:").setText("url2");
        mBot.textWithLabel("Web Service Hostname:").setText("host2");
        mBot.textWithLabel("Web Service Port:").setText("2");
        mBot.button("Cancel").click();
        StrategyAgentEngineTestUtil.assertStrategyAgentEngine(
                mStrategyAgentEngine, "SAEngine", null, "url1", "host1", 1,
                ConnectionState.DISCONNECTED);
    }

    @Test
    public void testReadOnly() throws Exception {
        AbstractUIRunner.syncRun(new ThrowableRunnable() {
            @Override
            public void run() throws Throwable {
                mStrategyAgentEngine = StrategyAgentEngineTestUtil
                        .createStrategyAgentEngine(null, null, "url1", "host1",
                                1);
                mStrategyAgentEngine
                        .setConnectionState(ConnectionState.CONNECTED);
            }
        });
        openDialog();
        SWTTestUtil.testReadOnlyText(mBot.textWithLabel("JMS URL:"), "url1");
        SWTTestUtil.testReadOnlyText(mBot
                .textWithLabel("Web Service Hostname:"), "host1");
        SWTTestUtil.testReadOnlyText(mBot.textWithLabel("Web Service Port:"),
                "1");
        mBot.button("OK").click();
        StrategyAgentEngineTestUtil.assertStrategyAgentEngine(
                mStrategyAgentEngine, "SAEngine", null, "url1", "host1", 1,
                ConnectionState.CONNECTED);
    }
}
