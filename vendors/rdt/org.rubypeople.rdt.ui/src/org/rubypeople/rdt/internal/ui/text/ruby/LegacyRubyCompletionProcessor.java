package org.rubypeople.rdt.internal.ui.text.ruby;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.TextPresentation;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationPresenter;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;
import org.eclipse.jface.text.templates.Template;
import org.eclipse.jface.text.templates.TemplateCompletionProcessor;
import org.eclipse.jface.text.templates.TemplateContextType;
import org.eclipse.swt.graphics.Image;
import org.rubypeople.rdt.core.IRubyScript;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.internal.corext.template.ruby.RubyContextType;
import org.rubypeople.rdt.internal.ui.RubyPlugin;
import org.rubypeople.rdt.internal.ui.RubyPluginImages;
import org.rubypeople.rdt.internal.ui.text.template.contentassist.RubyTemplateAccess;
import org.rubypeople.rdt.ui.text.ruby.CompletionProposalCollector;

public class LegacyRubyCompletionProcessor extends TemplateCompletionProcessor implements IContentAssistProcessor {

	protected IContextInformationValidator contextInformationValidator = new RubyContextInformationValidator();
	
	private RubyContentAssistInvocationContext context;

	public LegacyRubyCompletionProcessor() {
		super();
	}

	public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer, int documentOffset) {
		ITextSelection selection = (ITextSelection) viewer.getSelectionProvider().getSelection();
		return codeComplete(documentOffset);
	}
	
	private ICompletionProposal[] codeComplete(int offset) {
		try {
			IRubyScript script = getRubyScript();
			if (script == null) return new ICompletionProposal[0];
			CompletionProposalCollector requestor = new CompletionProposalCollector(context);
			script.codeComplete(offset - 1, requestor);
			return requestor.getRubyCompletionProposals();
		} catch (RubyModelException e) {
			RubyPlugin.log(e);
			return new ICompletionProposal[0];
		}		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.templates.TemplateCompletionProcessor#getImage(org.eclipse.jface.text.templates.Template)
	 */
	protected Image getImage(Template template) {
		return RubyPluginImages.get(RubyPluginImages.IMG_OBJS_TEMPLATE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.templates.TemplateCompletionProcessor#getContextType(org.eclipse.jface.text.ITextViewer,
	 *      org.eclipse.jface.text.IRegion)
	 */
	public TemplateContextType getContextType(ITextViewer textViewer,
			IRegion region) {
		return RubyTemplateAccess.getDefault().getContextTypeRegistry()
				.getContextType(RubyContextType.NAME);
	}

	public void setRubyContentAssistInvocationContext(RubyContentAssistInvocationContext context) {
		this.context = context;
	}

	private IRubyScript getRubyScript() {
		return this.context.getRubyScript();
	}

	protected String getCurrentPrefix(String documentString, int documentOffset) {
		int tokenLength = 0;
		while ((documentOffset - tokenLength > 0)
				&& !Character.isWhitespace(documentString.charAt(documentOffset
						- tokenLength - 1)))
			tokenLength++;
		return documentString.substring((documentOffset - tokenLength),
				documentOffset);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.templates.TemplateCompletionProcessor#getTemplates(java.lang.String)
	 */
	public Template[] getTemplates(String contextTypeId) {
		return RubyTemplateAccess.getDefault().getTemplateStore()
				.getTemplates();
	}

	public IContextInformation[] computeContextInformation(ITextViewer viewer,
			int documentOffset) {
		return new IContextInformation[0];
	}

	public char[] getCompletionProposalAutoActivationCharacters() {
		return null;
	}

	public char[] getContextInformationAutoActivationCharacters() {
		return new char[] { '#' };
	}

	public IContextInformationValidator getContextInformationValidator() {
		return contextInformationValidator;
	}

	public String getErrorMessage() {
		return null;
	}

	protected class RubyContextInformationValidator implements
			IContextInformationValidator, IContextInformationPresenter {

		protected int installDocumentPosition;

		/**
		 * @see org.eclipse.jface.text.contentassist.IContextInformationPresenter#install(IContextInformation,
		 *      ITextViewer, int)
		 */
		public void install(IContextInformation info, ITextViewer viewer,
				int documentPosition) {
			installDocumentPosition = documentPosition;
		}

		/**
		 * @see org.eclipse.jface.text.contentassist.IContextInformationValidator#isContextInformationValid(int)
		 */
		public boolean isContextInformationValid(int documentPosition) {
			return Math.abs(installDocumentPosition - documentPosition) < 1;
		}

		/**
		 * @see org.eclipse.jface.text.contentassist.IContextInformationPresenter#updatePresentation(int,
		 *      TextPresentation)
		 */
		public boolean updatePresentation(int documentPosition,
				TextPresentation presentation) {
			return false;
		}
	}
}