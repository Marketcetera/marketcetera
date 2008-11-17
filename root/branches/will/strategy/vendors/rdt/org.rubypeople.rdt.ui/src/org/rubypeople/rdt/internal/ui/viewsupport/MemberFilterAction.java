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
package org.rubypeople.rdt.internal.ui.viewsupport;

import org.eclipse.jface.action.Action;

import org.eclipse.ui.PlatformUI;

import org.rubypeople.rdt.ui.actions.*;

/**
 * Action used to enable / disable method filter properties
 */
public class MemberFilterAction extends Action {

    private int fFilterProperty;
    private MemberFilterActionGroup fFilterActionGroup;
    
    public MemberFilterAction(MemberFilterActionGroup actionGroup, String title, int property, String contextHelpId, boolean initValue) {
        super(title);
        fFilterActionGroup= actionGroup;
        fFilterProperty= property;
        
        PlatformUI.getWorkbench().getHelpSystem().setHelp(this, contextHelpId);

        setChecked(initValue);
    }
    
    /**
     * Returns this action's filter property.
     */
    public int getFilterProperty() {
        return fFilterProperty;
    }
    
    /*
     * @see Action#actionPerformed
     */
    public void run() { 
        fFilterActionGroup.setMemberFilter(fFilterProperty, isChecked());
    }
        
}
