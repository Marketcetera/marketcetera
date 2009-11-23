package org.marketcetera.symbology;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.io.IOException;

import org.marketcetera.core.ClassVersion;

/**
 * @author Graham Miller
 * @version $Id$
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class PropertiesExchangeMapTest extends TestCase{
    public PropertiesExchangeMapTest(String inName) {
        super(inName);
    }

    public static Test suite() {
        return new TestSuite(PropertiesExchangeMapTest.class);
    }

    public void testHyperfeed() throws IOException {
        PropertiesExchangeMap map = new PropertiesExchangeMap("hyperfeed-exchanges.properties"); //$NON-NLS-1$
        assertEquals("XASE", map.getExchange("A ").getMarketIdentifierCode()); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("XBOS", map.getExchange("B ").getMarketIdentifierCode()); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("XCIS", map.getExchange("C ").getMarketIdentifierCode()); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("XISX", map.getExchange("I ").getMarketIdentifierCode()); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("XCHI", map.getExchange("M ").getMarketIdentifierCode()); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("XNYS", map.getExchange("N ").getMarketIdentifierCode()); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("XARC", map.getExchange("P ").getMarketIdentifierCode()); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("XNAS", map.getExchange("Q ").getMarketIdentifierCode()); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("XPHL", map.getExchange("X ").getMarketIdentifierCode()); //$NON-NLS-1$ //$NON-NLS-2$
    }


    public void testBasic() throws IOException {
        PropertiesExchangeMap map = new PropertiesExchangeMap("basic-exchanges.properties"); //$NON-NLS-1$
        assertEquals("XASE", map.getExchange("A").getMarketIdentifierCode()); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("XBOS", map.getExchange("B").getMarketIdentifierCode()); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("XISX", map.getExchange("IO").getMarketIdentifierCode()); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("XNYS", map.getExchange("N").getMarketIdentifierCode()); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("XARC", map.getExchange("PO").getMarketIdentifierCode()); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("XNAS", map.getExchange("Q").getMarketIdentifierCode()); //$NON-NLS-1$ //$NON-NLS-2$
    }

}
