package org.rubypeople.rdt.internal.ui.text.ruby;

import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.rubypeople.rdt.ui.text.ruby.ContentAssistInvocationContext;
import org.rubypeople.rdt.ui.text.ruby.IRubyCompletionProposalComputer;

public class LegacyRubyCompletionProposalComputer implements IRubyCompletionProposalComputer {

	private LegacyRubyCompletionProcessor fProcessor = new LegacyRubyCompletionProcessor();
	
	public List computeCompletionProposals(ContentAssistInvocationContext context, IProgressMonitor monitor) {
		if (context instanceof RubyContentAssistInvocationContext) {
			fProcessor.setRubyContentAssistInvocationContext((RubyContentAssistInvocationContext) context);
		}
		return Arrays.asList(fProcessor.computeCompletionProposals(context.getViewer(), context.getInvocationOffset()));
	}

	public List computeContextInformation(ContentAssistInvocationContext context, IProgressMonitor monitor) {
		if (context instanceof RubyContentAssistInvocationContext) {
			fProcessor.setRubyContentAssistInvocationContext((RubyContentAssistInvocationContext) context);
		}
		return Arrays.asList(fProcessor.computeContextInformation(context.getViewer(), context.getInvocationOffset()));
	}

	public String getErrorMessage() {
		return fProcessor.getErrorMessage();
	}

	public void sessionEnded() {
	}

	public void sessionStarted() {
	}

}
