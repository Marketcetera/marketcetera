package org.marketcetera.util.misc;

import java.util.Locale;
import org.junit.Test;
import org.marketcetera.util.test.TestCaseBase;

import static org.junit.Assert.*;

public class SystemPropertiesTest
	extends TestCaseBase
{
    @Test
    public void propertiesExist()
    {
        assertNotNull(SystemProperties.JAVA_VERSION);
        assertNotNull(SystemProperties.JAVA_VENDOR);
        assertNotNull(SystemProperties.JAVA_VENDOR_URL);
        assertNotNull(SystemProperties.JAVA_HOME);
        assertNotNull(SystemProperties.JAVA_VM_SPECIFICATION_VERSION);
        assertNotNull(SystemProperties.JAVA_VM_SPECIFICATION_VENDOR);
        assertNotNull(SystemProperties.JAVA_VM_SPECIFICATION_NAME);
        assertNotNull(SystemProperties.JAVA_VM_VERSION);
        assertNotNull(SystemProperties.JAVA_VM_VENDOR);
        assertNotNull(SystemProperties.JAVA_VM_NAME);
        assertNotNull(SystemProperties.JAVA_SPECIFICATION_VERSION);
        assertNotNull(SystemProperties.JAVA_SPECIFICATION_VENDOR);
        assertNotNull(SystemProperties.JAVA_SPECIFICATION_NAME);
        assertNotNull(SystemProperties.JAVA_CLASS_VERSION);
        assertNotNull(SystemProperties.JAVA_CLASS_PATH);
        assertNotNull(SystemProperties.JAVA_LIBRARY_PATH);
        assertNotNull(SystemProperties.JAVA_IO_TMPDIR);
        assertNull(SystemProperties.JAVA_COMPILER);
        assertNotNull(SystemProperties.JAVA_EXT_DIRS);
        assertNotNull(SystemProperties.OS_NAME);
        assertNotNull(SystemProperties.OS_ARCH);
        assertNotNull(SystemProperties.OS_VERSION);
        assertNotNull(SystemProperties.FILE_SEPARATOR);
        assertNotNull(SystemProperties.PATH_SEPARATOR);
        assertNotNull(SystemProperties.LINE_SEPARATOR);
        assertNotNull(SystemProperties.USER_NAME);
        assertNotNull(SystemProperties.USER_HOME);
        assertNotNull(SystemProperties.USER_DIR);
    }
}
