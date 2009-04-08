package org.marketcetera.photon.internal.marketdata;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.marketcetera.photon.model.marketdata.MDItem;

/* $License$ */

/**
 * Test {@link Key}.
 *
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
public class KeyTest {

    /**
     * Test equals and hashCode, especially to ensure that different subclasses are not equal.
     */
    @Test
    public void testEqualsAndHashCode() {
        Key<MDItem> one = new Key<MDItem>("IBM") {
        };
        Key<MDItem> two = new Key<MDItem>("IBM") {
        };
        assertThat(one, is(one));
        assertThat(one.hashCode(), is(one.hashCode()));
        assertThat(two, is(two));
        assertThat(two.hashCode(), is(two.hashCode()));
        assertThat(one, not(two));
    }

}
