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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.BadPartitioningException;
import org.eclipse.jface.text.DefaultInformationControl;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentExtension3;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextHoverExtension;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.keys.IBindingService;
import org.osgi.framework.Bundle;
import org.rubypeople.rdt.core.ICodeAssist;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.internal.core.util.Messages;
import org.rubypeople.rdt.internal.ui.RubyPlugin;
import org.rubypeople.rdt.internal.ui.rubyeditor.RubyScriptEditorInput;
import org.rubypeople.rdt.internal.ui.rubyeditor.WorkingCopyManager;
import org.rubypeople.rdt.internal.ui.text.HTMLTextPresenter;
import org.rubypeople.rdt.internal.ui.text.IRubyPartitions;
import org.rubypeople.rdt.internal.ui.text.RubyWordFinder;
import org.rubypeople.rdt.ui.PreferenceConstants;
import org.rubypeople.rdt.ui.actions.IRubyEditorActionDefinitionIds;
import org.rubypeople.rdt.ui.text.ruby.hover.IRubyEditorTextHover;

/**
 * Abstract class for providing hover information for Ruby elements.
 *
 * @since 1.0
 */
public abstract class AbstractRubyEditorTextHover implements IRubyEditorTextHover, ITextHoverExtension {

	/**
	 * The style sheet (css).
	 * @since 1.0
	 */
	private static String fgStyleSheet;
	private IEditorPart fEditor;
	private IBindingService fBindingService;
	{
		fBindingService= (IBindingService)PlatformUI.getWorkbench().getAdapter(IBindingService.class);
	}

	/*
	 * @see IRubyEditorTextHover#setEditor(IEditorPart)
	 */
	public void setEditor(IEditorPart editor) {
		fEditor= editor;
	}

	protected IEditorPart getEditor() {
		return fEditor;
	}

	protected ICodeAssist getCodeAssist() {
		if (fEditor != null) {
			IEditorInput input= fEditor.getEditorInput();
			if (input instanceof RubyScriptEditorInput) {
				RubyScriptEditorInput cfeInput= (RubyScriptEditorInput) input;
				return cfeInput.getRubyScript();
			}

			WorkingCopyManager manager= RubyPlugin.getDefault().getWorkingCopyManager();
			return manager.getWorkingCopy(input, false);
		}

		return null;
	}

	/*
	 * @see ITextHover#getHoverRegion(ITextViewer, int)
	 */
	public IRegion getHoverRegion(ITextViewer textViewer, int offset) {
		return RubyWordFinder.findWord(textViewer.getDocument(), offset);
	}

	/*
	 * @see ITextHover#getHoverInfo(ITextViewer, IRegion)
	 */
	public String getHoverInfo(ITextViewer textViewer, IRegion hoverRegion) {
		
		/*
		 * The region should be a word region an not of length 0.
		 * This check is needed because codeSelect(...) also finds
		 * the Ruby element if the offset is behind the word.
		 */
		if (hoverRegion.getLength() == 0)
			return null;
		
		try {
			IDocument doc = textViewer.getDocument();		
			if (doc == null) return null;
			String contentType = null;
			if (doc instanceof IDocumentExtension3) {
				IDocumentExtension3 extension = (IDocumentExtension3) doc;		
				try {
					contentType = extension.getContentType(IRubyPartitions.RUBY_PARTITIONING, hoverRegion.getOffset(), false);
				} catch (BadPartitioningException e) {
					// ignore
				}
			}
			if (contentType != null && !contentType.equals(IRubyPartitions.RUBY_DEFAULT)) { // If we're not in code, don't bother
				return null;
			}
		} catch (BadLocationException e) {
			// ignore
		}			
		
		ICodeAssist resolve= getCodeAssist();
		if (resolve != null) {
			try {
				IRubyElement[] result= resolve.codeSelect(hoverRegion.getOffset(), hoverRegion.getLength());
				if (result == null)
					return null;

				int nResults= result.length;
				if (nResults == 0)
					return null;

				return getHoverInfo(result);

			} catch (RubyModelException x) {
				return null;
			}
		}
		return null;
	}

	/**
	 * Provides hover information for the given Ruby elements.
	 *
	 * @param rubyElements the Ruby elements for which to provide hover information
	 * @return the hover information string
	 * @since 1.0
	 */
	protected String getHoverInfo(IRubyElement[] rubyElements) {
		return null;
	}

	/*
	 * @see ITextHoverExtension#getHoverControlCreator()
	 * @since 1.0
	 */
	public IInformationControlCreator getHoverControlCreator() {
		return new IInformationControlCreator() {
			public IInformationControl createInformationControl(Shell parent) {
				return new DefaultInformationControl(parent, SWT.NONE, new HTMLTextPresenter(true), getTooltipAffordanceString());
			}
		};
	}

	/**
	 * Returns the tool tip affordance string.
	 *
	 * @return the affordance string or <code>null</code> if disabled or no key binding is defined
	 * @since 1.0
	 */
	protected String getTooltipAffordanceString() {
		if (fBindingService == null || !RubyPlugin.getDefault().getPreferenceStore().getBoolean(PreferenceConstants.EDITOR_SHOW_TEXT_HOVER_AFFORDANCE))
			return null;

		String keySequence= fBindingService.getBestActiveBindingFormattedFor(IRubyEditorActionDefinitionIds.SHOW_RDOC);
		if (keySequence == null)
			return null;
		
		return Messages.format(RubyHoverMessages.RubyTextHover_makeStickyHint, keySequence == null ? "" : keySequence); //$NON-NLS-1$
	}

	/**
	 * Returns the style sheet.
	 *
	 * @since 1.0
	 */
	protected static String getStyleSheet() {
		if (fgStyleSheet == null) {
			Bundle bundle= Platform.getBundle(RubyPlugin.getPluginId());
			URL styleSheetURL= bundle.getEntry("/RubydocHoverStyleSheet.css"); //$NON-NLS-1$
			if (styleSheetURL != null) {
				try {
					styleSheetURL= FileLocator.toFileURL(styleSheetURL);
					BufferedReader reader= new BufferedReader(new InputStreamReader(styleSheetURL.openStream()));
					StringBuffer buffer= new StringBuffer(200);
					String line= reader.readLine();
					while (line != null) {
						buffer.append(line);
						buffer.append('\n');
						line= reader.readLine();
					}
					fgStyleSheet= buffer.toString();
				} catch (IOException ex) {
					RubyPlugin.log(ex);
					fgStyleSheet= ""; //$NON-NLS-1$
				}
			}
		}
		return fgStyleSheet;
	}
	
}
