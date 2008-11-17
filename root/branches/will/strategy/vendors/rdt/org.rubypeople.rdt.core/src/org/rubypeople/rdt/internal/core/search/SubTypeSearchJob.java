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

import org.eclipse.core.runtime.IProgressMonitor;
import org.rubypeople.rdt.core.search.IRubySearchScope;
import org.rubypeople.rdt.core.search.SearchParticipant;
import org.rubypeople.rdt.core.search.SearchPattern;
import org.rubypeople.rdt.internal.compiler.util.SimpleSet;
import org.rubypeople.rdt.internal.core.index.Index;

public class SubTypeSearchJob extends PatternSearchJob {

SimpleSet indexes = new SimpleSet(5);

public SubTypeSearchJob(SearchPattern pattern, SearchParticipant participant, IRubySearchScope scope, IndexQueryRequestor requestor) {
	super(pattern, participant, scope, requestor);
}
public void finished() {
	Object[] values = this.indexes.values;
	for (int i = 0, l = values.length; i < l; i++)
		if (values[i] != null)
			((Index) values[i]).stopQuery();
}
public boolean search(Index index, IProgressMonitor progressMonitor) {
	if (index == null) return COMPLETE;
	if (!indexes.includes(index)) {
		indexes.add(index);
		index.startQuery();
	}
	return super.search(index, progressMonitor);
}
}
