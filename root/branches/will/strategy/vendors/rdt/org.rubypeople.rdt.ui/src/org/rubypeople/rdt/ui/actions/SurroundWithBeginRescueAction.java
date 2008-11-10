package org.rubypeople.rdt.ui.actions;

import java.util.Map;

import org.eclipse.core.filebuffers.FileBuffers;
import org.eclipse.core.filebuffers.ITextFileBufferManager;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.ui.PlatformUI;
import org.rubypeople.rdt.core.IRubyScript;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.core.formatter.Indents;
import org.rubypeople.rdt.internal.corext.util.RubyModelUtil;
import org.rubypeople.rdt.internal.ui.IRubyHelpContextIds;
import org.rubypeople.rdt.internal.ui.RubyPlugin;
import org.rubypeople.rdt.internal.ui.actions.ActionMessages;
import org.rubypeople.rdt.internal.ui.actions.SelectionConverter;
import org.rubypeople.rdt.internal.ui.rubyeditor.RubyEditor;
import org.rubypeople.rdt.internal.ui.util.ExceptionHandler;

public class SurroundWithBeginRescueAction extends SelectionDispatchAction {

    public static final String SURROUND_WTH_BEGIN_RESCUE = "SurroundWithBeginRescue";  //$NON-NLS-1$
    private RubyEditor fEditor;

    /**
     * Note: This constructor is for internal use only. Clients should not call
     * this constructor.
     * 
     * @param editor
     *            the compilation unit editor
     */
    public SurroundWithBeginRescueAction(RubyEditor editor) {
        super(editor.getEditorSite());

        setText(ActionMessages.SurroundWithBeginRescueAction_label);
        fEditor = editor;
        setEnabled((fEditor != null && SelectionConverter.getInputAsRubyScript(fEditor) != null));
        PlatformUI.getWorkbench().getHelpSystem().setHelp(this,
                IRubyHelpContextIds.SURROUND_WITH_TRY_CATCH_ACTION);
    }

    public void run(ITextSelection selection) {
        try {
            createChange(selection, new NullProgressMonitor());
        } catch (CoreException e) {
            ExceptionHandler.handle(e, getDialogTitle(),
            		ActionMessages.SurroundWithBeginRescueAction_error);
        }
    }

    private static String getDialogTitle() {
    	return ActionMessages.SurroundWithBeginRescueAction_dialog_title;
    }

    private IFile getFile() {
        IRubyScript cu = getRubyScript();
        return (IFile) RubyModelUtil.toOriginal(cu).getResource();
    }

    private IRubyScript getRubyScript() {
        return SelectionConverter.getInputAsRubyScript(fEditor);
    }

    /*
     * non Java-doc
     * 
     * @see IRefactoring#createChange(IProgressMonitor)
     */
    public void createChange(ITextSelection selection, IProgressMonitor pm) throws CoreException {
        final String NN = ""; //$NON-NLS-1$
        if (pm == null) pm = new NullProgressMonitor();
        pm.beginTask(NN, 2);
        // This is cheap since the compilation unit is already open in a editor.
        IPath path = getFile().getFullPath();
        ITextFileBufferManager bufferManager = FileBuffers.getTextFileBufferManager();
        try {
            bufferManager.connect(path, new SubProgressMonitor(pm, 1));
            IDocument document = bufferManager.getTextFileBuffer(path).getDocument();

            String text = createBeginRescueBlock(document, selection);
            document.replace(selection.getOffset(), selection.getLength(), text);
        } catch (BadLocationException e) {
            throw new CoreException(new Status(IStatus.ERROR, RubyPlugin.getPluginId(),
                    IStatus.ERROR, e.getMessage(), e));
        } finally {
            bufferManager.disconnect(path, new SubProgressMonitor(pm, 1));
            pm.done();
        }
    }

    private String createBeginRescueBlock(IDocument document, ITextSelection selection)
            throws BadLocationException, RubyModelException {
        String originalText = selection.getText();
        String lineDelimiter = document.getLineDelimiter(0);

        Map options = getRubyScript().getRubyProject().getOptions(true);
        int lineNumber = selection.getStartLine();
        String line = document.get(document.getLineOffset(lineNumber), document
                .getLineLength(lineNumber));

        int indentationUnits = Indents.measureIndentUnits(line, Indents.getTabWidth(options),
                Indents.getIndentWidth(options));

        StringBuffer text = new StringBuffer();
        text.append("begin");//$NON-NLS-1$
        text.append(lineDelimiter);
        text.append(Indents.createIndentString(indentationUnits + 1, options));
        text.append(originalText);
        text.append(lineDelimiter);
        text.append(Indents.createIndentString(indentationUnits, options));
        text.append("rescue StandardError => e");//$NON-NLS-1$
        text.append(lineDelimiter);
        text.append(Indents.createIndentString(indentationUnits + 1, options));
        text.append("puts e");//$NON-NLS-1$
        text.append(lineDelimiter);
        text.append(Indents.createIndentString(indentationUnits, options));
        text.append("end");//$NON-NLS-1$
        text.append(lineDelimiter);
        text.append(Indents.createIndentString(indentationUnits, options));
        return text.toString();
    }

    public void selectionChanged(ITextSelection selection) {
        setEnabled(selection.getLength() > 0
                && (fEditor != null && SelectionConverter.getInputAsRubyScript(fEditor) != null));
    }

}
