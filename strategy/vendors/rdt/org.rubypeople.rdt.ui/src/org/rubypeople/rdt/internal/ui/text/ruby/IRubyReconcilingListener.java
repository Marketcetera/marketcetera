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
package org.rubypeople.rdt.internal.ui.text.ruby;

import org.eclipse.core.runtime.IProgressMonitor;
import org.jruby.ast.RootNode;
import org.rubypeople.rdt.core.IRubyScript;


/**
 * Interface of an object listening to Ruby reconciling.
 *
 * @since 1.0
 */
public interface IRubyReconcilingListener {

    /**
     * Called before reconciling is started.
     */
    void aboutToBeReconciled();

    /**
     * Called after reconciling has been finished.
     * @param ast               the ruby script AST or <code>null</code> if
     *                              the working copy was consistent or reconciliation has been cancelled
     * @param forced            <code>true</code> iff this reconciliation was forced
     * @param progressMonitor   the progress monitor
     */
    void reconciled(IRubyScript script, RootNode ast, boolean forced, IProgressMonitor progressMonitor);
}
