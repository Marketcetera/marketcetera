package org.marketcetera.modules.cep.esper;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.marketcetera.core.ClassVersion;
import org.marketcetera.module.BlockingSinkDataListener;
import org.marketcetera.module.CopierModuleFactory;
import org.marketcetera.module.DataFlowID;
import org.marketcetera.module.DataRequest;
import org.marketcetera.module.ModuleManager;
import org.marketcetera.module.ModuleTestBase;
import org.marketcetera.module.ModuleURN;
import org.marketcetera.trade.Factory;

/**
 * Test having a receiver that reposts events back into Esper
 *
 * @author toli kuznets
 * @version $Id$
 */

@ClassVersion("$Id$")
public class SelfRepostingEventsTest extends ModuleTestBase {
    protected ModuleManager sManager;
    protected static BlockingSinkDataListener sSink;
    protected static Factory sFactory;

    private static ModuleURN TEST_URN = new ModuleURN(CEPEsperFactory.PROVIDER_URN, "toli");
    @Before
    public void before() throws Exception {
        sSink = new BlockingSinkDataListener();
        sManager = new ModuleManager();
        sManager.init();
        sManager.addSinkListener(sSink);
    }

    @After
    public void after() throws Exception {
        sManager.removeSinkListener(sSink);
        sManager.stop();
    }

    /**
     * Setup a data flow where 1st esper instance feeds data into the same Esper instance multiple times
     * So feed it a custom class, then grab an individual getter method on that until we get something in the end
     * and verify that comes through correctly.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test(timeout = 2*60*1000)
    public void testSelfRepostingEvents() throws Exception {
        DataFlowID flow = sManager.createDataFlow(new DataRequest[] {
                // Copier -> Esper
                new DataRequest(CopierModuleFactory.INSTANCE_URN, new Object[] {
                        new ClassB(new ClassA("vasya"))
                }),
                new DataRequest(TEST_URN, "select * from "+ClassB.class.getName()),
                new DataRequest(TEST_URN, "select attribA from "+ClassB.class.getName()),
                new DataRequest(TEST_URN, "select strAttrib from "+ClassA.class.getName())
        });

        assertEquals("vasya", sSink.getNextData());

        sManager.cancel(flow);
    }

    public static final class ClassA {
        private String strAttrib;

        public ClassA(String strAttrib) {
            this.strAttrib = strAttrib;
        }

        public String getStrAttrib() {
            return strAttrib;
        }
    }

    public static final class ClassB {
        private ClassA attribA;

        public ClassB(ClassA attribA) {
            this.attribA = attribA;
        }

        public ClassA getAttribA() {
            return attribA;
        }
    }

}