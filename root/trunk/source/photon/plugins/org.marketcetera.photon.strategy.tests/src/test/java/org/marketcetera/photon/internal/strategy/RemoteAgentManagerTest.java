package org.marketcetera.photon.internal.strategy;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.stub;
import static org.mockito.Mockito.verify;

import java.net.URI;

import javax.management.MBeanServerConnection;

import org.eclipse.core.runtime.IStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.marketcetera.module.ExpectedFailure;
import org.marketcetera.module.InvalidURNException;
import org.marketcetera.module.ModuleURN;
import org.marketcetera.modules.remote.emitter.EmitterFactory;
import org.marketcetera.photon.internal.strategy.AbstractStrategyConnection.State;
import org.marketcetera.photon.module.ModuleSupport;

/* $License$ */

/**
 * Tests {@link RemoteAgentManager}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.5.0
 */
public class RemoteAgentManagerTest {

	private static final String TEST_INSTANCE = "test";
	private static final ModuleURN TEST_URN =
			new ModuleURN(EmitterFactory.PROVIDER_URN, TEST_INSTANCE);
	private RemoteStrategyAgent mockAgent;
	private RemoteAgentManager fixture;

	@Test
	public void testConstructor() throws Exception {
		new ExpectedFailure<IllegalArgumentException>(null) {
			@Override
			protected void run() throws Exception {
				new RemoteAgentManager(null, mock(MBeanServerConnection.class),
						mock(RemoteStrategyAgent.class), TEST_INSTANCE);
			}
		};
		new ExpectedFailure<IllegalArgumentException>(null) {
			@Override
			protected void run() throws Exception {
				new RemoteAgentManager(ModuleSupport.getModuleManager(), null,
						mock(RemoteStrategyAgent.class), TEST_INSTANCE);
			}
		};
		new ExpectedFailure<IllegalArgumentException>(null) {
			@Override
			protected void run() throws Exception {
				new RemoteAgentManager(ModuleSupport.getModuleManager(),
						mock(MBeanServerConnection.class), null, TEST_INSTANCE);
			}
		};
		new ExpectedFailure<IllegalArgumentException>(null) {
			@Override
			protected void run() throws Exception {
				new RemoteAgentManager(ModuleSupport.getModuleManager(),
						mock(MBeanServerConnection.class), mock(RemoteStrategyAgent.class), null);
			}
		};
		new ExpectedFailure<InvalidURNException>(null) {
			@Override
			protected void run() throws Exception {
				new RemoteAgentManager(ModuleSupport.getModuleManager(),
						mock(MBeanServerConnection.class), mock(RemoteStrategyAgent.class), "$%#");
			}
		};
	}

	@Before
	public void setup() throws Exception {
		mockAgent = mock(RemoteStrategyAgent.class);
		stub(mockAgent.getState()).toReturn(State.STOPPED);
		fixture =
				new RemoteAgentManager(ModuleSupport.getModuleManager(), ModuleSupport
						.getMBeanServerConnection(), mockAgent, TEST_INSTANCE);
	}
	
	@After
	public void teardown() {
		ModuleSupport.getModuleAttributeSupport().removeDefaultFor(TEST_URN, "URL");
	}

	@Test
	public void testConnect() throws Exception {
		// test null URI fails
		stub(mockAgent.getURI()).toReturn(null);
		IStatus status = fixture.connect();
		assertStatus(status, IStatus.ERROR, Messages.REMOTE_AGENT_MANAGER_MISSING_URI.getText());
		// test a valid, but bogus URI
		URI remoteAgentURI = new URI("http://www.bogus.com");
		assertNull(ModuleSupport.getModuleAttributeSupport().getDefaultFor(TEST_URN, "URL"));
		stub(mockAgent.getURI()).toReturn(remoteAgentURI);
		status = fixture.connect();
		assertStatus(status, IStatus.ERROR, Messages.REMOTE_AGENT_MANAGER_CONNECT_FAILED
				.getText(remoteAgentURI));
		assertThat(ModuleSupport.getModuleAttributeSupport().getDefaultFor(
				new ModuleURN(EmitterFactory.PROVIDER_URN, TEST_INSTANCE), "URL"),
				is(remoteAgentURI.toString()));
	}

	private void assertStatus(IStatus status, int severity, String message) {
		assertThat(status.getSeverity(), is(severity));
		assertThat(status.getMessage(), is(message));
	}

	@Test
	public void testDisconnect() {
		// disconnect when module doesn't exists
		stub(mockAgent.getState()).toReturn(State.RUNNING);
		fixture.disconnect();
		verify(mockAgent).setState(State.STOPPED);
	}

}
