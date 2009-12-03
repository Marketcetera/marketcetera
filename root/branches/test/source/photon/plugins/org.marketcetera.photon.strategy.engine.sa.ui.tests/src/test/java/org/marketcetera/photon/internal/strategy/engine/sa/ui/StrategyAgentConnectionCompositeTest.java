package org.marketcetera.photon.internal.strategy.engine.sa.ui;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotText;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.marketcetera.photon.commons.ui.databinding.DataBindingTestUtils;
import org.marketcetera.photon.commons.ui.databinding.SWTBotControlDecoration;
import org.marketcetera.photon.internal.strategy.engine.sa.ui.StrategyAgentConnectionComposite;
import org.marketcetera.photon.strategy.engine.model.core.ConnectionState;
import org.marketcetera.photon.strategy.engine.model.sa.StrategyAgentEngine;
import org.marketcetera.photon.strategy.engine.model.sa.StrategyAgentEngineFactory;
import org.marketcetera.photon.strategy.engine.model.sa.StrategyAgentEnginePackage;
import org.marketcetera.photon.strategy.engine.sa.tests.StrategyAgentEngineTestUtil;
import org.marketcetera.photon.test.AbstractUIRunner;
import org.marketcetera.photon.test.PhotonTestBase;
import org.marketcetera.photon.test.SimpleUIRunner;
import org.marketcetera.photon.test.AbstractUIRunner.ThrowableRunnable;
import org.marketcetera.photon.test.AbstractUIRunner.UI;

/* $License$ */

/**
 * Tests {@link StrategyAgentConnectionComposite}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 2.0.0
 */
@RunWith(SimpleUIRunner.class)
public class StrategyAgentConnectionCompositeTest extends PhotonTestBase {

    private ApplicationWindow mWindow;
    private volatile StrategyAgentEngine mEngine;
    private final SWTBot mBot = new SWTBot();

    private void createAndOpenWindow() throws Exception {
        AbstractUIRunner.syncRun(new ThrowableRunnable() {
            @Override
            public void run() {
                mWindow = new ApplicationWindow(null) {
                    @Override
                    protected Control createContents(Composite parent) {
                        if (mEngine == null) {
                            mEngine = StrategyAgentEngineFactory.eINSTANCE
                                    .createStrategyAgentEngine();
                        }
                        final DataBindingContext dbc = new DataBindingContext();
                        final Composite control = new StrategyAgentConnectionComposite(
                                parent, dbc, mEngine);
                        return control;
                    }
                };
                mWindow.open();
            }
        });
    }

    @After
    @UI
    public void after() {
        if (mWindow != null) {
            mWindow.close();
        }
    }

    @Test
    public void testJMSURL() throws Exception {
        createAndOpenWindow();
        DataBindingTestUtils
                .testRequiredText(
                        mBot,
                        mEngine,
                        StrategyAgentEnginePackage.Literals.STRATEGY_AGENT_ENGINE__JMS_URL,
                        "JMS URL",
                        "The URL to connect to the strategy agent message queue",
                        "abc");
        SWTBotText jms = mBot.textWithLabel("JMS URL:");
        SWTBotControlDecoration jmsDecoration = new SWTBotControlDecoration(jms);
        jms.setText("::");
        jmsDecoration.assertError("JMS URL could not be parsed");
        jms.setText("tcp://localhost:67676");
        jmsDecoration.assertHidden();
    }

    @Test
    public void testHostname() throws Exception {
        createAndOpenWindow();
        DataBindingTestUtils
                .testRequiredText(
                        mBot,
                        mEngine,
                        StrategyAgentEnginePackage.Literals.STRATEGY_AGENT_ENGINE__WEB_SERVICE_HOSTNAME,
                        "Web Service Hostname",
                        "The hostname of the strategy agent web service", "abc");
    }

    @Test
    public void testPort() throws Exception {
        createAndOpenWindow();
        DataBindingTestUtils
                .testRequiredText(
                        mBot,
                        mEngine,
                        StrategyAgentEnginePackage.Literals.STRATEGY_AGENT_ENGINE__WEB_SERVICE_PORT,
                        "Web Service Port",
                        "The port of the strategy agent web service", "1");
        SWTBotText text = mBot.textWithLabel("Web Service Port:");
        SWTBotControlDecoration decoration = new SWTBotControlDecoration(text);
        text.setText("a");
        decoration
                .assertError("Web Service Port must be an integer between 1 and 65535");
        text.setText("67676");
        decoration
                .assertError("Web Service Port must be an integer between 1 and 65535");
        text.setText("1000000000000");
        decoration
                .assertError("Web Service Port must be an integer between 1 and 65535");
        text.setText("-1");
        decoration
                .assertError("Web Service Port must be an integer between 1 and 65535");
        text.setText("0");
        decoration
                .assertError("Web Service Port must be an integer between 1 and 65535");
        text.setText("65535");
        decoration.assertHidden();
    }

    @Test
    public void testSeededValues() throws Exception {
        AbstractUIRunner.syncRun(new ThrowableRunnable() {
            @Override
            public void run() throws Throwable {
                mEngine = StrategyAgentEngineTestUtil
                        .createStrategyAgentEngine(null, null, "tcp://abc",
                                "host", 8080);
                mEngine.setConnectionState(ConnectionState.CONNECTED);
            }
        });
        createAndOpenWindow();
        mBot.label("Disconnect the engine to edit these properties.");
        SWTBotText jms = mBot.textWithLabel("JMS URL:");
        assertThat(jms.getText(), is("tcp://abc"));
        jms.typeText("abc");
        assertThat(jms.getText(), is("tcp://abc"));
        SWTBotText wshost = mBot.textWithLabel("Web Service Hostname:");
        assertThat(wshost.getText(), is("host"));
        wshost.typeText("abc");
        assertThat(wshost.getText(), is("host"));
        SWTBotText wsport = mBot.textWithLabel("Web Service Port:");
        assertThat(wsport.getText(), is("8080"));
        wsport.typeText("abc");
        assertThat(wsport.getText(), is("8080"));
    }
}
