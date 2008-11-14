package org.marketcetera.photon.internal.strategy.ruby;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Handler that runs the {@link RegisterRubyStrategyWizard}.
 *
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public class RegisterRubyHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ISelection selection = HandlerUtil.getCurrentSelectionChecked(event);
		Shell shell = HandlerUtil.getActiveShellChecked(event);
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection sselection = (IStructuredSelection) selection;
			if (sselection.size() == 1) {
				Object element = sselection.getFirstElement();
				if (element instanceof IFile) {
					IFile file = (IFile) element;
					if (file.getFileExtension().equals("rb")) { //$NON-NLS-1$
						WizardDialog dialog = new WizardDialog(shell,
								new RegisterRubyStrategyWizard(file));
						dialog.create();
						dialog.open();
					}
				}
			}
		}
		return null;
	}

}
