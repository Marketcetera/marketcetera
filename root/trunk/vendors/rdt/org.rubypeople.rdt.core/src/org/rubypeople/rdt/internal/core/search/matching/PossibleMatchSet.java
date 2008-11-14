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
package org.rubypeople.rdt.internal.core.search.matching;

import org.eclipse.core.runtime.IPath;
import org.rubypeople.rdt.core.ISourceFolderRoot;
import org.rubypeople.rdt.internal.compiler.util.ObjectVector;
import org.rubypeople.rdt.internal.compiler.util.SimpleLookupTable;

/**
 * A set of PossibleMatches that is sorted by package fragment roots.
 */
public class PossibleMatchSet {

private SimpleLookupTable rootsToPossibleMatches = new SimpleLookupTable(5);
private int elementCount = 0;

public void add(PossibleMatch possibleMatch) {
	IPath path = possibleMatch.openable.getSourceFolderRoot().getPath();
	ObjectVector possibleMatches = (ObjectVector) this.rootsToPossibleMatches.get(path);
	if (possibleMatches != null) {
		if (possibleMatches.contains(possibleMatch)) return;
	} else {
		this.rootsToPossibleMatches.put(path, possibleMatches = new ObjectVector());
	}

	possibleMatches.add(possibleMatch);
	this.elementCount++;
}
public PossibleMatch[] getPossibleMatches(ISourceFolderRoot[] roots) {
	PossibleMatch[] result = new PossibleMatch[this.elementCount];
	int index = 0;
	for (int i = 0, length = roots.length; i < length; i++) {
		ObjectVector possibleMatches = (ObjectVector) this.rootsToPossibleMatches.get(roots[i].getPath());
		if (possibleMatches != null) {
			possibleMatches.copyInto(result, index);
			index += possibleMatches.size();
		}
	}
	if (index < this.elementCount)
		System.arraycopy(result, 0, result = new PossibleMatch[index], 0, index);
	return result;
}
public void reset() {
	this.rootsToPossibleMatches = new SimpleLookupTable(5);
	this.elementCount = 0;
}
}
