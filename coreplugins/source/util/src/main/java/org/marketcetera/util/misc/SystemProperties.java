package org.marketcetera.util.misc;

/**
 * Constants for all standard system properties, as listed in the
 * {@link System} class.
 *
 * @author tlerios@marketcetera.com
 * @since 0.5.0
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$") //$NON-NLS-1$
public interface SystemProperties
{
    static final String JAVA_VERSION=
        System.getProperty("java.version"); //$NON-NLS-1$
    static final String JAVA_VENDOR=
        System.getProperty("java.vendor"); //$NON-NLS-1$
    static final String JAVA_VENDOR_URL=
        System.getProperty("java.vendor.url"); //$NON-NLS-1$
    static final String JAVA_HOME=
        System.getProperty("java.home"); //$NON-NLS-1$
    static final String JAVA_VM_SPECIFICATION_VERSION=
        System.getProperty("java.vm.specification.version"); //$NON-NLS-1$
    static final String JAVA_VM_SPECIFICATION_VENDOR=
        System.getProperty("java.vm.specification.vendor"); //$NON-NLS-1$
    static final String JAVA_VM_SPECIFICATION_NAME=
        System.getProperty("java.vm.specification.name"); //$NON-NLS-1$
    static final String JAVA_VM_VERSION=
        System.getProperty("java.vm.version"); //$NON-NLS-1$
    static final String JAVA_VM_VENDOR=
        System.getProperty("java.vm.vendor"); //$NON-NLS-1$
    static final String JAVA_VM_NAME=
        System.getProperty("java.vm.name"); //$NON-NLS-1$
    static final String JAVA_SPECIFICATION_VERSION=
        System.getProperty("java.specification.version"); //$NON-NLS-1$
    static final String JAVA_SPECIFICATION_VENDOR=
        System.getProperty("java.specification.vendor"); //$NON-NLS-1$
    static final String JAVA_SPECIFICATION_NAME=
        System.getProperty("java.specification.name"); //$NON-NLS-1$
    static final String JAVA_CLASS_VERSION=
        System.getProperty("java.class.version"); //$NON-NLS-1$
    static final String JAVA_CLASS_PATH=
        System.getProperty("java.class.path"); //$NON-NLS-1$
    static final String JAVA_LIBRARY_PATH=
        System.getProperty("java.library.path"); //$NON-NLS-1$
    static final String JAVA_IO_TMPDIR=
        System.getProperty("java.io.tmpdir"); //$NON-NLS-1$
    static final String JAVA_COMPILER=
        System.getProperty("java.compiler"); //$NON-NLS-1$
    static final String JAVA_EXT_DIRS=
        System.getProperty("java.ext.dirs"); //$NON-NLS-1$
    static final String OS_NAME=
        System.getProperty("os.name"); //$NON-NLS-1$
    static final String OS_ARCH=
        System.getProperty("os.arch"); //$NON-NLS-1$
    static final String OS_VERSION=
        System.getProperty("os.version"); //$NON-NLS-1$
    static final String FILE_SEPARATOR=
        System.getProperty("file.separator"); //$NON-NLS-1$
    static final String PATH_SEPARATOR=
        System.getProperty("path.separator"); //$NON-NLS-1$
    static final String LINE_SEPARATOR=
        System.getProperty("line.separator"); //$NON-NLS-1$
    static final String USER_NAME=
        System.getProperty("user.name"); //$NON-NLS-1$
    static final String USER_HOME=
        System.getProperty("user.home"); //$NON-NLS-1$
    static final String USER_DIR=
        System.getProperty("user.dir"); //$NON-NLS-1$
}
