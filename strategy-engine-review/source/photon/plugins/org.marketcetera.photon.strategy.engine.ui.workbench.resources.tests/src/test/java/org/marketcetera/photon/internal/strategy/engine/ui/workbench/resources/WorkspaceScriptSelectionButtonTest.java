package org.marketcetera.photon.internal.strategy.engine.ui.workbench.resources;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import java.io.ByteArrayInputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.waits.Conditions;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotButton;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotCLabel;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotLabel;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.marketcetera.photon.test.AbstractUIRunner;
import org.marketcetera.photon.test.WorkbenchRunner;
import org.marketcetera.photon.test.AbstractUIRunner.ThrowableRunnable;
import org.marketcetera.photon.test.AbstractUIRunner.UI;

/* $License$ */

/**
 * Test {@link WorkspaceScriptSelectionButton}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@RunWith(WorkbenchRunner.class)
public class WorkspaceScriptSelectionButtonTest {

    private Fixture mFixture = new Fixture();

    @Before
    @UI
    public void before() throws Exception {
        IDE.registerAdapters();
        ResourcesPlugin.getWorkspace().getRoot().delete(true, null);
    }

    @After
    @UI
    public void after() throws Exception {
        ResourcesPlugin.getWorkspace().getRoot().delete(true, null);
    }

    @Test
    public void testEmpty() throws Exception {
        mFixture.open(null);
        assertThat(mFixture.getPrompt().isEnabled(), is(false));
        assertThat(mFixture.getTree().isEnabled(), is(false));
        assertThat(mFixture.getStatusLine().isEnabled(), is(true));
        assertThat(mFixture.getStatusLine().getText(),
                is("The workspace is empty"));
        assertThat(mFixture.getOKButton().isEnabled(), is(false));
        SWTBotButton cancelButton = mFixture.getCancelButton();
        assertThat(cancelButton.isEnabled(), is(true));
        cancelButton.click();
        mFixture.awaitClose();
        assertThat(mFixture.getResult(), nullValue());
    }

    @Test
    public void testWithDummyData() throws Exception {
        initDummyData();
        mFixture.open(null);
        assertThat(mFixture.getPrompt().isEnabled(), is(true));
        SWTBotTree tree = mFixture.getTree();
        assertThat(tree.isEnabled(), is(true));
        SWTBotTreeItem[] items = tree.getAllItems();
        assertThat(items.length, is(2));
        SWTBotTreeItem testProject = items[0];
        SWTBotTreeItem test2Project = items[1];
        assertThat(testProject.getText(), is("test"));
        assertThat(test2Project.getText(), is("test2"));
        testProject.expand();
        test2Project.expand();
        assertThat(test2Project.getItems().length, is(0));
        SWTBotTreeItem[] testChildren = testProject.getItems();
        assertThat(testChildren.length, is(1));
        SWTBotTreeItem scriptFile = testChildren[0];
        assertThat(scriptFile.getText(), is("strat.rb"));
        assertThat(mFixture.getStatusLine().getText(), is(""));
        assertThat(mFixture.getOKButton().isEnabled(), is(false));
        assertThat(mFixture.getCancelButton().isEnabled(), is(true));

        // test multiple selection not allowed
        mFixture.getTree().select(0, 1);
        assertThat(mFixture.getTree().selectionCount(), is(0));

        // test project is invalid
        testProject.select();
        assertThat(mFixture.getOKButton().isEnabled(), is(false));
        assertThat(mFixture.getCancelButton().isEnabled(), is(true));

        // test file is valid
        scriptFile.select();
        assertThat(mFixture.getOKButton().isEnabled(), is(true));
        assertThat(mFixture.getCancelButton().isEnabled(), is(true));

        mFixture.getShell().close();
        mFixture.awaitClose();
        assertThat(mFixture.getResult(), nullValue());
    }

    @Test
    public void testOk() throws Exception {
        initDummyData();
        mFixture.open(null);
        SWTBotTreeItem testProject = mFixture.getTree().getTreeItem("test");
        testProject.expand();
        testProject.getNode("strat.rb").select();
        mFixture.getOKButton().click();
        mFixture.awaitClose();
        assertThat(mFixture.getResult(), is("platform:/resource/test/strat.rb"));
    }

    @Test
    public void testCancel() throws Exception {
        initDummyData();
        mFixture.open(null);
        SWTBotTreeItem testProject = mFixture.getTree().getTreeItem("test");
        testProject.expand();
        testProject.getNode("strat.rb").select();
        mFixture.getCancelButton().click();
        mFixture.awaitClose();
        assertThat(mFixture.getResult(), nullValue());
    }

    @Test
    public void testInitialSelection() throws Exception {
        initDummyData();
        mFixture.open("platform:/resource/test/strat.rb");
        assertThat(mFixture.getTree().selectionCount(), is(1));
        assertThat(mFixture.getTree().selection().get(0).get(0), is("strat.rb"));
        assertThat(mFixture.getOKButton().isEnabled(), is(true));
        assertThat(mFixture.getCancelButton().isEnabled(), is(true));
        mFixture.getCancelButton().click();
        mFixture.awaitClose();
        assertThat(mFixture.getResult(), nullValue());
    }

    @Test
    public void testUnrecognizedInitialSelection() throws Exception {
        initDummyData();
        mFixture.open("c:\\script.rb");
        assertThat(mFixture.getOKButton().isEnabled(), is(false));
        assertThat(mFixture.getCancelButton().isEnabled(), is(true));
        mFixture.getCancelButton().click();
        mFixture.awaitClose();
        assertThat(mFixture.getResult(), nullValue());
    }

    @Test
    public void testUnrecognizedWorkspaceURL() throws Exception {
        initDummyData();
        mFixture.open("platform:/resource/test/stratx.rb");
        assertThat(mFixture.getOKButton().isEnabled(), is(false));
        assertThat(mFixture.getCancelButton().isEnabled(), is(true));
        mFixture.getCancelButton().click();
        mFixture.awaitClose();
        assertThat(mFixture.getResult(), nullValue());
    }

    private void initDummyData() throws Exception {
        ResourcesPlugin.getWorkspace().run(new IWorkspaceRunnable() {
            @Override
            public void run(IProgressMonitor monitor) throws CoreException {
                IProject project = ResourcesPlugin.getWorkspace().getRoot()
                        .getProject("test");
                // this creates a .project file that will be filtered
                project.create(null);
                project.open(null);
                IFile file = project.getFile("strat.rb");
                file.create(new ByteArrayInputStream("asdf".getBytes()), true,
                        null);
                project = ResourcesPlugin.getWorkspace().getRoot().getProject(
                        "test2");
                project.create(null);
                project.open(null);
            }
        }, null);
    }

    private static class Fixture {
        private final SWTBot mBot = new SWTBot();
        private volatile String mResult;
        private SWTBotShell mShell;

        public void open(final String initialSelection) {
            AbstractUIRunner.asyncRun(new ThrowableRunnable() {
                @Override
                public void run() throws Throwable {
                    WorkspaceScriptSelectionButton fixture = new WorkspaceScriptSelectionButton();
                    mResult = fixture.selectScript(PlatformUI.getWorkbench()
                            .getActiveWorkbenchWindow().getShell(),
                            initialSelection);
                }
            });
            mShell = mBot.shell("File Selection");
        }

        public SWTBotLabel getPrompt() {
            return mBot.label("Select a workspace file:");
        }

        public SWTBotTree getTree() {
            return mBot.tree();
        }

        public SWTBotCLabel getStatusLine() {
            return mBot.clabel();
        }

        public SWTBotButton getOKButton() {
            return mBot.button("OK");
        }

        public SWTBotButton getCancelButton() {
            return mBot.button("Cancel");
        }

        public SWTBotShell getShell() {
            return mShell;
        }

        public void awaitClose() {
            mBot.waitUntil(Conditions.shellCloses(mShell));
        }

        public String getResult() {
            return mResult;
        }
    }
}
