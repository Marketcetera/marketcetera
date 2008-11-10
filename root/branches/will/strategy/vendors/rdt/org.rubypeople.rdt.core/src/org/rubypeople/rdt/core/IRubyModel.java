/*
 * Created on Jan 13, 2005
 *
 */
package org.rubypeople.rdt.core;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * @author cawilliams
 * 
 */
public interface IRubyModel extends IParent, IRubyElement, IOpenable {

	/**
	 * Returns the Ruby project with the given name. This is a handle-only
	 * method. The project may or may not exist.
	 * 
	 * @param name
	 *            the name of the Ruby project
	 * @return the Ruby project with the given name
	 */
	IRubyProject getRubyProject(String name);

	/**
	 * Returns the Ruby projects in this Ruby model, or an empty array if there
	 * are none.
	 * 
	 * @return the Ruby projects in this Ruby model, or an empty array if there
	 *         are none
	 * @exception RubyModelException
	 *                if this request fails.
	 */
	IRubyProject[] getRubyProjects() throws RubyModelException;

	/**
	 * Returns an array of non-Ruby resources (that is, non-Ruby projects) in
	 * the workspace.
	 * <p>
	 * Non-Ruby projects include all projects that are closed (even if they have
	 * the Ruby nature).
	 * </p>
	 * 
	 * @return an array of non-Ruby projects (<code>IProject</code>s)
	 *         contained in the workspace.
	 * @throws RubyModelException
	 *             if this element does not exist or if an exception occurs
	 *             while accessing its corresponding resource
	 * @since 2.1
	 */
	Object[] getNonRubyResources() throws RubyModelException;

	/**
	 * Returns the workspace associated with this Ruby model.
	 * 
	 * @return the workspace associated with this Ruby model
	 */
	IWorkspace getWorkspace();

	/**
	 * Returns whether this Ruby model contains an <code>IRubyElement</code>
	 * whose resource is the given resource or a non-Ruby resource which is the
	 * given resource.
	 * <p>
	 * Note: no existency check is performed on the argument resource. If it is
	 * not accessible (see <code>IResource.isAccessible()</code>) yet but
	 * would be located in Ruby model range, then it will return
	 * <code>true</code>.
	 * </p>
	 * <p>
	 * If the resource is accessible, it can be reached by navigating the Ruby
	 * model down using the <code>getChildren()</code> and/or
	 * <code>getNonRubyResources()</code> methods.
	 * </p>
	 * 
	 * @param resource
	 *            the resource to check
	 * @return true if the resource is accessible through the Ruby model
	 * @since 2.1
	 */
	boolean contains(IResource resource);

	void refreshExternalArchives(IRubyElement[] elements, IProgressMonitor monitor) throws RubyModelException;
}
