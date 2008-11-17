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
package org.rubypeople.rdt.internal.ui.text.ruby.hover;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlExtension;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.rubypeople.rdt.internal.ui.RubyPlugin;
import org.rubypeople.rdt.internal.ui.rubyeditor.RubySourceViewer;
import org.rubypeople.rdt.internal.ui.text.IRubyPartitions;
import org.rubypeople.rdt.internal.ui.text.SimpleRubySourceViewerConfiguration;

/**
 * Source viewer based implementation of <code>IInformationControl</code>.
 * Displays information in a source viewer.
 *
 * @since 3.0
 */
public class SourceViewerInformationControl implements IInformationControl, IInformationControlExtension, DisposeListener {

	/** Border thickness in pixels. */
	private static final int BORDER= 1;
	/** The control's shell */
	private Shell fShell;
	/** The control's text widget */
	private StyledText fText;
	/** The control's source viewer */
	private SourceViewer fViewer;
	/**
	 * The optional status field.
	 *
	 * @since 3.0
	 */
	private Label fStatusField;
	/**
	 * The separator for the optional status field.
	 *
	 * @since 3.0
	 */
	private Label fSeparator;
	/**
	 * The font of the optional status text label.
	 *
	 * @since 3.0
	 */
	private Font fStatusTextFont;

	/**
	 * Creates a default information control with the given shell as parent. The given
	 * information presenter is used to process the information to be displayed. The given
	 * styles are applied to the created styled text widget.
	 *
	 * @param parent the parent shell
	 * @param shellStyle the additional styles for the shell
	 * @param style the additional styles for the styled text widget
	 */
	public SourceViewerInformationControl(Shell parent, int shellStyle, int style) {
		this(parent, shellStyle, style, null);
	}

	/**
	 * Creates a default information control with the given shell as parent. The given
	 * information presenter is used to process the information to be displayed. The given
	 * styles are applied to the created styled text widget.
	 *
	 * @param parent the parent shell
	 * @param shellStyle the additional styles for the shell
	 * @param style the additional styles for the styled text widget
	 * @param statusFieldText the text to be used in the optional status field
	 *                         or <code>null</code> if the status field should be hidden
	 * @since 3.0
	 */
	public SourceViewerInformationControl(Shell parent, int shellStyle, int style, String statusFieldText) {
		GridLayout layout;
		GridData gd;

		fShell= new Shell(parent, SWT.NO_FOCUS | SWT.ON_TOP | shellStyle);
		Display display= fShell.getDisplay();
		fShell.setBackground(display.getSystemColor(SWT.COLOR_BLACK));

		Composite composite= fShell;
		layout= new GridLayout(1, false);
		int border= ((shellStyle & SWT.NO_TRIM) == 0) ? 0 : BORDER;
		layout.marginHeight= border;
		layout.marginWidth= border;
		composite.setLayout(layout);
		gd= new GridData(GridData.FILL_HORIZONTAL);
		composite.setLayoutData(gd);

		if (statusFieldText != null) {
			composite= new Composite(composite, SWT.NONE);
			layout= new GridLayout(1, false);
			layout.marginHeight= 0;
			layout.marginWidth= 0;
			composite.setLayout(layout);
			gd= new GridData(GridData.FILL_BOTH);
			composite.setLayoutData(gd);
			composite.setForeground(display.getSystemColor(SWT.COLOR_INFO_FOREGROUND));
			composite.setBackground(display.getSystemColor(SWT.COLOR_INFO_BACKGROUND));
		}

		// Source viewer
		IPreferenceStore store= RubyPlugin.getDefault().getCombinedPreferenceStore();
		fViewer= new RubySourceViewer(composite, null, null, false, style, store);
		fViewer.configure(new SimpleRubySourceViewerConfiguration(RubyPlugin.getDefault().getRubyTextTools().getColorManager(), store, null, IRubyPartitions.RUBY_PARTITIONING, false));
		fViewer.setEditable(false);

		fText= fViewer.getTextWidget();
		gd= new GridData(GridData.BEGINNING | GridData.FILL_BOTH);
		fText.setLayoutData(gd);
		fText.setForeground(parent.getDisplay().getSystemColor(SWT.COLOR_INFO_FOREGROUND));
		fText.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_INFO_BACKGROUND));

		initializeFont();
		
		fText.addKeyListener(new KeyListener() {

			public void keyPressed(KeyEvent e)  {
				if (e.character == 0x1B) // ESC
					fShell.dispose();
			}

			public void keyReleased(KeyEvent e) {}
		});

		// Status field
		if (statusFieldText != null) {

			// Horizontal separator line
			fSeparator= new Label(composite, SWT.SEPARATOR | SWT.HORIZONTAL | SWT.LINE_DOT);
			fSeparator.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

			// Status field label
			fStatusField= new Label(composite, SWT.RIGHT);
			fStatusField.setText(statusFieldText);
			Font font= fStatusField.getFont();
			FontData[] fontDatas= font.getFontData();
			for (int i= 0; i < fontDatas.length; i++)
				fontDatas[i].setHeight(fontDatas[i].getHeight() * 9 / 10);
			fStatusTextFont= new Font(fStatusField.getDisplay(), fontDatas);
			fStatusField.setFont(fStatusTextFont);
			GridData gd2= new GridData(GridData.FILL_VERTICAL | GridData.FILL_HORIZONTAL | GridData.HORIZONTAL_ALIGN_BEGINNING | GridData.VERTICAL_ALIGN_BEGINNING);
			fStatusField.setLayoutData(gd2);

			// Regarding the color see bug 41128
			fStatusField.setForeground(display.getSystemColor(SWT.COLOR_WIDGET_DARK_SHADOW));

			fStatusField.setBackground(display.getSystemColor(SWT.COLOR_INFO_BACKGROUND));
		}

		addDisposeListener(this);
	}

	/**
	 * Creates a default information control with the given shell as parent. The given
	 * information presenter is used to process the information to be displayed. The given
	 * styles are applied to the created styled text widget.
	 *
	 * @param parent the parent shell
	 * @param style the additional styles for the styled text widget
	 */
	public SourceViewerInformationControl(Shell parent,int style) {
		this(parent, SWT.NO_TRIM | SWT.TOOL, style);
	}

	/**
	 * Creates a default information control with the given shell as parent. The given
	 * information presenter is used to process the information to be displayed. The given
	 * styles are applied to the created styled text widget.
	 *
	 * @param parent the parent shell
	 * @param style the additional styles for the styled text widget
	 * @param statusFieldText the text to be used in the optional status field
	 *                         or <code>null</code> if the status field should be hidden
	 * @since 3.0
	 */
	public SourceViewerInformationControl(Shell parent,int style, String statusFieldText) {
		this(parent, SWT.NO_TRIM | SWT.TOOL, style, statusFieldText);
	}

	/**
	 * Creates a default information control with the given shell as parent.
	 * No information presenter is used to process the information
	 * to be displayed. No additional styles are applied to the styled text widget.
	 *
	 * @param parent the parent shell
	 */
	public SourceViewerInformationControl(Shell parent) {
		this(parent, SWT.NONE);
	}

	/**
	 * Creates a default information control with the given shell as parent.
	 * No information presenter is used to process the information
	 * to be displayed. No additional styles are applied to the styled text widget.
	 *
	 * @param parent the parent shell
	 * @param statusFieldText the text to be used in the optional status field
	 *                         or <code>null</code> if the status field should be hidden
	 * @since 3.0
	 */
	public SourceViewerInformationControl(Shell parent, String statusFieldText) {
		this(parent, SWT.NONE, statusFieldText);
	}
	
	/**
	 * Initialize the font to the Ruby editor font.
	 * 
	 * @since 1.0
	 */
	private void initializeFont() {
		Font font= JFaceResources.getFont("org.rubypeople.rdt.ui.editors.textfont"); //$NON-NLS-1$
		StyledText styledText= getViewer().getTextWidget();
		styledText.setFont(font);
	}

	/*
	 * @see org.eclipse.jface.text.IInformationControlExtension2#setInput(java.lang.Object)
	 */
	public void setInput(Object input) {
		if (input instanceof String)
			setInformation((String)input);
		else
			setInformation(null);
	}

	/*
	 * @see IInformationControl#setInformation(String)
	 */
	public void setInformation(String content) {
		if (content == null) {
			fViewer.setInput(null);
			return;
		}

		IDocument doc= new Document(content);
		RubyPlugin.getDefault().getRubyTextTools().setupRubyDocumentPartitioner(doc, IRubyPartitions.RUBY_PARTITIONING);
		fViewer.setInput(doc);
	}

	/*
	 * @see IInformationControl#setVisible(boolean)
	 */
	public void setVisible(boolean visible) {
			fShell.setVisible(visible);
	}

	/**
	 * {@inheritDoc}
	 * @since 3.0
	 */
	public void widgetDisposed(DisposeEvent event) {
		if (fStatusTextFont != null && !fStatusTextFont.isDisposed())
			fStatusTextFont.dispose();

		fStatusTextFont= null;
		fShell= null;
		fText= null;
	}

	/**
	 * {@inheritDoc}
	 */
	public final void dispose() {
		if (fShell != null && !fShell.isDisposed())
			fShell.dispose();
		else
			widgetDisposed(null);
	}

	/*
	 * @see IInformationControl#setSize(int, int)
	 */
	public void setSize(int width, int height) {

		if (fStatusField != null) {
			GridData gd= (GridData)fViewer.getTextWidget().getLayoutData();
			Point statusSize= fStatusField.computeSize(SWT.DEFAULT, SWT.DEFAULT, true);
			Point separatorSize= fSeparator.computeSize(SWT.DEFAULT, SWT.DEFAULT, true);
			gd.heightHint= height - statusSize.y - separatorSize.y;
		}
		fShell.setSize(width, height);

		if (fStatusField != null)
			fShell.pack(true);
	}

	/*
	 * @see IInformationControl#setLocation(Point)
	 */
	public void setLocation(Point location) {
		Rectangle trim= fShell.computeTrim(0, 0, 0, 0);
		Point textLocation= fText.getLocation();
		location.x += trim.x - textLocation.x;
		location.y += trim.y - textLocation.y;
		fShell.setLocation(location);
	}

	/*
	 * @see IInformationControl#setSizeConstraints(int, int)
	 */
	public void setSizeConstraints(int maxWidth, int maxHeight) {
		maxWidth= maxHeight;
	}

	/*
	 * @see IInformationControl#computeSizeHint()
	 */
	public Point computeSizeHint() {
		return fShell.computeSize(SWT.DEFAULT, SWT.DEFAULT);
	}

	/*
	 * @see IInformationControl#addDisposeListener(DisposeListener)
	 */
	public void addDisposeListener(DisposeListener listener) {
		fShell.addDisposeListener(listener);
	}

	/*
	 * @see IInformationControl#removeDisposeListener(DisposeListener)
	 */
	public void removeDisposeListener(DisposeListener listener) {
		fShell.removeDisposeListener(listener);
	}

	/*
	 * @see IInformationControl#setForegroundColor(Color)
	 */
	public void setForegroundColor(Color foreground) {
		fText.setForeground(foreground);
	}

	/*
	 * @see IInformationControl#setBackgroundColor(Color)
	 */
	public void setBackgroundColor(Color background) {
		fText.setBackground(background);
	}

	/*
	 * @see IInformationControl#isFocusControl()
	 */
	public boolean isFocusControl() {
		return fText.isFocusControl();
	}

	/*
	 * @see IInformationControl#setFocus()
	 */
	public void setFocus() {
		fShell.forceFocus();
		fText.setFocus();
	}

	/*
	 * @see IInformationControl#addFocusListener(FocusListener)
	 */
	public void addFocusListener(FocusListener listener) {
		fText.addFocusListener(listener);
	}

	/*
	 * @see IInformationControl#removeFocusListener(FocusListener)
	 */
	public void removeFocusListener(FocusListener listener) {
		fText.removeFocusListener(listener);
	}

	/*
	 * @see IInformationControlExtension#hasContents()
	 */
	public boolean hasContents() {
		return fText.getCharCount() > 0;
	}

	protected ISourceViewer getViewer()  {
		return fViewer;
	}
}
