/*******************************************************************************
 * Copyright (c) 2000, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.rubypeople.rdt.internal.core;

import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IRubyElementDelta;
import org.rubypeople.rdt.core.RubyModelException;

/**
 * Discards a working copy (decrement its use count and remove its working copy
 * info if the use count is 0) and signal its removal through a delta.
 */
public class DiscardWorkingCopyOperation extends RubyModelOperation {

    public DiscardWorkingCopyOperation(RubyScript workingCopy) {
        super(new IRubyElement[] { workingCopy});
    }

    protected void executeOperation() throws RubyModelException {
        RubyScript workingCopy = getWorkingCopy();

        int useCount = RubyModelManager.getRubyModelManager()
                .discardPerWorkingCopyInfo(workingCopy);
        if (useCount == 0) {
            if (!workingCopy.isPrimary()) {
                // report removed java delta for a non-primary working copy
                RubyElementDelta delta = new RubyElementDelta(this.getRubyModel());
                delta.removed(workingCopy);
                addDelta(delta);
                removeReconcileDelta(workingCopy);
            } else {
                if (workingCopy.getResource().isAccessible()) {
                    // report a F_PRIMARY_WORKING_COPY change delta for a
                    // primary working copy
                    RubyElementDelta delta = new RubyElementDelta(this.getRubyModel());
                    delta.changed(workingCopy, IRubyElementDelta.F_PRIMARY_WORKING_COPY);
                    addDelta(delta);
                } else {
                    // report a REMOVED delta
                    RubyElementDelta delta = new RubyElementDelta(this.getRubyModel());
                    delta.removed(workingCopy, IRubyElementDelta.F_PRIMARY_WORKING_COPY);
                    addDelta(delta);
                }
            }
        }
    }

    /**
     * Returns the working copy this operation is working on.
     */
    protected RubyScript getWorkingCopy() {
        return (RubyScript) getElementToProcess();
    }

    /**
     * @see RubyModelOperation#isReadOnly
     */
    public boolean isReadOnly() {
        return true;
    }
}
