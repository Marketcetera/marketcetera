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

import org.rubypeople.rdt.core.IProblemRequestor;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IRubyElementDelta;
import org.rubypeople.rdt.core.RubyModelException;

/**
 * Switch and ICompilationUnit to working copy mode and signal the working copy
 * addition through a delta.
 */
public class BecomeWorkingCopyOperation extends RubyModelOperation {

    private IProblemRequestor problemRequestor;

    /*
     * Creates a BecomeWorkingCopyOperation for the given working copy.
     * perOwnerWorkingCopies map is not null if the working copy is a shared
     * working copy.
     */
    public BecomeWorkingCopyOperation(RubyScript workingCopy, IProblemRequestor problemRequestor) {
        super(new IRubyElement[] { workingCopy});
        this.problemRequestor = problemRequestor;
    }

    protected void executeOperation() throws RubyModelException {
        // open the working copy now to ensure contents are that of the current
        // state of this element
        RubyScript workingCopy = getWorkingCopy();
        RubyModelManager.getRubyModelManager().getPerWorkingCopyInfo(workingCopy,
                true/* create if needed */, true/* record usage */, this.problemRequestor);
        workingCopy.openWhenClosed(workingCopy.createElementInfo(), this.progressMonitor);

        if (!workingCopy.isPrimary()) {
            // report added java delta for a non-primary working copy
            RubyElementDelta delta = new RubyElementDelta(getRubyModel());
            delta.added(workingCopy);
            addDelta(delta);
        } else {
            if (workingCopy.getResource().isAccessible()) {
                // report a F_PRIMARY_WORKING_COPY change delta for a primary
                // working copy
                RubyElementDelta delta = new RubyElementDelta(getRubyModel());
                delta.changed(workingCopy, IRubyElementDelta.F_PRIMARY_WORKING_COPY);
                addDelta(delta);
            } else {
                // report an ADDED delta
                RubyElementDelta delta = new RubyElementDelta(this.getRubyModel());
                delta.added(workingCopy, IRubyElementDelta.F_PRIMARY_WORKING_COPY);
                addDelta(delta);
            }
        }

        this.resultElements = new IRubyElement[] { workingCopy};
    }

    /*
     * Returns the working copy this operation is working on.
     */
    protected RubyScript getWorkingCopy() {
        return (RubyScript) getElementToProcess();
    }

    /*
     * @see RubyModelOperation#isReadOnly
     */
    public boolean isReadOnly() {
        return true;
    }

}
