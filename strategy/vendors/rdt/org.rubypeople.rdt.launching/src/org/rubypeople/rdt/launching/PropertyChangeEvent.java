/*******************************************************************************
 * Copyright (c) 2000, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.rubypeople.rdt.launching;


import java.util.EventObject;
/**
 * An event object describing a change to a named property.
 * <p>
 * JavaRuntime provides change notification for properties of VM installs
 * </p>
 * <p>
 * Clients may instantiate this class; not intended to be subclassed.
 * </p>
 * @since 2.0
 */
public class PropertyChangeEvent extends EventObject {
    
    /**
     * All serializable objects should have a stable serialVersionUID
     */
    private static final long serialVersionUID = 1L;

	/**
	 * The name of the changed property.
	 */
	private String propertyName;
	
	/**
	 * The old value of the changed property, or <code>null</code> if
	 * not known or not relevant.
	 */
	private Object oldValue;
	
	/**
	 * The new value of the changed property, or <code>null</code> if
	 * not known or not relevant.
	 */
	private Object newValue;
	
	/**
	 * Creates a new property change event.
	 *
	 * @param source the object whose property has changed
	 * @param property the property that has changed (must not be 
	 *    <code>null</code>)
	 * @param oldValue the old value of the property, or 
	 *    <code>null</code> if none
	 * @param newValue the new value of the property, or 
	 *    <code>null</code> if none
	 */
	public PropertyChangeEvent(
		Object source,
		String property,
		Object oldValue,
		Object newValue) {
	
		super(source);
		if (property == null) {
			throw new IllegalArgumentException();
		}
		this.propertyName = property;
		this.oldValue = oldValue;
		this.newValue = newValue;
	}
	
	/**
	 * Returns the name of the property that changed.
	 *
	 * @return the name of the property that changed
	 */
	public String getProperty() {
		return propertyName;
	}
	
	/**
	 * Returns the new value of the property.
	 *
	 * @return the new value, or <code>null</code> if not known
	 *  or not relevant
	 */
	public Object getNewValue() {
		return newValue;
	}
	
	/**
	 * Returns the old value of the property.
	 *
	 * @return the old value, or <code>null</code> if not known
	 *  or not relevant
	 */
	public Object getOldValue() {
		return oldValue;
	}
}
