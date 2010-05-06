package org.marketcetera.photon.internal.strategy.ui;

import static org.eclipse.swtbot.swt.finder.waits.Conditions.shellCloses;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;
import static org.marketcetera.photon.strategy.engine.model.core.test.StrategyEngineCoreTestUtil.createDeployedStrategy;
import static org.marketcetera.photon.strategy.engine.model.core.test.StrategyEngineCoreTestUtil.createEngine;

import java.io.ByteArrayInputStream;
import java.util.Collections;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.marketcetera.photon.strategy.engine.IStrategyEngines;
import org.marketcetera.photon.strategy.engine.model.core.DeployedStrategy;
import org.marketcetera.photon.strategy.engine.model.core.StrategyEngine;
import org.marketcetera.photon.strategy.engine.ui.tests.MockUIConnection;
import org.marketcetera.photon.test.AbstractUIRunner;
import org.marketcetera.photon.test.CommandTestUtil;
import org.marketcetera.photon.test.OSGITestUtil;
import org.marketcetera.photon.test.WorkbenchRunner;
import org.marketcetera.photon.test.AbstractUIRunner.ThrowableRunnable;
import org.marketcetera.photon.test.AbstractUIRunner.UI;
import org.osgi.framework.ServiceRegistration;

/* $License$ */

/**
 * Tests {@link UndeployDeleteParticipant}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 2.0.0
 */
@RunWith(WorkbenchRunner.class)
public class UndeployDeleteParticipantTest {

    private IWorkspace mWorkspace;
    private ServiceRegistration mReference;

    @After
    @UI
    public void after() throws Exception {
        if (mWorkspace != null) {
            mWorkspace.run(new IWorkspaceRunnable() {
                @Override
                public void run(IProgressMonitor monitor) throws CoreException {
                    mWorkspace.getRoot().delete(true, null);
                }
            }, null);
        }
        if (mReference != null) {
            mReference.unregister();
        }
    }

    @Test
    public void testSingleFileOK() throws Exception {
        new TestTemplate() {
            private IFile mFile;

            @Override
            protected IStructuredSelection initWorkspace(IWorkspace workspace)
                    throws CoreException {
                IProject project = workspace.getRoot().getProject("test");
                project.create(null);
                project.open(null);
                mFile = project.getFile("xyz.txt");
                mFile.create(new ByteArrayInputStream("abc".getBytes()), true,
                        null);
                return new StructuredSelection(mFile);
            }

            @Override
            protected void initEngine(StrategyEngine engine) {
                DeployedStrategy strategy = createDeployedStrategy("abc");
                strategy.setScriptPath("platform:/resource/test/xyz.txt");
                engine.getDeployedStrategies().add(strategy);
                strategy = createDeployedStrategy("abc2");
                strategy.setScriptPath("platform:/resource/test/xyz.txt");
                engine.getDeployedStrategies().add(strategy);
            }

            @Override
            protected void interactWithWizard(SWTBot bot)
                    throws WidgetNotFoundException {
                bot.button("OK").click();
            }

            @Override
            protected void validateWorkspace(IWorkspace workspace) {
                assertThat(mFile.exists(), is(false));
            }

            @Override
            protected void validateEngine(StrategyEngine engine) {
                assertThat(engine.getDeployedStrategies().size(), is(0));
            }

        };
    }

    @Test
    public void testSingleFilePreviewCancel() throws Exception {
        new TestTemplate() {
            private IFile mFile;

            @Override
            protected IStructuredSelection initWorkspace(IWorkspace workspace)
                    throws CoreException {
                IProject project = workspace.getRoot().getProject("test");
                project.create(null);
                project.open(null);
                mFile = project.getFile("xyz.txt");
                mFile.create(new ByteArrayInputStream("abc".getBytes()), true,
                        null);
                return new StructuredSelection(mFile);
            }

            @Override
            protected void initEngine(StrategyEngine engine) {
                DeployedStrategy strategy = createDeployedStrategy("abc");
                strategy.setScriptPath("platform:/resource/test/xyz.txt");
                engine.getDeployedStrategies().add(strategy);
            }

            @Override
            protected void interactWithWizard(SWTBot bot)
                    throws WidgetNotFoundException {
                bot.button("Preview >").click();
                SWTBotTreeItem group = bot.tree().getTreeItem(
                        "Undeploy strategies using '/test/xyz.txt'");
                group.expand();
                group.getNode("Undeploy 'abc' on 'Engine'").uncheck();
                bot.button("OK").click();
            }

            @Override
            protected void validateWorkspace(IWorkspace workspace) {
                assertThat(mFile.exists(), is(false));
            }

            @Override
            protected void validateEngine(StrategyEngine engine) {
                assertThat(engine.getDeployedStrategies().size(), is(1));
            }

        };
    }

    @Test
    public void testDeleteFolder() throws Exception {
        new TestTemplate() {
            private IFolder mFolder;

            @Override
            protected IStructuredSelection initWorkspace(IWorkspace workspace)
                    throws CoreException {
                IProject project = workspace.getRoot().getProject("test");
                project.create(null);
                project.open(null);
                mFolder = project.getFolder("folder");
                mFolder.create(true, true, null);
                mFolder.getFile("xyz.txt").create(
                        new ByteArrayInputStream("abc".getBytes()), true, null);
                mFolder.getFile("abc.txt").create(
                        new ByteArrayInputStream("abc".getBytes()), true, null);
                mFolder.getFile("other.txt").create(
                        new ByteArrayInputStream("abc".getBytes()), true, null);
                return new StructuredSelection(mFolder);
            }

            @Override
            protected void initEngine(StrategyEngine engine) {
                DeployedStrategy strategy = createDeployedStrategy("abc");
                strategy
                        .setScriptPath("platform:/resource/test/folder/abc.txt");
                engine.getDeployedStrategies().add(strategy);
                strategy = createDeployedStrategy("xyz");
                strategy
                        .setScriptPath("platform:/resource/test/folder/xyz.txt");
                engine.getDeployedStrategies().add(strategy);
            }

            @Override
            protected void interactWithWizard(SWTBot bot)
                    throws WidgetNotFoundException {
                bot.button("Preview >").click();
                // uncheck to skip undeploy of abc
                SWTBotTreeItem group = bot.tree().getTreeItem(
                        "Undeploy strategies using '/test/folder'");
                group.expand();
                group.getNode("Undeploy 'abc' on 'Engine'").uncheck();
                group.getNode("Undeploy 'xyz' on 'Engine'");
                bot.button("OK").click();
            }

            @Override
            protected void validateWorkspace(IWorkspace workspace) {
                assertThat(mFolder.exists(), is(false));
            }

            @Override
            protected void validateEngine(StrategyEngine engine) {
                assertThat(engine.getDeployedStrategies().size(), is(1));
                assertThat(engine.getDeployedStrategies().get(0)
                        .getInstanceName(), is("abc"));
            }

        };
    }

    @Test
    public void testNoStrategiesAffected() throws Exception {
        new TestTemplate() {
            private IFile mFile;

            @Override
            protected IStructuredSelection initWorkspace(IWorkspace workspace)
                    throws CoreException {
                IProject project = workspace.getRoot().getProject("test");
                project.create(null);
                project.open(null);
                mFile = project.getFile("abc.txt");
                mFile.create(new ByteArrayInputStream("abc".getBytes()), true,
                        null);
                return new StructuredSelection(mFile);
            }

            @Override
            protected void initEngine(StrategyEngine engine) {
                DeployedStrategy strategy = createDeployedStrategy("xyz");
                strategy.setScriptPath("platform:/resource/test/xyz.txt");
                engine.getDeployedStrategies().add(strategy);
            }

            @Override
            protected void interactWithWizard(SWTBot bot)
                    throws WidgetNotFoundException {
                bot.button("Preview >").click();
                SWTBotTreeItem[] items = bot.tree().getAllItems();
                for (SWTBotTreeItem item : items) {
                    assertThat(
                            item.getText(),
                            not(is("Undeploy strategies using '/test/xyz.txt'")));
                }
                bot.button("OK").click();
            }

            @Override
            protected void validateWorkspace(IWorkspace workspace) {
                assertThat(mFile.exists(), is(false));
            }

            @Override
            protected void validateEngine(StrategyEngine engine) {
                assertThat(engine.getDeployedStrategies().size(), is(1));
            }

        };
    }

    @Test
    public void testUndeployFails() throws Exception {
        new TestTemplate() {
            private IFile mFile;

            @Override
            protected IStructuredSelection initWorkspace(IWorkspace workspace)
                    throws CoreException {
                IProject project = workspace.getRoot().getProject("test");
                project.create(null);
                project.open(null);
                mFile = project.getFile("xyz.txt");
                mFile.create(new ByteArrayInputStream("abc".getBytes()), true,
                        null);
                return new StructuredSelection(mFile);
            }

            @Override
            protected void initEngine(StrategyEngine engine) {
                DeployedStrategy strategy = createDeployedStrategy("abc");
                strategy.setScriptPath("platform:/resource/test/xyz.txt");
                engine.getDeployedStrategies().add(strategy);
                engine.setConnection(new MockUIConnection() {
                    @Override
                    public void undeploy(DeployedStrategy strategy)
                            throws Exception {
                        if (strategy.getInstanceName().equals("abc")) {
                            throw new Exception("Failed");
                        }
                        super.undeploy(strategy);
                    }
                });
            }

            @Override
            protected void interactWithWizard(SWTBot bot)
                    throws WidgetNotFoundException {
                bot.button("OK").click();
                SWTBotShell errorDialog = bot.shell("Refactoring");
                bot
                        .label("An exception has been caught while processing the refactoring 'Delete Resource'.\n\nReason:\nFailed");
                bot.button("Undo").click();
                bot.waitUntil(shellCloses(errorDialog));

            }

            @Override
            protected void validateWorkspace(IWorkspace workspace) {
                assertThat(mFile.exists(), is(true));
            }

            @Override
            protected void validateEngine(StrategyEngine engine) {
                assertThat(engine.getDeployedStrategies().size(), is(1));
            }

        };
    }

    private abstract class TestTemplate {
        private StrategyEngine mEngine;

        public TestTemplate() throws Exception {
            run();
        }

        private void run() throws Exception {
            mWorkspace = ResourcesPlugin.getWorkspace();
            mEngine = createEngine("Engine");
            initEngine(mEngine);
            mReference = OSGITestUtil.registerMockService(
                    IStrategyEngines.class, new IStrategyEngines() {
                        @Override
                        public void removeEngine(StrategyEngine engine) {
                        }

                        @Override
                        public IObservableList getStrategyEngines() {
                            return new WritableList(Collections
                                    .singletonList(mEngine),
                                    StrategyEngine.class);
                        }

                        @Override
                        public StrategyEngine addEngine(StrategyEngine engine) {
                            return null;
                        }
                    });
            final CountDownLatch latch = new CountDownLatch(1);
            AbstractUIRunner.asyncRun(new ThrowableRunnable() {
                @Override
                public void run() throws Throwable {
                    final AtomicReference<IStructuredSelection> selection = new AtomicReference<IStructuredSelection>();
                    mWorkspace.run(new IWorkspaceRunnable() {
                        @Override
                        public void run(IProgressMonitor monitor)
                                throws CoreException {
                            mWorkspace.getRoot().delete(true, null);
                            selection.set(initWorkspace(mWorkspace));
                        }
                    }, null);
                    CommandTestUtil
                            .runCommand(
                                    "org.eclipse.ltk.ui.refactoring.commands.deleteResources",
                                    selection.get());
                    latch.countDown();
                }
            });
            SWTBot bot = new SWTBot();
            interactWithWizard(bot);
            latch.await();
            mWorkspace.run(new IWorkspaceRunnable() {
                @Override
                public void run(IProgressMonitor monitor) throws CoreException {
                    validateWorkspace(mWorkspace);
                }
            }, null);
            AbstractUIRunner.syncRun(new ThrowableRunnable() {
                @Override
                public void run() throws Throwable {
                    validateEngine(mEngine);
                }
            });
        }

        protected abstract IStructuredSelection initWorkspace(
                IWorkspace workspace) throws CoreException;

        protected abstract void initEngine(StrategyEngine engine);

        protected abstract void interactWithWizard(SWTBot bot) throws Exception;

        protected abstract void validateWorkspace(IWorkspace workspace);;

        protected abstract void validateEngine(StrategyEngine engine);
    }

}
