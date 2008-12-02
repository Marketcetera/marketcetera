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
package com.swtworkbench.community.xswt.codegen;


/**
 * Class ICodeGenerator.  
 * 
 * @author djo
 */
public interface ICodeGenerator {
    /**
     * Method getCode.  Returns Java code that generates for the given value.
     * 
     * @param o The Object containing the value
     * @return Java code that generates the specified value
     */
    public String getCode(Object o, String source);
}
