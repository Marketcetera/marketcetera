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

public class CharacterHolder
{
    private char mItemP;
    private Character mItem;
    private char[] mArrayP;
    private Character[] mArray;
    private Collection<Character> mCollection;
    private List<Character> mList;
    private LinkedList<Character> mLinkedList;
    private Set<Character> mSet;
    private HashSet<Character> mHashSet;
    private TreeSet<Character> mTreeSet;
    private Map<Character,Character> mMap;
    private HashMap<Character,Character> mHashMap;
    private TreeMap<Character,Character> mTreeMap;


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
        setItemP(itemP);        
        setItem(item);
        setArrayP(arrayP);
        setArray(array);
        setCollection(collection);
        setList(list);
        setLinkedList(linkedList);
        setSet(set);
        setHashSet(hashSet);
        setTreeSet(treeSet);
        setMap(map);
        setHashMap(hashMap);
        setTreeMap(treeMap);
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

    public void setItem
        (Character item)
    {
        mItem=item;
    }

    public Character getItem()
    {
        return mItem;
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

    public void setCollection
        (Collection<Character> collection)
    {
        mCollection=collection;
    }

    public Collection<Character> getCollection()
    {
        return mCollection;
    }

    public void setList
        (List<Character> list)
    {
        mList=list;
    }

    public List<Character> getList()
    {
        return mList;
    }

    public void setLinkedList
        (LinkedList<Character> linkedList)
    {
        mLinkedList=linkedList;
    }

    public LinkedList<Character> getLinkedList()
    {
        return mLinkedList;
    }

    public void setSet
        (Set<Character> set)
    {
        mSet=set;
    }

    public Set<Character> getSet()
    {
        return mSet;
    }

    public void setHashSet
        (HashSet<Character> hashSet)
    {
        mHashSet=hashSet;
    }

    public HashSet<Character> getHashSet()
    {
        return mHashSet;
    }

    public void setTreeSet
        (TreeSet<Character> treeSet)
    {
        mTreeSet=treeSet;
    }

    public TreeSet<Character> getTreeSet()
    {
        return mTreeSet;
    }

    public void setMap
        (Map<Character,Character> map)
    {
        mMap=map;
    }

    public Map<Character,Character> getMap()
    {
        return mMap;
    }

    public void setHashMap
        (HashMap<Character,Character> hashMap)
    {
        mHashMap=hashMap;
    }

    public HashMap<Character,Character> getHashMap()
    {
        return mHashMap;
    }

    public void setTreeMap
        (TreeMap<Character,Character> treeMap)
    {
        mTreeMap=treeMap;
    }

    public TreeMap<Character,Character> getTreeMap()
    {
        return mTreeMap;
    }


    @Override
    public int hashCode()
    {
        return (ArrayUtils.hashCode(getItemP())+
                ArrayUtils.hashCode(getItem())+
                ArrayUtils.hashCode(getArrayP())+
                ArrayUtils.hashCode(getArray())+
                ArrayUtils.hashCode(getCollection())+
                ArrayUtils.hashCode(getList())+
                ArrayUtils.hashCode(getLinkedList())+
                ArrayUtils.hashCode(getSet())+
                ArrayUtils.hashCode(getHashSet())+
                ArrayUtils.hashCode(getTreeSet())+
                ArrayUtils.hashCode(getMap())+
                ArrayUtils.hashCode(getHashMap())+
                ArrayUtils.hashCode(getTreeMap()));
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
        return (ArrayUtils.isEquals(getItemP(),o.getItemP()) &&
                ArrayUtils.isEquals(getItem(),o.getItem()) &&
                ArrayUtils.isEquals(getArrayP(),o.getArrayP()) &&
                ArrayUtils.isEquals(getArray(),o.getArray()) &&
                ArrayUtils.isEquals(getCollection(),o.getCollection()) &&
                ArrayUtils.isEquals(getList(),o.getList()) &&
                ArrayUtils.isEquals(getLinkedList(),o.getLinkedList()) &&
                ArrayUtils.isEquals(getSet(),o.getSet()) &&
                ArrayUtils.isEquals(getHashSet(),o.getHashSet()) &&
                ArrayUtils.isEquals(getTreeSet(),o.getTreeSet()) &&
                ArrayUtils.isEquals(getMap(),o.getMap()) &&
                ArrayUtils.isEquals(getHashMap(),o.getHashMap()) &&
                ArrayUtils.isEquals(getTreeMap(),o.getTreeMap()));
    }
}
