package org.rubypeople.rdt.core;

import org.eclipse.core.runtime.IPath;

public interface ILoadpathContainer {
	
	/**
	 * Kind for a container mapping to an application library
	 */
	int K_APPLICATION = 1;

	/**
	 * Kind for a container mapping to a system library
	 */
	int K_SYSTEM = 2;

	/**
	 * Kind for a container mapping to a default system library, implicitly contributed by the runtime
	 */
	int K_DEFAULT_SYSTEM = 3;

	ILoadpathEntry[] getLoadpathEntries();

	/**
	 * Answers a readable description of this container
	 * 
	 * @return String - a string description of the container
	 */	
    String getDescription();

	/**
	 * Answers the kind of this container. Can be either:
	 * <ul>
	 * <li><code>K_APPLICATION</code> if this container maps to an application library</li>
	 * <li><code>K_SYSTEM</code> if this container maps to a system library</li>
	 * <li><code>K_DEFAULT_SYSTEM</code> if this container maps to a default system library (library
	 * 	implicitly contributed by the runtime).</li>
	 * </ul>
	 * Typically, system containers should be placed first on a build path.
	 * @return the kind of this container
	 */	
    int getKind();

	/**
	 * Answers the container path identifying this container.
	 * A container path is formed by a first ID segment followed with extra segments, which 
	 * can be used as additional hints for resolving to this container.
	 * <p>
	 * The container ID is also used to identify a<code>ClasspathContainerInitializer</code>
	 * registered on the extension point "org.eclipse.jdt.core.classpathContainerInitializer", which can
	 * be invoked if needing to resolve the container before it is explicitly set.
	 * <p>
	 * @return IPath - the container path that is associated with this container
	 */	
    IPath getPath();

}
