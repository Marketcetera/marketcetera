package org.marketcetera.photon.internal.strategy.ui;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
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
import org.marketcetera.photon.commons.ui.JFaceUtils;
import org.marketcetera.photon.strategy.StrategyUI;
import org.marketcetera.util.except.ExceptUtils;
import org.marketcetera.util.log.I18NBoundMessage2P;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Wizard to create a new strategy script from a template.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public abstract class AbstractNewStrategyWizard extends Wizard implements
        INewWizard {

    private NewStrategyWizardPage mPage;
    private ISelection mSelection;

    /**
     * Constructor.
     */
    public AbstractNewStrategyWizard() {
        setNeedsProgressMonitor(true);
    }

    @Override
    public void addPages() {
        mPage = createPage(mSelection);
        setWindowTitle(mPage.getTitle());
        addPage(mPage);
    }

    /**
     * Hook for subclasses to create the single wizard page.
     * 
     * @return the wizard page
     */
    protected abstract NewStrategyWizardPage createPage(ISelection selection);

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
        return JFaceUtils.runModalWithErrorDialog(getContainer(), op, false,
                new I18NBoundMessage2P(
                        Messages.ABSTRACT_NEW_STRATEGY_WIZARD_CREATION_FAILED,
                        className, containerName));
    }

    /**
     * The worker method. It will find the container, create the file if missing
     * or just replace its contents, and open the editor on the newly created
     * file.
     */
    private void doFinish(final String containerName, final String className,
            final IProgressMonitor monitor) throws CoreException {
        final SubMonitor progress = SubMonitor.convert(monitor);
        final String fileName = getScriptName(className);
        progress.beginTask(
                Messages.ABSTRACT_NEW_STRATEGY_WIZARD_CREATING_FILE__TASK_NAME
                        .getText(fileName), 100);
        final IWorkspace workspace = ResourcesPlugin.getWorkspace();
        IWorkspaceRoot root = workspace.getRoot();
        IResource resource = root.findMember(containerName);
        if (resource == null || !(resource instanceof IContainer)) {
            throwCoreException(Messages.ABSTRACT_NEW_STRATEGY_WIZARD_MISSING_CONTAINER
                    .getText(containerName));
        }
        IContainer container = (IContainer) resource;
        final IFile file = container.getFile(new Path(fileName));
        workspace.run(
                new IWorkspaceRunnable() {
                    @Override
                    public void run(IProgressMonitor monitor)
                            throws CoreException {
                        InputStream stream = openContentStream(className);
                        try {
                            if (file.exists()) {
                                throwCoreException(Messages.ABSTRACT_NEW_STRATEGY_WIZARD_FILE_EXISTS
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
                    }
                }, workspace.getRuleFactory().createRule(file),
                IWorkspace.AVOID_UPDATE, progress.newChild(50));
        progress
                .setTaskName(Messages.ABSTRACT_NEW_STRATEGY_WIZARD_OPENING_FILE__TASK_NAME
                        .getText());
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
        progress.worked(50);
    }

    protected abstract String getScriptName(String typeName);

    protected abstract InputStream openContentStream(String className);

    private void throwCoreException(String message) throws CoreException {
        IStatus status = new Status(IStatus.ERROR, StrategyUI.PLUGIN_ID,
                IStatus.OK, message, null);
        throw new CoreException(status);
    }

    @Override
    public void init(IWorkbench workbench, IStructuredSelection selection) {
        this.mSelection = selection;
    }
}