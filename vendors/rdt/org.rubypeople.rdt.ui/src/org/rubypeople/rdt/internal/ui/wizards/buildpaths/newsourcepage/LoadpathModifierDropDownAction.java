/**
 * Copyright (c) 2008 Aptana, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl -v10.html. If redistributing this code,
 * this entire header must remain intact.
 *
 * This file is based on a JDT equivalent:
 ********************************************************************************
 * Copyright (c) 2000, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.rubypeople.rdt.internal.ui.wizards.buildpaths.newsourcepage;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.rubypeople.rdt.core.RubyModelException;

/**
 * Drop down action for toolbars containing <code>LoadpathModifierAction</code>s.
 * The drop down action manages a list of actions that are displayed when invocing 
 * the drop down. If there is at least one valid action, then the drop down action 
 * itself will also be valid and invocing run will delegate the call to the 
 * first valid action in the list.
 */
public class LoadpathModifierDropDownAction extends LoadpathModifierAction implements IMenuCreator {
    
    /** The menu to be populated with items*/
    private Menu fMenu;
    /** A list of actions that will be used as 
     * drop down items*/
    protected List fActions;
    /** Index of the action that can be executed when clicking directly on the dropdown button.*/
    private int fIndex;
    
    /**
     * Create a drop down action using the same descriptors as the provided action, but it's on 
     * tool tip text. The action will automatically be put in the list of actions that are 
     * managed by this drop down menu.
     * 
     * @param action an action to be added to the dropdown menu
     * @param text a label text for the action
     * @param toolTipText the tooltip text for the drop down menu
     */
    public LoadpathModifierDropDownAction(LoadpathModifierAction action, String text, String toolTipText) {
        super(action.getOperation(), action.getImageDescriptor(), action.getDisabledImageDescriptor(), 
                text, toolTipText, IAction.AS_DROP_DOWN_MENU);
        fActions= new ArrayList();
        fActions.add(action);
        fIndex= 0;
    }
    
    /**
     * Runs the first action of the list of managed actions that is valid.
     */
    public void run() {
        LoadpathModifierAction action= (LoadpathModifierAction)fActions.get(fIndex);
        action.run();
    }

    public IMenuCreator getMenuCreator() {
        return this;
    }

    public Menu getMenu(Control parent) {
        if (fMenu != null) {
            fMenu.dispose();
        }
        fMenu = new Menu(parent);
        createEntries(fMenu);
        return fMenu;

    }

    public Menu getMenu(Menu parent) {
        return fMenu;
    }
    
    /**
     * Add dynamically an action to the drop down menu.
     * 
     * @param action the action to be added
     */
    public void addAction(LoadpathModifierAction action) {
        fActions.add(action);
    }
    
    /**
     * Add dynamically an array of actions to the 
     * drop down menu.
     * 
     * @param actions an array of actions to be added
     */
    public void addActions(LoadpathModifierAction[] actions) {
        for(int i= 0; i < actions.length; i++) {
            addAction(actions[i]);
        }
    }
    
    /**
     * Remove an action from the drop down menu
     *  
     * @param action the action to be removed
     */
    public void removeAction(LoadpathModifierAction action) {
        fActions.remove(action);
    }
    
    /**
     * Get all actions within this drop down menu.
     * 
     * @return an array of actions
     */
    public LoadpathModifierAction[] getActions() {
        return (LoadpathModifierAction[])fActions.toArray(new LoadpathModifierAction[fActions.size()]);
    }
    
    /**
     * Populate the menu with the given action item
     *  
     * @param parent the menu to add an action for
     * @param action the action to be added
     */
    private void addActionToMenu(Menu parent, IAction action) {
        ActionContributionItem item = new ActionContributionItem(action);
        item.fill(parent, -1);
    }
    
    /**
     * Fill the menu with all actions
     * 
     * @param menu the menu to be populated
     */
    private void createEntries(Menu menu) {
        for(int i= 0; i < fActions.size(); i++) {
            IAction action= (IAction)fActions.get(i);
            addActionToMenu(menu, action);
        }
    }
    
    public void dispose() {
        if (fMenu != null) {
            fMenu.dispose();
            fMenu = null;
        }
    }
    
    /**
     * Check all managed actions to find out if at least one is valid. 
     * The first valid action that is found will be used when calling 
     * <code>run()</code>.
     * 
     * @return <code>true</code> if at least one of the managed actions is valid, 
     * <code>false</code> otherwise.
     */
    public boolean isValid(List selectedElements, int[] types) throws RubyModelException {
        for(int i= 0; i < fActions.size(); i++) {
            LoadpathModifierAction action= (LoadpathModifierAction)fActions.get(i);
            if(action.isValid(selectedElements, types)) {
                fIndex= i;
                return true;
            }
        }
        return false;
    }
}
