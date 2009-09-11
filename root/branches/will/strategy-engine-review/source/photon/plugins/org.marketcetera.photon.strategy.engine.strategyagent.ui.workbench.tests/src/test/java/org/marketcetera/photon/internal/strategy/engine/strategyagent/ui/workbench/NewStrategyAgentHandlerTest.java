package org.marketcetera.photon.internal.strategy.engine.strategyagent.ui.workbench;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.swtbot.swt.finder.utils.TableCollection;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.marketcetera.photon.strategy.engine.model.core.StrategyEngine;
import org.marketcetera.photon.strategy.engine.strategyagent.ui.NewStrategyAgentWizardTest.NewStrategyAgentWizardFixture;
import org.marketcetera.photon.strategy.engine.ui.workbench.tests.StrategyEnginesViewFixture;
import org.marketcetera.photon.test.WorkbenchRunner;
import org.marketcetera.photon.test.AbstractUIRunner.UI;

/* $License$ */

/**
 * Tests {@link NewStrategyAgentHandler}.
 *
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@RunWith(WorkbenchRunner.class)
public class NewStrategyAgentHandlerTest {

    private volatile WritableList mEngines;
    private volatile StrategyEnginesViewFixture mView;

    @Before
    @UI
    public void before() throws Exception {
        mEngines = WritableList.withElementType(StrategyEngine.class);
        mView = StrategyEnginesViewFixture
                .openView();
        mView.setModel(mEngines);
    }

    @After
    public void after() throws Exception {
        mView.close();
    }

    @Test
    public void test() throws Exception {
        mView.getView().toolbarButton("Add a new strategy agent engine").click();
        NewStrategyAgentWizardFixture fixture = new NewStrategyAgentWizardFixture(null);
        fixture.setName("My Engine");
        fixture.setJmsUrl("tcp://localhost:123");
        fixture.setHostname("localhost");
        fixture.setPort("456");
        fixture.finish();
        fixture.waitForClose();
        TableCollection selection = mView.getView().bot().tree().selection();
        assertThat(selection.rowCount(), is(1));
        assertThat(selection.get(0).get(0), is("My Engine"));
    }

}
