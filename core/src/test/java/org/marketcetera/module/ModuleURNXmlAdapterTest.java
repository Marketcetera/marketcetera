package org.marketcetera.module;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.marketcetera.core.ExpectedFailure;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */
/**
 * Tests {@link ModuleURNXmlAdapter}.
 *
 * @author anshul@marketcetera.com
 * @version $Id: ModuleURNXmlAdapterTest.java 82384 2012-07-20 19:09:59Z colin $
 * @since 2.0.0
 */
@ClassVersion("$Id: ModuleURNXmlAdapterTest.java 82384 2012-07-20 19:09:59Z colin $")
public class ModuleURNXmlAdapterTest {
    private static final ModuleURNXmlAdapter sAdapter = new ModuleURNXmlAdapter();

    @Test
    public void roundtrip() throws Exception {
        roundtrip("metc:what");
        roundtrip("metc:what:why");
        roundtrip("metc:what:why:where");
    }
    @Test
    public void failures() throws Exception {
        //marshalling failures
        new ExpectedFailure<NullPointerException>(){
            @Override
            protected void run() throws Exception {
                sAdapter.marshal(null);
            }
        };
        //unmarshalling failures
        new ExpectedFailure<IllegalArgumentException>(Messages.EMPTY_URN.getText("")){
            @Override
            protected void run() throws Exception {
                sAdapter.unmarshal("");
            }
        };
    }
    
    private static void roundtrip(String inString) throws Exception {
        assertEquals(new ModuleURN(inString), sAdapter.unmarshal(inString));
        assertEquals(inString, sAdapter.marshal(new ModuleURN(inString)));
    }
}
