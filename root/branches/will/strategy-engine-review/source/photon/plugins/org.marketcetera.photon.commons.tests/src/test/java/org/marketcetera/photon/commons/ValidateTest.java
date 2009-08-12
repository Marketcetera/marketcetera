package org.marketcetera.photon.commons;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.junit.Test;
import org.marketcetera.photon.test.ExpectedIllegalArgumentException;

/* $License$ */

/**
 * Tests {@link Validate}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
public class ValidateTest {

    @Test
    public void testNotNull() throws Exception {
        new ImproperUsageFailure() {
            @Override
            protected void run() throws Exception {
                Validate.notNull(null, null);
            }
        };
        new ImproperUsageFailure() {
            @Override
            protected void run() throws Exception {
                Validate.notNull("ABC", "ABC", "ABC");
            }
        };
        new ExpectedNullArgumentFailure("a") {
            @Override
            protected void run() throws Exception {
                Validate.notNull(null, "a");
            }
        };
        new ExpectedNullArgumentFailure("b") {
            @Override
            protected void run() throws Exception {
                Validate.notNull("a", "a", null, "b");
            }
        };
    }
    
    @Test
    public void testNoNullElements() throws Exception {
        new ImproperUsageFailure() {
            @Override
            protected void run() throws Exception {
                Validate.noNullElements((Object[]) null, null);
            }
        };
        new ExpectedNullArgumentFailure("s") {
            @Override
            protected void run() throws Exception {
                Validate.noNullElements((Object[]) null, "s");
            }
        };
        new ExpectedNullElementFailure("o") {
            @Override
            protected void run() throws Exception {
                Validate.noNullElements(new Object[] { null }, "o");
            }
        };
        new ExpectedNullElementFailure("s") {
            @Override
            protected void run() throws Exception {
                Validate.noNullElements(new String[] { "xyz", null }, "s");
            }
        };
        new ImproperUsageFailure() {
            @Override
            protected void run() throws Exception {
                Validate.noNullElements((Collection<?>) null, null);
            }
        };
        new ExpectedNullArgumentFailure("c") {
            @Override
            protected void run() throws Exception {
                Validate.noNullElements((Collection<?>) null, "c");
            }
        };
        new ExpectedNullElementFailure("o") {
            @Override
            protected void run() throws Exception {
                Validate.noNullElements(Collections.singleton(null), "o");
            }
        };
        new ExpectedNullElementFailure("s") {
            @Override
            protected void run() throws Exception {
                Validate.noNullElements(Arrays.asList("x", null), "s");
            }
        };
    }
    
    @Test
    public void testNotEmpty() throws Exception {
        new ImproperUsageFailure() {
            @Override
            protected void run() throws Exception {
                Validate.notEmpty((Object[]) null, null);
            }
        };
        new ExpectedNullArgumentFailure("s") {
            @Override
            protected void run() throws Exception {
                Validate.notEmpty((Object[]) null, "s");
            }
        };
        new ExpectedEmptyFailure("o") {
            @Override
            protected void run() throws Exception {
                Validate.notEmpty(new Object[] {}, "o");
            }
        };
        new ImproperUsageFailure() {
            @Override
            protected void run() throws Exception {
                Validate.notEmpty((Collection<?>) null, null);
            }
        };
        new ExpectedNullArgumentFailure("c") {
            @Override
            protected void run() throws Exception {
                Validate.notEmpty((Collection<?>) null, "c");
            }
        };
        new ExpectedEmptyFailure("o") {
            @Override
            protected void run() throws Exception {
                Validate.notEmpty(Collections.emptyList(), "o");
            }
        };
    }
    
    @Test
    public void testNonNullElements() throws Exception {
        new ImproperUsageFailure() {
            @Override
            protected void run() throws Exception {
                Validate.nonNullElements((Object[]) null, null);
            }
        };
        new ExpectedNullArgumentFailure("s") {
            @Override
            protected void run() throws Exception {
                Validate.nonNullElements((Object[]) null, "s");
            }
        };
        new ExpectedEmptyFailure("o") {
            @Override
            protected void run() throws Exception {
                Validate.nonNullElements(new Object[] {}, "o");
            }
        };
        new ExpectedNullElementFailure("o") {
            @Override
            protected void run() throws Exception {
                Validate.nonNullElements(new Object[] { null }, "o");
            }
        };
        new ImproperUsageFailure() {
            @Override
            protected void run() throws Exception {
                Validate.nonNullElements((Collection<?>) null, null);
            }
        };
        new ExpectedNullArgumentFailure("c") {
            @Override
            protected void run() throws Exception {
                Validate.nonNullElements((Collection<?>) null, "c");
            }
        };
        new ExpectedEmptyFailure("o") {
            @Override
            protected void run() throws Exception {
                Validate.nonNullElements(Collections.emptyList(), "o");
            }
        };
        new ExpectedNullElementFailure("o") {
            @Override
            protected void run() throws Exception {
                Validate.nonNullElements(Collections.singleton(null), "o");
            }
        };
    }

    /**
     * Utility to test null argument checks.
     */
    public abstract static class ExpectedNullArgumentFailure extends
            ExpectedIllegalArgumentException {

        /**
         * Constructor.
         * 
         * @param description
         *            the argument being checked
         * 
         * @throws Exception
         *             if there was an unexpected failure
         */
        public ExpectedNullArgumentFailure(String description)
                throws Exception {
            super(MessageFormat.format("''{0}'' must not be null", description));
        }
    }

    /**
     * Utility to test null array/collection element checks.
     */
    public abstract static class ExpectedNullElementFailure extends
            ExpectedIllegalArgumentException {

        /**
         * Constructor.
         * 
         * @param description
         *            the argument being checked
         * 
         * @throws Exception
         *             if there was an unexpected failure
         */
        public ExpectedNullElementFailure(String description)
                throws Exception {
            super(MessageFormat.format("''{0}'' must not have null elements",
                    description));
        }
    }

    /**
     * Utility to test empty array/collection checks.
     */
    public abstract static class ExpectedEmptyFailure extends
            ExpectedIllegalArgumentException {

        /**
         * Constructor.
         * 
         * @param description
         *            the argument being checked
         * 
         * @throws Exception
         *             if there was an unexpected failure
         */
        public ExpectedEmptyFailure(String description)
                throws Exception {
            super(MessageFormat.format("''{0}'' must not be empty",
                    description));
        }
    }

    /**
     * Utility to test implementation details.
     */
    private abstract static class ImproperUsageFailure extends
            ExpectedIllegalArgumentException {

        public ImproperUsageFailure() throws Exception {
            super("improper usage of Validate.notNull");
        }
    }

}
