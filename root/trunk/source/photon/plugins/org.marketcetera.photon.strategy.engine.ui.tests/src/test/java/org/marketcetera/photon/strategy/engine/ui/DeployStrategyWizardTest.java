package org.marketcetera.photon.strategy.engine.ui;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.marketcetera.photon.strategy.engine.model.core.test.StrategyEngineCoreTestUtil.buildEngines;
import static org.marketcetera.photon.strategy.engine.model.core.test.StrategyEngineCoreTestUtil.createEngine;
import static org.marketcetera.photon.test.SWTBotConditions.isClosed;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotButton;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotCheckBox;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotCombo;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTable;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTableItem;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotText;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.marketcetera.photon.commons.ValidateTest.ExpectedEmptyFailure;
import org.marketcetera.photon.commons.ValidateTest.ExpectedNullArgumentFailure;
import org.marketcetera.photon.commons.ValidateTest.ExpectedNullElementFailure;
import org.marketcetera.photon.strategy.engine.model.core.DeployedStrategy;
import org.marketcetera.photon.strategy.engine.model.core.Strategy;
import org.marketcetera.photon.strategy.engine.model.core.StrategyEngine;
import org.marketcetera.photon.strategy.engine.model.core.StrategyEngineCoreFactory;
import org.marketcetera.photon.test.AbstractUIRunner;
import org.marketcetera.photon.test.PhotonTestBase;
import org.marketcetera.photon.test.SimpleUIRunner;
import org.marketcetera.photon.test.AbstractUIRunner.ThrowableRunnable;
import org.marketcetera.photon.test.AbstractUIRunner.UI;

/* $License$ */

/**
 * Tests {@link DeployStrategyWizard}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id: DeployStrategyWizardTest.java 10713 2009-08-30 09:08:28Z
 *          tlerios $
 * @since 2.0.0
 */
@RunWith(SimpleUIRunner.class)
public class DeployStrategyWizardTest extends PhotonTestBase {

    private final SWTBot mBot = new SWTBot();
    private final Strategy mStrategy = StrategyEngineCoreFactory.eINSTANCE
            .createStrategy();
    private volatile List<StrategyEngine> mAvailableEngines;
    private volatile WizardDialog mWizardDialog;
    private volatile Shell mShell;
    private volatile StrategyEngine mEngine1;
    private volatile DeployStrategyWizard mWizard;

    @Before
    @UI
    public void before() {
        mEngine1 = createEngine("Engine 1");
        mAvailableEngines = buildEngines(mEngine1, createEngine("Engine 2"));
        mShell = new Shell();
        mWizard = new DeployStrategyWizard(mStrategy, null, mAvailableEngines);

    }

    private void openWizard() throws Exception {
        AbstractUIRunner.syncRun(new ThrowableRunnable() {
            @Override
            public void run() throws Throwable {
                mWizardDialog = new WizardDialog(mShell, mWizard);
                mWizardDialog.setBlockOnOpen(false);
                mWizardDialog.open();
            }
        });
    }

    @After
    @UI
    public void after() {
        if (mWizardDialog != null) {
            mWizardDialog.close();
        }
        mShell.dispose();
    }

    @Test
    public void testSuccessfulDeployment() throws Exception {
        openWizard();
        DeployStrategyWizardFixture fixture = new DeployStrategyWizardFixture();
        mBot.button("Browse..."); // just to verify it's there
        assertThat(fixture.getFinishButton().isEnabled(), is(false));
        fixture.setScript("C:\\Strat.java");
        assertThat(fixture.getFinishButton().isEnabled(), is(false));
        fixture.setInstanceName("Strat");
        assertThat(fixture.getFinishButton().isEnabled(), is(false));
        fixture.getEngines()[0].check();
        fixture.finish();
        fixture.waitForClose();
        assertThat(getResult(), is(getChild(mEngine1, 0)));
    }

    @Test
    public void testSeededValues() throws Exception {
        AbstractUIRunner.syncRun(new ThrowableRunnable() {
            @Override
            public void run() throws Throwable {
                mWizard.dispose();
                mStrategy.setScriptPath("C:\\MyStrat.java");
                mStrategy.setInstanceName("MyStrat1");
                mWizard = new DeployStrategyWizard(mStrategy, mEngine1,
                        mAvailableEngines);
            }
        });
        openWizard();
        DeployStrategyWizardFixture fixture = new DeployStrategyWizardFixture();
        assertThat(fixture.getScriptText().getText(),
                is("C:\\MyStrat.java"));
        SWTBotTableItem[] engines = fixture.getEngines();
        assertThat(engines[0].isChecked(), is(true));
        assertThat(engines[1].isChecked(), is(false));
        fixture.finish();
        fixture.waitForClose();
        assertThat(getResult(), is(getChild(mEngine1, 0)));
    }

    @Test
    @UI
    public void testValidation() throws Exception {
        new ExpectedNullArgumentFailure("availableEngines") {
            @Override
            protected void run() throws Exception {
                new DeployStrategyWizard(null, null, null);
            }
        };
        new ExpectedNullElementFailure("availableEngines") {
            @Override
            protected void run() throws Exception {
                new DeployStrategyWizard(null, null, Arrays
                        .asList((StrategyEngine) null));
            }
        };
        new ExpectedEmptyFailure("availableEngines") {
            @Override
            protected void run() throws Exception {
                final List<StrategyEngine> emptyList = Collections.emptyList();
                new DeployStrategyWizard(null, null, emptyList);
            }
        };
        new ExpectedNullArgumentFailure("buttons") {
            @Override
            protected void run() throws Exception {
                new DeployStrategyWizard(null, null, mAvailableEngines,
                        (ScriptSelectionButton[]) null);
            }
        };
        new ExpectedNullElementFailure("buttons") {
            @Override
            protected void run() throws Exception {
                new DeployStrategyWizard(null, null, mAvailableEngines,
                        (ScriptSelectionButton) null);
            }
        };
        new ExpectedEmptyFailure("buttons") {
            @Override
            protected void run() throws Exception {
                new DeployStrategyWizard(null, null, mAvailableEngines,
                        new ScriptSelectionButton[] {});
            }
        };
    }

    private DeployedStrategy getChild(final StrategyEngine engine,
            final int index) throws Exception {
        return AbstractUIRunner.syncCall(new Callable<DeployedStrategy>() {
            @Override
            public DeployedStrategy call() throws Exception {
                return engine.getDeployedStrategies().get(index);
            }
        });
    }

    private DeployedStrategy getResult() throws Exception {
        return AbstractUIRunner.syncCall(new Callable<DeployedStrategy>() {
            @Override
            public DeployedStrategy call() throws Exception {
                return mWizard.getResult();
            }
        });
    }

    /**
     * Helper for testing {@link DeployStrategyWizard}.
     */
    public static class DeployStrategyWizardFixture {
        private final SWTBot mBot = new SWTBot();
        private final SWTBotShell mShell;
        private final SWTBotText mScript;
        private final SWTBotCombo mLanguageCombo;
        private final SWTBotText mClassName;
        private final SWTBotText mInstanceName;
        private final SWTBotCheckBox mRouteToServer;
        private final SWTBotButton mCancelButton;
        private final SWTBotButton mFinishButton;

        public DeployStrategyWizardFixture() {
            mShell = mBot.shell("Deploy Strategy");
            mScript = mBot.textWithLabel("Script:");
            mLanguageCombo = mBot.comboBoxWithLabel("Language:");
            mClassName = mBot.textWithLabel("Class:");
            mInstanceName = mBot.textWithLabel("Instance Name:");
            mRouteToServer = mBot.checkBox("Route orders to server");
            mCancelButton = mBot.button("Cancel");
            mFinishButton = mBot.button("Finish");
        }

        public SWTBotText getScriptText() {
            return mScript;
        }

        public SWTBotCombo getLanguageCombo() {
            return mLanguageCombo;
        }

        public SWTBotText getClassNameText() {
            return mClassName;
        }

        public SWTBotText getInstanceNameText() {
            return mInstanceName;
        }

        public SWTBotCheckBox getRouteToServerCheckBox() {
            return mRouteToServer;
        }

        public SWTBotButton getCancelButton() {
            return mCancelButton;
        }

        public SWTBotButton getFinishButton() {
            return mFinishButton;
        }

        public SWTBotTableItem[] getEngines() {
            SWTBotTable table = mBot.table();
            int rowCount = table.rowCount();
            SWTBotTableItem[] result = new SWTBotTableItem[rowCount];
            for (int i = 0; i < rowCount; i++) {
                result[i] = table.getTableItem(i);
            }
            return result;
        }
        
        public void setScript(String script) {
            mScript.setText(script);
        }
        
        public void setLanguage(String language) {
            mLanguageCombo.setText(language);
        }
        
        public void setClassName(String className) {
            mClassName.setText(className);
        }
        
        public void setInstanceName(String instanceName) {
            mInstanceName.setText(instanceName);
        }
        
        public void setRouteToServer(boolean route) {
            if (route) {
                mRouteToServer.select();
            } else {
                mRouteToServer.deselect();
            }                
        }
        
        public void finish() {
            mFinishButton.click();
        }
        
        public void cancel() {
            mCancelButton.click();
        }
        
        public void waitForClose() {
            mBot.waitUntil(isClosed(mShell));
        }
    }

}
