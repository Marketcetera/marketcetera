package org.marketcetera.core;

import java.io.Serializable;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Represents the version info of a running instance.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 2.4.0
 */
@ClassVersion("$Id$")
public class VersionInfo
        implements Serializable
{
    /**
     * Create a new VersionInfo instance.
     *
     * @param inVersionInfo a <code>String</code> value containing the raw version string
     * @throws IllegalArgumentException if the version is not of the pattern <code>#.#.#[-SNAPSHOT]</code>
     */
    public VersionInfo(String inVersionInfo)
    {
        inVersionInfo = StringUtils.trimToNull(inVersionInfo);
        Validate.isTrue(isValid(inVersionInfo));
        versionInfo = inVersionInfo;
        String[] components = inVersionInfo.split("\\."); //$NON-NLS-1$
        major = Integer.parseInt(components[0]);
        minor = Integer.parseInt(components[1]);
        if(components[2].contains("-")) { //$NON-NLS-1$
            patch = Integer.parseInt(components[2].split("-")[0]); //$NON-NLS-1$
            isSnapshot = true;
        } else {
            patch = Integer.parseInt(components[2]);
            isSnapshot = false;
        }
    }
    /**
     * Get the major value.
     *
     * @return an <code>int</code> value
     */
    public int getMajor()
    {
        return major;
    }
    /**
     * Get the minor value.
     *
     * @return an <code>int</code> value
     */
    public int getMinor()
    {
        return minor;
    }
    /**
     * Get the patch value.
     *
     * @return an <code>int</code> value
     */
    public int getPatch()
    {
        return patch;
    }
    /**
     * Get the versionInfo value.
     *
     * @return a <code>String</code> value
     */
    public String getVersionInfo()
    {
        return versionInfo;
    }
    /**
     * Get the isSnapshot value.
     *
     * @return a <code>boolean</code> value
     */
    public boolean getIsSnapshot()
    {
        return isSnapshot;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return versionInfo;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        return new HashCodeBuilder().append(major).append(minor).toHashCode();
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
        VersionInfo other = (VersionInfo) obj;
        return new EqualsBuilder().append(major,other.major).append(minor,other.minor).isEquals();
    }
    /**
     * Indicates if the given version info is valid.
     *
     * @param inVersionInfo a <code>String</code> value
     * @return a <code>boolean</code> value
     */
    public static boolean isValid(String inVersionInfo)
    {
        inVersionInfo = StringUtils.trimToNull(inVersionInfo);
        return inVersionInfo != null && VERSION_PATTERN.matcher(inVersionInfo).matches();
    }
    /**
     * Create a new VersionInfo instance.
     */
    @SuppressWarnings("unused")
    private VersionInfo()
    {
        major = -1;
        minor = -1;
        patch = -1;
        versionInfo = null;
        isSnapshot = false;
    }
    /**
     * major version value
     */
    private final int major;
    /**
     * minor version value
     */
    private final int minor;
    /**
     * patch version value
     */
    private final int patch;
    /**
     * version info value
     */
    private final String versionInfo;
    /**
     * indicates if the version is a snapshot or not
     */
    private final boolean isSnapshot;
    /**
     * allowable version pattern
     */
    private static final Pattern VERSION_PATTERN = Pattern.compile("^[0-9]+\\.[0-9]+\\.[0-9]+(-SNAPSHOT)?$"); //$NON-NLS-1$
    /**
     * represents an unknown version
     */
    public static final VersionInfo DEFAULT_VERSION = new VersionInfo("0.0.0"); //$NON-NLS-1$
    private static final long serialVersionUID = 6712745454199787033L;
}
