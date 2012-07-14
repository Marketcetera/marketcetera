package org.marketcetera.module;

import org.marketcetera.util.misc.ClassVersion;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

/* $License$ */
/**
 * Tests {@link ModuleURNXmlAdapter}.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 2.0.0
 */
@ClassVersion("$Id$")
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
