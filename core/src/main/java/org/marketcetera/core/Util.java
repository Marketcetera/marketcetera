package org.marketcetera.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.URL;
import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicLong;

import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.ws.tags.AppId;

/**
 * Collection of random utilities.
 * 
 * @author Toli Kuznets
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 */
@ClassVersion("$Id$")
public class Util
{
    /** Tries to load the named file from a classpath
     * If the file name doesn't start with a leading / then
     * if we fail to load it we prepend the / and try again
     * @param inFileName
     * @param inObj some object to get the classloader from
     * @return The URL to the resource or NULL if it's not found
     */
    public static URL loadFileFromClasspath(String inFileName, Object inObj)
    {
        if((inFileName == null) || ("".equals(inFileName)))  return null; //$NON-NLS-1$

        URL resource = inObj.getClass().getResource(inFileName);
        if((resource == null) && !inFileName.startsWith("/")) { //$NON-NLS-1$
            resource = inObj.getClass().getResource("/"+inFileName); //$NON-NLS-1$
        }
        return resource;
    }

    /**
     * Reads the entire file and stuffs it into a StringBuffer and returns the string
     * The file is loaded from classpath.
     * Use wisely, this will choke on very large files.
     *
     * @param inFileName
     * @param inObj some object to get the classloader from
     * @return The entire contents of the file
     */
    public static String getStringFromFile(String inFileName, Object inObj) throws Exception
    {
        URL url = Util.loadFileFromClasspath(inFileName,  inObj);
        BufferedReader reader = new BufferedReader(new FileReader(new File(url.getPath())));
        String line = null;
        StringBuffer result = new StringBuffer(2000);
        while((line = reader.readLine()) != null) {
            result.append(line);
        }
        reader.close();
        return result.toString();
    }
    /**
     * Deep copies the given object.
     *
     * @param inOriginalObject a <code>Clazz</code> value
     * @return a <code>Clazz</code> value
     * @throws IOException 
     * @throws ClassNotFoundException 
     */
    @SuppressWarnings("unchecked")
    public static <Clazz extends Serializable> Clazz deepCopy(Clazz inOriginalObject)
            throws IOException, ClassNotFoundException
    {
        Clazz objectToReturn = null;
        // Write the object out to a byte array
        FastByteArrayOutputStream fbos = new FastByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(fbos);
        out.writeObject(inOriginalObject);
        out.flush();
        out.close();
        // Retrieve an input stream from the byte array and read
        // a copy of the object back in. 
        ObjectInputStream in = new ObjectInputStream(fbos.getInputStream());
        objectToReturn = (Clazz)in.readObject();
        return objectToReturn;
    }
    /**
     * the character used to prevent a delimiter or separator from being interpreted literally
     */
    public static final String ESCAPE_CHARACTER = "\\"; //$NON-NLS-1$
    /**
     * the version of {@link #ESCAPE_CHARACTER} to use within a key or value
     */
    public static final String ESCAPED_ESCAPE_CHARACTER = ESCAPE_CHARACTER + ESCAPE_CHARACTER;
    /**
     * the delimiter used to distinguish key/value pairs in the string representation of properties 
     */
    public static final String KEY_VALUE_DELIMITER = ":"; //$NON-NLS-1$
    /**
     * the version of {@link #KEY_VALUE_DELIMITER} to use within a key or value
     */
    public static final String ESCAPED_KEY_VALUE_DELIMITER = ESCAPE_CHARACTER + KEY_VALUE_DELIMITER;
    /**
     * the separator used to separate key/value pairs in the string representation of properties 
     */
    public static final String KEY_VALUE_SEPARATOR = "="; //$NON-NLS-1$
    /**
     * the version of {@link #KEY_VALUE_SEPARATOR} to use within a key or value
     */
    public static final String ESCAPED_KEY_VALUE_SEPARATOR = ESCAPE_CHARACTER + KEY_VALUE_SEPARATOR;
    /**
     * base value used to replace a delimiter or separator in a key or value
     */
    private static final String PROCESSING_TOKEN = "$TOKEN-%s$"; //$NON-NLS-1$
    /**
     * Creates a <code>Properties</code> object from the given <code>String</code>.
     *
     * <p>This function assumes that the <code>String</code> consists of a series of key/value pairs separated by
     * the {@link #KEY_VALUE_DELIMITER}.  The key/value pairs themselves are separated by the {@link #KEY_VALUE_SEPARATOR}.
     * Any malformed entries are discarded.  A best-effort will be made to retain as many key/value pairs as possible.
     * 
     * <p>If either the key or value contains {@link #ESCAPE_CHARACTER}, {@link #KEY_VALUE_DELIMITER}, or {@link #KEY_VALUE_SEPARATOR},
     * these characters must be escaped using the {@link #ESCAPE_CHARACTER}.
     * 
     * @param inCondensedProperties a <code>String</code> value
     * @return a <code>Properties</code> value or null if <code>inCondensedProperties</code> is null or empty
     */
    public static final Properties propertiesFromString(String inCondensedProperties)
    {
        if(inCondensedProperties == null ||
           inCondensedProperties.isEmpty()) {
            return null;
        }
        // first, replace any escaped escape characters with a token ("\" -> "\\", e.g., path=c:\value -> path=c:$TOKEN-1$value)
        Pair<String,String> processedForEscapeCharacter = tokenizeEscapedDelimiters(inCondensedProperties,
                                                                                    ESCAPED_ESCAPE_CHARACTER);
        // next, replace any key/value delimiters with another token (":" -> "\:", e.g. path=c:$TOKEN-1$value -> path=c$TOKEN-2$$TOKEN-1$value)
        Pair<String,String> processedForKeyValueDelimiter = tokenizeEscapedDelimiters(processedForEscapeCharacter.getFirstMember(),
                                                                                      ESCAPED_KEY_VALUE_DELIMITER);
        // the result has now replaced any instances of the escaped key value delimiter with a token that will not
        //  be noticed when we split the properties string
        String[] statements = processedForKeyValueDelimiter.getFirstMember().split(KEY_VALUE_DELIMITER);
        Properties props = new Properties();
        // each statement should be "x=y"
        for(String statement : statements) {
            // the statement may contain a token which stands for an escaped key value delimiter or an escaped escape character 
            //  replace them with the intended unescaped character, this is what the user intended
            statement = untokenizeEscapedDelimiters(statement,
                                                    processedForKeyValueDelimiter.getSecondMember(),
                                                    KEY_VALUE_DELIMITER);
            Pair<String,String> processedForKeyValueSeparator = tokenizeEscapedDelimiters(statement,
                                                                                          ESCAPED_KEY_VALUE_SEPARATOR);
            String processedKeyValueSeparatorProperties = processedForKeyValueSeparator.getFirstMember();
            String keyValueSeparatorToken = processedForKeyValueSeparator.getSecondMember();
            if(processedKeyValueSeparatorProperties.contains(KEY_VALUE_SEPARATOR)) {
                String[] subStatements = processedKeyValueSeparatorProperties.split(KEY_VALUE_SEPARATOR);
                if(subStatements != null &&
                   (subStatements.length >= 1 &&
                    subStatements.length <= 2)) {
                    String key = untokenizeEscapedDelimiters(subStatements[0],
                                                             processedForEscapeCharacter.getSecondMember(),
                                                             ESCAPE_CHARACTER);
                    String value = ""; //$NON-NLS-1$
                    if(subStatements.length == 2) {
                        value = untokenizeEscapedDelimiters(subStatements[1],
                                                            processedForEscapeCharacter.getSecondMember(),
                                                            ESCAPE_CHARACTER);
                    }
                    key = untokenizeEscapedDelimiters(key,
                                                      keyValueSeparatorToken,
                                                      KEY_VALUE_SEPARATOR);
                    value = untokenizeEscapedDelimiters(value,
                                                        keyValueSeparatorToken,
                                                        KEY_VALUE_SEPARATOR);
                    props.setProperty(key,
                                      value);
                } else {
                    SLF4JLoggerProxy.debug(Util.class,
                                           "Putative key/value \"{}\" discarded", //$NON-NLS-1$
                                           (subStatements == null ? "null" : Arrays.toString(subStatements))); //$NON-NLS-1$
                }
            } else {
                SLF4JLoggerProxy.debug(Util.class,
                                       "Putative key/value \"{}\" discarded because it does not contain '{}'", //$NON-NLS-1$
                                       processedKeyValueSeparatorProperties,
                                       KEY_VALUE_SEPARATOR);
            }
        }
        return props;
    }
    /**
     * Takes the given source and replaces the given escaped delimiter with an arbitrary token guaranteed to not already exist
     * in the given source.
     * 
     * <p>The token chosen to replace the escaped delimiter will not otherwise exist in the source.  All occurrences
     * of the escaped delimiter will be replaced by the same token.
     * 
     * <p>The returned value is the tuple of the processed source (escaped delimiter replaced by token) and the actual
     * token used to replace the escaped delimiter.  Non-escaped delimiters will not be replaced.  If the source does
     * not contain the escaped delimiter, the source is returned unmodified.  The second member of the returned tuple
     * is a generated token that may be ignored.
     *
     * @param inSource a <code>String</code> value containing the source to be processed
     * @param inEscapedDelimiter a <code>String</code> value containing the escaped delimiter to be removed
     * @return a <code>Pair&lt;String,String&gt;</code> value containing the tuple of the processed source and the token used
     */
    private static Pair<String,String> tokenizeEscapedDelimiters(String inSource,
                                                                 String inEscapedDelimiter)
    {
        String generatedToken = String.format(PROCESSING_TOKEN,
                                              counter.incrementAndGet());
        String result = inSource;
        // create a token that is guaranteed not to be in the source
        while(inSource.contains(generatedToken)) {
            generatedToken = String.format(PROCESSING_TOKEN,
                                           counter.incrementAndGet());
        }
        result = inSource.replace(inEscapedDelimiter,
                                  generatedToken);
        return new Pair<String,String>(result,
                                       generatedToken);
    }
    /**
     * Takes the given source and replaces the given token with the given delimiter.
     * 
     * <p>If the source does not contain the given token, the source will be returned unmodified.
     *
     * @param inSource a <code>String</code> value containing source to be untokenized
     * @param inToken a <code>String</code> value containing the token to replace
     * @param inDelimiter a <code>String</code> value containing the value to replace the tokens with
     * @return
     */
    private static String untokenizeEscapedDelimiters(String inSource,
                                                      String inToken,
                                                      String inDelimiter)
    {
        return inSource.replace(inToken,
                                inDelimiter);
    }
    /**
     * Creates a <code>String</code> object from the given <code>Properties</code> object. 
     *
     * <p>This function returns a <code>String</code> containing a series of key/value pairs representing this object.
     * Each key/value pair is separated by the {@link #KEY_VALUE_DELIMITER}.  The pairs themselves are separated by
     * {@link #KEY_VALUE_SEPARATOR}.
     * 
     * <p>Note that if any of the keys or values of the <code>Properties</code> object contains any of
     * {@link #KEY_VALUE_DELIMITER}, {@link #KEY_VALUE_SEPARATOR}, or {@link #ESCAPE_CHARACTER} character,
     * the resulting String will have these values escaped with the {@link #ESCAPE_CHARACTER}.
     *
     * @param inProperties a <code>Properties</code> value
     * @return a <code>String</code> value or null if <code>inProperties</code> is null or empty
     */
    public static String propertiesToString(Properties inProperties)
    {
        if(inProperties == null ||
           inProperties.isEmpty()) {
            return null;
        }
        StringBuffer output = new StringBuffer();
        boolean delimiterNeeded = false;
        for(Object keyObject : inProperties.keySet()) {
            if(delimiterNeeded) {
                output.append(KEY_VALUE_DELIMITER);
            } else {
                delimiterNeeded = true;
            }
            String key = (String)keyObject;
            String value = (String)inProperties.getProperty(key);
            // escape ESCAPE_CHARACTER, KEY_VALUE_DELIMITER, and KEY_VALUE_SEPARATOR
            String escapedKey = untokenizeEscapedDelimiters(key,
                                                            ESCAPE_CHARACTER,
                                                            ESCAPED_ESCAPE_CHARACTER);
            escapedKey = untokenizeEscapedDelimiters(escapedKey,
                                                     KEY_VALUE_DELIMITER,
                                                     ESCAPED_KEY_VALUE_DELIMITER);
            escapedKey = untokenizeEscapedDelimiters(escapedKey,
                                                     KEY_VALUE_SEPARATOR,
                                                     ESCAPED_KEY_VALUE_SEPARATOR);
            String escapedValue = untokenizeEscapedDelimiters(value,
                                                              ESCAPE_CHARACTER,
                                                              ESCAPED_ESCAPE_CHARACTER);
            escapedValue = untokenizeEscapedDelimiters(escapedValue,
                                                       KEY_VALUE_DELIMITER,
                                                       ESCAPED_KEY_VALUE_DELIMITER);
            escapedValue = untokenizeEscapedDelimiters(escapedValue,
                                                       KEY_VALUE_SEPARATOR,
                                                       ESCAPED_KEY_VALUE_SEPARATOR);
            output.append(escapedKey).append(KEY_VALUE_SEPARATOR).append(escapedValue);
        }
        return output.toString();
    }

    /**
     * Returns the name portion of the given application ID.
     *
     * @param id The application ID. It may be null.
     * @return The name portion. It may be null if the provided ID
     *         lacks a name.
     */
    public static String getName(AppId id) {
        if ((id == null) || (id.getValue() == null)) {
            return null;
        }
        int index = id.getValue().indexOf(APP_ID_VERSION_SEPARATOR);
        String value = index == -1
                ? id.getValue()
                : id.getValue().substring(0, index);
        return value.isEmpty()
                ? null
                : value;
    }

    /**
     * Returns the version portion of the given application ID.
     *
     * @param id The application ID. It may be null.
     *
     * @return The version portion. It may be null if the provided ID
     * lacks a version.
     */
    public static String getVersion(AppId id)
    {
        if ((id==null) || (id.getValue()==null)) {
            return null;
        }
        int index=id.getValue().indexOf(APP_ID_VERSION_SEPARATOR);
        if (index==-1) {
            return null;
        }
        index++;
        if (index>=id.getValue().length()) {
            return null;
        }
        return id.getValue().substring(index);
    }
    /**
     * Returns an AppId, given the app name and version.
     *
     * @param inName the application name.
     * @param inVersion the application version.
     *
     * @return the application version instance.
     */
    public static AppId getAppId(String inName, String inVersion)
    {
        return new AppId(inName + APP_ID_VERSION_SEPARATOR + inVersion);
    }
    /**
     * Provides an optimized <code>InputStream</code> implementation.
     *
     * @author Phillip Isenhour
     * @version $Id$
     * @since $Release$
     */
    public static class FastByteArrayInputStream
            extends InputStream
    {
        /**
         * Create a new FastByteArrayInputStream instance.
         *
         * @param inBuffer a <code>byte[]</code> value
         * @param inCount an <code>int</code> value
         */
        public FastByteArrayInputStream(byte[] inBuffer,
                                        int inCount)
        {
            this.buf = inBuffer;
            this.count = inCount;
        }
        /* (non-Javadoc)
         * @see java.io.InputStream#read()
         */
        @Override
        public int read()
                throws IOException
        {
            return (pos < count) ? (buf[pos++] & 0xff) : -1;
        }
        /* (non-Javadoc)
         * @see java.io.InputStream#read(byte[], int, int)
         */
        @Override
        public int read(byte[] inBuffer,
                        int inOffset,
                        int inLen)
                throws IOException
        {
            if(pos >= count) {
                return -1;
            }
            if((pos + inLen) > count) {
                inLen = (count - pos);
            }
            System.arraycopy(buf,
                             pos,
                             inBuffer,
                             inOffset,
                             inLen);
            pos += inLen;
            return inLen;
        }
        /* (non-Javadoc)
         * @see java.io.InputStream#skip(long)
         */
        @Override
        public long skip(long inSkip)
                throws IOException
        {
            if((pos + inSkip) > count) {
                inSkip = count - pos;
            }
            if(inSkip < 0) {
                return 0;
            }
            pos += inSkip;
            return inSkip;
        }
        /* (non-Javadoc)
         * @see java.io.InputStream#available()
         */
        @Override
        public int available()
                throws IOException
        {
            return count - pos;
        }
        /**
         * Our byte buffer
         */
        private byte[] buf = null;
        /**
         * Number of bytes that we can read from the buffer
         */
        private int count = 0;
        /**
         * Number of bytes that have been read from the buffer
         */
        private int pos = 0;
    }
    /**
     * Provides an optimized <code>OutputStream</code> implementation.
     *
     * @author Phillip Isenhour
     * @version $Id$
     * @since $Release$
     */
    public static class FastByteArrayOutputStream
            extends OutputStream
    {
        /**
         * Create a new FastByteArrayOutputStream instance.
         *
         * @param inInitSize an <code>int</code> value
         */
        public FastByteArrayOutputStream(int inInitSize)
        {
            size = 0;
            buf = new byte[inInitSize];
        }
        /**
         * Create a new FastByteArrayOutputStream instance with an initial capacity size of 5k.
         */
        public FastByteArrayOutputStream()
        {
            this(5 * 1024);
        }
        /**
         * Gets the current size.
         *
         * @return an <code>int</code> value
         */
        public int getSize()
        {
            return size;
        }
        /**
         * Returns the byte array containing the written data.
         *
         *<p>Note that this array will almost always be larger than the amount of data actually written.
         *
         * @return a <code>byte[]</code> value
         */
        public byte[] getByteArray()
        {
            return buf;
        }
        /* (non-Javadoc)
         * @see java.io.OutputStream#write(int)
         */
        @Override
        public void write(int inByte)
                throws IOException
        {
            verifyBufferSize(size + 1);
            buf[size++] = (byte)inByte;
        }
        /* (non-Javadoc)
         * @see java.io.OutputStream#write(byte[])
         */
        @Override
        public void write(byte[] inBytes)
                throws IOException
        {
            verifyBufferSize(size + inBytes.length);
            System.arraycopy(inBytes, 0, buf, size, inBytes.length);
            size += inBytes.length;
        }
        /* (non-Javadoc)
         * @see java.io.OutputStream#write(byte[], int, int)
         */
        @Override
        public void write(byte[] inBytes,
                          int inOffset,
                          int inLength)
                throws IOException
        {
            verifyBufferSize(size + inLength);
            System.arraycopy(inBytes, inOffset, buf, size, inLength);
            size += inLength;
        }
        /**
         * Resets the object.
         */
        public void reset()
        {
            size = 0;
        }
        /**
         * Returns an InputStream for reading back the written data
         *
         * @return an <code>InputStream</code> value
         */
        public InputStream getInputStream()
        {
            return new FastByteArrayInputStream(buf, size);
        }
        /**
         * Ensures that we have a large enough buffer for the given size.
         *
         * @param inSize an <code>int</code> value
         */
        private void verifyBufferSize(int inSize)
        {
            if (inSize > buf.length) {
                byte[] old = buf;
                buf = new byte[Math.max(inSize, 2 * buf.length )];
                System.arraycopy(old, 0, buf, 0, old.length);
                old = null;
            }
        }
        /**
         * buffer value
         */
        private byte[] buf = null;
        /**
         * size value
         */
        private int size = 0;
    }
    /**
     * The the version separator used to separate Application Name and Version
     * number in application IDs.
     */
    private static final String APP_ID_VERSION_SEPARATOR="/"; //$NON-NLS-1$
    /**
     * counter used to guarantee unique tokens
     */
    private static final AtomicLong counter = new AtomicLong(0);
}
