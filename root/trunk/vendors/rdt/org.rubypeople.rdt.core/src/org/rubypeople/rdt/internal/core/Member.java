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

import org.rubypeople.rdt.core.IMember;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.ISourceRange;
import org.rubypeople.rdt.core.IType;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.core.WorkingCopyOwner;
import org.rubypeople.rdt.internal.core.util.MementoTokenizer;

/**
 * @see IMember
 */

public abstract class Member extends SourceRefElement implements IMember {

	protected Member(RubyElement parent) {
		super(parent);
	}

	protected static boolean areSimilarMethods(String name1, String[] params1, String name2, String[] params2, String[] simpleNames1) {
		if (name1.equals(name2)) {
			int params1Length = params1.length;
			if (params1Length == params2.length) {
				// TODO Check param types?
				return true;
			}
		}
		return false;
	}

	/**
	 * @see IMember
	 */
	public IType getDeclaringType() {
		RubyElement parentElement = (RubyElement) getParent();
		if (parentElement.getElementType() == TYPE) { return (IType) parentElement; }
		return null;
	}
	
	/*
	 * Returns the outermost context defining a local element. Per construction,
	 * it can only be a method/field/initializarer member; thus, returns null if
	 * this member is already a top-level type or member type. e.g for
	 * X.java/X/Y/foo()/Z/bar()/T, it will return X.java/X/Y/foo()
	 */
	public Member getOuterMostLocalContext() {
		IRubyElement current = this;
		Member lastLocalContext = null;
		parentLoop: while (true) {
			switch (current.getElementType()) {
			case SCRIPT:
				break parentLoop; // done recursing
			case TYPE:
				// cannot be a local context
				break;
			case CLASS_VAR:
			case INSTANCE_VAR:
			case METHOD:
				// these elements can define local members
				lastLocalContext = (Member) current;
				break;
			}
			current = current.getParent();
		}
		return lastLocalContext;
	}

	/**
	 * @see IMember
	 */
	public ISourceRange getNameRange() throws RubyModelException {
		MemberElementInfo info = (MemberElementInfo) getElementInfo();
		return new SourceRange(info.getNameSourceStart(), info.getNameSourceEnd() - info.getNameSourceStart() + 1);
	}

	/**
	 * @see IMember
	 */
	public IType getType(String typeName, int count) {
		RubyType type = new RubyType(this, typeName);
		type.occurrenceCount = count;
		return type;
	}

	/**
	 */
	public String readableName() {
		IRubyElement declaringType = getDeclaringType();
		if (declaringType != null) {
			String declaringName = ((RubyElement) getDeclaringType()).readableName();
			StringBuffer buffer = new StringBuffer(declaringName);
			buffer.append("::");
			buffer.append(this.getElementName());
			return buffer.toString();
		}
		return super.readableName();
	}

	/**
	 * Updates the name range for this element.
	 */
	protected void updateNameRange(int nameStart, int nameEnd) {
		try {
			MemberElementInfo info = (MemberElementInfo) getElementInfo();
			info.setNameSourceStart(nameStart);
			info.setNameSourceEnd(nameEnd);
		} catch (RubyModelException npe) {
			return;
		}
	}
	
	/*
	 * @see RubyElement
	 */
	public IRubyElement getHandleFromMemento(String token, MementoTokenizer memento, WorkingCopyOwner workingCopyOwner) {
		switch (token.charAt(0)) {
			case JEM_COUNT:
				return getHandleUpdatingCountFromMemento(memento, workingCopyOwner);
			case JEM_TYPE:
				String typeName;
				if (memento.hasMoreTokens()) {
					typeName = memento.nextToken();
					char firstChar = typeName.charAt(0);
					if (firstChar == JEM_FIELD || firstChar == JEM_METHOD || firstChar == JEM_TYPE || firstChar == JEM_COUNT) {
						token = typeName;
						typeName = ""; //$NON-NLS-1$
					} else {
						token = null;
					}
				} else {
					typeName = ""; //$NON-NLS-1$
					token = null;
				}
				RubyElement type = (RubyElement)getType(typeName, 1);
				if (token == null) {
					return type.getHandleFromMemento(memento, workingCopyOwner);
				} else {
					return type.getHandleFromMemento(token, memento, workingCopyOwner);
				}
//			case JEM_LOCALVARIABLE:
//				if (!memento.hasMoreTokens()) return this;
//				String varName = memento.nextToken();
//				if (!memento.hasMoreTokens()) return this;
//				memento.nextToken(); // JEM_COUNT
//				if (!memento.hasMoreTokens()) return this;
//				int declarationStart = Integer.parseInt(memento.nextToken());
//				if (!memento.hasMoreTokens()) return this;
//				memento.nextToken(); // JEM_COUNT
//				if (!memento.hasMoreTokens()) return this;
//				int declarationEnd = Integer.parseInt(memento.nextToken());
//				if (!memento.hasMoreTokens()) return this;
//				memento.nextToken(); // JEM_COUNT
//				if (!memento.hasMoreTokens()) return this;
//				int nameStart = Integer.parseInt(memento.nextToken());
//				if (!memento.hasMoreTokens()) return this;
//				memento.nextToken(); // JEM_COUNT
//				if (!memento.hasMoreTokens()) return this;
//				int nameEnd = Integer.parseInt(memento.nextToken());
//				if (!memento.hasMoreTokens()) return this;
//				memento.nextToken(); // JEM_COUNT
//				if (!memento.hasMoreTokens()) return this;
//				String typeSignature = memento.nextToken();
//				return new LocalVariable(this, varName, declarationStart, declarationEnd, nameStart, nameEnd, typeSignature);
		}
		return null;
	}
	/**
	 * @see JavaElement#getHandleMemento()
	 */
	protected char getHandleMementoDelimiter() {
		return RubyElement.JEM_TYPE;
	}
}
