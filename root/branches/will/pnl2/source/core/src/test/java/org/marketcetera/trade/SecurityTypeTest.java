package org.marketcetera.trade;

import org.marketcetera.util.misc.ClassVersion;
import static org.marketcetera.trade.SecurityType.*;
import org.marketcetera.core.Pair;
import java.util.Arrays;
import java.util.List;

/* $License$ */
/**
 * Tests {@link org.marketcetera.trade.SecurityType}
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$") //$NON-NLS-1$
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
    protected List<Pair<SecurityType,String>> knownValues() {
        return Arrays.asList(
                new Pair<SecurityType, String>(CommonStock,
                        quickfix.field.SecurityType.COMMON_STOCK),
                new Pair<SecurityType, String>(Option,
                        quickfix.field.SecurityType.OPTION)
        );
    }

    @Override
    protected List<String> unknownFIXValues() {
        return Arrays.asList("", null,"whatever");
    }
}