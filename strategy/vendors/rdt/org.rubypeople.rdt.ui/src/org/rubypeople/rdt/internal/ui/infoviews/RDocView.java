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
package org.rubypeople.rdt.internal.ui.infoviews;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.BadPartitioningException;
import org.eclipse.jface.text.DefaultInformationControl;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentExtension3;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.TextPresentation;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.text.TextUtilities;
import org.eclipse.jface.util.Assert;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.IAbstractTextEditorHelpContextIds;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;
import org.osgi.framework.Bundle;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IRubyScript;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.internal.corext.util.RDocUtil;
import org.rubypeople.rdt.internal.ui.IRubyHelpContextIds;
import org.rubypeople.rdt.internal.ui.RubyPlugin;
import org.rubypeople.rdt.internal.ui.rubyeditor.RubyEditor;
import org.rubypeople.rdt.internal.ui.text.HTMLPrinter;
import org.rubypeople.rdt.internal.ui.text.HTMLTextPresenter;
import org.rubypeople.rdt.internal.ui.text.IRubyPartitions;

/**
 * View which shows Rubydoc for a given Ruby element.
 *
 * FIXME: As of 3.0 selectAll() and getSelection() is not working
 *			see https://bugs.eclipse.org/bugs/show_bug.cgi?id=63022
 *
 * @since 3.0
 */
public class RDocView extends AbstractInfoView {

	/**
	 * Preference key for the preference whether to show a dialog
	 * when the SWT Browser widget is not available.
	 * @since 3.0
	 */
	private static final String DO_NOT_WARN_PREFERENCE_KEY= "RubydocView.error.doNotWarn"; //$NON-NLS-1$
	
	// see https://bugs.eclipse.org/bugs/show_bug.cgi?id=73558
	private static final boolean WARNING_DIALOG_ENABLED= false;

	/** The HTML widget. */
	private Browser fBrowser;
	/** The text widget. */
	private StyledText fText;
	/** The information presenter. */
	private DefaultInformationControl.IInformationPresenter fPresenter;
	/** The text presentation. */
	private TextPresentation fPresentation= new TextPresentation();
	/** The select all action */
	private SelectAllAction fSelectAllAction;
	/** The style sheet (css) */
	private static String fgStyleSheet;

	/** The Browser widget */
	private boolean fIsUsingBrowserWidget;

	private RGB fBackgroundColorRGB;

	
	/**
	 * The Rubydoc view's select all action.
	 */
	private class SelectAllAction extends Action {

		/** The control. */
		private Control fControl;
		/** The selection provider. */
		private SelectionProvider fSelectionProvider;

		/**
		 * Creates the action.
		 *
		 * @param control the widget
		 * @param selectionProvider the selection provider
		 */
		public SelectAllAction(Control control, SelectionProvider selectionProvider) {
			super("selectAll"); //$NON-NLS-1$

			Assert.isNotNull(control);
			Assert.isNotNull(selectionProvider);
			fControl= control;
			fSelectionProvider= selectionProvider;

			// FIXME: see https://bugs.eclipse.org/bugs/show_bug.cgi?id=63022
			setEnabled(!fIsUsingBrowserWidget);

			setText(InfoViewMessages.SelectAllAction_label);
			setToolTipText(InfoViewMessages.SelectAllAction_tooltip);
			setDescription(InfoViewMessages.SelectAllAction_description);

			PlatformUI.getWorkbench().getHelpSystem().setHelp(this, IAbstractTextEditorHelpContextIds.SELECT_ALL_ACTION);
		}

		/**
		 * Selects all in the view.
		 */
		public void run() {
			if (fControl instanceof StyledText)
		        ((StyledText)fControl).selectAll();
			else {
				// FIXME: see https://bugs.eclipse.org/bugs/show_bug.cgi?id=63022
//				((Browser)fControl).selectAll();
				if (fSelectionProvider != null)
					fSelectionProvider.fireSelectionChanged();
			}
		}
	}

	/**
	 * The Rubydoc view's selection provider.
	 */
	private static class SelectionProvider implements ISelectionProvider {

		/** The selection changed listeners. */
		private ListenerList fListeners= new ListenerList(ListenerList.IDENTITY);
		/** The widget. */
		private Control fControl;

		/**
		 * Creates a new selection provider.
		 *
		 * @param control	the widget
		 */
		public SelectionProvider(Control control) {
		    Assert.isNotNull(control);
			fControl= control;
			if (fControl instanceof StyledText) {
			    ((StyledText)fControl).addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(SelectionEvent e) {
					    fireSelectionChanged();
					}
			    });
			} else {
				// FIXME: see https://bugs.eclipse.org/bugs/show_bug.cgi?id=63022
//				((Browser)fControl).addSelectionListener(new SelectionAdapter() {
//					public void widgetSelected(SelectionEvent e) {
//						fireSelectionChanged();
//					}
//				});
			}
		}

		/**
		 * Sends a selection changed event to all listeners.
		 */
		public void fireSelectionChanged() {
			ISelection selection= getSelection();
			SelectionChangedEvent event= new SelectionChangedEvent(this, selection);
			Object[] selectionChangedListeners= fListeners.getListeners();
			for (int i= 0; i < selectionChangedListeners.length; i++)
				((ISelectionChangedListener)selectionChangedListeners[i]).selectionChanged(event);
		}

		/*
		 * @see org.eclipse.jface.viewers.ISelectionProvider#addSelectionChangedListener(org.eclipse.jface.viewers.ISelectionChangedListener)
		 */
		public void addSelectionChangedListener(ISelectionChangedListener listener) {
			fListeners.add(listener);
		}

		/*
		 * @see org.eclipse.jface.viewers.ISelectionProvider#getSelection()
		 */
		public ISelection getSelection() {
			if (fControl instanceof StyledText) {
				IDocument document= new Document(((StyledText)fControl).getSelectionText());
				return new TextSelection(document, 0, document.getLength());
			} else {
				// FIXME: see https://bugs.eclipse.org/bugs/show_bug.cgi?id=63022
				return StructuredSelection.EMPTY;
			}
		}

		/*
		 * @see org.eclipse.jface.viewers.ISelectionProvider#removeSelectionChangedListener(org.eclipse.jface.viewers.ISelectionChangedListener)
		 */
		public void removeSelectionChangedListener(ISelectionChangedListener listener) {
			fListeners.remove(listener);
		}

		/*
		 * @see org.eclipse.jface.viewers.ISelectionProvider#setSelection(org.eclipse.jface.viewers.ISelection)
		 */
		public void setSelection(ISelection selection) {
			// not supported
		}
	}

	/*
	 * @see AbstractInfoView#internalCreatePartControl(Composite)
	 */
	protected void internalCreatePartControl(Composite parent) {
		try {
			fBrowser= new Browser(parent, SWT.NONE);
			fIsUsingBrowserWidget= true;
			
		} catch (SWTError er) {

			/* The Browser widget throws an SWTError if it fails to
			 * instantiate properly. Application code should catch
			 * this SWTError and disable any feature requiring the
			 * Browser widget.
			 * Platform requirements for the SWT Browser widget are available
			 * from the SWT FAQ web site.
			 */

			IPreferenceStore store= RubyPlugin.getDefault().getPreferenceStore();
			boolean doNotWarn= store.getBoolean(DO_NOT_WARN_PREFERENCE_KEY);
			if (WARNING_DIALOG_ENABLED && !doNotWarn) {
				String title= InfoViewMessages.RubydocView_error_noBrowser_title;
				String message= InfoViewMessages.RubydocView_error_noBrowser_message;
				String toggleMessage= InfoViewMessages.RubydocView_error_noBrowser_doNotWarn;
				MessageDialogWithToggle dialog= MessageDialogWithToggle.openError(parent.getShell(), title, message, toggleMessage, false, null, null); 
				if (dialog.getReturnCode() == Window.OK)
					store.setValue(DO_NOT_WARN_PREFERENCE_KEY, dialog.getToggleState());
			}

			fIsUsingBrowserWidget= false;
		}

		if (!fIsUsingBrowserWidget) {
			fText= new StyledText(parent, SWT.V_SCROLL | SWT.H_SCROLL);
			fText.setEditable(false);
			fPresenter= new HTMLTextPresenter(false);

			fText.addControlListener(new ControlAdapter() {
				/*
				 * @see org.eclipse.swt.events.ControlAdapter#controlResized(org.eclipse.swt.events.ControlEvent)
				 */
				public void controlResized(ControlEvent e) {
					setInput(fText.getText());
				}
			});
		}

		initStyleSheet();
		getViewSite().setSelectionProvider(new SelectionProvider(getControl()));
	}

	private static void initStyleSheet() {
		Bundle bundle= Platform.getBundle(RubyPlugin.getPluginId());
		URL styleSheetURL= bundle.getEntry("/RDocViewStyleSheet.css"); //$NON-NLS-1$
		if (styleSheetURL == null)
			return;
		
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
		}
		
	}

	/*
	 * @see AbstractInfoView#createActions()
	 */
	protected void createActions() {
		super.createActions();
		fSelectAllAction= new SelectAllAction(getControl(), (SelectionProvider)getSelectionProvider());
	}


	/*
	 * @see org.eclipse.jdt.internal.ui.infoviews.AbstractInfoView#getSelectAllAction()
	 * @since 3.0
	 */
	protected IAction getSelectAllAction() {
		// FIXME: see https://bugs.eclipse.org/bugs/show_bug.cgi?id=63022
		if (fIsUsingBrowserWidget)
			return null;

		return fSelectAllAction;
	}

	/*
	 * @see org.eclipse.jdt.internal.ui.infoviews.AbstractInfoView#getCopyToClipboardAction()
	 * @since 3.0
	 */
//	protected IAction getCopyToClipboardAction() {
//		// FIXME: see https://bugs.eclipse.org/bugs/show_bug.cgi?id=63022
//		if (fIsUsingBrowserWidget)
//			return null;
//
//		return super.getCopyToClipboardAction();
//	}

	/*
 	 * @see AbstractInfoView#setForeground(Color)
 	 */
	protected void setForeground(Color color) {
		getControl().setForeground(color);
	}

	/*
	 * @see AbstractInfoView#setBackground(Color)
	 */
	protected void setBackground(Color color) {
		getControl().setBackground(color);

		// Apply style sheet
		fBackgroundColorRGB= color.getRGB();
		if (getInput() == null) {
			StringBuffer buffer= new StringBuffer(""); //$NON-NLS-1$
			HTMLPrinter.insertPageProlog(buffer, 0, fBackgroundColorRGB, fgStyleSheet);
			setInput(buffer.toString());
		} else {
			setInput(computeInput(getInput()));
		}
	}
	
	/*
	 * @see org.eclipse.jdt.internal.ui.infoviews.AbstractInfoView#getBackgroundColorKey()
	 * @since 3.2
	 */
	protected String getBackgroundColorKey() {
		return "org.eclipse.jdt.ui.RubydocView.backgroundColor";		 //$NON-NLS-1$
	}

	/*
	 * @see AbstractInfoView#internalDispose()
	 */
	protected void internalDispose() {
		fText= null;
		fBrowser= null;
	}

	/*
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	public void setFocus() {
		getControl().setFocus();
	}

	/*
	 * @see AbstractInfoView#computeInput(Object)
	 */
	protected Object computeInput(Object input) {
		if (getControl() == null || ! (input instanceof IRubyElement))
			return null;

		IRubyElement je= (IRubyElement)input;
		String javadocHtml;

		switch (je.getElementType()) {
			case IRubyElement.SCRIPT:
				try {
					javadocHtml= getRubydocHtml(((IRubyScript)je).getTypes());
				} catch (RubyModelException ex) {
					javadocHtml= null;
				}
				break;
			default:
				javadocHtml= getRubydocHtml(new IRubyElement[] { je });
		}
		
		if (javadocHtml == null)
			return ""; //$NON-NLS-1$
		
		return javadocHtml;
	}

	/*
	 * @see AbstractInfoView#setInput(Object)
	 */
	protected void setInput(Object input) {
		String javadocHtml= (String)input;

		if (fIsUsingBrowserWidget) {
			if (javadocHtml != null && javadocHtml.length() > 0) {
				boolean RTL= (getSite().getShell().getStyle() & SWT.RIGHT_TO_LEFT) != 0;
				if (RTL) {
					StringBuffer buffer= new StringBuffer(javadocHtml);
					HTMLPrinter.insertStyles(buffer, new String[] { "direction:rtl" } ); //$NON-NLS-1$
					javadocHtml= buffer.toString();
				}
			}
			fBrowser.setText(javadocHtml);
		} else {
			fPresentation.clear();
			Rectangle size=  fText.getClientArea();

			try {
				javadocHtml= ((DefaultInformationControl.IInformationPresenterExtension)fPresenter).updatePresentation(getSite().getShell(), javadocHtml, fPresentation, size.width, size.height);
			} catch (IllegalArgumentException ex) {
				// the javadoc might no longer be valid
				return;
			}
			fText.setText(javadocHtml);
			TextPresentation.applyTextPresentation(fPresentation, fText);
		}
	}
	
	/**
	 * Returns the RDoc in HTML format.
	 *
	 * @param result the Ruby elements for which to get the RDoc
	 * @return a string with the RDoc in HTML format.
	 */
	private String getRubydocHtml(IRubyElement[] result) {		
		StringBuffer buffer= new StringBuffer();
		String contents = RDocUtil.getHTMLDocumentation(result);
		if (contents != null) {
			buffer.append(contents);
		} else {
			HTMLPrinter.addParagraph(buffer, InfoViewMessages.RubydocView_noAttachedInformation);
		}
		if (buffer.length() > 0) {
			HTMLPrinter.insertPageProlog(buffer, 0, fBackgroundColorRGB, fgStyleSheet);
			HTMLPrinter.addPageEpilog(buffer);
			return buffer.toString();
		}

		return null;
	}

	/*
	 * @see org.eclipse.jdt.internal.ui.infoviews.AbstractInfoView#isIgnoringNewInput(org.eclipse.jdt.core.IRubyElement, org.eclipse.jface.viewers.ISelection)
	 * @since 3.2
	 */
	protected boolean isIgnoringNewInput(IRubyElement je, IWorkbenchPart part, ISelection selection) {
		if (super.isIgnoringNewInput(je, part, selection)
				&& part instanceof ITextEditor
				&& selection instanceof ITextSelection) {
			
			ITextEditor editor= (ITextEditor)part;
			IDocumentProvider docProvider= editor.getDocumentProvider();
			if (docProvider == null)
				return false;
			
			IDocument document= docProvider.getDocument(editor.getEditorInput());
			if (!(document instanceof IDocumentExtension3))
				return false;
			
			try {
				int offset= ((ITextSelection)selection).getOffset();
				String partition= ((IDocumentExtension3)document).getContentType(IRubyPartitions.RUBY_PARTITIONING, offset, false);
				return  partition != IRubyPartitions.RUBY_SINGLE_LINE_COMMENT && partition != IRubyPartitions.RUBY_MULTI_LINE_COMMENT;
			} catch (BadPartitioningException ex) {
				return false;
			} catch (BadLocationException ex) {
				return false;
			}

		}
		return false;
	}

	/*
	 * @see AbstractInfoView#findSelectedRubyElement(IWorkbenchPart)
	 */
	protected IRubyElement findSelectedRubyElement(IWorkbenchPart part, ISelection selection) {
		IRubyElement element;
		try {
			element= super.findSelectedRubyElement(part, selection);

			if (element == null && part instanceof RubyEditor && selection instanceof ITextSelection) {

				RubyEditor editor= (RubyEditor)part;
				ITextSelection textSelection= (ITextSelection)selection;

				IDocumentProvider documentProvider= editor.getDocumentProvider();
				if (documentProvider == null)
					return null;

				IDocument document= documentProvider.getDocument(editor.getEditorInput());
				if (document == null)
					return null;

				ITypedRegion typedRegion= TextUtilities.getPartition(document, IRubyPartitions.RUBY_PARTITIONING, textSelection.getOffset(), false);
				if (IRubyPartitions.RUBY_MULTI_LINE_COMMENT.equals(typedRegion.getType()) || IRubyPartitions.RUBY_SINGLE_LINE_COMMENT.equals(typedRegion.getType()))
					return TextSelectionConverter.getElementAtOffset((RubyEditor)part, textSelection);
				else
					return null;
			} else
				return element;
		} catch (RubyModelException e) {
			return null;
		} catch (BadLocationException e) {
			return null;
		}
	}

	/*
	 * @see AbstractInfoView#getControl()
	 */
	protected Control getControl() {
		if (fIsUsingBrowserWidget)
			return fBrowser;
		else
			return fText;
	}

	/*
	 * @see org.eclipse.jdt.internal.ui.infoviews.AbstractInfoView#getHelpContextId()
	 * @since 3.1
	 */
	protected String getHelpContextId() {
		return IRubyHelpContextIds.RDOC_VIEW;
	}
}
