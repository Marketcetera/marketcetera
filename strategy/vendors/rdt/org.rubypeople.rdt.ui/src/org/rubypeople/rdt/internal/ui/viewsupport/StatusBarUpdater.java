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


import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.internal.corext.util.Messages;
import org.rubypeople.rdt.internal.ui.RubyUIMessages;
import org.rubypeople.rdt.ui.RubyElementLabels;


/**
 * Add the <code>StatusBarUpdater</code> to your ViewPart to have the statusbar
 * describing the selected elements.
 */
public class StatusBarUpdater implements ISelectionChangedListener {
    
    private final long LABEL_FLAGS= RubyElementLabels.DEFAULT_QUALIFIED | RubyElementLabels.ROOT_POST_QUALIFIED | RubyElementLabels.APPEND_ROOT_PATH | RubyElementLabels.M_PARAMETER_NAMES;
            
    private IStatusLineManager fStatusLineManager;
    
    public StatusBarUpdater(IStatusLineManager statusLineManager) {
        fStatusLineManager= statusLineManager;
    }
        
    /*
     * @see ISelectionChangedListener#selectionChanged
     */
    public void selectionChanged(SelectionChangedEvent event) {
        String statusBarMessage= formatMessage(event.getSelection());
        fStatusLineManager.setMessage(statusBarMessage);
    }
    
    
    protected String formatMessage(ISelection sel) {
        if (sel instanceof IStructuredSelection && !sel.isEmpty()) {
            IStructuredSelection selection= (IStructuredSelection) sel;
            
            int nElements= selection.size();
            if (nElements > 1) {
                return Messages.format(RubyUIMessages.StatusBarUpdater_num_elements_selected, String.valueOf(nElements)); 
            } else { 
                Object elem= selection.getFirstElement();
                if (elem instanceof IRubyElement) {
                    return formatRubyElementMessage((IRubyElement) elem);
                } else if (elem instanceof IResource) {
                    return formatResourceMessage((IResource) elem);
                }
            }
        }
        return "";  //$NON-NLS-1$
    }
        
    private String formatRubyElementMessage(IRubyElement element) {
        return RubyElementLabels.getElementLabel(element, LABEL_FLAGS);
    }
        
    private String formatResourceMessage(IResource element) {
        IContainer parent= element.getParent();
        if (parent != null && parent.getType() != IResource.ROOT)
            return element.getName() + RubyElementLabels.CONCAT_STRING + parent.getFullPath().makeRelative().toString();
        else
            return element.getName();
    }   

}
