package org.marketcetera.client.jms;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.xml.bind.SchemaOutputResolver;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;

import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */
/**
 * Generates the XSD schema for the classes marshalled by
 * {@link JMSXMLMessageConverter} into the target directory.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.5.0
 */
@ClassVersion("$Id$")
public class SchemaGen {
    /**
     * Generates the schema for the types marshalled by this converter
     * to a directory named <code>target</code> in the current directory.
     *
     * @param args the command line arguments.
     */
    public static void main(String[] args) {
        try {
            JMSXMLMessageConverter j = new JMSXMLMessageConverter();
            j.getContext().generateSchema(new SchemaOutputResolver() {
                public Result createOutput(String namespaceUri,
                                           String suggestedFileName)
                        throws IOException {
                    // Try to name the file based on the
                    // path component of the namespace URI
                    URI u;
                    try {
                        u = new URI(namespaceUri);
                    } catch (URISyntaxException e) {
                        throw new IOException(e);
                    }
                    String name = u.getPath();
                    if(name != null) {
                        if(name.startsWith("/")) {
                            if(name.length() > 1) {
                                name = name.substring(1);
                            } else {
                                name = null;
                            }
                        }
                    }
                    if(name != null) {
                        name = name.replace('/', '_') + ".xsd";
                    } else {
                        //Use the suggested file name only if the path based
                        //namespace URI cannot be constructed.
                        name = suggestedFileName;
                    }
                    File f = new File("target", name);
                    SLF4JLoggerProxy.info(this, "Writing Schema {} to File: {}",
                            namespaceUri, f.getAbsolutePath());
                    return new StreamResult(f);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
