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

package org.rubypeople.rdt.internal.ui.text.spelling;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.rubypeople.rdt.internal.corext.util.Messages;
import org.rubypeople.rdt.internal.ui.RubyPluginImages;
import org.rubypeople.rdt.internal.ui.RubyUIMessages;
import org.rubypeople.rdt.internal.ui.text.spelling.engine.ISpellCheckEngine;
import org.rubypeople.rdt.internal.ui.text.spelling.engine.ISpellChecker;
import org.rubypeople.rdt.ui.PreferenceConstants;
import org.rubypeople.rdt.ui.text.ruby.IInvocationContext;
import org.rubypeople.rdt.ui.text.ruby.IRubyCompletionProposal;

/**
 * Proposal to ignore the word during the current editing session.
 *
 * @since 3.0
 */
public class WordIgnoreProposal implements IRubyCompletionProposal {

	/** The invocation context */
	private IInvocationContext fContext;

	/** The word to ignore */
	private String fWord;

	/**
	 * Creates a new spell ignore proposal.
	 *
	 * @param word
	 *                   The word to ignore
	 * @param context
	 *                   The invocation context
	 */
	public WordIgnoreProposal(final String word, final IInvocationContext context) {
		fWord= word;
		fContext= context;
	}

	/*
	 * @see org.eclipse.jface.text.contentassist.ICompletionProposal#apply(org.eclipse.jface.text.IDocument)
	 */
	public final void apply(final IDocument document) {

		final ISpellCheckEngine engine= SpellCheckEngine.getInstance();
		final ISpellChecker checker= engine.createSpellChecker(engine.getLocale(), PreferenceConstants.getPreferenceStore());

		if (checker != null) {
			checker.ignoreWord(fWord);
			RubySpellingProblem.removeAllInActiveEditor(fWord);
		}
	}

	/*
	 * @see org.eclipse.jface.text.contentassist.ICompletionProposal#getAdditionalProposalInfo()
	 */
	public String getAdditionalProposalInfo() {
		return Messages.format(RubyUIMessages.Spelling_ignore_info, new String[] { WordCorrectionProposal.getHtmlRepresentation(fWord)});
	}

	/*
	 * @see org.eclipse.jface.text.contentassist.ICompletionProposal#getContextInformation()
	 */
	public final IContextInformation getContextInformation() {
		return null;
	}

	/*
	 * @see org.eclipse.jface.text.contentassist.ICompletionProposal#getDisplayString()
	 */
	public String getDisplayString() {
		return Messages.format(RubyUIMessages.Spelling_ignore_label, new String[] { fWord });
	}

	/*
	 * @see org.eclipse.jface.text.contentassist.ICompletionProposal#getImage()
	 */
	public Image getImage() {
		return RubyPluginImages.get(RubyPluginImages.IMG_OBJS_NLS_NEVER_TRANSLATE);
	}
	/*
	 * @see org.eclipse.jdt.ui.text.java.IRubyCompletionProposal#getRelevance()
	 */
	public final int getRelevance() {
		return Integer.MIN_VALUE + 1;
	}

	/*
	 * @see org.eclipse.jface.text.contentassist.ICompletionProposal#getSelection(org.eclipse.jface.text.IDocument)
	 */
	public final Point getSelection(final IDocument document) {
		return new Point(fContext.getSelectionOffset(), fContext.getSelectionLength());
	}
}
