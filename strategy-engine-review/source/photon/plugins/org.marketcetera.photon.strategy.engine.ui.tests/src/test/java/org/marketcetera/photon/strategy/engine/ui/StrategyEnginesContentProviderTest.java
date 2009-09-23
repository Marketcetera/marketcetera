package org.marketcetera.photon.strategy.engine.ui;

import static org.eclipse.swtbot.swt.finder.SWTBotAssert.assertText;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.marketcetera.photon.strategy.engine.model.core.test.StrategyEngineCoreTestUtil.buildEngines;
import static org.marketcetera.photon.strategy.engine.model.core.test.StrategyEngineCoreTestUtil.createDeployedStrategy;
import static org.marketcetera.photon.strategy.engine.model.core.test.StrategyEngineCoreTestUtil.createEngine;

import java.text.MessageFormat;

import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.marketcetera.photon.strategy.engine.model.core.Strategy;
import org.marketcetera.photon.strategy.engine.model.core.StrategyEngine;
import org.marketcetera.photon.strategy.engine.ui.tests.StrategyEngineTreeTestHelper;
import org.marketcetera.photon.test.AbstractUIRunner;
import org.marketcetera.photon.test.ExpectedFailure;
import org.marketcetera.photon.test.PhotonTestBase;
import org.marketcetera.photon.test.SimpleUIRunner;
import org.marketcetera.photon.test.AbstractUIRunner.ThrowableRunnable;
import org.marketcetera.photon.test.AbstractUIRunner.UI;

import com.google.common.collect.Lists;

/* $License$ */

/**
 * Test {@link StrategyEnginesContentProvider}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@RunWith(SimpleUIRunner.class)
public class StrategyEnginesContentProviderTest extends PhotonTestBase {

    private StrategyEngine mEngine1;
    private StrategyEngine mEngine2;
    private Strategy mStrategy1a;
    private Strategy mStrategy1b;
    private Strategy mStrategy2a;
    private StrategyEngineTreeTestHelper mHelper;

    @Before
    @UI
    public void before() throws Throwable {
        // StrategyEnginesContentProvider must be attached to a
        // tree viewer to function
        mHelper = new StrategyEngineTreeTestHelper();
        createTestModel(mHelper.getModel());
        mHelper.openWindow();
    }

    public void createTestModel(WritableList model) {
        mEngine1 = createEngine("Strategy Engine 1");
        mEngine2 = createEngine("Strategy Engine 2");

        mStrategy1a = createDeployedStrategy("strategy1a");
        mStrategy1b = createDeployedStrategy("strategy1b");
        mStrategy2a = createDeployedStrategy("strategy2a");

        model.addAll(buildEngines(mEngine1, mStrategy1a, mStrategy1b, mEngine2,
                mStrategy2a));
    }

    @After
    @UI
    public void after() {
        mHelper.closeWindow();
    }

    @Test
    public void testTreeContents() throws Exception {
        SWTBot bot = new SWTBot();
        SWTBotTreeItem[] items = bot.tree().getAllItems();
        assertThat(items.length, is(2));
        assertText("Strategy Engine 1", items[0]);
        assertText("Strategy Engine 2", items[1]);
        items[0].expand();
        SWTBotTreeItem[] items1 = items[0].getItems();
        assertThat(items1.length, is(2));
        assertText("strategy1a", items1[0]);
        assertText("strategy1b", items1[1]);
        items[1].expand();
        SWTBotTreeItem[] items2 = items[1].getItems();
        assertThat(items2.length, is(1));
        assertText("strategy2a", items2[0]);
    }

    @Test
    public void testSelection() throws Throwable {
        AbstractUIRunner.syncRun(new ThrowableRunnable() {
            @Override
            public void run() throws Throwable {
                mHelper.getTreeViewer().setSelection(
                        new StructuredSelection(mStrategy1a));
            }
        });
        SWTBot bot = new SWTBot();
        assertThat(bot.tree().selection().get(0, 0), is("strategy1a"));
    }

    @Test
    public void testAddAndRemove() throws Throwable {
        SWTBot bot = new SWTBot();
        SWTBotTree tree = bot.tree();
        assertThat(tree.rowCount(), is(2));
        AbstractUIRunner.syncRun(new ThrowableRunnable() {
            @Override
            public void run() throws Throwable {
                mHelper.getModel().remove(0);
            }
        });
        assertThat(tree.rowCount(), is(1));
        assertThat(tree.getAllItems()[0].getText(), is("Strategy Engine 2"));
        AbstractUIRunner.syncRun(new ThrowableRunnable() {
            @Override
            public void run() throws Throwable {
                mHelper.getModel().add(createEngine("ABC"));
            }
        });
        assertThat(tree.rowCount(), is(2));
        assertThat(tree.getAllItems()[1].getText(), is("ABC"));
        AbstractUIRunner.syncRun(new ThrowableRunnable() {
            @Override
            public void run() throws Throwable {
                mHelper.getTreeViewer().setInput(
                        new WritableList(buildEngines(mEngine1),
                                StrategyEngine.class));
            }
        });
        assertThat(tree.rowCount(), is(1));
        assertThat(tree.getAllItems()[0].getText(), is("Strategy Engine 1"));
    }

    @Test
    @UI
    public void testValidation() throws Exception {
        final Object badInput = new Object();
        new ExpectedFailure<IllegalArgumentException>(MessageFormat.format(
                "input [{0}] is not an IObservableList", badInput.getClass())) {

            @Override
            protected void run() throws Exception {
                mHelper.getContentProvider().inputChanged(null, null,
                        badInput);
            }
        };
        final Realm badRealm = new Realm() {
            @Override
            public boolean isCurrent() {
                return false;
            }
        };
        final WritableList badInput2 = new WritableList(badRealm, Lists
                .newArrayList(), null);
        new ExpectedFailure<IllegalArgumentException>(
                MessageFormat
                        .format(
                                "input [{0} on realm {1}] must be on the current display realm",
                                badInput2.getClass(), badRealm)) {
            @Override
            protected void run() throws Exception {
                mHelper.getContentProvider().inputChanged(null, null,
                        badInput2);
            }
        };
        final WritableList badInput3 = new WritableList(Lists.newArrayList(),
                String.class);
        new ExpectedFailure<IllegalArgumentException>(
                MessageFormat
                        .format(
                                "input [{0} with element type {1}] should have element type StrategyEngine.class",
                                badInput3, String.class)) {
            @Override
            protected void run() throws Exception {
                mHelper.getContentProvider().inputChanged(null, null,
                        badInput3);
            }
        };
        final WritableList goodInput = new WritableList(Lists.newArrayList(),
                StrategyEngine.class);
        new ExpectedFailure<IllegalArgumentException>("viewer cannot be null") {
            @Override
            protected void run() throws Exception {
                mHelper.getContentProvider().inputChanged(null, null,
                        goodInput);
            }
        };
        final Viewer badViewer = new TableViewer(new Shell());
        new ExpectedFailure<IllegalArgumentException>(MessageFormat.format(
                "viewer [{0}] is not an AbstractTreeViewer", TableViewer.class)) {
            @Override
            protected void run() throws Exception {
                mHelper.getContentProvider().inputChanged(badViewer,
                        null, goodInput);
            }
        };
        final Viewer badViewer2 = new TreeViewer(new Shell());
        new ExpectedFailure<IllegalArgumentException>("viewer must have an IElementComparer set") {
            @Override
            protected void run() throws Exception {
                mHelper.getContentProvider().inputChanged(badViewer2,
                        null, goodInput);
            }
        };
    }
}
