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
 ********************************************************************************
 * Copyright (c) 2000, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.rubypeople.rdt.ui.search;

import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.search.IRubySearchScope;

/**
 * <p>
 * Describes a search query by giving the {@link IRubyElement} to search
 * for.
 * </p>
 * <p>
 * This class is not intended to be instantiated or subclassed by clients.
 * </p>
 * 
 * @see org.rubypeople.rdt.ui.search.QuerySpecification
 *
 * @since 1.0
 */
public class ElementQuerySpecification extends QuerySpecification {
	private IRubyElement fElement;

	/**
	 * A constructor.
	 * @param RubyElement The Ruby element the query should search for.
	 * @param limitTo		  The kind of occurrence the query should search for.
	 * @param scope		  The scope to search in.
	 * @param scopeDescription A human readable description of the search scope.
	 */
	public ElementQuerySpecification(IRubyElement RubyElement, int limitTo, IRubySearchScope scope, String scopeDescription) {
		super(limitTo, scope, scopeDescription);
		fElement= RubyElement;
	}
	
	/**
	 * Returns the element to search for.
	 * @return The element to search for.
	 */
	public IRubyElement getElement() {
		return fElement;
	}
}
