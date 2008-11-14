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
 * Copyright (c) 2000, 2006 IBM Corporation and others.
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


/**
 * Loadpath container pages that implement {@link ILoadpathContainerPage} can 
 * optionally implement {@link ILoadpathContainerPageExtension2} to return more
 * than one element when creating new containers. If implemented, the method {@link #getNewContainers()}
 * is used instead of the method {@link ILoadpathContainerPage#getSelection() } to get the
 * newly selected containers. {@link ILoadpathContainerPage#getSelection() } is still used
 * for edited elements.
 *
 * @since 1.0.0
 */
public interface ILoadpathContainerPageExtension2 {
	
	/**
	 * Method {@link #getNewContainers()} is called instead of {@link ILoadpathContainerPage#getSelection() }
	 * to get the newly added containers. {@link ILoadpathContainerPage#getSelection() } is still used
	 * to get the edited elements.
	 * @return the classpath entries created on the page. All returned entries must be on kind
	 * {@link ILoadpathEntry#CPE_CONTAINER}
	 */
	public ILoadpathEntry[] getNewContainers();

}
