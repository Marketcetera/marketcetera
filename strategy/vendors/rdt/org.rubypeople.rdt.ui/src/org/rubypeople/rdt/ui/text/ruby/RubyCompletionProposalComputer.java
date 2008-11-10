package org.rubypeople.rdt.ui.text.ruby;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.rubypeople.rdt.core.CompletionProposal;
import org.rubypeople.rdt.internal.ui.text.ruby.RubyContentAssistInvocationContext;

public abstract class RubyCompletionProposalComputer implements IRubyCompletionProposalComputer {
	
	protected RubyContentAssistInvocationContext fContext;

	public List computeCompletionProposals(ContentAssistInvocationContext context, IProgressMonitor monitor) {
		if (!(context instanceof RubyContentAssistInvocationContext))
			return Collections.EMPTY_LIST;
		
		fContext = (RubyContentAssistInvocationContext) context;
		
		CompletionProposalCollector collector = createCollector(fContext);
		List<CompletionProposal> proposals = doComputeCompletionProposals(fContext, monitor);
		for (CompletionProposal proposal : proposals) {
			collector.accept(proposal);
		}
		
		fContext = null;
		return Arrays.asList(collector.getRubyCompletionProposals());
	}

	protected abstract List<CompletionProposal> doComputeCompletionProposals(RubyContentAssistInvocationContext context, IProgressMonitor monitor);

	public List computeContextInformation(ContentAssistInvocationContext context, IProgressMonitor monitor) {
		return Collections.EMPTY_LIST;
	}

	public String getErrorMessage() {
		return null;
	}

	public void sessionEnded() {
	}

	public void sessionStarted() {
	}
	
	protected CompletionProposalCollector createCollector(RubyContentAssistInvocationContext context) {
		return new CompletionProposalCollector(context);
	}

}
