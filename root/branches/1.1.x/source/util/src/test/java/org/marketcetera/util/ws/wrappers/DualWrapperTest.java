package org.marketcetera.util.ws.wrappers;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.marketcetera.util.test.UnicodeData.*;

/**
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id$
 */

/* $License$ */

public class DualWrapperTest
    extends WrapperTestBase
{
    private static final class TestWrapper
        extends DualWrapper<String,byte[]>
    {
        public TestWrapper
            (String value)
        {
            super(value);
        }

        private TestWrapper() {}

        @Override
        protected void toRaw()
        {
            if (getMarshalled().length==0) {
                setRawOnly(null);
            } else {
                setRawOnly(new String(getMarshalled()));
            }
        }

        @Override
        protected void toMarshalled()
        {
            if (StringUtils.EMPTY.equals(getRaw())) {
                setMarshalledOnly(null);
            } else {
                setMarshalledOnly(getRaw().getBytes());
            }
        }
    }


    @Test
    public void all()
    {
        dual(new TestWrapper(TEST_VALUE),
             new TestWrapper(TEST_VALUE),
             new TestWrapper(),
             new TestWrapper(null),
             TEST_VALUE,
             HELLO_EN,HELLO_EN_NAT);

        TestWrapper wrapper=new TestWrapper(TEST_VALUE);
        assertEquals(TEST_VALUE,wrapper.getRaw());

        wrapper.setMarshalled(ArrayUtils.EMPTY_BYTE_ARRAY);
        assertNull(wrapper.getRaw());
        assertNull(wrapper.getMarshalled());

        wrapper=new TestWrapper(TEST_VALUE);
        wrapper.setRaw(StringUtils.EMPTY);
        assertNull(wrapper.getRaw());
        assertNull(wrapper.getMarshalled());
    }
}
