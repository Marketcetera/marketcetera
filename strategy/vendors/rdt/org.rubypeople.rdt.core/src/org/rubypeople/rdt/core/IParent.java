/*
 * Created on Jan 13, 2005
 */
package org.rubypeople.rdt.core;

/**
 * Common protocol for Ruby elements that contain other Ruby elements.
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 */
public interface IParent {

    /**
     * Returns the immediate children of this element. Unless otherwise
     * specified by the implementing element, the children are in no particular
     * order.
     * 
     * @exception RubyModelException
     *                if this element does not exist or if an exception occurs
     *                while accessing its corresponding resource
     * @return the immediate children of this element
     */
    IRubyElement[] getChildren() throws RubyModelException;

    /**
     * Returns whether this element has one or more immediate children. This is
     * a convenience method, and may be more efficient than testing whether
     * <code>getChildren</code> is an empty array.
     * 
     * @exception RubyModelException
     *                if this element does not exist or if an exception occurs
     *                while accessing its corresponding resource
     * @return true if the immediate children of this element, false otherwise
     */
    boolean hasChildren() throws RubyModelException;
}
