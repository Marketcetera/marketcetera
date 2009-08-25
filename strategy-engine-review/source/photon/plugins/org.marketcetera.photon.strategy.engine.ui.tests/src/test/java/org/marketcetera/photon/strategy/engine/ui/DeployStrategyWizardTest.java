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
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
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
 * @version $Id$
 * @since $Release$
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
        SWTBotShell dialog = mBot.shell("Deploy Strategy");
        mBot.button("Browse..."); // just verify it's there
        SWTBotButton finishButton = mBot.button("Finish");
        assertThat(finishButton.isEnabled(), is(false));
        mBot.textWithLabel("Script:").setText("C:\\Strat.java");
        assertThat(finishButton.isEnabled(), is(false));
        mBot.table().getTableItem(0).check();
        finishButton.click();
        mBot.waitUntil(isClosed(dialog));
        assertThat(getResult(), is(getChild(mEngine1, 0)));
    }

    @Test
    public void testSeededValues() throws Exception {
        AbstractUIRunner.syncRun(new ThrowableRunnable() {
            @Override
            public void run() throws Throwable {
                mWizard.dispose();
                mStrategy.setScriptPath("C:\\MyStrat.java");
                mWizard = new DeployStrategyWizard(mStrategy, mEngine1,
                        mAvailableEngines);
            }
        });
        openWizard();
        SWTBotShell dialog = mBot.shell("Deploy Strategy");
        final SWTBotButton finishButton = mBot.button("Finish");
        assertThat(mBot.textWithLabel("Script:").getText(),
                is("C:\\MyStrat.java"));
        assertThat(mBot.table().getTableItem(0).isChecked(), is(true));
        assertThat(mBot.table().getTableItem(1).isChecked(), is(false));
        finishButton.click();
        mBot.waitUntil(isClosed(dialog));
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

}
