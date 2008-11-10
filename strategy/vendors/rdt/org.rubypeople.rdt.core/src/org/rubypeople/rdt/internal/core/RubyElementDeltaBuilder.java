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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.rubypeople.rdt.core.IParent;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IRubyElementDelta;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.internal.core.util.CharOperation;

/**
 * A java element delta biulder creates a java element delta on a java element
 * between the version of the java element at the time the comparator was
 * created and the current version of the java element.
 * 
 * It performs this operation by locally caching the contents of the java
 * element when it is created. When the method createDeltas() is called, it
 * creates a delta over the cached contents and the new contents.
 */
public class RubyElementDeltaBuilder {

    /**
     * The ruby element handle
     */
    IRubyElement rubyElement;

    /**
     * The maximum depth in the ruby element children we should look into
     */
    int maxDepth = Integer.MAX_VALUE;

    /**
     * The old handle to info relationships
     */
    Map infos;

    /**
     * The old position info
     */
    Map oldPositions;

    /**
     * The new position info
     */
    Map newPositions;

    /**
     * Change delta
     */
    public RubyElementDelta delta;

    /**
     * List of added elements
     */
    ArrayList added;

    /**
     * List of removed elements
     */
    ArrayList removed;

    /**
     * Doubly linked list item
     */
    class ListItem {

        public IRubyElement previous;
        public IRubyElement next;

        public ListItem(IRubyElement previous, IRubyElement next) {
            this.previous = previous;
            this.next = next;
        }
    }

    /**
     * Creates a ruby element comparator on a ruby element looking as deep as
     * necessary.
     */
    public RubyElementDeltaBuilder(IRubyElement rubyElement) {
        this.rubyElement = rubyElement;
        this.initialize();
        this.recordElementInfo(rubyElement, (RubyModel) this.rubyElement.getRubyModel(), 0);
    }

    /**
     * Creates a ruby element comparator on a ruby element looking only
     * 'maxDepth' levels deep.
     */
    public RubyElementDeltaBuilder(IRubyElement rubyElement, int maxDepth) {
        this.rubyElement = rubyElement;
        this.maxDepth = maxDepth;
        this.initialize();
        this.recordElementInfo(rubyElement, (RubyModel) this.rubyElement.getRubyModel(), 0);
    }

    /**
     * Repairs the positioning information after an element has been added
     */
    private void added(IRubyElement element) {
        this.added.add(element);
        ListItem current = this.getNewPosition(element);
        ListItem previous = null, next = null;
        if (current.previous != null) previous = this.getNewPosition(current.previous);
        if (current.next != null) next = this.getNewPosition(current.next);
        if (previous != null) previous.next = current.next;
        if (next != null) next.previous = current.previous;
    }

    /**
     * Builds the ruby element deltas between the old content of the ruby script
     * and its new content.
     */
    public void buildDeltas() {
        this.recordNewPositions(this.rubyElement, 0);
        this.findAdditions(this.rubyElement, 0);
        this.findDeletions();
        this.findChangesInPositioning(this.rubyElement, 0);
        this.trimDelta(this.delta);
        if (this.delta.getAffectedChildren().length == 0) {
            // this is a fine grained but not children affected -> mark as
            // content changed
            this.delta.contentChanged();
        }
    }

    /**
     * Finds elements which have been added or changed.
     */
    private void findAdditions(IRubyElement newElement, int depth) {
        RubyElementInfo oldInfo = this.getElementInfo(newElement);
        if (oldInfo == null && depth < this.maxDepth) {
            this.delta.added(newElement);
            added(newElement);
        } else {
            this.removeElementInfo(newElement);
        }

        if (depth >= this.maxDepth) {
            // mark element as changed
            this.delta.changed(newElement, IRubyElementDelta.F_CONTENT);
            return;
        }

        RubyElementInfo newInfo = null;
        try {
            newInfo = (RubyElementInfo) ((RubyElement) newElement).getElementInfo();
        } catch (RubyModelException npe) {
            return;
        }

        this.findContentChange(oldInfo, newInfo, newElement);

        if (oldInfo != null && newElement instanceof IParent) {

            IRubyElement[] children = newInfo.getChildren();
            if (children != null) {
                int length = children.length;
                for (int i = 0; i < length; i++) {
                    this.findAdditions(children[i], depth + 1);
                }
            }
        }
    }

    /**
     * Looks for changed positioning of elements.
     */
    private void findChangesInPositioning(IRubyElement element, int depth) {
        if (depth >= this.maxDepth || this.added.contains(element)
                || this.removed.contains(element)) return;

        if (!isPositionedCorrectly(element)) {
            this.delta.changed(element, IRubyElementDelta.F_REORDER);
        }

        if (element instanceof IParent) {
            RubyElementInfo info = null;
            try {
                info = (RubyElementInfo) ((RubyElement) element).getElementInfo();
            } catch (RubyModelException npe) {
                return;
            }

            IRubyElement[] children = info.getChildren();
            if (children != null) {
                int length = children.length;
                for (int i = 0; i < length; i++) {
                    this.findChangesInPositioning(children[i], depth + 1);
                }
            }
        }
    }

    /**
     * The elements are equivalent, but might have content changes.
     */
    private void findContentChange(RubyElementInfo oldInfo, RubyElementInfo newInfo,
            IRubyElement newElement) {
        if (oldInfo instanceof MemberElementInfo && newInfo instanceof MemberElementInfo) {
            if (oldInfo instanceof RubyMethodElementInfo
                    && newInfo instanceof RubyMethodElementInfo) {
                RubyMethodElementInfo oldSourceMethodInfo = (RubyMethodElementInfo) oldInfo;
                RubyMethodElementInfo newSourceMethodInfo = (RubyMethodElementInfo) newInfo;
                if (oldSourceMethodInfo.getVisibility() != newSourceMethodInfo.getVisibility()) {
                    this.delta.changed(newElement, IRubyElementDelta.F_MODIFIERS);
                }
                if (!CharOperation.equals(oldSourceMethodInfo.getArgumentNames(),
                        newSourceMethodInfo.getArgumentNames())) {
                    this.delta.changed(newElement, IRubyElementDelta.F_CONTENT);
                }
            } else if (oldInfo instanceof RubyFieldElementInfo
                    && newInfo instanceof RubyFieldElementInfo) {
            	if ( ((RubyFieldElementInfo) oldInfo).getTypeName() != null && ((RubyFieldElementInfo) newInfo).getTypeName() != null) {
                	if (!((RubyFieldElementInfo) oldInfo).getTypeName().equals(
                        ((RubyFieldElementInfo) newInfo).getTypeName())) {
                    	this.delta.changed(newElement, IRubyElementDelta.F_CONTENT);
                	}
                }
            }
        }
        if (oldInfo instanceof RubyTypeElementInfo && newInfo instanceof RubyTypeElementInfo) {
            RubyTypeElementInfo oldSourceTypeInfo = (RubyTypeElementInfo) oldInfo;
            RubyTypeElementInfo newSourceTypeInfo = (RubyTypeElementInfo) newInfo;
            if (oldSourceTypeInfo.getSuperclassName() != null && newSourceTypeInfo.getSuperclassName() != null) {
            if (!oldSourceTypeInfo.getSuperclassName().equals(newSourceTypeInfo
                    .getSuperclassName())
                    || !CharOperation.equals(oldSourceTypeInfo.getIncludedModuleNames(),
                            newSourceTypeInfo.getIncludedModuleNames())) {
                this.delta.changed(newElement, IRubyElementDelta.F_SUPER_TYPES);
            }
            }
        }
    }

    /**
     * Adds removed deltas for any handles left in the table
     */
    private void findDeletions() {
        Iterator iter = this.infos.keySet().iterator();
        while (iter.hasNext()) {
            IRubyElement element = (IRubyElement) iter.next();
            this.delta.removed(element);
            this.removed(element);
        }
    }

    private RubyElementInfo getElementInfo(IRubyElement element) {
        return (RubyElementInfo) this.infos.get(element);
    }

    private ListItem getNewPosition(IRubyElement element) {
        return (ListItem) this.newPositions.get(element);
    }

    private ListItem getOldPosition(IRubyElement element) {
        return (ListItem) this.oldPositions.get(element);
    }

    private void initialize() {
        this.infos = new HashMap(20);
        this.oldPositions = new HashMap(20);
        this.newPositions = new HashMap(20);
        this.putOldPosition(this.rubyElement, new ListItem(null, null));
        this.putNewPosition(this.rubyElement, new ListItem(null, null));
        this.delta = new RubyElementDelta(rubyElement);

        // if building a delta on a ruby script or below,
        // it's a fine grained delta
        if (rubyElement.getElementType() >= IRubyElement.SCRIPT) {
            this.delta.fineGrained();
        }

        this.added = new ArrayList(5);
        this.removed = new ArrayList(5);
    }

    /**
     * Inserts position information for the elements into the new or old
     * positions table
     */
    private void insertPositions(IRubyElement[] elements, boolean isNew) {
        int length = elements.length;
        IRubyElement previous = null, current = null, next = (length > 0) ? elements[0] : null;
        for (int i = 0; i < length; i++) {
            previous = current;
            current = next;
            next = (i + 1 < length) ? elements[i + 1] : null;
            if (isNew) {
                this.putNewPosition(current, new ListItem(previous, next));
            } else {
                this.putOldPosition(current, new ListItem(previous, next));
            }
        }
    }

    /**
     * Returns whether the elements position has not changed.
     */
    private boolean isPositionedCorrectly(IRubyElement element) {
        ListItem oldListItem = this.getOldPosition(element);
        if (oldListItem == null) return false;

        ListItem newListItem = this.getNewPosition(element);
        if (newListItem == null) return false;

        IRubyElement oldPrevious = oldListItem.previous;
        IRubyElement newPrevious = newListItem.previous;
        if (oldPrevious == null) {
            return newPrevious == null;
        } else {
            return oldPrevious.equals(newPrevious);
        }
    }

    private void putElementInfo(IRubyElement element, RubyElementInfo info) {
        this.infos.put(element, info);
    }

    private void putNewPosition(IRubyElement element, ListItem position) {
        this.newPositions.put(element, position);
    }

    private void putOldPosition(IRubyElement element, ListItem position) {
        this.oldPositions.put(element, position);
    }

    /**
     * Records this elements info, and attempts to record the info for the
     * children.
     */
    private void recordElementInfo(IRubyElement element, RubyModel model, int depth) {
        if (depth >= this.maxDepth) { return; }
        RubyElementInfo info = (RubyElementInfo) RubyModelManager.getRubyModelManager().getInfo(
                element);
        if (info == null) // no longer in the ruby model.
            return;
        this.putElementInfo(element, info);

        if (element instanceof IParent) {
            IRubyElement[] children = info.getChildren();
            if (children != null) {
                insertPositions(children, false);
                for (int i = 0, length = children.length; i < length; i++)
                    recordElementInfo(children[i], model, depth + 1);
            }
        }
    }

    /**
     * Fills the newPositions hashtable with the new position information
     */
    private void recordNewPositions(IRubyElement newElement, int depth) {
        if (depth < this.maxDepth && newElement instanceof IParent) {
            RubyElementInfo info = null;
            try {
                info = (RubyElementInfo) ((RubyElement) newElement).getElementInfo();
            } catch (RubyModelException npe) {
                return;
            }

            IRubyElement[] children = info.getChildren();
            if (children != null) {
                insertPositions(children, true);
                for (int i = 0, length = children.length; i < length; i++) {
                    recordNewPositions(children[i], depth + 1);
                }
            }
        }
    }

    /**
     * Repairs the positioning information after an element has been removed
     */
    private void removed(IRubyElement element) {
        this.removed.add(element);
        ListItem current = this.getOldPosition(element);
        ListItem previous = null, next = null;
        if (current.previous != null) previous = this.getOldPosition(current.previous);
        if (current.next != null) next = this.getOldPosition(current.next);
        if (previous != null) previous.next = current.next;
        if (next != null) next.previous = current.previous;

    }

    private void removeElementInfo(IRubyElement element) {
        this.infos.remove(element);
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("Built delta:\n"); //$NON-NLS-1$
        buffer.append(this.delta.toString());
        return buffer.toString();
    }

    /**
     * Trims deletion deltas to only report the highest level of deletion
     */
    private void trimDelta(RubyElementDelta elementDelta) {
        if (elementDelta.getKind() == IRubyElementDelta.REMOVED) {
            IRubyElementDelta[] children = elementDelta.getAffectedChildren();
            for (int i = 0, length = children.length; i < length; i++) {
                elementDelta.removeAffectedChild((RubyElementDelta) children[i]);
            }
        } else {
            IRubyElementDelta[] children = elementDelta.getAffectedChildren();
            for (int i = 0, length = children.length; i < length; i++) {
                trimDelta((RubyElementDelta) children[i]);
            }
        }
    }
}
