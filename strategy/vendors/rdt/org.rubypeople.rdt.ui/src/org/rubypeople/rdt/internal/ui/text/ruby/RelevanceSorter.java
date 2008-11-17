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

import java.util.Comparator;

import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.rubypeople.rdt.ui.text.ruby.AbstractProposalSorter;
import org.rubypeople.rdt.ui.text.ruby.CompletionProposalComparator;

/**
 * A relevance based sorter.
 * 
 * @since 1.0
 */
public final class RelevanceSorter extends AbstractProposalSorter {

	private final Comparator fComparator= new CompletionProposalComparator();

	public RelevanceSorter() {
	}
	
	/*
	 * @see org.rubypeople.rdt.ui.text.ruby.AbstractProposalSorter#compare(org.eclipse.jface.text.contentassist.ICompletionProposal, org.eclipse.jface.text.contentassist.ICompletionProposal)
	 */
	public int compare(ICompletionProposal p1, ICompletionProposal p2) {
		return fComparator.compare(p1, p2);
	}
}
