package org.marketcetera.core.position;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

/* $License$ */

/**
 * Base class for testing PositionKey implementations.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
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
     * Returns a set of unique keys in sorted order. The keys should not be
     * equal to {@link #createFixture()}.
     * 
     * @return sorted keys that are all unequal
     */
    abstract protected List<PositionKey<?>> createDifferentFixtures();

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
