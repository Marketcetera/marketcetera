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

public class ByteHolder
    extends GenericHolder<Byte>
{
    private byte mItemP;
    private byte[] mArrayP;
    private Byte[] mArray;


    private ByteHolder() {}

    public ByteHolder
        (byte itemP,
         Byte item,
         byte[] arrayP,
         Byte[] array,
         Collection<Byte> collection,
         List<Byte> list,
         LinkedList<Byte> linkedList,
         Set<Byte> set,
         HashSet<Byte> hashSet,
         TreeSet<Byte> treeSet,
         Map<Byte,Byte> map,
         HashMap<Byte,Byte> hashMap,
         TreeMap<Byte,Byte> treeMap)
    {
        super(item,collection,list,linkedList,
              set,hashSet,treeSet,map,hashMap,treeMap);
        setItemP(itemP);        
        setArrayP(arrayP);
        setArray(array);
    }


    public void setItemP
        (byte itemP)
    {
        mItemP=itemP;
    }

    public byte getItemP()
    {
        return mItemP;
    }

    public void setArrayP
        (byte[] arrayP)
    {
        mArrayP=arrayP;
    }

    public byte[] getArrayP()
    {
        return mArrayP;
    }

    public void setArray
        (Byte[] array)
    {
        mArray=array;
    }

    public Byte[] getArray()
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
        ByteHolder o=(ByteHolder)other;
        return (super.equals(o) &&
                ArrayUtils.isEquals(getItemP(),o.getItemP()) &&
                ArrayUtils.isEquals(getArrayP(),o.getArrayP()) &&
                ArrayUtils.isEquals(getArray(),o.getArray()));
    }
}
