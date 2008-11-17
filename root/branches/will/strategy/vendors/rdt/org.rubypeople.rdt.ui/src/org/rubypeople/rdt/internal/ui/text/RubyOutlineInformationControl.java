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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.IDecoratorManager;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.keys.KeySequence;
import org.eclipse.ui.keys.SWTKeySupport;
import org.rubypeople.rdt.core.IMember;
import org.rubypeople.rdt.core.IMethod;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IRubyScript;
import org.rubypeople.rdt.core.IType;
import org.rubypeople.rdt.core.ITypeHierarchy;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.internal.corext.util.Messages;
import org.rubypeople.rdt.internal.corext.util.MethodOverrideTester;
import org.rubypeople.rdt.internal.corext.util.SuperTypeHierarchyCache;
import org.rubypeople.rdt.internal.ui.IRubyHelpContextIds;
import org.rubypeople.rdt.internal.ui.RubyPlugin;
import org.rubypeople.rdt.internal.ui.RubyPluginImages;
import org.rubypeople.rdt.internal.ui.RubyUIMessages;
import org.rubypeople.rdt.internal.ui.typehierarchy.AbstractHierarchyViewerSorter;
import org.rubypeople.rdt.internal.ui.util.StringMatcher;
import org.rubypeople.rdt.internal.ui.viewsupport.AppearanceAwareLabelProvider;
import org.rubypeople.rdt.internal.ui.viewsupport.MemberFilter;
import org.rubypeople.rdt.ui.OverrideIndicatorLabelDecorator;
import org.rubypeople.rdt.ui.ProblemsLabelDecorator;
import org.rubypeople.rdt.ui.RubyElementLabels;
import org.rubypeople.rdt.ui.StandardRubyElementContentProvider;

/**
 * Show outline in light-weight control.
 *
 * @since 2.1
 */
public class RubyOutlineInformationControl extends AbstractInformationControl {

	private KeyAdapter fKeyAdapter;
	private OutlineContentProvider fOutlineContentProvider;
	private IRubyElement fInput= null;

	private OutlineSorter fOutlineSorter;

	private OutlineLabelProvider fInnerLabelProvider;
	protected Color fForegroundColor;

	private boolean fShowOnlyMainType;
	private LexicalSortingAction fLexicalSortingAction;
	private SortByDefiningTypeAction fSortByDefiningTypeAction;
	private ShowOnlyMainTypeAction fShowOnlyMainTypeAction;
	private Map fTypeHierarchies= new HashMap();
	
	private String fPattern;

	private class OutlineLabelProvider extends AppearanceAwareLabelProvider {

		private boolean fShowDefiningType;

		private OutlineLabelProvider() {
			super(AppearanceAwareLabelProvider.DEFAULT_TEXTFLAGS, AppearanceAwareLabelProvider.DEFAULT_IMAGEFLAGS);
		}

		/*
		 * @see ILabelProvider#getText
		 */
		public String getText(Object element) {
			String text= super.getText(element);
			if (fShowDefiningType) {
				try {
					IType type= getDefiningType(element);
					if (type != null) {
						StringBuffer buf= new StringBuffer(super.getText(type));
						buf.append(RubyElementLabels.CONCAT_STRING);
						buf.append(text);
						return buf.toString();
					}
				} catch (RubyModelException e) {
				}
			}
			return text;
		}

		/*
		 * @see org.eclipse.jdt.internal.ui.viewsupport.RubyUILabelProvider#getForeground(java.lang.Object)
		 */
		public Color getForeground(Object element) {
			if (fOutlineContentProvider.isShowingInheritedMembers()) {
				if (element instanceof IRubyElement) {
					IRubyElement je= (IRubyElement)element;
					je= je.getAncestor(IRubyElement.SCRIPT);
					if (fInput.equals(je)) {
						return null;
					}
				}
				return fForegroundColor;
			}
			return null;
		}

		public void setShowDefiningType(boolean showDefiningType) {
			fShowDefiningType= showDefiningType;
		}

		public boolean isShowDefiningType() {
			return fShowDefiningType;
		}
		
		private IType getDefiningType(Object element) throws RubyModelException {
			int kind= ((IRubyElement) element).getElementType();
		
			if (kind != IRubyElement.METHOD && kind != IRubyElement.FIELD) {
				return null;
			}
			IType declaringType= ((IMember) element).getDeclaringType();
			if (kind != IRubyElement.METHOD) {
				return declaringType;
			}
			ITypeHierarchy hierarchy= getSuperTypeHierarchy(declaringType);
			if (hierarchy == null) {
				return declaringType;
			}
			IMethod method= (IMethod) element;
			MethodOverrideTester tester= new MethodOverrideTester(declaringType, hierarchy);
			IMethod res= tester.findDeclaringMethod(method, true);
			if (res == null || method.equals(res)) {
				return declaringType;
			}
			return res.getDeclaringType();
		}
	}


	private class OutlineTreeViewer extends TreeViewer {

		private boolean fIsFiltering= false;

		private OutlineTreeViewer(Tree tree) {
			super(tree);
		}

		/**
		 * {@inheritDoc}
		 */
		protected Object[] getFilteredChildren(Object parent) {
			Object[] result = getRawChildren(parent);
			int unfilteredChildren= result.length;
			ViewerFilter[] filters = getFilters();
			if (filters != null) {
				for (int i= 0; i < filters.length; i++)
					result = filters[i].filter(this, parent, result);
			}
			fIsFiltering= unfilteredChildren != result.length;
			return result;
		}

		/**
		 * {@inheritDoc}
		 */
		protected void internalExpandToLevel(Widget node, int level) {
			if (!fIsFiltering && node instanceof Item) {
				Item i= (Item) node;
				if (i.getData() instanceof IRubyElement) {
					IRubyElement je= (IRubyElement) i.getData();
					if (je.getElementType() == IRubyElement.IMPORT_CONTAINER || isInnerType(je)) {
						setExpanded(i, false);
						return;
					}
				}
			}
			super.internalExpandToLevel(node, level);
		}

		private boolean isInnerType(IRubyElement element) {
			if (element != null && element.getElementType() == IRubyElement.TYPE) {
				IType type= (IType)element;
				try {
					return type.isMember();
				} catch (RubyModelException e) {
					IRubyElement parent= type.getParent();
					if (parent != null) {
						int parentElementType= parent.getElementType();
						return (parentElementType != IRubyElement.SCRIPT);
					}
				}
			}
			return false;
		}
	}


	private class OutlineContentProvider extends StandardRubyElementContentProvider {

		private boolean fShowInheritedMembers;

		/**
		 * Creates a new Outline content provider.
		 *
		 * @param showInheritedMembers <code>true</code> iff inherited members are shown
		 */
		private OutlineContentProvider(boolean showInheritedMembers) {
			super(true);
			fShowInheritedMembers= showInheritedMembers;
		}

		public boolean isShowingInheritedMembers() {
			return fShowInheritedMembers;
		}

		public void toggleShowInheritedMembers() {
			Tree tree= getTreeViewer().getTree();

			tree.setRedraw(false);
			fShowInheritedMembers= !fShowInheritedMembers;
			getTreeViewer().refresh();
			getTreeViewer().expandToLevel(2);

			// reveal selection
			Object selectedElement= getSelectedElement();
			if (selectedElement != null)
				getTreeViewer().reveal(selectedElement);

			tree.setRedraw(true);
		}

		/**
		 * {@inheritDoc}
		 */
		public Object[] getChildren(Object element) {
			if (fShowOnlyMainType) {
				if (element instanceof IRubyScript) {
					element= getMainType((IRubyScript)element);
				}

				if (element == null)
					return NO_CHILDREN;
			}

			if (fShowInheritedMembers && element instanceof IType) {
				IType type= (IType)element;
				if (type.getDeclaringType() == null) {
					ITypeHierarchy th= getSuperTypeHierarchy(type);
					if (th != null) {
						List children= new ArrayList();
						IType[] superClasses= th.getAllSupertypes(type);
						children.addAll(Arrays.asList(super.getChildren(type)));
						for (int i= 0, scLength= superClasses.length; i < scLength; i++)
							children.addAll(Arrays.asList(super.getChildren(superClasses[i])));
						return children.toArray();
					}
				}
			}
			return super.getChildren(element);
		}

		/**
		 * {@inheritDoc}
		 */
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			super.inputChanged(viewer, oldInput, newInput);
			fTypeHierarchies.clear();
		}

		/**
		 * {@inheritDoc}
		 */
		public void dispose() {
			super.dispose();
			fTypeHierarchies.clear();
		}
	}


	private class ShowOnlyMainTypeAction extends Action {

		private static final String STORE_GO_INTO_TOP_LEVEL_TYPE_CHECKED= "GoIntoTopLevelTypeAction.isChecked"; //$NON-NLS-1$

		private TreeViewer fOutlineViewer;

		private ShowOnlyMainTypeAction(TreeViewer outlineViewer) {
			super(TextMessages.RubyOutlineInformationControl_GoIntoTopLevelType_label, IAction.AS_CHECK_BOX);
			setToolTipText(TextMessages.RubyOutlineInformationControl_GoIntoTopLevelType_tooltip);
			setDescription(TextMessages.RubyOutlineInformationControl_GoIntoTopLevelType_description);

			RubyPluginImages.setLocalImageDescriptors(this, "gointo_toplevel_type.gif"); //$NON-NLS-1$

			PlatformUI.getWorkbench().getHelpSystem().setHelp(this, IRubyHelpContextIds.GO_INTO_TOP_LEVEL_TYPE_ACTION);

			fOutlineViewer= outlineViewer;

			boolean showclass= getDialogSettings().getBoolean(STORE_GO_INTO_TOP_LEVEL_TYPE_CHECKED);
			setTopLevelTypeOnly(showclass);
		}

		/*
		 * @see org.eclipse.jface.action.Action#run()
		 */
		public void run() {
			setTopLevelTypeOnly(!fShowOnlyMainType);
		}

		private void setTopLevelTypeOnly(boolean show) {
			fShowOnlyMainType= show;
			setChecked(show);

			Tree tree= fOutlineViewer.getTree();
			tree.setRedraw(false);

			fOutlineViewer.refresh(false);
			if (!fShowOnlyMainType)
				fOutlineViewer.expandToLevel(2);


			// reveal selection
			Object selectedElement= getSelectedElement();
			if (selectedElement != null)
				fOutlineViewer.reveal(selectedElement);

			tree.setRedraw(true);

			getDialogSettings().put(STORE_GO_INTO_TOP_LEVEL_TYPE_CHECKED, show);
		}
	}

	private class OutlineSorter extends AbstractHierarchyViewerSorter {

		/*
		 * @see org.eclipse.jdt.internal.ui.typehierarchy.AbstractHierarchyViewerSorter#getHierarchy(org.eclipse.jdt.core.IType)
		 * @since 3.2
		 */
		protected ITypeHierarchy getHierarchy(IType type) {
			return getSuperTypeHierarchy(type);
		}

		/*
		 * @see org.eclipse.jdt.internal.ui.typehierarchy.AbstractHierarchyViewerSorter#isSortByDefiningType()
		 * @since 3.2
		 */
		public boolean isSortByDefiningType() {
			return fSortByDefiningTypeAction.isChecked();
		}

		/*
		 * @see org.eclipse.jdt.internal.ui.typehierarchy.AbstractHierarchyViewerSorter#isSortAlphabetically()
		 * @since 3.2
		 */
		public boolean isSortAlphabetically() {
			return fLexicalSortingAction.isChecked();
		}
	}


	private class LexicalSortingAction extends Action {

		private static final String STORE_LEXICAL_SORTING_CHECKED= "LexicalSortingAction.isChecked"; //$NON-NLS-1$

		private TreeViewer fOutlineViewer;

		private LexicalSortingAction(TreeViewer outlineViewer) {
			super(TextMessages.RubyOutlineInformationControl_LexicalSortingAction_label, IAction.AS_CHECK_BOX);
			setToolTipText(TextMessages.RubyOutlineInformationControl_LexicalSortingAction_tooltip);
			setDescription(TextMessages.RubyOutlineInformationControl_LexicalSortingAction_description);

			RubyPluginImages.setLocalImageDescriptors(this, "alphab_sort_co.gif"); //$NON-NLS-1$

			fOutlineViewer= outlineViewer;

			boolean checked=getDialogSettings().getBoolean(STORE_LEXICAL_SORTING_CHECKED);
			setChecked(checked);
			PlatformUI.getWorkbench().getHelpSystem().setHelp(this, IRubyHelpContextIds.LEXICAL_SORTING_BROWSING_ACTION);
		}

		public void run() {
			valueChanged(isChecked(), true);
		}

		private void valueChanged(final boolean on, boolean store) {
			setChecked(on);
			BusyIndicator.showWhile(fOutlineViewer.getControl().getDisplay(), new Runnable() {
				public void run() {
					fOutlineViewer.refresh(false);
				}
			});

			if (store)
				getDialogSettings().put(STORE_LEXICAL_SORTING_CHECKED, on);
		}
	}


	private class SortByDefiningTypeAction extends Action {

		private static final String STORE_SORT_BY_DEFINING_TYPE_CHECKED= "SortByDefiningType.isChecked"; //$NON-NLS-1$

		private TreeViewer fOutlineViewer;

		/**
		 * Creates the action.
		 *
		 * @param outlineViewer the outline viewer
		 */
		private SortByDefiningTypeAction(TreeViewer outlineViewer) {
			super(TextMessages.RubyOutlineInformationControl_SortByDefiningTypeAction_label);
			setDescription(TextMessages.RubyOutlineInformationControl_SortByDefiningTypeAction_description);
			setToolTipText(TextMessages.RubyOutlineInformationControl_SortByDefiningTypeAction_tooltip);

			RubyPluginImages.setLocalImageDescriptors(this, "definingtype_sort_co.gif"); //$NON-NLS-1$

			fOutlineViewer= outlineViewer;

			PlatformUI.getWorkbench().getHelpSystem().setHelp(this, IRubyHelpContextIds.SORT_BY_DEFINING_TYPE_ACTION);

			boolean state= getDialogSettings().getBoolean(STORE_SORT_BY_DEFINING_TYPE_CHECKED);
			setChecked(state);
			fInnerLabelProvider.setShowDefiningType(state);
		}

		/*
		 * @see Action#actionPerformed
		 */
		public void run() {
			BusyIndicator.showWhile(fOutlineViewer.getControl().getDisplay(), new Runnable() {
				public void run() {
					fInnerLabelProvider.setShowDefiningType(isChecked());
					getDialogSettings().put(STORE_SORT_BY_DEFINING_TYPE_CHECKED, isChecked());

					setMatcherString(fPattern, false);
					fOutlineViewer.refresh(true);

					// reveal selection
					Object selectedElement= getSelectedElement();
					if (selectedElement != null)
						fOutlineViewer.reveal(selectedElement);
				}
			});
		}
	}
	
	/**
	 * String matcher that can match two patterns.
	 * 
	 * @since 3.2
	 */
	private static class OrStringMatcher extends StringMatcher {
		
		private StringMatcher fMatcher1;
		private StringMatcher fMatcher2;
		
		private OrStringMatcher(String pattern1, String pattern2, boolean ignoreCase, boolean foo) {
			super("", false, false); //$NON-NLS-1$
			fMatcher1= new StringMatcher(pattern1, ignoreCase, false);
			fMatcher2= new StringMatcher(pattern2, ignoreCase, false);
		}
		
		public boolean match(String text) {
			return fMatcher2.match(text) || fMatcher1.match(text);
		}
		
	}


	/**
	 * Creates a new Ruby outline information control.
	 *
	 * @param parent
	 * @param shellStyle
	 * @param treeStyle
	 * @param commandId
	 */
	public RubyOutlineInformationControl(Shell parent, int shellStyle, int treeStyle, String commandId) {
		super(parent, shellStyle, treeStyle, commandId, true);
	}

	/**
	 * {@inheritDoc}
	 */
	protected Text createFilterText(Composite parent) {
		Text text= super.createFilterText(parent);
		text.addKeyListener(getKeyAdapter());
		return text;
	}

	/**
	 * {@inheritDoc}
	 */
	protected TreeViewer createTreeViewer(Composite parent, int style) {
		Tree tree= new Tree(parent, SWT.SINGLE | (style & ~SWT.MULTI));
		GridData gd= new GridData(GridData.FILL_BOTH);
		gd.heightHint= tree.getItemHeight() * 12;
		tree.setLayoutData(gd);

		final TreeViewer treeViewer= new OutlineTreeViewer(tree);

		// Hard-coded filters
		treeViewer.addFilter(new NamePatternFilter());
		treeViewer.addFilter(new MemberFilter());

		fForegroundColor= parent.getDisplay().getSystemColor(SWT.COLOR_DARK_GRAY);

		fInnerLabelProvider= new OutlineLabelProvider();
		fInnerLabelProvider.addLabelDecorator(new ProblemsLabelDecorator(null));
		IDecoratorManager decoratorMgr= PlatformUI.getWorkbench().getDecoratorManager();
		if (decoratorMgr.getEnabled("org.rubypeople.rdt.ui.override.decorator")) //$NON-NLS-1$
			fInnerLabelProvider.addLabelDecorator(new OverrideIndicatorLabelDecorator(null));

		treeViewer.setLabelProvider(fInnerLabelProvider);

		fLexicalSortingAction= new LexicalSortingAction(treeViewer);
		fSortByDefiningTypeAction= new SortByDefiningTypeAction(treeViewer);
		fShowOnlyMainTypeAction= new ShowOnlyMainTypeAction(treeViewer);

		fOutlineContentProvider= new OutlineContentProvider(false);
		treeViewer.setContentProvider(fOutlineContentProvider);
		fOutlineSorter= new OutlineSorter();
		treeViewer.setSorter(fOutlineSorter);
		treeViewer.setAutoExpandLevel(AbstractTreeViewer.ALL_LEVELS);


		treeViewer.getTree().addKeyListener(getKeyAdapter());

		return treeViewer;
	}

	/**
	 * {@inheritDoc}
	 */
	protected String getStatusFieldText() {
		KeySequence[] sequences= getInvokingCommandKeySequences();
		if (sequences == null || sequences.length == 0)
			return ""; //$NON-NLS-1$

		String keySequence= sequences[0].format();

		if (fOutlineContentProvider.isShowingInheritedMembers())
			return Messages.format(RubyUIMessages.RubyOutlineControl_statusFieldText_hideInheritedMembers, keySequence);
		else
			return Messages.format(RubyUIMessages.RubyOutlineControl_statusFieldText_showInheritedMembers, keySequence);
	}

	/*
	 * @see org.eclipse.jdt.internal.ui.text.AbstractInformationControl#getId()
	 * @since 3.0
	 */
	protected String getId() {
		return "org.eclipse.jdt.internal.ui.text.QuickOutline"; //$NON-NLS-1$
	}

	/**
	 * {@inheritDoc}
	 */
	public void setInput(Object information) {
		if (information == null || information instanceof String) {
			inputChanged(null, null);
			return;
		}
		IRubyElement je= (IRubyElement)information;
		IRubyScript cu= (IRubyScript)je.getAncestor(IRubyElement.SCRIPT);
		if (cu != null)
			fInput= cu;

		inputChanged(fInput, information);
	}

	private KeyAdapter getKeyAdapter() {
		if (fKeyAdapter == null) {
			fKeyAdapter= new KeyAdapter() {
				public void keyPressed(KeyEvent e) {
					int accelerator = SWTKeySupport.convertEventToUnmodifiedAccelerator(e);
					KeySequence keySequence = KeySequence.getInstance(SWTKeySupport.convertAcceleratorToKeyStroke(accelerator));
					KeySequence[] sequences= getInvokingCommandKeySequences();
					if (sequences == null)
						return;
					for (int i= 0; i < sequences.length; i++) {
						if (sequences[i].equals(keySequence)) {
							e.doit= false;
							toggleShowInheritedMembers();
							return;
						}
					}
				}
			};
		}
		return fKeyAdapter;
	}

	/**
	 * {@inheritDoc}
	 */
	protected void handleStatusFieldClicked() {
		toggleShowInheritedMembers();
	}

	protected void toggleShowInheritedMembers() {
		long flags= fInnerLabelProvider.getTextFlags();
		flags ^= RubyElementLabels.ALL_POST_QUALIFIED;
		fInnerLabelProvider.setTextFlags(flags);
		fOutlineContentProvider.toggleShowInheritedMembers();
		updateStatusFieldText();
	}

	/*
	 * @see org.eclipse.jdt.internal.ui.text.AbstractInformationControl#fillViewMenu(org.eclipse.jface.action.IMenuManager)
	 */
	protected void fillViewMenu(IMenuManager viewMenu) {
		super.fillViewMenu(viewMenu);
		viewMenu.add(fShowOnlyMainTypeAction); 

		viewMenu.add(new Separator("Sorters")); //$NON-NLS-1$
		viewMenu.add(fLexicalSortingAction);
	}
	
	/*
	 * @see org.eclipse.jdt.internal.ui.text.AbstractInformationControl#setMatcherString(java.lang.String, boolean)
	 * @since 3.2
	 */
	protected void setMatcherString(String pattern, boolean update) {
		fPattern= pattern;
		if (pattern.length() == 0 || !fSortByDefiningTypeAction.isChecked()) {
			super.setMatcherString(pattern, update);
			return;
		}
		
		boolean ignoreCase= pattern.toLowerCase().equals(pattern);
		String pattern2= "*" + RubyElementLabels.CONCAT_STRING + pattern; //$NON-NLS-1$
		fStringMatcher= new OrStringMatcher(pattern, pattern2, ignoreCase, false);

		if (update)
			stringMatcherUpdated();
		
	}
	
	private ITypeHierarchy getSuperTypeHierarchy(IType type) {
		ITypeHierarchy th= (ITypeHierarchy)fTypeHierarchies.get(type);
		if (th == null) {
			try {
				th= SuperTypeHierarchyCache.getTypeHierarchy(type, getProgressMonitor());
			} catch (RubyModelException e) {
				return null;
			} catch (OperationCanceledException e) {
				return null;
			}
			fTypeHierarchies.put(type, th);
		}
		return th;
	}

	private IProgressMonitor getProgressMonitor() {
		IWorkbenchPage wbPage= RubyPlugin.getActivePage();
		if (wbPage == null)
			return null;

		IEditorPart editor= wbPage.getActiveEditor();
		if (editor == null)
			return null;

		return editor.getEditorSite().getActionBars().getStatusLineManager().getProgressMonitor();
	}
	
	/**
	 * Returns the primary type of a compilation unit (has the same
	 * name as the compilation unit).
	 *
	 * @param compilationUnit the compilation unit
	 * @return returns the primary type of the compilation unit, or
	 * <code>null</code> if is does not have one
	 */
	private IType getMainType(IRubyScript compilationUnit) {

		if (compilationUnit == null)
			return null;

		return compilationUnit.findPrimaryType();
	}

}
