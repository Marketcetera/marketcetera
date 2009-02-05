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

public class BooleanHolder
    extends GenericHolder<Boolean>
{
    private boolean mItemP;
    private boolean[] mArrayP;
    private Boolean[] mArray;


    private BooleanHolder() {}

    public BooleanHolder
        (boolean itemP,
         Boolean item,
         boolean[] arrayP,
         Boolean[] array,
         Collection<Boolean> collection,
         List<Boolean> list,
         LinkedList<Boolean> linkedList,
         Set<Boolean> set,
         HashSet<Boolean> hashSet,
         TreeSet<Boolean> treeSet,
         Map<Boolean,Boolean> map,
         HashMap<Boolean,Boolean> hashMap,
         TreeMap<Boolean,Boolean> treeMap)
    {
        super(item,collection,list,linkedList,
              set,hashSet,treeSet,map,hashMap,treeMap);
        setItemP(itemP);        
        setArrayP(arrayP);
        setArray(array);
    }


    public void setItemP
        (boolean itemP)
    {
        mItemP=itemP;
    }

    public boolean getItemP()
    {
        return mItemP;
    }

    public void setArrayP
        (boolean[] arrayP)
    {
        mArrayP=arrayP;
    }

    public boolean[] getArrayP()
    {
        return mArrayP;
    }

    public void setArray
        (Boolean[] array)
    {
        mArray=array;
    }

    public Boolean[] getArray()
    {
        return mArray;
    }


    @Override
    public int hashCode()
    {
        return (super.hashCode()+
                ArrayUtils.hashCode(getItemP())+
                ArrayUtils.hashCode(getArrayP())+
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
        BooleanHolder o=(BooleanHolder)other;
        return (super.equals(o) &&
                ArrayUtils.isEquals(getItemP(),o.getItemP()) &&
                ArrayUtils.isEquals(getArrayP(),o.getArrayP()) &&
                ArrayUtils.isEquals(getArray(),o.getArray()));
    }
}
