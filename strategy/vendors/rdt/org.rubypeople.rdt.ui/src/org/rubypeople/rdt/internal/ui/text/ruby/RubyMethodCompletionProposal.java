/*******************************************************************************
 * Copyright (c) 2005, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.rubypeople.rdt.internal.ui.text.ruby;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.rubypeople.rdt.core.CompletionProposal;
import org.rubypeople.rdt.core.IMember;
import org.rubypeople.rdt.core.IRubyProject;
import org.rubypeople.rdt.internal.ui.RubyPlugin;
import org.rubypeople.rdt.ui.PreferenceConstants;

public class RubyMethodCompletionProposal extends LazyRubyCompletionProposal {
	/** Triggers for method proposals without parameters. Do not modify. */
	protected final static char[] METHOD_TRIGGERS= new char[] { ';', ',', '.', '\t', '[' };
	/** Triggers for method proposals. Do not modify. */
	protected final static char[] METHOD_WITH_ARGUMENTS_TRIGGERS= new char[] { '(', '-', ' ' };
	/** Triggers for method name proposals (static imports). Do not modify. */
	protected final static char[] METHOD_NAME_TRIGGERS= new char[] { ';' };
	
	private boolean fHasParameters;
	private boolean fHasParametersComputed= false;
	private int fContextInformationPosition;

	public RubyMethodCompletionProposal(CompletionProposal proposal, RubyContentAssistInvocationContext context) {
		super(proposal, context);
	}

	public void apply(IDocument document, char trigger, int offset) {
		if (trigger == ' ' || trigger == '(')
			trigger= '\0';
		super.apply(document, trigger, offset);
		if (needsLinkedMode()) {
			setUpLinkedMode(document, ')');
		}
	}

	protected boolean needsLinkedMode() {
		return hasArgumentList() && hasParameters();
	}
	
	public CharSequence getPrefixCompletionText(IDocument document, int completionOffset) {
		if (hasArgumentList()) {
			String completion= String.valueOf(fProposal.getName());
//			if (isCamelCaseMatching()) {
//				String prefix= getPrefix(document, completionOffset);
//				return getCamelCaseCompound(prefix, completion);
//			}

			return completion;
		}
		return super.getPrefixCompletionText(document, completionOffset);
	}
	
	protected IContextInformation computeContextInformation() {
		// no context information for METHOD_NAME_REF proposals (e.g. for static imports)
		// https://bugs.eclipse.org/bugs/show_bug.cgi?id=94654
		if (fProposal.getKind() == CompletionProposal.METHOD_REF &&  hasParameters() && (getReplacementString().endsWith(RPAREN) || getReplacementString().length() == 0)) {
			ProposalContextInformation contextInformation= new ProposalContextInformation(fProposal);
			if (fContextInformationPosition != 0 && fProposal.getCompletion().length() == 0)
				contextInformation.setContextInformationPosition(fContextInformationPosition);
			return contextInformation;
		}
		return super.computeContextInformation();
	}
	
	protected char[] computeTriggerCharacters() {
		if (fProposal.getKind() == CompletionProposal.METHOD_NAME_REFERENCE)
			return METHOD_NAME_TRIGGERS;
		if (hasParameters())
			return METHOD_WITH_ARGUMENTS_TRIGGERS;
		return METHOD_TRIGGERS;
	}
	
	/**
	 * Returns <code>true</code> if the method being inserted has at least one parameter. Note
	 * that this does not say anything about whether the argument list should be inserted. This
	 * depends on the position in the document and the kind of proposal; see
	 * {@link #hasArgumentList() }.
	 * 
	 * @return <code>true</code> if the method has any parameters, <code>false</code> if it has
	 *         no parameters
	 */
	protected final boolean hasParameters() {
		if (!fHasParametersComputed) {
			fHasParametersComputed= true;
			fHasParameters= computeHasParameters();
		}
		return fHasParameters;
	}

	private boolean computeHasParameters() throws IllegalArgumentException {
		return fProposal.getParameterNames() != null && fProposal.getParameterNames().length > 0;
//		return Signature.getParameterCount(fProposal.getSignature()) > 0;
	}

	/**
	 * Returns <code>true</code> if the argument list should be inserted by the proposal,
	 * <code>false</code> if not.
	 * 
	 * @return <code>true</code> when the proposal is not in RDoc nor within an import and comprises the
	 *         parameter list
	 */
	protected boolean hasArgumentList() {
		if (CompletionProposal.METHOD_NAME_REFERENCE == fProposal.getKind())
			return false;
		IPreferenceStore preferenceStore= RubyPlugin.getDefault().getPreferenceStore();
		boolean noOverwrite= preferenceStore.getBoolean(PreferenceConstants.CODEASSIST_INSERT_COMPLETION) ^ isToggleEating();
		String completion= fProposal.getCompletion();
		return !isInRubydoc() && completion.length() > 0 && (noOverwrite  || completion.charAt(completion.length() - 1) == ')');
	}
	
	/*
	 * @see org.rubypeople.rdt.internal.ui.text.ruby.LazyRubyCompletionProposal#computeReplacementString()
	 */
	protected String computeReplacementString() {
		if (!hasArgumentList())
			return super.computeReplacementString();
		
		// we're inserting a method plus the argument list - respect formatter preferences
		StringBuffer buffer= new StringBuffer();
		buffer.append(fProposal.getName());

//		FormatterPrefs prefs= getFormatterPrefs();
//		if (prefs.beforeOpeningParen)
//			buffer.append(SPACE);
		buffer.append(LPAREN);
		
		if (hasParameters()) {
			setCursorPosition(buffer.length());
			
//			if (prefs.afterOpeningParen)
//				buffer.append(SPACE);
			

			// don't add the trailing space, but let the user type it in himself - typing the closing paren will exit
//			if (prefs.beforeClosingParen)
//				buffer.append(SPACE);
		} else {
//			if (prefs.inEmptyList)
//				buffer.append(SPACE);
		}

		buffer.append(RPAREN);

		return buffer.toString();

	}
	
	protected ProposalInfo computeProposalInfo() {
		IRubyProject project= fInvocationContext.getProject();
		if (project != null)
			return new ProposalInfo((IMember) fProposal.getElement());
		return super.computeProposalInfo();
	}
	
	/**
	 * Overrides the default context information position. Ignored if set to zero.
	 * 
	 * @param contextInformationPosition the replaced position.
	 */
	public void setContextInformationPosition(int contextInformationPosition) {
		fContextInformationPosition= contextInformationPosition;
	}
	
	/*
	 * @see org.rubypeople.rdt.internal.ui.text.ruby.LazyRubyCompletionProposal#computeSortString()
	 */
	protected String computeSortString() {
		/*
		 * Lexicographical sort order:
		 * 1) by relevance (done by the proposal sorter)
		 * 2) by method name
		 * 3) by parameter count
		 * 4) by parameter type names
		 */
		String name= fProposal.getName();
		String parameterList= toCharArray(fProposal.getParameterNames(), ',');
		int parameterCount= fProposal.getParameterNames().length % 10; // we don't care about insane methods with >9 parameters
		StringBuffer buf= new StringBuffer(name.length() + 2 + parameterList.length());
		
		buf.append(name);
		buf.append('\0'); // separator
		buf.append(parameterCount);
		buf.append(parameterList);
		return buf.toString();
	}
	
	private String toCharArray(String[] parameterNames, char c) {
		if (parameterNames == null) return "";
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < parameterNames.length; i++) {
			if (i > 0) buffer.append(c);
			buffer.append(parameterNames[i]);
		}
		return buffer.toString();
	}

	/*
	 * @see org.rubypeople.rdt.internal.ui.text.ruby.AbstractRubyCompletionProposal#isValidPrefix(java.lang.String)
	 */
	protected boolean isValidPrefix(String prefix) {
		if (super.isValidPrefix(prefix))
			return true;
		
		String word= getDisplayString();
		return isPrefix(prefix, word);
	}
}
