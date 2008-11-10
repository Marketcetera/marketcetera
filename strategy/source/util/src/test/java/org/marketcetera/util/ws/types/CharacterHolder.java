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
 * @since $Release$
 * @version $Id$
 */

/* $License$ */

public class CharacterHolder
    extends GenericHolder<Character>
{
    private char mItemP;
    private char[] mArrayP;
    private Character[] mArray;


    private CharacterHolder() {}

    public CharacterHolder
        (char itemP,
         Character item,
         char[] arrayP,
         Character[] array,
         Collection<Character> collection,
         List<Character> list,
         LinkedList<Character> linkedList,
         Set<Character> set,
         HashSet<Character> hashSet,
         TreeSet<Character> treeSet,
         Map<Character,Character> map,
         HashMap<Character,Character> hashMap,
         TreeMap<Character,Character> treeMap)
    {
        super(item,collection,list,linkedList,
              set,hashSet,treeSet,map,hashMap,treeMap);
        setItemP(itemP);        
        setArrayP(arrayP);
        setArray(array);
    }


    public void setItemP
        (char itemP)
    {
        mItemP=itemP;
    }

    public char getItemP()
    {
        return mItemP;
    }

    public void setArrayP
        (char[] arrayP)
    {
        mArrayP=arrayP;
    }

    public char[] getArrayP()
    {
        return mArrayP;
    }

    public void setArray
        (Character[] array)
    {
        mArray=array;
    }

    public Character[] getArray()
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
        CharacterHolder o=(CharacterHolder)other;
        return (super.equals(o) &&
                ArrayUtils.isEquals(getItemP(),o.getItemP()) &&
                ArrayUtils.isEquals(getArrayP(),o.getArrayP()) &&
                ArrayUtils.isEquals(getArray(),o.getArray()));
    }
}
