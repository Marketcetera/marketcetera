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

import java.util.ArrayList;

import org.eclipse.core.resources.IResourceDelta;
import org.jruby.ast.Node;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IRubyElementDelta;

/**
 * @see IRubyElementDelta
 */
public class RubyElementDelta extends SimpleDelta implements IRubyElementDelta {

    /**
     * @see #getAffectedChildren()
     */
    protected IRubyElementDelta[] affectedChildren = EMPTY_DELTA;

    /*
     * The AST created during the last reconcile operation. Non-null only iff: -
     * in a POST_RECONCILE event - an AST was requested during the last
     * reconcile operation - the changed element is an IRubyScript in
     * working copy mode
     */
    protected Node ast = null;

    /*
     * The element that this delta describes the change to.
     */
    protected IRubyElement changedElement;

    /**
     * Collection of resource deltas that correspond to non java resources
     * deltas.
     */
    protected IResourceDelta[] resourceDeltas = null;

    /**
     * Counter of resource deltas
     */
    protected int resourceDeltasCounter;
    /**
     * @see #getMovedFromElement()
     */
    protected IRubyElement movedFromHandle = null;
    /**
     * @see #getMovedToElement()
     */
    protected IRubyElement movedToHandle = null;
    /**
     * Empty array of IRubyElementDelta
     */
    protected static IRubyElementDelta[] EMPTY_DELTA = new IRubyElementDelta[] {};

    /**
     * Creates the root delta. To create the nested delta hierarchies use the
     * following convenience methods. The root delta can be created at any level
     * (for example: project, package root, package fragment...).
     * <ul>
     * <li><code>added(IRubyElement)</code>
     * <li><code>changed(IRubyElement)</code>
     * <li><code>moved(IRubyElement, IRubyElement)</code>
     * <li><code>removed(IRubyElement)</code>
     * <li><code>renamed(IRubyElement, IRubyElement)</code>
     * </ul>
     */
    public RubyElementDelta(IRubyElement element) {
        this.changedElement = element;
    }

    /**
     * Adds the child delta to the collection of affected children. If the child
     * is already in the collection, walk down the hierarchy.
     */
    protected void addAffectedChild(RubyElementDelta child) {
        switch (this.kind) {
        case ADDED:
        case REMOVED:
            // no need to add a child if this parent is added or removed
            return;
        case CHANGED:
            this.changeFlags |= F_CHILDREN;
            break;
        default:
            this.kind = CHANGED;
            this.changeFlags |= F_CHILDREN;
        }

        // if a child delta is added to a compilation unit delta or below,
        // it's a fine grained delta
        if (this.changedElement.getElementType() >= IRubyElement.SCRIPT) {
            this.fineGrained();
        }

        if (this.affectedChildren.length == 0) {
            this.affectedChildren = new IRubyElementDelta[] { child};
            return;
        }
        RubyElementDelta existingChild = null;
        int existingChildIndex = -1;
        if (this.affectedChildren != null) {
            for (int i = 0; i < this.affectedChildren.length; i++) {
                if (this.equalsAndSameParent(this.affectedChildren[i].getElement(), child
                        .getElement())) { // handle case of two jars that can
                    // be equals but not in the same
                    // project
                    existingChild = (RubyElementDelta) this.affectedChildren[i];
                    existingChildIndex = i;
                    break;
                }
            }
        }
        if (existingChild == null) { // new affected child
            this.affectedChildren = growAndAddToArray(this.affectedChildren, child);
        } else {
            switch (existingChild.getKind()) {
            case ADDED:
                switch (child.getKind()) {
                case ADDED: // child was added then added -> it is added
                case CHANGED: // child was added then changed -> it is added
                    return;
                case REMOVED: // child was added then removed -> noop
                    this.affectedChildren = this.removeAndShrinkArray(this.affectedChildren,
                            existingChildIndex);
                    return;
                }
                break;
            case REMOVED:
                switch (child.getKind()) {
                case ADDED: // child was removed then added -> it is changed
                    child.kind = CHANGED;
                    this.affectedChildren[existingChildIndex] = child;
                    return;
                case CHANGED: // child was removed then changed -> it is
                // removed
                case REMOVED: // child was removed then removed -> it is
                    // removed
                    return;
                }
                break;
            case CHANGED:
                switch (child.getKind()) {
                case ADDED: // child was changed then added -> it is added
                case REMOVED: // child was changed then removed -> it is
                    // removed
                    this.affectedChildren[existingChildIndex] = child;
                    return;
                case CHANGED: // child was changed then changed -> it is
                    // changed
                    IRubyElementDelta[] children = child.getAffectedChildren();
                    for (int i = 0; i < children.length; i++) {
                        RubyElementDelta childsChild = (RubyElementDelta) children[i];
                        existingChild.addAffectedChild(childsChild);
                    }

                    // update flags
                    boolean childHadContentFlag = (child.changeFlags & F_CONTENT) != 0;
                    boolean existingChildHadChildrenFlag = (existingChild.changeFlags & F_CHILDREN) != 0;
                    existingChild.changeFlags |= child.changeFlags;

                    // remove F_CONTENT flag if existing child had F_CHILDREN
                    // flag set
                    // (case of fine grained delta (existing child) and delta
                    // coming from
                    // DeltaProcessor (child))
                    if (childHadContentFlag && existingChildHadChildrenFlag) {
                        existingChild.changeFlags &= ~F_CONTENT;
                    }

                    // add the non-java resource deltas if needed
                    // note that the child delta always takes precedence over
                    // this existing child delta
                    // as non-java resource deltas are always created last (by
                    // the DeltaProcessor)
                    IResourceDelta[] resDeltas = child.getResourceDeltas();
                    if (resDeltas != null) {
                        existingChild.resourceDeltas = resDeltas;
                        existingChild.resourceDeltasCounter = child.resourceDeltasCounter;
                    }

                    return;
                }
                break;
            default:
                // unknown -> existing child becomes the child with the existing
                // child's flags
                int flags = existingChild.getFlags();
                this.affectedChildren[existingChildIndex] = child;
                child.changeFlags |= flags;
            }
        }
    }

    /**
     * Creates the nested deltas resulting from an add operation. Convenience
     * method for creating add deltas. The constructor should be used to create
     * the root delta and then an add operation should call this method.
     */
    public void added(IRubyElement element) {
        added(element, 0);
    }

    public void added(IRubyElement element, int flags) {
        RubyElementDelta addedDelta = new RubyElementDelta(element);
        addedDelta.added();
        addedDelta.changeFlags |= flags;
        insertDeltaTree(element, addedDelta);
    }

    /**
     * Adds the child delta to the collection of affected children. If the child
     * is already in the collection, walk down the hierarchy.
     */
    protected void addResourceDelta(IResourceDelta child) {
        switch (this.kind) {
        case ADDED:
        case REMOVED:
            // no need to add a child if this parent is added or removed
            return;
        case CHANGED:
            this.changeFlags |= F_CONTENT;
            break;
        default:
            this.kind = CHANGED;
            this.changeFlags |= F_CONTENT;
        }
        if (resourceDeltas == null) {
            resourceDeltas = new IResourceDelta[5];
            resourceDeltas[resourceDeltasCounter++] = child;
            return;
        }
        if (resourceDeltas.length == resourceDeltasCounter) {
            // need a resize
            System.arraycopy(resourceDeltas, 0,
                    (resourceDeltas = new IResourceDelta[resourceDeltasCounter * 2]), 0,
                    resourceDeltasCounter);
        }
        resourceDeltas[resourceDeltasCounter++] = child;
    }

    /**
     * Creates the nested deltas resulting from a change operation. Convenience
     * method for creating change deltas. The constructor should be used to
     * create the root delta and then a change operation should call this
     * method.
     */
    public RubyElementDelta changed(IRubyElement element, int changeFlag) {
        RubyElementDelta changedDelta = new RubyElementDelta(element);
        changedDelta.changed(changeFlag);
        insertDeltaTree(element, changedDelta);
        return changedDelta;
    }

    /*
     * Records the last changed AST .
     */
    public void changedAST(Node changedAST) {
        this.ast = changedAST;
        changed(F_AST_AFFECTED);
    }

    /**
     * Mark this delta as a content changed delta.
     */
    public void contentChanged() {
        this.changeFlags |= F_CONTENT;
    }

    /**
     * Creates the nested deltas for a closed element.
     */
    public void closed(IRubyElement element) {
        RubyElementDelta delta = new RubyElementDelta(element);
        delta.changed(F_CLOSED);
        insertDeltaTree(element, delta);
    }

    /**
     * Creates the nested delta deltas based on the affected element its delta,
     * and the root of this delta tree. Returns the root of the created delta
     * tree.
     */
    protected RubyElementDelta createDeltaTree(IRubyElement element, RubyElementDelta delta) {
        RubyElementDelta childDelta = delta;
        ArrayList ancestors = getAncestors(element);
        if (ancestors == null) {
            if (this.equalsAndSameParent(delta.getElement(), getElement())) { // handle
                // case
                // of
                // two
                // jars
                // that
                // can
                // be
                // equals
                // but
                // not
                // in
                // the
                // same
                // project
                // the element being changed is the root element
                this.kind = delta.kind;
                this.changeFlags = delta.changeFlags;
                this.movedToHandle = delta.movedToHandle;
                this.movedFromHandle = delta.movedFromHandle;
            }
        } else {
            for (int i = 0, size = ancestors.size(); i < size; i++) {
                IRubyElement ancestor = (IRubyElement) ancestors.get(i);
                RubyElementDelta ancestorDelta = new RubyElementDelta(ancestor);
                ancestorDelta.addAffectedChild(childDelta);
                childDelta = ancestorDelta;
            }
        }
        return childDelta;
    }

    /**
     * Returns whether the two java elements are equals and have the same
     * parent.
     */
    protected boolean equalsAndSameParent(IRubyElement e1, IRubyElement e2) {
        IRubyElement parent1;
        return e1.equals(e2) && ((parent1 = e1.getParent()) != null)
                && parent1.equals(e2.getParent());
    }

    /**
     * Returns the <code>RubyElementDelta</code> for the given element in the
     * delta tree, or null, if no delta for the given element is found.
     */
    protected RubyElementDelta find(IRubyElement e) {
        if (this.equalsAndSameParent(this.changedElement, e)) { // handle case
            // of two jars
            // that can be
            // equals but
            // not in the
            // same project
            return this;
        } else {
            for (int i = 0; i < this.affectedChildren.length; i++) {
                RubyElementDelta delta = ((RubyElementDelta) this.affectedChildren[i]).find(e);
                if (delta != null) { return delta; }
            }
        }
        return null;
    }

    /**
     * Mark this delta as a fine-grained delta.
     */
    public void fineGrained() {
        changed(F_FINE_GRAINED);
    }

    /**
     * @see IRubyElementDelta
     */
    public IRubyElementDelta[] getAddedChildren() {
        return getChildrenOfType(ADDED);
    }

    /**
     * @see IRubyElementDelta
     */
    public IRubyElementDelta[] getAffectedChildren() {
        return this.affectedChildren;
    }

    /**
     * Returns a collection of all the parents of this element up to (but not
     * including) the root of this tree in bottom-up order. If the given element
     * is not a descendant of the root of this tree, <code>null</code> is
     * returned.
     */
    private ArrayList getAncestors(IRubyElement element) {
        IRubyElement parent = element.getParent();
        if (parent == null) { return null; }
        ArrayList parents = new ArrayList();
        while (!parent.equals(this.changedElement)) {
            parents.add(parent);
            parent = parent.getParent();
            if (parent == null) { return null; }
        }
        parents.trimToSize();
        return parents;
    }

    public Node getRubyScriptAST() {
        return this.ast;
    }

    /**
     * @see IRubyElementDelta
     */
    public IRubyElementDelta[] getChangedChildren() {
        return getChildrenOfType(CHANGED);
    }

    /**
     * @see IRubyElementDelta
     */
    protected IRubyElementDelta[] getChildrenOfType(int type) {
        int length = this.affectedChildren.length;
        if (length == 0) { return new IRubyElementDelta[] {}; }
        ArrayList children = new ArrayList(length);
        for (int i = 0; i < length; i++) {
            if (this.affectedChildren[i].getKind() == type) {
                children.add(this.affectedChildren[i]);
            }
        }

        IRubyElementDelta[] childrenOfType = new IRubyElementDelta[children.size()];
        children.toArray(childrenOfType);

        return childrenOfType;
    }

    /**
     * Returns the delta for a given element. Only looks below this delta.
     */
    protected RubyElementDelta getDeltaFor(IRubyElement element) {
        if (this.equalsAndSameParent(getElement(), element)) // handle case
            // of two jars
            // that can be
            // equals but
            // not in the
            // same project
            return this;
        if (this.affectedChildren.length == 0) return null;
        int childrenCount = this.affectedChildren.length;
        for (int i = 0; i < childrenCount; i++) {
            RubyElementDelta delta = (RubyElementDelta) this.affectedChildren[i];
            if (this.equalsAndSameParent(delta.getElement(), element)) { // handle
                // case
                // of
                // two
                // jars
                // that
                // can
                // be
                // equals
                // but
                // not
                // in
                // the
                // same
                // project
                return delta;
            } else {
                delta = delta.getDeltaFor(element);
                if (delta != null) return delta;
            }
        }
        return null;
    }

    /**
     * @see IRubyElementDelta
     */
    public IRubyElement getElement() {
        return this.changedElement;
    }

    /**
     * @see IRubyElementDelta
     */
    public IRubyElement getMovedFromElement() {
        return this.movedFromHandle;
    }

    /**
     * @see IRubyElementDelta
     */
    public IRubyElement getMovedToElement() {
        return movedToHandle;
    }

    /**
     * @see IRubyElementDelta
     */
    public IRubyElementDelta[] getRemovedChildren() {
        return getChildrenOfType(REMOVED);
    }

    /**
     * Return the collection of resource deltas. Return null if none.
     */
    public IResourceDelta[] getResourceDeltas() {
        if (resourceDeltas == null) return null;
        if (resourceDeltas.length != resourceDeltasCounter) {
            System.arraycopy(resourceDeltas, 0,
                    resourceDeltas = new IResourceDelta[resourceDeltasCounter], 0,
                    resourceDeltasCounter);
        }
        return resourceDeltas;
    }

    /**
     * Adds the new element to a new array that contains all of the elements of
     * the old array. Returns the new array.
     */
    protected IRubyElementDelta[] growAndAddToArray(IRubyElementDelta[] array,
            IRubyElementDelta addition) {
        IRubyElementDelta[] old = array;
        array = new IRubyElementDelta[old.length + 1];
        System.arraycopy(old, 0, array, 0, old.length);
        array[old.length] = addition;
        return array;
    }

    /**
     * Creates the delta tree for the given element and delta, and then inserts
     * the tree as an affected child of this node.
     */
    protected void insertDeltaTree(IRubyElement element, RubyElementDelta delta) {
        RubyElementDelta childDelta = createDeltaTree(element, delta);
        if (!this.equalsAndSameParent(element, getElement())) { // handle case
            // of two jars
            // that can be
            // equals but
            // not in the
            // same project
            addAffectedChild(childDelta);
        }
    }

    /**
     * Creates the nested deltas resulting from an move operation. Convenience
     * method for creating the "move from" delta. The constructor should be used
     * to create the root delta and then the move operation should call this
     * method.
     */
    public void movedFrom(IRubyElement movedFromElement, IRubyElement movedToElement) {
        RubyElementDelta removedDelta = new RubyElementDelta(movedFromElement);
        removedDelta.kind = REMOVED;
        removedDelta.changeFlags |= F_MOVED_TO;
        removedDelta.movedToHandle = movedToElement;
        insertDeltaTree(movedFromElement, removedDelta);
    }

    /**
     * Creates the nested deltas resulting from an move operation. Convenience
     * method for creating the "move to" delta. The constructor should be used
     * to create the root delta and then the move operation should call this
     * method.
     */
    public void movedTo(IRubyElement movedToElement, IRubyElement movedFromElement) {
        RubyElementDelta addedDelta = new RubyElementDelta(movedToElement);
        addedDelta.kind = ADDED;
        addedDelta.changeFlags |= F_MOVED_FROM;
        addedDelta.movedFromHandle = movedFromElement;
        insertDeltaTree(movedToElement, addedDelta);
    }

    /**
     * Creates the nested deltas for an opened element.
     */
    public void opened(IRubyElement element) {
        RubyElementDelta delta = new RubyElementDelta(element);
        delta.changed(F_OPENED);
        insertDeltaTree(element, delta);
    }

    /**
     * Removes the child delta from the collection of affected children.
     */
    protected void removeAffectedChild(RubyElementDelta child) {
        int index = -1;
        if (this.affectedChildren != null) {
            for (int i = 0; i < this.affectedChildren.length; i++) {
                if (this.equalsAndSameParent(this.affectedChildren[i].getElement(), child
                        .getElement())) { // handle case of two jars that can
                    // be equals but not in the same
                    // project
                    index = i;
                    break;
                }
            }
        }
        if (index >= 0) {
            this.affectedChildren = removeAndShrinkArray(this.affectedChildren, index);
        }
    }

    /**
     * Removes the element from the array. Returns the a new array which has
     * shrunk.
     */
    protected IRubyElementDelta[] removeAndShrinkArray(IRubyElementDelta[] old, int index) {
        IRubyElementDelta[] array = new IRubyElementDelta[old.length - 1];
        if (index > 0) System.arraycopy(old, 0, array, 0, index);
        int rest = old.length - index - 1;
        if (rest > 0) System.arraycopy(old, index + 1, array, index, rest);
        return array;
    }

    /**
     * Creates the nested deltas resulting from an delete operation. Convenience
     * method for creating removed deltas. The constructor should be used to
     * create the root delta and then the delete operation should call this
     * method.
     */
    public void removed(IRubyElement element) {
        removed(element, 0);
    }

    public void removed(IRubyElement element, int flags) {
        RubyElementDelta removedDelta = new RubyElementDelta(element);
        insertDeltaTree(element, removedDelta);
        RubyElementDelta actualDelta = getDeltaFor(element);
        if (actualDelta != null) {
            actualDelta.removed();
            actualDelta.changeFlags |= flags;
            actualDelta.affectedChildren = EMPTY_DELTA;
        }
    }

    /**
     * Creates the nested deltas resulting from a change operation. Convenience
     * method for creating change deltas. The constructor should be used to
     * create the root delta and then a change operation should call this
     * method.
     */
    public void sourceAttached(IRubyElement element) {
        RubyElementDelta attachedDelta = new RubyElementDelta(element);
        attachedDelta.changed(F_SOURCEATTACHED);
        insertDeltaTree(element, attachedDelta);
    }

    /**
     * Creates the nested deltas resulting from a change operation. Convenience
     * method for creating change deltas. The constructor should be used to
     * create the root delta and then a change operation should call this
     * method.
     */
    public void sourceDetached(IRubyElement element) {
        RubyElementDelta detachedDelta = new RubyElementDelta(element);
        detachedDelta.changed(F_SOURCEDETACHED);
        insertDeltaTree(element, detachedDelta);
    }

    /**
     * Returns a string representation of this delta's structure suitable for
     * debug purposes.
     * 
     * @see #toString()
     */
    public String toDebugString(int depth) {
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < depth; i++) {
            buffer.append('\t');
        }
        buffer.append(((RubyElement) getElement()).toDebugString());
        toDebugString(buffer);
        IRubyElementDelta[] children = getAffectedChildren();
        if (children != null) {
            for (int i = 0; i < children.length; ++i) {
                buffer.append("\n"); //$NON-NLS-1$
                buffer.append(((RubyElementDelta) children[i]).toDebugString(depth + 1));
            }
        }
        for (int i = 0; i < resourceDeltasCounter; i++) {
            buffer.append("\n");//$NON-NLS-1$
            for (int j = 0; j < depth + 1; j++) {
                buffer.append('\t');
            }
            IResourceDelta resourceDelta = resourceDeltas[i];
            buffer.append(resourceDelta.toString());
            buffer.append("["); //$NON-NLS-1$
            switch (resourceDelta.getKind()) {
            case IResourceDelta.ADDED:
                buffer.append('+');
                break;
            case IResourceDelta.REMOVED:
                buffer.append('-');
                break;
            case IResourceDelta.CHANGED:
                buffer.append('*');
                break;
            default:
                buffer.append('?');
                break;
            }
            buffer.append("]"); //$NON-NLS-1$
        }
        return buffer.toString();
    }

    protected boolean toDebugString(StringBuffer buffer, int flags) {
        boolean prev = super.toDebugString(buffer, flags);

        if ((flags & IRubyElementDelta.F_CHILDREN) != 0) {
            if (prev) buffer.append(" | "); //$NON-NLS-1$
            buffer.append("CHILDREN"); //$NON-NLS-1$
            prev = true;
        }
        if ((flags & IRubyElementDelta.F_CONTENT) != 0) {
            if (prev) buffer.append(" | "); //$NON-NLS-1$
            buffer.append("CONTENT"); //$NON-NLS-1$
            prev = true;
        }
        if ((flags & IRubyElementDelta.F_MOVED_FROM) != 0) {
            if (prev) buffer.append(" | "); //$NON-NLS-1$
            buffer
                    .append("MOVED_FROM(" + ((RubyElement) getMovedFromElement()).toStringWithAncestors() + ")"); //$NON-NLS-1$ //$NON-NLS-2$
            prev = true;
        }
        if ((flags & IRubyElementDelta.F_MOVED_TO) != 0) {
            if (prev) buffer.append(" | "); //$NON-NLS-1$
            buffer
                    .append("MOVED_TO(" + ((RubyElement) getMovedToElement()).toStringWithAncestors() + ")"); //$NON-NLS-1$ //$NON-NLS-2$
            prev = true;
        }
        if ((flags & IRubyElementDelta.F_ADDED_TO_CLASSPATH) != 0) {
            if (prev) buffer.append(" | "); //$NON-NLS-1$
            buffer.append("ADDED TO CLASSPATH"); //$NON-NLS-1$
            prev = true;
        }
        if ((flags & IRubyElementDelta.F_REMOVED_FROM_CLASSPATH) != 0) {
            if (prev) buffer.append(" | "); //$NON-NLS-1$
            buffer.append("REMOVED FROM CLASSPATH"); //$NON-NLS-1$
            prev = true;
        }
        if ((flags & IRubyElementDelta.F_REORDER) != 0) {
            if (prev) buffer.append(" | "); //$NON-NLS-1$
            buffer.append("REORDERED"); //$NON-NLS-1$
            prev = true;
        }
        if ((flags & IRubyElementDelta.F_ARCHIVE_CONTENT_CHANGED) != 0) {
            if (prev) buffer.append(" | "); //$NON-NLS-1$
            buffer.append("ARCHIVE CONTENT CHANGED"); //$NON-NLS-1$
            prev = true;
        }
        if ((flags & IRubyElementDelta.F_SOURCEATTACHED) != 0) {
            if (prev) buffer.append(" | "); //$NON-NLS-1$
            buffer.append("SOURCE ATTACHED"); //$NON-NLS-1$
            prev = true;
        }
        if ((flags & IRubyElementDelta.F_SOURCEDETACHED) != 0) {
            if (prev) buffer.append(" | "); //$NON-NLS-1$
            buffer.append("SOURCE DETACHED"); //$NON-NLS-1$
            prev = true;
        }
        if ((flags & IRubyElementDelta.F_FINE_GRAINED) != 0) {
            if (prev) buffer.append(" | "); //$NON-NLS-1$
            buffer.append("FINE GRAINED"); //$NON-NLS-1$
            prev = true;
        }
        if ((flags & IRubyElementDelta.F_PRIMARY_WORKING_COPY) != 0) {
            if (prev) buffer.append(" | "); //$NON-NLS-1$
            buffer.append("PRIMARY WORKING COPY"); //$NON-NLS-1$
            prev = true;
        }
        if ((flags & IRubyElementDelta.F_CLASSPATH_CHANGED) != 0) {
            if (prev) buffer.append(" | "); //$NON-NLS-1$
            buffer.append("CLASSPATH CHANGED"); //$NON-NLS-1$
            prev = true;
        }
        if ((flags & IRubyElementDelta.F_PRIMARY_RESOURCE) != 0) {
            if (prev) buffer.append(" | "); //$NON-NLS-1$
            buffer.append("PRIMARY RESOURCE"); //$NON-NLS-1$
            prev = true;
        }
        if ((flags & IRubyElementDelta.F_OPENED) != 0) {
            if (prev) buffer.append(" | "); //$NON-NLS-1$
            buffer.append("OPENED"); //$NON-NLS-1$
            prev = true;
        }
        if ((flags & IRubyElementDelta.F_CLOSED) != 0) {
            if (prev) buffer.append(" | "); //$NON-NLS-1$
            buffer.append("CLOSED"); //$NON-NLS-1$
            prev = true;
        }
        if ((flags & IRubyElementDelta.F_AST_AFFECTED) != 0) {
            if (prev) buffer.append(" | "); //$NON-NLS-1$
            buffer.append("AST AFFECTED"); //$NON-NLS-1$
            prev = true;
        }
        if ((flags & IRubyElementDelta.F_CATEGORIES) != 0) {
            if (prev) buffer.append(" | "); //$NON-NLS-1$
            buffer.append("CATEGORIES"); //$NON-NLS-1$
            prev = true;
        }
        return prev;
    }

    /**
     * Returns a string representation of this delta's structure suitable for
     * debug purposes.
     */
    public String toString() {
        return toDebugString(0);
    }
}
