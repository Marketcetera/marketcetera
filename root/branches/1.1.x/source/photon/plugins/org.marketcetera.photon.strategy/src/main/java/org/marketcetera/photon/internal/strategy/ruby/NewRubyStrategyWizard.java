package org.marketcetera.photon.internal.strategy.ruby;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.wizards.newresource.BasicNewFileResourceWizard;
import org.marketcetera.photon.internal.strategy.Activator;
import org.marketcetera.photon.internal.strategy.Messages;
import org.marketcetera.photon.strategy.StrategyUIConstants;
import org.marketcetera.util.except.ExceptUtils;
import org.marketcetera.util.misc.ClassVersion;
import org.rubypeople.rdt.core.util.Util;

/* $License$ */

/**
 * Wizard to create a new Ruby strategy script from a template.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$")
public class NewRubyStrategyWizard extends Wizard implements INewWizard {

	private static final String SCRIPT_EXTENSION = "." + StrategyUIConstants.RUBY_SCRIPT_EXTENSION; //$NON-NLS-1$

	private NewRubyStrategyWizardPage mPage;
	private ISelection mSelection;

	/**
	 * Constructor.
	 */
	public NewRubyStrategyWizard() {
		setNeedsProgressMonitor(true);
	}

	@Override
	public void addPages() {
		mPage = new NewRubyStrategyWizardPage(mSelection);
		addPage(mPage);
	}

	@Override
	public boolean performFinish() {
		final String containerName = mPage.getContainerName();
		final String className = mPage.getClassName();
		IRunnableWithProgress op = new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor)
					throws InvocationTargetException {
				try {
					doFinish(containerName, className, monitor);
				} catch (CoreException e) {
					throw new InvocationTargetException(e);
				} finally {
					monitor.done();
				}
			}
		};
		try {
			getContainer().run(true, false, op);
		} catch (InterruptedException e) {
			// Intentionally not restoring the interrupt status since this is
			// the main UI thread where it will be ignored
			Messages.NEW_RUBY_STRATEGY_CREATION_FAILED.error(this, e, className, containerName);
			return false;
		} catch (InvocationTargetException e) {
			Throwable realException = e.getTargetException();
			String message = realException.getLocalizedMessage();
			if (message == null) {
				message = Messages.NEW_RUBY_STRATEGY_GENERIC_EXCEPTION_MESSAGE
						.getText();
			}
			MessageDialog.openError(getShell(),
					Messages.NEW_RUBY_STRATEGY_ERROR_DIALOG_TITLE.getText(),
					message);
			Messages.NEW_RUBY_STRATEGY_CREATION_FAILED.error(this, realException, className, containerName);
			return false;
		}
		return true;
	}

	/**
	 * The worker method. It will find the container, create the file if missing
	 * or just replace its contents, and open the editor on the newly created
	 * file.
	 */
	private void doFinish(String containerName, String className,
			IProgressMonitor monitor) throws CoreException {
		String fileName = getRubyScriptName(className);
		monitor.beginTask(Messages.NEW_RUBY_STRATEGY_CREATING_FILE
				.getText(fileName), 2);
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IResource resource = root.findMember(new Path(containerName));
		if (!resource.exists() || !(resource instanceof IContainer)) {
			throwCoreException(Messages.NEW_RUBY_STRATEGY_MISSING_CONTAINER
					.getText(containerName));
		}
		IContainer container = (IContainer) resource;
		final IFile file = container.getFile(new Path(fileName));
		InputStream stream = openContentStream(className);
		try {
			if (file.exists()) {
				throwCoreException(Messages.NEW_RUBY_STRATEGY_FILE_EXISTS
						.getText(fileName));
			} else {
				file.create(stream, true, monitor);
			}
		} finally {
			try {
				stream.close();
			} catch (IOException e) {
				ExceptUtils.swallow(e);
			}
		}
		monitor.worked(1);
		monitor.setTaskName(Messages.NEW_RUBY_STRATEGY_OPENING_FILE.getText());
		getShell().getDisplay().asyncExec(new Runnable() {
			public void run() {
				final IWorkbenchPage window = PlatformUI.getWorkbench()
						.getActiveWorkbenchWindow().getActivePage();
				IWorkbenchPage page = window;
				try {
					IDE.openEditor(page, file, true);
					BasicNewFileResourceWizard.selectAndReveal(file, PlatformUI
							.getWorkbench().getActiveWorkbenchWindow());
				} catch (PartInitException e) {
					ExceptUtils.swallow(e);
				}
			}
		});
		monitor.worked(1);
	}

	private String getRubyScriptName(String typeName) {
		int index = typeName.lastIndexOf(NewRubyStrategyWizardPage.NAMESPACE_DELIMITER);
		// TODO: If they have set up namespace, should we offer to build nested
		// folders? A::B::C -> a/b/c.rb
		if (index != -1) {
			typeName = typeName.substring(index + 2);
		}
		return Util.camelCaseToUnderscores(typeName) + SCRIPT_EXTENSION;
	}

	private InputStream openContentStream(String className) {
		return new RubyStrategyTemplate().createNewScript(className);
	}

	private void throwCoreException(String message) throws CoreException {
		IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID,
				IStatus.OK, message, null);
		throw new CoreException(status);
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.mSelection = selection;
	}
}