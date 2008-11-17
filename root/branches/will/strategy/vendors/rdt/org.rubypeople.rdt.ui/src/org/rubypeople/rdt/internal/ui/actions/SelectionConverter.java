package org.rubypeople.rdt.internal.ui.actions;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.AbstractTextEditor;
import org.rubypeople.rdt.core.ICodeAssist;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IRubyScript;
import org.rubypeople.rdt.core.ISourceRange;
import org.rubypeople.rdt.core.ISourceReference;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.internal.corext.util.RubyModelUtil;
import org.rubypeople.rdt.internal.ui.RubyPlugin;
import org.rubypeople.rdt.internal.ui.rubyeditor.EditorUtility;
import org.rubypeople.rdt.internal.ui.rubyeditor.IRubyScriptEditorInput;
import org.rubypeople.rdt.internal.ui.rubyeditor.RubyEditor;
import org.rubypeople.rdt.ui.IWorkingCopyManager;

public class SelectionConverter {
	
	/**
	 * Converts the selection provided by the given part into a structured selection.
	 * The following conversion rules are used:
	 * <ul>
	 *	<li><code>part instanceof RubyEditor</code>: returns a structured selection
	 * 	using code resolve to convert the editor's text selection.</li>
	 * <li><code>part instanceof IWorkbenchPart</code>: returns the part's selection
	 * 	if it is a structured selection.</li>
	 * <li><code>default</code>: returns an empty structured selection.</li>
	 * </ul>
	 */
	public static IStructuredSelection getStructuredSelection(IWorkbenchPart part) throws RubyModelException {
		if (part instanceof RubyEditor)
			return new StructuredSelection(codeResolve((RubyEditor)part));
		ISelectionProvider provider= part.getSite().getSelectionProvider();
		if (provider != null) {
			ISelection selection= provider.getSelection();
			if (selection instanceof IStructuredSelection)
				return (IStructuredSelection)selection;
		}
		return StructuredSelection.EMPTY;
	}
	
	public static IRubyElement getElementAtOffset(RubyEditor editor) throws RubyModelException {
		return getElementAtOffset(editor, true);
	}
	
	/**
	 * @param primaryOnly if <code>true</code> only primary working copies will be returned
	 * @since 3.2
	 */
	private static IRubyElement getElementAtOffset(RubyEditor editor, boolean primaryOnly) throws RubyModelException {
		return getElementAtOffset(getInput(editor, primaryOnly), (ITextSelection)editor.getSelectionProvider().getSelection());
	}
	
	public static IRubyElement getElementAtOffset(IRubyElement input, ITextSelection selection) throws RubyModelException {
		if (input instanceof IRubyScript) {
			IRubyScript cunit= (IRubyScript) input;
			RubyModelUtil.reconcile(cunit);
			IRubyElement ref= cunit.getElementAt(selection.getOffset());
			if (ref == null)
				return input;
			else
				return ref;
		}
		return null;
	}

    public static IRubyScript getInputAsRubyScript(RubyEditor editor) {
        Object editorInput = SelectionConverter.getInput(editor);
        if (editorInput instanceof IRubyScript)
            return (IRubyScript) editorInput;
        else
            return null;
    }
    
    public static IRubyElement getInput(IEditorPart editor) {
    	if (editor == null)
            return null;
        IEditorInput input= editor.getEditorInput();
        if (input instanceof IRubyScriptEditorInput) {
        	IRubyScriptEditorInput scriptEditor = (IRubyScriptEditorInput) input;
        	return scriptEditor.getRubyScript();
        }
        IWorkingCopyManager manager= RubyPlugin.getDefault().getWorkingCopyManager();               
        return manager.getWorkingCopy(input);           
    }

	public static boolean canOperateOn(IEditorPart editor) {
		if (editor == null)
			return false;
		return getInput(editor) != null;		
	}
	
	private static final IRubyElement[] EMPTY_RESULT= new IRubyElement[0];

	/**
	 * Converts the text selection provided by the given editor a Ruby element by
	 * asking the user if code reolve returned more than one result. If the selection 
	 * doesn't cover a Ruby element <code>null</code> is returned.
	 */
	public static IRubyElement codeResolve(AbstractTextEditor editor, Shell shell, String title, String message) throws RubyModelException {
		IRubyElement[] elements= codeResolve(editor);
		if (elements == null || elements.length == 0)
			return null;
		IRubyElement candidate= elements[0];
		if (elements.length > 1) {
			candidate= OpenActionUtil.selectRubyElement(elements, shell, title, message);
		}
		return candidate;
	}
	
	public static IRubyElement[] codeResolve(AbstractTextEditor editor) throws RubyModelException {
		return codeResolve(getInput(editor), (ITextSelection)editor.getSelectionProvider().getSelection());
	}
	
	public static IRubyElement[] codeResolve(IRubyElement input, ITextSelection selection) throws RubyModelException {
		return codeResolve(input, selection.getOffset(), selection.getLength());
	}
	
	public static IRubyElement[] codeResolve(IRubyElement input, int offset, int length) throws RubyModelException {
		if (input instanceof ICodeAssist) {
			if (input instanceof IRubyScript) {
				RubyModelUtil.reconcile((IRubyScript) input);
			}
			IRubyElement[] elements= ((ICodeAssist)input).codeSelect(offset, length);
			if (elements != null && elements.length > 0)
				return elements;
		}
		return EMPTY_RESULT;
	}

	/**
	 * Perform a code resolve in a separate thread.
	 * @param primaryOnly if <code>true</code> only primary working copies will be returned
	 * @throws InterruptedException 
	 * @throws InvocationTargetException 
	 * @since 1.0
	 */
	public static IRubyElement[] codeResolveForked(RubyEditor editor, boolean primaryOnly) throws InvocationTargetException, InterruptedException {
		return performForkedCodeResolve(getInput(editor, primaryOnly), (ITextSelection)editor.getSelectionProvider().getSelection());
	}
	
	/**
	 * @param primaryOnly if <code>true</code> only primary working copies will be returned
	 * @since 1.0
	 */
	private static IRubyElement getInput(RubyEditor editor, boolean primaryOnly) {
		if (editor == null)
			return null;
		return EditorUtility.getEditorInputRubyElement(editor, primaryOnly);
	}
	
	private static IRubyElement[] performForkedCodeResolve(final IRubyElement input, final ITextSelection selection) throws InvocationTargetException, InterruptedException {
		final class CodeResolveRunnable implements IRunnableWithProgress {
			IRubyElement[] result;
			public void run(IProgressMonitor monitor) throws InvocationTargetException {
				try {
					result= codeResolve(input, selection);
				} catch (RubyModelException e) {
					throw new InvocationTargetException(e);
				}
			}
		}
		CodeResolveRunnable runnable= new CodeResolveRunnable();
		PlatformUI.getWorkbench().getProgressService().busyCursorWhile(runnable);
		return runnable.result;
	}

	public static IRubyElement[] codeResolveOrInputForked(RubyEditor editor) throws InvocationTargetException, InterruptedException {
		IRubyElement input= getInput(editor);
		ITextSelection selection= (ITextSelection)editor.getSelectionProvider().getSelection();
		IRubyElement[] result= performForkedCodeResolve(input, selection);
		if (result.length == 0) {
			result= new IRubyElement[] {input};
		}
		return result;
	}

	public static IRubyElement resolveEnclosingElement(RubyEditor editor, ITextSelection selection) throws RubyModelException {
		return resolveEnclosingElement(getInput(editor), selection);
	}
		
	public static IRubyElement resolveEnclosingElement(IRubyElement input, ITextSelection selection) throws RubyModelException {
		IRubyElement atOffset= null;
			if (input instanceof IRubyScript) {
				IRubyScript cunit= (IRubyScript)input;
				RubyModelUtil.reconcile(cunit);
				atOffset= cunit.getElementAt(selection.getOffset());
			} else {
				return null;
			}
			if (atOffset == null) {
				return input;
			} else {
				int selectionEnd= selection.getOffset() + selection.getLength();
				IRubyElement result= atOffset;
				if (atOffset instanceof ISourceReference) {
					ISourceRange range= ((ISourceReference)atOffset).getSourceRange();
					while (range.getOffset() + range.getLength() < selectionEnd) {
						result= result.getParent();
						if (! (result instanceof ISourceReference)) {
							result= input;
							break;
						}
						range= ((ISourceReference)result).getSourceRange();
					}
				}
				return result;
			}
		}

}
