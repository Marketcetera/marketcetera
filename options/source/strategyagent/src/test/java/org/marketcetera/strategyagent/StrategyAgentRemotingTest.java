package org.marketcetera.strategyagent;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.ws.stateless.Node;
import org.marketcetera.util.ws.stateless.StatelessClientContext;
import org.marketcetera.util.ws.wrappers.RemoteProperties;
import org.marketcetera.util.log.I18NBoundMessage;
import org.marketcetera.util.log.I18NBoundMessage1P;
import org.marketcetera.util.log.I18NBoundMessage3P;
import org.marketcetera.util.except.I18NException;
import org.marketcetera.util.file.Deleter;
import org.marketcetera.module.*;
import org.marketcetera.client.MockServer;
import org.marketcetera.client.ClientManager;
import org.marketcetera.client.ClientParameters;
import org.marketcetera.client.ClientModuleFactory;
import org.marketcetera.saclient.*;
import org.marketcetera.strategy.Language;
import org.marketcetera.core.ApplicationVersion;
import org.marketcetera.core.Util;
import org.junit.*;
import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import org.hamcrest.Matchers;
import static org.hamcrest.Matchers.*;

import javax.management.InstanceNotFoundException;
import javax.management.InvalidAttributeValueException;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.math.BigDecimal;


/* $License$ */
/**
 * Verifies Remoting capabilities of the strategy agent.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public class StrategyAgentRemotingTest extends StrategyAgentTestBase {

    /**
     * Initializes the mock server, its client connection and the strategy
     * agent so that remote receiver is able to authenticate its clients
     * successfully.
     *
     * @throws Exception if there were unexpected failures.
     */
    @BeforeClass
    public static void createServerAndClient() throws Exception {
        StrategyAgentRemotingConfigTest.setupConfiguration();
        sServer = new MockServer();
        ClientManager.init(new ClientParameters(DEFAULT_CREDENTIAL,
                DEFAULT_CREDENTIAL.toCharArray(), MockServer.URL,
                Node.DEFAULT_HOST, Node.DEFAULT_PORT));
    }

    /**
     * Closes the client connection and shuts down the mock server.
     *
     * @throws Exception if there were errors.
     */
    @AfterClass
    public static void stopServerAndClient() throws Exception {
        if(ClientManager.isInitialized()) {
            ClientManager.getInstance().close();
        }
        if (sServer != null) {
            sServer.close();
        }
    }

    /**
     * Starts the strategy agent.
     */
    @Before
    public void startAgent() {
        run(createAgent(false));
    }

    /**
     * Closes the SA client connection if it's active.
     */
    @After
    public void closeClient() {
        if(sSAClient != null) {
            sSAClient.close();
            sSAClient = null;
        }
    }

    /**
     * Tests failures due to user authentication failure.
     *
     * @throws Exception if there were unexpected errors.
     */
    @Test
    public void loginFailures() throws Exception {
        //test null password
        new ExpectedFailure<ConnectionException>(
                org.marketcetera.saclient.Messages.ERROR_WS_CONNECT){
            @Override
            protected void run() throws Exception {
                SAClientFactory.getInstance().create(
                        new SAClientParameters(DEFAULT_CREDENTIAL, null,
                                RECEIVER_URL, WS_HOST, WS_PORT));
            }
        };
        //empty password
        new ExpectedFailure<ConnectionException>(
                org.marketcetera.saclient.Messages.ERROR_WS_CONNECT){
            @Override
            protected void run() throws Exception {
                SAClientFactory.getInstance().create(
                        new SAClientParameters(DEFAULT_CREDENTIAL, "".toCharArray(),
                                RECEIVER_URL, WS_HOST, WS_PORT));
            }
        };
        //incorrect password
        new ExpectedFailure<ConnectionException>(
                org.marketcetera.saclient.Messages.ERROR_WS_CONNECT){
            @Override
            protected void run() throws Exception {
                SAClientFactory.getInstance().create(
                        new SAClientParameters(DEFAULT_CREDENTIAL, "what?".toCharArray(),
                                RECEIVER_URL, WS_HOST, WS_PORT));
            }
        };
        //incorrect username
        new ExpectedFailure<ConnectionException>(
                org.marketcetera.saclient.Messages.ERROR_WS_CONNECT){
            @Override
            protected void run() throws Exception {
                SAClientFactory.getInstance().create(
                        new SAClientParameters("who", DEFAULT_CREDENTIAL.toCharArray(),
                                RECEIVER_URL, WS_HOST, WS_PORT));
            }
        };
        //finally a successful login
        createClient();
    }

    /**
     * Tests {@link StrategyAgent#authenticate(org.marketcetera.util.ws.stateless.StatelessClientContext, String, char[])}
     *
     * @throws Exception if there were unexpected failures.
     */
    @Test
    public void clientAuth() throws Exception {
        //null context
        new ExpectedFailure<NullPointerException>(){
            @Override
            protected void run() throws Exception {
                StrategyAgent.authenticate(null,"value", "value".toCharArray());
            }
        };
        final StatelessClientContext ctx = new StatelessClientContext();
        assertNull(ctx.getAppId());
        //context without version
        new ExpectedFailure<I18NException>(Messages.VERSION_MISMATCH,
                null, ApplicationVersion.getVersion(), DEFAULT_CREDENTIAL){
            @Override
            protected void run() throws Exception {
               StrategyAgent.authenticate(ctx, DEFAULT_CREDENTIAL, 
                       DEFAULT_CREDENTIAL.toCharArray());
            }
        };
        //context with invalid version
        ctx.setAppId(Util.getAppId("dontcare", "invalid"));
        new ExpectedFailure<I18NException>(Messages.VERSION_MISMATCH,
                "invalid", ApplicationVersion.getVersion(), DEFAULT_CREDENTIAL){
            @Override
            protected void run() throws Exception {
               StrategyAgent.authenticate(ctx, DEFAULT_CREDENTIAL,
                       DEFAULT_CREDENTIAL.toCharArray());
            }
        };
        //context with default version number
        ctx.setAppId(Util.getAppId("dontcare", ApplicationVersion.DEFAULT_VERSION));
        new ExpectedFailure<I18NException>(Messages.VERSION_MISMATCH,
                ApplicationVersion.DEFAULT_VERSION,
                ApplicationVersion.getVersion(), DEFAULT_CREDENTIAL){
            @Override
            protected void run() throws Exception {
               StrategyAgent.authenticate(ctx, DEFAULT_CREDENTIAL,
                DEFAULT_CREDENTIAL.toCharArray());
            }
        };
        //context with server version number
        ctx.setAppId(Util.getAppId("dontcare", ApplicationVersion.getVersion()));
        assertTrue(StrategyAgent.authenticate(ctx, DEFAULT_CREDENTIAL,
                DEFAULT_CREDENTIAL.toCharArray()));
        //valid contexts
        //invalid user/password
        assertFalse(StrategyAgent.authenticate(ctx,"go","go".toCharArray()));
        //invalid password
        assertFalse(StrategyAgent.authenticate(ctx,DEFAULT_CREDENTIAL,"go".toCharArray()));
        //null password
        assertFalse(StrategyAgent.authenticate(ctx,DEFAULT_CREDENTIAL,null));
        //invalid user
        assertFalse(StrategyAgent.authenticate(ctx,"go",DEFAULT_CREDENTIAL.toCharArray()));
        //null user
        assertFalse(StrategyAgent.authenticate(ctx,null,DEFAULT_CREDENTIAL.toCharArray()));
    }

    @Test
    public void getInstances() throws Exception {
        final SAClient saClient = createClient();
        List<ModuleURN> urns = saClient.getInstances(null);
        assertFalse(urns.toString(), urns.isEmpty());
        //verify it contains the receiver and the client instances
        assertTrue(urns.toString(), urns.contains(ClientModuleFactory.INSTANCE_URN));
        assertTrue(urns.toString(), urns.contains(RECEIVER_URN));
        //try sending a URN of a provider that doesn't exist
        urns = saClient.getInstances(new ModuleURN("metc:not:exist"));
        assertTrue(urns.toString(), urns.isEmpty());
        //try sending a URN of a provider that has some instances
        urns = saClient.getInstances(RECEIVER_URN.parent());
        assertFalse(urns.toString(), urns.isEmpty());
        assertEquals(urns.toString(), 1, urns.size());
        assertTrue(urns.toString(), urns.contains(RECEIVER_URN));
    }
    
    @Test
    public void getProviders() throws Exception {
        SAClient saClient = createClient();
        List<ModuleURN> urns = saClient.getProviders();
        assertFalse(urns.toString(), urns.isEmpty());
        assertTrue(urns.toString(), urns.contains(ClientModuleFactory.INSTANCE_URN.parent()));
        assertTrue(urns.toString(), urns.contains(RECEIVER_URN.parent()));
    }

    @Test
    public void getModuleInfo() throws Exception {
        final SAClient saClient = createClient();
        //null URN
        verifyNullURNFailure(new WSOpFailure() {
            @Override
            protected void run() throws Exception {
                saClient.getModuleInfo(null);
            }
        });
        //non-existent module's URN
        final String urn = "metc:instance:not:exist";
        ConnectionException failure = new WSOpFailure() {
            @Override
            protected void run() throws Exception {
                saClient.getModuleInfo(new ModuleURN(urn));
            }
        }.getException();
        assertEquals(failure.getCause().getLocalizedMessage(),
                org.marketcetera.module.Messages.MODULE_NOT_FOUND.getText(urn));
        //valid URN
        ModuleTestBase.assertModuleInfo(saClient.getModuleInfo(RECEIVER_URN),
                RECEIVER_URN, ModuleState.STARTED,  null, null,
                false, true, true, false, false);
    }

    @Test
    public void getPropertiesFailure() throws Exception {
        final SAClient saClient = createClient();
        //null URN
        verifyNullURNFailure(new WSOpFailure() {
            @Override
            protected void run() throws Exception {
                saClient.getProperties(null);
            }
        });
        //non-existent module
        final String urn = "metc:instance:not:exist";
        ConnectionException failure = new WSOpFailure(){
            @Override
            protected void run() throws Exception {
                saClient.getProperties(new ModuleURN(urn));
            }
        }.getException();
        assertThat(failure.getCause(), Matchers.instanceOf(InstanceNotFoundException.class));
        assertEquals(failure.getCause().getMessage(), new ModuleURN(urn).toObjectName().toString());
    }

    @Test
    public void setPropertiesFailure() throws Exception {
        final SAClient saClient = createClient();
        //null URN
        verifyNullURNFailure(new WSOpFailure() {
            @Override
            protected void run() throws Exception {
                saClient.setProperties(null, null);
            }
        });
        //non strategy URN
        verifyNestedFailure(new I18NBoundMessage1P(
                Messages.SET_PROPERTY_MODULE_NOT_STRATEGY, RECEIVER_URN),
                new WSOpFailure() {
            @Override
            protected void run() throws Exception {
                saClient.setProperties(RECEIVER_URN, null);
            }
        });
        //non existent strategy
        final ModuleURN urn = new ModuleURN(STRATEGY_PROVIDER_URN, "notexist");
        ConnectionException failure = new WSOpFailure(){
            @Override
            protected void run() throws Exception {
                //Supply a non-null property value
                Map<String, Object> propMap = new HashMap<String, Object>();
                saClient.setProperties(urn, propMap);
            }
        }.getException();
        assertThat(failure.getCause(), Matchers.instanceOf(InstanceNotFoundException.class));
        assertEquals(failure.getCause().getMessage(), urn.toObjectName().toString());
        //non editable properties
        final String key = "OutputDestination";
        verifyNestedFailure(new I18NBoundMessage3P(
                Messages.UNEDITABLE_STRATEGY_PROPERTY, key, urn,
                SAServiceImpl.EDITABLE_STRATEGY_PROPERTIES.toString()),
                new WSOpFailure() {
                    @Override
                    protected void run() throws Exception {
                        Map<String, Object> propMap = new HashMap<String, Object>();
                        propMap.put(key, "value");
                        saClient.setProperties(urn, propMap);
                    }
                });
    }

    /**
     * Tests failures when creating strategy.
     *
     * @throws Exception if there were unexpected errors.
     */
    @Test
    public void createStrategyFailure() throws Exception {
        final SAClient saClient = createClient();
        //null parameter failure
        verifyNestedFailure(Messages.NO_STRATEGY_CREATE_PARMS_SPECIFIED, new WSOpFailure() {
            @Override
            protected void run() throws Exception {
                saClient.createStrategy(null);
            }
        });
        //Create a temp file
        final File scriptFile = File.createTempFile("script",".tmp");
        final String name = "Strat";
        //Test failure from specifying an incorrect language.
        ConnectionException failure = new WSOpFailure() {
            @Override
            protected void run() throws Exception {
                saClient.createStrategy(new CreateStrategyParameters(name,
                        "HelloWorld", "UBY", scriptFile, null, false));
            }
        }.getException();
        //verify that we failed to convert the language.
        ExpectedFailure.assertI18NException(failure.getCause(),
                org.marketcetera.strategy.Messages.INVALID_LANGUAGE_ERROR, "UBY");
        //Test for not-existing files
        CreateStrategyParameters parameters = new CreateStrategyParameters(name,
                "HelloWorld", "RUBY", scriptFile, null, false);
        //now delete the script file
        Deleter.apply(scriptFile);
        //Create strategy succeeds
        final ModuleURN urn = saClient.createStrategy(parameters);
        assertEquals(name, urn.instanceName());
        //But we get an error when we try to start it because the strategy file is empty
        failure = new WSOpFailure() {
            @Override
            protected void run() throws Exception {
                saClient.start(urn);
            }
        }.getException();
        assertThat(failure.getCause().getLocalizedMessage(),
                Matchers.containsString(org.marketcetera.strategy.Messages.FAILED_TO_START.getText()));
    }

    /**
     * Tests failures when fetching strategy create parameters.
     *
     * @throws Exception if there were unexpected errors.
     */
    @Test
    public void getStrategyCreateParmsFailure() throws Exception {
        final SAClient saClient = createClient();
        //null URN
        verifyNullURNFailure(new WSOpFailure() {
            @Override
            protected void run() throws Exception {
                saClient.getStrategyCreateParms(null);
            }
        });
        //non strategy URN
        verifyNestedFailure(new I18NBoundMessage1P(
                            Messages.NO_CREATE_PARAMETERS_FOR_STRATEGY,
                RECEIVER_URN), new WSOpFailure() {
            @Override
            protected void run() throws Exception {
                saClient.getStrategyCreateParms(RECEIVER_URN);
            }
        });
        //non existent strategy
        final ModuleURN urn = new ModuleURN(STRATEGY_PROVIDER_URN, "notexist");
        verifyNestedFailure(new I18NBoundMessage1P(
                            Messages.NO_CREATE_PARAMETERS_FOR_STRATEGY, urn),
                new WSOpFailure() {
                    @Override
                    protected void run() throws Exception {
                        saClient.getStrategyCreateParms(urn);
                    }
                });
    }
    
    @Test
    public void startFailure() throws Exception {
        final SAClient saClient = createClient();
        //null URN
        verifyNullURNFailure(new WSOpFailure() {
            @Override
            protected void run() throws Exception {
                saClient.start(null);
            }
        });
        //non strategy URN
        verifyNestedFailure(new I18NBoundMessage1P(
                Messages.START_MODULE_NOT_STRATEGY, RECEIVER_URN),
                new WSOpFailure() {
                    @Override
                    protected void run() throws Exception {
                        saClient.start(RECEIVER_URN);
                    }
                });
        //non strategy module
        final ModuleURN urn = new ModuleURN(STRATEGY_PROVIDER_URN, "notexist");
        ConnectionException failure = new WSOpFailure(){
            @Override
            protected void run() throws Exception {
                saClient.start(urn);
            }
        }.getException();
        assertThat(failure.getCause().getLocalizedMessage(),
                Matchers.containsString(org.marketcetera.module.Messages.MODULE_NOT_FOUND.getText(urn)));
    }

    @Test
    public void stopFailure() throws Exception {
        final SAClient saClient = createClient();
        //null URN
        verifyNullURNFailure(new WSOpFailure() {
            @Override
            protected void run() throws Exception {
                saClient.stop(null);
            }
        });
        //non strategy URN
        verifyNestedFailure(new I18NBoundMessage1P(
                Messages.STOP_MODULE_NOT_STRATEGY, RECEIVER_URN),
                new WSOpFailure() {
                    @Override
                    protected void run() throws Exception {
                        saClient.stop(RECEIVER_URN);
                    }
                });
        //non strategy module
        final ModuleURN urn = new ModuleURN(STRATEGY_PROVIDER_URN, "notexist");
        ConnectionException failure = new WSOpFailure(){
            @Override
            protected void run() throws Exception {
                saClient.stop(urn);
            }
        }.getException();
        assertThat(failure.getCause().getLocalizedMessage(),
                Matchers.containsString(org.marketcetera.module.Messages.MODULE_NOT_FOUND.getText(urn)));
    }

    @Test
    public void deleteFailure() throws Exception {
        final SAClient saClient = createClient();
        //null URN
        verifyNullURNFailure(new WSOpFailure() {
            @Override
            protected void run() throws Exception {
                saClient.delete(null);
            }
        });
        //non strategy URN
        verifyNestedFailure(new I18NBoundMessage1P(
                Messages.DELETE_MODULE_NOT_STRATEGY, RECEIVER_URN),
                new WSOpFailure() {
                    @Override
                    protected void run() throws Exception {
                        saClient.delete(RECEIVER_URN);
                    }
                });
        //non strategy module
        final ModuleURN urn = new ModuleURN(STRATEGY_PROVIDER_URN, "notexist");
        ConnectionException failure = new WSOpFailure(){
            @Override
            protected void run() throws Exception {
                saClient.delete(urn);
            }
        }.getException();
        assertThat(failure.getCause().getLocalizedMessage(),
                Matchers.containsString(org.marketcetera.module.Messages.MODULE_NOT_FOUND.getText(urn)));
    }

    /**
     * Tests strategy lifecycle and various methods that can be successfully
     * invoked after the strategy has been created.
     *
     * @throws Exception if there were unexpected errors.
     */
    @Test
    public void strategyLifecycle() throws Exception {
        final SAClient saClient = createClient();
        //create, start, stop, delete, get/set props, get createParms
        //verify no instances exist yet
        List<ModuleURN> instances = saClient.getInstances(STRATEGY_PROVIDER_URN);
        assertTrue(instances.toString(),  instances.isEmpty());
        //create a strategy
        assertTrue(TEST_STRATEGY.getAbsolutePath(), TEST_STRATEGY.isFile());
        String name = "myStrat";
        ModuleURN urn = saClient.createStrategy(new CreateStrategyParameters(name,
                "HelloWorld", "RUBY", TEST_STRATEGY, null, false));
        assertEquals(name, urn.instanceName());
        assertEquals(STRATEGY_PROVIDER_URN, urn.parent());

        //verify the instance is reported
        instances = saClient.getInstances(STRATEGY_PROVIDER_URN);
        assertEquals(instances.toString(), 1, instances.size());
        assertEquals(urn, instances.get(0));

        //verify strategy properties and state
        ModuleTestBase.assertModuleInfo(saClient.getModuleInfo(urn), urn,
                ModuleState.CREATED, null, null,
                false, false, true, true, true);
        Map<String, Object> props = saClient.getProperties(urn);
        assertNotNull(props);
        assertFalse(props.isEmpty());
        assertEquals(props.toString(), 5, props.size());
        assertThat(props, allOf(hasEntry("Parameters", null),
                hasEntry("Name", "HelloWorld"),
                hasEntry("Language", Language.RUBY.toString()),
                hasEntry(STRAT_PROP_ROUTING_ORDERS, false),
                hasEntry("OutputDestination", RECEIVER_URN.parent().getValue())));

        //start the strategy
        saClient.start(urn);
        //verify properties and state
        assertEquals(ModuleState.STARTED, saClient.getModuleInfo(urn).getState());
        props = saClient.getProperties(urn);
        assertNotNull(props);
        assertEquals(props.toString(), 6, props.size());
        assertThat(props, allOf(hasEntry("Parameters", null),
                hasEntry("Name", "HelloWorld"),
                hasEntry("Language", Language.RUBY.toString()),
                hasEntry(STRAT_PROP_ROUTING_ORDERS, false),
                hasEntry("Status", "RUNNING"),
                hasEntry("OutputDestination", RECEIVER_URN.parent().getValue())));

        //stop the strategy
        saClient.stop(urn);
        //verify properties and state
        assertEquals(ModuleState.STOPPED, saClient.getModuleInfo(urn).getState());

        //change properties
        props = new HashMap<String, Object>();
        String paramValue = "key1=value1";
        props.put("Parameters", paramValue);
        props.put(STRAT_PROP_ROUTING_ORDERS, true);
        props = saClient.setProperties(urn, props);
        assertNotNull(props);
        assertEquals(props.toString(), 2, props.size());
        assertThat(props, allOf(hasEntry("Parameters", (Object)paramValue),
                hasEntry(STRAT_PROP_ROUTING_ORDERS, true)));
        //verify that the property indeed changed by fetching them again
        props = saClient.getProperties(urn);
        assertNotNull(props);
        assertEquals(props.toString(), 6, props.size());
        assertThat(props, allOf(hasEntry("Parameters", (Object)paramValue),
                hasEntry("Name", "HelloWorld"),
                hasEntry("Language", Language.RUBY.toString()),
                hasEntry(STRAT_PROP_ROUTING_ORDERS, true),
                hasEntry("Status", "STOPPED"),
                hasEntry("OutputDestination", RECEIVER_URN.parent().getValue())));

        props.clear();
        props.put(STRAT_PROP_ROUTING_ORDERS, BigDecimal.ONE);
        props = saClient.setProperties(urn, props);
        assertNotNull(props);
        assertEquals(1, props.size());
        assertThat(props, Matchers.hasKey(STRAT_PROP_ROUTING_ORDERS));
        Object err = props.get(STRAT_PROP_ROUTING_ORDERS);
        assertThat(err, Matchers.instanceOf(RemoteProperties.class));
        RemoteProperties prop = (RemoteProperties)err;
        //Verify the we had the expected failure. 
        assertThat(prop.getServerString(),
                Matchers.containsString(InvalidAttributeValueException.class.getName()));

        //delete strategy
        saClient.delete(urn);
        //verify it's not reported any more
        instances = saClient.getInstances(STRATEGY_PROVIDER_URN);
        assertTrue(instances.toString(), instances.isEmpty());
    }

    private void verifyNullURNFailure(final WSOpFailure inTest) throws Exception {
        verifyNestedFailure(Messages.CANNOT_PROCESS_NULL_URN, inTest); }

    private void verifyNestedFailure(I18NBoundMessage inMessage,
                                     WSOpFailure inTest)
            throws Exception {
        assertEquals(inMessage, ((I18NException) inTest.getException().
                getCause()).getI18NBoundMessage());

    }

    private static SAClient createClient() throws ConnectionException {
        return sSAClient = SAClientFactory.getInstance().create(
                new SAClientParameters(DEFAULT_CREDENTIAL,
                        DEFAULT_CREDENTIAL.toCharArray(),
                        RECEIVER_URL, WS_HOST, WS_PORT));
    }

    /**
     * A class to test WS operation failure
     */
    private static abstract class WSOpFailure
            extends ExpectedFailure<ConnectionException> {
        protected WSOpFailure() throws Exception {
            super(org.marketcetera.saclient.Messages.ERROR_WS_OPERATION);
        }
    }

    private static MockServer sServer;
    private static final String DEFAULT_CREDENTIAL = "DrNo";
    private static final String WS_HOST = "localhost";
    private static final int WS_PORT = 9001;
    private static final int JMS_PORT = 61617;
    private static final String RECEIVER_URL = "tcp://" + WS_HOST + ":" + JMS_PORT;
    private static final File TEST_STRATEGY = new File("src/test/sample_data/test_strategy.rb");
    private static volatile SAClient sSAClient;
    private static final ModuleURN RECEIVER_URN = new ModuleURN("metc:remote:receiver:single");
    private static final ModuleURN STRATEGY_PROVIDER_URN = new ModuleURN("metc:strategy:system");
    private static final String STRAT_PROP_ROUTING_ORDERS = "RoutingOrdersToORS";
}
