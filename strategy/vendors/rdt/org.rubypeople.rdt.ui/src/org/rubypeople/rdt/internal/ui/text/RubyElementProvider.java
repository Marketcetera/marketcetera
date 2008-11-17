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
package org.rubypeople.rdt.internal.ui.text;


import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.information.IInformationProvider;
import org.eclipse.jface.text.information.IInformationProviderExtension;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorPart;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.internal.ui.actions.SelectionConverter;
import org.rubypeople.rdt.internal.ui.rubyeditor.EditorUtility;
import org.rubypeople.rdt.internal.ui.rubyeditor.RubyEditor;

/**
 * Provides a Ruby element to be displayed in by an information presenter.
 */
public class RubyElementProvider implements IInformationProvider, IInformationProviderExtension {

	private RubyEditor fEditor;
	private boolean fUseCodeResolve;

	public RubyElementProvider(IEditorPart editor) {
		fUseCodeResolve= false;
		if (editor instanceof RubyEditor)
			fEditor= (RubyEditor)editor;
	}

	public RubyElementProvider(IEditorPart editor, boolean useCodeResolve) {
		this(editor);
		fUseCodeResolve= useCodeResolve;
	}

	/*
	 * @see IInformationProvider#getSubject(ITextViewer, int)
	 */
	public IRegion getSubject(ITextViewer textViewer, int offset) {
		if (textViewer != null && fEditor != null) {
			IRegion region= RubyWordFinder.findWord(textViewer.getDocument(), offset);
			if (region != null)
				return region;
			else
				return new Region(offset, 0);
		}
		return null;
	}

	/*
	 * @see IInformationProvider#getInformation(ITextViewer, IRegion)
	 */
	public String getInformation(ITextViewer textViewer, IRegion subject) {
		return getInformation2(textViewer, subject).toString();
	}

	/*
	 * @see IInformationProviderExtension#getElement(ITextViewer, IRegion)
	 */
	public Object getInformation2(ITextViewer textViewer, IRegion subject) {
		if (fEditor == null)
			return null;

		try {
			if (fUseCodeResolve) {
				IStructuredSelection sel= SelectionConverter.getStructuredSelection(fEditor);
				if (!sel.isEmpty())
					return sel.getFirstElement();
			}
			IRubyElement element= SelectionConverter.getElementAtOffset(fEditor);
			if (element != null)
				return element;
			
			return EditorUtility.getEditorInputRubyElement(fEditor, false);
		} catch (RubyModelException e) {
			return null;
		}
	}
}
