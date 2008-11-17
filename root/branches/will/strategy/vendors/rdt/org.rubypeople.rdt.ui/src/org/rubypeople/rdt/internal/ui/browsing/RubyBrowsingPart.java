package org.rubypeople.rdt.internal.ui.browsing;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.util.Assert;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.util.TransferDragSourceListener;
import org.eclipse.jface.util.TransferDropTargetListener;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.IOpenListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.OpenEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.search.ui.IContextMenuConstants;
import org.eclipse.search.ui.ISearchResultView;
import org.eclipse.search.ui.ISearchResultViewPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.IWorkingSetManager;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionContext;
import org.eclipse.ui.actions.ActionGroup;
import org.eclipse.ui.part.IShowInSource;
import org.eclipse.ui.part.ResourceTransfer;
import org.eclipse.ui.part.ShowInContext;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.views.navigator.LocalSelectionTransfer;
import org.osgi.framework.Bundle;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IRubyScript;
import org.rubypeople.rdt.core.IType;
import org.rubypeople.rdt.core.RubyCore;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.internal.corext.util.Messages;
import org.rubypeople.rdt.internal.ui.RubyPlugin;
import org.rubypeople.rdt.internal.ui.actions.CompositeActionGroup;
import org.rubypeople.rdt.internal.ui.actions.NewWizardsActionGroup;
import org.rubypeople.rdt.internal.ui.dnd.DelegatingDropAdapter;
import org.rubypeople.rdt.internal.ui.dnd.RdtViewerDragAdapter;
import org.rubypeople.rdt.internal.ui.packageview.SelectionTransferDragAdapter;
import org.rubypeople.rdt.internal.ui.packageview.SelectionTransferDropAdapter;
import org.rubypeople.rdt.internal.ui.viewsupport.AppearanceAwareLabelProvider;
import org.rubypeople.rdt.internal.ui.viewsupport.DecoratingRubyLabelProvider;
import org.rubypeople.rdt.internal.ui.viewsupport.ProblemTableViewer;
import org.rubypeople.rdt.internal.ui.viewsupport.RubyElementImageProvider;
import org.rubypeople.rdt.internal.ui.viewsupport.RubyUILabelProvider;
import org.rubypeople.rdt.internal.ui.viewsupport.StatusBarUpdater;
import org.rubypeople.rdt.internal.ui.workingsets.WorkingSetFilterActionGroup;
import org.rubypeople.rdt.ui.IWorkingCopyManager;
import org.rubypeople.rdt.ui.PreferenceConstants;
import org.rubypeople.rdt.ui.RubyElementLabelProvider;
import org.rubypeople.rdt.ui.RubyElementLabels;
import org.rubypeople.rdt.ui.RubyElementSorter;
import org.rubypeople.rdt.ui.StandardRubyElementContentProvider;
import org.rubypeople.rdt.ui.actions.BuildActionGroup;
import org.rubypeople.rdt.ui.actions.CCPActionGroup;
import org.rubypeople.rdt.ui.actions.CustomFiltersActionGroup;
import org.rubypeople.rdt.ui.actions.OpenEditorActionGroup;
import org.rubypeople.rdt.ui.actions.OpenViewActionGroup;
import org.rubypeople.rdt.ui.actions.RubySearchActionGroup;

abstract class RubyBrowsingPart extends ViewPart implements
		ISelectionListener, IMenuListener {
	
	private static final String TAG_SELECTED_ELEMENTS= "selectedElements"; //$NON-NLS-1$
	private static final String TAG_SELECTED_ELEMENT= "selectedElement"; //$NON-NLS-1$
	private static final String TAG_SELECTED_ELEMENT_PATH= "selectedElementPath"; //$NON-NLS-1$

	private StructuredViewer fViewer;
	private IMemento fMemento;
	private RubyUILabelProvider fLabelProvider;
	protected IWorkbenchPart fPreviousSelectionProvider;
	protected Object fPreviousSelectedElement;
	private ILabelProvider fTitleProvider;
	
//	 Actions
	private WorkingSetFilterActionGroup fWorkingSetFilterActionGroup;
	private boolean fHasWorkingSetFilter= true;
	private boolean fHasCustomFilter= true;
	private OpenEditorActionGroup fOpenEditorGroup;
	protected CompositeActionGroup fActionGroups;
	private ToggleLinkingAction fToggleLinkingAction;
	
	// Filters
	private CustomFiltersActionGroup fCustomFiltersActionGroup;
	
	// Linking
	private boolean fLinkingEnabled;

	/*
	 * Ensure selection changed events being processed only if initiated by user
	 * interaction with this part.
	 */
	private boolean fProcessSelectionEvents = true;

	private RubyElementTypeComparator fTypeComparator;

	private IPartListener2 fPartListener = new IPartListener2() {
		public void partActivated(IWorkbenchPartReference ref) {
		}

		public void partBroughtToTop(IWorkbenchPartReference ref) {
		}

		public void partInputChanged(IWorkbenchPartReference ref) {
		}

		public void partClosed(IWorkbenchPartReference ref) {
		}

		public void partDeactivated(IWorkbenchPartReference ref) {
		}

		public void partOpened(IWorkbenchPartReference ref) {
		}

		public void partVisible(IWorkbenchPartReference ref) {
			if (ref != null && ref.getId() == getSite().getId()) {
				fProcessSelectionEvents = true;
				IWorkbenchPage page = getSite().getWorkbenchWindow()
						.getActivePage();
				if (page != null)
					selectionChanged(page.getActivePart(), page.getSelection());
			}
		}

		public void partHidden(IWorkbenchPartReference ref) {
			if (ref != null && ref.getId() == getSite().getId())
				fProcessSelectionEvents = false;
		}
	};
	private CCPActionGroup fCCPActionGroup;
	private BuildActionGroup fBuildActionGroup;	
	
	public RubyBrowsingPart() {
		super();
		initLinkingEnabled();
	}
	
	protected void createActions() {
		fActionGroups= new CompositeActionGroup(new ActionGroup[] {
				new NewWizardsActionGroup(this.getSite()),
				fOpenEditorGroup= new OpenEditorActionGroup(this),
				new OpenViewActionGroup(this),
				fCCPActionGroup= new CCPActionGroup(this),
//				new GenerateActionGroup(this),
//				new RefactorActionGroup(this),
//				new ImportActionGroup(this),
				fBuildActionGroup= new BuildActionGroup(this),
				new RubySearchActionGroup(this)});


		if (fHasWorkingSetFilter) {
			String viewId= getConfigurationElement().getAttribute("id"); //$NON-NLS-1$
			Assert.isNotNull(viewId);
			IPropertyChangeListener workingSetListener= new IPropertyChangeListener() {
				public void propertyChange(PropertyChangeEvent event) {
					doWorkingSetChanged(event);
				}
			};
			fWorkingSetFilterActionGroup= new WorkingSetFilterActionGroup(getSite(), workingSetListener);
			fViewer.addFilter(fWorkingSetFilterActionGroup.getWorkingSetFilter());
		}

//		 Custom filter group
		if (fHasCustomFilter)
			fCustomFiltersActionGroup= new CustomFiltersActionGroup(this, fViewer);
		
		fToggleLinkingAction= new ToggleLinkingAction(this);
	}
	
	/**
	 * Adds filters the viewer of this part.
	 */
	protected void addFilters() {
		// default is to have no filters
	}
	
	/**
	 * Adds the KeyListener
	 */
	protected void addKeyListener() {
		fViewer.getControl().addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent event) {
				handleKeyReleased(event);
			}
		});
	}
	
	protected void handleKeyReleased(KeyEvent event) {
		if (event.stateMask != 0)
			return;

		int key= event.keyCode;
		if (key == SWT.F5) {
			IAction action= fBuildActionGroup.getRefreshAction();
			if (action.isEnabled())
				action.run();
		}
	}
	
	protected void initDragAndDrop() {
		int ops= DND.DROP_COPY | DND.DROP_MOVE | DND.DROP_LINK;
		// drop
		Transfer[] dropTransfers= new Transfer[] {
			LocalSelectionTransfer.getInstance()
		};
		TransferDropTargetListener[] dropListeners= new TransferDropTargetListener[] {
			new SelectionTransferDropAdapter(fViewer)
		};
		fViewer.addDropSupport(ops | DND.DROP_DEFAULT, dropTransfers, new DelegatingDropAdapter(dropListeners));

		// Drag
		Transfer[] dragTransfers= new Transfer[] {
			LocalSelectionTransfer.getInstance(),
			ResourceTransfer.getInstance()};
		TransferDragSourceListener[] dragListeners= new TransferDragSourceListener[] {
			new SelectionTransferDragAdapter(fViewer)/*,
			new ResourceTransferDragAdapter(fViewer)*/  //Commented out by Will to fix compile error.  Not used in Photon.
		};
		fViewer.addDragSupport(ops, dragTransfers, new RdtViewerDragAdapter(fViewer, dragListeners));
	}
	
	protected void createContextMenu() {
		MenuManager menuManager= new MenuManager("#PopupMenu"); //$NON-NLS-1$
		menuManager.setRemoveAllWhenShown(true);
		menuManager.addMenuListener(this);
		Menu contextMenu= menuManager.createContextMenu(fViewer.getControl());
		fViewer.getControl().setMenu(contextMenu);
		getSite().registerContextMenu(menuManager, fViewer);
	}
	
	private void doWorkingSetChanged(PropertyChangeEvent event) {
		String property= event.getProperty();
		if (IWorkingSetManager.CHANGE_WORKING_SET_NAME_CHANGE.equals(property))
			updateTitle();
		else	if (IWorkingSetManager.CHANGE_WORKING_SET_CONTENT_CHANGE.equals(property)) {
			updateTitle();
			fViewer.getControl().setRedraw(false);
			fViewer.refresh();
			fViewer.getControl().setRedraw(true);
		}

	}
	
	/**
	 * Adds additional listeners to this view.
	 * This method can be overridden but should
	 * call super.
	 */
	protected void hookViewerListeners() {
		fViewer.addOpenListener(new IOpenListener() {
			public void open(OpenEvent event) {
				IAction open= fOpenEditorGroup.getOpenAction();
				if (open.isEnabled()) {
					open.run();
					restoreSelection();
				}
			}
		});
	}
	
	void setHasCustomSetFilter(boolean state) {
		fHasCustomFilter= state;
	}
	
	protected boolean hasCustomFilter() {
		return fHasCustomFilter;
	}
	
	protected void setCustomFiltersActionGroup(CustomFiltersActionGroup customFiltersActionGroup) {
		fCustomFiltersActionGroup= customFiltersActionGroup;
	}
	
	void restoreSelection() {
		// Default is to do nothing
	}
	
	protected void setOpenEditorGroup(OpenEditorActionGroup openEditorGroup) {
		fOpenEditorGroup= openEditorGroup;
	}

	protected OpenEditorActionGroup getOpenEditorGroup() {
		return fOpenEditorGroup;
	}

	public void createPartControl(Composite parent) {
		Assert.isTrue(fViewer == null);

		fViewer = createViewer(parent);
		

		fTypeComparator = new RubyElementTypeComparator();

		fLabelProvider = createLabelProvider();
		fViewer
				.setLabelProvider(createDecoratingLabelProvider(fLabelProvider));
		fViewer.setSorter(createRubyElementSorter());
		fViewer.setUseHashlookup(true);
		fTitleProvider= createTitleProvider();

		getSite().setSelectionProvider(fViewer);
		
		if (fMemento != null) { // initialize linking state before creating the actions
			restoreLinkingEnabled(fMemento);
		}
		createActions(); // call before registering for selection changes
		
		if (fMemento != null)
			restoreState(fMemento);
		
		getSite().setSelectionProvider(fViewer);
		
		hookViewerListeners();
		
		fViewer.setContentProvider(createContentProvider());
		setInitialInput();

		// Initialize selection
		// TODO Use selection from editor, etc
		setInitialSelection();

		// Listen to page changes
		getViewSite().getPage().addPostSelectionListener(this);
		getViewSite().getPage().addPartListener(fPartListener);
		
		fillActionBars(getViewSite().getActionBars());
	}
	
	protected StatusBarUpdater createStatusBarUpdater(IStatusLineManager slManager) {
		return new StatusBarUpdater(slManager);
	}
	
	protected ILabelProvider createTitleProvider() {
		return new RubyElementLabelProvider(RubyElementLabelProvider.SHOW_BASICS | RubyElementLabelProvider.SHOW_SMALL_ICONS);
	}
	
	/*
	 * Implements method from IViewPart.
	 */
	public void init(IViewSite site, IMemento memento) throws PartInitException {
		super.init(site, memento);
		fMemento= memento;
	}
	
	private void restoreLinkingEnabled(IMemento memento) {
		Integer val= memento.getInteger(getLinkToEditorKey());
		if (val != null) {
			fLinkingEnabled= val.intValue() != 0;
		}
	}
	
	private void saveSelectionState(IMemento memento) {
		Object elements[]= ((IStructuredSelection) fViewer.getSelection()).toArray();
		if (elements.length > 0) {
			IMemento selectionMem= memento.createChild(TAG_SELECTED_ELEMENTS);
			for (int i= 0; i < elements.length; i++) {
				IMemento elementMem= selectionMem.createChild(TAG_SELECTED_ELEMENT);
				Object o= elements[i];
				if (o instanceof IRubyElement)
					elementMem.putString(TAG_SELECTED_ELEMENT_PATH, ((IRubyElement) elements[i]).getHandleIdentifier());
			}
		}
	}
	
	protected void restoreState(IMemento memento) {
		if (fHasWorkingSetFilter)
			fWorkingSetFilterActionGroup.restoreState(memento);
		if (fHasCustomFilter)
			fCustomFiltersActionGroup.restoreState(memento);

		if (fHasCustomFilter /*|| fHasWorkingSetFilter*/) {
			fViewer.getControl().setRedraw(false);
			fViewer.refresh();
			fViewer.getControl().setRedraw(true);
		}
	}
	
	protected StructuredViewer createViewer(Composite parent) {
		return new ProblemTableViewer(parent);
	}

	protected void fillActionBars(IActionBars actionBars) {
		IToolBarManager toolBar= actionBars.getToolBarManager();
		fillToolBar(toolBar);


		if (fHasWorkingSetFilter)
			fWorkingSetFilterActionGroup.fillActionBars(getViewSite().getActionBars());

		actionBars.updateActionBars();

		fActionGroups.fillActionBars(actionBars);

		if (fHasCustomFilter)
			fCustomFiltersActionGroup.fillActionBars(actionBars);

		IMenuManager menu= actionBars.getMenuManager();
		menu.add(fToggleLinkingAction);
	}
	
	protected boolean hasWorkingSetFilter() {
		return fHasWorkingSetFilter;
	}
	
	protected void fillToolBar(IToolBarManager tbm) {
	}

	/**
	 * Creates the the content provider of this part.
	 */
	protected IContentProvider createContentProvider() {
		return new RubyBrowsingContentProvider(true, this);
	}

	protected final StructuredViewer getViewer() {
		return fViewer;
	}

	protected void setInitialInput() {
		// Use the selection, if any
		ISelection selection = getSite().getPage().getSelection();
		Object input = getSingleElementFromSelection(selection);
		if (!(input instanceof IRubyElement)) {
			// Use the input of the page
			input = getSite().getPage().getInput();
			if (!(input instanceof IRubyElement) && input instanceof IAdaptable)
				input = ((IAdaptable) input).getAdapter(IRubyElement.class);
		}
		setInput(findInputForRubyElement((IRubyElement) input));
	}

	protected RubyUILabelProvider createLabelProvider() {
		return new AppearanceAwareLabelProvider(
				AppearanceAwareLabelProvider.DEFAULT_TEXTFLAGS,
				AppearanceAwareLabelProvider.DEFAULT_IMAGEFLAGS
						| RubyElementImageProvider.SMALL_ICONS);
	}

	protected void setInput(Object input) {
		setViewerInput(input);
		updateTitle();
	}
	
	void updateTitle() {
		setTitleToolTip(getToolTipText(fViewer.getInput()));
	}
	
	/**
	 * Returns the tool tip text for the given element.
	 */
	String getToolTipText(Object element) {
		String result;
		if (!(element instanceof IResource)) {
			result= RubyElementLabels.getTextLabel(element, AppearanceAwareLabelProvider.DEFAULT_TEXTFLAGS);
		} else {
			IPath path= ((IResource) element).getFullPath();
			if (path.isRoot()) {
				result= getConfigurationElement().getAttribute("name"); //$NON-NLS-1$
			} else {
				result= path.makeRelative().toString();
			}
		}

		if (fWorkingSetFilterActionGroup == null || fWorkingSetFilterActionGroup.getWorkingSet() == null)
			return result;

		IWorkingSet ws= fWorkingSetFilterActionGroup.getWorkingSet();
		String wsstr= Messages.format(RubyBrowsingMessages.RubyBrowsingPart_toolTip, new String[] { ws.getLabel() });
		if (result.length() == 0)
			return wsstr;
		return Messages.format(RubyBrowsingMessages.RubyBrowsingPart_toolTip2, new String[] { result, ws.getLabel() });
	}

	protected final void setViewer(StructuredViewer viewer){
		fViewer= viewer;
	}
	
	private void setViewerInput(Object input) {
		fProcessSelectionEvents = false;
		fViewer.setInput(input);
		fProcessSelectionEvents = true;
	}

	private boolean isSearchResultView(IWorkbenchPart part) {
		return isSearchPlugInActivated()
				&& (part instanceof ISearchResultView || part instanceof ISearchResultViewPart);
	}

	// FIXME Move this to a ruby SearchUtil class
	public static boolean isSearchPlugInActivated() {
		return Platform.getBundle("org.eclipse.search").getState() == Bundle.ACTIVE; //$NON-NLS-1$
	}

	protected boolean needsToProcessSelectionChanged(IWorkbenchPart part,
			ISelection selection) {
		if (!fProcessSelectionEvents || part == this
				|| isSearchResultView(part)) {
			if (part == this)
				fPreviousSelectionProvider = part;
			return false;
		}
		return true;
	}

	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		if (!needsToProcessSelectionChanged(part, selection))
			return;


		 if (fToggleLinkingAction.isChecked() && (part instanceof ITextEditor)) {
			setSelectionFromEditor(part, selection);
			return;
		}

		if (!(selection instanceof IStructuredSelection))
			return;

		// Set selection
		Object selectedElement = getSingleElementFromSelection(selection);

		if (selectedElement != null
				&& (part == null || part.equals(fPreviousSelectionProvider))
				&& selectedElement.equals(fPreviousSelectedElement))
			return;

		fPreviousSelectedElement = selectedElement;

		Object currentInput = getViewer().getInput();
		if (selectedElement != null && selectedElement.equals(currentInput)) {
			IRubyElement elementToSelect = findElementToSelect(selectedElement);
			if (elementToSelect != null
					&& getTypeComparator().compare(selectedElement,
							elementToSelect) < 0)
				setSelection(new StructuredSelection(elementToSelect), true);
			else if (elementToSelect == null && (this instanceof MembersView)) {
				setSelection(StructuredSelection.EMPTY, true);
				fPreviousSelectedElement = StructuredSelection.EMPTY;
			}
			fPreviousSelectionProvider = part;
			return;
		}

		// Clear input if needed
		if (part != fPreviousSelectionProvider && selectedElement != null
				&& !selectedElement.equals(currentInput)
				&& isInputResetBy(selectedElement, currentInput, part)) {
			if (!isAncestorOf(selectedElement, currentInput))
				setInput(null);
			fPreviousSelectionProvider = part;
			return;
		} else if (selection.isEmpty() && !isInputResetBy(part)) {
			fPreviousSelectionProvider = part;
			return;
		} else if (selectedElement == null
				&& part == fPreviousSelectionProvider) {
			setInput(null);
			fPreviousSelectionProvider = part;
			return;
		}
		fPreviousSelectionProvider = part;

		// Adjust input and set selection and
		adjustInputAndSetSelection(selectedElement);
	}

	void setSelection(ISelection selection, boolean reveal) {
		if (selection != null && selection.equals(fViewer.getSelection()))
			return;
		fProcessSelectionEvents = false;
		fViewer.setSelection(selection, reveal);
		fProcessSelectionEvents = true;
	}

	protected Object getInput() {
		return fViewer.getInput();
	}

	public void setFocus() {
		fViewer.getControl().setFocus();
	}

	/**
	 * Answer the property defined by key.
	 */
	public Object getAdapter(Class key) {
		if (key == IShowInSource.class) {
			return getShowInSource();
		}
		// TODO Uncomment when we have RubyUIHelp
		// if (key == IContextProvider.class)
		// return RubyUIHelp.getHelpContextProvider(this, getHelpContextId());

		return super.getAdapter(key);
	}

	/**
	 * Returns the <code>IShowInSource</code> for this view.
	 */
	protected IShowInSource getShowInSource() {
		return new IShowInSource() {
			public ShowInContext getShowInContext() {
				return new ShowInContext(null, getSite().getSelectionProvider()
						.getSelection());
			}
		};
	}

	void adjustInputAndSetSelection(Object o) {
		if (!(o instanceof IRubyElement)) {
			if (o == null)
				setInput(null);
			setSelection(StructuredSelection.EMPTY, true);
			return;
		}

		IRubyElement je = (IRubyElement) o;
		IRubyElement elementToSelect = getSuitableRubyElement(findElementToSelect(je));
		IRubyElement newInput = findInputForRubyElement(je);
		IRubyElement oldInput = null;
		if (getInput() instanceof IRubyElement)
			oldInput = (IRubyElement) getInput();

		if (elementToSelect == null && !isValidInput(newInput)
				&& (newInput == null && !isAncestorOf(je, oldInput)))
			// Clear input
			setInput(null);
		else if (mustSetNewInput(elementToSelect, oldInput, newInput)) {
			// Adjust input to selection
			setInput(newInput);
			// Recompute suitable element since it depends on the viewer's input
			elementToSelect = getSuitableRubyElement(elementToSelect);
		}

		if (elementToSelect != null && elementToSelect.exists())
			setSelection(new StructuredSelection(elementToSelect), true);
		else
			setSelection(StructuredSelection.EMPTY, true);
	}

	/**
	 * Converts the given Ruby element to one which is suitable for this view.
	 * It takes into account whether the view shows working copies or not.
	 * 
	 * @param obj
	 *            the Ruby element to be converted
	 * @return an element suitable for this view
	 */
	IRubyElement getSuitableRubyElement(Object obj) {
		if (!(obj instanceof IRubyElement))
			return null;
		IRubyElement element = (IRubyElement) obj;
		if (fTypeComparator.compare(element, IRubyElement.SCRIPT) > 0)
			return element;
		if (isInputAWorkingCopy()) {
			IRubyElement wc = getWorkingCopy(element);
			if (wc != null)
				element = wc;
			return element;
		} else {
			return element.getPrimaryElement();
		}
	}

	boolean isInputAWorkingCopy() {
		return ((StandardRubyElementContentProvider) getViewer()
				.getContentProvider()).getProvideWorkingCopy();
	}

	/**
	 * Tries to find the given element in a workingcopy.
	 */
	protected static IRubyElement getWorkingCopy(IRubyElement input) {
		// MA: with new working copy story original == working copy
		return input;
	}

	/**
	 * Compute if a new input must be set.
	 * 
	 * @return <code>true</code> if the input has to be set
	 * @since 3.0
	 */
	private boolean mustSetNewInput(IRubyElement elementToSelect,
			IRubyElement oldInput, IRubyElement newInput) {
		return (newInput == null || !newInput.equals(oldInput))
				&& (elementToSelect == null || oldInput == null || (!(false && (elementToSelect
						.getParent().equals(oldInput.getParent())) && (!isAncestorOf(
						getViewPartInput(), elementToSelect)))));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jdt.internal.ui.viewsupport.IViewPartInputProvider#getViewPartInput()
	 */
	public Object getViewPartInput() {
		if (fViewer != null) {
			return fViewer.getInput();
		}
		return null;
	}

	/**
	 * Gets the typeComparator.
	 * 
	 * @return Returns a RubyElementTypeComparator
	 */
	protected Comparator getTypeComparator() {
		return fTypeComparator;
	}

	private boolean isInputResetBy(Object newInput, Object input,
			IWorkbenchPart part) {
		if (newInput == null)
			return part == fPreviousSelectionProvider;

		if (input instanceof IRubyElement && newInput instanceof IRubyElement)
			return getTypeComparator().compare(newInput, input) > 0;

		else
			return false;
	}

	private boolean isInputResetBy(IWorkbenchPart part) {
		if (!(part instanceof RubyBrowsingPart))
			return true;
		Object thisInput = getViewer().getInput();
		Object partInput = ((RubyBrowsingPart) part).getViewer().getInput();

		if (thisInput instanceof Collection)
			thisInput = ((Collection) thisInput).iterator().next();

		if (partInput instanceof Collection)
			partInput = ((Collection) partInput).iterator().next();

		if (thisInput instanceof IRubyElement
				&& partInput instanceof IRubyElement)
			return getTypeComparator().compare(partInput, thisInput) > 0;
		else
			return true;
	}

	protected boolean isAncestorOf(Object ancestor, Object element) {
		if (element instanceof IRubyElement && ancestor instanceof IRubyElement)
			return !element.equals(ancestor)
					&& internalIsAncestorOf((IRubyElement) ancestor,
							(IRubyElement) element);
		return false;
	}

	private boolean internalIsAncestorOf(IRubyElement ancestor,
			IRubyElement element) {
		if (element != null)
			return element.equals(ancestor)
					|| internalIsAncestorOf(ancestor, element.getParent());
		else
			return false;
	}

	protected final IRubyElement findElementToSelect(Object obj) {
		if (obj instanceof IRubyElement)
			return findElementToSelect((IRubyElement) obj);
		return null;
	}

	/**
	 * Finds the element which has to be selected in this part.
	 * 
	 * @param je
	 *            the Ruby element which has the focus
	 */
	abstract protected IRubyElement findElementToSelect(IRubyElement je);

	protected final Object getSingleElementFromSelection(ISelection selection) {
		if (!(selection instanceof StructuredSelection) || selection.isEmpty())
			return null;

		Iterator iter = ((StructuredSelection) selection).iterator();
		Object firstElement = iter.next();
		if (!(firstElement instanceof IRubyElement)) {
			if (firstElement instanceof IMarker)
				firstElement = ((IMarker) firstElement).getResource();
			if (firstElement instanceof IAdaptable) {
				IRubyElement je = (IRubyElement) ((IAdaptable) firstElement)
						.getAdapter(IRubyElement.class);
				if (je == null && firstElement instanceof IFile) {
					IContainer parent = ((IFile) firstElement).getParent();
					if (parent != null)
						return (IRubyElement) parent
								.getAdapter(IRubyElement.class);
					else
						return null;
				} else
					return je;

			} else
				return firstElement;
		}
		Object currentInput = getViewer().getInput();
		if (currentInput == null
				|| !currentInput
						.equals(findInputForRubyElement((IRubyElement) firstElement)))
			if (iter.hasNext())
				// multi-selection and view is empty
				return null;
			else
				// OK: single selection and view is empty
				return firstElement;

		// be nice to multi-selection
		while (iter.hasNext()) {
			Object element = iter.next();
			if (!(element instanceof IRubyElement))
				return null;
			if (!currentInput
					.equals(findInputForRubyElement((IRubyElement) element)))
				return null;
		}
		return firstElement;
	}

	/**
	 * Finds the closest Ruby element which can be used as input for this part
	 * and has the given Ruby element as child
	 * 
	 * @param je
	 *            the Ruby element for which to search the closest input
	 * @return the closest Ruby element used as input for this part
	 */
	protected IRubyElement findInputForRubyElement(IRubyElement je) {
		if (je == null || !je.exists())
			return null;
		if (isValidInput(je))
			return je;
		return findInputForRubyElement(je.getParent());
	}

	protected RubyElementSorter createRubyElementSorter() {
		return new RubyElementSorter() {
		
			@Override
			protected String getElementName(Object element) {
				if (element instanceof IType) {
					IType type = (IType) element;
					return type.getFullyQualifiedName();
				}
				return super.getElementName(element);
			}
		
		};
	}
	
	/**
	 * Returns the shell to use for opening dialogs.
	 * Used in this class, and in the actions.
	 */
	Shell getShell() {
		return fViewer.getControl().getShell();
	}

	protected final Display getDisplay() {
		return fViewer.getControl().getDisplay();
	}

	/**
	 * Returns the selection provider.
	 */
	ISelectionProvider getSelectionProvider() {
		return fViewer;
	}
	
	/**
	 * Answers if the given <code>element</code> is a valid input for this
	 * part.
	 * 
	 * @param element
	 *            the object to test
	 * @return <code>true</code> if the given element is a valid input
	 */
	abstract protected boolean isValidInput(Object element);

	/**
	 * Answers if the given <code>element</code> is a valid element for this
	 * part.
	 * 
	 * @param element
	 *            the object to test
	 * @return <code>true</code> if the given element is a valid element
	 */
	protected boolean isValidElement(Object element) {
		if (element == null)
			return false;
		element = getSuitableRubyElement(element);
		if (element == null)
			return false;
		Object input = getViewer().getInput();
		if (input == null)
			return false;
		if (input instanceof Collection)
			return ((Collection) input).contains(element);
		else
			return input.equals(element);

	}
	
	protected DecoratingLabelProvider createDecoratingLabelProvider(RubyUILabelProvider provider) {
//		XXX: Work in progress for problem decorator being a workbench decorator//
//		return new ExcludingDecoratingLabelProvider(provider, decorationMgr, "org.eclipse.jdt.ui.problem.decorator"); //$NON-NLS-1$
		return new DecoratingRubyLabelProvider(provider);
	}

	protected IType getTypeForRubyScript(IRubyScript script) {
		script = (IRubyScript) getSuitableRubyElement(script);

		// Use primary type if possible
		IType primaryType = script.findPrimaryType();
		if (primaryType != null)
			return primaryType;

		// Use first top-level type
		try {
			IType[] types = script.getTypes();
			if (types.length > 0)
				return types[0];
			else
				return null;
		} catch (RubyModelException ex) {
			return null;
		}
	}

	public void dispose() {
		if (fViewer != null) {
			getViewSite().getPage().removePostSelectionListener(this);
			getViewSite().getPage().removePartListener(fPartListener);
			fViewer = null;
		}
		if (fActionGroups != null)
			fActionGroups.dispose();

		super.dispose();
	}

	void setProcessSelectionEvents(boolean state) {
		fProcessSelectionEvents = state;
	}
	
	/**
	 * Called when the context menu is about to open.
	 * Override to add your own context dependent menu contributions.
	 */
	public void menuAboutToShow(IMenuManager menu) {
		RubyPlugin.createStandardGroups(menu);

		IStructuredSelection selection= (IStructuredSelection) fViewer.getSelection();
		int size= selection.size();
		Object element= selection.getFirstElement();

		if (size == 1)
			addOpenNewWindowAction(menu, element);
		fActionGroups.setContext(new ActionContext(selection));
		fActionGroups.fillContextMenu(menu);
		fActionGroups.setContext(null);
	}
	
	private void addOpenNewWindowAction(IMenuManager menu, Object element) {
		if (element instanceof IRubyElement) {
			element= ((IRubyElement)element).getResource();
		}
		if (!(element instanceof IContainer))
			return;
		menu.appendToGroup(
			IContextMenuConstants.GROUP_OPEN,
			new PatchedOpenInNewWindowAction(getSite().getWorkbenchWindow(), (IContainer)element));
	}
	
	/*
	 * Implements method from IViewPart.
	 */
	public void saveState(IMemento memento) {
		if (fViewer == null) {
			// part has not been created
			if (fMemento != null) //Keep the old state;
				memento.putMemento(fMemento);
			return;
		}
		if (fHasWorkingSetFilter)
			fWorkingSetFilterActionGroup.saveState(memento);
		if (fHasCustomFilter)
			fCustomFiltersActionGroup.saveState(memento);
		saveSelectionState(memento);
		saveLinkingEnabled(memento);
	}
	
	void setHasWorkingSetFilter(boolean state) {
		fHasWorkingSetFilter= state;
	}
	
	private void saveLinkingEnabled(IMemento memento) {
		memento.putInteger(getLinkToEditorKey(), fLinkingEnabled ? 1 : 0);
	}
	

	protected void setInitialSelection() {
		// Use the selection, if any
		Object input;
		IWorkbenchPage page= getSite().getPage();
		ISelection selection= null;
		if (page != null)
			selection= page.getSelection();
		if (selection instanceof ITextSelection) {
			Object part= PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActivePart();
			if (part instanceof IEditorPart) {
				setSelectionFromEditor((IEditorPart)part);
				if (fViewer.getSelection() != null)
					return;
			}
		}

		// Use saved selection from memento
		if (selection == null || selection.isEmpty())
			selection= restoreSelectionState(fMemento);

		if (selection == null || selection.isEmpty()) {
			// Use the input of the page
			input= getSite().getPage().getInput();
			if (!(input instanceof IRubyElement)) {
				if (input instanceof IAdaptable)
					input= ((IAdaptable)input).getAdapter(IRubyElement.class);
				else
					return;
			}
			selection= new StructuredSelection(input);
		}
		selectionChanged(null, selection);
	}
	
	private ISelection restoreSelectionState(IMemento memento) {
		if (memento == null)
			return null;

		IMemento childMem;
		childMem= memento.getChild(TAG_SELECTED_ELEMENTS);
		if (childMem != null) {
			ArrayList list= new ArrayList();
			IMemento[] elementMem= childMem.getChildren(TAG_SELECTED_ELEMENT);
			for (int i= 0; i < elementMem.length; i++) {
				String javaElementHandle= elementMem[i].getString(TAG_SELECTED_ELEMENT_PATH);

					IRubyElement element= RubyCore.create(javaElementHandle);
					if (element != null && element.exists())
						list.add(element);
				
			}
			return new StructuredSelection(list);
		}
		return null;
	}
	
	boolean isLinkingEnabled() {
		return fLinkingEnabled;
	}
	
	private void initLinkingEnabled() {
		fLinkingEnabled= PreferenceConstants.getPreferenceStore().getBoolean(getLinkToEditorKey());
	}
	
	private boolean linkBrowsingViewSelectionToEditor() {
		return isLinkingEnabled();
	}
	
	public void setLinkingEnabled(boolean enabled) {
		fLinkingEnabled= enabled;
		PreferenceConstants.getPreferenceStore().setValue(getLinkToEditorKey(), enabled);
		if (enabled) {
			IEditorPart editor = getSite().getPage().getActiveEditor();
			if (editor != null) {
				setSelectionFromEditor(editor);
			}
		}
	}
	
	protected final ILabelProvider getLabelProvider() {
		return fLabelProvider;
	}
	
	protected final ILabelProvider getTitleProvider() {
		return fTitleProvider;
	}
	
	/**
	 * Returns the preference key for the link to editor setting.
	 *
	 * @return	the string used as key into the preference store
	 */
	abstract protected String getLinkToEditorKey();
	
	void setSelectionFromEditor(IWorkbenchPart part) {
		if (!fProcessSelectionEvents || !linkBrowsingViewSelectionToEditor() || !(part instanceof IEditorPart))
			return;
		
		IWorkbenchPartSite site= part.getSite();
		if (site == null)
			return;
		ISelectionProvider provider= site.getSelectionProvider();
		if (provider != null)
			setSelectionFromEditor(part, provider.getSelection());
	}
	
	private void setSelectionFromEditor(IWorkbenchPart part, ISelection selection) {
		if (part instanceof IEditorPart) {
			IRubyElement element= null;
			if (selection instanceof IStructuredSelection) {
				Object obj= getSingleElementFromSelection(selection);
				if (obj instanceof IRubyElement)
					element= (IRubyElement)obj;
			}
			IEditorInput ei= ((IEditorPart)part).getEditorInput();
			if (selection instanceof ITextSelection) {
				int offset= ((ITextSelection)selection).getOffset();
				element= getElementAt(ei, offset);
			}
			if (element != null) {
				adjustInputAndSetSelection(element);
				return;
			}
			if (ei instanceof IFileEditorInput) {
				IFile file= ((IFileEditorInput)ei).getFile();
				IRubyElement je= (IRubyElement)file.getAdapter(IRubyElement.class);
				if (je == null) {
					IContainer container= ((IFileEditorInput)ei).getFile().getParent();
					if (container != null)
						je= (IRubyElement)container.getAdapter(IRubyElement.class);
				}
				if (je == null) {
					setSelection(null, false);
					return;
				}
				adjustInputAndSetSelection(je);
			}
		}
	}
	
	/**
	 * @see org.rubypeople.rdt.internal.ui.rubyeditor.RubyEditor#getElementAt(int)
	 */
	protected IRubyElement getElementAt(IEditorInput input, int offset) {
		IWorkingCopyManager manager= RubyPlugin.getDefault().getWorkingCopyManager();
		IRubyScript unit= manager.getWorkingCopy(input);
		if (unit != null)
			try {
				if (unit.isConsistent())
					return unit.getElementAt(offset);
				else {
					/*
					 * XXX: We should set the selection later when the
					 *      CU is reconciled.
					 *      see https://bugs.eclipse.org/bugs/show_bug.cgi?id=51290
					 */
				}
			} catch (RubyModelException ex) {
				// fall through
			}
		return null;
	}
}
