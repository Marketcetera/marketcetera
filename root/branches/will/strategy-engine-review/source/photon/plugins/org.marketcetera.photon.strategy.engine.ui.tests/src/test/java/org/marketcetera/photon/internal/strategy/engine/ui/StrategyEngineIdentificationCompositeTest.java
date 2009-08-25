package org.marketcetera.photon.internal.strategy.engine.ui;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotText;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.marketcetera.photon.commons.ui.databinding.DataBindingTestUtils;
import org.marketcetera.photon.commons.ui.databinding.SWTBotControlDecoration;
import org.marketcetera.photon.strategy.engine.model.core.StrategyEngine;
import org.marketcetera.photon.strategy.engine.model.core.StrategyEngineCoreFactory;
import org.marketcetera.photon.strategy.engine.model.core.StrategyEngineCorePackage;
import org.marketcetera.photon.test.AbstractUIRunner;
import org.marketcetera.photon.test.PhotonTestBase;
import org.marketcetera.photon.test.SimpleUIRunner;
import org.marketcetera.photon.test.AbstractUIRunner.ThrowableRunnable;
import org.marketcetera.photon.test.AbstractUIRunner.UI;

/* $License$ */

/**
 * Tests {@link StrategyEngineIdentificationComposite}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@RunWith(SimpleUIRunner.class)
public class StrategyEngineIdentificationCompositeTest extends PhotonTestBase {

    private final SWTBot mBot = new SWTBot();
    private final StrategyEngine mStrategyEngine = StrategyEngineCoreFactory.eINSTANCE
            .createStrategyEngine();
    private ApplicationWindow mWindow;

    private void createAndOpenWindow() throws Exception {
        AbstractUIRunner.syncRun(new ThrowableRunnable() {
            @Override
            public void run() throws Throwable {
                mWindow = new ApplicationWindow(null) {
                    @Override
                    protected Control createContents(Composite parent) {
                        final DataBindingContext dbc = new DataBindingContext();
                        final Composite control = new StrategyEngineIdentificationComposite(
                                parent, dbc, mStrategyEngine);
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
        mWindow.close();
    }

    @Test
    public void testFields() throws Exception {
        createAndOpenWindow();
        DataBindingTestUtils.testRequiredText(mBot, mStrategyEngine,
                StrategyEngineCorePackage.Literals.STRATEGY_ENGINE__NAME,
                "Name", "The engine name", "abc");
        DataBindingTestUtils
                .testOptionalText(
                        mBot,
                        mStrategyEngine,
                        StrategyEngineCorePackage.Literals.STRATEGY_ENGINE__DESCRIPTION,
                        "Description", "The engine description", "abc");
    }

    @Test
    public void testSeededFields() throws Exception {
        mStrategyEngine.setName("ABC");
        mStrategyEngine.setDescription("123");
        createAndOpenWindow();
        final SWTBotText name = mBot.textWithLabel("Name:");
        new SWTBotControlDecoration(name).assertHidden();
        assertThat(name.getText(), is("ABC"));
        assertThat(mBot.textWithLabel("Description:").getText(), is("123"));
    }

    @Test
    public void testReadOnly() throws Exception {
        mStrategyEngine.setReadOnly(true);
        mStrategyEngine.setName("Engine");
        mStrategyEngine.setDescription("An engine");
        createAndOpenWindow();
        testReadOnlyText("Name", "Engine");
        testReadOnlyText("Description", "An engine");
    }

    private void testReadOnlyText(String label, String value) {
        SWTBotText text = mBot.textWithLabel(label + ":");
        assertThat(text.getText(), is(value));
        text.typeText("abc");
        assertThat(text.getText(), is(value));
        assertThat(text.isEnabled(), is(true));
    }
}
