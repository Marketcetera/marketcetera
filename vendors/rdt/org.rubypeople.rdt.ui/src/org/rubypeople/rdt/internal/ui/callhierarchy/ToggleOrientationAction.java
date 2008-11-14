/*******************************************************************************
 * Copyright (c) 2000, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Jesper Kamstrup Linnet (eclipse@kamstrup-linnet.dk) - initial API and implementation 
 * 			(report 36180: Callers/Callees view)
 *******************************************************************************/
package org.rubypeople.rdt.internal.ui.callhierarchy;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.util.Assert;
import org.eclipse.ui.PlatformUI;
import org.rubypeople.rdt.internal.ui.IRubyHelpContextIds;
import org.rubypeople.rdt.internal.ui.RubyPluginImages;

/**
 * Toggles the orientationof the layout of the call hierarchy
 */
class ToggleOrientationAction extends Action {

    private CallHierarchyViewPart fView;    
    private int fActionOrientation;
    
    public ToggleOrientationAction(CallHierarchyViewPart v, int orientation) {
        super("", AS_RADIO_BUTTON); //$NON-NLS-1$
        if (orientation == CallHierarchyViewPart.VIEW_ORIENTATION_HORIZONTAL) {
            setText(CallHierarchyMessages.ToggleOrientationAction_horizontal_label); 
            setDescription(CallHierarchyMessages.ToggleOrientationAction_horizontal_description); 
            setToolTipText(CallHierarchyMessages.ToggleOrientationAction_horizontal_tooltip); 
            RubyPluginImages.setLocalImageDescriptors(this, "th_horizontal.gif"); //$NON-NLS-1$
        } else if (orientation == CallHierarchyViewPart.VIEW_ORIENTATION_VERTICAL) {
            setText(CallHierarchyMessages.ToggleOrientationAction_vertical_label); 
            setDescription(CallHierarchyMessages.ToggleOrientationAction_vertical_description); 
            setToolTipText(CallHierarchyMessages.ToggleOrientationAction_vertical_tooltip); 
            RubyPluginImages.setLocalImageDescriptors(this, "th_vertical.gif"); //$NON-NLS-1$
		} else if (orientation == CallHierarchyViewPart.VIEW_ORIENTATION_AUTOMATIC) {
			setText(CallHierarchyMessages.ToggleOrientationAction_automatic_label); 
			setDescription(CallHierarchyMessages.ToggleOrientationAction_automatic_description); 
			setToolTipText(CallHierarchyMessages.ToggleOrientationAction_automatic_tooltip); 
			RubyPluginImages.setLocalImageDescriptors(this, "th_automatic.gif"); //$NON-NLS-1$
        } else if (orientation == CallHierarchyViewPart.VIEW_ORIENTATION_SINGLE) {
            setText(CallHierarchyMessages.ToggleOrientationAction_single_label); 
            setDescription(CallHierarchyMessages.ToggleOrientationAction_single_description); 
            setToolTipText(CallHierarchyMessages.ToggleOrientationAction_single_tooltip); 
            RubyPluginImages.setLocalImageDescriptors(this, "th_single.gif"); //$NON-NLS-1$
        } else {
            Assert.isTrue(false);
        }
        fView= v;
        fActionOrientation= orientation;
        PlatformUI.getWorkbench().getHelpSystem().setHelp(this, IRubyHelpContextIds.CALL_HIERARCHY_TOGGLE_ORIENTATION_ACTION);
    }
    
    public int getOrientation() {
        return fActionOrientation;
    }   
    
    /*
     * @see Action#actionPerformed
     */     
    public void run() {
		if (isChecked()) {
			fView.fOrientation= fActionOrientation; 
			fView.computeOrientation();
		}
    }
    
}
