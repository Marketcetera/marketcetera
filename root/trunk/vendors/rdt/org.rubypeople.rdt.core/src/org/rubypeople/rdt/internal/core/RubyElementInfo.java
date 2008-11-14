/*******************************************************************************
 * Copyright (c) 2000, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.rubypeople.rdt.internal.core;

import org.rubypeople.rdt.core.IRubyElement;

/**
 * Holds cached structure and properties for a Ruby element. Subclassed to carry
 * properties for specific kinds of elements.
 */
/* package */class RubyElementInfo {

    /**
     * Collection of handles of immediate children of this object. This is an
     * empty array if this element has no children.
     */
    protected IRubyElement[] children;

    /**
     * Is the structure of this element known
     * 
     * @see IRubyElement#isStructureKnown()
     */
    protected boolean isStructureKnown = false;

    /**
     * Shared empty collection used for efficiency.
     */
    static Object[] NO_NON_RUBY_RESOURCES = new Object[] {};

    protected RubyElementInfo() {
        this.children = RubyElement.NO_ELEMENTS;
    }

    public void addChild(IRubyElement child) {
        if (this.children == RubyElement.NO_ELEMENTS) {
            setChildren(new IRubyElement[] { child});
        } else {
            if (!includesChild(child)) {
                setChildren(growAndAddToArray(this.children, child));
            }
        }
    }

    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            throw new Error();
        }
    }

    public IRubyElement[] getChildren() {
        return this.children;
    }

    /**
     * Adds the new element to a new array that contains all of the elements of
     * the old array. Returns the new array.
     */
    protected IRubyElement[] growAndAddToArray(IRubyElement[] array, IRubyElement addition) {
        IRubyElement[] old = array;
        array = new IRubyElement[old.length + 1];
        System.arraycopy(old, 0, array, 0, old.length);
        array[old.length] = addition;
        return array;
    }

    /**
     * Returns <code>true</code> if this child is in my children collection
     */
    protected boolean includesChild(IRubyElement child) {

        for (int i = 0; i < this.children.length; i++) {
            if (this.children[i].equals(child)) { return true; }
        }
        return false;
    }

    /**
     * @see IRubyElement#isStructureKnown()
     */
    public boolean isStructureKnown() {
        return this.isStructureKnown;
    }

    /**
     * Returns an array with all the same elements as the specified array except
     * for the element to remove. Assumes that the deletion is contained in the
     * array.
     */
    protected IRubyElement[] removeAndShrinkArray(IRubyElement[] array, IRubyElement deletion) {
        IRubyElement[] old = array;
        array = new IRubyElement[old.length - 1];
        int j = 0;
        for (int i = 0; i < old.length; i++) {
            if (!old[i].equals(deletion)) {
                array[j] = old[i];
            } else {
                System.arraycopy(old, i + 1, array, j, old.length - (i + 1));
                return array;
            }
            j++;
        }
        return array;
    }

    public void removeChild(IRubyElement child) {
        if (includesChild(child)) {
            setChildren(removeAndShrinkArray(this.children, child));
        }
    }

    public void setChildren(IRubyElement[] children) {
        this.children = children;
    }

    /**
     * Sets whether the structure of this element known
     * 
     * @see IRubyElement#isStructureKnown()
     */
    public void setIsStructureKnown(boolean newIsStructureKnown) {
        this.isStructureKnown = newIsStructureKnown;
    }
}
