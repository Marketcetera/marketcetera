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
public class StringCodeGenerator implements ICodeGenerator {

    private static StringCodeGenerator singleton = null;
    
    public static StringCodeGenerator getDefault() {
        if (singleton == null) new StringCodeGenerator();
        return singleton;
    }

    public StringCodeGenerator() {
        singleton = this;
    }

    /* (non-Javadoc)
     * @see net.sf.sweet_swt.xswt.constantgen.IConstantGenerator#getConstant(java.lang.Object)
     */
    public String getCode(Object o, String source) {
        return "\"" + o.toString() + "\"";
    }
}
