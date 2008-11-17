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
package org.rubypeople.rdt.internal.core;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.rubypeople.rdt.core.IField;
import org.rubypeople.rdt.core.IMember;
import org.rubypeople.rdt.core.IMethod;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IRubyScript;
import org.rubypeople.rdt.core.ISourceFolder;
import org.rubypeople.rdt.core.ISourceFolderRoot;
import org.rubypeople.rdt.core.IType;
import org.rubypeople.rdt.core.ITypeHierarchy;
import org.rubypeople.rdt.core.RubyCore;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.core.WorkingCopyOwner;
import org.rubypeople.rdt.core.search.SearchEngine;
import org.rubypeople.rdt.internal.core.util.MementoTokenizer;

/**
 * @author Chris
 * 
 */
public class RubyType extends NamedMember implements IType {

	/**
	 * The list of core classes whose parent is not Object or include modules, but could be re-defined without specifying their superclass or the included modules.
	 */
	private static final String[] CORE_NAMES = new String[] { "Array", "BasicSocket", "Bignum", "Class", "Complex", "Continuation", "Data", "Date", "DateTime",
		"Delegator", "Dir", "Enumerable", "Error", "FalseClass", "File", "Fixnum", "Float", "Generator", "Hash", "IO", "IPSocket", "Iconv",
		 "Integer", "Interrupt", "InvalidArgument", "List", "LoadError", "LocalJumpError", "Monitor", "NameError", "NilClass", "Numeric", "ParseError", 
		 "Prime", "Range", "RangeError", "Rational", "RegAnd", "RegOr", "Regexp", "RegexpError", "ScriptError", "SecurityError", "Set", "Shell", "SignalException",
		 "SimpleDelegator", "SizedQueue", "Socket", "SocketError", "SortedSet", "StandardError", "String", "StringIO", "Struct", "Symbol", "SyncEnumerator", 
		 "SyntaxError", "SystemCallError", "SystemExit", " 	SystemStackError", "TCPServer", "TCPSocket", "TempFile", "Thread", "ThreadError", "Time", "Timeout", 
		 "Tracer", "TrueClass", "TruncatedDataError", "TypeError", "UDPSocket", "URI", "UnboundMethod", "Vector", "ZeroDivisionError"};

	public RubyType(RubyElement parent, String name) {
		super(parent, name);
	}

	/**
	 * @see IType
	 */
	public String getSuperclassName() throws RubyModelException {
		if (isCoreClass() && !isCoreStub()) {
			IType type = getCoreClass(getElementName());
			if (type != null)
				return type.getSuperclassName();
		}
		RubyTypeElementInfo info = (RubyTypeElementInfo) getElementInfo();
		return info.getSuperclassName();
	}
	
	private boolean isCoreStub() throws RubyModelException {
		ISourceFolderRoot root = (ISourceFolderRoot) getAncestor(IRubyElement.SOURCE_FOLDER_ROOT);
		return root.equals(getCoreStubRoot());
	}

	private IType getCoreClass(String elementName) throws RubyModelException {
		ISourceFolderRoot root = getCoreStubRoot();
		if (root == null) return null;
		ISourceFolder folder = root.getSourceFolder(new String[0]);
		IRubyScript script = folder.getRubyScript(elementName.toLowerCase() + ".rb");
		return script.getType(elementName);
	}

	private ISourceFolderRoot getCoreStubRoot() throws RubyModelException {
		ISourceFolderRoot[] roots = getRubyProject().getSourceFolderRoots();
		for (int i = 0; i < roots.length; i++) {
			IPath path = roots[i].getPath();
			String string = path.toPortableString();
			if (string.contains("org.rubypeople.rdt.launching")) {
				return roots[i];
			}
		}
		return null;
	}

	private boolean isCoreClass() {
		String name = getElementName();
		for (int i = 0; i < CORE_NAMES.length; i++) {
			if (CORE_NAMES[i].equals(name)) return true;
		}
		return false;
	}

	/**
	 * @see IType
	 */
	public String[] getIncludedModuleNames() throws RubyModelException {		
		RubyTypeElementInfo info = (RubyTypeElementInfo) getElementInfo();
		String[] modules = info.getIncludedModuleNames();
		if ((modules == null || modules.length == 0) && getFullyQualifiedName().equals("Object")) {
			return new String[] {"Kernel"};
		}
		return modules;
	}
    
    /**
     * @see IType#isMember()
     */
    public boolean isMember() {
        return getDeclaringType() != null;
    }

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rubypeople.rdt.internal.core.parser.RubyElement#getElementType()
	 */
	public int getElementType() {
		return IRubyElement.TYPE;
	}

	/**
	 * @see IType#getField
	 */
	public IField getField(String fieldName) {
		if (fieldName.startsWith("@@"))
		  return new RubyClassVar(this, fieldName);
		if (fieldName.startsWith("@"))
			  return new RubyInstVar(this, fieldName);
		if (fieldName.startsWith("$"))
			  return new RubyGlobal(this, fieldName);
		if (Character.isUpperCase(fieldName.charAt(0)))
			  return new RubyConstant(this, fieldName);
		Assert.isTrue(false, "Tried to access a field which isn't an instance variable, class variable, global or constant");
		return null;
	}

	/**
	 * @see IType
	 */
	public IField[] getFields() throws RubyModelException {
		ArrayList list = getChildrenOfType(CONSTANT);
		list.addAll(getChildrenOfType(INSTANCE_VAR));
		list.addAll(getChildrenOfType(CLASS_VAR));
		IField[] array = new IField[list.size()];
		list.toArray(array);
		return array;
	}

	public IMethod getMethod(String name, String[] parameterNames) {
		return new RubyMethod(this, name, parameterNames);
	}

	/**
	 * @see IType
	 */
	public IMethod[] getMethods() throws RubyModelException {
		ArrayList list = getChildrenOfType(METHOD);
		IMethod[] array = new IMethod[list.size()];
		list.toArray(array);
		return array;
	}

	/**
	 * @see IMember
	 */
	public IType getDeclaringType() {
		IRubyElement parentElement = getParent();
		while (parentElement != null) {
			if (parentElement.getElementType() == IRubyElement.TYPE) {
				return (IType) parentElement;
			} else if (parentElement instanceof IMember) {
				parentElement = parentElement.getParent();
			} else {
				return null;
			}
		}
		return null;
	}

	/*
	 * @see RubyElement#getPrimaryElement(boolean)
	 */
	public IRubyElement getPrimaryElement(boolean checkOwner) {
		if (checkOwner) {
			RubyScript cu = (RubyScript) getAncestor(SCRIPT);
			if (cu.isPrimary()) return this;
		}
		IRubyElement primaryParent = this.parent.getPrimaryElement(false);
		switch (primaryParent.getElementType()) {
		case IRubyElement.SCRIPT:
			return ((IRubyScript) primaryParent).getType(this.name);
		case IRubyElement.TYPE:
			return ((IType) primaryParent).getType(this.name);
		case IRubyElement.INSTANCE_VAR:
		case IRubyElement.CLASS_VAR:
		case IRubyElement.BLOCK:
		case IRubyElement.LOCAL_VARIABLE:
		case IRubyElement.METHOD:
			return ((IMember) primaryParent).getType(this.name, this.occurrenceCount);
		}
		return this;
	}

	/**
	 * @see IType
	 */
	public IType getType(String typeName) {
		return new RubyType(this, typeName);
	}

	public boolean equals(Object o) {
		if (!(o instanceof RubyType)) return false;
		return super.equals(o);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rubypeople.rdt.core.IRubyType#isClass()
	 */
	public boolean isClass() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rubypeople.rdt.core.IRubyType#isModule()
	 */
	public boolean isModule() {
		return false;
	}

	public IMethod createMethod(String contents, IRubyElement sibling,
			boolean force, IProgressMonitor monitor) throws RubyModelException {
		CreateMethodOperation op = new CreateMethodOperation(this, contents, force);
		if (sibling != null) {
			op.createBefore(sibling);
		}
		op.runOperation(monitor);
		return (IMethod) op.getResultElements()[0];		
	}

	public ISourceFolder getSourceFolder() {
		IRubyElement parentElement = this.parent;
		while (parentElement != null) {
			if (parentElement.getElementType() == IRubyElement.SOURCE_FOLDER) {
				return (ISourceFolder)parentElement;
			}
			else {
				parentElement = parentElement.getParent();
			}
		}
		Assert.isTrue(false);  // should not happen
		return null;
	}

	public String getFullyQualifiedName() {
		IType declaring = getDeclaringType();
		if (declaring != null) {
			return declaring.getFullyQualifiedName() + "::" + getElementName();
		}
		return getElementName();
	}
	
	/*
	 * @see RubyElement
	 */
	public IRubyElement getHandleFromMemento(String token, MementoTokenizer memento, WorkingCopyOwner workingCopyOwner) {
		switch (token.charAt(0)) {
			case JEM_COUNT:
				return getHandleUpdatingCountFromMemento(memento, workingCopyOwner);
			case JEM_FIELD:
				if (!memento.hasMoreTokens()) return this;
				String fieldName = memento.nextToken();
				RubyElement field = (RubyElement)getField(fieldName);
				return field.getHandleFromMemento(memento, workingCopyOwner);
			case JEM_METHOD:
				if (!memento.hasMoreTokens()) return this;
				String selector = memento.nextToken();
				ArrayList params = new ArrayList();
				nextParam: while (memento.hasMoreTokens()) {
					token = memento.nextToken();
					switch (token.charAt(0)) {
						case JEM_TYPE:
							break nextParam;
						case JEM_METHOD:
							if (!memento.hasMoreTokens()) return this;
							String param = memento.nextToken();
							StringBuffer buffer = new StringBuffer();
							params.add(buffer.toString() + param);
							break;
						default:
							break nextParam;
					}
				}
				String[] parameters = new String[params.size()];
				params.toArray(parameters);
				RubyElement method = (RubyElement)getMethod(selector, parameters);
				switch (token.charAt(0)) {
					case JEM_TYPE:
					case JEM_LOCALVARIABLE:
						return method.getHandleFromMemento(token, memento, workingCopyOwner);
					default:
						return method;
				}
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
				RubyElement type = (RubyElement)getType(typeName);
				if (token == null) {
					return type.getHandleFromMemento(memento, workingCopyOwner);
				} else {
					return type.getHandleFromMemento(token, memento, workingCopyOwner);
				}			
		}
		return null;
	}

	/**
	 * @see IType#getTypeQualifiedName(char)
	 */
	public String getTypeQualifiedName(String enclosingTypeSeparator) {
		try {
			return getTypeQualifiedName(enclosingTypeSeparator, false/*don't show parameters*/);
		} catch (RubyModelException e) {
			// exception thrown only when showing parameters
			return null;
		}
	}

	/**
	 * @see IType
	 */
	public IType[] getTypes() throws RubyModelException {
		ArrayList list= getChildrenOfType(TYPE);
		IType[] array= new IType[list.size()];
		list.toArray(array);
		return array;
	}
	
	/**
	 * @see IType
	 */
	public ITypeHierarchy newTypeHierarchy(IProgressMonitor monitor) throws RubyModelException {
		CreateTypeHierarchyOperation op= new CreateTypeHierarchyOperation(this, null, SearchEngine.createWorkspaceScope(), true);
		op.runOperation(monitor);
		return op.getResult();
	}
	
	/**
	 * @see IType#newTypeHierarchy(WorkingCopyOwner, IProgressMonitor)
	 */
	public ITypeHierarchy newTypeHierarchy(
		WorkingCopyOwner owner,
		IProgressMonitor monitor)
		throws RubyModelException {
			
		IRubyScript[] workingCopies = RubyModelManager.getRubyModelManager().getWorkingCopies(owner, true/*add primary working copies*/);
		CreateTypeHierarchyOperation op= new CreateTypeHierarchyOperation(this, workingCopies, SearchEngine.createWorkspaceScope(), true);
		op.runOperation(monitor);
		return op.getResult();	
	}
	
	/**
	 * @see IType
	 */
	public ITypeHierarchy newSupertypeHierarchy(IProgressMonitor monitor) throws RubyModelException {
		return this.newSupertypeHierarchy(DefaultWorkingCopyOwner.PRIMARY, monitor);
	}
	
	/**
	 * @see IType#newSupertypeHierarchy(WorkingCopyOwner, IProgressMonitor)
	 */
	public ITypeHierarchy newSupertypeHierarchy(
		WorkingCopyOwner owner,
		IProgressMonitor monitor)
		throws RubyModelException {

		IRubyScript[] workingCopies = RubyModelManager.getRubyModelManager().getWorkingCopies(owner, true/*add primary working copies*/);
		CreateTypeHierarchyOperation op= new CreateTypeHierarchyOperation(this, workingCopies, SearchEngine.createWorkspaceScope(), false);
		op.runOperation(monitor);
		return op.getResult();
	}

	public IMethod[] findMethods(IMethod method) {
		List<IMethod> filtered = new ArrayList<IMethod>();
		try {
			ArrayList<IRubyElement> list = getChildrenOfType(METHOD);
			for (IRubyElement element : list) {
				IMethod other = (IMethod) element;
				if (!other.getElementName().equals(method.getElementName())) continue;
				filtered.add(other);
			}
		} catch (RubyModelException e) {
			RubyCore.log(e);
		}
		IMethod[] array = new IMethod[filtered.size()];
		filtered.toArray(array);
		return array;
	}

}