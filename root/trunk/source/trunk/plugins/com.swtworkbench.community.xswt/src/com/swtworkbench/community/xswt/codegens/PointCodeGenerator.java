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

import org.eclipse.swt.graphics.Point;

import com.swtworkbench.community.xswt.codegen.ICodeGenerator;


/**
 * Class FontCodeGenerator.  
 * 
 * @author djo
 */
public class PointCodeGenerator implements ICodeGenerator {

    /* (non-Javadoc)
     * @see net.sf.sweet_swt.xswt.constantgen.IConstantGenerator#getConstant(java.lang.Object)
     */
    public String getCode(Object o, String source) {
        Point p = (Point)o;
        return "new Point(" + p.x + ", " + p.y + ")";
    }
}
