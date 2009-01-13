package org.marketcetera.util.ws.wrappers;

import java.util.Locale;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.marketcetera.util.test.TestCaseBase;

import static org.junit.Assert.*;
import static org.marketcetera.util.test.EqualityAssert.*;

/**
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id$
 */

/* $License$ */

public class MarshalledLocaleTest
    extends TestCaseBase
{
    private static final String TEST_LANGUAGE=
        "language";
    private static final String TEST_COUNTRY=
        "COUNTRY";
    private static final String TEST_VARIANT=
        "variant";
    public static final Locale TEST_LOCALE=
        new Locale(TEST_LANGUAGE,TEST_COUNTRY,TEST_VARIANT);


    @Test
    public void all()
    {
        MarshalledLocale m=new MarshalledLocale(TEST_LOCALE);
        assertEquals(TEST_LANGUAGE,m.getLanguage());
        assertEquals(TEST_COUNTRY,m.getCountry());
        assertEquals(TEST_VARIANT,m.getVariant());
        assertEquals(TEST_LOCALE,m.toLocale());

        MarshalledLocale copy=new MarshalledLocale(TEST_LOCALE);

        MarshalledLocale empty=new MarshalledLocale();
        assertNull(empty.getLanguage());
        assertNull(empty.getCountry());
        assertNull(empty.getVariant());
        assertNull(empty.toLocale());

        assertEquality(m,copy,empty);
        assertEquality(empty,new MarshalledLocale(null));

        assertEquals(TEST_LOCALE.toString(),m.toString());
        assertEquals(StringUtils.EMPTY,empty.toString());
 
        m.setVariant(null);
        assertEquals(TEST_LANGUAGE,m.getLanguage());
        assertEquals(TEST_COUNTRY,m.getCountry());
        assertNull(m.getVariant());
        assertEquals(new Locale(TEST_LANGUAGE,TEST_COUNTRY),m.toLocale());

        m.setCountry(null);
        assertEquals(TEST_LANGUAGE,m.getLanguage());
        assertNull(m.getCountry());
        assertNull(m.getVariant());
        assertEquals(new Locale(TEST_LANGUAGE),m.toLocale());

        m.setLanguage(null);
        assertNull(m.getLanguage());
        assertNull(m.getCountry());
        assertNull(m.getVariant());
        assertNull(m.toLocale());
        assertEquals(empty,m);

        m.setVariant(TEST_VARIANT);
        assertEquals(empty,m);
        m.setCountry(TEST_COUNTRY);
        assertEquals(empty,m);
        m.setLanguage(TEST_LANGUAGE);
        assertEquals(copy,m);
   }
}
