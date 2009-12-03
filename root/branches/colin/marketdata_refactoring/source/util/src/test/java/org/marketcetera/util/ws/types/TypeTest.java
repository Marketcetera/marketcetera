package org.marketcetera.util.ws.types;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import org.apache.commons.lang.ArrayUtils;
import org.junit.Test;
import org.marketcetera.util.test.TestCaseBase;
import org.marketcetera.util.ws.stateless.StatelessClient;
import org.marketcetera.util.ws.stateless.StatelessServer;
import org.marketcetera.util.ws.wrappers.CompSerWrapper;
import org.marketcetera.util.ws.wrappers.DateWrapper;
import org.marketcetera.util.ws.wrappers.LocaleWrapper;
import org.marketcetera.util.ws.wrappers.MapWrapper;
import org.marketcetera.util.ws.wrappers.RemoteException;
import org.marketcetera.util.ws.wrappers.SerWrapper;

import static org.junit.Assert.*;
import static org.marketcetera.util.test.UnicodeData.*;

/**
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id$
 */

/* $License$ */

public class TypeTest
    extends TestCaseBase
{

    // HELPER CLASSES.

    private static class SerWrapperComparator
        implements Comparator<SerWrapper>
    {
        @Override
        public int compare
            (SerWrapper o1,
             SerWrapper o2)
        {
            return ((WrappableObject)(o1.getRaw())).compareTo
                ((WrappableObject)(o2.getRaw()));
        }

        @Override
        public int hashCode()
        {
            return 0;
        }

        @Override
        public boolean equals
            (Object other)
        {
            return ((other!=null) && getClass().equals(other.getClass()));
        }
    }


    // TEST DATA.

    // Boolean.

    private static final boolean TEST_P_BOOLEAN=
        true;
    private static final Boolean TEST_O_BOOLEAN=
        false;
    private static final boolean[] TEST_PARR_BOOLEAN=new boolean[]
        {true,false,true};
    private static final Boolean[] TEST_OARR_BOOLEAN=new Boolean[]
        {false,true,false};
    private static final List<Boolean> TEST_LST_BOOLEAN=
        Arrays.asList(TEST_OARR_BOOLEAN);
    private static final Collection<Boolean> TEST_COL_BOOLEAN=
        TEST_LST_BOOLEAN;
    private static final LinkedList<Boolean> TEST_LLST_BOOLEAN=
        toLinkedList(TEST_LST_BOOLEAN);
    private static final HashSet<Boolean> TEST_HSET_BOOLEAN=
        toHashSet(TEST_LST_BOOLEAN);
    private static final TreeSet<Boolean> TEST_TSET_BOOLEAN=
        toTreeSet(TEST_LST_BOOLEAN);
    private static final Set<Boolean> TEST_SET_BOOLEAN=
        TEST_HSET_BOOLEAN;
    private static final HashMap<Boolean,Boolean> TEST_HMAP_BOOLEAN=
        toHashMap(TEST_LST_BOOLEAN);
    private static final MapWrapper<Boolean,Boolean>
        TEST_WHMAP_BOOLEAN=
        new MapWrapper<Boolean,Boolean>(TEST_HMAP_BOOLEAN);
    private static final TreeMap<Boolean,Boolean> TEST_TMAP_BOOLEAN=
        toTreeMap(TEST_LST_BOOLEAN);
    private static final MapWrapper<Boolean,Boolean>
        TEST_WTMAP_BOOLEAN=
        new MapWrapper<Boolean,Boolean>(TEST_TMAP_BOOLEAN);
    private static final Map<Boolean,Boolean> TEST_MAP_BOOLEAN=
        TEST_HMAP_BOOLEAN;
    private static final MapWrapper<Boolean,Boolean> TEST_WMAP_BOOLEAN=
        new MapWrapper<Boolean,Boolean>(TEST_MAP_BOOLEAN);
    private static final BooleanHolder TEST_HLD_BOOLEAN=
        new BooleanHolder
        (TEST_P_BOOLEAN,TEST_O_BOOLEAN,
         TEST_PARR_BOOLEAN,TEST_OARR_BOOLEAN,
         TEST_COL_BOOLEAN,TEST_LST_BOOLEAN,TEST_LLST_BOOLEAN,
         TEST_SET_BOOLEAN,TEST_HSET_BOOLEAN,TEST_TSET_BOOLEAN,
         TEST_MAP_BOOLEAN,TEST_HMAP_BOOLEAN,TEST_TMAP_BOOLEAN);

    // Byte.

    private static final byte TEST_P_BYTE=
        Byte.MIN_VALUE;
    private static final Byte TEST_O_BYTE=
        Byte.MAX_VALUE;
    private static final byte[] TEST_PARR_BYTE=new byte[]
        {(byte)1,(byte)2,(byte)3};
    private static final Byte[] TEST_OARR_BYTE=new Byte[]
        {(byte)4,(byte)5,(byte)6};
    private static final List<Byte> TEST_LST_BYTE=
        Arrays.asList(TEST_OARR_BYTE);
    private static final Collection<Byte> TEST_COL_BYTE=
        TEST_LST_BYTE;
    private static final LinkedList<Byte> TEST_LLST_BYTE=
        toLinkedList(TEST_LST_BYTE);
    private static final HashSet<Byte> TEST_HSET_BYTE=
        toHashSet(TEST_LST_BYTE);
    private static final TreeSet<Byte> TEST_TSET_BYTE=
        toTreeSet(TEST_LST_BYTE);
    private static final Set<Byte> TEST_SET_BYTE=
        TEST_HSET_BYTE;
    private static final HashMap<Byte,Byte> TEST_HMAP_BYTE=
        toHashMap(TEST_LST_BYTE);
    private static final MapWrapper<Byte,Byte>
        TEST_WHMAP_BYTE=
        new MapWrapper<Byte,Byte>(TEST_HMAP_BYTE);
    private static final TreeMap<Byte,Byte> TEST_TMAP_BYTE=
        toTreeMap(TEST_LST_BYTE);
    private static final MapWrapper<Byte,Byte>
        TEST_WTMAP_BYTE=
        new MapWrapper<Byte,Byte>(TEST_TMAP_BYTE);
    private static final Map<Byte,Byte> TEST_MAP_BYTE=
        TEST_HMAP_BYTE;
    private static final MapWrapper<Byte,Byte> TEST_WMAP_BYTE=
        new MapWrapper<Byte,Byte>(TEST_MAP_BYTE);
    private static final ByteHolder TEST_HLD_BYTE=
        new ByteHolder
        (TEST_P_BYTE,TEST_O_BYTE,
         TEST_PARR_BYTE,TEST_OARR_BYTE,
         TEST_COL_BYTE,TEST_LST_BYTE,TEST_LLST_BYTE,
         TEST_SET_BYTE,TEST_HSET_BYTE,TEST_TSET_BYTE,
         TEST_MAP_BYTE,TEST_HMAP_BYTE,TEST_TMAP_BYTE);

    // Character.

    private static final char TEST_P_CHAR=
        '\u0001';
    private static final Character TEST_O_CHAR=
        '\u0002';
    private static final char[] TEST_PARR_CHAR=new char[]
        {'\u0001','\u0002','a'};
    private static final Character[] TEST_OARR_CHAR=new Character[]
        {'\u0003','\u0004','b'};
    private static final List<Character> TEST_LST_CHAR=
        Arrays.asList(TEST_OARR_CHAR);
    private static final Collection<Character> TEST_COL_CHAR=
        TEST_LST_CHAR;
    private static final LinkedList<Character> TEST_LLST_CHAR=
        toLinkedList(TEST_LST_CHAR);
    private static final HashSet<Character> TEST_HSET_CHAR=
        toHashSet(TEST_LST_CHAR);
    private static final TreeSet<Character> TEST_TSET_CHAR=
        toTreeSet(TEST_LST_CHAR);
    private static final Set<Character> TEST_SET_CHAR=
        TEST_HSET_CHAR;
    private static final HashMap<Character,Character> TEST_HMAP_CHAR=
        toHashMap(TEST_LST_CHAR);
    private static final MapWrapper<Character,Character>
        TEST_WHMAP_CHAR=
        new MapWrapper<Character,Character>(TEST_HMAP_CHAR);
    private static final TreeMap<Character,Character> TEST_TMAP_CHAR=
        toTreeMap(TEST_LST_CHAR);
    private static final MapWrapper<Character,Character>
        TEST_WTMAP_CHAR=
        new MapWrapper<Character,Character>(TEST_TMAP_CHAR);
    private static final Map<Character,Character> TEST_MAP_CHAR=
        TEST_HMAP_CHAR;
    private static final MapWrapper<Character,Character> TEST_WMAP_CHAR=
        new MapWrapper<Character,Character>(TEST_MAP_CHAR);
    private static final CharacterHolder TEST_HLD_CHAR=
        new CharacterHolder
        (TEST_P_CHAR,TEST_O_CHAR,
         TEST_PARR_CHAR,TEST_OARR_CHAR,
         TEST_COL_CHAR,TEST_LST_CHAR,TEST_LLST_CHAR,
         TEST_SET_CHAR,TEST_HSET_CHAR,TEST_TSET_CHAR,
         TEST_MAP_CHAR,TEST_HMAP_CHAR,TEST_TMAP_CHAR);

    // Double.

    private static final double TEST_P_DOUBLE=
        Double.MIN_VALUE;
    private static final Double TEST_O_DOUBLE=
        Double.MAX_VALUE;
    private static final double[] TEST_PARR_DOUBLE=new double[]
        {1.5,2.5,3.5,Double.MIN_NORMAL,Double.NEGATIVE_INFINITY};
    private static final Double[] TEST_OARR_DOUBLE=new Double[]
        {4.5,5.5,6.5,Double.NaN,Double.POSITIVE_INFINITY};
    private static final List<Double> TEST_LST_DOUBLE=
        Arrays.asList(TEST_OARR_DOUBLE);
    private static final Collection<Double> TEST_COL_DOUBLE=
        TEST_LST_DOUBLE;
    private static final LinkedList<Double> TEST_LLST_DOUBLE=
        toLinkedList(TEST_LST_DOUBLE);
    private static final HashSet<Double> TEST_HSET_DOUBLE=
        toHashSet(TEST_LST_DOUBLE);
    private static final TreeSet<Double> TEST_TSET_DOUBLE=
        toTreeSet(TEST_LST_DOUBLE);
    private static final Set<Double> TEST_SET_DOUBLE=
        TEST_HSET_DOUBLE;
    private static final HashMap<Double,Double> TEST_HMAP_DOUBLE=
        toHashMap(TEST_LST_DOUBLE);
    private static final MapWrapper<Double,Double>
        TEST_WHMAP_DOUBLE=
        new MapWrapper<Double,Double>(TEST_HMAP_DOUBLE);
    private static final TreeMap<Double,Double> TEST_TMAP_DOUBLE=
        toTreeMap(TEST_LST_DOUBLE);
    private static final MapWrapper<Double,Double>
        TEST_WTMAP_DOUBLE=
        new MapWrapper<Double,Double>(TEST_TMAP_DOUBLE);
    private static final Map<Double,Double> TEST_MAP_DOUBLE=
        TEST_HMAP_DOUBLE;
    private static final MapWrapper<Double,Double> TEST_WMAP_DOUBLE=
        new MapWrapper<Double,Double>(TEST_MAP_DOUBLE);
    private static final DoubleHolder TEST_HLD_DOUBLE=
        new DoubleHolder
        (TEST_P_DOUBLE,TEST_O_DOUBLE,
         TEST_PARR_DOUBLE,TEST_OARR_DOUBLE,
         TEST_COL_DOUBLE,TEST_LST_DOUBLE,TEST_LLST_DOUBLE,
         TEST_SET_DOUBLE,TEST_HSET_DOUBLE,TEST_TSET_DOUBLE,
         TEST_MAP_DOUBLE,TEST_HMAP_DOUBLE,TEST_TMAP_DOUBLE);

    // Float.

    private static final float TEST_P_FLOAT=
        Float.MIN_VALUE;
    private static final Float TEST_O_FLOAT=
        Float.MAX_VALUE;
    private static final float[] TEST_PARR_FLOAT=new float[]
        {1.5F,2.5F,3.5F,Float.MIN_NORMAL,Float.NEGATIVE_INFINITY};
    private static final Float[] TEST_OARR_FLOAT=new Float[]
        {4.5F,5.5F,6.5F,Float.NaN,Float.POSITIVE_INFINITY};
    private static final List<Float> TEST_LST_FLOAT=
        Arrays.asList(TEST_OARR_FLOAT);
    private static final Collection<Float> TEST_COL_FLOAT=
        TEST_LST_FLOAT;
    private static final LinkedList<Float> TEST_LLST_FLOAT=
        toLinkedList(TEST_LST_FLOAT);
    private static final HashSet<Float> TEST_HSET_FLOAT=
        toHashSet(TEST_LST_FLOAT);
    private static final TreeSet<Float> TEST_TSET_FLOAT=
        toTreeSet(TEST_LST_FLOAT);
    private static final Set<Float> TEST_SET_FLOAT=
        TEST_HSET_FLOAT;
    private static final HashMap<Float,Float> TEST_HMAP_FLOAT=
        toHashMap(TEST_LST_FLOAT);
    private static final MapWrapper<Float,Float>
        TEST_WHMAP_FLOAT=
        new MapWrapper<Float,Float>(TEST_HMAP_FLOAT);
    private static final TreeMap<Float,Float> TEST_TMAP_FLOAT=
        toTreeMap(TEST_LST_FLOAT);
    private static final MapWrapper<Float,Float>
        TEST_WTMAP_FLOAT=
        new MapWrapper<Float,Float>(TEST_TMAP_FLOAT);
    private static final Map<Float,Float> TEST_MAP_FLOAT=
        TEST_HMAP_FLOAT;
    private static final MapWrapper<Float,Float> TEST_WMAP_FLOAT=
        new MapWrapper<Float,Float>(TEST_MAP_FLOAT);
    private static final FloatHolder TEST_HLD_FLOAT=
        new FloatHolder
        (TEST_P_FLOAT,TEST_O_FLOAT,
         TEST_PARR_FLOAT,TEST_OARR_FLOAT,
         TEST_COL_FLOAT,TEST_LST_FLOAT,TEST_LLST_FLOAT,
         TEST_SET_FLOAT,TEST_HSET_FLOAT,TEST_TSET_FLOAT,
         TEST_MAP_FLOAT,TEST_HMAP_FLOAT,TEST_TMAP_FLOAT);

    // Integer.

    private static final int TEST_P_INT=
        Integer.MIN_VALUE;
    private static final Integer TEST_O_INT=
        Integer.MAX_VALUE;
    private static final int[] TEST_PARR_INT=new int[]
        {1,2,3};
    private static final Integer[] TEST_OARR_INT=new Integer[]
        {4,5,6};
    private static final List<Integer> TEST_LST_INT=
        Arrays.asList(TEST_OARR_INT);
    private static final Collection<Integer> TEST_COL_INT=
        TEST_LST_INT;
    private static final LinkedList<Integer> TEST_LLST_INT=
        toLinkedList(TEST_LST_INT);
    private static final HashSet<Integer> TEST_HSET_INT=
        toHashSet(TEST_LST_INT);
    private static final TreeSet<Integer> TEST_TSET_INT=
        toTreeSet(TEST_LST_INT);
    private static final Set<Integer> TEST_SET_INT=
        TEST_HSET_INT;
    private static final HashMap<Integer,Integer> TEST_HMAP_INT=
        toHashMap(TEST_LST_INT);
    private static final MapWrapper<Integer,Integer>
        TEST_WHMAP_INT=
        new MapWrapper<Integer,Integer>(TEST_HMAP_INT);
    private static final TreeMap<Integer,Integer> TEST_TMAP_INT=
        toTreeMap(TEST_LST_INT);
    private static final MapWrapper<Integer,Integer>
        TEST_WTMAP_INT=
        new MapWrapper<Integer,Integer>(TEST_TMAP_INT);
    private static final Map<Integer,Integer> TEST_MAP_INT=
        TEST_HMAP_INT;
    private static final MapWrapper<Integer,Integer> TEST_WMAP_INT=
        new MapWrapper<Integer,Integer>(TEST_MAP_INT);
    private static final IntegerHolder TEST_HLD_INT=
        new IntegerHolder
        (TEST_P_INT,TEST_O_INT,
         TEST_PARR_INT,TEST_OARR_INT,
         TEST_COL_INT,TEST_LST_INT,TEST_LLST_INT,
         TEST_SET_INT,TEST_HSET_INT,TEST_TSET_INT,
         TEST_MAP_INT,TEST_HMAP_INT,TEST_TMAP_INT);

    // Long.

    private static final long TEST_P_LONG=
        Long.MIN_VALUE;
    private static final Long TEST_O_LONG=
        Long.MAX_VALUE;
    private static final long[] TEST_PARR_LONG=new long[]
        {1L,2L,3L};
    private static final Long[] TEST_OARR_LONG=new Long[]
        {4L,5L,6L};
    private static final List<Long> TEST_LST_LONG=
        Arrays.asList(TEST_OARR_LONG);
    private static final Collection<Long> TEST_COL_LONG=
        TEST_LST_LONG;
    private static final LinkedList<Long> TEST_LLST_LONG=
        toLinkedList(TEST_LST_LONG);
    private static final HashSet<Long> TEST_HSET_LONG=
        toHashSet(TEST_LST_LONG);
    private static final TreeSet<Long> TEST_TSET_LONG=
        toTreeSet(TEST_LST_LONG);
    private static final Set<Long> TEST_SET_LONG=
        TEST_HSET_LONG;
    private static final HashMap<Long,Long> TEST_HMAP_LONG=
        toHashMap(TEST_LST_LONG);
    private static final MapWrapper<Long,Long>
        TEST_WHMAP_LONG=
        new MapWrapper<Long,Long>(TEST_HMAP_LONG);
    private static final TreeMap<Long,Long> TEST_TMAP_LONG=
        toTreeMap(TEST_LST_LONG);
    private static final MapWrapper<Long,Long>
        TEST_WTMAP_LONG=
        new MapWrapper<Long,Long>(TEST_TMAP_LONG);
    private static final Map<Long,Long> TEST_MAP_LONG=
        TEST_HMAP_LONG;
    private static final MapWrapper<Long,Long> TEST_WMAP_LONG=
        new MapWrapper<Long,Long>(TEST_MAP_LONG);
    private static final LongHolder TEST_HLD_LONG=
        new LongHolder
        (TEST_P_LONG,TEST_O_LONG,
         TEST_PARR_LONG,TEST_OARR_LONG,
         TEST_COL_LONG,TEST_LST_LONG,TEST_LLST_LONG,
         TEST_SET_LONG,TEST_HSET_LONG,TEST_TSET_LONG,
         TEST_MAP_LONG,TEST_HMAP_LONG,TEST_TMAP_LONG);

    // Short.

    private static final short TEST_P_SHORT=
        Short.MIN_VALUE;
    private static final Short TEST_O_SHORT=
        Short.MAX_VALUE;
    private static final short[] TEST_PARR_SHORT=new short[]
        {(short)1,(short)2,(short)3};
    private static final Short[] TEST_OARR_SHORT=new Short[]
        {(short)4,(short)5,(short)6};
    private static final List<Short> TEST_LST_SHORT=
        Arrays.asList(TEST_OARR_SHORT);
    private static final Collection<Short> TEST_COL_SHORT=
        TEST_LST_SHORT;
    private static final LinkedList<Short> TEST_LLST_SHORT=
        toLinkedList(TEST_LST_SHORT);
    private static final HashSet<Short> TEST_HSET_SHORT=
        toHashSet(TEST_LST_SHORT);
    private static final TreeSet<Short> TEST_TSET_SHORT=
        toTreeSet(TEST_LST_SHORT);
    private static final Set<Short> TEST_SET_SHORT=
        TEST_HSET_SHORT;
    private static final HashMap<Short,Short> TEST_HMAP_SHORT=
        toHashMap(TEST_LST_SHORT);
    private static final MapWrapper<Short,Short>
        TEST_WHMAP_SHORT=
        new MapWrapper<Short,Short>(TEST_HMAP_SHORT);
    private static final TreeMap<Short,Short> TEST_TMAP_SHORT=
        toTreeMap(TEST_LST_SHORT);
    private static final MapWrapper<Short,Short>
        TEST_WTMAP_SHORT=
        new MapWrapper<Short,Short>(TEST_TMAP_SHORT);
    private static final Map<Short,Short> TEST_MAP_SHORT=
        TEST_HMAP_SHORT;
    private static final MapWrapper<Short,Short> TEST_WMAP_SHORT=
        new MapWrapper<Short,Short>(TEST_MAP_SHORT);
    private static final ShortHolder TEST_HLD_SHORT=
        new ShortHolder
        (TEST_P_SHORT,TEST_O_SHORT,
         TEST_PARR_SHORT,TEST_OARR_SHORT,
         TEST_COL_SHORT,TEST_LST_SHORT,TEST_LLST_SHORT,
         TEST_SET_SHORT,TEST_HSET_SHORT,TEST_TSET_SHORT,
         TEST_MAP_SHORT,TEST_HMAP_SHORT,TEST_TMAP_SHORT);

    // String.

    private static final String TEST_STR=
        COMBO;
    private static final String[] TEST_ARR_STR=new String[]
        {HELLO_EN,LANGUAGE_NO,HELLO_GR,HOUSE_AR,GOODBYE_JA,GOATS_LNB,
         G_CLEF_MSC,COMBO};
    private static final List<String> TEST_LST_STR=
        Arrays.asList(TEST_ARR_STR);
    private static final Collection<String> TEST_COL_STR=
        TEST_LST_STR;
    private static final LinkedList<String> TEST_LLST_STR=
        toLinkedList(TEST_LST_STR);
    private static final HashSet<String> TEST_HSET_STR=
        toHashSet(TEST_LST_STR);
    private static final TreeSet<String> TEST_TSET_STR=
        toTreeSet(TEST_LST_STR);
    private static final Set<String> TEST_SET_STR=
        TEST_HSET_STR;
    private static final HashMap<String,String> TEST_HMAP_STR=
        toHashMap(TEST_LST_STR);
    private static final MapWrapper<String,String>
        TEST_WHMAP_STR=
        new MapWrapper<String,String>(TEST_HMAP_STR);
    private static final TreeMap<String,String> TEST_TMAP_STR=
        toTreeMap(TEST_LST_STR);
    private static final MapWrapper<String,String>
        TEST_WTMAP_STR=
        new MapWrapper<String,String>(TEST_TMAP_STR);
    private static final Map<String,String> TEST_MAP_STR=
        TEST_HMAP_STR;
    private static final MapWrapper<String,String> TEST_WMAP_STR=
        new MapWrapper<String,String>(TEST_MAP_STR);
    private static final StringHolder TEST_HLD_STR=
        new StringHolder
        (TEST_STR,
         TEST_ARR_STR,
         TEST_COL_STR,TEST_LST_STR,TEST_LLST_STR,
         TEST_SET_STR,TEST_HSET_STR,TEST_TSET_STR,
         TEST_MAP_STR,TEST_HMAP_STR,TEST_TMAP_STR);

    // Big decimal.

    private static final BigDecimal TEST_BD=
        new BigDecimal("10012001310004100.0499991992100045");
    private static final BigDecimal[] TEST_ARR_BD=new BigDecimal[]
        {new BigDecimal("10012001310004100.0499991992100045"),
         new BigDecimal("20012001310004100.0499991992100045"),
         new BigDecimal("30012001310004100.0499991992100045")};
    private static final List<BigDecimal> TEST_LST_BD=
        Arrays.asList(TEST_ARR_BD);
    private static final Collection<BigDecimal> TEST_COL_BD=
        TEST_LST_BD;
    private static final LinkedList<BigDecimal> TEST_LLST_BD=
        toLinkedList(TEST_LST_BD);
    private static final HashSet<BigDecimal> TEST_HSET_BD=
        toHashSet(TEST_LST_BD);
    private static final TreeSet<BigDecimal> TEST_TSET_BD=
        toTreeSet(TEST_LST_BD);
    private static final Set<BigDecimal> TEST_SET_BD=
        TEST_HSET_BD;
    private static final HashMap<BigDecimal,BigDecimal> TEST_HMAP_BD=
        toHashMap(TEST_LST_BD);
    private static final MapWrapper<BigDecimal,BigDecimal>
        TEST_WHMAP_BD=
        new MapWrapper<BigDecimal,BigDecimal>(TEST_HMAP_BD);
    private static final TreeMap<BigDecimal,BigDecimal> TEST_TMAP_BD=
        toTreeMap(TEST_LST_BD);
    private static final MapWrapper<BigDecimal,BigDecimal>
        TEST_WTMAP_BD=
        new MapWrapper<BigDecimal,BigDecimal>(TEST_TMAP_BD);
    private static final Map<BigDecimal,BigDecimal> TEST_MAP_BD=
        TEST_HMAP_BD;
    private static final MapWrapper<BigDecimal,BigDecimal> TEST_WMAP_BD=
        new MapWrapper<BigDecimal,BigDecimal>(TEST_MAP_BD);
    private static final BigDecimalHolder TEST_HLD_BD=
        new BigDecimalHolder
        (TEST_BD,
         TEST_ARR_BD,
         TEST_COL_BD,TEST_LST_BD,TEST_LLST_BD,
         TEST_SET_BD,TEST_HSET_BD,TEST_TSET_BD,
         TEST_MAP_BD,TEST_HMAP_BD,TEST_TMAP_BD);

    // Big integer.

    private static final BigInteger TEST_BI=
        new BigInteger("100120013100041000499991992100045");
    private static final BigInteger[] TEST_ARR_BI=new BigInteger[]
        {new BigInteger("100120013100041000499991992100045"),
         new BigInteger("200120013100041000499991992100045"),
         new BigInteger("300120013100041000499991992100045")};
    private static final List<BigInteger> TEST_LST_BI=
        Arrays.asList(TEST_ARR_BI);
    private static final Collection<BigInteger> TEST_COL_BI=
        TEST_LST_BI;
    private static final LinkedList<BigInteger> TEST_LLST_BI=
        toLinkedList(TEST_LST_BI);
    private static final HashSet<BigInteger> TEST_HSET_BI=
        toHashSet(TEST_LST_BI);
    private static final TreeSet<BigInteger> TEST_TSET_BI=
        toTreeSet(TEST_LST_BI);
    private static final Set<BigInteger> TEST_SET_BI=
        TEST_HSET_BI;
    private static final HashMap<BigInteger,BigInteger> TEST_HMAP_BI=
        toHashMap(TEST_LST_BI);
    private static final MapWrapper<BigInteger,BigInteger>
        TEST_WHMAP_BI=
        new MapWrapper<BigInteger,BigInteger>(TEST_HMAP_BI);
    private static final TreeMap<BigInteger,BigInteger> TEST_TMAP_BI=
        toTreeMap(TEST_LST_BI);
    private static final MapWrapper<BigInteger,BigInteger>
        TEST_WTMAP_BI=
        new MapWrapper<BigInteger,BigInteger>(TEST_TMAP_BI);
    private static final Map<BigInteger,BigInteger> TEST_MAP_BI=
        TEST_HMAP_BI;
    private static final MapWrapper<BigInteger,BigInteger> TEST_WMAP_BI=
        new MapWrapper<BigInteger,BigInteger>(TEST_MAP_BI);
    private static final BigIntegerHolder TEST_HLD_BI=
        new BigIntegerHolder
        (TEST_BI,
         TEST_ARR_BI,
         TEST_COL_BI,TEST_LST_BI,TEST_LLST_BI,
         TEST_SET_BI,TEST_HSET_BI,TEST_TSET_BI,
         TEST_MAP_BI,TEST_HMAP_BI,TEST_TMAP_BI);

    // Object with inner classes.

    private static final InnerObject TEST_IO=
        new InnerObject(1,2);
    private static final InnerObject[] TEST_ARR_IO=new InnerObject[]
        {new InnerObject(1,2),
         new InnerObject(3,4)};
    private static final List<InnerObject> TEST_LST_IO=
        Arrays.asList(TEST_ARR_IO);
    private static final Collection<InnerObject> TEST_COL_IO=
        TEST_LST_IO;
    private static final LinkedList<InnerObject> TEST_LLST_IO=
        toLinkedList(TEST_LST_IO);
    private static final HashSet<InnerObject> TEST_HSET_IO=
        toHashSet(TEST_LST_IO);
    private static final TreeSet<InnerObject> TEST_TSET_IO=
        toTreeSet(TEST_LST_IO);
    private static final Set<InnerObject> TEST_SET_IO=
        TEST_HSET_IO;
    private static final HashMap<InnerObject,InnerObject> TEST_HMAP_IO=
        toHashMap(TEST_LST_IO);
    private static final MapWrapper<InnerObject,InnerObject>
        TEST_WHMAP_IO=
        new MapWrapper<InnerObject,InnerObject>(TEST_HMAP_IO);
    private static final TreeMap<InnerObject,InnerObject> TEST_TMAP_IO=
        toTreeMap(TEST_LST_IO);
    private static final MapWrapper<InnerObject,InnerObject>
        TEST_WTMAP_IO=
        new MapWrapper<InnerObject,InnerObject>(TEST_TMAP_IO);
    private static final Map<InnerObject,InnerObject> TEST_MAP_IO=
        TEST_HMAP_IO;
    private static final MapWrapper<InnerObject,InnerObject> TEST_WMAP_IO=
        new MapWrapper<InnerObject,InnerObject>(TEST_MAP_IO);
    private static final InnerObjectHolder TEST_HLD_IO=
        new InnerObjectHolder
        (TEST_IO,
         TEST_ARR_IO,
         TEST_COL_IO,TEST_LST_IO,TEST_LLST_IO,
         TEST_SET_IO,TEST_HSET_IO,TEST_TSET_IO,
         TEST_MAP_IO,TEST_HMAP_IO,TEST_TMAP_IO);

    // Enum.

    private static final EnumObject TEST_EO=
        EnumObject.ONE;
    private static final EnumObject[] TEST_ARR_EO=new EnumObject[]
        {EnumObject.ONE,
         EnumObject.TWO};
    private static final List<EnumObject> TEST_LST_EO=
        Arrays.asList(TEST_ARR_EO);
    private static final Collection<EnumObject> TEST_COL_EO=
        TEST_LST_EO;
    private static final LinkedList<EnumObject> TEST_LLST_EO=
        toLinkedList(TEST_LST_EO);
    private static final HashSet<EnumObject> TEST_HSET_EO=
        toHashSet(TEST_LST_EO);
    private static final TreeSet<EnumObject> TEST_TSET_EO=
        toTreeSet(TEST_LST_EO);
    private static final Set<EnumObject> TEST_SET_EO=
        TEST_HSET_EO;
    private static final HashMap<EnumObject,EnumObject> TEST_HMAP_EO=
        toHashMap(TEST_LST_EO);
    private static final MapWrapper<EnumObject,EnumObject>
        TEST_WHMAP_EO=
        new MapWrapper<EnumObject,EnumObject>(TEST_HMAP_EO);
    private static final TreeMap<EnumObject,EnumObject> TEST_TMAP_EO=
        toTreeMap(TEST_LST_EO);
    private static final MapWrapper<EnumObject,EnumObject>
        TEST_WTMAP_EO=
        new MapWrapper<EnumObject,EnumObject>(TEST_TMAP_EO);
    private static final Map<EnumObject,EnumObject> TEST_MAP_EO=
        TEST_HMAP_EO;
    private static final MapWrapper<EnumObject,EnumObject>
        TEST_WMAP_EO=
        new MapWrapper<EnumObject,EnumObject>(TEST_MAP_EO);
    private static final EnumObjectHolder TEST_HLD_EO=
        new EnumObjectHolder
        (TEST_EO,
         TEST_ARR_EO,
         TEST_COL_EO,TEST_LST_EO,TEST_LLST_EO,
         TEST_SET_EO,TEST_HSET_EO,TEST_TSET_EO,
         TEST_MAP_EO,TEST_HMAP_EO,TEST_TMAP_EO);

    // Date.

    private static final Date TEST_DT=
        new Date(1);
    private static final Date[] TEST_ARR_DT=new Date[]
        {new Date(1),
         new Date(2)};
    private static final List<Date> TEST_LST_DT=
        Arrays.asList(TEST_ARR_DT);
    private static final Collection<Date> TEST_COL_DT=
        TEST_LST_DT;
    private static final LinkedList<Date> TEST_LLST_DT=
        toLinkedList(TEST_LST_DT);
    private static final HashSet<Date> TEST_HSET_DT=
        toHashSet(TEST_LST_DT);
    private static final TreeSet<Date> TEST_TSET_DT=
        toTreeSet(TEST_LST_DT);
    private static final Set<Date> TEST_SET_DT=
        TEST_HSET_DT;
    private static final HashMap<Date,Date> TEST_HMAP_DT=
        toHashMap(TEST_LST_DT);
    private static final MapWrapper<Date,Date>
        TEST_WHMAP_DT=
        new MapWrapper<Date,Date>(TEST_HMAP_DT);
    private static final TreeMap<Date,Date> TEST_TMAP_DT=
        toTreeMap(TEST_LST_DT);
    private static final MapWrapper<Date,Date>
        TEST_WTMAP_DT=
        new MapWrapper<Date,Date>(TEST_TMAP_DT);
    private static final Map<Date,Date> TEST_MAP_DT=
        TEST_HMAP_DT;
    private static final MapWrapper<Date,Date>
        TEST_WMAP_DT=
        new MapWrapper<Date,Date>(TEST_MAP_DT);
    private static final DateHolder TEST_HLD_DT=
        new DateHolder
        (TEST_DT,
         TEST_ARR_DT,
         TEST_COL_DT,TEST_LST_DT,TEST_LLST_DT,
         TEST_SET_DT,TEST_HSET_DT,TEST_TSET_DT,
         TEST_MAP_DT,TEST_HMAP_DT,TEST_TMAP_DT);

    // Wrapped date.

    private static final DateWrapper TEST_DW=
        new DateWrapper(new Date(1));
    private static final DateWrapper[] TEST_ARR_DW=new DateWrapper[]
        {new DateWrapper(new Date(1)),
         new DateWrapper(new Date(2))};
    private static final List<DateWrapper> TEST_LST_DW=
        Arrays.asList(TEST_ARR_DW);
    private static final Collection<DateWrapper> TEST_COL_DW=
        TEST_LST_DW;
    private static final LinkedList<DateWrapper> TEST_LLST_DW=
        toLinkedList(TEST_LST_DW);
    private static final HashSet<DateWrapper> TEST_HSET_DW=
        toHashSet(TEST_LST_DW);
    private static final TreeSet<DateWrapper> TEST_TSET_DW=
        toTreeSet(TEST_LST_DW);
    private static final Set<DateWrapper> TEST_SET_DW=
        TEST_HSET_DW;
    private static final HashMap<DateWrapper,DateWrapper> TEST_HMAP_DW=
        toHashMap(TEST_LST_DW);
    private static final MapWrapper<DateWrapper,DateWrapper>
        TEST_WHMAP_DW=
        new MapWrapper<DateWrapper,DateWrapper>(TEST_HMAP_DW);
    private static final TreeMap<DateWrapper,DateWrapper> TEST_TMAP_DW=
        toTreeMap(TEST_LST_DW);
    private static final MapWrapper<DateWrapper,DateWrapper>
        TEST_WTMAP_DW=
        new MapWrapper<DateWrapper,DateWrapper>(TEST_TMAP_DW);
    private static final Map<DateWrapper,DateWrapper> TEST_MAP_DW=
        TEST_HMAP_DW;
    private static final MapWrapper<DateWrapper,DateWrapper>
        TEST_WMAP_DW=
        new MapWrapper<DateWrapper,DateWrapper>(TEST_MAP_DW);
    private static final DateWrapperHolder TEST_HLD_DW=
        new DateWrapperHolder
        (TEST_DW,
         TEST_ARR_DW,
         TEST_COL_DW,TEST_LST_DW,TEST_LLST_DW,
         TEST_SET_DW,TEST_HSET_DW,TEST_TSET_DW,
         TEST_MAP_DW,TEST_HMAP_DW,TEST_TMAP_DW);

    // Locale.

    private static final LocaleWrapper TEST_LWRP=
        new LocaleWrapper(Locale.FRENCH);
    private static final LocaleWrapper[] TEST_ARR_LWRP=new LocaleWrapper[]
        {new LocaleWrapper(Locale.GERMAN),
         new LocaleWrapper(Locale.ITALIAN)};
    private static final List<LocaleWrapper> TEST_LST_LWRP=
        Arrays.asList(TEST_ARR_LWRP);
    private static final Collection<LocaleWrapper> TEST_COL_LWRP=
        TEST_LST_LWRP;
    private static final LinkedList<LocaleWrapper> TEST_LLST_LWRP=
        toLinkedList(TEST_LST_LWRP);
    private static final HashSet<LocaleWrapper> TEST_HSET_LWRP=
        toHashSet(TEST_LST_LWRP);
    private static final Set<LocaleWrapper> TEST_SET_LWRP=
        TEST_HSET_LWRP;
    private static final HashMap<LocaleWrapper,LocaleWrapper> TEST_HMAP_LWRP=
        toHashMap(TEST_LST_LWRP);
    private static final MapWrapper<LocaleWrapper,LocaleWrapper>
        TEST_WHMAP_LWRP=
        new MapWrapper<LocaleWrapper,LocaleWrapper>(TEST_HMAP_LWRP);
    private static final Map<LocaleWrapper,LocaleWrapper> TEST_MAP_LWRP=
        TEST_HMAP_LWRP;
    private static final MapWrapper<LocaleWrapper,LocaleWrapper>
        TEST_WMAP_LWRP=
        new MapWrapper<LocaleWrapper,LocaleWrapper>(TEST_MAP_LWRP);
    private static final LocaleWrapperHolder TEST_HLD_LWRP=
        new LocaleWrapperHolder
        (TEST_LWRP,
         TEST_ARR_LWRP,
         TEST_COL_LWRP,TEST_LST_LWRP,TEST_LLST_LWRP,
         TEST_SET_LWRP,TEST_HSET_LWRP,null,
         TEST_MAP_LWRP,TEST_HMAP_LWRP,null);

    // Wrapper for serializable objects.

    private static final SerWrapper<WrappableObject> TEST_SWRP=
        new SerWrapper<WrappableObject>
        (new WrappableObject(1));
    private static final SerWrapper[] TEST_ARR_SWRP=
        new SerWrapper[]
        {new SerWrapper<WrappableObject>(new WrappableObject(1)),
         new SerWrapper<WrappableObject>(new WrappableObject(2))};
    private static final List<SerWrapper> TEST_LST_SWRP=
        Arrays.asList(TEST_ARR_SWRP);
    private static final Collection<SerWrapper> TEST_COL_SWRP=
        TEST_LST_SWRP;
    private static final LinkedList<SerWrapper> TEST_LLST_SWRP=
        toLinkedList(TEST_LST_SWRP);
    private static final HashSet<SerWrapper> TEST_HSET_SWRP=
        toHashSet(TEST_LST_SWRP);
    private static final TreeSet<SerWrapper> TEST_TSET_SWRP=
        toTreeSet(TEST_LST_SWRP,new SerWrapperComparator());
    private static final Set<SerWrapper> TEST_SET_SWRP=
        TEST_HSET_SWRP;
    private static final HashMap<SerWrapper,
                                 SerWrapper> TEST_HMAP_SWRP=
        toHashMap(TEST_LST_SWRP);
    private static final MapWrapper<SerWrapper,SerWrapper>
        TEST_WHMAP_SWRP=
        new MapWrapper<SerWrapper,SerWrapper>(TEST_HMAP_SWRP);
    private static final TreeMap<SerWrapper,SerWrapper> TEST_TMAP_SWRP=
        toTreeMap(TEST_LST_SWRP,new SerWrapperComparator());
    private static final MapWrapper<SerWrapper,SerWrapper>
        TEST_WTMAP_SWRP=
        new MapWrapper<SerWrapper,SerWrapper>(TEST_TMAP_SWRP);
    private static final Map<SerWrapper,SerWrapper> TEST_MAP_SWRP=
        TEST_HMAP_SWRP;
    private static final MapWrapper<SerWrapper,SerWrapper> TEST_WMAP_SWRP=
        new MapWrapper<SerWrapper,
                       SerWrapper>(TEST_MAP_SWRP);
    /*
     * LIMITATION: when the unmarshaller attempts to build the tree
     * set/map, it does not have access to the original comparator,
     * hence it requires the map elements (SerWrapper) to be
     * Comparable, which they are not. Hence using null instead for
     * the test to pass.
     */
    private static final SerWrapperHolder TEST_HLD_SWRP=
        new SerWrapperHolder
        (TEST_SWRP,
         TEST_ARR_SWRP,
         TEST_COL_SWRP,TEST_LST_SWRP,TEST_LLST_SWRP,
         TEST_SET_SWRP,TEST_HSET_SWRP,null,
         TEST_MAP_SWRP,TEST_HMAP_SWRP,null);

    // Wrapper for comparable & serializable objects.

    private static final CompSerWrapper<WrappableObject> TEST_CWRP=
        new CompSerWrapper<WrappableObject>
        (new WrappableObject(1));
    private static final CompSerWrapper[] TEST_ARR_CWRP=new CompSerWrapper[]
        {new CompSerWrapper<WrappableObject>(new WrappableObject(1)),
         new CompSerWrapper<WrappableObject>(new WrappableObject(2))};
    private static final List<CompSerWrapper> TEST_LST_CWRP=
        Arrays.asList(TEST_ARR_CWRP);
    private static final Collection<CompSerWrapper> TEST_COL_CWRP=
        TEST_LST_CWRP;
    private static final LinkedList<CompSerWrapper> TEST_LLST_CWRP=
        toLinkedList(TEST_LST_CWRP);
    private static final HashSet<CompSerWrapper> TEST_HSET_CWRP=
        toHashSet(TEST_LST_CWRP);
    private static final TreeSet<CompSerWrapper> TEST_TSET_CWRP=
        toTreeSet(TEST_LST_CWRP);
    private static final Set<CompSerWrapper> TEST_SET_CWRP=
        TEST_HSET_CWRP;
    private static final HashMap<CompSerWrapper,CompSerWrapper> TEST_HMAP_CWRP=
        toHashMap(TEST_LST_CWRP);
    private static final MapWrapper<CompSerWrapper,CompSerWrapper>
        TEST_WHMAP_CWRP=
        new MapWrapper<CompSerWrapper,CompSerWrapper>(TEST_HMAP_CWRP);
    private static final TreeMap<CompSerWrapper,CompSerWrapper> TEST_TMAP_CWRP=
        toTreeMap(TEST_LST_CWRP);
    private static final MapWrapper<CompSerWrapper,CompSerWrapper>
        TEST_WTMAP_CWRP=
        new MapWrapper<CompSerWrapper,CompSerWrapper>(TEST_TMAP_CWRP);
    private static final Map<CompSerWrapper,CompSerWrapper> TEST_MAP_CWRP=
        TEST_HMAP_CWRP;
    private static final MapWrapper<CompSerWrapper,CompSerWrapper>
        TEST_WMAP_CWRP=
        new MapWrapper<CompSerWrapper,CompSerWrapper>(TEST_MAP_CWRP);
    private static final CompSerWrapperHolder TEST_HLD_CWRP=
        new CompSerWrapperHolder
        (TEST_CWRP,
         TEST_ARR_CWRP,
         TEST_COL_CWRP,TEST_LST_CWRP,TEST_LLST_CWRP,
         TEST_SET_CWRP,TEST_HSET_CWRP,TEST_TSET_CWRP,
         TEST_MAP_CWRP,TEST_HMAP_CWRP,TEST_TMAP_CWRP);

    // Faults.

    private static final String TEST_MSG=
        "a";


    // UTILITIES.

    // Test data generation.

    private static <V> LinkedList<V> toLinkedList
        (List<V> in)
    {
        LinkedList<V> out=new LinkedList<V>();
        out.addAll(in);
        return out;
    }

    private static <V> HashSet<V> toHashSet
        (List<V> in)
    {
        HashSet<V> out=new HashSet<V>();
        out.addAll(in);
        return out;
    }

    private static <V> TreeSet<V> toTreeSet
        (List<V> in,
         Comparator<? super V> comparator)
    {
        TreeSet<V> out;
        if (comparator==null) {
            out=new TreeSet<V>();
        } else {
            out=new TreeSet<V>(comparator);
        }
        out.addAll(in);
        return out;
    }

    private static <V> TreeSet<V> toTreeSet
        (List<V> in)
    {
        return toTreeSet(in,null);
    }

    private static <V> HashMap<V,V> toHashMap
        (List<V> in)
    {
        HashMap<V,V> out=new HashMap<V,V>();
        for (V item:in) {
            out.put(item,item);
        }
        return out;
    }

    private static <V> TreeMap<V,V> toTreeMap
        (List<V> in,
         Comparator<? super V> comparator)
    {
        TreeMap<V,V> out;
        if (comparator==null) {
            out=new TreeMap<V,V>();
        } else {
            out=new TreeMap<V,V>(comparator);
        }
        for (V item:in) {
            out.put(item,item);
        }
        return out;
    }

    private static <V> TreeMap<V,V> toTreeMap
        (List<V> in)
    {
        return toTreeMap(in,null);
    }

    // Assertions.

    private static void assertColEquals
        (Collection<?> expected,
         Collection<?> actual)
    {
        assertEquals(expected,actual);
        /*
         * LIMITATION: the unmarshalled collection is an ArrayList
         * regardless of the actual type of the original collection.
         */
        assertEquals(ArrayList.class,actual.getClass());
    }

    private static void assertSetEquals
        (Set<?> expected,
         Set<?> actual)
    {
        assertEquals(expected,actual);
        /*
         * LIMITATION: the unmarshalled set is a HashSet regardless of
         * the actual type of the original set.
         */
        assertEquals(HashSet.class,actual.getClass());
    }

    private static void assertMapEquals
        (MapWrapper<?,?> expected,
         MapWrapper<?,?> actual)
    {
        assertEquals(expected.getMap(),actual.getMap());
        /*
         * LIMITATION: the unmarshalled map is always a HashMap
         * regardless of the actual type of the original map.
         */
        assertEquals(HashMap.class,actual.getMap().getClass());
    }


    // TEST.

    @Test
    public void arguments()
        throws Exception
    {
        StatelessServer server=new StatelessServer();
        server.publish(new ServiceImpl(),Service.class);
        StatelessClient client=new StatelessClient();
        Service i=client.getService(Service.class);

        // Raw maps.

        /*
         * LIMITATION: maps are not properly marshalled (resulting
         * map is empty).
        assertEquals(TEST_HMAP_BOOLEAN,
                     i.hMap(client.getContext(),TEST_HMAP_BOOLEAN));
        assertEquals(TEST_TMAP_BOOLEAN,
                     i.tMap(client.getContext(),TEST_TMAP_BOOLEAN));
         */

        // Boolean.

        assertEquals(TEST_P_BOOLEAN,
                     i.pBoolean(client.getContext(),TEST_P_BOOLEAN));
        assertEquals(TEST_O_BOOLEAN,
                     i.oBoolean(client.getContext(),TEST_O_BOOLEAN));
        assertTrue(ArrayUtils.isEquals
                   (TEST_PARR_BOOLEAN,
                    i.pArrBoolean(client.getContext(),TEST_PARR_BOOLEAN)));
        assertTrue(ArrayUtils.isEquals
                   (TEST_OARR_BOOLEAN,
                    i.oArrBoolean(client.getContext(),TEST_OARR_BOOLEAN)));
        assertColEquals(TEST_COL_BOOLEAN,
                        i.colBoolean(client.getContext(),TEST_COL_BOOLEAN));
        assertColEquals(TEST_LST_BOOLEAN,
                        i.colBoolean(client.getContext(),TEST_LST_BOOLEAN));
        assertColEquals(TEST_LLST_BOOLEAN,
                        i.colBoolean(client.getContext(),TEST_LLST_BOOLEAN));
        assertSetEquals(TEST_SET_BOOLEAN,
                        i.setBoolean(client.getContext(),TEST_SET_BOOLEAN));
        assertSetEquals(TEST_HSET_BOOLEAN,
                        i.setBoolean(client.getContext(),TEST_HSET_BOOLEAN));
        assertSetEquals(TEST_TSET_BOOLEAN,
                        i.setBoolean(client.getContext(),TEST_TSET_BOOLEAN));
        assertMapEquals(TEST_WMAP_BOOLEAN,
                        i.mapBoolean(client.getContext(),TEST_WMAP_BOOLEAN));
        assertMapEquals(TEST_WHMAP_BOOLEAN,
                        i.mapBoolean(client.getContext(),TEST_WHMAP_BOOLEAN));
        assertMapEquals(TEST_WTMAP_BOOLEAN,
                        i.mapBoolean(client.getContext(),TEST_WTMAP_BOOLEAN));
        assertEquals(TEST_HLD_BOOLEAN,
                     i.hldBoolean(client.getContext(),TEST_HLD_BOOLEAN));

        // Byte.

        assertEquals(TEST_P_BYTE,
                     i.pByte(client.getContext(),TEST_P_BYTE));
        assertEquals(TEST_O_BYTE,
                     i.oByte(client.getContext(),TEST_O_BYTE));
        assertArrayEquals(TEST_PARR_BYTE,
                          i.pArrByte(client.getContext(),TEST_PARR_BYTE));
        assertArrayEquals(TEST_OARR_BYTE,
                          i.oArrByte(client.getContext(),TEST_OARR_BYTE));
        assertColEquals(TEST_COL_BYTE,
                        i.colByte(client.getContext(),TEST_COL_BYTE));
        assertColEquals(TEST_LST_BYTE,
                        i.colByte(client.getContext(),TEST_LST_BYTE));
        assertColEquals(TEST_LLST_BYTE,
                        i.colByte(client.getContext(),TEST_LLST_BYTE));
        assertSetEquals(TEST_SET_BYTE,
                        i.setByte(client.getContext(),TEST_SET_BYTE));
        assertSetEquals(TEST_HSET_BYTE,
                        i.setByte(client.getContext(),TEST_HSET_BYTE));
        assertSetEquals(TEST_TSET_BYTE,
                        i.setByte(client.getContext(),TEST_TSET_BYTE));
        assertMapEquals(TEST_WMAP_BYTE,
                        i.mapByte(client.getContext(),TEST_WMAP_BYTE));
        assertMapEquals(TEST_WHMAP_BYTE,
                        i.mapByte(client.getContext(),TEST_WHMAP_BYTE));
        assertMapEquals(TEST_WTMAP_BYTE,
                        i.mapByte(client.getContext(),TEST_WTMAP_BYTE));
        assertEquals(TEST_HLD_BYTE,
                     i.hldByte(client.getContext(),TEST_HLD_BYTE));

        // Character.

        assertEquals(TEST_P_CHAR,
                     i.pChar(client.getContext(),TEST_P_CHAR));
        assertEquals(TEST_O_CHAR,
                     i.oChar(client.getContext(),TEST_O_CHAR));
        assertArrayEquals(TEST_PARR_CHAR,
                          i.pArrChar(client.getContext(),TEST_PARR_CHAR));
        assertArrayEquals(TEST_OARR_CHAR,
                          i.oArrChar(client.getContext(),TEST_OARR_CHAR));
        assertColEquals(TEST_COL_CHAR,
                        i.colChar(client.getContext(),TEST_COL_CHAR));
        assertColEquals(TEST_LST_CHAR,
                        i.colChar(client.getContext(),TEST_LST_CHAR));
        assertColEquals(TEST_LLST_CHAR,
                        i.colChar(client.getContext(),TEST_LLST_CHAR));
        assertSetEquals(TEST_SET_CHAR,
                        i.setChar(client.getContext(),TEST_SET_CHAR));
        assertSetEquals(TEST_HSET_CHAR,
                        i.setChar(client.getContext(),TEST_HSET_CHAR));
        assertSetEquals(TEST_TSET_CHAR,
                        i.setChar(client.getContext(),TEST_TSET_CHAR));
        /*
         * LIMITATION: maps of Characters are treated as integers.
        assertMapEquals(TEST_WMAP_CHAR,
                        i.mapChar(client.getContext(),TEST_WMAP_CHAR));
        assertMapEquals(TEST_WHMAP_CHAR,
                        i.mapChar(client.getContext(),TEST_WHMAP_CHAR));
        assertMapEquals(TEST_WTMAP_CHAR,
                        i.mapChar(client.getContext(),TEST_WTMAP_CHAR));
         */
        assertEquals(TEST_HLD_CHAR,
                     i.hldChar(client.getContext(),TEST_HLD_CHAR));

        // Double.

        assertEquals(TEST_P_DOUBLE,
                     i.pDouble(client.getContext(),TEST_P_DOUBLE),0.0001);
        assertEquals(TEST_O_DOUBLE,
                     i.oDouble(client.getContext(),TEST_O_DOUBLE),0.0001);
        assertTrue(ArrayUtils.isEquals
                   (TEST_PARR_DOUBLE,
                    i.pArrDouble(client.getContext(),TEST_PARR_DOUBLE)));
        assertTrue(ArrayUtils.isEquals
                   (TEST_OARR_DOUBLE,
                    i.oArrDouble(client.getContext(),TEST_OARR_DOUBLE)));
        assertColEquals(TEST_COL_DOUBLE,
                        i.colDouble(client.getContext(),TEST_COL_DOUBLE));
        assertColEquals(TEST_LST_DOUBLE,
                        i.colDouble(client.getContext(),TEST_LST_DOUBLE));
        assertColEquals(TEST_LLST_DOUBLE,
                        i.colDouble(client.getContext(),TEST_LLST_DOUBLE));
        assertSetEquals(TEST_SET_DOUBLE,
                        i.setDouble(client.getContext(),TEST_SET_DOUBLE));
        assertSetEquals(TEST_HSET_DOUBLE,
                        i.setDouble(client.getContext(),TEST_HSET_DOUBLE));
        assertSetEquals(TEST_TSET_DOUBLE,
                        i.setDouble(client.getContext(),TEST_TSET_DOUBLE));
        assertMapEquals(TEST_WMAP_DOUBLE,
                        i.mapDouble(client.getContext(),TEST_WMAP_DOUBLE));
        assertMapEquals(TEST_WHMAP_DOUBLE,
                        i.mapDouble(client.getContext(),TEST_WHMAP_DOUBLE));
        assertMapEquals(TEST_WTMAP_DOUBLE,
                        i.mapDouble(client.getContext(),TEST_WTMAP_DOUBLE));
        assertEquals(TEST_HLD_DOUBLE,
                     i.hldDouble(client.getContext(),TEST_HLD_DOUBLE));

        // Float.

        assertEquals(TEST_P_FLOAT,
                     i.pFloat(client.getContext(),TEST_P_FLOAT),0.0001F);
        assertEquals(TEST_O_FLOAT,
                     i.oFloat(client.getContext(),TEST_O_FLOAT),0.0001F);
        assertTrue(ArrayUtils.isEquals
                   (TEST_PARR_FLOAT,
                    i.pArrFloat(client.getContext(),TEST_PARR_FLOAT)));
        assertTrue(ArrayUtils.isEquals
                   (TEST_OARR_FLOAT,
                    i.oArrFloat(client.getContext(),TEST_OARR_FLOAT)));
        assertColEquals(TEST_COL_FLOAT,
                        i.colFloat(client.getContext(),TEST_COL_FLOAT));
        assertColEquals(TEST_LST_FLOAT,
                        i.colFloat(client.getContext(),TEST_LST_FLOAT));
        assertColEquals(TEST_LLST_FLOAT,
                        i.colFloat(client.getContext(),TEST_LLST_FLOAT));
        assertSetEquals(TEST_SET_FLOAT,
                        i.setFloat(client.getContext(),TEST_SET_FLOAT));
        assertSetEquals(TEST_HSET_FLOAT,
                        i.setFloat(client.getContext(),TEST_HSET_FLOAT));
        assertSetEquals(TEST_TSET_FLOAT,
                        i.setFloat(client.getContext(),TEST_TSET_FLOAT));
        assertMapEquals(TEST_WMAP_FLOAT,
                        i.mapFloat(client.getContext(),TEST_WMAP_FLOAT));
        assertMapEquals(TEST_WHMAP_FLOAT,
                        i.mapFloat(client.getContext(),TEST_WHMAP_FLOAT));
        assertMapEquals(TEST_WTMAP_FLOAT,
                        i.mapFloat(client.getContext(),TEST_WTMAP_FLOAT));
        assertEquals(TEST_HLD_FLOAT,
                     i.hldFloat(client.getContext(),TEST_HLD_FLOAT));

        // Integer.

        assertEquals(TEST_P_INT,
                     i.pInt(client.getContext(),TEST_P_INT));
        assertEquals(TEST_O_INT,
                     i.oInt(client.getContext(),TEST_O_INT));
        assertArrayEquals(TEST_PARR_INT,
                          i.pArrInt(client.getContext(),TEST_PARR_INT));
        assertArrayEquals(TEST_OARR_INT,
                          i.oArrInt(client.getContext(),TEST_OARR_INT));
        assertColEquals(TEST_COL_INT,
                        i.colInt(client.getContext(),TEST_COL_INT));
        assertColEquals(TEST_LST_INT,
                        i.colInt(client.getContext(),TEST_LST_INT));
        assertColEquals(TEST_LLST_INT,
                        i.colInt(client.getContext(),TEST_LLST_INT));
        assertSetEquals(TEST_SET_INT,
                        i.setInt(client.getContext(),TEST_SET_INT));
        assertSetEquals(TEST_HSET_INT,
                        i.setInt(client.getContext(),TEST_HSET_INT));
        assertSetEquals(TEST_TSET_INT,
                        i.setInt(client.getContext(),TEST_TSET_INT));
        assertMapEquals(TEST_WMAP_INT,
                        i.mapInt(client.getContext(),TEST_WMAP_INT));
        assertMapEquals(TEST_WHMAP_INT,
                        i.mapInt(client.getContext(),TEST_WHMAP_INT));
        assertMapEquals(TEST_WTMAP_INT,
                        i.mapInt(client.getContext(),TEST_WTMAP_INT));
        assertEquals(TEST_HLD_INT,
                     i.hldInt(client.getContext(),TEST_HLD_INT));

        // Long.

        assertEquals(TEST_P_LONG,
                     i.pLong(client.getContext(),TEST_P_LONG));
        assertEquals(TEST_O_LONG,
                     i.oLong(client.getContext(),TEST_O_LONG));
        assertArrayEquals(TEST_PARR_LONG,
                          i.pArrLong(client.getContext(),TEST_PARR_LONG));
        assertArrayEquals(TEST_OARR_LONG,
                          i.oArrLong(client.getContext(),TEST_OARR_LONG));
        assertColEquals(TEST_COL_LONG,
                        i.colLong(client.getContext(),TEST_COL_LONG));
        assertColEquals(TEST_LST_LONG,
                        i.colLong(client.getContext(),TEST_LST_LONG));
        assertColEquals(TEST_LLST_LONG,
                        i.colLong(client.getContext(),TEST_LLST_LONG));
        assertSetEquals(TEST_SET_LONG,
                        i.setLong(client.getContext(),TEST_SET_LONG));
        assertSetEquals(TEST_HSET_LONG,
                        i.setLong(client.getContext(),TEST_HSET_LONG));
        assertSetEquals(TEST_TSET_LONG,
                        i.setLong(client.getContext(),TEST_TSET_LONG));
        assertMapEquals(TEST_WMAP_LONG,
                        i.mapLong(client.getContext(),TEST_WMAP_LONG));
        assertMapEquals(TEST_WHMAP_LONG,
                        i.mapLong(client.getContext(),TEST_WHMAP_LONG));
        assertMapEquals(TEST_WTMAP_LONG,
                        i.mapLong(client.getContext(),TEST_WTMAP_LONG));
        assertEquals(TEST_HLD_LONG,
                     i.hldLong(client.getContext(),TEST_HLD_LONG));

        // Short.

        assertEquals(TEST_P_SHORT,
                     i.pShort(client.getContext(),TEST_P_SHORT));
        assertEquals(TEST_O_SHORT,
                     i.oShort(client.getContext(),TEST_O_SHORT));
        assertArrayEquals(TEST_PARR_SHORT,
                          i.pArrShort(client.getContext(),TEST_PARR_SHORT));
        assertArrayEquals(TEST_OARR_SHORT,
                          i.oArrShort(client.getContext(),TEST_OARR_SHORT));
        assertColEquals(TEST_COL_SHORT,
                        i.colShort(client.getContext(),TEST_COL_SHORT));
        assertColEquals(TEST_LST_SHORT,
                        i.colShort(client.getContext(),TEST_LST_SHORT));
        assertColEquals(TEST_LLST_SHORT,
                        i.colShort(client.getContext(),TEST_LLST_SHORT));
        assertSetEquals(TEST_SET_SHORT,
                        i.setShort(client.getContext(),TEST_SET_SHORT));
        assertSetEquals(TEST_HSET_SHORT,
                        i.setShort(client.getContext(),TEST_HSET_SHORT));
        assertSetEquals(TEST_TSET_SHORT,
                        i.setShort(client.getContext(),TEST_TSET_SHORT));
        assertMapEquals(TEST_WMAP_SHORT,
                        i.mapShort(client.getContext(),TEST_WMAP_SHORT));
        assertMapEquals(TEST_WHMAP_SHORT,
                        i.mapShort(client.getContext(),TEST_WHMAP_SHORT));
        assertMapEquals(TEST_WTMAP_SHORT,
                        i.mapShort(client.getContext(),TEST_WTMAP_SHORT));
        assertEquals(TEST_HLD_SHORT,
                     i.hldShort(client.getContext(),TEST_HLD_SHORT));

        // String.

        assertEquals(TEST_STR,
                     i.str(client.getContext(),TEST_STR));
        assertArrayEquals(TEST_ARR_STR,
                          i.arrStr(client.getContext(),TEST_ARR_STR));
        assertColEquals(TEST_COL_STR,
                        i.colStr(client.getContext(),TEST_COL_STR));
        assertColEquals(TEST_LST_STR,
                        i.colStr(client.getContext(),TEST_LST_STR));
        assertColEquals(TEST_LLST_STR,
                        i.colStr(client.getContext(),TEST_LLST_STR));
        assertSetEquals(TEST_SET_STR,
                        i.setStr(client.getContext(),TEST_SET_STR));
        assertSetEquals(TEST_HSET_STR,
                        i.setStr(client.getContext(),TEST_HSET_STR));
        assertSetEquals(TEST_TSET_STR,
                        i.setStr(client.getContext(),TEST_TSET_STR));
        assertMapEquals(TEST_WMAP_STR,
                        i.mapStr(client.getContext(),TEST_WMAP_STR));
        assertMapEquals(TEST_WHMAP_STR,
                        i.mapStr(client.getContext(),TEST_WHMAP_STR));
        assertMapEquals(TEST_WTMAP_STR,
                        i.mapStr(client.getContext(),TEST_WTMAP_STR));
        assertEquals(TEST_HLD_STR,
                     i.hldStr(client.getContext(),TEST_HLD_STR));

        // Big decimal.

        assertEquals(TEST_BD,
                     i.bd(client.getContext(),TEST_BD));
        assertArrayEquals(TEST_ARR_BD,
                          i.arrBd(client.getContext(),TEST_ARR_BD));
        assertColEquals(TEST_COL_BD,
                        i.colBd(client.getContext(),TEST_COL_BD));
        assertColEquals(TEST_LST_BD,
                        i.colBd(client.getContext(),TEST_LST_BD));
        assertColEquals(TEST_LLST_BD,
                        i.colBd(client.getContext(),TEST_LLST_BD));
        assertSetEquals(TEST_SET_BD,
                        i.setBd(client.getContext(),TEST_SET_BD));
        assertSetEquals(TEST_HSET_BD,
                        i.setBd(client.getContext(),TEST_HSET_BD));
        assertSetEquals(TEST_TSET_BD,
                        i.setBd(client.getContext(),TEST_TSET_BD));
        assertMapEquals(TEST_WMAP_BD,
                        i.mapBd(client.getContext(),TEST_WMAP_BD));
        assertMapEquals(TEST_WHMAP_BD,
                        i.mapBd(client.getContext(),TEST_WHMAP_BD));
        assertMapEquals(TEST_WTMAP_BD,
                        i.mapBd(client.getContext(),TEST_WTMAP_BD));
        assertEquals(TEST_HLD_BD,
                     i.hldBd(client.getContext(),TEST_HLD_BD));

        // Big integer.

        assertEquals(TEST_BI,
                     i.bi(client.getContext(),TEST_BI));
        assertArrayEquals(TEST_ARR_BI,
                          i.arrBi(client.getContext(),TEST_ARR_BI));
        assertColEquals(TEST_COL_BI,
                        i.colBi(client.getContext(),TEST_COL_BI));
        assertColEquals(TEST_LST_BI,
                        i.colBi(client.getContext(),TEST_LST_BI));
        assertColEquals(TEST_LLST_BI,
                        i.colBi(client.getContext(),TEST_LLST_BI));
        assertSetEquals(TEST_SET_BI,
                        i.setBi(client.getContext(),TEST_SET_BI));
        assertSetEquals(TEST_HSET_BI,
                        i.setBi(client.getContext(),TEST_HSET_BI));
        assertSetEquals(TEST_TSET_BI,
                        i.setBi(client.getContext(),TEST_TSET_BI));
        assertMapEquals(TEST_WMAP_BI,
                        i.mapBi(client.getContext(),TEST_WMAP_BI));
        assertMapEquals(TEST_WHMAP_BI,
                        i.mapBi(client.getContext(),TEST_WHMAP_BI));
        assertMapEquals(TEST_WTMAP_BI,
                        i.mapBi(client.getContext(),TEST_WTMAP_BI));
        assertEquals(TEST_HLD_BI,
                     i.hldBi(client.getContext(),TEST_HLD_BI));

        // Object with inner classes.

        assertEquals(TEST_IO,
                     i.io(client.getContext(),TEST_IO));
        assertArrayEquals(TEST_ARR_IO,
                          i.arrIo(client.getContext(),TEST_ARR_IO));
        assertColEquals(TEST_COL_IO,
                        i.colIo(client.getContext(),TEST_COL_IO));
        assertColEquals(TEST_LST_IO,
                        i.colIo(client.getContext(),TEST_LST_IO));
        assertColEquals(TEST_LLST_IO,
                        i.colIo(client.getContext(),TEST_LLST_IO));
        assertSetEquals(TEST_SET_IO,
                        i.setIo(client.getContext(),TEST_SET_IO));
        assertSetEquals(TEST_HSET_IO,
                        i.setIo(client.getContext(),TEST_HSET_IO));
        assertSetEquals(TEST_TSET_IO,
                        i.setIo(client.getContext(),TEST_TSET_IO));
        assertMapEquals(TEST_WMAP_IO,
                        i.mapIo(client.getContext(),TEST_WMAP_IO));
        assertMapEquals(TEST_WHMAP_IO,
                        i.mapIo(client.getContext(),TEST_WHMAP_IO));
        assertMapEquals(TEST_WTMAP_IO,
                        i.mapIo(client.getContext(),TEST_WTMAP_IO));
        assertEquals(TEST_HLD_IO,
                     i.hldIo(client.getContext(),TEST_HLD_IO));

        // Enum.

        assertEquals(TEST_EO,
                     i.eo(client.getContext(),TEST_EO));
        assertArrayEquals(TEST_ARR_EO,
                          i.arrEo(client.getContext(),TEST_ARR_EO));
        assertColEquals(TEST_COL_EO,
                        i.colEo(client.getContext(),TEST_COL_EO));
        assertColEquals(TEST_LST_EO,
                        i.colEo(client.getContext(),TEST_LST_EO));
        assertColEquals(TEST_LLST_EO,
                        i.colEo(client.getContext(),TEST_LLST_EO));
        assertSetEquals(TEST_SET_EO,
                        i.setEo(client.getContext(),TEST_SET_EO));
        assertSetEquals(TEST_HSET_EO,
                        i.setEo(client.getContext(),TEST_HSET_EO));
        assertSetEquals(TEST_TSET_EO,
                        i.setEo(client.getContext(),TEST_TSET_EO));
        assertMapEquals(TEST_WMAP_EO,
                        i.mapEo(client.getContext(),TEST_WMAP_EO));
        assertMapEquals(TEST_WHMAP_EO,
                        i.mapEo(client.getContext(),TEST_WHMAP_EO));
        assertMapEquals(TEST_WTMAP_EO,
                        i.mapEo(client.getContext(),TEST_WTMAP_EO));
        assertEquals(TEST_HLD_EO,
                     i.hldEo(client.getContext(),TEST_HLD_EO));
        // Date.

        /*
         * LIMITATION: Dates fail in certain timezones such as GMT due to
         * a JAXB bug.
        assertEquals(TEST_DT,
                     i.dt(client.getContext(),TEST_DT));
        assertArrayEquals(TEST_ARR_DT,
                          i.arrDt(client.getContext(),TEST_ARR_DT));
        assertColEquals(TEST_COL_DT,
                        i.colDt(client.getContext(),TEST_COL_DT));
        assertColEquals(TEST_LST_DT,
                        i.colDt(client.getContext(),TEST_LST_DT));
        assertColEquals(TEST_LLST_DT,
                        i.colDt(client.getContext(),TEST_LLST_DT));
        assertSetEquals(TEST_SET_DT,
                        i.setDt(client.getContext(),TEST_SET_DT));
        assertSetEquals(TEST_HSET_DT,
                        i.setDt(client.getContext(),TEST_HSET_DT));
        assertSetEquals(TEST_TSET_DT,
                        i.setDt(client.getContext(),TEST_TSET_DT));
        */
        /*
         * LIMITATION: maps of Dates are treated as calendars.
        assertMapEquals(TEST_WMAP_DT,
                        i.mapDt(client.getContext(),TEST_WMAP_DT));
        assertMapEquals(TEST_WHMAP_DT,
                        i.mapDt(client.getContext(),TEST_WHMAP_DT));
        assertMapEquals(TEST_WTMAP_DT,
                        i.mapDt(client.getContext(),TEST_WTMAP_DT));
        */
        /*
         * LIMITATION: Dates fail in certain timezones such as GMT due to
         * a JAXB bug.
        assertEquals(TEST_HLD_DT,
                     i.hldDt(client.getContext(),TEST_HLD_DT));
        */

        // Wrapped date.

        assertEquals(TEST_DW,
                     i.dw(client.getContext(),TEST_DW));
        assertArrayEquals(TEST_ARR_DW,
                          i.arrDw(client.getContext(),TEST_ARR_DW));
        assertColEquals(TEST_COL_DW,
                        i.colDw(client.getContext(),TEST_COL_DW));
        assertColEquals(TEST_LST_DW,
                        i.colDw(client.getContext(),TEST_LST_DW));
        assertColEquals(TEST_LLST_DW,
                        i.colDw(client.getContext(),TEST_LLST_DW));
        assertSetEquals(TEST_SET_DW,
                        i.setDw(client.getContext(),TEST_SET_DW));
        assertSetEquals(TEST_HSET_DW,
                        i.setDw(client.getContext(),TEST_HSET_DW));
        assertSetEquals(TEST_TSET_DW,
                        i.setDw(client.getContext(),TEST_TSET_DW));
        assertMapEquals(TEST_WMAP_DW,
                        i.mapDw(client.getContext(),TEST_WMAP_DW));
        assertMapEquals(TEST_WHMAP_DW,
                        i.mapDw(client.getContext(),TEST_WHMAP_DW));
        assertMapEquals(TEST_WTMAP_DW,
                        i.mapDw(client.getContext(),TEST_WTMAP_DW));
        assertEquals(TEST_HLD_DW,
                     i.hldDw(client.getContext(),TEST_HLD_DW));

        // Locale.

        assertEquals(TEST_LWRP,
                     i.lwrp(client.getContext(),TEST_LWRP));
        assertArrayEquals(TEST_ARR_LWRP,
                          i.arrLwrp(client.getContext(),TEST_ARR_LWRP));
        assertColEquals(TEST_COL_LWRP,
                        i.colLwrp(client.getContext(),TEST_COL_LWRP));
        assertColEquals(TEST_LST_LWRP,
                        i.colLwrp(client.getContext(),TEST_LST_LWRP));
        assertColEquals(TEST_LLST_LWRP,
                        i.colLwrp(client.getContext(),TEST_LLST_LWRP));
        assertSetEquals(TEST_SET_LWRP,
                        i.setLwrp(client.getContext(),TEST_SET_LWRP));
        assertSetEquals(TEST_HSET_LWRP,
                        i.setLwrp(client.getContext(),TEST_HSET_LWRP));
        assertMapEquals(TEST_WMAP_LWRP,
                        i.mapLwrp(client.getContext(),TEST_WMAP_LWRP));
        assertMapEquals(TEST_WHMAP_LWRP,
                        i.mapLwrp(client.getContext(),TEST_WHMAP_LWRP));
        assertEquals(TEST_HLD_LWRP,
                     i.hldLwrp(client.getContext(),TEST_HLD_LWRP));

        // Wrapper for serializable objects.

        assertEquals(TEST_SWRP,
                     i.swrp(client.getContext(),TEST_SWRP));
        assertArrayEquals(TEST_ARR_SWRP,
                          i.arrSwrp(client.getContext(),TEST_ARR_SWRP));
        assertColEquals(TEST_COL_SWRP,
                        i.colSwrp(client.getContext(),TEST_COL_SWRP));
        assertColEquals(TEST_LST_SWRP,
                        i.colSwrp(client.getContext(),TEST_LST_SWRP));
        assertColEquals(TEST_LLST_SWRP,
                        i.colSwrp(client.getContext(),TEST_LLST_SWRP));
        assertSetEquals(TEST_SET_SWRP,
                        i.setSwrp(client.getContext(),TEST_SET_SWRP));
        assertSetEquals(TEST_HSET_SWRP,
                        i.setSwrp(client.getContext(),TEST_HSET_SWRP));
        assertSetEquals(TEST_TSET_SWRP,
                        i.setSwrp(client.getContext(),TEST_TSET_SWRP));
        assertMapEquals(TEST_WMAP_SWRP,
                        i.mapSwrp(client.getContext(),TEST_WMAP_SWRP));
        assertMapEquals(TEST_WHMAP_SWRP,
                        i.mapSwrp(client.getContext(),TEST_WHMAP_SWRP));
        assertMapEquals(TEST_WTMAP_SWRP,
                        i.mapSwrp(client.getContext(),TEST_WTMAP_SWRP));
        assertEquals(TEST_HLD_SWRP,
                     i.hldSwrp(client.getContext(),TEST_HLD_SWRP));

        // Wrapper for comparable & serializable objects.

        assertEquals(TEST_CWRP,
                     i.cwrp(client.getContext(),TEST_CWRP));
        assertArrayEquals(TEST_ARR_CWRP,
                          i.arrCwrp(client.getContext(),TEST_ARR_CWRP));
        assertColEquals(TEST_COL_CWRP,
                        i.colCwrp(client.getContext(),TEST_COL_CWRP));
        assertColEquals(TEST_LST_CWRP,
                        i.colCwrp(client.getContext(),TEST_LST_CWRP));
        assertColEquals(TEST_LLST_CWRP,
                        i.colCwrp(client.getContext(),TEST_LLST_CWRP));
        assertSetEquals(TEST_SET_CWRP,
                        i.setCwrp(client.getContext(),TEST_SET_CWRP));
        assertSetEquals(TEST_HSET_CWRP,
                        i.setCwrp(client.getContext(),TEST_HSET_CWRP));
        assertSetEquals(TEST_TSET_CWRP,
                        i.setCwrp(client.getContext(),TEST_TSET_CWRP));
        assertMapEquals(TEST_WMAP_CWRP,
                        i.mapCwrp(client.getContext(),TEST_WMAP_CWRP));
        assertMapEquals(TEST_WHMAP_CWRP,
                        i.mapCwrp(client.getContext(),TEST_WHMAP_CWRP));
        assertMapEquals(TEST_WTMAP_CWRP,
                        i.mapCwrp(client.getContext(),TEST_WTMAP_CWRP));
        assertEquals(TEST_HLD_CWRP,
                     i.hldCwrp(client.getContext(),TEST_HLD_CWRP));

        // Faults.

        try {
            i.checkedException(client.getContext(),TEST_MSG);
            fail();
        } catch (RemoteException ex) {
            assertTrue(ex.getCause().getClass().getName(),
                       ex.getCause() instanceof
                       Service.CustomCheckedException);
            assertEquals(TEST_MSG,ex.getCause().getMessage());
        }

        try {
            i.runtimeException(client.getContext(),TEST_MSG);
            fail();
        } catch (RemoteException ex) {
            assertTrue(ex.getCause().getClass().getName(),
                       ex.getCause() instanceof 
                       Service.CustomRuntimeException);
            assertEquals(TEST_MSG,ex.getCause().getMessage());
        }

        try {
            i.error(client.getContext(),TEST_MSG);
            fail();
        } catch (RemoteException ex) {
            assertTrue(ex.getCause().getClass().getName(),
                       ex.getCause() instanceof
                       Service.CustomError);
            assertEquals(TEST_MSG,ex.getCause().getMessage());
        }
    }
}
