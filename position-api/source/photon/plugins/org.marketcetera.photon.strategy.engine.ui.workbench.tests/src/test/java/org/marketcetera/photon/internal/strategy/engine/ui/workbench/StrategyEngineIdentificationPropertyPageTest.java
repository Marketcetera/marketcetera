package org.marketcetera.photon.internal.strategy.engine.ui.workbench;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.marketcetera.photon.strategy.engine.model.core.test.StrategyEngineCoreTestUtil.createEngine;

import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.dialogs.PropertyDialog;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.marketcetera.photon.strategy.engine.model.core.StrategyEngine;
import org.marketcetera.photon.strategy.engine.ui.workbench.StrategyEngineWorkbenchUI;
import org.marketcetera.photon.test.AbstractUIRunner;
import org.marketcetera.photon.test.SWTTestUtil;
import org.marketcetera.photon.test.WorkbenchRunner;
import org.marketcetera.photon.test.AbstractUIRunner.ThrowableRunnable;
import org.marketcetera.photon.test.AbstractUIRunner.UI;

/* $License$ */

/**
 * Tests {@link StrategyEngineIdentificationPropertyPage}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
// see http://bugs.eclipse.org/231619
@SuppressWarnings("restriction")
@RunWith(WorkbenchRunner.class)
public class StrategyEngineIdentificationPropertyPageTest {

    private final SWTBot mBot = new SWTBot();
    private StrategyEngine mStrategyEngine;
    private PropertyDialog mDialog;

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
                if (mStrategyEngine == null) {
                    mStrategyEngine = createEngine("StratEngine", "My strategy engine");
                }
                mDialog = PropertyDialog
                        .createDialogOn(
                                PlatformUI.getWorkbench()
                                        .getActiveWorkbenchWindow().getShell(),
                                StrategyEngineWorkbenchUI.STRATEGY_ENGINE_IDENTIFICATION_PROPERTY_PAGE_ID,
                                mStrategyEngine);
                mDialog.setBlockOnOpen(false);
                mDialog.open();
            }
        });
        mBot.shell("Properties for StratEngine");
        SWTTestUtil.assertButtonDoesNotExist("Apply");
        SWTTestUtil.assertButtonDoesNotExist("Restore Defaults");
    }

    @Test
    public void testChangeProperties() throws Exception {
        openDialog();
        mBot.textWithLabel("Name:").setText("NewName");
        mBot.textWithLabel("Description:").setText("NewDesc");
        mBot.button("OK").click();
        AbstractUIRunner.syncRun(new ThrowableRunnable() {
            @Override
            public void run() throws Throwable {
                assertThat(mStrategyEngine.getName(), is("NewName"));
                assertThat(mStrategyEngine.getDescription(), is("NewDesc"));
            }
        });
    }

    @Test
    public void testCancelChange() throws Exception {
        openDialog();
        mBot.textWithLabel("Name:").setText("NewName");
        mBot.textWithLabel("Description:").setText("NewDesc");
        mBot.button("Cancel").click();
        AbstractUIRunner.syncRun(new ThrowableRunnable() {
            @Override
            public void run() throws Throwable {
                assertThat(mStrategyEngine.getName(), is("StratEngine"));
                assertThat(mStrategyEngine.getDescription(), is("My strategy engine"));
            }
        });
    }

    @Test
    public void testReadOnly() throws Exception {
        AbstractUIRunner.syncRun(new ThrowableRunnable() {
            @Override
            public void run() throws Throwable {
                mStrategyEngine = createEngine("StratEngine", "2");
                mStrategyEngine.setReadOnly(true);
            }
        });
        openDialog();
        SWTTestUtil.testReadOnlyText(mBot.textWithLabel("Name:"), "StratEngine");
        SWTTestUtil.testReadOnlyText(mBot.textWithLabel("Description:"), "2");
        mBot.button("OK").click();
        AbstractUIRunner.syncRun(new ThrowableRunnable() {
            @Override
            public void run() throws Throwable {
                assertThat(mStrategyEngine.getName(), is("StratEngine"));
                assertThat(mStrategyEngine.getDescription(), is("2"));
            }
        });
    }
}
