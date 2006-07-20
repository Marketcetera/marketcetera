package org.marketcetera.symbology;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.io.IOException;

/**
 * @author Graham Miller
 * @version $Id$
 */
public class PropertiesExchangeMapTest extends TestCase{
    public PropertiesExchangeMapTest(String inName) {
        super(inName);
    }

    public static Test suite() {
        return new TestSuite(PropertiesExchangeMapTest.class);
    }

    public void testHyperfeed() throws IOException {
        PropertiesExchangeMap map = new PropertiesExchangeMap("hyperfeed-exchanges.properties");
        assertEquals("XASE", map.getExchange("A ").getMarketIdentifierCode());
        assertEquals("XBOS", map.getExchange("B ").getMarketIdentifierCode());
        assertEquals("XCIS", map.getExchange("C ").getMarketIdentifierCode());
        assertEquals("XISX", map.getExchange("I ").getMarketIdentifierCode());
        assertEquals("XCHI", map.getExchange("M ").getMarketIdentifierCode());
        assertEquals("XNYS", map.getExchange("N ").getMarketIdentifierCode());
        assertEquals("XARC", map.getExchange("P ").getMarketIdentifierCode());
        assertEquals("XNAS", map.getExchange("Q ").getMarketIdentifierCode());
        assertEquals("XPHL", map.getExchange("X ").getMarketIdentifierCode());
    }


    public void testBasic() throws IOException {
        PropertiesExchangeMap map = new PropertiesExchangeMap("basic-exchanges.properties");
        assertEquals("XASE", map.getExchange("A").getMarketIdentifierCode());
        assertEquals("XBOS", map.getExchange("B").getMarketIdentifierCode());
        assertEquals("XISX", map.getExchange("IO").getMarketIdentifierCode());
        assertEquals("XNYS", map.getExchange("N").getMarketIdentifierCode());
        assertEquals("XARC", map.getExchange("PO").getMarketIdentifierCode());
        assertEquals("XNAS", map.getExchange("Q").getMarketIdentifierCode());
    }

}
