package org.marketcetera.util.unicode;

import java.io.FileOutputStream;
import java.io.Reader;

/**
 * @author tlerios@marketcetera.com
 * @since 0.6.0
 * @version $Id$
 */

/* $License$ */

public class FileEncoderDescriptorTest
    extends FileEncoderTestBase
{
    private static FileOutputStream out;

    @Override
    protected byte[] encode
        (String string)
        throws Exception
    {
        out=null;
        try {
            return encode(new WriterCreator()
                {
                    @Override
                    public UnicodeFileWriter create()
                        throws Exception
                    {
                        out=new FileOutputStream(TEST_FILE);
                        return new UnicodeFileWriter(out.getFD());
                    }
                },null,null,string);
        } finally {
            if (out!=null) {
                out.close();
            }
        }
    }

    @Override
    protected byte[] encode
        (final SignatureCharset sc,
         String string)
        throws Exception
    {
        out=null;
        try {
            return encode(new WriterCreator()
                {
                    @Override
                    public UnicodeFileWriter create()
                        throws Exception
                    {
                        out=new FileOutputStream(TEST_FILE);
                        return new UnicodeFileWriter(out.getFD(),sc);
                    }
                },sc,sc,string);
        } finally {
            if (out!=null) {
                out.close();
            }
        }
    }

    @Override
    protected byte[] encode
        (final Reader reader,
         SignatureCharset sc,
         String string)
        throws Exception
    { 
        out=null;
        try {
            return encode(new WriterCreator()
                {
                    @Override
                    public UnicodeFileWriter create()
                        throws Exception
                    {
                        out=new FileOutputStream(TEST_FILE);
                        return new UnicodeFileWriter(out.getFD(),reader);
                    }
                },sc,sc,string);
        } finally {
            if (out!=null) {
                out.close();
            }
        }
    }
}
