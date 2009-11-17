package org.marketcetera.photon.internal.strategy.engine.ui.workbench.handlers;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.marketcetera.photon.strategy.engine.model.core.test.StrategyEngineCoreTestUtil.buildEngines;
import static org.marketcetera.photon.strategy.engine.model.core.test.StrategyEngineCoreTestUtil.createConnectedEngine;
import static org.marketcetera.photon.strategy.engine.model.core.test.StrategyEngineCoreTestUtil.createEngine;

import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.swtbot.swt.finder.utils.TableCollection;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTableItem;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.marketcetera.photon.strategy.engine.model.core.StrategyEngine;
import org.marketcetera.photon.strategy.engine.ui.DeployStrategyWizardTest.DeployStrategyWizardFixture;
import org.marketcetera.photon.strategy.engine.ui.tests.MockUIConnection;
import org.marketcetera.photon.strategy.engine.ui.workbench.tests.StrategyEnginesViewFixture;
import org.marketcetera.photon.test.ContextMenuHelper;
import org.marketcetera.photon.test.PhotonTestBase;
import org.marketcetera.photon.test.WorkbenchRunner;
import org.marketcetera.photon.test.AbstractUIRunner.UI;

/* $License$ */

/**
 * Tests {@link DeployHandler}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 2.0.0
 */
@RunWith(WorkbenchRunner.class)
public class DeployHandlerTest extends PhotonTestBase {

    private volatile WritableList mEngines;
    private volatile StrategyEnginesViewFixture mView;

    @Before
    @UI
    public void before() throws Exception {
        StrategyEngine engine = createConnectedEngine("My Engine");
        engine.setConnection(new MockUIConnection());
        mEngines = new WritableList(
                buildEngines(engine,
                        createEngine("My Engine 2")), StrategyEngine.class);
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
        SWTBotTree tree = mView.getView().bot().tree();
        tree.select("My Engine");
        ContextMenuHelper.clickContextMenu(tree, "Deploy...");
        DeployStrategyWizardFixture wizard = new DeployStrategyWizardFixture();
        SWTBotTableItem[] engines = wizard.getEngines();
        assertThat(engines.length, is(1));
        assertThat(engines[0].getText(), is("My Engine"));
        assertThat(engines[0].isChecked(), is(true));
        wizard.setScript("C:\\abc.java");
        wizard.setInstanceName("abc");
        wizard.finish();
        wizard.waitForClose();
        TableCollection selection = tree.selection();
        assertThat(selection.rowCount(), is(1));
        assertThat(selection.get(0).get(0), is("abc"));
    }
}
