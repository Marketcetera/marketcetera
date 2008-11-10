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
package org.rubypeople.rdt.ui.text.ruby;

import java.util.Comparator;

import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.templates.TemplateProposal;
import org.rubypeople.rdt.internal.ui.text.ruby.AbstractRubyCompletionProposal;

/**
 * Comparator for ruby completion proposals. Completion proposals can be sorted by relevance or
 * alphabetically.
 * <p>
 * Note: this comparator imposes orderings that are inconsistent with equals.
 * </p>
 * 
 * @since 1.0
 */
public final class CompletionProposalComparator implements Comparator {

	private boolean fOrderAlphabetically;

	/**
	 * Creates a comparator that sorts by relevance.
	 */
	public CompletionProposalComparator() {
		fOrderAlphabetically= false;
	}

	/**
	 * Sets the sort order. Default is <code>false</code>, i.e. order by
	 * relevance.
	 *
	 * @param orderAlphabetically <code>true</code> to order alphabetically,
	 *        <code>false</code> to order by relevance
	 */
	public void setOrderAlphabetically(boolean orderAlphabetically) {
		fOrderAlphabetically= orderAlphabetically;
	}

	/*
	 * @see Comparator#compare(Object, Object)
	 */
	public int compare(Object o1, Object o2) {
		ICompletionProposal p1= (ICompletionProposal) o1;
		ICompletionProposal p2= (ICompletionProposal) o2;

		if (!fOrderAlphabetically) {
			int r1= getRelevance(p1);
			int r2= getRelevance(p2);
			int relevanceDif= r2 - r1;
			if (relevanceDif != 0) {
				return relevanceDif;
			}
		}
		/*
		 * TODO the correct (but possibly much slower) sorting would use a
		 * collator.
		 */
		// fix for bug 67468
		return getSortKey(p1).compareToIgnoreCase(getSortKey(p2));
	}

	private String getSortKey(ICompletionProposal p) {
		if (p instanceof AbstractRubyCompletionProposal)
			return ((AbstractRubyCompletionProposal) p).getSortString();
		return p.getDisplayString();
	}

	private int getRelevance(ICompletionProposal obj) {
		if (obj instanceof IRubyCompletionProposal) {
			IRubyCompletionProposal jcp= (IRubyCompletionProposal) obj;
			return jcp.getRelevance();
		} else if (obj instanceof TemplateProposal) {
			TemplateProposal tp= (TemplateProposal) obj;
			return tp.getRelevance();
		}
		// catch all
		return 0;
	}

}
