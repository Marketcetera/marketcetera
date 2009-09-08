package org.marketcetera.photon.internal.strategy.engine.ui;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.marketcetera.photon.strategy.engine.model.core.StrategyEngineCorePackage.Literals.STRATEGY__CLASS_NAME;
import static org.marketcetera.photon.strategy.engine.model.core.StrategyEngineCorePackage.Literals.STRATEGY__INSTANCE_NAME;
import static org.marketcetera.photon.strategy.engine.model.core.StrategyEngineCorePackage.Literals.STRATEGY__LANGUAGE;
import static org.marketcetera.photon.strategy.engine.model.core.StrategyEngineCorePackage.Literals.STRATEGY__ROUTE_ORDERS_TO_SERVER;
import static org.marketcetera.photon.strategy.engine.model.core.StrategyEngineCorePackage.Literals.STRATEGY__SCRIPT_PATH;
import static org.marketcetera.photon.strategy.engine.model.core.test.StrategyEngineCoreTestUtil.buildEngines;
import static org.marketcetera.photon.strategy.engine.model.core.test.StrategyEngineCoreTestUtil.createEngine;

import java.util.concurrent.Callable;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.widgets.AbstractSWTBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotButton;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotCheckBox;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotCombo;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTable;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTableItem;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.marketcetera.photon.commons.ui.databinding.DataBindingTestUtils;
import org.marketcetera.photon.commons.ui.databinding.RequiredFieldSupportTest;
import org.marketcetera.photon.commons.ui.databinding.SWTBotControlDecoration;
import org.marketcetera.photon.strategy.engine.model.core.Strategy;
import org.marketcetera.photon.strategy.engine.model.core.StrategyEngine;
import org.marketcetera.photon.strategy.engine.model.core.StrategyEngineCoreFactory;
import org.marketcetera.photon.strategy.engine.ui.ScriptSelectionButton;
import org.marketcetera.photon.test.AbstractUIRunner;
import org.marketcetera.photon.test.PhotonTestBase;
import org.marketcetera.photon.test.SimpleUIRunner;
import org.marketcetera.photon.test.AbstractUIRunner.ThrowableRunnable;
import org.marketcetera.photon.test.AbstractUIRunner.UI;

/* $License$ */

/**
 * Tests {@link DeployStrategyComposite}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@RunWith(SimpleUIRunner.class)
public class DeployStrategyCompositeTest extends PhotonTestBase {

    private final SWTBot mBot = new SWTBot();
    private final Strategy mStrategy = StrategyEngineCoreFactory.eINSTANCE
            .createStrategy();
    private WritableValue mEngine;
    private StrategyEngine[] mAvailableEngines;
    private ApplicationWindow mWindow;
    private MockScriptSelectionButton mMockSelectionButton;

    @Before
    @UI
    public void before() {
        mEngine = WritableValue.withValueType(StrategyEngine.class);
        mAvailableEngines = buildEngines(createEngine("Engine 1"),
                createEngine("Engine 2")).toArray(new StrategyEngine[0]);
        mMockSelectionButton = new MockScriptSelectionButton("Mock");
        createAndOpenWindow();
    }

    private void createAndOpenWindow() {
        mWindow = new ApplicationWindow(null) {
            @Override
            protected Control createContents(Composite parent) {
                final DataBindingContext dbc = new DataBindingContext();
                final Composite control = new DeployStrategyComposite(parent,
                        dbc, mStrategy, mAvailableEngines, mEngine,
                        mMockSelectionButton);
                return control;
            }
        };
        mWindow.open();
    }

    @After
    @UI
    public void after() {
        mWindow.close();
        mEngine.dispose();
    }

    @Test
    public void testLanguageCombo() throws Exception {
        final SWTBotCombo combo = mBot.comboBoxWithLabel("Language:");
        testRequiredControl(combo, STRATEGY__LANGUAGE, "Language",
                "The script language");
        SWTBotControlDecoration decoration = new SWTBotControlDecoration(combo);
        assertThat(combo.items(), is(new String[] { "JAVA", "RUBY" }));
        combo.setSelection("JAVA");
        assertThat(DataBindingTestUtils.eGet(mStrategy, STRATEGY__LANGUAGE),
                is((Object) "JAVA"));
        assertThat(decoration.isVisible(), is(false));
        combo.setSelection("RUBY");
        assertThat(DataBindingTestUtils.eGet(mStrategy, STRATEGY__LANGUAGE),
                is((Object) "RUBY"));
        assertThat(decoration.isVisible(), is(false));
    }

    @Test
    public void testScriptText() throws Exception {
        testRequiredText("Script", STRATEGY__SCRIPT_PATH,
                "The path to the script which contains the strategy");
        final SWTBotButton button = mBot.button("Mock");
        // mock button will return "c:\\strategy.xml"
        button.click();
        assertThat(DataBindingTestUtils.eGet(mStrategy, STRATEGY__SCRIPT_PATH),
                is((Object) "c:\\strategy.xml"));
        button.click();
        // mock button will return "c:\\strategy2.xml " (note space)
        assertThat(DataBindingTestUtils.eGet(mStrategy, STRATEGY__SCRIPT_PATH),
                is((Object) "c:\\strategy2.xml"));
        // mock button will return null
        button.click();
        assertThat(DataBindingTestUtils.eGet(mStrategy, STRATEGY__SCRIPT_PATH),
                is((Object) "c:\\strategy2.xml"));
        mBot.textWithLabel("Script:").setText("    ");
        // mock button will return null
        button.click();
        assertThat(DataBindingTestUtils.eGet(mStrategy, STRATEGY__SCRIPT_PATH),
                is((Object) "    "));
    }

    @Test
    public void testClass() throws Exception {
        testRequiredText("Class", STRATEGY__CLASS_NAME,
                "The strategy class in the script");
    }

    @Test
    public void testInstanceName() throws Exception {
        testOptionalText(
                "Instance Name",
                STRATEGY__INSTANCE_NAME,
                "The unique instance name for the deployed strategy, will be generated by the engine if left blank");
    }

    @Test
    public void testRouteOrdersToServer() throws Exception {
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
    public void testEngineSelection() throws Exception {
        final SWTBotTable table = mBot.table();
        SWTBotControlDecoration decoration = new SWTBotControlDecoration(table);
        String message = RequiredFieldSupportTest
                .getRequiredValueMessage("Engine");
        decoration.assertRequired(message);
        assertThat(table.rowCount(), is(2));
        final SWTBotTableItem item1 = table.getTableItem(0);
        assertThat(item1.getText(), is("Engine 1"));
        final SWTBotTableItem item2 = table.getTableItem(1);
        assertThat(item2.getText(), is("Engine 2"));
        item1.check();
        assertThat(getSelectedEngine(), is(mAvailableEngines[0]));
        decoration.assertHidden();
        item2.check();
        assertThat(item1.isChecked(), is(false));
        assertThat(getSelectedEngine(), is(mAvailableEngines[1]));
        decoration.assertHidden();
        item2.uncheck();
        assertThat(getSelectedEngine(), nullValue());
        decoration.assertRequired(message);
    }

    @Test
    public void testScriptHeuristics() throws Exception {
        assertInferred("", "");
        AbstractUIRunner.syncRun(new ThrowableRunnable() {
            @Override
            public void run() throws Throwable {
                mWindow.close();
                mStrategy.setScriptPath("c:\\MyStrategy.java");
                createAndOpenWindow();
            }
        });
        assertInferred("JAVA", "MyStrategy");
        AbstractUIRunner.syncRun(new ThrowableRunnable() {
            @Override
            public void run() throws Throwable {
                mStrategy.setScriptPath("c:\\ruby_strategy.rb");
            }
        });
        assertInferred("RUBY", "RubyStrategy");
        AbstractUIRunner.syncRun(new ThrowableRunnable() {
            @Override
            public void run() throws Throwable {
                mStrategy.setScriptPath("c:\\unknown.xml");
            }
        });
        assertInferred("RUBY", "RubyStrategy");
        AbstractUIRunner.syncRun(new ThrowableRunnable() {
            @Override
            public void run() throws Throwable {
                mStrategy.setScriptPath("c:\\3unknown3.rb");
            }
        });
        assertInferred("RUBY", "Unknown3");
        AbstractUIRunner.syncRun(new ThrowableRunnable() {
            @Override
            public void run() throws Throwable {
                mStrategy.setScriptPath("c:\\unknown_5.rb");
            }
        });
        assertInferred("RUBY", "Unknown5");
        AbstractUIRunner.syncRun(new ThrowableRunnable() {
            @Override
            public void run() throws Throwable {
                mStrategy.setScriptPath("c:\\Unknown_5.java");
            }
        });
        assertInferred("JAVA", "Unknown_5");
    }

    @Test
    public void testSeededValues() throws Exception {
        AbstractUIRunner.syncRun(new ThrowableRunnable() {
            @Override
            public void run() throws Throwable {
                mWindow.close();
                mEngine.setValue(mAvailableEngines[0]);
                mStrategy.setScriptPath("C:\\MyStrategy.java");
                createAndOpenWindow();
            }
        });
        assertInferred("JAVA", "MyStrategy");
        assertThat(mBot.table().getTableItem(0).isChecked(), is(true));
        assertThat(mBot.table().isEnabled(), is(false));
    }

    private void assertInferred(String language, String className) {
        assertThat(mBot.comboBox().getText(), is(language));
        assertThat(mBot.textWithLabel("Class:").getText(), is(className));
    }

    private StrategyEngine getSelectedEngine() throws Exception {
        return (StrategyEngine) AbstractUIRunner
                .syncCall(new Callable<Object>() {
                    @Override
                    public Object call() throws Exception {
                        return mEngine.getValue();
                    }
                });
    }

    private void testRequiredText(String description,
            EStructuralFeature feature, String tooltip) throws Exception {
        DataBindingTestUtils.testRequiredText(mBot, mStrategy, feature,
                description, tooltip, "abc");
    }

    private void testOptionalText(String description,
            EStructuralFeature feature, String tooltip) throws Exception {
        DataBindingTestUtils.testOptionalText(mBot, mStrategy, feature,
                description, tooltip, "abc");
    }

    private void testRequiredControl(AbstractSWTBot<? extends Control> control,
            EStructuralFeature feature, String description, String tooltip)
            throws Exception {
        DataBindingTestUtils.testRequiredControl(mBot, control, mStrategy,
                feature, description, tooltip, "abc");
    }

    private final static class MockScriptSelectionButton extends
            ScriptSelectionButton {

        public MockScriptSelectionButton(String text) {
            super(text);
        }

        @Override
        public String selectScript(Shell shell, String current) {
            if (current == null) {
                return "c:\\strategy.xml";
            } else if (current.equals("c:\\strategy.xml")) {
                // extra spaces to ensure trim
                return " c:\\strategy2.xml ";
            } else {
                return null;
            }
        }
    }
}
