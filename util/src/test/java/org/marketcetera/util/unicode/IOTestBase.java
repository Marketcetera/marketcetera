package org.marketcetera.util.unicode;

import java.io.File;
import org.apache.commons.lang.ArrayUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.marketcetera.util.file.Deleter;
import org.marketcetera.util.test.TestCaseBase;

import static org.marketcetera.util.test.UnicodeData.*;

/**
 * @author tlerios@marketcetera.com
 * @since 0.6.0
 * @version $Id$
 */

/* $License$ */

@Ignore
public abstract class IOTestBase
    extends TestCaseBase
{
    protected static final String TEST_FILE_ROOT=
        DIR_ROOT+File.separator+"unicode"+File.separator;
    protected static final String TEST_FILE=
        TEST_FILE_ROOT+"sample.txt";

    
    protected abstract void testNative()
        throws Exception;

    protected abstract void testSignatureCharset
        (SignatureCharset sc,
         byte[] bytes)
        throws Exception;

    protected abstract void testStrategy
        (DecodingStrategy strategy,
         SignatureCharset sc,
         String string,
         byte[] bytes)
        throws Exception;
    

    @Before
    @After
    public void setupTearDownIOTest()
        throws Exception
    {
        Deleter.apply(TEST_FILE);
    }


    @Test
    public void all()
        throws Exception
    {
        testNative();
        
        testSignatureCharset
            (SignatureCharset.NONE_UTF16BE,COMBO_UTF16BE);
        testSignatureCharset
            (SignatureCharset.NONE_UTF16LE,COMBO_UTF16LE);
        testSignatureCharset
            (SignatureCharset.NONE_UTF32BE,COMBO_UTF32BE);
        testSignatureCharset
            (SignatureCharset.NONE_UTF32LE,COMBO_UTF32LE);
        
        testSignatureCharset
            (SignatureCharset.UTF8_UTF8,ArrayUtils.addAll
             (Signature.UTF8.getMark(),COMBO_UTF8));
        testSignatureCharset
            (SignatureCharset.UTF16BE_UTF16BE,ArrayUtils.addAll
             (Signature.UTF16BE.getMark(),COMBO_UTF16BE));
        testSignatureCharset
            (SignatureCharset.UTF16LE_UTF16LE,ArrayUtils.addAll
             (Signature.UTF16LE.getMark(),COMBO_UTF16LE));
        testSignatureCharset
            (SignatureCharset.UTF32BE_UTF32BE,ArrayUtils.addAll
             (Signature.UTF32BE.getMark(),COMBO_UTF32BE));
        testSignatureCharset
            (SignatureCharset.UTF32LE_UTF32LE,ArrayUtils.addAll
             (Signature.UTF32LE.getMark(),COMBO_UTF32LE));

        testStrategy
            (DecodingStrategy.UTF8_DEFAULT,SignatureCharset.NONE_UTF8,
             COMBO,COMBO_UTF8);
        testStrategy
            (DecodingStrategy.UTF16_DEFAULT,SignatureCharset.NONE_UTF16BE,
             COMBO,COMBO_UTF16BE);
        testStrategy
            (DecodingStrategy.UTF32_DEFAULT,SignatureCharset.NONE_UTF32BE,
             COMBO,COMBO_UTF32BE);
        testStrategy
            (DecodingStrategy.SIG_REQ,null,
             HELLO_EN,HELLO_EN_NAT);
    }
}
