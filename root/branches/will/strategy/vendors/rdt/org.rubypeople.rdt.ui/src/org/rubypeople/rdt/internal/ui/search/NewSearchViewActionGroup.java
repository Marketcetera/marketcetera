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
package org.rubypeople.rdt.internal.ui.search;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.util.Assert;
import org.eclipse.jface.viewers.OpenEvent;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.actions.ActionGroup;
import org.rubypeople.rdt.internal.ui.actions.CompositeActionGroup;
import org.rubypeople.rdt.ui.actions.NavigateActionGroup;

class NewSearchViewActionGroup extends CompositeActionGroup {
	NavigateActionGroup fNavigateActionGroup;
	
	public NewSearchViewActionGroup(IViewPart part) {
		Assert.isNotNull(part);
		setGroups(new ActionGroup[]{
			fNavigateActionGroup= new NavigateActionGroup(part),
//			new GenerateActionGroup(part), 
//			new RefactorActionGroup(part),
//			new RubySearchActionGroup(part)  XXX Create the RubySearchActionGroup!
			});
	}
	
	public void handleOpen(OpenEvent event) {
		IAction openAction= fNavigateActionGroup.getOpenAction();
		if (openAction != null && openAction.isEnabled()) {
			openAction.run();
			return;
		}
	}
}

