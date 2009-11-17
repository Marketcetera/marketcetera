package org.marketcetera.core.position;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Test;
import org.marketcetera.util.test.EqualityAssert;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

/* $License$ */

/**
 * Base class for testing PositionKey implementations.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 2.0.0
 */
public abstract class PositionKeyTestBase {

    /**
     * Returns a key.
     * 
     * @return a key
     */
    abstract protected PositionKey<?> createFixture();

    /**
     * Returns another key that is equal to that returned by
     * {@link #createFixture()}.
     * 
     * @return an equal key
     */
    abstract protected PositionKey<?> createEqualFixture();

    /**
     * Returns a list of unique instruments that are not equal to
     * {@link #createFixture()}.
     * 
     * @return a list of unequal keys
     */
    abstract protected List<PositionKey<?>> createDifferentFixtures();

    @Test
    public void testEqualsAndHashCode() throws Exception {
        EqualityAssert.assertEquality(createFixture(), createEqualFixture(),
                createDifferentFixtures().toArray());
    }

    @Test
    public void instrumentNotNull() throws Exception {
        for (PositionKey<?> key : Iterables.concat(ImmutableList.of(
                createFixture(), createEqualFixture()),
                createDifferentFixtures())) {
            assertThat(key.getInstrument(), not(nullValue()));
        }
    }

    @Test
    public void accountNotEmpty() throws Exception {
        for (PositionKey<?> key : Iterables.concat(ImmutableList.of(
                createFixture(), createEqualFixture()),
                createDifferentFixtures())) {
            assertNullOrNotEmpty(key.getAccount());
        }
    }

    @Test
    public void traderIdNotEmpty() throws Exception {
        for (PositionKey<?> key : Iterables.concat(ImmutableList.of(
                createFixture(), createEqualFixture()),
                createDifferentFixtures())) {
            assertNullOrNotEmpty(key.getTraderId());
        }
    }

    private void assertNullOrNotEmpty(String string) {
        if (string != null) {
            assertThat(string.length(), greaterThan(0));
        }
    }
}
