package org.marketcetera.photon.internal.strategy.ui;

import static org.eclipse.swtbot.swt.finder.waits.Conditions.shellCloses;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.io.ByteArrayInputStream;
import java.text.MessageFormat;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotButton;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotText;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.marketcetera.photon.commons.ui.databinding.RequiredFieldSupportTest;
import org.marketcetera.photon.commons.ui.databinding.SWTBotControlDecoration;
import org.marketcetera.photon.internal.strategy.ui.AbstractNewStrategyWizard;
import org.marketcetera.photon.test.AbstractUIRunner;
import org.marketcetera.photon.test.PhotonTestBase;
import org.marketcetera.photon.test.WorkbenchRunner;
import org.marketcetera.photon.test.AbstractUIRunner.ThrowableRunnable;
import org.marketcetera.photon.test.AbstractUIRunner.UI;

/* $License$ */

/**
 * Tests {@link AbstractNewStrategyWizard}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@RunWith(WorkbenchRunner.class)
public abstract class AbstractNewStrategyWizardTestBase<T extends AbstractNewStrategyWizard>
        extends PhotonTestBase {

    private WizardDialog mDialog;
    private IWorkspace mWorkspace;

    protected abstract T createWizard();

    protected abstract Fixture createFixture();

    protected abstract String getFileNameForMyStrategy();

    @Before
    @UI
    public void before() throws Exception {
        IDE.registerAdapters();
        mWorkspace = ResourcesPlugin.getWorkspace();
        mWorkspace.run(new IWorkspaceRunnable() {
            @Override
            public void run(IProgressMonitor monitor) throws CoreException {
                mWorkspace.getRoot().delete(true, null);
            }
        }, null);
    }

    @After
    @UI
    public void after() throws Exception {
        mWorkspace.run(new IWorkspaceRunnable() {
            @Override
            public void run(IProgressMonitor monitor) throws CoreException {
                mWorkspace.getRoot().delete(true, null);
            }
        }, null);
        if (mDialog != null) {
            mDialog.close();
        }
    }

    @Test
    public void testAbstractNewStrategyWizard() throws Exception {
        new TestTemplate() {

            private IProject mTestProject;

            @Override
            protected void initWorkspace(IWorkspace workspace)
                    throws CoreException {
                mTestProject = workspace.getRoot().getProject("test");
                mTestProject.create(null);
                mTestProject.open(null);
            }

            @Override
            protected IStructuredSelection getInitialSelection() {
                return new StructuredSelection(mTestProject);
            }

            @Override
            protected void testWizard(Fixture fixture) throws Exception {
                String containerMessage = RequiredFieldSupportTest
                        .getRequiredValueMessage("Container");
                String classNameMessage = RequiredFieldSupportTest
                        .getRequiredValueMessage("Class Name");
                assertThat(fixture.getContainerText().getText(), is("/test"));
                fixture.getContainerDecoration().assertHidden();
                fixture.getClassNameDecoration().assertRequired(
                        classNameMessage);
                assertThat(fixture.getFinishButton().isEnabled(), is(false));
                fixture.getContainerText().setText("");
                fixture.getContainerDecoration().assertRequired(
                        containerMessage);
                assertThat(fixture.getFinishButton().isEnabled(), is(false));
                fixture.getContainerText().setText("/test");
                fixture.getContainerDecoration().assertHidden();
                fixture.getClassNameDecoration().assertRequired(
                        classNameMessage);
                assertThat(fixture.getFinishButton().isEnabled(), is(false));
                fixture.getClassNameText().setText("MyStrategy");
                fixture.getContainerDecoration().assertHidden();
                fixture.getClassNameDecoration().assertHidden();
                assertThat(fixture.getFinishButton().isEnabled(), is(true));
                fixture.getFinishButton().click();
                SWTWorkbenchBot bot = new SWTWorkbenchBot();
                bot.waitUntil(shellCloses(fixture.getShell()), 10000, 500);
                bot.editorByTitle(getFileNameForMyStrategy());
            }

            protected void validateWorkspace(IWorkspace workspace)
                    throws CoreException {
                assertThat(workspace.getRoot().getFile(
                        new Path("/test/" + getFileNameForMyStrategy()))
                        .exists(), is(true));
            };
        }.run();
    }

    @Test
    public void testContainerDoesNotExist() throws Exception {
        new TestTemplate() {
            @Override
            protected void testWizard(Fixture fixture) throws Exception {
                fixture.getContainerText().setText("bogus");
                fixture.getClassNameText().setText("MyStrategy");
                fixture.getFinishButton().click();
                fixture.dismissMissingContainerError();
                fixture.getCancelButton().click();
            }
        }.run();
    }

    @Test
    public void testInvalidClassName() throws Exception {
        new TestTemplate() {
            @Override
            protected void testWizard(Fixture fixture) throws Exception {
                String error = "The class name is invalid. It must begin with a capital letter, and contain only letters, digits, or underscores.";
                fixture.getContainerText().setText("bogus");
                fixture.getClassNameText().setText("abc");
                fixture.getClassNameDecoration().assertError(error);
                fixture.getClassNameText().setText("1234");
                fixture.getClassNameDecoration().assertError(error);
                fixture.getClassNameText().setText("A___123asdf234");
                fixture.getClassNameDecoration().assertHidden();
                fixture.getCancelButton().click();
            }
        }.run();
    }

    @Test
    public void testFileExists() throws Exception {
        new TestTemplate() {
            @Override
            protected void initWorkspace(IWorkspace workspace)
                    throws CoreException {
                IProject testProject = workspace.getRoot().getProject("test");
                testProject.create(null);
                testProject.open(null);
                testProject.getFile(getFileNameForMyStrategy()).create(
                        new ByteArrayInputStream("abc".getBytes()), true, null);
            }

            @Override
            protected void testWizard(Fixture fixture) throws Exception {
                fixture.getContainerText().setText("/test");
                fixture.getClassNameText().setText("MyStrategy");
                fixture.getFinishButton().click();
                fixture.dismissFileExistsError(getFileNameForMyStrategy());
                fixture.getCancelButton();
            }
        }.run();
    }

    @Test
    public void testFileSelectionIntializesToContainer() throws Exception {
        new TestTemplate() {
            private IFile mFile;

            @Override
            protected void initWorkspace(IWorkspace workspace)
                    throws CoreException {
                IProject project = workspace.getRoot().getProject("test");
                project.create(null);
                project.open(null);
                mFile = project.getFile("xyz.txt");
                mFile.create(new ByteArrayInputStream("abc".getBytes()), true,
                        null);
            }

            @Override
            protected IStructuredSelection getInitialSelection() {
                return new StructuredSelection(mFile);
            }

            @Override
            protected void testWizard(Fixture fixture) throws Exception {
                assertThat(fixture.getContainerText().getText(), is("/test"));
                fixture.getCancelButton().click();
            }
        }.run();
    }

    @Test
    public void testBrowseButton() throws Exception {
        new TestTemplate() {
            @Override
            protected void initWorkspace(IWorkspace workspace)
                    throws CoreException {
                IProject project = workspace.getRoot().getProject("test");
                project.create(null);
                project.open(null);
                project = workspace.getRoot().getProject("test2");
                project.create(null);
                project.open(null);
                project.getFolder("folder").create(true, true, null);
            }

            @Override
            protected void testWizard(Fixture fixture) throws Exception {
                fixture.getBrowseButton().click();
                SWTBot bot = new SWTBot();
                bot.shell("Folder Selection");
                bot.label("Select new file container:");
                SWTBotTreeItem item = bot.tree().getTreeItem("test2");
                item.expand();
                item.getNode("folder").select();
                bot.button("OK").click();
                assertThat(fixture.getContainerText().getText(),
                        is("/test2/folder"));
                fixture.getCancelButton().click();
            }
        }.run();
    }

    protected abstract class TestTemplate {

        public final void run() throws Exception {
            mWorkspace.run(new IWorkspaceRunnable() {
                @Override
                public void run(IProgressMonitor monitor) throws CoreException {
                    mWorkspace.getRoot().delete(true, null);
                    initWorkspace(mWorkspace);
                }
            }, null);
            AbstractUIRunner.syncRun(new ThrowableRunnable() {
                @Override
                public void run() throws Throwable {
                    T wizard = createWizard();
                    IStructuredSelection selection = getInitialSelection();
                    if (selection != null) {
                        wizard.init(PlatformUI.getWorkbench(), selection);
                    }
                    mDialog = new WizardDialog(PlatformUI.getWorkbench()
                            .getActiveWorkbenchWindow().getShell(), wizard);
                    mDialog.setBlockOnOpen(false);
                    mDialog.open();
                }
            });

            testWizard(createFixture());

            mWorkspace.run(new IWorkspaceRunnable() {
                @Override
                public void run(IProgressMonitor monitor) throws CoreException {
                    validateWorkspace(mWorkspace);
                }
            }, null);
        }

        protected void initWorkspace(IWorkspace workspace) throws CoreException {
        }

        protected IStructuredSelection getInitialSelection() {
            return null;
        }

        protected abstract void testWizard(Fixture fixture) throws Exception;

        protected void validateWorkspace(IWorkspace workspace)
                throws CoreException {
        }
    }

    protected static class Fixture {

        private final SWTBot mBot = new SWTBot();
        private final SWTBotShell mShell;
        private final SWTBotText mContainerText;
        private final SWTBotControlDecoration mContainerDecoration;
        private final SWTBotText mClassNameText;
        private final SWTBotControlDecoration mClassNameDecoration;
        private final SWTBotButton mBrowseButton;
        private final SWTBotButton mFinishButton;
        private final SWTBotButton mCancelButton;

        public Fixture(String title) {
            mShell = mBot.shell(title);
            mBot.label(title);
            mBot.text("Create a new strategy script from a template.");
            assertThat(mBot.label("Container:").getToolTipText(),
                    is("The project or folder in which to create the script"));
            assertThat(mBot.label("Class Name:").getToolTipText(),
                    is("The strategy class name to use"));
            mContainerText = mBot.textWithLabel("Container:");
            mContainerDecoration = new SWTBotControlDecoration(mContainerText);
            mClassNameText = mBot.textWithLabel("Class Name:");
            mClassNameDecoration = new SWTBotControlDecoration(mClassNameText);
            mBrowseButton = mBot.button("Browse...");
            mFinishButton = mBot.button("Finish");
            mCancelButton = mBot.button("Cancel");
        }

        public void dismissMissingContainerError() {
            mBot.shell("Operation Failed");
            mBot.label(MessageFormat.format(
                    "Container ''{0}'' does not exist.", getContainerText()
                            .getText()));
            mBot.button("OK").click();
        }

        public void dismissFileExistsError(String file) {
            mBot.shell("Operation Failed");
            mBot.label(MessageFormat.format(
                    "There is already a file named ''{0}''.", file));
            mBot.button("OK").click();
        }

        public SWTBotShell getShell() {
            return mShell;
        }

        public SWTBotText getContainerText() {
            return mContainerText;
        }

        public SWTBotControlDecoration getContainerDecoration() {
            return mContainerDecoration;
        }

        public SWTBotText getClassNameText() {
            return mClassNameText;
        }

        public SWTBotControlDecoration getClassNameDecoration() {
            return mClassNameDecoration;
        }

        public SWTBotButton getBrowseButton() {
            return mBrowseButton;
        }

        public SWTBotButton getFinishButton() {
            return mFinishButton;
        }

        public SWTBotButton getCancelButton() {
            return mCancelButton;
        }
    }
}
