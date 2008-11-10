package org.rubypeople.rdt.internal.ui.text.ruby;

import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.ui.IEditorPart;
import org.rubypeople.rdt.ui.text.ruby.ContentAssistInvocationContext;

public class RubyCompletionProcessor extends ContentAssistProcessor {

	protected final IEditorPart fEditor;

	public RubyCompletionProcessor(IEditorPart editor, ContentAssistant assistant, String partition) {
		super(assistant, partition);
		fEditor= editor;
	}
	
	/*
	 * @see org.rubypeople.rdt.internal.ui.text.ruby.ContentAssistProcessor#createContext(org.eclipse.jface.text.ITextViewer, int)
	 */
	protected ContentAssistInvocationContext createContext(ITextViewer viewer, int offset) {
		return new RubyContentAssistInvocationContext(viewer, offset, fEditor);
	}
	
	/*
	 * @see org.rubypeople.rdt.internal.ui.text.ruby.ContentAssistProcessor#filterAndSort(java.util.List, org.eclipse.core.runtime.IProgressMonitor)
	 */
	protected List filterAndSortProposals(List proposals, IProgressMonitor monitor, ContentAssistInvocationContext context) {
		ProposalSorterRegistry.getDefault().getCurrentSorter().sortProposals(context, proposals);
		return proposals;
	}
}
