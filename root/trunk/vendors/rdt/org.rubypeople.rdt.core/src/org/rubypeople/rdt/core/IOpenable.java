/*
 * Created on Jan 13, 2005
 *
 */
package org.rubypeople.rdt.core;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * @author cawilliams
 * 
 */
public interface IOpenable {

	/**
	 * Closes this element and its buffer (if any). Closing an element which is
	 * not open has no effect.
	 * 
	 * <p>
	 * Note: although <code>close</code> is exposed in the API, clients are
	 * not expected to open and close elements - the Ruby model does this
	 * automatically as elements are accessed.
	 * 
	 * @exception RubyModelException
	 *                if an error occurs closing this element
	 */
	public void close() throws RubyModelException;

	/**
	 * Returns the buffer opened for this element, or <code>null</code> if
	 * this element does not have a buffer.
	 * 
	 * @exception RubyModelException
	 *                if this element does not exist or if an exception occurs
	 *                while accessing its corresponding resource.
	 * @return the buffer opened for this element, or <code>null</code> if
	 *         this element does not have a buffer
	 */
	public IBuffer getBuffer() throws RubyModelException;

	/**
	 * Returns <code>true</code> if this element is open and:
	 * <ul>
	 * <li>its buffer has unsaved changes, or
	 * <li>one of its descendants has unsaved changes, or
	 * <li>a working copy has been created on one of this element's children
	 * and has not yet destroyed
	 * </ul>
	 * 
	 * @exception RubyModelException
	 *                if this element does not exist or if an exception occurs
	 *                while accessing its corresponding resource.
	 * @return <code>true</code> if this element is open and:
	 *         <ul>
	 *         <li>its buffer has unsaved changes, or
	 *         <li>one of its descendants has unsaved changes, or
	 *         <li>a working copy has been created on one of this element's
	 *         children and has not yet destroyed
	 *         </ul>
	 */
	boolean hasUnsavedChanges() throws RubyModelException;

	/**
	 * Returns whether the element is consistent with its underlying resource or
	 * buffer. The element is consistent when opened, and is consistent if the
	 * underlying resource or buffer has not been modified since it was last
	 * consistent.
	 * 
	 * <p>
	 * NOTE: Child consistency is not considered. For example, a package
	 * fragment responds <code>true</code> when it knows about all of its
	 * compilation units present in its underlying folder. However, one or more
	 * of the compilation units could be inconsistent.
	 * 
	 * @exception RubyModelException
	 *                if this element does not exist or if an exception occurs
	 *                while accessing its corresponding resource.
	 * @return true if the element is consistent with its underlying resource or
	 *         buffer, false otherwise.
	 * @see IOpenable#makeConsistent(IProgressMonitor)
	 */
	boolean isConsistent() throws RubyModelException;

	/**
	 * Returns whether this openable is open. This is a handle-only method.
	 * 
	 * @return true if this openable is open, false otherwise
	 */
	boolean isOpen();

	/**
	 * Makes this element consistent with its underlying resource or buffer by
	 * updating the element's structure and properties as necessary.
	 * <p>
	 * Note: Using this functionality on a working copy will interfere with any
	 * subsequent reconciling operation. Indeed, the next
	 * <code>ICompilationUnit#reconcile()</code> operation will not account
	 * for changes which occurred before an explicit use of
	 * <code>#makeConsistent(IProgressMonitor)</code>
	 * <p>
	 * 
	 * @param progress
	 *            the given progress monitor
	 * @exception RubyModelException
	 *                if the element is unable to access the contents of its
	 *                underlying resource. Reasons include:
	 *                <ul>
	 *                <li>This Ruby element does not exist
	 *                (ELEMENT_DOES_NOT_EXIST)</li>
	 *                </ul>
	 * @see IOpenable#isConsistent()
	 * @see ICompilationUnit#reconcile(int, boolean, WorkingCopyOwner,
	 *      IProgressMonitor)
	 */
	void makeConsistent(IProgressMonitor progress) throws RubyModelException;

	/**
	 * Opens this element and all parent elements that are not already open. For
	 * compilation units, a buffer is opened on the contents of the underlying
	 * resource.
	 * 
	 * <p>
	 * Note: although <code>open</code> is exposed in the API, clients are not
	 * expected to open and close elements - the Ruby model does this
	 * automatically as elements are accessed.
	 * 
	 * @param progress
	 *            the given progress monitor
	 * @exception RubyModelException
	 *                if an error occurs accessing the contents of its
	 *                underlying resource. Reasons include:
	 *                <ul>
	 *                <li>This Ruby element does not exist
	 *                (ELEMENT_DOES_NOT_EXIST)</li>
	 *                </ul>
	 */
	public void open(IProgressMonitor progress) throws RubyModelException;

	/**
	 * Saves any changes in this element's buffer to its underlying resource via
	 * a workspace resource operation. This has no effect if the element has no
	 * underlying buffer, or if there are no unsaved changed in the buffer.
	 * <p>
	 * The <code>force</code> parameter controls how this method deals with
	 * cases where the workbench is not completely in sync with the local file
	 * system. If <code>false</code> is specified, this method will only
	 * attempt to overwrite a corresponding file in the local file system
	 * provided it is in sync with the workbench. This option ensures there is
	 * no unintended data loss; it is the recommended setting. However, if
	 * <code>true</code> is specified, an attempt will be made to write a
	 * corresponding file in the local file system, overwriting any existing one
	 * if need be. In either case, if this method succeeds, the resource will be
	 * marked as being local (even if it wasn't before).
	 * <p>
	 * As a result of this operation, the element is consistent with its
	 * underlying resource or buffer.
	 * 
	 * @param progress
	 *            the given progress monitor
	 * @param force
	 *            it controls how this method deals with cases where the
	 *            workbench is not completely in sync with the local file system
	 * @exception RubyModelException
	 *                if an error occurs accessing the contents of its
	 *                underlying resource. Reasons include:
	 *                <ul>
	 *                <li>This Ruby element does not exist
	 *                (ELEMENT_DOES_NOT_EXIST)</li>
	 *                <li>This Ruby element is read-only (READ_ONLY)</li>
	 *                </ul>
	 */
	public void save(IProgressMonitor progress, boolean force) throws RubyModelException;

	/**
	 * Finds and returns the recommended line separator for this element.
	 * The element's buffer is first searched and the first line separator in this buffer is returned if any.
	 * Otherwise the preference {@link org.eclipse.core.runtime.Platform#PREF_LINE_SEPARATOR} 
	 * on this element's project or workspace is returned.
	 * Finally if no such preference is set, the system line separator is returned.
	 * 
	 * @return the recommended line separator for this element
	 * @exception RubyModelException if this element does not exist or if an
	 *		exception occurs while accessing its corresponding resource.
	 * @since 0.9.0
	 */
	public String findRecommendedLineSeparator() throws RubyModelException;
}
