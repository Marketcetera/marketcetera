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

import org.eclipse.jface.wizard.IWizardPage;
import org.rubypeople.rdt.core.ILoadpathEntry;

/**
 * A classpath container page allows the user to create a new or edit an 
 * existing classpath container entry.
 * <p>
 * Clients should implement this interface and include the name of their 
 * class in an extension contributed to the jdt.ui's classpath container page 
 * extension point (named <code>org.eclipse.jdt.ui.classpathContainerPage
 * </code>).
 * </p>
 * <p>
 * Clients implementing this interface may subclass from 
 * <code>org.eclipse.jface.wizard.WizardPage</code>.
 * </p>
 * Clients implementing this interface may also implement
 * <code>ILoadpathContainerPageExtension</code> to get additional context
 * before this page is opened.
 *
 * @since 2.0
 */
public interface ILoadpathContainerPage extends IWizardPage {
	
	/**
	 * Called when the classpath container wizard is closed by selecting 
	 * the finish button. Implementers typically override this method to 
	 * store the page result (new/changed classpath entry returned in 
	 * getSelection) into its model.
	 * 
	 * @return if the operation was successful. Only when returned
	 * <code>true</code>, the wizard will close.
	 */
	public boolean finish();
	
	/**
	 * Returns the edited or created classpath container entry. This method
	 * may return <code>null</code> if no classpath container entry exists.
	 * The returned classpath entry is of kind <code>ILoadpathEntry.CPE_CONTAINER
	 * </code>.
	 * 
	 * @return the classpath entry edited or created on the page.
	 */
	public ILoadpathEntry getSelection();
	
	/**
	 * Sets the classpath container entry to be edited or <code>null</code> 
	 * if a new entry should be created.
	 * 
	 * @param containerEntry the classpath entry to edit or <code>null</code>.
	 * If not <code>null</code> then the classpath entry must be of
	 * kind <code>ILoadpathEntry.CPE_CONTAINER</code>
	 */
	public void setSelection(ILoadpathEntry containerEntry);
		
}
