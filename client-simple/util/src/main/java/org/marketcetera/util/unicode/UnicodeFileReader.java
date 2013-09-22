package org.marketcetera.util.unicode;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import org.marketcetera.util.misc.ClassVersion;

/**
 * A {@link UnicodeInputStreamReader} which gets its input from a
 * file. All constructors pass-thru to a {@link
 * UnicodeInputStreamReader} constructor an {@link InputStream} that
 * is built using a {@link FileInputStream} constructor.
 *
 * @author tlerios@marketcetera.com
 * @since 0.6.0
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
public class UnicodeFileReader
    extends UnicodeInputStreamReader
{

    // CONSTRUCTORS.

    /**
     * Creates a new reader using the given file name, and the {@link
     * UnicodeInputStreamReader#UnicodeInputStreamReader(InputStream)}
     * and {@link FileInputStream#FileInputStream(String)}
     * constructors.
     *
     * @param fileName The file name.
     *
     * @throws FileNotFoundException Propagated from the {@link
     * FileInputStream} constructor.
     */

    public UnicodeFileReader
        (String fileName)
        throws FileNotFoundException
    {
        super(new FileInputStream(fileName));
    }

    /**
     * Creates a new reader using the given file name and
     * signature/charset, and the {@link
     * UnicodeInputStreamReader#UnicodeInputStreamReader(InputStream,
     * SignatureCharset)} and {@link
     * FileInputStream#FileInputStream(String)} constructors.
     *
     * @param fileName The file name.
     * @param requestedSignatureCharset The signature/charset. It may
     * be null to use the default JVM charset.
     *
     * @throws FileNotFoundException Propagated from the {@link
     * FileInputStream} constructor.
     */

    public UnicodeFileReader
        (String fileName,
         SignatureCharset requestedSignatureCharset)
        throws FileNotFoundException
    {
        super(new FileInputStream(fileName),requestedSignatureCharset);
    }

    /**
     * Creates a new reader using the given file name and decoding
     * strategy, and the {@link
     * UnicodeInputStreamReader#UnicodeInputStreamReader(InputStream,
     * DecodingStrategy)} and {@link
     * FileInputStream#FileInputStream(String)} constructors.
     *
     * @param fileName The file name.
     * @param decodingStrategy The decoding strategy. It may be null
     * to use the default JVM charset.
     *
     * @throws FileNotFoundException Propagated from the {@link
     * FileInputStream} constructor.
     */

    public UnicodeFileReader
        (String fileName,
         DecodingStrategy decodingStrategy)
        throws FileNotFoundException
    {
        super(new FileInputStream(fileName),decodingStrategy);
    }

    /**
     * Creates a new reader using the given file, and the {@link
     * UnicodeInputStreamReader#UnicodeInputStreamReader(InputStream)}
     * and {@link FileInputStream#FileInputStream(File)} constructors.
     *
     * @param file The file.
     *
     * @throws FileNotFoundException Propagated from the {@link
     * FileInputStream} constructor.
     */

    public UnicodeFileReader
        (File file)
        throws FileNotFoundException
    {
        super(new FileInputStream(file));
    }

    /**
     * Creates a new reader using the given file and
     * signature/charset, and the {@link
     * UnicodeInputStreamReader#UnicodeInputStreamReader(InputStream,
     * SignatureCharset)} and {@link
     * FileInputStream#FileInputStream(File)} constructors.
     *
     * @param file The file.
     * @param requestedSignatureCharset The signature/charset. It may
     * be null to use the default JVM charset.
     *
     * @throws FileNotFoundException Propagated from the {@link
     * FileInputStream} constructor.
     */

    public UnicodeFileReader
        (File file,
         SignatureCharset requestedSignatureCharset)
        throws FileNotFoundException
    {
        super(new FileInputStream(file),requestedSignatureCharset);
    }

    /**
     * Creates a new reader using the given file and decoding
     * strategy, and the {@link
     * UnicodeInputStreamReader#UnicodeInputStreamReader(InputStream,
     * DecodingStrategy)} and {@link
     * FileInputStream#FileInputStream(File)} constructors.
     *
     * @param file The file.
     * @param decodingStrategy The decoding strategy. It may be null
     * to use the default JVM charset.
     *
     * @throws FileNotFoundException Propagated from the {@link
     * FileInputStream} constructor.
     */

    public UnicodeFileReader
        (File file,
         DecodingStrategy decodingStrategy)
        throws FileNotFoundException
    {
        super(new FileInputStream(file),decodingStrategy);
    }

    /**
     * Creates a new reader using the given file descriptor, and the
     * {@link
     * UnicodeInputStreamReader#UnicodeInputStreamReader(InputStream)}
     * and {@link FileInputStream#FileInputStream(FileDescriptor)}
     * constructors.
     *
     * @param fd The file descriptor.
     */

    public UnicodeFileReader
        (FileDescriptor fd)
    {
        super(new FileInputStream(fd));
    }

    /**
     * Creates a new reader using the given file descriptor and
     * signature/charset, and the {@link
     * UnicodeInputStreamReader#UnicodeInputStreamReader(InputStream,
     * SignatureCharset)} and {@link
     * FileInputStream#FileInputStream(FileDescriptor)} constructors.
     *
     * @param fd The file descriptor.
     * @param requestedSignatureCharset The signature/charset. It may
     * be null to use the default JVM charset.
     */

    public UnicodeFileReader
        (FileDescriptor fd,
         SignatureCharset requestedSignatureCharset)
    {
        super(new FileInputStream(fd),requestedSignatureCharset);
    }

    /**
     * Creates a new reader using the given file descriptor and
     * decoding strategy, the {@link
     * UnicodeInputStreamReader#UnicodeInputStreamReader(InputStream,
     * DecodingStrategy)} and {@link
     * FileInputStream#FileInputStream(FileDescriptor)} constructors.
     *
     * @param fd The file descriptor.
     * @param decodingStrategy The decoding strategy. It may be null
     * to use the default JVM charset.
     */

    public UnicodeFileReader
        (FileDescriptor fd,
         DecodingStrategy decodingStrategy)
    {
        super(new FileInputStream(fd),decodingStrategy);
    }
}
