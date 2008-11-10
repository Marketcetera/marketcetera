package org.rubypeople.rdt.core;

import org.eclipse.core.runtime.IProgressMonitor;


public interface ISourceFolder extends IRubyElement, IParent, IOpenable  {
	
	/**	
	 * <p>
	 * The name of package fragment for the default package (value: the empty 
	 * string, <code>""</code>).
	 * </p>
 	*/
	public static final String DEFAULT_PACKAGE_NAME = ""; //$NON-NLS-1$
	
	/**
	 * Returns whether this fragment contains at least one Ruby resource.
	 * @return true if this fragment contains at least one Ruby resource, false otherwise
	 * @exception RubyModelException if this element does not exist or if an
	 *		exception occurs while accessing its corresponding resource.
	 */
	boolean containsRubyResources() throws RubyModelException;
	/**
	 * Creates and returns a compilation unit in this package fragment 
	 * with the specified name and contents. No verification is performed
	 * on the contents.
	 *
	 * <p>It is possible that a compilation unit with the same name already exists in this 
	 * package fragment.
	 * The value of the <code>force</code> parameter effects the resolution of
	 * such a conflict:<ul>
	 * <li> <code>true</code> - in this case the compilation is created with the new contents</li>
	 * <li> <code>false</code> - in this case a <code>RubyModelException</code> is thrown</li>
	 * </ul>
	 *
	 * @param contents the given contents
	 * @param force specify how to handle conflict is the same name already exists
	 * @param monitor the given progress monitor
	 * @param name the given name
	 * @exception RubyModelException if the element could not be created. Reasons include:
	 * <ul>
	 * <li> This Ruby element does not exist (ELEMENT_DOES_NOT_EXIST)</li>
	 * <li> A <code>CoreException</code> occurred while creating an underlying resource
	 * <li> The name is not a valid compilation unit name (INVALID_NAME)
	 * <li> The contents are <code>null</code> (INVALID_CONTENTS)
	 * </ul>
	 * @return a compilation unit in this package fragment 
	 * with the specified name and contents
	 */
	IRubyScript createRubyScript(String name, String contents, boolean force, IProgressMonitor monitor) throws RubyModelException;
	/**
	 * Returns the compilation unit with the specified name
	 * in this package (for example, <code>"Object.java"</code>).
	 * The name has to be a valid compilation unit name.
	 * This is a handle-only method.  The compilation unit may or may not be present.
	 * 
	 * @param name the given name
	 * @return the compilation unit with the specified name in this package
	 * @see RubyConventions#validateCompilationUnitName(String)
	 */
//	IRubyScript getRubyScript(String name);
	/**
	 * Returns all of the compilation units in this package fragment.
	 *
	 * <p>Note: it is possible that a package fragment contains only
	 * class files (in other words, its kind is <code>K_BINARY</code>), in which
	 * case this method returns an empty collection.
	 * </p>
	 *
	 * @exception RubyModelException if this element does not exist or if an
	 *		exception occurs while accessing its corresponding resource.
	 * @return all of the compilation units in this package fragment
	 */
	IRubyScript[] getRubyScripts() throws RubyModelException;
	/**
	 * Returns all of the compilation units in this package fragment that are 
	 * in working copy mode and that have the given owner.
	 * <p>
	 * Only existing working copies are returned. So a compilation unit handle that has no 
	 * corresponding resource on disk will be included if and only if is in working copy mode.
	 * </p>
	 * <p>Note: it is possible that a package fragment contains only
	 * class files (in other words, its kind is <code>K_BINARY</code>), in which
	 * case this method returns an empty collection.
	 * </p>
	 *
	 * @param owner the owner of the returned compilation units
	 * @exception RubyModelException if this element does not exist or if an
	 *		exception occurs while accessing its corresponding resource.
	 * @return all of the compilation units in this package fragment
	 * @since 3.0
	 */
	IRubyScript[] getRubyScripts(WorkingCopyOwner owner) throws RubyModelException;
	/**
	 * Returns the dot-separated package name of this fragment, for example
	 * <code>"java.lang"</code>, or <code>""</code> (the empty string),
	 * for the default package.
	 * 
	 * @return the dot-separated package name of this fragment
	 */
	String getElementName();
	
	/**
	 * Returns an array of non-Ruby resources contained in this package fragment.
	 * <p>
	 * Non-Ruby resources includes other files and folders located in the same
	 * directory as the compilation units or class files for this package 
	 * fragment. Source files excluded from this package by virtue of 
	 * inclusion/exclusion patterns on the corresponding source classpath entry
	 * are considered non-Ruby resources and will appear in the result
	 * (possibly in a folder).
	 * </p>
	 * 
	 * @exception RubyModelException if this element does not exist or if an
	 *		exception occurs while accessing its corresponding resource.
	 * @return an array of non-Ruby resources (<code>IFile</code>s, 
	 *              <code>IFolder</code>s, or <code>IStorage</code>s if the
	 *              package fragment is in an archive) contained in this package 
	 *              fragment
	 * @see IClasspathEntry#getInclusionPatterns()
	 * @see IClasspathEntry#getExclusionPatterns()
	 */
	Object[] getNonRubyResources() throws RubyModelException;
	
	IRubyScript getRubyScript(String name);
	
	boolean isDefaultPackage();
	
	/**
	 * Returns whether this source folder's name is
	 * a prefix of other source folders in this source folder's
	 * root.
	 *
	 * @exception RubyModelException if this element does not exist or if an
	 *		exception occurs while accessing its corresponding resource.
	 * @return true if this source folder's name is a prefix of other source fragments in this source folder's root, false otherwise
	 */
	boolean hasSubfolders() throws RubyModelException;

}
