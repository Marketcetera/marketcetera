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
import org.marketcetera.photon.strategy.StrategyUIConstants;
import org.marketcetera.util.misc.ClassVersion;
import org.springframework.util.Assert;

/* $License$ */

/**
 * Handler that runs the {@link RegisterRubyStrategyWizard}. This handler should
 * only be used when the default selection consists of exactly on {@link IFile}
 * that has a file extension equal to {@link StrategyUIConstants#RUBY_SCRIPT_EXTENSION}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$")
public class RegisterRubyHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ISelection selection = HandlerUtil.getCurrentSelectionChecked(event);
		Shell shell = HandlerUtil.getActiveShellChecked(event);
		IStructuredSelection sselection = (IStructuredSelection) selection;
		Assert.isTrue(sselection.size() == 1);
		Object element = sselection.getFirstElement();
		IFile file = (IFile) element;
		Assert.isTrue(file.getFileExtension().equals(
				StrategyUIConstants.RUBY_SCRIPT_EXTENSION));
		WizardDialog dialog = new WizardDialog(shell,
				new RegisterRubyStrategyWizard(file));
		dialog.create();
		dialog.open();
		return null;
	}

}
