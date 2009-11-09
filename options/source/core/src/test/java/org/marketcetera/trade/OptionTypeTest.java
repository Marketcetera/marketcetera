package org.marketcetera.trade;

import org.marketcetera.util.misc.ClassVersion;
import static org.marketcetera.trade.OptionType.*;
import org.marketcetera.core.Pair;

import java.util.Arrays;
import java.util.List;

/* $License$ */
/**
 * Tests {@link OptionType}
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id")
public class OptionTypeTest extends FIXEnumTestBase<Integer, OptionType> {
    @Override
    protected OptionType getInstanceForFIXValue(Integer inFIXValue) {
        return OptionType.getInstanceForFIXValue(inFIXValue);
    }

    @Override
    protected Integer getFIXValue(OptionType inValue) {
        return inValue.getFIXValue();
    }

    @Override
    protected OptionType unknownInstance() {
        return Unknown;
    }

    @Override
    protected List<OptionType> getValues() {
        return Arrays.asList(values());
    }

    @Override
    protected List<Pair<OptionType, Integer>> knownValues() {
        return Arrays.asList(
                new Pair<OptionType, Integer>(Put,
                        quickfix.field.PutOrCall.PUT),
                new Pair<OptionType, Integer>(Call,
                        quickfix.field.PutOrCall.CALL)
        );
    }

    @Override
    protected List<Integer> unknownFIXValues() {
        return Arrays.asList(-1, 10, 1001);
    }
}