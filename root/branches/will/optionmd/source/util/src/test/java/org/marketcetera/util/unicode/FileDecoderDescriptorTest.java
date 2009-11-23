package org.marketcetera.util.unicode;

import java.io.FileInputStream;

/**
 * @author tlerios@marketcetera.com
 * @since 0.6.0
 * @version $Id$
 */

/* $License$ */

public class FileDecoderDescriptorTest
    extends FileDecoderTestBase
{
    private static FileInputStream in;

    @Override
    protected String decode
        (byte[] bytes)
        throws Exception
    {
        in=null;
        try {
            return decode(new ReaderCreator()
                {
                    @Override
                    public UnicodeFileReader create()
                        throws Exception
                    {
                        in=new FileInputStream(TEST_FILE);
                        return new UnicodeFileReader(in.getFD());
                    }
                },null,null,null,bytes);
        } finally {
            if (in!=null) {
                in.close();
            }
        }
    }

    @Override
    protected String decode
        (final SignatureCharset sc,
         byte[] bytes)
        throws Exception
    {
        in=null;
        try {
            return decode(new ReaderCreator()
                {
                    @Override
                    public UnicodeFileReader create()
                        throws Exception
                    {
                        in=new FileInputStream(TEST_FILE);
                        return new UnicodeFileReader(in.getFD(),sc);
                    }
                },null,sc,sc,bytes);
        } finally {
            if (in!=null) {
                in.close();
            }
        }
    }

    @Override
    protected String decode
        (final DecodingStrategy strategy,
         SignatureCharset sc,
         byte[] bytes)
        throws Exception
    {
        in=null;
        try {
            return decode(new ReaderCreator()
                {
                    @Override
                    public UnicodeFileReader create()
                        throws Exception
                    {
                        in=new FileInputStream(TEST_FILE);
                        return new UnicodeFileReader(in.getFD(),strategy);
                    }
                },strategy,null,sc,bytes);
        } finally {
            if (in!=null) {
                in.close();
            }
        }
    }
}
