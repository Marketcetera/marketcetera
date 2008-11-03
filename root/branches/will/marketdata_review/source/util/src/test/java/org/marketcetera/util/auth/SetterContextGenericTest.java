package org.marketcetera.util.auth;

import org.junit.Test;
import org.marketcetera.util.log.I18NBoundMessage;

/**
 * @author tlerios@marketcetera.com
 * @since 0.5.0
 * @version $Id$
 */

/* $License$ */

public class SetterContextGenericTest
    extends SetterContextTestBase
{
    private static class TestContext
        extends Context<Setter<Holder<String>>>
    {
        public TestContext
            (I18NBoundMessage name,
             boolean override)
        {
            super(name,override);
        }

        public TestContext
            (boolean override)
        {
            super(override);
        }

        public void setValues() {}
    }


    @Test
    public void setterBasics()
        throws Exception
    {
        simpleSetter
            (new Setter<Holder<String>>
             (TEST_STRING_HOLDER,TestMessages.TEST_USAGE),
             TEST_STRING_HOLDER);
    }

    @Test
    public void contextBasics()
        throws Exception
    {
        Holder<String> holder=new Holder<String>();
        Setter<Holder<String>> setter=
            new Setter<Holder<String>>(holder,TestMessages.TEST_USAGE);

        simpleContextNoName
            (new TestContext(true),
             setter,true,true);
        simpleContextNoName
            (new TestContext(false),
             setter,false,true);
        simpleContextWithName
            (new TestContext(TestMessages.TEST_CONTEXT,true),
             setter,true,true,TestMessages.TEST_CONTEXT,TEST_CONTEXT);
        simpleContextWithName
            (new TestContext(TestMessages.TEST_CONTEXT,false),
             setter,false,true,TestMessages.TEST_CONTEXT,TEST_CONTEXT);

        holder.setValue("x");
        simpleContextNoName
            (new TestContext(true),
             setter,true,true);
        simpleContextNoName
            (new TestContext(false),
             setter,false,false);
        simpleContextWithName
            (new TestContext(TestMessages.TEST_CONTEXT,true),
             setter,true,true,TestMessages.TEST_CONTEXT,TEST_CONTEXT);
        simpleContextWithName
            (new TestContext(TestMessages.TEST_CONTEXT,false),
             setter,false,false,TestMessages.TEST_CONTEXT,TEST_CONTEXT);
    }
}
