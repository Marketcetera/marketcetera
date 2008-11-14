/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.rubypeople.rdt.internal.ui.search;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.search.ui.NewSearchUI;
import org.jruby.ast.Node;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IRubyScript;
import org.rubypeople.rdt.core.ISourceReference;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.internal.ui.RubyPlugin;
import org.rubypeople.rdt.internal.ui.rubyeditor.ASTProvider;

public abstract class FindOccurrencesEngine {
	
	private IOccurrencesFinder fFinder;

	private static class FindOccurencesCUEngine extends FindOccurrencesEngine {
		private IRubyScript fScript;
		
		public FindOccurencesCUEngine(IRubyScript unit, IOccurrencesFinder finder) {
			super(finder);
			fScript= unit;
		}
		protected Node createAST() {
			return RubyPlugin.getDefault().getASTProvider().getAST(fScript, ASTProvider.WAIT_YES, null);
		}
		protected IRubyElement getInput() {
			return fScript;
		}
		protected ISourceReference getSourceReference() {
			return fScript;
		}
	}
	
	protected FindOccurrencesEngine(IOccurrencesFinder finder) {
		fFinder= finder;
	}
	
	public static FindOccurrencesEngine create(IRubyElement root, IOccurrencesFinder finder) {
		if (root == null || finder == null)
			return null;
		
		IRubyScript unit= (IRubyScript)root.getAncestor(IRubyElement.SCRIPT);
		if (unit != null)
			return new FindOccurencesCUEngine(unit, finder);
		return null;
	}

	protected abstract Node createAST();
	
	protected abstract IRubyElement getInput();
	
	protected abstract ISourceReference getSourceReference();
	
	protected IOccurrencesFinder getOccurrencesFinder() {
		return fFinder;
	}

	public String run(int offset, int length) throws RubyModelException {
		ISourceReference sr= getSourceReference();
		if (sr.getSourceRange() == null) {
			return SearchMessages.FindOccurrencesEngine_noSource_text; 
		}
		
		final Node root= createAST();
		if (root == null) {
			return SearchMessages.FindOccurrencesEngine_cannotParse_text; 
		}
		String message= fFinder.initialize(root, offset, length);
		if (message != null)
			return message;
		// FIXME We shouldn't have to set these!
		fFinder.setFMarkConstantOccurrences(true);
		fFinder.setFMarkFieldOccurrences(true);
		fFinder.setFMarkLocalVariableOccurrences(true);
		fFinder.setFMarkTypeOccurrences(true);
		fFinder.setFMarkMethodOccurrences(true);
		
		final IDocument document= new Document(getSourceReference().getSource());
		
		performNewSearch(fFinder, document, getInput());
		return null;
	}
	
	private void performNewSearch(IOccurrencesFinder finder, IDocument document, IRubyElement element) {
		NewSearchUI.runQueryInBackground(new OccurrencesSearchQuery(finder, document, element));
	}
}
