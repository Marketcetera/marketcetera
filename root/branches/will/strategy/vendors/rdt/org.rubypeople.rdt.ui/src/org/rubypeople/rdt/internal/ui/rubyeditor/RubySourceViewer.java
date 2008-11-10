/*
 * Created on Jan 13, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.rubypeople.rdt.internal.ui.rubyeditor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.BadPositionCategoryException;
import org.eclipse.jface.text.DocumentCommand;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IAutoEditStrategy;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IPositionUpdater;
import org.eclipse.jface.text.information.IInformationPresenter;
import org.eclipse.jface.text.link.ILinkedModeListener;
import org.eclipse.jface.text.link.InclusivePositionUpdater;
import org.eclipse.jface.text.link.LinkedModeModel;
import org.eclipse.jface.text.link.LinkedModeUI;
import org.eclipse.jface.text.link.LinkedPosition;
import org.eclipse.jface.text.link.LinkedPositionGroup;
import org.eclipse.jface.text.source.IOverviewRuler;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditorPreferenceConstants;
import org.eclipse.ui.texteditor.AbstractTextEditor;
import org.rubypeople.rdt.ui.text.RubySourceViewerConfiguration;

public class RubySourceViewer extends ProjectionViewer implements IPropertyChangeListener {

	/**
	 * Text operation code for requesting the outline for the current input.
	 */
	public static final int SHOW_OUTLINE= 51;

	/**
	 * Text operation code for requesting the outline for the element at the current position.
	 */
	public static final int OPEN_STRUCTURE= 52;

	/**
	 * Text operation code for requesting the hierarchy for the current input.
	 */
	public static final int SHOW_HIERARCHY= 53;
	
    private boolean fIgnoreTextConverters = false;
    
    /** The linked position list for code auto edit */
	protected final LinkedList fPositionList = new LinkedList();
    
	private IInformationPresenter fOutlinePresenter;
	private IInformationPresenter fStructurePresenter;
	
    /**
     * This viewer's foreground color.
     * @since 0.8.0
     */
    private Color fForegroundColor;
    /**
     * The viewer's background color.
     * @since 0.8.0
     */
    private Color fBackgroundColor;
    /**
     * This viewer's selection foreground color.
     * @since 0.8.0
     */
    private Color fSelectionForegroundColor;
    /**
     * The viewer's selection background color.
     * @since 0.8.0
     */
    private Color fSelectionBackgroundColor;
    
    /**
     * The preference store.
     *
     * @since 0.8.0
     */
    private IPreferenceStore fPreferenceStore;
    
    /**
     * Is this source viewer configured?
     *
     * @since 0.8.0
     */
    private boolean fIsConfigured;
    
    /** The auto edit category */
	protected static final String CATEGORY_AUTO_EDIT = "org.rubypeople.rdt.ui.RubyEditor"
			+ ".auto.edit." + System.currentTimeMillis(); //$NON-NLS-1$

	/** The position updater for auto edit */
	protected final IPositionUpdater fAutoEditUpdater;

    public RubySourceViewer(Composite composite, IVerticalRuler verticalRuler,
            IOverviewRuler overviewRuler, boolean overviewRulerVisible, int styles, IPreferenceStore store) {
        super(composite, verticalRuler, overviewRuler, overviewRulerVisible, styles);
        setPreferenceStore(store);
        fAutoEditUpdater = new InclusivePositionUpdater(CATEGORY_AUTO_EDIT);
    }
    
    /**
     * Sets the preference store on this viewer.
     *
     * @param store the preference store
     *
     * @since 0.8.0
     */
    public void setPreferenceStore(IPreferenceStore store) {
        if (fIsConfigured && fPreferenceStore != null)
            fPreferenceStore.removePropertyChangeListener(this);

        fPreferenceStore= store;

        if (fIsConfigured && fPreferenceStore != null) {
            fPreferenceStore.addPropertyChangeListener(this);
            initializeViewerColors();
        }
    }
    
    protected void initializeViewerColors() {
        if (fPreferenceStore != null) {

            StyledText styledText= getTextWidget();

            // ----------- foreground color --------------------
            Color color= fPreferenceStore.getBoolean(AbstractTextEditor.PREFERENCE_COLOR_FOREGROUND_SYSTEM_DEFAULT)
            ? null
            : createColor(fPreferenceStore, AbstractTextEditor.PREFERENCE_COLOR_FOREGROUND, styledText.getDisplay());
            styledText.setForeground(color);

            if (fForegroundColor != null)
                fForegroundColor.dispose();

            fForegroundColor= color;

            // ---------- background color ----------------------
            color= fPreferenceStore.getBoolean(AbstractTextEditor.PREFERENCE_COLOR_BACKGROUND_SYSTEM_DEFAULT)
            ? null
            : createColor(fPreferenceStore, AbstractTextEditor.PREFERENCE_COLOR_BACKGROUND, styledText.getDisplay());
            styledText.setBackground(color);

            if (fBackgroundColor != null)
                fBackgroundColor.dispose();

            fBackgroundColor= color;

            // ----------- selection foreground color --------------------
            color= fPreferenceStore.getBoolean(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_SELECTION_FOREGROUND_DEFAULT_COLOR)
                ? null
                : createColor(fPreferenceStore, AbstractDecoratedTextEditorPreferenceConstants.EDITOR_SELECTION_FOREGROUND_COLOR, styledText.getDisplay());
            styledText.setSelectionForeground(color);

            if (fSelectionForegroundColor != null)
                fSelectionForegroundColor.dispose();

            fSelectionForegroundColor= color;

            // ---------- selection background color ----------------------
            color= fPreferenceStore.getBoolean(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_SELECTION_BACKGROUND_DEFAULT_COLOR)
                ? null
                : createColor(fPreferenceStore, AbstractDecoratedTextEditorPreferenceConstants.EDITOR_SELECTION_BACKGROUND_COLOR, styledText.getDisplay());
            styledText.setSelectionBackground(color);

            if (fSelectionBackgroundColor != null)
                fSelectionBackgroundColor.dispose();

            fSelectionBackgroundColor= color;
        }
    }
    
    /*
     * @see ISourceViewer#configure(SourceViewerConfiguration)
     */
    public void configure(SourceViewerConfiguration configuration) {

        /*
         * Prevent access to colors disposed in unconfigure(), see:
         *   https://bugs.eclipse.org/bugs/show_bug.cgi?id=53641
         *   https://bugs.eclipse.org/bugs/show_bug.cgi?id=86177
         */
        StyledText textWidget= getTextWidget();
        if (textWidget != null && !textWidget.isDisposed()) {
            Color foregroundColor= textWidget.getForeground();
            if (foregroundColor != null && foregroundColor.isDisposed())
                textWidget.setForeground(null);
            Color backgroundColor= textWidget.getBackground();
            if (backgroundColor != null && backgroundColor.isDisposed())
                textWidget.setBackground(null);
        }

        super.configure(configuration);
		if (configuration instanceof RubySourceViewerConfiguration) {
			RubySourceViewerConfiguration javaSVCconfiguration= (RubySourceViewerConfiguration)configuration;
			fOutlinePresenter= javaSVCconfiguration.getOutlinePresenter(this, false);
			if (fOutlinePresenter != null)
				fOutlinePresenter.install(this);

			fStructurePresenter= javaSVCconfiguration.getOutlinePresenter(this, true);
			if (fStructurePresenter != null)
				fStructurePresenter.install(this);

//			fHierarchyPresenter= javaSVCconfiguration.getHierarchyPresenter(this, true);
//			if (fHierarchyPresenter != null)
//				fHierarchyPresenter.install(this);

		}

        if (fPreferenceStore != null) {
            fPreferenceStore.addPropertyChangeListener(this);
            initializeViewerColors();
        }

        fIsConfigured= true;
    }
    
    /*
     * @see org.eclipse.jface.text.source.ISourceViewerExtension2#unconfigure()
     * @since 0.8.0
     */
    public void unconfigure() {
    	if (fOutlinePresenter != null) {
			fOutlinePresenter.uninstall();
			fOutlinePresenter= null;
		}
		if (fStructurePresenter != null) {
			fStructurePresenter.uninstall();
			fStructurePresenter= null;
		}
//		if (fHierarchyPresenter != null) {
//			fHierarchyPresenter.uninstall();
//			fHierarchyPresenter= null;
//		}
		if (fForegroundColor != null) {
			fForegroundColor.dispose();
			fForegroundColor= null;
		}
		if (fBackgroundColor != null) {
			fBackgroundColor.dispose();
			fBackgroundColor= null;
		}

		if (fPreferenceStore != null)
			fPreferenceStore.removePropertyChangeListener(this);

		super.unconfigure();

		fIsConfigured= false;
    }
    
    /*
     * @see org.eclipse.jface.util.IPropertyChangeListener#propertyChange(org.eclipse.jface.util.PropertyChangeEvent)
     */
    public void propertyChange(PropertyChangeEvent event) {
        String property = event.getProperty();
        if (AbstractTextEditor.PREFERENCE_COLOR_FOREGROUND.equals(property)
                || AbstractTextEditor.PREFERENCE_COLOR_FOREGROUND_SYSTEM_DEFAULT.equals(property)
                || AbstractTextEditor.PREFERENCE_COLOR_BACKGROUND.equals(property)
                || AbstractTextEditor.PREFERENCE_COLOR_BACKGROUND_SYSTEM_DEFAULT.equals(property)
                || AbstractDecoratedTextEditorPreferenceConstants.EDITOR_SELECTION_FOREGROUND_COLOR.equals(property)
                || AbstractDecoratedTextEditorPreferenceConstants.EDITOR_SELECTION_FOREGROUND_DEFAULT_COLOR.equals(property)
                || AbstractDecoratedTextEditorPreferenceConstants.EDITOR_SELECTION_BACKGROUND_COLOR.equals(property)
                || AbstractDecoratedTextEditorPreferenceConstants.EDITOR_SELECTION_BACKGROUND_DEFAULT_COLOR.equals(property))
        {
            initializeViewerColors();
        }
    }
    
    /**
     * Creates a color from the information stored in the given preference store.
     * Returns <code>null</code> if there is no such information available.
     *
     * @param store the store to read from
     * @param key the key used for the lookup in the preference store
     * @param display the display used create the color
     * @return the created color according to the specification in the preference store
     * @since 3.0
     */
    private Color createColor(IPreferenceStore store, String key, Display display) {

        RGB rgb= null;

        if (store.contains(key)) {

            if (store.isDefault(key))
                rgb= PreferenceConverter.getDefaultColor(store, key);
            else
                rgb= PreferenceConverter.getColor(store, key);

            if (rgb != null)
                return new Color(display, rgb);
        }

        return null;
    }
    
	/*
	 * @see ITextOperationTarget#canDoOperation(int)
	 */
	public boolean canDoOperation(int operation) {
		if (operation == SHOW_OUTLINE)
			return fOutlinePresenter != null;
		if (operation == OPEN_STRUCTURE)
			return fStructurePresenter != null;
//		if (operation == SHOW_HIERARCHY)
//			return fHierarchyPresenter != null;

		return super.canDoOperation(operation);
	}

    public void doOperation(int operation) {
    	if (getTextWidget() == null)
			return;

		switch (operation) {
			case SHOW_OUTLINE:
				if (fOutlinePresenter != null)
					fOutlinePresenter.showInformation();
				return;
			case OPEN_STRUCTURE:
				if (fStructurePresenter != null)
					fStructurePresenter.showInformation();
				return;
//			case SHOW_HIERARCHY:
//				if (fHierarchyPresenter != null)
//					fHierarchyPresenter.showInformation();
//				return;
		}

		super.doOperation(operation);
    }
    
	/*
	 * @see org.eclipse.jface.text.TextViewer#handleVisibleDocumentChanged(org.eclipse.jface.text.DocumentEvent)
	 */
	protected final void handleVisibleDocumentChanged(final DocumentEvent event) {
		super.handleVisibleDocumentChanged(event);

		if (!fPositionList.isEmpty()) {

			try {

				final IDocument document = event.getDocument();
				final LinkedModeModel model = new LinkedModeModel();

				final String category = CATEGORY_AUTO_EDIT;
				if (!document.containsPositionCategory(category)) {

					document.addPositionCategory(category);
					document.addPositionUpdater(fAutoEditUpdater);

					model.addLinkingListener(new ILinkedModeListener() {

						public final void left(final LinkedModeModel dummy,
								final int flags) {

							if (document.containsPositionCategory(category)) {

								try {
									document.removePositionCategory(category);
								} catch (BadPositionCategoryException exception) {
									// Do nothing
								}
								document
										.removePositionUpdater(fAutoEditUpdater);
							}
						}

						public final void resume(final LinkedModeModel dummy,
								final int flags) {
						}

						public final void suspend(final LinkedModeModel dummy) {
						}
					});
				}

				LinkedPosition position = null;
				LinkedPositionGroup group = null;

				for (final Iterator iterator = fPositionList.iterator(); iterator
						.hasNext();) {

					position = (LinkedPosition) iterator.next();

					group = new LinkedPositionGroup();
					group.addPosition(position);

					model.addGroup(group);
				}
				model.forceInstall();

				final LinkedModeUI handler = new LinkedModeUI(model, this);

				final LinkedPosition exit = (LinkedPosition) fPositionList
						.getFirst();
				final LinkedPosition entry = (LinkedPosition) fPositionList
						.getLast();

				addSelectionChangedListener(new ISelectionChangedListener() {

					private boolean fHandled = false;

					public final void selectionChanged(
							final SelectionChangedEvent dummy) {

						if (!fHandled) {

							fHandled = true;

							removeSelectionChangedListener(this);
						}
					}
				});

				addPostSelectionChangedListener(new ISelectionChangedListener() {

					private boolean fHandled = false;

					public final void selectionChanged(
							final SelectionChangedEvent dummy) {

						if (!fHandled) {
							setSelectedRange(entry.offset, entry.length);

							invalidateTextPresentation(entry.offset, 0);

							fHandled = true;

							removePostSelectionChangedListener(this);
						}
					}
				});

				handler
						.setExitPosition(this, exit.offset, 0,
								LinkedPositionGroup.NO_STOP);

				handler.enter();

			} catch (BadLocationException exception) {
				// Do nothing
			} finally {
				fPositionList.clear();
			}
		}
	}
	
	protected void customizeDocumentCommand(DocumentCommand command) {
		if (isIgnoringAutoEditStrategies())
			return;

		List strategies = (List) selectContentTypePlugin(command.offset,
				fAutoIndentStrategies);
		if (strategies == null)
			return;

		IDocument document = getDocument();
		if (!strategies.isEmpty()) {

			fPositionList.clear();
			String originalCommandText = command.text;
			
			LinkedPosition[] result = null;
			IAutoEditStrategy strategy = null;

			for (final Iterator iterator = new ArrayList(strategies).iterator(); iterator
					.hasNext();) {

				strategy = (IAutoEditStrategy) iterator.next();
				strategy.customizeDocumentCommand(document, command);

				if ((strategy instanceof ILinkedModeEditStrategy) && originalCommandText.equals("\t")) {
					result = ((ILinkedModeEditStrategy) strategy)
							.getLinkedPositions();
					if (result != null && result.length > 0)
						fPositionList.addAll(Arrays.asList(result));
				}
			}
		}
	}
	
}
