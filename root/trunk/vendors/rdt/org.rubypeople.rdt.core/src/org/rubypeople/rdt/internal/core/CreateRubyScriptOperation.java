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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.rubypeople.rdt.core.IBuffer;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IRubyElementDelta;
import org.rubypeople.rdt.core.IRubyModelStatus;
import org.rubypeople.rdt.core.IRubyModelStatusConstants;
import org.rubypeople.rdt.core.IRubyScript;
import org.rubypeople.rdt.core.ISourceFolder;
import org.rubypeople.rdt.core.RubyConventions;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.internal.core.util.Messages;
import org.rubypeople.rdt.internal.core.util.Util;

/**
 * <p>This operation creates a compilation unit (CU).
 * If the CU doesn't exist yet, a new compilation unit will be created with the content provided.
 * Otherwise the operation will override the contents of an existing CU with the new content.
 *
 * <p>Note: It is possible to create a CU automatically when creating a
 * class or interface. Thus, the preferred method of creating a CU is
 * to perform a create type operation rather than
 * first creating a CU and secondly creating a type inside the CU.
 *
 * <p>Required Attributes:<ul>
 *  <li>The package fragment in which to create the compilation unit.
 *  <li>The name of the compilation unit.  
 *      Do not include the <code>".java"</code> suffix (ex. <code>"Object"</code> -
 * 		the <code>".java"</code> will be added for the name of the compilation unit.)
 *  <li>
  * </ul>
 */
public class CreateRubyScriptOperation extends RubyModelOperation {

	/**
	 * The name of the compilation unit being created.
	 */
	protected String fName;
	/**
	 * The source code to use when creating the element.
	 */
	protected String fSource= null;
/**
 * When executed, this operation will create a compilation unit with the given name.
 * The name should have the ".java" suffix.
 */
public CreateRubyScriptOperation(ISourceFolder parentElement, String name, String source, boolean force) {
	super(null, new IRubyElement[] {parentElement}, force);
	fName = name;
	fSource = source;
}
/**
 * Creates a compilation unit.
 *
 * @exception RubyModelException if unable to create the compilation unit.
 */
protected void executeOperation() throws RubyModelException {
	try {
		beginTask(Messages.operation_createUnitProgress, 2); 
		RubyElementDelta delta = newRubyElementDelta();
		IRubyScript unit = getRubyScript();
		ISourceFolder pkg = (ISourceFolder) getParentElement();
		IContainer folder = (IContainer) pkg.getResource();
		worked(1);
		IFile compilationUnitFile = folder.getFile(new Path(fName));
		if (compilationUnitFile.exists()) {
			// update the contents of the existing unit if fForce is true
			if (force) {
				IBuffer buffer = unit.getBuffer();
				if (buffer == null) return;
				buffer.setContents(fSource);
				unit.save(new NullProgressMonitor(), false);
				resultElements = new IRubyElement[] {unit};
				if (!Util.isExcluded(unit)
						&& unit.getParent().exists()) {
					for (int i = 0; i < resultElements.length; i++) {
						delta.changed(resultElements[i], IRubyElementDelta.F_CONTENT);
					}
					addDelta(delta);
				}
			} else {
				throw new RubyModelException(new RubyModelStatus(
					IRubyModelStatusConstants.NAME_COLLISION, 
					Messages.bind(Messages.status_nameCollision, compilationUnitFile.getFullPath().toString()))); 
			}
		} else {
			try {
				String encoding = null;
				try {
					encoding = folder.getDefaultCharset(); // get folder encoding as file is not accessible
				}
				catch (CoreException ce) {
					// use no encoding
				}
				InputStream stream = new ByteArrayInputStream(encoding == null ? fSource.getBytes() : fSource.getBytes(encoding));
				createFile(folder, unit.getElementName(), stream, force);
				resultElements = new IRubyElement[] {unit};
				if (!Util.isExcluded(unit)
						&& unit.getParent().exists()) {
					for (int i = 0; i < resultElements.length; i++) {
						delta.added(resultElements[i]);
					}
					addDelta(delta);
				}
			} catch (IOException e) {
				throw new RubyModelException(e, IRubyModelStatusConstants.IO_EXCEPTION);
			}
		} 
		worked(1);
	} finally {
		done();
	}
}
/**
 * @see CreateElementInCUOperation#getCompilationUnit()
 */
protected IRubyScript getRubyScript() {
	return ((ISourceFolder)getParentElement()).getRubyScript(fName);
}
protected ISchedulingRule getSchedulingRule() {
	IResource resource  = getRubyScript().getResource();
	IWorkspace workspace = resource.getWorkspace();
	if (resource.exists()) {
		return workspace.getRuleFactory().modifyRule(resource);
	} else {
		return workspace.getRuleFactory().createRule(resource);
	}
}
/**
 * Possible failures: <ul>
 *  <li>NO_ELEMENTS_TO_PROCESS - the package fragment supplied to the operation is
 * 		<code>null</code>.
 *	<li>INVALID_NAME - the compilation unit name provided to the operation 
 * 		is <code>null</code> or has an invalid syntax
 *  <li>INVALID_CONTENTS - the source specified for the compiliation unit is null
 * </ul>
 */
public IRubyModelStatus verify() {
	if (getParentElement() == null) {
		return new RubyModelStatus(IRubyModelStatusConstants.NO_ELEMENTS_TO_PROCESS);
	}
	if (RubyConventions.validateRubyScriptName(fName).getSeverity() == IStatus.ERROR) {
		return new RubyModelStatus(IRubyModelStatusConstants.INVALID_NAME, fName);
	}
	if (fSource == null) {
		return new RubyModelStatus(IRubyModelStatusConstants.INVALID_CONTENTS);
	}
	return RubyModelStatus.VERIFIED_OK;
}
}
