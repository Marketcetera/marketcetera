package org.marketcetera.util.ws.types;

import javax.xml.bind.annotation.XmlTransient;
import org.apache.commons.lang.ObjectUtils;
import org.marketcetera.util.ws.wrappers.BaseWrapper;

/**
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id$
 */

/* $License$ */

public class InnerObject
    implements Comparable<InnerObject>
{
    public static class Base<T>
        extends BaseWrapper<T>
    {
        protected Base() {}

        public Base
            (T base)
        {
            super(base);
        }

        public void setBase
            (T base)
        {
            setValue(base);
        }

        public T getBase()
        {
            return getValue();
        }
    }

    public static class InnerStatic
        extends Base<Integer>
    {
        private InnerStatic() {}

        public InnerStatic
            (int value)
        {
            super(value);
        }
    }

    public class InnerNonstatic
        extends Base<Long>
    {
        private InnerNonstatic() {}

        public InnerNonstatic
            (long value)
        {
            super(value);
        }
    }


    private InnerStatic mIs;
    private InnerNonstatic mIns;
    private int mRank;


    private InnerObject() {}

    public InnerObject
        (int isValue,
         long insValue)
    {
        mIs=new InnerStatic(isValue);
        mIns=new InnerNonstatic(insValue);
        setRank(isValue*100+(int)insValue);
    }


    public void setInnerStatic
        (InnerStatic is)
    {
        mIs=is;
    }
    
    public InnerStatic getInnerStatic()
    {
        return mIs;
    }

    public void setInnerNonstatic
        (InnerNonstatic ins)
    {
        mIns=ins;
    }

    /*
     * LIMITATION: JAXB cannot support non-static inner classes. To
     * get the test to pass, this property is marked transient.
     */
    @XmlTransient
    public InnerNonstatic getInnerNonstatic()
    {
        return mIns;
    }

    public void setRank
        (int rank)
    {
        mRank=rank;
    }

    public int getRank()
    {
        return mRank;
    }


    @Override
    public int compareTo
        (InnerObject o)
    {
        return getRank()-o.getRank();
    }

    @Override
    public int hashCode()
    {
        return ObjectUtils.hashCode(getInnerStatic());
    }

    @Override
    public boolean equals
        (Object other)
    {
        if (this==other) {
            return true;
        }
        if ((other==null) || !getClass().equals(other.getClass())) {
            return false;
        }
        InnerObject o=(InnerObject)other;
        return ObjectUtils.equals(getInnerStatic(),o.getInnerStatic());
    }
}
