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
package org.rubypeople.rdt.internal.ui.preferences;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Preferences;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.ColorSelector;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Scrollable;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.eclipse.ui.editors.text.EditorsUI;
import org.eclipse.ui.texteditor.ChainedPreferenceStore;
import org.rubypeople.rdt.core.RubyCore;
import org.rubypeople.rdt.internal.corext.util.Messages;
import org.rubypeople.rdt.internal.ui.RubyPlugin;
import org.rubypeople.rdt.internal.ui.rubyeditor.RubySourceViewer;
import org.rubypeople.rdt.internal.ui.text.IRubyColorConstants;
import org.rubypeople.rdt.internal.ui.text.IRubyPartitions;
import org.rubypeople.rdt.internal.ui.text.PreferencesAdapter;
import org.rubypeople.rdt.internal.ui.text.RubyColorManager;
import org.rubypeople.rdt.internal.ui.text.SimpleRubySourceViewerConfiguration;
import org.rubypeople.rdt.internal.ui.util.ExceptionHandler;
import org.rubypeople.rdt.internal.ui.util.PixelConverter;
import org.rubypeople.rdt.internal.ui.util.SWTUtil;
import org.rubypeople.rdt.ui.PreferenceConstants;
import org.rubypeople.rdt.ui.RubyUI;
import org.rubypeople.rdt.ui.text.IColorManager;

/**
 * Configures Ruby Editor hover preferences.
 * 
 * @since 0.9.0
 */
class RubyEditorColoringConfigurationBlock extends AbstractConfigurationBlock {
	
    private static final String DIALOGSTORE_LASTLOADPATH= RubyUI.ID_PLUGIN + ".syntaxcoloring.loadpath"; //$NON-NLS-1$
    private static final String DIALOGSTORE_LASTSAVEPATH= RubyUI.ID_PLUGIN + ".syntaxcoloring.savepath"; //$NON-NLS-1$
	
	/**
	 * Item in the highlighting color list.
	 * 
	 * @since 0.9.0
	 */
	public static class HighlightingColorListItem {
		/** Display name */
		private String fDisplayName;
		/** Color preference key */
		private String fColorKey;
		/** Bold preference key */
		private String fBoldKey;
		/** Background preference key */
		private String fBackgroundKey;
		/** Italic preference key */
		private String fItalicKey;
		/**
		 * Strikethrough preference key.
		 * @since 0.9.0
		 */
		private String fStrikethroughKey;
		/** 
		 * Underline preference key.
		 * @since 0.9.0
		 */
		private String fUnderlineKey;
		/** 
		 * Background enablement key.
		 * @since 1.0.0
		 */
		private String fBackgroundEnabledKey;
		
		/**
		 * Initialize the item with the given values.
		 * @param displayName the display name
		 * @param colorKey the color preference key
		 * @param bgColorKey the color preference key
		 * @param bgEnabledKey whetehr the custom background color is enabled
		 * @param boldKey the bold preference key
		 * @param italicKey the italic preference key
		 * @param strikethroughKey the strikethrough preference key
		 * @param underlineKey the underline preference key
		 */
		public HighlightingColorListItem(String displayName, String colorKey, String bgColorKey, String bgEnabledKey, String boldKey, String italicKey, String strikethroughKey, String underlineKey) {
			fDisplayName= displayName;
			fColorKey= colorKey;
			fBackgroundKey = bgColorKey;
			fBackgroundEnabledKey = bgEnabledKey;
			fBoldKey= boldKey;
			fItalicKey= italicKey;
			fStrikethroughKey= strikethroughKey; 
			fUnderlineKey= underlineKey; 
		}
		
		/**
		 * @return the bold preference key
		 */
		public String getBoldKey() {
			return fBoldKey;
		}
		
		/**
		 * @return the background preference key
		 */
		public String getBackgroundKey() {
			return fBackgroundKey;
		}
		
		/**
		 * @return the bold preference key
		 */
		public String getItalicKey() {
			return fItalicKey;
		}
		
		/**
		 * @return the strikethrough preference key
		 * @since 3.1
		 */
		public String getStrikethroughKey() {
			return fStrikethroughKey;
		}
		
		/**
		 * @return the underline preference key
		 * @since 3.1
		 */
		public String getUnderlineKey() {
			return fUnderlineKey;
		}
		
		/**
		 * @return the color preference key
		 */
		public String getColorKey() {
			return fColorKey;
		}
		
		/**
		 * @return the display name
		 */
		public String getDisplayName() {
			return fDisplayName;
		}

		public String getBackgroundEnabledKey() {
			return fBackgroundEnabledKey;
		}
	}
	
	private static class SemanticHighlightingColorListItem extends HighlightingColorListItem {
	
		/** Enablement preference key */
		private final String fEnableKey;
		
		/**
		 * Initialize the item with the given values.
		 * @param displayName the display name
		 * @param colorKey the color preference key
		 * @param bgColorKey the color preference key
		 * @param bgEnabledKey
		 * @param boldKey the bold preference key
		 * @param italicKey the italic preference key
		 * @param strikethroughKey the strikethroughKey preference key
		 * @param underlineKey the underlineKey preference key
		 * @param enableKey the enable preference key
		 */
		public SemanticHighlightingColorListItem(String displayName, String colorKey, String bgColorKey, String bgEnabledKey, String boldKey, String italicKey, String strikethroughKey, String underlineKey, String enableKey) {
			super(displayName, colorKey, bgColorKey, bgEnabledKey, boldKey, italicKey, strikethroughKey, underlineKey);
			fEnableKey= enableKey;
		}
	
		/**
		 * @return the enablement preference key
		 */
		public String getEnableKey() {
			return fEnableKey;
		}
	}

	/**
	 * Color list label provider.
	 * 
	 * @since 3.0
	 */
	private class ColorListLabelProvider extends LabelProvider {
		/*
		 * @see org.eclipse.jface.viewers.ILabelProvider#getText(ruby.lang.Object)
		 */
		public String getText(Object element) {
			if (element instanceof String)
				return (String) element;
			return ((HighlightingColorListItem)element).getDisplayName();
		}
	}

	/**
	 * Color list content provider.
	 * 
	 * @since 3.0
	 */
	private class ColorListContentProvider implements ITreeContentProvider {
	
		/*
		 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(ruby.lang.Object)
		 */
		public Object[] getElements(Object inputElement) {
			return new String[] {fRubyCategory};
		}
	
		/*
		 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
		 */
		public void dispose() {
		}
	
		/*
		 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, ruby.lang.Object, ruby.lang.Object)
		 */
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}

		public Object[] getChildren(Object parentElement) {
			if (parentElement instanceof String) {
				String entry= (String) parentElement;
				if (fRubyCategory.equals(entry))
					return fListModel.toArray();
			}
			return new Object[0];
		}

		public Object getParent(Object element) {
			if (element instanceof String)
				return null;
			return fRubyCategory;
		}

		public boolean hasChildren(Object element) {
			return element instanceof String;
		}
	}

	private static final String BOLD= PreferenceConstants.EDITOR_BOLD_SUFFIX;
	private static final String BACKGROUND= PreferenceConstants.EDITOR_BG_SUFFIX;
	private static final String BACKGROUND_ENABLED= PreferenceConstants.EDITOR_BG_ENABLED_SUFFIX;
	/**
	 * Preference key suffix for italic preferences.
	 * @since 0.9.0
	 */
	private static final String ITALIC= PreferenceConstants.EDITOR_ITALIC_SUFFIX;
	/**
	 * Preference key suffix for strikethrough preferences.
	 * @since  0.9.0
	 */
	private static final String STRIKETHROUGH= PreferenceConstants.EDITOR_STRIKETHROUGH_SUFFIX;
	/**
	 * Preference key suffix for underline preferences.
	 * @since 0.9.0
	 */
	private static final String UNDERLINE= PreferenceConstants.EDITOR_UNDERLINE_SUFFIX;
	
	private static final String COMPILER_TASK_TAGS= RubyCore.COMPILER_TASK_TAGS;
	/**
	 * The keys of the overlay store. 
	 */
	private final String[][] fSyntaxColorListModel= new String[][] {
			{ PreferencesMessages.RubyEditorPreferencePage_multiLineComment, IRubyColorConstants.RUBY_MULTI_LINE_COMMENT }, 
			{ PreferencesMessages.RubyEditorPreferencePage_singleLineComment, IRubyColorConstants.RUBY_SINGLE_LINE_COMMENT }, 
			{ PreferencesMessages.RubyEditorPreferencePage_rubyCommentTaskTags, IRubyColorConstants.TASK_TAG }, 
			{ PreferencesMessages.RubyEditorPreferencePage_keywords, IRubyColorConstants.RUBY_KEYWORD },
			{ PreferencesMessages.RubyEditorPreferencePage_strings, IRubyColorConstants.RUBY_STRING }, 
			{ PreferencesMessages.RubyEditorPreferencePage_characters, IRubyColorConstants.RUBY_CHARACTER },
			{ PreferencesMessages.RubyEditorPreferencePage_commands, IRubyColorConstants.RUBY_COMMAND },
			{ PreferencesMessages.RubyEditorPreferencePage_fixnums, IRubyColorConstants.RUBY_FIXNUM },
			{ PreferencesMessages.RubyEditorPreferencePage_globals, IRubyColorConstants.RUBY_GLOBAL },
			{ PreferencesMessages.RubyEditorPreferencePage_regular_expressions, IRubyColorConstants.RUBY_REGEXP },
			{ PreferencesMessages.RubyEditorPreferencePage_symbols, IRubyColorConstants.RUBY_SYMBOL },
			{ PreferencesMessages.RubyEditorPreferencePage_variables, IRubyColorConstants.RUBY_INSTANCE_VARIABLE },
			{ PreferencesMessages.RubyEditorPreferencePage_others, IRubyColorConstants.RUBY_DEFAULT } 
	};
	
	private final String fRubyCategory= PreferencesMessages.RubyEditorPreferencePage_coloring_category_ruby; 

	private ColorSelector fSyntaxForegroundColorEditor;
	private ColorSelector fSyntaxBackgroundColorEditor;
	private Label fColorEditorLabel;
	private Button fBoldCheckBox;
	private Button fEnableCheckbox;
	/**
	 * Check box for italic preference.
	 * @since  3.0
	 */
	private Button fItalicCheckBox;
	/**
	 * Check box for strikethrough preference.
	 * @since  3.1
	 */
	private Button fStrikethroughCheckBox;
	/**
	 * Check box for underline preference.
	 * @since  3.1
	 */
	private Button fUnderlineCheckBox;
	/**
	 * Highlighting color list
	 * @since  3.0
	 */
	private final java.util.List fListModel= new ArrayList();
	/**
	 * Highlighting color list viewer
	 * @since  3.0
	 */
	private StructuredViewer fListViewer;
	/**
	 * The previewer.
	 * @since 3.0
	 */
	private RubySourceViewer fPreviewViewer;
	/**
	 * The color manager.
	 * @since 3.1
	 */
	private IColorManager fColorManager;
	/**
	 * The font metrics.
	 * @since 3.1
	 */
	private FontMetrics fFontMetrics;
	private Button fLoadButton;
	private Button fSaveButton;
	private Composite fComposite;
	private Button fEnableBackgroundCheckbox;

	public RubyEditorColoringConfigurationBlock(OverlayPreferenceStore store) {
		super(store);
		
		fColorManager= new RubyColorManager(false);
		
		for (int i= 0, n= fSyntaxColorListModel.length; i < n; i++)
			fListModel.add(new HighlightingColorListItem (fSyntaxColorListModel[i][0], fSyntaxColorListModel[i][1], fSyntaxColorListModel[i][1] + BACKGROUND, fSyntaxColorListModel[i][1] + BACKGROUND_ENABLED, fSyntaxColorListModel[i][1] + BOLD, fSyntaxColorListModel[i][1] + ITALIC, fSyntaxColorListModel[i][1] + STRIKETHROUGH, fSyntaxColorListModel[i][1] + UNDERLINE));
		
		store.addKeys(createOverlayStoreKeys());
	}

	private OverlayPreferenceStore.OverlayKey[] createOverlayStoreKeys() {
		
		ArrayList overlayKeys= new ArrayList();
		
		for (int i= 0, n= fListModel.size(); i < n; i++) {
			HighlightingColorListItem item= (HighlightingColorListItem) fListModel.get(i);
			overlayKeys.add(new OverlayPreferenceStore.OverlayKey(OverlayPreferenceStore.STRING, item.getColorKey()));
			overlayKeys.add(new OverlayPreferenceStore.OverlayKey(OverlayPreferenceStore.STRING, item.getBackgroundKey()));
			overlayKeys.add(new OverlayPreferenceStore.OverlayKey(OverlayPreferenceStore.BOOLEAN, item.getBackgroundEnabledKey()));
			overlayKeys.add(new OverlayPreferenceStore.OverlayKey(OverlayPreferenceStore.BOOLEAN, item.getBoldKey()));
			overlayKeys.add(new OverlayPreferenceStore.OverlayKey(OverlayPreferenceStore.BOOLEAN, item.getItalicKey()));
			overlayKeys.add(new OverlayPreferenceStore.OverlayKey(OverlayPreferenceStore.BOOLEAN, item.getStrikethroughKey()));
			overlayKeys.add(new OverlayPreferenceStore.OverlayKey(OverlayPreferenceStore.BOOLEAN, item.getUnderlineKey()));
			
			if (item instanceof SemanticHighlightingColorListItem)
				overlayKeys.add(new OverlayPreferenceStore.OverlayKey(OverlayPreferenceStore.BOOLEAN, ((SemanticHighlightingColorListItem) item).getEnableKey()));
		}
		
		OverlayPreferenceStore.OverlayKey[] keys= new OverlayPreferenceStore.OverlayKey[overlayKeys.size()];
		overlayKeys.toArray(keys);
		return keys;
	}

	/**
	 * Creates page for hover preferences.
	 * 
	 * @param parent the parent composite
	 * @return the control for the preference page
	 */
	public Control createControl(Composite parent) {
		initializeDialogUnits(parent);
		return createSyntaxPage(parent);
	}
	
	/**
     * Returns the number of pixels corresponding to the width of the given
     * number of characters.
     * <p>
     * This method may only be called after <code>initializeDialogUnits</code>
     * has been called.
     * </p>
     * <p>
     * Clients may call this framework method, but should not override it.
     * </p>
     * 
     * @param chars
     *            the number of characters
     * @return the number of pixels
     */
    private int convertWidthInCharsToPixels(int chars) {
        // test for failure to initialize for backward compatibility
        if (fFontMetrics == null)
            return 0;
        return Dialog.convertWidthInCharsToPixels(fFontMetrics, chars);
    }

	/**
     * Returns the number of pixels corresponding to the height of the given
     * number of characters.
     * <p>
     * This method may only be called after <code>initializeDialogUnits</code>
     * has been called.
     * </p>
     * <p>
     * Clients may call this framework method, but should not override it.
     * </p>
     * 
     * @param chars
     *            the number of characters
     * @return the number of pixels
     */
    private int convertHeightInCharsToPixels(int chars) {
        // test for failure to initialize for backward compatibility
        if (fFontMetrics == null)
            return 0;
        return Dialog.convertHeightInCharsToPixels(fFontMetrics, chars);
    }
    
	public void initialize() {
		super.initialize();
		
		fListViewer.setInput(fListModel);
		fListViewer.setSelection(new StructuredSelection(fRubyCategory));
	}

	public void performDefaults() {
		super.performDefaults();
		
		handleSyntaxColorListSelection();

		fPreviewViewer.invalidateTextPresentation();
	}

	/*
	 * @see org.eclipse.jdt.internal.ui.preferences.IPreferenceConfigurationBlock#dispose()
	 */
	public void dispose() {
		fColorManager.dispose();
		
		super.dispose();
	}

	private void handleSyntaxColorListSelection() {
		HighlightingColorListItem item= getHighlightingColorListItem();
		if (item == null) {
			fEnableCheckbox.setEnabled(false);
			fSyntaxForegroundColorEditor.getButton().setEnabled(false);
			fSyntaxBackgroundColorEditor.getButton().setEnabled(false);
			fColorEditorLabel.setEnabled(false);
			fBoldCheckBox.setEnabled(false);
			fItalicCheckBox.setEnabled(false);
			fStrikethroughCheckBox.setEnabled(false);
			fUnderlineCheckBox.setEnabled(false);
			return;
		}
		RGB rgb= PreferenceConverter.getColor(getPreferenceStore(), item.getColorKey());
		fSyntaxForegroundColorEditor.setColorValue(rgb);
		rgb= PreferenceConverter.getColor(getPreferenceStore(), item.getBackgroundKey());
		// TODO If we get back default color, show the default text editor bg color.
		fSyntaxBackgroundColorEditor.setColorValue(rgb);
		fBoldCheckBox.setSelection(getPreferenceStore().getBoolean(item.getBoldKey()));
		fItalicCheckBox.setSelection(getPreferenceStore().getBoolean(item.getItalicKey()));
		fStrikethroughCheckBox.setSelection(getPreferenceStore().getBoolean(item.getStrikethroughKey()));
		fUnderlineCheckBox.setSelection(getPreferenceStore().getBoolean(item.getUnderlineKey()));
		if (item instanceof SemanticHighlightingColorListItem) {
			fEnableCheckbox.setEnabled(true);
			boolean enable= getPreferenceStore().getBoolean(((SemanticHighlightingColorListItem) item).getEnableKey());
			fEnableCheckbox.setSelection(enable);
			fSyntaxForegroundColorEditor.getButton().setEnabled(enable);
			fColorEditorLabel.setEnabled(enable);
			fBoldCheckBox.setEnabled(enable);
			fItalicCheckBox.setEnabled(enable);
			fStrikethroughCheckBox.setEnabled(enable);
			fUnderlineCheckBox.setEnabled(enable);
			fEnableBackgroundCheckbox.setEnabled(enable);
			
			boolean bgEnabled = getPreferenceStore().getBoolean(((SemanticHighlightingColorListItem) item).getBackgroundEnabledKey());
			fEnableBackgroundCheckbox.setSelection(bgEnabled);
			fSyntaxBackgroundColorEditor.getButton().setEnabled(bgEnabled);		
		} else {
			fSyntaxForegroundColorEditor.getButton().setEnabled(true);
			fColorEditorLabel.setEnabled(true);
			fBoldCheckBox.setEnabled(true);
			fItalicCheckBox.setEnabled(true);
			fStrikethroughCheckBox.setEnabled(true);
			fUnderlineCheckBox.setEnabled(true);
			fEnableCheckbox.setEnabled(false);
			fEnableCheckbox.setSelection(true);
			
			fEnableBackgroundCheckbox.setEnabled(true);
			boolean bgEnabled = getPreferenceStore().getBoolean(item.getBackgroundEnabledKey());
			fEnableBackgroundCheckbox.setSelection(bgEnabled);
			fSyntaxBackgroundColorEditor.getButton().setEnabled(bgEnabled);
		}
	}
	
	private Control createSyntaxPage(final Composite parent) {
		
		Composite colorComposite= new Composite(parent, SWT.NONE);
		fComposite = colorComposite;
		GridLayout layout= new GridLayout();
		layout.marginHeight= 0;
		layout.marginWidth= 0;
		colorComposite.setLayout(layout);

		Link link= new Link(colorComposite, SWT.NONE);
		link.setText(PreferencesMessages.RubyEditorColoringConfigurationBlock_link);
		link.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				PreferencesUtil.createPreferenceDialogOn(parent.getShell(), e.text, null, null); 
			}
		});
		// TODO replace by link-specific tooltips when
		// bug https://bugs.eclipse.org/bugs/show_bug.cgi?id=88866 gets fixed
//		link.setToolTipText(PreferencesMessages.RubyEditorColoringConfigurationBlock_link_tooltip); 
		
		GridData gridData= new GridData(SWT.FILL, SWT.BEGINNING, true, false);
		gridData.widthHint= 150; // only expand further if anyone else requires it
		gridData.horizontalSpan= 2;
		link.setLayoutData(gridData);

		addFiller(colorComposite, 1);
		
		final Composite group= createComposite(colorComposite, 4);
        final GridData groupData= new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        groupData.horizontalSpan= 4;
        group.setLayoutData(groupData);
		fLoadButton= createButton(group, "Import", GridData.HORIZONTAL_ALIGN_END); 
        fSaveButton= createButton(group, "Export", GridData.HORIZONTAL_ALIGN_END); 
        new ButtonController();
		
		
		Label label;
		label= new Label(colorComposite, SWT.LEFT);
		label.setText(PreferencesMessages.RubyEditorPreferencePage_coloring_element); 
		label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	
		Composite editorComposite= new Composite(colorComposite, SWT.NONE);
		layout= new GridLayout();
		layout.numColumns= 2;
		layout.marginHeight= 0;
		layout.marginWidth= 0;
		editorComposite.setLayout(layout);
		GridData gd= new GridData(SWT.FILL, SWT.BEGINNING, true, false);
		editorComposite.setLayoutData(gd);		
	
		fListViewer= new TreeViewer(editorComposite, SWT.SINGLE | SWT.BORDER);
		fListViewer.setLabelProvider(new ColorListLabelProvider());
		fListViewer.setContentProvider(new ColorListContentProvider());
		fListViewer.setSorter(new ViewerSorter() {
			public int category(Object element) {
				// don't sort the top level categories
				if (fRubyCategory.equals(element))
					return 0;
				// to sort semantic settings after partition based ones:
//				if (element instanceof SemanticHighlightingColorListItem)
//					return 1;
				return 0;
			}
		});
		gd= new GridData(SWT.BEGINNING, SWT.BEGINNING, false, true);
		gd.heightHint= convertHeightInCharsToPixels(9);
		int maxWidth= 0;
		for (Iterator it= fListModel.iterator(); it.hasNext();) {
			HighlightingColorListItem item= (HighlightingColorListItem) it.next();
			maxWidth= Math.max(maxWidth, convertWidthInCharsToPixels(item.getDisplayName().length()));
		}
		ScrollBar vBar= ((Scrollable) fListViewer.getControl()).getVerticalBar();
		if (vBar != null)
			maxWidth += vBar.getSize().x * 3; // scrollbars and tree indentation guess
		gd.widthHint= maxWidth;
		
		fListViewer.getControl().setLayoutData(gd);
						
		Composite stylesComposite= new Composite(editorComposite, SWT.NONE);
		layout= new GridLayout();
		layout.marginHeight= 0;
		layout.marginWidth= 0;
		layout.numColumns= 2;
		stylesComposite.setLayout(layout);
		stylesComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		fEnableCheckbox= new Button(stylesComposite, SWT.CHECK);
		fEnableCheckbox.setText(PreferencesMessages.RubyEditorPreferencePage_enable); 
		gd= new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalAlignment= GridData.BEGINNING;
		gd.horizontalSpan= 2;
		fEnableCheckbox.setLayoutData(gd);
		
		fColorEditorLabel= new Label(stylesComposite, SWT.LEFT);
		fColorEditorLabel.setText(PreferencesMessages.RubyEditorPreferencePage_color); 
		gd= new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		gd.horizontalIndent= 20;
		fColorEditorLabel.setLayoutData(gd);
	
		fSyntaxForegroundColorEditor= new ColorSelector(stylesComposite);
		Button foregroundColorButton= fSyntaxForegroundColorEditor.getButton();
		gd= new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		foregroundColorButton.setLayoutData(gd);
		
		
		// Background color
		// TODO Create an enable/system default checkbox for background
		fEnableBackgroundCheckbox= new Button(stylesComposite, SWT.CHECK | SWT.LEFT);
		fEnableBackgroundCheckbox.setText(PreferencesMessages.RubyEditorPreferencePage_background_color); 
		gd= new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		gd.horizontalIndent= 20;
		fEnableBackgroundCheckbox.setLayoutData(gd);		
		fEnableBackgroundCheckbox.setEnabled(false);
	
		fSyntaxBackgroundColorEditor= new ColorSelector(stylesComposite);
		Button backgroundColorButton= fSyntaxBackgroundColorEditor.getButton();
		gd= new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		backgroundColorButton.setLayoutData(gd);
		
		
		fBoldCheckBox= new Button(stylesComposite, SWT.CHECK);
		fBoldCheckBox.setText(PreferencesMessages.RubyEditorPreferencePage_bold); 
		gd= new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		gd.horizontalIndent= 20;
		gd.horizontalSpan= 2;
		fBoldCheckBox.setLayoutData(gd);
		
		fItalicCheckBox= new Button(stylesComposite, SWT.CHECK);
		fItalicCheckBox.setText(PreferencesMessages.RubyEditorPreferencePage_italic); 
		gd= new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		gd.horizontalIndent= 20;
		gd.horizontalSpan= 2;
		fItalicCheckBox.setLayoutData(gd);
		
		fStrikethroughCheckBox= new Button(stylesComposite, SWT.CHECK);
		fStrikethroughCheckBox.setText(PreferencesMessages.RubyEditorPreferencePage_strikethrough); 
		gd= new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		gd.horizontalIndent= 20;
		gd.horizontalSpan= 2;
		fStrikethroughCheckBox.setLayoutData(gd);
		
		fUnderlineCheckBox= new Button(stylesComposite, SWT.CHECK);
		fUnderlineCheckBox.setText(PreferencesMessages.RubyEditorPreferencePage_underline); 
		gd= new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		gd.horizontalIndent= 20;
		gd.horizontalSpan= 2;
		fUnderlineCheckBox.setLayoutData(gd);
		
		label= new Label(colorComposite, SWT.LEFT);
		label.setText(PreferencesMessages.RubyEditorPreferencePage_preview); 
		label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		Control previewer= createPreviewer(colorComposite);
		gd= new GridData(GridData.FILL_BOTH);
		gd.widthHint= convertWidthInCharsToPixels(20);
		gd.heightHint= convertHeightInCharsToPixels(5);
		previewer.setLayoutData(gd);
		
		fListViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				handleSyntaxColorListSelection();
			}
		});
		
		foregroundColorButton.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
				// do nothing
			}
			public void widgetSelected(SelectionEvent e) {
				HighlightingColorListItem item= getHighlightingColorListItem();
				PreferenceConverter.setValue(getPreferenceStore(), item.getColorKey(), fSyntaxForegroundColorEditor.getColorValue());
			}
		});
				
		backgroundColorButton.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
				// do nothing
			}
			public void widgetSelected(SelectionEvent e) {
				HighlightingColorListItem item= getHighlightingColorListItem();
				PreferenceConverter.setValue(getPreferenceStore(), item.getBackgroundKey(), fSyntaxBackgroundColorEditor.getColorValue());
			}
		});
	
		fBoldCheckBox.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
				// do nothing
			}
			public void widgetSelected(SelectionEvent e) {
				HighlightingColorListItem item= getHighlightingColorListItem();
				getPreferenceStore().setValue(item.getBoldKey(), fBoldCheckBox.getSelection());
			}
		});
				
		fItalicCheckBox.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
				// do nothing
			}
			public void widgetSelected(SelectionEvent e) {
				HighlightingColorListItem item= getHighlightingColorListItem();
				getPreferenceStore().setValue(item.getItalicKey(), fItalicCheckBox.getSelection());
			}
		});
		fStrikethroughCheckBox.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
				// do nothing
			}
			public void widgetSelected(SelectionEvent e) {
				HighlightingColorListItem item= getHighlightingColorListItem();
				getPreferenceStore().setValue(item.getStrikethroughKey(), fStrikethroughCheckBox.getSelection());
			}
		});
		
		fUnderlineCheckBox.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
				// do nothing
			}
			public void widgetSelected(SelectionEvent e) {
				HighlightingColorListItem item= getHighlightingColorListItem();
				getPreferenceStore().setValue(item.getUnderlineKey(), fUnderlineCheckBox.getSelection());
			}
		});
				
		fEnableCheckbox.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
				// do nothing
			}
			public void widgetSelected(SelectionEvent e) {
				HighlightingColorListItem item= getHighlightingColorListItem();
				if (item instanceof SemanticHighlightingColorListItem) {
					boolean enable= fEnableCheckbox.getSelection();
					getPreferenceStore().setValue(((SemanticHighlightingColorListItem) item).getEnableKey(), enable);
					fEnableCheckbox.setSelection(enable);
					fEnableBackgroundCheckbox.setEnabled(enable);
					fSyntaxForegroundColorEditor.getButton().setEnabled(enable);
					fSyntaxBackgroundColorEditor.getButton().setEnabled(enable);
					fColorEditorLabel.setEnabled(enable);
					fBoldCheckBox.setEnabled(enable);
					fItalicCheckBox.setEnabled(enable);
					fStrikethroughCheckBox.setEnabled(enable);
					fUnderlineCheckBox.setEnabled(enable);
				}
			}
		});
		
		fEnableBackgroundCheckbox.addSelectionListener(new SelectionListener() {
			
			public void widgetSelected(SelectionEvent e) {
				HighlightingColorListItem item= getHighlightingColorListItem();
				boolean enable= fEnableBackgroundCheckbox.getSelection();
				getPreferenceStore().setValue(item.getBackgroundEnabledKey(), enable);
				fSyntaxBackgroundColorEditor.getButton().setEnabled(enable);				
			}
		
			public void widgetDefaultSelected(SelectionEvent e) {		
			}
		
		});
		
		colorComposite.layout(false);
				
		return colorComposite;
	}
	
    private Composite createComposite(Composite parent, int numColumns) {
        final Composite composite = new Composite(parent, SWT.NONE);
        composite.setFont(parent.getFont());
        
        final GridLayout layout = new GridLayout(numColumns, false);
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        composite.setLayout(layout);
        return composite;
    }
    
    private static Button createButton(Composite composite, String text, final int style) {
        final Button button= new Button(composite, SWT.PUSH);
        button.setFont(composite.getFont());
        button.setText(text);

        final GridData gd= new GridData(style);
        gd.widthHint= SWTUtil.getButtonWidthHint(button);
        button.setLayoutData(gd);
        return button;
    }
	
	private void addFiller(Composite composite, int horizontalSpan) {
		PixelConverter pixelConverter= new PixelConverter(composite);
		Label filler= new Label(composite, SWT.LEFT );
		GridData gd= new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gd.horizontalSpan= horizontalSpan;
		gd.heightHint= pixelConverter.convertHeightInCharsToPixels(1) / 2;
		filler.setLayoutData(gd);
	}

	private Control createPreviewer(Composite parent) {
		
		IPreferenceStore generalTextStore= EditorsUI.getPreferenceStore();
		IPreferenceStore store= new ChainedPreferenceStore(new IPreferenceStore[] { getPreferenceStore(), new PreferencesAdapter(createTemporaryCorePreferenceStore()), generalTextStore });
		fPreviewViewer= new RubySourceViewer(parent, null, null, false, SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER, store);
		SimpleRubySourceViewerConfiguration configuration= new SimpleRubySourceViewerConfiguration(fColorManager, store, null, IRubyPartitions.RUBY_PARTITIONING, false);
		fPreviewViewer.configure(configuration);
		Font font= JFaceResources.getFont(PreferenceConstants.EDITOR_TEXT_FONT);
		fPreviewViewer.getTextWidget().setFont(font);
		new RubySourcePreviewerUpdater(fPreviewViewer, configuration, store);
		fPreviewViewer.setEditable(false);
		
		String content= loadPreviewContentFromFile("ColorSettingPreviewCode.txt"); //$NON-NLS-1$
		IDocument document= new Document(content);
		RubyPlugin.getDefault().getRubyTextTools().setupRubyDocumentPartitioner(document, IRubyPartitions.RUBY_PARTITIONING);
		fPreviewViewer.setDocument(document);
		
		return fPreviewViewer.getControl();
	}


	private Preferences createTemporaryCorePreferenceStore() {
		Preferences result= new Preferences();
		
		result.setValue(COMPILER_TASK_TAGS, "TASK,TODO"); //$NON-NLS-1$
		
		return result;
	}


	private String loadPreviewContentFromFile(String filename) {
		String line;
		String separator= System.getProperty("line.separator"); //$NON-NLS-1$
		StringBuffer buffer= new StringBuffer(512);
		BufferedReader reader= null;
		try {
			reader= new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(filename)));
			while ((line= reader.readLine()) != null) {
				buffer.append(line);
				buffer.append(separator);
			}
		} catch (IOException io) {
			RubyPlugin.log(io);
		} finally {
			if (reader != null) {
				try { reader.close(); } catch (IOException e) {}
			}
		}
		return buffer.toString();
	}

	/**
	 * Returns the current highlighting color list item.
	 * 
	 * @return the current highlighting color list item
	 * @since 3.0
	 */
	private HighlightingColorListItem getHighlightingColorListItem() {
		IStructuredSelection selection= (IStructuredSelection) fListViewer.getSelection();
		Object element= selection.getFirstElement();
		if (element instanceof String)
			return null;
		return (HighlightingColorListItem) element;
	}
	
	/**
     * Initializes the computation of horizontal and vertical dialog units based
     * on the size of current font.
     * <p>
     * This method must be called before any of the dialog unit based conversion
     * methods are called.
     * </p>
     * 
     * @param testControl
     *            a control from which to obtain the current font
     */
    private void initializeDialogUnits(Control testControl) {
        // Compute and store a font metric
        GC gc = new GC(testControl);
        gc.setFont(JFaceResources.getDialogFont());
        fFontMetrics = gc.getFontMetrics();
        gc.dispose();
    }
    
    private class ButtonController implements SelectionListener {
    	public ButtonController() {
			fLoadButton.addSelectionListener(this);
			fSaveButton.addSelectionListener(this);
		}

		public void widgetDefaultSelected(SelectionEvent e) {
		}

		public void widgetSelected(SelectionEvent e) {
			 final Button button= (Button)e.widget;
	            if (button == fSaveButton)
	                saveButtonPressed();
	            else if (button == fLoadButton)
	                loadButtonPressed();		
		}

		private void loadButtonPressed() {
			final FileDialog dialog= new FileDialog(fComposite.getShell(), SWT.OPEN);
            dialog.setText("Load Profile"); 
            dialog.setFilterExtensions(new String [] {"*.xml"}); //$NON-NLS-1$
            final String lastPath= RubyPlugin.getDefault().getDialogSettings().get(DIALOGSTORE_LASTLOADPATH);
            if (lastPath != null) {
                dialog.setFilterPath(lastPath);
            }
            final String path= dialog.open();
            if (path == null) 
                return;
            RubyPlugin.getDefault().getDialogSettings().put(DIALOGSTORE_LASTLOADPATH, dialog.getFilterPath());
            
            final File file= new File(path);
            Map<String, String> profiles= null;
            try {
                profiles= SyntaxColoringStore.readFromFile(file);
            } catch (CoreException e) {
                final String title= "Import Profile"; 
                final String message= "Could not import the profile"; 
                ExceptionHandler.handle(e, fComposite.getShell(), title, message);
            }
            if (profiles == null || profiles.isEmpty())
                return;
            
	        // apply the colors to the current settings
            for (String key : profiles.keySet()) {
				getPreferenceStore().setValue(key, profiles.get(key));
			}
            performDefaults();
		}

		private void saveButtonPressed() {
            final FileDialog dialog= new FileDialog(fComposite.getShell(), SWT.SAVE);
            dialog.setText("Export profile"); 
            dialog.setFilterExtensions(new String [] {"*.xml"}); //$NON-NLS-1$
            
            final String lastPath= RubyPlugin.getDefault().getDialogSettings().get(DIALOGSTORE_LASTSAVEPATH);
            if (lastPath != null) {
                dialog.setFilterPath(lastPath);
            }
            final String path= dialog.open();
            if (path == null) 
                return;
            
            RubyPlugin.getDefault().getDialogSettings().put(DIALOGSTORE_LASTSAVEPATH, dialog.getFilterPath());
            
            final File file= new File(path);
            if (file.exists() && !MessageDialog.openQuestion(fComposite.getShell(), "Export profile", Messages.format("{0} already exists. Do you want to replace it?", path))) { 
                return;
            }
            try{
            	SyntaxColoringStore.write(fListModel, file);
            } catch (CoreException e) {
            	final String title= "Export Profile"; 
         	   	final String message= "Could not export the profiles."; 
         	   	ExceptionHandler.handle(e, fComposite.getShell(), title, message);
            }
		}
    }
}
