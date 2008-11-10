package org.rubypeople.rdt.ui.text.correction;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.swt.graphics.Image;
import org.rubypeople.rdt.internal.ui.text.ruby.RubyCompletionProposal;

public class CorrectionProposal extends RubyCompletionProposal {

	public CorrectionProposal(String replacementString, int replacementOffset, int replacementLength, Image image, String displayString, int relevance) {
		super(replacementString, replacementOffset, replacementLength, image, displayString, relevance);
	}
	
	@Override
	protected boolean isValidPrefix(String prefix) {
		return true;
	}
	
	@Override
	public void apply(ITextViewer viewer, char trigger, int stateMask, int offset) {
		IDocument document= viewer.getDocument();
		apply(document, trigger, getReplacementOffset());
	}

}
