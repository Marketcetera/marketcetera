package org.rubypeople.rdt.internal.ui.rubyeditor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.ResourceBundle;
import java.util.Stack;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Preferences;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.AbstractInformationControlManager;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.BadPositionCategoryException;
import org.eclipse.jface.text.DefaultInformationControl;
import org.eclipse.jface.text.DocumentCommand;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentExtension;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.ILineTracker;
import org.eclipse.jface.text.IPositionUpdater;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.ITextOperationTarget;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.ITextViewerExtension;
import org.eclipse.jface.text.ITextViewerExtension2;
import org.eclipse.jface.text.ITextViewerExtension4;
import org.eclipse.jface.text.ITextViewerExtension5;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.TextUtilities;
import org.eclipse.jface.text.information.IInformationProvider;
import org.eclipse.jface.text.information.IInformationProviderExtension;
import org.eclipse.jface.text.information.IInformationProviderExtension2;
import org.eclipse.jface.text.information.InformationPresenter;
import org.eclipse.jface.text.link.ILinkedModeListener;
import org.eclipse.jface.text.link.LinkedModeModel;
import org.eclipse.jface.text.link.LinkedModeUI;
import org.eclipse.jface.text.link.LinkedPosition;
import org.eclipse.jface.text.link.LinkedPositionGroup;
import org.eclipse.jface.text.link.LinkedModeUI.ExitFlags;
import org.eclipse.jface.text.link.LinkedModeUI.IExitPolicy;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.IAnnotationHover;
import org.eclipse.jface.text.source.IAnnotationHoverExtension;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.ICharacterPairMatcher;
import org.eclipse.jface.text.source.ILineRange;
import org.eclipse.jface.text.source.IOverviewRuler;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.ISourceViewerExtension3;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.jface.text.source.IVerticalRulerInfo;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.jface.text.source.projection.ProjectionSupport;
import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.search.ui.IContextMenuConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.custom.VerifyKeyListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.SelectionEnabler;
import org.eclipse.ui.actions.ActionContext;
import org.eclipse.ui.actions.ActionGroup;
import org.eclipse.ui.editors.text.EditorsUI;
import org.eclipse.ui.editors.text.IEncodingSupport;
import org.eclipse.ui.help.WorkbenchHelp;
import org.eclipse.ui.part.IShowInSource;
import org.eclipse.ui.part.IShowInTargetList;
import org.eclipse.ui.part.ShowInContext;
import org.eclipse.ui.texteditor.AnnotationPreference;
import org.eclipse.ui.texteditor.ContentAssistAction;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.IEditorStatusLine;
import org.eclipse.ui.texteditor.ITextEditorActionConstants;
import org.eclipse.ui.texteditor.MarkerAnnotation;
import org.eclipse.ui.texteditor.ResourceAction;
import org.eclipse.ui.texteditor.TextEditorAction;
import org.eclipse.ui.texteditor.TextOperationAction;
import org.eclipse.ui.texteditor.link.EditorLinkedModeUI;
import org.jruby.ast.RootNode;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IRubyProject;
import org.rubypeople.rdt.core.IRubyScript;
import org.rubypeople.rdt.core.RubyCore;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.core.formatter.DefaultCodeFormatterConstants;
import org.rubypeople.rdt.internal.corext.util.CodeFormatterUtil;
import org.rubypeople.rdt.internal.corext.util.RubyModelUtil;
import org.rubypeople.rdt.internal.ui.IRubyHelpContextIds;
import org.rubypeople.rdt.internal.ui.RubyPlugin;
import org.rubypeople.rdt.internal.ui.actions.CompositeActionGroup;
import org.rubypeople.rdt.internal.ui.actions.FoldingActionGroup;
import org.rubypeople.rdt.internal.ui.actions.SelectionConverter;
import org.rubypeople.rdt.internal.ui.text.HTMLTextPresenter;
import org.rubypeople.rdt.internal.ui.text.IRubyPartitions;
import org.rubypeople.rdt.internal.ui.text.RubyHeuristicScanner;
import org.rubypeople.rdt.internal.ui.text.Symbols;
import org.rubypeople.rdt.internal.ui.text.ruby.IRubyReconcilingListener;
import org.rubypeople.rdt.internal.ui.text.ruby.hover.SourceViewerInformationControl;
import org.rubypeople.rdt.ui.IWorkingCopyManager;
import org.rubypeople.rdt.ui.PreferenceConstants;
import org.rubypeople.rdt.ui.RubyUI;
import org.rubypeople.rdt.ui.actions.FormatAction;
import org.rubypeople.rdt.ui.actions.IRubyEditorActionDefinitionIds;
import org.rubypeople.rdt.ui.actions.OpenEditorActionGroup;
import org.rubypeople.rdt.ui.actions.OpenViewActionGroup;
import org.rubypeople.rdt.ui.actions.RubyActionGroup;
import org.rubypeople.rdt.ui.actions.RubySearchActionGroup;
import org.rubypeople.rdt.ui.actions.ShowInRubyExplorerViewAction;
import org.rubypeople.rdt.ui.actions.SurroundWithBeginRescueAction;
import org.rubypeople.rdt.ui.text.folding.IRubyFoldingStructureProvider;
import org.rubypeople.rdt.ui.text.folding.IRubyFoldingStructureProviderExtension;

public class RubyEditor extends RubyAbstractEditor implements IRubyReconcilingListener {
    
    private ProjectionSupport fProjectionSupport;
    
	/** The editor's tab converter */
	private TabConverter fTabConverter;    

	/** Preference key for automatically closing strings */
	private final static String CLOSE_STRINGS= PreferenceConstants.EDITOR_CLOSE_STRINGS;
	/** Preference key for automatically closing brackets and parenthesis */
	private final static String CLOSE_BRACKETS= PreferenceConstants.EDITOR_CLOSE_BRACKETS;
	/** Preference key for automatically closing braces */
	private final static String CLOSE_BRACES= PreferenceConstants.EDITOR_CLOSE_BRACES;
	/** Preference key for code formatter tab size */
	private final static String CODE_FORMATTER_TAB_SIZE= DefaultCodeFormatterConstants.FORMATTER_TAB_SIZE;
	/** Preference key for inserting spaces rather than tabs */
	private final static String SPACES_FOR_TABS= DefaultCodeFormatterConstants.FORMATTER_TAB_CHAR;
	
    /**
     * Mutex for the reconciler. See
     * https://bugs.eclipse.org/bugs/show_bug.cgi?id=63898 for a description of
     * the problem.
     * <p>
     * TODO remove once the underlying problem is solved.
     * </p>
     */
    private final Object fReconcilerLock = new Object();

    /**
     * This editor's projection model updater
     * 
     * @since 3.0
     */
    private IRubyFoldingStructureProvider fProjectionModelUpdater;
	/**
	 * The override and implements indicator manager for this editor.
	 * @since 3.0
	 */
	protected OverrideIndicatorManager fOverrideIndicatorManager;
    /**
     * Indicates whether this editor is about to update any annotation views.
     * 
     * @since 3.0
     */
    private boolean fIsUpdatingAnnotationViews = false;
    /**
     * The marker that served as last target for a goto marker request.
     * 
     * @since 3.0
     */
    private IMarker fLastMarkerTarget = null;
    
	/**
	 * The folding runner.
	 * @since 0.9.0
	 */
	private ToggleFoldingRunner fFoldingRunner;
	
	/**
	 * The action group for folding.
	 *
	 * @since 0.9.0
	 */
	private FoldingActionGroup fFoldingGroup;
    
	/**
	 * Reconciling listeners.
	 * @since 3.0
	 */
	private ListenerList fReconcilingListeners= new ListenerList(ListenerList.IDENTITY);
	
    private BracketInserter fBracketInserter = new BracketInserter();
	private CompositeActionGroup fActionGroups;
	private CompositeActionGroup fContextMenuGroup;
	private RubyActionGroup fGenerateActionGroup;
	
	private InformationPresenter fInformationPresenter;

    public RubyEditor() {
        super();
        setDocumentProvider(RubyPlugin.getDefault().getRubyDocumentProvider());

        this.setRulerContextMenuId(RubyUI.ID_RULER_CONTEXT_MENU); //$NON-NLS-1$
        this.setEditorContextMenuId(RubyUI.ID_EDITOR_CONTEXT_MENU); //$NON-NLS-1$
        setKeyBindingScopes(new String[] { "org.rubypeople.rdt.ui.rubyEditorScope"}); //$NON-NLS-1$
        setOutlinerContextMenuId("#RubyScriptOutlinerContext"); //$NON-NLS-1$
    }
    
	/**
	 * Returns the standard action group of this editor.
	 *
	 * @return returns this editor's standard action group
	 */
	protected ActionGroup getActionGroup() {
		return fActionGroups;
	}

    protected void createActions() {
        super.createActions();
        
        ActionGroup oeg, ovg, rsg;
		fActionGroups= new CompositeActionGroup(new ActionGroup[] {
			oeg= new OpenEditorActionGroup(this),
			ovg= new OpenViewActionGroup(this),
			rsg= new RubySearchActionGroup(this)
		});
		
		fGenerateActionGroup= new RubyActionGroup(this, ITextEditorActionConstants.GROUP_EDIT);
		fContextMenuGroup= new CompositeActionGroup(new ActionGroup[] {oeg, ovg, rsg, fGenerateActionGroup});
        
        fFoldingGroup= new FoldingActionGroup(this, getViewer());
        
        ISelectionProvider provider= getSite().getSelectionProvider();
        ISelection selection= provider.getSelection();
        
        ResourceAction resAction= new TextOperationAction(RubyEditorMessages.getBundleForConstructedKeys(), "ShowRDoc.", this, ISourceViewer.INFORMATION, true); //$NON-NLS-1$
		resAction= new InformationDispatchAction(RubyEditorMessages.getBundleForConstructedKeys(), "ShowRDoc.", (TextOperationAction) resAction); //$NON-NLS-1$
		resAction.setActionDefinitionId(IRubyEditorActionDefinitionIds.SHOW_RDOC);
		setAction("ShowRDoc", resAction); //$NON-NLS-1$
		PlatformUI.getWorkbench().getHelpSystem().setHelp(resAction, IRubyHelpContextIds.SHOW_JAVADOC_ACTION);
        
        SurroundWithBeginRescueAction beginRescueAction = new SurroundWithBeginRescueAction(this);
        beginRescueAction.setActionDefinitionId(IRubyEditorActionDefinitionIds.SURROUND_WITH_BEGIN_RESCUE);
        beginRescueAction.update(selection);
        provider.addSelectionChangedListener(beginRescueAction);
        setAction(SurroundWithBeginRescueAction.SURROUND_WTH_BEGIN_RESCUE, beginRescueAction);
        
        Action action = new ContentAssistAction(RubyPlugin.getDefault().getPluginProperties(),
                "ContentAssistProposal.", this);
        action.setActionDefinitionId(IRubyEditorActionDefinitionIds.CONTENT_ASSIST_PROPOSALS);
        setAction("ContentAssistProposal", action);

        action = new TextOperationAction(RubyPlugin.getDefault().getPluginProperties(), "CommentAction.", this,
                ITextOperationTarget.PREFIX);
        action.setActionDefinitionId(IRubyEditorActionDefinitionIds.COMMENT);
        setAction("Comment", action);

        action = new TextOperationAction(RubyPlugin.getDefault().getPluginProperties(), "UncommentAction.", this,
                ITextOperationTarget.STRIP_PREFIX);
        action.setActionDefinitionId(IRubyEditorActionDefinitionIds.UNCOMMENT);
        setAction("Uncomment", action);

        action = new ToggleCommentAction(RubyPlugin.getDefault().getPluginProperties(),
                "ToggleCommentAction.", this); //$NON-NLS-1$
        action.setActionDefinitionId(IRubyEditorActionDefinitionIds.TOGGLE_COMMENT);
        setAction("ToggleComment", action); //$NON-NLS-1$
        markAsStateDependentAction("ToggleComment", true); //$NON-NLS-1$
        WorkbenchHelp.setHelp(action, IRubyHelpContextIds.TOGGLE_COMMENT_ACTION);
        configureToggleCommentAction();
                
//      add annotation actions for roll-over expand hover
		action= new RubySelectMarkerRulerAction2(RubyEditorMessages.getBundleForConstructedKeys(), "Editor.RulerAnnotationSelection.", this); //$NON-NLS-1$
		setAction("AnnotationAction", action); //$NON-NLS-1$
        
        action= new ShowInRubyExplorerViewAction(this);
		action.setActionDefinitionId(IRubyEditorActionDefinitionIds.SHOW_IN_RUBY_RESOURCES_VIEW);
		setAction("ShowInPackageView", action); //$NON-NLS-1$
        
        action= new GotoMatchingBracketAction(this);
        action.setActionDefinitionId(IRubyEditorActionDefinitionIds.GOTO_MATCHING_BRACKET);
        setAction(GotoMatchingBracketAction.GOTO_MATCHING_BRACKET, action);
        
		action= new TextOperationAction(RubyEditorMessages.getBundleForConstructedKeys(),"ShowOutline.", this, RubySourceViewer.SHOW_OUTLINE, true); //$NON-NLS-1$
		action.setActionDefinitionId(IRubyEditorActionDefinitionIds.SHOW_OUTLINE);
		setAction(IRubyEditorActionDefinitionIds.SHOW_OUTLINE, action);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(action, IRubyHelpContextIds.SHOW_OUTLINE_ACTION);
		
		action= new TextOperationAction(RubyEditorMessages.getBundleForConstructedKeys(),"OpenStructure.", this, RubySourceViewer.OPEN_STRUCTURE, true); //$NON-NLS-1$
		action.setActionDefinitionId(IRubyEditorActionDefinitionIds.OPEN_STRUCTURE);
		setAction(IRubyEditorActionDefinitionIds.OPEN_STRUCTURE, action);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(action, IRubyHelpContextIds.OPEN_STRUCTURE_ACTION);
		
		action= new TextOperationAction(RubyEditorMessages.getBundleForConstructedKeys(),"OpenHierarchy.", this, RubySourceViewer.SHOW_HIERARCHY, true); //$NON-NLS-1$
		action.setActionDefinitionId(IRubyEditorActionDefinitionIds.OPEN_HIERARCHY);
		setAction(IRubyEditorActionDefinitionIds.OPEN_HIERARCHY, action);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(action, IRubyHelpContextIds.OPEN_HIERARCHY_ACTION);

        action = new FormatAction(RubyPlugin.getDefault().getPluginProperties(), "FormatAction.", this);
        action.setActionDefinitionId(IRubyEditorActionDefinitionIds.FORMAT);
        setAction("Format", action);
    }
   
    /**
     * Configures the toggle comment action
     * 
     * @since 3.0
     */
    private void configureToggleCommentAction() {
        IAction action = getAction("ToggleComment"); //$NON-NLS-1$
        if (action instanceof ToggleCommentAction) {
            ISourceViewer sourceViewer = getSourceViewer();
            SourceViewerConfiguration configuration = getSourceViewerConfiguration();
            ((ToggleCommentAction) action).configure(sourceViewer, configuration);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.texteditor.AbstractDecoratedTextEditor#createPartControl(org.eclipse.swt.widgets.Composite)
     */
    public void createPartControl(Composite parent) {
        super.createPartControl(parent);
        
		IInformationControlCreator informationControlCreator= new IInformationControlCreator() {
			public IInformationControl createInformationControl(Shell shell) {
				boolean cutDown= false;
				int style= cutDown ? SWT.NONE : (SWT.V_SCROLL | SWT.H_SCROLL);
				return new DefaultInformationControl(shell, SWT.RESIZE | SWT.TOOL, style, new HTMLTextPresenter(cutDown));
			}
		};

		fInformationPresenter= new InformationPresenter(informationControlCreator);
		fInformationPresenter.setSizeConstraints(60, 10, true, true);
		fInformationPresenter.install(getSourceViewer());
		fInformationPresenter.setDocumentPartitioning(IRubyPartitions.RUBY_PARTITIONING);

        if (isTabConversionEnabled())
			startTabConversion();
        
        ISourceViewer sourceViewer = getSourceViewer();
        if (sourceViewer instanceof ITextViewerExtension) {
        	IPreferenceStore preferenceStore= getPreferenceStore();
    		boolean closeBrackets= preferenceStore.getBoolean(CLOSE_BRACKETS);
    		boolean closeBraces= preferenceStore.getBoolean(CLOSE_BRACES);
    		boolean closeStrings= preferenceStore.getBoolean(CLOSE_STRINGS);
    		fBracketInserter.setCloseBracketsEnabled(closeBrackets);
    		fBracketInserter.setCloseBracesEnabled(closeBraces);
    		fBracketInserter.setCloseStringsEnabled(closeStrings);
    		((ITextViewerExtension) sourceViewer).prependVerifyKeyListener(fBracketInserter);
        }
        
        if (sourceViewer instanceof ProjectionViewer) {
        	ProjectionViewer pv = (ProjectionViewer) sourceViewer;
        	pv.doOperation(ProjectionViewer.TOGGLE);
        }
    }
    
	/**
	 * Resets the foldings structure according to the folding
	 * preferences.
	 * 
	 * @since 0.9.0
	 */
	public void resetProjection() {
		if (fProjectionModelUpdater != null) {
			fProjectionModelUpdater.initialize();
		}
	}

	/*
	 * @see org.eclipse.ui.texteditor.AbstractTextEditor#rulerContextMenuAboutToShow(org.eclipse.jface.action.IMenuManager)
	 */
	protected void rulerContextMenuAboutToShow(IMenuManager menu) {
		super.rulerContextMenuAboutToShow(menu);
		IMenuManager foldingMenu= new MenuManager(RubyEditorMessages.Editor_FoldingMenu_name, "projection"); //$NON-NLS-1$
		menu.appendToGroup(ITextEditorActionConstants.GROUP_RULERS, foldingMenu);

		IAction action= getAction("FoldingToggle"); //$NON-NLS-1$
		foldingMenu.add(action);
		action= getAction("FoldingExpandAll"); //$NON-NLS-1$
		foldingMenu.add(action);
		action= getAction("FoldingCollapseAll"); //$NON-NLS-1$
		foldingMenu.add(action);
		action= getAction("FoldingRestore"); //$NON-NLS-1$
		foldingMenu.add(action);
		action= getAction("FoldingCollapseMembers"); //$NON-NLS-1$
		foldingMenu.add(action);
		action= getAction("FoldingCollapseComments"); //$NON-NLS-1$
		foldingMenu.add(action);
	}
	
    /**
     * Returns the annotation overlapping with the given range or
     * <code>null</code>.
     * 
     * @param offset
     *            the region offset
     * @param length
     *            the region length
     * @return the found annotation or <code>null</code>
     * @since 3.0
     */
    private Annotation getAnnotation(int offset, int length) {
        IAnnotationModel model = getDocumentProvider().getAnnotationModel(getEditorInput());
        Iterator e = new RubyAnnotationIterator(model, true, true);
        while (e.hasNext()) {
            Annotation a = (Annotation) e.next();
            if (!isNavigationTarget(a)) continue;

            Position p = model.getPosition(a);
            if (p != null && p.overlapsWith(offset, length)) return a;
        }

        return null;
    }
    
	
	/**
	 * Returns the annotation closest to the given range respecting the given
	 * direction. If an annotation is found, the annotations current position
	 * is copied into the provided annotation position.
	 * 
	 * @param offset the region offset
	 * @param length the region length
	 * @param forward <code>true</code> for forwards, <code>false</code> for backward
	 * @param annotationPosition the position of the found annotation
	 * @return the found annotation
	 */
	private Annotation getNextAnnotation(final int offset, final int length, boolean forward, Position annotationPosition) {
		
		Annotation nextAnnotation= null;
		Position nextAnnotationPosition= null;
		Annotation containingAnnotation= null;
		Position containingAnnotationPosition= null;
		boolean currentAnnotation= false;
		
		IDocument document= getDocumentProvider().getDocument(getEditorInput());
		int endOfDocument= document.getLength(); 
		int distance= Integer.MAX_VALUE;
		
		IAnnotationModel model= getDocumentProvider().getAnnotationModel(getEditorInput());
		Iterator e= new RubyAnnotationIterator(model, true, true);
		while (e.hasNext()) {
			Annotation a= (Annotation) e.next();
			if ((a instanceof IRubyAnnotation) && ((IRubyAnnotation)a).hasOverlay() || !isNavigationTarget(a))
				continue;
				
			Position p= model.getPosition(a);
			if (p == null)
				continue;
			
			if (forward && p.offset == offset || !forward && p.offset + p.getLength() == offset + length) {// || p.includes(offset)) {
				if (containingAnnotation == null || (forward && p.length >= containingAnnotationPosition.length || !forward && p.length >= containingAnnotationPosition.length)) { 
					containingAnnotation= a;
					containingAnnotationPosition= p;
					currentAnnotation= p.length == length;
				}
			} else {
				int currentDistance= 0;
				
				if (forward) {
					currentDistance= p.getOffset() - offset;
					if (currentDistance < 0)
						currentDistance= endOfDocument + currentDistance;
					
					if (currentDistance < distance || currentDistance == distance && p.length < nextAnnotationPosition.length) {
						distance= currentDistance;
						nextAnnotation= a;
						nextAnnotationPosition= p;
					}
				} else {
					currentDistance= offset + length - (p.getOffset() + p.length);
					if (currentDistance < 0)
						currentDistance= endOfDocument + currentDistance;
					
					if (currentDistance < distance || currentDistance == distance && p.length < nextAnnotationPosition.length) {
						distance= currentDistance;
						nextAnnotation= a;
						nextAnnotationPosition= p;
					}
				}
			}
		}
		if (containingAnnotationPosition != null && (!currentAnnotation || nextAnnotation == null)) {
			annotationPosition.setOffset(containingAnnotationPosition.getOffset());
			annotationPosition.setLength(containingAnnotationPosition.getLength());
			return containingAnnotation;
		}
		if (nextAnnotationPosition != null) {
			annotationPosition.setOffset(nextAnnotationPosition.getOffset());
			annotationPosition.setLength(nextAnnotationPosition.getLength());
		}
		
		return nextAnnotation;
	}
	
	/**
	 * Returns whether the given annotation is configured as a target for the
	 * "Go to Next/Previous Annotation" actions
	 * 
	 * CHANGED TO WORK WITH 3.2 (Non-breaking in 3.1)
	 * Method couldn't be restricted to private, changed to protected
	 * 
	 * @param annotation the annotation
	 * @return <code>true</code> if this is a target, <code>false</code>
	 *         otherwise
	 * @since 3.2
	 */
	protected boolean isNavigationTarget(Annotation annotation) {
		Preferences preferences= EditorsUI.getPluginPreferences();
		AnnotationPreference preference= getAnnotationPreferenceLookup().getAnnotationPreference(annotation);
//		See bug 41689
//		String key= forward ? preference.getIsGoToNextNavigationTargetKey() : preference.getIsGoToPreviousNavigationTargetKey();
		String key= preference == null ? null : preference.getIsGoToNextNavigationTargetKey();
		return (key != null && preferences.getBoolean(key));
	}
	
	/**
	 * Jumps to the next enabled annotation according to the given direction.
	 * An annotation type is enabled if it is configured to be in the
	 * Next/Previous tool bar drop down menu and if it is checked.
	 * 
	 * CHANGED TO WORK WITH 3.2 (Non-breaking in 3.1)
	 * Annotation type must be returned
	 * 
	 * @param forward <code>true</code> if search direction is forward, <code>false</code> if backward
	 * @since 3.2
	 */
	public Annotation gotoAnnotation(boolean forward) {
		Annotation annotation = null;
		ITextSelection selection= (ITextSelection) getSelectionProvider().getSelection();
		Position position= new Position(0, 0);
		if (false /* delayed - see bug 18316 */) {
			getNextAnnotation(selection.getOffset(), selection.getLength(), forward, position);
			selectAndReveal(position.getOffset(), position.getLength());
		} else /* no delay - see bug 18316 */ {
			annotation= getNextAnnotation(selection.getOffset(), selection.getLength(), forward, position);
			setStatusLineErrorMessage(null);
			setStatusLineMessage(null);
			if (annotation != null) {
				updateAnnotationViews(annotation);
				selectAndReveal(position.getOffset(), position.getLength());
				setStatusLineMessage(annotation.getText());
			}
		}
		return annotation;
	}
	

    /**
     * Updates the annotation views that show the given annotation.
     * 
     * @param annotation
     *            the annotation
     */
    private void updateAnnotationViews(Annotation annotation) {
        IMarker marker = null;
        if (annotation instanceof MarkerAnnotation)
            marker = ((MarkerAnnotation) annotation).getMarker();
        else if (annotation instanceof IRubyAnnotation) {
            Iterator e = ((IRubyAnnotation) annotation).getOverlaidIterator();
            if (e != null) {
                while (e.hasNext()) {
                    Object o = e.next();
                    if (o instanceof MarkerAnnotation) {
                        marker = ((MarkerAnnotation) o).getMarker();
                        break;
                    }
                }
            }
        }

        if (marker != null && !marker.equals(fLastMarkerTarget)) {
            try {
                boolean isProblem = marker.isSubtypeOf(IMarker.PROBLEM);
                IWorkbenchPage page = getSite().getPage();
                IViewPart view = page.findView(isProblem ? IPageLayout.ID_PROBLEM_VIEW
                        : IPageLayout.ID_TASK_LIST); //$NON-NLS-1$  //$NON-NLS-2$
                if (view != null) {
                    Method method = view
                            .getClass()
                            .getMethod(
                                    "setSelection", new Class[] { IStructuredSelection.class, boolean.class}); //$NON-NLS-1$
                    method.invoke(view, new Object[] { new StructuredSelection(marker),
                            Boolean.TRUE});
                }
            } catch (CoreException x) {
            } catch (NoSuchMethodException x) {
            } catch (IllegalAccessException x) {
            } catch (InvocationTargetException x) {
            }
            // ignore exceptions, don't update any of the lists, just set status
            // line
        }
    }

    /*
     * @see org.eclipse.ui.texteditor.AbstractTextEditor#gotoMarker(org.eclipse.core.resources.IMarker)
     */
    public void gotoMarker(IMarker marker) {
        fLastMarkerTarget = marker;
        if (!fIsUpdatingAnnotationViews) {
            super.gotoMarker(marker);
        }
    }

    protected void updateStatusLine() {
        ITextSelection selection = (ITextSelection) getSelectionProvider().getSelection();
        Annotation annotation = getAnnotation(selection.getOffset(), selection.getLength());
        setStatusLineErrorMessage(null);
        setStatusLineMessage(null);
        if (annotation != null) {
            try {
                fIsUpdatingAnnotationViews = true;
                updateAnnotationViews(annotation);
            } finally {
                fIsUpdatingAnnotationViews = false;
            }
            if (annotation instanceof IRubyAnnotation && ((IRubyAnnotation) annotation).isProblem())
                setStatusLineMessage(annotation.getText());
        }
    }

    /**
     * Sets the given message as error message to this editor's status line.
     * 
     * @param msg
     *            message to be set
     */
    protected void setStatusLineErrorMessage(String msg) {
        IEditorStatusLine statusLine = (IEditorStatusLine) getAdapter(IEditorStatusLine.class);
        if (statusLine != null) statusLine.setMessage(true, msg, null);
    }

    /**
     * Sets the given message as message to this editor's status line.
     * 
     * @param msg
     *            message to be set
     * @since 3.0
     */
    protected void setStatusLineMessage(String msg) {
        IEditorStatusLine statusLine = (IEditorStatusLine) getAdapter(IEditorStatusLine.class);
        if (statusLine != null) statusLine.setMessage(false, msg, null);
    }

    boolean isFoldingEnabled() {
        return RubyPlugin.getDefault().getPreferenceStore().getBoolean(
                PreferenceConstants.EDITOR_FOLDING_ENABLED);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.rubypeople.rdt.internal.ui.rubyeditor.RubyAbstractEditor#dispose()
     */
    public void dispose() {        
        ISourceViewer sourceViewer= getSourceViewer();
		if (sourceViewer instanceof ITextViewerExtension) {
			((ITextViewerExtension) sourceViewer).removeVerifyKeyListener(fBracketInserter);
		}

        if (fProjectionModelUpdater != null) {
            fProjectionModelUpdater.uninstall();
            fProjectionModelUpdater = null;
        }

        if (fProjectionSupport != null) {
            fProjectionSupport.dispose();
            fProjectionSupport = null;
        }
        
        if (fActionGroups != null) {
			fActionGroups.dispose();
			fActionGroups= null;
		}
        	
        super.dispose();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.texteditor.AbstractTextEditor#performRevert()
     */
    protected void performRevert() {
        ProjectionViewer projectionViewer = (ProjectionViewer) getSourceViewer();
        projectionViewer.setRedraw(false);
        try {

            boolean projectionMode = projectionViewer.isProjectionMode();
            if (projectionMode) {
                projectionViewer.disableProjection();
                if (fProjectionModelUpdater != null) fProjectionModelUpdater.uninstall();
            }

            super.performRevert();

            if (projectionMode) {
                if (fProjectionModelUpdater != null)
                    fProjectionModelUpdater.install(this, projectionViewer);
                projectionViewer.enableProjection();
            }

        } finally {
            projectionViewer.setRedraw(true);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.rubypeople.rdt.internal.ui.rubyeditor.RubyAbstractEditor#getAdapter(java.lang.Class)
     */
    public Object getAdapter(Class required) {
		if (IEncodingSupport.class.equals(required))
			return fEncodingSupport;

		if (required == IShowInTargetList.class) {
			return new IShowInTargetList() {
				public String[] getShowInTargetIds() {
					// Commented out by Will since Photon does not use these views
					return new String[] { /*RubyUI.ID_RUBY_EXPLORER, */IPageLayout.ID_OUTLINE/*, IPageLayout.ID_RES_NAV*/ };
				}

			};
		}

		if (required == IShowInSource.class) {
			return new IShowInSource() {
				public ShowInContext getShowInContext() {
					return new ShowInContext(getEditorInput(), null) {
						/*
						 * @see org.eclipse.ui.part.ShowInContext#getSelection()
						 * @since 3.3
						 */
						public ISelection getSelection() {
							IRubyElement re= null;
							try {
								re= SelectionConverter.getElementAtOffset(RubyEditor.this);
								return new StructuredSelection(re);
							} catch (RubyModelException ex) {
								return null;
							}
						}
					};
				}
			};
		}

		if (required == IRubyFoldingStructureProvider.class)
			return fProjectionModelUpdater;

		if (fProjectionSupport != null) {
			Object adapter= fProjectionSupport.getAdapter(getSourceViewer(), required);
			if (adapter != null)
				return adapter;
		}

//		if (required == IContextProvider.class)
//			return RubyUIHelp.getHelpContextProvider(this, IRubyHelpContextIds.JAVA_EDITOR);

		return super.getAdapter(required);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.rubypeople.rdt.internal.ui.rubyeditor.RubyAbstractEditor#doSetInput(org.eclipse.ui.IEditorInput)
     */
    protected void doSetInput(IEditorInput input) throws CoreException {
        super.doSetInput(input);
        configureTabConverter();
        if (fProjectionModelUpdater != null) fProjectionModelUpdater.initialize();
        
        if (isShowingOverrideIndicators())
			installOverrideIndicator(false);
    }

    protected void editorContextMenuAboutToShow(IMenuManager menu) {
        super.editorContextMenuAboutToShow(menu);        
		menu.insertAfter(IContextMenuConstants.GROUP_OPEN, new GroupMarker(IContextMenuConstants.GROUP_SHOW));

		ActionContext context= new ActionContext(getSelectionProvider().getSelection());
		fContextMenuGroup.setContext(context);
		fContextMenuGroup.fillContextMenu(menu);
		fContextMenuGroup.setContext(null);

		// Quick views
		IAction action= getAction(IRubyEditorActionDefinitionIds.SHOW_OUTLINE);
		menu.appendToGroup(IContextMenuConstants.GROUP_OPEN, action);  
		action= getAction(IRubyEditorActionDefinitionIds.OPEN_HIERARCHY);
		menu.appendToGroup(IContextMenuConstants.GROUP_OPEN, action);
		
		addExtensionMenuItems(menu);
    }

	private void addExtensionMenuItems(IMenuManager menu) {
		IExtensionRegistry registry = Platform.getExtensionRegistry();
        IExtensionPoint extensionPoint = registry
                .getExtensionPoint("org.rubypeople.rdt.ui.editorPopupExtender");
        IExtension[] extensions = extensionPoint.getExtensions();
        for (int i = 0; i < extensions.length; i++) {
            IConfigurationElement[] elements = extensions[i].getConfigurationElements();
            for (int j = 0; j < elements.length; j++) {
                IConfigurationElement element = elements[j];
                SelectionEnabler selectionEnabler = new SelectionEnabler(element);
                if (selectionEnabler.isEnabledForSelection(this.getSelectionProvider()
                        .getSelection())) {
                    try {
                        Object menuExtender = element.createExecutableExtension("class");
                        if (!(menuExtender instanceof ActionGroup)) {
                            String message = "The editorPopupExtender" + element.getName()
                                    + " is of type " + menuExtender.getClass().getName()
                                    + " , but should be of type ActionGroup";
                            RubyPlugin.log(IStatus.ERROR, message, null);
                            continue;
                        }
                        ActionGroup menuExtenderActionGroup = (ActionGroup) menuExtender;
                        menuExtenderActionGroup.setContext(new ActionContext(this
                                .getSelectionProvider().getSelection()));
                        menuExtenderActionGroup.fillContextMenu(menu);
                    } catch (CoreException e) {
                        RubyPlugin.log(e);
                    }

                }
            }
        }
	}

    protected void handlePreferenceStoreChanged(PropertyChangeEvent event) {
        super.handlePreferenceStoreChanged(event);
        String property = event.getProperty();
        
		if (CLOSE_BRACKETS.equals(property)) {
			fBracketInserter.setCloseBracketsEnabled(getPreferenceStore().getBoolean(property));
			return;
		}
		
		if (CLOSE_BRACES.equals(property)) {
			fBracketInserter.setCloseBracesEnabled(getPreferenceStore().getBoolean(property));
			return;
		}

		if (CLOSE_STRINGS.equals(property)) {
			fBracketInserter.setCloseStringsEnabled(getPreferenceStore().getBoolean(property));
			return;
		}          
		
		AdaptedSourceViewer sourceViewer= (AdaptedSourceViewer) getSourceViewer();
		if (sourceViewer == null)
			return;

		if (SPACES_FOR_TABS.equals(property)) {
			if (isTabConversionEnabled())
				startTabConversion();
			else
				stopTabConversion();
			return;
		}
		
		if (CODE_FORMATTER_TAB_SIZE.equals(property)) {
			sourceViewer.updateIndentationPrefixes();
			if (fTabConverter != null)
				fTabConverter.setNumberOfSpacesPerTab(getTabSize());
		}
        
		if (PreferenceConstants.EDITOR_FOLDING_PROVIDER.equals(property)) {
			if (sourceViewer instanceof ProjectionViewer) {
				ProjectionViewer projectionViewer= (ProjectionViewer) sourceViewer;
				if (fProjectionModelUpdater != null)
					fProjectionModelUpdater.uninstall();
				// either freshly enabled or provider changed
				fProjectionModelUpdater= RubyPlugin.getDefault().getFoldingStructureProviderRegistry().getCurrentFoldingProvider();
				if (fProjectionModelUpdater != null) {
					fProjectionModelUpdater.install(this, projectionViewer);
				}
			}
			return;
		}     
		
		if (PreferenceConstants.EDITOR_FOLDING_ENABLED.equals(property)) {
			if (sourceViewer instanceof ProjectionViewer) {
				new ToggleFoldingRunner().runWhenNextVisible();
			}
			return;
		}
    }

	protected ISourceViewer createRubySourceViewer(Composite parent, IVerticalRuler verticalRuler, IOverviewRuler overviewRuler, boolean isOverviewRulerVisible, int styles, IPreferenceStore store) {
		ISourceViewer viewer = new AdaptedSourceViewer(parent, verticalRuler, overviewRuler, isOverviewRulerVisible, styles, store);
		RubySourceViewer rubySourceViewer= null;
		if (viewer instanceof RubySourceViewer)
			rubySourceViewer= (RubySourceViewer)viewer;

		/*
		 * This is a performance optimization to reduce the computation of
		 * the text presentation triggered by {@link #setVisibleDocument(IDocument)}
		 */
//		if (rubySourceViewer != null && isFoldingEnabled() && (store == null || !store.getBoolean(PreferenceConstants.EDITOR_SHOW_SEGMENTS)))
//			rubySourceViewer.prepareDelayedProjection();
				
		ProjectionViewer projectionViewer= (ProjectionViewer)viewer;
		fProjectionSupport= new ProjectionSupport(projectionViewer, getAnnotationAccess(), getSharedColors());
		fProjectionSupport.addSummarizableAnnotationType("org.eclipse.ui.workbench.texteditor.error"); //$NON-NLS-1$
		fProjectionSupport.addSummarizableAnnotationType("org.eclipse.ui.workbench.texteditor.warning"); //$NON-NLS-1$
		fProjectionSupport.setHoverControlCreator(new IInformationControlCreator() {
			public IInformationControl createInformationControl(Shell shell) {
				return new SourceViewerInformationControl(shell, SWT.TOOL | SWT.NO_TRIM | getOrientation(), SWT.NONE);
			}
		});
//		fProjectionSupport.setInformationPresenterControlCreator(new IInformationControlCreator() {
//			public IInformationControl createInformationControl(Shell shell) {
//				int shellStyle= SWT.RESIZE | SWT.TOOL | getOrientation();
//				int style= SWT.V_SCROLL | SWT.H_SCROLL;
//				return new SourceViewerInformationControl(shell, shellStyle, style);
//			}
//		});
		fProjectionSupport.install();
		
		fProjectionModelUpdater= RubyPlugin.getDefault().getFoldingStructureProviderRegistry().getCurrentFoldingProvider();
		if (fProjectionModelUpdater != null)
			fProjectionModelUpdater.install(this, projectionViewer);
		
		if (isFoldingEnabled()) projectionViewer.doOperation(ProjectionViewer.TOGGLE);
		return viewer;
	}

    /**
     * Returns the mutex for the reconciler. See
     * https://bugs.eclipse.org/bugs/show_bug.cgi?id=63898 for a description of
     * the problem.
     * <p>
     * TODO remove once the underlying problem is solved.
     * </p>
     * 
     * @return the lock reconcilers may use to synchronize on
     */
    public Object getReconcilerLock() {
        return fReconcilerLock;
    }

    private static char getEscapeCharacter(char character) {
        switch (character) {
        case '"':
        case '\'':
            return '\\';
        default:
            return 0;
        }
    }

    private static char getPeerCharacter(char character) {
        switch (character) {
        case '(':
            return ')';

        case ')':
            return '(';

        case '{':
            return '}';

        case '}':
            return '{';

        case '[':
            return ']';

        case ']':
            return '[';

        case '"':
            return character;

        case '\'':
            return character;

        default:
            throw new IllegalArgumentException();
        }
    }

    public void setCaretPosition(CaretPosition pos) {

        try {
            int lineOffset = this.getSourceViewer().getDocument().getLineOffset(pos.line);
            this.selectAndReveal(lineOffset + pos.column, 0);
        } catch (BadLocationException e) {
        }
    }

    public class CaretPosition {

        public CaretPosition(int line, int column) {
            this.line = line;
            this.column = column;
        }

        public CaretPosition(int line, int column, int offset) {
    		this(line, column);
    		this.offset = offset;
    	}

    	public int getColumn() {
            return column;
        }

        public int getLine() {
            return line;
        }
            
        public int getOffset() {
            return offset;
        }

        private int line;

        private int column;
            
        private int offset;

    }

    private class ExitPolicy implements IExitPolicy {

        final char fExitCharacter;
        final char fEscapeCharacter;
        final Stack fStack;
        final int fSize;

        public ExitPolicy(char exitCharacter, char escapeCharacter, Stack stack) {
            fExitCharacter = exitCharacter;
            fEscapeCharacter = escapeCharacter;
            fStack = stack;
            fSize = fStack.size();
        }

        /*
         * @see org.eclipse.jdt.internal.ui.text.link.LinkedPositionUI.ExitPolicy#doExit(org.eclipse.jdt.internal.ui.text.link.LinkedPositionManager,
         *      org.eclipse.swt.events.VerifyEvent, int, int)
         */
        public ExitFlags doExit(LinkedModeModel model, VerifyEvent event, int offset, int length) {

            if (event.character == fExitCharacter) {

                if (fSize == fStack.size() && !isMasked(offset)) {
                    BracketLevel level = (BracketLevel) fStack.peek();
                    if (level.fFirstPosition.offset > offset
                            || level.fSecondPosition.offset < offset) return null;
                    if (level.fSecondPosition.offset == offset && length == 0)
                    // don't enter the character if if its the closing peer
                        return new ExitFlags(ILinkedModeListener.UPDATE_CARET, false);
                }
            }
            return null;
        }

        private boolean isMasked(int offset) {
            IDocument document = getSourceViewer().getDocument();
            try {
                return fEscapeCharacter == document.getChar(offset - 1);
            } catch (BadLocationException e) {
            }
            return false;
        }
    }

    private static class BracketLevel {

        int fOffset;
        int fLength;
        LinkedModeUI fUI;
        Position fFirstPosition;
        Position fSecondPosition;
    }

    /**
     * Position updater that takes any changes at the borders of a position to
     * not belong to the position.
     * 
     * @since 3.0
     */
    private static class ExclusivePositionUpdater implements IPositionUpdater {

        /** The position category. */
        private final String fCategory;

        /**
         * Creates a new updater for the given <code>category</code>.
         * 
         * @param category
         *            the new category.
         */
        public ExclusivePositionUpdater(String category) {
            fCategory = category;
        }

        /*
         * @see org.eclipse.jface.text.IPositionUpdater#update(org.eclipse.jface.text.DocumentEvent)
         */
        public void update(DocumentEvent event) {

            int eventOffset = event.getOffset();
            int eventOldLength = event.getLength();
            int eventNewLength = event.getText() == null ? 0 : event.getText().length();
            int deltaLength = eventNewLength - eventOldLength;

            try {
                Position[] positions = event.getDocument().getPositions(fCategory);

                for (int i = 0; i != positions.length; i++) {

                    Position position = positions[i];

                    if (position.isDeleted()) continue;

                    int offset = position.getOffset();
                    int length = position.getLength();
                    int end = offset + length;

                    if (offset >= eventOffset + eventOldLength)
                        // position comes
                        // after change - shift
                        position.setOffset(offset + deltaLength);
                    else if (end <= eventOffset) {
                        // position comes way before change -
                        // leave alone
                    } else if (offset <= eventOffset && end >= eventOffset + eventOldLength) {
                        // event completely internal to the position - adjust
                        // length
                        position.setLength(length + deltaLength);
                    } else if (offset < eventOffset) {
                        // event extends over end of position - adjust length
                        int newEnd = eventOffset;
                        position.setLength(newEnd - offset);
                    } else if (end > eventOffset + eventOldLength) {
                        // event extends from before position into it - adjust
                        // offset
                        // and length
                        // offset becomes end of event, length ajusted
                        // acordingly
                        int newOffset = eventOffset + eventNewLength;
                        position.setOffset(newOffset);
                        position.setLength(end - newOffset);
                    } else {
                        // event consumes the position - delete it
                        position.delete();
                    }
                }
            } catch (BadPositionCategoryException e) {
                // ignore and return
            }
        }

        /**
         * Returns the position category.
         * 
         * @return the position category
         */
        public String getCategory() {
            return fCategory;
        }

    }
    private class BracketInserter implements VerifyKeyListener, ILinkedModeListener {

        private boolean fCloseBrackets = true;
        private boolean fCloseStrings = true;
        private boolean fCloseBraces = true;
        private final String CATEGORY = toString();
        private IPositionUpdater fUpdater = new ExclusivePositionUpdater(CATEGORY);
        private Stack fBracketLevelStack = new Stack();

        public void setCloseBracketsEnabled(boolean enabled) {
            fCloseBrackets = enabled;
        }

        public void setCloseStringsEnabled(boolean enabled) {
            fCloseStrings = enabled;
        }
        
        public void setCloseBracesEnabled(boolean enabled) {
            fCloseBraces = enabled;
        }

        /*
         * @see org.eclipse.swt.custom.VerifyKeyListener#verifyKey(org.eclipse.swt.events.VerifyEvent)
         */
        public void verifyKey(VerifyEvent event) {
            // FIXME Why aren't we normally in SMART_INSERT mode like JDT?
            // early pruning to slow down normal typing as little as possible
            if (!event.doit /* || getInsertMode() != SMART_INSERT */) return;

            switch (event.character) {
            case '(':
            case '{':
            case '[':
            case '\'':
            case '\"':
                break;
            default:
                return;
            }

            final ISourceViewer sourceViewer = getSourceViewer();
            IDocument document = sourceViewer.getDocument();

            final Point selection = sourceViewer.getSelectedRange();
            final int offset = selection.x;
            final int length = selection.y;

            try {
                IRegion startLine = document.getLineInformationOfOffset(offset);
                IRegion endLine = document.getLineInformationOfOffset(offset + length);

                RubyHeuristicScanner scanner = new RubyHeuristicScanner(document);
                int nextToken = scanner.nextToken(offset + length, endLine.getOffset()
                        + endLine.getLength());
                String next = nextToken == Symbols.TokenEOF ? null : document.get(offset,
                        scanner.getPosition() - offset).trim();
                int prevToken = scanner.previousToken(offset - 1, startLine.getOffset());
                int prevTokenOffset = scanner.getPosition() + 1;
                String previous = prevToken == Symbols.TokenEOF ? null : document.get(
                        prevTokenOffset, offset - prevTokenOffset).trim();

                switch (event.character) {
                case '(':
                    if (!fCloseBrackets || nextToken == Symbols.TokenLPAREN
                            || nextToken == Symbols.TokenIDENT || next != null && next.length() > 1)
                        return;
                    break;

                case '{':
                    if (!fCloseBraces || nextToken == Symbols.TokenLBRACE
                            || nextToken == Symbols.TokenIDENT || next != null && next.length() > 1)
                        return;
                    break;

                case '[':
                    if (!fCloseBrackets || nextToken == Symbols.TokenIDENT || next != null
                            && next.length() > 1) return;
                    break;

                case '\'':
                case '"':
                    if (!fCloseStrings || nextToken == Symbols.TokenIDENT
                            /*|| prevToken == Symbols.TokenIDENT */ || next != null && next.length() > 1
                            /*|| previous != null && previous.length() > 1*/) return;
                    break;

                default:
                    return;
                }

                ITypedRegion partition = TextUtilities.getPartition(document,
                        IRubyPartitions.RUBY_PARTITIONING, offset - 1, true);
                if (!IDocument.DEFAULT_CONTENT_TYPE.equals(partition.getType())) return;

                if (!validateEditorInputState()) return;

                final char character = event.character;
                final char closingCharacter = getPeerCharacter(character);
                final StringBuffer buffer = new StringBuffer();
                buffer.append(character);
                buffer.append(closingCharacter);

                document.replace(offset, length, buffer.toString());

                BracketLevel level = new BracketLevel();
                fBracketLevelStack.push(level);

                LinkedPositionGroup group = new LinkedPositionGroup();
                group.addPosition(new LinkedPosition(document, offset + 1, 0,
                        LinkedPositionGroup.NO_STOP));

                LinkedModeModel model = new LinkedModeModel();
                model.addLinkingListener(this);
                model.addGroup(group);
                model.forceInstall();

                level.fOffset = offset;
                level.fLength = 2;

                // set up position tracking for our magic peers
                if (fBracketLevelStack.size() == 1) {
                    document.addPositionCategory(CATEGORY);
                    document.addPositionUpdater(fUpdater);
                }
                level.fFirstPosition = new Position(offset, 1);
                level.fSecondPosition = new Position(offset + 1, 1);
                document.addPosition(CATEGORY, level.fFirstPosition);
                document.addPosition(CATEGORY, level.fSecondPosition);

                level.fUI = new EditorLinkedModeUI(model, sourceViewer);
                level.fUI.setSimpleMode(true);
                level.fUI.setExitPolicy(new ExitPolicy(closingCharacter,
                        getEscapeCharacter(closingCharacter), fBracketLevelStack));
                level.fUI.setExitPosition(sourceViewer, offset + 2, 0, Integer.MAX_VALUE);
                level.fUI.setCyclingMode(LinkedModeUI.CYCLE_NEVER);
                level.fUI.enter();

                IRegion newSelection = level.fUI.getSelectedRegion();
                sourceViewer.setSelectedRange(newSelection.getOffset(), newSelection.getLength());

                event.doit = false;

            } catch (BadLocationException e) {
                RubyPlugin.log(e);
            } catch (BadPositionCategoryException e) {
                RubyPlugin.log(e);
            }
        }

        /*
         * @see org.eclipse.jface.text.link.ILinkedModeListener#left(org.eclipse.jface.text.link.LinkedModeModel,
         *      int)
         */
        public void left(LinkedModeModel environment, int flags) {

            final BracketLevel level = (BracketLevel) fBracketLevelStack.pop();

            if (flags != ILinkedModeListener.EXTERNAL_MODIFICATION) return;

            // remove brackets
            final ISourceViewer sourceViewer = getSourceViewer();
            final IDocument document = sourceViewer.getDocument();
            if (document instanceof IDocumentExtension) {
                IDocumentExtension extension = (IDocumentExtension) document;
                extension.registerPostNotificationReplace(null, new IDocumentExtension.IReplace() {

                    public void perform(IDocument d, IDocumentListener owner) {
                        if ((level.fFirstPosition.isDeleted || level.fFirstPosition.length == 0)
                                && !level.fSecondPosition.isDeleted
                                && level.fSecondPosition.offset == level.fFirstPosition.offset) {
                            try {
                                document.replace(level.fSecondPosition.offset,
                                        level.fSecondPosition.length, null);
                            } catch (BadLocationException e) {
                                RubyPlugin.log(e);
                            }
                        }

                        if (fBracketLevelStack.size() == 0) {
                            document.removePositionUpdater(fUpdater);
                            try {
                                document.removePositionCategory(CATEGORY);
                            } catch (BadPositionCategoryException e) {
                                RubyPlugin.log(e);
                            }
                        }
                    }
                });
            }

        }

        /*
         * @see org.eclipse.jface.text.link.ILinkedModeListener#suspend(org.eclipse.jface.text.link.LinkedModeModel)
         */
        public void suspend(LinkedModeModel environment) {
        }

        /*
         * @see org.eclipse.jface.text.link.ILinkedModeListener#resume(org.eclipse.jface.text.link.LinkedModeModel,
         *      int)
         */
        public void resume(LinkedModeModel environment, int flags) {
        }
    }

    public CaretPosition getCaretPosition() {
        // needed for positioning the cursor after formatting without selection

        StyledText styledText = this.getSourceViewer().getTextWidget();
        int caret = widgetOffset2ModelOffset(getSourceViewer(), styledText.getCaretOffset());
        IDocument document = getSourceViewer().getDocument();
        try {
            int line = document.getLineOfOffset(caret);
            int lineOffset = document.getLineOffset(line);
            return new CaretPosition(line, caret - lineOffset, caret);
        } catch (BadLocationException e) {
            return new CaretPosition(0, 0);
        }
    }
    
    /**
     * Returns the most narrow element including the given offset.  If <code>reconcile</code>
     * is <code>true</code> the editor's input element is reconciled in advance. If it is
     * <code>false</code> this method only returns a result if the editor's input element
     * does not need to be reconciled.
     *
     * @param offset the offset included by the retrieved element
     * @param reconcile <code>true</code> if working copy should be reconciled
     * @return the most narrow element which includes the given offset
     */
    protected IRubyElement getElementAt(int offset, boolean reconcile) {
        IWorkingCopyManager manager= RubyPlugin.getDefault().getWorkingCopyManager();
        IRubyScript unit= manager.getWorkingCopy(getEditorInput());

        if (unit != null) {
            try {
                if (reconcile) {
                    RubyModelUtil.reconcile(unit);
                    return unit.getElementAt(offset);
                } else if (unit.isConsistent())
                    return unit.getElementAt(offset);

            } catch (RubyModelException x) {
                if (!x.isDoesNotExist())
                RubyPlugin.log(x.getStatus());
                // nothing found, be tolerant and go on
            }
        }

        return null;
    }

    /*
     * @see RubyEditor#getElementAt(int)
     */
    protected IRubyElement getElementAt(int offset) {
        return getElementAt(offset, true);
    }
    
    /**
     * Jumps to the matching bracket.
     */
    public void gotoMatchingBracket() {

        ISourceViewer sourceViewer= getSourceViewer();
        IDocument document= sourceViewer.getDocument();
        if (document == null)
            return;

        IRegion selection= getSignedSelection(sourceViewer);

        int selectionLength= Math.abs(selection.getLength());
        if (selectionLength > 1) {
            setStatusLineErrorMessage(RubyEditorMessages.GotoMatchingBracket_error_invalidSelection);
            sourceViewer.getTextWidget().getDisplay().beep();
            return;
        }

        // #26314
        int sourceCaretOffset= selection.getOffset() + selection.getLength();
        if (isSurroundedByBrackets(document, sourceCaretOffset))
            sourceCaretOffset -= selection.getLength();

        IRegion region= fBracketMatcher.match(document, sourceCaretOffset);
        if (region == null) {
            setStatusLineErrorMessage(RubyEditorMessages.GotoMatchingBracket_error_noMatchingBracket);
            sourceViewer.getTextWidget().getDisplay().beep();
            return;
        }

        int offset= region.getOffset();
        int length= region.getLength();

        if (length < 1)
            return;

        int anchor= fBracketMatcher.getAnchor();
        // http://dev.eclipse.org/bugs/show_bug.cgi?id=34195
        int targetOffset= (ICharacterPairMatcher.RIGHT == anchor) ? offset + 1: offset + length;

        boolean visible= false;
        if (sourceViewer instanceof ITextViewerExtension5) {
            ITextViewerExtension5 extension= (ITextViewerExtension5) sourceViewer;
            visible= (extension.modelOffset2WidgetOffset(targetOffset) > -1);
        } else {
            IRegion visibleRegion= sourceViewer.getVisibleRegion();
            // http://dev.eclipse.org/bugs/show_bug.cgi?id=34195
            visible= (targetOffset >= visibleRegion.getOffset() && targetOffset <= visibleRegion.getOffset() + visibleRegion.getLength());
        }

        if (!visible) {
            setStatusLineErrorMessage(RubyEditorMessages.GotoMatchingBracket_error_bracketOutsideSelectedElement);
            sourceViewer.getTextWidget().getDisplay().beep();
            return;
        }

        if (selection.getLength() < 0)
            targetOffset -= selection.getLength();

        sourceViewer.setSelectedRange(targetOffset, selection.getLength());
        sourceViewer.revealRange(targetOffset, selection.getLength());
    }
    
    /**
     * Returns the signed current selection.
     * The length will be negative if the resulting selection
     * is right-to-left (RtoL).
     * <p>
     * The selection offset is model based.
     * </p>
     *
     * @param sourceViewer the source viewer
     * @return a region denoting the current signed selection, for a resulting RtoL selections length is < 0
     */
    protected IRegion getSignedSelection(ISourceViewer sourceViewer) {
        StyledText text= sourceViewer.getTextWidget();
        Point selection= text.getSelectionRange();

        if (text.getCaretOffset() == selection.x) {
            selection.x= selection.x + selection.y;
            selection.y= -selection.y;
        }

        selection.x= widgetOffset2ModelOffset(sourceViewer, selection.x);

        return new Region(selection.x, selection.y);
    }
    
    private static boolean isSurroundedByBrackets(IDocument document, int offset) {
        if (offset == 0 || offset == document.getLength())
            return false;

        try {
            return
                isBracket(document.getChar(offset - 1)) &&
                isBracket(document.getChar(offset));

        } catch (BadLocationException e) {
            return false;
        }
    }
    
    private static boolean isBracket(char character) {
        for (int i= 0; i != BRACKETS.length; ++i)
            if (character == BRACKETS[i])
                return true;
        return false;
    }
    
    /**
	 * Runner that will toggle folding either instantly (if the editor is
	 * visible) or the next time it becomes visible. If a runner is started when
	 * there is already one registered, the registered one is canceled as
	 * toggling folding twice is a no-op.
	 * <p>
	 * The access to the fFoldingRunner field is not thread-safe, it is assumed
	 * that <code>runWhenNextVisible</code> is only called from the UI thread.
	 * </p>
	 *
	 * @since 0.9.0
	 */
	private final class ToggleFoldingRunner implements IPartListener2 {
		/**
		 * The workbench page we registered the part listener with, or
		 * <code>null</code>.
		 */
		private IWorkbenchPage fPage;

		/**
		 * Does the actual toggling of projection.
		 */
		private void toggleFolding() {
			ISourceViewer sourceViewer= getSourceViewer();
			if (sourceViewer instanceof ProjectionViewer) {
				ProjectionViewer pv= (ProjectionViewer) sourceViewer;
				if (pv.isProjectionMode() != isFoldingEnabled()) {
					if (pv.canDoOperation(ProjectionViewer.TOGGLE))
						pv.doOperation(ProjectionViewer.TOGGLE);
				}
			}
		}

		/**
		 * Makes sure that the editor's folding state is correct the next time
		 * it becomes visible. If it already is visible, it toggles the folding
		 * state. If not, it either registers a part listener to toggle folding
		 * when the editor becomes visible, or cancels an already registered
		 * runner.
		 */
		public void runWhenNextVisible() {
			// if there is one already: toggling twice is the identity
			if (fFoldingRunner != null) {
				fFoldingRunner.cancel();
				return;
			}
			IWorkbenchPartSite site= getSite();
			if (site != null) {
				IWorkbenchPage page= site.getPage();
				if (!page.isPartVisible(RubyEditor.this)) {
					// if we're not visible - defer until visible
					fPage= page;
					fFoldingRunner= this;
					page.addPartListener(this);
					return;
				}
			}
			// we're visible - run now
			toggleFolding();
		}

		/**
		 * Remove the listener and clear the field.
		 */
		private void cancel() {
			if (fPage != null) {
				fPage.removePartListener(this);
				fPage= null;
			}
			if (fFoldingRunner == this)
				fFoldingRunner= null;
		}

		/*
		 * @see org.eclipse.ui.IPartListener2#partVisible(org.eclipse.ui.IWorkbenchPartReference)
		 */
		public void partVisible(IWorkbenchPartReference partRef) {
			if (RubyEditor.this.equals(partRef.getPart(false))) {
				cancel();
				toggleFolding();
			}
		}

		/*
		 * @see org.eclipse.ui.IPartListener2#partClosed(org.eclipse.ui.IWorkbenchPartReference)
		 */
		public void partClosed(IWorkbenchPartReference partRef) {
			if (RubyEditor.this.equals(partRef.getPart(false))) {
				cancel();
			}
		}

		public void partActivated(IWorkbenchPartReference partRef) {}
		public void partBroughtToTop(IWorkbenchPartReference partRef) {}
		public void partDeactivated(IWorkbenchPartReference partRef) {}
		public void partOpened(IWorkbenchPartReference partRef) {}
		public void partHidden(IWorkbenchPartReference partRef) {}
		public void partInputChanged(IWorkbenchPartReference partRef) {}
	}
	
	interface ITextConverter {
		void customizeDocumentCommand(IDocument document, DocumentCommand command);
	}
	
	public static class TabConverter implements ITextConverter {

		private int fTabRatio;
		private ILineTracker fLineTracker;

		public TabConverter() {
		}

		public void setNumberOfSpacesPerTab(int ratio) {
			fTabRatio= ratio;
		}

		public void setLineTracker(ILineTracker lineTracker) {
			fLineTracker= lineTracker;
		}

		private int insertTabString(StringBuffer buffer, int offsetInLine) {

			if (fTabRatio == 0)
				return 0;

			int remainder= offsetInLine % fTabRatio;
			remainder= fTabRatio - remainder;
			for (int i= 0; i < remainder; i++)
				buffer.append(' ');
			return remainder;
		}

		public void customizeDocumentCommand(IDocument document, DocumentCommand command) {
			String text= command.text;
			if (text == null)
				return;

			int index= text.indexOf('\t');
			if (index > -1) {

				StringBuffer buffer= new StringBuffer();

				fLineTracker.set(command.text);
				int lines= fLineTracker.getNumberOfLines();

				try {

						for (int i= 0; i < lines; i++) {

							int offset= fLineTracker.getLineOffset(i);
							int endOffset= offset + fLineTracker.getLineLength(i);
							String line= text.substring(offset, endOffset);

							int position= 0;
							if (i == 0) {
								IRegion firstLine= document.getLineInformationOfOffset(command.offset);
								position= command.offset - firstLine.getOffset();
							}

							int length= line.length();
							for (int j= 0; j < length; j++) {
								char c= line.charAt(j);
								if (c == '\t') {
									position += insertTabString(buffer, position);
								} else {
									buffer.append(c);
									++ position;
								}
							}

						}

						command.text= buffer.toString();

				} catch (BadLocationException x) {
				}
			}
		}
	}

	/**
	 * Collapses all foldable members if supported by the folding
	 * structure provider.
	 * 
	 * @since 0.9.0
	 */
	public void collapseMembers() {
		if (fProjectionModelUpdater instanceof IRubyFoldingStructureProviderExtension) {
			IRubyFoldingStructureProviderExtension extension= (IRubyFoldingStructureProviderExtension) fProjectionModelUpdater;
			extension.collapseMembers();
		}
	}
	
	/**
	 * Collapses all foldable comments if supported by the folding
	 * structure provider.
	 * 
	 * @since 0.9.0
	 */
	public void collapseComments() {
		if (fProjectionModelUpdater instanceof IRubyFoldingStructureProviderExtension) {
			IRubyFoldingStructureProviderExtension extension= (IRubyFoldingStructureProviderExtension) fProjectionModelUpdater;
			extension.collapseComments();
		}
	}

	public FoldingActionGroup getFoldingActionGroup() {
		return fFoldingGroup;
	}
	
    private int getTabSize() {
		IRubyElement element= getInputRubyElement();
		IRubyProject project= element == null ? null : element.getRubyProject();
		return CodeFormatterUtil.getTabWidth(project);
	}

	private void startTabConversion() {
		if (fTabConverter == null) {
			fTabConverter= new TabConverter();
			configureTabConverter();
			fTabConverter.setNumberOfSpacesPerTab(getTabSize());
			AdaptedSourceViewer asv= (AdaptedSourceViewer) getSourceViewer();
			asv.addTextConverter(fTabConverter);
			// http://dev.eclipse.org/bugs/show_bug.cgi?id=19270
			asv.updateIndentationPrefixes();
		}
	}
	
	private void configureTabConverter() {
		if (fTabConverter != null) {
			IDocumentProvider provider= getDocumentProvider();
			if (provider instanceof IRubyScriptDocumentProvider) {
				IRubyScriptDocumentProvider cup= (IRubyScriptDocumentProvider) provider;
				fTabConverter.setLineTracker(cup.createLineTracker(getEditorInput()));
			}
		}
	}
	

	private void stopTabConversion() {
		if (fTabConverter != null) {
			AdaptedSourceViewer asv= (AdaptedSourceViewer) getSourceViewer();
			asv.removeTextConverter(fTabConverter);
			// http://dev.eclipse.org/bugs/show_bug.cgi?id=19270
			asv.updateIndentationPrefixes();
			fTabConverter= null;
		}
	}
	
	private boolean isTabConversionEnabled() {
		IRubyElement element= getInputRubyElement();
		IRubyProject project= element == null ? null : element.getRubyProject();
		String option;
		if (project == null)
			option= RubyCore.getOption(SPACES_FOR_TABS);
		else
			option= project.getOption(SPACES_FOR_TABS, true);
		return RubyCore.SPACE.equals(option);
	}
	
	/**
	 * Tells whether override indicators are shown.
	 *
	 * @return <code>true</code> if the override indicators are shown
	 * @since 3.0
	 */
	protected boolean isShowingOverrideIndicators() {
		AnnotationPreference preference= getAnnotationPreferenceLookup().getAnnotationPreference(OverrideIndicatorManager.ANNOTATION_TYPE);
		IPreferenceStore store= getPreferenceStore();
		return getBoolean(store, preference.getHighlightPreferenceKey())
			|| getBoolean(store, preference.getVerticalRulerPreferenceKey())
			|| getBoolean(store, preference.getOverviewRulerPreferenceKey())
			|| getBoolean(store, preference.getTextPreferenceKey());
	}
	
	/**
	 * Returns the boolean preference for the given key.
	 *
	 * @param store the preference store
	 * @param key the preference key
	 * @return <code>true</code> if the key exists in the store and its value is <code>true</code>
	 * @since 3.0
	 */
	private boolean getBoolean(IPreferenceStore store, String key) {
		return key != null && store.getBoolean(key);
	}
	
	protected void installOverrideIndicator(boolean provideAST) {
		uninstallOverrideIndicator();
		IAnnotationModel model= getDocumentProvider().getAnnotationModel(getEditorInput());
		final IRubyElement inputElement= getInputRubyElement();

		if (model == null || inputElement == null)
			return;

		fOverrideIndicatorManager= new OverrideIndicatorManager(model, inputElement, null);

		if (provideAST) {
			Job job= new Job("Installing override indicators...") {
				/*
				 * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
				 * @since 3.0
				 */
				protected IStatus run(IProgressMonitor monitor) {
					if (fOverrideIndicatorManager != null) // editor might have been closed in the meanwhile
						fOverrideIndicatorManager.reconciled((IRubyScript)inputElement, null, true, monitor);
					return Status.OK_STATUS;
				}
			};
			job.setPriority(Job.DECORATE);
			job.setSystem(true);
			job.schedule();
		}
		
		if (fOverrideIndicatorManager == null)
			return;

		addReconcileListener(fOverrideIndicatorManager);
	}
	
	/**
	 * Adds the given listener.
	 * Has no effect if an identical listener was not already registered.
	 *
	 * @param listener	The reconcile listener to be added
	 * @since 3.0
	 */
	final void addReconcileListener(IRubyReconcilingListener listener) {
		synchronized (fReconcilingListeners) {
			fReconcilingListeners.add(listener);
		}
	}
	
	protected void uninstallOverrideIndicator() {
		if (fOverrideIndicatorManager != null) {
			fOverrideIndicatorManager.removeAnnotations();
			fOverrideIndicatorManager= null;
		}
	}
	
	/**
	 * Determines whether the preference change encoded by the given event
	 * changes the override indication.
	 *
	 * @param event the event to be investigated
	 * @return <code>true</code> if event causes a change
	 * @since 3.0
	 */
	protected boolean affectsOverrideIndicatorAnnotations(PropertyChangeEvent event) {
		String key= event.getProperty();
		AnnotationPreference preference= getAnnotationPreferenceLookup().getAnnotationPreference(OverrideIndicatorManager.ANNOTATION_TYPE);
		if (key == null || preference == null)
			return false;

		return key.equals(preference.getHighlightPreferenceKey())
			|| key.equals(preference.getVerticalRulerPreferenceKey())
			|| key.equals(preference.getOverviewRulerPreferenceKey())
			|| key.equals(preference.getTextPreferenceKey());
	}
	
	/*
	 * @see org.rubypeople.rdt.internal.ui.text.ruby.IRubyReconcilingListener#aboutToBeReconciled()
	 * @since 1.0
	 */
	public void aboutToBeReconciled() {

		// Notify AST provider
		RubyPlugin.getDefault().getASTProvider().aboutToBeReconciled(getInputRubyElement());

		// Notify listeners
		Object[] listeners = fReconcilingListeners.getListeners();
		for (int i = 0, length= listeners.length; i < length; ++i)
			((IRubyReconcilingListener)listeners[i]).aboutToBeReconciled();
	}

	/*
	 * @see org.rubypeople.rdt.internal.ui.text.ruby.IRubyReconcilingListener#reconciled(RootNode, boolean, IProgressMonitor)
	 * @since 1.0
	 */
	public void reconciled(IRubyScript script, RootNode ast, boolean forced, IProgressMonitor progressMonitor) {
		
		// see: https://bugs.eclipse.org/bugs/show_bug.cgi?id=58245
		RubyPlugin javaPlugin= RubyPlugin.getDefault();
		if (javaPlugin == null)
			return;
		
		// Always notify AST provider
		javaPlugin.getASTProvider().reconciled(ast, getInputRubyElement(), progressMonitor);

		// Notify listeners
		Object[] listeners = fReconcilingListeners.getListeners();
		for (int i = 0, length= listeners.length; i < length; ++i)
			((IRubyReconcilingListener)listeners[i]).reconciled(script, ast, forced, progressMonitor);

		// Update Ruby Outline page selection
		if (!forced && !progressMonitor.isCanceled()) {
			Shell shell= getSite().getShell();
			if (shell != null && !shell.isDisposed()) {
				shell.getDisplay().asyncExec(new Runnable() {
					public void run() {
						selectionChanged();
					}
				});
			}
		}
	}
	
	/**
	 * React to changed selection.
	 *
	 * @since 3.0
	 */
	protected void selectionChanged() {
		if (getSelectionProvider() == null)
			return;
		// FIXME Uncomment
//		ISourceReference element= computeHighlightRangeSourceReference();
//		if (getPreferenceStore().getBoolean(PreferenceConstants.EDITOR_SYNC_OUTLINE_ON_CURSOR_MOVE))
//			synchronizeOutlinePage(element);
//		setSelection(element, false);
//		if (!fSelectionChangedViaGotoAnnotation)
//			updateStatusLine();
//		fSelectionChangedViaGotoAnnotation= false;
	}
	
	/**
	 * This action behaves in two different ways: If there is no current text
	 * hover, the javadoc is displayed using information presenter. If there is
	 * a current text hover, it is converted into a information presenter in
	 * order to make it sticky.
	 */
	class InformationDispatchAction extends TextEditorAction {

		/** The wrapped text operation action. */
		private final TextOperationAction fTextOperationAction;

		/**
		 * Creates a dispatch action.
		 *
		 * @param resourceBundle the resource bundle
		 * @param prefix the prefix
		 * @param textOperationAction the text operation action
		 */
		public InformationDispatchAction(ResourceBundle resourceBundle, String prefix, final TextOperationAction textOperationAction) {
			super(resourceBundle, prefix, RubyEditor.this);
			if (textOperationAction == null)
				throw new IllegalArgumentException();
			fTextOperationAction= textOperationAction;
		}

		/*
		 * @see org.eclipse.jface.action.IAction#run()
		 */
		public void run() {

			ISourceViewer sourceViewer= getSourceViewer();
			if (sourceViewer == null) {
				fTextOperationAction.run();
				return;
			}

			if (sourceViewer instanceof ITextViewerExtension4)  {
				ITextViewerExtension4 extension4= (ITextViewerExtension4) sourceViewer;
				if (extension4.moveFocusToWidgetToken())
					return;
			}

			if (sourceViewer instanceof ITextViewerExtension2) {
				// does a text hover exist?
				ITextHover textHover= ((ITextViewerExtension2) sourceViewer).getCurrentTextHover();
				if (textHover != null && makeTextHoverFocusable(sourceViewer, textHover))
					return;
			}

			if (sourceViewer instanceof ISourceViewerExtension3) {
				// does an annotation hover exist?
				IAnnotationHover annotationHover= ((ISourceViewerExtension3) sourceViewer).getCurrentAnnotationHover();
				if (annotationHover != null && makeAnnotationHoverFocusable(sourceViewer, annotationHover))
					return;
			}
			
			// otherwise, just run the action
			fTextOperationAction.run();
		}

		/**
		 * Tries to make a text hover focusable (or "sticky").
		 * 
		 * @param sourceViewer the source viewer to display the hover over
		 * @param textHover the hover to make focusable
		 * @return <code>true</code> if successful, <code>false</code> otherwise
		 * @since 3.2
		 */
		private boolean makeTextHoverFocusable(ISourceViewer sourceViewer, ITextHover textHover) {
			Point hoverEventLocation= ((ITextViewerExtension2) sourceViewer).getHoverEventLocation();
			int offset= computeOffsetAtLocation(sourceViewer, hoverEventLocation.x, hoverEventLocation.y);
			if (offset == -1)
				return false;
			
			try {
				IRegion hoverRegion= textHover.getHoverRegion(sourceViewer, offset);
				if (hoverRegion == null)
					return false;

				String hoverInfo= textHover.getHoverInfo(sourceViewer, hoverRegion);

				IInformationControlCreator controlCreator= null;
				if (textHover instanceof IInformationProviderExtension2)
					controlCreator= ((IInformationProviderExtension2)textHover).getInformationPresenterControlCreator();

				IInformationProvider informationProvider= new InformationProvider(hoverRegion, hoverInfo, controlCreator);

				fInformationPresenter.setOffset(offset);
				fInformationPresenter.setAnchor(AbstractInformationControlManager.ANCHOR_BOTTOM);
				fInformationPresenter.setMargins(6, 6); // default values from AbstractInformationControlManager
				String contentType= TextUtilities.getContentType(sourceViewer.getDocument(), IRubyPartitions.RUBY_PARTITIONING, offset, true);
				fInformationPresenter.setInformationProvider(informationProvider, contentType);
				fInformationPresenter.showInformation();

				return true;

			} catch (BadLocationException e) {
				return false;
			}
		}

		/**
		 * Tries to make an annotation hover focusable (or "sticky").
		 * 
		 * @param sourceViewer the source viewer to display the hover over
		 * @param annotationHover the hover to make focusable
		 * @return <code>true</code> if successful, <code>false</code> otherwise
		 * @since 3.2
		 */
		private boolean makeAnnotationHoverFocusable(ISourceViewer sourceViewer, IAnnotationHover annotationHover) {
			IVerticalRulerInfo info= getVerticalRuler();
			int line= info.getLineOfLastMouseButtonActivity();
			if (line == -1)
				return false;

			try {

				// compute the hover information
				Object hoverInfo;
				if (annotationHover instanceof IAnnotationHoverExtension) {
					IAnnotationHoverExtension extension= (IAnnotationHoverExtension) annotationHover;
					ILineRange hoverLineRange= extension.getHoverLineRange(sourceViewer, line);
					if (hoverLineRange == null)
						return false;
					final int maxVisibleLines= Integer.MAX_VALUE; // allow any number of lines being displayed, as we support scrolling
					hoverInfo= extension.getHoverInfo(sourceViewer, hoverLineRange, maxVisibleLines);
				} else {
					hoverInfo= annotationHover.getHoverInfo(sourceViewer, line);
				}
				
				// hover region: the beginning of the concerned line to place the control right over the line
				IDocument document= sourceViewer.getDocument();
				int offset= document.getLineOffset(line);
				String contentType= TextUtilities.getContentType(document, IRubyPartitions.RUBY_PARTITIONING, offset, true);

				IInformationControlCreator controlCreator= null;
				
				/* 
				 * XXX: This is a hack to avoid API changes at the end of 3.2,
				 * and should be fixed for 3.3, see: https://bugs.eclipse.org/bugs/show_bug.cgi?id=137967
				 */
				if ("org.eclipse.jface.text.source.projection.ProjectionAnnotationHover".equals(annotationHover.getClass().getName())) { //$NON-NLS-1$
					controlCreator= new IInformationControlCreator() {
						public IInformationControl createInformationControl(Shell shell) {
							int shellStyle= SWT.RESIZE | SWT.TOOL | getOrientation();
							int style= SWT.V_SCROLL | SWT.H_SCROLL;
							return new SourceViewerInformationControl(shell, shellStyle, style);
						}
					};
					
				} else {
					if (annotationHover instanceof IInformationProviderExtension2)
						controlCreator= ((IInformationProviderExtension2) annotationHover).getInformationPresenterControlCreator();
					else if (annotationHover instanceof IAnnotationHoverExtension)
						controlCreator= ((IAnnotationHoverExtension) annotationHover).getHoverControlCreator();
				}
				
				IInformationProvider informationProvider= new InformationProvider(new Region(offset, 0), hoverInfo, controlCreator);

				fInformationPresenter.setOffset(offset);
				fInformationPresenter.setAnchor(AbstractInformationControlManager.ANCHOR_RIGHT);
				fInformationPresenter.setMargins(4, 0); // AnnotationBarHoverManager sets (5,0), minus SourceViewer.GAP_SIZE_1
				fInformationPresenter.setInformationProvider(informationProvider, contentType);
				fInformationPresenter.showInformation();

				return true;

			} catch (BadLocationException e) {
				return false;
			}
        }

		// modified version from TextViewer
		private int computeOffsetAtLocation(ITextViewer textViewer, int x, int y) {

			StyledText styledText= textViewer.getTextWidget();
			IDocument document= textViewer.getDocument();

			if (document == null)
				return -1;

			try {
				int widgetOffset= styledText.getOffsetAtLocation(new Point(x, y));
				Point p= styledText.getLocationAtOffset(widgetOffset);
				if (p.x > x)
					widgetOffset--;
				
				if (textViewer instanceof ITextViewerExtension5) {
					ITextViewerExtension5 extension= (ITextViewerExtension5) textViewer;
					return extension.widgetOffset2ModelOffset(widgetOffset);
				} else {
					IRegion visibleRegion= textViewer.getVisibleRegion();
					return widgetOffset + visibleRegion.getOffset();
				}
			} catch (IllegalArgumentException e) {
				return -1;
			}

		}
	}
	
	/**
	 * Information provider used to present focusable information shells.
	 *
	 * @since 3.2
	 */
	private static final class InformationProvider implements IInformationProvider, IInformationProviderExtension, IInformationProviderExtension2 {
		
		private IRegion fHoverRegion;
		private Object fHoverInfo;
		private IInformationControlCreator fControlCreator;
		
		InformationProvider(IRegion hoverRegion, Object hoverInfo, IInformationControlCreator controlCreator) {
			fHoverRegion= hoverRegion;
			fHoverInfo= hoverInfo;
			fControlCreator= controlCreator;
		}
		/*
		 * @see org.eclipse.jface.text.information.IInformationProvider#getSubject(org.eclipse.jface.text.ITextViewer, int)
		 */
		public IRegion getSubject(ITextViewer textViewer, int invocationOffset) {
			return fHoverRegion;
		}
		/*
		 * @see org.eclipse.jface.text.information.IInformationProvider#getInformation(org.eclipse.jface.text.ITextViewer, org.eclipse.jface.text.IRegion)
		 */
		public String getInformation(ITextViewer textViewer, IRegion subject) {
			return fHoverInfo.toString();
		}
		/*
		 * @see org.eclipse.jface.text.information.IInformationProviderExtension#getInformation2(org.eclipse.jface.text.ITextViewer, org.eclipse.jface.text.IRegion)
		 * @since 3.2
		 */
		public Object getInformation2(ITextViewer textViewer, IRegion subject) {
			return fHoverInfo;
		}
		/*
		 * @see org.eclipse.jface.text.information.IInformationProviderExtension2#getInformationPresenterControlCreator()
		 */
		public IInformationControlCreator getInformationPresenterControlCreator() {
			return fControlCreator;
		}
	}
}