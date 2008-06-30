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
package com.swtworkbench.community.xswt.examples.newsyntax;

import java.io.IOException;

import com.swtworkbench.community.xswt.XSWT;
import com.swtworkbench.community.xswt.XSWTException;
import com.swtworkbench.community.xswt.layoutbuilder.SWTSourceBuilder;
import com.swtworkbench.community.xswt.metalogger.Logger;


/**
 * Class XSWTTest.  
 * 
 * @author daveo
 */
public class TestCodeGen {

    public static void main(String[] args) throws IOException {
        try {
            XSWT parser = XSWT.create(TestCodeGen.class.getResourceAsStream("SimpleSample.xswt"));
            SWTSourceBuilder translator = new SWTSourceBuilder(parser);  // Automatically registers itself with XSWT
            parser.parsel(null);

            System.out.println(translator);

        } catch (XSWTException e) {
            Logger.log().error(e, "Unable to parse XSWT file");
        }
    }
}


