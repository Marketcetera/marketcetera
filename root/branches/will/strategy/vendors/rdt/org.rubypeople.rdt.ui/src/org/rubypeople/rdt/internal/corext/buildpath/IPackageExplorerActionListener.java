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

package org.rubypeople.rdt.internal.corext.buildpath;

import org.rubypeople.rdt.internal.ui.wizards.buildpaths.newsourcepage.DialogPackageExplorerActionGroup;

/**
 * Interface for listeners of <code>PackageExplorerActionEvent</code>.
 */
public interface IPackageExplorerActionListener {
    
    /**
     * Handle the <code>PackageExplorerActionEvent</code> which is fired 
     * whenever the set of available actions changes.
     * 
     * @param event event to be processed
     * 
     * @see PackageExplorerActionEvent
     * @see DialogPackageExplorerActionGroup#addListener(IPackageExplorerActionListener)
     */
    public void handlePackageExplorerActionEvent(PackageExplorerActionEvent event);
    
}
