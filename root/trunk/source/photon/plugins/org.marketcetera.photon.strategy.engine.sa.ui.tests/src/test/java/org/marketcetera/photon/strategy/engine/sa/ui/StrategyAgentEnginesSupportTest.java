package org.marketcetera.photon.strategy.engine.sa.ui;

import static org.marketcetera.photon.strategy.engine.sa.tests.StrategyAgentEngineTestUtil.assertStrategyAgentEngine;
import static org.marketcetera.photon.strategy.engine.sa.tests.StrategyAgentEngineTestUtil.createStrategyAgentEngine;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.marketcetera.photon.core.ICredentialsService;
import org.marketcetera.photon.core.ILogoutService;
import org.marketcetera.photon.strategy.engine.model.core.StrategyEngine;
import org.marketcetera.photon.strategy.engine.model.sa.StrategyAgentEngine;
import org.marketcetera.photon.strategy.engine.sa.ui.StrategyAgentEnginesSupport;
import org.marketcetera.photon.strategy.engine.ui.AbstractStrategyEnginesSupportTestBase;
import org.marketcetera.photon.test.ExpectedIllegalStateException;
import org.marketcetera.photon.test.SimpleUIRunner;
import org.marketcetera.photon.test.AbstractUIRunner.UI;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

/* $License$ */

/**
 * Tests {@link StrategyAgentEnginesSupport}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 2.0.0
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
        ServiceReference mockCredentialsReference = mock(ServiceReference.class);
        ServiceReference mockLogoutReference = mock(ServiceReference.class);
        when(
                mMockContext.getServiceReference(ICredentialsService.class
                        .getName())).thenReturn(mockCredentialsReference);
        when(mMockContext.getServiceReference(ILogoutService.class.getName()))
                .thenReturn(mockLogoutReference);
        when(mMockContext.getService(mockCredentialsReference)).thenReturn(
                mMockCredentialsService);
        when(mMockContext.getService(mockLogoutReference)).thenReturn(
                mMockLogoutService);
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

    @Test
    @UI
    public void credentialsServiceUnavailable() throws Exception {
        when(
                mMockContext.getServiceReference(ICredentialsService.class
                        .getName())).thenReturn(null);
        new ExpectedIllegalStateException("ICredentialsService is unavailable") {
            @Override
            protected void run() throws Exception {
                createAndInit(mMockContext);
            };
        };
    }

    @Test
    @UI
    public void servicesUnavailable() throws Exception {
        when(mMockContext.getServiceReference(ILogoutService.class.getName()))
                .thenReturn(null);
        new ExpectedIllegalStateException("ILogoutService is unavailable") {
            @Override
            protected void run() throws Exception {
                createAndInit(mMockContext);
            };
        };
    }
}
