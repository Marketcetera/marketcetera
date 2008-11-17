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
 *******************************************************************************
 * Copyright (c) 2000, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.rubypeople.rdt.internal.corext.buildpath;

import org.rubypeople.rdt.internal.ui.wizards.buildpaths.newsourcepage.LoadpathModifierAction;


/**
 * Event that is fired from <code>PackageExplorerActionGroup</code> 
 * if some change on the available actions happens.
 */
public class PackageExplorerActionEvent {
    
    private String[] fEnabledActionsDescriptions;
    private LoadpathModifierAction[] fEnabledActions;
    
    /**
     * Create a package explorer action event containing actions 
     * and their corresponding descriptions. It is allowed to 
     * provide arrays which are empty, <code>null</code> is not 
     * allowed.
     * 
     * Also consider the case where there are no actions (the size is zero), but 
     * the description array contains one element which contains a statement for 
     * the fact that there are no actions.
     * 
     * @param enabledActionsDescriptions an array of descriptions for the 
     * actions. The description at position 'i' must correspond to the action at 
     * position 'i'.
     * @param enabledActions an array of actions
     */
    public PackageExplorerActionEvent(String[] enabledActionsDescriptions, LoadpathModifierAction[] enabledActions) {
        fEnabledActionsDescriptions= enabledActionsDescriptions;
        fEnabledActions= enabledActions;
    }
    
    /**
     * Get the available actions. To get the corresponding 
     * descriptions, <code>getEnabledActionsText()</code> can 
     * be used.
     * 
     * @return an array of available actions, can be empty, but 
     * not <code>null</code>.
     * 
     * @see #getEnabledActionsText()
     */
    public LoadpathModifierAction[] getEnabledActions() {
        return fEnabledActions;
    }
    
    /**
     * Get the descriptions to the available actions.
     * 
     * @return an array of descriptions corresponding to 
     * the available actions of <code>getEnabledActions</code>. 
     * Can be empty, but not <code>null</code>.
     * 
     * @see #getEnabledActions()
     */
    public String[] getEnabledActionsText() {
        return fEnabledActionsDescriptions;
    }
}
