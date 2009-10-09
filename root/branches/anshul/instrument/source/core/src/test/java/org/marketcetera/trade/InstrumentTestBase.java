package org.marketcetera.trade;

import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Test;
import org.marketcetera.trade.Instrument;
import org.marketcetera.util.test.EqualityAssert;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

/* $License$ */

/**
 * Base class for testing {@link Instrument} implementations.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
public abstract class InstrumentTestBase<T extends Instrument> {

    /**
     * Returns an instance of the instrument.
     * 
     * @return an instance of the instrument.
     */
    abstract protected T createFixture();

    /**
     * Returns another instrument that is equal to that returned by
     * {@link #createFixture()}.
     * 
     * @return an equal key
     */
    abstract protected T createEqualFixture();

    /**
     * Returns a list of unique instruments that are not equal to
     * {@link #createFixture()}.
     * 
     * @return a list of unequal items
     */
    abstract protected List<T> createDifferentFixtures();

    @Test
    public void testEqualsAndHashCode() throws Exception {
        EqualityAssert.assertEquality(createFixture(), createEqualFixture(),
                createDifferentFixtures().toArray());
    }

    @Test
    public void symbolNotEmpty() throws Exception {
        for (Instrument instrument : Iterables.concat(ImmutableList.of(
                createFixture(), createEqualFixture()),
                createDifferentFixtures())) {
            assertNullOrNotEmpty(instrument.getSymbol());
        }
    }

    private void assertNullOrNotEmpty(String string) {
        if (string != null) {
            assertThat(string.length(), greaterThan(0));
        }
    }

}
