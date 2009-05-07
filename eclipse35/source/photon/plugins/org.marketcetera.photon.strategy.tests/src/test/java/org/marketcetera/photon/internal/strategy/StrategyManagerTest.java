package org.marketcetera.photon.internal.strategy;

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import java.io.InputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import javax.management.JMX;
import javax.management.MBeanServerConnection;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.marketcetera.core.Util;
import org.marketcetera.module.ModuleManager;
import org.marketcetera.module.ModuleState;
import org.marketcetera.module.SinkModuleFactory;
import org.marketcetera.photon.internal.strategy.ruby.RubyStrategyTemplate;
import org.marketcetera.photon.module.ModuleSupport;
import org.marketcetera.photon.test.SWTTestUtil;
import org.marketcetera.strategy.StrategyMXBean;

/* $License$ */

/**
 * Tests {@link StrategyManager}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.5.0
 */
public class StrategyManagerTest {

	private final ModuleManager moduleManager = ModuleSupport.getModuleManager();
	private final MBeanServerConnection mBeanConnection = ModuleSupport.getMBeanServerConnection();
	private StrategyManager fixture;
	private IProject project;

	private class StrategyLifecycleTestTemplate {

		public void run(String classname, String displayname, String filename, boolean routeToServer)
				throws Exception {
			Strategy strategy = registerAndAssert(classname, displayname, filename, routeToServer);
			StrategyMXBean bean = startAndAssert(strategy);
			assertThat(bean.getOutputDestination(), is(SinkModuleFactory.INSTANCE_URN.toString()));
			assertThat(bean.isRoutingOrdersToORS(), is(routeToServer));
			testWhileRunning(strategy, bean);
			stopAndAssert(strategy, bean);
			removeAndAssert(strategy);
			project.getFile(filename).delete(true, null);
		}

		protected void testWhileRunning(Strategy strategy, StrategyMXBean bean) throws Exception {
			// do nothing in the template, subclasses can override
		}
	}

	private Strategy registerAndAssert(String classname, String displayname, String filename,
			boolean routeToServer) throws Exception {
		IFile file = createTemplateStrategy(filename, classname);
		int size = fixture.getStrategies().size();
		fixture.registerStrategy(file, classname, displayname, routeToServer);
		assertThat(fixture.getStrategies().size(), is(size + 1));
		// assuming strategies are appended to the end of the list...
		Strategy strategy = (Strategy) fixture.getStrategies().get(size);
		assertThat(strategy.getFile().getName(), is(filename));
		assertThat(strategy.getFile(), sameInstance(file));
		assertThat(strategy.getClassName(), is(classname));
		assertThat(strategy.getDisplayName(), is(displayname));
		assertThat(strategy.getRouteToServer(), is(routeToServer));
		return strategy;
	}

	private StrategyMXBean startAndAssert(Strategy strategy) throws Exception {
		fixture.start(strategy);
		assertThat(moduleManager.getModuleInfo(strategy.getURN()).getState(),
				is(ModuleState.STARTED));
		final StrategyMXBean bean = JMX.newMXBeanProxy(mBeanConnection, strategy.getURN()
				.toObjectName(), StrategyMXBean.class);
		// give time for strategy to compile/start since it is run in a separate thread
		SWTTestUtil.conditionalDelayUnchecked(5, TimeUnit.SECONDS, new Callable<Boolean>() {

			@Override
			public Boolean call() throws Exception {
				return bean.getStatus().equals("RUNNING");
			}
		});
		return bean;
	}

	private void stopAndAssert(Strategy strategy, final StrategyMXBean bean) throws Exception {
		bean.interrupt();
		// give time for strategy to stop since it is run in a separate thread
		SWTTestUtil.conditionalDelayUnchecked(5, TimeUnit.SECONDS, new Callable<Boolean>() {
		
			@Override
			public Boolean call() throws Exception {
				return bean.getStatus().equals("STOPPED");
			}
		});
		fixture.stop(strategy);
		assertThat(moduleManager.getModuleInfo(strategy.getURN()).getState(),
				is(ModuleState.STOPPED));
	}

	private void removeAndAssert(Strategy strategy) throws Exception {
		fixture.removeStrategy(strategy);
		for (Object object : fixture.getStrategies()) {
			assertThat(object, not(sameInstance((Object) strategy)));
		}
	}

	private IFile createTemplateStrategy(String filename, String classname) throws Exception {
		InputStream stream = new RubyStrategyTemplate().createNewScript(classname);
		IFile file = project.getFile(filename);
		file.create(stream, true, null);
		stream.close();
		return file;
	}

	@Before
	public void setup() throws Exception {
		fixture = new StrategyManager();
		project = ResourcesPlugin.getWorkspace().getRoot().getProject("junit-scripts");
		project.create(null);
		project.open(null);
		assertThat(fixture.getStrategies().size(), is(0));
	}

	@After
	public void cleanup() throws Exception {
		// make a copy to avoid concurrent modification exception
		List<Strategy> toRemove = new ArrayList<Strategy>();
		for (Object object : fixture.getStrategies()) {
			toRemove.add((Strategy) object);
		}
		for (Strategy strategy : toRemove) {
			fixture.removeStrategy(strategy);
		}
		project.delete(true, true, null);
	}

	/**
	 * Register a strategy
	 */
	@Test
	public void registerStrategy() throws Exception {
		new StrategyLifecycleTestTemplate().run("TestClass", "Test", "test.test", false);
	}

	/**
	 * Register another strategy
	 */
	@Test
	public void registerStrategy2() throws Exception {
		new StrategyLifecycleTestTemplate().run("TestClass2", "Test2", "test2.test", true);
	}

	/**
	 * Tests {@link StrategyManager#isUniqueName(String)}
	 */
	@Test
	public void uniqueName() throws Exception {
		new StrategyLifecycleTestTemplate() {
			@Override
			protected void testWhileRunning(Strategy strategy, StrategyMXBean bean)
					throws Exception {
				assertThat(fixture.isUniqueName("Test"), is(false));
				assertThat(fixture.isUniqueName("ABC"), is(true));
				// create a new strategy with ABC
				new StrategyLifecycleTestTemplate() {
					@Override
					protected void testWhileRunning(Strategy strategy, StrategyMXBean bean) {
						assertThat(fixture.isUniqueName("Test"), is(false));
						assertThat(fixture.isUniqueName("ABC"), is(false));
					}
				}.run("TestClass2", "ABC", "test2.test", false);
				assertThat(fixture.isUniqueName("ABC"), is(true));
			}
		}.run("TestClass", "Test", "test.test", false);
	}

	/**
	 * Tests {@link StrategyManager#setParameters(Strategy, Properties)}
	 */
	@Test
	public void modifyParameters() throws Exception {
		new StrategyLifecycleTestTemplate() {
			@Override
			protected void testWhileRunning(Strategy strategy, StrategyMXBean bean)
					throws Exception {
				assertNull(bean.getParameters());
				Properties properties = new Properties();
				properties.put("ABC", "XYZ");
				properties.put("123", "456");
				fixture.setParameters(strategy, properties);
				for (Map.Entry<Object, Object> entry : properties.entrySet()) {
					assertThat(strategy.getParameters().get(entry.getKey()), is(entry.getValue()));
				}
				assertThat(bean.getParameters(), is(MessageFormat.format("{0}{1}{2}{3}{4}{1}{5}",
						"ABC", Util.KEY_VALUE_SEPARATOR, "XYZ", Util.KEY_VALUE_DELIMITER, "123",
						"456")));
				fixture.setParameters(strategy, new Properties());
				assertThat(strategy.getParameters().size(), is(0));
				assertNull(bean.getParameters());
			}
		}.run("TestClass", "Test", "test.test", false);
	}

	/**
	 * Tests {@link StrategyManager#setRouteToServer(Strategy, boolean)}
	 */
	@Test
	public void modifyRoute() throws Exception {
		new StrategyLifecycleTestTemplate() {
			@Override
			protected void testWhileRunning(Strategy strategy, StrategyMXBean bean)
					throws Exception {
				assertThat(bean.isRoutingOrdersToORS(), is(false));
				fixture.setRouteToServer(strategy, true);
				assertThat(strategy.getRouteToServer(), is(true));
				assertThat(bean.isRoutingOrdersToORS(), is(true));
				fixture.setRouteToServer(strategy, false);
				assertThat(strategy.getRouteToServer(), is(false));
				assertThat(bean.isRoutingOrdersToORS(), is(false));
			}
		}.run("TestClass", "Test", "test.test", false);
	}

	/**
	 * Tests enabling and disabling the remote agent.
	 */
	@Test
	public void enableRemoteAgent() throws Exception {
		fixture.enableRemoteAgent();
		assertThat(fixture.getStrategies().size(), is(1));
		assertThat(fixture.getStrategies().get(0), instanceOf(RemoteStrategyAgent.class));
		fixture.disableRemoteAgent();
		assertThat(fixture.getStrategies().size(), is(0));
	}

	@Test
	public void verifyStrategyClasspath() throws Exception {
		assertNotNull(System.getProperty(org.marketcetera.strategy.Strategy.CLASSPATH_PROPERTYNAME));
	}
	
	@Test
	public void verifyStrategyFileDeletion() throws Exception {
		String filename = "to_delete.test";
		registerAndAssert("ToDelete", "ToDelete", filename, false);
		project.getFile(filename).delete(true, null);
		// strategy should have been unregistered and removed
		assertThat(fixture.getStrategies().size(), is(0));
	}
	
	@Test
	public void verifyMultipleStrategyDeletion() throws Exception {
		String filename = "to_delete.test";
		Strategy s = registerAndAssert("ToDelete", "ToDelete1", filename, false);
		fixture.registerStrategy(s.getFile(), "ToDelete", "ToDelete2", false);
		project.getFile(filename).delete(true, null);
		// strategy should have been unregistered and removed
		assertThat(fixture.getStrategies().size(), is(0));
	}
	
	@Test
	public void verifyProjectDeletion() throws Exception {
		registerAndAssert("ToDelete", "ToDelete", "to_delete1.test", false);
		registerAndAssert("ToDelete", "ToDelete", "to_delete2.test", false);
		project.delete(true, null);
		// strategy should have been unregistered and removed
		assertThat(fixture.getStrategies().size(), is(0));
	}
}
