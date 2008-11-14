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
package org.rubypeople.rdt.ui.dialogs;

import org.eclipse.jface.resource.ImageDescriptor;

/**
 * A special image descriptor provider for {@link ITypeInfoRequestor}.
 * <p>
 * The interface should be implemented by clients wishing to provide special
 * images inside the type selection dialog.
 * </p>
 * 
 * @since 1.0
 */
public interface ITypeInfoImageProvider {

	/**
	 * Returns the image descriptor for the type represented by the 
	 * given {@link ITypeInfoRequestor}.
	 * <p>
	 * Note, that this method may be called from non UI threads.
	 * </p>
	 * 
	 * @param typeInfoRequestor the {@link ITypeInfoRequestor} to access
	 *  information for the type under inspection
	 * 
	 * @return the image descriptor or <code>null</code> to use the default
	 *  image
	 */
	public ImageDescriptor getImageDescriptor(ITypeInfoRequestor typeInfoRequestor);
	
}
