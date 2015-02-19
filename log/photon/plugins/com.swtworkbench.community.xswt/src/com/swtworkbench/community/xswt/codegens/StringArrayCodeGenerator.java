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

import com.swtworkbench.community.xswt.codegen.ICodeGenerator;

/**
 * Class FontCodeGenerator.  
 * 
 * @author djo
 */
public class StringArrayCodeGenerator implements ICodeGenerator {

    /* (non-Javadoc)
     * @see net.sf.sweet_swt.xswt.constantgen.IConstantGenerator#getConstant(java.lang.Object)
     */
    public String getCode(Object o, String source) {
        String[] strarray = (String[])o
        
        ;
        StringBuffer result = new StringBuffer("new String[] {");
        if (strarray.length >= 1) {
            result.append(StringCodeGenerator.getDefault().getCode(strarray[0], strarray[0]));
        }
        if (strarray.length >= 2) {
            for (int i = 1; i < strarray.length; i++) {
                result.append(", ");
                result.append(StringCodeGenerator.getDefault().getCode(strarray[i], strarray[i]));
            }
        }
        result.append("}");
        return result.toString();
    }
}
