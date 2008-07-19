package org.marketcetera.util.unicode;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;
import org.marketcetera.util.misc.ClassVersion;

/**
 * A {@link UnicodeOutputStreamWriter} which directs its output to a
 * file. All constructors pass-thru to a {@link
 * UnicodeOutputStreamWriter} constructor an {@link OutputStream} that
 * is built using a {@link FileOutputStream} constructor.
 *
 * <p>Appending to an existing file is safe: if the file exists, and
 * is non-empty, then no signature BOM is recorded (regardless of the
 * specified or deduced signature/charset) because we assume that a
 * BOM is already present. However, this class does not attempt to
 * check that, in this case, the existing signature BOM and associated
 * charset match the one supplied in the constructor.</p>
 *
 * @author tlerios@marketcetera.com
 * @since 0.6.0
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
public class UnicodeFileWriter
    extends UnicodeOutputStreamWriter
{

    // CONSTRUCTORS.

    /**
     * Creates a new writer using the given file name, and the {@link
     * UnicodeOutputStreamWriter#UnicodeOutputStreamWriter(OutputStream)}
     * and {@link FileOutputStream#FileOutputStream(String)}
     * constructors.
     *
     * @param fileName The file name.
     *
     * @throws FileNotFoundException Propagated from the {@link
     * FileOutputStream} constructor.
     */

    public UnicodeFileWriter
        (String fileName)
        throws FileNotFoundException
    {
        super(new FileOutputStream(fileName));
    }

    /**
     * Creates a new writer using the given file name and
     * signature/charset, and the {@link
     * UnicodeOutputStreamWriter#UnicodeOutputStreamWriter(OutputStream,
     * SignatureCharset)} and {@link
     * FileOutputStream#FileOutputStream(String)} constructors.
     *
     * @param fileName The file name.
     * @param requestedSignatureCharset The signature/charset. It may
     * be null to use the default JVM charset.
     *
     * @throws FileNotFoundException Propagated from the {@link
     * FileOutputStream} constructor.
     */

    public UnicodeFileWriter
        (String fileName,
         SignatureCharset requestedSignatureCharset)
        throws FileNotFoundException
    {
        super(new FileOutputStream(fileName),requestedSignatureCharset);
    }

    /**
     * Creates a new writer using the given file name and reader, and
     * the {@link
     * UnicodeOutputStreamWriter#UnicodeOutputStreamWriter(OutputStream,
     * Reader)} and {@link FileOutputStream#FileOutputStream(String)}
     * constructors.
     *
     * @param fileName The file name.
     * @param reader The reader.
     *
     * @throws IOException Thrown if an I/O error occurs,
     * incl. propagation from the {@link FileOutputStream}
     * constructor.
     */

    public UnicodeFileWriter
        (String fileName,
         Reader reader)
        throws IOException
    {
        super(new FileOutputStream(fileName),reader);
    }

    /**
     * Creates a new writer using the given file name, and the {@link
     * UnicodeOutputStreamWriter#UnicodeOutputStreamWriter(OutputStream)}
     * and {@link FileOutputStream#FileOutputStream(String,boolean)}
     * constructors.
     *
     * @param fileName The file name.
     * @param append True if data should be appended to the file.
     *
     * @throws FileNotFoundException Propagated from the {@link
     * FileOutputStream} constructor.
     */

    public UnicodeFileWriter
        (String fileName,
         boolean append)
        throws FileNotFoundException
    {
        super(new FileOutputStream(fileName,append));
    }

    /**
     * Creates a new writer using the given file name and
     * signature/charset, and the {@link
     * UnicodeOutputStreamWriter#UnicodeOutputStreamWriter(OutputStream,
     * SignatureCharset)} and {@link
     * FileOutputStream#FileOutputStream(String,boolean)}
     * constructors.
     *
     * @param fileName The file name.
     * @param append True if data should be appended to the file.
     * @param requestedSignatureCharset The signature/charset. It may
     * be null to use the default JVM charset.
     *
     * @throws FileNotFoundException Propagated from the {@link
     * FileOutputStream} constructor.
     */

    public UnicodeFileWriter
        (String fileName,
         boolean append,
         SignatureCharset requestedSignatureCharset)
        throws FileNotFoundException
    {
        super(new FileOutputStream(fileName,append),requestedSignatureCharset,
              shouldWriteSignature(fileName,append));
    }

    /**
     * Creates a new writer using the given file name and reader, and
     * the {@link
     * UnicodeOutputStreamWriter#UnicodeOutputStreamWriter(OutputStream,
     * Reader)} and {@link
     * FileOutputStream#FileOutputStream(String,boolean)}
     * constructors.
     *
     * @param fileName The file name.
     * @param append True if data should be appended to the file.
     * @param reader The reader.
     *
     * @throws IOException Thrown if an I/O error occurs,
     * incl. propagation from the {@link FileOutputStream}
     * constructor.
     */

    public UnicodeFileWriter
        (String fileName,
         boolean append,
         Reader reader)
        throws IOException
    {
        super(new FileOutputStream(fileName,append),reader,
              shouldWriteSignature(fileName,append));
    }

    /**
     * Creates a new writer using the given file, and the {@link
     * UnicodeOutputStreamWriter#UnicodeOutputStreamWriter(OutputStream)}
     * and {@link FileOutputStream#FileOutputStream(File)}
     * constructors.
     *
     * @param file The file.
     *
     * @throws FileNotFoundException Propagated from the {@link
     * FileOutputStream} constructor.
     */

    public UnicodeFileWriter
        (File file)
        throws FileNotFoundException
    {
        super(new FileOutputStream(file));
    }

    /**
     * Creates a new writer using the given file and
     * signature/charset, and the {@link
     * UnicodeOutputStreamWriter#UnicodeOutputStreamWriter(OutputStream,
     * SignatureCharset)} and {@link
     * FileOutputStream#FileOutputStream(File)} constructors.
     *
     * @param file The file.
     * @param requestedSignatureCharset The signature/charset. It may
     * be null to use the default JVM charset.
     *
     * @throws FileNotFoundException Propagated from the {@link
     * FileOutputStream} constructor.
     */

    public UnicodeFileWriter
        (File file,
         SignatureCharset requestedSignatureCharset)
        throws FileNotFoundException
    {
        super(new FileOutputStream(file),requestedSignatureCharset);
    }

    /**
     * Creates a new writer using the given file and reader, and the
     * {@link
     * UnicodeOutputStreamWriter#UnicodeOutputStreamWriter(OutputStream,
     * Reader)} and {@link FileOutputStream#FileOutputStream(File)}
     * constructors.
     *
     * @param file The file.
     * @param reader The reader.
     *
     * @throws IOException Thrown if an I/O error occurs,
     * incl. propagation from the {@link FileOutputStream} constructor.
     */

    public UnicodeFileWriter
        (File file,
         Reader reader)
        throws IOException
    {
        super(new FileOutputStream(file),reader);
    }

    /**
     * Creates a new writer using the given file, and the {@link
     * UnicodeOutputStreamWriter#UnicodeOutputStreamWriter(OutputStream)}
     * and {@link FileOutputStream#FileOutputStream(File,boolean)}
     * constructors.
     *
     * @param file The file.
     * @param append True if data should be appended to the file.
     *
     * @throws FileNotFoundException Propagated from the {@link
     * FileOutputStream} constructor.
     */

    public UnicodeFileWriter
        (File file,
         boolean append)
        throws FileNotFoundException
    {
        super(new FileOutputStream(file,append));
    }

    /**
     * Creates a new writer using the given file and
     * signature/charset, and the {@link
     * UnicodeOutputStreamWriter#UnicodeOutputStreamWriter(OutputStream,
     * SignatureCharset)} and {@link
     * FileOutputStream#FileOutputStream(File,boolean)} constructors.
     *
     * @param file The file.
     * @param append True if data should be appended to the file.
     * @param requestedSignatureCharset The signature/charset. It may
     * be null to use the default JVM charset.
     *
     * @throws FileNotFoundException Propagated from the {@link
     * FileOutputStream} constructor.
     */

    public UnicodeFileWriter
        (File file,
         boolean append,
         SignatureCharset requestedSignatureCharset)
        throws FileNotFoundException
    {
        super(new FileOutputStream(file,append),requestedSignatureCharset,
              shouldWriteSignature(file,append));
    }

    /**
     * Creates a new writer using the given file and reader, and the
     * {@link
     * UnicodeOutputStreamWriter#UnicodeOutputStreamWriter(OutputStream,
     * Reader)} and {@link
     * FileOutputStream#FileOutputStream(File,boolean)} constructors.
     *
     * @param file The file.
     * @param append True if data should be appended to the file.
     * @param reader The reader.
     *
     * @throws IOException Thrown if an I/O error occurs,
     * incl. propagation from the {@link FileOutputStream} constructor.
     */

    public UnicodeFileWriter
        (File file,
         boolean append,
         Reader reader)
        throws IOException
    {
        super(new FileOutputStream(file,append),reader,
              shouldWriteSignature(file,append));
    }

    /**
     * Creates a new writer using the given file descriptor, and the
     * {@link
     * UnicodeOutputStreamWriter#UnicodeOutputStreamWriter(OutputStream)}
     * and {@link FileOutputStream#FileOutputStream(FileDescriptor)}
     * constructors.
     *
     * @param fd The file descriptor.
     */

    public UnicodeFileWriter
        (FileDescriptor fd)
    {
        super(new FileOutputStream(fd));
    }

    /**
     * Creates a new writer using the given file descriptor and
     * signature/charset, and the {@link
     * UnicodeOutputStreamWriter#UnicodeOutputStreamWriter(OutputStream,
     * SignatureCharset)} and {@link
     * FileOutputStream#FileOutputStream(FileDescriptor)} constructors.
     *
     * @param fd The file descriptor.
     * @param requestedSignatureCharset The signature/charset. It may
     * be null to use the default JVM charset.
     */

    public UnicodeFileWriter
        (FileDescriptor fd,
         SignatureCharset requestedSignatureCharset)
    {
        super(new FileOutputStream(fd),requestedSignatureCharset);
    }

    /**
     * Creates a new writer using the given file descriptor and
     * reader, and the {@link
     * UnicodeOutputStreamWriter#UnicodeOutputStreamWriter(OutputStream,
     * Reader)} and {@link
     * FileOutputStream#FileOutputStream(FileDescriptor)}
     * constructors.
     *
     * @param fd The file descriptor.
     * @param reader The reader.
     *
     * @throws IOException Thrown if an I/O error occurs.
     */

    public UnicodeFileWriter
        (FileDescriptor fd,
         Reader reader)
        throws IOException
    {
        super(new FileOutputStream(fd),reader);
    }


    // CLASS METHODS.

    /**
     * Checks whether signature injection should take place for the
     * given file and output mode.
     *
     * @param file The file.
     * @param append True if data should be appended to the file.
     *
     * @return True if so.
     */

    private static boolean shouldWriteSignature
        (File file,
         boolean append)
    {
        if (!append) {
            return true;
        }
        try {
            return (file.length()==0);
        } catch (SecurityException ex) {
            Messages.CANNOT_GET_LENGTH.warn
                (UnicodeFileWriter.class,ex,file.getName());
            return true;
        }
    }

    /**
     * Checks whether signature injection should take place for the
     * given file and output mode.
     *
     * @param fileName The file name.
     * @param append True if data should be appended to the file.
     *
     * @return True if so.
     */

    private static boolean shouldWriteSignature
        (String fileName,
         boolean append)
    {
        return shouldWriteSignature(new File(fileName),append);
    }
}
