package org.marketcetera.photon.internal.strategy.engine.ui.workbench.handlers;

import static org.marketcetera.photon.strategy.engine.model.core.test.StrategyEngineCoreTestUtil.buildEngines;
import static org.marketcetera.photon.strategy.engine.model.core.test.StrategyEngineCoreTestUtil.createConnectedEngine;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;

import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.junit.Test;
import org.marketcetera.photon.commons.ui.JFaceUtilsTest.ErrorDialogFixture;
import org.marketcetera.photon.commons.ui.workbench.ProgressUtilsTest.ProgressDialogFixture;
import org.marketcetera.photon.strategy.engine.model.core.StrategyEngine;
import org.marketcetera.photon.strategy.engine.ui.workbench.tests.StrategyEnginesViewContextMenuTestBase;

/* $License$ */

/**
 * Base class for handlers that change {@link StrategyEngine}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 2.0.0
 */
public abstract class ChangeEngineHandlerTestBase extends
        StrategyEnginesViewContextMenuTestBase {

    private final String mTaskFormat;
    protected final StrategyEngine mEngine1;
    protected final StrategyEngine mEngine2;

    public ChangeEngineHandlerTestBase(String menuItem, String verb) {
        super(menuItem);
        mTaskFormat = verb + " ''{0}''...";
        mEngine1 = createConnectedEngine("My Engine");
        BlockingConnection connection1 = new BlockingConnection();
        mEngine1.setConnection(connection1);
        mEngine2 = createConnectedEngine("My Engine 2");
        BlockingConnection connection2 = new BlockingConnection();
        mEngine2.setConnection(connection2);
    }

    @Override
    protected List<? extends StrategyEngine> createModel() {
        return buildEngines(mEngine1, mEngine2);
    }

    abstract protected void acceptChange(BlockingConnection connection,
            Object object) throws Exception;

    protected List<StrategyEngine> getMultipleAffected() {
        return Arrays.asList(mEngine1, mEngine2);
    }

    protected void validateSingle(ProgressDialogFixture fixture,
            StrategyEngine engine) throws Exception {
        fixture.assertTask(MessageFormat.format(mTaskFormat, engine.getName()));
    }

    @Test
    public void testSingle() throws Exception {
        new TestTemplate() {
            @Override
            protected void select(SWTBotTree tree) {
                tree.select("My Engine");
            }

            @Override
            protected void validate() throws Exception {
                ProgressDialogFixture fixture = new ProgressDialogFixture();
                validateSingle(fixture, mEngine1);
                acceptChange((BlockingConnection) mEngine1.getConnection(),
                        null);
                fixture.waitForClose();
            }
        };
    }

    @Test
    public void testMultiple() throws Exception {
        new TestTemplate() {
            @Override
            protected void select(SWTBotTree tree) {
                tree.select("My Engine", "My Engine 2");
            }

            @Override
            protected void validate() throws Exception {
                ProgressDialogFixture fixture = new ProgressDialogFixture();
                for (StrategyEngine engine : getMultipleAffected()) {
                    validateSingle(fixture, engine);
                    acceptChange((BlockingConnection) engine.getConnection(),
                            null);
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
                tree.select("My Engine", "My Engine 2");
            }

            @Override
            protected void validate() throws Exception {
                ProgressDialogFixture fixture = new ProgressDialogFixture();
                validateSingle(fixture, mEngine1);
                fixture.cancel();
                acceptChange((BlockingConnection) mEngine1.getConnection(),
                        null);
                fixture.waitForClose();
            }
        };
    }

    @Test
    public void testException() throws Exception {
        new TestTemplate() {
            @Override
            protected void select(SWTBotTree tree) {
                tree.select("My Engine");
            }

            @Override
            protected void validate() throws Exception {
                ProgressDialogFixture fixture = new ProgressDialogFixture();
                validateSingle(fixture, mEngine1);
                acceptChange((BlockingConnection) mEngine1.getConnection(),
                        new Exception("Problem!"));
                fixture.waitForClose();
                ErrorDialogFixture errorDialog = new ErrorDialogFixture();
                errorDialog.assertError("Problem!");
                errorDialog.dismiss();
            }
        };
    }
}
