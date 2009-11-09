package org.marketcetera.photon.strategy.engine.ui;

import static org.eclipse.swtbot.swt.finder.SWTBotAssert.assertText;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.marketcetera.photon.strategy.engine.model.core.test.StrategyEngineCoreTestUtil.buildEngines;
import static org.marketcetera.photon.strategy.engine.model.core.test.StrategyEngineCoreTestUtil.createDeployedStrategy;
import static org.marketcetera.photon.strategy.engine.model.core.test.StrategyEngineCoreTestUtil.createEngine;
import static org.marketcetera.photon.strategy.engine.model.core.test.StrategyEngineCoreTestUtil.createStrategy;
import static org.marketcetera.photon.test.JFaceAsserts.assertImage;

import java.text.MessageFormat;

import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.set.WritableSet;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.marketcetera.photon.commons.ui.SWTUtilsTest.ExpectedThreadCheckFailure;
import org.marketcetera.photon.strategy.engine.model.core.DeployedStrategy;
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
 * Test {@link StrategyEnginesLabelProvider}.
 * <p>
 * Note: some basic testing of StrategyEnginesLabelProvider is covered in
 * {@link StrategyEnginesContentProviderTest}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@RunWith(SimpleUIRunner.class)
public class StrategyEnginesLabelProviderTest extends PhotonTestBase {

    private StrategyEngineTreeTestHelper mHelper;
    private StrategyEnginesLabelProvider mFixture;

    @Before
    @UI
    public void before() {
        mHelper = new StrategyEngineTreeTestHelper();
        mFixture = (StrategyEnginesLabelProvider) mHelper.getLabelProvider();
        mHelper.openWindow();
    }

    @After
    @UI
    public void after() {
        mHelper.closeWindow();
    }

    @Test
    @UI
    public void testText() throws Exception {
        StrategyEngine engine = createEngine("ABC");
        assertThat(mFixture.getText(engine), is("ABC"));

        Strategy strategy = createStrategy("def");
        assertThat(mFixture.getText(strategy), is("def"));

        Object unknown = new Object();
        assertThat(mFixture.getText(unknown), is(unknown.toString()));
    }

    @Test
    @UI
    public void testStyledText() throws Exception {
        StrategyEngine engine = createEngine("ABC");
        StyledString styledText = mFixture.getStyledText(engine);
        assertThat(styledText.getString(), is("ABC"));
        assertThat(styledText.getStyleRanges().length, is(0));

        Strategy strategy = createStrategy("def");
        styledText = mFixture.getStyledText(strategy);
        assertThat(styledText.getString(), is("def"));
        assertThat(styledText.getStyleRanges().length, is(0));

        Object unknown = new Object();
        styledText = mFixture.getStyledText(unknown);
        assertThat(styledText.getString(), is(unknown.toString()));
        assertThat(styledText.getStyleRanges().length, is(0));
    }

    @Test
    @UI
    public void testImages() throws Exception {
        StrategyEngine engine = createEngine("");
        assertImage(mFixture.getImage(engine), StrategyEngineImage.ENGINE_OBJ
                .getImageDescriptor());

        Strategy strategy = createStrategy("");
        assertImage(mFixture.getImage(strategy),
                StrategyEngineImage.STRATEGY_OBJ.getImageDescriptor());

        Object unknown = new Object();
        assertThat(mFixture.getImage(unknown), nullValue());
    }

    private StrategyEngine mEngine1;
    private DeployedStrategy mStrategy1a;
    private DeployedStrategy mStrategy1b;

    @Test
    public void testUpdates() throws Throwable {
        AbstractUIRunner.syncRun(new ThrowableRunnable() {
            @Override
            public void run() throws Throwable {
                mEngine1 = createEngine("Strategy Engine 1");
                mStrategy1a = createDeployedStrategy("strategy1a");
                mStrategy1b = createDeployedStrategy("strategy1b");
                mHelper.getModel().addAll(buildEngines(mEngine1, mStrategy1a));
            }
        });
        SWTBot bot = new SWTBot();
        final SWTBotTree tree = bot.tree();
        SWTBotTreeItem engine1 = tree.getAllItems()[0];
        assertText("Strategy Engine 1", engine1);
        engine1.expand();
        SWTBotTreeItem strat1a = engine1.getItems()[0];
        assertText("strategy1a", strat1a);
        AbstractUIRunner.syncRun(new ThrowableRunnable() {
            @Override
            public void run() throws Throwable {
                mEngine1.setName("Changed Engine 1");
            }
        });
        assertText("Changed Engine 1", engine1);
        AbstractUIRunner.syncRun(new ThrowableRunnable() {
            @Override
            public void run() throws Throwable {
                mStrategy1a.setInstanceName("cstrategy1a");
            }
        });
        assertText("cstrategy1a", strat1a);
        AbstractUIRunner.syncRun(new ThrowableRunnable() {
            @Override
            public void run() throws Throwable {
                mEngine1.getDeployedStrategies().add(mStrategy1b);
            }
        });
        SWTBotTreeItem strat1b = engine1.getItems()[1];
        assertText("strategy1b", strat1b);
        AbstractUIRunner.syncRun(new ThrowableRunnable() {
            @Override
            public void run() throws Throwable {
                mStrategy1b.setInstanceName("cstrategy1b");
            }
        });
        assertText("cstrategy1b", strat1b);
    }

    @Test
    public void testValidation() throws Exception {
        new ExpectedThreadCheckFailure() {
            @Override
            protected void run() throws Exception {
                new StrategyEnginesLabelProvider();
            }
        };
    }

    @Test
    @UI
    public void testValidation2() throws Exception {
        final Realm badRealm = new Realm() {
            @Override
            public boolean isCurrent() {
                return false;
            }
        };
        new ExpectedFailure<Exception>(
                MessageFormat
                        .format(
                                "the realm of elements [{0}] is not the realm of the current display", //$NON-NLS-1$
                                badRealm)) {
            @Override
            protected void run() throws Exception {
                StrategyEnginesLabelProvider.createAndTrack(new WritableSet(
                        badRealm, Lists.newArrayList(), null));
            }
        };
    }

}
