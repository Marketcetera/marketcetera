package org.marketcetera.photon.commons;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.junit.Test;
import org.marketcetera.photon.test.ExpectedIllegalArgumentException;
import org.marketcetera.photon.test.PhotonTestBase;

/* $License$ */

/**
 * Tests {@link Validate}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
public class ValidateTest extends PhotonTestBase {

    @Test
    public void testNotNull() throws Exception {
        new ImproperUsageFailure2(1) {
            @Override
            protected void run() throws Exception {
                Validate.notNull(null, null);
            }
        };
        new ImproperUsageFailure1(2) {
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
        Validate.notNull("a", "myParam");
    }

    @Test
    public void testNoNullElements() throws Exception {
        new ImproperUsageFailure2(1) {
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
        Validate.noNullElements(new String[] { "a" }, "myParam");
        new ImproperUsageFailure2(1) {
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
        Validate.noNullElements(Collections.singleton("s"), "myParam");
    }

    @Test
    public void testNotEmpty() throws Exception {
        new ImproperUsageFailure2(1) {
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
        Validate.notEmpty(new String[] { "a" }, "myParam");
        new ImproperUsageFailure2(1) {
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
        Validate.notEmpty(Collections.singleton("s"), "myParam");
    }

    @Test
    public void testNonNullElements() throws Exception {
        new ImproperUsageFailure2(1) {
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
        Validate.nonNullElements(new String[] { "a" }, "myParam");
        new ImproperUsageFailure2(1) {
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
        Validate.nonNullElements(Collections.singleton("s"), "myParam");
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
         * @throws Exception
         *             if there was an unexpected failure
         */
        public ExpectedNullArgumentFailure(String description) throws Exception {
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
         * @throws Exception
         *             if there was an unexpected failure
         */
        public ExpectedNullElementFailure(String description) throws Exception {
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
         * @throws Exception
         *             if there was an unexpected failure
         */
        public ExpectedEmptyFailure(String description) throws Exception {
            super(MessageFormat
                    .format("''{0}'' must not be empty", description));
        }
    }

    /**
     * Utility to test internal description checks.
     */
    private abstract static class ImproperUsageFailure1 extends
            ExpectedIllegalArgumentException {

        /**
         * Constructor.
         * 
         * @param index
         *            the index of the parameter missing a description throws
         * @Exception if there was an unexpected failure
         */
        public ImproperUsageFailure1(int index) throws Exception {
            super(
                    MessageFormat
                            .format(
                                    "improper usage of Validate.notNull: parameter at index {0} has no description",
                                    index));
        }
    }

    /**
     * Utility to test internal description checks.
     */
    private abstract static class ImproperUsageFailure2 extends
            ExpectedIllegalArgumentException {

        /**
         * @param index
         *            the index of the invalid description
         * @Exception if there was an unexpected failure
         */
        public ImproperUsageFailure2(int index) throws Exception {
            super(
                    MessageFormat
                            .format(
                                    "improper usage of Validate.notNull: parameter at index {0} is not a String",
                                    index));
        }
    }

}
