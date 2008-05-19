package org.marketcetera.util.misc;

import org.marketcetera.core.ClassVersion;

/**
 * Constants for all standard system properties, as listed in the
 * {@link System} class.
 *
 * @author tlerios
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
public interface SystemProperties
{
    static final String JAVA_VERSION=
        System.getProperty("java.version");
    static final String JAVA_VENDOR=
        System.getProperty("java.vendor");
    static final String JAVA_VENDOR_URL=
        System.getProperty("java.vendor.url");
    static final String JAVA_HOME=
        System.getProperty("java.home");
    static final String JAVA_VM_SPECIFICATION_VERSION=
        System.getProperty("java.vm.specification.version");
    static final String JAVA_VM_SPECIFICATION_VENDOR=
        System.getProperty("java.vm.specification.vendor");
    static final String JAVA_VM_SPECIFICATION_NAME=
        System.getProperty("java.vm.specification.name");
    static final String JAVA_VM_VERSION=
        System.getProperty("java.vm.version");
    static final String JAVA_VM_VENDOR=
        System.getProperty("java.vm.vendor");
    static final String JAVA_VM_NAME=
        System.getProperty("java.vm.name");
    static final String JAVA_SPECIFICATION_VERSION=
        System.getProperty("java.specification.version");
    static final String JAVA_SPECIFICATION_VENDOR=
        System.getProperty("java.specification.vendor");
    static final String JAVA_SPECIFICATION_NAME=
        System.getProperty("java.specification.name");
    static final String JAVA_CLASS_VERSION=
        System.getProperty("java.class.version");
    static final String JAVA_CLASS_PATH=
        System.getProperty("java.class.path");
    static final String JAVA_LIBRARY_PATH=
        System.getProperty("java.library.path");
    static final String JAVA_IO_TMPDIR=
        System.getProperty("java.io.tmpdir");
    static final String JAVA_COMPILER=
        System.getProperty("java.compiler");
    static final String JAVA_EXT_DIRS=
        System.getProperty("java.ext.dirs");
    static final String OS_NAME=
        System.getProperty("os.name");
    static final String OS_ARCH=
        System.getProperty("os.arch");
    static final String OS_VERSION=
        System.getProperty("os.version");
    static final String FILE_SEPARATOR=
        System.getProperty("file.separator");
    static final String PATH_SEPARATOR=
        System.getProperty("path.separator");
    static final String LINE_SEPARATOR=
        System.getProperty("line.separator");
    static final String USER_NAME=
        System.getProperty("user.name");
    static final String USER_HOME=
        System.getProperty("user.home");
    static final String USER_DIR=
        System.getProperty("user.dir");
}
