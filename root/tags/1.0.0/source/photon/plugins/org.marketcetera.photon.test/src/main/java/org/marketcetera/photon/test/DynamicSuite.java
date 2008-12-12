package org.marketcetera.photon.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.junit.internal.runners.InitializationError;
import org.junit.runners.Suite;

/* $License$ */

/**
 * Custom runner that finds tests by plug-in extension.
 *
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.0.0
 */
@SuppressWarnings("restriction")
public class DynamicSuite extends Suite {

	private static Class<?>[] sTests;

	public DynamicSuite(Class<?> klass) throws InitializationError {
		super(klass, sTests);
	}

	static {
		List<Class<?>> tests = new ArrayList<Class<?>>();
		IConfigurationElement[] elements = Platform.getExtensionRegistry()
				.getConfigurationElementsFor("org.marketcetera.photon.test",
						"testSuites");
		for (IConfigurationElement element : elements) {
			try {
				Object test = element.createExecutableExtension("class");
				SuiteClasses classes = test.getClass().getAnnotation(
						SuiteClasses.class);
				tests.addAll(Arrays.asList(classes.value()));
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}
		sTests = tests.toArray(new Class<?>[tests.size()]);
	}
}
