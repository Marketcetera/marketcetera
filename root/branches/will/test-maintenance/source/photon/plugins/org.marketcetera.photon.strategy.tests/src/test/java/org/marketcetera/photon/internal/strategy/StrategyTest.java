package org.marketcetera.photon.internal.strategy;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import java.util.Map;
import java.util.Properties;

import org.eclipse.core.resources.IFile;
import org.junit.Test;
import org.marketcetera.module.ExpectedFailure;
import org.marketcetera.module.ModuleURN;

/* $License$ */

/**
 * Test {@link Strategy}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.0.0
 */
public class StrategyTest {

	/**
	 * Test constructor.
	 */
	@Test
	public void constructor() {
		String displayName = "Null Strategy";
		ModuleURN urn = new ModuleURN("metc:strategy:system:test");
		IFile mockFile = mock(IFile.class);
		String className = "Null Strategy Class";
		boolean routeToServer = false;
		Properties parameters = new Properties();
		Strategy strategy = new Strategy(displayName, urn,
				mockFile, className, routeToServer, parameters);
		assertStrategy(strategy, displayName, urn, mockFile, className, routeToServer, parameters);
		// try again with some different values
		displayName = "Null Strategy 2";
		urn = new ModuleURN("metc:strategy:system:test2");
		mockFile = mock(IFile.class);
		className = "Null Strategy Class 2";
		routeToServer = true;
		parameters = new Properties();
		parameters.put("A", "B");
		strategy = new Strategy(displayName, urn,
				mockFile, className, routeToServer, parameters);
		assertStrategy(strategy, displayName, urn, mockFile, className, routeToServer, parameters);
	}

	private void assertStrategy(Strategy strategy, String displayName, ModuleURN urn, IFile mockFile,
			String className, boolean routeToServer, Properties parameters) {
		assertThat(strategy.getDisplayName(), is(displayName));
		assertThat(strategy.getURN(), is(urn));
		assertThat(strategy.getFile(), is(mockFile));
		assertThat(strategy.getClassName(), is(className));
		assertThat(strategy.getRouteToServer(), is(routeToServer));
		assertParametersEqual(strategy, parameters);
		
	}

	/**
	 * Test get/set parameters.
	 */
	@Test
	public void testParameters() {
		Strategy fixture = createTestStrategy();
		assertTrue(fixture.getParameters().isEmpty());
		Properties props = new Properties();
		props.put("ABC", "DEF");
		props.put("XYZ", "123");
		fixture.setParameters(props);
		assertParametersEqual(fixture, props);
		props = new Properties();
		fixture.setParameters(props);
		assertTrue(fixture.getParameters().isEmpty());
	}

	private void assertParametersEqual(Strategy fixture, Properties props) {
		assertThat(fixture.getParameters().size(), is(props.size()));
		for (Map.Entry<Object, Object> entry : props.entrySet()) {
			assertEquals(entry.getValue(), fixture.getParameters().get(entry.getKey()));
		}
	}

	/**
	 * Test that null arguments fail.
	 */
	@Test
	public void nulls() throws Exception {
		new ExpectedFailure<IllegalArgumentException>(null) {
			@Override
			protected void run() throws Exception {
				new Strategy(null, new ModuleURN("metc:strategy:system:test"), mock(IFile.class),
						"Null Strategy", false, new Properties());
			}
		};
		new ExpectedFailure<IllegalArgumentException>(null) {
			@Override
			protected void run() throws Exception {
				new Strategy("Null Strategy", null, mock(IFile.class), "Null Strategy", false,
						new Properties());
			}
		};
		new ExpectedFailure<IllegalArgumentException>(null) {
			@Override
			protected void run() throws Exception {
				new Strategy("Null Strategy", new ModuleURN("metc:strategy:system:test"), null,
						"Null Strategy", false, new Properties());
			}
		};
		new ExpectedFailure<IllegalArgumentException>(null) {
			@Override
			protected void run() throws Exception {
				new Strategy("Null Strategy", new ModuleURN("metc:strategy:system:test"),
						mock(IFile.class), null, false, new Properties());
			}
		};
		new ExpectedFailure<IllegalArgumentException>(null) {
			@Override
			protected void run() throws Exception {
				new Strategy("Null Strategy", new ModuleURN("metc:strategy:system:test"),
						mock(IFile.class), "Null Strategy", false, null);
			}
		};
		new ExpectedFailure<IllegalArgumentException>(null) {
			@Override
			protected void run() throws Exception {
				Strategy strategy = createTestStrategy();
				strategy.setParameters(null);
			}
		};
	}

	public static Strategy createTestStrategy() {
		return new Strategy("Null Strategy", new ModuleURN("metc:strategy:system:test"),
				mock(IFile.class), "Null Strategy", false, new Properties());
	}

}
