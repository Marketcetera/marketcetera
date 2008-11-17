/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.rubypeople.rdt.internal.corext.template.ruby;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.TextUtilities;
import org.eclipse.jface.text.templates.Template;
import org.eclipse.jface.text.templates.TemplateBuffer;
import org.eclipse.jface.text.templates.TemplateContextType;
import org.eclipse.jface.text.templates.TemplateException;
import org.eclipse.jface.text.templates.TemplateTranslator;
import org.eclipse.jface.text.templates.TemplateVariable;
import org.rubypeople.rdt.core.IRubyProject;
import org.rubypeople.rdt.core.IRubyScript;
import org.rubypeople.rdt.internal.corext.util.Strings;
import org.rubypeople.rdt.internal.ui.RubyPlugin;
import org.rubypeople.rdt.internal.ui.text.template.contentassist.MultiVariable;
import org.rubypeople.rdt.ui.PreferenceConstants;

public class RubyContext extends RubyScriptContext {

	/**
	 * Creates a ruby template context.
	 * 
	 * @param type
	 *            the context type.
	 * @param document
	 *            the document.
	 * @param completionOffset
	 *            the completion offset within the document.
	 * @param completionLength
	 *            the completion length.
	 * @param compilationUnit
	 *            the compilation unit (may be <code>null</code>).
	 */
	public RubyContext(TemplateContextType type, IDocument document,
			int completionOffset, int completionLength,
			IRubyScript compilationUnit) {
		super(type, document, completionOffset, completionLength,
				compilationUnit);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.templates.TemplateContext#evaluate(org.eclipse.jface.text.templates.Template)
	 */
	public TemplateBuffer evaluate(Template template)
			throws BadLocationException, TemplateException {
		if (!canEvaluate(template))
			throw new TemplateException(
					RubyTemplateMessages.Context_error_cannot_evaluate);

		TemplateTranslator translator = new TemplateTranslator() {
			/*
			 * @see org.eclipse.jface.text.templates.TemplateTranslator#createVariable(java.lang.String,
			 *      java.lang.String, int[])
			 */
			protected TemplateVariable createVariable(String type, String name,
					int[] offsets) {
				return new MultiVariable(type, name, offsets);
			}
		};
		TemplateBuffer buffer = translator.translate(template);

		getContextType().resolve(buffer, this);

		IPreferenceStore prefs = RubyPlugin.getDefault().getPreferenceStore();
		boolean useCodeFormatter = prefs
				.getBoolean(PreferenceConstants.TEMPLATES_USE_CODEFORMATTER);

		IRubyProject project = getRubyScript() != null ? getRubyScript()
				.getRubyProject() : null;
		RubyFormatter formatter = new RubyFormatter(TextUtilities
				.getDefaultLineDelimiter(getDocument()), getIndentation(),
				useCodeFormatter, project);
		formatter.format(buffer, this);

		return buffer;
	}

	/**
	 * Returns the indentation level at the position of code completion.
	 * 
	 * @return the indentation level at the position of the code completion
	 */
	private int getIndentation() {
		int start = getStart();
		IDocument document = getDocument();
		try {
			IRegion region = document.getLineInformationOfOffset(start);
			String lineContent = document.get(region.getOffset(), region
					.getLength());
			IRubyScript compilationUnit = getRubyScript();
			IRubyProject project = compilationUnit == null ? null
					: compilationUnit.getRubyProject();
			return Strings.computeIndentUnits(lineContent, project);
		} catch (BadLocationException e) {
			return 0;
		}
	}

	/*
	 * @see TemplateContext#canEvaluate(Template templates)
	 */
	public boolean canEvaluate(Template template) {
		if (fForceEvaluation)
			return true;

		String key = getKey();
		return template.matches(key, getContextType().getId())
				&& key.length() != 0
				&& template.getName().toLowerCase().startsWith(
						key.toLowerCase());
	}

	/*
	 * @see org.eclipse.jdt.internal.corext.template.DocumentTemplateContext#getKey()
	 */
	public String getKey() {

		if (getCompletionLength() == 0)
			return super.getKey();

		try {
			IDocument document = getDocument();

			int start = getStart();
			int end = getCompletionOffset();
			return start <= end ? document.get(start, end - start) : ""; //$NON-NLS-1$

		} catch (BadLocationException e) {
			return super.getKey();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.templates.DocumentTemplateContext#getEnd()
	 */
	public int getEnd() {

		if (getCompletionLength() == 0)
			return super.getEnd();

		try {
			IDocument document = getDocument();

			int start = getCompletionOffset();
			int end = getCompletionOffset() + getCompletionLength();

			while (start != end
					&& Character.isWhitespace(document.getChar(end - 1)))
				end--;

			return end;

		} catch (BadLocationException e) {
			return super.getEnd();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.templates.DocumentTemplateContext#getStart()
	 */
	public int getStart() {

		try {
			IDocument document = getDocument();

			int start = getCompletionOffset();
			int end = getCompletionOffset() + getCompletionLength();

			while (start != 0
					&& Character.isUnicodeIdentifierPart(document
							.getChar(start - 1)))
				start--;

			while (start != end
					&& Character.isWhitespace(document.getChar(start)))
				start++;

			if (start == end)
				start = getCompletionOffset();

			return start;

		} catch (BadLocationException e) {
			return super.getStart();
		}
	}
}
