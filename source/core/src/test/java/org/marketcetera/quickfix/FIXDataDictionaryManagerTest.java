package org.marketcetera.quickfix;

import junit.framework.Test;
import junit.framework.TestCase;
import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.MarketceteraTestSuite;
import quickfix.field.ExecType;
import quickfix.field.Side;

/**
 * @author Toli Kuznets
 * @version $Id$
 */
@ClassVersion("$Id$")
public class FIXDataDictionaryManagerTest extends TestCase {
    public FIXDataDictionaryManagerTest(String name) {
        super(name);
    }

    public static Test suite()
    {
        return new MarketceteraTestSuite(FIXDataDictionaryManagerTest.class);
    }

    public void testDictionaryInit() throws Exception {
        FIXDataDictionaryManager.setDataDictionary(FIXVersion.FIX42.getDataDictionaryURL());

        // lookup some fields
        assertEquals("OrderID", FIXDataDictionaryManager.getHumanFieldName(37));
        assertEquals("OrderID", FIXDataDictionaryManager.getHumanFieldName(37));

        FIXDataDictionaryManager.setDataDictionary(FIXVersion.FIX44.getDataDictionaryURL());
        assertEquals("OrderID", FIXDataDictionaryManager.getHumanFieldName(37));
        assertEquals("CollAction", FIXDataDictionaryManager.getHumanFieldName(944));

        assertNull(FIXDataDictionaryManager.getHumanFieldName(-32));
    }

    public void testValueGetting() throws Exception {
        FIXDataDictionaryManager.setDataDictionary(FIXVersion.FIX42.getDataDictionaryURL());

        assertEquals("BUY", FIXDataDictionaryManager.getHumanFieldValue(Side.FIELD, ""+Side.BUY));
        assertEquals("SELL", FIXDataDictionaryManager.getHumanFieldValue(Side.FIELD, ""+Side.SELL));
        assertEquals("SELL SHORT", FIXDataDictionaryManager.getHumanFieldValue(Side.FIELD, ""+Side.SELL_SHORT));
        assertEquals("SELL SHORT EXEMPT", FIXDataDictionaryManager.getHumanFieldValue(Side.FIELD, ""+Side.SELL_SHORT_EXEMPT));

        assertEquals("PARTIAL FILL", FIXDataDictionaryManager.getHumanFieldValue(ExecType.FIELD, ""+ExecType.PARTIAL_FILL));
    }

    public void testWhenValueNotFound() throws Exception {
        assertEquals("non-existing value", null, FIXDataDictionaryManager.getHumanFieldValue(Side.FIELD, "37"));
    }


    public void testGetDataDictionary() throws Exception {
        FIXDataDictionaryManager.setDataDictionary(FIXVersion.FIX41.getDataDictionaryURL());
        assertEquals(FIXDataDictionaryManager.FIX_4_1_BEGIN_STRING, FIXDataDictionaryManager.getDictionary().getVersion());
        FIXDataDictionaryManager.setDataDictionary(FIXVersion.FIX42.getDataDictionaryURL());
        assertEquals(FIXDataDictionaryManager.FIX_4_2_BEGIN_STRING, FIXDataDictionaryManager.getDictionary().getVersion());
        FIXDataDictionaryManager.setDataDictionary(FIXVersion.FIX43.getDataDictionaryURL());
        assertEquals(FIXDataDictionaryManager.FIX_4_3_BEGIN_STRING, FIXDataDictionaryManager.getDictionary().getVersion());
        FIXDataDictionaryManager.setDataDictionary(FIXVersion.FIX44.getDataDictionaryURL());
        assertEquals(FIXDataDictionaryManager.FIX_4_4_BEGIN_STRING, FIXDataDictionaryManager.getDictionary().getVersion());
    }
}
