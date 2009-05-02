package org.marketcetera.util.ws.types;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeMap;
import javax.jws.WebService;
import org.marketcetera.util.ws.stateless.StatelessClientContext;
import org.marketcetera.util.ws.stateless.StatelessServiceBase;
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

@WebService
public interface Service
    extends StatelessServiceBase
{

    /*
     * Custom checked exception.
     */

    public static final class CustomCheckedException
        extends Exception
    {
        private static final long serialVersionUID=1L;
        
        public CustomCheckedException
            (String message)
        {
            super(message);
        }
    }

    /*
     * Custom runtime exception.
     */

    public static final class CustomRuntimeException
        extends RuntimeException
    {
        private static final long serialVersionUID=1L;

        public CustomRuntimeException
            (String message)
        {
            super(message);
        }
    }

    /*
     * Custom error.
     */

    public static final class CustomError
        extends Error
    {
        private static final long serialVersionUID=1L;

        public CustomError
            (String message)
        {
            super(message);
        }
    }


    // Raw maps.

    /*
     * LIMITATION: interfaces cannot be handled by JAXB, hence this is
     * not an appropriate signature:
     * Map<Boolean,Boolean> mapBoolean
     *     (StatelessClientContext context,
     *      Map<Boolean,Boolean> arg)
     *     throws RemoteException;
     */

    HashMap<Boolean,Boolean> hMap
        (StatelessClientContext context,
         HashMap<Boolean,Boolean> arg)
        throws RemoteException;

    TreeMap<Boolean,Boolean> tMap
        (StatelessClientContext context,
         TreeMap<Boolean,Boolean> arg)
        throws RemoteException;


    // Boolean.

    boolean pBoolean
        (StatelessClientContext context,
         boolean arg)
        throws RemoteException;

    Boolean oBoolean
        (StatelessClientContext context,
         Boolean arg)
        throws RemoteException;

    boolean[] pArrBoolean
        (StatelessClientContext context,
         boolean[] arg)
        throws RemoteException;

    Boolean[] oArrBoolean
        (StatelessClientContext context,
         Boolean[] arg)
        throws RemoteException;

    Collection<Boolean> colBoolean
        (StatelessClientContext context,
         Collection<Boolean> arg)
        throws RemoteException;

    Set<Boolean> setBoolean
        (StatelessClientContext context,
         Set<Boolean> arg)
        throws RemoteException;

    MapWrapper<Boolean,Boolean> mapBoolean
        (StatelessClientContext context,
         MapWrapper<Boolean,Boolean> arg)
        throws RemoteException;

    BooleanHolder hldBoolean
        (StatelessClientContext context,
         BooleanHolder arg)
        throws RemoteException;


    // Byte.

    byte pByte
        (StatelessClientContext context,
         byte arg)
        throws RemoteException;

    Byte oByte
        (StatelessClientContext context,
         Byte arg)
        throws RemoteException;

    byte[] pArrByte
        (StatelessClientContext context,
         byte[] arg)
        throws RemoteException;

    Byte[] oArrByte
        (StatelessClientContext context,
         Byte[] arg)
        throws RemoteException;

    Collection<Byte> colByte
        (StatelessClientContext context,
         Collection<Byte> arg)
        throws RemoteException;

    Set<Byte> setByte
        (StatelessClientContext context,
         Set<Byte> arg)
        throws RemoteException;

    MapWrapper<Byte,Byte> mapByte
        (StatelessClientContext context,
         MapWrapper<Byte,Byte> arg)
        throws RemoteException;

    ByteHolder hldByte
        (StatelessClientContext context,
         ByteHolder arg)
        throws RemoteException;


    // Character.

    char pChar
        (StatelessClientContext context,
         char arg)
        throws RemoteException;

    Character oChar
        (StatelessClientContext context,
         Character arg)
        throws RemoteException;

    char[] pArrChar
        (StatelessClientContext context,
         char[] arg)
        throws RemoteException;

    Character[] oArrChar
        (StatelessClientContext context,
         Character[] arg)
        throws RemoteException;

    Collection<Character> colChar
        (StatelessClientContext context,
         Collection<Character> arg)
        throws RemoteException;

    Set<Character> setChar
        (StatelessClientContext context,
         Set<Character> arg)
        throws RemoteException;

    MapWrapper<Character,Character> mapChar
        (StatelessClientContext context,
         MapWrapper<Character,Character> arg)
        throws RemoteException;

    CharacterHolder hldChar
        (StatelessClientContext context,
         CharacterHolder arg)
        throws RemoteException;


    // Double.

    double pDouble
        (StatelessClientContext context,
         double arg)
        throws RemoteException;

    Double oDouble
        (StatelessClientContext context,
         Double arg)
        throws RemoteException;

    double[] pArrDouble
        (StatelessClientContext context,
         double[] arg)
        throws RemoteException;

    Double[] oArrDouble
        (StatelessClientContext context,
         Double[] arg)
        throws RemoteException;

    Collection<Double> colDouble
        (StatelessClientContext context,
         Collection<Double> arg)
        throws RemoteException;

    Set<Double> setDouble
        (StatelessClientContext context,
         Set<Double> arg)
        throws RemoteException;

    MapWrapper<Double,Double> mapDouble
        (StatelessClientContext context,
         MapWrapper<Double,Double> arg)
        throws RemoteException;

    DoubleHolder hldDouble
        (StatelessClientContext context,
         DoubleHolder arg)
        throws RemoteException;


    // Float.

    float pFloat
        (StatelessClientContext context,
         float arg)
        throws RemoteException;

    Float oFloat
        (StatelessClientContext context,
         Float arg)
        throws RemoteException;

    float[] pArrFloat
        (StatelessClientContext context,
         float[] arg)
        throws RemoteException;

    Float[] oArrFloat
        (StatelessClientContext context,
         Float[] arg)
        throws RemoteException;

    Collection<Float> colFloat
        (StatelessClientContext context,
         Collection<Float> arg)
        throws RemoteException;

    Set<Float> setFloat
        (StatelessClientContext context,
         Set<Float> arg)
        throws RemoteException;

    MapWrapper<Float,Float> mapFloat
        (StatelessClientContext context,
         MapWrapper<Float,Float> arg)
        throws RemoteException;

    FloatHolder hldFloat
        (StatelessClientContext context,
         FloatHolder arg)
        throws RemoteException;


    // Integer.

    int pInt
        (StatelessClientContext context,
         int arg)
        throws RemoteException;

    Integer oInt
        (StatelessClientContext context,
         Integer arg)
        throws RemoteException;

    int[] pArrInt
        (StatelessClientContext context,
         int[] arg)
        throws RemoteException;

    Integer[] oArrInt
        (StatelessClientContext context,
         Integer[] arg)
        throws RemoteException;

    Collection<Integer> colInt
        (StatelessClientContext context,
         Collection<Integer> arg)
        throws RemoteException;

    Set<Integer> setInt
        (StatelessClientContext context,
         Set<Integer> arg)
        throws RemoteException;

    MapWrapper<Integer,Integer> mapInt
        (StatelessClientContext context,
         MapWrapper<Integer,Integer> arg)
        throws RemoteException;

    IntegerHolder hldInt
        (StatelessClientContext context,
         IntegerHolder arg)
        throws RemoteException;


    // Long.

    long pLong
        (StatelessClientContext context,
         long arg)
        throws RemoteException;

    Long oLong
        (StatelessClientContext context,
         Long arg)
        throws RemoteException;

    long[] pArrLong
        (StatelessClientContext context,
         long[] arg)
        throws RemoteException;

    Long[] oArrLong
        (StatelessClientContext context,
         Long[] arg)
        throws RemoteException;

    Collection<Long> colLong
        (StatelessClientContext context,
         Collection<Long> arg)
        throws RemoteException;

    Set<Long> setLong
        (StatelessClientContext context,
         Set<Long> arg)
        throws RemoteException;

    MapWrapper<Long,Long> mapLong
        (StatelessClientContext context,
         MapWrapper<Long,Long> arg)
        throws RemoteException;

    LongHolder hldLong
        (StatelessClientContext context,
         LongHolder arg)
        throws RemoteException;


    // Short.

    short pShort
        (StatelessClientContext context,
         short arg)
        throws RemoteException;

    Short oShort
        (StatelessClientContext context,
         Short arg)
        throws RemoteException;

    short[] pArrShort
        (StatelessClientContext context,
         short[] arg)
        throws RemoteException;

    Short[] oArrShort
        (StatelessClientContext context,
         Short[] arg)
        throws RemoteException;

    Collection<Short> colShort
        (StatelessClientContext context,
         Collection<Short> arg)
        throws RemoteException;

    Set<Short> setShort
        (StatelessClientContext context,
         Set<Short> arg)
        throws RemoteException;

    MapWrapper<Short,Short> mapShort
        (StatelessClientContext context,
         MapWrapper<Short,Short> arg)
        throws RemoteException;

    ShortHolder hldShort
        (StatelessClientContext context,
         ShortHolder arg)
        throws RemoteException;


    // String.

    String str
        (StatelessClientContext context,
         String arg)
        throws RemoteException;

    String[] arrStr
        (StatelessClientContext context,
         String[] arg)
        throws RemoteException;

    Collection<String> colStr
        (StatelessClientContext context,
         Collection<String> arg)
        throws RemoteException;

    Set<String> setStr
        (StatelessClientContext context,
         Set<String> arg)
        throws RemoteException;

    MapWrapper<String,String> mapStr
        (StatelessClientContext context,
         MapWrapper<String,String> arg)
        throws RemoteException;

    StringHolder hldStr
        (StatelessClientContext context,
         StringHolder arg)
        throws RemoteException;


    // Big decimal.

    BigDecimal bd
        (StatelessClientContext context,
         BigDecimal arg)
        throws RemoteException;

    BigDecimal[] arrBd
        (StatelessClientContext context,
         BigDecimal[] arg)
        throws RemoteException;

    Collection<BigDecimal> colBd
        (StatelessClientContext context,
         Collection<BigDecimal> arg)
        throws RemoteException;

    Set<BigDecimal> setBd
        (StatelessClientContext context,
         Set<BigDecimal> arg)
        throws RemoteException;

    MapWrapper<BigDecimal,BigDecimal> mapBd
        (StatelessClientContext context,
         MapWrapper<BigDecimal,BigDecimal> arg)
        throws RemoteException;

    BigDecimalHolder hldBd
        (StatelessClientContext context,
         BigDecimalHolder arg)
        throws RemoteException;


    // Big integer.

    BigInteger bi
        (StatelessClientContext context,
         BigInteger arg)
        throws RemoteException;

    BigInteger[] arrBi
        (StatelessClientContext context,
         BigInteger[] arg)
        throws RemoteException;

    Collection<BigInteger> colBi
        (StatelessClientContext context,
         Collection<BigInteger> arg)
        throws RemoteException;

    Set<BigInteger> setBi
        (StatelessClientContext context,
         Set<BigInteger> arg)
        throws RemoteException;

    MapWrapper<BigInteger,BigInteger> mapBi
        (StatelessClientContext context,
         MapWrapper<BigInteger,BigInteger> arg)
        throws RemoteException;

    BigIntegerHolder hldBi
        (StatelessClientContext context,
         BigIntegerHolder arg)
        throws RemoteException;


    // Object with inner classes.

    InnerObject io
        (StatelessClientContext context,
         InnerObject arg)
        throws RemoteException;

    InnerObject[] arrIo
        (StatelessClientContext context,
         InnerObject[] arg)
        throws RemoteException;

    Collection<InnerObject> colIo
        (StatelessClientContext context,
         Collection<InnerObject> arg)
        throws RemoteException;

    Set<InnerObject> setIo
        (StatelessClientContext context,
         Set<InnerObject> arg)
        throws RemoteException;

    MapWrapper<InnerObject,InnerObject> mapIo
        (StatelessClientContext context,
         MapWrapper<InnerObject,InnerObject> arg)
        throws RemoteException;

    InnerObjectHolder hldIo
        (StatelessClientContext context,
         InnerObjectHolder arg)
        throws RemoteException;


    // Enum.

    EnumObject eo
        (StatelessClientContext context,
         EnumObject arg)
        throws RemoteException;

    EnumObject[] arrEo
        (StatelessClientContext context,
         EnumObject[] arg)
        throws RemoteException;

    Collection<EnumObject> colEo
        (StatelessClientContext context,
         Collection<EnumObject> arg)
        throws RemoteException;

    Set<EnumObject> setEo
        (StatelessClientContext context,
         Set<EnumObject> arg)
        throws RemoteException;

    MapWrapper<EnumObject,EnumObject> mapEo
        (StatelessClientContext context,
         MapWrapper<EnumObject,EnumObject> arg)
        throws RemoteException;

    EnumObjectHolder hldEo
        (StatelessClientContext context,
         EnumObjectHolder arg)
        throws RemoteException;


    // Date.

    Date dt
        (StatelessClientContext context,
         Date arg)
        throws RemoteException;

    Date[] arrDt
        (StatelessClientContext context,
         Date[] arg)
        throws RemoteException;

    Collection<Date> colDt
        (StatelessClientContext context,
         Collection<Date> arg)
        throws RemoteException;

    Set<Date> setDt
        (StatelessClientContext context,
         Set<Date> arg)
        throws RemoteException;

    MapWrapper<Date,Date> mapDt
        (StatelessClientContext context,
         MapWrapper<Date,Date> arg)
        throws RemoteException;

    DateHolder hldDt
        (StatelessClientContext context,
         DateHolder arg)
        throws RemoteException;


    // Wrapped date.

    DateWrapper dw
        (StatelessClientContext context,
         DateWrapper arg)
        throws RemoteException;

    DateWrapper[] arrDw
        (StatelessClientContext context,
         DateWrapper[] arg)
        throws RemoteException;

    Collection<DateWrapper> colDw
        (StatelessClientContext context,
         Collection<DateWrapper> arg)
        throws RemoteException;

    Set<DateWrapper> setDw
        (StatelessClientContext context,
         Set<DateWrapper> arg)
        throws RemoteException;

    MapWrapper<DateWrapper,DateWrapper> mapDw
        (StatelessClientContext context,
         MapWrapper<DateWrapper,DateWrapper> arg)
        throws RemoteException;

    DateWrapperHolder hldDw
        (StatelessClientContext context,
         DateWrapperHolder arg)
        throws RemoteException;


    // Locale.

    LocaleWrapper lwrp
        (StatelessClientContext context,
         LocaleWrapper arg)
        throws RemoteException;

    LocaleWrapper[] arrLwrp
        (StatelessClientContext context,
         LocaleWrapper[] arg)
        throws RemoteException;

    Collection<LocaleWrapper> colLwrp
        (StatelessClientContext context,
         Collection<LocaleWrapper> arg)
        throws RemoteException;

    Set<LocaleWrapper> setLwrp
        (StatelessClientContext context,
         Set<LocaleWrapper> arg)
        throws RemoteException;

    MapWrapper<LocaleWrapper,LocaleWrapper> mapLwrp
        (StatelessClientContext context,
         MapWrapper<LocaleWrapper,LocaleWrapper> arg)
        throws RemoteException;

    LocaleWrapperHolder hldLwrp
        (StatelessClientContext context,
         LocaleWrapperHolder arg)
        throws RemoteException;


    // Wrapper for serializable objects.

    SerWrapper<WrappableObject> swrp
        (StatelessClientContext context,
         SerWrapper<WrappableObject> arg)
        throws RemoteException;

    /*
     * LIMITATION: adding generic type, e.g.  SerWrapper<?> or
     * SerWrapper<WrappableObject>, leads to run-time problems:
     */
    SerWrapper[] arrSwrp
        (StatelessClientContext context,
         SerWrapper[] arg)
        throws RemoteException;

    /*
     * LIMITATION: adding generic type, e.g.  SerWrapper<?> or
     * SerWrapper<WrappableObject>, leads to run-time problems:
     */
    Collection<SerWrapper> colSwrp
        (StatelessClientContext context,
         Collection<SerWrapper> arg)
        throws RemoteException;

    /*
     * LIMITATION: adding generic type, e.g.  SerWrapper<?> or
     * SerWrapper<WrappableObject>, leads to run-time problems:
     */
    Set<SerWrapper> setSwrp
        (StatelessClientContext context,
         Set<SerWrapper> arg)
        throws RemoteException;

    /*
     * LIMITATION: adding generic type, e.g.  SerWrapper<?> or
     * SerWrapper<WrappableObject>, leads to run-time problems:
     */
    MapWrapper<SerWrapper,SerWrapper> mapSwrp
        (StatelessClientContext context,
         MapWrapper<SerWrapper,SerWrapper> arg)
        throws RemoteException;

    SerWrapperHolder hldSwrp
        (StatelessClientContext context,
         SerWrapperHolder arg)
        throws RemoteException;


    // Wrapper for comparable & serializable objects.

    CompSerWrapper<WrappableObject> cwrp
        (StatelessClientContext context,
         CompSerWrapper<WrappableObject> arg)
        throws RemoteException;

    CompSerWrapper[] arrCwrp
        (StatelessClientContext context,
         CompSerWrapper[] arg)
        throws RemoteException;

    Collection<CompSerWrapper> colCwrp
        (StatelessClientContext context,
         Collection<CompSerWrapper> arg)
        throws RemoteException;

    Set<CompSerWrapper> setCwrp
        (StatelessClientContext context,
         Set<CompSerWrapper> arg)
        throws RemoteException;

    MapWrapper<CompSerWrapper,CompSerWrapper> mapCwrp
        (StatelessClientContext context,
         MapWrapper<CompSerWrapper,CompSerWrapper> arg)
        throws RemoteException;

    CompSerWrapperHolder hldCwrp
        (StatelessClientContext context,
         CompSerWrapperHolder arg)
        throws RemoteException;


    // Faults.

    void checkedException
        (StatelessClientContext context,
         String message)
        throws RemoteException;

    void runtimeException
        (StatelessClientContext context,
         String message)
        throws RemoteException;

    void error
        (StatelessClientContext context,
         String message)
        throws RemoteException;
}
