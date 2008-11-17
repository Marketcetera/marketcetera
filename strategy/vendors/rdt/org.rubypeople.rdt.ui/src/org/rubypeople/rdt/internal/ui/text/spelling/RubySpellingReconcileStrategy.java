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

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.reconciler.DirtyRegion;
import org.eclipse.jface.text.reconciler.IReconcilingStrategy;
import org.eclipse.jface.text.reconciler.IReconcilingStrategyExtension;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.editors.text.EditorsUI;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.spelling.ISpellingProblemCollector;
import org.eclipse.ui.texteditor.spelling.SpellingContext;
import org.eclipse.ui.texteditor.spelling.SpellingProblem;
import org.eclipse.ui.texteditor.spelling.SpellingService;
import org.rubypeople.rdt.core.IProblemRequestor;
import org.rubypeople.rdt.core.RubyCore;
import org.rubypeople.rdt.core.compiler.IProblem;

/**
 * Reconcile strategy for spell checking comments.
 *
 * @since 3.1
 */
public class RubySpellingReconcileStrategy implements IReconcilingStrategy, IReconcilingStrategyExtension {

	/**
	 * Spelling problem collector that forwards {@link SpellingProblem}s as
	 * {@link IProblem}s to the {@link IProblemRequestor}.
	 */
	private class SpellingProblemCollector implements ISpellingProblemCollector {

		/*
		 * @see org.eclipse.ui.texteditor.spelling.ISpellingProblemCollector#accept(org.eclipse.ui.texteditor.spelling.SpellingProblem)
		 */
		public void accept(SpellingProblem problem) {
			IProblemRequestor requestor= fRequestor;
			if (requestor != null) {
				try {
					int line= fDocument.getLineOfOffset(problem.getOffset()) + 1;
					String word= fDocument.get(problem.getOffset(), problem.getLength());
					boolean dictionaryMatch= false;
					boolean sentenceStart= false;
					if (problem instanceof RubySpellingProblem) {
						dictionaryMatch= ((RubySpellingProblem)problem).isDictionaryMatch();
						sentenceStart= ((RubySpellingProblem) problem).isSentenceStart();
					}
					// see: https://bugs.eclipse.org/bugs/show_bug.cgi?id=81514
					IEditorInput editorInput= fEditor.getEditorInput();
					if (editorInput != null) {
						CoreSpellingProblem iProblem= new CoreSpellingProblem(problem.getOffset(), problem.getOffset() + problem.getLength() - 1, line, problem.getMessage(), word, dictionaryMatch, sentenceStart, fDocument, editorInput.getName());
						requestor.acceptProblem(iProblem);
					}
				} catch (BadLocationException x) {
					// drop this SpellingProblem
				}
			}
		}

		/*
		 * @see org.eclipse.ui.texteditor.spelling.ISpellingProblemCollector#beginCollecting()
		 */
		public void beginCollecting() {
			if (fRequestor != null)
				fRequestor.beginReporting();
		}

		/*
		 * @see org.eclipse.ui.texteditor.spelling.ISpellingProblemCollector#endCollecting()
		 */
		public void endCollecting() {
			if (fRequestor != null)
				fRequestor.endReporting();
		}
	}

	/** The id of the problem */
	public static final int SPELLING_PROBLEM_ID= 0x80000000;

	/** The text editor to operate on. */
	private ITextEditor fEditor;

	/** The document to operate on. */
	private IDocument fDocument;

	/** The progress monitor. */
	private IProgressMonitor fProgressMonitor;

	/** The problem requester. */
	private IProblemRequestor fRequestor;

	/** The spelling problem collector. */
	private ISpellingProblemCollector fCollector;
	
	/**
	 * The spelling context containing the Ruby source
	 * content type.
	 * <p>
	 * Since his reconcile strategy is for the Compilation Unit
	 * editor which normally edits Ruby source files we always
	 * use the Ruby properties file content type for performance
	 * reasons.
	 * </p>
	 * @since 3.2
	 */
	private SpellingContext fSpellingContext;

	
	/**
	 * Creates a new comment reconcile strategy.
	 *
	 * @param editor the text editor to operate on
	 */
	public RubySpellingReconcileStrategy(ITextEditor editor) {
		fEditor= editor;
		fCollector= new SpellingProblemCollector();
		fSpellingContext= new SpellingContext();
		fSpellingContext.setContentType(Platform.getContentTypeManager().getContentType(RubyCore.RUBY_SOURCE_CONTENT_TYPE));
		updateProblemRequester();
	}

	/*
	 * @see org.eclipse.jface.text.reconciler.IReconcilingStrategyExtension#initialReconcile()
	 */
	public void initialReconcile() {
		reconcile(new Region(0, fDocument.getLength()));
	}

	/*
	 * @see org.eclipse.jface.text.reconciler.IReconcilingStrategy#reconcile(org.eclipse.jface.text.reconciler.DirtyRegion,org.eclipse.jface.text.IRegion)
	 */
	public void reconcile(DirtyRegion dirtyRegion, IRegion subRegion) {
		reconcile(subRegion);
	}

	/*
	 * @see org.eclipse.jface.text.reconciler.IReconcilingStrategy#reconcile(org.eclipse.jface.text.IRegion)
	 */
	public void reconcile(IRegion region) {
		if (fRequestor != null && isSpellingEnabled())
			EditorsUI.getSpellingService().check(fDocument, fSpellingContext, fCollector, fProgressMonitor);
	}
	
	private boolean isSpellingEnabled() {
		return EditorsUI.getPreferenceStore().getBoolean(SpellingService.PREFERENCE_SPELLING_ENABLED);
	}

	/*
	 * @see org.eclipse.jface.text.reconciler.IReconcilingStrategy#setDocument(org.eclipse.jface.text.IDocument)
	 */
	public void setDocument(IDocument document) {
		fDocument= document;
		updateProblemRequester();
	}

	/*
	 * @see org.eclipse.jface.text.reconciler.IReconcilingStrategyExtension#setProgressMonitor(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void setProgressMonitor(IProgressMonitor monitor) {
		fProgressMonitor= monitor;
	}

	/**
	 * Update the problem requester based on the current editor
	 */
	private void updateProblemRequester() {
		IAnnotationModel model= fEditor.getDocumentProvider().getAnnotationModel(fEditor.getEditorInput());
		fRequestor= (model instanceof IProblemRequestor) ? (IProblemRequestor) model : null;
	}
}
