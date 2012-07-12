package org.marketcetera.marketdata;

import static org.junit.Assert.assertEquals;

import java.net.URI;

import org.junit.Test;
import org.marketcetera.module.ExpectedFailure;
import org.marketcetera.util.log.I18NBoundMessage0P;
import org.marketcetera.util.log.I18NBoundMessage1P;
import org.marketcetera.util.test.EqualityAssert;
import org.marketcetera.util.test.UnicodeData;

/* $License$ */

/**
 * Tests {@link UriScheme}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 2.0.0
 */
public class UriSchemeTest
    implements Messages
{
    /**
     * Tests the <code>UriScheme</code> constructor.
     *
     * @throws Exception if an error occurs
     */
    @Test
    public void constructor()
        throws Exception
    {
        new ExpectedFailure<NullPointerException>() {
            @Override
            protected void run()
                    throws Exception
            {
                new UriScheme(null);
            }
        };
        for(String invalidScheme : invalidSchemes) {
            final String theSchemeName = invalidScheme;
            new ExpectedFailure<IllegalArgumentException>(new I18NBoundMessage1P(INVALID_SCHEME_NAME,
                                                                                 theSchemeName).getText()) {
                @Override
                protected void run()
                    throws Exception
                {
                    new UriScheme(theSchemeName);
                }
            };
        }
        for(String validScheme : validSchemes) {
            UriScheme scheme = new UriScheme(validScheme);
            assertEquals(validScheme,
                         scheme.getScheme());
            assertEquals(String.format("%s://",
                                       validScheme),
                         scheme.getFullScheme());
        }
    }
    /**
     * Tests {@link UriScheme#composeUriString(String)} and {@link UriScheme#composeUriString(String, int)}. 
     *
     * @throws Exception if an error occurs
     */
    @Test
    public void composition()
        throws Exception
    {
        final UriScheme scheme = new UriScheme("metc");
        new ExpectedFailure<NullPointerException>() {
            @Override
            protected void run()
                    throws Exception
            {
                scheme.composeUriString(null);
            }
        };
        new ExpectedFailure<NullPointerException>() {
            @Override
            protected void run()
                    throws Exception
            {
                scheme.composeUriString(null,
                                        1000);
            }
        };
        for(String invalidHostname : invalidHostnames) {
            final String theHostname = invalidHostname;
            new ExpectedFailure<IllegalArgumentException>(new I18NBoundMessage1P(INVALID_HOSTNAME,
                                                                                 theHostname).getText()) {
                @Override
                protected void run()
                    throws Exception
                {
                    scheme.composeUriString(theHostname);
                }
            };
            new ExpectedFailure<IllegalArgumentException>(new I18NBoundMessage1P(INVALID_HOSTNAME,
                                                                                 theHostname).getText()) {
                @Override
                protected void run()
                    throws Exception
                {
                    scheme.composeUriString(theHostname,
                                            1000);
                }
            };
        }
        for(String validHostname : validHostnames) {
            verifyUri(scheme,
                      "metc",
                      validHostname);
        }
        for(int invalidPort : invalidPorts) {
            final int thePort = invalidPort;
            new ExpectedFailure<IllegalArgumentException>(new I18NBoundMessage0P(PORT_REQUIRED).getText()) {
                @Override
                protected void run()
                    throws Exception
                {
                    scheme.composeUriString("hostname",
                                            thePort);
                }
            };
        }
        for(int validPort : validPorts) {
            verifyUri(scheme,
                      "metc",
                      "hostname",
                      validPort);
        }
    }
    /**
     * Tests {@link UriScheme#validate(String)}.
     *
     * @throws Exception if an error occurs
     */
    @Test
    public void validate()
        throws Exception
    {
        final UriScheme scheme = new UriScheme("metc");
        new ExpectedFailure<NullPointerException>() {
            @Override
            protected void run()
                    throws Exception
            {
                scheme.validate(null);
            }
        };
        for(String invalidURI : invalidURIs) {
            final String theURI = invalidURI;
            new ExpectedFailure<IllegalArgumentException>(new I18NBoundMessage1P(SCHEME_REQUIRED,
                                                                                 scheme.getScheme()).getText()) {
                @Override
                protected void run()
                        throws Exception
                {
                    scheme.validate(theURI);
                }
            };
        }
        for(String badPortURI : badPortURIs) {
            final String theURI = badPortURI;
            new ExpectedFailure<IllegalArgumentException>(new I18NBoundMessage0P(PORT_REQUIRED).getText()) {
                @Override
                protected void run()
                        throws Exception
                {
                    scheme.validate(theURI);
                }
            };
        }
        for(String validURI : validURIs) {
            scheme.validate(validURI);
        }
    }
    /**
     * Tests {@link UriScheme#hostnameFromUri(String)} and {@link UriScheme#portFromUri(String)}. 
     *
     * @throws Exception if an error occurs
     */
    @Test
    public void hostnameAndPortFromURI()
        throws Exception
    {
        final UriScheme scheme = new UriScheme("metc");
        new ExpectedFailure<NullPointerException>() {
            @Override
            protected void run()
                    throws Exception
            {
                scheme.hostnameFromUri(null);
            }
        };
        new ExpectedFailure<NullPointerException>() {
            @Override
            protected void run()
                    throws Exception
            {
                scheme.portFromUri(null);
            }
        };
        for(String invalidURI : invalidURIs) {
            final String theURI = invalidURI;
            new ExpectedFailure<IllegalArgumentException>(new I18NBoundMessage1P(SCHEME_REQUIRED,
                                                                                 scheme.getScheme()).getText()) {
                @Override
                protected void run()
                        throws Exception
                {
                    scheme.portFromUri(theURI);
                }
            };
        }
        for(String validHostname : validHostnames) {
            StringBuilder uri = new StringBuilder().append(scheme.getFullScheme()).append(validHostname);
            assertEquals(validHostname,
                         scheme.hostnameFromUri(uri.toString()));
        }
        for(int validPort : validPorts) {
            StringBuilder uri = new StringBuilder().append(scheme.getFullScheme()).append("hostname:").append(validPort);
            assertEquals(validPort,
                         scheme.portFromUri(uri.toString()));
        }
    }
    /**
     * Tests {@link UriScheme#equals(Object)} and {@link UriScheme#hashCode()}.
     *
     * @throws Exception if an error occurs
     */
    @Test
    public void equalsAndHashcode()
        throws Exception
    {
        UriScheme scheme1 = new UriScheme("metc1");
        UriScheme scheme2 = new UriScheme("metc1");
        UriScheme scheme3 = new UriScheme("metc2");
        EqualityAssert.assertEquality(scheme1,
                                      scheme2,
                                      scheme3,
                                      this);
    }
    /**
     * Verifies the given <code>UriScheme</code> value is properly composed of the given parameters.
     *
     * @param inScheme a <code>UriScheme</code> value to verify
     * @param inParams an <code>Object...</code> value containing the parts that comprise the scheme
     * @throws Exception if an error occurs
     */
    private static void verifyUri(UriScheme inScheme,
                                  Object... inParams)
        throws Exception
    {
        String schemeName = (String)inParams[0];
        String hostname = (String)inParams[1];
        Integer port = (inParams.length == 3 ? (Integer)inParams[2] : null);
        assertEquals(schemeName,
                     inScheme.getScheme());
        assertEquals(String.format("%s://",
                                   schemeName),
                     inScheme.getFullScheme());
        String uriString;
        if(port == null) {
            uriString = new StringBuilder().append(schemeName).append("://").append(hostname).toString();
            assertEquals(uriString,
                         inScheme.composeUriString(hostname));
            URI uri = new URI(uriString);
            assertEquals(hostname,
                         uri.getHost());
            assertEquals(schemeName,
                         uri.getScheme());
        } else {
            uriString = new StringBuilder().append(schemeName).append("://").append(hostname).append(":").append(port).toString();
            assertEquals(uriString,
                         inScheme.composeUriString(hostname,
                                                   port));
            URI uri = new URI(uriString);
            assertEquals(hostname,
                         uri.getHost());
            assertEquals(schemeName,
                         uri.getScheme());
            assertEquals(port.intValue(),
                         uri.getPort());
        }
    }
    private final String[] invalidSchemes = new String[] { "", UnicodeData.GOODBYE_JA, "not:a:valid:scheme", "no spaces allowed", "also,not,valid" };
    private final String[] validSchemes = new String[] { "scheme", "a+valid+scheme", "another-123-valid-scheme", "yet.another.valid.scheme" };
    private final String[] validURIs = new String[] { "metc://hostname:1", "metc://hostname", "metc://127.0.0.1:80", "metc://127.0.0.1" };
    private final String[] invalidURIs = new String[] { "", "some bogus string", "metc://", "non-metc://hostname" };
    private final String[] badPortURIs = new String[] { "metc://hostname:0", "metc://hostname:65536" };
    private final String[] invalidHostnames = new String[] { "", "not a valid hostname" };
    private final String[] validHostnames = new String[] { "some-hostname", "some.other.hostname", "127.0.0.1" };
    private final int[] invalidPorts = new int[] { Integer.MIN_VALUE, -1, 0, 65536, Integer.MAX_VALUE };
    private final int[] validPorts = new int[] { 1, 1000, 65535 };
}
