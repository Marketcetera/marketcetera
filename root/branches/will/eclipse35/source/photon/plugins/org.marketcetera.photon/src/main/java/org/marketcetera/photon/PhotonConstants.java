package org.marketcetera.photon;

public class PhotonConstants {

	  public static final String  OSName = System.getProperty("os.name"); //$NON-NLS-1$

	  public static final boolean isOSX                             = OSName.toLowerCase().startsWith("mac os"); //$NON-NLS-1$
	  public static final boolean isLinux                   = OSName.equalsIgnoreCase("Linux"); //$NON-NLS-1$
	  public static final boolean isSolaris                 = OSName.equalsIgnoreCase("SunOS"); //$NON-NLS-1$
	  public static final boolean isFreeBSD                 = OSName.equalsIgnoreCase("FreeBSD"); //$NON-NLS-1$
	  public static final boolean isWindowsXP               = OSName.equalsIgnoreCase("Windows XP"); //$NON-NLS-1$
	  public static final boolean isWindows95               = OSName.equalsIgnoreCase("Windows 95"); //$NON-NLS-1$
	  public static final boolean isWindows98               = OSName.equalsIgnoreCase("Windows 98"); //$NON-NLS-1$
	  public static final boolean isWindowsME               = OSName.equalsIgnoreCase("Windows ME"); //$NON-NLS-1$
	  public static final boolean isWindows9598ME   = isWindows95 || isWindows98 || isWindowsME;

	  public static final boolean isWindows = OSName.toLowerCase().startsWith("windows"); //$NON-NLS-1$
	  // If it isn't windows or osx, it's most likely an unix flavor
	  public static final boolean isUnix = !isWindows && !isOSX;

	  public static final String    JAVA_VERSION = System.getProperty("java.version"); //$NON-NLS-1$

	  /**
	   * The ID of the script menu.
	   */
	  public static final String M_SCRIPT = "script"; //$NON-NLS-1$
}
