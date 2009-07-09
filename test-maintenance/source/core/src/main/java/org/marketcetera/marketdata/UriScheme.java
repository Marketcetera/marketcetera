package org.marketcetera.marketdata;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Pattern;

import org.marketcetera.util.log.I18NBoundMessage1P;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Creates URIs for a given <a href="http://en.wikipedia.org/wiki/URI_scheme">scheme</a>.
 * 
 * <p>This class may be used to create URIs for a particular URI scheme.  Construct an instance
 * for a particular URI scheme (like "http" as in "http://").  Thereafter, use that instance to
 * construct syntactically valid URIs for that scheme ("http://www.marketcetera.com").  For example,
 * <pre>
 * UriScheme httpScheme = new UriScheme("http");
 * String marketceteraUri = httpScheme.composeUriString("www.marketcetera.com");
 * System.out.println(marketceteraUri);
 * </pre>
 * yields
 * <pre>
 * http://www.marketcetera.com
 * </pre>
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public class UriScheme
    implements Messages
{
    /**
     * Create a new UriScheme instance.
     * 
     * <p>Create a new scheme as defined by <a href="http://rfc.net/std0066.html">Internet Standard 66</a> and 
     * <a href="http://tools.ietf.org/html/rfc3986">RFC 3986</a>.  The given <code>inSchemeName</code> may be
     * composed of any combination of letters, digits, the plus character("+"), period("."), and hyphen("-").   
     *
     * @param inSchemeName a <code>String</code> value containing the name of the <code>UriScheme</code>
     * @throws IllegalArgumentException if the given scheme name is not valid as defined above
     */
    public UriScheme(String inSchemeName)
    {
        if(inSchemeName.isEmpty() ||
           !SCHEME_PATTERN.matcher(inSchemeName).matches()) {
            throw new IllegalArgumentException(new I18NBoundMessage1P(INVALID_SCHEME_NAME,
                                                                      inSchemeName).getText());
        }
        scheme = inSchemeName;
        fullScheme = String.format("%s://", //$NON-NLS-1$
                                   scheme);
    }
    /**
     * Gets the scheme.
     *
     * @return a <code>String</code> value
     */
    public String getScheme()
    {
        return scheme;
    }
    /**
     * Gets the full scheme.
     *
     * @return a <code>String</code> value
     */
    public String getFullScheme()
    {
        return fullScheme;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((scheme == null) ? 0 : scheme.hashCode());
        return result;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        UriScheme other = (UriScheme) obj;
        if (scheme == null) {
            if (other.scheme != null)
                return false;
        } else if (!scheme.equals(other.scheme))
            return false;
        return true;
    }
    /**
     * Creates a URI composed of the given host and port and this object's {@link #scheme}.
     *
     * <P>The contents of an internet URI are regulated by <a href="http://www.ietf.org/rfc/rfc2396.txt">RFC 2396</a>.
     * This method does not enforce full compliance with this RFC because hostnames might not be internet hostnames
     * but may be local network hostnames instead.  Different standards apply to non-internet hostnames and are difficult
     * to deterministically predict.
     * 
     * @param inHostname a <code>String</code> value
     * @param inPort an <code>int</code> value
     * @return a <code>String</code> value containing a conforming URI
     * @throws URISyntaxException if the given URI is not syntactically valid
     * @throws IllegalArgumentException if the host or port is not valid 
     */
    public String composeUriString(String inHostname,
                                   int inPort) 
    {
        validateHost(inHostname);
        validatePort(inPort);
        try {
            return makeUri(getFullScheme(),
                           inHostname,
                           inPort).toString();
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(INVALID_HOSTNAME.getText(inHostname));
        }
    }
    /**
     * Creates a URI composed of the given host and port and this object's {@link #scheme}.
     *
     * <P>The contents of an internet URI are regulated by <a href="http://www.ietf.org/rfc/rfc2396.txt">RFC 2396</a>.
     * This method does not enforce full compliance with this RFC because hostnames might not be internet hostnames
     * but may be local network hostnames instead.  Different standards apply to non-internet hostnames and are difficult
     * to deterministically predict.
     * 
     * @param inHostname a <code>String</code> value
     * @return a <code>String</code> value containing a conforming URI
     * @throws IllegalArgumentException if the host is not valid 
     */
    public String composeUriString(String inHostname) 
    {
        validateHost(inHostname);
        try {
            return makeUri(getFullScheme(),
                           inHostname,
                           null).toString();
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(INVALID_HOSTNAME.getText(inHostname));
        }
    }
    /**
     * Extracts the hostname from the given URI string.
     * 
     * <p>The URI must be syntactically valid and of this object's scheme.
     *
     * @param inUriString a <code>String</code> value
     * @return a <code>String</code> value
     * @throws IllegalArgumentException if the given URI is not syntactically valid, is syntactically valid but is not of
     *  the correct scheme, or if the host is missing or invalid
     */
    public String hostnameFromUri(String inUriString)
    {
        validate(inUriString);
        URI uri;
        try {
            uri = new URI(inUriString);
            return uri.getHost();
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(new I18NBoundMessage1P(SCHEME_REQUIRED,
                                                                      getScheme()).getText());
        }
    }
    /**
     * Extracts the port from the given URI string.
     * 
     * <p>The URI must be syntactically valid and of this object's scheme.
     *
     * @param inUriString a <code>String</code> value
     * @return an <code>int</code> value
     * @throws IllegalArgumentException if the given URI is not syntactically valid, is syntactically valid but is not of
     *  the correct scheme, or if the hostname or port is missing or invalid
     */
    public int portFromUri(String inUriString)
    {
        validate(inUriString);
        URI uri;
        try {
            uri = new URI(inUriString);
            validatePort(uri.getPort());
            return uri.getPort();
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(new I18NBoundMessage1P(SCHEME_REQUIRED,
                                                                      getScheme()).getText());
        }
    }
    /**
     * Validates the given argument to see if it contains a valid URI of this scheme.
     *
     * @param inUriString a <code>String</code> value containing a URI to be validated according to this object's scheme
     * @throws IllegalArgumentException if the given URI is not syntactically valid, is syntactically valid but is not of
     *  the correct scheme, or if the host or port is invalid
     */
    public void validate(String inUriString)
    {
        if(inUriString.isEmpty()) {
            throw new IllegalArgumentException(new I18NBoundMessage1P(SCHEME_REQUIRED,
                                                                      getScheme()).getText());
        }
        URI uri;
        try {
            uri = new URI(inUriString);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(new I18NBoundMessage1P(SCHEME_REQUIRED,
                                                                      getScheme()).getText());
        }
        // a URI must have the correct scheme, host, and port, and must be otherwise a syntactically
        //  valid URI according to the Java JVM
        if(uri.getScheme() == null ||
           !uri.getScheme().equals(getScheme())) {
            throw new IllegalArgumentException(new I18NBoundMessage1P(SCHEME_REQUIRED,
                                                                      getScheme()).getText());
        }
        validateHost(uri.getHost());
        if(uri.getPort() != -1) {
            validatePort(uri.getPort());
        }
    }
    /**
     * Constructs a <code>URI</code> object of the given pieces. 
     *
     * @param inFullScheme a <code>String</code> value containing the full scheme
     * @param inHostname a <code>String</code> value containing the hostname
     * @param inPort an <code>Integer</code> value containing the port to use or <code>null</code> for no port specified
     * @return a <code>URI</code> value
     * @throws URISyntaxException if the URI cannot be constructed because it is syntactically invalid
     */
    private static URI makeUri(String inFullScheme,
                               String inHostname,
                               Integer inPort)
        throws URISyntaxException
    {
        if(inPort == null) {
            return new URI(new StringBuilder().append(inFullScheme).append(inHostname).toString());
        } else {
            return new URI(new StringBuilder().append(inFullScheme).append(inHostname).append(":").append(inPort).toString()); //$NON-NLS-1$
        }
    }
    /**
     * Validates the given hostname value.
     * 
     * @param inHostname a <code>String</code> value
     * @throws IllegalArgumentException if <code>inHostname</code> is not valid 
     */
    private static void validateHost(String inHostname)
    {
        if(inHostname == null) {
            throw new NullPointerException(INVALID_HOSTNAME.getText(inHostname));
        }
        if(inHostname.isEmpty()) {
            throw new IllegalArgumentException(INVALID_HOSTNAME.getText(inHostname));
        }
        // this is done to pick up extra validation done by the JVM
        try {
            makeUri("validator://", //$NON-NLS-1$
                    inHostname,
                    null);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(INVALID_HOSTNAME.getText(inHostname));
        }
    }
    /**
     * Validates the given port value.
     *
     * @param inPort an <code>int</code> value
     * @throws IllegalArgumentException if <code>inPort</code> is not valid 
     */
    private void validatePort(int inPort)
    {
        if(inPort <= 0 ||
           inPort >= 65536) {
            throw new IllegalArgumentException(PORT_REQUIRED.getText());
        }
    }
    /**
     * the scheme value
     */
    private final String scheme;
    /**
     * the full scheme value based on the {@link #scheme}
     */
    private final String fullScheme;
    /**
     * the pattern used to validate scheme names
     */
    private static final Pattern SCHEME_PATTERN = Pattern.compile("[a-zA-Z0-9+\\-\\.]*"); //$NON-NLS-1$
}
