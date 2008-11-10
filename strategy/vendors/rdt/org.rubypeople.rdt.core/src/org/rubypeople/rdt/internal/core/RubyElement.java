/*
 * Author: C.Williams
 * 
 * Copyright (c) 2004 RubyPeople.
 * 
 * This file is part of the Ruby Development Tools (RDT) plugin for eclipse. You
 * can get copy of the GPL along with further information about RubyPeople and
 * third party software bundled with RDT in the file
 * org.rubypeople.rdt.core_x.x.x/RDT.license or otherwise at
 * http://www.rubypeople.org/RDT.license.
 * 
 * RDT is free software; you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * RDT is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * RDT; if not, write to the Free Software Foundation, Inc., 59 Temple Place,
 * Suite 330, Boston, MA 02111-1307 USA
 */
package org.rubypeople.rdt.internal.core;

import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.PlatformObject;
import org.jruby.ast.Node;
import org.rubypeople.rdt.core.IField;
import org.rubypeople.rdt.core.IOpenable;
import org.rubypeople.rdt.core.IParent;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IRubyModel;
import org.rubypeople.rdt.core.IRubyModelStatus;
import org.rubypeople.rdt.core.IRubyModelStatusConstants;
import org.rubypeople.rdt.core.IRubyProject;
import org.rubypeople.rdt.core.IRubyScript;
import org.rubypeople.rdt.core.ISourceRange;
import org.rubypeople.rdt.core.ISourceReference;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.core.WorkingCopyOwner;
import org.rubypeople.rdt.internal.core.util.MementoTokenizer;
import org.rubypeople.rdt.internal.core.util.Util;

/**
 * @author Chris
 * 
 */
public abstract class RubyElement extends PlatformObject implements IRubyElement {

	public static final char JEM_ESCAPE = '\\';
	public static final char JEM_RUBYPROJECT = '=';
	public static final char JEM_SOURCEFOLDERROOT = '/';
	public static final char JEM_SOURCE_FOLDER = '<';
	public static final char JEM_FIELD = '^';
	public static final char JEM_METHOD = '~';
	public static final char JEM_RUBYSCRIPT = '{';
	public static final char JEM_TYPE = '[';
	public static final char JEM_IMPORTDECLARATION = '#';
	public static final char JEM_COUNT = '!';
	public static final char JEM_LOCALVARIABLE = '@';
	
	public static final IRubyElement[] NO_ELEMENTS = new IRubyElement[0];
	protected static final Object NO_INFO = new Object();

	protected RubyElement parent;

	public RubyElement(RubyElement parent) {
		this.parent = parent;
	}

	public String getElementName() {
		return ""; //$NON-NLS-1$
	}

	/**
	 * Returns true if this handle represents the same Ruby element as the given
	 * handle. By default, two handles represent the same element if they are
	 * identical or if they represent the same type of element, have equal
	 * names, parents, and occurrence counts.
	 * 
	 * <p>
	 * If a subclass has other requirements for equality, this method must be
	 * overridden.
	 * 
	 * @see Object#equals
	 */
	public boolean equals(Object o) {

		if (this == o) return true;

		// Ruby model parent is null
		if (this.parent == null) return super.equals(o);

		// assume instanceof check is done in subclass
		RubyElement other = (RubyElement) o;
		return getElementName().equals(other.getElementName()) && this.parent.equals(other.parent);
	}

	/**
	 * @see IRubyElement
	 */
	public boolean exists() {

		try {
			getElementInfo();
			return true;
		} catch (RubyModelException e) {
			// element doesn't exist: return false
		}
		return false;
	}

	public abstract int getElementType();

	void setParent(RubyElement parent) {
		this.parent = parent;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rubypeople.rdt.core.IRubyElement#getParent()
	 */
	public IRubyElement getParent() {
		return parent;
	}

	/*
	 * @see IRubyElement#getPrimaryElement()
	 */
	public IRubyElement getPrimaryElement() {
		return getPrimaryElement(true);
	}

	/*
	 * Returns the primary element. If checkOwner, and the cu owner is primary,
	 * return this element.
	 */
	public IRubyElement getPrimaryElement(boolean checkOwner) {
		return this;
	}
    
    /**
     * Returns the element that is located at the given source position
     * in this element.  This is a helper method for <code>IRubyScript#getElementAt</code>,
     * and only works on ruby scripts and types. The position given is
     * known to be within this element's source range already, and if no finer
     * grained element is found at the position, this element is returned.
     */
    protected IRubyElement getSourceElementAt(int position) throws RubyModelException {
        if (this instanceof ISourceReference) {
            IRubyElement[] children = getChildren();
            for (int i = children.length-1; i >= 0; i--) {
                IRubyElement aChild = children[i];
                if (aChild instanceof SourceRefElement) {
                    SourceRefElement child = (SourceRefElement) children[i];
                    ISourceRange range = child.getSourceRange();
                    int start = range.getOffset();
                    int end = start + range.getLength();
                    if (start <= position && position <= end) {
                        if (child instanceof IField) {
                            // check muti-declaration case (see https://bugs.eclipse.org/bugs/show_bug.cgi?id=39943)
                            int declarationStart = start;
                            SourceRefElement candidate = null;
                            do {
                                // check name range
                                range = ((IField)child).getNameRange();
                                if (position <= range.getOffset() + range.getLength()) {
                                    candidate = child;
                                } else {
                                    return candidate == null ? child.getSourceElementAt(position) : candidate.getSourceElementAt(position);
                                }
                                child = --i>=0 ? (SourceRefElement) children[i] : null;
                            } while (child != null && child.getSourceRange().getOffset() == declarationStart);
                            // position in field's type: use first field
                            return candidate.getSourceElementAt(position);
                        } else if (child instanceof IParent) {
                            return child.getSourceElementAt(position);
                        } else {
                            return child;
                        }
                    }
                }
            }
        } else {
            // should not happen
            Assert.isTrue(false);
        }
        return this;
    }

	/**
	 * @see IRubyElement
	 */
	public IRubyElement getAncestor(int ancestorType) {

		IRubyElement element = this;
		while (element != null) {
			if (element.getElementType() == ancestorType) return element;
			element = element.getParent();
		}
		return null;
	}

	/**
	 * @see IParent
	 */
	public IRubyElement[] getChildren() throws RubyModelException {
		Object elementInfo = getElementInfo();
		if (elementInfo instanceof RubyElementInfo) {
			return ((RubyElementInfo) elementInfo).getChildren();
		}
		return NO_ELEMENTS;		
	}
	
	/**
	 * Returns a collection of (immediate) children of this node of the
	 * specified type.
	 *
	 * @param type - one of the type constants defined by RubyElement
	 */
	public ArrayList<IRubyElement> getChildrenOfType(int type) throws RubyModelException {
		IRubyElement[] children = getChildren();
		int size = children.length;
		ArrayList<IRubyElement> list = new ArrayList<IRubyElement>(size);
		for (int i = 0; i < size; ++i) {
			RubyElement elt = (RubyElement)children[i];
			if (elt.getElementType() == type) {
				list.add(elt);
			}
		}
		return list;
	}

	/**
	 * @see IParent
	 */
	public boolean hasChildren() throws RubyModelException {
		// if I am not open, return true to avoid opening (case of a Java
		// project, a compilation unit or a class file).
		// also see https://bugs.eclipse.org/bugs/show_bug.cgi?id=52474
		Object elementInfo = RubyModelManager.getRubyModelManager().getInfo(this);
		if (elementInfo instanceof RubyElementInfo) {
			return ((RubyElementInfo) elementInfo).getChildren().length > 0;
		}
		return true;
	}

    /**
     * Returns the hash code for this Ruby element. By default,
     * the hash code for an element is a combination of its name
     * and parent's hash code. Elements with other requirements must
     * override this method.
     */
    public int hashCode() {
        if (this.parent == null) return super.hashCode();
        return Util.combineHashCodes(getElementName().hashCode(), this.parent.hashCode());
    }
    
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rubypeople.rdt.core.IRubyElement#isType(int)
	 */
	public boolean isType(int type) {
		return type == getElementType();
	}

	/**
	 * @return
	 */
	public IRubyScript getRubyScript() {
		return null;
	}

	public IRubyProject getRubyProject() {
		IRubyElement current = this;
		do {
			if (current instanceof IRubyProject) return (IRubyProject) current;
		} while ((current = current.getParent()) != null);
		return null;
	}

	public boolean isReadOnly() {
		return false;
	}

	/**
	 * @see IRubyElement
	 */
	public IRubyModel getRubyModel() {
		IRubyElement current = this;
		do {
			if (current instanceof IRubyModel) return (IRubyModel) current;
		} while ((current = current.getParent()) != null);
		return null;
	}

	/**
	 * @see IOpenable
	 */
	public void close() throws RubyModelException {
		RubyModelManager.getRubyModelManager().removeInfoAndChildren(this);
	}

	/**
	 * This element is being closed. Do any necessary cleanup.
	 * 
	 * @throws RubyModelException
	 */
	protected abstract void closing(Object info) throws RubyModelException;

	/**
	 * Returns true if this element is an ancestor of the given element,
	 * otherwise false.
	 */
	public boolean isAncestorOf(IRubyElement e) {
		IRubyElement parentElement = e.getParent();
		while (parentElement != null && !parentElement.equals(this)) {
			parentElement = parentElement.getParent();
		}
		return parentElement != null;
	}

	/*
	 * Opens an <code> Openable </code> that is known to be closed (no check for
	 * <code> isOpen() </code> ). Returns the created element info.
	 */
	protected Object openWhenClosed(Object info, IProgressMonitor monitor) throws RubyModelException {
		RubyModelManager manager = RubyModelManager.getRubyModelManager();
		boolean hadTemporaryCache = manager.hasTemporaryCache();
		try {
			HashMap newElements = manager.getTemporaryCache();
			generateInfos(info, newElements, monitor);
			if (info == null) {
				info = newElements.get(this);
			}
			if (info == null) { // a source ref element could not be opened
				// close the buffer that was opened for the openable parent
				// close only the openable's buffer (see
				// https://bugs.eclipse.org/bugs/show_bug.cgi?id=62854)
				Openable openable = (Openable) getOpenable();
				if (newElements.containsKey(openable)) {
					openable.closeBuffer();
				}
				throw newNotPresentException();
			}
			if (!hadTemporaryCache) {
				manager.putInfos(this, newElements);
			}
		} finally {
			if (!hadTemporaryCache) {
				manager.resetTemporaryCache();
			}
		}
		return info;
	}

	/**
	 * Creates and returns a new not present exception for this element.
	 */
	public RubyModelException newNotPresentException() {
		return new RubyModelException(new RubyModelStatus(IRubyModelStatusConstants.ELEMENT_DOES_NOT_EXIST, this));
	}

	/**
	 * Creates and returns a new Ruby model exception for this element with the
	 * given status.
	 */
	public RubyModelException newRubyModelException(IStatus status) {
		if (status instanceof IRubyModelStatus)
			return new RubyModelException((IRubyModelStatus) status);
		
		return new RubyModelException(new RubyModelStatus(status.getSeverity(), status.getCode(), status.getMessage()));
	}

	/**
	 * @param newElements
	 * @param info
	 * @param monitor
	 */
	abstract protected void generateInfos(Object info, HashMap newElements, IProgressMonitor monitor) throws RubyModelException;

	/*
	 * @see IRubyElement
	 */
	public IOpenable getOpenable() {
		return this.getOpenableParent();
	}

	/**
	 * Return the first instance of IOpenable in the parent hierarchy of this
	 * element.
	 * 
	 * <p>
	 * Subclasses that are not IOpenable's must override this method.
	 */
	public IOpenable getOpenableParent() {
		return (IOpenable) this.parent;
	}

	/**
	 */
	public String readableName() {
		return this.getElementName();
	}

	/**
	 * Returns the info for this handle. If this element is not already open, it
	 * and all of its parents are opened. Does not return null. NOTE: BinaryType
	 * infos are NOT rooted under RubyElementInfo.
	 * 
	 * @exception RubyModelException
	 *                if the element is not present or not accessible
	 */
	public Object getElementInfo() throws RubyModelException {
		return getElementInfo(null);
	}

	/**
	 * Returns the info for this handle. If this element is not already open, it
	 * and all of its parents are opened. Does not return null. 
	 * 
	 * @exception RubyModelException
	 *                if the element is not present or not accessible
	 */
	public Object getElementInfo(IProgressMonitor monitor) throws RubyModelException {
		RubyModelManager manager = RubyModelManager.getRubyModelManager();
		Object info = manager.getInfo(this);
		if (info != null) return info;
		return openWhenClosed(createElementInfo(), monitor);
	}

	/*
	 * Returns a new element info for this element.
	 */
	protected abstract Object createElementInfo();

	protected String tabString(int tab) {
		StringBuffer buffer = new StringBuffer();
		for (int i = tab; i > 0; i--)
			buffer.append("  "); //$NON-NLS-1$
		return buffer.toString();
	}

	/**
	 * Debugging purposes
	 */
	public String toDebugString() {
		StringBuffer buffer = new StringBuffer();
		this.toStringInfo(0, buffer, NO_INFO);
		return buffer.toString();
	}

	/**
	 * Debugging purposes
	 */
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		toString(0, buffer);
		return buffer.toString();
	}

	/**
	 * Debugging purposes
	 */
	protected void toString(int tab, StringBuffer buffer) {
		Object info = this.toStringInfo(tab, buffer);
		if (tab == 0) {
			this.toStringAncestors(buffer);
		}
		this.toStringChildren(tab, buffer, info);
	}
	/**
	 *  Debugging purposes
	 */
	public String toStringWithAncestors(boolean showResolvedInfo) {
		StringBuffer buffer = new StringBuffer();
		this.toStringInfo(0, buffer, NO_INFO, showResolvedInfo);
		this.toStringAncestors(buffer);
		return buffer.toString();
	}

	/**
	 * Debugging purposes
	 * 
	 * @param showResolvedInfo
	 *            
	 */
	protected void toStringInfo(int tab, StringBuffer buffer, Object info,
			boolean showResolvedInfo) {
		buffer.append(this.tabString(tab));
		toStringName(buffer);
		if (info == null) {
			buffer.append(" (not open)"); //$NON-NLS-1$
		}
	}
	/**
	 * Debugging purposes
	 */
	public String toStringWithAncestors() {
		StringBuffer buffer = new StringBuffer();
		this.toStringInfo(0, buffer, NO_INFO);
		this.toStringAncestors(buffer);
		return buffer.toString();
	}

	/**
	 * Debugging purposes
	 */
	protected void toStringAncestors(StringBuffer buffer) {
		RubyElement parentElement = (RubyElement) this.getParent();
		if (parentElement != null && parentElement.getParent() != null) {
			buffer.append(" [in "); //$NON-NLS-1$
			parentElement.toStringInfo(0, buffer, NO_INFO);
			parentElement.toStringAncestors(buffer);
			buffer.append("]"); //$NON-NLS-1$
		}
	}

	/**
	 * Debugging purposes
	 */
	protected void toStringChildren(int tab, StringBuffer buffer, Object info) {
		if (info == null || !(info instanceof RubyElementInfo)) return;
		IRubyElement[] children = ((RubyElementInfo) info).getChildren();
		for (int i = 0; i < children.length; i++) {
			buffer.append("\n"); //$NON-NLS-1$
			((RubyElement) children[i]).toString(tab + 1, buffer);
		}
	}

	/**
	 * Debugging purposes
	 */
	public Object toStringInfo(int tab, StringBuffer buffer) {
		Object info = RubyModelManager.getRubyModelManager().peekAtInfo(this);
		this.toStringInfo(tab, buffer, info);
		return info;
	}

	/**
	 * Debugging purposes
	 */
	protected void toStringInfo(int tab, StringBuffer buffer, Object info) {
		buffer.append(this.tabString(tab));
		toStringName(buffer);
		if (info == null) {
			buffer.append(" (not open)"); //$NON-NLS-1$
		}
	}

	/**
	 * Debugging purposes
	 */
	protected void toStringName(StringBuffer buffer) {
		buffer.append(getElementName());
	}

	public Node findNode(Node cuAST) {
		return null; // works only inside a ruby script
	}
	
	/*
	 * Creates a Ruby element handle from the given memento.
	 * The given token is the current delimiter indicating the type of the next token(s).
	 * The given working copy owner is used only for ruby script handles.
	 */
	public abstract IRubyElement getHandleFromMemento(String token, MementoTokenizer memento, WorkingCopyOwner owner);
	/*
	 * Creates a Ruby element handle from the given memento.
	 * The given working copy owner is used only for ruby script handles.
	 */
	public IRubyElement getHandleFromMemento(MementoTokenizer memento, WorkingCopyOwner owner) {
		if (!memento.hasMoreTokens()) return this;
		String token = memento.nextToken();
		return getHandleFromMemento(token, memento, owner);
	}
	/**
	 * @see IJavaElement
	 */
	public String getHandleIdentifier() {
		return getHandleMemento();
	}
	/**
	 * @see JavaElement#getHandleMemento()
	 */
	public String getHandleMemento(){
		StringBuffer buff = new StringBuffer();
		getHandleMemento(buff);
		return buff.toString();
	}
	protected void getHandleMemento(StringBuffer buff) {
		((RubyElement)getParent()).getHandleMemento(buff);
		buff.append(getHandleMementoDelimiter());
		escapeMementoName(buff, getElementName());
	}
	/**
	 * Returns the <code>char</code> that marks the start of this handles
	 * contribution to a memento.
	 */
	protected abstract char getHandleMementoDelimiter();

	protected void escapeMementoName(StringBuffer buffer, String mementoName) {
		for (int i = 0, length = mementoName.length(); i < length; i++) {
			char character = mementoName.charAt(i);
			switch (character) {
				case JEM_ESCAPE:
				case JEM_COUNT:
				case JEM_RUBYPROJECT:
				case JEM_SOURCEFOLDERROOT:
				case JEM_SOURCE_FOLDER:
				case JEM_FIELD:
				case JEM_METHOD:
				case JEM_RUBYSCRIPT:
				case JEM_TYPE:
				case JEM_IMPORTDECLARATION:
				case JEM_LOCALVARIABLE:
					buffer.append(JEM_ESCAPE);
			}
			buffer.append(character);
		}
	}

	public IRubyElement unresolved() {
		return this;
	}
}
