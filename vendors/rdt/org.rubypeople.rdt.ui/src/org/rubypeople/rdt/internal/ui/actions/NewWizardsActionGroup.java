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
package org.rubypeople.rdt.internal.ui.actions;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchSite;
import org.eclipse.ui.actions.ActionGroup;
import org.eclipse.ui.actions.NewWizardMenu;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.ui.IContextMenuConstants;


/**
 * Action group that adds the 'new' menu to a context menu.
 * <p>
 * This class may be instantiated; it is not intended to be subclassed.
 * </p>
 * 
 * @since 2.1
 */
public class NewWizardsActionGroup extends ActionGroup {

	private IWorkbenchSite fSite;
	
	/**
	 * Creates a new <code>NewWizardsActionGroup</code>. The group requires
	 * that the selection provided by the part's selection provider is of type <code>
	 * org.eclipse.jface.viewers.IStructuredSelection</code>.
	 * 
	 * @param site the view part that owns this action group
	 */
	public NewWizardsActionGroup(IWorkbenchSite site) {
		fSite= site;
	}
	

	/* (non-Javadoc)
	 * Method declared in ActionGroup
	 */
	public void fillContextMenu(IMenuManager menu) {
		super.fillContextMenu(menu);
		
		ISelection selection= getContext().getSelection();
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection sel= (IStructuredSelection) selection;
			if (sel.size() <= 1 && isNewTarget(sel.getFirstElement())) {
		        MenuManager newMenu = new MenuManager(ActionMessages.NewWizardsActionGroup_new);
		        menu.appendToGroup(IContextMenuConstants.GROUP_NEW, newMenu);
		        newMenu.add(new NewWizardMenu(fSite.getWorkbenchWindow()));
			}
		}		
		
	}
	
	private boolean isNewTarget(Object element) {
		if (element == null)
			return true;
		if (element instanceof IResource) {
			return true;
		}
		if (element instanceof IRubyElement) {
			int type= ((IRubyElement)element).getElementType();
			return type == IRubyElement.RUBY_PROJECT ||
				type == IRubyElement.SOURCE_FOLDER_ROOT || 
				type == IRubyElement.SOURCE_FOLDER ||
				type == IRubyElement.SCRIPT ||
				type == IRubyElement.TYPE;
		}
		return false;
	}	

}
