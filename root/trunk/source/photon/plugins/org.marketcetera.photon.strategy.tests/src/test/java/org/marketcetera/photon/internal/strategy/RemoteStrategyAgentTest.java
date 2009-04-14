package org.marketcetera.photon.internal.strategy;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import java.net.URI;

import org.junit.Test;
import org.marketcetera.module.ExpectedFailure;

/* $License$ */

/**
 * Test {@link RemoteStrategyAgent}.
 *
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.5.0
 */
public class RemoteStrategyAgentTest {

	/**
	 * Test expected errors.
	 */
	@Test
	public void nulls() throws Exception {
		new ExpectedFailure<IllegalArgumentException>(null) {
			@Override
			protected void run() throws Exception {
				new RemoteStrategyAgent(null);
			}
		};
	}

	/**
	 * Test constructor.
	 */
	@Test
	public void constructor() {
		createAgent("abc");
	}

	private RemoteStrategyAgent createAgent(String displayName) {
		RemoteStrategyAgent agent = new RemoteStrategyAgent(displayName);
		assertThat(agent.getDisplayName(), is(displayName));
		assertNull(agent.getURI());
		assertNull(agent.getUsername());
		assertNull(agent.getPassword());
		return agent;
	}

	/**
	 * Test setters and getters.
	 */
	@Test
	public void testSetURI() throws Exception {
		RemoteStrategyAgent agent = createAgent("uri");
		URI uri = new URI("http://www");
		agent.setURI(uri);
		assertThat(agent.getURI(), is(uri));
		agent.setURI(null);
		assertNull(agent.getURI());
		String username = "abc";
		agent.setUsername(username);
		assertThat(agent.getUsername(), is(username));
		agent.setUsername(null);
		assertNull(agent.getUsername());
		String password = "def";
		agent.setPassword(password);
		assertThat(agent.getPassword(), is(password));
		agent.setPassword(null);
		assertNull(agent.getPassword());
	}
}
