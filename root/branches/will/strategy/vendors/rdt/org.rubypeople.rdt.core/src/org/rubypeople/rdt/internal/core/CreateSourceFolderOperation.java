/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.rubypeople.rdt.internal.core;

import java.util.ArrayList;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Path;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IRubyModelStatus;
import org.rubypeople.rdt.core.IRubyModelStatusConstants;
import org.rubypeople.rdt.core.IRubyProject;
import org.rubypeople.rdt.core.ISourceFolder;
import org.rubypeople.rdt.core.RubyConventions;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.internal.core.util.CharOperation;
import org.rubypeople.rdt.internal.core.util.Messages;
import org.rubypeople.rdt.internal.core.util.Util;

/**
 * This operation creates a new package fragment under a given package fragment root. 
 * The following must be specified: <ul>
 * <li>the package fragment root
 * <li>the package name
 * </ul>
 * <p>Any needed folders/package fragments are created.
 * If the package fragment already exists, this operation has no effect.
 * The result elements include the <code>IPackageFragment</code> created and any side effect
 * package fragments that were created.
 *
 * <p>NOTE: A default package fragment exists by default for a given root.
 *
 * <p>Possible exception conditions: <ul>
 *  <li>Package fragment root is read-only
 *  <li>Package fragment's name is taken by a simple (non-folder) resource
 * </ul>
 */
public class CreateSourceFolderOperation extends RubyModelOperation {
	/**
	 * The fully qualified, dot-delimited, package name.
	 */
	protected String[] pkgName;
/**
 * When executed, this operation will create a package fragment with the given name
 * under the given package fragment root. The dot-separated name is broken into
 * segments. Intermediate folders are created as required for each segment.
 * If the folders already exist, this operation has no effect.
 */
public CreateSourceFolderOperation(SourceFolderRoot root, String packageName, boolean force) {
	super(null, new IRubyElement[]{root}, force);
	this.pkgName = packageName == null ? null : Util.getTrimmedSimpleNames(packageName);
}
/**
 * Execute the operation - creates the new package fragment and any
 * side effect package fragments.
 *
 * @exception RubyModelException if the operation is unable to complete
 */
protected void executeOperation() throws RubyModelException {
	RubyElementDelta delta = null;
	SourceFolderRoot root = (SourceFolderRoot) getParentElement();
	beginTask(Messages.operation_createPackageFragmentProgress, this.pkgName.length); 
	IContainer parentFolder = (IContainer) root.getResource();
	String[] sideEffectPackageName = CharOperation.NO_STRINGS; 
	ArrayList results = new ArrayList(this.pkgName.length);
	int i;
	for (i = 0; i < this.pkgName.length; i++) {
		String subFolderName = this.pkgName[i];
		sideEffectPackageName = Util.arrayConcat(sideEffectPackageName, subFolderName);
		IResource subFolder = parentFolder.findMember(subFolderName);
		if (subFolder == null) {
			createFolder(parentFolder, subFolderName, force);
			parentFolder = parentFolder.getFolder(new Path(subFolderName));
			ISourceFolder addedFrag = root.getSourceFolder(sideEffectPackageName);
			if (delta == null) {
				delta = newRubyElementDelta();
			}
			delta.added(addedFrag);
			results.add(addedFrag);
		} else {
			parentFolder = (IContainer) subFolder;
		}
		worked(1);
	}
	if (results.size() > 0) {
		this.resultElements = new IRubyElement[results.size()];
		results.toArray(this.resultElements);
		if (delta != null) {
			addDelta(delta);
		}
	}
	done();
}
/**
 * Possible failures: <ul>
 *  <li>NO_ELEMENTS_TO_PROCESS - the root supplied to the operation is
 * 		<code>null</code>.
 *	<li>INVALID_NAME - the name provided to the operation 
 * 		is <code>null</code> or is not a valid package fragment name.
 *	<li>READ_ONLY - the root provided to this operation is read only.
 *	<li>NAME_COLLISION - there is a pre-existing resource (file)
 * 		with the same name as a folder in the package fragment's hierarchy.
 *	<li>ELEMENT_NOT_PRESENT - the underlying resource for the root is missing
 * </ul>
 * @see IRubyModelStatus
 * @see RubyConventions
 */
public IRubyModelStatus verify() {
	if (getParentElement() == null) {
		return new RubyModelStatus(IRubyModelStatusConstants.NO_ELEMENTS_TO_PROCESS);
	}
	
	String packageName = this.pkgName == null ? null : Util.concatWith(this.pkgName, '/');
	if (this.pkgName == null) {
		return new RubyModelStatus(IRubyModelStatusConstants.INVALID_NAME, packageName);
	}
	IRubyProject root = (IRubyProject) getParentElement();
	if (root.isReadOnly()) {
		return new RubyModelStatus(IRubyModelStatusConstants.READ_ONLY, root);
	}
	IContainer parentFolder = (IContainer) root.getResource();
	int i;
	for (i = 0; i < this.pkgName.length; i++) {
		IResource subFolder = parentFolder.findMember(this.pkgName[i]);
		if (subFolder != null) {
			if (subFolder.getType() != IResource.FOLDER) {
				return new RubyModelStatus(
					IRubyModelStatusConstants.NAME_COLLISION, 
					Messages.bind(Messages.status_nameCollision, subFolder.getFullPath().toString())); 
			}
			parentFolder = (IContainer) subFolder;
		}
	}
	return RubyModelStatus.VERIFIED_OK;
}
}
