package org.marketcetera.photon.strategy.engine.strategyagent;

import static org.mockito.Mockito.mock;

import java.util.concurrent.ExecutorService;

import org.junit.Test;
import org.marketcetera.photon.commons.ValidateTest.ExpectedNullArgumentFailure;
import org.marketcetera.photon.core.ICredentialsService;
import org.marketcetera.photon.core.ILogoutService;
import org.marketcetera.photon.strategy.engine.model.strategyagent.StrategyAgentEngine;
import org.marketcetera.photon.strategy.engine.strategyagent.tests.StrategyAgentEngineTestUtil;
import org.marketcetera.photon.test.OSGITestUtil;

/* $License$ */

/**
 * Tests {@link StrategyAgentEngines}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
public class StrategyAgentEnginesTest {

    @Test
    public void testBundle() throws Exception {
        OSGITestUtil.assertBundle(StrategyAgentEngines.PLUGIN_ID);
    }

    @Test
    public void testCreateStrategyAgentEngine() throws Exception {
        final ExecutorService mockExecutor = mock(ExecutorService.class);
        final ICredentialsService mockCredentialsService = mock(ICredentialsService.class);
        final ILogoutService mockLogoutService = mock(ILogoutService.class);
        final StrategyAgentEngine engine = StrategyAgentEngineTestUtil
                .createStrategyAgentEngine("Dummy", "Desc", "url", "host", 12);
        StrategyAgentEngine agent = StrategyAgentEngines
                .createStrategyAgentEngine(engine, mockExecutor,
                        mockCredentialsService, mockLogoutService);
        StrategyAgentEngineTestUtil.assertStrategyAgentEngine(agent, engine);

    }

    @Test
    public void testCreateStrategyAgentEngineValidation() throws Exception {
        final ExecutorService mockExecutor = mock(ExecutorService.class);
        final ICredentialsService mockCredentialsService = mock(ICredentialsService.class);
        final ILogoutService mockLogoutService = mock(ILogoutService.class);
        final StrategyAgentEngine engine = StrategyAgentEngineTestUtil
                .createStrategyAgentEngine("Dummy");
        new ExpectedNullArgumentFailure("guiExecutor") {
            @Override
            protected void run() throws Exception {
                StrategyAgentEngines.createStrategyAgentEngine(engine, null,
                        mockCredentialsService, mockLogoutService);
            }
        };
        new ExpectedNullArgumentFailure("credentialsService") {
            @Override
            protected void run() throws Exception {
                StrategyAgentEngines.createStrategyAgentEngine(engine,
                        mockExecutor, null, mockLogoutService);
            }
        };
        new ExpectedNullArgumentFailure("logoutService") {
            @Override
            protected void run() throws Exception {
                StrategyAgentEngines.createStrategyAgentEngine(engine,
                        mockExecutor, mockCredentialsService, null);
            }
        };
        new ExpectedNullArgumentFailure("engine") {
            @Override
            protected void run() throws Exception {
                StrategyAgentEngines
                        .createStrategyAgentEngine(null, mockExecutor,
                                mockCredentialsService, mockLogoutService);
            }
        };
    }
}
