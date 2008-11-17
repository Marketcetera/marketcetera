/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.rubypeople.rdt.internal.ui.text.ruby;

import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.rubypeople.rdt.ui.text.ruby.AbstractProposalSorter;
import org.rubypeople.rdt.ui.text.ruby.CompletionProposalComparator;

/**
 * A alphabetic proposal based sorter.
 * 
 * @since 3.2
 */
public final class AlphabeticSorter extends AbstractProposalSorter {

	private final CompletionProposalComparator fComparator= new CompletionProposalComparator();
	
	public AlphabeticSorter() {
		fComparator.setOrderAlphabetically(true);
	}

	/*
	 * @see org.eclipse.jdt.ui.text.java.AbstractProposalSorter#compare(org.eclipse.jface.text.contentassist.ICompletionProposal, org.eclipse.jface.text.contentassist.ICompletionProposal)
	 */
	public int compare(ICompletionProposal p1, ICompletionProposal p2) {
		return fComparator.compare(p1, p2);
	}

}
