package org.marketcetera.photon.internal.strategy.engine.ui.workbench.handlers;

import static org.marketcetera.photon.strategy.engine.model.core.test.StrategyEngineCoreTestUtil.buildEngines;
import static org.marketcetera.photon.strategy.engine.model.core.test.StrategyEngineCoreTestUtil.createConnectedEngine;
import static org.marketcetera.photon.strategy.engine.model.core.test.StrategyEngineCoreTestUtil.createDeployedStrategy;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;

import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.junit.Test;
import org.marketcetera.photon.commons.ui.JFaceUtilsTest.ErrorDialogFixture;
import org.marketcetera.photon.commons.ui.workbench.ProgressUtilsTest.ProgressDialogFixture;
import org.marketcetera.photon.strategy.engine.model.core.DeployedStrategy;
import org.marketcetera.photon.strategy.engine.model.core.StrategyEngine;
import org.marketcetera.photon.strategy.engine.ui.workbench.tests.StrategyEnginesViewContextMenuTestBase;

/* $License$ */

/**
 * Base class for handlers that change {@link DeployedStrategy}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
public abstract class ChangeStrategyHandlerTestBase extends
        StrategyEnginesViewContextMenuTestBase {

    private final String mTaskFormat;
    protected final StrategyEngine mEngine;
    protected final BlockingConnection mConnection;
    protected final DeployedStrategy mStrategy1;
    protected final DeployedStrategy mStrategy2;
    protected final DeployedStrategy mStrategy3;

    public ChangeStrategyHandlerTestBase(String menuItem, String verb) {
        super(menuItem);
        mTaskFormat = verb + " strategy ''{0}'' on ''{1}''...";
        mConnection = new BlockingConnection();
        mEngine = createConnectedEngine("My Engine");
        mEngine.setConnection(mConnection);
        mStrategy1 = createDeployedStrategy("deployed 1");
        mStrategy2 = createDeployedStrategy("deployed 2");
        mStrategy3 = createDeployedStrategy("deployed 3");
    }

    @Override
    protected List<? extends StrategyEngine> createModel() {
        return buildEngines(mEngine, mStrategy1, mStrategy2, mStrategy3);
    }

    abstract protected void acceptChange(BlockingConnection connection,
            Object object) throws Exception;

    protected List<DeployedStrategy> getMultipleAffected() {
        return Arrays.asList(mStrategy1, mStrategy2, mStrategy3);
    }

    protected void validateProgress(ProgressDialogFixture fixture,
            DeployedStrategy strategy) throws Exception {
        fixture.assertTask(MessageFormat.format(mTaskFormat, strategy
                .getInstanceName(), strategy.getEngine().getName()));
    }

    protected void selectSingle(SWTBotTree tree) {
        tree.getTreeItem("My Engine").select("deployed 1");
    }

    protected void selectMultiple(SWTBotTree tree) {
        tree.getTreeItem("My Engine").select("deployed 1", "deployed 2",
                "deployed 3");
    }

    @Test
    public void testSingle() throws Exception {
        new TestTemplate() {
            @Override
            protected void select(SWTBotTree tree) {
                selectSingle(tree);
            }

            @Override
            protected void validate() throws Exception {
                ProgressDialogFixture fixture = new ProgressDialogFixture();
                validateProgress(fixture, mStrategy1);
                acceptChange((BlockingConnection) mStrategy1.getEngine()
                        .getConnection(), mStrategy1);
                fixture.waitForClose();
            }
        };
    }

    @Test
    public void testMultiple() throws Exception {
        new TestTemplate() {
            @Override
            protected void select(SWTBotTree tree) {
                selectMultiple(tree);
            }

            @Override
            protected void validate() throws Exception {
                ProgressDialogFixture fixture = new ProgressDialogFixture();
                for (DeployedStrategy strategy : getMultipleAffected()) {
                    validateProgress(fixture, strategy);
                    acceptChange((BlockingConnection) strategy.getEngine()
                            .getConnection(), strategy);
                }
                fixture.waitForClose();
            }
        };
    }

    @Test
    public void testCancel() throws Exception {
        new TestTemplate() {
            @Override
            protected void select(SWTBotTree tree) {
                selectMultiple(tree);
            }

            @Override
            protected void validate() throws Exception {
                ProgressDialogFixture fixture = new ProgressDialogFixture();
                validateProgress(fixture, mStrategy1);
                fixture.cancel();
                acceptChange((BlockingConnection) mStrategy1.getEngine()
                        .getConnection(), mStrategy1);
                fixture.waitForClose();
            }
        };
    }

    @Test
    public void testException() throws Exception {
        new TestTemplate() {
            @Override
            protected void select(SWTBotTree tree) {
                selectSingle(tree);
            }

            @Override
            protected void validate() throws Exception {
                ProgressDialogFixture fixture = new ProgressDialogFixture();
                validateProgress(fixture, mStrategy1);
                acceptChange((BlockingConnection) mStrategy1.getEngine()
                        .getConnection(), new Exception("Problem!"));
                fixture.waitForClose();
                ErrorDialogFixture errorDialog = new ErrorDialogFixture();
                errorDialog.assertError("Problem!");
                errorDialog.dismiss();
            }

        };
    }
}
