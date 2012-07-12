package org.marketcetera.trade;

import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Test;
import org.marketcetera.trade.Instrument;
import org.marketcetera.util.test.EqualityAssert;
import org.marketcetera.util.test.SerializableAssert;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

/* $License$ */

/**
 * Base class for testing {@link Instrument} implementations.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 2.0.0
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

    /**
     * Returns the expected security type for the instruments.
     * 
     * @return the expected security type.
     */
    abstract protected SecurityType getSecurityType();

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
    
    @Test 
    public void serialization() throws Exception {
        for (Instrument instrument : Iterables.concat(ImmutableList.of(
                createFixture(), createEqualFixture()),
                createDifferentFixtures())) {
            SerializableAssert.assertSerializable(instrument);
        }
    }
    
    @Test 
    public void securityType() throws Exception {
        for (Instrument instrument : Iterables.concat(ImmutableList.of(
                createFixture(), createEqualFixture()),
                createDifferentFixtures())) {
            assertEquals(getSecurityType(), instrument.getSecurityType());
        }
    }

	private void assertNullOrNotEmpty(String string) {
        if (string != null) {
            assertThat(string.length(), greaterThan(0));
        }
    }

}
