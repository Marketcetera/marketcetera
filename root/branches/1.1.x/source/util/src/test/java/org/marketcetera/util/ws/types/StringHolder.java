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

/**
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id$
 */

/* $License$ */

public class StringHolder
    extends GenericHolder<String>
{
    private String[] mArray;


    private StringHolder() {}

    public StringHolder
        (String item,
         String[] array,
         Collection<String> collection,
         List<String> list,
         LinkedList<String> linkedList,
         Set<String> set,
         HashSet<String> hashSet,
         TreeSet<String> treeSet,
         Map<String,String> map,
         HashMap<String,String> hashMap,
         TreeMap<String,String> treeMap)
    {
        super(item,collection,list,linkedList,
              set,hashSet,treeSet,map,hashMap,treeMap);
        setArray(array);
    }


    public void setArray
        (String[] array)
    {
        mArray=array;
    }

    public String[] getArray()
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
        StringHolder o=(StringHolder)other;
        return (super.equals(o) &&
                ArrayUtils.isEquals(getArray(),o.getArray()));
    }
}
