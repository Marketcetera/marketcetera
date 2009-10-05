package org.marketcetera.core.position;

import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Test;

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
public abstract class AbstractInstrumentTestBase<T extends Instrument> {

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
     * Returns a set of unique instruments in sorted order. The keys should not
     * be equal to {@link #createFixture()}.
     * 
     * @return sorted keys that are all unequal
     */
    abstract protected List<? extends Instrument> createDifferentFixtures();

    @Test
    public void testEquals() throws Exception {
        EqualsTestHelper.testEquals(createFixture(), createEqualFixture(),
                createDifferentFixtures());
    }

    @Test
    public void testHashCode() throws Exception {
        EqualsTestHelper.testHashCode(createFixture(), createEqualFixture(),
                createDifferentFixtures());
    }

    @Test
    public void testOrdering() throws Exception {
        OrderingTestHelper.testOrdering(createDifferentFixtures());
    }

    @Test
    public void underlyingNotEmpty() throws Exception {
        for (Instrument instrument : Iterables.concat(ImmutableList.of(
                createFixture(), createEqualFixture()),
                createDifferentFixtures())) {
            assertNullOrNotEmpty(instrument.getUnderlying());
        }
    }

    private void assertNullOrNotEmpty(String string) {
        if (string != null) {
            assertThat(string.length(), greaterThan(0));
        }
    }

}
