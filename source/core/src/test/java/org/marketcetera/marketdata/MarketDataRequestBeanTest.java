package org.marketcetera.marketdata;

import static org.junit.Assert.*;
import static org.marketcetera.marketdata.AssetClass.OPTION;
import static org.marketcetera.marketdata.Content.DIVIDEND;
import static org.marketcetera.marketdata.Content.LATEST_TICK;
import static org.marketcetera.marketdata.Content.LEVEL_2;
import static org.marketcetera.marketdata.Content.*;

import java.util.*;

import org.junit.Test;
import org.marketcetera.module.ExpectedFailure;
import org.marketcetera.util.test.EqualityAssert;

/* $License$ */

/**
 * Tests {@link MarketDataRequestBean}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class MarketDataRequestBeanTest
{
    /**
     * Tests {@link MarketDataRequestBean#getProvider()} and {@link MarketDataRequestBean#setProvider(String)}. 
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void provider()
            throws Exception
    {
        MarketDataRequestBean bean = new MarketDataRequestBean();
        assertNull(bean.getProvider());
        bean.setProvider("");
        assertEquals("",
                     bean.getProvider());
        bean.setProvider("provider");
        assertEquals("provider",
                     bean.getProvider());
        bean.setProvider(null);
        assertNull(bean.getProvider());
        assertNotNull(bean.toString());
    }
    /**
     * Tests {@link MarketDataRequestBean#getExchange()} and {@link MarketDataRequestBean#setExchange(String)}. 
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void exchange()
            throws Exception
    {
        MarketDataRequestBean bean = new MarketDataRequestBean();
        assertNull(bean.getExchange());
        bean.setExchange("");
        assertEquals("",
                     bean.getExchange());
        bean.setExchange("Exchange");
        assertEquals("Exchange",
                     bean.getExchange());
        bean.setExchange(null);
        assertNull(bean.getExchange());
        assertNotNull(bean.toString());
    }
    /**
     * Tests {@link MarketDataRequestBean#getAssetClass()} and {@link MarketDataRequestBean#setAssetClass(AssetClass)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void assetClass()
            throws Exception
    {
        MarketDataRequestBean bean = new MarketDataRequestBean();
        assertNull(bean.getAssetClass());
        bean.setAssetClass(AssetClass.EQUITY);
        assertEquals(AssetClass.EQUITY,
                     bean.getAssetClass());
        bean.setAssetClass(null);
        assertNull(bean.getAssetClass());
        assertNotNull(bean.toString());
    }
    /**
     * Tests {@link MarketDataRequestBean#getSymbols()} and {@link MarketDataRequestBean#setSymbols(java.util.Collection)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void symbols()
            throws Exception
    {
        final MarketDataRequestBean bean = new MarketDataRequestBean();
        assertTrue(bean.getSymbols().isEmpty());
        new ExpectedFailure<NullPointerException>() {
            @Override
            protected void run()
                    throws Exception
            {
                bean.setSymbols(null);
            }
        };
       bean.setSymbols(Arrays.asList(symbols));
       assertEquals(new LinkedHashSet<String>(Arrays.asList(expectedSymbols)),
                    bean.getSymbols());
       bean.setSymbols(new ArrayList<String>()); 
       assertTrue(bean.getSymbols().isEmpty());
       assertNotNull(bean.toString());
    }
    /**
     * Tests {@link MarketDataRequestBean#getUnderlyingSymbols()} and {@link MarketDataRequestBean#setUnderlyingSymbols(java.util.Collection)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void underlyingSymbols()
            throws Exception
    {
        final MarketDataRequestBean bean = new MarketDataRequestBean();
        assertTrue(bean.getUnderlyingSymbols().isEmpty());
        new ExpectedFailure<NullPointerException>() {
            @Override
            protected void run()
                    throws Exception
            {
                bean.setUnderlyingSymbols(null);
            }
        };
       bean.setUnderlyingSymbols(Arrays.asList(symbols));
       assertTrue(Arrays.equals(expectedSymbols,
                                bean.getUnderlyingSymbols().toArray(new String[0])));
       bean.setUnderlyingSymbols(new ArrayList<String>()); 
       assertTrue(bean.getUnderlyingSymbols().isEmpty());
       assertNotNull(bean.toString());
    }
    /**
     * Tests {@link MarketDataRequestBean#getParameters()}, {@link MarketDataRequestBean#setParameter(String, String)},
     * and {@link MarketDataRequestBean#setParameters(java.util.Map)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void parameters()
            throws Exception
    {
        final MarketDataRequestBean bean = new MarketDataRequestBean();
        Map<String,String> expectedResults = new HashMap<String,String>();
        assertTrue(bean.getParameters().isEmpty());
        bean.setParameter(null,
                          "value");
        expectedResults.put(null,
                            "value");
        assertEquals(expectedResults,
                     bean.getParameters());
        bean.setParameter("",
                          "value2");
        expectedResults.put("",
                            "value2");
        assertEquals(expectedResults,
                     bean.getParameters());
        bean.setParameter("key",
                          "value3");
        expectedResults.put("key",
                            "value3");
        assertEquals(expectedResults,
                     bean.getParameters());
        bean.setParameter("key",
                          "value4");
        expectedResults.put("key",
                            "value4");
        assertEquals(expectedResults,
                     bean.getParameters());
        bean.setParameter("key",
                          null);
        expectedResults.put("key",
                            null);
        assertEquals(expectedResults,
                     bean.getParameters());
        bean.setParameter("key2",
                          "");
        expectedResults.put("key2",
                            "");
        assertEquals(expectedResults,
                     bean.getParameters());
        // test setParameters
        expectedResults.clear();
        new ExpectedFailure<NullPointerException>() {
            @Override
            protected void run()
                    throws Exception
            {
                bean.setParameters(null);
            }
        };
        Map<String,String> testMap = new HashMap<String,String>();
        bean.setParameters(testMap);
        assertTrue(bean.getParameters().isEmpty());
        testMap.put(null,
                    "value");
        expectedResults.put(null,
                            "value");
        bean.setParameters(testMap);
        assertEquals(expectedResults,
                     bean.getParameters());
        testMap.put("",
                    "value2");
        expectedResults.put("",
                            "value2");
        bean.setParameters(testMap);
        assertEquals(expectedResults,
                     bean.getParameters());
        testMap.put("key3",
                    "value3");
        expectedResults.put("key3",
                            "value3");
        bean.setParameters(testMap);
        assertEquals(expectedResults,
                     bean.getParameters());
        testMap.put("key3",
                    null);
        expectedResults.put("key3",
                            null);
        bean.setParameters(testMap);
        assertEquals(expectedResults,
                     bean.getParameters());
        testMap.put("key4",
                    "");
        expectedResults.put("key4",
                            "");
        bean.setParameters(testMap);
        assertEquals(expectedResults,
                     bean.getParameters());
        assertNotNull(bean.toString());
    }
    /**
     * Tests {@link MarketDataRequestBean#getContent()} and {@link MarketDataRequestBean#setContent(java.util.Collection)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void content()
            throws Exception
    {
        final MarketDataRequestBean bean = new MarketDataRequestBean();
        new ExpectedFailure<NullPointerException>() {
            @Override
            protected void run()
                    throws Exception
            {
                bean.setContent(null);
            }
        };
        assertTrue(bean.getContent().isEmpty());
        bean.setContent(Arrays.asList(contents));
        assertTrue(Arrays.equals(expectedContents,
                                 bean.getContent().toArray(new Content[0])));
        bean.setContent(EnumSet.noneOf(Content.class));
        assertTrue(bean.getContent().isEmpty());
        assertNotNull(bean.toString());
    }
    /**
     * Tests {@link MarketDataRequestBean#clone()}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testClone()
            throws Exception
    {
        // this test will create a MDRB and change its attributes one-by-one.  as each attribute
        //  is changed, a clone will be spawned.  the clone will be equal to the original until the
        //  next attribute is changed.  within each attribute test block, the attribute is modified
        //  to make sure that the clone has its own attribute value, not a reference to the original's.
        // clone an empty bean
       MarketDataRequestBean bean1 = new MarketDataRequestBean();
       // asset class
       MarketDataRequestBean bean2 = bean1.clone();
       EqualityAssert.assertEquality(bean1,
                                     bean2,
                                     this);
       bean1.setAssetClass(OPTION);
       EqualityAssert.assertEquality(bean1,
                                     bean1.clone(),
                                     bean2);
       // content
       MarketDataRequestBean bean3 = bean1.clone();
       EqualityAssert.assertEquality(bean1,
                                     bean3,
                                     bean2);
       bean1.setContent(Arrays.asList(contents));
       EqualityAssert.assertEquality(bean1,
                                     bean1.clone(),
                                     bean2,
                                     bean3);
       // exchange
       MarketDataRequestBean bean4 = bean1.clone();
       EqualityAssert.assertEquality(bean1,
                                     bean4,
                                     bean2,
                                     bean3);
       bean1.setExchange("exchange");
       EqualityAssert.assertEquality(bean1,
                                     bean1.clone(),
                                     bean2,
                                     bean3,
                                     bean4);
       // parameters
       MarketDataRequestBean bean5 = bean1.clone();
       EqualityAssert.assertEquality(bean1,
                                     bean5,
                                     bean2,
                                     bean3,
                                     bean4);
       bean1.setParameter("key",
                          "value");
       EqualityAssert.assertEquality(bean1,
                                     bean1.clone(),
                                     bean2,
                                     bean3,
                                     bean4,
                                     bean5);
       // provider
       MarketDataRequestBean bean6 = bean1.clone();
       EqualityAssert.assertEquality(bean1,
                                     bean6,
                                     bean2,
                                     bean3,
                                     bean4,
                                     bean5);
       bean1.setProvider("provider");
       EqualityAssert.assertEquality(bean1,
                                     bean1.clone(),
                                     bean2,
                                     bean3,
                                     bean4,
                                     bean5,
                                     bean6);
       // symbols
       MarketDataRequestBean bean7 = bean1.clone();
       EqualityAssert.assertEquality(bean1,
                                     bean7,
                                     bean2,
                                     bean3,
                                     bean4,
                                     bean5,
                                     bean6);
       List<String> symbols = Arrays.asList(new String[] { "symbol1", "symbol2" } );
       bean1.setSymbols(symbols);
       EqualityAssert.assertEquality(bean1,
                                     bean1.clone(),
                                     bean2,
                                     bean3,
                                     bean4,
                                     bean5,
                                     bean6,
                                     bean7);
       // special test for non-empty collections, make sure the collection was deep-copied
       bean7 = bean1.clone();
       EqualityAssert.assertEquality(bean1,
                                     bean7,
                                     bean2);
       assertFalse(bean7.getSymbols().isEmpty());
       assertEquals(bean1.getSymbols(),
                    bean7.getSymbols());
       symbols = Arrays.asList(new String[] { "symbol1", "symbol2", "symbol3" } );
       bean1.setSymbols(symbols);
       EqualityAssert.assertEquality(bean1,
                                     bean1.clone(),
                                     bean7);
       // underlying symbols
       MarketDataRequestBean bean8 = bean1.clone();
       EqualityAssert.assertEquality(bean1,
                                     bean8,
                                     bean2,
                                     bean3,
                                     bean4,
                                     bean5,
                                     bean6,
                                     bean7);
       symbols = Arrays.asList(new String[] { "symbol1", "symbol2" } );
       bean1.setUnderlyingSymbols(symbols);
       EqualityAssert.assertEquality(bean1,
                                     bean1.clone(),
                                     bean2,
                                     bean3,
                                     bean4,
                                     bean5,
                                     bean6,
                                     bean7,
                                     bean8);
       // special test for non-empty collections, make sure the collection was deep-copied
       bean8 = bean1.clone();
       EqualityAssert.assertEquality(bean1,
                                     bean8,
                                     bean2);
       assertFalse(bean8.getUnderlyingSymbols().isEmpty());
       assertEquals(bean1.getUnderlyingSymbols(),
                    bean8.getUnderlyingSymbols());
       symbols = Arrays.asList(new String[] { "symbol1", "symbol2", "symbol3" } );
       bean1.setUnderlyingSymbols(symbols);
       EqualityAssert.assertEquality(bean1,
                                     bean1.clone(),
                                     bean8);
       assertNotNull(bean1.toString());
    }
    /**
     * Tests {@link MarketDataRequest#hashCode()} and {@link MarketDataRequest#equals(Object)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void hashCodeAndEquals()
            throws Exception
    {
        // the pattern of this test is to create two beans: 1 & 2 that will always
        //  be equal (but not same).  there will also be a bean: 3 that is always
        //  not equal to 1 & 2.  the attributes are set in 3 one-at-a-time to a
        //  non-null or non-empty value.  after each comparison, the values in 1 & 2
        //  are synced to 3 to make 1 & 2 progressively non-null or non-empty.  in this
        //  way, each component of the equals (and hashCode) method is tested in sequence.
        MarketDataRequestBean bean1 = new MarketDataRequestBean();
        // this bean will always be the same
        MarketDataRequestBean bean2 = bean1.clone();
        // compare with null and not MDRB
        EqualityAssert.assertEquality(bean1,
                                      bean2,
                                      this,
                                      null);
        // this bean will always be different
        MarketDataRequestBean bean3 = new MarketDataRequestBean();
        // differ by AssetClass
        assertNull(bean1.getAssetClass());
        bean3.setAssetClass(OPTION);
        EqualityAssert.assertEquality(bean1,
                                      bean2,
                                      bean3);
        bean1 = bean3.clone();
        bean2 = bean1.clone();
        // differ by content
        assertTrue(bean1.getContent().isEmpty());
        bean3.setContent(EnumSet.of(TOP_OF_BOOK));
        EqualityAssert.assertEquality(bean1,
                                      bean2,
                                      bean3);
        bean1 = bean3.clone();
        bean2 = bean1.clone();
        // differ by exchange
        assertNull(bean1.getExchange());
        bean3.setExchange("exchange");
        EqualityAssert.assertEquality(bean1,
                                      bean2,
                                      bean3);
        bean1 = bean3.clone();
        bean2 = bean1.clone();
        // differ by provider
        assertNull(bean1.getProvider());
        bean3.setProvider("provider");
        EqualityAssert.assertEquality(bean1,
                                      bean2,
                                      bean3);
        bean1 = bean3.clone();
        bean2 = bean1.clone();
        // differ by symbols
        assertTrue(bean1.getSymbols().isEmpty());
        bean3.setSymbols(Arrays.asList(symbols));
        EqualityAssert.assertEquality(bean1,
                                      bean2,
                                      bean3);
        bean1 = bean3.clone();
        bean2 = bean1.clone();
        // differ by underlying symbols
        assertTrue(bean1.getUnderlyingSymbols().isEmpty());
        bean3.setUnderlyingSymbols(Arrays.asList(symbols));
        EqualityAssert.assertEquality(bean1,
                                      bean2,
                                      bean3);
        bean1 = bean3.clone();
        bean2 = bean1.clone();
        // differ by parameters
        assertTrue(bean1.getParameters().isEmpty());
        bean3.setParameter("key",
                           "value");
        EqualityAssert.assertEquality(bean1,
                                      bean2,
                                      bean3);
    }
    /**
     * test content values (contain duplicates and null) 
     */
    private static Content[] contents = new Content[] { LEVEL_2, DIVIDEND, LATEST_TICK, LATEST_TICK, OPEN_BOOK, null };
    /**
     * expected content values (same as {@link #contents} except duplicates removed)
     */
    private static Content[] expectedContents = new Content[] { LEVEL_2, DIVIDEND, LATEST_TICK, OPEN_BOOK, null };
    /**
     * test symbols (contain duplicates, null, and empty)
     */
    private static String[] symbols = new String[] { "A", "ABC", "ABCD", "B", "A", "AAA", null, "" };
    /**
     * expected symbol values (same as {@link #symbols} except duplicates removed)
     */
    private static String[] expectedSymbols = new String[] { "A", "ABC", "ABCD", "B", "AAA", null, "" };
}
