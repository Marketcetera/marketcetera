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

import org.rubypeople.rdt.core.IField;
import org.rubypeople.rdt.core.IImportDeclaration;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IMethod;
import org.rubypeople.rdt.core.IType;
import org.rubypeople.rdt.core.ISourceImport;
import org.rubypeople.rdt.core.RubyModelException;

/**
 * Element info for an IType element that originated from source.
 */
public class RubyTypeElementInfo extends MemberElementInfo {

	protected static final ImportDeclarationElementInfo[] NO_IMPORTS = new ImportDeclarationElementInfo[0];
	protected static final RubyField[] NO_FIELDS = new RubyField[0];
	protected static final RubyMethod[] NO_METHODS = new RubyMethod[0];
	protected static final RubyType[] NO_TYPES = new RubyType[0];
	/**
	 * The name of the superclass for this type. This name is fully qualified
	 * for binary types and is NOT fully qualified for source types.
	 */
	protected String superclassName;

	/**
	 * The names of the module this type includes. These names
	 * are fully qualified in the case of a binary type, and are NOT fully
	 * qualified in the case of a source type
	 */
	protected String[] includedModuleNames;

	/**
	 * The name of the source file this type is declared in.
	 */
	protected String sourceFileName;

	/**
	 * The name of the package this type is contained in.
	 */
	protected String namespaceName;

	/**
	 * The infos of the imports in this type's compilation unit
	 */
	private ISourceImport[] imports;

	/**
	 * Backpointer to my type handle - useful for translation from info to
	 * handle.
	 */
	protected IType handle = null;

	/**
	 * Returns the IRubyType that is the enclosing type for this type, or
	 * <code>null</code> if this type is a top level type.
	 */
	public IType getEnclosingType() {
		IRubyElement parent = this.handle.getParent();
		if (parent != null && parent.getElementType() == IRubyElement.TYPE) {
			try {
				return (IType) ((RubyElement) parent).getElementInfo();
			} catch (RubyModelException e) {
				return null;
			}
		}
		return null;
	}

	/**
	 * @see IType
	 */
	public IField[] getFields() {
		RubyField[] fieldHandles = getFieldHandles();
		int length = fieldHandles.length;
		IField[] fields = new IField[length];
		for (int i = 0; i < length; i++) {
			try {
				IField field = (IField) fieldHandles[i].getElementInfo();
				fields[i] = field;
			} catch (RubyModelException e) {
				// ignore
			}
		}
		return fields;
	}

	public RubyField[] getFieldHandles() {
		int length = this.children.length;
		if (length == 0) return NO_FIELDS;
		RubyField[] fields = new RubyField[length];
		int fieldIndex = 0;
		for (int i = 0; i < length; i++) {
			IRubyElement child = this.children[i];
			if (child instanceof RubyField) fields[fieldIndex++] = (RubyField) child;
		}
		if (fieldIndex == 0) return NO_FIELDS;
		if (fieldIndex < length) System.arraycopy(fields, 0, fields = new RubyField[fieldIndex], 0, fieldIndex);
		return fields;
	}

	/**
	 * @see org.eclipse.jdt.internal.compiler.env.IDependent#getFileName()
	 */
	public String getFileName() {
		return this.sourceFileName;
	}

	/**
	 * Returns the handle for this type info
	 */
	public IType getHandle() {
		return this.handle;
	}

	/**
	 * @see IType
	 */
	public ISourceImport[] getImports() {
		if (this.imports == null) {
			try {
				IImportDeclaration[] importDeclarations = this.handle.getRubyScript().getImports();
				int length = importDeclarations.length;
				if (length == 0) {
					this.imports = NO_IMPORTS;
				} else {
					ISourceImport[] sourceImports = new ImportDeclarationElementInfo[length];
					for (int i = 0; i < length; i++) {
						sourceImports[i] = (ImportDeclarationElementInfo) ((RubyImport) importDeclarations[i]).getElementInfo();
					}
					this.imports = sourceImports; // only commit at the end,
													// once completed (bug
													// 36854)
				}
			} catch (RubyModelException e) {
				this.imports = NO_IMPORTS;
			}
		}
		return this.imports;
	}

	/**
	 * @see IType
	 */
	public String[] getIncludedModuleNames() {
		if (this.handle.getElementName().length() == 0) { // if anonymous type
			return null;
		}
		return this.includedModuleNames;
	}

	/**
	 * @see IType
	 */
	public IType[] getMemberTypes() {
		RubyType[] memberTypeHandles = getMemberTypeHandles();
		int length = memberTypeHandles.length;
		IType[] memberTypes = new IType[length];
		for (int i = 0; i < length; i++) {
			try {
				IType type = (IType) memberTypeHandles[i].getElementInfo();
				memberTypes[i] = type;
			} catch (RubyModelException e) {
				// ignore
			}
		}
		return memberTypes;
	}

	public RubyType[] getMemberTypeHandles() {
		int length = this.children.length;
		if (length == 0) return NO_TYPES;
		RubyType[] memberTypes = new RubyType[length];
		int typeIndex = 0;
		for (int i = 0; i < length; i++) {
			IRubyElement child = this.children[i];
			if (child instanceof RubyType) memberTypes[typeIndex++] = (RubyType) child;
		}
		if (typeIndex == 0) return NO_TYPES;
		if (typeIndex < length) System.arraycopy(memberTypes, 0, memberTypes = new RubyType[typeIndex], 0, typeIndex);
		return memberTypes;
	}

	/**
	 * @see IType
	 */
	public IMethod[] getMethods() {
		return getMethodHandles();
//		RubyMethod[] methodHandles = getMethodHandles();
//		int length = methodHandles.length;
//		IMethod[] methods = new IMethod[length];
//		int methodIndex = 0;
//		for (int i = 0; i < length; i++) {
//			try {
//				IMethod method = (IMethod) methodHandles[i].getElementInfo();
//				methods[methodIndex++] = method;
//			} catch (RubyModelException e) {
//				// ignore
//			}
//		}
//		return methods;
	}

	public RubyMethod[] getMethodHandles() {
		int length = this.children.length;
		if (length == 0) return NO_METHODS;
		RubyMethod[] methods = new RubyMethod[length];
		int methodIndex = 0;
		for (int i = 0; i < length; i++) {
			IRubyElement child = this.children[i];
			if (child instanceof RubyMethod) methods[methodIndex++] = (RubyMethod) child;
		}
		if (methodIndex == 0) return NO_METHODS;
		if (methodIndex < length) System.arraycopy(methods, 0, methods = new RubyMethod[methodIndex], 0, methodIndex);
		return methods;
	}

	/**
	 * @see org.eclipse.jdt.internal.compiler.env.IType#getName()
	 */
	public char[] getName() {
		return this.handle.getElementName().toCharArray();
	}

	/**
	 * @see IType
	 */
	public String getNamespace() {
		return this.namespaceName;
	}

	/**
	 * @see IType
	 */
	public String getSuperclassName() {
		if (this.handle.getElementName().length() == 0) { // if anonymous type
			String[] interfaceNames = this.includedModuleNames;
			if (interfaceNames != null && interfaceNames.length > 0) { return interfaceNames[0]; }
		}
		return this.superclassName;
	}

	/**
	 * Sets the handle for this type info
	 */
	protected void setHandle(IType handle) {
		this.handle = handle;
	}

	/**
	 * Sets the name of the package this type is declared in.
	 */
	protected void setNamespaceName(String name) {
		this.namespaceName = name;
	}

	/**
	 * Sets the name of the source file this type is declared in.
	 */
	protected void setSourceFileName(String name) {
		this.sourceFileName = name;
	}

	/**
	 * Sets the (unqualified) name of this type's superclass
	 */
	protected void setSuperclassName(String superclassName) {
		this.superclassName = superclassName;
	}

	/**
	 * Sets the (unqualified) names of the modules this type includes
	 */
	protected void setIncludedModuleNames(String[] includedModuleNames) {
		this.includedModuleNames = includedModuleNames;
	}

	public String toString() {
		return "Info for " + this.handle.toString(); //$NON-NLS-1$
	}
}
