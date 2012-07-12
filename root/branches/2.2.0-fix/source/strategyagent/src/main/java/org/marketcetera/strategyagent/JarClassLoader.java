package org.marketcetera.strategyagent;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.module.RefreshListener;

import java.net.URLClassLoader;
import java.net.URL;
import java.net.MalformedURLException;
import java.io.File;
import java.io.FilenameFilter;
import java.io.FileNotFoundException;
import java.util.*;

/* $License$ */
/**
 * A classloader to help load classes / files from module jars and
 * their default configuration files. This classloader is able to discover
 * new jars when invoked and adds them to its search class path as they
 * are discovered.
 * The classloader is initialized with a root directory that contains two
 * sub-directories, <code>jars</code> and <code>conf</code>.
 * The classloader loads all the files available in the <code>conf</code>
 * sub-directory. It loads the jar files directly contained within the
 * <code>jars</code> sub-directory. Any file with a suffix <code>.jar</code>
 * is considered a jar file, other files are ignored.
 * <p>
 * The classloader adds the jars found in the <code>jars</code> sub-directory
 * and the <code>conf</code> sub-directory to its search path. Whenever
 * {@link #refresh()} is invoked, the classloader lists the jars files
 * contained within the <code>jars</code> sub-directory and adds any new ones
 * to its search path.
 * <p>
 * The classloader sorts the files found in the <code>jars</code> sub-directory
 * by their {@link java.io.File#getName() base names} using
 * {@link String#compareTo(String)} before adding them.
 * The files are sorted before adding both during initialization and
 * refresh. Do note that files that are added during refresh are always
 * after the files added during initialization and previous refreshes, even
 * though their base names may be lexicographically before the files that
 * are already there. 
 *
 * @author anshul@marketcetera.com
 */
@ClassVersion("$Id$") //$NON-NLS-1$
class JarClassLoader extends URLClassLoader implements RefreshListener {
    /**
     * Creates an instance that loads classes from all the jars in the
     * supplied directory. When refreshed, the classloader will discover
     * any jars added to the directory and load classes from them.
     *
     * @param inJarDir the directory containing all the module jars.
     * @param inParent the parent class loader
     *
     * @throws MalformedURLException if there were errors
     * constructing URLs from file paths.
     * @throws FileNotFoundException if the supplied jar directory does not
     * exist or is inaccessible.
     */
    public JarClassLoader(File inJarDir, ClassLoader inParent)
            throws MalformedURLException, FileNotFoundException {
        super(new URL[0], inParent);
        Messages.LOG_JAR_LOADER_INIT.info(this, inJarDir);
        if(!inJarDir.isDirectory()) {
            throw new FileNotFoundException(Messages.JAR_DIR_DOES_NOT_EXIST.
                    getText(inJarDir.getAbsolutePath()));
        }
        //Initialize the directory containing all the module jars
        mJarDir = new File(inJarDir,"jars");  //$NON-NLS-1$
        if(!mJarDir.isDirectory()) {
            throw new FileNotFoundException(Messages.JAR_DIR_DOES_NOT_EXIST.
                    getText(mJarDir.getAbsolutePath()));
        }
        //The directory containing the module default configuration
        //properties files.
        inJarDir = new File(inJarDir, "conf");  //$NON-NLS-1$
        if(!inJarDir.isDirectory()) {
            throw new FileNotFoundException(Messages.JAR_DIR_DOES_NOT_EXIST.
                    getText(inJarDir.getAbsolutePath()));
        }
        //Add the conf directory to the classloader
        addURL(inJarDir.toURI().toURL());
        List<URL> urls = getJarURLs();
        if (urls != null) {
            for(URL url:urls) {
                addURL(url);
            }
        }
    }

    /**
     * Refreshes the Jar loader's list of jars that it loads classes from.
     * It lists the jar files in the loader's directory and adds any new
     * files to the set of files it loads classes from.
     *
     * @throws MalformedURLException if there were errors
     * constructing URLs from file paths.
     * 
     * @return if any jars were found and added to the classloader
     */
    public boolean refresh() throws MalformedURLException {
        Messages.LOG_REFRESH_JAR_LOADER.info(this);
        List<URL> urlList = getJarURLs();
        if (urlList != null && !urlList.isEmpty()) {
            //Use linked hash set to preserve URL orderings.
            Set<URL> found = new LinkedHashSet<URL>(urlList);
            Set<URL> loaded = new HashSet<URL>(Arrays.asList(getURLs()));
            //Find out all the jars that are not loaded.
            found.removeAll(loaded);
            if(!found.isEmpty()) {
                //Add all the jars that were found but are not already loaded
                for(URL url: found) {
                    addURL(url);
                }
                return true;
            }
        }
        return false;
    }

    @Override
    protected void addURL(URL url) {
        super.addURL(url);
        //Overridden to be able to log
        Messages.LOG_JAR_LOADER_ADD_URL.info(this, url);
    }

    /**
     * Returns the list of URLs of jar files found in the classloader's
     * directory.
     *
     * @return the list of jar URLs, null if the contents of the directory
     * could not be listed.
     *
     * @throws MalformedURLException if there were errors creating the URLs
     */
    private List<URL> getJarURLs() throws MalformedURLException {
        File[] jars = mJarDir.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith(JAR_SUFFIX);  //$NON-NLS-1$
            }
        });
        //Sort the Files by their base names
        Arrays.sort(jars, new Comparator<File>() {
            public int compare(File inFile1, File inFile2) {
                return inFile1.getName().compareTo(inFile2.getName());
            }
        });
        List<URL> urls = null;
        if (jars != null) {
            urls = new ArrayList<URL>(jars.length);
            for(File f: jars) {
                urls.add(f.toURI().toURL());
            }
        }
        return urls;
    }

    private File mJarDir;
    private static final String JAR_SUFFIX = ".jar";
}
