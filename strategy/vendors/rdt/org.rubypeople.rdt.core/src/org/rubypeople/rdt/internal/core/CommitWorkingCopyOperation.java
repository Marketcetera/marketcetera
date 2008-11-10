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

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.rubypeople.rdt.core.IBuffer;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IRubyModelStatus;
import org.rubypeople.rdt.core.IRubyModelStatusConstants;
import org.rubypeople.rdt.core.IRubyScript;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.internal.core.util.Messages;
import org.rubypeople.rdt.internal.core.util.Util;

/**
 * Commits the contents of a working copy compilation unit to its original
 * element and resource, bringing the Ruby Model up-to-date with the current
 * contents of the working copy.
 * 
 * <p>
 * It is possible that the contents of the original resource have changed since
 * the working copy was created, in which case there is an update conflict. This
 * operation allows for two settings to resolve conflict set by the
 * <code>fForce</code> flag:
 * <ul>
 * <li>force flag is <code>false</code> - in this case an
 * <code>RubyModelException</code> is thrown</li>
 * <li>force flag is <code>true</code> - in this case the contents of the
 * working copy are applied to the underlying resource even though the working
 * copy was created before a subsequent change in the resource</li>
 * </ul>
 * 
 * <p>
 * The default conflict resolution setting is the force flag is
 * <code>false</code>
 * 
 * A RubyModelOperation exception is thrown either if the commit could not be
 * performed or if the new content of the compilation unit violates some Ruby
 * Model constraint (e.g. if the new package declaration doesn't match the name
 * of the folder containing the compilation unit).
 */
public class CommitWorkingCopyOperation extends RubyModelOperation {


    /**
     * Constructs an operation to commit the contents of a working copy to its
     * original compilation unit.
     */
    public CommitWorkingCopyOperation(IRubyScript element, boolean force) {
        super(new IRubyElement[] { element}, force);
    }

    /**
     * @exception RubyModelException
     *                if setting the source of the original compilation unit
     *                fails
     */
    protected void executeOperation() throws RubyModelException {
        try {
            beginTask(Messages.workingCopy_commit, 2);
            RubyScript workingCopy = getRubyScript();
            IFile resource = (IFile) workingCopy.getResource();

            if (resource == null) {
                // case of a working copy without a resource
                workingCopy.getBuffer().save(this.progressMonitor, this.force);
                return;
            }

            IRubyScript primary = workingCopy.getPrimary();
            boolean isPrimary = workingCopy.isPrimary();

            RubyElementDeltaBuilder deltaBuilder = null;
           
            boolean isIncluded = !Util.isExcluded(workingCopy);
            if (isPrimary
                    || (isIncluded && resource.isAccessible() && Util
                            .isValidRubyScriptName(workingCopy.getElementName()))) {

                // force opening so that the delta builder can get the old info
                if (!isPrimary && !primary.isOpen()) {
                    primary.open(null);
                }

                // creates the delta builder (this remembers the content of the
                // cu) if:
                // - it is not excluded
                // - and it is not a primary or it is a non-consistent primary
                if (isIncluded && (!isPrimary || !workingCopy.isConsistent())) {
                    deltaBuilder = new RubyElementDeltaBuilder(primary);
                }

                // save the cu
                IBuffer primaryBuffer = primary.getBuffer();
                if (!isPrimary) {
                    if (primaryBuffer == null) return;
                    char[] primaryContents = primaryBuffer.getCharacters();
                    boolean hasSaved = false;
                    try {
                        IBuffer workingCopyBuffer = workingCopy.getBuffer();
                        if (workingCopyBuffer == null) return;
                        primaryBuffer.setContents(workingCopyBuffer.getCharacters());
                        primaryBuffer.save(this.progressMonitor, this.force);
                        primary.makeConsistent(this);
                        hasSaved = true;
                    } finally {
                        if (!hasSaved) {
                            // restore original buffer contents since something
                            // went wrong
                            primaryBuffer.setContents(primaryContents);
                        }
                    }
                } else {
                    // for a primary working copy no need to set the content of
                    // the buffer again
                    primaryBuffer.save(this.progressMonitor, this.force);
                    primary.makeConsistent(this);
                }
            } else {
                // working copy on cu outside classpath OR resource doesn't
                // exist yet
                String encoding = null;
                try {
                    encoding = resource.getCharset();
                } catch (CoreException ce) {
                    // use no encoding
                }
                String contents = workingCopy.getSource();
                if (contents == null) return;
                try {
                    byte[] bytes = encoding == null ? contents.getBytes() : contents
                            .getBytes(encoding);
                    ByteArrayInputStream stream = new ByteArrayInputStream(bytes);
                    if (resource.exists()) {
                        resource.setContents(stream, this.force ? IResource.FORCE
                                | IResource.KEEP_HISTORY : IResource.KEEP_HISTORY, null);
                    } else {
                        resource.create(stream, this.force, this.progressMonitor);
                    }
                } catch (CoreException e) {
                    throw new RubyModelException(e);
                } catch (UnsupportedEncodingException e) {
                    throw new RubyModelException(e, IRubyModelStatusConstants.IO_EXCEPTION);
                }

            }

            setAttribute(HAS_MODIFIED_RESOURCE_ATTR, TRUE);

            // make sure working copy is in sync
            workingCopy.updateTimeStamp((RubyScript) primary);
            workingCopy.makeConsistent(this);
            worked(1);

            // build the deltas
            if (deltaBuilder != null) {
                deltaBuilder.buildDeltas();

                // add the deltas to the list of deltas created during this
                // operation
                if (deltaBuilder.delta != null) {
                    addDelta(deltaBuilder.delta);
                }
            }
            worked(1);
        } finally {
            done();
        }
    }

    /**
     * Returns the compilation unit this operation is working on.
     */
    protected RubyScript getRubyScript() {
        return (RubyScript) getElementToProcess();
    }

    protected ISchedulingRule getSchedulingRule() {
        IResource resource = getElementToProcess().getResource();
        if (resource == null) return null;
        IWorkspace workspace = resource.getWorkspace();
        if (resource.exists()) {
            return workspace.getRuleFactory().modifyRule(resource);
        } else {
            return workspace.getRuleFactory().createRule(resource);
        }
    }

    /**
     * Possible failures:
     * <ul>
     * <li>INVALID_ELEMENT_TYPES - the compilation unit supplied to this
     * operation is not a working copy
     * <li>ELEMENT_NOT_PRESENT - the compilation unit the working copy is based
     * on no longer exists.
     * <li>UPDATE_CONFLICT - the original compilation unit has changed since
     * the working copy was created and the operation specifies no force
     * <li>READ_ONLY - the original compilation unit is in read-only mode
     * </ul>
     */
    public IRubyModelStatus verify() {
        RubyScript cu = getRubyScript();
        if (!cu.isWorkingCopy()) { return new RubyModelStatus(
                IRubyModelStatusConstants.INVALID_ELEMENT_TYPES, cu); }
        if (cu.hasResourceChanged() && !this.force) { return new RubyModelStatus(
                IRubyModelStatusConstants.UPDATE_CONFLICT); }
        // no read-only check, since some repository adapters can change the
        // flag on save
        // operation.
        return RubyModelStatus.VERIFIED_OK;
    }
}
