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
package org.rubypeople.rdt.internal.core.hierarchy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.rubypeople.rdt.core.IImportContainer;
import org.rubypeople.rdt.core.IImportDeclaration;
import org.rubypeople.rdt.core.IMember;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IRubyElementDelta;
import org.rubypeople.rdt.core.IRubyScript;
import org.rubypeople.rdt.core.IType;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.internal.core.RubyElement;
import org.rubypeople.rdt.internal.core.SimpleDelta;

/*
 * Collects changes (reported through fine-grained deltas) that can affect a
 * type hierarchy.
 */
public class ChangeCollector {

	/*
	 * A table from ITypes to TypeDeltas
	 */
	HashMap changes = new HashMap();

	TypeHierarchy hierarchy;

	public ChangeCollector(TypeHierarchy hierarchy) {
		this.hierarchy = hierarchy;
	}

	/*
	 * Adds the children of the given delta to the list of changes.
	 */
	private void addAffectedChildren(IRubyElementDelta delta)
			throws RubyModelException {
		IRubyElementDelta[] children = delta.getAffectedChildren();
		for (int i = 0, length = children.length; i < length; i++) {
			IRubyElementDelta child = children[i];
			IRubyElement childElement = child.getElement();
			switch (childElement.getElementType()) {
			case IRubyElement.IMPORT_CONTAINER:
				addChange((IImportContainer) childElement, child);
				break;
			case IRubyElement.IMPORT_DECLARATION:
				addChange((IImportDeclaration) childElement, child);
				break;
			case IRubyElement.TYPE:
				addChange((IType) childElement, child);
				break;
			// case IRubyElement.INITIALIZER:
			case IRubyElement.FIELD:
			case IRubyElement.METHOD:
				addChange((IMember) childElement, child);
				break;
			}
		}
	}

	/*
	 * Adds the given delta on a compilation unit to the list of changes.
	 */
	public void addChange(IRubyScript cu, IRubyElementDelta newDelta)
			throws RubyModelException {
		int newKind = newDelta.getKind();
		switch (newKind) {
		case IRubyElementDelta.ADDED:
			ArrayList allTypes = new ArrayList();
			getAllTypesFromElement(cu, allTypes);
			for (int i = 0, length = allTypes.size(); i < length; i++) {
				IType type = (IType) allTypes.get(i);
				addTypeAddition(type, (SimpleDelta) this.changes.get(type));
			}
			break;
		case IRubyElementDelta.REMOVED:
			allTypes = new ArrayList();
			getAllTypesFromHierarchy((RubyElement) cu, allTypes);
			for (int i = 0, length = allTypes.size(); i < length; i++) {
				IType type = (IType) allTypes.get(i);
				addTypeRemoval(type, (SimpleDelta) this.changes.get(type));
			}
			break;
		case IRubyElementDelta.CHANGED:
			addAffectedChildren(newDelta);
			break;
		}
	}

	private void addChange(IImportContainer importContainer,
			IRubyElementDelta newDelta) throws RubyModelException {
		int newKind = newDelta.getKind();
		if (newKind == IRubyElementDelta.CHANGED) {
			addAffectedChildren(newDelta);
			return;
		}
		SimpleDelta existingDelta = (SimpleDelta) this.changes
				.get(importContainer);
		if (existingDelta != null) {
			switch (newKind) {
			case IRubyElementDelta.ADDED:
				if (existingDelta.getKind() == IRubyElementDelta.REMOVED) {
					// REMOVED then ADDED
					this.changes.remove(importContainer);
				}
				break;
			case IRubyElementDelta.REMOVED:
				if (existingDelta.getKind() == IRubyElementDelta.ADDED) {
					// ADDED then REMOVED
					this.changes.remove(importContainer);
				}
				break;
			// CHANGED handled above
			}
		} else {
			SimpleDelta delta = new SimpleDelta();
			switch (newKind) {
			case IRubyElementDelta.ADDED:
				delta.added();
				break;
			case IRubyElementDelta.REMOVED:
				delta.removed();
				break;
			}
			this.changes.put(importContainer, delta);
		}
	}

	private void addChange(IImportDeclaration importDecl,
			IRubyElementDelta newDelta) {
		SimpleDelta existingDelta = (SimpleDelta) this.changes.get(importDecl);
		int newKind = newDelta.getKind();
		if (existingDelta != null) {
			switch (newKind) {
			case IRubyElementDelta.ADDED:
				if (existingDelta.getKind() == IRubyElementDelta.REMOVED) {
					// REMOVED then ADDED
					this.changes.remove(importDecl);
				}
				break;
			case IRubyElementDelta.REMOVED:
				if (existingDelta.getKind() == IRubyElementDelta.ADDED) {
					// ADDED then REMOVED
					this.changes.remove(importDecl);
				}
				break;
			// CHANGED cannot happen for import declaration
			}
		} else {
			SimpleDelta delta = new SimpleDelta();
			switch (newKind) {
			case IRubyElementDelta.ADDED:
				delta.added();
				break;
			case IRubyElementDelta.REMOVED:
				delta.removed();
				break;
			}
			this.changes.put(importDecl, delta);
		}
	}

	/*
	 * Adds a change for the given member (a method, a field or an initializer)
	 * and the types it defines.
	 */
	private void addChange(IMember member, IRubyElementDelta newDelta)
			throws RubyModelException {
		int newKind = newDelta.getKind();
		switch (newKind) {
		case IRubyElementDelta.ADDED:
			ArrayList allTypes = new ArrayList();
			getAllTypesFromElement(member, allTypes);
			for (int i = 0, length = allTypes.size(); i < length; i++) {
				IType innerType = (IType) allTypes.get(i);
				addTypeAddition(innerType, (SimpleDelta) this.changes
						.get(innerType));
			}
			break;
		case IRubyElementDelta.REMOVED:
			allTypes = new ArrayList();
			getAllTypesFromHierarchy((RubyElement) member, allTypes);
			for (int i = 0, length = allTypes.size(); i < length; i++) {
				IType type = (IType) allTypes.get(i);
				addTypeRemoval(type, (SimpleDelta) this.changes.get(type));
			}
			break;
		case IRubyElementDelta.CHANGED:
			addAffectedChildren(newDelta);
			break;
		}
	}

	/*
	 * Adds a change for the given type and the types it defines.
	 */
	private void addChange(IType type, IRubyElementDelta newDelta)
			throws RubyModelException {
		int newKind = newDelta.getKind();
		SimpleDelta existingDelta = (SimpleDelta) this.changes.get(type);
		switch (newKind) {
		case IRubyElementDelta.ADDED:
			addTypeAddition(type, existingDelta);
			ArrayList allTypes = new ArrayList();
			getAllTypesFromElement(type, allTypes);
			for (int i = 0, length = allTypes.size(); i < length; i++) {
				IType innerType = (IType) allTypes.get(i);
				addTypeAddition(innerType, (SimpleDelta) this.changes
						.get(innerType));
			}
			break;
		case IRubyElementDelta.REMOVED:
			addTypeRemoval(type, existingDelta);
			allTypes = new ArrayList();
			getAllTypesFromHierarchy((RubyElement) type, allTypes);
			for (int i = 0, length = allTypes.size(); i < length; i++) {
				IType innerType = (IType) allTypes.get(i);
				addTypeRemoval(innerType, (SimpleDelta) this.changes
						.get(innerType));
			}
			break;
		case IRubyElementDelta.CHANGED:
			addTypeChange(type, newDelta.getFlags(), existingDelta);
			addAffectedChildren(newDelta);
			break;
		}
	}

	private void addTypeAddition(IType type, SimpleDelta existingDelta)
			throws RubyModelException {
		if (existingDelta != null) {
			switch (existingDelta.getKind()) {
			case IRubyElementDelta.REMOVED:
				// REMOVED then ADDED
				boolean hasChange = false;
				if (hasSuperTypeChange(type)) {
					existingDelta.superTypes();
					hasChange = true;
				}
				if (hasVisibilityChange(type)) {
					existingDelta.modifiers();
					hasChange = true;
				}
				if (!hasChange) {
					this.changes.remove(type);
				}
				break;
			// CHANGED then ADDED
			// or ADDED then ADDED: should not happen
			}
		} else {
			// check whether the type addition affects the hierarchy
			String typeName = type.getElementName();
			if (this.hierarchy.hasSupertype(typeName)
					|| this.hierarchy.subtypesIncludeSupertypeOf(type)
					|| this.hierarchy.missingTypes.contains(typeName)) {
				SimpleDelta delta = new SimpleDelta();
				delta.added();
				this.changes.put(type, delta);
			}
		}
	}

	private void addTypeChange(IType type, int newFlags,
			SimpleDelta existingDelta) throws RubyModelException {
		if (existingDelta != null) {
			switch (existingDelta.getKind()) {
			case IRubyElementDelta.CHANGED:
				// CHANGED then CHANGED
				int existingFlags = existingDelta.getFlags();
				boolean hasChange = false;
				if ((existingFlags & IRubyElementDelta.F_SUPER_TYPES) != 0
						&& hasSuperTypeChange(type)) {
					existingDelta.superTypes();
					hasChange = true;
				}
				if ((existingFlags & IRubyElementDelta.F_MODIFIERS) != 0
						&& hasVisibilityChange(type)) {
					existingDelta.modifiers();
					hasChange = true;
				}
				if (!hasChange) {
					// super types and visibility are back to the ones in the
					// existing hierarchy
					this.changes.remove(type);
				}
				break;
			// ADDED then CHANGED: leave it as ADDED
			// REMOVED then CHANGED: should not happen
			}
		} else {
			// check whether the type change affects the hierarchy
			SimpleDelta typeDelta = null;
			if ((newFlags & IRubyElementDelta.F_SUPER_TYPES) != 0
					&& this.hierarchy.includesTypeOrSupertype(type)) {
				typeDelta = new SimpleDelta();
				typeDelta.superTypes();
			}
			if ((newFlags & IRubyElementDelta.F_MODIFIERS) != 0
					&& (this.hierarchy.hasSupertype(type.getElementName()) || type
							.equals(this.hierarchy.focusType))) {
				if (typeDelta == null) {
					typeDelta = new SimpleDelta();
				}
				typeDelta.modifiers();
			}
			if (typeDelta != null) {
				this.changes.put(type, typeDelta);
			}
		}
	}

	private void addTypeRemoval(IType type, SimpleDelta existingDelta) {
		if (existingDelta != null) {
			switch (existingDelta.getKind()) {
			case IRubyElementDelta.ADDED:
				// ADDED then REMOVED
				this.changes.remove(type);
				break;
			case IRubyElementDelta.CHANGED:
				// CHANGED then REMOVED
				existingDelta.removed();
				break;
			// REMOVED then REMOVED: should not happen
			}
		} else {
			// check whether the type removal affects the hierarchy
			if (this.hierarchy.contains(type)) {
				SimpleDelta typeDelta = new SimpleDelta();
				typeDelta.removed();
				this.changes.put(type, typeDelta);
			}
		}
	}

	/*
	 * Returns all types defined in the given element excluding the given
	 * element.
	 */
	private void getAllTypesFromElement(IRubyElement element, ArrayList allTypes)
			throws RubyModelException {
		switch (element.getElementType()) {
		case IRubyElement.SCRIPT:
			IType[] types = ((IRubyScript) element).getTypes();
			for (int i = 0, length = types.length; i < length; i++) {
				IType type = types[i];
				allTypes.add(type);
				getAllTypesFromElement(type, allTypes);
			}
			break;
		case IRubyElement.TYPE:
			types = ((IType) element).getTypes();
			for (int i = 0, length = types.length; i < length; i++) {
				IType type = types[i];
				allTypes.add(type);
				getAllTypesFromElement(type, allTypes);
			}
			break;
		// case IRubyElement.INITIALIZER:
		case IRubyElement.FIELD:
		case IRubyElement.METHOD:
			IRubyElement[] children = ((IMember) element).getChildren();
			for (int i = 0, length = children.length; i < length; i++) {
				if (children[i] instanceof IType) {
					IType type = (IType) children[i];
					allTypes.add(type);
					getAllTypesFromElement(type, allTypes);
				}
			}
			break;
		}
	}

	/*
	 * Returns all types in the existing hierarchy that have the given element
	 * as a parent.
	 */
	private void getAllTypesFromHierarchy(RubyElement element,
			ArrayList allTypes) {
		switch (element.getElementType()) {
		case IRubyElement.SCRIPT:
			ArrayList types = (ArrayList) this.hierarchy.files.get(element);
			if (types != null) {
				allTypes.addAll(types);
			}
			break;
		case IRubyElement.TYPE:
			// case IRubyElement.INITIALIZER:
		case IRubyElement.FIELD:
		case IRubyElement.METHOD:
			types = (ArrayList) this.hierarchy.files.get(((IMember) element)
					.getRubyScript());
			if (types != null) {
				for (int i = 0, length = types.size(); i < length; i++) {
					IType type = (IType) types.get(i);
					if (element.isAncestorOf(type)) {
						allTypes.add(type);
					}
				}
			}
			break;
		}
	}

	private boolean hasSuperTypeChange(IType type) throws RubyModelException {
		// check super class
		IType superclass = this.hierarchy.getSuperclass(type);
		String existingSuperclassName = superclass == null ? null : superclass
				.getElementName();
		String newSuperclassName = type.getSuperclassName();
		if (existingSuperclassName != null
				&& !existingSuperclassName.equals(newSuperclassName)) {
			return true;
		}

		// check super interfaces
		IType[] existingSuperInterfaces = this.hierarchy.getSuperModules(type);
		String[] newSuperInterfaces = type.getIncludedModuleNames();
		if (existingSuperInterfaces.length != newSuperInterfaces.length) {
			return true;
		}
		for (int i = 0, length = newSuperInterfaces.length; i < length; i++) {
			String superInterfaceName = newSuperInterfaces[i];
			if (!superInterfaceName.equals(newSuperInterfaces[i])) {
				return true;
			}
		}

		return false;
	}

	private boolean hasVisibilityChange(IType type) throws RubyModelException {
		// int existingFlags = this.hierarchy.getCachedFlags(type);
		// int newFlags = type.getFlags();
		// return existingFlags != newFlags;
		return false; // FIXME Types don't have visibility options in Ruby! We
						// shouldn't even ask this.
	}

	/*
	 * Whether the hierarchy needs refresh according to the changes collected so
	 * far.
	 */
	public boolean needsRefresh() {
		return changes.size() != 0;
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer();
		Iterator iterator = this.changes.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry entry = (Map.Entry) iterator.next();
			buffer.append(((RubyElement) entry.getKey()).toDebugString());
			buffer.append(entry.getValue());
			if (iterator.hasNext()) {
				buffer.append('\n');
			}
		}
		return buffer.toString();
	}
}
