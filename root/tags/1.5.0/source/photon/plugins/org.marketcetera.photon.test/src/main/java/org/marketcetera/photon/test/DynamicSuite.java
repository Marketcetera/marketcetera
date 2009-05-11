package org.marketcetera.photon.test;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.junit.runners.model.InitializationError;
import org.junit.runners.Suite;

/* $License$ */

/**
 * Custom runner that finds tests by plug-in extension.
 *
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.0.0
 */
public class DynamicSuite extends Suite {

	public DynamicSuite(Class<?> klass) throws InitializationError {
		super(klass, getTests());
	}

	static Class<?>[] getTests() {
		List<Class<?>> tests = new ArrayList<Class<?>>();
		IConfigurationElement[] elements = Platform.getExtensionRegistry()
				.getConfigurationElementsFor("org.marketcetera.photon.test",
						"testSuites");
		for (IConfigurationElement element : elements) {
			try {
				Object test = element.createExecutableExtension("class");
				tests.add(test.getClass());
			} catch (CoreException e) {
				throw new IllegalArgumentException(e);
			}
		}
		return tests.toArray(new Class<?>[tests.size()]);
	}
}
