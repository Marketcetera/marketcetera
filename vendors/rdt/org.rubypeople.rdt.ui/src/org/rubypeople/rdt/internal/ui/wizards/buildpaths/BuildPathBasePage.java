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
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IPath;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.rubypeople.rdt.core.ILoadpathEntry;
import org.rubypeople.rdt.core.IRubyProject;
import org.rubypeople.rdt.internal.corext.util.RubyModelUtil;

public abstract class BuildPathBasePage {
	
	public abstract List getSelection();
	public abstract void setSelection(List selection, boolean expand);
	
	public void addElement(CPListElement element) {
		
	}
	
	public abstract boolean isEntryKind(int kind);
			
	protected void filterAndSetSelection(List list) {
		ArrayList res= new ArrayList(list.size());
		for (int i= list.size()-1; i >= 0; i--) {
			Object curr= list.get(i);
			if (curr instanceof CPListElement) {
				CPListElement elem= (CPListElement) curr;
				if (elem.getParentContainer() == null && isEntryKind(elem.getEntryKind())) {
					res.add(curr);
				}
			}
		}
		setSelection(res, false);
	}
	
	public static void fixNestingConflicts(CPListElement[] newEntries, CPListElement[] existing, Set modifiedSourceEntries) {
		for (int i= 0; i < newEntries.length; i++) {
			addExclusionPatterns(newEntries[i], existing, modifiedSourceEntries);
		}
	}
	
	private static void addExclusionPatterns(CPListElement newEntry, CPListElement[] existing, Set modifiedEntries) {
		IPath entryPath= newEntry.getPath();
		for (int i= 0; i < existing.length; i++) {
			CPListElement curr= existing[i];
			if (curr.getEntryKind() == ILoadpathEntry.CPE_SOURCE) {
				IPath currPath= curr.getPath();
				if (!currPath.equals(entryPath)) {
					if (currPath.isPrefixOf(entryPath)) {
						if (addToExclusions(entryPath, curr)) {
							modifiedEntries.add(curr);
						}
					} else if (entryPath.isPrefixOf(currPath)) {
						if (addToExclusions(currPath, newEntry)) {
							modifiedEntries.add(curr);
						}
					}
				}
			}
		}
	}
	
	private static boolean addToExclusions(IPath entryPath, CPListElement curr) {
		IPath[] exclusionFilters= (IPath[]) curr.getAttribute(CPListElement.EXCLUSION);
		if (!RubyModelUtil.isExcludedPath(entryPath, exclusionFilters)) {
			IPath pathToExclude= entryPath.removeFirstSegments(curr.getPath().segmentCount()).addTrailingSeparator();
			IPath[] newExclusionFilters= new IPath[exclusionFilters.length + 1];
			System.arraycopy(exclusionFilters, 0, newExclusionFilters, 0, exclusionFilters.length);
			newExclusionFilters[exclusionFilters.length]= pathToExclude;
			curr.setAttribute(CPListElement.EXCLUSION, newExclusionFilters);
			return true;
		}
		return false;
	}
	
	protected boolean containsOnlyTopLevelEntries(List selElements) {
		if (selElements.size() == 0) {
			return true;
		}
		for (int i= 0; i < selElements.size(); i++) {
			Object elem= selElements.get(i);
			if (elem instanceof CPListElement) {
				if (((CPListElement) elem).getParentContainer() != null) {
					return false;
				}
			} else {
				return false;
			}
		}
		return true;
	}

	public abstract void init(IRubyProject rubyProject);

	public abstract Control getControl(Composite parent);
	
}

