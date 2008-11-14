package org.rubypeople.rdt.core.search;

import org.eclipse.core.runtime.IPath;
import org.rubypeople.rdt.core.IRubyElement;

public interface IRubySearchScope {

	/**
	 * Include type constant (bit mask) indicating that source folders should be
	 * considered in the search scope.
	 * 
	 * @since 0.9.0
	 */
	int SOURCES = 1;
	/**
	 * Include type constant (bit mask) indicating that application libraries
	 * should be considered in the search scope.
	 * 
	 * @since 0.9.0
	 */
	int APPLICATION_LIBRARIES = 2;
	/**
	 * Include type constant (bit mask) indicating that system libraries should
	 * be considered in the search scope.
	 * 
	 * @since 0.9.0
	 */
	int SYSTEM_LIBRARIES = 4;
	/**
	 * Include type constant (bit mask) indicating that referenced projects
	 * should be considered in the search scope.
	 * 
	 * @since 0.9.0
	 */
	int REFERENCED_PROJECTS = 8;

	/**
	 * Returns the paths to the enclosing projects and JARs for this search
	 * scope.
	 * <ul>
	 * <li> If the path is a project path, this is the full path of the project
	 * (see <code>IResource.getFullPath()</code>). For example, /MyProject
	 * </li>
	 * <li> If the path is a JAR path and this JAR is internal to the workspace,
	 * this is the full path of the JAR file (see
	 * <code>IResource.getFullPath()</code>). For example,
	 * /MyProject/mylib.jar </li>
	 * <li> If the path is a JAR path and this JAR is external to the workspace,
	 * this is the full OS path to the JAR file on the file system. For example,
	 * d:\libs\mylib.jar </li>
	 * </ul>
	 * 
	 * @return an array of paths to the enclosing projects and JARS.
	 */
	IPath[] enclosingProjectsAndJars();

	/**
	 * Checks whether the resource at the given path is enclosed by this scope.
	 * 
	 * @param resourcePath
	 *            if the resource is contained in a JAR file, the path is
	 *            composed of 2 paths separated by
	 *            <code>JAR_FILE_ENTRY_SEPARATOR</code>: the first path is
	 *            the full OS path to the JAR (if it is an external JAR), or the
	 *            workspace relative <code>IPath</code> to the JAR (if it is
	 *            an internal JAR), the second path is the path to the resource
	 *            inside the JAR.
	 * @return whether the resource is enclosed by this scope
	 */
	public boolean encloses(String resourcePath);

	boolean encloses(IRubyElement element);

}
