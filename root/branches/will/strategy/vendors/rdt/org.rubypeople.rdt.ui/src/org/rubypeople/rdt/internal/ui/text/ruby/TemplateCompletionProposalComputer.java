package org.rubypeople.rdt.internal.ui.text.ruby;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.templates.TemplateContextType;
import org.rubypeople.rdt.core.IRubyScript;
import org.rubypeople.rdt.internal.corext.template.ruby.RubyContextType;
import org.rubypeople.rdt.internal.ui.RubyPlugin;
import org.rubypeople.rdt.internal.ui.RubyPluginImages;
import org.rubypeople.rdt.internal.ui.text.template.contentassist.TemplateEngine;
import org.rubypeople.rdt.internal.ui.text.template.contentassist.TemplateProposal;
import org.rubypeople.rdt.ui.text.RubyTextTools;
import org.rubypeople.rdt.ui.text.ruby.ContentAssistInvocationContext;
import org.rubypeople.rdt.ui.text.ruby.IRubyCompletionProposal;
import org.rubypeople.rdt.ui.text.ruby.IRubyCompletionProposalComputer;

public class TemplateCompletionProposalComputer implements IRubyCompletionProposalComputer {

	private TemplateEngine fRubyTemplateEngine;

	public TemplateCompletionProposalComputer() {
		TemplateContextType contextType = RubyPlugin.getDefault().getTemplateContextRegistry().getContextType(RubyContextType.NAME);
		if (contextType == null) {
			contextType = new RubyContextType();
			RubyPlugin.getDefault().getTemplateContextRegistry().addContextType(contextType);
		}
		if (contextType != null)
			fRubyTemplateEngine = new TemplateEngine(contextType);
		else
			fRubyTemplateEngine = null;
	}

	public List computeCompletionProposals(ContentAssistInvocationContext context, IProgressMonitor monitor) {
		TemplateEngine engine = fRubyTemplateEngine;
		
		if (engine != null) {
			IRubyScript unit = null;
			if (context instanceof RubyContentAssistInvocationContext) {
				RubyContentAssistInvocationContext rContext = (RubyContentAssistInvocationContext) context;
				unit = rContext.getRubyScript();
			}
			if (unit == null)
				return Collections.EMPTY_LIST;

			engine.reset();
			engine.complete(context.getViewer(), context.getInvocationOffset(), unit);

			TemplateProposal[] templateProposals = engine.getResults();
			List result = new ArrayList(Arrays.asList(templateProposals));

			IRubyCompletionProposal[] keyWordResults = getKeywordProposals(context);
			if (keyWordResults.length > 0) {
				// update relevance of template proposals that match with a
				// keyword
				// give those templates slightly more relevance than the keyword
				// to
				// sort them first
				// remove keyword templates that don't have an equivalent
				// keyword proposal
				if (keyWordResults.length > 0) {
					outer: for (int k = 0; k < templateProposals.length; k++) {
						TemplateProposal curr = templateProposals[k];
						String name = curr.getTemplate().getName();
						for (int i = 0; i < keyWordResults.length; i++) {
							String keyword = keyWordResults[i]
									.getDisplayString();
							if (name.startsWith(keyword)) {
								curr.setRelevance(keyWordResults[i]
										.getRelevance() + 1);
								continue outer;
							}
						}

					}
				}
			}
			return result;
		}
		return Collections.EMPTY_LIST;
	}

	private IRubyCompletionProposal[] getKeywordProposals(ContentAssistInvocationContext context) {
		List keywords = getKeywords();
		List fKeywords = new ArrayList();
		for (Iterator iter = keywords.iterator(); iter.hasNext();) {
			String keyword = (String) iter.next();
			String prefix = getCurrentPrefix(context.getDocument().get(), context.getInvocationOffset());
			if (prefix.length() >= keyword.length())
				continue;
			fKeywords.add(createKeywordProposal(keyword, prefix, context.getInvocationOffset()));
		}
		return (IRubyCompletionProposal[]) fKeywords.toArray(new RubyCompletionProposal[fKeywords.size()]);
	}
	
	protected String getCurrentPrefix(String documentString, int documentOffset) {
		int tokenLength = 0;
		while ((documentOffset - tokenLength > 0)
				&& !Character.isWhitespace(documentString.charAt(documentOffset
						- tokenLength - 1)))
			tokenLength++;
		return documentString.substring((documentOffset - tokenLength), documentOffset);
	}

	private IRubyCompletionProposal createKeywordProposal(String keyword, String prefix, int documentOffset) {
		String completion = keyword
				.substring(prefix.length(), keyword.length());
		return new RubyCompletionProposal(completion, documentOffset, completion.length(), RubyPluginImages.get(RubyPluginImages.IMG_OBJS_TEMPLATE), keyword, 0);
	}

	private List getKeywords() {
		List list = new ArrayList();
		String[] keywords = RubyTextTools.getKeyWords();
		for (int i = 0; i < keywords.length; i++) {
			list.add(keywords[i]);
		}
		return list;
	}
	
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

}
