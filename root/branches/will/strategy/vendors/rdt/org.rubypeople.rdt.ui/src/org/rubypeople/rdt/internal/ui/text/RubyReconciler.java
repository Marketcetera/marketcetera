/*
 * Created on Jan 13, 2005
 *
 */
package org.rubypeople.rdt.internal.ui.text;

import org.eclipse.core.resources.IMarkerDelta;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.reconciler.DirtyRegion;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.texteditor.ITextEditor;
import org.rubypeople.rdt.core.RubyCore;
import org.rubypeople.rdt.internal.ui.rubyeditor.RubyEditor;

/**
 * @author cawilliams
 * 
 */
public class RubyReconciler extends NotifyingReconciler {

    /**
     * Internal resource change listener.
     * 
     * @since 3.0
     */
    class ResourceChangeListener implements IResourceChangeListener {

        private IResource getResource() {
            if (fTextEditor == null) return null;
            IEditorInput input = fTextEditor.getEditorInput();
            if (input instanceof IFileEditorInput) {
                IFileEditorInput fileInput = (IFileEditorInput) input;
                return fileInput.getFile();
            }
            return null;
        }

        /*
         * @see IResourceChangeListener#resourceChanged(org.eclipse.core.resources.IResourceChangeEvent)
         */
        public void resourceChanged(IResourceChangeEvent e) {
            IResourceDelta delta = e.getDelta();
            IResource resource = getResource();
            if (delta != null && resource != null) {
                IResourceDelta child = delta.findMember(resource.getFullPath());
                if (child != null) {
                    IMarkerDelta[] deltas = child.getMarkerDeltas();
                    if (deltas.length > 0) forceReconciling();
                }
            }
        }
    }

    /**
     * The mutex that keeps us from running multiple reconcilers on one editor.
     * TODO remove once we have ensured that there is only one reconciler per
     * editor.
     */
    private Object fMutex;
    private boolean fIninitalProcessDone = false;

    private ResourceChangeListener fResourceChangeListener;
    private ITextEditor fTextEditor;

    /**
     * @param strategy
     * @param isIncremental
     */
    public RubyReconciler(ITextEditor editor, RubyCompositeReconcilingStrategy strategy, boolean isIncremental) {
        super(strategy, isIncremental);
        this.fTextEditor = editor;

        if (editor instanceof RubyEditor)
            fMutex = ((RubyEditor) editor).getReconcilerLock();
        else
            fMutex = new Object(); // Null Object
    }

    /*
     * @see org.eclipse.jface.text.reconciler.MonoReconciler#process(org.eclipse.jface.text.reconciler.DirtyRegion)
     */
    protected void process(DirtyRegion dirtyRegion) {
        // TODO remove once we have ensured that there is only one reconciler
        // per editor.
        synchronized (fMutex) {
            super.process(dirtyRegion);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.text.reconciler.AbstractReconciler#install(org.eclipse.jface.text.ITextViewer)
     */
    public void install(ITextViewer textViewer) {
        super.install(textViewer);

        fResourceChangeListener = new ResourceChangeListener();
        IWorkspace workspace = RubyCore.getWorkspace();
        workspace.addResourceChangeListener(fResourceChangeListener);
    }

    /*
     * @see org.eclipse.jface.text.reconciler.AbstractReconciler#forceReconciling()
     */
    protected void forceReconciling() {
        if (!fIninitalProcessDone) return;

        super.forceReconciling();
        RubyCompositeReconcilingStrategy strategy= (RubyCompositeReconcilingStrategy) getReconcilingStrategy(IDocument.DEFAULT_CONTENT_TYPE);
		strategy.notifyListeners(false);
    }

    /*
     * @see org.eclipse.jface.text.reconciler.MonoReconciler#initialProcess()
     */
    protected void initialProcess() {
        // TODO remove once we have ensured that there is only one reconciler
        // per editor.
        synchronized (fMutex) {
            super.initialProcess();
        }
        fIninitalProcessDone = true;
    }
    
    @Override
    protected void reconcilerReset() {
    	super.reconcilerReset();
    	RubyCompositeReconcilingStrategy strategy= (RubyCompositeReconcilingStrategy) getReconcilingStrategy(IDocument.DEFAULT_CONTENT_TYPE);
		strategy.notifyListeners(true);
    }
    
    @Override
    protected void aboutToBeReconciled() {
    	RubyCompositeReconcilingStrategy strategy= (RubyCompositeReconcilingStrategy) getReconcilingStrategy(IDocument.DEFAULT_CONTENT_TYPE);
		strategy.aboutToBeReconciled();
    }

}
