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
 * Class CharacterCodeGenerator.  
 * 
 * @author djo
 */
public class IntArrayCodeGenerator implements ICodeGenerator {

    /* (non-Javadoc)
     * @see net.sf.sweet_swt.xswt.constantgen.IConstantGenerator#getConstant(java.lang.Object)
     */
    public String getCode(Object o, String source) {
        int[] intarray = (int[])o;
        StringBuffer result = new StringBuffer("new int[] {");
        if (intarray.length >= 1) {
            result.append(intarray[0]);
        }
        if (intarray.length >= 2) {
            for (int i = 1; i < intarray.length; i++) {
                result.append(", ");
                result.append(intarray[i]);
            }
        }
        result.append("}");
        return result.toString();
    }
}
