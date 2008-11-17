package org.rubypeople.rdt.core;

import org.eclipse.core.runtime.IProgressMonitor;

public interface ISourceFolderRoot extends IParent, IRubyElement, IOpenable {

	/**
	 * Empty root path
	 */
	String DEFAULT_PACKAGEROOT_PATH = ""; //$NON-NLS-1$
	
	/**
	 * Returns whether this package fragment root is external
	 * to the workbench (that is, a local file), and has no
	 * underlying resource.
	 * <p>
	 * This is a handle-only method.
	 * </p>
	 * 
	 * @return true if this package fragment root is external
	 * to the workbench (that is, a local file), and has no
	 * underlying resource, false otherwise
	 */
	boolean isExternal();
		
	/**
	 * Returns the package fragment with the given package name.
	 * An empty string indicates the default package.
	 * This is a handle-only operation.  The package fragment
	 * may or may not exist.
	 * 
	 * @param packageName the given package name
	 * @return the package fragment with the given package name
	 */
	ISourceFolder getSourceFolder(String names[]);
		
	/**
	 * Creates and returns a package fragment in this root with the 
	 * given dot-separated package name.  An empty string specifies the default package. 
	 * This has the side effect of creating all package
	 * fragments that are a prefix of the new package fragment which
	 * do not exist yet. If the package fragment already exists, this
	 * has no effect.
	 *
	 * For a description of the <code>force</code> flag, see <code>IFolder.create</code>.
	 *
	 * @param name the given dot-separated package name
	 * @param force a flag controlling how to deal with resources that
	 *    are not in sync with the local file system
	 * @param monitor the given progress monitor
	 * @exception JavaModelException if the element could not be created. Reasons include:
	 * <ul>
	 * <li> This Java element does not exist (ELEMENT_DOES_NOT_EXIST)</li>
	 * <li> A <code>CoreException</code> occurred while creating an underlying resource
	 * <li> This package fragment root is read only (READ_ONLY)
	 * <li> The name is not a valid package name (INVALID_NAME)
	 * </ul>
	 * @return a package fragment in this root with the given dot-separated package name
	 * @see org.eclipse.core.resources.IFolder#create(boolean, boolean, IProgressMonitor)
	 */
	ISourceFolder createSourceFolder(
		String name,
		boolean force,
		IProgressMonitor monitor)
		throws RubyModelException;
	
	/**
	 * Deletes the resource of this package fragment root as specified by
	 * <code>IResource.delete(int, IProgressMonitor)</code> but excluding nested
	 * source folders.
	 * <p>
	 * If <code>NO_RESOURCE_MODIFICATION</code> is specified in 
	 * <code>updateModelFlags</code> or if this package fragment root is external, 
	 * this operation doesn't delete the resource. <code>updateResourceFlags</code> 
	 * is then ignored.
	 * </p><p>
	 * If <code>ORIGINATING_PROJECT_CLASSPATH</code> is specified in 
	 * <code>updateModelFlags</code>, update the raw classpath of this package 
	 * fragment root's project by removing the corresponding classpath entry.
	 * </p><p>
	 * If <code>OTHER_REFERRING_PROJECTS_CLASSPATH</code> is specified in 
	 * <code>updateModelFlags</code>, update the raw classpaths of all other Java
	 * projects referring to this root's resource by removing the corresponding classpath 
	 * entries.
	 * </p><p>
	 * If no flags is specified in <code>updateModelFlags</code> (using 
	 * <code>IResource.NONE</code>), the default behavior applies: the
	 * resource is deleted (if this package fragment root is not external) and no
	 * classpaths are updated.
	 * </p>
	 * 
	 * @param updateResourceFlags bit-wise or of update resource flag constants
	 *   (<code>IResource.FORCE</code> and <code>IResource.KEEP_HISTORY</code>)
	 * @param updateModelFlags bit-wise or of update resource flag constants
	 *   (<code>ORIGINATING_PROJECT_CLASSPATH</code>,
	 *   <code>OTHER_REFERRING_PROJECTS_CLASSPATH</code> and 
	 *   <code>NO_RESOURCE_MODIFICATION</code>)
	 * @param monitor a progress monitor
	 * 
	 * @exception JavaModelException if this root could not be deleted. Reasons
	 * include:
	 * <ul>
	 * <li> This root does not exist (ELEMENT_DOES_NOT_EXIST)</li>
	 * <li> A <code>CoreException</code> occurred while deleting the resource 
	 * or updating a classpath
	 * </li>
	 * </ul>
	 * @see org.eclipse.core.resources.IResource#delete(boolean, IProgressMonitor)
	 * @since 2.1
	 */
	void delete(int updateResourceFlags, int updateModelFlags, IProgressMonitor monitor) throws RubyModelException;

	boolean isArchive();

	Object[] getNonRubyResources() throws RubyModelException;

	ISourceFolder getSourceFolder(String packName);

	/**
	 * Returns the first raw loadpath entry that corresponds to this package
	 * fragment root.
	 * A raw loadpath entry corresponds to a package fragment root if once resolved
	 * this entry's path is equal to the root's path. 
	 * 
	 * @exception RubyModelException if this element does not exist or if an
	 *		exception occurs while accessing its corresponding resource.
	 * @return the first raw classpath entry that corresponds to this package fragment root
	 * @since 1.0.0
	 */
	ILoadpathEntry getRawLoadpathEntry() throws RubyModelException;
	
}
