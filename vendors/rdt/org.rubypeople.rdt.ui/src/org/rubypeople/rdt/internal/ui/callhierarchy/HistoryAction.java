/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
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
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.util.Assert;
import org.eclipse.ui.PlatformUI;
import org.rubypeople.rdt.core.IMethod;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.internal.corext.util.Messages;
import org.rubypeople.rdt.internal.ui.IRubyHelpContextIds;
import org.rubypeople.rdt.internal.ui.viewsupport.RubyElementImageProvider;
import org.rubypeople.rdt.ui.RubyElementLabelProvider;

/**
 * Action used for the type hierarchy forward / backward buttons
 */
class HistoryAction extends Action {
    private static RubyElementLabelProvider fLabelProvider = new RubyElementLabelProvider(RubyElementLabelProvider.SHOW_POST_QUALIFIED /*|
            RubyElementLabelProvider.SHOW_PARAMETERS |*/);
    private CallHierarchyViewPart fView;
    private IMethod fMethod;

    public HistoryAction(CallHierarchyViewPart viewPart, IMethod element) {
        super("", AS_RADIO_BUTTON); //$NON-NLS-1$
        fView = viewPart;
        fMethod = element;

        String elementName = getElementLabel(element);
        setText(elementName);
        setImageDescriptor(getImageDescriptor(element));

        setDescription(Messages.format(CallHierarchyMessages.HistoryAction_description, elementName)); 
        setToolTipText(Messages.format(CallHierarchyMessages.HistoryAction_tooltip, elementName)); 
        
        PlatformUI.getWorkbench().getHelpSystem().setHelp(this, IRubyHelpContextIds.CALL_HIERARCHY_HISTORY_ACTION);
    }

    private ImageDescriptor getImageDescriptor(IRubyElement elem) {
        RubyElementImageProvider imageProvider = new RubyElementImageProvider();
        ImageDescriptor desc = imageProvider.getBaseImageDescriptor(elem, 0);
        imageProvider.dispose();

        return desc;
    }

    /*
     * @see Action#run()
     */
    public void run() {
        fView.gotoHistoryEntry(fMethod);
    }

    /**
     * @param element
     * @return String
     */
    private String getElementLabel(IRubyElement element) {
        Assert.isNotNull(element);
        return fLabelProvider.getText(element);
    }
}
