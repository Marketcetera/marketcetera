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
 *******************************************************************************
 * Copyright (c) 2000, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.rubypeople.rdt.internal.ui.wizards.buildpaths;

import java.util.ArrayList;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.rubypeople.rdt.core.ILoadpathEntry;
import org.rubypeople.rdt.internal.ui.RubyPlugin;
import org.rubypeople.rdt.internal.ui.util.CoreUtility;
import org.rubypeople.rdt.ui.RubyUI;
import org.rubypeople.rdt.ui.wizards.ILoadpathContainerPage;

/**
  */
public class LoadpathContainerDescriptor {

	private IConfigurationElement fConfigElement;
	private ILoadpathContainerPage fPage;

	private static final String ATT_EXTENSION = "loadpathContainerPage"; //$NON-NLS-1$

	private static final String ATT_ID = "id"; //$NON-NLS-1$
	private static final String ATT_NAME = "name"; //$NON-NLS-1$
	private static final String ATT_PAGE_CLASS = "class"; //$NON-NLS-1$	

	public LoadpathContainerDescriptor(IConfigurationElement configElement) throws CoreException {
		super();
		fConfigElement = configElement;
		fPage= null;

		String id = fConfigElement.getAttribute(ATT_ID);
		String name = configElement.getAttribute(ATT_NAME);
		String pageClassName = configElement.getAttribute(ATT_PAGE_CLASS);

		if (name == null) {
			throw new CoreException(new Status(IStatus.ERROR, RubyUI.ID_PLUGIN, 0, "Invalid extension (missing name): " + id, null)); //$NON-NLS-1$
		}
		if (pageClassName == null) {
			throw new CoreException(new Status(IStatus.ERROR, RubyUI.ID_PLUGIN, 0, "Invalid extension (missing page class name): " + id, null)); //$NON-NLS-1$
		}
	}

	public ILoadpathContainerPage createPage() throws CoreException  {
		if (fPage == null) {
			Object elem= CoreUtility.createExtension(fConfigElement, ATT_PAGE_CLASS);
			if (elem instanceof ILoadpathContainerPage) {
				fPage= (ILoadpathContainerPage) elem;
			} else {
				String id= fConfigElement.getAttribute(ATT_ID);
				throw new CoreException(new Status(IStatus.ERROR, RubyUI.ID_PLUGIN, 0, "Invalid extension (page not of type ILoadpathContainerPage): " + id, null)); //$NON-NLS-1$
			}
		}
		return fPage;
	}
	
	public ILoadpathContainerPage getPage() {
		return fPage;
	}
	
	public void setPage(ILoadpathContainerPage page) {
		fPage= page;
	}
	
	public void dispose() {
		if (fPage != null) {
			fPage.dispose();
			fPage= null;
		}
	}

	public String getName() {
		return fConfigElement.getAttribute(ATT_NAME);
	}
	
	public String getPageClass() {
		return fConfigElement.getAttribute(ATT_PAGE_CLASS);
	}	

	public boolean canEdit(ILoadpathEntry entry) {
		String id = fConfigElement.getAttribute(ATT_ID);
		if (entry.getEntryKind() == ILoadpathEntry.CPE_CONTAINER) {
			String type = entry.getPath().segment(0);
			return id.equals(type);
		}
		return false;
	}

	public static LoadpathContainerDescriptor[] getDescriptors() {
		ArrayList containers= new ArrayList();
		
		IExtensionPoint extensionPoint = Platform.getExtensionRegistry().getExtensionPoint(RubyUI.ID_PLUGIN, ATT_EXTENSION);
		if (extensionPoint != null) {
			LoadpathContainerDescriptor defaultPage= null;
			String defaultPageName= LoadpathContainerDefaultPage.class.getName();
			
			IConfigurationElement[] elements = extensionPoint.getConfigurationElements();
			for (int i = 0; i < elements.length; i++) {
				try {
					LoadpathContainerDescriptor curr= new LoadpathContainerDescriptor(elements[i]);					
					if (defaultPageName.equals(curr.getPageClass())) {
						defaultPage= curr;
					} else {
						containers.add(curr);
					}
				} catch (CoreException e) {
					RubyPlugin.log(e);
				}
			}
			if (defaultPageName != null && containers.isEmpty()) {
				// default page only added of no other extensions found
				containers.add(defaultPage);
			}
		}
		return (LoadpathContainerDescriptor[]) containers.toArray(new LoadpathContainerDescriptor[containers.size()]);
	}

}
