package org.marketcetera.photon.internal.strategy.engine.sa.ui.workbench;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotToolbarButton;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.marketcetera.photon.internal.strategy.engine.sa.ui.workbench.NewStrategyAgentHandler;
import org.marketcetera.photon.strategy.engine.model.core.StrategyEngine;
import org.marketcetera.photon.strategy.engine.sa.ui.NewStrategyAgentWizardTest.NewStrategyAgentWizardFixture;
import org.marketcetera.photon.strategy.engine.ui.workbench.tests.StrategyEnginesViewFixture;
import org.marketcetera.photon.test.WorkbenchRunner;
import org.marketcetera.photon.test.AbstractUIRunner.UI;

/* $License$ */

/**
 * Tests {@link NewStrategyAgentHandler}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 2.0.0
 */
@RunWith(WorkbenchRunner.class)
public class NewStrategyAgentHandlerTest {

    private volatile WritableList mEngines;
    private volatile StrategyEnginesViewFixture mView;

    @Before
    @UI
    public void before() throws Exception {
        mEngines = WritableList.withElementType(StrategyEngine.class);
        mView = StrategyEnginesViewFixture.openView();
        mView.setModel(mEngines);
    }

    @After
    public void after() throws Exception {
        mView.close();
    }

    @Test
    public void test() throws Exception {
        SWTBotToolbarButton button = mView.getView().toolbarButton(
                "Add a new strategy agent engine");
        button.click();
        NewStrategyAgentWizardFixture fixture = new NewStrategyAgentWizardFixture(
                null);
        fixture.setName("My Engine");
        fixture.setJmsUrl("tcp://localhost:123");
        fixture.setHostname("localhost");
        fixture.setPort("456");
        fixture.finish();
        fixture.waitForClose();
        SWTBotTree tree = mView.getView().bot().tree();
        assertThat(tree.getAllItems().length, is(1));
        assertThat(tree.selection().get(0).get(0), is("My Engine"));
        // open the wizard again and cancel
        button.click();
        fixture = new NewStrategyAgentWizardFixture(null);
        fixture.cancel();
        fixture.waitForClose();
        assertThat(tree.getAllItems().length, is(1));
        assertThat(tree.selection().get(0).get(0), is("My Engine"));
    }

}
