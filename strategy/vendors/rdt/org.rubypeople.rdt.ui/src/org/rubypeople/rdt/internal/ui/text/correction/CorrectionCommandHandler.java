/*******************************************************************************
 * Copyright (c) 2000, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.rubypeople.rdt.internal.ui.text.correction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.bindings.TriggerSequence;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension2;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.keys.IBindingService;
import org.eclipse.ui.texteditor.ITextEditor;
import org.jruby.ast.Node;
import org.rubypeople.rdt.core.IRubyScript;
import org.rubypeople.rdt.internal.ui.rubyeditor.RubyEditor;
import org.rubypeople.rdt.ui.RubyUI;
import org.rubypeople.rdt.ui.text.correction.ICommandAccess;
import org.rubypeople.rdt.ui.text.ruby.IInvocationContext;


/**
 * Handler to be used to run a quick fix or assist by keyboard shortcut
 */
public class CorrectionCommandHandler extends AbstractHandler {
		
	private final ITextEditor fEditor;
	private final String fId;
	private final boolean fIsAssist;

	public CorrectionCommandHandler(ITextEditor editor, String id, boolean isAssist) {
		fEditor= editor;
		fId= id;
		fIsAssist= isAssist;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.ExecutionEvent)
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ISelection selection= fEditor.getSelectionProvider().getSelection();
		IRubyScript cu= RubyUI.getWorkingCopyManager().getWorkingCopy(fEditor.getEditorInput());
		IAnnotationModel model= RubyUI.getDocumentProvider().getAnnotationModel(fEditor.getEditorInput());
		if (selection instanceof ITextSelection && cu != null && model != null) {
			ICompletionProposal proposal= findCorrection(fId, fIsAssist, (ITextSelection) selection, cu, model);
			if (proposal != null) {
				invokeProposal(proposal, ((ITextSelection) selection).getOffset());
			}
		}
		return null;
	}
	
	private ICompletionProposal findCorrection(String id, boolean isAssist, ITextSelection selection, IRubyScript cu, IAnnotationModel model) {
		AssistContext context= new AssistContext(cu, selection.getOffset(), selection.getLength());
		Collection proposals= new ArrayList(10);
		if (isAssist) {
//			if (id.equals(LinkedNamesAssistProposal.ASSIST_ID)) {
//				return getLocalRenameProposal(context); // shortcut for local rename
//			}
			RubyCorrectionProcessor.collectAssists(context, new ProblemLocation[0], proposals);
		} else {
			try {
				boolean goToClosest= selection.getLength() == 0; 
				Annotation[] annotations= getAnnotations(selection.getOffset(), goToClosest);
				RubyCorrectionProcessor.collectProposals(context, model, annotations, true, false, proposals);
			} catch (BadLocationException e) {
				return null;
			}
		}
		for (Iterator iter= proposals.iterator(); iter.hasNext();) {
			Object curr= iter.next();
			if (curr instanceof ICommandAccess) {
				if (id.equals(((ICommandAccess) curr).getCommandId())) {
					return (ICompletionProposal) curr;
				}
			}
		}
		return null;
	}

	private Annotation[] getAnnotations(int offset, boolean goToClosest) throws BadLocationException {
		ArrayList resultingAnnotations= new ArrayList();
		RubyCorrectionAssistant.collectQuickFixableAnnotations(fEditor, offset, goToClosest, resultingAnnotations);
		return (Annotation[]) resultingAnnotations.toArray(new Annotation[resultingAnnotations.size()]);
	}
	
	private ICompletionProposal getLocalRenameProposal(IInvocationContext context) {
		Node node= context.getCoveredNode();
//		if (node instanceof SimpleName) { FIXME What do we do here?
//			return new LinkedNamesAssistProposal(context.getRubyScript(), (SimpleName) node);
//		}
		return null;
	}

	private ITextViewer getTextViewer() {
		if (fEditor instanceof RubyEditor) {
			return ((RubyEditor) fEditor).getViewer();
		}
		return null;
	}
	
	private IDocument getDocument() {
		return RubyUI.getDocumentProvider().getDocument(fEditor.getEditorInput());
	}
	
	
	private void invokeProposal(ICompletionProposal proposal, int offset) {
		if (proposal instanceof ICompletionProposalExtension2) {
			ITextViewer viewer= getTextViewer();
			if (viewer != null) {
				((ICompletionProposalExtension2) proposal).apply(viewer, (char) 0, 0, offset);
				return;
			}
		} else if (proposal instanceof ICompletionProposalExtension) {
			IDocument document= getDocument();
			if (document != null) {
				((ICompletionProposalExtension) proposal).apply(document, (char) 0, offset);
				return;
			}
		}
		IDocument document= getDocument();
		if (document != null) {
			proposal.apply(document);
		}
	}
	
	public static String getShortCutString(String proposalId) {
		if (proposalId != null) {
			IBindingService bindingService= (IBindingService) PlatformUI.getWorkbench().getAdapter(IBindingService.class);
			if (bindingService != null) {
				TriggerSequence[] activeBindingsFor= bindingService.getActiveBindingsFor(proposalId);
				if (activeBindingsFor.length > 0) {
					return activeBindingsFor[0].format();
				}
			}
		}
		return null;
	}
	
}
