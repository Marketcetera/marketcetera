package org.marketcetera.core.trade;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.marketcetera.core.Pair;
import org.marketcetera.core.attributes.ClassVersion;

import static org.marketcetera.core.trade.OptionType.*;

/* $License$ */
/**
 * Tests {@link org.marketcetera.core.trade.OptionType}
 *
 * @author anshul@marketcetera.com
 * @version $Id: OptionTypeTest.java 16063 2012-01-31 18:21:55Z colin $
 * @since 2.0.0
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
    protected List<Pair<OptionType, Integer>> knownValues()
    {
        List<Pair<OptionType,Integer>> values = new ArrayList<Pair<OptionType,Integer>>();
        values.add(new Pair<OptionType,Integer>(Put,
                                                quickfix.field.PutOrCall.PUT));
        values.add(new Pair<OptionType,Integer>(Call,
                                                quickfix.field.PutOrCall.CALL));
        return values;
    }

    @Override
    protected List<Integer> unknownFIXValues() {
        return Arrays.asList(-1, 10, 1001);
    }
}