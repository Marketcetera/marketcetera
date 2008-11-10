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
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.text.Assert;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.reconciler.DirtyRegion;
import org.eclipse.jface.text.reconciler.IReconcilingStrategy;
import org.eclipse.jface.text.reconciler.IReconcilingStrategyExtension;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;
import org.jruby.ast.RootNode;
import org.rubypeople.rdt.core.IRubyScript;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.internal.ui.RubyPlugin;
import org.rubypeople.rdt.ui.IWorkingCopyManager;
import org.rubypeople.rdt.ui.RubyUI;


public class RubyReconcilingStrategy implements IReconcilingStrategy, IReconcilingStrategyExtension {


    private ITextEditor fEditor;

    private IWorkingCopyManager fManager;
    private IDocumentProvider fDocumentProvider;
    private IProgressMonitor fProgressMonitor;
    private boolean fNotify= true;

    private IRubyReconcilingListener fRubyReconcilingListener;
    private boolean fIsRubyReconcilingListener;


    public RubyReconcilingStrategy(ITextEditor editor) {
        fEditor= editor;
        fManager= RubyPlugin.getDefault().getWorkingCopyManager();
        fDocumentProvider= RubyPlugin.getDefault().getRubyDocumentProvider();
        fIsRubyReconcilingListener= fEditor instanceof IRubyReconcilingListener;
        if (fIsRubyReconcilingListener)
            fRubyReconcilingListener= (IRubyReconcilingListener)fEditor;
    }

    private IProblemRequestorExtension getProblemRequestorExtension() {
        IAnnotationModel model= fDocumentProvider.getAnnotationModel(fEditor.getEditorInput());
        if (model instanceof IProblemRequestorExtension)
            return (IProblemRequestorExtension) model;
        return null;
    }

    private void reconcile(final boolean initialReconcile) {
        final RootNode[] ast= new RootNode[1];
        final IRubyScript unit= fManager.getWorkingCopy(fEditor.getEditorInput());
        try {
            if (unit != null) {
                Platform.run(new ISafeRunnable() {
                    public void run() {
                        try {
                            
                            /* fix for missing cancel flag communication */
                            IProblemRequestorExtension extension= getProblemRequestorExtension();
                            if (extension != null) {
                                extension.setProgressMonitor(fProgressMonitor);
                                extension.setIsActive(true);
                            }
                            
                            try {
                                ast[0] = unit.reconcile(true, null, fProgressMonitor);
                            } catch (OperationCanceledException ex) {
                                Assert.isTrue(fProgressMonitor == null || fProgressMonitor.isCanceled());
                                ast[0]= null;
                            } finally {
                                /* fix for missing cancel flag communication */
                                if (extension != null) {
                                    extension.setProgressMonitor(null);
                                    extension.setIsActive(false);
                                }
                            }
                            
                        } catch (RubyModelException ex) {
                            handleException(ex);
                        }
                    }
                    public void handleException(Throwable ex) {
                        IStatus status= new Status(IStatus.ERROR, RubyUI.ID_PLUGIN, IStatus.OK, "Error in RDT Core during reconcile", ex);  //$NON-NLS-1$
                        RubyPlugin.getDefault().getLog().log(status);
                    }
                });
                
            }
        } finally {
            // Always notify listeners, see https://bugs.eclipse.org/bugs/show_bug.cgi?id=55969 for the final solution
            try {
                if (fIsRubyReconcilingListener) {
                    IProgressMonitor pm= fProgressMonitor;
                    if (pm == null)
                        pm= new NullProgressMonitor();
                    fRubyReconcilingListener.reconciled(unit, ast[0], !fNotify, pm);
                }
            } finally {
                fNotify= true;
            }
        }
    }

    /*
     * @see IReconcilingStrategy#reconcile(IRegion)
     */
    public void reconcile(IRegion partition) {
        reconcile(false);
    }

    /*
     * @see IReconcilingStrategy#reconcile(DirtyRegion, IRegion)
     */
    public void reconcile(DirtyRegion dirtyRegion, IRegion subRegion) {
        reconcile(false);
    }

    /*
     * @see IReconcilingStrategy#setDocument(IDocument)
     */
    public void setDocument(IDocument document) {
    }

    /*
     * @see IReconcilingStrategyExtension#setProgressMonitor(IProgressMonitor)
     */
    public void setProgressMonitor(IProgressMonitor monitor) {
        fProgressMonitor= monitor;
    }

    /*
     * @see IReconcilingStrategyExtension#initialReconcile()
     */
    public void initialReconcile() {
        reconcile(true);
    }

    /**
     * Tells this strategy whether to inform its listeners.
     *
     * @param notify <code>true</code> if listeners should be notified
     */
    public void notifyListeners(boolean notify) {
        fNotify= notify;
    }

    /**
     * Called before reconciling is started.
     *
     * @since 3.0
     */
    public void aboutToBeReconciled() {
        if (fIsRubyReconcilingListener)
            fRubyReconcilingListener.aboutToBeReconciled();
    }
}
