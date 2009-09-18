package org.marketcetera.photon.strategy.engine.strategyagent.ui;

import static org.marketcetera.photon.strategy.engine.strategyagent.tests.StrategyAgentEngineTestUtil.assertStrategyAgentEngine;
import static org.marketcetera.photon.strategy.engine.strategyagent.tests.StrategyAgentEngineTestUtil.createStrategyAgentEngine;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.runner.RunWith;
import org.marketcetera.photon.core.ICredentialsService;
import org.marketcetera.photon.core.ILogoutService;
import org.marketcetera.photon.strategy.engine.model.core.StrategyEngine;
import org.marketcetera.photon.strategy.engine.model.strategyagent.StrategyAgentEngine;
import org.marketcetera.photon.strategy.engine.ui.AbstractStrategyEnginesSupportTestBase;
import org.marketcetera.photon.test.SimpleUIRunner;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

/* $License$ */

/**
 * Tests {@link StrategyAgentEnginesSupport}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@RunWith(SimpleUIRunner.class)
public class StrategyAgentEnginesSupportTest extends
        AbstractStrategyEnginesSupportTestBase {

    private ICredentialsService mMockCredentialsService;
    private ILogoutService mMockLogoutService;

    @Override
    public void before() {
        super.before();
        mMockCredentialsService = mock(ICredentialsService.class);
        mMockLogoutService = mock(ILogoutService.class);
        when(mMockContext.getService((ServiceReference) anyObject()))
                .thenReturn(mMockCredentialsService, mMockLogoutService);
    }

    @Override
    protected StrategyAgentEnginesSupport createAndInit(
            BundleContext bundleContext) {
        return new StrategyAgentEnginesSupport(bundleContext, null);
    }

    @Override
    protected StrategyAgentEngine createEngineToAdd() {
        return createStrategyAgentEngine("A", "B", "C", "D", 3);
    }

    @Override
    protected void assertAdded(StrategyEngine returned, StrategyEngine added) {
        assertStrategyAgentEngine((StrategyAgentEngine) returned,
                (StrategyAgentEngine) added);
    }
}
