package org.marketcetera.trade;

import org.marketcetera.util.misc.ClassVersion;
import static org.marketcetera.trade.PutOrCall.*;
import org.marketcetera.core.Pair;

import java.util.Arrays;
import java.util.List;

/* $License$ */
/**
 * Tests {@link PutOrCall}
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id")
public class PutOrCallTest extends FIXEnumTestBase<Integer, PutOrCall> {
    @Override
    protected PutOrCall getInstanceForFIXValue(Integer inFIXValue) {
        return PutOrCall.getInstanceForFIXValue(inFIXValue);
    }

    @Override
    protected Integer getFIXValue(PutOrCall e) {
        return e.getFIXValue();
    }

    @Override
    protected PutOrCall unknownInstance() {
        return Unknown;
    }

    @Override
    protected List<PutOrCall> getValues() {
        return Arrays.asList(values());
    }

    @Override
    protected List<Pair<PutOrCall, Integer>> knownValues() {
        return Arrays.asList(
                new Pair<PutOrCall, Integer>(Put,
                        quickfix.field.PutOrCall.PUT),
                new Pair<PutOrCall, Integer>(Call,
                        quickfix.field.PutOrCall.CALL)
        );
    }

    @Override
    protected List<Integer> unknownFIXValues() {
        return Arrays.asList(-1, 10, 1001);
    }
}