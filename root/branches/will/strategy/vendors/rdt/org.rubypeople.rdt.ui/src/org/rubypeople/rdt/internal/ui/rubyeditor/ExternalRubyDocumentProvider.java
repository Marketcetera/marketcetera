package org.rubypeople.rdt.internal.ui.rubyeditor;

import java.io.BufferedReader;
import java.io.FileReader;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.ui.internal.editors.text.WorkspaceOperationRunner;
import org.eclipse.ui.texteditor.AbstractDocumentProvider;
import org.rubypeople.rdt.internal.ui.RubyPlugin;
import org.rubypeople.rdt.internal.ui.RubyUIMessages;
import org.rubypeople.rdt.internal.ui.text.IRubyPartitions;
import org.rubypeople.rdt.ui.text.RubyTextTools;

public class ExternalRubyDocumentProvider extends AbstractDocumentProvider {
	
	private WorkspaceOperationRunner fOperationRunner;
	public ExternalRubyDocumentProvider() {
		super();
	}

	protected IDocument createDocument(Object element) throws CoreException {
		
		if (!(element instanceof ExternalRubyFileEditorInput)) {
			return null ;
		}
		ExternalRubyFileEditorInput editorInput = (ExternalRubyFileEditorInput) element ;
		StringBuffer fileContent = new StringBuffer() ;
		
		try {
			BufferedReader fr = new BufferedReader(new FileReader(editorInput.getFilesystemFile()));		
			while (fr.ready()) {
				fileContent.append(fr.readLine()) ;
				fileContent.append("\n") ; //$NON-NLS-1$
			}
		} catch (Exception e) {
			String message = RubyUIMessages.getFormattedString("Error while opening/reading file: ", editorInput.getFilesystemFile().getAbsolutePath()) ; //$NON-NLS-1$
			RubyPlugin.log(IStatus.ERROR, message, e) ;
		}
		Document document = new Document() ;
		document.set(fileContent.toString()) ;
        // TODO: check if this should be inherited from RubyDocumentProvider
		if (document != null) {			
			RubyTextTools tools= RubyPlugin.getDefault().getRubyTextTools();
			tools.setupRubyDocumentPartitioner(document, IRubyPartitions.RUBY_PARTITIONING);
		}
		return document;
	}

	protected IAnnotationModel createAnnotationModel(Object element) throws CoreException {
		return new ExternalFileRubyAnnotationModel(null);
	}
	

	protected void doSaveDocument(
		IProgressMonitor monitor,
		Object element,
		IDocument document,
		boolean overwrite)
		throws CoreException {
		// do nothing
	}

	protected IRunnableContext getOperationRunner(IProgressMonitor monitor) {
		if (fOperationRunner == null)
			fOperationRunner = new WorkspaceOperationRunner();
		fOperationRunner.setProgressMonitor(monitor);
		return fOperationRunner;
		
	}

}