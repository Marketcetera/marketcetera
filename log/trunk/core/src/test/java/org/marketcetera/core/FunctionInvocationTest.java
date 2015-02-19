package org.marketcetera.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.marketcetera.core.FunctionInvocation;
import org.marketcetera.util.log.SLF4JLoggerProxy;

import com.google.common.collect.Lists;

/* $License$ */

/**
 * Tests {@link FunctionInvocation}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 2.4.0
 */
public class FunctionInvocationTest
{
    /**
     * Runs once before all tests.
     *
     * @throws Exception if an unexpected error occurs
     */
    public static void once()
            throws Exception
    {
        LoggerConfiguration.logSetup();
    }
    /**
     * Test {@link FunctionInvocation#parse(String)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testParse()
            throws Exception
    {
        List<TestSpec> specs = Lists.newArrayList();
        specs.add(new TestSpec("Function(1,2)","Function",new String[]{"1","2"}));
        specs.add(new TestSpec("function(1,2)","function",new String[]{"1","2"}));
        specs.add(new TestSpec("function123-__-(1)","function123-__-",new String[]{"1"}));
        specs.add(new TestSpec("  f   (  1  ,  2  )  ","f",new String[]{"1","2"}));
        specs.add(new TestSpec("f(x)","f",new String[]{"x"}));
        specs.add(new TestSpec("f(x,y)","f",new String[]{"x","y"}));
        specs.add(new TestSpec("f(-1,2.5,-3.0)","f",new String[]{"-1","2.5","-3.0"}));
        for(TestSpec spec : specs) {
            SLF4JLoggerProxy.debug(this,
                                   "Testing: {}",
                                   spec);
            verifyInvocation(spec.expectedName,
                             spec.expectedArguments,
                             FunctionInvocation.parse(spec.invocation));
        }
    }
    /**
     * Verifies that the given invocation matches the given expected attributes.
     *
     * @param inExpectedName a <code>String</code> value
     * @param inExpectedArguments a <code>List&lt;String&gt;</code> value
     * @param inActualInvocation a <code>FunctionInvocation</code> value
     * @throws Exception if an unexpected error occurs
     */
    private void verifyInvocation(String inExpectedName,
                                  List<String> inExpectedArguments,
                                  FunctionInvocation inActualInvocation)
            throws Exception
    {
        assertNotNull(inActualInvocation.toString());
        assertEquals(inExpectedName,
                     inActualInvocation.getFunctionName());
        assertEquals(inExpectedArguments,
                     Arrays.asList(inActualInvocation.getArguments()));
    }
    /**
     * Represents a single test case.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since 2.4.0
     */
    private static class TestSpec
    {
        /**
         * Create a new TestSpec instance.
         *
         * @param inInvocation a <code>String</code> value
         * @param inExpectedName a <code>String</code> value
         * @param inExpectedArguments a <code>String...</code> value
         */
        public TestSpec(String inInvocation,
                        String inExpectedName,
                        String...inExpectedArguments)
        {
            invocation = inInvocation;
            expectedName = inExpectedName;
            if(inExpectedArguments != null && inExpectedArguments.length != 0) {
                expectedArguments.addAll(Arrays.asList(inExpectedArguments));
            }
        }
        /* (non-Javadoc)
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString()
        {
            StringBuilder builder = new StringBuilder();
            builder.append("TestSpec [invocation=").append(invocation).append(", expectedName=").append(expectedName)
                    .append(", expectedArguments=").append(expectedArguments).append("]");
            return builder.toString();
        }
        /**
         * actual invocation used to create the test value
         */
        private final String invocation;
        /**
         * expected function name
         */
        private final String expectedName;
        /**
         * expected function arguments, may be empty
         */
        private final List<String> expectedArguments = Lists.newArrayList();
    }
}
