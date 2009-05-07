package org.marketcetera.trade;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.core.Pair;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.Set;
import java.util.HashSet;
import java.util.List;

/* $License$ */
/**
 * Test Base class for enums based on FIX enum values.
 *
 * @param <F> The fix parameter value type.
 * @param <E> The Enum type.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public abstract class FIXEnumTestBase<F,E extends Enum<E>> {
    /**
     * Verifies all enum values.
     */
    @Test
    public final void verifyKnownValues() {
        Set<E> set = new HashSet<E>();
        for(Pair<E,F> p:knownValues()) {
            checkValue(p.getFirstMember(), p.getSecondMember(), set);
        }
        set.removeAll(getValues());
        //Verify that we are verifying all the defined values.
        assertTrue(set.toString(), set.isEmpty());
    }

    /**
     * Verifies unknown enum values.
     */
    @Test
    public final void verifyUnknownValues() {
        for(F u:unknownFIXValues()) {
            assertSame(unknownInstance(), getInstanceForFIXValue(u));
        }
    }
    protected final void checkValue(E inEnum,
                                    F inFIXValue,
                                    Set<E> inValuesSet) {
        assertEquals(inFIXValue, getFIXValue(inEnum));
        assertSame(inEnum, getInstanceForFIXValue(inFIXValue));
        inValuesSet.add(inEnum);
    }

    /**
     * The set of known enum values. Does not include unknown value.
     *
     * @return the set of known enum values.
     */
    protected abstract List<Pair<E,F>> knownValues();

    /**
     * The list of invalid / unknown FIX values.
     *
     * @return the list of invalid / unknown FIX values.
     */
    protected abstract List<F> unknownFIXValues();

    /**
     * Returns enum instance for the supplied FIX value.
     *
     * @param inFIXValue the FIX value.
     *
     * @return the enum instance.
     */
    protected abstract E getInstanceForFIXValue(F inFIXValue);

    /**
     * Gets the FIX value from the supplied enum instance.
     *
     * @param e the enum instance.
     *
     * @return the FIX value.
     */
    protected abstract F getFIXValue(E e);

    /**
     * The unknown value instance.
     *
     * @return the unknown value instance.
     */
    protected abstract E unknownInstance();

    /**
     * The list of all values of the Enum.
     *
     * @return the list of all values of the Enum.
     */
    protected abstract List<E> getValues();
}