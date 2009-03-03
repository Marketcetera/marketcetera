package org.marketcetera.strategyagent;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.file.Deleter;
import org.marketcetera.util.except.I18NException;
import org.marketcetera.module.ExpectedFailure;
import org.marketcetera.core.Pair;
import org.marketcetera.core.LoggerConfiguration;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

import java.io.*;
import java.util.Properties;
import java.util.HashSet;
import java.util.Arrays;
import java.util.jar.JarOutputStream;
import java.util.jar.JarEntry;
import java.net.URL;

/* $License$ */
/**
 * Tests {@link JarClassLoader}
 *
 * @author anshul@marketcetera.com
 */
@ClassVersion("$Id$")
public class JarClassLoaderTest {
    /**
     * Cleans up the jars in the testing directory.
     * This method can only be run before the test as attempts to delete
     * these files after the test fails as the classloader has these
     * files opened and locked. See the
     * <a href="http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=5041014">
     * bug report</a> for details on this issue.
     *
     * @throws I18NException if there were errors.
     */
    @BeforeClass
    public static void cleanup() throws Exception {
        LoggerConfiguration.logSetup();
        cleanDir(JAR_DIR);
        cleanDir(CONF_DIR);
    }

    /**
     * Tests jar classloader with invalid directories and sub-directories.
     *
     * @throws Exception if there were unexpected errors.
     */
    @Test
    public void invalidDirectory() throws Exception {
        final File f = new File("/doesnotexist");
        //Invalid module directory
        new ExpectedFailure<FileNotFoundException>(
                Messages.JAR_DIR_DOES_NOT_EXIST.getText(f.getAbsolutePath())){
            protected void run() throws Exception {
                new JarClassLoader(f, getClass().getClassLoader());
            }
        };
        //Invalid jars directory
        new ExpectedFailure<FileNotFoundException>(
                Messages.JAR_DIR_DOES_NOT_EXIST.getText(
                        new File(JAR_DIR,"jars").getAbsolutePath())){
            protected void run() throws Exception {
                new JarClassLoader(JAR_DIR, getClass().getClassLoader());
            }
        };
        //Create a temporary jars dir in JAR_DIR to get an error for
        //conf sub-directory
        File jarsDir = new File(JAR_DIR, "jars");
        jarsDir.mkdirs();
        assertTrue(jarsDir.isDirectory());
        new ExpectedFailure<FileNotFoundException>(
                Messages.JAR_DIR_DOES_NOT_EXIST.getText(
                        new File(JAR_DIR,"conf").getAbsolutePath())){
            protected void run() throws Exception {
                new JarClassLoader(JAR_DIR, getClass().getClassLoader());
            }
        };
        //cleanup
        Deleter.apply(jarsDir);
    }

    /**
     * Tests class loader with an empty directory.
     *
     * @throws Exception if there were unexpected errors.
     */
    @Test
    public void emptyDir() throws Exception {
        JarClassLoader loader = createLoader();
        //try finding a random resource and verify it fails
        assertNull(loader.findResource("random"));
    }

    /**
     * Tests loading of properties files within the conf sub-directory.
     *
     * @throws Exception if there were unexpected errors.
     */
    @Test
    public void loadConfResources() throws Exception {
        //Prepare a properties file
        final String firstRes = "ek.properties";
        final Properties firstProp = createProperties("ek", "value");
        writePropertiesToFile(firstRes, firstProp);
        //Prepare another property but do not write it.
        final String secondRes = "do.properties";
        final Properties secondProp = createProperties("do", "value");
        JarClassLoader loader = createLoader();
        //verify that we can read it.
        assertEquals(firstProp, loadProperty(firstRes, loader));
        //verify the hitherto unwritten property cannot be read.
        assertNull(loader.getResourceAsStream(secondRes));
        //now write the second properties, verify it can be read without refresh
        writePropertiesToFile(secondRes, secondProp);
        assertEquals(secondProp, loadProperty(secondRes, loader));
        //verify first one is still there
        assertEquals(firstProp, loadProperty(firstRes, loader));
        //Prepare a third set of property and verify that it can be
        //read with refresh
        final String thirdRes = "teen.properties";
        final Properties thirdProp = createProperties("teen", "value");
        writePropertiesToFile(thirdRes, thirdProp);
        loader.refresh();
        assertEquals(thirdProp, loadProperty(thirdRes, loader));
        //verify other two are still there
        assertEquals(secondProp, loadProperty(secondRes, loader));
        assertEquals(firstProp, loadProperty(firstRes, loader));
    }

    /**
     * Tests loading of jar resources from the jars subdirectory.
     *
     * @throws Exception if there were unexpected errors.
     */
    @Test
    public void loadJarResources() throws Exception {
        //Create a jar containing some random properties.
        final String firstRes = "first.properties";
        File jar1 = createJar("first.jar", firstRes,
                createProperties("first", "value"));
        //Create another jar but with the wrong suffix.
        final String secondRes = "second.properties";
        File jar2 = createJar("second.mar", secondRes,
                createProperties("second","value"));
        //Create a third jar for fun
        final String thirdRes = "third.properties";
        final Properties thirdProp = createProperties("third", "value");
        File jar3 = createJar("third.jar", thirdRes,
                thirdProp);
        //To test sorting, create more jar files with same property files
        final Properties firstFoundProp = createProperties("first", "actualValue");
        File jar1a = createJar("FIRST1.jar", firstRes, firstFoundProp);
        File jar3a = createJar("zhird.jar", thirdRes,
                createProperties("third", "notfound"));
        //Create the loader and verify that we can load the properties files
        JarClassLoader loader = createLoader();
        //The 2nd version of the first property file can be loaded
        assertEquals(firstFoundProp,loadProperty(firstRes, loader));
        //The second one cannot be as the jar was not included.
        assertNull(loader.getResourceAsStream(secondRes));
        //The third property file can be loaded.
        assertEquals(thirdProp,loadProperty(thirdRes, loader));
        assertJars(loader, jar1,jar3, jar1a, jar3a);
        //Prep a fourth properties file to add to the mix
        final String fourthRes = "fourth.properties";
        final String fifthRes = "fifth.properties";
        final Properties fifthProp = createProperties("fifth", "value");
        //Invoke refresh and verify nothing changes
        loader.refresh();
        assertEquals(firstFoundProp,loadProperty(firstRes, loader));
        assertNull(loader.getResourceAsStream(secondRes));
        assertEquals(thirdProp,loadProperty(thirdRes, loader));
        //verify that the new files cannot be loaded
        assertNull(loader.getResourceAsStream(fourthRes));
        assertNull(loader.getResourceAsStream(fifthRes));
        //Now create jar files for the new properties.
        File jar4 = createJar("fourth.jar", fourthRes,
                createProperties("fourth", "value"));
        //Chose a wrong suffix
        File jar5 = createJar("fifth.mar", fifthRes,
                fifthProp);
        //Create jars to test ordering behavior
        //Cannot override the jars already there before refresh
        File jar1b = createJar("A.jar", firstRes,
                createProperties("first","notfound"));
        //Can override a jar in the same refresh as this one
        final Properties fourthFoundProp = createProperties("fourth", "value");
        File jar4a = createJar("4ourth.jar", fourthRes,
                fourthFoundProp);
        //now refresh
        loader.refresh();
        //verify that the new property can be loaded
        assertEquals(fourthFoundProp,loadProperty(fourthRes, loader));
        //verify the one with wrong suffix cannot be loaded
        assertNull(loader.getResourceAsStream(fifthRes));
        //verify the old ones maintain status-quo
        assertEquals(firstFoundProp,loadProperty(firstRes, loader));
        assertNull(loader.getResourceAsStream(secondRes));
        assertEquals(thirdProp,loadProperty(thirdRes, loader));
        assertJars(loader, jar1,jar3, jar1a, jar1b, jar3a, jar4, jar4a);
    }

    /**
     * Deletes all the contents of the supplied directory.
     *
     * @param inDir the directory to be cleaned.
     *
     * @throws Exception if there were unexpected errors.
     */
    private static void cleanDir(File inDir) throws Exception {
        assertTrue(inDir.exists());
        File [] contents = inDir.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                //Only delete select files to make sure
                //that we do not delete svn files.
                return (name.endsWith("jar") ||
                        name.endsWith("mar") ||
                        name.endsWith("properties"));
            }
        });
        if(contents != null) {
            for(File f: contents) {
                Deleter.apply(f);
            }
        }
    }

    /**
     * Verifies that the supplied class loader has the expected set of files
     * in it.
     *
     * @param inLoader the classloader instance to test.
     *
     * @param inFiles the expected set of files.
     * @throws Exception if there were unexpected errors.
     */
    private static void assertJars(JarClassLoader inLoader, File... inFiles)
            throws Exception {
        HashSet<URL> loaderURLs = new HashSet<URL>(Arrays.asList(
                inLoader.getURLs()));
        HashSet<URL> expected = new HashSet<URL>();
        for(File f: inFiles) {
            expected.add(f.toURI().toURL());
        }
        //add the conf dir to expected
        expected.add(CONF_DIR.toURI().toURL());
        assertEquals(expected, loaderURLs);
    }

    /**
     * Loads the properties file from the supplied classloader.
     *
     * @param inRes the properties file name.
     * @param inLoader the classloader instance.
     *
     * @return the loaded properties instance.
     *
     * @throws IOException if there were unexpected errors.
     */
    private Properties loadProperty(String inRes,
                                    JarClassLoader inLoader)
            throws IOException {
        Properties p = new Properties();
        p.load(inLoader.getResourceAsStream(inRes));
        return p;
    }

    /**
     * Creates a properties instance with supplied key value pair
     * and some other key value pairs.
     *
     * @param inKey the key
     * @param inValue the value
     *
     * @return the initialized properties instance
     */
    private static Properties createProperties(String inKey, String inValue) {
        Properties p = new Properties();
        p.put(inKey, inValue);
        //throw in some extra values for fun
        p.put("cat","mouse");
        p.put("keyboard","mouse");
        return p;
    }

    /**
     * Write the supplied properties instance to the named properties file
     * within the conf subdirectory.
     *
     * @param inFileName the file name
     * @param inContents the contents of the file.
     *
     * @return the file object pointing to the newly created file.
     *
     * @throws IOException if there were unexpected errors
     */
    private static File writePropertiesToFile(
            String inFileName, Properties inContents) throws IOException {
        final File file = new File(CONF_DIR, inFileName);
        FileOutputStream fos = new FileOutputStream(file);
        inContents.store(fos,"");
        fos.close();
        file.deleteOnExit();
        return file;
    }

    /**
     * Creates a loader instance rooted at the testing modules dir.
     *
     * @return a new loader instance
     *
     * @throws IOException if there were unexpected errors.
     */
    private JarClassLoader createLoader() throws IOException {
        return new JarClassLoader(MODULE_DIR, getClass().getClassLoader());
    }

    /**
     * Creates a jar file in the testing directory, with the given name,
     * containing a single properties file having the specified with the
     * supplied contents.
     *
     * @param inJarName the name of the jar file.
     * @param inFileName the name of the properties file within the jar
     * @param inFileContents the contents of the properties file
     *
     * @return the file object pointing to the newly created jar file.
     *
     * @throws IOException if there were errors creating the file.
     */
    private static File createJar(String inJarName,
                                  String inFileName,
                                  Properties inFileContents)
            throws IOException {
        //Write the properties contents to a temporary byte array.
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        inFileContents.store(baos,"");
        baos.close();
        return createJar(inJarName,new JarContents[]{
                new JarContents(inFileName, baos.toByteArray())});
    }

    /**
     * Instances of this class specify contents that need to be stuffed
     * into a jar file.
     *
     * @see JarClassLoaderTest#createJar(String, String, Properties) 
     */
    static class JarContents extends Pair<String, byte[]> {
        /**
         * Creates a new instance.
         *
         * @param o1 the path of the file to be added to the jar.
         * @param o2 the contents of the file to be added.
         */
        public JarContents(String o1, byte[] o2) {
            super(o1, o2);
        }
    }

    /**
     * Creates a new jar in the jars sub-directory with the specified name
     * and the contents.
     *
     * @param inJarName the name of the jar file.
     * @param inContents the contents of the jar file.
     *
     * @return the file object pointing to the newly created jar.
     *
     * @throws IOException if there were unexpected errors.
     */
    static File createJar(String inJarName,
                          JarContents[] inContents)
            throws IOException {
        File jar = new File(JAR_DIR, inJarName);
        FileOutputStream fos = new FileOutputStream(jar);
        JarOutputStream jos = new JarOutputStream(fos);
        for(JarContents jc: inContents) {
            JarEntry entry = new JarEntry(jc.getFirstMember());
            jos.putNextEntry(entry);
            jos.write(jc.getSecondMember());
        }
        jos.close();
        fos.close();
        jar.deleteOnExit();
        return jar;
    }

    /**
     * The directory where all the sample data is kept.
     */
    static final File SAMPLE_DATA_DIR = new File("src" + File.separator +
            "test", "sample_data"); 
    /**
     * The directory where all the module files are kept.
     */
    static final File MODULE_DIR = new File(SAMPLE_DATA_DIR, "modules");
    /**
     * The directory where all the jars are kept.
     */
    static final File JAR_DIR = new File(MODULE_DIR, "jars");
    /**
     * The directory where all the module configuration files are kept.
     */
    static final File CONF_DIR = new File(MODULE_DIR, "conf");
}
