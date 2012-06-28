package org.marketcetera.trade;

import static org.marketcetera.trade.SecurityType.CommonStock;
import static org.marketcetera.trade.SecurityType.Option;
import static org.marketcetera.trade.SecurityType.Unknown;
import static org.marketcetera.trade.SecurityType.values;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.marketcetera.core.Pair;
import org.marketcetera.core.attributes.ClassVersion;

/* $License$ */
/**
 * Tests {@link org.marketcetera.trade.SecurityType}
 *
 * @author anshul@marketcetera.com
 * @version $Id: SecurityTypeTest.java 16063 2012-01-31 18:21:55Z colin $
 * @since 1.0.0
 */
@ClassVersion("$Id: SecurityTypeTest.java 16063 2012-01-31 18:21:55Z colin $") //$NON-NLS-1$
public class SecurityTypeTest extends FIXEnumTestBase <String, SecurityType>{
    @Override
    protected SecurityType getInstanceForFIXValue(String inFIXValue) {
        return SecurityType.getInstanceForFIXValue(inFIXValue);
    }

    @Override
    protected String getFIXValue(SecurityType e) {
        return e.getFIXValue();
    }

    @Override
    protected SecurityType unknownInstance() {
        return Unknown;
    }

    @Override
    protected List<SecurityType> getValues() {
        return Arrays.asList(values());
    }

    @Override
    protected List<Pair<SecurityType,String>> knownValues()
    {
        List<Pair<SecurityType,String>> values = new ArrayList<Pair<SecurityType,String>>();
        values.add(new Pair<SecurityType, String>(CommonStock,
                                                  quickfix.field.SecurityType.COMMON_STOCK));
        values.add(new Pair<SecurityType, String>(Option,
                                                  quickfix.field.SecurityType.OPTION));
        return values;
    }

    @Override
    protected List<String> unknownFIXValues() {
        return Arrays.asList("", null,"whatever");
    }
}