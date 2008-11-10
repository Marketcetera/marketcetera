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
package org.rubypeople.rdt.internal.ui;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.ui.IElementFactory;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IPersistableElement;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.RubyCore;

/** 
 * The RubyElementFactory is used to save and recreate an IRubyElement object.
 * As such, it implements the IPersistableElement interface for storage
 * and the IElementFactory interface for recreation.
 *
 * @see IMemento
 * @see IPersistableElement
 * @see IElementFactory
 */
public class PersistableRubyElementFactory implements IElementFactory, IPersistableElement {

	private static final String KEY= "elementID"; //$NON-NLS-1$
	private static final String FACTORY_ID= "org.rubypeople.rdt.ui.PersistableRubyElementFactory"; //$NON-NLS-1$

	private IRubyElement fElement;
	
	/**
	 * Create a RubyElementFactory.  
	 */
	public PersistableRubyElementFactory() {
	}

	/**
	 * Create a RubyElementFactory.  This constructor is typically used
	 * for our IPersistableElement side.
	 */
	public PersistableRubyElementFactory(IRubyElement element) {
		fElement= element;
	}

	/*
	 * @see IElementFactory
	 */
	public IAdaptable createElement(IMemento memento) {
	
		String identifier= memento.getString(KEY);
		if (identifier != null) {
			return RubyCore.create(identifier);
		}
		return null;
	}
	
	/*
	 * @see IPersistableElement.
	 */
	public String getFactoryId() {
		return FACTORY_ID;
	}
	/*
	 * @see IPersistableElement
	 */
	public void saveState(IMemento memento) {
		memento.putString(KEY, fElement.getHandleIdentifier());
	}
}
