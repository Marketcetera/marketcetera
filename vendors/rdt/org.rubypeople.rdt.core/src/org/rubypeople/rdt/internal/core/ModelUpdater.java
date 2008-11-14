/*******************************************************************************
 * Copyright (c) 2000, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.rubypeople.rdt.internal.core;

import java.util.HashSet;
import java.util.Iterator;

import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IRubyElementDelta;
import org.rubypeople.rdt.core.IRubyProject;
import org.rubypeople.rdt.core.ISourceFolderRoot;
import org.rubypeople.rdt.core.RubyModelException;

/**
 * This class is used by <code>RubyModelManager</code> to update the RubyModel
 * based on some <code>IRubyElementDelta</code>s.
 */
public class ModelUpdater {

    HashSet projectsToUpdate = new HashSet();

    /**
     * Adds the given child handle to its parent's cache of children.
     */
    protected void addToParentInfo(Openable child) {

        Openable parent = (Openable) child.getParent();
        if (parent != null && parent.isOpen()) {
            try {
                RubyElementInfo info = (RubyElementInfo) parent.getElementInfo();
                info.addChild(child);
            } catch (RubyModelException e) {
                // do nothing - we already checked if open
            }
        }
    }

    /**
     * Closes the given element, which removes it from the cache of open
     * elements.
     */
    protected static void close(Openable element) {

        try {
            element.close();
        } catch (RubyModelException e) {
            // do nothing
        }
    }

    /**
     * Processing for an element that has been added:
     * <ul>
     * <li>If the element is a project, do nothing, and do not process
     * children, as when a project is created it does not yet have any natures -
     * specifically a java nature.
     * <li>If the elemet is not a project, process it as added (see
     * <code>basicElementAdded</code>.
     * </ul>
     */
    protected void elementAdded(Openable element) {

        int elementType = element.getElementType();
        if (elementType == IRubyElement.RUBY_PROJECT) {
            // project add is handled by RubyProject.configure() because
            // when a project is created, it does not yet have a java nature
            addToParentInfo(element);
            this.projectsToUpdate.add(element);
        } else {
            addToParentInfo(element);

            // Force the element to be closed as it might have been opened
            // before the resource modification came in and it might have a new
            // child
            // For example, in an IWorkspaceRunnable:
            // 1. create a package fragment p using a java model operation
            // 2. open package p
            // 3. add file X.java in folder p
            // When the resource delta comes in, only the addition of p is
            // notified,
            // but the package p is already opened, thus its children are not
            // recomputed
            // and it appears empty.
            close(element);
        }
        
        switch (elementType) {
		case IRubyElement.SOURCE_FOLDER_ROOT :
			// when a root is added, and is on the classpath, the project must be updated
			this.projectsToUpdate.add(element.getRubyProject());
			break;
		case IRubyElement.SOURCE_FOLDER :
			// get rid of package fragment cache
			RubyProject project = (RubyProject) element.getRubyProject();
			project.resetCaches();
			break;
	}
    }

    /**
     * Generic processing for elements with changed contents:
     * <ul>
     * <li>The element is closed such that any subsequent accesses will re-open
     * the element reflecting its new structure.
     * </ul>
     */
    protected void elementChanged(Openable element) {

        close(element);
    }

    /**
     * Generic processing for a removed element:
     * <ul>
     * <li>Close the element, removing its structure from the cache
     * <li>Remove the element from its parent's cache of children
     * <li>Add a REMOVED entry in the delta
     * </ul>
     */
    protected void elementRemoved(Openable element) {        
        if (element.isOpen()) {
			close(element);
		}
		removeFromParentInfo(element);
		int elementType = element.getElementType();

		switch (elementType) {
			case IRubyElement.RUBY_MODEL :
//				RubyModelManager.getRubyModelManager().getIndexManager().reset();
				break;
			case IRubyElement.RUBY_PROJECT :
				RubyModelManager manager = RubyModelManager.getRubyModelManager();
				RubyProject javaProject = (RubyProject) element;
				manager.removePerProjectInfo(javaProject);
				manager.containerRemove(javaProject);
				break;
			case IRubyElement.SOURCE_FOLDER_ROOT :
				this.projectsToUpdate.add(element.getRubyProject());
				break;
			case IRubyElement.SOURCE_FOLDER :
				// get rid of package fragment cache
				RubyProject project = (RubyProject) element.getRubyProject();
				project.resetCaches();
				break;
		}
    }

    /**
     * Converts a <code>IResourceDelta</code> rooted in a
     * <code>Workspace</code> into the corresponding set of
     * <code>IRubyElementDelta</code>, rooted in the relevant
     * <code>RubyModel</code>s.
     */
    public void processRubyDelta(IRubyElementDelta delta) {

        // if (DeltaProcessor.VERBOSE){
        // System.out.println("UPDATING Model with Delta:
        // ["+Thread.currentThread()+":" + delta + "]:");
        // }

		try {
			this.traverseDelta(delta, null, null); // traverse delta

			// update package fragment roots of projects that were affected
			Iterator iterator = this.projectsToUpdate.iterator();
			while (iterator.hasNext()) {
				RubyProject project = (RubyProject) iterator.next();
				project.updateSourceFolderRoots();
			}
		} finally {
			this.projectsToUpdate = new HashSet();
		}
    }

    /**
     * Removes the given element from its parents cache of children. If the
     * element does not have a parent, or the parent is not currently open, this
     * has no effect.
     */
    protected void removeFromParentInfo(Openable child) {

        Openable parent = (Openable) child.getParent();
        if (parent != null && parent.isOpen()) {
            try {
                RubyElementInfo info = (RubyElementInfo) parent.getElementInfo();
                info.removeChild(child);
            } catch (RubyModelException e) {
                // do nothing - we already checked if open
            }
        }
    }

    /**
     * Converts an <code>IResourceDelta</code> and its children into the
     * corresponding <code>IRubyElementDelta</code>s. Return whether the
     * delta corresponds to a resource on the classpath. If it is not a resource
     * on the classpath, it will be added as a non-java resource by the sender
     * of this method.
     */
    protected void traverseDelta(IRubyElementDelta delta, ISourceFolderRoot root, 
            IRubyProject project) {

    	boolean processChildren = true;

		Openable element = (Openable) delta.getElement();
		switch (element.getElementType()) {
			case IRubyElement.RUBY_PROJECT :
				project = (IRubyProject) element;
				break;
			case IRubyElement.SOURCE_FOLDER_ROOT :
				root = (ISourceFolderRoot) element;
				break;
			case IRubyElement.SCRIPT :
				// filter out working copies that are not primary (we don't want to add/remove them to/from the package fragment
				RubyScript cu = (RubyScript)element;
				if (cu.isWorkingCopy() && !cu.isPrimary()) {
					return;
				}
		}

		switch (delta.getKind()) {
			case IRubyElementDelta.ADDED :
				elementAdded(element);
				break;
			case IRubyElementDelta.REMOVED :
				elementRemoved(element);
				break;
			case IRubyElementDelta.CHANGED :
				if ((delta.getFlags() & IRubyElementDelta.F_CONTENT) != 0){
					elementChanged(element);
				}
				break;
		}
		if (processChildren) {
			IRubyElementDelta[] children = delta.getAffectedChildren();
			for (int i = 0; i < children.length; i++) {
				IRubyElementDelta childDelta = children[i];
				this.traverseDelta(childDelta, root, project);
			}
		}
    }
}
