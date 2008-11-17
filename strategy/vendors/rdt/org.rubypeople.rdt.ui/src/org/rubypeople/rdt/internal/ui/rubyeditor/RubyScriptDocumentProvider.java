package org.rubypeople.rdt.internal.ui.rubyeditor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.filebuffers.FileBuffers;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ISynchronizable;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.editors.text.FileDocumentProvider;
import org.rubypeople.rdt.core.ElementChangedEvent;
import org.rubypeople.rdt.core.IElementChangedListener;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IRubyElementDelta;
import org.rubypeople.rdt.core.IRubyProject;
import org.rubypeople.rdt.core.IRubyScript;
import org.rubypeople.rdt.core.ISourceFolderRoot;
import org.rubypeople.rdt.core.RubyCore;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.internal.ui.RubyPlugin;
import org.rubypeople.rdt.internal.ui.text.IRubyPartitions;
import org.rubypeople.rdt.ui.text.RubyTextTools;

public class RubyScriptDocumentProvider extends FileDocumentProvider {
	/**
	 * An input change listener to request the editor to reread the input.
	 */
	public interface InputChangeListener {
		void inputChanged(IRubyScriptEditorInput input);
	}

	/**
	 * Synchronizes the document with external resource changes.
	 */
	protected class RubyScriptSynchronizer implements IElementChangedListener {

		protected IRubyScriptEditorInput fInput;
		protected ISourceFolderRoot fSourceFolderRoot;

		/**
		 * Default constructor.
		 */
		public RubyScriptSynchronizer(IRubyScriptEditorInput input) {

			fInput= input;

			IRubyElement parent= fInput.getRubyScript().getParent();
			while (parent != null && !(parent instanceof ISourceFolderRoot)) {
				parent= parent.getParent();
			}
			fSourceFolderRoot= (ISourceFolderRoot) parent;
		}

		/**
		 * Installs the synchronizer.
		 */
		public void install() {
			RubyCore.addElementChangedListener(this);
		}

		/**
		 * Uninstalls the synchronizer.
		 */
		public void uninstall() {
			RubyCore.removeElementChangedListener(this);
		}

		/*
		 * @see IElementChangedListener#elementChanged
		 */
		public void elementChanged(ElementChangedEvent e) {
			check(fSourceFolderRoot, e.getDelta());
		}

		/**
		 * Recursively check whether the class file has been deleted.
		 * Returns true if delta processing can be stopped.
		 */
		protected boolean check(ISourceFolderRoot input, IRubyElementDelta delta) {
			IRubyElement element= delta.getElement();

			if ((delta.getKind() & IRubyElementDelta.REMOVED) != 0 || (delta.getFlags() & IRubyElementDelta.F_CLOSED) != 0) {
				// http://dev.eclipse.org/bugs/show_bug.cgi?id=19023
				if (element.equals(input.getRubyProject()) || element.equals(input)) {
					handleDeleted(fInput);
					return true;
				}
			}

			if (((delta.getFlags() & IRubyElementDelta.F_ARCHIVE_CONTENT_CHANGED) != 0) && input.equals(element)) {
				handleDeleted(fInput);
				return true;
			}

			if (((delta.getFlags() & IRubyElementDelta.F_REMOVED_FROM_CLASSPATH) != 0) && input.equals(element)) {
				handleDeleted(fInput);
				return true;
			}

			IRubyElementDelta[] subdeltas= delta.getAffectedChildren();
			for (int i= 0; i < subdeltas.length; i++) {
				if (check(input, subdeltas[i]))
					return true;
			}

			if ((delta.getFlags() & IRubyElementDelta.F_SOURCEDETACHED) != 0 ||
				(delta.getFlags() & IRubyElementDelta.F_SOURCEATTACHED) != 0)
			{
				IRubyScript file= fInput != null ? fInput.getRubyScript() : null;
				IRubyProject project= input != null ? input.getRubyProject() : null;

				boolean isOnClasspath= false;
				if (file != null && project != null)
					isOnClasspath= project.isOnLoadpath(file);

				if (isOnClasspath) {
					fireInputChanged(fInput);
					return false;
				} else {
					handleDeleted(fInput);
					return true;
				}
			}

			return false;
		}
	}

	/**
	 * Correcting the visibility of <code>FileSynchronizer</code>.
	 */
	protected class _FileSynchronizer extends FileSynchronizer {
		public _FileSynchronizer(IFileEditorInput fileEditorInput) {
			super(fileEditorInput);
		}
	}

	/**
	 * Bundle of all required informations.
	 */
	protected class RubyScriptInfo extends FileInfo {

		RubyScriptSynchronizer fRubyScriptSynchronizer= null;

		RubyScriptInfo(IDocument document, IAnnotationModel model, _FileSynchronizer fileSynchronizer) {
			super(document, model, fileSynchronizer);
		}

		RubyScriptInfo(IDocument document, IAnnotationModel model, RubyScriptSynchronizer classFileSynchronizer) {
			super(document, model, null);
			fRubyScriptSynchronizer= classFileSynchronizer;
		}
	}

	/** Input change listeners. */
	private List fInputListeners= new ArrayList();

	/**
	 * Creates a new document provider.
	 */
	public RubyScriptDocumentProvider() {
		super();
	}

	/*
	 * @see StorageDocumentProvider#setDocumentContent(IDocument, IEditorInput)
	 */
	protected boolean setDocumentContent(IDocument document, IEditorInput editorInput, String encoding) throws CoreException {
		if (editorInput instanceof IRubyScriptEditorInput) {
			IRubyScript rubyScript= ((IRubyScriptEditorInput) editorInput).getRubyScript();
			document.set(rubyScript.getSource());
			return true;
		}
		return super.setDocumentContent(document, editorInput, encoding);
	}

	/**
	 * Creates an annotation model derived from the given class file editor input.
	 * 
	 * @param rubyScriptEditorInput the editor input from which to query the annotations
	 * @return the created annotation model
	 * @exception CoreException if the editor input could not be accessed
	 */
	protected IAnnotationModel createRubyScriptAnnotationModel(IRubyScriptEditorInput rubyScriptEditorInput) throws CoreException {
		IRubyScript script = rubyScriptEditorInput.getRubyScript();
		ExternalFileRubyAnnotationModel model = new ExternalFileRubyAnnotationModel(script);
		return model;
	}

	/*
	 * @see org.eclipse.ui.editors.text.StorageDocumentProvider#createEmptyDocument()
	 * @since 3.1
	 */
	protected IDocument createEmptyDocument() {
		IDocument document= FileBuffers.getTextFileBufferManager().createEmptyDocument(null);
		if (document instanceof ISynchronizable)
			((ISynchronizable)document).setLockObject(new Object());		
		return document;
	}

	/*
	 * @see AbstractDocumentProvider#createDocument(Object)
	 */
	protected IDocument createDocument(Object element) throws CoreException {
		IDocument document= super.createDocument(element);
		if (document != null) {
			RubyTextTools tools= RubyPlugin.getDefault().getRubyTextTools();
			tools.setupRubyDocumentPartitioner(document, IRubyPartitions.RUBY_PARTITIONING);
		}
		return document;
	}

	/*
	 * @see AbstractDocumentProvider#createElementInfo(Object)
	 */
	protected ElementInfo createElementInfo(Object element) throws CoreException {

		if (element instanceof IRubyScriptEditorInput) {

			IRubyScriptEditorInput input = (IRubyScriptEditorInput) element;
//			ExternalRubyScriptEditorInput external= null;
//			if (input instanceof ExternalRubyScriptEditorInput)
//				external= (ExternalRubyScriptEditorInput) input;
//
//			if (external != null) {
//				try {
//					refreshFile(external.getFile());
//				} catch (CoreException x) {
//					handleCoreException(x, JavaEditorMessages.RubyScriptDocumentProvider_error_createElementInfo);
//				}
//			}

			IDocument d= createDocument(input);
			IAnnotationModel m= createRubyScriptAnnotationModel(input);

//			if (external != null) {
//				RubyScriptInfo info= new RubyScriptInfo(d, m,  (_FileSynchronizer) null);
//				info.fModificationStamp= computeModificationStamp(external.getFile());
//				info.fEncoding= getPersistedEncoding(element);
//				return info;
//			} else 
			if (input instanceof RubyScriptEditorInput) {
				RubyScriptSynchronizer s= new RubyScriptSynchronizer(input);
				s.install();
				RubyScriptInfo info= new RubyScriptInfo(d, m, s);
				info.fEncoding= getPersistedEncoding(element);
				return info;
			}
		}

		return null;
	}

	/*
	 * @see FileDocumentProvider#disposeElementInfo(Object, ElementInfo)
	 */
	protected void disposeElementInfo(Object element, ElementInfo info) {
		RubyScriptInfo classFileInfo= (RubyScriptInfo) info;
		if (classFileInfo.fRubyScriptSynchronizer != null) {
			classFileInfo.fRubyScriptSynchronizer.uninstall();
			classFileInfo.fRubyScriptSynchronizer= null;
		}

		super.disposeElementInfo(element, info);
	}

	/*
	 * @see AbstractDocumentProvider#doSaveDocument(IProgressMonitor, Object, IDocument)
	 */
	protected void doSaveDocument(IProgressMonitor monitor, Object element, IDocument document) throws CoreException {
	}


	/*
	 * @see org.eclipse.ui.texteditor.IDocumentProviderExtension3#isSynchronized(java.lang.Object)
	 * @since 3.0
	 */
	public boolean isSynchronized(Object element) {
		Object elementInfo= getElementInfo(element);
		if (elementInfo instanceof RubyScriptInfo) {
			IRubyScriptEditorInput input= (IRubyScriptEditorInput)element;
			IResource resource;
			try {
				resource= input.getRubyScript().getUnderlyingResource();
			} catch (RubyModelException e) {
				return true;
			}
			return resource == null || resource.isSynchronized(IResource.DEPTH_ZERO);
		}
		return false;
	}

	/**
	 * Handles the deletion of the element underlying the given class file editor input.
	 * @param input the editor input
	 */
	protected void handleDeleted(IRubyScriptEditorInput input) {
		fireElementDeleted(input);
	}

	/**
	 * Fires input changes to input change listeners.
	 */
	protected void fireInputChanged(IRubyScriptEditorInput input) {
		List list= new ArrayList(fInputListeners);
		for (Iterator i = list.iterator(); i.hasNext();)
			((InputChangeListener) i.next()).inputChanged(input);
	}

	/**
	 * Adds an input change listener.
	 */
	public void addInputChangeListener(InputChangeListener listener) {
		fInputListeners.add(listener);
	}

	/**
	 * Removes an input change listener.
	 */
	public void removeInputChangeListener(InputChangeListener listener) {
		fInputListeners.remove(listener);
	}
}
