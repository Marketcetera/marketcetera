/**
 * Copyright (c) 2008 Aptana, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl -v10.html. If redistributing this code,
 * this entire header must remain intact.
 *
 * This file is based on a JDT equivalent:
 *******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.rubypeople.rdt.internal.ui.wizards.buildpaths;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.rubypeople.rdt.core.ILoadpathAttribute;
import org.rubypeople.rdt.core.ILoadpathContainer;
import org.rubypeople.rdt.core.ILoadpathEntry;
import org.rubypeople.rdt.core.IRubyProject;
import org.rubypeople.rdt.core.LoadpathContainerInitializer;
import org.rubypeople.rdt.core.RubyCore;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.internal.corext.util.RubyModelUtil;
import org.rubypeople.rdt.internal.ui.RubyPlugin;
import org.rubypeople.rdt.launching.RubyRuntime;

public class CPListElement {

	public static final String EXCLUSION = "exclusion"; //$NON-NLS-1$
	public static final String INCLUSION = "inclusion"; //$NON-NLS-1$

	private IRubyProject fProject;

	private int fEntryKind;
	private IPath fPath, fOrginalPath;
	private IResource fResource;
	private boolean fIsExported;
	private boolean fIsMissing;

	private Object fParentContainer;

	private ILoadpathEntry fCachedEntry;
	private ArrayList fChildren;
	private IPath fLinkTarget, fOrginalLinkTarget;

	public CPListElement(IRubyProject project, int entryKind, IPath path,
			IResource res) {
		this(null, project, entryKind, path, res);
	}

	public CPListElement(Object parent, IRubyProject project, int entryKind,
			IPath path, IResource res) {
		this(parent, project, entryKind, path, res, null);
	}

	public CPListElement(IRubyProject project, int entryKind) {
		this(null, project, entryKind, null, null);
	}

	public CPListElement(Object parent, IRubyProject project, int entryKind,
			IPath path, IResource res, IPath linkTarget) {
		fProject = project;

		fEntryKind = entryKind;
		fPath = path;
		fOrginalPath = path;
		fLinkTarget = linkTarget;
		fOrginalLinkTarget = linkTarget;
		fChildren = new ArrayList();
		fResource = res;
		fIsExported = false;

		fIsMissing = false;
		fCachedEntry = null;
		fParentContainer = parent;

		switch (entryKind) {
		case ILoadpathEntry.CPE_SOURCE:
			createAttributeElement(INCLUSION, new Path[0], true);
			createAttributeElement(EXCLUSION, new Path[0], true);
			break;
		case ILoadpathEntry.CPE_LIBRARY:
		case ILoadpathEntry.CPE_VARIABLE:
			break;
		case ILoadpathEntry.CPE_PROJECT:
			break;
		case ILoadpathEntry.CPE_CONTAINER:
			try {
				ILoadpathContainer container = RubyCore.getLoadpathContainer(
						fPath, fProject);
				if (container != null) {
					ILoadpathEntry[] entries = container.getLoadpathEntries();
					for (int i = 0; i < entries.length; i++) {
						ILoadpathEntry entry = entries[i];
						if (entry != null) {
							CPListElement curr = createFromExisting(this,
									entry, fProject);
							fChildren.add(curr);
						} else {
							RubyPlugin
									.logErrorMessage("Null entry in container '" + fPath + "'"); //$NON-NLS-1$//$NON-NLS-2$
						}
					}
				}
			} catch (RubyModelException e) {
			}
			break;
		default:
		}

	}

	public ILoadpathEntry getLoadpathEntry() {
		if (fCachedEntry == null) {
			fCachedEntry = newLoadpathEntry();
		}
		return fCachedEntry;
	}

	private ILoadpathAttribute[] getLoadpathAttributes() {
		ArrayList res = new ArrayList();
		for (int i = 0; i < fChildren.size(); i++) {
			Object curr = fChildren.get(i);
			if (curr instanceof CPListElementAttribute) {
				CPListElementAttribute elem = (CPListElementAttribute) curr;
				if (!elem.isBuiltIn() && elem.getValue() != null) {
					res.add(elem.newLoadpathAttribute());
				}
			}
		}
		return (ILoadpathAttribute[]) res.toArray(new ILoadpathAttribute[res
				.size()]);
	}

	private ILoadpathEntry newLoadpathEntry() {

		ILoadpathAttribute[] extraAttributes = getLoadpathAttributes();
		switch (fEntryKind) {
		case ILoadpathEntry.CPE_SOURCE:
			IPath[] inclusionPattern = (IPath[]) getAttribute(INCLUSION);
			IPath[] exclusionPattern = (IPath[]) getAttribute(EXCLUSION);
			return RubyCore.newSourceEntry(fPath, inclusionPattern,
					exclusionPattern, extraAttributes);
		case ILoadpathEntry.CPE_LIBRARY: {
			return RubyCore.newLibraryEntry(fPath, extraAttributes,
					isExported());
		}
		case ILoadpathEntry.CPE_PROJECT: {
			return RubyCore.newProjectEntry(fPath, extraAttributes,
					isExported());
		}
		case ILoadpathEntry.CPE_CONTAINER: {
			return RubyCore.newContainerEntry(fPath, extraAttributes,
					isExported());
		}
		case ILoadpathEntry.CPE_VARIABLE: {
			return RubyCore.newVariableEntry(fPath, extraAttributes,
					isExported());
		}
		default:
			return null;
		}
	}

	/**
	 * Gets the class path entry path.
	 * 
	 * @see ILoadpathEntry#getPath()
	 */
	public IPath getPath() {
		return fPath;
	}

	/**
	 * Gets the class path entry kind.
	 * 
	 * @see ILoadpathEntry#getEntryKind()
	 */
	public int getEntryKind() {
		return fEntryKind;
	}

	/**
	 * Entries without resource are either non existing or a variable entry
	 * External jars do not have a resource
	 */
	public IResource getResource() {
		return fResource;
	}

	public CPListElementAttribute setAttribute(String key, Object value) {
		CPListElementAttribute attribute = findAttributeElement(key);
		if (attribute == null) {
			return null;
		}
		if (key.equals(EXCLUSION) || key.equals(INCLUSION)) {
			Assert.isTrue(value != null
					|| fEntryKind != ILoadpathEntry.CPE_SOURCE);
		}

		attribute.setValue(value);
		attributeChanged(key);
		return attribute;
	}

	public boolean addToExclusions(IPath path) {
		String key = CPListElement.EXCLUSION;
		return addFilter(path, key);
	}

	public boolean addToInclusion(IPath path) {
		String key = CPListElement.INCLUSION;
		return addFilter(path, key);
	}

	public boolean removeFromExclusions(IPath path) {
		String key = CPListElement.EXCLUSION;
		return removeFilter(path, key);
	}

	public boolean removeFromInclusion(IPath path) {
		String key = CPListElement.INCLUSION;
		return removeFilter(path, key);
	}

	private boolean addFilter(IPath path, String key) {
		IPath[] exclusionFilters = (IPath[]) getAttribute(key);
		if (!RubyModelUtil.isExcludedPath(path, exclusionFilters)) {
			IPath pathToExclude = path.removeFirstSegments(
					getPath().segmentCount()).addTrailingSeparator();
			IPath[] newExclusionFilters = new IPath[exclusionFilters.length + 1];
			System.arraycopy(exclusionFilters, 0, newExclusionFilters, 0,
					exclusionFilters.length);
			newExclusionFilters[exclusionFilters.length] = pathToExclude;
			setAttribute(key, newExclusionFilters);
			return true;
		}
		return false;
	}

	private boolean removeFilter(IPath path, String key) {
		IPath[] exclusionFilters = (IPath[]) getAttribute(key);
		IPath pathToExclude = path
				.removeFirstSegments(getPath().segmentCount())
				.addTrailingSeparator();
		if (RubyModelUtil.isExcludedPath(pathToExclude, exclusionFilters)) {

			List l = new ArrayList(Arrays.asList(exclusionFilters));
			l.remove(pathToExclude);
			IPath[] newExclusionFilters = (IPath[]) l.toArray(new IPath[l
					.size()]);
			setAttribute(key, newExclusionFilters);
			return true;
		}
		return false;
	}

	public CPListElementAttribute findAttributeElement(String key) {
		for (int i = 0; i < fChildren.size(); i++) {
			Object curr = fChildren.get(i);
			if (curr instanceof CPListElementAttribute) {
				CPListElementAttribute elem = (CPListElementAttribute) curr;
				if (key.equals(elem.getKey())) {
					return elem;
				}
			}
		}
		return null;
	}

	public Object getAttribute(String key) {
		CPListElementAttribute attrib = findAttributeElement(key);
		if (attrib != null) {
			return attrib.getValue();
		}
		return null;
	}

	private void createAttributeElement(String key, Object value,
			boolean builtIn) {
		fChildren.add(new CPListElementAttribute(this, key, value, builtIn));
	}

	private static boolean isFiltered(Object entry, String[] filteredKeys) {
		if (entry instanceof CPListElementAttribute) {
			String key = ((CPListElementAttribute) entry).getKey();
			for (int i = 0; i < filteredKeys.length; i++) {
				if (key.equals(filteredKeys[i])) {
					return true;
				}
			}
		}
		return false;
	}

	private Object[] getFilteredChildren(String[] filteredKeys) {
		int nChildren = fChildren.size();
		ArrayList res = new ArrayList(nChildren);

		for (int i = 0; i < nChildren; i++) {
			Object curr = fChildren.get(i);
			if (!isFiltered(curr, filteredKeys)) {
				res.add(curr);
			}
		}
		return res.toArray();
	}

	public Object[] getChildren(boolean hideOutputFolder) {
		if (hideOutputFolder && fEntryKind == ILoadpathEntry.CPE_SOURCE) {
			return getFilteredChildren(new String[] {});
		}
		if (fParentContainer instanceof CPListElement) {
			IPath jreContainerPath = new Path(RubyRuntime.RUBY_CONTAINER);
			if (jreContainerPath.isPrefixOf(((CPListElement) fParentContainer)
					.getPath())) {
				// don't show access rules and native path for containers (bug
				// 98710)
				return getFilteredChildren(new String[] {});
			}
		}
		if (fEntryKind == ILoadpathEntry.CPE_PROJECT) {
			return getFilteredChildren(new String[] {});
		}
		return fChildren.toArray();
	}

	public Object getParentContainer() {
		return fParentContainer;
	}

	private void attributeChanged(String key) {
		fCachedEntry = null;
	}

	private boolean canUpdateContainer() {
		if (fEntryKind == ILoadpathEntry.CPE_CONTAINER && fProject != null) {
			LoadpathContainerInitializer initializer = RubyCore
					.getLoadpathContainerInitializer(fPath.segment(0));
			return (initializer != null && initializer
					.canUpdateLoadpathContainer(fPath, fProject));
		}
		return false;
	}

	public boolean isInNonModifiableContainer() {
		if (fParentContainer instanceof CPListElement) {
			return !((CPListElement) fParentContainer).canUpdateContainer();
		}
		return false;
	}

	/*
	 * @see Object#equals(java.lang.Object)
	 */
	public boolean equals(Object other) {
		if (other != null && other.getClass().equals(getClass())) {
			CPListElement elem = (CPListElement) other;
			return getLoadpathEntry().equals(elem.getLoadpathEntry());
		}
		return false;
	}

	/*
	 * @see Object#hashCode()
	 */
	public int hashCode() {
		return fPath.hashCode() + fEntryKind;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return getLoadpathEntry().toString();
	}

	/**
	 * Returns if a entry is missing.
	 * 
	 * @return Returns a boolean
	 */
	public boolean isMissing() {
		return fIsMissing;
	}

	/**
	 * Sets the 'missing' state of the entry.
	 */
	public void setIsMissing(boolean isMissing) {
		fIsMissing = isMissing;
	}

	/**
	 * Returns if a entry is exported (only applies to libraries)
	 * 
	 * @return Returns a boolean
	 */
	public boolean isExported() {
		return fIsExported;
	}

	/**
	 * Sets the export state of the entry.
	 */
	public void setExported(boolean isExported) {
		if (isExported != fIsExported) {
			fIsExported = isExported;

			attributeChanged(null);
		}
	}

	/**
	 * Gets the project.
	 * 
	 * @return Returns a IRubyProject
	 */
	public IRubyProject getRubyProject() {
		return fProject;
	}

	public static CPListElement createFromExisting(ILoadpathEntry curr,
			IRubyProject project) {
		return createFromExisting(null, curr, project);
	}

	public static CPListElement createFromExisting(Object parent,
			ILoadpathEntry curr, IRubyProject project) {
		IPath path = curr.getPath();
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();

		// get the resource
		IResource res = null;
		boolean isMissing = false;
		IPath linkTarget = null;

		switch (curr.getEntryKind()) {
		case ILoadpathEntry.CPE_CONTAINER:
			res = null;
			try {
				isMissing = project != null
						&& (RubyCore.getLoadpathContainer(path, project) == null);
			} catch (RubyModelException e) {
				isMissing = true;
			}
			break;
		case ILoadpathEntry.CPE_VARIABLE:
			IPath resolvedPath = RubyCore.getResolvedVariablePath(path);
			res = null;
			if (resolvedPath == null) {
				isMissing = true;
			} else {
				isMissing = !resolvedPath.toFile().isDirectory();
			}
			break;
		case ILoadpathEntry.CPE_LIBRARY:
			res = root.findMember(path);
			if (res == null) {
				if (root.getWorkspace().validatePath(path.toString(),
						IResource.FOLDER).isOK()
						&& root.getProject(path.segment(0)).exists()) {
					res = root.getFolder(path);
				}
				isMissing = !path.toFile().isDirectory(); // look for external
															// Folders
			} else if (res.isLinked()) {
				linkTarget = res.getLocation();
			}
			break;
		case ILoadpathEntry.CPE_SOURCE:
			path = path.removeTrailingSeparator();
			res = root.findMember(path);
			if (res == null) {
				if (root.getWorkspace().validatePath(path.toString(),
						IResource.FOLDER).isOK()) {
					res = root.getFolder(path);
				}
				isMissing = true;
			} else if (res.isLinked()) {
				linkTarget = res.getLocation();
			}
			break;
		case ILoadpathEntry.CPE_PROJECT:
			res = root.findMember(path);
			isMissing = (res == null);
			break;
		}
		CPListElement elem = new CPListElement(parent, project, curr
				.getEntryKind(), path, res, linkTarget);
		elem.setExported(curr.isExported());
		elem.setAttribute(EXCLUSION, curr.getExclusionPatterns());
		elem.setAttribute(INCLUSION, curr.getInclusionPatterns());

		ILoadpathAttribute[] extraAttributes = curr.getExtraAttributes();
		for (int i = 0; i < extraAttributes.length; i++) {
			ILoadpathAttribute attrib = extraAttributes[i];
			elem.setAttribute(attrib.getName(), attrib.getValue());
		}

		if (project != null && project.exists()) {
			elem.setIsMissing(isMissing);
		}
		return elem;
	}

	public static StringBuffer appendEncodePath(IPath path, StringBuffer buf) {
		if (path != null) {
			String str = path.toString();
			buf.append('[').append(str.length()).append(']').append(str);
		} else {
			buf.append('[').append(']');
		}
		return buf;
	}

	public static StringBuffer appendEncodedString(String str, StringBuffer buf) {
		if (str != null) {
			buf.append('[').append(str.length()).append(']').append(str);
		} else {
			buf.append('[').append(']');
		}
		return buf;
	}

	public static StringBuffer appendEncodedFilter(IPath[] filters,
			StringBuffer buf) {
		if (filters != null) {
			buf.append('[').append(filters.length).append(']');
			for (int i = 0; i < filters.length; i++) {
				appendEncodePath(filters[i], buf).append(';');
			}
		} else {
			buf.append('[').append(']');
		}
		return buf;
	}

	public StringBuffer appendEncodedSettings(StringBuffer buf) {
		buf.append(fEntryKind).append(';');
		if (getLinkTarget() == null) {
			appendEncodePath(fPath, buf).append(';');
		} else {
			appendEncodePath(fPath, buf).append('-').append('>');
			appendEncodePath(getLinkTarget(), buf).append(';');
		}
		buf.append(Boolean.valueOf(fIsExported)).append(';');
		for (int i = 0; i < fChildren.size(); i++) {
			Object curr = fChildren.get(i);
			if (curr instanceof CPListElementAttribute) {
				CPListElementAttribute elem = (CPListElementAttribute) curr;
				if (elem.isBuiltIn()) {
					String key = elem.getKey();
					if (EXCLUSION.equals(key) || INCLUSION.equals(key)) {
						appendEncodedFilter((IPath[]) elem.getValue(), buf)
								.append(';');
					}
				} else {
					appendEncodedString((String) elem.getValue(), buf);
				}
			}
		}
		return buf;
	}

	public IPath getLinkTarget() {
		return fLinkTarget;
	}

	public void setPath(IPath path) {
		fCachedEntry = null;
		fPath = path;
	}

	public void setLinkTarget(IPath linkTarget) {
		fCachedEntry = null;
		fLinkTarget = linkTarget;
	}

	public static void insert(CPListElement element, List cpList) {
		int length = cpList.size();
		CPListElement[] elements = (CPListElement[]) cpList
				.toArray(new CPListElement[length]);
		int i = 0;
		while (i < length
				&& elements[i].getEntryKind() != element.getEntryKind()) {
			i++;
		}
		if (i < length) {
			i++;
			while (i < length
					&& elements[i].getEntryKind() == element.getEntryKind()) {
				i++;
			}
			cpList.add(i, element);
			return;
		}

		switch (element.getEntryKind()) {
		case ILoadpathEntry.CPE_SOURCE:
			cpList.add(0, element);
			break;
		case ILoadpathEntry.CPE_CONTAINER:
		case ILoadpathEntry.CPE_LIBRARY:
		case ILoadpathEntry.CPE_PROJECT:
		case ILoadpathEntry.CPE_VARIABLE:
		default:
			cpList.add(element);
			break;
		}
	}

	public static ILoadpathEntry[] convertToLoadpathEntries(
			List/* <CPListElement> */cpList) {
		ILoadpathEntry[] result = new ILoadpathEntry[cpList.size()];
		int i = 0;
		for (Iterator iter = cpList.iterator(); iter.hasNext();) {
			CPListElement cur = (CPListElement) iter.next();
			result[i] = cur.getLoadpathEntry();
			i++;
		}
		return result;
	}

	public static CPListElement[] createFromExisting(IRubyProject project)
			throws RubyModelException {
		ILoadpathEntry[] rawLoadpath = project.getRawLoadpath();
		CPListElement[] result = new CPListElement[rawLoadpath.length];
		for (int i = 0; i < rawLoadpath.length; i++) {
			result[i] = CPListElement.createFromExisting(rawLoadpath[i],
					project);
		}
		return result;
	}

	public static boolean isProjectSourceFolder(CPListElement[] existing,
			IRubyProject project) {
		IPath projPath = project.getProject().getFullPath();
		for (int i = 0; i < existing.length; i++) {
			ILoadpathEntry curr = existing[i].getLoadpathEntry();
			if (curr.getEntryKind() == ILoadpathEntry.CPE_SOURCE) {
				if (projPath.equals(curr.getPath())) {
					return true;
				}
			}
		}
		return false;
	}

	public IPath getOrginalPath() {
		return fOrginalPath;
	}

	public IPath getOrginalLinkTarget() {
		return fOrginalLinkTarget;
	}

}
