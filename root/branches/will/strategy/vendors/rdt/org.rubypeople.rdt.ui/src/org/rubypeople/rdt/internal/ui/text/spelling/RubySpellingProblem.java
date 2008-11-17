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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.IAnnotationModelExtension;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.spelling.SpellingProblem;
import org.rubypeople.rdt.internal.corext.util.Messages;
import org.rubypeople.rdt.internal.ui.RubyPlugin;
import org.rubypeople.rdt.internal.ui.RubyUIMessages;
import org.rubypeople.rdt.internal.ui.rubyeditor.RubyDocumentProvider.ProblemAnnotation;
import org.rubypeople.rdt.internal.ui.text.spelling.engine.ISpellEvent;

/**
 * A {@link SpellingProblem} that adapts a {@link ISpellEvent}.
 * <p>
 * TODO: remove {@link ISpellEvent} notification mechanism
 * </p>
 */
public class RubySpellingProblem extends SpellingProblem {

	/** Spell event */
	private ISpellEvent fSpellEvent;

	/**
	 * Initialize with the given spell event.
	 *
	 * @param spellEvent the spell event
	 */
	public RubySpellingProblem(ISpellEvent spellEvent) {
		super();
		fSpellEvent= spellEvent;
	}

	/*
	 * @see org.eclipse.ui.texteditor.spelling.SpellingProblem#getOffset()
	 */
	public int getOffset() {
		return fSpellEvent.getBegin();
	}

	/*
	 * @see org.eclipse.ui.texteditor.spelling.SpellingProblem#getLength()
	 */
	public int getLength() {
		return fSpellEvent.getEnd() - fSpellEvent.getBegin() + 1;
	}

	/*
	 * @see org.eclipse.ui.texteditor.spelling.SpellingProblem#getMessage()
	 */
	public String getMessage() {
		if (isSentenceStart() && isDictionaryMatch())
			return Messages.format(RubyUIMessages.Spelling_error_case_label, new String[] { fSpellEvent.getWord() });

		return Messages.format(RubyUIMessages.Spelling_error_label, new String[] { fSpellEvent.getWord() });
	}

	/*
	 * @see org.eclipse.ui.texteditor.spelling.SpellingProblem#getProposals()
	 */
	public ICompletionProposal[] getProposals() {
		/*
		 * TODO: implement, see WordQuickFixProcessor
		 * isDictionaryMatch() and isSentenceStart() are workarounds
		 * that could be removed once getProposals() is implemented
		 */
		return new ICompletionProposal[0];
	}

	/**
	 * Returns <code>true</code> iff the corresponding word was found in the dictionary.
	 * <p>
	 * NOTE: to be removed, see {@link #getProposals()}
	 * </p>
	 *
	 * @return <code>true</code> iff the corresponding word was found in the dictionary
	 */
	public boolean isDictionaryMatch() {
		return fSpellEvent.isMatch();
	}

	/**
	 * Returns <code>true</code> iff the corresponding word starts a sentence.
	 * <p>
	 * NOTE: to be removed, see {@link #getProposals()}
	 * </p>
	 *
	 * @return <code>true</code> iff the corresponding word starts a sentence
	 */
	public boolean isSentenceStart() {
		return fSpellEvent.isStart();
	}
	
	/**
	 * Removes all spelling problems that are reported
	 * for the given <code>word</code> in the active editor.
	 * <p>
	 * <em>This a workaround to fix bug https://bugs.eclipse.org/bugs/show_bug.cgi?id=134338
	 * for 3.2 at the time where spelling still resides in JDT Text.
	 * Once we move the spell check engine along with its quick fixes
	 * down to Platform Text we need to provide the proposals with
	 * a way to access the annotation model.</em>
	 * </p>
	 * 
	 * @param word the word for which to remove the problems
	 * @since 3.2
	 */
	public static void removeAllInActiveEditor(String word) {
		if (word == null)
			return;
		
		IWorkbenchPage activePage= RubyPlugin.getActivePage();
		if (activePage == null)
			return;

		IEditorPart editor= activePage.getActiveEditor();
		if (activePage.getActivePart() != editor ||  !(editor instanceof ITextEditor))
			return;
		
		IDocumentProvider documentProvider= ((ITextEditor)editor).getDocumentProvider();
		if (documentProvider == null)
			return;
		
		IAnnotationModel model= documentProvider.getAnnotationModel(editor.getEditorInput());
		if (model == null)
			return;
		
		boolean supportsBatchReplace= (model instanceof IAnnotationModelExtension);
		List toBeRemovedAnnotations= new ArrayList();
		Iterator iter= model.getAnnotationIterator();
		while (iter.hasNext()) {
			Annotation annotation= (Annotation)iter.next();
			if (ProblemAnnotation.SPELLING_ANNOTATION_TYPE.equals(annotation.getType()) && annotation instanceof ProblemAnnotation) {
				String[] arguments= ((ProblemAnnotation)annotation).getArguments();
				if (arguments != null && arguments.length > 0 && word.equals(arguments[0]))
					if (supportsBatchReplace)
						toBeRemovedAnnotations.add(annotation);
					else
						model.removeAnnotation(annotation);
			}
		}
		
		if (supportsBatchReplace && !toBeRemovedAnnotations.isEmpty()) {
			Annotation[] annotationArray= (Annotation[])toBeRemovedAnnotations.toArray(new Annotation[toBeRemovedAnnotations.size()]);
			((IAnnotationModelExtension)model).replaceAnnotations(annotationArray, null);
		}
	}

}
