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

package org.rubypeople.rdt.internal.ui.text.ruby.hover;

import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.ITextHoverExtension;
import org.eclipse.jface.text.information.IInformationProviderExtension2;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.part.IWorkbenchPartOrientation;
import org.rubypeople.rdt.core.IMember;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.ISourceReference;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.internal.corext.codemanipulation.StubUtility;
import org.rubypeople.rdt.internal.corext.util.Strings;

/**
 * Provides source as hover info for Ruby elements.
 */
public class RubySourceHover extends AbstractRubyEditorTextHover implements ITextHoverExtension, IInformationProviderExtension2 {

	/*
	 * @see RubyElementHover
	 */
	protected String getHoverInfo(IRubyElement[] result) {
		int nResults= result.length;

		if (nResults > 1)
			return null;

		IRubyElement curr= result[0];
		if ((curr instanceof IMember) && curr instanceof ISourceReference) {
			try {
				String source= ((ISourceReference) curr).getSource();
				if (source == null)
					return null;

				source= removeLeadingComments(source);
				String delim= StubUtility.getLineDelimiterUsed(result[0]);

				String[] sourceLines= Strings.convertIntoLines(source);
				String firstLine= sourceLines[0];
				if (!Character.isWhitespace(firstLine.charAt(0)))
					sourceLines[0]= ""; //$NON-NLS-1$
				Strings.trimIndentation(sourceLines, curr.getRubyProject());

				if (!Character.isWhitespace(firstLine.charAt(0)))
					sourceLines[0]= firstLine;

				source= Strings.concatenate(sourceLines, delim);

				return source;

			} catch (RubyModelException ex) {
			}
		}

		return null;
	}

	private String removeLeadingComments(String source) {
		// FIXME Actually strip out the leading comments!
		return source;
	}

	/*
	 * @see org.eclipse.jface.text.ITextHoverExtension#getHoverControlCreator()
	 * @since 3.0
	 */
	public IInformationControlCreator getHoverControlCreator() {
		return new IInformationControlCreator() {
			public IInformationControl createInformationControl(Shell parent) {
				IEditorPart editor= getEditor(); 
				int shellStyle= SWT.TOOL | SWT.NO_TRIM;
				if (editor instanceof IWorkbenchPartOrientation)
					shellStyle |= ((IWorkbenchPartOrientation)editor).getOrientation();
				return new SourceViewerInformationControl(parent, shellStyle, SWT.NONE, getTooltipAffordanceString());
			}
		};
	}

	/*
	 * @see IInformationProviderExtension2#getInformationPresenterControlCreator()
	 * @since 3.0
	 */
	public IInformationControlCreator getInformationPresenterControlCreator() {
		return new IInformationControlCreator() {
			public IInformationControl createInformationControl(Shell parent) {
				int style= SWT.V_SCROLL | SWT.H_SCROLL;
				int shellStyle= SWT.RESIZE | SWT.TOOL;
				IEditorPart editor= getEditor(); 
				if (editor instanceof IWorkbenchPartOrientation)
					shellStyle |= ((IWorkbenchPartOrientation)editor).getOrientation();
				return new SourceViewerInformationControl(parent, shellStyle, style);
			}
		};
	}
}
