package org.marketcetera.util.quickfix;

import java.util.Locale;
import org.apache.commons.lang.SystemUtils;
import org.apache.log4j.Level;
import org.junit.Before;
import org.junit.BeforeClass;
import org.marketcetera.util.log.ActiveLocale;
import org.marketcetera.util.test.TestCaseBase;
import quickfix.DataDictionary;

/**
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id$
 */

/* $License$ */

public class AnalyzerTestBase
    extends TestCaseBase
{
    protected static final String TEST_MESSAGE_CATEGORY=
        AnalyzedMessage.class.getName();
    protected static final String TEST_FIELD_CATEGORY=
        AnalyzedField.class.getName();
    protected static final String TEST_HEADER=
        SystemUtils.LINE_SEPARATOR+
        "Validation error"+
        SystemUtils.LINE_SEPARATOR+
        " Required tag missing, field=34"+
        SystemUtils.LINE_SEPARATOR+
        "Header"+
        SystemUtils.LINE_SEPARATOR+
        " BeginString [8R] = FIX.4.2"+
        SystemUtils.LINE_SEPARATOR+
        " BodyLength [9R] = ";
    protected static final String TEST_FOOTER=
        SystemUtils.LINE_SEPARATOR+
        "Trailer"+
        SystemUtils.LINE_SEPARATOR+
        " CheckSum [10R] = ";
    protected static DataDictionary TEST_DICTIONARY;
    protected static final char SOH=
        '\u0001';


    @BeforeClass
    public static void setupClassAnalyzerTestBase()
        throws Exception
    {
        TEST_DICTIONARY=new DataDictionary("FIX42.xml");
    }

    @Before
    public void setupAnalyzerTestBase()
    {
        ActiveLocale.setProcessLocale(Locale.ROOT);
        setLevel(TEST_MESSAGE_CATEGORY,Level.ERROR);
        setLevel(TEST_FIELD_CATEGORY,Level.ERROR);
    }
}
