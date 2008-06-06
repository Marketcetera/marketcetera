/*******************************************************************************
 * Copyright (c) 2000, 2003 Advanced Systems Concepts, Inc.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     David Orme (ASC) - Initial implementation
 ******************************************************************************/
package com.swtworkbench.community.xswt.codegens;

import java.util.StringTokenizer;

import com.swtworkbench.community.xswt.codegen.ICodeGenerator;


/**
 * Class ImageCodeGenerator.  
 * 
 * @author djo
 */
public class ImageCodeGenerator implements ICodeGenerator {

    private String dequalify(String className) {
        int lastPeriod = className.lastIndexOf(".");
        if (lastPeriod >= 0)
            className = className.substring(lastPeriod+1);
        return className;
    }

    /* (non-Javadoc)
     * @see net.sf.sweet_swt.xswt.constantgen.IConstantGenerator#getConstant(java.lang.Object)
     */
    public String getCode(Object o, String source) {
        StringTokenizer stringTokenizer = new StringTokenizer(source, " \t\r\n");
        String classname = dequalify(stringTokenizer.nextToken());
        String relativePath = stringTokenizer.nextToken();

        return "new Image(Display.getCurrent(), " + classname + ".class.getResource(\"" + relativePath + "\").openStream())";
    }
}
