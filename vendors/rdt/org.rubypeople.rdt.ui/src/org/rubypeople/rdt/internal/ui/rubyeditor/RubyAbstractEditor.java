package org.rubypeople.rdt.internal.ui.rubyeditor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentCommand;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentExtension4;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ISelectionValidator;
import org.eclipse.jface.text.ISynchronizable;
import org.eclipse.jface.text.ITextInputListener;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.ITextViewerExtension5;
import org.eclipse.jface.text.IWidgetTokenKeeper;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.link.LinkedModeModel;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.IAnnotationModelExtension;
import org.eclipse.jface.text.source.IOverviewRuler;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.ListenerList;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartService;
import org.eclipse.ui.IWindowListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.editors.text.EditorsUI;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditorPreferenceConstants;
import org.eclipse.ui.texteditor.ChainedPreferenceStore;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.SourceViewerDecorationSupport;
import org.eclipse.ui.views.contentoutline.ContentOutline;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.jruby.ast.RootNode;
import org.osgi.service.prefs.BackingStoreException;
import org.rubypeople.rdt.core.IImportContainer;
import org.rubypeople.rdt.core.IImportDeclaration;
import org.rubypeople.rdt.core.IMember;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IRubyProject;
import org.rubypeople.rdt.core.IRubyScript;
import org.rubypeople.rdt.core.ISourceFolder;
import org.rubypeople.rdt.core.ISourceFolderRoot;
import org.rubypeople.rdt.core.ISourceRange;
import org.rubypeople.rdt.core.ISourceReference;
import org.rubypeople.rdt.core.RubyCore;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.internal.core.ExternalRubyScript;
import org.rubypeople.rdt.internal.ui.RubyPlugin;
import org.rubypeople.rdt.internal.ui.rubyeditor.RubyEditor.ITextConverter;
import org.rubypeople.rdt.internal.ui.search.IOccurrencesFinder;
import org.rubypeople.rdt.internal.ui.search.OccurrencesFinder;
import org.rubypeople.rdt.internal.ui.text.ContentAssistPreference;
import org.rubypeople.rdt.internal.ui.text.IRubyPartitions;
import org.rubypeople.rdt.internal.ui.text.PreferencesAdapter;
import org.rubypeople.rdt.internal.ui.text.RubyPairMatcher;
import org.rubypeople.rdt.internal.ui.text.RubyWordFinder;
import org.rubypeople.rdt.internal.ui.viewsupport.ISelectionListenerWithAST;
import org.rubypeople.rdt.internal.ui.viewsupport.SelectionListenerWithASTManager;
import org.rubypeople.rdt.ui.PreferenceConstants;
import org.rubypeople.rdt.ui.RubyUI;
import org.rubypeople.rdt.ui.text.RubySourceViewerConfiguration;
import org.rubypeople.rdt.ui.text.RubyTextTools;

public abstract class RubyAbstractEditor extends TextEditor {

	private static final boolean CODE_ASSIST_DEBUG= "true".equalsIgnoreCase(Platform.getDebugOption("org.rubypeople.rdt.ui/debug/ResultCollector"));  //$NON-NLS-1$//$NON-NLS-2$
	
    protected RubyTextTools textTools;
    private ISourceReference reference;

    /** Outliner context menu Id */
    protected String fOutlinerContextMenuId;

    /** The selection changed listener */
    protected AbstractSelectionChangedListener fOutlineSelectionChangedListener = new OutlineSelectionChangedListener();
    private RubyOutlinePage fOutlinePage;
    
	/** Preference key for matching brackets */
	protected final static String MATCHING_BRACKETS=  PreferenceConstants.EDITOR_MATCHING_BRACKETS;
	/** Preference key for matching brackets color */
	protected final static String MATCHING_BRACKETS_COLOR=  PreferenceConstants.EDITOR_MATCHING_BRACKETS_COLOR;


	protected final static char[] BRACKETS= { '{', '}', '(', ')', '[', ']' };

	/** The editor's bracket matcher */
	protected RubyPairMatcher fBracketMatcher= new RubyPairMatcher(BRACKETS);
	
    
    /**
	 * Holds the current occurrence annotations.
	 * @since 3.0
	 */
	private Annotation[] fOccurrenceAnnotations= null;
	/**
	 * Tells whether all occurrences of the element at the
	 * current caret location are automatically marked in
	 * this editor.
	 * @since 3.0
	 */
	private boolean fMarkOccurrenceAnnotations;
	/**
	 * Tells whether the occurrence annotations are sticky
	 * i.e. whether they stay even if there's no valid Java
	 * element at the current caret position.
	 * Only valid if {@link #fMarkOccurrenceAnnotations} is <code>true</code>.
	 * @since 3.0
	 */
	private boolean fStickyOccurrenceAnnotations;
	/**
	 * Tells whether to mark type occurrences in this editor.
	 * Only valid if {@link #fMarkOccurrenceAnnotations} is <code>true</code>.
	 * @since 3.0
	 */
	private boolean fMarkTypeOccurrences;
	/**
	 * Tells whether to mark method occurrences in this editor.
	 * Only valid if {@link #fMarkOccurrenceAnnotations} is <code>true</code>.
	 * @since 3.0
	 */
	private boolean fMarkMethodOccurrences;
	/**
	 * Tells whether to mark constant occurrences in this editor.
	 * Only valid if {@link #fMarkOccurrenceAnnotations} is <code>true</code>.
	 * @since 3.0
	 */
	private boolean fMarkConstantOccurrences;
	/**
	 * Tells whether to mark field occurrences in this editor.
	 * Only valid if {@link #fMarkOccurrenceAnnotations} is <code>true</code>.
	 * @since 3.0
	 */
	private boolean fMarkFieldOccurrences;
	/**
	 * Tells whether to mark local variable occurrences in this editor.
	 * Only valid if {@link #fMarkOccurrenceAnnotations} is <code>true</code>.
	 * @since 3.0
	 */
	private boolean fMarkLocalVariableOccurrences;
	/**
	 * Tells whether to mark method exits in this editor.
	 * Only valid if {@link #fMarkOccurrenceAnnotations} is <code>true</code>.
	 * @since 3.0
	 */
	private boolean fMarkMethodExitPoints;

	/**
	 * The selection used when forcing occurrence marking
	 * through code.
	 * @since 3.0
	 */
	private ISelection fForcedMarkOccurrencesSelection;
	/**
	 * The document modification stamp at the time when the last
	 * occurrence marking took place.
	 * @since 3.1
	 */
	//TODO: Do I need to use this?
	private long fMarkOccurrenceModificationStamp= IDocumentExtension4.UNKNOWN_MODIFICATION_STAMP;

	/**
	 * The internal shell activation listener for updating occurrences.
	 */
	private ActivationListener fActivationListener= new ActivationListener();
	private ISelectionListenerWithAST fPostSelectionListenerWithAST;
	private OccurrencesFinderJob fOccurrencesFinderJob;
	/** The occurrences finder job canceler */
	private OccurrencesFinderJobCanceler fOccurrencesFinderJobCanceler;

	private IRegion fMarkOccurrenceTargetRegion;
    
	/**
	 * Creates and returns the preference store for this Ruby editor with the given input.
	 *
	 * @param input The editor input for which to create the preference store
	 * @return the preference store for this editor
	 *
	 * @since 0.9.0
	 */
	private IPreferenceStore createCombinedPreferenceStore(IEditorInput input) {
		List stores= new ArrayList(3);

		IRubyProject project= EditorUtility.getRubyProject(input);
		if (project != null) {
			stores.add(new EclipsePreferencesAdapter(new ProjectScope(project.getProject()), RubyCore.PLUGIN_ID));
		}

		stores.add(RubyPlugin.getDefault().getPreferenceStore());
		stores.add(new PreferencesAdapter(RubyCore.getPlugin().getPluginPreferences()));
		stores.add(EditorsUI.getPreferenceStore());

		return new ChainedPreferenceStore((IPreferenceStore[]) stores.toArray(new IPreferenceStore[stores.size()]));
	}

    /**
     * Informs the editor that its outliner has been closed.
     */
    public void outlinePageClosed() {
        if (fOutlinePage != null) {
            fOutlineSelectionChangedListener.uninstall(fOutlinePage);
            fOutlinePage = null;
            resetHighlightRange();
        }
    }
    
	protected void configureSourceViewerDecorationSupport(SourceViewerDecorationSupport support) {
		support.setCharacterPairMatcher(fBracketMatcher);
		support.setMatchingCharacterPainterPreferenceKeys(MATCHING_BRACKETS, MATCHING_BRACKETS_COLOR);

		super.configureSourceViewerDecorationSupport(support);
	}
	
    protected ISourceViewer createSourceViewer(Composite parent, IVerticalRuler ruler, int styles) {
        fAnnotationAccess = createAnnotationAccess();
        fOverviewRuler = createOverviewRuler(getSharedColors());

        IPreferenceStore store= getPreferenceStore();
        ISourceViewer viewer = createRubySourceViewer(parent, ruler, getOverviewRuler(), isOverviewRulerVisible(), styles, store);
        // ensure decoration support has been created and configured.
        getSourceViewerDecorationSupport(viewer);
        return viewer;
    }

	protected ISourceViewer createRubySourceViewer(Composite parent, IVerticalRuler verticalRuler, IOverviewRuler overviewRuler, boolean isOverviewRulerVisible, int styles, IPreferenceStore store) {
		return new AdaptedSourceViewer(parent, verticalRuler, overviewRuler,
				isOverviewRulerVisible, styles, store);
	}

    /**
     * Sets the outliner's context menu ID.
     * 
     * @param menuId
     *            the menu ID
     */
    protected void setOutlinerContextMenuId(String menuId) {
        fOutlinerContextMenuId = menuId;
    }

    protected void initializeEditor() {
        super.initializeEditor();
        IPreferenceStore store= createCombinedPreferenceStore(null);
		setPreferenceStore(store);
		RubyTextTools textTools= RubyPlugin.getDefault().getRubyTextTools();
        setSourceViewerConfiguration(new RubySourceViewerConfiguration(textTools.getColorManager(), store, this, IRubyPartitions.RUBY_PARTITIONING));
        fMarkOccurrenceAnnotations= store.getBoolean(PreferenceConstants.EDITOR_MARK_OCCURRENCES);
        fStickyOccurrenceAnnotations= store.getBoolean(PreferenceConstants.EDITOR_STICKY_OCCURRENCES);
        fMarkTypeOccurrences= store.getBoolean(PreferenceConstants.EDITOR_MARK_TYPE_OCCURRENCES);
        fMarkMethodOccurrences= store.getBoolean(PreferenceConstants.EDITOR_MARK_METHOD_OCCURRENCES);
        fMarkConstantOccurrences= store.getBoolean(PreferenceConstants.EDITOR_MARK_CONSTANT_OCCURRENCES);
        fMarkFieldOccurrences= store.getBoolean(PreferenceConstants.EDITOR_MARK_FIELD_OCCURRENCES);
        fMarkLocalVariableOccurrences= store.getBoolean(PreferenceConstants.EDITOR_MARK_LOCAL_VARIABLE_OCCURRENCES);
        fMarkMethodExitPoints= store.getBoolean(PreferenceConstants.EDITOR_MARK_METHOD_EXIT_POINTS);
    }
    
	/*
	 * @see org.eclipse.ui.texteditor.AbstractTextEditor#setPreferenceStore(org.eclipse.jface.preference.IPreferenceStore)
	 * @since 0.9.0
	 */
	protected void setPreferenceStore(IPreferenceStore store) {
		super.setPreferenceStore(store);
		if (getSourceViewerConfiguration() instanceof RubySourceViewerConfiguration) {
			RubyTextTools textTools= RubyPlugin.getDefault().getRubyTextTools();
			setSourceViewerConfiguration(new RubySourceViewerConfiguration(textTools.getColorManager(), store, this, IRubyPartitions.RUBY_PARTITIONING));
		}
		if (getSourceViewer() instanceof RubySourceViewer)
			((RubySourceViewer)getSourceViewer()).setPreferenceStore(store);
	}

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.texteditor.ExtendedTextEditor#dispose()
     */
    public void dispose() {
        super.dispose();
        // cancel possible running computation
		fMarkOccurrenceAnnotations= false;
		uninstallOccurrencesFinder();

		if (fActivationListener != null) {
			PlatformUI.getWorkbench().removeWindowListener(fActivationListener);
			fActivationListener= null;
		}
    }

    public Object getAdapter(Class required) {
        if (IContentOutlinePage.class.equals(required)) {
            if (fOutlinePage == null) fOutlinePage = createRubyOutlinePage();
            return fOutlinePage;
        }

        return super.getAdapter(required);
    }
    
    public void createPartControl(Composite parent) {
    	super.createPartControl(parent);
    	
		if (fMarkOccurrenceAnnotations)
			installOccurrencesFinder(false);
    }

    protected RubyOutlinePage createRubyOutlinePage() {
        RubyOutlinePage outlinePage = new RubyOutlinePage(fOutlinerContextMenuId, this);
        fOutlineSelectionChangedListener.install(outlinePage);
        setOutlinePageInput(outlinePage, getEditorInput());
        return outlinePage;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.editors.text.TextEditor#doSetInput(org.eclipse.ui.IEditorInput)
     */
    protected void doSetInput(IEditorInput input) throws CoreException {  
    	// HACK Ugly code here in the beginning to handle Eclipse 3.2 and 3.3 opening external files. We wrap them into our own external ruby editor..
    	IPath path = null;
    	if (externalFileOpenedInEclipse32(input)) { // Eclipse 3.2 uses this editor input for external files...
    		try {
				Method method = input.getClass().getDeclaredMethod("getPath", new Class[0]);
				if (method != null) {
					path = (IPath) method.invoke(input, new Object[0]);
				}
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	} else if (externalFileOpenedInEclipse33(input)) { // Eclipse 3.3 uses this editor input for external files...
    		try {
				Method method = input.getClass().getDeclaredMethod("getURI", new Class[0]);
				if (method != null) {
					URI uri = (URI) method.invoke(input, new Object[0]);
					if (uri != null && uri.getScheme().equals("file")) {
						path = new Path(uri.getPath());
					}
				}
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    	if (path != null && (externalFileOpenedInEclipse32(input) || externalFileOpenedInEclipse33(input))) {
    		IProject[] projects = RubyCore.getRubyProjects();
    		if (projects != null && projects.length > 0) { // search for the file in one of the ruby projects loadpaths
    			IRubyProject proj = RubyCore.create(projects[0]);    		
        		ISourceFolderRoot root = proj.getSourceFolderRoot(path.removeLastSegments(1).toPortableString());
        		ISourceFolder folder = root.getSourceFolder("");
        		IRubyScript script = folder.getRubyScript(path.lastSegment());
        		// FIXME Check to see if it exists?
        		input = new RubyScriptEditorInput((ExternalRubyScript) script);
    		}
    	}
    	if (input instanceof IRubyScriptEditorInput) {
    		setDocumentProvider(RubyPlugin.getDefault().getExternalDocumentProvider());
    	} else {
    		setDocumentProvider(RubyPlugin.getDefault().getRubyDocumentProvider());
    	}
        super.doSetInput(input);
        setOutlinePageInput(fOutlinePage, input);
    }

	private boolean externalFileOpenedInEclipse33(IEditorInput input) {
		return input.getClass().getName().equals("org.eclipse.ui.ide.FileStoreEditorInput");
	}

	private boolean externalFileOpenedInEclipse32(IEditorInput input) {
		return input.getClass().getName().equals("org.eclipse.ui.internal.editors.text.JavaFileEditorInput");
	}

    protected void setOutlinePageInput(RubyOutlinePage page, IEditorInput input) {
		if (page == null)
			return;
		
		IRubyElement re= getInputRubyElement();
		if (re != null && re.exists())
			page.setInput(re);
		else
			page.setInput(null);
    }
    
    protected void handlePreferenceStoreChanged(PropertyChangeEvent event) {

		String property= event.getProperty();

		if (AbstractDecoratedTextEditorPreferenceConstants.EDITOR_TAB_WIDTH.equals(property)) {
			/*
			 * Ignore tab setting since we rely on the formatter preferences.
			 * We do this outside the try-finally block to avoid that EDITOR_TAB_WIDTH
			 * is handled by the sub-class (AbstractDecoratedTextEditor).
			 */
			return;
		}

		try {
			boolean newBooleanValue= false;
			Object newValue= event.getNewValue();
			if (newValue != null)
				newBooleanValue= Boolean.valueOf(newValue.toString()).booleanValue();
			if (PreferenceConstants.EDITOR_MARK_OCCURRENCES.equals(property)) {
				if (newBooleanValue != fMarkOccurrenceAnnotations) {
					fMarkOccurrenceAnnotations= newBooleanValue;
					if (!fMarkOccurrenceAnnotations)
						uninstallOccurrencesFinder();
					else
						installOccurrencesFinder(true);
				}
				return;
			}
			if (PreferenceConstants.EDITOR_MARK_TYPE_OCCURRENCES.equals(property)) {
				fMarkTypeOccurrences= newBooleanValue;
				return;
			}
			if (PreferenceConstants.EDITOR_MARK_METHOD_OCCURRENCES.equals(property)) {
				fMarkMethodOccurrences= newBooleanValue;
				return;
			}
			if (PreferenceConstants.EDITOR_MARK_CONSTANT_OCCURRENCES.equals(property)) {
				fMarkConstantOccurrences= newBooleanValue;
				return;
			}
			if (PreferenceConstants.EDITOR_MARK_FIELD_OCCURRENCES.equals(property)) {
				fMarkFieldOccurrences= newBooleanValue;
				return;
			}
			if (PreferenceConstants.EDITOR_MARK_LOCAL_VARIABLE_OCCURRENCES.equals(property)) {
				fMarkLocalVariableOccurrences= newBooleanValue;
				return;
			}
			if (PreferenceConstants.EDITOR_MARK_METHOD_EXIT_POINTS.equals(property)) {
				fMarkMethodExitPoints= newBooleanValue;
				return;
			}
			if (PreferenceConstants.EDITOR_STICKY_OCCURRENCES.equals(property)) {
				fStickyOccurrenceAnnotations= newBooleanValue;
				return;
			}
			AdaptedSourceViewer sourceViewer= (AdaptedSourceViewer) getSourceViewer();
			if (sourceViewer == null)
				return;

			((RubySourceViewerConfiguration)getSourceViewerConfiguration()).handlePropertyChangeEvent(event);
		
			IContentAssistant c= sourceViewer.getContentAssistant();
			if (c instanceof ContentAssistant)
				ContentAssistPreference.changeConfiguration((ContentAssistant) c, getPreferenceStore(), event);


		} finally {
			super.handlePreferenceStoreChanged(event);
		}
		
		if (AbstractDecoratedTextEditorPreferenceConstants.SHOW_RANGE_INDICATOR.equals(property)) {
			// superclass already installed the range indicator
			Object newValue= event.getNewValue();
			ISourceViewer viewer= getSourceViewer();
			if (newValue != null && viewer != null) {
				if (Boolean.valueOf(newValue.toString()).booleanValue()) {
					// adjust the highlightrange in order to get the magnet right after changing the selection
					Point selection= viewer.getSelectedRange();
					adjustHighlightRange(selection.x, selection.y);
				}
			}
		}		
    }
    
	/**
	 * Returns the Ruby element wrapped by this editors input.
	 *
	 * @return the Ruby element wrapped by this editors input.
	 * @since 3.0
	 */
	protected IRubyElement getInputRubyElement() {
		IEditorInput editorInput= getEditorInput();
		if (editorInput == null)
			return null;
		return RubyUI.getEditorInputRubyElement(getEditorInput());
	}

    protected void handleOutlinePageSelection(SelectionChangedEvent event) {
        StructuredSelection selection = (StructuredSelection) event.getSelection();
        Iterator iter = ((IStructuredSelection) selection).iterator();
        while (iter.hasNext()) {
            Object o = iter.next();
            if (o instanceof ISourceReference) {
                reference = (ISourceReference) o;
                break;
            }
        }
        if (!isActivePart() && RubyPlugin.getActivePage() != null)
            RubyPlugin.getActivePage().bringToTop(this);

        // setSelection(reference, !isActivePart());
        setSelection(reference, true);
    }

    protected boolean isActivePart() {
        IWorkbenchPart part = getActivePart();
        return part != null && part.equals(this);
    }

    private IWorkbenchPart getActivePart() {
        IWorkbenchWindow window = getSite().getWorkbenchWindow();
        IPartService service = window.getPartService();
        IWorkbenchPart part = service.getActivePart();
        return part;
    }

    public void setSelection(IRubyElement element) {

        if (element == null || element instanceof IRubyScript) {
            /*
             * If the element is an IRubyScript this unit is either the input of
             * this editor or not being displayed. In both cases, nothing should
             * happened. (http://dev.eclipse.org/bugs/show_bug.cgi?id=5128)
             */
            return;
        }

        IRubyElement corresponding = getCorrespondingElement(element);
        if (corresponding instanceof ISourceReference) {
            ISourceReference reference = (ISourceReference) corresponding;
            // set highlight range
            setSelection(reference, true);
            // set outliner selection
            if (fOutlinePage != null) {
                fOutlineSelectionChangedListener.uninstall(fOutlinePage);
                fOutlinePage.select(reference);
                fOutlineSelectionChangedListener.install(fOutlinePage);
            }
        }
    }

    protected IRubyElement getCorrespondingElement(IRubyElement element) {
        // TODO: With new working copy story: original == working copy.
        // Note that the previous code could result in a reconcile as side
        // effect. Should check if that
        // is still required.
        return element;
    }

    /**
     * Synchronizes the outliner selection with the given element position in
     * the editor.
     * 
     * @param element
     *            the java element to select
     * @param checkIfOutlinePageActive
     *            <code>true</code> if check for active outline page needs to
     *            be done
     */
    protected void synchronizeOutlinePage(ISourceReference element, boolean checkIfOutlinePageActive) {
        if (fOutlinePage != null && element != null
                && !(checkIfOutlinePageActive && isRubyOutlinePageActive())) {
            fOutlineSelectionChangedListener.uninstall(fOutlinePage);
            fOutlinePage.select(element);
            fOutlineSelectionChangedListener.install(fOutlinePage);
        }
    }

    private boolean isRubyOutlinePageActive() {
        IWorkbenchPart part = getActivePart();
        return part instanceof ContentOutline
                && ((ContentOutline) part).getCurrentPage() == fOutlinePage;
    }

    protected void setSelection(ISourceReference reference, boolean moveCursor) {
        if (getSelectionProvider() == null) return;

        ISelection selection = getSelectionProvider().getSelection();
        if (selection instanceof TextSelection) {
            TextSelection textSelection = (TextSelection) selection;
            // PR 39995: [navigation] Forward history cleared after going back
            // in navigation history:
            // mark only in navigation history if the cursor is being moved
            // (which it isn't if
            // this is called from a PostSelectionEvent that should only update
            // the magnet)
            if (moveCursor && (textSelection.getOffset() != 0 || textSelection.getLength() != 0))
                markInNavigationHistory();
        }

        if (reference != null) {

            StyledText textWidget = null;

            ISourceViewer sourceViewer = getSourceViewer();
            if (sourceViewer != null) textWidget = sourceViewer.getTextWidget();

            if (textWidget == null) return;

            try {
                ISourceRange range = null;
                // if (reference instanceof ILocalVariable) {
                // IRubyElement je = ((ILocalVariable) reference).getParent();
                // if (je instanceof ISourceReference) range =
                // ((ISourceReference) je).getSourceRange();
                // } else
                range = reference.getSourceRange();

                if (range == null) return;

                int offset = range.getOffset();
                int length = range.getLength();

                if (offset < 0 || length < 0) return;

                setHighlightRange(offset, length, moveCursor);

                if (!moveCursor) return;

                offset = -1;
                length = -1;

                if (reference instanceof IMember) {
                    range = ((IMember) reference).getNameRange();
                    if (range != null) {
                        offset = range.getOffset();
                        length = range.getLength();
                    }
                    // } else if (reference instanceof ILocalVariable) {
                    // range= ((ILocalVariable)reference).getNameRange();
                    // if (range != null) {
                    // offset= range.getOffset();
                    // length= range.getLength();
                    // }
                } else if (reference instanceof IImportDeclaration) {
                    String name = ((IImportDeclaration) reference).getElementName();
                    if (name != null && name.length() > 0) {
                        String content = reference.getSource();
                        if (content != null) {
                            offset = range.getOffset() + content.indexOf(name);
                            length = name.length();
                        }
                    }
                }

                if (offset > -1 && length > 0) {

                    try {
                        textWidget.setRedraw(false);
                        sourceViewer.revealRange(offset, length);
                        sourceViewer.setSelectedRange(offset, length);
                    } finally {
                        textWidget.setRedraw(true);
                    }

                    markInNavigationHistory();
                }

            } catch (RubyModelException x) {
            } catch (IllegalArgumentException x) {
            }

        } else if (moveCursor) {
            resetHighlightRange();
            markInNavigationHistory();
        }
    }

    protected boolean affectsTextPresentation(PropertyChangeEvent event) {
    	return ((RubySourceViewerConfiguration)getSourceViewerConfiguration()).affectsTextPresentation(event) || super.affectsTextPresentation(event);
    }

    protected void doSelectionChanged(SelectionChangedEvent event) {

        ISourceReference reference = null;

        ISelection selection = event.getSelection();
        Iterator iter = ((IStructuredSelection) selection).iterator();
        while (iter.hasNext()) {
            Object o = iter.next();
            if (o instanceof ISourceReference) {
                reference = (ISourceReference) o;
                break;
            }
        }
        if (!isActivePart() && RubyPlugin.getActivePage() != null)
            RubyPlugin.getActivePage().bringToTop(this);

        setSelection(reference, !isActivePart());
    }

    class OutlineSelectionChangedListener extends AbstractSelectionChangedListener {

        public void selectionChanged(SelectionChangedEvent event) {
            boolean isLinkingEnabled = PreferenceConstants.getPreferenceStore().getBoolean(PreferenceConstants.EDITOR_SYNC_OUTLINE_ON_CURSOR_MOVE); 
            if (isLinkingEnabled) {
                doSelectionChanged(event);
            }
        }
    }

    /**
     * Computes and returns the source reference that includes the caret and
     * serves as provider for the outline page selection and the editor range
     * indication.
     *
     * @return the computed source reference
     * @since 3.0
     */
    protected ISourceReference computeHighlightRangeSourceReference() {
        ISourceViewer sourceViewer= getSourceViewer();
        if (sourceViewer == null)
            return null;

        StyledText styledText= sourceViewer.getTextWidget();
        if (styledText == null)
            return null;

        int caret= 0;
        if (sourceViewer instanceof ITextViewerExtension5) {
            ITextViewerExtension5 extension= (ITextViewerExtension5)sourceViewer;
            caret= extension.widgetOffset2ModelOffset(styledText.getCaretOffset());
        } else {
            int offset= sourceViewer.getVisibleRegion().getOffset();
            caret= offset + styledText.getCaretOffset();
        }

        IRubyElement element= getElementAt(caret, false);

        if ( !(element instanceof ISourceReference))
            return null;

        if (element.getElementType() == IRubyElement.IMPORT_DECLARATION) {

            IImportDeclaration declaration= (IImportDeclaration) element;
            IImportContainer container= (IImportContainer) declaration.getParent();
            ISourceRange srcRange= null;

            try {
                srcRange= container.getSourceRange();
            } catch (RubyModelException e) {
            }

            if (srcRange != null && srcRange.getOffset() == caret)
                return container;
        }

        return (ISourceReference) element;
    }

    protected abstract IRubyElement getElementAt(int caret, boolean b);

    protected abstract IRubyElement getElementAt(int offset);

	public final ISourceViewer getViewer() {
		return getSourceViewer();
	}
    
    /**
	 * Adapts an options {@link IEclipsePreferences} to {@link org.eclipse.jface.preference.IPreferenceStore}.
	 * <p>
	 * This preference store is read-only i.e. write access
	 * throws an {@link java.lang.UnsupportedOperationException}.
	 * </p>
	 *
	 * @since 3.1
	 */
	private static class EclipsePreferencesAdapter implements IPreferenceStore {

		/**
		 * Preference change listener. Listens for events preferences
		 * fires a {@link org.eclipse.jface.util.PropertyChangeEvent}
		 * on this adapter with arguments from the received event.
		 */
		private class PreferenceChangeListener implements IEclipsePreferences.IPreferenceChangeListener {

			/**
			 * {@inheritDoc}
			 */
			public void preferenceChange(final IEclipsePreferences.PreferenceChangeEvent event) {
				if (Display.getCurrent() == null) {
					Display.getDefault().asyncExec(new Runnable() {
						public void run() {
							firePropertyChangeEvent(event.getKey(), event.getOldValue(), event.getNewValue());
						}
					});
				} else {
					firePropertyChangeEvent(event.getKey(), event.getOldValue(), event.getNewValue());
				}
			}
		}

		// TODO When we move to Eclipse 3.2 change ListenerList to eclipse.core.runtime.ListenerList
		/** Listeners on on this adapter */
		private ListenerList fListeners= new ListenerList();

		/** Listener on the node */
		private IEclipsePreferences.IPreferenceChangeListener fListener= new PreferenceChangeListener();

		/** wrapped node */
		private final IScopeContext fContext;
		private final String fQualifier;

		/**
		 * Initialize with the node to wrap
		 *
		 * @param context The context to access
		 */
		public EclipsePreferencesAdapter(IScopeContext context, String qualifier) {
			fContext= context;
			fQualifier= qualifier;
		}

		private IEclipsePreferences getNode() {
			return fContext.getNode(fQualifier);
		}

		/**
		 * {@inheritDoc}
		 */
		public void addPropertyChangeListener(IPropertyChangeListener listener) {
			if (fListeners.size() == 0)
				getNode().addPreferenceChangeListener(fListener);
			fListeners.add(listener);
		}

		/**
		 * {@inheritDoc}
		 */
		public void removePropertyChangeListener(IPropertyChangeListener listener) {
			fListeners.remove(listener);
			if (fListeners.size() == 0) {
				getNode().removePreferenceChangeListener(fListener);
			}
		}

		/**
		 * {@inheritDoc}
		 */
		public boolean contains(String name) {
			return getNode().get(name, null) != null;
		}

		/**
		 * {@inheritDoc}
		 */
		public void firePropertyChangeEvent(String name, Object oldValue, Object newValue) {
			PropertyChangeEvent event= new PropertyChangeEvent(this, name, oldValue, newValue);
			Object[] listeners= fListeners.getListeners();
			for (int i= 0; i < listeners.length; i++)
				((IPropertyChangeListener) listeners[i]).propertyChange(event);
		}

		/**
		 * {@inheritDoc}
		 */
		public boolean getBoolean(String name) {
			return getNode().getBoolean(name, BOOLEAN_DEFAULT_DEFAULT);
		}

		/**
		 * {@inheritDoc}
		 */
		public boolean getDefaultBoolean(String name) {
			return BOOLEAN_DEFAULT_DEFAULT;
		}

		/**
		 * {@inheritDoc}
		 */
		public double getDefaultDouble(String name) {
			return DOUBLE_DEFAULT_DEFAULT;
		}

		/**
		 * {@inheritDoc}
		 */
		public float getDefaultFloat(String name) {
			return FLOAT_DEFAULT_DEFAULT;
		}

		/**
		 * {@inheritDoc}
		 */
		public int getDefaultInt(String name) {
			return INT_DEFAULT_DEFAULT;
		}

		/**
		 * {@inheritDoc}
		 */
		public long getDefaultLong(String name) {
			return LONG_DEFAULT_DEFAULT;
		}

		/**
		 * {@inheritDoc}
		 */
		public String getDefaultString(String name) {
			return STRING_DEFAULT_DEFAULT;
		}

		/**
		 * {@inheritDoc}
		 */
		public double getDouble(String name) {
			return getNode().getDouble(name, DOUBLE_DEFAULT_DEFAULT);
		}

		/**
		 * {@inheritDoc}
		 */
		public float getFloat(String name) {
			return getNode().getFloat(name, FLOAT_DEFAULT_DEFAULT);
		}

		/**
		 * {@inheritDoc}
		 */
		public int getInt(String name) {
			return getNode().getInt(name, INT_DEFAULT_DEFAULT);
		}

		/**
		 * {@inheritDoc}
		 */
		public long getLong(String name) {
			return getNode().getLong(name, LONG_DEFAULT_DEFAULT);
		}

		/**
		 * {@inheritDoc}
		 */
		public String getString(String name) {
			return getNode().get(name, STRING_DEFAULT_DEFAULT);
		}

		/**
		 * {@inheritDoc}
		 */
		public boolean isDefault(String name) {
			return false;
		}

		/**
		 * {@inheritDoc}
		 */
		public boolean needsSaving() {
			try {
				return getNode().keys().length > 0;
			} catch (BackingStoreException e) {
				// ignore
			}
			return true;
		}

		/**
		 * {@inheritDoc}
		 */
		public void putValue(String name, String value) {
			throw new UnsupportedOperationException();
		}

		/**
		 * {@inheritDoc}
		 */
		public void setDefault(String name, double value) {
			throw new UnsupportedOperationException();
		}

		/**
		 * {@inheritDoc}
		 */
		public void setDefault(String name, float value) {
			throw new UnsupportedOperationException();
		}

		/**
		 * {@inheritDoc}
		 */
		public void setDefault(String name, int value) {
			throw new UnsupportedOperationException();
		}

		/**
		 * {@inheritDoc}
		 */
		public void setDefault(String name, long value) {
			throw new UnsupportedOperationException();
		}

		/**
		 * {@inheritDoc}
		 */
		public void setDefault(String name, String defaultObject) {
			throw new UnsupportedOperationException();
		}

		/**
		 * {@inheritDoc}
		 */
		public void setDefault(String name, boolean value) {
			throw new UnsupportedOperationException();
		}

		/**
		 * {@inheritDoc}
		 */
		public void setToDefault(String name) {
			throw new UnsupportedOperationException();
		}

		/**
		 * {@inheritDoc}
		 */
		public void setValue(String name, double value) {
			throw new UnsupportedOperationException();
		}

		/**
		 * {@inheritDoc}
		 */
		public void setValue(String name, float value) {
			throw new UnsupportedOperationException();
		}

		/**
		 * {@inheritDoc}
		 */
		public void setValue(String name, int value) {
			throw new UnsupportedOperationException();
		}

		/**
		 * {@inheritDoc}
		 */
		public void setValue(String name, long value) {
			throw new UnsupportedOperationException();
		}

		/**
		 * {@inheritDoc}
		 */
		public void setValue(String name, String value) {
			throw new UnsupportedOperationException();
		}

		/**
		 * {@inheritDoc}
		 */
		public void setValue(String name, boolean value) {
			throw new UnsupportedOperationException();
		}
	}
	
	class AdaptedSourceViewer extends RubySourceViewer  {

		private List fTextConverters;
		private boolean fIgnoreTextConverters= false;

		public AdaptedSourceViewer(Composite parent, IVerticalRuler verticalRuler, IOverviewRuler overviewRuler, boolean showAnnotationsOverview, int styles, IPreferenceStore store) {
			super(parent, verticalRuler, overviewRuler, showAnnotationsOverview, styles, store);
		}

		public IContentAssistant getContentAssistant() {
			return fContentAssistant;
		}
		
		public void addTextConverter(ITextConverter textConverter) {
			if (fTextConverters == null) {
				fTextConverters= new ArrayList(1);
				fTextConverters.add(textConverter);
			} else if (!fTextConverters.contains(textConverter))
				fTextConverters.add(textConverter);
		}

		public void removeTextConverter(ITextConverter textConverter) {
			if (fTextConverters != null) {
				fTextConverters.remove(textConverter);
				if (fTextConverters.size() == 0)
					fTextConverters= null;
			}
		}

		/*
		 * @see TextViewer#customizeDocumentCommand(DocumentCommand)
		 */
		protected void customizeDocumentCommand(DocumentCommand command) {
			super.customizeDocumentCommand(command);
			if (!fIgnoreTextConverters && fTextConverters != null) {
				for (Iterator e = fTextConverters.iterator(); e.hasNext();)
					((ITextConverter) e.next()).customizeDocumentCommand(getDocument(), command);
			}
		}
		
//		 http://dev.eclipse.org/bugs/show_bug.cgi?id=19270
		public void updateIndentationPrefixes() {
			SourceViewerConfiguration configuration= getSourceViewerConfiguration();
			String[] types= configuration.getConfiguredContentTypes(this);
			for (int i= 0; i < types.length; i++) {
				String[] prefixes= configuration.getIndentPrefixes(this, types[i]);
				if (prefixes != null && prefixes.length > 0)
					setIndentPrefixes(prefixes, types[i]);
			}
		}

		/*
		 * @see IWidgetTokenOwner#requestWidgetToken(IWidgetTokenKeeper)
		 */
		public boolean requestWidgetToken(IWidgetTokenKeeper requester) {
			if (PlatformUI.getWorkbench().getHelpSystem().isContextHelpDisplayed())
				return false;
			return super.requestWidgetToken(requester);
		}

		/*
		 * @see IWidgetTokenOwnerExtension#requestWidgetToken(IWidgetTokenKeeper, int)
		 * @since 3.0
		 */
		public boolean requestWidgetToken(IWidgetTokenKeeper requester, int priority) {
			if (PlatformUI.getWorkbench().getHelpSystem().isContextHelpDisplayed())
				return false;
			return super.requestWidgetToken(requester, priority);
		}
		
		/*
		 * @see ITextOperationTarget#doOperation(int)
		 */
		public void doOperation(int operation) {

			if (getTextWidget() == null)
				return;

			switch (operation) {
				case CONTENTASSIST_PROPOSALS:
					long time= CODE_ASSIST_DEBUG ? System.currentTimeMillis() : 0;
					String msg= fContentAssistant.showPossibleCompletions();
					if (CODE_ASSIST_DEBUG) {
						long delta= System.currentTimeMillis() - time;
						System.err.println("Code Assist (total): " + delta); //$NON-NLS-1$
					}
					setStatusLineErrorMessage(msg);
					return;
				case QUICK_ASSIST:
					/*
					 * XXX: We can get rid of this once the SourceViewer has a way to update the status line
					 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=133787
					 */
					msg= fQuickAssistAssistant.showPossibleQuickAssists();
					setStatusLineErrorMessage(msg);
					return;
				case UNDO:
					fIgnoreTextConverters= true;
					super.doOperation(operation);
					fIgnoreTextConverters= false;
					return;
				case REDO:
					fIgnoreTextConverters= true;
					super.doOperation(operation);
					fIgnoreTextConverters= false;
					return;
			}

			super.doOperation(operation);
		}
	}
	/**
	 * Internal activation listener.
	 * @since 3.0
	 */
	private class ActivationListener implements IWindowListener {

		/*
		 * @see org.eclipse.ui.IWindowListener#windowActivated(org.eclipse.ui.IWorkbenchWindow)
		 * @since 3.1
		 */
		public void windowActivated(IWorkbenchWindow window) {
			if (window == getEditorSite().getWorkbenchWindow() && fMarkOccurrenceAnnotations && isActivePart()) {
				fForcedMarkOccurrencesSelection= getSelectionProvider().getSelection();
 				updateOccurrenceAnnotations((ITextSelection)fForcedMarkOccurrencesSelection, RubyPlugin.getDefault().getASTProvider().getAST(getInputRubyElement(), ASTProvider.WAIT_NO, getProgressMonitor()));
			}
		}

		/*
		 * @see org.eclipse.ui.IWindowListener#windowDeactivated(org.eclipse.ui.IWorkbenchWindow)
		 * @since 3.1
		 */
		public void windowDeactivated(IWorkbenchWindow window) {
			if (window == getEditorSite().getWorkbenchWindow() && fMarkOccurrenceAnnotations && isActivePart())
				removeOccurrenceAnnotations();
		}

		/*
		 * @see org.eclipse.ui.IWindowListener#windowClosed(org.eclipse.ui.IWorkbenchWindow)
		 * @since 3.1
		 */
		public void windowClosed(IWorkbenchWindow window) {
		}

		/*
		 * @see org.eclipse.ui.IWindowListener#windowOpened(org.eclipse.ui.IWorkbenchWindow)
		 * @since 3.1
		 */
		public void windowOpened(IWorkbenchWindow window) {
		}
	}
	
	/**
	 * Cancels the occurrences finder job upon document changes.
	 *
	 * @since 3.0
	 */
	class OccurrencesFinderJobCanceler implements IDocumentListener, ITextInputListener {

		public void install() {
			ISourceViewer sourceViewer= getSourceViewer();
			if (sourceViewer == null)
				return;

			StyledText text= sourceViewer.getTextWidget();
			if (text == null || text.isDisposed())
				return;

			sourceViewer.addTextInputListener(this);

			IDocument document= sourceViewer.getDocument();
			if (document != null)
				document.addDocumentListener(this);
		}

		public void uninstall() {
			ISourceViewer sourceViewer= getSourceViewer();
			if (sourceViewer != null)
				sourceViewer.removeTextInputListener(this);

			IDocumentProvider documentProvider= getDocumentProvider();
			if (documentProvider != null) {
				IDocument document= documentProvider.getDocument(getEditorInput());
				if (document != null)
					document.removeDocumentListener(this);
			}
		}


		/*
		 * @see org.eclipse.jface.text.IDocumentListener#documentAboutToBeChanged(org.eclipse.jface.text.DocumentEvent)
		 */
		public void documentAboutToBeChanged(DocumentEvent event) {
			if (fOccurrencesFinderJob != null)
				fOccurrencesFinderJob.doCancel();
		}

		/*
		 * @see org.eclipse.jface.text.IDocumentListener#documentChanged(org.eclipse.jface.text.DocumentEvent)
		 */
		public void documentChanged(DocumentEvent event) {
		}

		/*
		 * @see org.eclipse.jface.text.ITextInputListener#inputDocumentAboutToBeChanged(org.eclipse.jface.text.IDocument, org.eclipse.jface.text.IDocument)
		 */
		public void inputDocumentAboutToBeChanged(IDocument oldInput, IDocument newInput) {
			if (oldInput == null)
				return;

			oldInput.removeDocumentListener(this);
		}

		/*
		 * @see org.eclipse.jface.text.ITextInputListener#inputDocumentChanged(org.eclipse.jface.text.IDocument, org.eclipse.jface.text.IDocument)
		 */
		public void inputDocumentChanged(IDocument oldInput, IDocument newInput) {
			if (newInput == null)
				return;
			newInput.addDocumentListener(this);
		}
	}
	
	/**
	 * Finds and marks occurrence annotations.
	 *
	 * @since 3.0
	 */
	class OccurrencesFinderJob extends Job {

		private IDocument fDocument;
		private ISelection fSelection;
		private ISelectionValidator fPostSelectionValidator;
		private boolean fCanceled= false;
		private IProgressMonitor fProgressMonitor;
		private Position[] fPositions;

		public OccurrencesFinderJob(IDocument document, Position[] positions, ISelection selection) {
			//TODO: Refactor job name to resource string somewhere
			super("OccurrencesFinderJob");
			fDocument= document;
			fSelection= selection;
			fPositions= positions;

			if (getSelectionProvider() instanceof ISelectionValidator)
				fPostSelectionValidator= (ISelectionValidator)getSelectionProvider();
		}

		// cannot use cancel() because it is declared final
		void doCancel() {
			fCanceled= true;
			cancel();
		}

		private boolean isCanceled() {
			return fCanceled || fProgressMonitor.isCanceled()
				||  fPostSelectionValidator != null && !(fPostSelectionValidator.isValid(fSelection) || fForcedMarkOccurrencesSelection == fSelection)
				|| LinkedModeModel.hasInstalledModel(fDocument);
		}

		/*
		 * @see Job#run(org.eclipse.core.runtime.IProgressMonitor)
		 */
		public IStatus run(IProgressMonitor progressMonitor) {

			fProgressMonitor= progressMonitor;

			if (isCanceled())
				return Status.CANCEL_STATUS;

			ITextViewer textViewer= getViewer();
			if (textViewer == null)
				return Status.CANCEL_STATUS;

			IDocument document= textViewer.getDocument();
			if (document == null)
				return Status.CANCEL_STATUS;

			IDocumentProvider documentProvider= getDocumentProvider();
			if (documentProvider == null)
				return Status.CANCEL_STATUS;

			IAnnotationModel annotationModel= documentProvider.getAnnotationModel(getEditorInput());
			if (annotationModel == null)
				return Status.CANCEL_STATUS;

			// Add occurrence annotations
			int length= fPositions.length;
			Map annotationMap= new HashMap(length);
			for (int i= 0; i < length; i++) {

				if (isCanceled())
					return Status.CANCEL_STATUS;

				String message;
				Position position= fPositions[i];

				// Create & add annotation
				try {
					message= document.get(position.offset, position.length);
				} catch (BadLocationException ex) {
					// Skip this match
					continue;
				}
				annotationMap.put(
						new Annotation("org.rubypeople.rdt.ui.occurrences", false, message), //$NON-NLS-1$
						position);
			}

			if (isCanceled())
				return Status.CANCEL_STATUS;

			synchronized (getLockObject(annotationModel)) {
				if (annotationModel instanceof IAnnotationModelExtension) {
					((IAnnotationModelExtension)annotationModel).replaceAnnotations(fOccurrenceAnnotations, annotationMap);
				} else {
					removeOccurrenceAnnotations();
					Iterator iter= annotationMap.entrySet().iterator();
					while (iter.hasNext()) {
						Map.Entry mapEntry= (Map.Entry)iter.next();
						annotationModel.addAnnotation((Annotation)mapEntry.getKey(), (Position)mapEntry.getValue());
					}
				}
				fOccurrenceAnnotations= (Annotation[])annotationMap.keySet().toArray(new Annotation[annotationMap.keySet().size()]);
			}

			return Status.OK_STATUS;
		}
	}

	/**
	 * Updates the occurrences annotations based
	 * on the current selection.
	 *
	 * @param selection the text selection
	 * @param ast 
	 */
	protected void updateOccurrenceAnnotations(ITextSelection selection, RootNode ast) {

		if (fOccurrencesFinderJob != null)
			fOccurrencesFinderJob.cancel();

		if (!fMarkOccurrenceAnnotations)
			return;

		if (selection == null)
			return;
		
		IDocument document= getDocumentProvider().getDocument(getEditorInput());
		if (document == null)
			return;

		if (document instanceof IDocumentExtension4) {
			int offset= selection.getOffset();
			long currentModificationStamp= ((IDocumentExtension4)document).getModificationStamp();
			if (fMarkOccurrenceTargetRegion != null && currentModificationStamp == fMarkOccurrenceModificationStamp) {
				if (fMarkOccurrenceTargetRegion.getOffset() <= offset && offset <= fMarkOccurrenceTargetRegion.getOffset() + fMarkOccurrenceTargetRegion.getLength())
					return;
			}
			fMarkOccurrenceTargetRegion= RubyWordFinder.findWord(document, offset);
			fMarkOccurrenceModificationStamp= currentModificationStamp;
		}
		
		
		String source = document.get();

		// Search for occurrences
		OccurrencesFinder finder = new OccurrencesFinder();
		finder.setFMarkConstantOccurrences(fMarkConstantOccurrences);
		finder.setFMarkFieldOccurrences(fMarkFieldOccurrences);
		finder.setFMarkLocalVariableOccurrences(fMarkLocalVariableOccurrences);
		finder.setFMarkMethodExitPoints(fMarkMethodExitPoints);
		finder.setFMarkMethodOccurrences(fMarkMethodOccurrences);
		finder.setFMarkOccurrenceAnnotations(fMarkOccurrenceAnnotations);
		finder.setFMarkTypeOccurrences(fMarkTypeOccurrences);
		finder.setFStickyOccurrenceAnnotations(fStickyOccurrenceAnnotations);
		finder.initialize(ast, selection.getOffset(), selection.getLength());		
		List<Position> matches = finder.perform();

		if (matches == null || matches.isEmpty()) {
			if (!fStickyOccurrenceAnnotations) {
				removeOccurrenceAnnotations();
			}
			return;
		} 
		
		// Convert to array
		Position[] positions = matches.toArray(new Position[matches.size()]);

		// Mark occurrences
		fOccurrencesFinderJob= new OccurrencesFinderJob(document, positions, selection);
//		fOccurrencesFinderJob.setPriority(Job.DECORATE);
//		fOccurrencesFinderJob.setSystem(true);
//		fOccurrencesFinderJob.schedule();
		fOccurrencesFinderJob.run(new NullProgressMonitor());
	}
	
	protected void setMarkOccurrencePreferences(IOccurrencesFinder occurrencesFinder)
	{
		
	}

	protected void installOccurrencesFinder(boolean forceUpdate) {
		fMarkOccurrenceAnnotations= true;

		fPostSelectionListenerWithAST= new ISelectionListenerWithAST() {
			public void selectionChanged(IEditorPart part, ITextSelection selection, RootNode astRoot) {
				updateOccurrenceAnnotations(selection, astRoot);
			}
		};
		SelectionListenerWithASTManager.getDefault().addListener(this, fPostSelectionListenerWithAST);
		if (forceUpdate && getSelectionProvider() != null) {
			fForcedMarkOccurrencesSelection= getSelectionProvider().getSelection();
			updateOccurrenceAnnotations((ITextSelection)fForcedMarkOccurrencesSelection, RubyPlugin.getDefault().getASTProvider().getAST(getInputRubyElement(), ASTProvider.WAIT_NO, getProgressMonitor()));
		}

		if (fOccurrencesFinderJobCanceler == null) {
			fOccurrencesFinderJobCanceler= new OccurrencesFinderJobCanceler();
			fOccurrencesFinderJobCanceler.install();
		}
	}

	protected void uninstallOccurrencesFinder() {
		fMarkOccurrenceAnnotations= false;

		if (fOccurrencesFinderJob != null) {
			fOccurrencesFinderJob.cancel();
			fOccurrencesFinderJob= null;
		}

		if (fOccurrencesFinderJobCanceler != null) {
			fOccurrencesFinderJobCanceler.uninstall();
			fOccurrencesFinderJobCanceler= null;
		}

		if (fPostSelectionListenerWithAST != null) {
			SelectionListenerWithASTManager.getDefault().removeListener(this, fPostSelectionListenerWithAST);
			fPostSelectionListenerWithAST= null;
		}

		removeOccurrenceAnnotations();
	}

	protected boolean isMarkingOccurrences() {
		return fMarkOccurrenceAnnotations;
	}

	void removeOccurrenceAnnotations() {
		fMarkOccurrenceModificationStamp= IDocumentExtension4.UNKNOWN_MODIFICATION_STAMP;
		fMarkOccurrenceTargetRegion= null;
		
		IDocumentProvider documentProvider= getDocumentProvider();
		if (documentProvider == null)
			return;

		IAnnotationModel annotationModel= documentProvider.getAnnotationModel(getEditorInput());
		if (annotationModel == null || fOccurrenceAnnotations == null)
			return;

		synchronized (getLockObject(annotationModel)) {
			if (annotationModel instanceof IAnnotationModelExtension) {
				((IAnnotationModelExtension)annotationModel).replaceAnnotations(fOccurrenceAnnotations, null);
			} else {
				for (int i= 0, length= fOccurrenceAnnotations.length; i < length; i++)
					annotationModel.removeAnnotation(fOccurrenceAnnotations[i]);
			}
			fOccurrenceAnnotations= null;
		}
	}
	
	
	/**
	 * Returns the lock object for the given annotation model.
	 *
	 * @param annotationModel the annotation model
	 * @return the annotation model's lock object
	 * @since 3.0
	 */
	private Object getLockObject(IAnnotationModel annotationModel) {
		if (annotationModel instanceof ISynchronizable) {
			Object lock= ((ISynchronizable)annotationModel).getLockObject();
			if (lock != null)
				return lock;
		}
		return annotationModel;
	}
}