/*******************************************************************************
 * Copyright (c) 2000, 2003 Advanced Systems Concepts, Inc. and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     Advanced Systems Concepts - Initial api and implementation
 *******************************************************************************/
package com.swtworkbench.community.xswt.dataparser.parsers;

import java.net.URL;
import java.util.StringTokenizer;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

import com.swtworkbench.community.xswt.ClassBuilder;
import com.swtworkbench.community.xswt.XSWTException;
import com.swtworkbench.community.xswt.dataparser.DisposableDataParser;
import com.swtworkbench.community.xswt.metalogger.Logger;

/**
 * Class ImageDataParser.  
 * 
 * @author daveo
 */
public class ImageDataParser extends DisposableDataParser  {

    /* (non-Javadoc)
     * @see com.swtworkbench.community.xswt.dataparser.IDataParser#parse(java.lang.String)
     */
    public Object parse(String source) throws XSWTException {
        StringTokenizer stringTokenizer = new StringTokenizer(source, " \t\r\n");
        String classname = stringTokenizer.nextToken();
        String relativePath = stringTokenizer.nextToken();
        Image result = null;
        try {
            // FIXME: This only works at runtime.  It will not find the proper
            // class if the class is in a project you're developing inside
            // Eclipse.
            //
            // The fix would be to have the Eclipse plug-in supply a different
            // classloader that searches the project's build path
            Class relativeClass = ClassBuilder.getDefault().getClass(classname);
            URL imageLocation = relativeClass.getResource(relativePath);
            
            // FIXME: This won't work with the XSWT compiler
            result = new Image(Display.getCurrent(), imageLocation.openStream());
        } catch (Exception e) {
            Logger.log().error(e, "Unable to load Image");
        }
        return result;
    }
    
}
