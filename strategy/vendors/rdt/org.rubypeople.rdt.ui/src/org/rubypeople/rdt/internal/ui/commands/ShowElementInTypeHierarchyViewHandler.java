/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 ******************************************************************************/
package org.rubypeople.rdt.internal.ui.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchWindow;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.internal.ui.RubyPlugin;
import org.rubypeople.rdt.internal.ui.util.OpenTypeHierarchyUtil;

/**
 * A command handler to show a ruby element in the type hierarchy view.
 * 
 * @since 1.0
 */
public class ShowElementInTypeHierarchyViewHandler extends AbstractHandler {

	private static final String PARAM_ID_ELEMENT_REF= "elementRef"; //$NON-NLS-1$

	public Object execute(ExecutionEvent event) throws ExecutionException {

		IWorkbenchWindow window= RubyPlugin.getActiveWorkbenchWindow();
		if (window == null)
			return null;

		IRubyElement rubyElement= (IRubyElement) event.getObjectParameterForExecution(PARAM_ID_ELEMENT_REF);

		OpenTypeHierarchyUtil.open(rubyElement, window);

		return null;
	}
}
