/*
 * Author: C.Williams
 *
 *  Copyright (c) 2004 RubyPeople. 
 *
 *  This file is part of the Ruby Development Tools (RDT) plugin for eclipse.
 *  You can get copy of the GPL along with further information about RubyPeople 
 *  and third party software bundled with RDT in the file 
 *  org.rubypeople.rdt.core_0.4.0/RDT.license or otherwise at 
 *  http://www.rubypeople.org/RDT.license.
 *
 *  RDT is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  RDT is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of 
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with RDT; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package org.rubypeople.rdt.core;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;

public interface IRubyElement extends IAdaptable {

	public static final int RUBY_MODEL = 0;
	/**
	 * Constant representing a Ruby project.
	 * A Ruby element with this type can be safely cast to <code>IRubyProject</code>.
	 */
	public static final int RUBY_PROJECT = 1;
	public static final int SOURCE_FOLDER_ROOT = 2;
	/**
	 * Constant representing a source folder
	 * A Ruby element with this type can be safely cast to <code>ISourceFolder</code>.
	 */
	public static final int SOURCE_FOLDER = 3;
	public static final int SCRIPT = 4;
	public static final int TYPE = 5;
	public static final int METHOD = 6;
	public static final int GLOBAL = 7;
	public static final int IMPORT_DECLARATION = 8;
	public static final int CONSTANT = 9;
	public static final int CLASS_VAR = 10;
	public static final int INSTANCE_VAR = 11;
	public static final int LOCAL_VARIABLE = 12;
	public static final int BLOCK = 13;
	public static final int DYNAMIC_VAR = 14;
	public static final int FIELD = 15;
	public static final int IMPORT_CONTAINER = 16;

	/**
	 * Returns the first ancestor of this Ruby element that has the given type.
	 * Returns <code>null</code> if no such an ancestor can be found. This is
	 * a handle-only method.
	 * 
	 * @param ancestorType
	 *            the given type
	 * @return the first ancestor of this Ruby element that has the given type,
	 *         null if no such an ancestor can be found
	 * @since 2.0
	 */
	IRubyElement getAncestor(int ancestorType);

	/**
	 * Returns whether this Java element exists in the model.
	 * <p>
	 * Java elements are handle objects that may or may not be backed by an
	 * actual element. Java elements that are backed by an actual element are
	 * said to "exist", and this method returns <code>true</code>. For Java
	 * elements that are not working copies, it is always the case that if the
	 * element exists, then its parent also exists (provided it has one) and
	 * includes the element as one of its children. It is therefore possible to
	 * navigated to any existing Java element from the root of the Java model
	 * along a chain of existing Java elements. On the other hand, working
	 * copies are said to exist until they are destroyed (with
	 * <code>IWorkingCopy.destroy</code>). Unlike regular Java elements, a
	 * working copy never shows up among the children of its parent element
	 * (which may or may not exist).
	 * </p>
	 * 
	 * @return <code>true</code> if this element exists in the Java model, and
	 *         <code>false</code> if this element does not exist
	 */
	boolean exists();

	/**
	 * Returns the name of this element. This is a handle-only method.
	 * 
	 * @return the element name
	 */
	String getElementName();
	
	/**
	 * Returns the resource that corresponds directly to this element,
	 * or <code>null</code> if there is no resource that corresponds to
	 * this element.
	 * <p>
	 * For example, the corresponding resource for an <code>ICompilationUnit</code>
	 * is its underlying <code>IFile</code>. The corresponding resource for
	 * an <code>IPackageFragment</code> that is not contained in an archive 
	 * is its underlying <code>IFolder</code>. An <code>IPackageFragment</code>
	 * contained in an archive has no corresponding resource. Similarly, there
	 * are no corresponding resources for <code>IMethods</code>,
	 * <code>IFields</code>, etc.
	 * <p>
	 *
	 * @return the corresponding resource, or <code>null</code> if none
	 * @exception RubyModelException if this element does not exist or if an
	 *		exception occurs while accessing its corresponding resource
	 */
	IResource getCorrespondingResource() throws RubyModelException;

	/**
	 * Returns the smallest underlying resource that contains this element, or
	 * <code>null</code> if this element is not contained in a resource.
	 * 
	 * @return the underlying resource, or <code>null</code> if none
	 * @exception RubyModelException
	 *                if this element does not exist or if an exception occurs
	 *                while accessing its underlying resource
	 */
	IResource getUnderlyingResource() throws RubyModelException;

	/**
	 * Returns the first openable parent. If this element is openable, the
	 * element itself is returned. Returns <code>null</code> if this element
	 * doesn't have an openable parent. This is a handle-only method.
	 * 
	 * @return the first openable parent or <code>null</code> if this element
	 *         doesn't have an openable parent.
	 * @since 2.0
	 */
	IOpenable getOpenable();

	/**
	 * Returns the element directly containing this element, or
	 * <code>null</code> if this element has no parent. This is a handle-only
	 * method.
	 * 
	 * @return the parent element, or <code>null</code> if this element has no
	 *         parent
	 */
	IRubyElement getParent();

	/**
	 * @param type
	 * @return
	 */
	boolean isType(int type);

	/**
	 * Returns the path to the innermost resource enclosing this element. If
	 * this element is not included in an external archive, the path returned is
	 * the full, absolute path to the underlying resource, relative to the
	 * workbench. If this element is included in an external archive, the path
	 * returned is the absolute path to the archive in the file system. This is
	 * a handle-only method.
	 * 
	 * @return the path to the innermost resource enclosing this element
	 * @since 2.0
	 */
	IPath getPath();

	/**
	 * @return
	 */
	int getElementType();

	/**
	 * Returns the Ruby project this element is contained in, or
	 * <code>null</code> if this element is not contained in any Ruby project
	 * (for instance, the <code>IRubyModel</code> is not contained in any Ruby
	 * project). This is a handle-only method.
	 * 
	 * @return the containing Ruby project, or <code>null</code> if this
	 *         element is not contained in a Ruby project
	 */
	public IRubyProject getRubyProject();

	/**
	 * Returns the Ruby model. This is a handle-only method.
	 * 
	 * @return the Ruby model
	 */
	IRubyModel getRubyModel();

	/**
	 * Returns whether this Ruby element is read-only. An element is read-only
	 * if its structure cannot be modified by the ruby model.
	 * <p>
	 * Note this is different from IResource.isReadOnly(). For example, .jar
	 * files are read-only as the ruby model doesn't know how to add/remove
	 * elements in this file, but the underlying IFile can be writable.
	 * <p>
	 * This is a handle-only method.
	 * 
	 * @return <code>true</code> if this element is read-only
	 */
	boolean isReadOnly();

	/**
	 * Returns the primary element (whose compilation unit is the primary
	 * compilation unit) this working copy element was created from, or this
	 * element if it is a descendant of a primary compilation unit or if it is
	 * not a descendant of a working copy (e.g. it is a binary member). The
	 * returned element may or may not exist.
	 * 
	 * @return the primary element this working copy element was created from,
	 *         or this element.
	 * @since 3.0
	 */
	IRubyElement getPrimaryElement();

	/**
	 * Returns the innermost resource enclosing this element. If this element is
	 * included in an archive and this archive is not external, this is the
	 * underlying resource corresponding to the archive. If this element is
	 * included in an external archive, <code>null</code> is returned. This is
	 * a handle-only method.
	 * 
	 * @return the innermost resource enclosing this element, <code>null</code>
	 *         if this element is included in an external archive
	 * @since 2.0
	 */
	IResource getResource();

	/**
	 * Returns whether the structure of this element is known. For example, for
	 * a compilation unit that could not be parsed, <code>false</code> is
	 * returned. If the structure of an element is unknown, navigations will
	 * return reasonable defaults. For example, <code>getChildren</code> will
	 * return an empty collection.
	 * <p>
	 * Note: This does not imply anything about consistency with the underlying
	 * resource/buffer contents.
	 * </p>
	 * 
	 * @return <code>true</code> if the structure of this element is known
	 * @exception RubyModelException
	 *                if this element does not exist or if an exception occurs
	 *                while accessing its corresponding resource
	 */
	// TODO (philippe) predicate shouldn't throw an exception
	boolean isStructureKnown() throws RubyModelException;

	public String getHandleIdentifier();
	
}