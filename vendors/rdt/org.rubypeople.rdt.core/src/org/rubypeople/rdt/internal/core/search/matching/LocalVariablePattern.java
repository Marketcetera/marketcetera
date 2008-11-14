/*******************************************************************************
 * Copyright (c) 2000, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.rubypeople.rdt.internal.core.search.matching;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.ISourceFolderRoot;
import org.rubypeople.rdt.core.search.IRubySearchScope;
import org.rubypeople.rdt.core.search.SearchParticipant;
import org.rubypeople.rdt.internal.core.LocalVariable;
import org.rubypeople.rdt.internal.core.index.Index;
import org.rubypeople.rdt.internal.core.search.IndexQueryRequestor;
import org.rubypeople.rdt.internal.core.search.RubySearchScope;
import org.rubypeople.rdt.internal.core.search.indexing.IIndexConstants;
import org.rubypeople.rdt.internal.core.util.Util;

public class LocalVariablePattern extends VariablePattern implements IIndexConstants {
	
LocalVariable localVariable;

public LocalVariablePattern(boolean findDeclarations, boolean readAccess, boolean writeAccess, LocalVariable localVariable, int matchRule) {
	super(LOCAL_VAR_PATTERN, findDeclarations, readAccess, writeAccess, localVariable.getElementName().toCharArray(), matchRule);
	this.localVariable = localVariable;
}
public void findIndexMatches(Index index, IndexQueryRequestor requestor, SearchParticipant participant, IRubySearchScope scope, IProgressMonitor progressMonitor) {
    ISourceFolderRoot root = (ISourceFolderRoot)this.localVariable.getAncestor(IRubyElement.SOURCE_FOLDER_ROOT);
	String documentPath;
	String relativePath;
//    if (root.isArchive()) {
//        IType type = (IType)this.localVariable.getAncestor(IRubyElement.TYPE);
//        relativePath = (type.getFullyQualifiedName("/")).replace('.', '/') + SuffixConstants.SUFFIX_STRING_class;
//        documentPath = root.getPath() + IRubySearchScope.JAR_FILE_ENTRY_SEPARATOR + relativePath;
//    } else {
		IPath path = this.localVariable.getPath();
        documentPath = path.toString();
		relativePath = Util.relativePath(path, 1/*remove project segment*/);
//    }

	if (scope instanceof RubySearchScope) {
		RubySearchScope javaSearchScope = (RubySearchScope) scope;
		// Get document path access restriction from java search scope
		// Note that requestor has to verify if needed whether the document violates the access restriction or not
//		AccessRuleSet access = javaSearchScope.getAccessRuleSet(relativePath, index.containerPath);
//		if (access != RubySearchScope.NOT_ENCLOSED) { // scope encloses the path
			if (!requestor.acceptIndexMatch(documentPath, this, participant)) 
				throw new OperationCanceledException();
//		}
	} else if (scope.encloses(documentPath)) {
		if (!requestor.acceptIndexMatch(documentPath, this, participant)) 
			throw new OperationCanceledException();
	}
}
protected StringBuffer print(StringBuffer output) {
	if (this.findDeclarations) {
		output.append(this.findReferences
			? "LocalVarCombinedPattern: " //$NON-NLS-1$
			: "LocalVarDeclarationPattern: "); //$NON-NLS-1$
	} else {
		output.append("LocalVarReferencePattern: "); //$NON-NLS-1$
	}
	output.append(this.localVariable.toStringWithAncestors());
	return super.print(output);
}
}
