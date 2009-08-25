package org.marketcetera.photon.internal.strategy.engine.ui;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.marketcetera.photon.strategy.engine.model.core.StrategyEngineCorePackage.Literals.STRATEGY__ROUTE_ORDERS_TO_SERVER;
import static org.marketcetera.photon.strategy.engine.model.core.test.StrategyEngineCoreTestUtil.createDeployedStrategy;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotCheckBox;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotText;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.marketcetera.photon.commons.ui.databinding.DataBindingTestUtils;
import org.marketcetera.photon.strategy.engine.model.core.DeployedStrategy;
import org.marketcetera.photon.strategy.engine.model.core.StrategyEngineCoreFactory;
import org.marketcetera.photon.strategy.engine.model.core.StrategyState;
import org.marketcetera.photon.test.AbstractUIRunner;
import org.marketcetera.photon.test.PhotonTestBase;
import org.marketcetera.photon.test.SimpleUIRunner;
import org.marketcetera.photon.test.AbstractUIRunner.ThrowableRunnable;
import org.marketcetera.photon.test.AbstractUIRunner.UI;

/* $License$ */

/**
 * Tests {@link DeployedStrategyConfigurationComposite}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@RunWith(SimpleUIRunner.class)
public class DeployedStrategyConfigurationCompositeTest extends PhotonTestBase {

    private final SWTBot mBot = new SWTBot();
    private volatile DeployedStrategy mStrategy;
    private ApplicationWindow mWindow;

    private void createAndOpenWindow() throws Exception {
        AbstractUIRunner.syncRun(new ThrowableRunnable() {
            @Override
            public void run() {
                mWindow = new ApplicationWindow(null) {
                    @Override
                    protected Control createContents(Composite parent) {
                        if (mStrategy == null) {
                            mStrategy = StrategyEngineCoreFactory.eINSTANCE
                                    .createDeployedStrategy();
                        }
                        final DataBindingContext dbc = new DataBindingContext();
                        final Composite control = new DeployedStrategyConfigurationComposite(
                                parent, dbc, mStrategy);
                        return control;
                    }
                };
                mWindow.open();
            }
        });
    }

    @After
    @UI
    public void after() {
        if (mWindow != null) {
            mWindow.close();
        }
    }

    @Test
    public void testReadOnlyFields() throws Exception {
        AbstractUIRunner.syncRun(new ThrowableRunnable() {
            @Override
            public void run() {
                mStrategy = createDeployedStrategy("instance1");
                mStrategy.setClassName("MyClass");
                mStrategy.setLanguage("Python");
                mStrategy.setScriptPath("c:\\MyClass.java");
            }
        });
        createAndOpenWindow();
        testReadOnlyText("Instance Name",
                "The unique instance name for the deployed strategy",
                "instance1");
        testReadOnlyText("Class", "The class name of the deployed strategy",
                "MyClass");
        testReadOnlyText("Language", "The language of the deployed strategy",
                "Python");
        testReadOnlyText("Script",
                "The path to the script which contains the strategy, if known",
                "c:\\MyClass.java");
    }

    private void testReadOnlyText(String label, String tooltip, String value) {
        assertThat(mBot.label(label + ":").getToolTipText(), is(tooltip));
        SWTBotText text = mBot.textWithLabel(label + ":");
        assertThat(text.getText(), is(value));
        text.typeText("abc");
        assertThat(text.getText(), is(value));
        assertThat(text.isEnabled(), is(true));
    }

    @Test
    public void testNulls() throws Exception {
        createAndOpenWindow();
        testReadOnlyText("Instance Name",
                "The unique instance name for the deployed strategy", "");
        testReadOnlyText("Class", "The class name of the deployed strategy", "");
        testReadOnlyText("Language", "The language of the deployed strategy",
                "");
        testReadOnlyText("Script",
                "The path to the script which contains the strategy, if known",
                "");
    }

    @Test
    public void testRouteOrdersToServer() throws Exception {
        createAndOpenWindow();
        final SWTBotCheckBox check = mBot.checkBox("Route orders to server");
        assertThat(
                check.getToolTipText(),
                is("If checked, the strategy will send orders to the order routing server"));
        assertThat(check.isChecked(), is(false));
        check.click();
        assertThat(DataBindingTestUtils.eGet(mStrategy,
                STRATEGY__ROUTE_ORDERS_TO_SERVER), is((Object) true));
        check.click();
        assertThat(DataBindingTestUtils.eGet(mStrategy,
                STRATEGY__ROUTE_ORDERS_TO_SERVER), is((Object) false));
    }

    @Test
    public void testRunningStrategy() throws Exception {
        AbstractUIRunner.syncRun(new ThrowableRunnable() {
            @Override
            public void run() {
                mStrategy = createDeployedStrategy("instance1");
                mStrategy.setState(StrategyState.RUNNING);
            }
        });
        createAndOpenWindow();
        final SWTBotCheckBox check = mBot.checkBox("Route orders to server");
        assertThat(check.isEnabled(), is(false));
    }
}
