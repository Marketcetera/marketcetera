package org.marketcetera.security.impl;

import java.nio.charset.Charset;
import java.security.SecureRandom;
import java.util.Map;
import java.util.Random;

import org.apache.commons.codec.binary.Base32;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.BaseNCodec;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.marketcetera.core.ExpectedFailure;
import org.marketcetera.core.LoggerConfiguration;
import org.marketcetera.security.Messages;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.test.UnicodeData;

import static org.junit.Assert.*;

/* $License$ */

/**
 * Tests {@link PBKDF2Encoder}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: PBKDF2EncoderTest.java 82318 2012-03-22 22:37:06Z colin $
 * @since $Release$
 */
public class PBKDF2EncoderTest
{
    /**
     * Run once before all tests.
     *
     * @throws Exception if an unexpected error occurs
     */
    @BeforeClass
    public static void once()
            throws Exception
    {
        LoggerConfiguration.logSetup();
    }
    /**
     * Run before each test. 
     *
     * @throws Exception if an unexpected error occurs
     */
    @Before
    public void setup()
            throws Exception
    {
        hasher = new PBKDF2Encoder();
    }
    /**
     * Tests validation of salt.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testSalt()
            throws Exception
    {
        new ExpectedFailure<IllegalArgumentException>(Messages.NULL_SALT.getText()) {
            @Override
            protected void run()
                    throws Exception
            {
                hasher.setSalt(null);
            }
        };
        new ExpectedFailure<IllegalArgumentException>(Messages.NULL_SALT.getText()) {
            @Override
            protected void run()
                    throws Exception
            {
                hasher.setSalt("");
            }
        };
        new ExpectedFailure<IllegalArgumentException>(Messages.NULL_SALT.getText()) {
            @Override
            protected void run()
                    throws Exception
            {
                hasher.setSalt("     ");
            }
        };
        String saltValue = "this is a salt value " + System.nanoTime();
        hasher.setSalt(saltValue);
        assertEquals(saltValue,
                     hasher.getSalt());
        hasher.setSalt("       " + saltValue + "        ");
        assertEquals(saltValue,
                     hasher.getSalt());
        hasher.setSalt(UnicodeData.HELLO_GR);
        assertEquals(UnicodeData.HELLO_GR,
                     hasher.getSalt());
    }
    /**
     * Tests {@link PBKDF2Encoder#setEncoder(org.apache.commons.codec.binary.BaseNCodec)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testEncoder()
            throws Exception
    {
        new ExpectedFailure<IllegalArgumentException>(Messages.NULL_ENCODER.getText()) {
            @Override
            protected void run()
                    throws Exception
            {
                hasher.setEncoder(null);
            }
        };
        // prove indirectly that the default encoder is base64
        String testValue = "some test value " + System.nanoTime();
        Base64 baselineEncoder = new Base64();
        String expectedEncodedValue = baselineEncoder.encodeAsString(testValue.getBytes(hasher.getEncoding()));
        String actualEncodedValue = hasher.getEncoder().encodeAsString(testValue.getBytes(hasher.getEncoding()));
        assertEquals(expectedEncodedValue,
                     actualEncodedValue);
        // change the encoder and repeat the test
        BaseNCodec testEncoder = new Base32();
        hasher.setEncoder(testEncoder);
        expectedEncodedValue = testEncoder.encodeAsString(testValue.getBytes(hasher.getEncoding()));
        actualEncodedValue = hasher.getEncoder().encodeAsString(testValue.getBytes(hasher.getEncoding()));
        assertEquals(expectedEncodedValue,
                     actualEncodedValue);
    }
    /**
     * Tests {@link PBKDF2Encoder#setEncoding(String)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testEncoding()
            throws Exception
    {
        new ExpectedFailure<IllegalArgumentException>(Messages.NULL_ENCODING.getText()) {
            @Override
            protected void run()
                    throws Exception
            {
                hasher.setEncoding(null);
            }
        };
        new ExpectedFailure<IllegalArgumentException>(Messages.NULL_ENCODING.getText()) {
            @Override
            protected void run()
                    throws Exception
            {
                hasher.setEncoding("");
            }
        };
        new ExpectedFailure<IllegalArgumentException>(Messages.NULL_ENCODING.getText()) {
            @Override
            protected void run()
                    throws Exception
            {
                hasher.setEncoding("    ");
            }
        };
        final String invalidCharset = "not-a-charset";
        assertFalse(Charset.isSupported(invalidCharset));
        new ExpectedFailure<IllegalArgumentException>(Messages.INVALID_ENCODING.getText("not-a-charset")) {
            @Override
            protected void run()
                    throws Exception
            {
                hasher.setEncoding(invalidCharset);
            }
        };
        new ExpectedFailure<IllegalArgumentException>(Messages.INVALID_ENCODING.getText("not-a-charset")) {
            @Override
            protected void run()
                    throws Exception
            {
                hasher.setEncoding("    " + invalidCharset + "     ");
            }
        };
        assertEquals("UTF-8",
                     hasher.getEncoding());
        // find a valid charset that is not UTF-8
        Charset testCharset = null;
        for(Map.Entry<String,Charset> entry : Charset.availableCharsets().entrySet()) {
            if(!(entry.getKey().equals("UTF-8"))) {
                testCharset = entry.getValue();
            }
        }
        assertNotNull("Unable to find a supported charset not UTF-8 in " + Charset.availableCharsets().keySet(),
                      testCharset);
        SLF4JLoggerProxy.debug(PBKDF2EncoderTest.class,
                               "Using {} for encoding test",
                               testCharset);
        hasher.setEncoding(testCharset.name());
        assertEquals(testCharset.name(),
                     hasher.getEncoding());
        hasher.setEncoding("      " + testCharset.name() + "      ");
        assertEquals(testCharset.name(),
                     hasher.getEncoding());
    }
    /**
     * Tests {@link PBKDF2Encoder#setIterationCount(int)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testIterationCount()
            throws Exception
    {
        new ExpectedFailure<IllegalArgumentException>(Messages.ITERATION_COUNT_TOO_LOW.getText(Integer.MIN_VALUE,
                                                                                               PBKDF2Encoder.MINIMUM_ITERATION_COUNT)) {
            @Override
            protected void run()
                    throws Exception
            {
                hasher.setIterationCount(Integer.MIN_VALUE);
            }
        };
        new ExpectedFailure<IllegalArgumentException>(Messages.ITERATION_COUNT_TOO_LOW.getText(0,
                                                                                               PBKDF2Encoder.MINIMUM_ITERATION_COUNT)) {
            @Override
            protected void run()
                    throws Exception
            {
                hasher.setIterationCount(0);
            }
        };
        new ExpectedFailure<IllegalArgumentException>(Messages.ITERATION_COUNT_TOO_LOW.getText(2047,
                                                                                               PBKDF2Encoder.MINIMUM_ITERATION_COUNT)) {
            @Override
            protected void run()
                    throws Exception
            {
                hasher.setIterationCount(2047);
            }
        };
        assertEquals(2048,
                     hasher.getIterationCount());
        hasher.setIterationCount(Integer.MAX_VALUE);
        assertEquals(Integer.MAX_VALUE,
                     hasher.getIterationCount());
        hasher.setIterationCount(PBKDF2Encoder.MINIMUM_ITERATION_COUNT);
        assertEquals(PBKDF2Encoder.MINIMUM_ITERATION_COUNT,
                     hasher.getIterationCount());
    }
    /**
     * Tests {@link PBKDF2Encoder#setKeyLength(int)}. 
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testKeyLength()
            throws Exception
    {
        new ExpectedFailure<IllegalArgumentException>(Messages.KEY_LENGTH_TOO_SHORT.getText(Integer.MIN_VALUE,
                                                                                            PBKDF2Encoder.MINIMUM_KEY_LENGTH)) {
            @Override
            protected void run()
                    throws Exception
            {
                hasher.setKeyLength(Integer.MIN_VALUE);
            }
        };
        new ExpectedFailure<IllegalArgumentException>(Messages.KEY_LENGTH_TOO_SHORT.getText(0,
                                                                                            PBKDF2Encoder.MINIMUM_KEY_LENGTH)) {
            @Override
            protected void run()
                    throws Exception
            {
                hasher.setKeyLength(0);
            }
        };
        new ExpectedFailure<IllegalArgumentException>(Messages.KEY_LENGTH_TOO_SHORT.getText(159,
                                                                                            PBKDF2Encoder.MINIMUM_KEY_LENGTH)) {
            @Override
            protected void run()
                    throws Exception
            {
                hasher.setKeyLength(159);
            }
        };
        assertEquals(160,
                     hasher.getKeyLength());
        hasher.setKeyLength(Integer.MAX_VALUE);
        assertEquals(Integer.MAX_VALUE,
                     hasher.getKeyLength());
        hasher.setKeyLength(PBKDF2Encoder.MINIMUM_KEY_LENGTH);
        assertEquals(PBKDF2Encoder.MINIMUM_KEY_LENGTH,
                     hasher.getKeyLength());
    }
    /**
     * Tests {@link PBKDF2Encoder#setAlgorithm(String)}. 
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testSetAlgorithm()
            throws Exception
    {
        new ExpectedFailure<IllegalArgumentException>(Messages.NULL_ALGORITHM.getText()) {
            @Override
            protected void run()
                    throws Exception
            {
                hasher.setAlgorithm(null);
            }
        };
        new ExpectedFailure<IllegalArgumentException>(Messages.NULL_ALGORITHM.getText()) {
            @Override
            protected void run()
                    throws Exception
            {
                hasher.setAlgorithm("");
            }
        };
        new ExpectedFailure<IllegalArgumentException>(Messages.NULL_ALGORITHM.getText()) {
            @Override
            protected void run()
                    throws Exception
            {
                hasher.setAlgorithm("     ");
            }
        };
        assertEquals("PBKDF2WithHmacSHA1",
                     hasher.getAlgorithm());
        String testAlgo = "not-a-valid-algo-but-not-validated-yet";
        hasher.setAlgorithm(testAlgo);
        assertEquals(testAlgo,
                     hasher.getAlgorithm());
        hasher.setAlgorithm("      " + testAlgo + "     ");
        assertEquals(testAlgo,
                     hasher.getAlgorithm());
    }
    /**
     * Tests {@link PBKDF2Encoder#encode(CharSequence)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testEncode()
            throws Exception
    {
        new ExpectedFailure<IllegalArgumentException>(Messages.NULL_SOURCE.getText()) {
            @Override
            protected void run()
                    throws Exception
            {
                hasher.encode(null);
            }
        };
        new ExpectedFailure<IllegalArgumentException>(Messages.NULL_SOURCE.getText()) {
            @Override
            protected void run()
                    throws Exception
            {
                hasher.encode(new String(new char[0]));
            }
        };
        // no salt
        assertNull(hasher.getSalt());
        new ExpectedFailure<IllegalArgumentException>(Messages.NULL_SALT.getText()) {
            @Override
            protected void run()
                    throws Exception
            {
                hasher.encode("any old thing");
            }
        };
        // salt too short
        hasher.setSalt(generateSalt(1));
        new ExpectedFailure<IllegalArgumentException>(Messages.SALT_TOO_SHORT.getText(1,
                                                                                      PBKDF2Encoder.MINIMUM_SALT_LENGTH)) {
            @Override
            protected void run()
                    throws Exception
            {
                hasher.encode("some value here");
            }
        };
        // salt just right
        String goodSalt = generateSalt(PBKDF2Encoder.MINIMUM_SALT_LENGTH);
        SLF4JLoggerProxy.debug(PBKDF2EncoderTest.class,
                               "{}-byte salt: {}",
                               PBKDF2Encoder.MINIMUM_SALT_LENGTH,
                               goodSalt);
        hasher.setSalt(goodSalt);
        String validAlgo = hasher.getAlgorithm();
        // invalid algo
        hasher.setAlgorithm("this-is-not-a-real-algo");
        new ExpectedFailure<RuntimeException>() {
            @Override
            protected void run()
                    throws Exception
            {
                hasher.encode("something here");
            }
        };
        hasher.setAlgorithm(validAlgo);
        String valid = "test message";
        // do not encode the message
        String result = hasher.encode(valid);
        assertFalse(result.equals(valid));
        // now encode it
        result = hasher.encode(hasher.getEncoder().encodeAsString(valid.getBytes(hasher.getEncoding())));
        // no way to reverse the hash (hopefully), so just assert that it's non-null
        assertNotNull(result);
        // go again with a longer salt
        hasher.setSalt(generateSalt(PBKDF2Encoder.MINIMUM_SALT_LENGTH*2));
        result = hasher.encode(hasher.getEncoder().encodeAsString(valid.getBytes(hasher.getEncoding())));
        assertNotNull(result);
        assertNotNull(hasher.toString());
    }
    /**
     * Generates a valid salt as an encoded string of the given length.
     *
     * @param inLength an <code>int</code> value
     * @return a <code>String</code> value
     */
    private String generateSalt(int inLength)
    {
        byte[] salt = new byte[inLength];
        random.nextBytes(salt);
        return hasher.getEncoder().encodeAsString(salt);
    }
    /**
     * random number generator value
     */
    private static final Random random = new SecureRandom();
    /**
     * test hasher value
     */
    private volatile PBKDF2Encoder hasher;
}
