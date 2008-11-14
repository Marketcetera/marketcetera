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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.SafeRunner;
import org.rubypeople.rdt.core.ElementChangedEvent;
import org.rubypeople.rdt.core.IElementChangedListener;
import org.rubypeople.rdt.core.ILoadpathEntry;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IRubyElementDelta;
import org.rubypeople.rdt.core.IRubyModelStatusConstants;
import org.rubypeople.rdt.core.IRubyProject;
import org.rubypeople.rdt.core.IRubyScript;
import org.rubypeople.rdt.core.ISourceFolder;
import org.rubypeople.rdt.core.ISourceFolderRoot;
import org.rubypeople.rdt.core.IType;
import org.rubypeople.rdt.core.ITypeHierarchy;
import org.rubypeople.rdt.core.ITypeHierarchyChangedListener;
import org.rubypeople.rdt.core.RubyCore;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.core.WorkingCopyOwner;
import org.rubypeople.rdt.core.search.IRubySearchScope;
import org.rubypeople.rdt.core.search.SearchEngine;
import org.rubypeople.rdt.internal.core.Openable;
import org.rubypeople.rdt.internal.core.Region;
import org.rubypeople.rdt.internal.core.RubyElement;
import org.rubypeople.rdt.internal.core.RubyModelStatus;
import org.rubypeople.rdt.internal.core.RubyProject;
import org.rubypeople.rdt.internal.core.RubyScript;
import org.rubypeople.rdt.internal.core.SourceFolder;
import org.rubypeople.rdt.internal.core.TypeVector;
import org.rubypeople.rdt.internal.core.util.Messages;
import org.rubypeople.rdt.internal.core.util.Util;


/**
 * @see ITypeHierarchy
 */
public class TypeHierarchy implements ITypeHierarchy, IElementChangedListener {

	public static boolean DEBUG = false;
	
	static final byte VERSION = 0x0000;
	// SEPARATOR
	static final byte SEPARATOR1 = '\n';
	static final byte SEPARATOR2 = ',';
	static final byte SEPARATOR3 = '>';
	static final byte SEPARATOR4 = '\r';
	// general info
	static final byte COMPUTE_SUBTYPES = 0x0001;
	
	// type info
	static final byte CLASS = 0x0000;
	static final byte INTERFACE = 0x0001;
	static final byte COMPUTED_FOR = 0x0002;
	static final byte ROOT = 0x0004;

	// cst
	static final byte[] NO_FLAGS = new byte[]{};
	static final int SIZE = 10;
	
	/**
	 * The Ruby Project in which the hierarchy is being built - this
	 * provides the context for determining a classpath and namelookup rules.
	 * Possibly null.
	 */
	protected IRubyProject project;
	/**
	 * The type the hierarchy was specifically computed for,
	 * possibly null.
	 */
	protected IType focusType;
	
	/*
	 * The working copies that take precedence over original compilation units
	 */
	protected IRubyScript[] workingCopies;

	protected Map classToSuperclass;
	protected Map typeToSuperModules;
	protected Map typeToSubtypes;
	protected Map typeFlags;
	protected TypeVector rootClasses = new TypeVector();
	protected ArrayList modules = new ArrayList(10);
	public ArrayList missingTypes = new ArrayList(4);
	
	protected static final IType[] NO_TYPE = new IType[0];
	
	/**
	 * The progress monitor to report work completed too.
	 */
	protected IProgressMonitor progressMonitor = null;

	/**
	 * Change listeners - null if no one is listening.
	 */
	protected ArrayList changeListeners = null;

	/*
	 * A map from Openables to ArrayLists of ITypes
	 */
	public Map files = null;

	/**
	 * A region describing the packages considered by this
	 * hierarchy. Null if not activated.
	 */
	protected Region packageRegion = null;

	/**
	 * A region describing the projects considered by this
	 * hierarchy. Null if not activated.
	 */
	protected Region projectRegion = null;
	
	/**
	 * Whether this hierarchy should contains subtypes.
	 */
	protected boolean computeSubtypes;

	/**
	 * The scope this hierarchy should restrain itsef in.
	 */
	IRubySearchScope scope;
	
	/*
	 * Whether this hierarchy needs refresh
	 */
	public boolean needsRefresh = true;
	
	/*
	 * Collects changes to types
	 */
	protected ChangeCollector changeCollector;

/**
 * Creates an empty TypeHierarchy
 */
public TypeHierarchy() {
	// Creates an empty TypeHierarchy
}
/**
 * Creates a TypeHierarchy on the given type.
 */
public TypeHierarchy(IType type, IRubyScript[] workingCopies, IRubyProject project, boolean computeSubtypes) {
	this(type, workingCopies, SearchEngine.createRubySearchScope(new IRubyElement[] {project}), computeSubtypes);
	this.project = project;
}
/**
 * Creates a TypeHierarchy on the given type.
 */
public TypeHierarchy(IType type, IRubyScript[] workingCopies, IRubySearchScope scope, boolean computeSubtypes) {
	this.focusType = type == null ? null : (IType) ((RubyElement) type).unresolved(); // unsure the focus type is unresolved (see https://bugs.eclipse.org/bugs/show_bug.cgi?id=92357)
	this.workingCopies = workingCopies;
	this.computeSubtypes = computeSubtypes;
	this.scope = scope;
}
/**
 * Initializes the file, package and project regions
 */
protected void initializeRegions() {

	IType[] allTypes = getAllTypes();
	for (int i = 0; i < allTypes.length; i++) {
		IType type = allTypes[i];
		Openable o = (Openable) ((RubyElement) type).getOpenableParent();
		if (o != null) {
			ArrayList types = (ArrayList)this.files.get(o);
			if (types == null) {
				types = new ArrayList();
				this.files.put(o, types);
			}
			types.add(type);
		}
		ISourceFolder pkg = type.getSourceFolder();
		this.packageRegion.add(pkg);
		IRubyProject declaringProject = type.getRubyProject();
		if (declaringProject != null) {
			this.projectRegion.add(declaringProject);
		}
		checkCanceled();
	}
}
/**
 * Adds all of the elements in the collection to the list if the
 * element is not already in the list.
 */
private void addAllCheckingDuplicates(ArrayList list, IType[] collection) {
	for (int i = 0; i < collection.length; i++) {
		IType element = collection[i];
		if (!list.contains(element)) {
			list.add(element);
		}
	}
}
/**
 * Adds the type to the collection of interfaces.
 */
protected void addModule(IType type) {
	this.modules.add(type);
}
/**
 * Adds the type to the collection of root classes
 * if the classes is not already present in the collection.
 */
protected void addRootClass(IType type) {
	if (this.rootClasses.contains(type)) return;
	this.rootClasses.add(type);
}
/**
 * Adds the given subtype to the type.
 */
protected void addSubtype(IType type, IType subtype) {
	TypeVector subtypes = (TypeVector)this.typeToSubtypes.get(type);
	if (subtypes == null) {
		subtypes = new TypeVector();
		this.typeToSubtypes.put(type, subtypes);
	}
	if (!subtypes.contains(subtype)) {
		subtypes.add(subtype);
	}
}
/**
 * @see ITypeHierarchy
 */
public synchronized void addTypeHierarchyChangedListener(ITypeHierarchyChangedListener listener) {
	ArrayList listeners = this.changeListeners;
	if (listeners == null) {
		this.changeListeners = listeners = new ArrayList();
	}
	
	// register with RubyCore to get Ruby element delta on first listener added
	if (listeners.size() == 0) {
		RubyCore.addElementChangedListener(this);
	}
	
	// add listener only if it is not already present
	if (listeners.indexOf(listener) == -1) {
		listeners.add(listener);
	}
}
private static Integer bytesToFlags(byte[] bytes){
	if(bytes != null && bytes.length > 0) {
		return new Integer(new String(bytes));
	} else {
		return null;
	}
}
/**
 * cacheFlags.
 */
public void cacheFlags(IType type, int flags) {
	this.typeFlags.put(type, new Integer(flags));
}
/**
 * Caches the handle of the superclass for the specified type.
 * As a side effect cache this type as a subtype of the superclass.
 */
protected void cacheSuperclass(IType type, IType superclass) {
	if (superclass != null) {
		this.classToSuperclass.put(type, superclass);
		addSubtype(superclass, type);
	} 
}
/**
 * Caches all of the supermodules that are specified for the
 * type.
 */
protected void cacheSuperModules(IType type, IType[] supermodules) {
	this.typeToSuperModules.put(type, supermodules);
	for (int i = 0; i < supermodules.length; i++) {
		IType supermodule = supermodules[i];
		if (supermodule != null) {
			addModule(supermodule);
			addSubtype(supermodule, type);
		}
	}
}
/**
 * Checks with the progress monitor to see whether the creation of the type hierarchy
 * should be canceled. Should be regularly called
 * so that the user can cancel.
 *
 * @exception OperationCanceledException if cancelling the operation has been requested
 * @see IProgressMonitor#isCanceled
 */
protected void checkCanceled() {
	if (this.progressMonitor != null && this.progressMonitor.isCanceled()) {
		throw new OperationCanceledException();
	}
}
/**
 * Compute this type hierarchy.
 */
protected void compute() throws RubyModelException, CoreException {
	if (this.focusType != null) {
		HierarchyBuilder builder = 
			new IndexBasedHierarchyBuilder(
				this, 
				this.scope);
		builder.build(this.computeSubtypes);
	} // else a RegionBasedTypeHierarchy should be used
}
/**
 * @see ITypeHierarchy
 */
public boolean contains(IType type) {
	// classes
	if (this.classToSuperclass.get(type) != null) {
		return true;
	}

	// root classes
	if (this.rootClasses.contains(type)) return true;

	// interfaces
	if (this.modules.contains(type)) return true;
	
	return false;
}
/**
 * Determines if the change effects this hierarchy, and fires
 * change notification if required.
 */
public void elementChanged(ElementChangedEvent event) {
	// type hierarchy change has already been fired
	if (this.needsRefresh) return;
	
	if (isAffected(event.getDelta())) {
		this.needsRefresh = true;
		fireChange();
	}
}
/**
 * @see ITypeHierarchy
 */
public boolean exists() {
	if (!this.needsRefresh) return true;
	
	return (this.focusType == null || this.focusType.exists()) && this.rubyProject().exists();
}
/**
 * Notifies listeners that this hierarchy has changed and needs
 * refreshing. Note that listeners can be removed as we iterate
 * through the list.
 */
public void fireChange() {
	ArrayList listeners = this.changeListeners;
	if (listeners == null) {
		return;
	}
	if (DEBUG) {
		System.out.println("FIRING hierarchy change ["+Thread.currentThread()+"]"); //$NON-NLS-1$ //$NON-NLS-2$
		if (this.focusType != null) {
			System.out.println("    for hierarchy focused on " + ((RubyElement)this.focusType).toStringWithAncestors()); //$NON-NLS-1$
		}
	}
	// clone so that a listener cannot have a side-effect on this list when being notified
	listeners = (ArrayList)listeners.clone();
	for (int i= 0; i < listeners.size(); i++) {
		final ITypeHierarchyChangedListener listener= (ITypeHierarchyChangedListener)listeners.get(i);
		SafeRunner.run(new ISafeRunnable() {
			public void handleException(Throwable exception) {
				Util.log(exception, "Exception occurred in listener of Type hierarchy change notification"); //$NON-NLS-1$
			}
			public void run() throws Exception {
				listener.typeHierarchyChanged(TypeHierarchy.this);
			}
		});
	}
}
private static byte[] flagsToBytes(Integer flags){
	if(flags != null) {
		return flags.toString().getBytes();
	} else {
		return NO_FLAGS;
	}
}
/**
 * @see ITypeHierarchy
 */
public IType[] getAllClasses() {

	TypeVector classes = this.rootClasses.copy();
	for (Iterator iter = this.classToSuperclass.keySet().iterator(); iter.hasNext();){
		classes.add((IType)iter.next());
	}
	return classes.elements();
}
/**
 * @see ITypeHierarchy
 */
public IType[] getAllModules() {
	IType[] collection= new IType[this.modules.size()];
	this.modules.toArray(collection);
	return collection;
}
/**
 * @see ITypeHierarchy
 */
public IType[]  getAllSubtypes(IType type) {
	return getAllSubtypesForType(type);
}
/**
 * @see #getAllSubtypes(IType)
 */
private IType[] getAllSubtypesForType(IType type) {
	ArrayList subTypes = new ArrayList();
	getAllSubtypesForType0(type, subTypes);
	IType[] subClasses = new IType[subTypes.size()];
	subTypes.toArray(subClasses);
	return subClasses;
}
/**
 */
private void getAllSubtypesForType0(IType type, ArrayList subs) {
	IType[] subTypes = getSubtypesForType(type);
	if (subTypes.length != 0) {
		for (int i = 0; i < subTypes.length; i++) {
			IType subType = subTypes[i];
			subs.add(subType);
			getAllSubtypesForType0(subType, subs);
		}
	}
}
/**
 * @see ITypeHierarchy
 */
public IType[] getAllSuperclasses(IType type) {
	IType superclass = getSuperclass(type);
	TypeVector supers = new TypeVector();
	while (superclass != null) {
		supers.add(superclass);
		superclass = getSuperclass(superclass);
	}
	return supers.elements();
}
/**
 * @see ITypeHierarchy
 */
public IType[] getAllSuperModules(IType type) {
	ArrayList supers = new ArrayList();
	if (this.typeToSuperModules.get(type) == null) {
		return NO_TYPE;
	}
	getAllSuperModules0(type, supers);
	IType[] supermodules = new IType[supers.size()];
	supers.toArray(supermodules);
	return supermodules;
}
private void getAllSuperModules0(IType type, ArrayList supers) {
	IType[] superinterfaces = (IType[]) this.typeToSuperModules.get(type);
	if (superinterfaces != null && superinterfaces.length != 0) {
		addAllCheckingDuplicates(supers, superinterfaces);
		for (int i = 0; i < superinterfaces.length; i++) {
			getAllSuperModules0(superinterfaces[i], supers);
		}
	}
	IType superclass = (IType) this.classToSuperclass.get(type);
	if (superclass != null) {
		getAllSuperModules0(superclass, supers);
	}
}
/**
 * @see ITypeHierarchy
 */
public IType[] getAllSupertypes(IType type) {
	ArrayList supers = new ArrayList();
	if (this.typeToSuperModules.get(type) == null) {
		return NO_TYPE;
	}
	getAllSupertypes0(type, supers);
	IType[] supertypes = new IType[supers.size()];
	supers.toArray(supertypes);
	return supertypes;
}
private void getAllSupertypes0(IType type, ArrayList supers) {
	IType[] superinterfaces = (IType[]) this.typeToSuperModules.get(type);
	if (superinterfaces != null && superinterfaces.length != 0) {
		addAllCheckingDuplicates(supers, superinterfaces);
		for (int i = 0; i < superinterfaces.length; i++) {
			getAllSuperModules0(superinterfaces[i], supers);
		}
	}
	IType superclass = (IType) this.classToSuperclass.get(type);
	if (superclass != null) {
		supers.add(superclass);
		getAllSupertypes0(superclass, supers);
	}
}
/**
 * @see ITypeHierarchy
 */
public IType[] getAllTypes() {
	IType[] classes = getAllClasses();
	int classesLength = classes.length;
	IType[] allInterfaces = getAllModules();
	int interfacesLength = allInterfaces.length;
	IType[] all = new IType[classesLength + interfacesLength];
	System.arraycopy(classes, 0, all, 0, classesLength);
	System.arraycopy(allInterfaces, 0, all, classesLength, interfacesLength);
	return all;
}

/**
 * @see ITypeHierarchy#getCachedFlags(IType)
 */
public int getCachedFlags(IType type) {
	Integer flagObject = (Integer) this.typeFlags.get(type);
	if (flagObject != null){
		return flagObject.intValue();
	}
	return -1;
}

/**
 * @see ITypeHierarchy
 */
public IType[] getExtendingModules(IType type) {
	if (!this.isModule(type)) return NO_TYPE;
	return getExtendingModules0(type);
}
/**
 * Assumes that the type is an module
 * @see #getExtendingModules
 */
private IType[] getExtendingModules0(IType extendedInterface) {
	Iterator iter = this.typeToSuperModules.keySet().iterator();
	ArrayList interfaceList = new ArrayList();
	while (iter.hasNext()) {
		IType type = (IType) iter.next();
		if (!this.isModule(type)) {
			continue;
		}
		IType[] superInterfaces = (IType[]) this.typeToSuperModules.get(type);
		if (superInterfaces != null) {
			for (int i = 0; i < superInterfaces.length; i++) {
				IType superInterface = superInterfaces[i];
				if (superInterface.equals(extendedInterface)) {
					interfaceList.add(type);
				}
			}
		}
	}
	IType[] extendingInterfaces = new IType[interfaceList.size()];
	interfaceList.toArray(extendingInterfaces);
	return extendingInterfaces;
}
/**
 * @see ITypeHierarchy
 */
public IType[] getIncludingClasses(IType type) {
	if (!this.isModule(type)) {
		return NO_TYPE;
	}
	return getIncludingClasses0(type);
}
/**
 * Assumes that the type is an interface
 * @see #getIncludingClasses
 */
private IType[] getIncludingClasses0(IType interfce) {
	
	Iterator iter = this.typeToSuperModules.keySet().iterator();
	ArrayList iMenters = new ArrayList();
	while (iter.hasNext()) {
		IType type = (IType) iter.next();
		if (this.isModule(type)) {
			continue;
		}
		IType[] types = (IType[]) this.typeToSuperModules.get(type);
		for (int i = 0; i < types.length; i++) {
			IType iFace = types[i];
			if (iFace.equals(interfce)) {
				iMenters.add(type);
			}
		}
	}
	IType[] implementers = new IType[iMenters.size()];
	iMenters.toArray(implementers);
	return implementers;
}
/**
 * @see ITypeHierarchy
 */
public IType[] getRootClasses() {
	return this.rootClasses.elements();
}
/**
 * @see ITypeHierarchy
 */
public IType[] getRootModules() {
	IType[] allInterfaces = getAllModules();
	IType[] roots = new IType[allInterfaces.length];
	int rootNumber = 0;
	for (int i = 0; i < allInterfaces.length; i++) {
		IType[] superInterfaces = getSuperModules(allInterfaces[i]);
		if (superInterfaces == null || superInterfaces.length == 0) {
			roots[rootNumber++] = allInterfaces[i];
		}
	}
	IType[] result = new IType[rootNumber];
	if (result.length > 0) {
		System.arraycopy(roots, 0, result, 0, rootNumber);
	}
	return result;
}
/**
 * @see ITypeHierarchy
 */
public IType[] getSubclasses(IType type) {
	if (this.isModule(type)) {
		return NO_TYPE;
	}
	TypeVector vector = (TypeVector)this.typeToSubtypes.get(type);
	if (vector == null)
		return NO_TYPE;
	else 
		return vector.elements();
}
/**
 * @see ITypeHierarchy
 */
public IType[] getSubtypes(IType type) {
	return getSubtypesForType(type);
}
/**
 * Returns an array of subtypes for the given type - will never return null.
 */
private IType[] getSubtypesForType(IType type) {
	TypeVector vector = (TypeVector)this.typeToSubtypes.get(type);
	if (vector == null)
		return NO_TYPE;
	else 
		return vector.elements();
}
/**
 * @see ITypeHierarchy
 */
public IType getSuperclass(IType type) {
	if (this.isModule(type)) {
		return null;
	}
	return (IType) this.classToSuperclass.get(type);
}
/**
 * @see ITypeHierarchy
 */
public IType[] getSuperModules(IType type) {
	IType[] types = (IType[]) this.typeToSuperModules.get(type);
	if (types == null) {
		return NO_TYPE;
	}
	return types;
}
/**
 * @see ITypeHierarchy
 */
public IType[] getSupertypes(IType type) {
	IType superclass = getSuperclass(type);
	if (superclass == null) {
		return getSuperModules(type);
	} else {
		TypeVector superTypes = new TypeVector(getSuperModules(type));
		superTypes.add(superclass);
		return superTypes.elements();
	}
}
/**
 * @see ITypeHierarchy
 */
public IType getType() {
	return this.focusType;
}
/**
 * Adds the new elements to a new array that contains all of the elements of the old array.
 * Returns the new array.
 */
protected IType[] growAndAddToArray(IType[] array, IType[] additions) {
	if (array == null || array.length == 0) {
		return additions;
	}
	IType[] old = array;
	array = new IType[old.length + additions.length];
	System.arraycopy(old, 0, array, 0, old.length);
	System.arraycopy(additions, 0, array, old.length, additions.length);
	return array;
}
/**
 * Adds the new element to a new array that contains all of the elements of the old array.
 * Returns the new array.
 */
protected IType[] growAndAddToArray(IType[] array, IType addition) {
	if (array == null || array.length == 0) {
		return new IType[] {addition};
	}
	IType[] old = array;
	array = new IType[old.length + 1];
	System.arraycopy(old, 0, array, 0, old.length);
	array[old.length] = addition;
	return array;
}
/*
 * Whether fine-grained deltas where collected and affects this hierarchy.
 */
public boolean hasFineGrainChanges() {
    ChangeCollector collector = this.changeCollector;
	return collector != null && collector.needsRefresh();
}
/**
 * Returns whether one of the subtypes in this hierarchy has the given simple name
 * or this type has the given simple name.
 */
private boolean hasSubtypeNamed(String simpleName) {
	if (this.focusType != null && this.focusType.getElementName().equals(simpleName)) {
		return true;
	}
	IType[] types = this.focusType == null ? getAllTypes() : getAllSubtypes(this.focusType);
	for (int i = 0, length = types.length; i < length; i++) {
		if (types[i].getElementName().equals(simpleName)) {
			return true;
		}
	}
	return false;
}

/**
 * Returns whether one of the types in this hierarchy has the given simple name.
 */
private boolean hasTypeNamed(String simpleName) {
	IType[] types = this.getAllTypes();
	for (int i = 0, length = types.length; i < length; i++) {
		if (types[i].getElementName().equals(simpleName)) {
			return true;
		}
	}
	return false;
}

/**
 * Returns whether the simple name of the given type or one of its supertypes is 
 * the simple name of one of the types in this hierarchy.
 */
boolean includesTypeOrSupertype(IType type) {
	try {
		// check type
		if (hasTypeNamed(type.getElementName())) return true;
		
		// check superclass
		String superclassName = type.getSuperclassName();
		if (superclassName != null) {
			int lastSeparator = superclassName.lastIndexOf('.');
			String simpleName = superclassName.substring(lastSeparator+1);
			if (hasTypeNamed(simpleName)) return true;
		}
	
		// check superinterfaces
		String[] superinterfaceNames = type.getIncludedModuleNames();
		if (superinterfaceNames != null) {
			for (int i = 0, length = superinterfaceNames.length; i < length; i++) {
				String superinterfaceName = superinterfaceNames[i];
				int lastSeparator = superinterfaceName.lastIndexOf('.');
				String simpleName = superinterfaceName.substring(lastSeparator+1);
				if (hasTypeNamed(simpleName)) return true;
			}
		}
	} catch (RubyModelException e) {
		// ignore
	}
	return false;
}
/**
 * Initializes this hierarchy's internal tables with the given size.
 */
protected void initialize(int size) {
	if (size < 10) {
		size = 10;
	}
	int smallSize = (size / 2);
	this.classToSuperclass = new HashMap(size);
	this.modules = new ArrayList(smallSize);
	this.missingTypes = new ArrayList(smallSize);
	this.rootClasses = new TypeVector();
	this.typeToSubtypes = new HashMap(smallSize);
	this.typeToSuperModules = new HashMap(smallSize);
	this.typeFlags = new HashMap(smallSize);
	
	this.projectRegion = new Region();
	this.packageRegion = new Region();
	this.files = new HashMap(5);
}
/**
 * Returns true if the given delta could change this type hierarchy
 */
public synchronized boolean isAffected(IRubyElementDelta delta) {
	IRubyElement element= delta.getElement();
	switch (element.getElementType()) {
		case IRubyElement.RUBY_MODEL:
			return isAffectedByRubyModel(delta, element);
		case IRubyElement.RUBY_PROJECT:
			return isAffectedByRubyProject(delta, element);
		case IRubyElement.SOURCE_FOLDER_ROOT:
			return isAffectedBySourceFolderRoot(delta, element);
		case IRubyElement.SOURCE_FOLDER:
			return isAffectedBySourceFolder(delta, (SourceFolder) element);
//		case IRubyElement.CLASS_FILE:
		case IRubyElement.SCRIPT:
			return isAffectedByOpenable(delta, element);
	}
	return false;
}
/**
 * Returns true if any of the children of a project, package
 * fragment root, or package fragment have changed in a way that
 * effects this type hierarchy.
 */
private boolean isAffectedByChildren(IRubyElementDelta delta) {
	if ((delta.getFlags() & IRubyElementDelta.F_CHILDREN) > 0) {
		IRubyElementDelta[] children= delta.getAffectedChildren();
		for (int i= 0; i < children.length; i++) {
			if (isAffected(children[i])) {
				return true;
			}
		}
	}
	return false;
}
/**
 * Returns true if the given java model delta could affect this type hierarchy
 */
private boolean isAffectedByRubyModel(IRubyElementDelta delta, IRubyElement element) {
	switch (delta.getKind()) {
		case IRubyElementDelta.ADDED :
		case IRubyElementDelta.REMOVED :
			return element.equals(this.rubyProject().getRubyModel());
		case IRubyElementDelta.CHANGED :
			return isAffectedByChildren(delta);
	}
	return false;
}
/**
 * Returns true if the given java project delta could affect this type hierarchy
 */
private boolean isAffectedByRubyProject(IRubyElementDelta delta, IRubyElement element) {
    int kind = delta.getKind();
    int flags = delta.getFlags();
    if ((flags & IRubyElementDelta.F_OPENED) != 0) {
        kind = IRubyElementDelta.ADDED; // affected in the same way
    }
    if ((flags & IRubyElementDelta.F_CLOSED) != 0) {
        kind = IRubyElementDelta.REMOVED; // affected in the same way
    }
	switch (kind) {
		case IRubyElementDelta.ADDED :
			try {
				// if the added project is on the classpath, then the hierarchy has changed
				ILoadpathEntry[] classpath = ((RubyProject)this.rubyProject()).getExpandedLoadpath(true);
				for (int i = 0; i < classpath.length; i++) {
					if (classpath[i].getEntryKind() == ILoadpathEntry.CPE_PROJECT 
							&& classpath[i].getPath().equals(element.getPath())) {
						return true;
					}
				}
				if (this.focusType != null) {
					// if the hierarchy's project is on the added project classpath, then the hierarchy has changed
					classpath = ((RubyProject)element).getExpandedLoadpath(true);
					IPath hierarchyProject = rubyProject().getPath();
					for (int i = 0; i < classpath.length; i++) {
						if (classpath[i].getEntryKind() == ILoadpathEntry.CPE_PROJECT 
								&& classpath[i].getPath().equals(hierarchyProject)) {
							return true;
						}
					}
				}
				return false;
			} catch (RubyModelException e) {
				return false;
			}
		case IRubyElementDelta.REMOVED :
			// removed project - if it contains packages we are interested in
			// then the type hierarchy has changed
			IRubyElement[] pkgs = this.packageRegion.getElements();
			for (int i = 0; i < pkgs.length; i++) {
				IRubyProject javaProject = pkgs[i].getRubyProject();
				if (javaProject != null && javaProject.equals(element)) {
					return true;
				}
			}
			return false;
		case IRubyElementDelta.CHANGED :
			return isAffectedByChildren(delta);
	}
	return false;
}
/**
 * Returns true if the given package fragment delta could affect this type hierarchy
 */
private boolean isAffectedBySourceFolder(IRubyElementDelta delta, SourceFolder element) {
	switch (delta.getKind()) {
		case IRubyElementDelta.ADDED :
			// if the package fragment is in the projects being considered, this could
			// introduce new types, changing the hierarchy
			return this.projectRegion.contains(element);
		case IRubyElementDelta.REMOVED :
			// is a change if the package fragment contains types in this hierarchy
			return packageRegionContainsSameSourceFolder(element);
		case IRubyElementDelta.CHANGED :
			// look at the files in the package fragment
			return isAffectedByChildren(delta);
	}
	return false;
}
/**
 * Returns true if the given package fragment root delta could affect this type hierarchy
 */
private boolean isAffectedBySourceFolderRoot(IRubyElementDelta delta, IRubyElement element) {
	switch (delta.getKind()) {
		case IRubyElementDelta.ADDED :
			return this.projectRegion.contains(element);
		case IRubyElementDelta.REMOVED :
		case IRubyElementDelta.CHANGED :
			int flags = delta.getFlags();
			if ((flags & IRubyElementDelta.F_ADDED_TO_CLASSPATH) > 0) {
				// check if the root is in the classpath of one of the projects of this hierarchy
				if (this.projectRegion != null) {
					ISourceFolderRoot root = (ISourceFolderRoot)element;
					IPath rootPath = root.getPath();
					IRubyElement[] elements = this.projectRegion.getElements();
					for (int i = 0; i < elements.length; i++) {
						RubyProject javaProject = (RubyProject)elements[i];
						try {
							ILoadpathEntry[] classpath = javaProject.getResolvedLoadpath(true/*ignoreUnresolvedEntry*/, false/*don't generateMarkerOnError*/, false/*don't returnResolutionInProgress*/);
							for (int j = 0; j < classpath.length; j++) {
								ILoadpathEntry entry = classpath[j];
								if (entry.getPath().equals(rootPath)) {
									return true;
								}
							}
						} catch (RubyModelException e) {
							// igmore this project
						}
					}
				}
			}
			if ((flags & IRubyElementDelta.F_REMOVED_FROM_CLASSPATH) > 0 || (flags & IRubyElementDelta.F_CONTENT) > 0) {
				// 1. removed from classpath - if it contains packages we are interested in
				// the the type hierarchy has changed
				// 2. content of a jar changed - if it contains packages we are interested in
				// the the type hierarchy has changed
				IRubyElement[] pkgs = this.packageRegion.getElements();
				for (int i = 0; i < pkgs.length; i++) {
					if (pkgs[i].getParent().equals(element)) {
						return true;
					}
				}
				return false;
			}
	}
	return isAffectedByChildren(delta);
}
/**
 * Returns true if the given type delta (a compilation unit delta or a class file delta)
 * could affect this type hierarchy.
 */
protected boolean isAffectedByOpenable(IRubyElementDelta delta, IRubyElement element) {
	if (element instanceof RubyScript) {
		RubyScript cu = (RubyScript)element;
		ChangeCollector collector = this.changeCollector;
		if (collector == null) {
		    collector = new ChangeCollector(this);
		}
		try {
			collector.addChange(cu, delta);
		} catch (RubyModelException e) {
			if (DEBUG)
				e.printStackTrace();
		}
		if (cu.isWorkingCopy()) {
			// changes to working copies are batched
			this.changeCollector = collector;
			return false;
		} else {
			return collector.needsRefresh();
		}
	}
	return false;
}
private boolean isModule(IType type) {
	return type.isModule();
}
/**
 * Returns the ruby project this hierarchy was created in.
 */
public IRubyProject rubyProject() {
	return this.focusType.getRubyProject();
}
protected static byte[] readUntil(InputStream input, byte separator) throws RubyModelException, IOException{
	return readUntil(input, separator, 0);
}
protected static byte[] readUntil(InputStream input, byte separator, int offset) throws IOException, RubyModelException{
	int length = 0;
	byte[] bytes = new byte[SIZE];
	byte b;
	while((b = (byte)input.read()) != separator && b != -1) {
		if(bytes.length == length) {
			System.arraycopy(bytes, 0, bytes = new byte[length*2], 0, length);
		}
		bytes[length++] = b;
	}
	if(b == -1) {
		throw new RubyModelException(new RubyModelStatus(IStatus.ERROR));
	}
	System.arraycopy(bytes, 0, bytes = new byte[length + offset], offset, length);
	return bytes;
}
public static ITypeHierarchy load(IType type, InputStream input, WorkingCopyOwner owner) throws RubyModelException {
	try {
		TypeHierarchy typeHierarchy = new TypeHierarchy();
		typeHierarchy.initialize(1);
		
		IType[] types = new IType[SIZE];
		int typeCount = 0;
		
		byte version = (byte)input.read();
	
		if(version != VERSION) {
			throw new RubyModelException(new RubyModelStatus(IStatus.ERROR));
		}
		byte generalInfo = (byte)input.read();
		if((generalInfo & COMPUTE_SUBTYPES) != 0) {
			typeHierarchy.computeSubtypes = true;
		}
		
		byte b;
		byte[] bytes;
		
		// read project
		bytes = readUntil(input, SEPARATOR1);
		if(bytes.length > 0) {
			typeHierarchy.project = (IRubyProject)RubyCore.create(new String(bytes));
			typeHierarchy.scope = SearchEngine.createRubySearchScope(new IRubyElement[] {typeHierarchy.project});
		} else {
			typeHierarchy.project = null;
			typeHierarchy.scope = SearchEngine.createWorkspaceScope();
		}
		
		// read missing type
		{
			bytes = readUntil(input, SEPARATOR1);
			byte[] missing;
			int j = 0;
			int length = bytes.length;
			for (int i = 0; i < length; i++) {
				b = bytes[i];
				if(b == SEPARATOR2) {
					missing = new byte[i - j];
					System.arraycopy(bytes, j, missing, 0, i - j);
					typeHierarchy.missingTypes.add(new String(missing));
					j = i + 1;
				}
			}
			System.arraycopy(bytes, j, missing = new byte[length - j], 0, length - j);
			typeHierarchy.missingTypes.add(new String(missing));
		}

		// read types
		while((b = (byte)input.read()) != SEPARATOR1 && b != -1) {
			bytes = readUntil(input, SEPARATOR4, 1);
			bytes[0] = b;
			IType element = (IType)RubyCore.create(new String(bytes), owner);
			
			if(types.length == typeCount) {
				System.arraycopy(types, 0, types = new IType[typeCount * 2], 0, typeCount);
			}
			types[typeCount++] = element;
			
			// read flags
			bytes = readUntil(input, SEPARATOR4);
			Integer flags = bytesToFlags(bytes);
			if(flags != null) {
				typeHierarchy.cacheFlags(element, flags.intValue());
			}
			
			// read info
			byte info = (byte)input.read();
			
			if((info & INTERFACE) != 0) {
				typeHierarchy.addModule(element);
			}
			if((info & COMPUTED_FOR) != 0) {
				if(!element.equals(type)) {
					throw new RubyModelException(new RubyModelStatus(IStatus.ERROR)); 
				}
				typeHierarchy.focusType = element;
			}
			if((info & ROOT) != 0) {
				typeHierarchy.addRootClass(element);
			}
		}
		
		// read super class
		while((b = (byte)input.read()) != SEPARATOR1 && b != -1) {
			bytes = readUntil(input, SEPARATOR3, 1);
			bytes[0] = b;
			int subClass = new Integer(new String(bytes)).intValue();
			
			// read super type
			bytes = readUntil(input, SEPARATOR1);
			int superClass = new Integer(new String(bytes)).intValue();
			
			typeHierarchy.cacheSuperclass(
				types[subClass],
				types[superClass]);
		}
		
		// read super interface
		while((b = (byte)input.read()) != SEPARATOR1 && b != -1) {
			bytes = readUntil(input, SEPARATOR3, 1);
			bytes[0] = b;
			int subClass = new Integer(new String(bytes)).intValue();
			
			// read super interface
			bytes = readUntil(input, SEPARATOR1);
			IType[] superInterfaces = new IType[(bytes.length / 2) + 1];
			int interfaceCount = 0;
			
			int j = 0;
			byte[] b2;
			for (int i = 0; i < bytes.length; i++) {
				if(bytes[i] == SEPARATOR2){
					b2 = new byte[i - j];
					System.arraycopy(bytes, j, b2, 0, i - j);
					j = i + 1;
					superInterfaces[interfaceCount++] = types[new Integer(new String(b2)).intValue()];
				}
			}
			b2 = new byte[bytes.length - j];
			System.arraycopy(bytes, j, b2, 0, bytes.length - j);
			superInterfaces[interfaceCount++] = types[new Integer(new String(b2)).intValue()];
			System.arraycopy(superInterfaces, 0, superInterfaces = new IType[interfaceCount], 0, interfaceCount);
			
			typeHierarchy.cacheSuperModules(
				types[subClass],
				superInterfaces);
		}
		if(b == -1) {
			throw new RubyModelException(new RubyModelStatus(IStatus.ERROR));
		}
		return typeHierarchy;
	} catch(IOException e){
		throw new RubyModelException(e, IRubyModelStatusConstants.IO_EXCEPTION);
	}
}
/**
 * Returns <code>true</code> if an equivalent package fragment is included in the package
 * region. Package fragments are equivalent if they both have the same name.
 */
protected boolean packageRegionContainsSameSourceFolder(SourceFolder element) {
	IRubyElement[] pkgs = this.packageRegion.getElements();
	for (int i = 0; i < pkgs.length; i++) {
		SourceFolder pkg = (SourceFolder) pkgs[i];
		if (Util.equalArraysOrNull(pkg.names, element.names))
			return true;
	}
	return false;
}

/**
 * @see ITypeHierarchy
 * TODO (jerome) should use a PerThreadObject to build the hierarchy instead of synchronizing
 * (see also isAffected(IRubyElementDelta))
 */
public synchronized void refresh(IProgressMonitor monitor) throws RubyModelException {
	try {
		this.progressMonitor = monitor;
		if (monitor != null) {
			if (this.focusType != null) {
				monitor.beginTask(Messages.bind(Messages.hierarchy_creatingOnType, this.focusType.getFullyQualifiedName()), 100); 
			} else {
				monitor.beginTask(Messages.hierarchy_creating, 100); 
			}
		}
		long start = -1;
		if (DEBUG) {
			start = System.currentTimeMillis();
			if (this.computeSubtypes) {
				System.out.println("CREATING TYPE HIERARCHY [" + Thread.currentThread() + "]"); //$NON-NLS-1$ //$NON-NLS-2$
			} else {
				System.out.println("CREATING SUPER TYPE HIERARCHY [" + Thread.currentThread() + "]"); //$NON-NLS-1$ //$NON-NLS-2$
			}
			if (this.focusType != null) {
				System.out.println("  on type " + ((RubyElement)this.focusType).toStringWithAncestors()); //$NON-NLS-1$
			}
		}

		compute();
		initializeRegions();
		this.needsRefresh = false;
		this.changeCollector = null;

		if (DEBUG) {
			if (this.computeSubtypes) {
				System.out.println("CREATED TYPE HIERARCHY in " + (System.currentTimeMillis() - start) + "ms"); //$NON-NLS-1$ //$NON-NLS-2$
			} else {
				System.out.println("CREATED SUPER TYPE HIERARCHY in " + (System.currentTimeMillis() - start) + "ms"); //$NON-NLS-1$ //$NON-NLS-2$
			}
			System.out.println(this.toString());
		}
	} catch (RubyModelException e) {
		throw e;
	} catch (CoreException e) {
		throw new RubyModelException(e);
	} finally {
		if (monitor != null) {
			monitor.done();
		}
		this.progressMonitor = null;
	}
}

/**
 * @see ITypeHierarchy
 */
public synchronized void removeTypeHierarchyChangedListener(ITypeHierarchyChangedListener listener) {
	ArrayList listeners = this.changeListeners;
	if (listeners == null) {
		return;
	}
	listeners.remove(listener);

	// deregister from RubyCore on last listener removed
	if (listeners.isEmpty()) {
		RubyCore.removeElementChangedListener(this);
	}
}
/**
 * @see ITypeHierarchy
 */
public void store(OutputStream output, IProgressMonitor monitor) throws RubyModelException {
	try {
		// compute types in hierarchy
		Hashtable hashtable = new Hashtable();
		Hashtable hashtable2 = new Hashtable();
		int count = 0;
		
		if(this.focusType != null) {
			Integer index = new Integer(count++);
			hashtable.put(this.focusType, index);
			hashtable2.put(index, this.focusType);
		}
		Object[] types = this.classToSuperclass.keySet().toArray();
		for (int i = 0; i < types.length; i++) {
			Object t = types[i];
			if(hashtable.get(t) == null) {
				Integer index = new Integer(count++);
				hashtable.put(t, index);
				hashtable2.put(index, t);
			}
			Object superClass = this.classToSuperclass.get(t);
			if(superClass != null && hashtable.get(superClass) == null) {
				Integer index = new Integer(count++);
				hashtable.put(superClass, index);
				hashtable2.put(index, superClass);
			}
		}
		types = this.typeToSuperModules.keySet().toArray();
		for (int i = 0; i < types.length; i++) {
			Object t = types[i];
			if(hashtable.get(t) == null) {
				Integer index = new Integer(count++);
				hashtable.put(t, index);
				hashtable2.put(index, t);
			}
			Object[] sp = (Object[])this.typeToSuperModules.get(t);
			if(sp != null) {
				for (int j = 0; j < sp.length; j++) {
					Object superInterface = sp[j];
					if(sp[j] != null && hashtable.get(superInterface) == null) {
						Integer index = new Integer(count++);
						hashtable.put(superInterface, index);
						hashtable2.put(index, superInterface);
					}
				}
			}
		}
		// save version of the hierarchy format
		output.write(VERSION);
		
		// save general info
		byte generalInfo = 0;
		if(this.computeSubtypes) {
			generalInfo |= COMPUTE_SUBTYPES;
		}
		output.write(generalInfo);
		
		// save project
		if(this.project != null) {
			output.write(this.project.getHandleIdentifier().getBytes());
		}
		output.write(SEPARATOR1);
		
		// save missing types
		for (int i = 0; i < this.missingTypes.size(); i++) {
			if(i != 0) {
				output.write(SEPARATOR2);
			}
			output.write(((String)this.missingTypes.get(i)).getBytes());
			
		}
		output.write(SEPARATOR1);
		
		// save types
		for (int i = 0; i < count ; i++) {
			IType t = (IType)hashtable2.get(new Integer(i));
			
			// n bytes
			output.write(t.getHandleIdentifier().getBytes());
			output.write(SEPARATOR4);
			output.write(flagsToBytes((Integer)this.typeFlags.get(t)));
			output.write(SEPARATOR4);
			byte info = CLASS;
			if(this.focusType != null && this.focusType.equals(t)) {
				info |= COMPUTED_FOR;
			}
			if(this.modules.contains(t)) {
				info |= INTERFACE;
			}
			if(this.rootClasses.contains(t)) {
				info |= ROOT;
			}
			output.write(info);
		}
		output.write(SEPARATOR1);
		
		// save superclasses
		types = this.classToSuperclass.keySet().toArray();
		for (int i = 0; i < types.length; i++) {
			IRubyElement key = (IRubyElement)types[i];
			IRubyElement value = (IRubyElement)this.classToSuperclass.get(key);
			
			output.write(((Integer)hashtable.get(key)).toString().getBytes());
			output.write('>');
			output.write(((Integer)hashtable.get(value)).toString().getBytes());
			output.write(SEPARATOR1);
		}
		output.write(SEPARATOR1);
		
		// save superinterfaces
		types = this.typeToSuperModules.keySet().toArray();
		for (int i = 0; i < types.length; i++) {
			IRubyElement key = (IRubyElement)types[i];
			IRubyElement[] values = (IRubyElement[])this.typeToSuperModules.get(key);
			
			if(values.length > 0) {
				output.write(((Integer)hashtable.get(key)).toString().getBytes());
				output.write(SEPARATOR3);
				for (int j = 0; j < values.length; j++) {
					IRubyElement value = values[j];
					if(j != 0) output.write(SEPARATOR2);
					output.write(((Integer)hashtable.get(value)).toString().getBytes());
				}
				output.write(SEPARATOR1);
			}
		}
		output.write(SEPARATOR1);
	} catch(IOException e) {
		throw new RubyModelException(e, IRubyModelStatusConstants.IO_EXCEPTION);
	}
}
/**
 * Returns whether the simple name of a supertype of the given type is 
 * the simple name of one of the subtypes in this hierarchy or the
 * simple name of this type.
 */
boolean subtypesIncludeSupertypeOf(IType type) {
	// look for superclass
	String superclassName = null;
	try {
		superclassName = type.getSuperclassName();
	} catch (RubyModelException e) {
		if (DEBUG) {
			e.printStackTrace();
		}
		return false;
	}
	if (superclassName == null) {
		superclassName = "Object"; //$NON-NLS-1$
	}
	int dot = -1;
	String simpleSuper = (dot = superclassName.lastIndexOf('.')) > -1 ?
		superclassName.substring(dot + 1) :
		superclassName;
	if (hasSubtypeNamed(simpleSuper)) {
		return true;
	}

	// look for super interfaces
	String[] interfaceNames = null;
	try {
		interfaceNames = type.getIncludedModuleNames();
	} catch (RubyModelException e) {
		if (DEBUG)
			e.printStackTrace();
		return false;
	}
	for (int i = 0, length = interfaceNames.length; i < length; i++) {
		dot = -1;
		String interfaceName = interfaceNames[i];
		String simpleInterface = (dot = interfaceName.lastIndexOf('.')) > -1 ?
			interfaceName.substring(dot) :
			interfaceName;
		if (hasSubtypeNamed(simpleInterface)) {
			return true;
		}
	}
	
	return false;
}
/**
 * @see ITypeHierarchy
 */
public String toString() {
	StringBuffer buffer = new StringBuffer();
	buffer.append("Focus: "); //$NON-NLS-1$
	buffer.append(this.focusType == null ? "<NONE>" : ((RubyElement)this.focusType).toStringWithAncestors(false/*don't show key*/)); //$NON-NLS-1$
	buffer.append("\n"); //$NON-NLS-1$
	if (exists()) {
		if (this.focusType != null) {
			buffer.append("Super types:\n"); //$NON-NLS-1$
			toString(buffer, this.focusType, 1, true);
			buffer.append("Sub types:\n"); //$NON-NLS-1$
			toString(buffer, this.focusType, 1, false);
		} else {
			buffer.append("Sub types of root classes:\n"); //$NON-NLS-1$
			IRubyElement[] roots = Util.sortCopy(getRootClasses());
			for (int i= 0; i < roots.length; i++) {
				toString(buffer, (IType) roots[i], 1, false);
			}
		}
		if (this.rootClasses.size > 1) {
			buffer.append("Root classes:\n"); //$NON-NLS-1$
			IRubyElement[] roots = Util.sortCopy(getRootClasses());
			for (int i = 0, length = roots.length; i < length; i++) {
				toString(buffer, (IType) roots[i], 1, false);
			}
		} else if (this.rootClasses.size == 0) {
			// see http://bugs.eclipse.org/bugs/show_bug.cgi?id=24691
			buffer.append("No root classes"); //$NON-NLS-1$
		}
	} else {
		buffer.append("(Hierarchy became stale)"); //$NON-NLS-1$
	}
	return buffer.toString();
}
/**
 * Append a String to the given buffer representing the hierarchy for the type,
 * beginning with the specified indentation level.
 * If ascendant, shows the super types, otherwise show the sub types.
 */
private void toString(StringBuffer buffer, IType type, int indent, boolean ascendant) {
	IType[] types= ascendant ? getSupertypes(type) : getSubtypes(type);
	IRubyElement[] sortedTypes = Util.sortCopy(types);
	for (int i= 0; i < sortedTypes.length; i++) {
		for (int j= 0; j < indent; j++) {
			buffer.append("  "); //$NON-NLS-1$
		}
		RubyElement element = (RubyElement)sortedTypes[i];
		buffer.append(element.toStringWithAncestors(false/*don't show key*/));
		buffer.append('\n');
		toString(buffer, types[i], indent + 1, ascendant);
	}
}
/**
 * Returns whether one of the types in this hierarchy has a supertype whose simple 
 * name is the given simple name.
 */
boolean hasSupertype(String simpleName) {
	for(Iterator iter = this.classToSuperclass.values().iterator(); iter.hasNext();){
		IType superType = (IType)iter.next();
		if (superType.getElementName().equals(simpleName)) {
			return true;
		}
	}
	return false;
}
/**
 * @see IProgressMonitor
 */
protected void worked(int work) {
	if (this.progressMonitor != null) {
		this.progressMonitor.worked(work);
		checkCanceled();
	}
}
}
