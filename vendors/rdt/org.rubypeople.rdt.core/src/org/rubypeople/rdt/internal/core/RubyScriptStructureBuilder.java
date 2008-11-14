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
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.rubypeople.rdt.core.IMethod;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IRubyScript;
import org.rubypeople.rdt.core.RubyCore;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.core.compiler.CategorizedProblem;
import org.rubypeople.rdt.internal.compiler.ISourceElementRequestor;

/**
 * @author Chris
 * 
 */
public class RubyScriptStructureBuilder implements ISourceElementRequestor {

	private InfoStack infoStack;
	private HandleStack modelStack;
	private RubyScriptElementInfo scriptInfo;
	private IRubyScript script;
	private Map newElements;
	private RubyElementInfo importContainerInfo;

	/**
	 * 
	 * @param script
	 *            The RubyScript whose contents we're parsing
	 * @param info
	 *            The RubyElementInfo of the RubyScript
	 * @param newElements
	 *            a Map passed in. It is actually a temporarcy cache from the
	 *            RubyModelManager. It holds elements below the level of a
	 *            RubyScript in our hierarchy.
	 */
	public RubyScriptStructureBuilder(IRubyScript script, RubyScriptElementInfo info, Map newElements) {
		this.script = script;
		this.scriptInfo = info;
		this.newElements = newElements;
		infoStack = new InfoStack();
		modelStack = new HandleStack();
		
		modelStack.push(script);
		infoStack.push(scriptInfo);
	}

	/**
	 * @return
	 */
	private RubyElementInfo getCurrentTypeInfo() {
		List extras = new ArrayList();
		RubyElementInfo element = infoStack.peek();
		while (!(element instanceof RubyTypeElementInfo)) {
			extras.add(infoStack.pop());
			element = infoStack.peek();
			if (element == null)
				break;
		}		
		Collections.reverse(extras); // Need to reverse extra before pushing back on the stack!
		for (Iterator iter = extras.iterator(); iter.hasNext();) {
			infoStack.push((RubyElementInfo) iter.next());
		}
		if (element == null)
			return scriptInfo;
		return element;
	}

	private RubyType findChild(RubyElement parent, int type, String name) {
		try {
			// FIXME What should we do when resource doesn't "exist" (is
			// external?)
			if (!parent.exists())
				return null;
			List<IRubyElement> children = parent.getChildrenOfType(type);
			for (IRubyElement element : children) {
				if (element.getElementName().equals(name))
					return (RubyType) element;
			}
		} catch (RubyModelException e) {
			RubyCore.log(e);
		}
		return null;
	}

	/**
	 * @return
	 */
	private RubyElement getCurrentType() {
		List extras = new ArrayList();
		IRubyElement element = modelStack.peek();
		while (!element.isType(IRubyElement.TYPE)) {
			extras.add(modelStack.pop());
			element = modelStack.peek();
			if (element == null)
				break;
		}
		Collections.reverse(extras); // Need to reverse elements before pushing back onto stack!
		for (Iterator iter = extras.iterator(); iter.hasNext();) {
			modelStack.push((RubyElement) iter.next());
		}
		if (element == null)
			return (RubyScript) script;
		return (RubyElement) element;
	}

	public void acceptConstructorReference(String name, int argCount, int offset) {
	// TODO Auto-generated method stub

	}

	public void acceptFieldReference(String name, int offset) {
	// TODO Auto-generated method stub

	}

	public void acceptImport(String value, int startOffset, int endOffset) {
		ImportContainer importContainer = (ImportContainer) script.getImportContainer();
		// create the import container and its info
		if (this.importContainerInfo == null) {
			this.importContainerInfo = new RubyElementInfo();
			scriptInfo.addChild(importContainer);
			this.newElements.put(importContainer, this.importContainerInfo);
		}
		RubyImport handle = new RubyImport(importContainer, value);

		ImportDeclarationElementInfo info = new ImportDeclarationElementInfo();
		info.setNameSourceStart(startOffset);
		info.setNameSourceEnd(endOffset);
		info.setSourceRangeStart(startOffset);
		info.setSourceRangeEnd(endOffset);
		info.name = value;

		this.importContainerInfo.addChild(handle);
		this.newElements.put(handle, info);
	}

	public void acceptMethodReference(String name, int argCount, int offset) {
	// TODO Auto-generated method stub

	}

	public void acceptProblem(CategorizedProblem problem) {
	// TODO Auto-generated method stub

	}

	public void acceptTypeReference(String name, int startOffset, int endOffset) {
	// TODO Auto-generated method stub

	}

	public void acceptUnknownReference(String name, int startOffset, int endOffset) {
	// TODO Auto-generated method stub

	}

	public void enterConstructor(MethodInfo constructor) {
		enterMethod(constructor);
	}

	public void enterField(FieldInfo field) {
		RubyField handle;
		if (field.name.startsWith("@@") ) {
			handle = new RubyClassVar(getCurrentType(), field.name);
		} else if (field.name.startsWith("@") ) {
			handle = new RubyInstVar(getCurrentType(), field.name);
		} else if (field.name.startsWith("$") ) {
			handle = new RubyGlobal(script, field.name);
		} else if (Character.isUpperCase(field.name.charAt(0))) {
			handle = new RubyConstant(getCurrentType(), field.name);
		} else {
			int start = field.declarationStart - field.name.length() + 1;
			int end = start + field.name.length();
			if (field.isDynamic) {
				handle = new RubyDynamicVar(modelStack.peek(), field.name, start, end);
			} else {
				handle = new LocalVariable(modelStack.peek(), field.name, start, end);
			}
		}		
		modelStack.push(handle);
		
		// Add to enclosing type
		RubyElementInfo parentInfo;
		if (handle instanceof LocalVariable || handle instanceof RubyDynamicVar) {
			parentInfo = infoStack.peek();
		} else if (handle instanceof RubyGlobal){
			parentInfo = scriptInfo; // FIXME Grab the project info?
		} else {
			parentInfo = getCurrentTypeInfo();
		}
		parentInfo.addChild(handle);
		
		RubyFieldElementInfo info = new RubyFieldElementInfo();
		info.setSourceRangeStart(field.declarationStart);
		info.setNameSourceStart(field.nameSourceStart);
		info.setNameSourceEnd(field.nameSourceEnd);
		
		infoStack.push(info);
		newElements.put(handle, info);		
	}

	public void enterMethod(MethodInfo methodInfo) {
		RubyMethod method = new RubyMethod(getCurrentType(), methodInfo.name, methodInfo.parameterNames);
		modelStack.push(method);

		infoStack.peek().addChild(method);

		RubyMethodElementInfo info = new RubyMethodElementInfo();
		info.setArgumentNames(methodInfo.parameterNames);
		info.setVisibility(methodInfo.visibility);
		info.setNameSourceStart(methodInfo.nameSourceStart);
		info.setNameSourceEnd(methodInfo.nameSourceEnd);
		info.setSourceRangeStart(methodInfo.declarationStart);
		info.setIsSingleton(methodInfo.isClassLevel);
		infoStack.push(info);
		newElements.put(method, info);
	}

	public void acceptYield(String name) {
		((RubyMethodElementInfo)infoStack.peek()).addBlockVar(name);		
	}
	
	public void enterScript() {
		// do nothing
	}

	public void enterType(TypeInfo type) {
		RubyType handle;
		if (type.isModule) {
			handle = new RubyModule(modelStack.peek(), type.name);
		} else {
			handle = new RubyType(modelStack.peek(), type.name);
		}
		RubyElement parent = modelStack.peek();
		RubyType existing = findChild(parent, IRubyElement.TYPE, type.name);
		if (existing != null) {
			// FIXME Should we just increment the occurence count like I do
			// here, or should we conglomerate the types into one LogicalType?
			handle.occurrenceCount = existing.occurrenceCount + 1;
		}
		modelStack.push(handle);

		infoStack.peek().addChild(handle);

		RubyTypeElementInfo info = new RubyTypeElementInfo();
		info.setHandle(handle);
		info.setNameSourceStart(type.nameSourceStart);
		info.setNameSourceEnd(type.nameSourceEnd);
		info.setSourceRangeStart(type.declarationStart);
		info.setSuperclassName(type.superclass);
		info.setIncludedModuleNames(type.modules);
		infoStack.push(info);

		newElements.put(handle, info);
	}

	public void exitConstructor(int endOffset) {
		exitMethod(endOffset);
	}

	public void exitField(int endOffset) {
		RubyFieldElementInfo info = (RubyFieldElementInfo) infoStack.pop();
		info.setSourceRangeEnd(endOffset); // TODO Does this also update the
											// instance in newElements?
		modelStack.pop();
	}

	public void exitMethod(int endOffset) {
		RubyMethodElementInfo info = (RubyMethodElementInfo) infoStack.pop();
		info.setSourceRangeEnd(endOffset); // TODO Does this also update the
											// instance in newElements?
		modelStack.pop();
	}

	public void exitScript(int endOffset) {
		modelStack.pop();
		infoStack.pop();
	}

	public void exitType(int endOffset) {
		RubyTypeElementInfo info = (RubyTypeElementInfo) infoStack.pop();
		info.setSourceRangeEnd(endOffset); // TODO Does this also update the
											// instance in newElements?
		modelStack.pop();
	}
	
	public void acceptMixin(String string) {
		// Push mixins into parent type, if available
		RubyElementInfo info = getCurrentTypeInfo();
		if (!(info instanceof RubyTypeElementInfo)) return; // FIXME Include this in a default toplevel type for the script?!
		RubyTypeElementInfo parentType = (RubyTypeElementInfo) info;

		// Get existing imported module names
		String[] importedModuleNames = parentType.getIncludedModuleNames();
		List<String> mergedModuleNames = new LinkedList<String>();

		// Merge newly found module name(s)
		if (importedModuleNames != null) {
			mergedModuleNames.addAll((Arrays.asList(importedModuleNames)));
		}
		mergedModuleNames.add(string);

		// Apply included module names back to parent type info
		String[] newIncludedModuleNames = mergedModuleNames.toArray(new String[] {});
		parentType.setIncludedModuleNames(newIncludedModuleNames);
	}

	public void acceptMethodVisibilityChange(String methodName, int visibility) {
		RubyElementInfo info = getCurrentTypeInfo();
		if (!(info instanceof RubyTypeElementInfo)) return;
		RubyTypeElementInfo parentType = (RubyTypeElementInfo) info;
		
		IMethod[] methods = parentType.getMethods();
		for (int i = 0; i < methods.length; i++) {
			RubyMethod method = (RubyMethod) methods[i];
			if (!method.getElementName().equals(methodName)) continue;;
			try {
				RubyMethodElementInfo methodInfo = (RubyMethodElementInfo) method.getElementInfo();
				methodInfo.setVisibility(visibility);
				return;
			} catch (RubyModelException e) {
				RubyCore.log(e);
			}
		}
	}

	public void acceptModuleFunction(String methodName) {
		RubyElementInfo info = getCurrentTypeInfo();
		if (!(info instanceof RubyTypeElementInfo)) return;
		RubyTypeElementInfo parentType = (RubyTypeElementInfo) info;
		
		IMethod[] methods = parentType.getMethods();
		for (int i = 0; i < methods.length; i++) {
			RubyMethod method = (RubyMethod) methods[i];
			if (!method.getElementName().equals(methodName)) continue;
			try {
				RubyMethodElementInfo methodInfo = (RubyMethodElementInfo) method.getElementInfo();
				methodInfo.setIsSingleton(true);
				return;
			} catch (RubyModelException e) {
				RubyCore.log(e);
			}
		}
		
	}

}