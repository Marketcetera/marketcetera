package org.marketcetera.util.ws.wrappers;

import java.io.StringReader;
import java.io.StringWriter;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.MarshalException;
import org.junit.Test;
import org.marketcetera.util.test.TestCaseBase;

import static org.junit.Assert.*;
import static org.marketcetera.util.test.EqualityAssert.*;

/**
 * @author tlerios@marketcetera.com
 * @since 2.0.0
 * @version $Id$
 */

/* $License$ */

public class RootElementWrapperTest
    extends TestCaseBase
{
    @SuppressWarnings("unused")
    private static class MyInteger
        extends BaseWrapper<Integer>
    {
        public MyInteger
            (int value)
        {
            super(value);
        }

        private MyInteger() {}

        public void setInt
            (int value)
        {
            setValue(value);
        }

        public int getInt()
        {
            return getValue();
        }
    }


    @Test
    public void basics()
        throws Exception
    {
        Object o=new Object();
        assertEquality(new RootElementWrapper<Object>(o),
                       new RootElementWrapper<Object>(o),
                       new RootElementWrapper<Object>(new Object()));

        RootElementWrapper<Object> w=new RootElementWrapper<Object>(o);
        assertSame(o,w.getObject());

        Object o2=new Object();
        w.setObject(o2);
        assertSame(o2,w.getObject());

        w.setObject(null);
        assertNull(w.getObject());
    }

    @Test
    public void marshalling()
        throws Exception
    {
        MyInteger value=new MyInteger(1);

        // MyInteger cannot be marshalled because it
        // lacks @XmlRootElement.

        JAXBContext context=JAXBContext.newInstance(MyInteger.class);
        StringWriter writer=new StringWriter();
        try {
            context.createMarshaller().marshal(value,writer);
            fail();
        } catch (MarshalException ex) {
            // Expected.
        }

        // When MyInteger is wrapped by RootElementWrapper,
        // marshalling becomes possible.

        RootElementWrapper<MyInteger> w=
            new RootElementWrapper<MyInteger>(value);

        context=JAXBContext.newInstance
            (RootElementWrapper.class,MyInteger.class);
        writer=new StringWriter();
        context.createMarshaller().marshal(w,writer);
        assertEquals(w,
                     (context.createUnmarshaller().unmarshal
                      (new StringReader(writer.toString()))));
    }
}
