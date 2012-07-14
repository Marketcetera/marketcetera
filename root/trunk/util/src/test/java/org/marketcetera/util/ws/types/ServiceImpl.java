package org.marketcetera.util.ws.types;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeMap;
import org.marketcetera.util.ws.stateless.StatelessClientContext;
import org.marketcetera.util.ws.stateless.StatelessRemoteCaller;
import org.marketcetera.util.ws.stateless.StatelessRemoteRunner;
import org.marketcetera.util.ws.stateless.StatelessServiceBaseImpl;
import org.marketcetera.util.ws.wrappers.CompSerWrapper;
import org.marketcetera.util.ws.wrappers.DateWrapper;
import org.marketcetera.util.ws.wrappers.LocaleWrapper;
import org.marketcetera.util.ws.wrappers.MapWrapper;
import org.marketcetera.util.ws.wrappers.RemoteException;
import org.marketcetera.util.ws.wrappers.SerWrapper;

/**
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id$
 */

/* $License$ */

public class ServiceImpl
    extends StatelessServiceBaseImpl
    implements Service
{

    // Raw maps.

    @Override
    public HashMap<Boolean,Boolean> hMap
        (StatelessClientContext context,
         final HashMap<Boolean,Boolean> arg)
        throws RemoteException
    {
        return new StatelessRemoteCaller<HashMap<Boolean,Boolean>>() {
            @Override
            protected HashMap<Boolean,Boolean> call
                (StatelessClientContext context)
            {
                return arg;
            }
        }.execute(context);
    }

    @Override
    public TreeMap<Boolean,Boolean> tMap
        (StatelessClientContext context,
         final TreeMap<Boolean,Boolean> arg)
        throws RemoteException
    {
        return new StatelessRemoteCaller<TreeMap<Boolean,Boolean>>() {
            @Override
            protected TreeMap<Boolean,Boolean> call
                (StatelessClientContext context)
            {
                return arg;
            }
        }.execute(context);
    }


    // Boolean.

    @Override
    public boolean pBoolean
        (StatelessClientContext context,
         final boolean arg)
        throws RemoteException
    {
        return new StatelessRemoteCaller<Boolean>() {
            @Override
            protected Boolean call
                (StatelessClientContext context)
            {
                return arg;
            }
        }.execute(context);
    }

    @Override
    public Boolean oBoolean
        (StatelessClientContext context,
         final Boolean arg)
        throws RemoteException
    {
        return new StatelessRemoteCaller<Boolean>() {
            @Override
            protected Boolean call
                (StatelessClientContext context)
            {
                return arg;
            }
        }.execute(context);
    }

    @Override
    public boolean[] pArrBoolean
        (StatelessClientContext context,
         final boolean[] arg)
        throws RemoteException
    {
        return new StatelessRemoteCaller<boolean[]>() {
            @Override
            protected boolean[] call
                (StatelessClientContext context)
            {
                return arg;
            }
        }.execute(context);
    }

    @Override
    public Boolean[] oArrBoolean
        (StatelessClientContext context,
         final Boolean[] arg)
        throws RemoteException
    {
        return new StatelessRemoteCaller<Boolean[]>() {
            @Override
            protected Boolean[] call
                (StatelessClientContext context)
            {
                return arg;
            }
        }.execute(context);
    }

    @Override
    public Collection<Boolean> colBoolean
        (StatelessClientContext context,
         final Collection<Boolean> arg)
        throws RemoteException
    {
        return new StatelessRemoteCaller<Collection<Boolean>>() {
            @Override
            protected Collection<Boolean> call
                (StatelessClientContext context)
            {
                return arg;
            }
        }.execute(context);
    }

    @Override
    public Set<Boolean> setBoolean
        (StatelessClientContext context,
         final Set<Boolean> arg)
        throws RemoteException
    {
        return new StatelessRemoteCaller<Set<Boolean>>() {
            @Override
            protected Set<Boolean> call
                (StatelessClientContext context)
            {
                return arg;
            }
        }.execute(context);
    }

    @Override
    public MapWrapper<Boolean,Boolean> mapBoolean
        (StatelessClientContext context,
         final MapWrapper<Boolean,Boolean> arg)
        throws RemoteException
    {
        return new StatelessRemoteCaller<MapWrapper<Boolean,Boolean>>() {
            @Override
            protected MapWrapper<Boolean,Boolean> call
                (StatelessClientContext context)
            {
                return arg;
            }
        }.execute(context);
    }

    @Override
    public BooleanHolder hldBoolean
        (StatelessClientContext context,
         final BooleanHolder arg)
        throws RemoteException
    {
        return new StatelessRemoteCaller<BooleanHolder>() {
            @Override
            protected BooleanHolder call
                (StatelessClientContext context)
            {
                return arg;
            }
        }.execute(context);
    }


    // Byte.

    @Override
    public byte pByte
        (StatelessClientContext context,
         final byte arg)
        throws RemoteException
    {
        return new StatelessRemoteCaller<Byte>() {
            @Override
            protected Byte call
                (StatelessClientContext context)
            {
                return arg;
            }
        }.execute(context);
    }

    @Override
    public Byte oByte
        (StatelessClientContext context,
         final Byte arg)
        throws RemoteException
    {
        return new StatelessRemoteCaller<Byte>() {
            @Override
            protected Byte call
                (StatelessClientContext context)
            {
                return arg;
            }
        }.execute(context);
    }

    @Override
    public byte[] pArrByte
        (StatelessClientContext context,
         final byte[] arg)
        throws RemoteException
    {
        return new StatelessRemoteCaller<byte[]>() {
            @Override
            protected byte[] call
                (StatelessClientContext context)
            {
                return arg;
            }
        }.execute(context);
    }

    @Override
    public Byte[] oArrByte
        (StatelessClientContext context,
         final Byte[] arg)
        throws RemoteException
    {
        return new StatelessRemoteCaller<Byte[]>() {
            @Override
            protected Byte[] call
                (StatelessClientContext context)
            {
                return arg;
            }
        }.execute(context);
    }

    @Override
    public Collection<Byte> colByte
        (StatelessClientContext context,
         final Collection<Byte> arg)
        throws RemoteException
    {
        return new StatelessRemoteCaller<Collection<Byte>>() {
            @Override
            protected Collection<Byte> call
                (StatelessClientContext context)
            {
                return arg;
            }
        }.execute(context);
    }

    @Override
    public Set<Byte> setByte
        (StatelessClientContext context,
         final Set<Byte> arg)
        throws RemoteException
    {
        return new StatelessRemoteCaller<Set<Byte>>() {
            @Override
            protected Set<Byte> call
                (StatelessClientContext context)
            {
                return arg;
            }
        }.execute(context);
    }

    @Override
    public MapWrapper<Byte,Byte> mapByte
        (StatelessClientContext context,
         final MapWrapper<Byte,Byte> arg)
        throws RemoteException
    {
        return new StatelessRemoteCaller<MapWrapper<Byte,Byte>>() {
            @Override
            protected MapWrapper<Byte,Byte> call
                (StatelessClientContext context)
            {
                return arg;
            }
        }.execute(context);
    }

    @Override
    public ByteHolder hldByte
        (StatelessClientContext context,
         final ByteHolder arg)
        throws RemoteException
    {
        return new StatelessRemoteCaller<ByteHolder>() {
            @Override
            protected ByteHolder call
                (StatelessClientContext context)
            {
                return arg;
            }
        }.execute(context);
    }


    // Character.

    @Override
    public char pChar
        (StatelessClientContext context,
         final char arg)
        throws RemoteException
    {
        return new StatelessRemoteCaller<Character>() {
            @Override
            protected Character call
                (StatelessClientContext context)
            {
                return arg;
            }
        }.execute(context);
    }

    @Override
    public Character oChar
        (StatelessClientContext context,
         final Character arg)
        throws RemoteException
    {
        return new StatelessRemoteCaller<Character>() {
            @Override
            protected Character call
                (StatelessClientContext context)
            {
                return arg;
            }
        }.execute(context);
    }

    @Override
    public char[] pArrChar
        (StatelessClientContext context,
         final char[] arg)
        throws RemoteException
    {
        return new StatelessRemoteCaller<char[]>() {
            @Override
            protected char[] call
                (StatelessClientContext context)
            {
                return arg;
            }
        }.execute(context);
    }

    @Override
    public Character[] oArrChar
        (StatelessClientContext context,
         final Character[] arg)
        throws RemoteException
    {
        return new StatelessRemoteCaller<Character[]>() {
            @Override
            protected Character[] call
                (StatelessClientContext context)
            {
                return arg;
            }
        }.execute(context);
    }

    @Override
    public Collection<Character> colChar
        (StatelessClientContext context,
         final Collection<Character> arg)
        throws RemoteException
    {
        return new StatelessRemoteCaller<Collection<Character>>() {
            @Override
            protected Collection<Character> call
                (StatelessClientContext context)
            {
                return arg;
            }
        }.execute(context);
    }

    @Override
    public Set<Character> setChar
        (StatelessClientContext context,
         final Set<Character> arg)
        throws RemoteException
    {
        return new StatelessRemoteCaller<Set<Character>>() {
            @Override
            protected Set<Character> call
                (StatelessClientContext context)
            {
                return arg;
            }
        }.execute(context);
    }

    @Override
    public MapWrapper<Character,Character> mapChar
        (StatelessClientContext context,
         final MapWrapper<Character,Character> arg)
        throws RemoteException
    {
        return new StatelessRemoteCaller<MapWrapper<Character,Character>>() {
            @Override
            protected MapWrapper<Character,Character> call
                (StatelessClientContext context)
            {
                return arg;
            }
        }.execute(context);
    }

    @Override
    public CharacterHolder hldChar
        (StatelessClientContext context,
         final CharacterHolder arg)
        throws RemoteException
    {
        return new StatelessRemoteCaller<CharacterHolder>() {
            @Override
            protected CharacterHolder call
                (StatelessClientContext context)
            {
                return arg;
            }
        }.execute(context);
    }


    // Double.

    @Override
    public double pDouble
        (StatelessClientContext context,
         final double arg)
        throws RemoteException
    {
        return new StatelessRemoteCaller<Double>() {
            @Override
            protected Double call
                (StatelessClientContext context)
            {
                return arg;
            }
        }.execute(context);
    }

    @Override
    public Double oDouble
        (StatelessClientContext context,
         final Double arg)
        throws RemoteException
    {
        return new StatelessRemoteCaller<Double>() {
            @Override
            protected Double call
                (StatelessClientContext context)
            {
                return arg;
            }
        }.execute(context);
    }

    @Override
    public double[] pArrDouble
        (StatelessClientContext context,
         final double[] arg)
        throws RemoteException
    {
        return new StatelessRemoteCaller<double[]>() {
            @Override
            protected double[] call
                (StatelessClientContext context)
            {
                return arg;
            }
        }.execute(context);
    }

    @Override
    public Double[] oArrDouble
        (StatelessClientContext context,
         final Double[] arg)
        throws RemoteException
    {
        return new StatelessRemoteCaller<Double[]>() {
            @Override
            protected Double[] call
                (StatelessClientContext context)
            {
                return arg;
            }
        }.execute(context);
    }

    @Override
    public Collection<Double> colDouble
        (StatelessClientContext context,
         final Collection<Double> arg)
        throws RemoteException
    {
        return new StatelessRemoteCaller<Collection<Double>>() {
            @Override
            protected Collection<Double> call
                (StatelessClientContext context)
            {
                return arg;
            }
        }.execute(context);
    }

    @Override
    public Set<Double> setDouble
        (StatelessClientContext context,
         final Set<Double> arg)
        throws RemoteException
    {
        return new StatelessRemoteCaller<Set<Double>>() {
            @Override
            protected Set<Double> call
                (StatelessClientContext context)
            {
                return arg;
            }
        }.execute(context);
    }

    @Override
    public MapWrapper<Double,Double> mapDouble
        (StatelessClientContext context,
         final MapWrapper<Double,Double> arg)
        throws RemoteException
    {
        return new StatelessRemoteCaller<MapWrapper<Double,Double>>() {
            @Override
            protected MapWrapper<Double,Double> call
                (StatelessClientContext context)
            {
                return arg;
            }
        }.execute(context);
    }

    @Override
    public DoubleHolder hldDouble
        (StatelessClientContext context,
         final DoubleHolder arg)
        throws RemoteException
    {
        return new StatelessRemoteCaller<DoubleHolder>() {
            @Override
            protected DoubleHolder call
                (StatelessClientContext context)
            {
                return arg;
            }
        }.execute(context);
    }


    // Float.

    @Override
    public float pFloat
        (StatelessClientContext context,
         final float arg)
        throws RemoteException
    {
        return new StatelessRemoteCaller<Float>() {
            @Override
            protected Float call
                (StatelessClientContext context)
            {
                return arg;
            }
        }.execute(context);
    }

    @Override
    public Float oFloat
        (StatelessClientContext context,
         final Float arg)
        throws RemoteException
    {
        return new StatelessRemoteCaller<Float>() {
            @Override
            protected Float call
                (StatelessClientContext context)
            {
                return arg;
            }
        }.execute(context);
    }

    @Override
    public float[] pArrFloat
        (StatelessClientContext context,
         final float[] arg)
        throws RemoteException
    {
        return new StatelessRemoteCaller<float[]>() {
            @Override
            protected float[] call
                (StatelessClientContext context)
            {
                return arg;
            }
        }.execute(context);
    }

    @Override
    public Float[] oArrFloat
        (StatelessClientContext context,
         final Float[] arg)
        throws RemoteException
    {
        return new StatelessRemoteCaller<Float[]>() {
            @Override
            protected Float[] call
                (StatelessClientContext context)
            {
                return arg;
            }
        }.execute(context);
    }

    @Override
    public Collection<Float> colFloat
        (StatelessClientContext context,
         final Collection<Float> arg)
        throws RemoteException
    {
        return new StatelessRemoteCaller<Collection<Float>>() {
            @Override
            protected Collection<Float> call
                (StatelessClientContext context)
            {
                return arg;
            }
        }.execute(context);
    }

    @Override
    public Set<Float> setFloat
        (StatelessClientContext context,
         final Set<Float> arg)
        throws RemoteException
    {
        return new StatelessRemoteCaller<Set<Float>>() {
            @Override
            protected Set<Float> call
                (StatelessClientContext context)
            {
                return arg;
            }
        }.execute(context);
    }

    @Override
    public MapWrapper<Float,Float> mapFloat
        (StatelessClientContext context,
         final MapWrapper<Float,Float> arg)
        throws RemoteException
    {
        return new StatelessRemoteCaller<MapWrapper<Float,Float>>() {
            @Override
            protected MapWrapper<Float,Float> call
                (StatelessClientContext context)
            {
                return arg;
            }
        }.execute(context);
    }

    @Override
    public FloatHolder hldFloat
        (StatelessClientContext context,
         final FloatHolder arg)
        throws RemoteException
    {
        return new StatelessRemoteCaller<FloatHolder>() {
            @Override
            protected FloatHolder call
                (StatelessClientContext context)
            {
                return arg;
            }
        }.execute(context);
    }


    // Integer.

    @Override
    public int pInt
        (StatelessClientContext context,
         final int arg)
        throws RemoteException
    {
        return new StatelessRemoteCaller<Integer>() {
            @Override
            protected Integer call
                (StatelessClientContext context)
            {
                return arg;
            }
        }.execute(context);
    }

    @Override
    public Integer oInt
        (StatelessClientContext context,
         final Integer arg)
        throws RemoteException
    {
        return new StatelessRemoteCaller<Integer>() {
            @Override
            protected Integer call
                (StatelessClientContext context)
            {
                return arg;
            }
        }.execute(context);
    }

    @Override
    public int[] pArrInt
        (StatelessClientContext context,
         final int[] arg)
        throws RemoteException
    {
        return new StatelessRemoteCaller<int[]>() {
            @Override
            protected int[] call
                (StatelessClientContext context)
            {
                return arg;
            }
        }.execute(context);
    }

    @Override
    public Integer[] oArrInt
        (StatelessClientContext context,
         final Integer[] arg)
        throws RemoteException
    {
        return new StatelessRemoteCaller<Integer[]>() {
            @Override
            protected Integer[] call
                (StatelessClientContext context)
            {
                return arg;
            }
        }.execute(context);
    }

    @Override
    public Collection<Integer> colInt
        (StatelessClientContext context,
         final Collection<Integer> arg)
        throws RemoteException
    {
        return new StatelessRemoteCaller<Collection<Integer>>() {
            @Override
            protected Collection<Integer> call
                (StatelessClientContext context)
            {
                return arg;
            }
        }.execute(context);
    }

    @Override
    public Set<Integer> setInt
        (StatelessClientContext context,
         final Set<Integer> arg)
        throws RemoteException
    {
        return new StatelessRemoteCaller<Set<Integer>>() {
            @Override
            protected Set<Integer> call
                (StatelessClientContext context)
            {
                return arg;
            }
        }.execute(context);
    }

    @Override
    public MapWrapper<Integer,Integer> mapInt
        (StatelessClientContext context,
         final MapWrapper<Integer,Integer> arg)
        throws RemoteException
    {
        return new StatelessRemoteCaller<MapWrapper<Integer,Integer>>() {
            @Override
            protected MapWrapper<Integer,Integer> call
                (StatelessClientContext context)
            {
                return arg;
            }
        }.execute(context);
    }

    @Override
    public IntegerHolder hldInt
        (StatelessClientContext context,
         final IntegerHolder arg)
        throws RemoteException
    {
        return new StatelessRemoteCaller<IntegerHolder>() {
            @Override
            protected IntegerHolder call
                (StatelessClientContext context)
            {
                return arg;
            }
        }.execute(context);
    }


    // Long.

    @Override
    public long pLong
        (StatelessClientContext context,
         final long arg)
        throws RemoteException
    {
        return new StatelessRemoteCaller<Long>() {
            @Override
            protected Long call
                (StatelessClientContext context)
            {
                return arg;
            }
        }.execute(context);
    }

    @Override
    public Long oLong
        (StatelessClientContext context,
         final Long arg)
        throws RemoteException
    {
        return new StatelessRemoteCaller<Long>() {
            @Override
            protected Long call
                (StatelessClientContext context)
            {
                return arg;
            }
        }.execute(context);
    }

    @Override
    public long[] pArrLong
        (StatelessClientContext context,
         final long[] arg)
        throws RemoteException
    {
        return new StatelessRemoteCaller<long[]>() {
            @Override
            protected long[] call
                (StatelessClientContext context)
            {
                return arg;
            }
        }.execute(context);
    }

    @Override
    public Long[] oArrLong
        (StatelessClientContext context,
         final Long[] arg)
        throws RemoteException
    {
        return new StatelessRemoteCaller<Long[]>() {
            @Override
            protected Long[] call
                (StatelessClientContext context)
            {
                return arg;
            }
        }.execute(context);
    }

    @Override
    public Collection<Long> colLong
        (StatelessClientContext context,
         final Collection<Long> arg)
        throws RemoteException
    {
        return new StatelessRemoteCaller<Collection<Long>>() {
            @Override
            protected Collection<Long> call
                (StatelessClientContext context)
            {
                return arg;
            }
        }.execute(context);
    }

    @Override
    public Set<Long> setLong
        (StatelessClientContext context,
         final Set<Long> arg)
        throws RemoteException
    {
        return new StatelessRemoteCaller<Set<Long>>() {
            @Override
            protected Set<Long> call
                (StatelessClientContext context)
            {
                return arg;
            }
        }.execute(context);
    }

    @Override
    public MapWrapper<Long,Long> mapLong
        (StatelessClientContext context,
         final MapWrapper<Long,Long> arg)
        throws RemoteException
    {
        return new StatelessRemoteCaller<MapWrapper<Long,Long>>() {
            @Override
            protected MapWrapper<Long,Long> call
                (StatelessClientContext context)
            {
                return arg;
            }
        }.execute(context);
    }

    @Override
    public LongHolder hldLong
        (StatelessClientContext context,
         final LongHolder arg)
        throws RemoteException
    {
        return new StatelessRemoteCaller<LongHolder>() {
            @Override
            protected LongHolder call
                (StatelessClientContext context)
            {
                return arg;
            }
        }.execute(context);
    }


    // Short.

    @Override
    public short pShort
        (StatelessClientContext context,
         final short arg)
        throws RemoteException
    {
        return new StatelessRemoteCaller<Short>() {
            @Override
            protected Short call
                (StatelessClientContext context)
            {
                return arg;
            }
        }.execute(context);
    }

    @Override
    public Short oShort
        (StatelessClientContext context,
         final Short arg)
        throws RemoteException
    {
        return new StatelessRemoteCaller<Short>() {
            @Override
            protected Short call
                (StatelessClientContext context)
            {
                return arg;
            }
        }.execute(context);
    }

    @Override
    public short[] pArrShort
        (StatelessClientContext context,
         final short[] arg)
        throws RemoteException
    {
        return new StatelessRemoteCaller<short[]>() {
            @Override
            protected short[] call
                (StatelessClientContext context)
            {
                return arg;
            }
        }.execute(context);
    }

    @Override
    public Short[] oArrShort
        (StatelessClientContext context,
         final Short[] arg)
        throws RemoteException
    {
        return new StatelessRemoteCaller<Short[]>() {
            @Override
            protected Short[] call
                (StatelessClientContext context)
            {
                return arg;
            }
        }.execute(context);
    }

    @Override
    public Collection<Short> colShort
        (StatelessClientContext context,
         final Collection<Short> arg)
        throws RemoteException
    {
        return new StatelessRemoteCaller<Collection<Short>>() {
            @Override
            protected Collection<Short> call
                (StatelessClientContext context)
            {
                return arg;
            }
        }.execute(context);
    }

    @Override
    public Set<Short> setShort
        (StatelessClientContext context,
         final Set<Short> arg)
        throws RemoteException
    {
        return new StatelessRemoteCaller<Set<Short>>() {
            @Override
            protected Set<Short> call
                (StatelessClientContext context)
            {
                return arg;
            }
        }.execute(context);
    }

    @Override
    public MapWrapper<Short,Short> mapShort
        (StatelessClientContext context,
         final MapWrapper<Short,Short> arg)
        throws RemoteException
    {
        return new StatelessRemoteCaller<MapWrapper<Short,Short>>() {
            @Override
            protected MapWrapper<Short,Short> call
                (StatelessClientContext context)
            {
                return arg;
            }
        }.execute(context);
    }

    @Override
    public ShortHolder hldShort
        (StatelessClientContext context,
         final ShortHolder arg)
        throws RemoteException
    {
        return new StatelessRemoteCaller<ShortHolder>() {
            @Override
            protected ShortHolder call
                (StatelessClientContext context)
            {
                return arg;
            }
        }.execute(context);
    }


    // String.

    @Override
    public String str
        (StatelessClientContext context,
         final String arg)
        throws RemoteException
    {
        return new StatelessRemoteCaller<String>() {
            @Override
            protected String call
                (StatelessClientContext context)
            {
                return arg;
            }
        }.execute(context);
    }

    @Override
    public String[] arrStr
        (StatelessClientContext context,
         final String[] arg)
        throws RemoteException
    {
        return new StatelessRemoteCaller<String[]>() {
            @Override
            protected String[] call
                (StatelessClientContext context)
            {
                return arg;
            }
        }.execute(context);
    }

    @Override
    public Collection<String> colStr
        (StatelessClientContext context,
         final Collection<String> arg)
        throws RemoteException
    {
        return new StatelessRemoteCaller<Collection<String>>() {
            @Override
            protected Collection<String> call
                (StatelessClientContext context)
            {
                return arg;
            }
        }.execute(context);
    }

    @Override
    public Set<String> setStr
        (StatelessClientContext context,
         final Set<String> arg)
        throws RemoteException
    {
        return new StatelessRemoteCaller<Set<String>>() {
            @Override
            protected Set<String> call
                (StatelessClientContext context)
            {
                return arg;
            }
        }.execute(context);
    }

    @Override
    public MapWrapper<String,String> mapStr
        (StatelessClientContext context,
         final MapWrapper<String,String> arg)
        throws RemoteException
    {
        return new StatelessRemoteCaller<MapWrapper<String,String>>() {
            @Override
            protected MapWrapper<String,String> call
                (StatelessClientContext context)
            {
                return arg;
            }
        }.execute(context);
    }

    @Override
    public StringHolder hldStr
        (StatelessClientContext context,
         final StringHolder arg)
        throws RemoteException
    {
        return new StatelessRemoteCaller<StringHolder>() {
            @Override
            protected StringHolder call
                (StatelessClientContext context)
            {
                return arg;
            }
        }.execute(context);
    }


    // Big decimal.

    @Override
    public BigDecimal bd
        (StatelessClientContext context,
         final BigDecimal arg)
        throws RemoteException
    {
        return new StatelessRemoteCaller<BigDecimal>() {
            @Override
            protected BigDecimal call
                (StatelessClientContext context)
            {
                return arg;
            }
        }.execute(context);
    }

    @Override
    public BigDecimal[] arrBd
        (StatelessClientContext context,
         final BigDecimal[] arg)
        throws RemoteException
    {
        return new StatelessRemoteCaller<BigDecimal[]>() {
            @Override
            protected BigDecimal[] call
                (StatelessClientContext context)
            {
                return arg;
            }
        }.execute(context);
    }

    @Override
    public Collection<BigDecimal> colBd
        (StatelessClientContext context,
         final Collection<BigDecimal> arg)
        throws RemoteException
    {
        return new StatelessRemoteCaller<Collection<BigDecimal>>() {
            @Override
            protected Collection<BigDecimal> call
                (StatelessClientContext context)
            {
                return arg;
            }
        }.execute(context);
    }

    @Override
    public Set<BigDecimal> setBd
        (StatelessClientContext context,
         final Set<BigDecimal> arg)
        throws RemoteException
    {
        return new StatelessRemoteCaller<Set<BigDecimal>>() {
            @Override
            protected Set<BigDecimal> call
                (StatelessClientContext context)
            {
                return arg;
            }
        }.execute(context);
    }

    @Override
    public MapWrapper<BigDecimal,BigDecimal> mapBd
        (StatelessClientContext context,
         final MapWrapper<BigDecimal,BigDecimal> arg)
        throws RemoteException
    {
        return new StatelessRemoteCaller<MapWrapper<BigDecimal,BigDecimal>>() {
            @Override
            protected MapWrapper<BigDecimal,BigDecimal> call
                (StatelessClientContext context)
            {
                return arg;
            }
        }.execute(context);
    }

    @Override
    public BigDecimalHolder hldBd
        (StatelessClientContext context,
         final BigDecimalHolder arg)
        throws RemoteException
    {
        return new StatelessRemoteCaller<BigDecimalHolder>() {
            @Override
            protected BigDecimalHolder call
                (StatelessClientContext context)
            {
                return arg;
            }
        }.execute(context);
    }


    // Big integer.

    @Override
    public BigInteger bi
        (StatelessClientContext context,
         final BigInteger arg)
        throws RemoteException
    {
        return new StatelessRemoteCaller<BigInteger>() {
            @Override
            protected BigInteger call
                (StatelessClientContext context)
            {
                return arg;
            }
        }.execute(context);
    }

    @Override
    public BigInteger[] arrBi
        (StatelessClientContext context,
         final BigInteger[] arg)
        throws RemoteException
    {
        return new StatelessRemoteCaller<BigInteger[]>() {
            @Override
            protected BigInteger[] call
                (StatelessClientContext context)
            {
                return arg;
            }
        }.execute(context);
    }

    @Override
    public Collection<BigInteger> colBi
        (StatelessClientContext context,
         final Collection<BigInteger> arg)
        throws RemoteException
    {
        return new StatelessRemoteCaller<Collection<BigInteger>>() {
            @Override
            protected Collection<BigInteger> call
                (StatelessClientContext context)
            {
                return arg;
            }
        }.execute(context);
    }

    @Override
    public Set<BigInteger> setBi
        (StatelessClientContext context,
         final Set<BigInteger> arg)
        throws RemoteException
    {
        return new StatelessRemoteCaller<Set<BigInteger>>() {
            @Override
            protected Set<BigInteger> call
                (StatelessClientContext context)
            {
                return arg;
            }
        }.execute(context);
    }

    @Override
    public MapWrapper<BigInteger,BigInteger> mapBi
        (StatelessClientContext context,
         final MapWrapper<BigInteger,BigInteger> arg)
        throws RemoteException
    {
        return new StatelessRemoteCaller<MapWrapper<BigInteger,BigInteger>>() {
            @Override
            protected MapWrapper<BigInteger,BigInteger> call
                (StatelessClientContext context)
            {
                return arg;
            }
        }.execute(context);
    }

    @Override
    public BigIntegerHolder hldBi
        (StatelessClientContext context,
         final BigIntegerHolder arg)
        throws RemoteException
    {
        return new StatelessRemoteCaller<BigIntegerHolder>() {
            @Override
            protected BigIntegerHolder call
                (StatelessClientContext context)
            {
                return arg;
            }
        }.execute(context);
    }


    // Object with inner classes.

    @Override
    public InnerObject io
        (StatelessClientContext context,
         final InnerObject arg)
        throws RemoteException
    {
        return new StatelessRemoteCaller<InnerObject>() {
            @Override
            protected InnerObject call
                (StatelessClientContext context)
            {
                return arg;
            }
        }.execute(context);
    }

    @Override
    public InnerObject[] arrIo
        (StatelessClientContext context,
         final InnerObject[] arg)
        throws RemoteException
    {
        return new StatelessRemoteCaller<InnerObject[]>() {
            @Override
            protected InnerObject[] call
                (StatelessClientContext context)
            {
                return arg;
            }
        }.execute(context);
    }

    @Override
    public Collection<InnerObject> colIo
        (StatelessClientContext context,
         final Collection<InnerObject> arg)
        throws RemoteException
    {
        return new StatelessRemoteCaller<Collection<InnerObject>>() {
            @Override
            protected Collection<InnerObject> call
                (StatelessClientContext context)
            {
                return arg;
            }
        }.execute(context);
    }

    @Override
    public Set<InnerObject> setIo
        (StatelessClientContext context,
         final Set<InnerObject> arg)
        throws RemoteException
    {
        return new StatelessRemoteCaller<Set<InnerObject>>() {
            @Override
            protected Set<InnerObject> call
                (StatelessClientContext context)
            {
                return arg;
            }
        }.execute(context);
    }

    @Override
    public MapWrapper<InnerObject,InnerObject> mapIo
        (StatelessClientContext context,
         final MapWrapper<InnerObject,InnerObject> arg)
        throws RemoteException
    {
        return new StatelessRemoteCaller<MapWrapper<InnerObject,
                                                    InnerObject>>() {
            @Override
            protected MapWrapper<InnerObject,InnerObject> call
                (StatelessClientContext context)
            {
                return arg;
            }
        }.execute(context);
    }

    @Override
    public InnerObjectHolder hldIo
        (StatelessClientContext context,
         final InnerObjectHolder arg)
        throws RemoteException
    {
        return new StatelessRemoteCaller<InnerObjectHolder>() {
            @Override
            protected InnerObjectHolder call
                (StatelessClientContext context)
            {
                return arg;
            }
        }.execute(context);
    }


    // Enum.

    @Override
    public EnumObject eo
        (StatelessClientContext context,
         final EnumObject arg)
        throws RemoteException
    {
        return new StatelessRemoteCaller<EnumObject>() {
            @Override
            protected EnumObject call
                (StatelessClientContext context)
            {
                return arg;
            }
        }.execute(context);
    }

    @Override
    public EnumObject[] arrEo
        (StatelessClientContext context,
         final EnumObject[] arg)
        throws RemoteException
    {
        return new StatelessRemoteCaller<EnumObject[]>() {
            @Override
            protected EnumObject[] call
                (StatelessClientContext context)
            {
                return arg;
            }
        }.execute(context);
    }

    @Override
    public Collection<EnumObject> colEo
        (StatelessClientContext context,
         final Collection<EnumObject> arg)
        throws RemoteException
    {
        return new StatelessRemoteCaller<Collection<EnumObject>>() {
            @Override
            protected Collection<EnumObject> call
                (StatelessClientContext context)
            {
                return arg;
            }
        }.execute(context);
    }

    @Override
    public Set<EnumObject> setEo
        (StatelessClientContext context,
         final Set<EnumObject> arg)
        throws RemoteException
    {
        return new StatelessRemoteCaller<Set<EnumObject>>() {
            @Override
            protected Set<EnumObject> call
                (StatelessClientContext context)
            {
                return arg;
            }
        }.execute(context);
    }

    @Override
    public MapWrapper<EnumObject,EnumObject> mapEo
        (StatelessClientContext context,
         final MapWrapper<EnumObject,EnumObject> arg)
        throws RemoteException
    {
        return new StatelessRemoteCaller<MapWrapper<EnumObject,EnumObject>>() {
            @Override
            protected MapWrapper<EnumObject,EnumObject> call
                (StatelessClientContext context)
            {
                return arg;
            }
        }.execute(context);
    }

    @Override
    public EnumObjectHolder hldEo
        (StatelessClientContext context,
         final EnumObjectHolder arg)
        throws RemoteException
    {
        return new StatelessRemoteCaller<EnumObjectHolder>() {
            @Override
            protected EnumObjectHolder call
                (StatelessClientContext context)
            {
                return arg;
            }
        }.execute(context);
    }


    // Date.

    @Override
    public Date dt
        (StatelessClientContext context,
         final Date arg)
        throws RemoteException
    {
        return new StatelessRemoteCaller<Date>() {
            @Override
            protected Date call
                (StatelessClientContext context)
            {
                return arg;
            }
        }.execute(context);
    }

    @Override
    public Date[] arrDt
        (StatelessClientContext context,
         final Date[] arg)
        throws RemoteException
    {
        return new StatelessRemoteCaller<Date[]>() {
            @Override
            protected Date[] call
                (StatelessClientContext context)
            {
                return arg;
            }
        }.execute(context);
    }

    @Override
    public Collection<Date> colDt
        (StatelessClientContext context,
         final Collection<Date> arg)
        throws RemoteException
    {
        return new StatelessRemoteCaller<Collection<Date>>() {
            @Override
            protected Collection<Date> call
                (StatelessClientContext context)
            {
                return arg;
            }
        }.execute(context);
    }

    @Override
    public Set<Date> setDt
        (StatelessClientContext context,
         final Set<Date> arg)
        throws RemoteException
    {
        return new StatelessRemoteCaller<Set<Date>>() {
            @Override
            protected Set<Date> call
                (StatelessClientContext context)
            {
                return arg;
            }
        }.execute(context);
    }

    @Override
    public MapWrapper<Date,Date> mapDt
        (StatelessClientContext context,
         final MapWrapper<Date,Date> arg)
        throws RemoteException
    {
        return new StatelessRemoteCaller<MapWrapper<Date,Date>>() {
            @Override
            protected MapWrapper<Date,Date> call
                (StatelessClientContext context)
            {
                return arg;
            }
        }.execute(context);
    }

    @Override
    public DateHolder hldDt
        (StatelessClientContext context,
         final DateHolder arg)
        throws RemoteException
    {
        return new StatelessRemoteCaller<DateHolder>() {
            @Override
            protected DateHolder call
                (StatelessClientContext context)
            {
                return arg;
            }
        }.execute(context);
    }


    // Wrapped date.

    @Override
    public DateWrapper dw
        (StatelessClientContext context,
         final DateWrapper arg)
        throws RemoteException
    {
        return new StatelessRemoteCaller<DateWrapper>() {
            @Override
            protected DateWrapper call
                (StatelessClientContext context)
            {
                return arg;
            }
        }.execute(context);
    }

    @Override
    public DateWrapper[] arrDw
        (StatelessClientContext context,
         final DateWrapper[] arg)
        throws RemoteException
    {
        return new StatelessRemoteCaller<DateWrapper[]>() {
            @Override
            protected DateWrapper[] call
                (StatelessClientContext context)
            {
                return arg;
            }
        }.execute(context);
    }

    @Override
    public Collection<DateWrapper> colDw
        (StatelessClientContext context,
         final Collection<DateWrapper> arg)
        throws RemoteException
    {
        return new StatelessRemoteCaller<Collection<DateWrapper>>() {
            @Override
            protected Collection<DateWrapper> call
                (StatelessClientContext context)
            {
                return arg;
            }
        }.execute(context);
    }

    @Override
    public Set<DateWrapper> setDw
        (StatelessClientContext context,
         final Set<DateWrapper> arg)
        throws RemoteException
    {
        return new StatelessRemoteCaller<Set<DateWrapper>>() {
            @Override
            protected Set<DateWrapper> call
                (StatelessClientContext context)
            {
                return arg;
            }
        }.execute(context);
    }

    @Override
    public MapWrapper<DateWrapper,DateWrapper> mapDw
        (StatelessClientContext context,
         final MapWrapper<DateWrapper,DateWrapper> arg)
        throws RemoteException
    {
        return new StatelessRemoteCaller<MapWrapper<DateWrapper,
                                                    DateWrapper>>() {
            @Override
            protected MapWrapper<DateWrapper,DateWrapper> call
                (StatelessClientContext context)
            {
                return arg;
            }
        }.execute(context);
    }

    @Override
    public DateWrapperHolder hldDw
        (StatelessClientContext context,
         final DateWrapperHolder arg)
        throws RemoteException
    {
        return new StatelessRemoteCaller<DateWrapperHolder>() {
            @Override
            protected DateWrapperHolder call
                (StatelessClientContext context)
            {
                return arg;
            }
        }.execute(context);
    }


    // Locale.

    @Override
    public LocaleWrapper lwrp
        (StatelessClientContext context,
         final LocaleWrapper arg)
        throws RemoteException
    {
        return new StatelessRemoteCaller<LocaleWrapper>() {
            @Override
            protected LocaleWrapper call
                (StatelessClientContext context)
            {
                return arg;
            }
        }.execute(context);
    }

    @Override
    public LocaleWrapper[] arrLwrp
        (StatelessClientContext context,
         final LocaleWrapper[] arg)
        throws RemoteException
    {
        return new StatelessRemoteCaller<LocaleWrapper[]>() {
            @Override
            protected LocaleWrapper[] call
                (StatelessClientContext context)
            {
                return arg;
            }
        }.execute(context);
    }

    @Override
    public Collection<LocaleWrapper> colLwrp
        (StatelessClientContext context,
         final Collection<LocaleWrapper> arg)
        throws RemoteException
    {
        return new StatelessRemoteCaller<Collection<LocaleWrapper>>() {
            @Override
            protected Collection<LocaleWrapper> call
                (StatelessClientContext context)
            {
                return arg;
            }
        }.execute(context);
    }

    @Override
    public Set<LocaleWrapper> setLwrp
        (StatelessClientContext context,
         final Set<LocaleWrapper> arg)
        throws RemoteException
    {
        return new StatelessRemoteCaller<Set<LocaleWrapper>>() {
            @Override
            protected Set<LocaleWrapper> call
                (StatelessClientContext context)
            {
                return arg;
            }
        }.execute(context);
    }

    @Override
    public MapWrapper<LocaleWrapper,LocaleWrapper> mapLwrp
        (StatelessClientContext context,
         final MapWrapper<LocaleWrapper,LocaleWrapper> arg)
        throws RemoteException
    {
        return new StatelessRemoteCaller<MapWrapper<LocaleWrapper,
                                                    LocaleWrapper>>() {
            @Override
            protected MapWrapper<LocaleWrapper,LocaleWrapper> call
                (StatelessClientContext context)
            {
                return arg;
            }
        }.execute(context);
    }

    @Override
    public LocaleWrapperHolder hldLwrp
        (StatelessClientContext context,
         final LocaleWrapperHolder arg)
        throws RemoteException
    {
        return new StatelessRemoteCaller<LocaleWrapperHolder>() {
            @Override
            protected LocaleWrapperHolder call
                (StatelessClientContext context)
            {
                return arg;
            }
        }.execute(context);
    }


    // Wrapper for serializable objects.

    @Override
    public SerWrapper<WrappableObject> swrp
        (StatelessClientContext context,
         final SerWrapper<WrappableObject> arg)
        throws RemoteException
    {
        return new StatelessRemoteCaller<SerWrapper<WrappableObject>>() {
            @Override
            protected SerWrapper<WrappableObject> call
                (StatelessClientContext context)
            {
                return arg;
            }
        }.execute(context);
    }

    @Override
    public SerWrapper[] arrSwrp
        (StatelessClientContext context,
         final SerWrapper[] arg)
        throws RemoteException
    {
        return new StatelessRemoteCaller<SerWrapper[]>() {
            @Override
            protected SerWrapper[] call
                (StatelessClientContext context)
            {
                return arg;
            }
        }.execute(context);
    }

    @Override
    public Collection<SerWrapper> colSwrp
        (StatelessClientContext context,
         final Collection<SerWrapper> arg)
        throws RemoteException
    {
        return new StatelessRemoteCaller<Collection<SerWrapper>>() {
            @Override
            protected Collection<SerWrapper> call
                (StatelessClientContext context)
            {
                return arg;
            }
        }.execute(context);
    }

    @Override
    public Set<SerWrapper> setSwrp
        (StatelessClientContext context,
         final Set<SerWrapper> arg)
        throws RemoteException
    {
        return new StatelessRemoteCaller<Set<SerWrapper>>() {
            @Override
            protected Set<SerWrapper> call
                (StatelessClientContext context)
            {
                return arg;
            }
        }.execute(context);
    }

    @Override
    public MapWrapper<SerWrapper,SerWrapper> mapSwrp
        (StatelessClientContext context,
         final MapWrapper<SerWrapper,SerWrapper> arg)
        throws RemoteException
    {
        return new StatelessRemoteCaller<MapWrapper<SerWrapper,SerWrapper>>() {
            @Override
            protected MapWrapper<SerWrapper,SerWrapper> call
                (StatelessClientContext context)
            {
                return arg;
            }
        }.execute(context);
    }

    @Override
    public SerWrapperHolder hldSwrp
        (StatelessClientContext context,
         final SerWrapperHolder arg)
        throws RemoteException
    {
        return new StatelessRemoteCaller<SerWrapperHolder>() {
            @Override
            protected SerWrapperHolder call
                (StatelessClientContext context)
            {
                return arg;
            }
        }.execute(context);
    }


    // Wrapper for comparable & serializable objects.

    @Override
    public CompSerWrapper<WrappableObject> cwrp
        (StatelessClientContext context,
         final CompSerWrapper<WrappableObject> arg)
        throws RemoteException
    {
        return new StatelessRemoteCaller<CompSerWrapper<WrappableObject>>() {
            @Override
            protected CompSerWrapper<WrappableObject> call
                (StatelessClientContext context)
            {
                return arg;
            }
        }.execute(context);
    }

    @Override
    public CompSerWrapper[] arrCwrp
        (StatelessClientContext context,
         final CompSerWrapper[] arg)
        throws RemoteException
    {
        return new StatelessRemoteCaller<CompSerWrapper[]>() {
            @Override
            protected CompSerWrapper[] call
                (StatelessClientContext context)
            {
                return arg;
            }
        }.execute(context);
    }

    @Override
    public Collection<CompSerWrapper> colCwrp
        (StatelessClientContext context,
         final Collection<CompSerWrapper> arg)
        throws RemoteException
    {
        return new StatelessRemoteCaller<Collection<CompSerWrapper>>() {
            @Override
            protected Collection<CompSerWrapper> call
                (StatelessClientContext context)
            {
                return arg;
            }
        }.execute(context);
    }

    @Override
    public Set<CompSerWrapper> setCwrp
        (StatelessClientContext context,
         final Set<CompSerWrapper> arg)
        throws RemoteException
    {
        return new StatelessRemoteCaller<Set<CompSerWrapper>>() {
            @Override
            protected Set<CompSerWrapper> call
                (StatelessClientContext context)
            {
                return arg;
            }
        }.execute(context);
    }

    @Override
    public MapWrapper<CompSerWrapper,CompSerWrapper> mapCwrp
        (StatelessClientContext context,
         final MapWrapper<CompSerWrapper,CompSerWrapper> arg)
        throws RemoteException
    {
        return new StatelessRemoteCaller<MapWrapper<CompSerWrapper,
                                                    CompSerWrapper>>() {
            @Override
            protected MapWrapper<CompSerWrapper,CompSerWrapper> call
                (StatelessClientContext context)
            {
                return arg;
            }
        }.execute(context);
    }

    @Override
    public CompSerWrapperHolder hldCwrp
        (StatelessClientContext context,
         final CompSerWrapperHolder arg)
        throws RemoteException
    {
        return new StatelessRemoteCaller<CompSerWrapperHolder>() {
            @Override
            protected CompSerWrapperHolder call
                (StatelessClientContext context)
            {
                return arg;
            }
        }.execute(context);
    }


    // Faults.

    @Override
    public void checkedException
        (StatelessClientContext context,
         final String message)
        throws RemoteException
    {
        new StatelessRemoteRunner() {
            @Override
            protected void run
                (StatelessClientContext context)
                throws CustomCheckedException
            {
                throw new CustomCheckedException(message);
            }
        }.execute(context);
    }
    
    @Override
    public void runtimeException
        (StatelessClientContext context,
         final String message)
        throws RemoteException
    {
        new StatelessRemoteRunner() {
            @Override
            protected void run
                (StatelessClientContext context)
            {
                throw new CustomRuntimeException(message);
            }
        }.execute(context);
    }

    @Override
    public void error
        (StatelessClientContext context,
         final String message)
        throws RemoteException
    {
       new StatelessRemoteRunner() {
            @Override
            protected void run
                (StatelessClientContext context)
            {
                throw new CustomError(message);
            }
       }.execute(context);
    }
}
