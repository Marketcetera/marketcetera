package org.marketcetera.core.trade;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.marketcetera.core.Pair;

import static org.marketcetera.core.trade.PositionEffect.*;

/* $License$ */
/**
 * Tests {@link org.marketcetera.core.trade.PositionEffect}
 *
 * @version $Id: PositionEffectTest.java 16063 2012-01-31 18:21:55Z colin $
 * @since 1.0.0
 */
public class PositionEffectTest extends FIXCharEnumTestBase<PositionEffect> {

    @Override
    protected PositionEffect getInstanceForFIXValue(Character inFIXValue) {
        return PositionEffect.getInstanceForFIXValue(inFIXValue);
    }

    @Override
    protected Character getFIXValue(PositionEffect e) {
        return e.getFIXValue();
    }

    @Override
    protected PositionEffect unknownInstance() {
        return Unknown;
    }

    @Override
    protected List<PositionEffect> getValues() {
        return Arrays.asList(values());
    }
    @Override
    protected List<Pair<PositionEffect,Character>> knownValues()
    {
        List<Pair<PositionEffect,Character>> values = new ArrayList<Pair<PositionEffect,Character>>();
        values.add(new Pair<PositionEffect, Character>(Open, quickfix.field.PositionEffect.OPEN));
        values.add(new Pair<PositionEffect, Character>(Close, quickfix.field.PositionEffect.CLOSE));
        return values;
    }
}