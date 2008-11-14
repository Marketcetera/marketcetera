package org.rubypeople.rdt.internal.ui.actions;

import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.internal.ui.rubyeditor.EditorUtility;
import org.rubypeople.rdt.ui.RubyElementLabelProvider;

public class OpenActionUtil {
	
	/**
	 * Opens the editor on the given element and subsequently selects it.
	 */
	public static void open(Object element, boolean activate) throws RubyModelException, PartInitException {
		IEditorPart part= EditorUtility.openInEditor(element, activate);
		if (element instanceof IRubyElement)
			EditorUtility.revealInEditor(part, (IRubyElement)element);
	}

	/**
	 * Shows a dialog for resolving an ambiguous ruby element.
	 * Utility method that can be called by subclasses.
	 */
	public static IRubyElement selectRubyElement(IRubyElement[] elements, Shell shell, String title, String message) {
		
		int nResults= elements.length;
		
		if (nResults == 0)
			return null;
		
		if (nResults == 1)
			return elements[0];
		
		int flags= RubyElementLabelProvider.SHOW_DEFAULT
						| RubyElementLabelProvider.SHOW_POST_QUALIFIED
						| RubyElementLabelProvider.SHOW_ROOT;
						
		ElementListSelectionDialog dialog= new ElementListSelectionDialog(shell, new RubyElementLabelProvider(flags));
		dialog.setTitle(title);
		dialog.setMessage(message);
		dialog.setElements(elements);
		
		if (dialog.open() == Window.OK) {
			Object[] selection= dialog.getResult();
			if (selection != null && selection.length > 0) {
				nResults= selection.length;
				for (int i= 0; i < nResults; i++) {
					Object current= selection[i];
					if (current instanceof IRubyElement)
						return (IRubyElement) current;
				}
			}
		}		
		return null;
	}

}
