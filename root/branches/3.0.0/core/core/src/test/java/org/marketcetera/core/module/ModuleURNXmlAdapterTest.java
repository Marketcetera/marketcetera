package org.marketcetera.core.module;

import org.junit.Test;
import org.marketcetera.core.ExpectedFailure;
import org.marketcetera.core.attributes.ClassVersion;

import static org.junit.Assert.assertEquals;

/* $License$ */
/**
 * Tests {@link org.marketcetera.core.module.ModuleURNXmlAdapter}.
 *
 * @author anshul@marketcetera.com
 * @version $Id: ModuleURNXmlAdapterTest.java 82330 2012-04-10 16:29:13Z colin $
 * @since 2.0.0
 */
@ClassVersion("$Id: ModuleURNXmlAdapterTest.java 82330 2012-04-10 16:29:13Z colin $")
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
