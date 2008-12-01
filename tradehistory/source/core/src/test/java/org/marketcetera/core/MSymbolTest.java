package org.marketcetera.core;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.test.EqualityAssert;
import org.marketcetera.util.test.SerializableAssert;
import org.marketcetera.symbology.SymbolScheme;
import org.marketcetera.trade.SecurityType;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

/* $License$ */
/**
 * Tests {@link MSymbol}
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class MSymbolTest {
    @Test
    public void basic() {

        MSymbol symbol = new MSymbol("IBM");
        EqualityAssert.assertEquality(symbol, new MSymbol("IBM"),
                new MSymbol("IMB"), new MSymbol("IBM", SymbolScheme.BLOOMBERG),
                new MSymbol("IBM", SecurityType.Option),
                new MSymbol("IBM", SymbolScheme.BASIC, SecurityType.Option));
        assertSymbol(symbol, "IBM", SymbolScheme.BASIC, null);

        symbol = new MSymbol("IBM",SymbolScheme.BLOOMBERG);
        EqualityAssert.assertEquality(symbol, new MSymbol("IBM", SymbolScheme.BLOOMBERG),
                new MSymbol("IMB"), new MSymbol("IBM", SymbolScheme.BASIC),
                new MSymbol("IBM", SecurityType.Option),
                new MSymbol("IBM", SymbolScheme.BLOOMBERG, SecurityType.Option));
        assertSymbol(symbol, "IBM", SymbolScheme.BLOOMBERG, null);

        symbol = new MSymbol("IBM", SecurityType.Option);
        EqualityAssert.assertEquality(symbol, new MSymbol("IBM", SecurityType.Option),
                new MSymbol("IMB"), new MSymbol("IBM", SymbolScheme.BASIC),
                new MSymbol("IBM", SecurityType.CommonStock),
                new MSymbol("IBM", SymbolScheme.BLOOMBERG, SecurityType.Option));
        assertSymbol(symbol, "IBM", SymbolScheme.BASIC, SecurityType.Option);

        symbol = new MSymbol("IBM", SymbolScheme.BLOOMBERG, SecurityType.CommonStock);
        EqualityAssert.assertEquality(symbol, new MSymbol("IBM",
                SymbolScheme.BLOOMBERG, SecurityType.CommonStock),
                new MSymbol("IMB"), new MSymbol("IBM", SymbolScheme.BLOOMBERG),
                new MSymbol("IBM", SecurityType.CommonStock),
                new MSymbol("IBM", SymbolScheme.BLOOMBERG, SecurityType.Option),
                new MSymbol("IBM", SymbolScheme.BASIC, SecurityType.CommonStock));
        assertSymbol(symbol, "IBM", SymbolScheme.BLOOMBERG, SecurityType.CommonStock);
    }
    static void assertSymbol(MSymbol inSymbol,
                             String inFullSymbol,
                             SymbolScheme inScheme,
                             SecurityType inSecurityType) {
        assertEquals(inFullSymbol, inSymbol.getFullSymbol());
        assertEquals(inScheme, inSymbol.getScheme());
        assertEquals(inSecurityType, inSymbol.getSecurityType());
        SerializableAssert.assertSerializable(inSymbol);
    }
}
