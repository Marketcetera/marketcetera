package org.rubypeople.rdt.internal.ui.browsing;

import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.util.Assert;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchPart;
import org.rubypeople.rdt.core.IImportContainer;
import org.rubypeople.rdt.core.IImportDeclaration;
import org.rubypeople.rdt.core.IMember;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IRubyScript;
import org.rubypeople.rdt.core.IType;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.internal.core.LogicalType;
import org.rubypeople.rdt.internal.ui.RubyPlugin;
import org.rubypeople.rdt.internal.ui.actions.LexicalSortingAction;
import org.rubypeople.rdt.internal.ui.preferences.MembersOrderPreferenceCache;
import org.rubypeople.rdt.internal.ui.viewsupport.AppearanceAwareLabelProvider;
import org.rubypeople.rdt.internal.ui.viewsupport.ColoredViewersManager;
import org.rubypeople.rdt.internal.ui.viewsupport.ProblemTreeViewer;
import org.rubypeople.rdt.internal.ui.viewsupport.RubyUILabelProvider;
import org.rubypeople.rdt.ui.PreferenceConstants;
import org.rubypeople.rdt.ui.RubyElementLabels;
import org.rubypeople.rdt.ui.RubyUI;
import org.rubypeople.rdt.ui.actions.MemberFilterActionGroup;

public class MembersView extends RubyBrowsingPart implements IPropertyChangeListener {

	private MemberFilterActionGroup fMemberFilterActionGroup;
	
	public MembersView() {
//		setHasWorkingSetFilter(false);
		setHasCustomSetFilter(true);
		RubyPlugin.getDefault().getPreferenceStore().addPropertyChangeListener(this);
	}
	
	/**
	 * Creates and returns the label provider for this part.
	 *
	 * @return the label provider
	 * @see org.eclipse.jface.viewers.ILabelProvider
	 */
	protected RubyUILabelProvider createLabelProvider() {
		return new AppearanceAwareLabelProvider(
						AppearanceAwareLabelProvider.DEFAULT_TEXTFLAGS | RubyElementLabels.M_PARAMETER_NAMES,
						AppearanceAwareLabelProvider.DEFAULT_IMAGEFLAGS
						);
	}
	
	protected String getLinkToEditorKey() {
		return PreferenceConstants.LINK_BROWSING_MEMBERS_TO_EDITOR;
	}
	
	/**
	 * Answers if the given <code>element</code> is a valid
	 * input for this part.
	 *
	 * @param 	element	the object to test
	 * @return	<true> if the given element is a valid input
	 */
	protected boolean isValidInput(Object element) {
		if (element instanceof IType) {
			IType type= (IType)element;
			return type.getDeclaringType() == null;
		}
		return false;
	}

	/**
	 * Answers if the given <code>element</code> is a valid
	 * element for this part.
	 *
	 * @param 	element	the object to test
	 * @return	<true> if the given element is a valid element
	 */
	protected boolean isValidElement(Object element) {
		if (element instanceof IMember)
			return super.isValidElement(((IMember)element).getDeclaringType());
		else if (element instanceof IImportDeclaration)
			return isValidElement(((IRubyElement)element).getParent());
		else if (element instanceof IImportContainer) {
			Object input= getViewer().getInput();
			if (input instanceof IRubyElement) {
				IRubyScript cu= (IRubyScript)((IRubyElement)input).getAncestor(IRubyElement.SCRIPT);
				if (cu != null) {
					IRubyScript importContainerCu= (IRubyScript)((IRubyElement)element).getAncestor(IRubyElement.SCRIPT);
					return cu.equals(importContainerCu);
				}
			}
		}
		return false;
	}
	
	protected void hookViewerListeners() {
		super.hookViewerListeners();
		getViewer().addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				TreeViewer viewer= (TreeViewer)getViewer();
				Object element= ((IStructuredSelection)event.getSelection()).getFirstElement();
				if (viewer.isExpandable(element))
					viewer.setExpandedState(element, !viewer.getExpandedState(element));
			}
		});
	}

	/**
	 * Finds the element which has to be selected in this part.
	 *
	 * @param je	the Ruby element which has the focus
	 */
	protected IRubyElement findElementToSelect(IRubyElement je) {
		if (je == null)
			return null;

		switch (je.getElementType()) {
			case IRubyElement.TYPE:
				if (((IType)je).getDeclaringType() == null)
					return null;
				// fall through
			case IRubyElement.METHOD:
				// fall through
			case IRubyElement.FIELD:
				// fall through
			case IRubyElement.IMPORT_CONTAINER:
				return getSuitableRubyElement(je);
			case IRubyElement.IMPORT_DECLARATION:
				je= getSuitableRubyElement(je);
				if (je != null) {
					IRubyScript cu= (IRubyScript)je.getParent().getParent();
					try {
						if (cu.getImports()[0].equals(je)) {
							Object selectedElement= getSingleElementFromSelection(getViewer().getSelection());
							if (selectedElement instanceof IImportContainer)
								return (IImportContainer)selectedElement;
						}
					} catch (RubyModelException ex) {
						// return je;
					}
					return je;
				}
				break;
		}
		return null;
	}

	/**
	 * Finds the closest Ruby element which can be used as input for
	 * this part and has the given Ruby element as child
	 *
	 * @param 	je 	the Ruby element for which to search the closest input
	 * @return	the closest Ruby element used as input for this part
	 */
	protected IRubyElement findInputForRubyElement(IRubyElement je) {
		if (je == null || !je.exists())
			return null;

		switch (je.getElementType()) {
			case IRubyElement.TYPE:
				return je;
			case IRubyElement.SCRIPT:
				return getTypeForRubyScript((IRubyScript)je);
			case IRubyElement.IMPORT_DECLARATION:
				return findInputForRubyElement(je.getParent());
			case IRubyElement.IMPORT_CONTAINER:
				IRubyElement parent= je.getParent();
				if (parent instanceof IRubyScript) {
					return getTypeForRubyScript((IRubyScript)parent);
				}
			default:
				if (je instanceof IMember)
					return findInputForRubyElement(((IMember)je).getDeclaringType());
		}
		return null;
	}
	
	boolean isInputAWorkingCopy() {
		Object input= getViewer().getInput();
		if (input instanceof IRubyElement) {
			IRubyScript cu= (IRubyScript)((IRubyElement)input).getAncestor(IRubyElement.SCRIPT);
			if (cu != null)
				return cu.isWorkingCopy();
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.util.IPropertyChangeListener#propertyChange(org.eclipse.jface.util.PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent event) {
		if (MembersOrderPreferenceCache.isMemberOrderProperty(event.getProperty())) {
			getViewer().refresh();
		}
	}
	
	/* (non-Javadoc)
	 * @see org.rubypeople.rdt.internal.ui.browsing.RubyBrowsingPart#dispose()
	 */
	public void dispose() {
		if (fMemberFilterActionGroup != null) {
			fMemberFilterActionGroup.dispose();
			fMemberFilterActionGroup= null;
		}
		super.dispose();
		RubyPlugin.getDefault().getPreferenceStore().removePropertyChangeListener(this);
	}
	
	/**
	 * Creates the the viewer of this part.
	 *
	 * @param parent	the parent for the viewer
	 */
	protected StructuredViewer createViewer(Composite parent) {		
		ProblemTreeViewer viewer= new ProblemTreeViewer(parent, SWT.MULTI);
		ColoredViewersManager.install(viewer);
		fMemberFilterActionGroup= new MemberFilterActionGroup(viewer, RubyUI.ID_MEMBERS_VIEW);
		return viewer;
	}
	
	protected void fillToolBar(IToolBarManager tbm) {
		tbm.add(new LexicalSortingAction(getViewer(), RubyUI.ID_MEMBERS_VIEW));
		fMemberFilterActionGroup.contributeToToolBar(tbm);
		super.fillToolBar(tbm);
	}
	
	@Override
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		if (!needsToProcessSelectionChanged(part, selection))
			return;

		if (selection instanceof IStructuredSelection) {
			IStructuredSelection sel= (IStructuredSelection) selection;
			Object selectedElement= sel.getFirstElement();
			if (sel.size() == 1 && (selectedElement instanceof LogicalType)) {
				IType[] fragments= ((LogicalType)selectedElement).getOriginalTypes();
				List selectedElements= Arrays.asList(fragments);
				if (selectedElements.size() > 1) {
					adjustInput(part, selectedElements);
					fPreviousSelectedElement= selectedElements;
					fPreviousSelectionProvider= part;
				} else if (selectedElements.size() == 1)
					super.selectionChanged(part, new StructuredSelection(selectedElements.get(0)));
				else
					Assert.isLegal(false);
				return;
			}
		}
		super.selectionChanged(part, selection);
	}
	
	private void adjustInput(IWorkbenchPart part, List selectedElements) {
		Object currentInput= getViewer().getInput();
		if (!selectedElements.equals(currentInput))
			setInput(selectedElements);
	}
		
}
