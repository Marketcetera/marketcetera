package org.marketcetera.trade;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.test.EqualityAssert;
import org.marketcetera.util.test.SerializableAssert;
import org.marketcetera.trade.MSymbol;
import org.marketcetera.trade.SecurityType;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

/* $License$ */
/**
 * Tests {@link MSymbol}
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class MSymbolTest {
    @Test
    public void basic() {

        MSymbol symbol = new MSymbol("IBM");
        EqualityAssert.assertEquality(symbol, new MSymbol("IBM"),
                new MSymbol("IMB"), 
                new MSymbol("IBM", SecurityType.Option));
        assertSymbol(symbol, "IBM", null);

        symbol = new MSymbol("IBM", SecurityType.Option);
        EqualityAssert.assertEquality(symbol, new MSymbol("IBM", SecurityType.Option),
                new MSymbol("IMB"), new MSymbol("IBM"),
                new MSymbol("IBM", SecurityType.CommonStock));
        assertSymbol(symbol, "IBM", SecurityType.Option);
    }
    static void assertSymbol(MSymbol inSymbol,
                             String inFullSymbol,
                             SecurityType inSecurityType) {
        assertEquals(inFullSymbol, inSymbol.getFullSymbol());
        assertEquals(inSecurityType, inSymbol.getSecurityType());
        SerializableAssert.assertSerializable(inSymbol);
    }
}
