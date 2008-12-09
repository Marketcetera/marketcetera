package org.marketcetera.util.spring;

import org.junit.Test;
import org.marketcetera.util.except.I18NRuntimeException;
import org.marketcetera.util.test.TestCaseBase;

import static org.junit.Assert.*;

/**
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id$
 */

/* $License$ */

public class LazyBeanTest
    extends TestCaseBase
{
    private static class Correct
        extends LazyBean
    {
        private int mRawValue;
        private int mCompValue;

        public void setRawValue
            (int rawValue)
        {
            assertNotProcessed();
            mRawValue=rawValue;
        }

        public int getRawValue()
        {
            return mRawValue;
        }

        private void setCompValue
            (int compValue)
        {
            mCompValue=compValue;
        }

        public int getCompValue()
        {
            ensureProcessed();
            return mCompValue;
        }

        @Override
        protected void process()
        {
            setCompValue(getRawValue()+1);
        }
    }

    private static class Recursive
        extends Correct
    {
        @Override
        protected void process()
        {
            if (getRawValue()==0) {
                getCompValue();
            }
            super.process();
        }
    }


    @Test
    public void correct()
    {
        Correct c=new Correct();
        c.setRawValue(0);
        // Raw properties can be set more than once before processing.
        c.setRawValue(1);
        assertEquals(2,c.getCompValue());
        // Raw properties cannot be set after processing.
        try {
            c.setRawValue(2);
            fail();
        } catch (I18NRuntimeException ex) {
            assertEquals(new I18NRuntimeException
                         (Messages.LAZY_ALREADY_PROCESSED),ex);
        }
    }

    @Test
    public void recursive()
    {
        Recursive r=new Recursive();
        r.setRawValue(0);
        try {
            r.getCompValue();
            fail();
        } catch (I18NRuntimeException ex) {
            assertEquals(new I18NRuntimeException
                         (Messages.LAZY_IN_PROCESS),ex);
        }
        // Failure to process means we can still set raw properties...
        r.setRawValue(1);
        // ... and retry to process.
        assertEquals(2,r.getCompValue());
    }
}
