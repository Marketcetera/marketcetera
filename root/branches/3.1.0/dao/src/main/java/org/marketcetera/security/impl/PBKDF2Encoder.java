package org.marketcetera.security.impl;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.annotation.concurrent.NotThreadSafe;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.BaseNCodec;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.marketcetera.security.Messages;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.misc.ClassVersion;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/* $License$ */

/**
 * Provides a <code>PasswordEncoder</code> implementation using the
 * <a href="http://en.wikipedia.org/wiki/PBKDF2">PBKDF2</a> standard.
 * 
 * <p>This class is not thread-safe.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: PBKDF2Encoder.java 82384 2012-07-20 19:09:59Z colin $
 * @since $Release$
 */
@NotThreadSafe
@Component
@ClassVersion("$Id: PBKDF2Encoder.java 82384 2012-07-20 19:09:59Z colin $")
public class PBKDF2Encoder
        implements PasswordEncoder
{
    /* (non-Javadoc)
     * @see org.springframework.security.crypto.password.PasswordEncoder#encode(java.lang.CharSequence)
     */
    @Override
    public String encode(CharSequence inRawPassword)
    {
        try {
            Validate.notNull(inRawPassword,
                             Messages.NULL_SOURCE.getText());
            return hash(inRawPassword.toString().toCharArray());
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    /* (non-Javadoc)
     * @see org.springframework.security.crypto.password.PasswordEncoder#matches(java.lang.CharSequence, java.lang.String)
     */
    @Override
    public boolean matches(CharSequence inRawPassword,
                           String inEncodedPassword)
    {
        try {
            String encodedRawPassword = hash(inRawPassword.toString().toCharArray());
            return encodedRawPassword.equals(inEncodedPassword);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * Sets the salt value.
     * 
     * <p>The salt value is expected to be encoded using the encoder {@link #setEncoder(BaseNCodec) specified here}. Note
     * that the salt is subject to a minimum-length requirement that will not be validated until the encoder is invoked.
     * 
     * <p>There is no default salt value.
     *
     * @param inSalt a <code>String</code> value containing an encoded salt value
     * @throws IllegalArgumentException if the salt value is <code>null</code> or empty
     */
    public void setSalt(String inSalt)
    {
        String saltValue = StringUtils.trimToNull(inSalt);
        Validate.notNull(saltValue,
                         Messages.NULL_SALT.getText());
        SLF4JLoggerProxy.debug(PBKDF2Encoder.class,
                               "Setting salt to {}",
                               saltValue);
        salt = saltValue;
    }
    /**
     * Sets the encoder to use to encode and decode raw values including passwords and salts.
     * 
     * <p>Defaults to Base64 encoding.
     *
     * @param inEncoder a <code>BaseNCodec</code> value
     * @throws IllegalArgumentException if the encoder is <code>null</code>.
     */
    public void setEncoder(BaseNCodec inEncoder)
    {
        Validate.notNull(inEncoder,
                         Messages.NULL_ENCODER.getText());
        encoder = inEncoder;
    }
    /**
     * Sets the encoding to use to translate <code>String</code> values to raw byte arrays.
     *
     * <p>Defaults to <code>UTF-8</code>.
     *
     * @param inEncoding a <code>String</code> value
     * @throws IllegalArgumentException if the encoding is <code>null</code> or invalid
     */
    public void setEncoding(String inEncoding)
    {
        String encodingValue = StringUtils.trimToNull(inEncoding);
        Validate.notNull(encodingValue,
                         Messages.NULL_ENCODING.getText());
        Validate.notEmpty(encodingValue,
                          Messages.NULL_ENCODING.getText());
        Validate.isTrue(Charset.isSupported(encodingValue),
                        Messages.INVALID_ENCODING.getText(encodingValue));
        encoding = encodingValue;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("PBKDF2Encoder [salt=").append(salt).append(", iterationCount=").append(iterationCount) //$NON-NLS-1$ //$NON-NLS-2$
                .append(", keyLength=").append(keyLength).append(", encoder=").append(encoder).append(", algorithm=") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                .append(algorithm).append(", encoding=").append(encoding).append("]"); //$NON-NLS-1$ //$NON-NLS-2$
        return builder.toString();
    }
    /**
     * Get the iterationCount value.
     *
     * @return an <code>int</code> value
     */
    public int getIterationCount()
    {
        return iterationCount;
    }
    /**
     * Sets the iterationCount value.
     * 
     * <p>Defaults to 2048.
     *
     * @param inIterationCount an <code>int</code> value
     * @throws IllegalArgumentException if the iteration count is less than the minimum suggested value
     */
    public void setIterationCount(int inIterationCount)
    {
        Validate.isTrue(inIterationCount >= MINIMUM_ITERATION_COUNT,
                        Messages.ITERATION_COUNT_TOO_LOW.getText(inIterationCount,
                                                                 MINIMUM_ITERATION_COUNT));
        iterationCount = inIterationCount;
    }
    /**
     * Get the keyLength value.
     *
     * @return an <code>int</code> value
     */
    public int getKeyLength()
    {
        return keyLength;
    }
    /**
     * Sets the keyLength value.
     * 
     * <p>Defaults to 160.
     *
     * @param inKeyLength an <code>int</code> value
     * @throws IllegalArgumentException if the key length is less than the minimum suggested value
     */
    public void setKeyLength(int inKeyLength)
    {
        Validate.isTrue(inKeyLength >= MINIMUM_KEY_LENGTH,
                        Messages.KEY_LENGTH_TOO_SHORT.getText(inKeyLength,
                                                              MINIMUM_KEY_LENGTH));
        keyLength = inKeyLength;
    }
    /**
     * Get the encoder value.
     *
     * @return a <code>BaseNCodec</code> value
     */
    public BaseNCodec getEncoder()
    {
        return encoder;
    }
    /**
     * Get the algorithm value.
     *
     * @return a <code>String</code> value
     */
    public String getAlgorithm()
    {
        return algorithm;
    }
    /**
     * Sets the algorithm value.
     * 
     * <p>Defaults to <code>PBKDF2WithHmacSHA1</code>.
     *
     * @param inAlgorithm a <code>String</code> value
     * @throws IllegalArgumentException if the given algorithm is <code>null</code> or empty
     */
    public void setAlgorithm(String inAlgorithm)
    {
        String algorithmValue = StringUtils.trimToNull(inAlgorithm);
        Validate.notNull(algorithmValue,
                         Messages.NULL_ALGORITHM.getText());
        algorithm = algorithmValue;
    }
    /**
     * Get the encoding value.
     *
     * @return a <code>String</code> value
     */
    public String getEncoding()
    {
        return encoding;
    }
    /**
     * Get the salt value.
     *
     * @return a <code>String</code> value
     */
    public String getSalt()
    {
        return salt;
    }
    /**
     * Hashes the given source to an encoded, hashed result.
     *
     * @param inSource a <code>char[]</code> value
     * @return a <code>String</code> value
     * @throws UnsupportedEncodingException if the encoding is unknown
     * @throws NoSuchAlgorithmException if the algorithm does not exist
     * @throws InvalidKeySpecException if the key spec is invalid
     * @throws IllegalArgumentException if a validation error occurs
     */
    private String hash(char[] inSource)
            throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeySpecException
    {
        Validate.notNull(inSource,
                         Messages.NULL_SOURCE.getText());
        Validate.isTrue(inSource.length > 0,
                        Messages.NULL_SOURCE.getText());
        Validate.notNull(salt,
                         Messages.NULL_SALT.getText());
        byte[] rawSalt = encoder.decode(salt.getBytes(encoding));
        Validate.isTrue(rawSalt.length >= MINIMUM_SALT_LENGTH,
                        Messages.SALT_TOO_SHORT.getText(rawSalt.length,
                                                        MINIMUM_SALT_LENGTH));
        KeySpec spec = new PBEKeySpec(inSource,
                                      rawSalt,
                                      iterationCount,
                                      keyLength);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(algorithm);
        return new String(encoder.encode(keyFactory.generateSecret(spec).getEncoded()),
                          encoding);
    }
    /**
     * salt value used to generate key spec
     */
    private String salt;
    /**
     * iteration count used to generate key spec
     */
    private int iterationCount = MINIMUM_ITERATION_COUNT;
    /**
     * key length used to generate key spec
     */
    private int keyLength = MINIMUM_KEY_LENGTH;
    /**
     * encoder used to encode/decode hashed values
     */
    private BaseNCodec encoder = new Base64();
    /**
     * algorithm used to create key factory
     */
    private String algorithm = "PBKDF2WithHmacSHA1"; //$NON-NLS-1$
    /**
     * encoding used for input/output strings
     */
    private String encoding = "UTF-8"; //$NON-NLS-1$
    /**
     * minimum acceptable iteration count for key spec generation
     */
    public static final int MINIMUM_ITERATION_COUNT = 2048;
    /**
     * minimum acceptable key length for key spec generation
     */
    public static final int MINIMUM_KEY_LENGTH = 160;
    /**
     * minimum length of the salt value for key spec generation
     */
    public static final int MINIMUM_SALT_LENGTH = 64;
}
