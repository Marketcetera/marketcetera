/*******************************************************************************
 * Copyright (c) 2000, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.rubypeople.rdt.internal.core;

import org.rubypeople.rdt.core.IImportContainer;
import org.rubypeople.rdt.core.IImportDeclaration;
import org.rubypeople.rdt.core.IParent;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.ISourceRange;
import org.rubypeople.rdt.core.ISourceReference;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.core.WorkingCopyOwner;
import org.rubypeople.rdt.internal.core.util.MementoTokenizer;


/**
 * @see IImportContainer
 */
public class ImportContainer extends SourceRefElement implements IImportContainer {
protected ImportContainer(RubyScript parent) {
	super(parent);
}
public boolean equals(Object o) {
	if (!(o instanceof ImportContainer)) return false;
	return super.equals(o);
}


/* (non-Javadoc)
 * @see org.rubypeople.rdt.internal.core.RubyElement#getElementName()
 */
public String getElementName() {
	return "import declarations";
}
/**
 * @see IRubyElement
 */
public int getElementType() {
	return IMPORT_CONTAINER;
}
/**
 * @see IImportContainer
 */
public IImportDeclaration getImport(String importName) {
	return new RubyImport(this, importName);
}
/*
 * @see RubyElement#getPrimaryElement(boolean)
 */
public IRubyElement getPrimaryElement(boolean checkOwner) {
	RubyScript cu = (RubyScript)this.parent;
	if (checkOwner && cu.isPrimary()) return this;
	return cu.getImportContainer();
}
/**
 * @see ISourceReference
 */
public ISourceRange getSourceRange() throws RubyModelException {
	IRubyElement[] imports= getChildren();
	ISourceRange firstRange= ((ISourceReference)imports[0]).getSourceRange();
	ISourceRange lastRange= ((ISourceReference)imports[imports.length - 1]).getSourceRange();
	SourceRange range= new SourceRange(firstRange.getOffset(), lastRange.getOffset() + lastRange.getLength() - firstRange.getOffset());
	return range;
}
/**
 * Import containers only exist if they have children.
 * @see IParent
 */
public boolean hasChildren() {
	return true;
}
/**
 */
public String readableName() {
	return null;
}
/**
 * @private Debugging purposes
 */
protected void toString(int tab, StringBuffer buffer) {
	Object info = RubyModelManager.getRubyModelManager().peekAtInfo(this);
	if (info == null || !(info instanceof RubyElementInfo)) return;
	IRubyElement[] children = ((RubyElementInfo)info).getChildren();
	for (int i = 0; i < children.length; i++) {
		if (i > 0) buffer.append("\n"); //$NON-NLS-1$
		((RubyElement)children[i]).toString(tab, buffer);
	}
}
/**
 *  Debugging purposes
 */
protected void toStringInfo(int tab, StringBuffer buffer, Object info) {
	buffer.append(this.tabString(tab));
	buffer.append("<import container>"); //$NON-NLS-1$
	if (info == null) {
		buffer.append(" (not open)"); //$NON-NLS-1$
	}
}

/*
 * @see RubyElement
 */
public IRubyElement getHandleFromMemento(String token, MementoTokenizer memento, WorkingCopyOwner workingCopyOwner) {
	switch (token.charAt(0)) {
		case JEM_COUNT:
			return getHandleUpdatingCountFromMemento(memento, workingCopyOwner);
		case JEM_IMPORTDECLARATION:
			if (memento.hasMoreTokens()) {
				String importName = memento.nextToken();
				RubyElement importDecl = (RubyElement)getImport(importName);
				return importDecl.getHandleFromMemento(memento, workingCopyOwner);
			} else {
				return this;
			}
	}
	return null;
}
/**
 * @see RubyElement#getHandleMemento()
 */
protected char getHandleMementoDelimiter() {
	return RubyElement.JEM_IMPORTDECLARATION;
}
}
