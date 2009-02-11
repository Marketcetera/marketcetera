package org.marketcetera.module;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.log.SLF4JLoggerProxy;

import java.util.regex.Pattern;
import java.util.Enumeration;
import java.net.URL;
import java.io.IOException;

/* $License$ */
/**
 * This class loader is used to test class/resource loading failures
 * when loading module factories, etc.
 *
 * Classes that match {@link #getFailClasses()} value, if it is set,
 * fail to load with a ClassNotFoundException.
 *
 * Requested resources whose names match {@link #getFailResources()} value, if
 * it is set, are not found. ie. empty enumerations are returned for them from
 * {@link ClassLoader#getResources(String)} 
 *
 * Found resources whose names match {@link #getFilterResources()} are
 * filtered out from enumerations returned from {@link ClassLoader#getResources(String)}
 *
 * @author anshul@marketcetera.com
 */
@ClassVersion("$Id$")
class ModuleTestClassLoader extends ClassLoader{
    /**
     * Creates an instance.
     *
     * @param parent the parent classloader.
     */
    public ModuleTestClassLoader(ClassLoader parent) {
        super(parent);
    }

    @Override
    public Enumeration<URL> getResources(String name) throws IOException {
        if(mFailResources != null && mFailResources.matcher(name).matches()) {
            SLF4JLoggerProxy.info(this, "Ignoring matched resource {}", name);
            return new Enumeration<URL>(){
                public boolean hasMoreElements() {
                    return false;
                }
                public URL nextElement() {
                    return null;
                }
            };
        }
        SLF4JLoggerProxy.info(this, "Finding resources {}", name);
        final Enumeration<URL> resources = super.getResources(name);
        return new Enumeration<URL>() {

            public boolean hasMoreElements() {
                while(resources.hasMoreElements()) {
                    mNext = resources.nextElement();
                    if(mFilterResources == null ||
                            !mFilterResources.matcher(
                                    mNext.toString()).matches()) {
                        return true;
                    } else {
                        SLF4JLoggerProxy.info(this, "Ignoring URL {}", mNext);
                    }
                }
                return false;
            }

            public URL nextElement() {
                SLF4JLoggerProxy.info(this, "Returning URL {}", mNext);
                return mNext;
            }
            private URL mNext;
        };
    }

    /**
     * Pattern for resources that should not be found.
     *
     * @return pattern for resources that should not be found.
     */
    public Pattern getFailResources() {
        return mFailResources;
    }

    /**
     * Pattern for resources that should not be found.
     *
     * @param inFailResources pattern for resources that should
     * not be found, can be null.
     */
    public void setFailResources(Pattern inFailResources) {
        mFailResources = inFailResources;
    }

    /**
     * The pattern for classes that should fail to load.
     *
     * @return pattern for classes that should fail to load.
     */
    public Pattern getFailClasses() {
        return mFailClasses;
    }

    /**
     * The pattern for classes that should fail to load.
     *
     * @param inFailClasses pattern for classes that should fail
     * to load, can be null.
     */
    public void setFailClasses(Pattern inFailClasses) {
        mFailClasses = inFailClasses;
    }

    /**
     * Returns the resource filter.
     *
     * @return the resource filter.
     */
    public Pattern getFilterResources() {
        return mFilterResources;
    }

    /**
     * Sets the resource filter.
     *
     * @param inFilterResources the resource filter, can be null.
     */
    public void setFilterResources(Pattern inFilterResources) {
        mFilterResources = inFilterResources;
    }

    @Override
    protected synchronized Class<?> loadClass(String name, boolean resolve)
            throws ClassNotFoundException {
        if(mFailClasses!= null && mFailClasses.matcher(name).matches()) {
            SLF4JLoggerProxy.info(this, "Ignoring matched class {}", name);
            throw new ClassNotFoundException("Sorry!");
        }
        SLF4JLoggerProxy.info(this, "Fetching class {}", name);
        return super.loadClass(name, resolve);
    }

    private Pattern mFailClasses;
    private Pattern mFailResources;
    private Pattern mFilterResources;
}
