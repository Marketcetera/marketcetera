package org.marketcetera.photon.scripting;

import junit.framework.TestCase;

import org.eclipse.core.runtime.Path;

public class ClasspathTest extends TestCase {
	private static final String PATH_SEPARATOR = System.getProperty("path.separator");

	public void testToString() throws Exception {
		Classpath cp = new Classpath();
		cp.add(Path.fromOSString("/foo/bar"));
		cp.add(Path.fromOSString("/baz/quux"));
		assertEquals("/foo/bar"+PATH_SEPARATOR+"/baz/quux", cp.toString());
	}
	
	public void testAddString() throws Exception {
		Classpath cp = new Classpath();
		cp.add("/foo/bar");
		cp.add("/baz/quux");
		Object[] arr = cp.toArray();
		assertEquals(arr[0], Path.fromOSString("/foo/bar"));
		assertEquals(arr[1], Path.fromOSString("/baz/quux"));
	}
}
