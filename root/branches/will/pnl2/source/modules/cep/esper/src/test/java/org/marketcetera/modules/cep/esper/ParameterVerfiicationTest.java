package org.marketcetera.modules.cep.esper;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.marketcetera.core.ExpectedTestFailure;
import org.marketcetera.module.*;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */
/**
 * Tests the {@link CEPEsperProcessor} module.
 *
 * @author anshul@marketcetera.com
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class ParameterVerfiicationTest extends ModuleTestBase {
    private static ModuleURN TEST_URN = new ModuleURN(CEPEsperFactory.PROVIDER_URN, "toli");
    private CEPEsperProcessor esperPr;

    @Before public void before() {
        esperPr = new CEPEsperProcessor(TEST_URN);
    }

    /**
     * Verifies failures when incorrect request parameters are provided.
     *
     * @throws Exception if there were unexpected failures.
     */
    @Test
    public void invalidRequests_null() throws Exception {
        // null request
        new ExpectedFailure<IllegalRequestParameterValue>(org.marketcetera.module.Messages.ILLEGAL_REQ_PARM_VALUE,
                TEST_URN.getValue(), null) {
            protected void run() throws Exception{
                esperPr.requestData(new DataRequest(TEST_URN, null), null);
            }
        };
    }

    // what if we just send it an empty array as a request?
    @Test
    public void invalidRequests_EmptyArray() throws Exception {
        final String[] emptyParams = new String[0];
        new ExpectedTestFailure(IllegalRequestParameterValue.class,
                org.marketcetera.module.Messages.ILLEGAL_REQ_PARM_VALUE.getText(TEST_URN.getValue(), emptyParams)) {
                    protected void execute() throws Throwable {
                        esperPr.requestData(new DataRequest(TEST_URN, emptyParams), null);
            }
        }.run();
    }

    @Test
    public void invalidRequests_InvalidReq() throws Exception {
        // invalide syntax
        final String query = "v lesu rodilas yolochka";
        esperPr.preStart();
        new ExpectedTestFailure(IllegalRequestParameterValue.class) {
                    protected void execute() throws Exception {
                        esperPr.requestData(new DataRequest(TEST_URN, query), null);
            }
        }.run();
    }

    @Test
    public void invalidRequests_Pattern() throws Exception {
        // invalid pattern syntax
        final String query = "p:v lesu rodilas yolochka";
        esperPr.preStart();
        new ExpectedTestFailure(IllegalRequestParameterValue.class) {
                    protected void execute() throws Throwable {
                        esperPr.requestData(new DataRequest(TEST_URN, query), null);
            }
        }.run();
    }

    /**
     * Verifies the provider and module infos.
     *
     * @throws Exception if there were unexpected errors
     */
    @Test
    public void providerInfo() throws Exception {
        assertProviderInfo(mManager, CEPEsperFactory.PROVIDER_URN,
                new String[]{ModuleURN.class.getName()},
                new Class[]{ModuleURN.class},
                Messages.PROVIDER_DESCRIPTION.getText(),
                true, true);
    }
    
    @Before
    public void setup() throws Exception {
        mManager = new ModuleManager();
        mManager.init();
    }
    @After
    public void cleanup() throws Exception {
        mManager.stop();
        mManager = null;
    }
    private ModuleManager mManager;
}
