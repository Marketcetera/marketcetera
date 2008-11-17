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
package org.rubypeople.rdt.internal.ui.browsing;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.OpenInNewWindowAction;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.RubyCore;

/*
 * XXX: This is a workaround for: http://dev.eclipse.org/bugs/show_bug.cgi?id=13070
 * This class can be removed once the bug is fixed.
 *
 * @since 2.0
 */
public class PatchedOpenInNewWindowAction extends OpenInNewWindowAction {

	private IWorkbenchWindow fWorkbenchWindow;

	public PatchedOpenInNewWindowAction(IWorkbenchWindow window, IAdaptable input) {
		super(window, input);
		fWorkbenchWindow= window;
	}

	public void run() {
		RubyBrowsingPerspectiveFactory.setInputFromAction(getSelectedRubyElement());
		try {
			super.run();
		} finally {
			RubyBrowsingPerspectiveFactory.setInputFromAction(null);
		}
	}

	private IRubyElement getSelectedRubyElement() {
		if (fWorkbenchWindow.getActivePage() != null) {
			ISelection selection= fWorkbenchWindow.getActivePage().getSelection();
			if (selection instanceof IStructuredSelection && !selection.isEmpty()) {
				Object selectedElement= ((IStructuredSelection)selection).getFirstElement();
				if (selectedElement instanceof IRubyElement)
					return (IRubyElement)selectedElement;
				if (!(selectedElement instanceof IRubyElement) && selectedElement instanceof IAdaptable)
					return (IRubyElement)((IAdaptable)selectedElement).getAdapter(IRubyElement.class);
				else if (selectedElement instanceof IWorkspace)
						return RubyCore.create(((IWorkspace)selectedElement).getRoot());
			}
		}
		return null;
	}
}
