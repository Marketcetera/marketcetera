/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.rubypeople.rdt.internal.core.search;

import org.rubypeople.rdt.core.search.SearchParticipant;
import org.rubypeople.rdt.core.search.SearchPattern;

/**
 * TODO add spec
 */
public abstract class IndexQueryRequestor {
	
	// answer false if requesting cancel
	public abstract boolean acceptIndexMatch(String documentPath, SearchPattern indexRecord, SearchParticipant participant); // TODO take in AccessRuleSet when it exists in our model
	
}
