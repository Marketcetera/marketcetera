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
@ClassVersion("$Id$") //$NON-NLS-1$
public class FIXDataDictionaryManagerTest extends TestCase {
    public FIXDataDictionaryManagerTest(String name) {
        super(name);
    }

    public static Test suite()
    {
        return new MarketceteraTestSuite(FIXDataDictionaryManagerTest.class);
    }

    public void testDictionaryInit() throws Exception {
        FIXDataDictionary fdd = FIXDataDictionary.initializeDataDictionary(FIXVersion.FIX42.getDataDictionaryURL());

        // lookup some fields
        assertEquals("OrderID", fdd.getHumanFieldName(37)); //$NON-NLS-1$
        assertEquals("OrderID", fdd.getHumanFieldName(37)); //$NON-NLS-1$

        fdd = FIXDataDictionary.initializeDataDictionary(FIXVersion.FIX44.getDataDictionaryURL());
        assertEquals("OrderID", fdd.getHumanFieldName(37)); //$NON-NLS-1$
        assertEquals("CollAction", fdd.getHumanFieldName(944)); //$NON-NLS-1$

        assertNull(fdd.getHumanFieldName(-32));
    }

    public void testValueGetting() throws Exception {
        FIXDataDictionary fdd = FIXDataDictionary.initializeDataDictionary(FIXVersion.FIX42.getDataDictionaryURL());

        assertEquals("BUY", fdd.getHumanFieldValue(Side.FIELD, ""+Side.BUY)); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("SELL", fdd.getHumanFieldValue(Side.FIELD, ""+Side.SELL)); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("SELL SHORT", fdd.getHumanFieldValue(Side.FIELD, ""+Side.SELL_SHORT)); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("SELL SHORT EXEMPT", fdd.getHumanFieldValue(Side.FIELD, ""+Side.SELL_SHORT_EXEMPT)); //$NON-NLS-1$ //$NON-NLS-2$

        assertEquals("PARTIAL FILL", fdd.getHumanFieldValue(ExecType.FIELD, ""+ExecType.PARTIAL_FILL)); //$NON-NLS-1$ //$NON-NLS-2$
    }

    public void testWhenValueNotFound() throws Exception {
        FIXDataDictionary fdd = FIXDataDictionary.initializeDataDictionary(FIXVersion.FIX42.getDataDictionaryURL());
        assertEquals("non-existing value", null, fdd.getHumanFieldValue(Side.FIELD, "37")); //$NON-NLS-1$ //$NON-NLS-2$
    }


    public void testGetDataDictionary() throws Exception {
        assertEquals(FIXDataDictionary.FIX_4_0_BEGIN_STRING,
                FIXDataDictionary.initializeDataDictionary(FIXVersion.FIX40.getDataDictionaryURL()).getDictionary().getVersion());
        assertEquals(FIXDataDictionary.FIX_4_1_BEGIN_STRING,
                FIXDataDictionary.initializeDataDictionary(FIXVersion.FIX41.getDataDictionaryURL()).getDictionary().getVersion());
        assertEquals(FIXDataDictionary.FIX_4_2_BEGIN_STRING,
                FIXDataDictionary.initializeDataDictionary(FIXVersion.FIX42.getDataDictionaryURL()).getDictionary().getVersion());
        assertEquals(FIXDataDictionary.FIX_4_3_BEGIN_STRING,
                FIXDataDictionary.initializeDataDictionary(FIXVersion.FIX43.getDataDictionaryURL()).getDictionary().getVersion());
        assertEquals(FIXDataDictionary.FIX_4_4_BEGIN_STRING,
                FIXDataDictionary.initializeDataDictionary(FIXVersion.FIX44.getDataDictionaryURL()).getDictionary().getVersion());
        assertEquals(FIXDataDictionary.FIX_SYSTEM_BEGIN_STRING,
                FIXDataDictionary.initializeDataDictionary(FIXVersion.FIX_SYSTEM.getDataDictionaryURL()).getDictionary().getVersion());
    }
}
