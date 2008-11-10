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

import org.rubypeople.rdt.core.search.IRubySearchScope;


/**
 * <p>
 * Describes a Ruby search query. A query is described by giving a scope, a
 * scope description, what kind of match to search for (reference, declarations,
 * etc) and either a Ruby element or a string and what kind of element to search
 * for (type, field, etc). What exactly it means to, for example, to search for
 * "references to type foo" is up to query participants. For example, a
 * participant might consider the "class" attribute of an extension in a
 * plugin.xml file to be a reference to the class mentioned in the attribute.
 * </p>
 * 
 * <p>
 * This class is not intended to be instantiated or subclassed by clients.
 * </p>
 * 
 * @since 1.0
 */
public abstract class QuerySpecification {
	private IRubySearchScope fScope;
	private int fLimitTo;
	private String fScopeDescription;

	QuerySpecification(int limitTo, IRubySearchScope scope, String scopeDescription) {
		fScope= scope;
		fLimitTo= limitTo;
		fScopeDescription= scopeDescription;
	}

	/**
	 * Returns the search scope to be used in the query.
	 * @return The search scope.
	 */
	public IRubySearchScope getScope() {
		return fScope;
	}
	
	/**
	 * Returns a human readable description of the search scope.
	 * @return A description of the search scope. 
	 * @see QuerySpecification#getScope()
	 */
	public String getScopeDescription() {
		return fScopeDescription;
	}
	
	/**
	 * Returns what kind of occurrences the query should look for.
	 * @return Whether to search for reference, declaration, etc.
	 * @see org.rubypeople.rdt.core.search.IRubySearchConstants
	 */
	public int getLimitTo() {
		return fLimitTo;
	}

}
