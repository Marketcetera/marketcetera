package org.marketcetera.photon.strategy.engine.ui.tests;

import java.util.concurrent.Callable;

import org.marketcetera.photon.strategy.engine.model.core.DeployedStrategy;
import org.marketcetera.photon.strategy.engine.model.core.Strategy;
import org.marketcetera.photon.strategy.engine.model.core.test.StrategyEngineCoreTestUtil.MockConnection;
import org.marketcetera.photon.test.AbstractUIRunner;
import org.marketcetera.photon.test.AbstractUIRunner.ThrowableRunnable;

/* $License$ */

/**
 * Mock connection that makes sure updates are done in the UI thread.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 2.0.0
 */
public class MockUIConnection extends MockConnection {

    @Override
    public DeployedStrategy deploy(final Strategy strategy) throws Exception {
        return AbstractUIRunner.syncCall(new Callable<DeployedStrategy>() {
            @Override
            public DeployedStrategy call() throws Exception {
                return MockUIConnection.super.deploy(strategy);
            }
        });
    }

    @Override
    public void start(final DeployedStrategy strategy) throws Exception {
        AbstractUIRunner.syncRun(new ThrowableRunnable() {
            @Override
            public void run() throws Throwable {
                MockUIConnection.super.start(strategy);
            }
        });
    }

    @Override
    public void stop(final DeployedStrategy strategy) throws Exception {
        AbstractUIRunner.syncRun(new ThrowableRunnable() {
            @Override
            public void run() throws Throwable {
                MockUIConnection.super.stop(strategy);
            }
        });
    }

    @Override
    public void undeploy(final DeployedStrategy strategy) throws Exception {
        AbstractUIRunner.syncRun(new ThrowableRunnable() {
            @Override
            public void run() throws Throwable {
                MockUIConnection.super.undeploy(strategy);
            }
        });
    }

    @Override
    public void update(final DeployedStrategy strategy, final Strategy newConfiguration)
            throws Exception {
        AbstractUIRunner.syncRun(new ThrowableRunnable() {
            @Override
            public void run() throws Throwable {
                MockUIConnection.super.update(strategy, newConfiguration);
            }
        });
    }

}
