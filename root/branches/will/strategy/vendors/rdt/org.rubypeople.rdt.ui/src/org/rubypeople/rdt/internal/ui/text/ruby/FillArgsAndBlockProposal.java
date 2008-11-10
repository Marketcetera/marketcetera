/*******************************************************************************
 * Copyright (c) 2000, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.rubypeople.rdt.internal.ui.text.ruby;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.link.LinkedModeModel;
import org.eclipse.jface.text.link.LinkedModeUI;
import org.eclipse.jface.text.link.LinkedPosition;
import org.eclipse.jface.text.link.LinkedPositionGroup;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.texteditor.link.EditorLinkedModeUI;
import org.rubypeople.rdt.core.CompletionProposal;
import org.rubypeople.rdt.internal.ui.RubyPlugin;
import org.rubypeople.rdt.internal.ui.rubyeditor.EditorHighlightingSynchronizer;
import org.rubypeople.rdt.internal.ui.rubyeditor.RubyEditor;
import org.rubypeople.rdt.internal.ui.text.ruby.AbstractRubyCompletionProposal.ExitPolicy;

/**
 * An experimental proposal which inserts both method arguments and a block for methods that yield args
 */
public final class FillArgsAndBlockProposal extends RubyMethodCompletionProposal {

	private IRegion fSelectedRegion; // initialized by apply()
	private int[] fArgumentOffsets;
	private int[] fArgumentLengths;

	public FillArgsAndBlockProposal(CompletionProposal proposal, RubyContentAssistInvocationContext context) {
		super(proposal, context);
	}

	/*
	 * @see ICompletionProposalExtension#apply(IDocument, char)
	 */
	public void apply(IDocument document, char trigger, int offset) {
		super.apply(document, trigger, offset);
		int baseOffset= getReplacementOffset();
		String replacement= getReplacementString();

		if (fArgumentOffsets != null && getTextViewer() != null) {
			try {
				LinkedModeModel model= new LinkedModeModel();
				for (int i= 0; i != fArgumentOffsets.length; i++) {
					LinkedPositionGroup group= new LinkedPositionGroup();
					group.addPosition(new LinkedPosition(document, baseOffset + fArgumentOffsets[i], fArgumentLengths[i], LinkedPositionGroup.NO_STOP));
					model.addGroup(group);
				}

				model.forceInstall();
				RubyEditor editor= getRubyEditor();
				if (editor != null) {
					model.addLinkingListener(new EditorHighlightingSynchronizer(editor));
				}

				LinkedModeUI ui= new EditorLinkedModeUI(model, getTextViewer());
				ui.setExitPosition(getTextViewer(), baseOffset + replacement.length() - 2, 0, Integer.MAX_VALUE);
				ui.setExitPolicy(new ExitPolicy(')', document));
				ui.setDoContextInfo(true);
				ui.setCyclingMode(LinkedModeUI.CYCLE_WHEN_NO_PARENT);
				ui.enter();

				fSelectedRegion= ui.getSelectedRegion();

			} catch (BadLocationException e) {
				RubyPlugin.log(e);
				openErrorDialog(e);
			}
		} else {
			fSelectedRegion= new Region(baseOffset + replacement.length(), 0);
		}
	}
	
	/*
	 * @see org.rubypeople.rdt.internal.ui.text.ruby.RubyMethodCompletionProposal#needsLinkedMode()
	 */
	protected boolean needsLinkedMode() {
		return false; // we handle it ourselves
	}
	
	/*
	 * @see org.rubypeople.rdt.internal.ui.text.ruby.LazyRubyCompletionProposal#computeReplacementString()
	 */
	protected String computeReplacementString() {
		
		if (!hasBlockVars() && (!hasParameters() || !hasArgumentList()))
			return super.computeReplacementString();

		String[] parameterNames= fProposal.getParameterNames();
		String[] blockVars= fProposal.getBlockVars();		
		int parameterCount= parameterNames.length;
		int blockVarsCount= blockVars.length;
		
		fArgumentOffsets= new int[parameterCount + blockVarsCount];
		fArgumentLengths= new int[parameterCount + blockVarsCount];
		StringBuffer buffer= new StringBuffer(String.valueOf(fProposal.getName()));
		
//		FormatterPrefs prefs= getFormatterPrefs();
//		if (prefs.beforeOpeningParen)
//			buffer.append(SPACE);
		buffer.append(LPAREN);
		
		setCursorPosition(buffer.length());
		
//		if (prefs.afterOpeningParen)
//			buffer.append(SPACE);
		
		for (int i= 0; i != parameterCount; i++) {
			if (i != 0) {
//				if (prefs.beforeComma)
//					buffer.append(SPACE);
				buffer.append(COMMA);
//				if (prefs.afterComma)
					buffer.append(SPACE);
			}
			
			fArgumentOffsets[i]= buffer.length();
			buffer.append(parameterNames[i]);
			fArgumentLengths[i]= parameterNames[i].length();
		}
		
//		if (prefs.beforeClosingParen)
//			buffer.append(SPACE);

		buffer.append(RPAREN);

		if (blockVarsCount > 0) {
			buffer.append(SPACE);
			buffer.append("{");
			buffer.append("|");
			for (int x = 0; x < blockVars.length; x++) {
				if (x != 0) {
//				if (prefs.beforeComma)
//					buffer.append(SPACE);
					buffer.append(COMMA);
//				if (prefs.afterComma)
					buffer.append(SPACE);
				}
				fArgumentOffsets[parameterCount + x]= buffer.length();
				buffer.append(blockVars[x]);
				fArgumentLengths[parameterCount + x]= blockVars[x].length();
			}
			buffer.append("|");
			buffer.append(SPACE);
			buffer.append(SPACE);
			buffer.append("}");
		}		
		return buffer.toString();
	}

	private boolean hasBlockVars() {
		String[] vars = fProposal.getBlockVars();
		if (vars == null) return false;
		return vars.length > 0;
	}

	/**
	 * Returns the currently active ruby editor, or <code>null</code> if it
	 * cannot be determined.
	 *
	 * @return  the currently active ruby editor, or <code>null</code>
	 */
	private RubyEditor getRubyEditor() {
		IEditorPart part= RubyPlugin.getActivePage().getActiveEditor();
		if (part instanceof RubyEditor)
			return (RubyEditor) part;
		else
			return null;
	}

	/*
	 * @see ICompletionProposal#getSelection(IDocument)
	 */
	public Point getSelection(IDocument document) {
		if (fSelectedRegion == null)
			return new Point(getReplacementOffset(), 0);

		return new Point(fSelectedRegion.getOffset(), fSelectedRegion.getLength());
	}

	private void openErrorDialog(BadLocationException e) {
		Shell shell= getTextViewer().getTextWidget().getShell();
		MessageDialog.openError(shell, RubyTextMessages.ExperimentalProposal_error_msg, e.getMessage());
	}

}
