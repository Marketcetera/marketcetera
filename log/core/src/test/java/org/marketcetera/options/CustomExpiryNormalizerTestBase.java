package org.marketcetera.options;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;

import org.junit.BeforeClass;
import org.junit.Test;
import org.marketcetera.util.file.CopyCharsUtils;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */
/**
 * Base class for testing loading of custom expiry normalizers.
 * <p>
 * Subclasses override the abstract methods to carry out the tests.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 2.0.0
 */
@ClassVersion("$Id$")
public abstract class CustomExpiryNormalizerTestBase {
    @BeforeClass
    public static void setupLog() {
        OptionUtils.resetNormalizerLoaded();
    }

    /**
     * Invokes the {@link #doTest()} method after setting up the thread
     * context classloader.
     *
     * @throws Exception if there were unexpected failures.
     */
    @Test
    public final void testWithLoader() throws Exception {
        ClassLoader oldLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(new MyLoader(getClass().getClassLoader()));
        try {
            doTest();
        } finally {
            Thread.currentThread().setContextClassLoader(oldLoader);
        }
    }

    /**
     * This method should be overridden to carry out the test.
     * The classloader {@link MyLoader} is set as the thread context class
     * loader during the test.
     *
     * @throws Exception if there were unexpected failures.
     */
    protected abstract void doTest() throws Exception;

    /**
     * Creates a file containing the name of the custom expiry normalizer class.
     *
     * @return the URL to the file.
     *
     * @throws Exception if there were unexpected failures.
     */
    protected abstract URL createServicesFile() throws Exception;

    /**
     * Creates a text file containing the absolute name of the supplied class
     * and returns a URL pointing to it.
     *
     * @param inClass the class name.
     *
     * @return the URL to file containing the class name.
     *
     * @throws Exception if there were unexpected failures.
     */
    protected static URL createServicesFileFor(Class<?> inClass) throws Exception {
        File f = File.createTempFile("test",".txt");
        f.deleteOnExit();
        CopyCharsUtils.copy(inClass.getName().toCharArray(), f.getAbsolutePath());
        return f.toURI().toURL();
    }

    /**
     * The custom classloader that customizes the search for files with the
     * name {@code SERVICES_FILE_NAME}.
     */
    protected class MyLoader extends ClassLoader {
        /**
         * Creates an instance.
         *
         * @param parent the parent loader.
         */
        public MyLoader(ClassLoader parent) {
            super(parent);
        }

        @Override
        protected Enumeration<URL> findResources(String name) throws IOException {
            if(SERVICES_FILE_NAME.equals(name)) {
                try {
                    return Collections.enumeration(Arrays.asList(createServicesFile()));
                } catch (Exception e) {
                    throw new IOException(e);
                }
            }
            return super.findResources(name);
        }
    }

    private static final String SERVICES_FILE_NAME = "META-INF/services/" +
            OptionExpiryNormalizer.class.getName();
}
