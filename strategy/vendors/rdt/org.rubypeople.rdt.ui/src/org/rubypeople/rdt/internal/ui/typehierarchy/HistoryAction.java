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
package org.rubypeople.rdt.internal.ui.typehierarchy;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.PlatformUI;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.internal.corext.util.Messages;
import org.rubypeople.rdt.internal.ui.IRubyHelpContextIds;
import org.rubypeople.rdt.internal.ui.viewsupport.RubyElementImageProvider;
import org.rubypeople.rdt.ui.RubyElementLabels;

/**
 * Action used for the type hierarchy forward / backward buttons
 */
public class HistoryAction extends Action {

	private TypeHierarchyViewPart fViewPart;
	private IRubyElement fElement;
	
	public HistoryAction(TypeHierarchyViewPart viewPart, IRubyElement element) {
        super("", AS_RADIO_BUTTON); //$NON-NLS-1$
		fViewPart= viewPart;
		fElement= element;		
		
		String elementName= RubyElementLabels.getElementLabel(element, RubyElementLabels.ALL_POST_QUALIFIED | RubyElementLabels.ALL_DEFAULT);
		setText(elementName);
		setImageDescriptor(getImageDescriptor(element));
				
		setDescription(Messages.format(TypeHierarchyMessages.HistoryAction_description, elementName)); 
		setToolTipText(Messages.format(TypeHierarchyMessages.HistoryAction_tooltip, elementName)); 
		PlatformUI.getWorkbench().getHelpSystem().setHelp(this, IRubyHelpContextIds.HISTORY_ACTION);
	}
	
	private ImageDescriptor getImageDescriptor(IRubyElement elem) {
		RubyElementImageProvider imageProvider= new RubyElementImageProvider();
		ImageDescriptor desc= imageProvider.getBaseImageDescriptor(elem, 0);
		imageProvider.dispose();
		return desc;
	}
	
	/*
	 * @see Action#run()
	 */
	public void run() {
		fViewPart.gotoHistoryEntry(fElement);
	}
	
}
