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
package org.rubypeople.rdt.ui.wizards;

import org.rubypeople.rdt.core.ILoadpathEntry;
import org.rubypeople.rdt.core.IRubyProject;

/**
 * Loadpath container pages that implement <code>ILoadpathContainerPage</code> can 
 * optionally implement <code>ILoadpathContainerPageExtension</code> to get additional
 * information about the context when the page is opened. Method <code>initialize()</code>
 * is called before  <code>ILoadpathContainerPage.setSelection</code>.
 *
 * @since 2.1
 */
public interface ILoadpathContainerPageExtension {
	
	/**
	 * Method <code>initialize()</code> is called before  <code>ILoadpathContainerPage.setSelection</code>
	 * to give additional information about the context the classpath container entry is configured in. This information
	 * only reflects the underlying dialogs current selection state. The user still can make changes after the
	 * the classpath container pages has been closed or decide to cancel the operation.
	 * @param project The project the new or modified entry is added to. The project does not have to exist. 
	 * Project can be <code>null</code>.
	 * @param currentEntries The class path entries currently selected to be set as the projects classpath. This can also
	 * include the entry to be edited.
	 */
	public void initialize(IRubyProject project, ILoadpathEntry[] currentEntries);

}
