package org.marketcetera.photon.scripting;

import junit.framework.TestCase;

import org.eclipse.core.runtime.Path;

public class ClasspathTest extends TestCase {
	private static final String PATH_SEPARATOR = System.getProperty("path.separator");
	private static final String FILE_SEPARATOR = System.getProperty("file.separator");

	public void testToString() throws Exception {
		Classpath cp = new Classpath();
		cp.add(Path.fromOSString("/foo/bar"));
		cp.add(Path.fromOSString("/baz/quux"));
		assertEquals(
				FILE_SEPARATOR
				+"foo"
				+FILE_SEPARATOR
				+"bar"
				+PATH_SEPARATOR
				+FILE_SEPARATOR
				+"baz"
				+FILE_SEPARATOR
				+"quux",
				cp.toString());
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
