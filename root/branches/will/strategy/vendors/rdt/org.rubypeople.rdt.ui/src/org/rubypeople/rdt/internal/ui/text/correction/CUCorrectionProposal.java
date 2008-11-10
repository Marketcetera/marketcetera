/**
 * Copyright (c) 2008 Aptana, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl -v10.html. If redistributing this code,
 * this entire header must remain intact.
 *
 * This file is based on a JDT equivalent:
 ********************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.rubypeople.rdt.internal.ui.text.correction;

import org.eclipse.compare.rangedifferencer.IRangeComparator;
import org.eclipse.compare.rangedifferencer.RangeDifference;
import org.eclipse.compare.rangedifferencer.RangeDifferencer;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.DocumentChange;
import org.eclipse.ltk.core.refactoring.TextChange;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.ltk.internal.core.refactoring.Resources;
import org.eclipse.swt.graphics.Image;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.rubypeople.rdt.core.IRubyScript;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.internal.corext.codemanipulation.StubUtility;
import org.rubypeople.rdt.internal.corext.refactoring.changes.RubyScriptChange;
import org.rubypeople.rdt.internal.corext.util.Strings;
import org.rubypeople.rdt.internal.ui.RubyPlugin;
import org.rubypeople.rdt.internal.ui.RubyUIStatus;
import org.rubypeople.rdt.internal.ui.rubyeditor.EditorUtility;
import org.rubypeople.rdt.internal.ui.util.ExceptionHandler;
import org.rubypeople.rdt.ui.RubyUI;
import org.rubypeople.rdt.ui.text.correction.ChangeCorrectionProposal;

/**
 * A proposal for quick fixes and quick assist that work on a single compilation unit.
 * Either a {@link TextChange text change} is directly passed in the constructor or method
 * {@link #addEdits(IDocument, TextEdit)} is overridden to provide the text edits that are
 * applied to the document when the proposal is evaluated.
 * <p>
 * The proposal takes care of the preview of the changes as proposal information.
 * </p>
 * @since 3.2
 */
public class CUCorrectionProposal extends ChangeCorrectionProposal  {

	private IRubyScript fRubyScript;

	/**
	 * Constructs a correction proposal working on a compilation unit with a given text change
	 * 
	 * @param name the name that is displayed in the proposal selection dialog.
	 * @param cu the compilation unit on that the change works.
	 * @param change the change that is executed when the proposal is applied or <code>null</code>
	 * if implementors override {@link #addEdits(IDocument, TextEdit)} to provide
	 * the text edits or {@link #createTextChange()} to provide a text change.
	 * @param relevance the relevance of this proposal.
	 * @param image the image that is displayed for this proposal or <code>null</code> if no
	 * image is desired.
	 */
	public CUCorrectionProposal(String name, IRubyScript cu, TextChange change, int relevance, Image image) {
		super(name, change, relevance, image);
		if (cu == null) {
			throw new IllegalArgumentException("Ruby script must not be null"); //$NON-NLS-1$
		}
		fRubyScript= cu;
	}
	
	/**
	 * Constructs a correction proposal working on a compilation unit.
	 * <p>Users have to override {@link #addEdits(IDocument, TextEdit)} to provide
	 * the text edits or {@link #createTextChange()} to provide a text change.
	 * </p>
	 * 
	 * @param name The name that is displayed in the proposal selection dialog.
	 * @param cu The compilation unit on that the change works.
	 * @param relevance The relevance of this proposal.
	 * @param image The image that is displayed for this proposal or <code>null</code> if no
	 * image is desired.
	 */
	protected CUCorrectionProposal(String name, IRubyScript cu, int relevance, Image image) {
		this(name, cu, null, relevance, image);
	}

	/**
	 * Called when the {@link RubyScriptChange} is initialized. Subclasses can override to
	 * add text edits to the root edit of the change. Implementors must not access the proposal,
	 * e.g getting the change.
	 * <p>The default implementation does not add any edits</p>
	 * 
	 * @param document content of the underlying compilation unit. To be accessed read only.
	 * @param editRoot The root edit to add all edits to
	 * @throws CoreException can be thrown if adding the edits is failing.
	 */
	protected void addEdits(IDocument document, TextEdit editRoot) throws CoreException {
		if (false) {
			throw new CoreException(RubyUIStatus.createError(IStatus.ERROR, "Implementors can throw an exception", null)); //$NON-NLS-1$
		}
	}

	/*
	 * @see ICompletionProposal#getAdditionalProposalInfo()
	 */
	public String getAdditionalProposalInfo() {
		StringBuffer buf= new StringBuffer();

//		try {
//			TextChange change= getTextChange();
//
//			IDocument previewContent= change.getPreviewDocument(new NullProgressMonitor());
//			String currentConentString= change.getCurrentContent(new NullProgressMonitor());
//
//			/*
//			 * Do not change the type of those local variables. We use Object
//			 * here in order to prevent loading of the Compare plug-in at load
//			 * time of this class.
//			 */
//			Object leftSide= new RubyTokenComparator(previewContent.get(), true);
//			Object rightSide= new RubyTokenComparator(currentConentString, true);
//
//			RangeDifference[] differences= RangeDifferencer.findRanges((IRangeComparator)leftSide, (IRangeComparator)rightSide);
//			for (int i= 0; i < differences.length; i++) {
//				RangeDifference curr= differences[i];
//				int start= ((RubyTokenComparator)leftSide).getTokenStart(curr.leftStart());
//				int end= ((RubyTokenComparator)leftSide).getTokenStart(curr.leftEnd());
//				if (curr.kind() == RangeDifference.CHANGE && curr.leftLength() > 0) {
//					buf.append("<b>"); //$NON-NLS-1$
//					appendContent(previewContent, start, end, buf, false);
//					buf.append("</b>"); //$NON-NLS-1$
//				} else if (curr.kind() == RangeDifference.NOCHANGE) {
//					appendContent(previewContent, start, end, buf, true);
//				}
//			}
//		} catch (CoreException e) {
//			RubyPlugin.log(e);
//		} catch (BadLocationException e) {
//			RubyPlugin.log(e);
//		}
		return buf.toString();
	}

	private final int surroundLines= 1;

	private void appendContent(IDocument text, int startOffset, int endOffset, StringBuffer buf, boolean surroundLinesOnly) throws BadLocationException {
		int startLine= text.getLineOfOffset(startOffset);
		int endLine= text.getLineOfOffset(endOffset);

		boolean dotsAdded= false;
		if (surroundLinesOnly && startOffset == 0) { // no surround lines for the top no-change range
			startLine= Math.max(endLine - surroundLines, 0);
			buf.append("...<br>"); //$NON-NLS-1$
			dotsAdded= true;
		}

		for (int i= startLine; i <= endLine; i++) {
			if (surroundLinesOnly) {
				if ((i - startLine > surroundLines) && (endLine - i > surroundLines)) {
					if (!dotsAdded) {
						buf.append("...<br>"); //$NON-NLS-1$
						dotsAdded= true;
					} else if (endOffset == text.getLength()) {
						return; // no surround lines for the bottom no-change range
					}
					continue;
				}
			}

			IRegion lineInfo= text.getLineInformation(i);
			int start= lineInfo.getOffset();
			int end= start + lineInfo.getLength();

			int from= Math.max(start, startOffset);
			int to= Math.min(end, endOffset);
			String content= text.get(from, to - from);
			if (surroundLinesOnly && (from == start) && Strings.containsOnlyWhitespaces(content)) {
				continue; // ignore empty lines except when range started in the middle of a line
			}
			for (int k= 0; k < content.length(); k++) {
				char ch= content.charAt(k);
				if (ch == '<') {
					buf.append("&lt;"); //$NON-NLS-1$
				} else if (ch == '>') {
					buf.append("&gt;"); //$NON-NLS-1$
				} else {
					buf.append(ch);
				}
			}
			if (to == end && to != endOffset) { // new line when at the end of the line, and not end of range
				buf.append("<br>"); //$NON-NLS-1$
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.contentassist.ICompletionProposal#apply(org.eclipse.jface.text.IDocument)
	 */
	public void apply(IDocument document) {
		try {
			IRubyScript unit= getRubyScript();
			IEditorPart part= null;
			if (unit.getResource().exists()) {
				boolean canEdit= performValidateEdit(unit);
				if (!canEdit) {
					return;
				}
				part= EditorUtility.isOpenInEditor(unit);
				if (part == null) {
					part= EditorUtility.openInEditor(unit, true);
					if (part != null) {
						document= RubyUI.getDocumentProvider().getDocument(part.getEditorInput());
					}
				}
				IWorkbenchPage page= RubyPlugin.getActivePage();
				if (page != null && part != null) {
					page.bringToTop(part);
				}
				if (part != null) {
					part.setFocus();
				}
			}
			performChange(part, document);
		} catch (CoreException e) {
			ExceptionHandler.handle(e, CorrectionMessages.CUCorrectionProposal_error_title, CorrectionMessages.CUCorrectionProposal_error_message);
		}
	}

	private boolean performValidateEdit(IRubyScript unit) {
		IStatus status= Resources.makeCommittable(unit.getResource(), RubyPlugin.getActiveWorkbenchShell());
		if (!status.isOK()) {
			String label= CorrectionMessages.CUCorrectionProposal_error_title;
			String message= CorrectionMessages.CUCorrectionProposal_error_message;
			ErrorDialog.openError(RubyPlugin.getActiveWorkbenchShell(), label, message, status);
			return false;
		}
		return true;
	}
	
	/**
	 * Creates the text change for this proposal.
	 * This method is only called once and only when no text change has been passed in
	 * {@link #CUCorrectionProposal(String, IRubyScript, TextChange, int, Image)}.
	 * 
	 * @return returns the created text change.
	 * @throws CoreException thrown if the creation of the text change failed.
	 */
	protected TextChange createTextChange() throws CoreException {
		IRubyScript cu= getRubyScript();
		String name= getDisplayString();
		TextChange change;
		if (!cu.getResource().exists()) {
			String source;
			try {
				source= cu.getSource();
			} catch (RubyModelException e) {
				RubyPlugin.log(e);
				source= new String(); // empty
			}
			Document document= new Document(source);
			document.setInitialLineDelimiter(StubUtility.getLineDelimiterUsed(cu));
			change= new DocumentChange(name, document);
		} else {
			RubyScriptChange cuChange = new RubyScriptChange(name, cu);
			cuChange.setSaveMode(TextFileChange.LEAVE_DIRTY);
			change= cuChange;
		}
		TextEdit rootEdit= new MultiTextEdit();
		change.setEdit(rootEdit);

		// initialize text change
		IDocument document= change.getCurrentDocument(new NullProgressMonitor());
		addEdits(document, rootEdit);
		return change;
	}
		
	
	/* (non-Javadoc)
	 * @see org.rubypeople.rdt.internal.ui.text.correction.ChangeCorrectionProposal#createChange()
	 */
	protected final Change createChange() throws CoreException {
		return createTextChange(); // make sure that only text changes are allowed here
	}
	
	/**
	 * Gets the text change that is invoked when the change is applied.
	 * 
	 * @return returns the text change that is invoked when the change is applied.
	 * @throws CoreException throws an exception if accessing the change failed
	 */
	public final TextChange getTextChange() throws CoreException {
		return (TextChange) getChange();
	}

	/**
	 * The compilation unit on that the change works.
	 * 
	 * @return the compilation unit on that the change works.
	 */
	public final IRubyScript getRubyScript() {
		return fRubyScript;
	}

	/**
	 * Creates a preview of the content of the compilation unit after applying the change.
	 * 
	 * @return returns the preview of the changed compilation unit.
	 * @throws CoreException thrown if the creation of the change failed.
	 */
	public String getPreviewContent() throws CoreException {
		return getTextChange().getPreviewContent(new NullProgressMonitor());
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		try {
			return getPreviewContent();
		} catch (CoreException e) {
		}
		return super.toString();
	}
}
