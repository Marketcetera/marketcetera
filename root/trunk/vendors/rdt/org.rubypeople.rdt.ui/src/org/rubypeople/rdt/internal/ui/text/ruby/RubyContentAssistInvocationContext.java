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

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.ui.IEditorPart;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IRubyProject;
import org.rubypeople.rdt.core.IRubyScript;
import org.rubypeople.rdt.internal.ui.rubyeditor.EditorUtility;
import org.rubypeople.rdt.ui.text.ruby.ContentAssistInvocationContext;

/**
 * Describes the context of a content assist invocation in a Ruby editor.
 * <p>
 * Clients may use but not subclass this class.
 * </p>
 * 
 * @since 1.0.0
 */
public class RubyContentAssistInvocationContext extends ContentAssistInvocationContext {
	private final IEditorPart fEditor;
	
	private IRubyScript fCU= null;
	private boolean fCUComputed= false;
	
	private CompletionProposalLabelProvider fLabelProvider;

	/**
	 * Creates a new context.
	 * 
	 * @param viewer the viewer used by the editor
	 * @param offset the invocation offset
	 * @param editor the editor that content assist is invoked in
	 */
	public RubyContentAssistInvocationContext(ITextViewer viewer, int offset, IEditorPart editor) {
		super(viewer, offset);
		Assert.isNotNull(editor);
		fEditor= editor;
	}
	
	/**
	 * Creates a new context.
	 * 
	 * @param unit the compilation unit in <code>document</code>
	 */
	public RubyContentAssistInvocationContext(IRubyScript unit) {
		super();
		fCU= unit;
		fCUComputed= true;
		fEditor= null;
	}
	
	/**
	 * Returns the compilation unit that content assist is invoked in, <code>null</code> if there
	 * is none.
	 * 
	 * @return the compilation unit that content assist is invoked in, possibly <code>null</code>
	 */
	public IRubyScript getRubyScript() {
		if (!fCUComputed) {
			fCUComputed= true;
//			if (fCollector != null)
//				fCU= fCollector.getRubyScript();
//			else {
				IRubyElement re= EditorUtility.getEditorInputRubyElement(fEditor, false);
				if (re instanceof IRubyScript)
					fCU= (IRubyScript)re;
//			}
		}
		return fCU;
	}
	
	/**
	 * Returns the project of the compilation unit that content assist is invoked in,
	 * <code>null</code> if none.
	 * 
	 * @return the current ruby project, possibly <code>null</code>
	 */
	public IRubyProject getProject() {
		IRubyScript unit= getRubyScript();
		return unit == null ? null : unit.getRubyProject();
	}
	
	/**
	 * Returns a label provider that can be used to compute proposal labels.
	 * 
	 * @return a label provider that can be used to compute proposal labels
	 */
	public CompletionProposalLabelProvider getLabelProvider() {
		if (fLabelProvider == null) {
//			if (fCollector != null)
//				fLabelProvider= fCollector.getLabelProvider();
//			else
				fLabelProvider= new CompletionProposalLabelProvider();
		}

		return fLabelProvider;
	}
	
	/*
	 * Implementation note: There is no need to override hashCode and equals, as we only add cached
	 * values shared across one assist invocation.
	 */
}
