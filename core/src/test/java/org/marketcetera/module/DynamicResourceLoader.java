package org.marketcetera.module;

import org.marketcetera.util.misc.ClassVersion;

import java.io.InputStream;
import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.util.Properties;
import java.util.HashMap;

/**
 * A class loader that is used to test provider refresh behavior. The
 * class loader dynamically returns properties files based on the
 * resources that have been added to it.
 * 
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$")
public class DynamicResourceLoader extends ClassLoader {
    @Override
    public InputStream getResourceAsStream(String name) {
        try {
            if (mResources.containsKey(name)) {
                if (mFail) {
                    return new InputStream() {
                        public int read() throws IOException {
                            throw new IOException();
                        }
                    };
                }
                Properties p = mResources.get(name);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                p.store(baos, "");
                baos.close();
                return new ByteArrayInputStream(baos.toByteArray());
            }
        } catch (IOException ignore) {
        }
        return super.getResourceAsStream(name);
    }

    /**
     * Adds the property file with the appropriate name and contents to
     * the set of resources that should be returned by this class loader for
     * the module with the specified URN..
     *
     * @param inURN       the URN of the module for which the properties are added.
     * @param inProperties the property value.
     */
    public void addResource(ModuleURN inURN, Properties inProperties) {
        mResources.put(getPropertiesName(inURN), inProperties);
    }

    /**
     * Returns the properties file name for the module/provider with the
     * specified URN.
     *
     * @param inURN the module/provider URN.
     *
     * @return the expected properties file name
     */
    public static String getPropertiesName(ModuleURN inURN) {
        return new StringBuilder().
                append(inURN.providerType()).
                append("_").append(inURN.providerName()).
                append(".properties").toString();
    }

    /**
     * if the returne input stream for a resource should
     * throw an IOException when an attempt is  made to read it.
     *
     * @param inFail if the returne input stream for a resource should
     *               throw an IOException when an attempt is  made to read it.
     */
    public void setFail(boolean inFail) {
        mFail = inFail;
    }

    /**
     * Clears the classloader state.
     */
    public void clear() {
        mResources.clear();
        mFail = false;
    }

    private final HashMap<String, Properties> mResources =
            new HashMap<String, Properties>();
    private boolean mFail = false;
}
