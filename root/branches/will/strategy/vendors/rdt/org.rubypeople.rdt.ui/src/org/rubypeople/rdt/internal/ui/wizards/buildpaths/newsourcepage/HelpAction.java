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

import org.eclipse.jface.action.Action;
import org.eclipse.ui.PlatformUI;
import org.rubypeople.rdt.internal.ui.RubyPluginImages;
import org.rubypeople.rdt.internal.ui.wizards.NewWizardMessages;

/**
 * Action to get help.
 */
public class HelpAction extends Action {
    
    public HelpAction() {
        super();
        setImageDescriptor(RubyPluginImages.DESC_OBJS_HELP);
        setText(NewWizardMessages.NewSourceContainerWorkbookPage_ToolBar_Help_label); 
        setToolTipText(NewWizardMessages.NewSourceContainerWorkbookPage_ToolBar_Help_tooltip); 
    }
    
    public void run() {
        PlatformUI.getWorkbench().getHelpSystem().displayHelpResource(NewWizardMessages.NewSourceContainerWorkbookPage_ToolBar_Help_link); 
    }
}
