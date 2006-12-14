package org.marketcetera.photon;

public class PhotonConstants {

	  public static final String  OSName = System.getProperty("os.name");

	  public static final boolean isOSX                             = OSName.toLowerCase().startsWith("mac os");
	  public static final boolean isLinux                   = OSName.equalsIgnoreCase("Linux");
	  public static final boolean isSolaris                 = OSName.equalsIgnoreCase("SunOS");
	  public static final boolean isFreeBSD                 = OSName.equalsIgnoreCase("FreeBSD");
	  public static final boolean isWindowsXP               = OSName.equalsIgnoreCase("Windows XP");
	  public static final boolean isWindows95               = OSName.equalsIgnoreCase("Windows 95");
	  public static final boolean isWindows98               = OSName.equalsIgnoreCase("Windows 98");
	  public static final boolean isWindowsME               = OSName.equalsIgnoreCase("Windows ME");
	  public static final boolean isWindows9598ME   = isWindows95 || isWindows98 || isWindowsME;

	  public static final boolean isWindows = OSName.toLowerCase().startsWith("windows");
	  // If it isn't windows or osx, it's most likely an unix flavor
	  public static final boolean isUnix = !isWindows && !isOSX;

	  public static final String    JAVA_VERSION = System.getProperty("java.version");
}
