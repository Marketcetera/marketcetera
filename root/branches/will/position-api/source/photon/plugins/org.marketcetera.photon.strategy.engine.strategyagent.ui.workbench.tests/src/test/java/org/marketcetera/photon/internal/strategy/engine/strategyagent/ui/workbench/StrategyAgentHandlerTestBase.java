package org.marketcetera.photon.internal.strategy.engine.strategyagent.ui.workbench;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.ObjectUtils;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.junit.Test;
import org.marketcetera.photon.commons.ui.JFaceUtilsTest.ErrorDialogFixture;
import org.marketcetera.photon.commons.ui.workbench.ProgressUtilsTest.ProgressDialogFixture;
import org.marketcetera.photon.strategy.engine.model.core.ConnectionState;
import org.marketcetera.photon.strategy.engine.model.core.StrategyEngine;
import org.marketcetera.photon.strategy.engine.model.strategyagent.StrategyAgentEngine;
import org.marketcetera.photon.strategy.engine.model.strategyagent.impl.StrategyAgentEngineImpl;
import org.marketcetera.photon.strategy.engine.ui.workbench.tests.StrategyEnginesViewContextMenuTestBase;

import com.google.common.collect.Lists;

/* $License$ */

/**
 * Base class for handlers that change {@link StrategyAgentEngine}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
public abstract class StrategyAgentHandlerTestBase extends
        StrategyEnginesViewContextMenuTestBase {

    protected final BlockingEngine mEngine1;
    protected final BlockingEngine mEngine2;
    protected final BlockingEngine mEngine3;
    private final String mTaskFormat;

    public StrategyAgentHandlerTestBase(String menuItem, String verb) {
        super(menuItem);
        mTaskFormat = verb + " ''{0}''...";
        mEngine1 = new BlockingEngine("My Engine");
        mEngine2 = new BlockingEngine("My Engine 2");
        mEngine3 = new BlockingEngine("My Engine 3");
    }

    @Override
    protected List<? extends StrategyEngine> createModel() {
        return Lists.newArrayList(mEngine1, mEngine2, mEngine3);
    }

    abstract protected void acceptChange(BlockingEngine engine, Object object)
            throws Exception;

    protected List<BlockingEngine> getMultipleAffected() {
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
                acceptChange(mEngine1, null);
                fixture.waitForClose();
            }
        };
    }

    @Test
    public void testMultiple() throws Exception {
        new TestTemplate() {
            @Override
            protected void select(SWTBotTree tree) {
                tree.select("My Engine", "My Engine 2", "My Engine 3");
            }

            @Override
            protected void validate() throws Exception {
                ProgressDialogFixture fixture = new ProgressDialogFixture();
                for (BlockingEngine engine : getMultipleAffected()) {
                    validateSingle(fixture, engine);
                    acceptChange(engine, null);
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
                tree.select("My Engine", "My Engine 2", "My Engine 3");
            }

            @Override
            protected void validate() throws Exception {
                ProgressDialogFixture fixture = new ProgressDialogFixture();
                validateSingle(fixture, mEngine1);
                fixture.cancel();
                acceptChange(mEngine1, null);
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
                acceptChange(mEngine1, new Exception("Problem!"));
                fixture.waitForClose();
                ErrorDialogFixture errorDialog = new ErrorDialogFixture();
                errorDialog.assertError("Problem!");
                errorDialog.dismiss();
            }
        };
    }

    public static class BlockingEngine extends StrategyAgentEngineImpl {

        private BlockingQueue<Object> mConnect = new SynchronousQueue<Object>();
        private BlockingQueue<Object> mDisconnect = new SynchronousQueue<Object>();
        private Object NOT_EXCEPTION = new Object();

        public BlockingEngine(String name) {
            setName(name);
        }

        @Override
        public void connect() throws Exception {
            pollHelper(mConnect, NOT_EXCEPTION);
            setConnectionState(ConnectionState.CONNECTED);
        }

        public void acceptConnect(Object object) throws Exception {
            mConnect.offer(ObjectUtils.defaultIfNull(object, NOT_EXCEPTION), 5,
                    TimeUnit.SECONDS);
        }

        @Override
        public void disconnect() throws Exception {
            pollHelper(mDisconnect, NOT_EXCEPTION);
            setConnectionState(ConnectionState.DISCONNECTED);
        }

        public void acceptDisconnect(Object object) throws Exception {
            mDisconnect.offer(ObjectUtils.defaultIfNull(object, NOT_EXCEPTION),
                    5, TimeUnit.SECONDS);
        }

        private void pollHelper(BlockingQueue<Object> queue, Object expected)
                throws Exception {
            Object queued = queue.poll(5, TimeUnit.SECONDS);
            if (queued instanceof Exception) {
                throw (Exception) queued;
            } else {
                assertThat(queued, is(expected));
            }
        }
    }

}
