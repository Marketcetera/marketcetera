package org.marketcetera.core.position;

import static org.junit.Assert.assertThat;
import static org.marketcetera.core.position.impl.BigDecimalMatchers.comparesEqualTo;

import java.math.BigDecimal;

import org.junit.Test;

import com.google.common.collect.ImmutableMap;

/* $License$ */

/**
 * Tests {@link ImmutablePositionSupport}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id: ImmutablePositionSupportTest.java 10534 2009-04-25 00:38:38Z
 *          klim $
 * @since 1.5.0
 */
public class ImmutablePositionSupportTest {

    @Test
    public void testGetIncomingPositionFor() {
        ImmutablePositionSupport fixture = new ImmutablePositionSupport(
                ImmutableMap.of(PositionKeyFactory.createEquityKey("abc",
                        "abc", "abc"), BigDecimal.ZERO, PositionKeyFactory
                        .createEquityKey("abc", "abc", null), BigDecimal.TEN));
        assertThat(fixture.getIncomingPositionFor(PositionKeyFactory
                .createEquityKey("abc", "abc", "abc")), comparesEqualTo(0));
        assertThat(fixture.getIncomingPositionFor(PositionKeyFactory
                .createEquityKey("abc", "abc", null)), comparesEqualTo(10));
        // if the key doesn't exist the result should be zero, not null
        assertThat(fixture.getIncomingPositionFor(PositionKeyFactory
                .createEquityKey("xyz", "abc", "abc")), comparesEqualTo(0));
    }

}
