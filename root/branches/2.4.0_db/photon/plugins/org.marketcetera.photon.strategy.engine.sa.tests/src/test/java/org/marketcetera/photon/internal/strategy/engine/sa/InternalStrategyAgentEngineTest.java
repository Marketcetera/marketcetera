package org.marketcetera.photon.internal.strategy.engine.sa;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.marketcetera.photon.strategy.engine.model.core.test.StrategyEngineCoreTestUtil.assertDeployedStrategy;
import static org.marketcetera.photon.strategy.engine.model.core.test.StrategyEngineCoreTestUtil.createDeployedStrategy;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.marketcetera.core.ImmediateExecutorService;
import org.marketcetera.module.ModuleState;
import org.marketcetera.module.ModuleURN;
import org.marketcetera.photon.core.Credentials;
import org.marketcetera.photon.core.ICredentialsService;
import org.marketcetera.photon.core.LogoutService;
import org.marketcetera.photon.module.ISinkDataManager;
import org.marketcetera.photon.strategy.engine.model.core.ConnectionState;
import org.marketcetera.photon.strategy.engine.model.core.StrategyState;
import org.marketcetera.photon.strategy.engine.model.sa.StrategyAgentEngine;
import org.marketcetera.photon.strategy.engine.sa.tests.StrategyAgentEngineTestUtil;
import org.marketcetera.photon.test.ExpectedFailure;
import org.marketcetera.photon.test.PhotonTestBase;
import org.marketcetera.saclient.ConnectionException;
import org.marketcetera.saclient.ConnectionStatusListener;
import org.marketcetera.saclient.DataReceiver;
import org.marketcetera.saclient.SAClient;
import org.marketcetera.saclient.SAClientFactory;
import org.marketcetera.saclient.SAClientParameters;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.google.common.collect.ImmutableMap;

/* $License$ */

/**
 * Tests {@link InternalStrategyAgentEngine}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 2.0.0
 */
public class InternalStrategyAgentEngineTest extends PhotonTestBase {

    private SAClientFactory mMockFactory;
    private SAClient mMockClient;
    private ICredentialsService mMockCredentials;
    private InternalStrategyAgentEngine mFixture;
    private StrategyAgentEngine mEngine;
    private LogoutService mLogoutService;
    private ISinkDataManager mMockSinkDataManager;
    private ConnectionStatusListener mConnectionStatusListener;
    private DataReceiver mDataReceiver;

    @Before
    public void before() throws Exception {
        mMockFactory = mock(SAClientFactory.class);
        mMockClient = mock(SAClient.class);
        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                mConnectionStatusListener = (ConnectionStatusListener) invocation
                        .getArguments()[0];
                return null;
            }
        }).when(mMockClient).addConnectionStatusListener(
                (ConnectionStatusListener) anyObject());
        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                mDataReceiver = (DataReceiver) invocation.getArguments()[0];
                return null;
            }
        }).when(mMockClient).addDataReceiver((DataReceiver) anyObject());
        mMockCredentials = new ICredentialsService() {
            @Override
            public void invalidate() {
            }

            @Override
            public boolean authenticateWithCredentials(
                    IAuthenticationHelper helper) {
                return helper.authenticate(new Credentials("me", "pass"));
            }
        };
        mLogoutService = new LogoutService();
        mMockSinkDataManager = mock(ISinkDataManager.class);
        mEngine = StrategyAgentEngineTestUtil.createStrategyAgentEngine("sa",
                "strategy agent", "url", "host", 1000);
        mFixture = new InternalStrategyAgentEngine(mEngine,
                new ImmediateExecutorService(), mMockCredentials,
                mLogoutService, mMockFactory, mMockSinkDataManager);
        assertThat(mFixture.getConnection(), nullValue());
        StrategyAgentEngineTestUtil
                .assertStrategyAgentEngine(mFixture, mEngine);
    }

    @Test
    public void testSuccessfulConnect() throws Exception {
        SAClientParameters parameters = new SAClientParameters("me", "pass"
                .toCharArray(), "url", "host", 1000);
        when(mMockFactory.create(parameters)).thenReturn(mMockClient);
        mFixture.connect();
        assertThat(mFixture.getConnectionState(), is(ConnectionState.CONNECTED));
        assertThat(mFixture.getConnection(), is(not(nullValue())));
    }

    @Test
    public void testConnectionException() throws Exception {
        SAClientParameters parameters = new SAClientParameters("me", "pass"
                .toCharArray(), "url", "host", 1000);
        final Exception mockException = mock(ConnectionException.class);
        when(mMockFactory.create(parameters)).thenThrow(mockException);
        new ExpectedFailure<ConnectionException>(null) {
            @Override
            protected void run() throws Exception {
                try {
                    mFixture.connect();
                } catch (Exception e) {
                    assertThat(e, sameInstance(mockException));
                    throw e;
                }
            }
        };
        assertThat(mFixture.getConnectionState(),
                is(ConnectionState.DISCONNECTED));
        assertThat(mFixture.getConnection(), is(nullValue()));
    }

    @Test
    public void testConnectionCanceled() throws Exception {
        mMockCredentials = new ICredentialsService() {
            @Override
            public void invalidate() {
            }

            @Override
            public boolean authenticateWithCredentials(
                    IAuthenticationHelper helper) {
                return false;
            }
        };
        mFixture = new InternalStrategyAgentEngine(mEngine,
                new ImmediateExecutorService(), mMockCredentials,
                mLogoutService, mMockFactory, mMockSinkDataManager);
        mFixture.connect();
        assertThat(mFixture.getConnectionState(),
                is(ConnectionState.DISCONNECTED));
        assertThat(mFixture.getConnection(), is(nullValue()));
    }

    @Test
    public void testConnectWithExistingState() throws Exception {
        SAClientParameters parameters = new SAClientParameters("me", "pass"
                .toCharArray(), "url", "host", 1000);
        when(mMockFactory.create(parameters)).thenReturn(mMockClient);
        ModuleURN urn1 = new ModuleURN("metc:strategy:system:strat1");
        when(mMockClient.getInstances(new ModuleURN("metc:strategy:system")))
                .thenReturn(Arrays.asList(urn1));
        when(mMockClient.getProperties(urn1)).thenReturn(
                StrategyAgentConnectionTest.createParameters(false,
                        "MySAStrategy2", "RUBY", StrategyAgentConnectionTest
                                .getParametersString(ImmutableMap.of("xyz",
                                        "123"))));
        when(mMockClient.getModuleInfo(urn1)).thenReturn(
                StrategyAgentConnectionTest
                        .createModuleInfo(ModuleState.STARTED));
        mFixture.connect();
        assertThat(mFixture.getConnectionState(), is(ConnectionState.CONNECTED));
        assertThat(mFixture.getConnection(), is(not(nullValue())));
        assertThat(mFixture.getDeployedStrategies().size(), is(1));
        assertDeployedStrategy(mFixture.getDeployedStrategies().get(0),
                mFixture, StrategyState.RUNNING, "strat1", "MySAStrategy2",
                "RUBY", null, false, ImmutableMap.of("xyz", "123"));
    }

    @Test
    public void testDisconnect() throws Exception {
        SAClientParameters parameters = new SAClientParameters("me", "pass"
                .toCharArray(), "url", "host", 1000);
        when(mMockFactory.create(parameters)).thenReturn(mMockClient);
        doAnswer(new Answer<Void>() {
            private boolean once;
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                if (!once) {
                    once = true;
                    mConnectionStatusListener.receiveConnectionStatus(false);
                }
                return null;
            }
        }).when(mMockClient).close();
        mFixture.connect();
        mFixture.getDeployedStrategies().add(createDeployedStrategy("ABC"));
        mFixture.disconnect();
        verify(mMockClient).close();
        assertThat(mFixture.getConnectionState(),
                is(ConnectionState.DISCONNECTED));
        assertThat(mFixture.getConnection(), is(nullValue()));
        assertThat(mFixture.getDeployedStrategies().size(), is(0));
        /*
         * Disconnect again, make sure no exceptions.
         */
        mFixture.disconnect();
    }

    @Test
    public void testLogout() throws Exception {
        SAClientParameters parameters = new SAClientParameters("me", "pass"
                .toCharArray(), "url", "host", 1000);
        when(mMockFactory.create(parameters)).thenReturn(mMockClient);
        mFixture.connect();
        mLogoutService.logout();
        verify(mMockClient).close();
        assertThat(mFixture.getConnectionState(),
                is(ConnectionState.DISCONNECTED));
        assertThat(mFixture.getConnection(), is(nullValue()));
        assertThat(mFixture.getDeployedStrategies().size(), is(0));
    }

    @Test
    public void testLogoutAfterDisconnect() throws Exception {
        SAClientParameters parameters = new SAClientParameters("me", "pass"
                .toCharArray(), "url", "host", 1000);
        when(mMockFactory.create(parameters)).thenReturn(mMockClient);
        mFixture.connect();
        mFixture.disconnect();
        mLogoutService.logout();
        assertThat(mFixture.getConnectionState(),
                is(ConnectionState.DISCONNECTED));
        assertThat(mFixture.getConnection(), is(nullValue()));
        assertThat(mFixture.getDeployedStrategies().size(), is(0));
    }

    @Test
    public void testAutoLogout() throws Exception {
        SAClientParameters parameters = new SAClientParameters("me", "pass"
                .toCharArray(), "url", "host", 1000);
        when(mMockFactory.create(parameters)).thenReturn(mMockClient);
        mFixture.connect();
        mConnectionStatusListener.receiveConnectionStatus(false);
        verify(mMockClient).close();
        assertThat(mFixture.getConnectionState(),
                is(ConnectionState.DISCONNECTED));
        assertThat(mFixture.getConnection(), is(nullValue()));
        assertThat(mFixture.getDeployedStrategies().size(), is(0));
    }

    @Test
    public void testDataReceiver() throws Exception {
        SAClientParameters parameters = new SAClientParameters("me", "pass"
                .toCharArray(), "url", "host", 1000);
        when(mMockFactory.create(parameters)).thenReturn(mMockClient);
        mFixture.connect();
        mDataReceiver.receiveData("ABC");
        verify(mMockSinkDataManager).sendData("sa", "ABC");
    }

}
