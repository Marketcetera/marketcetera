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
package org.rubypeople.rdt.internal.core;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.*;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.rubypeople.rdt.core.*;
import org.rubypeople.rdt.internal.core.util.Messages;
import org.eclipse.jface.text.IDocument;

/**
 * Defines behavior common to all Ruby Model operations
 */
public abstract class RubyModelOperation implements IWorkspaceRunnable, IProgressMonitor {
    protected interface IPostAction {
        /*
         * Returns the id of this action.
         * @see JavaModelOperation#postAction
         */
        String getID();
        /*
         * Run this action.
         */
        void run() throws RubyModelException;
    }
    /*
     * Constants controlling the insertion mode of an action.
     * @see JavaModelOperation#postAction
     */
    protected static final int APPEND = 1; // insert at the end
    protected static final int REMOVEALL_APPEND = 2; // remove all existing ones with same ID, and add new one at the end
    protected static final int KEEP_EXISTING = 3; // do not insert if already existing with same ID
    
    /*
     * Whether tracing post actions is enabled.
     */
    protected static boolean POST_ACTION_VERBOSE;

    /*
     * A list of IPostActions.
     */
    protected IPostAction[] actions;
    protected int actionsStart = 0;
    protected int actionsEnd = -1;
    /*
     * A HashMap of attributes that can be used by operations
     */
    protected HashMap<Object, Object> attributes;

    public static final String HAS_MODIFIED_RESOURCE_ATTR = "hasModifiedResource"; //$NON-NLS-1$
    public static final String TRUE = "true"; //$NON-NLS-1$
    //public static final String FALSE = "false";
        
    /**
     * The elements this operation operates on,
     * or <code>null</code> if this operation
     * does not operate on specific elements.
     */
    protected IRubyElement[] elementsToProcess;
    /**
     * The parent elements this operation operates with
     * or <code>null</code> if this operation
     * does not operate with specific parent elements.
     */
    protected IRubyElement[] parentElements;
    /**
     * An empty collection of <code>IRubyElement</code>s - the common
     * empty result if no elements are created, or if this
     * operation is not actually executed.
     */
    protected static IRubyElement[] NO_ELEMENTS= new IRubyElement[] {};


    /**
     * The elements created by this operation - empty
     * until the operation actually creates elements.
     */
    protected IRubyElement[] resultElements= NO_ELEMENTS;

    /**
     * The progress monitor passed into this operation
     */
    protected IProgressMonitor progressMonitor= null;
    /**
     * A flag indicating whether this operation is nested.
     */
    protected boolean isNested = false;
    /**
     * Conflict resolution policy - by default do not force (fail on a conflict).
     */
    protected boolean force = false;

    /*
     * A per thread stack of ruby model operations (PerThreadObject of ArrayList).
     */
    protected static ThreadLocal<ArrayList<RubyModelOperation>> operationStacks = new ThreadLocal<ArrayList<RubyModelOperation>>();
    protected RubyModelOperation() {
        // default constructor used in subclasses
    }
    /**
     * A common constructor for all Ruby Model operations.
     */
    protected RubyModelOperation(IRubyElement[] elements) {
        this.elementsToProcess = elements;
    }
    /**
     * Common constructor for all Ruby Model operations.
     */
    protected RubyModelOperation(IRubyElement[] elementsToProcess, IRubyElement[] parentElements) {
        this.elementsToProcess = elementsToProcess;
        this.parentElements= parentElements;
    }
    /**
     * A common constructor for all Ruby Model operations.
     */
    protected RubyModelOperation(IRubyElement[] elementsToProcess, IRubyElement[] parentElements, boolean force) {
        this.elementsToProcess = elementsToProcess;
        this.parentElements= parentElements;
        this.force= force;
    }
    /**
     * A common constructor for all Ruby Model operations.
     */
    protected RubyModelOperation(IRubyElement[] elements, boolean force) {
        this.elementsToProcess = elements;
        this.force= force;
    }
    
    /**
     * Common constructor for all Ruby Model operations.
     */
    protected RubyModelOperation(IRubyElement element) {
        this.elementsToProcess = new IRubyElement[]{element};
    }
    /**
     * A common constructor for all Ruby Model operations.
     */
    protected RubyModelOperation(IRubyElement element, boolean force) {
        this.elementsToProcess = new IRubyElement[]{element};
        this.force= force;
    }
    
    /*
     * Registers the given action at the end of the list of actions to run.
     */
    protected void addAction(IPostAction action) {
        int length = this.actions.length;
        if (length == ++this.actionsEnd) {
            System.arraycopy(this.actions, 0, this.actions = new IPostAction[length*2], 0, length);
        }
        this.actions[this.actionsEnd] = action;
    }
    /*
     * Registers the given delta with the Ruby Model Manager.
     */
    protected void addDelta(IRubyElementDelta delta) {
        RubyModelManager.getRubyModelManager().getDeltaProcessor().registerRubyModelDelta(delta);
    }
    /*
     * Registers the given reconcile delta with the Ruby Model Manager.
     */
    protected void addReconcileDelta(IRubyScript workingCopy, IRubyElementDelta delta) {
        HashMap<IRubyScript, IRubyElementDelta> reconcileDeltas = RubyModelManager.getRubyModelManager().getDeltaProcessor().reconcileDeltas;
        RubyElementDelta previousDelta = (RubyElementDelta)reconcileDeltas.get(workingCopy);
        if (previousDelta != null) {
            IRubyElementDelta[] children = delta.getAffectedChildren();
            for (int i = 0, length = children.length; i < length; i++) {
                RubyElementDelta child = (RubyElementDelta)children[i];
                previousDelta.insertDeltaTree(child.getElement(), child);
            }
            // note that the last delta's AST always takes precedence over the existing delta's AST
            // since it is the result of the last reconcile operation
            if ((delta.getFlags() & IRubyElementDelta.F_AST_AFFECTED) != 0) {
                previousDelta.changedAST(delta.getRubyScriptAST());
            }
                        
        } else {
            reconcileDeltas.put(workingCopy, delta);
        }
    }
    /*
     * Deregister the reconcile delta for the given working copy
     */
    protected void removeReconcileDelta(IRubyScript workingCopy) {
        RubyModelManager.getRubyModelManager().getDeltaProcessor().reconcileDeltas.remove(workingCopy);     
    }
    /**
     * @see IProgressMonitor
     */
    public void beginTask(String name, int totalWork) {
        if (progressMonitor != null) {
            progressMonitor.beginTask(name, totalWork);
        }
    }
    /*
     * Returns whether this operation can modify the package fragment roots.
     */
    protected boolean canModifyRoots() {
        return false;
    }
    /**
     * Checks with the progress monitor to see whether this operation
     * should be canceled. An operation should regularly call this method
     * during its operation so that the user can cancel it.
     *
     * @exception OperationCanceledException if cancelling the operation has been requested
     * @see IProgressMonitor#isCanceled
     */
    protected void checkCanceled() {
        if (isCanceled()) {
            throw new OperationCanceledException(Messages.operation_cancelled); 
        }
    }
    /**
     * Common code used to verify the elements this operation is processing.
     * @see RubyModelOperation#verify()
     */
    protected IRubyModelStatus commonVerify() {
        if (elementsToProcess == null || elementsToProcess.length == 0) {
            return new RubyModelStatus(IRubyModelStatusConstants.NO_ELEMENTS_TO_PROCESS);
        }
        for (int i = 0; i < elementsToProcess.length; i++) {
            if (elementsToProcess[i] == null) {
                return new RubyModelStatus(IRubyModelStatusConstants.NO_ELEMENTS_TO_PROCESS);
            }
        }
        return RubyModelStatus.VERIFIED_OK;
    }
    /**
     * Convenience method to copy resources
     */
    protected void copyResources(IResource[] resources, IPath destinationPath) throws RubyModelException {
        IProgressMonitor subProgressMonitor = getSubProgressMonitor(resources.length);
        IWorkspace workspace = resources[0].getWorkspace();
        try {
            workspace.copy(resources, destinationPath, false, subProgressMonitor);
            this.setAttribute(HAS_MODIFIED_RESOURCE_ATTR, TRUE); 
        } catch (CoreException e) {
            throw new RubyModelException(e);
        }
    }
    /**
     * Convenience method to create a file
     */
    protected void createFile(IContainer folder, String name, InputStream contents, boolean forceFlag) throws RubyModelException {
        IFile file= folder.getFile(new Path(name));
        try {
            file.create(
                contents, 
                forceFlag ? IResource.FORCE | IResource.KEEP_HISTORY : IResource.KEEP_HISTORY, 
                getSubProgressMonitor(1));
                this.setAttribute(HAS_MODIFIED_RESOURCE_ATTR, TRUE); 
        } catch (CoreException e) {
            throw new RubyModelException(e);
        }
    }
    /**
     * Convenience method to create a folder
     */
    protected void createFolder(IContainer parentFolder, String name, boolean forceFlag) throws RubyModelException {
        IFolder folder= parentFolder.getFolder(new Path(name));
        try {
            // we should use true to create the file locally. Only VCM should use tru/false
            folder.create(
                forceFlag ? IResource.FORCE | IResource.KEEP_HISTORY : IResource.KEEP_HISTORY,
                true, // local
                getSubProgressMonitor(1));
            this.setAttribute(HAS_MODIFIED_RESOURCE_ATTR, TRUE); 
        } catch (CoreException e) {
            throw new RubyModelException(e);
        }
    }
    
    /**
     * Convenience method to delete a resource
     */
    protected void deleteResource(IResource resource,int flags) throws RubyModelException {
        try {
            resource.delete(flags, getSubProgressMonitor(1));
            this.setAttribute(HAS_MODIFIED_RESOURCE_ATTR, TRUE); 
        } catch (CoreException e) {
            throw new RubyModelException(e);
        }
    }
    /**
     * Convenience method to delete resources
     */
    protected void deleteResources(IResource[] resources, boolean forceFlag) throws RubyModelException {
        if (resources == null || resources.length == 0) return;
        IProgressMonitor subProgressMonitor = getSubProgressMonitor(resources.length);
        IWorkspace workspace = resources[0].getWorkspace();
        try {
            workspace.delete(
                resources,
                forceFlag ? IResource.FORCE | IResource.KEEP_HISTORY : IResource.KEEP_HISTORY, 
                subProgressMonitor);
                this.setAttribute(HAS_MODIFIED_RESOURCE_ATTR, TRUE); 
        } catch (CoreException e) {
            throw new RubyModelException(e);
        }
    }
    /**
     * @see IProgressMonitor
     */
    public void done() {
        if (progressMonitor != null) {
            progressMonitor.done();
        }
    }
    /*
     * Returns whether the given path is equals to one of the given other paths.
     */
    protected boolean equalsOneOf(IPath path, IPath[] otherPaths) {
        for (int i = 0, length = otherPaths.length; i < length; i++) {
            if (path.equals(otherPaths[i])) {
                return true;
            }
        }
        return false;
    }
    /**
     * Convenience method to run an operation within this operation
     */
    public void executeNestedOperation(RubyModelOperation operation, int subWorkAmount) throws RubyModelException {
        IRubyModelStatus status= operation.verify();
        if (!status.isOK()) {
            throw new RubyModelException(status);
        }
        IProgressMonitor subProgressMonitor = getSubProgressMonitor(subWorkAmount);
        // fix for 1FW7IKC, part (1)
        try {
            operation.setNested(true);
            operation.run(subProgressMonitor);
        } catch (CoreException ce) {
            if (ce instanceof RubyModelException) {
                throw (RubyModelException)ce;
            }
			// translate the core exception to a ruby model exception
			if (ce.getStatus().getCode() == IResourceStatus.OPERATION_FAILED) {
			    Throwable e = ce.getStatus().getException();
			    if (e instanceof RubyModelException) {
			        throw (RubyModelException) e;
			    }
			}
			throw new RubyModelException(ce);
        }
    }
    /**
     * Performs the operation specific behavior. Subclasses must override.
     */
    protected abstract void executeOperation() throws RubyModelException;
    /*
     * Returns the attribute registered at the given key with the top level operation.
     * Returns null if no such attribute is found.
     */
    protected Object getAttribute(Object key) {
        ArrayList stack = this.getCurrentOperationStack();
        if (stack.size() == 0) return null;
        RubyModelOperation topLevelOp = (RubyModelOperation)stack.get(0);
        if (topLevelOp.attributes == null) {
            return null;
        }
		return topLevelOp.attributes.get(key);
    }
    /**
     * Returns the compilation unit the given element is contained in,
     * or the element itself (if it is a compilation unit),
     * otherwise <code>null</code>.
     */
    protected IRubyScript getRubyScriptFor(IRubyElement element) {
    
        return ((RubyElement)element).getRubyScript();
    }
    /*
     * Returns the stack of operations running in the current thread.
     * Returns an empty stack if no operations are currently running in this thread. 
     */
    protected ArrayList<RubyModelOperation> getCurrentOperationStack() {
        ArrayList<RubyModelOperation> stack = operationStacks.get();
        if (stack == null) {
            stack = new ArrayList<RubyModelOperation>();
            operationStacks.set(stack);
        }
        return stack;
    }
    /*
     * Returns the existing document for the given cu, or a DocumentAdapter if none.
     */
    protected IDocument getDocument(IRubyScript cu) throws RubyModelException {
        IBuffer buffer = cu.getBuffer();
        if (buffer instanceof IDocument)
            return (IDocument) buffer;
        return new DocumentAdapter(buffer);
    }
    /**
     * Returns the elements to which this operation applies,
     * or <code>null</code> if not applicable.
     */
    protected IRubyElement[] getElementsToProcess() {
        return elementsToProcess;
    }
    /**
     * Returns the element to which this operation applies,
     * or <code>null</code> if not applicable.
     */
    protected IRubyElement getElementToProcess() {
        if (elementsToProcess == null || elementsToProcess.length == 0) {
            return null;
        }
        return elementsToProcess[0];
    }
    /**
     * Returns the Ruby Model this operation is operating in.
     */
    public IRubyModel getRubyModel() {
        if (elementsToProcess == null || elementsToProcess.length == 0) {
            return getParentElement().getRubyModel();
        }
		return elementsToProcess[0].getRubyModel();
    }

    /**
     * Returns the parent element to which this operation applies,
     * or <code>null</code> if not applicable.
     */
    protected IRubyElement getParentElement() {
        if (parentElements == null || parentElements.length == 0) {
            return null;
        }
        return parentElements[0];
    }
    /**
     * Returns the parent elements to which this operation applies,
     * or <code>null</code> if not applicable.
     */
    protected IRubyElement[] getParentElements() {
        return parentElements;
    }
    /**
     * Returns the elements created by this operation.
     */
    public IRubyElement[] getResultElements() {
        return resultElements;
    }
    /*
     * Returns the scheduling rule for this operation (i.e. the resource that needs to be locked 
     * while this operation is running.
     * Subclasses can override.
     */
    protected ISchedulingRule getSchedulingRule() {
        return ResourcesPlugin.getWorkspace().getRoot();
    }
    /**
     * Creates and returns a subprogress monitor if appropriate.
     */
    protected IProgressMonitor getSubProgressMonitor(int workAmount) {
        IProgressMonitor sub = null;
        if (progressMonitor != null) {
            sub = new SubProgressMonitor(progressMonitor, workAmount, SubProgressMonitor.PREPEND_MAIN_LABEL_TO_SUBTASK);
        }
        return sub;
    }

    /**
     * Returns whether this operation has performed any resource modifications.
     * Returns false if this operation has not been executed yet.
     */
    public boolean hasModifiedResource() {
        return !this.isReadOnly() && this.getAttribute(HAS_MODIFIED_RESOURCE_ATTR) == TRUE; 
    }
    public void internalWorked(double work) {
        if (progressMonitor != null) {
            progressMonitor.internalWorked(work);
        }
    }
    /**
     * @see IProgressMonitor
     */
    public boolean isCanceled() {
        if (progressMonitor != null) {
            return progressMonitor.isCanceled();
        }
        return false;
    }
    /**
     * Returns <code>true</code> if this operation performs no resource modifications,
     * otherwise <code>false</code>. Subclasses must override.
     */
    public boolean isReadOnly() {
        return false;
    }
    /*
     * Returns whether this operation is the first operation to run in the current thread.
     */
    protected boolean isTopLevelOperation() {
        ArrayList stack;
        return 
            (stack = this.getCurrentOperationStack()).size() > 0
            && stack.get(0) == this;
    }
    /*
     * Returns the index of the first registered action with the given id, starting from a given position.
     * Returns -1 if not found.
     */
    protected int firstActionWithID(String id, int start) {
        for (int i = start; i <= this.actionsEnd; i++) {
            if (this.actions[i].getID().equals(id)) {
                return i;
            }
        }
        return -1;
    }
    
    /**
     * Convenience method to move resources
     */
    protected void moveResources(IResource[] resources, IPath destinationPath) throws RubyModelException {
        IProgressMonitor subProgressMonitor = null;
        if (progressMonitor != null) {
            subProgressMonitor = new SubProgressMonitor(progressMonitor, resources.length, SubProgressMonitor.PREPEND_MAIN_LABEL_TO_SUBTASK);
        }
        IWorkspace workspace = resources[0].getWorkspace();
        try {
            workspace.move(resources, destinationPath, false, subProgressMonitor);
            this.setAttribute(HAS_MODIFIED_RESOURCE_ATTR, TRUE); 
        } catch (CoreException e) {
            throw new RubyModelException(e);
        }
    }
    /**
     * Creates and returns a new <code>IRubyElementDelta</code>
     * on the Ruby Model.
     */
    public RubyElementDelta newRubyElementDelta() {
        return new RubyElementDelta(getRubyModel());
    }
    /*
     * Removes the last pushed operation from the stack of running operations.
     * Returns the poped operation or null if the stack was empty.
     */
    protected RubyModelOperation popOperation() {
        ArrayList stack = getCurrentOperationStack();
        int size = stack.size();
        if (size > 0) {
            if (size == 1) { // top level operation 
                operationStacks.set(null); // release reference (see http://bugs.eclipse.org/bugs/show_bug.cgi?id=33927)
            }
            return (RubyModelOperation)stack.remove(size-1);
        }
		return null;
    }
    /*
     * Registers the given action to be run when the outer most java model operation has finished.
     * The insertion mode controls whether:
     * - the action should discard all existing actions with the same id, and be queued at the end (REMOVEALL_APPEND),
     * - the action should be ignored if there is already an action with the same id (KEEP_EXISTING),
     * - the action should be queued at the end without looking at existing actions (APPEND)
     */
    protected void postAction(IPostAction action, int insertionMode) {
        if (POST_ACTION_VERBOSE) {
            System.out.print("(" + Thread.currentThread() + ") [RubyModelOperation.postAction(IPostAction, int)] Posting action " + action.getID()); //$NON-NLS-1$ //$NON-NLS-2$
            switch(insertionMode) {
                case REMOVEALL_APPEND:
                    System.out.println(" (REMOVEALL_APPEND)"); //$NON-NLS-1$
                    break;
                case KEEP_EXISTING:
                    System.out.println(" (KEEP_EXISTING)"); //$NON-NLS-1$
                    break;
                case APPEND:
                    System.out.println(" (APPEND)"); //$NON-NLS-1$
                    break;
            }
        }
        
        RubyModelOperation topLevelOp = getCurrentOperationStack().get(0);
        IPostAction[] postActions = topLevelOp.actions;
        if (postActions == null) {
            topLevelOp.actions = postActions = new IPostAction[1];
            postActions[0] = action;
            topLevelOp.actionsEnd = 0;
        } else {
            String id = action.getID();
            switch (insertionMode) {
                case REMOVEALL_APPEND :
                    int index = this.actionsStart-1;
                    while ((index = topLevelOp.firstActionWithID(id, index+1)) >= 0) {
                        // remove action[index]
                        System.arraycopy(postActions, index+1, postActions, index, topLevelOp.actionsEnd - index);
                        postActions[topLevelOp.actionsEnd--] = null;
                    }
                    topLevelOp.addAction(action);
                    break;
                case KEEP_EXISTING:
                    if (topLevelOp.firstActionWithID(id, 0) < 0) {
                        topLevelOp.addAction(action);
                    }
                    break;
                case APPEND:
                    topLevelOp.addAction(action);
                    break;
            }
        }
    }
    /*
     * Returns whether the given path is the prefix of one of the given other paths.
     */
    protected boolean prefixesOneOf(IPath path, IPath[] otherPaths) {
        for (int i = 0, length = otherPaths.length; i < length; i++) {
            if (path.isPrefixOf(otherPaths[i])) {
                return true;
            }
        }
        return false;
    }   
    /*
     * Pushes the given operation on the stack of operations currently running in this thread.
     */
    protected void pushOperation(RubyModelOperation operation) {
        getCurrentOperationStack().add(operation);
    }
    /*
     * Removes all actions with the given id from the queue of post actions.
     * Does nothing if no such action is in the queue.
     */
    protected void removeAllPostAction(String actionID) {
        if (POST_ACTION_VERBOSE) {
            System.out.println("(" + Thread.currentThread() + ") [RubyModelOperation.removeAllPostAction(String)] Removing actions " + actionID); //$NON-NLS-1$ //$NON-NLS-2$
        }
        
        RubyModelOperation topLevelOp = getCurrentOperationStack().get(0);
        IPostAction[] postActions = topLevelOp.actions;
        if (postActions == null) return;
        int index = this.actionsStart-1;
        while ((index = topLevelOp.firstActionWithID(actionID, index+1)) >= 0) {
            // remove action[index]
            System.arraycopy(postActions, index+1, postActions, index, topLevelOp.actionsEnd - index);
            postActions[topLevelOp.actionsEnd--] = null;
        }
    }
    
    /**
     * Runs this operation and registers any deltas created.
     *
     * @see IWorkspaceRunnable
     * @exception CoreException if the operation fails
     */
    public void run(IProgressMonitor monitor) throws CoreException {
        RubyModelManager manager = RubyModelManager.getRubyModelManager();
        DeltaProcessor deltaProcessor = manager.getDeltaProcessor();
        int previousDeltaCount = deltaProcessor.rubyModelDeltas.size();
        try {
            progressMonitor = monitor;
            pushOperation(this);
            try {
                if (canModifyRoots()) {
                    // computes the root infos before executing the operation
                    // noop if aready initialized
                    RubyModelManager.getRubyModelManager().deltaState.initializeRoots();
                }
                
                executeOperation();
            } finally {
                if (this.isTopLevelOperation()) {
                    this.runPostActions();
                }
            }
        } finally {
            try {
                // reacquire delta processor as it can have been reset during executeOperation()
                deltaProcessor = manager.getDeltaProcessor();
                
                // update RubyModel using deltas that were recorded during this operation
                for (int i = previousDeltaCount, size = deltaProcessor.rubyModelDeltas.size(); i < size; i++) {
                    deltaProcessor.updateRubyModel(deltaProcessor.rubyModelDeltas.get(i));
                }
                
                // close the parents of the created elements and reset their project's cache (in case we are in an 
                // IWorkspaceRunnable and the clients wants to use the created element's parent)
                // see https://bugs.eclipse.org/bugs/show_bug.cgi?id=83646
                for (int i = 0, length = this.resultElements.length; i < length; i++) {
                    IRubyElement element = this.resultElements[i];
                    Openable openable = (Openable) element.getOpenable();
                    if (!(openable instanceof RubyScript) || !((RubyScript) openable).isWorkingCopy()) { // a working copy must remain a child of its parent even after a move
                        ((RubyElement) openable.getParent()).close();
                    }
                }
                
                // fire only iff:
                // - the operation is a top level operation
                // - the operation did produce some delta(s)
                // - but the operation has not modified any resource
                if (this.isTopLevelOperation()) {
                    if ((deltaProcessor.rubyModelDeltas.size() > previousDeltaCount || !deltaProcessor.reconcileDeltas.isEmpty()) 
                            && !this.hasModifiedResource()) {
                        deltaProcessor.fire(null, DeltaProcessor.DEFAULT_CHANGE_EVENT);
                    } // else deltas are fired while processing the resource delta
                }
            } finally {
                popOperation();
            }
        }
    }
    /**
     * Main entry point for Ruby Model operations. Runs a Ruby Model Operation as an IWorkspaceRunnable
     * if not read-only.
     */
    public void runOperation(IProgressMonitor monitor) throws RubyModelException {
        IRubyModelStatus status= verify();
        if (!status.isOK()) {
            throw new RubyModelException(status);
        }
        try {
            if (isReadOnly()) {
                run(monitor);
            } else {
                // Use IWorkspace.run(...) to ensure that a build will be done in autobuild mode.
                // Note that if the tree is locked, this will throw a CoreException, but this is ok
                // as this operation is modifying the tree (not read-only) and a CoreException will be thrown anyway.
                ResourcesPlugin.getWorkspace().run(this, getSchedulingRule(), IWorkspace.AVOID_UPDATE, monitor);
            }
        } catch (CoreException ce) {
            if (ce instanceof RubyModelException) {
                throw (RubyModelException)ce;
            }
			if (ce.getStatus().getCode() == IResourceStatus.OPERATION_FAILED) {
			    Throwable e= ce.getStatus().getException();
			    if (e instanceof RubyModelException) {
			        throw (RubyModelException) e;
			    }
			}
			throw new RubyModelException(ce);
        }
    }
    protected void runPostActions() throws RubyModelException {
        while (this.actionsStart <= this.actionsEnd) {
            IPostAction postAction = this.actions[this.actionsStart++];
            if (POST_ACTION_VERBOSE) {
                System.out.println("(" + Thread.currentThread() + ") [RubyModelOperation.runPostActions()] Running action " + postAction.getID()); //$NON-NLS-1$ //$NON-NLS-2$
            }
            postAction.run();
        }
    }
    /*
     * Registers the given attribute at the given key with the top level operation.
     */
    protected void setAttribute(Object key, Object attribute) {
        RubyModelOperation topLevelOp = this.getCurrentOperationStack().get(0);
        if (topLevelOp.attributes == null) {
            topLevelOp.attributes = new HashMap<Object, Object>();
        }
        topLevelOp.attributes.put(key, attribute);
    }
    /**
     * @see IProgressMonitor
     */
    public void setCanceled(boolean b) {
        if (progressMonitor != null) {
            progressMonitor.setCanceled(b);
        }
    }
    /**
     * Sets whether this operation is nested or not.
     * @see CreateElementInCUOperation#checkCanceled
     */
    protected void setNested(boolean nested) {
        isNested = nested;
    }
    /**
     * @see IProgressMonitor
     */
    public void setTaskName(String name) {
        if (progressMonitor != null) {
            progressMonitor.setTaskName(name);
        }
    }
    /**
     * @see IProgressMonitor
     */
    public void subTask(String name) {
        if (progressMonitor != null) {
            progressMonitor.subTask(name);
        }
    }
    /**
     * Returns a status indicating if there is any known reason
     * this operation will fail.  Operations are verified before they
     * are run.
     *
     * Subclasses must override if they have any conditions to verify
     * before this operation executes.
     *
     * @see IRubyModelStatus
     */
    protected IRubyModelStatus verify() {
        return commonVerify();
    }
    
    /**
     * @see IProgressMonitor
     */
    public void worked(int work) {
        if (progressMonitor != null) {
            progressMonitor.worked(work);
            checkCanceled();
        }
    }
}
