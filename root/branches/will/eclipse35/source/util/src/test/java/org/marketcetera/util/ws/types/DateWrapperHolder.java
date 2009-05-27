package org.marketcetera.util.ws.types;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import org.apache.commons.lang.ArrayUtils;
import org.marketcetera.util.ws.wrappers.DateWrapper;

/**
 * @author tlerios@marketcetera.com
 * @since 1.5.0
 * @version $Id$
 */

/* $License$ */

public class DateWrapperHolder
    extends GenericHolder<DateWrapper>
{
    private DateWrapper[] mArray;


    private DateWrapperHolder() {}

    public DateWrapperHolder
        (DateWrapper item,
         DateWrapper[] array,
         Collection<DateWrapper> collection,
         List<DateWrapper> list,
         LinkedList<DateWrapper> linkedList,
         Set<DateWrapper> set,
         HashSet<DateWrapper> hashSet,
         TreeSet<DateWrapper> treeSet,
         Map<DateWrapper,DateWrapper> map,
         HashMap<DateWrapper,DateWrapper> hashMap,
         TreeMap<DateWrapper,DateWrapper> treeMap)
    {
        super(item,collection,list,linkedList,
              set,hashSet,treeSet,map,hashMap,treeMap);
        setArray(array);
    }


    public void setArray
        (DateWrapper[] array)
    {
        mArray=array;
    }

    public DateWrapper[] getArray()
    {
        return mArray;
    }


    @Override
    public int hashCode()
    {
        return (super.hashCode()+
                ArrayUtils.hashCode(getArray()));
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
        DateWrapperHolder o=(DateWrapperHolder)other;
        return (super.equals(o) &&
                ArrayUtils.isEquals(getArray(),o.getArray()));
    }
}
