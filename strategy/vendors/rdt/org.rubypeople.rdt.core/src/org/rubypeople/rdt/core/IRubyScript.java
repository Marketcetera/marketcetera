/*
 * Author: C.Williams
 * 
 * Copyright (c) 2004 RubyPeople.
 * 
 * This file is part of the Ruby Development Tools (RDT) plugin for eclipse. You
 * can get copy of the GPL along with further information about RubyPeople and
 * third party software bundled with RDT in the file
 * org.rubypeople.rdt.core_x.x.x/RDT.license or otherwise at
 * http://www.rubypeople.org/RDT.license.
 * 
 * RDT is free software; you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * RDT is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * RDT; if not, write to the Free Software Foundation, Inc., 59 Temple Place,
 * Suite 330, Boston, MA 02111-1307 USA
 */
package org.rubypeople.rdt.core;

import org.eclipse.core.runtime.IProgressMonitor;
import org.jruby.ast.RootNode;

/**
 * @author Chris
 * 
 */
public interface IRubyScript extends IRubyElement, ISourceReference, IParent, IOpenable, ICodeAssist {

	/**
	 * @throws RubyModelException
	 * 
	 */
	void reconcile() throws RubyModelException;

	/**
	 * Reconciles the contents of this working copy, sends out a Ruby delta
	 * notification indicating the nature of the change of the working copy
	 * since the last time it was either reconciled or made consistent (see
	 * <code>IOpenable#makeConsistent()</code>), and returns a compilation
	 * unit AST if requested.
	 * <p>
	 * It performs the reconciliation by locally caching the contents of the
	 * working copy, updating the contents, then creating a delta over the
	 * cached contents and the new contents, and finally firing this delta.
	 * <p>
	 * The boolean argument allows to force problem detection even if the
	 * working copy is already consistent.
	 * </p>
	 * <p>
	 * This functionality allows to specify a working copy owner which is used
	 * during problem detection. All references contained in the working copy
	 * are resolved against other units; for which corresponding owned working
	 * copies are going to take precedence over their original compilation
	 * units. If <code>null</code> is passed in, then the primary working copy
	 * owner is used.
	 * </p>
	 * <p>
	 * Compilation problems found in the new contents are notified through the
	 * <code>IProblemRequestor</code> interface which was passed at creation,
	 * and no longer as transient markers.
	 * </p>
	 * <p>
	 * Note: Since 3.0, added/removed/changed inner types generate change
	 * deltas.
	 * </p>
	 * 
	 * @param owner
	 *            the owner of working copies that take precedence over the
	 *            original compilation units, or <code>null</code> if the
	 *            primary working copy owner should be used
	 * @param monitor
	 *            a progress monitor
	 * @throws RubyModelException
	 *             if the contents of the original element cannot be accessed.
	 *             Reasons include:
	 *             <ul>
	 *             <li>The original Ruby element does not exist
	 *             (ELEMENT_DOES_NOT_EXIST)</li>
	 *             </ul>
	 * @since 3.0
	 */
	RootNode reconcile(boolean forceProblemDetection, WorkingCopyOwner owner, IProgressMonitor monitor) throws RubyModelException;

	/**
	 * Returns the top-level type declared in this compilation unit with the
	 * given simple type name. The type name has to be a valid compilation unit
	 * name. This is a handle-only method. The type may or may not exist.
	 * 
	 * @param name
	 *            the simple name of the requested type in the compilation unit
	 * @return a handle onto the corresponding type. The type may or may not
	 *         exist.
	 */
	IType getType(String name);

	/**
	 * Returns the primary ruby script (whose owner is the primary owner)
	 * this working copy was created from, or this ruby script if this a
	 * primary ruby script.
	 * <p>
	 * Note that the returned primary ruby script can be in working copy
	 * mode.
	 * </p>
	 * 
	 * @return the primary ruby script this working copy was created from,
	 *         or this ruby script if it is primary
	 * @since 0.8.0
	 */
	IRubyScript getPrimary();

	/**
	 * Returns whether this element is a working copy.
	 * 
	 * @return true if this element is a working copy, false otherwise
	 * @since 0.8.0
	 */
	boolean isWorkingCopy();

	/**
	 * Returns a new working copy of this compilation unit if it is a primary
	 * compilation unit, or this compilation unit if it is already a non-primary
	 * working copy.
	 * <p>
	 * Note: if intending to share a working copy amongst several clients, then
	 * <code>#getWorkingCopy(WorkingCopyOwner, IProblemRequestor, IProgressMonitor)</code>
	 * should be used instead.
	 * </p>
	 * <p>
	 * When the working copy instance is created, an ADDED IRubyElementDelta is
	 * reported on this working copy.
	 * </p>
	 * <p>
	 * Once done with the working copy, users of this method must discard it
	 * using <code>discardWorkingCopy()</code>.
	 * </p>
	 * <p>
	 * Since 2.1, a working copy can be created on a not-yet existing
	 * compilation unit. In particular, such a working copy can then be
	 * committed in order to create the corresponding compilation unit.
	 * </p>
	 * 
	 * @param monitor
	 *            a progress monitor used to report progress while opening this
	 *            compilation unit or <code>null</code> if no progress should
	 *            be reported
	 * @throws RubyModelException
	 *             if the contents of this element can not be determined.
	 * @return a new working copy of this element if this element is not a
	 *         working copy, or this element if this element is already a
	 *         working copy
	 * @since 0.8.0
	 */
	IRubyScript getWorkingCopy(IProgressMonitor monitor) throws RubyModelException;

	/**
	 * Returns a shared working copy on this compilation unit using the given
	 * working copy owner to create the buffer, or this compilation unit if it
	 * is already a non-primary working copy. This API can only answer an
	 * already existing working copy if it is based on the same original
	 * compilation unit AND was using the same working copy owner (that is, as
	 * defined by <code>Object.equals</code>).
	 * <p>
	 * The life time of a shared working copy is as follows:
	 * <ul>
	 * <li>The first call to
	 * <code>getWorkingCopy(WorkingCopyOwner, IProblemRequestor, IProgressMonitor)</code>
	 * creates a new working copy for this element</li>
	 * <li>Subsequent calls increment an internal counter.</li>
	 * <li>A call to <code>discardWorkingCopy()</code> decrements the
	 * internal counter.</li>
	 * <li>When this counter is 0, the working copy is discarded.
	 * </ul>
	 * So users of this method must discard exactly once the working copy.
	 * <p>
	 * Note that the working copy owner will be used for the life time of this
	 * working copy, that is if the working copy is closed then reopened, this
	 * owner will be used. The buffer will be automatically initialized with the
	 * original's compilation unit content upon creation.
	 * <p>
	 * When the shared working copy instance is created, an ADDED
	 * IRubyElementDelta is reported on this working copy.
	 * </p>
	 * <p>
	 * Since 2.1, a working copy can be created on a not-yet existing
	 * compilation unit. In particular, such a working copy can then be
	 * committed in order to create the corresponding compilation unit.
	 * </p>
	 * 
	 * @param owner
	 *            the working copy owner that creates a buffer that is used to
	 *            get the content of the working copy
	 * @param monitor
	 *            a progress monitor used to report progress while opening this
	 *            compilation unit or <code>null</code> if no progress should
	 *            be reported
	 * @throws RubyModelException
	 *             if the contents of this element can not be determined.
	 * @return a new working copy of this element using the given factory to
	 *         create the buffer, or this element if this element is already a
	 *         working copy
	 * @since 3.0
	 */
	IRubyScript getWorkingCopy(WorkingCopyOwner owner, IProblemRequestor requestor, IProgressMonitor monitor) throws RubyModelException;
	
	/**
	 * Returns whether the resource of this working copy has changed since the
	 * inception of this working copy.
	 * Returns <code>false</code> if this compilation unit is not in working copy mode.
	 * 
	 * @return whether the resource has changed
	 * @since 3.0
	 */
	public boolean hasResourceChanged();
	
	/**
	 * Changes this compilation unit handle into a working copy. A new
	 * <code>IBuffer</code> is created using this compilation unit handle's
	 * owner. Uses the primary owner is none was specified when this compilation
	 * unit handle was created.
	 * <p>
	 * Once in working copy mode, changes to this compilation unit or its
	 * children are done in memory. Only the new buffer is affected. Using
	 * <code>commitWorkingCopy(boolean, IProgressMonitor)</code> will bring
	 * the underlying resource in sync with this compilation unit.
	 * </p>
	 * <p>
	 * If this compilation unit was already in working copy mode, an internal
	 * counter is incremented and no other action is taken on this compilation
	 * unit. To bring this compilation unit back into the original mode (where
	 * it reflects the underlying resource), <code>discardWorkingCopy</code>
	 * must be call as many times as <code>becomeWorkingCopy</code>.
	 * </p>
	 * 
	 * @param requestor
	 * 
	 * @param monitor
	 *            a progress monitor used to report progress while opening this
	 *            compilation unit or <code>null</code> if no progress should
	 *            be reported
	 * @throws RubyModelException
	 *             if this compilation unit could not become a working copy.
	 * @see #discardWorkingCopy()
	 * @since 3.0
	 */
	void becomeWorkingCopy(IProblemRequestor requestor, IProgressMonitor monitor) throws RubyModelException;

	/**
	 * Commits the contents of this working copy to its underlying resource.
	 *
	 * <p>It is possible that the contents of the original resource have changed
	 * since this working copy was created, in which case there is an update conflict.
	 * The value of the <code>force</code> parameter effects the resolution of
	 * such a conflict:<ul>
	 * <li> <code>true</code> - in this case the contents of this working copy are applied to
	 * 	the underlying resource even though this working copy was created before
	 *		a subsequent change in the resource</li>
	 * <li> <code>false</code> - in this case a {@link JavaModelException} is thrown</li>
	 * </ul>
	 * <p>
	 * Since 2.1, a working copy can be created on a not-yet existing compilation
	 * unit. In particular, such a working copy can then be committed in order to create
	 * the corresponding compilation unit.
	 * </p>
	 * @param force a flag to handle the cases when the contents of the original resource have changed
	 * since this working copy was created
	 * @param monitor the given progress monitor
	 * @throws JavaModelException if this working copy could not commit. Reasons include:
	 * <ul>
	 * <li> A {@link org.eclipse.core.runtime.CoreException} occurred while updating an underlying resource
	 * <li> This element is not a working copy (INVALID_ELEMENT_TYPES)
	 * <li> A update conflict (described above) (UPDATE_CONFLICT)
	 * </ul>
	 * @since 3.0
	 */
	void commitWorkingCopy(boolean force, IProgressMonitor monitor) throws RubyModelException;
	
	/**
	 * Changes this compilation unit in working copy mode back to its original
	 * mode.
	 * <p>
	 * This has no effect if this compilation unit was not in working copy mode.
	 * </p>
	 * <p>
	 * If <code>becomeWorkingCopy</code> was called several times on this
	 * compilation unit, <code>discardWorkingCopy</code> must be called as
	 * many times before it switches back to the original mode.
	 * </p>
	 * 
	 * @throws RubyModelException
	 *             if this working copy could not return in its original mode.
	 * @see #becomeWorkingCopy(IProblemRequestor, IProgressMonitor)
	 * @since 3.0
	 */
	void discardWorkingCopy() throws RubyModelException;

	/**
	 * Returns the first import declaration in this compilation unit with the
	 * given name. This is a handle-only method. The import declaration may or
	 * may not exist. This is a convenience method - imports can also be
	 * accessed from a compilation unit's import container.
	 * 
	 * @param name
	 *            the name of the import to find as defined by JLS2 7.5. (For
	 *            example: <code>"java.io.File"</code> or
	 *            <code>"java.awt.*"</code>)
	 * @return a handle onto the corresponding import declaration. The import
	 *         declaration may or may not exist.
	 */
	IImportDeclaration getImport(String name);

	/**
	 * Returns the import container for this compilation unit. This is a
	 * handle-only method. The import container may or may not exist. The import
	 * container can used to access the imports.
	 * 
	 * @return a handle onto the corresponding import container. The import
	 *         contain may or may not exist.
	 */
	IImportContainer getImportContainer();

	/**
	 * Returns the import declarations in this compilation unit in the order in
	 * which they appear in the source. This is a convenience method - import
	 * declarations can also be accessed from a compilation unit's import
	 * container.
	 * 
	 * @return the import declarations in this compilation unit
	 * @throws JavaModelException
	 *             if this element does not exist or if an exception occurs
	 *             while accessing its corresponding resource
	 */
	IImportDeclaration[] getImports() throws RubyModelException;

	/**
	 * Returns the working copy owner of this working copy. Returns null if it
	 * is not a working copy or if it has no owner.
	 * 
	 * @return WorkingCopyOwner the owner of this working copy or
	 *         <code>null</code>
	 * @since 3.0
	 */
	WorkingCopyOwner getOwner();

	/**
	 * Returns the top-level types declared in this compilation unit
	 * in the order in which they appear in the source.
	 *
	 * @return the top-level types declared in this compilation unit
	 * @throws RubyModelException if this element does not exist or if an
	 *		exception occurs while accessing its corresponding resource
	 */
	IType[] getTypes() throws RubyModelException;

    /**
     * Returns the smallest element within this compilation unit that 
     * includes the given source position (that is, a method, field, etc.), or
     * <code>null</code> if there is no element other than the compilation
     * unit itself at the given position, or if the given position is not
     * within the source range of this compilation unit.
     *
     * @param position a source position inside the compilation unit
     * @return the innermost Ruby element enclosing a given source position or <code>null</code>
     *  if none (excluding the compilation unit).
     * @throws RubyModelException if the compilation unit does not exist or if an
     *      exception occurs while accessing its corresponding resource
     */
    IRubyElement getElementAt(int position) throws RubyModelException;

	IType findPrimaryType();

	IType[] getAllTypes() throws RubyModelException;
}