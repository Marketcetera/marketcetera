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

import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.texteditor.HippieProposalProcessor;
import org.rubypeople.rdt.ui.text.ruby.ContentAssistInvocationContext;
import org.rubypeople.rdt.ui.text.ruby.IRubyCompletionProposalComputer;


/**
 * A computer wrapper for the hippie processor.
 * 
 * @since 1.0
 */
public final class HippieProposalComputer implements IRubyCompletionProposalComputer {
	/** The wrapped processor. */
	private final HippieProposalProcessor fProcessor= new HippieProposalProcessor();

	/**
	 * Default ctor to make it instantiatable via the extension mechanism.
	 */
	public HippieProposalComputer() {
	}
	
	/*
	 * @see org.eclipse.jface.text.contentassist.ICompletionProposalComputer#computeCompletionProposals(org.eclipse.jface.text.contentassist.TextContentAssistInvocationContext, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public List computeCompletionProposals(ContentAssistInvocationContext context, IProgressMonitor monitor) {
		return Arrays.asList(fProcessor.computeCompletionProposals(context.getViewer(), context.getInvocationOffset()));
	}

	/*
	 * @see org.eclipse.jface.text.contentassist.ICompletionProposalComputer#computeContextInformation(org.eclipse.jface.text.contentassist.TextContentAssistInvocationContext, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public List computeContextInformation(ContentAssistInvocationContext context, IProgressMonitor monitor) {
		return Arrays.asList(fProcessor.computeContextInformation(context.getViewer(), context.getInvocationOffset()));
	}

	/*
	 * @see org.eclipse.jface.text.contentassist.ICompletionProposalComputer#getErrorMessage()
	 */
	public String getErrorMessage() {
		return fProcessor.getErrorMessage();
	}

	/*
	 * @see org.rubypeople.rdt.ui.text.ruby.IRubyCompletionProposalComputer#sessionStarted()
	 */
	public void sessionStarted() {
	}

	/*
	 * @see org.rubypeople.rdt.ui.text.ruby.IRubyCompletionProposalComputer#sessionEnded()
	 */
	public void sessionEnded() {
	}
}
