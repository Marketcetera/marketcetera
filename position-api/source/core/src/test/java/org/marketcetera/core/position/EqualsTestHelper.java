package org.marketcetera.core.position;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.List;

import com.google.common.collect.Lists;

/* $License$ */

/**
 * Helper for testing {@link Object#equals(Object)} and
 * {@link Object#hashCode()} behavior.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
public class EqualsTestHelper {

    /**
     * Verifies {@link Object#equals(Object)}.
     * 
     * @param object
     *            an object
     * @param equalObject
     *            another object equal to object
     * @param differentObjects
     *            a list of objects not equal to object
     */
    public static void testEquals(Object object, Object equalObject,
            List<? extends Object> differentObjects) {
        assertTrue(object.equals(object));
        assertFalse(object.equals(null));
        assertFalse(object.equals(new Object()));
        assertThat(object, is(equalObject));
        assertThat(equalObject, is(object));
        List<?> others = Lists.newArrayList(differentObjects);
        for (Object other : others) {
            assertThat(object, not(other));
            assertThat(other, not(object));
            assertThat(equalObject, not(other));
            assertThat(other, not(equalObject));
            for (Object other2 : others) {
                if (other != other2) {
                    assertThat(other, not(other2));
                }
            }
        }
    }

    /**
     * Verifies {@link Object#hashCode()}.
     * 
     * @param object
     *            an object
     * @param equalObject
     *            another object equal to object
     * @param otherObjects
     *            a list of other objects to test
     */
    public static void testHashCode(Object object, Object equalObject,
            List<? extends Object> otherObjects) throws Exception {
        assertThat(object.hashCode(), is(object.hashCode()));
        assertThat(equalObject.hashCode(), is(equalObject.hashCode()));
        assertThat(object.hashCode(), is(equalObject.hashCode()));
        for (Object other : otherObjects) {
            assertThat(other.hashCode(), is(other.hashCode()));
        }
    }
}
