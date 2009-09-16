package org.marketcetera.photon.strategy.engine.strategyagent.ui;

import static org.eclipse.swtbot.swt.finder.waits.Conditions.shellCloses;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.concurrent.Callable;

import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.marketcetera.photon.internal.strategy.engine.strategyagent.ui.NewStrategyAgentWizardPage;
import org.marketcetera.photon.strategy.engine.IStrategyEngines;
import org.marketcetera.photon.strategy.engine.model.core.ConnectionState;
import org.marketcetera.photon.strategy.engine.model.core.StrategyEngine;
import org.marketcetera.photon.strategy.engine.model.strategyagent.StrategyAgentEngine;
import org.marketcetera.photon.strategy.engine.model.strategyagent.StrategyAgentEngineFactory;
import org.marketcetera.photon.strategy.engine.strategyagent.tests.StrategyAgentEngineTestUtil;
import org.marketcetera.photon.test.AbstractUIRunner;
import org.marketcetera.photon.test.PhotonTestBase;
import org.marketcetera.photon.test.SimpleUIRunner;
import org.marketcetera.photon.test.AbstractUIRunner.UI;

/* $License$ */

/**
 * Tests {@link NewStrategyAgentWizard} and {@link NewStrategyAgentWizardPage}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@RunWith(SimpleUIRunner.class)
public class NewStrategyAgentWizardTest extends PhotonTestBase {

    private volatile Shell mShell;
    private volatile IStrategyEngines mMockService;

    @Before
    @UI
    public void before() {
        mShell = new Shell();
        mMockService = new IStrategyEngines() {
            @Override
            public void removeEngine(StrategyEngine engine) {
            }

            @Override
            public IObservableList getStrategyEngines() {
                return null;
            }

            @Override
            public StrategyEngine addEngine(StrategyEngine engine) {
                return (StrategyAgentEngine) engine;
            }
        };
    }

    @After
    @UI
    public void after() {
        mShell.dispose();
    }

    @Test
    public void testEngineCreation() throws Exception {
        NewStrategyAgentWizardFixture fixture = NewStrategyAgentWizardFixture
                .create(mShell, mMockService, null);
        fixture.assertFinishEnabled(false);
        fixture.setName("My Engine");
        fixture.assertFinishEnabled(false);
        fixture.setJmsUrl("tcp://localhost:123");
        fixture.assertFinishEnabled(false);
        fixture.setHostname("localhost");
        fixture.assertFinishEnabled(false);
        fixture.setPort("456");
        fixture.assertFinishEnabled(true);
        fixture.setDescription("My Strategy Agent Engine");
        fixture.finish();
        StrategyAgentEngineTestUtil.assertStrategyAgentEngine(fixture.waitForClose(), "My Engine",
                "My Strategy Agent Engine", "tcp://localhost:123", "localhost",
                456, ConnectionState.DISCONNECTED);
    }

    @Test
    public void testSeededValues() throws Exception {
        StrategyAgentEngine engine = AbstractUIRunner
                .syncCall(new Callable<StrategyAgentEngine>() {
                    @Override
                    public StrategyAgentEngine call() {
                        StrategyAgentEngine engine = StrategyAgentEngineFactory.eINSTANCE
                                .createStrategyAgentEngine();
                        engine.setName("abc");
                        engine.setDescription("xyz");
                        engine.setConnectionState(ConnectionState.CONNECTED);
                        engine.setJmsUrl("tcp://abc");
                        engine.setWebServiceHostname("host");
                        engine.setWebServicePort(8080);
                        return engine;
                    }
                });
        NewStrategyAgentWizardFixture fixture = NewStrategyAgentWizardFixture
                .create(mShell, mMockService, engine);
        fixture.finish();
        StrategyAgentEngineTestUtil.assertStrategyAgentEngine(fixture.waitForClose(), "abc", "xyz", "tcp://abc", "host",
                8080, ConnectionState.CONNECTED);
    }

    /**
     * Helper for testing {@link NewStrategyAgentWizard}.
     */
    public static class NewStrategyAgentWizardFixture {

        public static NewStrategyAgentWizardFixture create(final Shell shell,
                final IStrategyEngines engines, final StrategyAgentEngine engine)
                throws Exception {
            NewStrategyAgentWizard wizard = AbstractUIRunner.syncCall(new Callable<NewStrategyAgentWizard>() {
                @Override
                public NewStrategyAgentWizard call() {
                    NewStrategyAgentWizard wizard = new NewStrategyAgentWizard(engines, engine);
                    WizardDialog dialog = new WizardDialog(shell,
                            wizard);
                    dialog.setBlockOnOpen(false);
                    dialog.open();
                    return wizard;
                }
            });
            return new NewStrategyAgentWizardFixture(wizard);
        }

        private final SWTBot mBot = new SWTBot();
        private final SWTBotShell mShell;
        private final NewStrategyAgentWizard mWizard;

        public NewStrategyAgentWizardFixture(NewStrategyAgentWizard wizard) {
            mWizard = wizard;
            mShell = mBot.shell("New Engine");
        }

        public void setName(String name) {
            mBot.textWithLabel("Name:").setText(name);
        }

        public void setDescription(String description) {
            mBot.textWithLabel("Description:").setText(description);
        }

        public void setJmsUrl(String jmsUrl) {
            mBot.textWithLabel("JMS URL:").setText(jmsUrl);
        }

        public void setHostname(String hostname) {
            mBot.textWithLabel("Web Service Hostname:").setText(hostname);
        }

        public void setPort(String port) {
            mBot.textWithLabel("Web Service Port:").setText(port);
        }

        public void assertFinishEnabled(boolean enabled) {
            assertThat(mBot.button("Finish").isEnabled(), is(enabled));
        }

        public void finish() {
            mBot.button("Finish").click();
        }

        public StrategyAgentEngine waitForClose() {
            mBot.waitUntil(shellCloses(mShell));
            return mWizard == null ? null : mWizard.getResult();
        }
    }

}
